package core;

import fileUtils.FileUtils;

import globals.Globals;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import java.util.StringTokenizer;

import structs.Point3d;

public class ScnFileParser {
    private SceneList sceneList;

    // These were defined in SCENELST.H
    public static final int MONOCHROME = 1;
    public static final int COLOR      = 2;

    // Effect Types
    // These were defined in ICT20.H
    public static final int STILL    = 1;
    public static final int SEQUENCE = 2;
    public static final int MORPH    = 3;

    // Model Types
    // These were defined in ICT20.H
    public static final int IMAGE       = 1;
    public static final int SHAPE       = 2;
    public static final int QUADMESH    = 3;
    public static final int COMPOUND    = 4;
    public static final int LIGHTSOURCE = 5;

    // These were defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;

    // These were defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 0;


    // Called from MainFrame.onToolsCreateASceneList
    public ScnFileParser(SceneList sceneList) {
        this.sceneList = sceneList;
    } // ScnFileParser ctor


    // Method readList parses the Scene and Model-related information in a .scn file
    // specified by parameter pathName. If an error occurs while parsing, the String 
    // errorText is set to an error message.
    // If successfully parsed, it adds the scene and model information to a 
    // doubly-linked list. The start of this list is pointed to by field sceneListHead.
    // Called from:
    //     MainFrame.onToolsCreateASceneList
    public int readList(String errorText, String pathName) {
        String TheText = "", TheKeyWord;
        String theModelName = "", theFileName, theMotionPath;
        String theSceneName = "", theAlphaPath, msgBuffer;
        String colorAdjustedPath = "";
        int lineCounter, numScenes;
        int outImageCols = 0, outImageRows = 0;
        int notFound; // = TRUE, FALSE, or THREE_NUMBERS_NOT_FOUND
        boolean theBlend, theWarp;
        int theSequence = 0, theColorMode = 0, theType,
        compoundMMember;
        boolean compoundMember = false; // changed from int to boolean
        boolean getOutImageSizeFlag = false; // changed from int to boolean
        boolean definedRefPoint = false; // changed from int to boolean
        float theAlpha;
        Point3d rt, sc, tr, pointOfReference;
        Color anAdjustment = new Color(0, 0, 0);
        String adjustmentType;
        int myStatus, minLineSize = 4;
        byte midRed = (byte)0, midGreen = (byte)0, midBlue = (byte)0;
        final int FALSE = 0;
        final int TRUE = 1;
        final int THREE_NUMBERS_NOT_FOUND = 2;
        StringTokenizer strtok = new StringTokenizer("test");
        StringTokenizer numtok;

        final String BLANK = " ";
        // final int DUPLICATESCENE = 3; // This variable is not used

        File scnFile = new File(pathName);
        FileReader fileReader;
        try {
            fileReader = new FileReader(scnFile);
        } catch(FileNotFoundException fnfe) {
            errorText = "Could not find file.";
            // errorText will be printed with Global.statusPrint by the caller.

            return -1;
        }
        LineNumberReader filein = new LineNumberReader(fileReader);

        theMotionPath = "None";
        adjustmentType = "None";
        theAlphaPath = "Default";
        lineCounter = 0;
        numScenes = 0; 
        compoundMMember = 0;
        compoundMember = false;
        rt = new Point3d();
        sc = new Point3d();
        tr = new Point3d();
        pointOfReference = new Point3d();
        // Assume no problems will occur and initialize errorText accordingly.
        errorText = "Scene file read successfully";
        
        definedRefPoint = false;

        // Start parsing the .scn file. 
        // We exit the following "forever" loop via return statements.
        // We return -1 if we encounter an error.
        // We return 0 if no error occurs and we encounter the EOF keyword.
        while(true) {  // Get the scene components
            TheKeyWord = Shape3d.getNextLine(TheText, lineCounter, filein, minLineSize);
            // As long as we haven't encountered line that indicates 
            // the start of a Model description ...
            while(!TheKeyWord.equalsIgnoreCase("MODEL")) {
                // Each iteration through this loop will parse one line.
                // Note that we call Shape3d.getNextLine again at the end of this loop.
                String aBase, aSceneName, effectType, aColorMode;
                String tempImageSize;
                notFound = TRUE;

                // We have just read a new line from the .scn file.
                // We expect to find one of the following keywords:
                // "SCENE", "MOTIONPATH", "ROTATION", "TRANSLATION", "END", or "EOF".
                // "EOF" is a keyword generated by method Shape3d.getNextLine when
                // it finds the end of the file.
                if (TheKeyWord.equalsIgnoreCase("SCENE")) {
                    // We have found the start of a Scene description, 
                    // so we will parse it. It has the following format:
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    // Rotation <Rx>, <Ry>, <Rz>
                    // Translation <Tx>, <Ty>, <Tz>
                    // MotionPath [None]
                    notFound = FALSE;
                    // Skip over the word "SCENE"
                    aBase = TheText.substring(6);

                    // After the token "SCENE" we should have the scene name
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    strtok = new StringTokenizer(aBase, BLANK);
                    aSceneName = strtok.nextToken();
                    if(aSceneName != null) {
                        theSceneName = aSceneName;
                    } else {
                        errorText = "A Scene must have a name. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.
                        
                        return -1;
                    }

                    // Parse the effect type (i.e., [Sequence|Still])
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    effectType = strtok.nextToken();
                    theSequence = 1;
                    if(effectType != null) {
                        if(effectType.equalsIgnoreCase("SEQUENCE")) {
                            theSequence = SEQUENCE;
                        }
                        if(effectType.equalsIgnoreCase("MORPH")) {
                            theSequence = MORPH;
                        }
                    } else {
                        errorText = "A Sequence must have a name. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    // Now parse the image dimensions (i.e., <outHeight>, <outWidth>)
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    // tempImageSize is equal to the string starting with <outHeight>:
                    // <outHeight>, <outWidth> [Color|Mono]
                    tempImageSize = strtok.nextToken();

                    // Now looking for Color or Mono:
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    aColorMode = strtok.nextToken();
                    // Assume "Mono"
                    theColorMode = MONOCHROME;
                    if(aColorMode != null) {
                        if(aColorMode.equalsIgnoreCase("COLOR")) { 
                            // We assumed wrong, so we correct our assumption.
                            theColorMode = COLOR;
                        }
                    } else {
                        // We expected Color or Mono. We didn't find anything.
                        errorText = "Expected: Color or Monochrome. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    // Now parse outHeight and outWidth values (i.e., <outHeight>, <outWidth>)
                    // These should be integers.
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    if(tempImageSize != null) { // Output Image Height, Width
                        numtok = new StringTokenizer(tempImageSize, ",");
                        outImageRows = Integer.parseInt(numtok.nextToken());
                        outImageCols = Integer.parseInt(numtok.nextToken());
                        getOutImageSizeFlag = false;
                        if(outImageCols == 0 || outImageRows == 0) { 
                            getOutImageSizeFlag = true;
                        }
                    } else {
                        errorText = "Expected Image Height, Image Width. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }
                } // end scene processing

                if(TheKeyWord.equalsIgnoreCase("MOTIONPATH")) {
                    // Found a MOTIONPATH line. So we parse it. 
                    // It should have the followng format:
                    // MotionPath [None|<pathName>]
                    theMotionPath = TheText.substring(11);
                    if(theMotionPath.length() == 0) {
                        errorText = "MotionPath file missing on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }
                    notFound = FALSE;
                }

                if(TheKeyWord.equalsIgnoreCase("ROTATION")) {
                    // Found a ROTATION line. So we parse it. 
                    // It should have the following format:
                    // ROTATION <Rx>, <Ry>, <Rz>
                    // where Rx, Ry and Rz represent angles 
                    // expressed as floating-point numbers.
                    String theRt, localRt;
                    // theRt = strtok(TheText + 9, BLANK);
                    theRt = strtok.nextToken();
                    localRt = theRt;
                    if(checkFor3(localRt) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(localRt, ",");
                        rt.x = Float.parseFloat(numtok.nextToken());
                        rt.y = Float.parseFloat(numtok.nextToken());
                        rt.z = Float.parseFloat(numtok.nextToken());
                        notFound = FALSE;
                    }
                }

                if(TheKeyWord.equalsIgnoreCase("TRANSLATION")) {
                    // Found a TRANSLATION line. So we parse it. 
                    // It should have the following format:
                    // TRANSLATION <Tx>, <Ty>, <Tz>
                    // where Tx, Ty, and Tz represent translation values 
                    // expressed as floating-point numbers.
                    String theTr, localTr;
                    // theTr = strtok(TheText + 12, BLANK);
                    theTr = strtok.nextToken();
                    localTr = theTr;
                    if(checkFor3(localTr) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(localTr, ",");
                        tr.x = Float.parseFloat(numtok.nextToken());
                        tr.y = Float.parseFloat(numtok.nextToken());
                        tr.z = Float.parseFloat(numtok.nextToken());
                        notFound = FALSE;
                    }
                }
              
                if(TheKeyWord.equalsIgnoreCase("END")) {
                    String theToken, localToken;
                    // theToken = strtok(TheText + 4, BLANK);
                    theToken = strtok.nextToken();
                    localToken = theToken;
                    if(localToken.equalsIgnoreCase("COMPOUND")) {
                        compoundMMember = 0;
                        notFound = FALSE;
                    }
                }

                if (TheKeyWord.equalsIgnoreCase("EOF")) {
                    errorText = "sceneFile may be corrupted or has no models";
                    // errorText will be printed with Global.statusPrint by the caller.

                    closeLineNumberReader(filein);
                    return -1;
                }

                // notFound = TRUE (1), FALSE (0), or THREE_NUMBERS_NOT_FOUND (2)
                if (notFound != 0) {
                    if(notFound == 1) {
                        errorText = "Unknown Keyword: " + TheKeyWord + ". Line  " + lineCounter;
                    }
                    if(notFound == THREE_NUMBERS_NOT_FOUND) {
                        errorText = "Expected 3 numeric values separated by commas: " + TheKeyWord + 
                            "  Line " + lineCounter;
                    }
                    // // errorText will be printed with Global.statusPrint by the caller.

                    closeLineNumberReader(filein);
                    return -1;
                }

                TheKeyWord = Shape3d.getNextLine(TheText, lineCounter, filein, minLineSize);
            }  // while(!TheKeyWord.equalsIgnoreCase("MODEL"))

            // Add the scene to the sceneList and read its elements.
            numScenes++;
            if (numScenes > 1) {
                errorText = "Only 1 scene definition permitted per scene file";
                // errorText will be printed with Global.statusPrint by the caller.

                closeLineNumberReader(filein);
                return -1;
            }

            myStatus = sceneList.addScene(theSceneName, theSequence, outImageCols, outImageRows, theColorMode, rt, tr, theMotionPath);
            if(myStatus != 0) {
                errorText = "Could not add Scene to Scene List. Line " + lineCounter;
                // errorText will be printed with Global.statusPrint by the caller.

                closeLineNumberReader(filein);
                return -1;
            }

            theType = IMAGE;
            theMotionPath = "";
            theFileName = "";
            int nModels = 0;
            String aModelName, aBlend, aWarp, aScale, aScaleValue, aType;
            theBlend = true;
            theWarp = true;
            theAlpha = 1.0f;
            
            // Until we find the start of another SCENE attribute, we'll look for 
            // MODEL lines and parse the model information.
            while(!TheKeyWord.equalsIgnoreCase("SCENE")) {
                // Each iteration through this loop will parse one line.
                // Note that we call Shape3d.getNextLine at the end of this loop.
                notFound = TRUE;

                // We expect MODEL, ROTATION, SCALE, TRANSLATION, ADJUSTCOLOR, or MOTIONPATH
                if (TheKeyWord.equalsIgnoreCase("MODEL")) {
                    // The Model attributes in a scene file have the following format:
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    // FileName <pathName>
                    // MotionPath [None|<pathName>]
                    // [AlphaImagePath [None|<pathName>]]
                    // AdjustColor [Target|Relative] <R> <G> <B>
                    // Rotation <rx>, <ry>, <rz>
                    // Scale <sx>, <sy>, <sz>
                    // Translation <tx>, <ty>, <tz>
                    nModels++;

                    if (nModels > 1) {
                        // If the color is to be adjusted, adjust it now and change the input image
                        // file name to point to the color corrected image.
                        // AdjustColor [Target|Relative] <R> <G> <B>
                        if(adjustmentType.equalsIgnoreCase("None")) {
                            MemImage inputImage = new MemImage(theFileName, 0, 0, RANDOM, 'R', RGBCOLOR);
                            if (!inputImage.isValid()) {
                                msgBuffer = "sceneList.readList: Can't open image for color correction: " + theFileName;
                                Globals.statusPrint(msgBuffer);
                                return -1;
                            }

                            MemImage correctedImage = new MemImage(inputImage);
                            Globals.statusPrint("Adjusting color image");	    
                            inputImage.adjustColor(anAdjustment.getRed(), anAdjustment.getGreen(), anAdjustment.getBlue(),
                                midRed, midGreen, midBlue, 
                                correctedImage, adjustmentType, 0);

                            FileUtils.constructPathName(colorAdjustedPath, theFileName, 'j');     
                            msgBuffer = "sceneList.readList: Saving adjusted color image: " + colorAdjustedPath;
                            Globals.statusPrint(msgBuffer);
            
                            correctedImage.writeBMP(colorAdjustedPath);
                        }

                        if(compoundMMember == 1 && theType == COMPOUND) compoundMember = false;
                        if(compoundMMember == 1 && theType != COMPOUND) compoundMember = true;

                        myStatus = sceneList.addSceneElement(theModelName, theFileName, theBlend, theType,
                            theWarp, theAlpha, rt, sc, tr, theMotionPath, theAlphaPath,
                            compoundMember, anAdjustment, adjustmentType, colorAdjustedPath,
                            definedRefPoint, pointOfReference);
                        if(compoundMMember == 0) {
                            compoundMember = false;
                        }

                        if(myStatus != 0) {
                            errorText = "Could not add model to scene list. Line " + lineCounter;
                            // errorText will be printed with Global.statusPrint by the caller.

                            closeLineNumberReader(filein);
                            return -1;
                        }

                        // Reset the variables used to store values parsed from the .scn file
                        theBlend = true; 
                        theWarp = true; 
                        theAlpha = 1.0f; 
                        theType = IMAGE;
                        definedRefPoint = false;
                        theMotionPath = "";
                        theFileName = "";
                        adjustmentType = "None";
                        colorAdjustedPath = "None";
                    } // if (nModels > 1)

                    // Look for the model name:
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    // aModelName = strtok(TheText+6, BLANK);
                    aModelName = strtok.nextToken();

                    // If the modelName is ".", set it to an empty string
                    // causing the model to be displayed without a label
                    if(aModelName.equalsIgnoreCase(".")) {
                        theModelName = "";
                    } else {
                        theModelName = aModelName;
                    }

                    // Look for the BLEND specification (i.e., [Blend|NoBlend]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aBlend      = strtok.nextToken();

                    // Look for the Warp/NoWarp specification (i.e., [Warp|NoWarp]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aWarp       = strtok.nextToken();

                    // Look for the AlphaScale specification (i.e., AlphaScale <alpha>):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aScale      = strtok.nextToken();
                    aScaleValue = strtok.nextToken();

                    // Look for the model type (i.e., for [Image|Shape|QuadMesh|Sequence]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aType       = strtok.nextToken();

                    theBlend = true;
                    if(aBlend != null) {
                        if(aBlend.equalsIgnoreCase("NOBLEND")) { 
                            theBlend = false;
                        }
                    } else {
                        errorText = "Missing value or term on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    theWarp = true;
                    if(aWarp != null) {
                        if(aWarp.equalsIgnoreCase("NOWARP")) { 
                            theWarp = false;
                        }
                    } else {
                        errorText = "Missing value or term on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    theAlpha = 1.0f;
                    if(aScale != null) {
                        if(aScale.equalsIgnoreCase("ALPHASCALE")) {
                            theAlpha = Float.parseFloat(aScaleValue);
                        }
                    } else {
                        errorText = "Missing value or term on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    // Look for the model type (i.e., [Image|Shape|QuadMesh|Sequence]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    theType = IMAGE;
                    if(aType != null) {
                        if(aType.equalsIgnoreCase("SHAPE"))    theType = SHAPE;
                        if(aType.equalsIgnoreCase("QUADMESH")) theType = QUADMESH;
                        if(aType.equalsIgnoreCase("SEQUENCE")) theType = SEQUENCE;
                        if(aType.equalsIgnoreCase("COMPOUND")) {
                            theType = COMPOUND;
                            compoundMMember = 1;
                        }
                    } else {
                        errorText = "Expected a model type on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    notFound = FALSE;
                } // if (strcmpi (TheKeyWord, "MODEL")

                if(TheKeyWord.equalsIgnoreCase("REFERENCEPOINT")) {
                    String theRef, localRef;
                    // Skip over the word "REFERENCEPOINT"
                    // theRef = strtok(TheText + 15, BLANK);
                    int idxRefPt = TheText.indexOf("REFERENCEPOINT");
                    theRef = TheText.substring(idxRefPt + 15);
                    localRef = theRef;

                    if(checkFor3(localRef) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(localRef, ",");
                        pointOfReference.x = Float.parseFloat(numtok.nextToken());
                        pointOfReference.y = Float.parseFloat(numtok.nextToken());
                        pointOfReference.z = Float.parseFloat(numtok.nextToken());
                        definedRefPoint = true;
                        notFound = FALSE;
                    }
                }

                if(TheKeyWord.equalsIgnoreCase("ROTATION")) {
                    // Found a ROTATION line. So we parse it. 
                    // It should have the following format:
                    // ROTATION <rx>, <ry>, <rz>
                    // where rx, ry and rz are rotation degrees, 
                    // expressed as floating-point numbers.
                    String theRt;
                    String localRt;
                    // Skip over the word "ROTATION"
                    // theRt = strtok(TheText + 9, BLANK);
                    int rtIdx = TheText.indexOf("ROTATION");
                    theRt = TheText.substring(rtIdx + 9);
                    localRt = theRt;

                    if(checkFor3(localRt) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(localRt, ",");
                        rt.x = Float.parseFloat(strtok.nextToken());
                        rt.y = Float.parseFloat(strtok.nextToken());
                        rt.z = Float.parseFloat(strtok.nextToken());
                        notFound = FALSE;
                    }
                }

                if(TheKeyWord.equalsIgnoreCase("SCALE")) {
                    // Found a SCALE line. So we parse it. 
                    // It should have the following format:
                    // SCALE <sx>, <sy>, <sz>
                    // were sx, sy, and sz are scale values, 
                    // expressed as floating-point numbers.
                    String theSc, localSc;
                    // Skip over the word "SCALE"
                    // theSc = strtok(TheText + 6, BLANK);
                    int scIdx = TheText.indexOf("SCALE");
                    theSc = TheText.substring(scIdx + 6);
                    localSc = theSc;

                    if(checkFor3(localSc) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(localSc, ",");
                        sc.x = Float.parseFloat(numtok.nextToken());
                        sc.y = Float.parseFloat(numtok.nextToken());
                        sc.z = Float.parseFloat(numtok.nextToken());
                        notFound = FALSE;
                    }
                }

                if(TheKeyWord.equalsIgnoreCase("TRANSLATION")) {
                    // Found a TRANSLATION line. So we parse it. 
                    // It should have the following format:
                    // TRANSLATION <tx>, <ty>, <tz>
                    // where tx, ty, and tz are translation values, 
                    // expressed as floating-point numbers.
                    String theTr, localTr;
                    // Skip over the word "TRANSLATION"
                    // theTr = strtok(TheText + 12, BLANK);
                    int trIdx = TheText.indexOf("TRANSLATION");
                    theTr = TheText.substring(trIdx + 12);
                    localTr = theTr;

                    if(checkFor3(localTr) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(localTr, ",");
                        tr.x = Float.parseFloat(numtok.nextToken());
                        tr.y = Float.parseFloat(numtok.nextToken());
                        tr.z = Float.parseFloat(numtok.nextToken());
                        notFound = FALSE;
                    }
                }

                if(TheKeyWord.equalsIgnoreCase("ADJUSTCOLOR")) {
                    // Found an ADJUSTCOLOR line. So we parse it. 
                    // It should have the following format:
                    // ADJUSTCOLOR [Target|Relative] <R>, <G>, <B>
                    // where R, G, and B are RGB color values, 
                    // expressed as integers in the range from 0 to 255.
                    String adjustment, theColor;
                    // String adjustmentCopy; // This variable is not used
                    
                    // Skip over the word "ADJUSTCOLOR"
                    // adjustment = strtok(TheText + 12, BLANK);
                    adjustment = strtok.nextToken();

                    // adjustmentType should be "TARGET" or "RELATIVE"
                    adjustmentType = adjustment;
                    // int aLength = adjustment.length(); // This variable is no longer used
                    // Skip over both the words "ADJUSTCOLOR" and 
                    // "TARGET" or "RELATIVE"
                    // theColor = strtok(TheText + 12 + aLength + 1, BLANK);  // move forward to the RGB color
                    
                    int idx = TheText.indexOf("RELATIVE");
                    if (idx == -1) {
                        idx = TheText.indexOf("TARGET");
                        theColor = TheText.substring(idx + 6);
                    } else {
                        theColor = TheText.substring(idx + 9);
                    }

                    // Parse the R, G, B color values
                    numtok = new StringTokenizer(theColor, ",");
                    int r = Integer.parseInt(numtok.nextToken());
                    int g = Integer.parseInt(numtok.nextToken());
                    int b = Integer.parseInt(numtok.nextToken());
                    anAdjustment = new Color(r, g, b);
                    notFound = FALSE;
                }

                if(TheKeyWord.equalsIgnoreCase("MOTIONPATH")) {
                    // Found an MOTIONPATH line. So we parse it. 
                    // It should have the following format:
                    // MOTIONPATH [None|<pathName>]
                    // where pathName, if provided, is the path to a ".pth" file

                    // Skip over the word "MOTIONPATH"
                    // Now theMotionPath should be either "None" or the path
                    int idxMotionPath = TheText.indexOf("MOTIONPATH");
                    theMotionPath = TheText.substring(idxMotionPath + 11);

                    if(theMotionPath.length() == 0) {
                        errorText = "MotionPath file missing on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }
                    notFound = FALSE;
                }

                if(TheKeyWord.equalsIgnoreCase("ALPHAIMAGEPATH")) {
                    // Found an ALPHAIMAGEPATH line. So we parse it. 
                    // It should have the following format:
                    // ALPHAIMAGEPATH [None|<pathName>]
                    int idxAlphaImgPath = TheText.indexOf("ALPHAIMAGEPATH");
                    theAlphaPath = TheText.substring(idxAlphaImgPath + 15);

                    if(theAlphaPath.length() == 0) {
                        errorText = "Alpha Image Path file missing. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }
                    notFound = FALSE;
                }

                if(TheKeyWord.equalsIgnoreCase("FILENAME")) {
                    // Found a FILENAME line. So we parse it. 
                    // It should have the following format:
                    // FILENAME <pathName>
                    // where pathName, if provided, is the path to a file.
                    // If the model type is IMAGE or QUADMESH, then pathName
                    // should be the full path to a Windows bitmap (.bmp) file.
                    // If the model type is SHAPE, then pathName 
                    // should be the full path to a shape (.shp) file.
                    int idxFileName = TheText.indexOf("FILENAME");
                    theFileName = TheText.substring(idxFileName + 9);

                    // If the user previously specified either outWidth = 0 or outHeight = 0
                    // in the SCENE line ...
                    if(getOutImageSizeFlag == true) {
                        // Read the outWidth and outHeight values from the .bmp file
                        Integer bpp = 0;
                        int bmpStatus;
                        bmpStatus = Globals.readBMPHeader(theFileName, outImageRows, outImageCols, bpp);
                        if(bmpStatus != 0) {
                            errorText = "File name not valid. Line " + lineCounter;
                            // errorText will be printed with Global.statusPrint by the caller.

                            closeLineNumberReader(filein);
                            return -1;
                        }
                        sceneList.setSceneOutImageSize(outImageRows, outImageCols);
                        getOutImageSizeFlag = false;
                    }
                    notFound = FALSE;
                }

                // Look for other keywords - not model related
                if(TheKeyWord.equalsIgnoreCase("END")) {
                    String theToken, localToken;
                    int tokenIdx = TheText.indexOf("END");
                    theToken = TheText.substring(tokenIdx + 4);
                    localToken = theToken;

                    if(localToken.equalsIgnoreCase("COMPOUND")) {
                        compoundMMember = 0;
                        notFound = FALSE;
                    }
                }

                if (TheKeyWord.equalsIgnoreCase("EOF")) {
                    // Save the last model
                    // If the color is to be adjusted (i.e., we previously read an ADJUSTCOLOR line), 
                    // adjust it now and change the input image
                    // file name to point to the color corrected image.
                    if(adjustmentType.equalsIgnoreCase("None")) {
                        MemImage inputImage = new MemImage(theFileName, 0, 0, RANDOM, 'R', RGBCOLOR);
                        if (!inputImage.isValid()) {
                            msgBuffer = "sceneList.readList: Can't open image for color correction: " + theFileName;
                            Globals.statusPrint(msgBuffer);
                            return -1;
                        }
                        
                        MemImage correctedImage = new MemImage(inputImage);
                        Globals.statusPrint("Adjusting color image");	    
                        inputImage.adjustColor(anAdjustment.getRed(), anAdjustment.getGreen(), anAdjustment.getBlue(),
                            midRed, midGreen, midBlue, 
                            correctedImage, adjustmentType, 0);

                        FileUtils.constructPathName(colorAdjustedPath, theFileName, 'j');     
                        msgBuffer = "Saving adjusted color image: " + colorAdjustedPath;
                        Globals.statusPrint(msgBuffer);
                  
                        correctedImage.writeBMP(colorAdjustedPath);
                    }

                    if(compoundMMember == 1 && theType == COMPOUND) compoundMember = false;
                    if(compoundMMember == 1 && theType != COMPOUND) compoundMember = true;

                    myStatus = sceneList.addSceneElement(theModelName, theFileName, theBlend, theType,
                        theWarp, theAlpha, rt, sc, tr, theMotionPath, theAlphaPath,
                        compoundMember, anAdjustment, adjustmentType, colorAdjustedPath,
                        definedRefPoint, pointOfReference);
                    if(compoundMMember == 0) compoundMember = false;

                    if(myStatus != 0) {
                        errorText = "Could not add a model to scene list. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    closeLineNumberReader(filein);
                    return 0;
                } // if (TheKeyWord.equalsIgnoreCase("EOF"))
            
                if (notFound != 0) {
                    if(notFound == 1) {
                        errorText = "Unknown Keyword: " + TheKeyWord + "  Line " + lineCounter;
                    }
                    if(notFound == THREE_NUMBERS_NOT_FOUND) {
                        errorText = "Expected 3 numeric values separated by commas: " + TheKeyWord + "  Line " + lineCounter;
                    }
                    Globals.statusPrint(errorText);

                    closeLineNumberReader(filein);
                    return -1;
                } // if (notFound != 0)
                
                TheKeyWord = Shape3d.getNextLine(TheText, lineCounter, filein, minLineSize);
            } // while(!TheKeyWord.equalsIgnoreCase("SCENE"))
        } // while(true)
    } // readList


    // Called from:
    //     readList
    public int checkFor3(String aString) {
        // Perform a syntax check on the input string:
        // The string must have two commas and three numeric values
        int numChars = aString.length();
        char aChar;
        int iStrIdx = 0;
        
        int numCommas = 0;
        int numNumbers = 0;

        for (int j = 0; j < numChars; j++) {
            aChar = aString.charAt(j);
            if (aChar == ',') { 
              numCommas++;
              iStrIdx++;
            } else {
                if(Character.isDigit(aChar) || aChar == '.'|| aChar == '-') {
                    numNumbers++;
                }

                while (iStrIdx < numChars - 1 && 
                (Character.isDigit(aChar) || aChar == '.'|| aChar == '-')) {
                    iStrIdx++;
                    aChar = aString.charAt(iStrIdx);
                }
            }
        }

        if ((numCommas == 2) && (numNumbers == 3)) {
            return 1;
        } else {
            return 0;
        }
    } // checkFor3


    // Called from:
    //     readList
    private void closeLineNumberReader(LineNumberReader filein) {
        try {
            filein.close();
        } catch(IOException ioe) {
            // do nothing
        }
    } // closeLineNumberReader
} // ScnFileParser