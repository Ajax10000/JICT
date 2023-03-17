Original C++ code issues

There are some issues with the original C++ code, but so far nothing major.

# Non-Issues

First of all, there are some methods in the code that are not called. I don't think
this is a mistake, as the title of the book is "Visual Special Effects Toolkit in C++",
so I believe the author added code that is meant for future use by programmers using
the toolkit.

In particular I'd like to point out that many classes have a display method that I
believe was meant for debugging purposes. Some classes have more than one method
meant for debugging purposes - SCENELST.CPP also has a method named fshowlist.
Some classes that have a display method are:\
memImage - see MEMIMAGE.H and MEMIMG32.CPP\
point2d - see VECTOR.CPP\
point3d - see VECTOR.CPP\
scene - see SCENELST.H\
sceneElement - see SCENELST.H\
sceneList - see SCENELST.H and SCENELST.CPP\
tMatrix - see TMATRIX.H and TMATRIX.CPP

# Minor Issues

This is what I've found so far.

## Throughout the code

Throughout the code many functions and methods will return an integer status code. Typically a 0 means everything succeeded. However instead of using 0, many times the original C++ code uses the value NULL. Having been a C/C++ programmer myself, I find this weird, as I only used that to indicate a null pointer - one indicating a pointer that had no memory associated with it and not pointing to anything.

## In MAINFRAME.H, MAINFRAME.CPP, and SCENELST.CPP

MAINFRAME.H defines a field named hazeFogEnabled.\
MAINFRAME.CPP defines method CMainFrame::OnRenderHazefog(). Apparently this is supposed to be method invoked when the user selects a non-existent Haze Fog menu item in the Render menu item.\
SCENELST.CPP defines method sceneList::render(imageView *displayWindow, tMatrix *viewMatrix, int depthSortingEnabled, int zBufferEnabled, int antiAliasEnabled, int hazeFogEnabled). Note the last parameter. However the method does nothing with this parameter named hazeFogEnabled.

## In MAINFRAME.CPP

### In Method OnToolsCreateascenelist

After calling sceneList.readList, the code checks to see if the scene type (which should be either STILL or SEQUENCE) is equal to MORPH. The code I'm talking about is shown below:

```cpp
    /// The following method parses the .scn file the user just selected,
    /// and places the information in a scene object and one or more
    /// sceneElement objects. The scene and sceneElement object(s) are
    /// then placed into the doubly linked list the sceneList maintains.
    myStatus = mySceneList->readList(g_msgText, aFileName);
    statusPrint(g_msgText);
    if(myStatus != 0) {
        mySceneList->clear();
        return;
    }
    strcpy(sceneFileName, dlg.GetPathName()); // save the file name

    // Load the scene information into the client object
    mySceneList->getSceneInfo(sceneName, &effectType, &colorMode,
        &outputRows, &outputColumns);
    mySceneList->getViewTransform(
        &viewTranslateX, &viewTranslateY, &viewTranslateZ,
        &viewRotateX, &viewRotateY, &viewRotateZ);
    getViewMatrix(viewMatrix);

    /// effectType == MORPH - why?
    if((effectType == SEQUENCE) || (effectType == MORPH)) {
        previewSequenceEnabled = 1;
        previewSceneEnabled = 0;
        renderSceneEnabled = 0;
        renderSequenceEnabled = 0;
    } else {
        previewSceneEnabled = 1;
        previewSequenceEnabled = 0;
        renderSceneEnabled = 0;
        renderSequenceEnabled = 0;
    }
```

## In MEMIMG32.CPP

### In Method adjustColor

The parameter adjustmentType passed in can have two values, "Target" or "Relative". But the code in method adjustColor also compares it to "Delta". A summary of the code in method adjustColor is shown below:

```cpp
    if(strcmpi(adjustmentType, "Target") == 0) {
        // code here
    } // if

    if(strcmpi(adjustmentType, "Relative") == 0) {
        // switch stmt
    } // if

    for(row = 1; row <= numRows; row++) {
        for(col = 1; col <= numCols; col++) {
            // switch stmt
        } // for col
    } // for row

    if(strcmpi(adjustmentType, "Delta") == 0) {
        // for loop ...
    }
```

## In SCENELST.H and MODEL.CPP

The sceneElement defined in SCENELST.H defines field 'char adjustmentType[10]':\
`  char adjustmentType[10];    // 'Relative' or 'Target'`

However, in MODEL.CPP, which contains all the sceneElement methods, this field is only set in the sceneElement constructor:\
`strcpy(adjustmentType,adjType);`\
but is not thereafter used.

It appears that sceneElement does not need to keep this field, as it is used when the .scn file is being parsed. See the following snippet from method readList in SCENELST.CPP:

```cpp
    // If the color is to be adjusted, adjust it now and change the input image
    // file name to point to the color corrected image.
    if(strcmpi(adjustmentType,"None") != 0) {
        memImage *inputImage = new memImage(theFileName, 0, 0, RANDOM, 'R', RGBCOLOR);
        if (!inputImage->isValid()) {
            sprintf(msgBuffer,
                "sceneList.readList: Can't open image for color correction: %s",
                theFileName);
            statusPrint(msgBuffer);
            return -1;
        }
        memImage *correctedImage = new memImage(inputImage);
        statusPrint("Adjusting color image");
        inputImage->adjustColor(anAdjustment.rgbtRed,
            anAdjustment.rgbtGreen, anAdjustment.rgbtBlue,
            &midRed, &midGreen, &midBlue, correctedImage,
            adjustmentType, NULL);

        constructPathName(colorAdjustedPath, theFileName, 'j');
        sprintf(msgBuffer,
            "sceneList::readList: Saving adjusted color image: %s",
            colorAdjustedPath);
        statusPrint(msgBuffer);

        correctedImage->writeBMP(colorAdjustedPath);
        delete inputImage;
        delete correctedImage;
    }
```

However it can be argued that it can be kept and written out to a file in the sceneElement.writeFile method, which the code currently does not do.

## In SCENELST.CPP

### In Method readList - parsing for scene type MORPH

The code, when parsing for the scene type of a scene line in a .scn file, checks to see if there is an scene type (also called effect type) value of "Morph". However the book indicates on page 75 that the only possible values for an effect type are "Still" and "Sequence". The code actually assumes a value of "Still" and then checks for "Sequence", and if it is "Sequence", fixes the assumption. Then it checks for "Morph". Below is the snippet that I am referring to. The two comments below are mine.

```cpp
    effectType = strtok(NULL,BLANK);
    theSequence = 1; // Assumes theSequence = STILL
    if(effectType != NULL) {
    if(strcmpi(effectType,"SEQUENCE") == 0) theSequence = SEQUENCE;
    if(strcmpi(effectType,"MORPH") == 0) theSequence = MORPH; // why
```

### In Method readList - parsing for COMPOUND

The same readList method, when parsing for the model type, checks for a model of type Compound. This model type is not discussed in the book. The possible image types, per p 75, are Image, Shape, QuadMesh, and Sequence. Below is the code snippet I am referring to. The comment below is mine.

```cpp
    theType = IMAGE;
    if(aType != NULL) {
        if(strcmpi(aType,"SHAPE") == 0) theType = SHAPE;
        if(strcmpi(aType,"QUADMESH") == 0) theType = QUADMESH;
        if(strcmpi(aType,"SEQUENCE") == 0) theType = SEQUENCE;
        if(strcmpi(aType,"COMPOUND") == 0) { // why?
            theType = COMPOUND;
            compoundMMember = 1;
        }
    } else {
        sprintf(errorText,"Expected a model type on Line %d",lineCounter);
        delete rt;delete sc;delete tr;
        delete pointOfReference;
        filein.close();
        return -1;
    }
```

### In Method readList - parsing for REFERENCEPOINT

Also in the same readList method, in the code that parses for model-related information, there is code that parses for a REFERENCEPOINT keyword. No such keyword is mentioned in the book.

# Bad design choices

## In GPIPE.CPP

The gPipe constructor uses a hard-coded path:\
`strcpy(scenePathName, "d:\\ict20\\output\\gPipe.bmp");`

## In IWARP.CPP

Method iwarpz uses several hard-coded paths, but they are all in code that is run only when debugging:

```cpp
    #ifdef ICTDEBUG
    if(zImage != NULL) {
        statusPrint("iwarpz: Writing zBuffer - d:\\ict20\\output\\rawWarpz.bmp");
        zImage->saveAs8("d:\\ict20\\output\\Warpz8.bmp");
    }
    #endif
```

```cpp
    #ifdef ICTDEBUG
        statusPrint("fWarp1: Writing output -  d:\\ict20\\output\\rawfWarp.bmp");
        outImage->writeBMP("d:\\ict20\\output\\rawfWarp.bmp");
    #endif
```

Method fwarpz also uses hard-coded paths, but again only in code that runs when debugging:

```cpp
    #ifdef ICTDEBUG
        zImage->writeBMP("d:\\ict20\\output\\zBuffer32.bmp");
        statusPrint("fWarp3: Writing z output - d:\\ict20\\output\\zBuffer32.bmp");
        zImage->saveAs8("d:\\ict20\\output\\zBuffer8.bmp");
        statusPrint("fWarp3: Writing z output - d:\\ict20\\output\\zBuffer8.bmp");

        statusPrint("fWarp3: Writing output -  c:\\ict\\output\\rawfWarp.bmp");
        outImage->writeBMP("c:\\ict\\output\\rawfWarp.bmp");
    #endif
```

## In MAINFRAME.CPP

Method OnToolsWarpimage uses a hard-coded path:\
`outImage->writeBMP("d:\\ict20\\output\\testwarp.bmp");`

Method OnToolsRenderVrmlFile uses a hard-coded path:\
`strcpy(outPath, "D:\\ict20\\output\\vrmlImage.bmp");`

## In QMESHMODEL.CPP

Method createQMeshModel uses three hard-coded paths:

```
    xImage8->writeBMP("d:\\ict20\\output\\meshx8.bmp");
    yImage8->writeBMP("d:\\ict20\\output\\meshy8.bmp");
    zImage8->writeBMP("d:\\ict20\\output\\meshz8.bmp");
```

## In READVRML.CPP

In method renderVRML, two hard-coded paths are used:

```cpp
    aGraphicPipe.saveZBuffer("d:\\ict20\\output\\gPipeZBuffer8.bmp");
    sprintf(g_msgText,"d:\\ict20\\output\\VRMLImage.bmp");
```

# Possible Bugs

## In MEMIMG32.CPP

Method getBoundingBox appears to compute the bounding box of a memImage incorrectly. I am referring to the code snippet below from method getBoundingBox:

```cpp
    for (x = 1; x <= imageWidth; x++) {
        for (y = 1; y < imageHeight; y++) {
            switch (bitsPerPixel) {
            case 8:
                thePixel = getMPixel(x, y);
                if(thePixel != CHROMAVALUE) {
                    if(x < *xBeg) *xBeg = x;
                    if(x > *xEnd) *xEnd = x;
                    if(y < *yBeg) *yBeg = x; // Shouldn't this x be y?
                    if(y > *yEnd) *yEnd = x; // Shouldn't this x be y?
                }
                break;

            case 24:
                getMPixelRGB(x, y, &red, &green, &blue);
                if(red != CHROMARED) {
                    if(x < *xBeg) *xBeg = x;
                    if(x > *xEnd) *xEnd = x;
                    if(y < *yBeg) *yBeg = x; // Shouldn't this x be y?
                    if(y > *yEnd) *yEnd = x; // Shouldn't this x be y?
                }

                if(green != CHROMAGREEN) {
                    if(x < *xBeg) *xBeg = x;
                    if(x > *xEnd) *xEnd = x;
                    if(y < *yBeg) *yBeg = x; // Shouldn't this x be y?
                    if(y > *yEnd) *yEnd = x; // Shouldn't this x be y?
                }

                if(blue != CHROMABLUE) {
                    if(x < *xBeg) *xBeg = x;
                    if(x > *xEnd) *xEnd = x;
                    if(y < *yBeg) *yBeg = x; // Shouldn't this x be y?
                    if(y > *yEnd) *yEnd = x; // Shouldn't this x be y?
                }
                break;
            } // switch
        } // for y

        statusPrint(savedFileName);
        sprintf(g_msgText,"getBoundingBox: xBeg: %d  xEnd: %d yBeg: %d  yEnd: %d",
            xBeg, xEnd, yBeg, yEnd);
        statusPrint(g_msgText);
    } // for x
```

## In RENDER.CPP

Method renderMeshz contains the following code, which modifies parameters vx, vy and vz, all of which are of type float.

```cpp
    //  Temporary - for testing
    vx = (float)outWidth/2.0;
    vy = (float)outHeight/2.0;
    vz = 512.0;
```

I believe the author forgot to comment out or delete this code. These are obviously not output parameters, as otherwise they would have been declared as float \*, not float.
