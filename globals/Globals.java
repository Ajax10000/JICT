package globals;

import core.MemImage;
import core.SceneElement;
import core.Shape3d;

import java.io.File;

import math.TMatrix;
import math.Vect;

import structs.Point3d;

public class Globals {
    private static boolean ictdebug = false;

    // This variable came from ICT20.CPP
    public static Preference ictPreference = new Preference();  // declare a global preference object

    // This variable came from MAINFRM.CPP
    public static GPipe aGraphicPipe = new GPipe();  // a globally defined graphic pipeline for VRML viewing

    public static final int CHROMAVALUE  = 0;
    public static final int CHROMARED    = 0;
    public static final int CHROMAGREEN  = 0;
    public static final int CHROMABLUE   = 0;

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
    
    public static final float ZBUFFERMAXVALUE = 2.0E31f;

    // This value came from ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f;

    // This value came from IWARP.CPP:
    public static final int I_MAXWVERTICES = 8;

    // These values came from SHADERS.CPP
    // Define three standard types of triangle
    public static final int I_POINTONSIDE = 1;
    public static final int I_POINTONTOP = 2;
    public static final int I_POINTONBOTTOM = 3;

    public static void statusPrint(String aMessage) {
        File theLog;
        String msgText;
        String theString;
        theString = msgText; // why?
        CStatusBar pStatus;

        pStatus = null;
        // If the mainframe window is not open, post the message to the log file.
        // else display the message on the status bar and post it to the log file.
        if(AfxGetApp().m_pMainWnd > 0) {
            pStatus = (CStatusBar)AfxGetApp().m_pMainWnd.GetDescendantWindow(AFX_IDW_STATUS_BAR);
        }
      
        theLog = fopen(ictPreference.getPath(Preference.ProcessLog), "a+");
        if (theLog == null) {
            if(AfxGetApp().m_pMainWnd > 0) {
                pStatus.SetPaneText(0, "statusPrint: Unable to open the ICT log file ict.log");
                pStatus.UpdateWindow();
            }
            return;
        }

        theString = aMessage;
        int myLength = theString.length();
        *(theString + myLength) = '\n';
        *(theString + myLength + 1) = '\0';
        fwrite(theString, theString.length(), 1, theLog);
        fclose(theLog);
        //
        // Display the message immediately on the status bar
        if(AfxGetApp().m_pMainWnd > 0) {
            pStatus.SetPaneText(0, aMessage);
            pStatus.UpdateWindow();
        }
    } // statusPrint


    // This method came from UTILS.CPP
    public static boolean fileExists(String psPathName) {
        File stream;
        if( (stream  = fopen(psPathName, "r")) == null ) {
           return false;
        } else {
           fclose(stream);
           return true;
        }
    } // fileExists


    // This method came from UTILS.CPP
    public static float interpolate(float desired1, float desired2, float reference1, float reference2, float referenceCurrent) {
        if(reference1 == reference2) { 
            return desired1;
        }

        return desired1 - (desired1 - desired2) * ((reference1 - referenceCurrent) / 
                          (reference1 - reference2));
    } // interpolate
      
        
    // This method came from UTILS.CPP
    public static float fPolar(float angle) {
        if(angle > 0.0f) {
            while(angle >= 360.0f) {
                angle -= 360.0f;
            }
        } else {
            while(angle <= 0.0f) {
                angle += 360.0f;
            }
        }

        if(angle == 360.0f) {
            angle = 0.0f;
        }

        return angle;
    } // fPolar
    

    // This method came from UTILS.CPP
    public static float polarAtan(float run, float rise) {
        //  This arcTangent returns a result between 0 and 2Pi radians;
        float rayAngle = atan2(rise, run);
        if(rayAngle < 0.0f) {
            rayAngle = 3.1415926f + (3.1415926f + rayAngle);
        }

        return rayAngle;
    } // polarAtan
    

    // This method came from UTILS.CPP
    public static float bound(float value, float minValue, float maxValue) {
        if (value < minValue) {
            value = minValue;
        }

        if(value > maxValue) {
            value = maxValue;
        }

        return value;
    } // bound
    

    // This method came from UTILS.CPP
    public static void makePath(String currentPath, String inPath, String prefix, int frameCounter, String inSuffix) {
        sprintf(currentPath, "%s%.31s%#04d%s.bmp\0", inPath, prefix, frameCounter, inSuffix);
    } // makePath
      

    // This method came from UTILS.CPP
    public static int getPathPieces(String firstImagePath, String directory, String fileName,
      String prefix, Integer frameNum, String inSuffix) {
        String ddrive, dext, aFrameNum, tempDirectory;
        char aDot;
        aDot = '.';
      
       _splitpath(firstImagePath, ddrive, directory, fileName, dext);

       // Assumed input:   xxxxx0000c
       tempDirectory = ddrive + directory;
       directory = tempDirectory;
      
       int aLen = fileName.length();
       
       *inSuffix = *(fileName + aLen - 1);
       *(inSuffix + 1) = 0;
      
        strncpy(aFrameNum, fileName + aLen - 5,4);
       *(aFrameNum + 4) = 0;
       *frameNum = Integer.parseInt(aFrameNum);
       
       strncpy(prefix, fileName, aLen - 5);
       *(prefix + aLen - 5) = 0;
      
        return 0;
    } // getPathPieces


    // This method came from DEPTHSRT.CPP
    public static float getDistance2d(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    } // getDistance2d
      

    // This method came from DEPTHSRT.CPP
    public static float getDistance3d(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float)Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)) +
          ((z1 - z2) * (z1 - z2)));
    } // getDistance3d


    // This method came from DEPTHSRT.CPP
    public static void insertionSort2(float theItems[], SceneElement itemData[], int numItems) {
        float itemTemp, theValue;
        SceneElement itemDataTemp;
        int index, indexTmp;

        // Sort theItems array into decending order for depth sorting
        for(index = 0; index < numItems; index++) {
            itemTemp     = theItems[index];
            itemDataTemp = itemData[index];
            theValue     = theItems[index];

            for(indexTmp = index; indexTmp > 0; indexTmp--) {
                if(theItems[indexTmp - 1] < theValue) {
                    theItems[indexTmp] = theItems[indexTmp - 1];
                    itemData[indexTmp] = itemData[indexTmp - 1];
                } else {
                    break;
                }
            } // for indexTmp

            // Insert the original item in the temporary position.
            theItems[indexTmp] = itemTemp;
            itemData[indexTmp] = itemDataTemp;
        } // for index
    } // insertionSort2


    // This method came from BLEND.CPP
    public static int blend(MemImage inImage, MemImage maskImage, MemImage outImage, float alphaScale) {
        // Blend over the common area in input and mask images
        int inputRows  = inImage.getHeight();
        int inputCols  = inImage.getWidth();
        int maskRows   = maskImage.getHeight();
        int maskCols   = maskImage.getWidth();
        int commonRows = Math.min(inputRows, maskRows);
        int commonCols = Math.min(inputCols, maskCols);

        // Each MemImage is assumed to be opened for random access
        int x, y;
        byte maskPixel, inPixel, outPixel, addedPixel;
        float inWeight, outWeight;

        for(y = 1; y <= commonRows; y++) {
            for(x = 1; x <= commonCols; x++) {
                maskPixel = maskImage.getMPixel(x, y);
                inPixel   = inImage.getMPixel(x, y);
                if(maskPixel > 0 && inPixel > 0) {
                    outPixel = outImage.getMPixel(x, y);
                    inWeight = (float)maskPixel / 255.0f * alphaScale;
                    outWeight = 1.0f - inWeight;

                    if(alphaScale > 0.0) {
                        addedPixel = (inWeight * (float)inPixel) + (outWeight * (float)outPixel) + 0.5f;
                    } else {
                        addedPixel = (float)outPixel + (inWeight * (float)inPixel) + 0.5f;
                        // Make certain shadows won't produce negative values
                        if (addedPixel > outPixel) {
                            addedPixel = outPixel;
                        }
                    }

                    if (addedPixel < 1) {
                        addedPixel = 1;
                    }

                    if (alphaScale == 0.0) {
                        addedPixel = 0;
                    }

                    outImage.setMPixel(x, y, addedPixel);
                }
            }
        }

        return 0;
    } // blend
  

    // This method came from BLEND.CPP
    public static int blendz(MemImage inImage, MemImage matteImage, 
    MemImage zImage, MemImage zBuffer,
    MemImage outImage,
    float alphaScale) {

        // zImage is the rendered model's zBuffer image
        // zBuffer is the effect frame's zBuffer image
        // Both of these need to be considered in a zBuffer render operation since each zBuffered
        // model contributes to the rendered effect frame's zBuffer
        //
        // Blend over the common area in input and matte images
        int inputRows  = inImage.getHeight();
        int inputCols  = inImage.getWidth();
        int matteRows  = matteImage.getHeight();
        int matteCols  = matteImage.getWidth();
        int commonRows = Math.min(inputRows, matteRows);
        int commonCols = Math.min(inputCols, matteCols);

        int bpp = inImage.getBitsPerPixel();
        int outBPP =  outImage.getBitsPerPixel();
        if(outBPP != bpp) {
            String msgText = "blendz: inImage bpp: " + bpp + " must match outImage bpp: " + outBPP;
            statusPrint(msgText);
            return -1;
        }

        int matteBPP = matteImage.getBitsPerPixel();
        if(matteBPP != 8) {
            statusPrint("blendz: Matte image must be 8 bits per pixel");
            return -2;
        }

        // Each image is assumed to be opened for random access
        int x, y;
        byte mattePixel, inPixel, outPixel, addedPixel;
        byte inRed, inGreen, inBlue, outRed, outGreen, outBlue;
        byte addedRed, addedGreen, addedBlue;
        float inWeight, outWeight;
    
        boolean usingZBuffer = false;
        if((zImage != null) && (zBuffer != null)) {
            usingZBuffer = true;
        }
    
        for(y = 1; y <= commonRows; y++) {
            for(x = 1; x <= commonCols; x++) {
                mattePixel = matteImage.getMPixel(x, y);
                switch(bpp) {  // Optionally blend in color or monochrome
                case 8:
                    inPixel = inImage.getMPixel(x, y);
                    if((mattePixel > CHROMAVALUE) && (inPixel > CHROMAVALUE)) {
                        outPixel = outImage.getMPixel(x, y );
                        inWeight = (float)mattePixel / 255.0f * alphaScale;
                        outWeight = 1.0f - inWeight;

                        if(alphaScale > 0.0f) {
                            addedPixel = (inWeight * (float)inPixel) + (outWeight *(float)outPixel) + 0.5f;
                        } else {
                            addedPixel = (float)outPixel + (inWeight *(float)inPixel) + 0.5f;
                            // Make certain shadows won't produce negative intensities
                            if (addedPixel > outPixel) {
                                addedPixel = outPixel;
                            }
                        }

                        if (addedPixel < 1) {
                            addedPixel = (byte)1;
                        }
                        if (alphaScale == 0.0f) {
                            addedPixel = (byte)0;
                        }

                        if(usingZBuffer) {
                            if(zImage.getMPixel32(x, y) < zBuffer.getMPixel32(x, y)) { 
                                zBuffer.setMPixel32(x, y, zImage.getMPixel32(x, y));
                                outImage.setMPixel(x, y, addedPixel);
                            }
                        } else {
                            outImage.setMPixel(x, y, addedPixel);
                        }
                    } // end if non-zero values
                    break;
        
                case 24:                           // RGB Blend with Z-Buffer
                    inImage.getMPixelRGB(x, y, inRed, inGreen, inBlue);
                    if((mattePixel > CHROMAVALUE) && (inGreen > CHROMAVALUE)) {
                        outPixel = outImage.getMPixelRGB(x, y, outRed, outGreen, outBlue);
                        inWeight = (float)mattePixel / 255.0f * alphaScale;
                        outWeight = 1.0f - inWeight;

                        if(alphaScale > 0.0f) {
                            addedRed   = (inWeight * (float)inRed)   + (outWeight *(float)outRed)   + 0.5f;
                            addedGreen = (inWeight * (float)inGreen) + (outWeight *(float)outGreen) + 0.5f;
                            addedBlue  = (inWeight * (float)inBlue)  + (outWeight *(float)outBlue)  + 0.5f;
                        } else {  // shadow
                            addedRed   = (float)outRed   + (inWeight *(float)inRed)   + 0.5f;
                            addedGreen = (float)outGreen + (inWeight *(float)inGreen) + 0.5f;
                            addedBlue  = (float)outBlue  + (inWeight *(float)inBlue)  + 0.5f;

                            // Make certain shadows won't produce negative intensities
                            if (addedRed > outRed)     addedRed = outRed;
                            if (addedGreen > outGreen) addedGreen = outGreen;
                            if (addedBlue > outBlue)   addedBlue = outBlue;
                        }

                        if (addedRed < 1)   addedRed   = (byte)1;
                        if (addedGreen < 1) addedGreen = (byte)1;
                        if (addedBlue < 1)  addedBlue  = (byte)1;
                        if (alphaScale == 0.0f) {
                            addedRed   = (byte)0;
                            addedGreen = (byte)0;
                            addedBlue  = (byte)0;
                        }

                        if(usingZBuffer) {
                            if(zImage.getMPixel32(x, y) < zBuffer.getMPixel32(x, y)) { 
                                zBuffer.setMPixel32(x, y, zImage.getMPixel32(x, y));
                                outImage.setMPixelRGB(x, y, addedRed, addedGreen, addedBlue);
                            }
                        } else {
                            outImage.setMPixelRGB(x, y, addedRed, addedGreen, addedBlue);
                        }
                    } // end if non zero values
                    break;
                } // switch
            } // for x
        } // for y

        return 0;
    } // blendz
  

    // This method came from BLEND.CPP
    public static int createCutout(MemImage pOriginalImage, MemImage pMaskImage,
    String psCutoutName, Shape3d aShape) {
        String msgText;

        // Create the cutout image and translate the shape to coincide with the cutout.
        // assumes the mask image is an unpacked (8 bit) mask image opened RANDOM,
        // The original must be opened for sequential access.
        // cutoutName: name of cutout image and shape file without the suffix
        if(pOriginalImage.getAccessMode() != SEQUENTIAL) {
            statusPrint("createCutout: original image access mode must be SEQUENTIAL");
            return 1;
        }

        if (pOriginalImage.getColorSpec() == ONEBITMONOCHROME) {
            statusPrint("createCutout: original image colorSpec cannot be ONEBITMONOCHROME");
            return 2;
        }

        // A cutout version of both the mask and original
        // image is created in which the zero pixel border is removed.
        String sCutoutRImage, sCutoutGImage, sCutoutBImage, sCutoutMImage;
        String sCutoutRGBImage;

        // Prepare pathnames for mask and cutout images
        String sCutoutDir, sMaskDir;
        sCutoutDir = ictPreference.getPath(Preference.InputImageDirectory);
        sMaskDir   = ictPreference.getPath(Preference.MaskImageDirectory);
        String sCutoutPath, sMaskPath;
    
        appendFileName(sCutoutRImage, psCutoutName, "r");
        appendFileName(sCutoutGImage, psCutoutName, "g");
        appendFileName(sCutoutBImage, psCutoutName, "b");
        appendFileName(sCutoutMImage, psCutoutName, "a");
        appendFileName(sCutoutRGBImage, psCutoutName, "c");
        sCutoutPath = sCutoutDir + sCutoutRGBImage;
        sMaskPath   = sMaskDir + sCutoutMImage;
    
        int iMaskHeight, iMaskWidth;
        iMaskHeight = pMaskImage.getHeight();
        iMaskWidth  = pMaskImage.getWidth();
        int y, minX, maxX, minY, maxY;
    
        if ((aShape != null) && (aShape.getNumVertices() > 0)) {
            aShape.worldBoundingBox();
            minY = (int)aShape.minY;
            maxY = (int)aShape.maxY;
            minX = (int)aShape.minX;
            maxX = (int)aShape.maxX;
        } else {
            statusPrint("createCutout: Shape object not supplied or has 0 vertices");
            return -1;
        }

        // Calculate the size of the new images
        // use the shape and enlarge by 1 pixel on each side to account for round off
        // error in the centroid calculation
        int newMinY = minY - 1;
        if (newMinY < 1) {
            newMinY = 1;
        }

        int newMaxY = maxY + 1;
        if (newMaxY > iMaskHeight) {
            newMaxY = iMaskHeight;
        }

        int newMinX = minX - 1;
        if (newMinX < 1) {
            newMinX = 1;
        }

        int newMaxX = maxX + 1;
        if (newMaxX > iMaskWidth) {
            newMaxX = iMaskWidth;
        }

        // Translate the boundary so it is centered within the cutout image
        int iCutoutHeight = newMaxY - newMinY + 1;
        int iCutoutWidth  = newMaxX - newMinX + 1;
        int dx = -(minX - 3);  // screen coords are 0 relative,
        int dy = -(minY - 2);  // memImage coords are 1 relative
        aShape.translateW(dx, dy, 0L);

        // Create the shape file name and write out the translated shape file
        String sShapeDir;
        String sShapeName;
        sShapeDir = ictPreference.getPath(Preference.ShapeFileDirectory);
        sShapeName = sShapeDir + psCutoutName + ".shp";

        msgText = "createCutout: Saving shape file: " + sShapeName;
        statusPrint(msgText);

        aShape.invertY(iCutoutHeight);
        int myStatus = aShape.writeShape(sShapeName);
        if(myStatus != 0) {
            msgText = "createCutout: Unable to save the shape file. " + myStatus;
            statusPrint(msgText);
            return -1;
        }

        // Open two new output images
        boolean color = false;
        if(pOriginalImage.getColorSpec() == RGBCOLOR) {
            color = true;
        }

        MemImage cutoutM = new MemImage(iCutoutHeight, iCutoutWidth);
        if (!cutoutM.isValid()) {
            msgText = "createCutout: Unable to open cutout alpha image: " + sCutoutMImage;
            statusPrint(msgText);
            return 3;
        }
    
        MemImage cutoutG = new MemImage(iCutoutHeight, iCutoutWidth);
        if (!cutoutG.isValid()) {
            msgText = "createCutout: Unable to open cutout g image: " + sCutoutGImage;
            statusPrint(msgText);
            return 4;
        }

        MemImage cutoutR, cutoutB;
        if(color) {
            cutoutR = new MemImage(iCutoutHeight, iCutoutWidth);
            if (!cutoutR.isValid()) {
                msgText = "createCutout: Unable to open cutout r image: " + sCutoutRImage;
                statusPrint(msgText);
                return 5;
            }

            cutoutB = new MemImage(iCutoutHeight, iCutoutWidth);
            if (!cutoutB.isValid()) {
                msgText = "createCutout: Unable to open cutout b image: " + sCutoutBImage;
                statusPrint(msgText);
                return 6;
            }
        }
    
        for(y = 1; y < iMaskHeight-newMaxY; y++) {
            pOriginalImage.readNextRow();
        }

        int iyCounter = 1;
        for (y = iMaskHeight-newMaxY; y <= iMaskHeight-newMinY; y++) {
            pOriginalImage.readNextRow();
            int ixCounter = 0;

            for(int x = newMinX ;x <= newMaxX; x++) {
                ixCounter++;
                byte theMaskValue = pMaskImage.getMPixel(x, y);

                if(theMaskValue > 0) {
                    cutoutM.setMPixel(ixCounter, iyCounter, theMaskValue);
                    if(!color) {
                        cutoutG.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(x, 1));
                    }
            
                    if(color) {
                        cutoutR.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(x, 1, 'R'));
                        cutoutG.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(x, 1, 'G'));
                        cutoutB.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(x, 1, 'B'));
                    }
                } else {
                    cutoutM.setMPixel(ixCounter, iyCounter, (byte)0);
                    cutoutG.setMPixel(ixCounter, iyCounter, (byte)0);
                    if(color) {
                        cutoutR.setMPixel(ixCounter, iyCounter, (byte)0);
                        cutoutB.setMPixel(ixCounter, iyCounter, (byte)0);
                    }
                }
            } // for x

            iyCounter++;
        } // for y

        // Smooth the mask
        statusPrint("createCutout: Smoothing the cutout mask");
        cutoutM.alphaSmooth5();
    
        cutoutG.writeBMP(sCutoutGImage);
        msgText = "createCutout: Saving alpha image: " + sMaskPath;
        statusPrint(msgText);
        cutoutM.writeBMP(sMaskPath);
    
        if(color) {
            cutoutR.writeBMP(sCutoutRImage);
            cutoutB.writeBMP(sCutoutBImage);
            msgText = "createCutout: Saving color cutout image: " + sCutoutPath;
            statusPrint(msgText);
            makeRGBimage(sCutoutRImage, sCutoutGImage, sCutoutBImage, sCutoutPath);
        }

        return 0;
    } // createCutout


    //----------------------------------------------------------------------
    //	 Author:  Tim Feldman, Island Graphics Corporation  
    //
    //  (The original code was published in Graphics Gems III without restrictions 
    //   and is in the public domain.  Tim, if you are reading this....thanks!).
    //
    //   Modified: Tim Wittenburg May 1996
    //   The original procedure found contours in a grayscale image.
    //   This procedure finds the boundary of a single blob in a binary image.
    //
    //	 Populates a shape3d object with vertices from a blob in a binary image.
    //   The number of vertices produced is reduced substantially by saving 
    //   only points in which the direction changes from the previous point.
    //

    /*	these are the direction values:

        0	right
        1	right and up
        2	up
        3	left and up
        4	left
        5	left and down
        6	down
        7	right and down		
    */



    // This method came from BLEND.CPP
    //
    //	in_boundary(x, y)
    //
    //	Determines whether the intensity at 'x, y' is within the boundary
    //	being outlined. Points outside of the array of intensities are not
    //	in the boundary.
    //
    //	Returns 0 if the point is not in the boundary.
    //	Returns 1 if the point is     in the boundary.
    //
    //
    public static boolean in_boundary(MemImage anImage, int x, int y) {
        int imHeight = anImage.getHeight();
        int imWidth = anImage.getWidth();
        int bpp = anImage.getBitsPerPixel();
        byte red, green, blue;

        if ( (x < 1) || (x > imWidth) || (y < 1) || (y > imHeight) ) {
            return false;
        }

        switch(bpp) {
        case 8:
            if (anImage.getMPixel(x, y) != CHROMAVALUE) {
                return true;
            } else {
                return false;
            }
            break;

        case 24:
            anImage.getMPixelRGB(x, y, red, green, blue);
            if (
            (red != CHROMARED) || 
            (green != CHROMAGREEN) ||
            (blue != CHROMABLUE)) {
                return true;
            } else {
                return false;
            }
            break;

        default:
            statusPrint("in_boundary: Image must have 8 or 24 bit pixels");
            return false;
            break;
        } // switch
    } // in_boundary


    // This method came from BLEND.CPP
    //
    //	probe(x, y, dir, new_x, new_y)
    //
    //	Checks a sample neighboring 'x, y' to see if it is in the boundary
    //	being outlined.  'dir' specifies which neighboring sample to check.
    //	'new_x, new_y' always get the coordinates of the neighbor.
    //
    //	Returns 0 if the neighbor is not in the boundary.
    //	Returns 1 if the neighbor is     in the boundary.
    //
    //
    public static int probe(MemImage anImage, int x, int y, int dir, Integer new_x, Integer new_y) {
        // Figure out coordinates of neighbor
        if ( (dir < 2) || (dir > 6) ) {
            ++x;
        }

        if ( (dir > 2) && (dir < 6) ) {
            --x;
        }

        if ( (dir > 0) && (dir < 4) ) {
            ++y;
        }

        if (dir > 4) {
            --y;
        }

        // always return the new coordinates
        new_x = x;
        new_y = y;

        // determine if the new sample point is in the boundary
        return (in_boundary(anImage, x, y));
    } // probe


    // This method came from BLEND.CPP
    //
    //	neighbor(x, y, last_dir, new_x, new_y)
    //
    //	Finds a neighbor of the sample at 'x, y' that is in the same
    //	boundary.  Always follows the boundary in a clockwise direction.
    //	'last_dir' is the direction that was used to get to 'x, y'
    //	when it was found.  'new_x, new_y' always get the coordinates
    //	of the neighbor.
    //
    //	This procedure should only be called for a sample that has at
    //	least one neighbor in the same boundary.
    //
    //	Returns the direction to the neighbor.
    public static int neighbor(MemImage anImage, int x, int y, int last_dir, Integer new_x, Integer new_y) {
        int	n;
        int	new_dir;

        /*	figure out where to start looking for a neighbor --
            always look ahead and to the left of the last direction

            if the last vector was 0
            then start looking at  1

            if the last vector was 1
            then start looking at  3

            if the last vector was 2
            then start looking at  3

            if the last vector was 3
            then start looking at  5

            if the last vector was 4
            then start looking at  5

            if the last vector was 5
            then start looking at  7

            if the last vector was 6
            then start looking at  7

            if the last vector was 7
            then start looking at  1	*/

        if ((last_dir & 0x01) != 0) {
            // last dir is odd -- add 2 to it
            new_dir = last_dir + 2;
        } else {
            // last dir is even -- add 1 to it
            new_dir = last_dir + 1;
        }

        // Keep new_dir in the range 0 through 7
        if (new_dir > 7) {
            new_dir -= 8;
        }

        // Probe the neighbors, looking for one on the edge
        for (n = 0; n < 8; n++) {
            if (probe(anImage, x, y, new_dir, new_x, new_y) != 0) {
                // Found the next clockwise edge neighbor --
                // its coordinates have already been
                // stuffed into new_x, new_y

                return(new_dir);
            } else {
                // check the next clockwise neighbor
                if (--new_dir < 0) {
                    new_dir += 8;
                }
            }
        }

        return 0;
    } // neighbor


    // This method came from BLEND.CPP
    //
    //	shapeFromImage
    //
    //	Builds a shape object that describes the boundary of the first contigous 
    //   blob of non-zero pixels it finds.  Always follows the boundary
    //	in a clockwise direction.  Uses 'start_x, start_y' as the
    //	starting point. 
    //
    //	Returns 0 if successful.
    //	Returns 1 if unsuccessful.
    public static int shapeFromImage(MemImage anImage, Shape3d aShape) {
        int	x, y;
        Integer	new_x, new_y;
        int	dir, last_dir;
        int start_x = 1;
        int start_y = 1;
        int row, col;
        int counter = 0;

        int imHeight = anImage.getHeight();
        int imWidth = anImage.getWidth();
        int bpp = anImage.getBitsPerPixel();
        if((bpp != 8) && (bpp != 24)) {
            statusPrint("shapeFromImage: Binary image must have 8 or 24 bit pixels.");
            return -1;
        }

        // Find the first point on the boundary
        start_x = -1;
        start_y = -1;

        // Start at the left-top corner of the image, scanning from left to right
        for (row = imHeight; row >= 1; row--) {
            for (col = 1; col <= imWidth; col++) {
                if(in_boundary(anImage, col, row)) {
                    start_x = col;
                    start_y = row;
                    goto nextStep;
                }
            }
        }

        nextStep:
        if((start_x < 0) || (start_y < 0)) {
            statusPrint("shapeFromImage: Binary image has no non-zero pixels");
            return -1;
        }

        //	go left in the starting row until out of the boundary
        while (in_boundary(anImage, start_x, start_y)) {
            --start_x;
        }
        
        // Move back right one point, to the leftmost edge
        // in the boundary, in that row
        start_x++;

        // Check if the starting point has no neighbors in the boundary --
        // the starting direction to check is arbitrary	*/
        x = start_x;
        y = start_y;

        dir = 0;

        for ( ; ; ) {
            if (probe(anImage, x, y, dir, new_x, new_y) != 0) {
                // Found a neighbor in that direction (its coordinates are in new_x, new_y
                // but we don't use them here)

                break;
            }

            // Try next direction
            if (++dir == 8) {
                //	starting point has no neighbors -- make the chain one vector long
                
                // fill in the vector -- the direction is arbitrary,
                // since the point is isolated
                aShape.addWorldVertex((float)new_x, (float)new_y, 0.0f);

                return 0;
            }
        }

        // Get ready to follow the edge -- since we are at the left edge,
        // force initial probe to be to upper left by initializing last_dir to 1
        last_dir = 1;

        // Follow the edge clockwise
        for ( ; ; ) {
            // Get the next point on the edge and the vector to it
            dir = neighbor(anImage, x, y, last_dir, new_x, new_y);

            // Add the new point
            if(dir != last_dir) {
                aShape.addWorldVertex((float)new_x, (float)new_y, 0.0f);
            }

            // maybe done with boundary
            if ( (new_x == start_x) && (new_y == start_y) ) {
                return 0;
            }

            // else get ready to continue following the edge
            x = new_x;
            y = new_y;
            last_dir = dir;
        }

        return 0;
    } // shapeFromImage


    // This method came from MOTION.CPP
    public static String getNextMotionLine(String theText, Integer lineNumber, ifstream *filein) {
        boolean aComment;
        int theLength = 80;
        String theKeyWord;
        aComment = true;
      
        while (aComment) {
            filein.getline(theText, theLength);  //ignore comments and near empty lines
            if(filein.eof()) {
                theText = "EOF";
                theKeyWord = theText;
                return(theKeyWord);
            }

            lineNumber++;
            if (strncmp(theText,"//",2) == 0 || strlen(theText) < 2) //single C/R
                aComment = true;
            else
                aComment = false;
        }

        theKeyWord = theText;
        return(theKeyWord);
    } // getNextMotionLine


    // This method came from MOTION.CPP
    public static int motionBlur(String firstImagePath, String outputDir, int numFrames, int blurDepth) {
        String msgText;
        MemImage[] images = new MemImage[32]; 
        MemImage outImage;
        String directory, fileName, prefix, inSuffix;
        String currentPath, inPath;
        String outPath, outSuffix;
        byte red, green, blue;
        int blur, numOpenImages, bucket, redBucket, greenBucket, blueBucket;
        int frameNum, i, j, status;
        int imHeight, imWidth, bpp, frameCounter, row, col;

        if(blurDepth > 15) {
            statusPrint("motionBlur: blurDepth cannot be > 15");
            return -1;
        }

        // the directory includes the drive letter
        status = getPathPieces(firstImagePath, directory, fileName, prefix, 
            frameNum, inSuffix);
        if(status != 0) {
            statusPrint("motionBlur: Check the first image pathname");
            return -2;
        }

        status = readBMPHeader(firstImagePath, imHeight, imWidth, bpp);
        if(status != 0) {
            msgText = "motionBlur: Cannot open: " + firstImagePath;
            statusPrint(msgText);
            return -3;
        }

        numOpenImages = 2 * blurDepth + 1;

        for (frameCounter = frameNum + blurDepth; frameCounter <=frameNum + numFrames - blurDepth; frameCounter++) {
            //  Open and close the appropriate images
            if(frameCounter == frameNum + blurDepth) {
                for(i = -blurDepth; i <= blurDepth; i++) { // open the first blurDepth images
                    makePath(currentPath, directory, prefix, frameCounter + i, inSuffix);
                    switch(bpp) {
                    case 8:
                        images[i + blurDepth] = new MemImage(currentPath, 0, 0, RANDOM, 'R', EIGHTBITMONOCHROME);
                        break;

                    case 24:
                        images[i + blurDepth] = new MemImage(currentPath, 0, 0, RANDOM, 'R', RGBCOLOR);
                        break;
                    } // switch

                    if(!images[i + blurDepth].isValid()) {
                        msgText = "motionBlur: Unable to open image: " + currentPath;
                        statusPrint(msgText);
                        return -4;
                    }
                }
            } else {
                delete images[0];               //close oldest image
                for (j = 0; j < numOpenImages - 1; j++) {//move the image pointers
                    images[j] = images[j + 1];
                }
                                                //open new image
                makePath(currentPath, directory, prefix, frameCounter + blurDepth, inSuffix);
                switch(bpp) {
                case 8:
                    images[numOpenImages - 1] = new MemImage(currentPath, 0, 0, RANDOM, 'R', EIGHTBITMONOCHROME);
                    break;

                case 24:
                    images[numOpenImages - 1] = new MemImage(currentPath, 0, 0, RANDOM, 'R', RGBCOLOR);
                    break;
                } // switch

                if(!images[numOpenImages - 1].isValid()) {
                    msgText = "motionBlur: Unable to open image 2: " + currentPath;
                    statusPrint(msgText);
                    return -4;
                }
            }

            // Blur the images
            float avgBucket, avgRedBucket, avgGreenBucket, avgBlueBucket;

            outSuffix = "b";
            makePath(outPath, outputDir, prefix, frameCounter, outSuffix);
            outImage = new MemImage(imHeight, imWidth, bpp);

            for (row = 1; row < imHeight; row++) {
                for (col = 1; col < imWidth; col++) {
                    bucket = 0;
                    redBucket = 0;
                    greenBucket = 0;
                    blueBucket = 0;

                    for (blur = 0; blur < numOpenImages; blur++) {
                        switch (bpp) {
                        case 8:
                            bucket += images[blur].getMPixel(col, row);
                            break;

                        case 24:
                            images[blur].getMPixelRGB(col, row, red, green, blue);
                            redBucket += red;
                            greenBucket += green;
                            blueBucket += blue;
                            break;

                        default:
                            statusPrint("motionBlur: image must be 8 or 24 bits per pixel");
                            return -1;
                            break;
                        }  // switch
                    } // end accumulation loop

                    switch(bpp) {
                    case 8:
                        avgBucket = bucket / numOpenImages;
                        outImage.setMPixel(col, row, (byte)(avgBucket + 0.5));
                        break;

                    case 24:
                        avgRedBucket = redBucket / numOpenImages;
                        avgGreenBucket = greenBucket / numOpenImages;
                        avgBlueBucket = blueBucket / numOpenImages;
                        outImage.setMPixelRGB(col, row, 
                            (byte)(avgRedBucket + 0.5),
                            (byte)(avgGreenBucket + 0.5),
                            (byte)(avgBlueBucket + 0.5));
                        break;
                    } // switch
                }  //end inner loop
            }  //end outer loop

            //  Save the blurred image
            msgText = "Saving: " + outPath;
            statusPrint(msgText);
            outImage.writeBMP(outPath);
        }   //end sequence loop;

        //  Close the remaining images
        for(i = 0; i < numOpenImages; i++) {
            delete images[i];
        }

        return 0;
    } // motionBlur


    // This method came from SceneList
    public static void appendFileName(String psOutputFileName, String psPrefix, String psSuffix) {
        sprintf(psOutputFileName, "%.31s%s.bmp\0", psPrefix, psSuffix);
    } // appendFileName


    public static void constructPathName(String outPath, String inPath, char lastLetter) {
        String drive, dir, file, ext;
        _splitpath(inPath, drive, dir, file, ext);
        int theLength = file.length();

        if(theLength > 0) {
            *(file + theLength - 1) = lastLetter;  // Substitute a letter
        }

        _makepath(outPath, drive, dir, file, ext);
    } // constructPathName


    // This method came from IWARP.CPP
    //	iwarpz  - zbuffered planar texture mapping
    public static int iwarpz(MemImage inImage, MemImage outImage, MemImage zImage,
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    float vx, float vy, float vz,
    TMatrix viewMatrix,
    float refPointX, float refPointY, float refPointZ) {
        // To use this function without a zBuffer, call with zImage = null.
        // in this case, vx, vy, and vz are ignored
        String msgText;
        int x, y;
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES], tXCoords = new float[I_MAXWVERTICES], tYCoords = new float[MAXWVERTICES];

        //  The shape object contains the projected 4 sided polygon and a z coordinate
        //  at each of the projected vertices.
        if(ictdebug) {
            statusPrint("iwarpz input arguments");
            sprintf(msgText, "rx: %6.2f  ry: %6.2f  rz: %6.2f", rx, ry, rz);
            statusPrint(msgText);

            sprintf(msgText, "sx: %6.2f  sy: %6.2f  sz: %6.2f", sx, sy, sz);
            statusPrint(msgText);

            sprintf(msgText, "tx: %6.2f  ty: %6.2f  tz: %6.2f", tx, ty, tz);
            statusPrint(msgText);

            sprintf(msgText, "refx: %6.2f  refy: %6.2f  refz: %6.2f", refPointX, refPointY, refPointZ);
            statusPrint(msgText);
        }

        //  Build the forward and inverse transformation matrices
        TMatrix forwardMatrix = new TMatrix();
        float XRadians = rx * F_DTR;
        float YRadians = ry * F_DTR;
        float ZRadians = rz * F_DTR;
        forwardMatrix.scale(sx, sy, sz);
        forwardMatrix.rotate(XRadians, YRadians, ZRadians);
        forwardMatrix.translate(tx, ty, tz);
        TMatrix viewModelMatrix = new TMatrix();
        viewModelMatrix.multiply(viewMatrix, forwardMatrix);

        TMatrix inverseMatrix = new TMatrix(viewModelMatrix);  // copy the forward transform
        inverseMatrix.invertg();                               // and invert it

        if(ictdebug) {
            forwardMatrix.display("Forward Matrix:");
            inverseMatrix.display("Inverse Matrix:");
        }

        int bpp       = inImage.getBitsPerPixel();
        int inHeight  = inImage.getHeight();
        int inWidth   = inImage.getWidth();
        int outHeight = outImage.getHeight();
        int outWidth  = outImage.getWidth();
        float halfInHeight = inHeight / 2.0f;
        float halfInWidth  = inWidth / 2.0f;

        float xCentOffset = (outWidth - inWidth) / 2.0f;
        float yCentOffset = (outHeight - inHeight) / 2.0f;

        if(ictdebug) {
            msgText = "iWarpz: Viewer location: vx: " + vx + ", vy: " + vy + ", vz: " + vz;
            statusPrint(msgText);
        }

        // iwarpz uses a reference point defined in pixel space.
        // Convert it now.
        float intRefPointX = refPointX + halfInWidth;
        float intRefPointY = refPointY + halfInHeight;
        float intRefPointZ = refPointZ;

        // Load a shape object with the original image boundary coordinates
        Shape3d aShape = new Shape3d(4);
        aShape.addWorldVertex(1.0f, 1.0f, 0.0f);
        aShape.addWorldVertex((float)inWidth, 1.0f, 0.0f);
        aShape.addWorldVertex((float)inWidth, (float)inHeight, 0.0f);
        aShape.addWorldVertex(1.0f, (float)inHeight, 0.0f);

        // Transform and project the image coords, taking into account the reference point
        viewModelMatrix.transformAndProject(aShape, outHeight, outWidth, 
            true, intRefPointX, intRefPointY, intRefPointZ);

        if(ictdebug) {
            aShape.printShape("Transformed Image Boundary:");
        }

        aShape.screenBoundingBox();
        float minY = aShape.minY;
        float maxY = aShape.maxY;
        float minX = aShape.minX;
        float maxX = aShape.maxX;
        
        aShape.transformBoundingBox();

        if (ictdebug) {
            //
            // Inverse check.  Map transformed shape cornerpoints into original image
            aShape.initCurrentVertex();
            float xo, yo, zo;

            for (int index = 1; index <= aShape.getNumVertices(); index++) {
                float anX = aShape.currentVertex.tx;
                float anY = aShape.currentVertex.ty;
                float anZ = aShape.currentVertex.tz;
                inverseMatrix.transformPoint (anX, anY, anZ, xo, yo, zo);
                aShape.currentVertex++;
                sprintf(msgText, "transformed: %6.2f %6.2f %6.2f texture: %6.2f %6.2f %6.2f",
                    anX, anY, anZ, xo + halfInWidth, yo + halfInHeight, zo);
                statusPrint(msgText);
            }

            msgText = "read offsets: halfInWidth: " + halfInWidth + "  halfInHeight: " + halfInHeight;
            statusPrint(msgText);

            msgText = "write offsets: xCentOffset: " + xCentOffset + "  yCentOffset: " + yCentOffset;
            statusPrint(msgText);
        }

        float xIn, yIn, zIn, xOut, yOut, zOut;
        float xOut1, yOut1, zOut1, xOut2, yOut2, zOut2;
        float xOut3, yOut3, zOut3, xOut4, yOut4, zOut4;
        float fIntensity, xIncrement, yIncrement, zIncrement;
        float dx, dy, dz;
        float d, w, theZ, aDist;
        d = -512.0f;
        byte intensity, red, green, blue;
        int xMin, xMax, yMin, yMax, zMin, zMax;
        int numSteps;

        // Loop through the screen coordinates, filling in with inverse mapped pixels
        for (y = (int)minY; y <= (int)maxY; y++) {
            myStatus = getIntervals(aShape, y, numXCoordsFound, I_MAXWVERTICES,
                screenXCoords, tXCoords, tYCoords, tZCoords);

            if (myStatus != 0) {
                msgText = "iwarp: getInterval error: " + myStatus;
                statusPrint(msgText);
                return 2;
            }

            if (ictdebug) {
                statusPrint("y:\tsx  \ttx  \tty  \ttz");
                for(int i = 0; i < numXCoordsFound; i++) {
                    sprintf(msgText,"%d\t%d\t%6.2f\t%6.2f\t%6.2f" , y, screenXCoords[i],
                    tXCoords[i], tYCoords[i], tZCoords[i]);
                    statusPrint(msgText);
                }
            }

            if (numXCoordsFound != 2) {
                msgText = "iWarp: numCoords <> 2. y: " + y + " numCoords " + numXCoordsFound;
                statusPrint(msgText);
                for(int i = 0; i < numXCoordsFound; i++) {
                    sprintf(msgText,"%d\t%d\t%6.2f\t%6.2f\t%6.2f" , y, screenXCoords[i],
                      tXCoords[i], tYCoords[i], tZCoords[i]);
                    statusPrint(msgText);
                    goto nextScanLine;
                }
            }

            dx = tXCoords[1] - tXCoords[0];
            dy = tYCoords[1] - tYCoords[0];
            dz = tZCoords[1] - tZCoords[0];
            numSteps = (int)screenXCoords[1] - (int)screenXCoords[0] + 1;
            
            if (numSteps - 1.0 > 0.0) {
                xIncrement = dx/(float)(numSteps - 1);
                yIncrement = dy/(float)(numSteps - 1);
                zIncrement = dz/(float)(numSteps - 1);
            } else {
                xIncrement = 0.0f;
                yIncrement = 0.0f;
                zIncrement = 0.0f;
            }

            xIn = tXCoords[0];
            yIn = tYCoords[0];
            zIn = tZCoords[0];

            float dpx, dpy;
            dpx = 1.0f / sx;
            dpy = 1.0f / sy;
            if(dpx > 0.5) dpx = 0.5f;
            if(dpy > 0.5) dpy = 0.5f;

            // Loop through a single scan line
            for(x = (int)screenXCoords[0];x <= (int)screenXCoords[1]; x++) {
                // Determine the transformed x, y by inverting the true perspective
                // projection
                w = (zIn + d) / d;
                xIn = (x - halfInWidth) * w;
                yIn = (y - halfInHeight)* w;

                inverseMatrix.transformPoint(xIn, yIn, zIn, xOut, yOut, zOut);

                if(ictdebug) {
                    if(
                    (x == (int)screenXCoords[0]) || 
                    (x == (int)screenXCoords[1])) {
                        sprintf(msgText,"scanLine: %2d xi: %6.2f yi: %6.2f zi: %6.2f xo: %6.2f yo: %6.2f zo: %6.2f",
                            y,xIn, yIn, zIn, xOut, yOut, zOut);
                        statusPrint(msgText);
                    }
                }

                // if (TRUE) // no super-sampling
                // if (sx <= 1.0 && sy <= 1.0 && sz <= 1.0) {  // super-sample expansions only
                switch(bpp) {
                case 8:
                    intensity = inImage.getMPixel((int)(xOut + halfInWidth + 1),
                        (int)(yOut + halfInHeight + 1));
                    break;

                case 24:
                    inImage.getMPixelRGB((int)(xOut + halfInWidth + 1),
                        (int)(yOut + halfInHeight + 1), red, green, blue);
                    break;
                }

                if(zImage != null) {
                    theZ = zImage.getMPixel32((int)x + xCentOffset, (int)y + yCentOffset);
                    aDist = getDistance3d(xIn, yIn, zIn, vx, vy, vz);

                    //update the zbuffer if a smaller distance and non transparent color
                    if((aDist < theZ) && ((int)intensity != CHROMAVALUE)) {
                        zImage.setMPixel32((int)x + xCentOffset, (int)y + yCentOffset, aDist);
                        switch(bpp) {
                        case 8:
                            outImage.setMPixel((int)x + xCentOffset, (int)y + yCentOffset, intensity);
                            break;

                        case 24:
                            outImage.setMPixelRGB((int)x + xCentOffset, (int)y + yCentOffset, red, green, blue);
                            break;
                        }
                    }
                } else {
                    switch(bpp) {
                    case 8:
                        outImage.setMPixel((int)x + xCentOffset, (int)y + yCentOffset, intensity);
                        break;

                    case 24:
                        outImage.setMPixelRGB((int)x + xCentOffset, (int)y + yCentOffset, red, green, blue);
                        break;
                    }
                }

                xIn += xIncrement;
                yIn += yIncrement;
                zIn += zIncrement;
            }  //  end of column loop

            nextScanLine:  continue;
        } //  end of scan line loop

        if(ictdebug) {
            if(zImage != null) {
                statusPrint("iwarpz: Writing zBuffer -  d:\\ict20\\output\\rawWarpz.bmp");
                zImage.saveAs8("d:\\ict20\\output\\Warpz8.bmp");
            }
        }

        return 0;
    } // iwarpz


    // This method came from IWARP.CPP
    public static void getLineEquation(int x1,int y1, int x2, int y2, Float m,
    Float b, Boolean horzFlag, Boolean vertFlag) {
        // Determine the line equation y = mx + b from 2 points on the line
        m = 0.0f;
        b = 0.0f;
        horzFlag = false;
        vertFlag = false;
        float rise = (y2 - y1);
        float run  = (x2 - x1);

        if (rise == 0.0f) horzFlag = true;
        if (run == 0.0f)  vertFlag = true;
        if (!(vertFlag || horzFlag)) {
            m = rise / run;
            b = (float) y2 - (m * ((float) x2));
        }
    } // getLineEquation


    // This method came from IWARP.CPP
    public static void getFLineEquation(float x1,float y1, float x2, float y2, Float m,
    Float b, Boolean horzFlag, Boolean vertFlag) {
        // Determine the line equation y = mx + b from 2 points on the line
        m = 0.0f;
        b = 0.0f;
        horzFlag = false;
        vertFlag = false;

        float rise = (y2 - y1);
        float run = (x2 - x1);
        if (rise == 0.0) {
            horzFlag = true;
        }
        if (run == 0.0) {
            vertFlag = true;
        }

        if (!(vertFlag || horzFlag)) {
            m = rise / run;
            b = y2 - (m * x2);
        }
    } // getFLineEquation


    // This method came from IWARP.CPP
    public static int getIntervals(Shape3d theShape, int y, Integer numCoords,
    int numAllocatedXCoords, Integer screenXCoords,
    Float tXCoords, Float tYCoords, Float tZCoords) {
        //  Scan Conversion. For the indicated scan line y,  find all screen x coords
        //  where the
        //  shape crosses scan line y.  Sort the resulting screen x coordinate array.
        //  For each screen x, find the corresponding tx, ty, and tz by interpolating 
        //	from the two cornerpoints.
        String msgText;
        int[] tempScreenXCoords = new int[4];
        int[] intDistance = new int[4];
        float[] tempXCoords = new float[4];
        float[] tempYCoords = new float[4];
        float[] tempZCoords = new float[4];
        int tempIndex = 0;

        int numShapeVertices = theShape.getNumVertices();
        if(numShapeVertices != 4) {
            statusPrint("getIntervals: numShapeVertices must = 4");
            return -1;
        }

        float m, b;
        int i, index, newX;
        Boolean horzFlag = false, vertFlag = false;
        int *currentScreenX;
        float *currenttX, *currenttY, *currenttZ; 
        float theX;
        currentScreenX = screenXCoords;
        currenttX = tXCoords;
        currenttY = tYCoords;
        currenttZ = tZCoords;
        *numCoords = 0;
        int sx1, sy1, sx2, sy2, minx, maxx, miny, maxy;
        float tx1, ty1, tz1, tx2, ty2, tz2;
        float partialDistance, totalDistance, ratio;

        theShape.initCurrentVertex();
        for (index = 1; index <= numShapeVertices; index++) {
            sx1 = theShape.currentVertex.sx;
            sy1 = theShape.currentVertex.sy;
            
            tx1 = theShape.currentVertex.tx;
            ty1 = theShape.currentVertex.ty;
            tz1 = theShape.currentVertex.tz;
            theShape.currentVertex++;

            // if this is the last line segment, circle around to the beginning
            if(index == numShapeVertices) {
                theShape.initCurrentVertex();
            }
            sx2 = theShape.currentVertex.sx;  //Can't use (currentVertex+1).x
            sy2 = theShape.currentVertex.sy;
            
            tx2 = theShape.currentVertex.tx;
            ty2 = theShape.currentVertex.ty;	 
            tz2 = theShape.currentVertex.tz;
            theShape.currentVertex--;
            minx = Math.min(sx1, sx2);
            maxx = Math.max(sx1, sx2);
            miny = Math.min(sy1, sy2);
            maxy = Math.max(sy1, sy2);

            getLineEquation(sx1, sy1, sx2, sy2, m, b, horzFlag, vertFlag);
            theX = 0.0;
            if(m != 0.0) {
                theX = ((float)y - b) / m;
            }
            newX = (int)theX;
            
            if(ictdebug) {
                sprintf(msgText,"getIntervals: sx1: %d  sx2: %d  sy1: %d sy2: %d",
                    sx1,sx2,sy1,sy2);
                statusPrint(msgText);

                sprintf(msgText,"getIntervals: index: %d newX: %d  Horz: %d  vert: %d ", 
                    index, newX, horzFlag, vertFlag);
                statusPrint(msgText);
            }
            
            if (!(horzFlag || vertFlag)) {
                // determine z by interpolating between screen line segment endpoints
                totalDistance   = getDistance2d(sx1, sy1, sx2, sy2);
                partialDistance = getDistance2d(newX, y, sx1, sy1);
                //this is a ratio of screen coordinates
                if(totalDistance != 0.0) {
                    ratio = partialDistance/totalDistance; // 0 <= ratio <= 1
                } else {
                    statusPrint("getIntervals: totalDistance cannot equal 0");
                    return -1;
                }
                
                ratio = 1.0 - ratio;
                
                if ((newX >= minx && newX <= maxx) && (y >= miny && y <= maxy)) {
                    *currentScreenX = newX;
                    *currenttX = tx2 + (ratio * (tx1 - tx2));
                    *currenttY = ty2 + (ratio * (ty1 - ty2));	
                    *currenttZ = tz2 + (ratio * (tz1 - tz2));
                    if(ictdebug) {
                        statusPrint("diagPoint");
                    }

                    currenttX++;
                    currenttY++;
                    currenttZ++;
                    currentScreenX++;
                    intDistance[index-1] = intervalDistance(minx, maxx, theX);
                    numCoords++;
                    // end if between sx1 and sx2
                } else { 
                    // Store the point for possible later use
                    tempScreenXCoords[tempIndex] = theX;
                    tempXCoords[tempIndex] = tx2 + (ratio * (tx1 - tx2));
                    tempYCoords[tempIndex] = ty2 + (ratio * (ty1 - ty2));	
                    tempZCoords[tempIndex] = tz2 + (ratio * (tz1 - tz2));
                    intDistance[tempIndex] = intervalDistance(minx, maxx, theX);
                    tempIndex++;

                    if(ictdebug) {
                        statusPrint(" non diagPoint");
                    }
                }
                // end if not horizontal or vertical
            } else {
                // handle horizontal and vertical lines
                if (vertFlag) {
                    totalDistance   = Math.abs(sy2 - sy1);
                    partialDistance = Math.abs(y - sy1);		
                    if(totalDistance != 0.0f) {
                        ratio = partialDistance/totalDistance; // 0 <= ratio <= 1
                    } else {
                        statusPrint("getIntervals: totalDistance cannot equal 0");
                        return -1;
                    }

                    ratio = 1.0 - ratio;
                    if (y >= miny && y <= maxy) {
                        *currentScreenX = sx1;
                        *currenttX = tx1;
                        *currenttY = ty2 + (ratio * (ty1 - ty2));	
                        *currenttZ = tz2 + (ratio * (tz1 - tz2));
                        currentScreenX++;
                        currenttX++;
                        currenttY++;
                        currenttZ++;
                        intDistance[index-1] = intervalDistance(miny, maxy, y);
                        numCoords++;
                        if(ictdebug) {
                            statusPrint("vertPoint");
                        }
                    } else {
                        // store the point for possible later use
                        tempScreenXCoords[tempIndex] = sx1;
                        tempXCoords[tempIndex] = tx1;
                        tempYCoords[tempIndex] = ty2 + (ratio * (ty1 - ty2));	
                        tempZCoords[tempIndex] = tz2 + (ratio * (tz1 - tz2));
                        intDistance[tempIndex] = intervalDistance(miny, maxy, y);
                        tempIndex++;
                    }
                }
            }
            theShape.currentVertex++;
        }

        //  Sort the found x coordinates in ascending order
        insertionSort(screenXCoords, tXCoords, tYCoords, tZCoords, numCoords);
        removeDuplicates(screenXCoords, tXCoords, tYCoords, tZCoords, numCoords);

        if(numCoords > 2) {
            removeSimilar (screenXCoords, tXCoords, tYCoords, tZCoords, numCoords, 2);
        }

        int minIntDist = 999999999;
        int aCol;

        if (numCoords == 1) {
            for(i = 0; i < tempIndex; i++) {
                if(intDistance[i] < minIntDist) {
                    aCol = i;
                    minIntDist = intDistance[i];
                }
            }

            // Correct missed points due to roundoff
            if(minIntDist < 3) {
                numCoords++;
                tXCoords[1] = tempXCoords[aCol];
                tYCoords[1] = tempYCoords[aCol];
                tZCoords[1] = tempZCoords[aCol];
                screenXCoords[1] = tempScreenXCoords[aCol];
                insertionSort(screenXCoords, tXCoords, tYCoords, tZCoords, numCoords);
            } else {
                numCoords++;
                tXCoords[1] = tXCoords[0];
                tYCoords[1] = tYCoords[0];
                tZCoords[1] = tZCoords[0];
                screenXCoords[1] = screenXCoords[0];
            }
        }

        if(ictdebug) {
            statusPrint("GetIntervals Found: intdist\t sx  \t tx  \t ty  \t tz");
            for(i = 0; i < numCoords; i++) {
                sprintf(msgText, "\t%d\t%d\t%6.2f\t%6.2f\t%6.2f" , intDistance[i], screenXCoords[i], tXCoords[i], tYCoords[i], tZCoords[i]);
                statusPrint(msgText);
            }
        }
        
        return 0;
    } // getIntervals


    // This method came from IWARP.CPP
    public static void insertionSort(int theItems[], float itemData1[], float itemData2[],
    float itemData3[], int numItems) {
        //  Sort theItems into ascending order, carrying along the three optional 
        //  itemData arrays.
        //
        int itemTemp;
        float itemData1Temp,itemData2Temp,itemData3Temp;
        int index, indexTmp, theValue;

        for(index = 0; index < numItems; index++) {
            itemTemp = theItems[index];
            itemData1Temp = itemData1[index];
            itemData2Temp = itemData2[index];
            itemData3Temp = itemData3[index];
            theValue = theItems[index];
            for(indexTmp = index; indexTmp > 0; indexTmp--) {
                if(theItems[indexTmp - 1] > theValue) {
                    theItems[indexTmp] = theItems[indexTmp - 1];
                    itemData1[indexTmp] = itemData1[indexTmp - 1];
                    itemData2[indexTmp] = itemData2[indexTmp - 1];
                    itemData3[indexTmp] = itemData3[indexTmp - 1];
                } else {
                    break;
                }
            }

            theItems[indexTmp] = itemTemp;
            itemData1[indexTmp] = itemData1Temp;
            itemData2[indexTmp] = itemData2Temp;
            itemData3[indexTmp] = itemData3Temp;
        }

        return;
    } // insertionSort


    // This method came from IWARP.CPP
    public static void insertionSort(int theItems[], int itemData1[], float itemData2[], float itemData3[],
    float itemData4[], int numItems) {
        //  Sort theItems into ascending order, carrying along the four optional 
        //  itemData arrays.
        int itemTemp;
        int itemData1Temp;
        float itemData2Temp, itemData3Temp, itemData4Temp;
        int index, indexTmp, theValue;

        for(index = 0; index < numItems; index++) {
            itemTemp = theItems[index];
            itemData1Temp = itemData1[index];
            itemData2Temp = itemData2[index];
            itemData3Temp = itemData3[index];
            itemData4Temp = itemData4[index];
            theValue = theItems[index];
            for(indexTmp = index; indexTmp > 0; indexTmp--) {
                if(theItems[indexTmp - 1] > theValue) {
                    theItems[indexTmp]  = theItems[indexTmp - 1];
                    itemData1[indexTmp] = itemData1[indexTmp - 1];
                    itemData2[indexTmp] = itemData2[indexTmp - 1];
                    itemData3[indexTmp] = itemData3[indexTmp - 1];
                    itemData4[indexTmp] = itemData4[indexTmp - 1];
                } else {
                    break;
                }
            }

            theItems[indexTmp]  = itemTemp;
            itemData1[indexTmp] = itemData1Temp;
            itemData2[indexTmp] = itemData2Temp;
            itemData3[indexTmp] = itemData3Temp;
            itemData4[indexTmp] = itemData4Temp;
        }

        return;
    } // insertionSort


    // This method came from IWARP.CPP
    public static int removeDuplicates(int theList[], float theItemData1[],
    float theItemData2[], float theItemData3[], Integer listLength) {
        // Remove duplicates from a list pre-sorted in ascending order.
        // listlength is 1 relative.
        if (listLength == 1) {
            return 0;
        }

        if (listLength < 1) {
            statusPrint("RemoveDuplicates: Input list must have 2 or more members");
            return -1;
        }

        for (int index = 0; index + 1 < listLength; index++) {
            if (theList[index + 1] == theList[index]) {
                for (int index2 = index; index2 < listLength; index2++) {
                    theList[index2] = theList[index2 + 1];
                    theItemData1[index2] = theItemData1[index2 + 1];
                    theItemData2[index2] = theItemData2[index2 + 1];
                    theItemData3[index2] = theItemData3[index2 + 1];
                }

                listLength--;
                index--;
            }
        }

        return 0;
    } // removeDuplicates


    // This method came from IWARP.CPP
    public static int removeDuplicates(int theList[], int theItemData1[], float theItemData2[],
    float theItemData3[], float theItemData4[], Integer listLength) {
        // remove duplicates from a list pre-sorted in ascending order.
        // listlength is 1 relative.
        if (listLength == 1) {
            return 0;
        }

        if (listLength < 1) {
            statusPrint("RemoveDuplicates: Input list must have 2 or more members");
            return -1;
        }

        for (int index = 0; index + 1 < listLength; index++) {
            if (theList[index + 1] == theList[index]) {
                for (int index2 = index; index2 < listLength; index2++) {
                    theList[index2] = theList[index2 + 1];
                    theItemData1[index2] = theItemData1[index2 + 1];
                    theItemData2[index2] = theItemData2[index2 + 1];
                    theItemData3[index2] = theItemData3[index2 + 1];
                    theItemData4[index2] = theItemData4[index2 + 1];
                }

                listLength--;
                index--;
            }
        }

        return 0;
    } // removeDuplicates


    // This method came from IWARP.CPP
    public static int removeSimilar(int theList[], float theItemData1[],
    float theItemData2[], float theItemData3[], Integer listLength, int difference) {
        // Remove items from a list that are less then difference units apart.  
        // The list is assumed to pre-sorted in ascending order.
        // listlength is 1 relative.
        if (listLength == 1) {
            return 0;
        }
        if (listLength < 1) {
            statusPrint("RemoveDuplicates: Input list must have 2 or more members");
            return -1;
        }

        for (int index = 0; index + 1 < listLength; index++) {
            if (Math.abs(theList[index + 1] - theList[index]) < difference) {
                for (int index2 = index; index2 < listLength; index2++) {
                    theList[index2] = theList[index2 + 1];
                    theItemData1[index2] = theItemData1[index2 + 1];
                    theItemData2[index2] = theItemData2[index2 + 1];
                    theItemData3[index2] = theItemData3[index2 + 1];
                }

                listLength--;
                index--;
            }
        }

        return 0;
    } // removeSimilar


    // This method came from IWARP.CPP
    public static int iRender(MemImage outImage, MemImage maskImage, MemImage inImage,
    float rx, float ry, float rz, float sx, float sy, float sz,
    float tx, float ty, float tz, TMatrix viewMatrix,
    boolean warpIndicator, boolean blendIndicator, float alphaScale,
    float refPointX, float refPointY, float refPointZ) {
        int outputRows = outImage.getHeight();
        int outputCols = outImage.getWidth();
        MemImage midImage, midMaskImage;
        int xOffset = (int)tx; // Set these for the noblend nowarp case
        int yOffset = (int)ty;
        int imXOffset, imYOffset, msXOffset, msYOffset;
        float vx, vy, vz;

        if(!(warpIndicator || blendIndicator)) { // Background plate
            xOffset = 0;
            yOffset = 0;
        }

        if(warpIndicator) {
            midImage = new MemImage(outputRows, outputCols); //open intermediate image
            if (!midImage.isValid()) {
                statusPrint("Unable to open intermediate warp image");
                return -1;
            }

            fwarpz(inImage, midImage, null, 
                rx, ry, rz, sx, sy, sz, tx, ty, tz,
                vx, vy, vz,
                viewMatrix,
                refPointX, refPointY, refPointZ);
        }

        if(warpIndicator && blendIndicator) {
            // Open intermediate matte image
            midMaskImage = new MemImage(outputRows, outputCols);
            if (!midMaskImage.isValid()) {
                statusPrint("Unable to open intermediate warp mask image");
                return -1;
            }
            fwarpz(maskImage, midMaskImage, null, rx, ry, rz, sx, sy, sz, tx, ty, tz,
                vx, vy, vz,
                viewMatrix,
                refPointX, refPointY, refPointZ);
        }

        // Composite the cutout image into the output scene
        int myStatus;
        if(blendIndicator) {
            if(!warpIndicator) {
                myStatus = blend(inImage, maskImage, outImage, alphaScale);
            } else {
                myStatus = blend(midImage, midMaskImage, outImage, alphaScale);
            }
        } else {
            if(warpIndicator) { 
                // Copy warped image to output
                midImage.copy(outImage, 0, 0);
            } else {             
                // Copy input image to output
                inImage.copy(outImage, 0, 0);
            }
        }
        
        return myStatus;
    } // iRender


    // This method came from IWARP.CPP
    public static int iRenderz(MemImage outImage, MemImage matteImage, MemImage inImage,
    MemImage zImage, MemImage zBuffer,
    float rx, float ry, float rz, float sx, float sy, float sz,
    float tx, float ty, float tz, float vx, float vy, float vz,
    TMatrix viewMatrix,
    boolean warpIndicator, boolean blendIndicator, float alphaScale,
    float refPointX, float refPointY, float refPointZ) {
        int outputRows = outImage.getHeight();
        int outputCols = outImage.getWidth();
        MemImage midImage, alphaImage;
        int xOffset = (int)tx; // Set these for the blend nowarp case
        int yOffset = (int)ty;
        int imXOffset, imYOffset, msXOffset, msYOffset;

        // If matteImage (same as alpha image) is NULL, the alpha image is created
        // from the warped model image. 
        // If matteImage is not NULL, compositing process uses the supplied image
        //
        // Use of the ZBuffer is handled in the rendering/blending functions called 
        // by iRenderz

        // Controls whether texture mapping occurs with forward
        // or inverse warp procedures.  quadmesh models are unaffected
        // by this setting.
        boolean forwardWarp;   
        forwardWarp = true;

        if(!(warpIndicator || blendIndicator)) { // Background plate
            xOffset = 0;
            yOffset = 0;
        }

        if(warpIndicator) {
            midImage = new MemImage(outputRows, outputCols); // open intermediate image
            if (!midImage.isValid()) {
                statusPrint("iRenderz: Unable to open intermediate warp image");
                return -1;
            }
            if(forwardWarp) {
                fwarpz(inImage, midImage, zImage, 
                    rx, ry, rz, 
                    sx, sy, sz, 
                    tx, ty, tz,
                    vx, vy, vz, 
                    viewMatrix, 
                    refPointX, refPointY, refPointZ);
            } else {
                iwarpz(inImage, midImage, zImage, 
                    rx, ry, rz, 
                    sx, sy, sz, 
                    tx, ty, tz,
                    vx, vy, vz, 
                    viewMatrix, 
                    refPointX, refPointY, refPointZ);
            }
        }

        if(blendIndicator) {
            alphaImage = new MemImage(outputRows, outputCols);
            if (!alphaImage.isValid()) {
                statusPrint("iRenderz: Unable to open intermediate alpha image");
                return -1;
            }

            if (matteImage != null) {     // its been supplied by the user, warp it
                if (forwardWarp) {
                    fwarpz(matteImage, alphaImage, zImage, 
                        rx, ry, rz, 
                        sx, sy, sz, 
                        tx, ty, tz,
                        vx, vy, vz, 
                        viewMatrix, 
                        refPointX, refPointY, refPointZ);
                } else {
                    iwarpz(inImage, midImage, zImage, 
                        rx, ry, rz, 
                        sx, sy, sz, 
                        tx, ty, tz,
                        vx, vy, vz, 
                        viewMatrix, 
                        refPointX, refPointY, refPointZ);
                }
                //
                // Generate the alpha image
            } else { 
                // Create the alpha image
                midImage.createAlphaImage(alphaImage);
                alphaImage.alphaSmooth3();
            }
        }

        //
        // Composite the element into the background plate
        int myStatus;
        if(blendIndicator) {
            // blend but no warp
            if(!warpIndicator) {
                myStatus = blendz(inImage, alphaImage, zImage, zBuffer, outImage, alphaScale);
            } else {
                // blend and warp
                myStatus = blendz(midImage, alphaImage, zImage, zBuffer, outImage, alphaScale);
                delete midImage;
            }
        } else {
            if(warpIndicator) { 
                // copy warped image to background plate
                midImage.copy(outImage, 0, 0);
                delete midImage;
            } else {
                // copy input image to output
                inImage.copy(outImage, xOffset, yOffset);
            }
        }

        if(
        (matteImage == null) && 
        (blendIndicator == true) && 
        alphaImage.isValid()) {
            delete alphaImage;
        }

        return myStatus;
    } // iRenderz


    // This method came from IWARP.CPP
    public static int antiAlias(MemImage inImage, MemImage outImage) {
        // Each image must be the same size.
        if(
        inImage.getHeight() == outImage.getHeight() && 
        inImage.getWidth() == outImage.getWidth() ) {
            // do nothing?
        } else {
            statusPrint("antiAlias: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() == outImage.getBitsPerPixel()) && 
        (inImage.getBitsPerPixel() == 8)) {
            // do nothing?
        } else {
            statusPrint("antiAlias: images must have 8 or 24 bit pixels.");
            return -2;
        }

        int bpp = inImage.getBitsPerPixel();
        float[][] weight = new float[3][3];
        weight[0][0] = 0.05f;    // impulse function
        weight[0][1] = 0.05f;
        weight[0][2] = 0.05f;
        weight[1][0] = 0.05f;
        weight[1][1] = 0.60f;
        weight[1][2] = 0.05f;
        weight[2][0] = 0.05f;
        weight[2][1] = 0.05f;
        weight[2][2] = 0.05f;

    /*
        weight[0][0] = -0.01;    //sinc function
        weight[0][1] = -0.01;
        weight[0][2] = -0.01;
        weight[1][0] = -0.01;
        weight[1][1] = 1.08;
        weight[1][2] = -0.01;
        weight[2][0] = -0.01;
        weight[2][1] = -0.01;
        weight[2][2] = -0.01;
    */
        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        int row, col;
        float sum,  q00,  q10,  q20,  q01,  q11,  q21,  q02,  q12,  q22;
        float sumr, q00r, q10r, q20r, q01r, q11r, q21r, q02r, q12r, q22r;
        float sumg, q00g, q10g, q20g, q01g, q11g, q21g, q02g, q12g, q22g;
        float sumb, q00b, q10b, q20b, q01b, q11b, q21b, q02b, q12b, q22b;
        byte red, green, blue;

        for (row = 2; row <= imHeight - 1; row++) {
            for (col = 2; col <= imWidth - 1; col++) {
                switch(bpp) {
                case 8:
                    q00 = inImage.getMPixel(col - 1, row - 1) * weight[0][0];
                    q10 = inImage.getMPixel(col, row - 1)     * weight[1][0];
                    q20 = inImage.getMPixel(col + 1, row - 1) * weight[2][0];

                    q01 = inImage.getMPixel(col - 1, row)     * weight[0][1];
                    q11 = inImage.getMPixel(col, row)         * weight[1][1];
                    q21 = inImage.getMPixel(col + 1, row)     * weight[2][1];

                    q02 = inImage.getMPixel(col - 1, row + 1) * weight[0][2];
                    q12 = inImage.getMPixel(col, row + 1)     * weight[1][2];
                    q22 = inImage.getMPixel(col + 1, row + 1) * weight[2][2];

                    sum = q00 + q10 + q20 + q10 + q11 + q12 + q20 + q21 + q22;
                    sum = bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col,row, (BYTE)(sum + 0.5f));
                    break;

                case 24:
                    inImage.getMPixelRGB(col - 1, row - 1, red, green, blue);
                    q00r = (float) red   * weight[0][0];
                    q00g = (float) green * weight[0][0];
                    q00b = (float) blue  * weight[0][0];
                    inImage.getMPixelRGB(col, row - 1, red, green, blue);
                    q10r = (float) red   * weight[1][0];
                    q10g = (float) green * weight[1][0];
                    q10b = (float) blue  * weight[1][0];
                    q20 = inImage.getMPixel(col + 1, row - 1) * weight[2][0];

                    q01 = inImage.getMPixel(col - 1, row)     * weight[0][1];
                    q11 = inImage.getMPixel(col, row)         * weight[1][1];
                    q21 = inImage.getMPixel(col + 1, row)     * weight[2][1];

                    q02 = inImage.getMPixel(col - 1, row + 1) * weight[0][2];
                    q12 = inImage.getMPixel(col, row + 1)     * weight[1][2];
                    q22 = inImage.getMPixel(col + 1, row + 1) * weight[2][2];

                    sumr = q00r + q10r + q20r + q10r + q11r + q12r + q20r + q21r + q22r;
                    sumg = q00g + q10g + q20g + q10g + q11g + q12g + q20g + q21g + q22g;
                    sumb = q00b + q10b + q20b + q10b + q11b + q12b + q20b + q21b + q22b;
                    sumr = bound(sumr, 0.0f, 255.0f);
                    sumg = bound(sumg, 0.0f, 255.0f);
                    sumb = bound(sumb, 0.0f, 255.0f);
                    outImage.setMPixelRGB(col, row, (BYTE)(sumr + 0.5f), (BYTE)(sumg + 0.5f), (BYTE)(sumb + 0.5f));
                    break;
                } // switch
            }
        }

        return 0;
    } // antiAlias


    // This method came from IWARP.CPP
    public static int fWarp1(MemImage inImage, MemImage outImage,
    float rx, float ry, float rz, float sx, float sy, float sz,
    float tx, float ty, float tz, TMatrix viewMatrix,
    float refPointX, float refPointY, float refPointZ) {
        //  Project the points to the screen and copy from the input image 
        //
        // the reference point is a point in the texture image's 
        // original(i.e. initial, default) position in Cartesian space.
        // about which the image is rotated and scalled.
        // For example, the reference point 0,0,0 is the center of the 
        // texture image.
        String msgText;
        float x, y;
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES];
        float[] tXCoords = new float[I_MAXWVERTICES]; 
        float[] tYCoords = new float[I_MAXWVERTICES];

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if (ictdebug) {
            statusPrint("fWarp inputs:");
            sprintf(msgText, "rx: %6.2f  ry: %6.2f  rz: %6.2f", rx, ry, rz);
            statusPrint(msgText);

            sprintf(msgText, "sx: %6.2f  sy: %6.2f  sz: %6.2f", sx, sy, sz);
            statusPrint(msgText);

            sprintf(msgText, "tx: %6.2f  ty: %6.2f  tz: %6.2f", tx, ty, tz);
            statusPrint(msgText);

            sprintf(msgText, "refx: %6.2f  refy: %6.2f  refz: %6.2f", refPointX, refPointY, refPointZ);
            statusPrint(msgText);
        }

        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        float XRadians = rx * F_DTR;
        float YRadians = ry * F_DTR;
        float ZRadians = rz * F_DTR;
        forwardMatrix.scale(sx, sy, sz);
        forwardMatrix.rotate(XRadians, YRadians, ZRadians);
        forwardMatrix.translate(tx, ty, tz);
        TMatrix viewModelMatrix = new TMatrix();
        viewModelMatrix.multiply(viewMatrix, forwardMatrix);
    
        if (ictdebug) {
            forwardMatrix.display("Forward Matrix:");
        }
    
        int inHeight  = inImage.getHeight();
        int inWidth   = inImage.getWidth();
        int outHeight = outImage.getHeight();
        int outWidth  = outImage.getWidth();
        
        float halfHeight = inHeight / 2.0f;
        float halfWidth  = inWidth / 2.0f;

        // This algorithm actually uses a reference point defined in pixel space
        // therefore we convert it now.
        float intRefPointX = refPointX + halfWidth;
        float intRefPointY = refPointY + halfHeight;
        float intRefPointZ = refPointZ;
        halfWidth  -= (halfWidth - intRefPointX);
        halfHeight -= (halfHeight - intRefPointY);

        // Calculate offsets that will center the warped image in the output image
        int xOffset = outWidth / 2.0;
        int yOffset = outHeight/ 2.0;
        
        //  shortcut:  if no rotation or scale, just copy the image
        if(
        rx == 0.0f && ry == 0.0f && rz == 0.0f && 
        sx == 1.0f && sy == 1.0f && sz == 1.0f && 
        tz == 0.0f) {
            inImage.copy(outImage, (int)tx + xOffset, (int)ty + yOffset);
            statusPrint("fWarp: shortcut");
            return 0;
        }
        
        float xIn, yIn, zIn; 
        int xOut, yOut;
        byte intensity;

        // Loop through the texture coordinates, projecting to the screen
        zIn = 0.0f;
        float atx, aty, atz;
        float increment = 1.0f;
    
        for (y = 1; y <= inHeight; y += increment) {
            yIn = y - halfHeight;
        
            for(x = 1; x < inWidth; x += increment) {
                intensity = inImage.getMPixel(x, y);
                xIn = x - halfWidth;
                forwardMatrix.transformAndProjectPoint(xIn, yIn, zIn, xOut, yOut, 
                    refPointX, refPointY, refPointZ, 
                    outHeight, outWidth, 
                    atx, aty, atz);
        
                outImage.setMPixel((int)xOut, (int)yOut, intensity);
            }
        }
    
        if (ictdebug) {
            statusPrint("fWarp1: Writing output -  d:\\ict20\\output\\rawfWarp.bmp");
            outImage.writeBMP("d:\\ict20\\output\\rawfWarp.bmp");
        }
    
        return 0;
    } // fWarp1
  
  
    // This method came from IWARP.CPP
    public static int fwarpz(MemImage inImage, MemImage outImage, MemImage zImage,
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    float vx, float vy, float vz, 
    TMatrix viewMatrix,
    float refPointX, float refPointY, float refPointZ) {
        // The reference point is a point in the texture image's 
        // original(i.e. initial, default) position in Cartesian space.
        // about which the image is rotated and scaled.
        // For example, the reference point 0,0,0 is the center of the 
        // texture image.

        String msgText;
        float x, y;
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES]; 
        float[] tXCoords = new float[I_MAXWVERTICES];
        float[] tYCoords = new float[I_MAXWVERTICES];

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if (ictdebug) {
            statusPrint("fWarp inputs:");
            sprintf(msgText, "rx: %6.2f  ry: %6.2f  rz: %6.2f", rx, ry, rz);
            statusPrint(msgText);

            sprintf(msgText, "sx: %6.2f  sy: %6.2f  sz: %6.2f", sx, sy, sz);
            statusPrint(msgText);

            sprintf(msgText, "tx: %6.2f  ty: %6.2f  tz: %6.2f", tx, ty, tz);
            statusPrint(msgText);

            sprintf(msgText, "refx: %6.2f  refy: %6.2f  refz: %6.2f", refPointX, refPointY, refPointZ);
            statusPrint(msgText);
        }

        // Build the forward transformation matrix
        TMatrix forwardMatrix, viewModelMatrix;
        float XRadians = rx * F_DTR;
        float YRadians = ry * F_DTR;
        float ZRadians = rz * F_DTR;
        forwardMatrix.scale(sx, sy, sz);
        forwardMatrix.rotate(XRadians, YRadians, ZRadians);
        forwardMatrix.translate(tx, ty, tz);
        viewModelMatrix.multiply(viewMatrix, forwardMatrix);
    
        if (ictdebug) {
            forwardMatrix.display("Forward Matrix:");
        }
    
        int inHeight  = inImage.getHeight();
        int inWidth   = inImage.getWidth();
        int outHeight = outImage.getHeight();
        int outWidth  = outImage.getWidth();
    
        float halfHeight = inHeight / 2.0f;
        float halfWidth = inWidth / 2.0f;
        int bpp = inImage.getBitsPerPixel();

        int xOffset = outWidth / 2.0;
        int yOffset = outHeight/ 2.0;
    
        //
        //  shortcut:  if no rotation or scale, just copy the image
        if(
        rx == 0.0f && ry == 0.0f && rz == 0.0f && 
        sx == 1.0f && sy == 1.0f && sz == 1.0f && 
        tz == 0.0f) {
            inImage.copy(outImage, (int)tx + halfWidth, (int)ty + halfHeight);
            statusPrint("fWarpz: shortcut");
            return 0;
        }
        
        float xIn, yIn, zIn; 
        int xOut1, yOut1, xOut2, yOut2;
        int xOut3, yOut3, xOut4, yOut4;
        byte intensity1, intensity2, intensity3, intensity4;

        // Loop through the texture coordinates, projecting to the screen
        zIn = 0.0f;
        float atx, aty, atz;
        float increment = 0.5f;                  // oversample 2:1
        float inverseInc = 1.0f / increment;
        float d1, d2, d3, d4;
        byte red1, green1, blue1, red2, green2, blue2, red3, green3, blue3,
            red4, green4, blue4;

        for (y = inverseInc * increment; y <= inHeight; y += increment) {
            yIn = y - halfHeight;
        
            for(x = inverseInc * increment; x <= inWidth; x += increment) {
                if(bpp == 8) intensity1 = inImage.getMPixel(x - increment, y);
                if(bpp == 8) intensity2 = inImage.getMPixel(x, y);
                if(bpp == 8) intensity3 = inImage.getMPixel(x, y - increment);
                if(bpp == 8) intensity4 = inImage.getMPixel(x - increment, y - increment);
            
                if(bpp == 24) inImage.getMPixelRGB(x - increment, y, red1, intensity1, blue1);
                if(bpp == 24) inImage.getMPixelRGB(x, y, red2, intensity2, blue2);
                if(bpp == 24) inImage.getMPixelRGB(x, y - increment, red3, intensity3, blue3);
                if(bpp == 24) inImage.getMPixelRGB(x - increment, y - increment, red4, intensity4, blue4);

                xIn = x - halfWidth;
                forwardMatrix.transformAndProjectPoint(xIn-increment, yIn, zIn, 
                    xOut1, yOut1, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d1 = getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                forwardMatrix.transformAndProjectPoint(xIn, yIn, zIn, 
                    xOut2, yOut2, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d2 = getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                forwardMatrix.transformAndProjectPoint(xIn, yIn-increment, zIn, 
                    xOut3, yOut3, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d3 = getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                forwardMatrix.transformAndProjectPoint(xIn-increment, yIn-increment, zIn, 
                    xOut4, yOut4, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d4 = getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                outImage.fillPolyz(xOut1, yOut1, intensity1, d1, 
                                    xOut2, yOut2, intensity2, d2, 
                                    xOut3, yOut3, intensity3, d3, 
                                    xOut4, yOut4, intensity4, d4, zImage);
            }
        }
    
        if (ictdebug) {
            zImage.writeBMP("d:\\ict20\\output\\zBuffer32.bmp");
            statusPrint("fWarp3: Writing z output - d:\\ict20\\output\\zBuffer32.bmp");

            zImage.saveAs8("d:\\ict20\\output\\zBuffer8.bmp");
            statusPrint("fWarp3: Writing z output - d:\\ict20\\output\\zBuffer8.bmp");
        
            statusPrint("fWarp3: Writing output -  c:\\ict\\output\\rawfWarp.bmp");
            outImage.writeBMP("c:\\ict\\output\\rawfWarp.bmp");
        }
    
        return 0;
    } // fwarpz


    // This method came from IWARP.CPP
    public static int fwarpz2(MemImage inputImage, MemImage outputImage, MemImage zBuffer, 
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    float vx, float vy, float vz,  
    TMatrix viewMatrix,
    float refPointX, float refPointY, float refpointZ) {
        String msgText;

        // Create the line buffer data structures
        int *xBuffer, *yBuffer,  *xTemp, *yTemp;
        float *wxBuffer, *wyBuffer, *wzBuffer, *dBuffer;
        float *wxTemp, *wyTemp, *wzTemp;
        BYTE *iBuffer, *iTemp, iTemp1, iTemp2, *iPrev1, *iPrev2;
        int xTemp1, yTemp1, xTemp2, yTemp2;
        int *xPrev1, *yPrev1, *xPrev2, *yPrev2;
        float *dTemp, dTemp1, dTemp2, *dPrev1, *dPrev2;
        
        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        float XRadians = rx * F_DTR;
        float YRadians = ry * F_DTR;
        float ZRadians = rz * F_DTR;
        forwardMatrix.scale(sx, sy, sz);
        forwardMatrix.rotate(XRadians, YRadians, ZRadians);
        forwardMatrix.translate(tx, ty, tz);
        TMatrix viewModelMatrix = new TMatrix();
        viewModelMatrix.multiply(viewMatrix, forwardMatrix);
    
        if (ictdebug) {
            forwardMatrix.display("Forward Matrix:");
        }
    
        int bpp       = inputImage.getBitsPerPixel();
        int inHeight  = inputImage.getHeight();
        int inWidth   = inputImage.getWidth();
        int outHeight = outputImage.getHeight();
        int outWidth  = outputImage.getWidth();
        float halfHeight = inHeight / 2.0f;
        float halfWidth  =  inWidth / 2.0f;
        
        float increment = 0.5f;
        float inverseInc = 1.0f / increment;
        int numCalcs = inWidth * inverseInc;
    
        xBuffer = (int)malloc(numCalcs * sizeof(int));
        if (xBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for xBuffer");
            return -1;
        }

        yBuffer = (int)malloc(numCalcs * sizeof(int));
        if (yBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for yBuffer");
            return -1;
        }
    
        dBuffer = (float)malloc(numCalcs * sizeof(float));
        if (dBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for distance Buffer");
            return -1;
        }
    
        iBuffer = (BYTE)malloc(numCalcs * sizeof(BYTE));
        if (iBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for iBuffer");
            return -1;
        }
    
        //  Temporary - for testing
        vx = (float)outWidth/2.0f;
        vy = (float)outHeight/2.0f;
        vz = 512.0f;
    
        sprintf(msgText, "fwarpz2: Viewer location: vx: %f, vy: %f, vz: %f", vx, vy, vz);
        statusPrint(msgText);
        xTemp = xBuffer;
        yTemp = yBuffer;
        iTemp = iBuffer;
        dTemp = dBuffer;

        float row, col;
        float x1,y1,z1;
        BYTE i1, red1, green1, blue1;
        int sx1, sy1;
        float refX, refY, refZ;
    
        for (row = inverseInc * increment; row <= inHeight; row+= increment) {
            for (col = inverseInc * increment; col <= inWidth; col+= increment) {
                x1 = col - halfWidth;
                y1 = row - halfHeight;
                z1 = 0.0;
                if(bpp == 8) {
                    i1 = inputImage.getMPixel(col, row);
                }
                if(bpp == 24) {
                    i1 = inputImage.getMPixelRGB(col, row, red1, i1, blue1);
                }
    
                // project to the screen
                viewModelMatrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                    refX, refY, refZ, outHeight, outWidth, tx, ty, tz);
                if(row == 1.0f) {
                    *xTemp = sx1;
                    xTemp++;
                    *yTemp = sy1;
                    yTemp++;
                    *iTemp = i1;
                    iTemp++;
                    *dTemp = getDistance3d(tx, ty, tz, vx, vy, vz);
                    dTemp++;
                }
            
                if ((row > 1.0f) && (col == 1.0f)) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;
                    xPrev1 = xBuffer;
                    yPrev1 = yBuffer;
                    xPrev2 = xBuffer;
                    yPrev2 = yBuffer;
                    xPrev2++;
                    yPrev2++;
                    iPrev1 = iBuffer;
                    iPrev2 = iBuffer;
                    iPrev2++;
        
                    dPrev1 = dBuffer;
                    dPrev2 = dBuffer;
                    dPrev2++;
                    if(zBuffer != null) {
                        dTemp1 = getDistance3d(tx, ty, tz, vx, vy, vz);
                    }
                }
    
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;
                    if(zBuffer != null) {
                        dTemp2 = getDistance3d(tx, ty, tz, vx, vy, vz);
                    }
         
                    // Render the quadrangle intensities
                    //                     
                    // Render the quadrangle distances and update the intermediate zBuffer
                    outputImage.fillPolyz( 
                                *xPrev1, *yPrev1, *iPrev1, *dPrev1,
                                *xPrev2, *yPrev2, *iPrev2, *dPrev2,
                                xTemp2, yTemp2, iTemp2, dTemp2,
                                xTemp1, yTemp1, iTemp1, dTemp1, zBuffer);
        
                    *xPrev1 = xTemp1;
                    *yPrev1 = yTemp1;
                    *iPrev1 = iTemp1;
                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;
                    xPrev1++;
                    yPrev1++;
                    xPrev2++;
                    yPrev2++;
                    iPrev1++;
                    iPrev2++;
        
                    *dPrev1 = dTemp1;
                    dTemp1 = dTemp2;
                    dPrev1++;
                    dPrev2++;
                }
            }
        }
    
        return 0;
    } // fwarpz2
  

    // This method came from IWARP.CPP
    public static int intervalDistance(int a, int b, int c) {
        // returns 0 if c is inside the interval (a,b).  i.e. a <= c <= b
        // else returns distance between c and interval (a,b)
        int b1 = a - c;
        int b2 = c - b;
        int b3 = Math.max(b1, b2);
        if( b3 < 0) {
            return 0;
        } else {
            return b3;
        }
    } // intervalDistance


    // This method came from QMESHMODEL.CPP
    public static int createQMeshModel(String inputImagePath, String destinationDir, int modelType) {  
        float Pi = 3.1415926f;
        int row, col; 
        float x, y, z, sizeFactor, theDimension, yValue;
 
        float randMax = (float)RAND_MAX;
        float xMagnitude = 2.4f;
        float yMagnitude = 1.5f;
        float zMagnitude = 5.7f;
 
        float smallerSide, cubeFaceSize;
        int v1, v2, v3, v4;
 
        int imHeight, imWidth;
        int bitsPerPixel, aStatus;
        aStatus = readBMPHeader(inputImagePath, imHeight, imWidth, bitsPerPixel);
        if(aStatus != 0) {
            statusPrint("createQMeshModel: Unable to open texture image");
            return -1;
        }

        MemImage inputImage = new MemImage(inputImagePath, 0, 0, RANDOM, 'R', RGBCOLOR);
        MemImage xImage = new MemImage(imHeight, imWidth, 32);
        MemImage yImage = new MemImage(imHeight, imWidth, 32);
        MemImage zImage = new MemImage(imHeight, imWidth, 32);
         
        float radius, startTheta, stopTheta, angularInc, angleTemp, asAngle, asAngleInc;
        float xCent, yCent, distance;
        String msgText;
        
        sizeFactor = (float)imWidth;
 
        switch(modelType) {
        case CYLINDER:
            radius = (float)imWidth/(2.0f * Pi);
            startTheta = 0.0f;
            stopTheta = 360.0f;
            angularInc = 360.0f / (float)imWidth;
            angleTemp = 0.0f;
            for (row = 1; row <= imHeight; row++) {
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    x = radius * cos(angleTemp * F_DTR);
                    y = radius * sin(angleTemp * F_DTR);
                    angleTemp += angularInc;
                    xImage.setMPixel32(col, row, x);
                    yImage.setMPixel32(col, row, (float)row);
                    zImage.setMPixel32(col, row, y);
                } // for col
            } // for row
            break;
 
        case PLANAR:
            for (row = 1; row <= imHeight; row++) {
                for (col = 1; col <= imWidth; col++) {
                    x = (float)col;
                    y = (float)row;
                    xImage.setMPixel32(col, row, x);
                    yImage.setMPixel32(col, row, y);
                    zImage.setMPixel32(col, row, 0.0f);
                } // for col
            } // for row
            break;
 
        case SPHERE:
            startTheta = 0.0f;
            stopTheta = 360.0f;
            asAngle = 0.0f;
            theDimension = (float)imHeight;  //set the horz and vert dims equal to the max of the rectangle sides
            if (imWidth > imHeight) {
                theDimension = (float)imWidth;
            }

            asAngleInc = 180.0f / (float)imHeight; //the height traces out a hemispherical arc
            angularInc = 360.0f / (float)imWidth;  //Width
            angleTemp = 0.0f;

            for (row = 1; row <= imHeight; row++) {
                radius = Math.sin(asAngle * F_DTR) * sizeFactor;
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    x = (radius * Math.cos(angleTemp * F_DTR) ) + sizeFactor; //put the left most edge in the positive quadrant
                    z = (radius * Math.sin(angleTemp * F_DTR) ) + sizeFactor;
                    xImage.setMPixel32(col, row, x);
                    zImage.setMPixel32(col, row, z);
                    yValue = acos(asAngle * F_DTR) * sizeFactor;
                    yImage.setMPixel32(col, row, yValue);
                    angleTemp += angularInc;
                } // for col
                asAngle += asAngleInc;
            } // for row
            break;
 
        case SINE1D:
            angularInc = 1.0f;
            radius = 100.0f;
            for (row = 1; row <= imHeight; row++) {
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    z = radius * Math.sin(angleTemp * F_DTR);
                    xImage.setMPixel32(col, row, (float)col);
                    yImage.setMPixel32(col, row, z);
                    zImage.setMPixel32(col, row, (float)row);
                    angleTemp += angularInc;
                } // for col
            } // for row
            break;
 
        case SINE2D:
            //  Circumfrence = 2*Pi*radius
            radius = (float)(imWidth/(2.0f * Pi)/3.0f);   // make r small enough for three sinusoidal rotations
            angularInc = 360.0f / (float)imWidth;
            xCent = (float)imWidth/2.0f;
            yCent = (float)imHeight/2.0f;
            distance; // ?
            for (row = 1; row <= imHeight; row++) {
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    xImage.setMPixel32(col, row, (float)col);
                    zImage.setMPixel32(col, row, (float)row);
                    distance = Math.sqrt(((col - xCent) * (col - xCent)) + ((row - yCent) * (row - yCent)));           
                    yImage.setMPixel32(col, row, radius * Math.sin(5.0f * distance  * F_DTR));
                } // for col
            } // for row
            break;
 
        case WHITENOISE:
            // Seed the random-number generator with current time so that
            // the numbers will be different every time we run.
            srand( (unsigned)time( NULL ) );
 
            for (row = 1; row <= imHeight; row++) {
                for (col = 1; col <= imWidth; col++) {
                    xImage.setMPixel32(col, row, (float)col + ((float)rand()/randMax * xMagnitude));
                    yImage.setMPixel32(col, row, (float)row + ((float)rand()/randMax * yMagnitude));
                    zImage.setMPixel32(col, row, 0.0f);
                } // for col
            } // for row
            break;
 
        default:
            break;
        } // switch 
 
        // Generate the output path names and save the results
        //
        // Apply a file naming convention:
        // first determine the input file name, then substitute the last letter with x, y, or z
        String drive, dir, file, ext;
        String ddrive, ddir, dfile, dext;
        String outPath, xPath, yPath, zPath;
 
        _splitpath(inputImagePath, drive, dir, file, ext);
        int theLength = strlen(file);
  
        _splitpath(destinationDir, ddrive, ddir, dfile, dext);
        _makepath(outPath, ddrive, ddir, file, ext);

        // As a matter of convenience copy the texture image to the same directory
        // in which the surface images reside, if it isn't there already.
        if(!fileExists(outPath)) {
            msgText = "Copying QMesh Model Texture Image to: " + outPath;
            statusPrint(msgText);
            CopyFile(inputImagePath, outPath, TRUE);
        }
 
        constructPathName(xPath, outPath, 'x');
        constructPathName(yPath, outPath, 'y');
        constructPathName(zPath, outPath, 'z');

        // Insure that a generated path is not the same as the texture path
        if(
        strcmpi(xPath,inputImagePath) == 0 ||
        strcmpi(yPath,inputImagePath) == 0 ||
        strcmpi(zPath,inputImagePath) == 0) {
            statusPrint("createQMeshModel: A surface image may not have the same name as the texture image.");
            msgText = "textureImage: " + inputImagePath;
            statusPrint(msgText);

            msgText = "xImage: " + xPath;
            statusPrint(msgText);

            msgText = "yImage: " + yPath;
            statusPrint(msgText);

            msgText = "zImage: " + zPath;
            statusPrint(msgText);

            return -1;
        }

        float x1, y1, z1, refX, refY, refZ;
        float xBucket, yBucket, zBucket;
        float xMin, xMax, yMin, yMax, zMin, zMax;
        xMin = xImage.getMPixel32(1, 1);
        xMax = xMin;
        yMin = yImage.getMPixel32(1, 1);
        yMax = yMin;
        zMin = zImage.getMPixel32(1, 1);
        zMax = zMin;

        // Get an approximate model centroid, bounding box and display it.
        statusPrint("calculating approximate mesh centroid and bounding box");
        xBucket = 0.0f;
        yBucket = 0.0f;
        zBucket = 0.0f;
        float totalCells = 0.0f;
        int meshIncrement = 3;
 
        for (row = 1; row <= imHeight; row += meshIncrement) {
            for (col = 1; col <= imWidth; col += meshIncrement) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                if(x1 > xMax) xMax = x1;
                if(x1 < xMin) xMin = x1;
                if(y1 > yMax) yMax = y1;
                if(y1 < yMin) yMin = y1;
                if(z1 > zMax) zMax = z1;
                if(z1 < zMin) zMin = z1;
                xBucket += x1;
                yBucket += y1;
                zBucket += z1;
                totalCells++;
            } // for col
        } // for row

        refX = xBucket/totalCells;
        refY = yBucket/totalCells;
        refZ = zBucket/totalCells;

        msgText = "QuadMesh centroid x: " + refX + " y: " + refY + " z: " + refZ;
        statusPrint(msgText);

        msgText = "QuadMesh BBox Mins x: " + xMin + " y: " + yMin + " z: " + xMin;
        statusPrint(msgText);

        msgText = "QuadMesh BBox Maxs x: " + xMax + " y: " + yMax + " z: " + xMax;
        statusPrint(msgText);
 
        msgText = "Saving QMesh: " + xPath;
        statusPrint(msgText);
        xImage.writeBMP(xPath);

        msgText = "Saving QMesh: " + yPath;
        statusPrint(msgText);
        yImage.writeBMP(yPath);

        msgText = "Saving QMesh: " + zPath;
        statusPrint(msgText);
        zImage.writeBMP(zPath);
        
        MemImage xImage8 = new MemImage(imHeight, imWidth, 8);
        MemImage yImage8 = new MemImage(imHeight, imWidth, 8);
        MemImage zImage8 = new MemImage(imHeight, imWidth, 8);
       
        xImage.scaleTo8(xImage8);
        yImage.scaleTo8(yImage8);
        zImage.scaleTo8(zImage8);
 
        xImage8.writeBMP("d:\\ict20\\output\\meshx8.bmp");
        yImage8.writeBMP("d:\\ict20\\output\\meshy8.bmp");
        zImage8.writeBMP("d:\\ict20\\output\\meshz8.bmp");
 
        return 0;
    } // createQMeshModel
 

    // This method came from QMESHMODEL.CPP
    public static int getMeshCentroid(MemImage xImage, MemImage yImage, MemImage zImage,
    Float centroidX, Float centroidY, Float centroidZ) {
        String msgText;
        statusPrint("Calculating mesh centroid");

        // Each image must be the same size.
        if(
        xImage.getHeight() == yImage.getHeight() && 
        yImage.getHeight() == zImage.getHeight() &&
        xImage.getWidth() == yImage.getWidth() && 
        yImage.getWidth() == zImage.getWidth()) {
            //Do nothing cause Everything's OK
        } else {
            statusPrint("getMeshCentroid: Surface images must have equal size.");
            return -1;
        }
    
        // Each image must have 32 bit pixels.
        if(
        xImage.getBitsPerPixel() == 32 && 
        yImage.getBitsPerPixel() == 32 &&
        zImage.getBitsPerPixel() == 32) {
            //Do nothing cause Everything's OK
        } else {
            statusPrint("getMeshCentroid: Surface images must have 32 bit pixels.");
            return -2;
        }
    
        int imHeight = xImage.getHeight();
        int imWidth = xImage.getWidth();
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        int meshIncrement = 1;  // increase for greater speed, but less accuracy
        int row, col;
    
        for (row = 1; row <= imHeight; row += meshIncrement) {
            for (col = 1; col <= imWidth; col += meshIncrement) {
                x1 += xImage.getMPixel32(col, row);
                y1 += yImage.getMPixel32(col, row);
                z1 += zImage.getMPixel32(col, row);
                totalCells++;
            } // for col
        } // for row

        centroidX = x1/totalCells;
        centroidY = y1/totalCells;
        centroidZ = z1/totalCells;
        msgText = "Mesh centroid calculated: " centroidX + " " + centroidY + " " + centroidZ;
        statusPrint(msgText);
        return 0;
    } // getMeshCentroid
  

    // This method came from QMESHMODEL.CPP
    public static int translateMesh(MemImage xImage, MemImage yImage, MemImage zImage,
    float offsetX, float offsetY, float offsetZ) {
        statusPrint("Translating mesh.");

        // Each image must be the same size.
        if(xImage.getHeight() == yImage.getHeight() && 
            yImage.getHeight() == zImage.getHeight() &&
            xImage.getWidth() == yImage.getWidth() && 
            yImage.getWidth() == zImage.getWidth()) {
            //Do nothing cause Everything's OK
        } else {
            statusPrint("translateMesh: Surface images must have equal size.");
            return -1;
        }
    
        // Each image must have 32 bit pixels.
        if(
        xImage.getBitsPerPixel() == 32 && 
        yImage.getBitsPerPixel() == 32 &&
        zImage.getBitsPerPixel() == 32) {
            //Do nothing cause Everything's OK
        } else {
            statusPrint("translateMesh: Surface images must have 32 bit pixels.");
            return -2;
        }
    
        int imHeight = xImage.getHeight();
        int imWidth = xImage.getWidth();
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        int meshIncrement = 1;  // increase for greater speed, but less accuracy
        int row, col;
    
        for (row = 1; row <= imHeight; row += meshIncrement) {
            for (col = 1; col <= imWidth; col += meshIncrement) {
                x1 = xImage.getMPixel32(col, row);
                x1 += offsetX;
                xImage.setMPixel32(col, row, x1);

                y1 = yImage.getMPixel32(col, row);
                y1 += offsetY;
                yImage.setMPixel32(col, row, y1);

                z1 = zImage.getMPixel32(col, row);
                z1 += offsetZ;
                zImage.setMPixel32(col, row, z1);
            } // for col
        } // for row

        statusPrint("Mesh translated."); 
        return 0;
    } // translateMesh


    // This method came from MEMIMG32.CPP
    public static int makeRGBimage(String redImage, String greenImage, String blueImage, String outFileName) {
        String msgBuffer;

        // Combine separate color channels into one RGB BMP
        int rHeight, rWidth, gHeight, gWidth, bHeight, bWidth;

        MemImage theRed = new MemImage(redImage, 0, 0, SEQUENTIAL, 'R', REDCOLOR);
        if (!theRed.isValid()) {
            msgBuffer = "makeRGBIMage: Unable to open Red image: " + redImage;
            Globals.statusPrint(msgBuffer);
            return 1;
        }

        MemImage theGreen = new MemImage(greenImage, 0, 0, SEQUENTIAL,'R', GREENCOLOR);
        if (!theGreen.isValid()) {
            msgBuffer = "makeRGBIMage: Unable to open Green image: " + greenImage;
            Globals.statusPrint(msgBuffer);
            return 1;
        }

        MemImage theBlue = new MemImage(blueImage, 0, 0, SEQUENTIAL,'R', BLUECOLOR);
        if (!theBlue.isValid()) {
            msgBuffer = "makeRGBIMage: Unable to open Blue image: %s" + blueImage;
            Globals.statusPrint(msgBuffer);
            return 1;
        }

        rHeight = theRed.getHeight();
        rWidth  = theRed.getWidth();

        gHeight = theGreen.getHeight();
        gWidth  = theGreen.getWidth();

        bHeight = theBlue.getHeight();
        bWidth  = theBlue.getWidth();

        if (!(rWidth == gWidth && gWidth == bWidth && rWidth == bWidth)) {
            Globals.statusPrint("makeRGBIMage: R,G, and B image widths are not equal.");
            return 1;
        }
        if (!(rHeight == gHeight && gHeight == bHeight && rHeight == bHeight)) {
            Globals.statusPrint("makeRGBIMage: R,G, and B image heights are not equal.");
            return 1;
        }

        MemImage theRGB = new MemImage(outFileName, gHeight, gWidth, SEQUENTIAL, 'W', RGBCOLOR);
        if (!theRGB.isValid()) {
            Globals.statusPrint("makeRGBIMage: Unable to open RGB image.");

            theRed.close();
            theGreen.close();
            theBlue.close();
            return 1;
        }

        byte *redPixel, *greenPixel, *bluePixel, *rgbPixel;
        int rStatus, gStatus, bStatus;

        for (int y = 1; y <= gHeight; y++) {
            rStatus = theRed.readNextRow();
            if (rStatus != 0) {
                Globals.statusPrint("makeRGBImage: red readNextRow error.");

                theRed.close();
                theGreen.close();
                theBlue.close();
                return 1;
            }

            gStatus = theGreen.readNextRow();
            if (gStatus != 0) {
                Globals.statusPrint("makeRGBImage: green readNextRow error.");

                theRed.close();
                theGreen.close();
                theBlue.close();
                return 1;
            }

            bStatus = theBlue.readNextRow();
            if (bStatus != 0) {
                Globals.statusPrint("makeRGBImage: blue readNextRow error.");

                theRed.close();
                theGreen.close();
                theBlue.close();
                return 1;
            }

            redPixel   = theRed.getBytes();
            greenPixel = theGreen.getBytes();
            bluePixel  = theBlue.getBytes();
            rgbPixel   = theRGB.getBytes();

            for (int x = 1; x <= gWidth; x++) {
                *rgbPixel = *bluePixel;
                rgbPixel++;

                *rgbPixel =*greenPixel;
                rgbPixel++;

                *rgbPixel = *redPixel;
                rgbPixel++;

                redPixel++;
                greenPixel++;
                bluePixel++;
            } // for x

            // Write the output
            theRGB.writeNextRow();
        } // for y

        //close the files and destroy the objects
        theRed.close();
        theGreen.close();
        theBlue.close();
        theRGB.close();

        remove(redImage);     // to conserve disk space, remove the
        remove(greenImage);   // input files
        remove(blueImage);
        return 0;
    }


    // This method came from TWEEN.CPP
    int getRowIntervals(MemImage anImage, int row, int[] intervalList, Integer numIntervals) {
        int imWidth = anImage.getWidth();
        int bpp = anImage.getBitsPerPixel();
        int col; 
        int intervalStatus = 0;
        int counter = 0;
        byte aValue, red, green, blue;
        
        for(col = 1; col <= imWidth; col++) {
            switch(bpp) {
            case 8:
                aValue = anImage.getMPixel(col, row);
                break;
                
            case 24:
                anImage.getMPixelRGB(col, row, red, green, blue);
                if(
                red != CHROMARED || 
                green != CHROMAGREEN || 
                blue != CHROMABLUE) {
                    aValue = 255;
                } else {
                    aValue = CHROMAVALUE;
                }
                break;
            } // switch

            if(intervalStatus == 0 && aValue != CHROMAVALUE) {  //interval start
                intervalList[counter] = col;
                counter++;
                intervalStatus = 1;
            }

            if(intervalStatus == 1 && aValue == CHROMAVALUE) {  //interval stop
                intervalList[counter] = col;
                counter++;
                intervalStatus = 0;
            }
        }

        if(intervalStatus == 1) {                            // catch end of line
            intervalList[counter] = imWidth;
            counter++;
        }

        numIntervals = counter / 2;
        return NULL;
    } // getRowIntervals


    // This method came from TWEEN.CPP
    int getTotalIntervalLength(int[] intervalList, int numIntervals) {
        int totalLength = 0;
        int i;

        if(numIntervals == 0) {
            return totalLength;
        }

        for (i = 0; i < numIntervals * 2; i += 2) {
            totalLength += (intervalList[i + 1] - intervalList[i] + 1);
        }

        return totalLength;
    } // getTotalIntervalLength


    // This method came from TWEEN.CPP
    int indexToCoord(int index, int *intervalList, int numIntervals) {
        // Map index into the interval list
        String msgText;
        int count, runningCount,countDelta;
        int aCoord, i;

        runningCount = 0;
        if(numIntervals == 0) {
            statusPrint("indexToCoord: numIntervals is 0");
            return -1;
        }

        int maxIndex = getTotalIntervalLength(intervalList, numIntervals);
        if(index > maxIndex) {
            msgText = "indexToCoord: Index: " + index + " > maxIndex: " + maxIndex;
            statusPrint(msgText);
            return -2;
        }

        for(i = 0; i < numIntervals * 2; i += 2) {
            count = intervalList[i + 1] - intervalList[i] + 1;
            runningCount = runningCount + count;

            if(index <= runningCount) {
                countDelta = runningCount - index;
                aCoord = intervalList[i + 1] - countDelta;
                return aCoord;
            }
        }

        msgText = "indexToCoord: Coord not found. Index: " + index;
        statusPrint(msgText);

        for(i = 0; i < numIntervals * 2; i += 2) {
            msgText = "i: " + i + " B: " + intervalList[i] + " E: " + intervalList[i + 1];
            statusPrint(msgText);
        }

        return -3;
    } // indexToCoord


    // This method came from SHADERS.CPP
    public static int fillTrianglez(int x1, int y1, float i1, float d1,
    int x2, int y2, float i2, float d2,
    int x3, int y3, float i3, float d3,
    MemImage outImage, MemImage zImage) {
        // zImage contains the distance values.
        //
        // To use this function without a z-buffer, call with zImage equal to NULL

        // Pixels are written to outImage only if the new distance (derived from d1,d2,d3) is 
        // less than the corresponding distance in the zImage.
        //
        // The assumption is made here that sets of points describing vertical or horizontal 
        // lines have been handled elsewhere

        int midPoint, minX, minY, maxX, maxY, midX, midY, denominator;
        float minI, maxI, midI, minD, maxD, midD;
        float intensity, intensityStep, distance, distanceStep, id1, id2, oldZ;

        int bpp = outImage.getBitsPerPixel();
        if(zImage != null) {
            if (zImage.getBitsPerPixel() != 32) {
                statusPrint("fillTrianglez: z image must be 32 bits/pixel");
                return -1;
            }
        }

        midY = -1;
        int yMax = y1;
        int yMin = y1;
        int minPoint = 1;
        int maxPoint = 1;

        if(y2 > yMax) { 
            yMax = y2;
            maxPoint = 2;
        }

        if(y3 > yMax) {
            yMax = y3;
            maxPoint = 4;
        }

        if(y2 < yMin) { 
            yMin = y2;
            minPoint = 2;
        }

        if(y3 < yMin) {
            yMin = y3;
            minPoint = 4;
        }

        if ((minPoint + maxPoint) == 3) midPoint = 4;
        if ((minPoint + maxPoint) == 5) midPoint = 2;
        if ((minPoint + maxPoint) == 6) midPoint = 1;

        switch (minPoint) {
        case 1:
            minX = x1;
            minY = y1;
            minI = i1;
            minD = d1;
            break;

        case 2:
            minX = x2;
            minY = y2;
            minI = i2;
            minD = d2;
            break;

        case 4:
            minX = x3;
            minY = y3;
            minI = i3;
            minD = d3;
            break;
        } // switch

        switch (maxPoint) {
        case 1:
            maxX = x1;
            maxY = y1;
            maxI = i1;
            maxD = d1;
            break;

        case 2:
            maxX = x2;
            maxY = y2;
            maxI = i2;
            maxD = d2;
            break;

        case 4:
            maxX = x3;
            maxY = y3;
            maxI = i3;
            maxD = d3;
            break;
        } // switch

        switch (midPoint) {
        case 1:
            midX = x1;
            midY = y1;
            midI = i1;
            midD = d1;
            break;

        case 2:
            midX = x2;
            midY = y2;
            midI = i2;
            midD = d2;
            break;

        case 4:
            midX = x3;
            midY = y3;
            midI = i3;
            midD = d3;
            break;
        } // switch

        int row, col, firstX, lastX, triangleType;
        int ix1, ix2, ip1, ip2, nSteps;

        triangleType = I_POINTONSIDE;
        if(midY == maxY) triangleType = I_POINTONTOP;
        if(minY == midY) triangleType = I_POINTONBOTTOM;

        //  Now we have a rotationally independant situation.  Interpolate rows from 
        //  minY to midY then from midY to maxY
        if (midY == -1) {
            midY = maxY;
            return -1;
        }

        if(triangleType == I_POINTONSIDE) {
            for(row = minY; row <= midY; row++) {
                //interpolate the x interval and the intensities at the interval boundary
                ix1 = (int)interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                ix2 = (int)interpolate((float)minX, (float)midX, (float)minY, (float)midY, (float)row);
                ip1 = (int)interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                ip2 = (int)interpolate(       minI,        midI, (float)minY, (float)midY, (float)row);
                if(zImage != null) {
                    id1 = interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                    id2 = interpolate(minD, midD, (float)minY, (float)midY, (float)row);
                }

                nSteps = Math.abs(ix2 - ix1);
                if(ix1 <= ix2) {
                    firstX = ix1;
                    lastX = ix2;
                    intensity = ip1;
                    distance = id1;
                    denominator = nSteps + 1;
                    if(denominator > 0) {
                        intensityStep = (ip2 - ip1)/denominator;
                        distanceStep  = (id2 - id1)/denominator;
                    } else {
                        intensityStep = 0.0f;
                        distanceStep  = 0.0f;
                    }
                } else {
                    firstX = ix2;
                    lastX = ix1;
                    intensity = ip2;
                    distance = id2;
                    denominator = nSteps + 1;

                    if(denominator > 0) {
                        intensityStep = (ip1 - ip2)/denominator;
                        distanceStep  = (id1 - id2)/denominator;
                    } else {
                        intensityStep = 0.0f;
                        distanceStep  = 0.0f;
                    }
                } 

                for (col = firstX; col <= lastX; col++) {
                    if(zImage != null) {  //render with a Z Buffer
                        oldZ = zImage.getMPixel32(col, row);

                        if(distance <= oldZ) {
                            if(distance <= 1.0f) {
                                distance = 1.0f;
                            }
                            intensity = bound(intensity, 0.0f, 255.0f);
                            zImage.setMPixel32(col, row, distance);

                            if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                            if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                                (byte)intensity, (byte)intensity);
                        }
                    } else {
                        //render without a Z Buffer
                        intensity = bound(intensity, 1.0f, 255.0f);
                        if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                        if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }

                    intensity += intensityStep;
                    distance  += distanceStep;
                }
            }

            // Handle the second half of the pointonside case
            for(row = midY; row <= maxY; row++) {
                // Interpolate the x interval and the intensities at the interval boundary
                ix1 = (int)interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                ix2 = (int)interpolate((float)midX, (float)maxX, (float)midY, (float)maxY, (float)row);
                ip1 = (int)interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                ip2 = (int)interpolate(       midI,        maxI, (float)midY, (float)maxY, (float)row);
                if(zImage != null) {
                    id1 = interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                    id2 = interpolate(midD, maxD, (float)midY, (float)maxY, (float)row);
                }

                nSteps = Math.abs(ix2 - ix1);
                if(ix1 <= ix2) {
                    firstX = ix1;
                    lastX = ix2;
                    intensity = ip1;
                    distance = id1;
                    denominator = nSteps + 1;
                    if(denominator > 0.0) {
                        intensityStep = (ip2 - ip1)/denominator;
                        distanceStep  = (id2 - id1)/denominator;
                    } else {
                        intensityStep = 0.0f;
                        distanceStep  = 0.0f;
                    }
                } else {
                    firstX = ix2;
                    lastX = ix1;
                    intensity = ip2;
                    distance = id2;
                    denominator = nSteps + 1;
                    if(denominator > 0) {
                        intensityStep = (ip1 - ip2)/denominator;
                        distanceStep  = (id1 - id2)/denominator;
                    } else {
                        intensityStep = 0.0f;
                        distanceStep  = 0.0f;
                    }
                }

                for (col = firstX; col <= lastX; col++) {
                    if(zImage != null) {
                        oldZ = zImage.getMPixel32(col, row);
                        if(distance <= oldZ) {
                            if(distance <= 1.0f) {
                                distance = 1.0f;
                            }
                            intensity = bound(intensity, 0.0f, 255.0f);
                            zImage.setMPixel32(col, row, distance);
                            if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                            if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                                (byte)intensity, (byte)intensity);
                        }
                    } else {
                        intensity = bound(intensity, 0.0f, 255.0f);
                        if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                        if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }
                    intensity += intensityStep;
                    distance  += distanceStep;
                }
            }
        } else {
            // handle pointontop, pointonbottom cases
            for(row = minY; row <= maxY; row++) {
                // interpolate the x interval and the intensities at the interval boundary
                if(triangleType == POINTONTOP) {
                    ix1 = (int)interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                    ix2 = (int)interpolate((float)minX, (float)midX, (float)minY, (float)midY, (float)row);
                    ip1 = (int)interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                    ip2 = (int)interpolate(       minI,        midI, (float)minY, (float)midY, (float)row);
                    if(zImage != null) {
                        id1 = interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                        id2 = interpolate(minD, midD, (float)minY, (float)midY, (float)row);
                    }
                }
                if(triangleType == POINTONBOTTOM) {
                    ix1 = (int)interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                    ix2 = (int)interpolate((float)midX, (float)maxX, (float)midY, (float)maxY, (float)row);
                    ip1 = (int)interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                    ip2 = (int)interpolate(       midI,        maxI, (float)midY, (float)maxY, (float)row);
                    if(zImage != null) {
                        id1 = interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                        id2 = interpolate(midD, maxD, (float)midY, (float)maxY, (float)row);
                    }
                }

                nSteps = Math.abs(ix2 - ix1);
                if(ix1 <= ix2) {
                    firstX = ix1;
                    lastX = ix2;
                    intensity = ip1;
                    distance = id1;
                    denominator = nSteps + 1;
                    if(denominator > 0) {
                        intensityStep = (ip2 - ip1)/denominator;
                        distanceStep  = (id2 - id1)/denominator;
                    } else {
                        intensityStep = 0.0f;
                        distanceStep  = 0.0f;
                    }
                } else {
                    firstX = ix2;
                    lastX = ix1;
                    intensity = ip2;
                    distance = id2;
                    denominator = nSteps  + 1;
                    if(denominator > 0) {
                        intensityStep = (ip1 - ip2)/denominator;
                        distanceStep  = (id1 - id2)/denominator;
                    } else {
                        intensityStep = 0.0f;
                        distanceStep  = 0.0f;
                    }
                }   

                for (col = firstX; col <= lastX; col++) {
                    if(zImage != null) {
                        oldZ = zImage.getMPixel32(col, row);
                        if(distance <= oldZ) {
                            if(distance <= 1.0f) {
                                distance = 1.0f;
                            }
                            intensity = bound(intensity, 0.0f, 255.0f);
                            zImage.setMPixel32(col, row, distance);
                            if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                            if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                                (byte)intensity, (byte)intensity);
                        }
                    } else {
                        intensity = bound(intensity, 0.0f, 255.0f);
                        if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                        if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }

                    intensity += intensityStep;
                    distance  += distanceStep;
                }
            }
        }

        return 0;
    } // fillTrianglez


    // This method came from SHADERS.CPP
    public static byte getLight(Point3d p1, Point3d p2, Point3d c1, Point3d c2) {
        // Input points are oriented counterclockwise from the first point
        Point3d centroid;
        Point3d np1;
        // Point3d *np2, *nc1, *nc2;  // lighting normals // these local variables are not used
        float ip1, ip2, ic1, ic2;		  // intensities at face cornerpoints
        Point3d lightSource;
        
        if(false) {
            statusPrint("-----------------getLight-------------------");
            p1.display("p1");
            p2.display("p2");
            c1.display("c1");
            if(c2 == null) {
                statusPrint("c2 is NULL");
            } else {
                c2.display("c2");
            }
        }
        
        ////////////////////////////////////////////////////////////
        //  Lambertian Shading
        //
        //  Assume a face is planar.  Thus only one surface normal
        ////////////////////////////////////////////////////////////
        
        float xMax = c1.x;
        float yMax = c1.y;
        float zMax = c1.z;
        float xMin = c1.x;
        float yMin = c1.y;
        float zMin = c1.z;

        // Get the 3D bounding box
        if(p1.x > xMax) xMax = p1.x;
        if(p2.x > xMax) xMax = p2.x;
        
        if(p1.x < xMin) xMin = p1.x;
        if(p2.x < xMin) xMin = p2.x;
        
        if(p1.y > yMax) yMax = p1.y;
        if(p2.y > yMax) yMax = p2.y;
        
        if(p1.y < yMin) yMin = p1.y;
        if(p2.y < yMin) yMin = p2.y;
        
        if(p1.z > zMax) zMax = p1.z;
        if(p2.z > zMax) zMax = p2.z;
        
        if(p1.z < zMin) zMin = p1.z;
        if(p2.z < zMin) zMin = p2.z;
        
        if(c2 != null) {
            if(c2.z < zMin) zMin = c2.z;
            if(c2.z > zMax) zMax = c2.z;
            if(c2.x > xMax) xMax = c2.x;
            if(c2.x < xMin) xMin = c2.x;
            if(c2.y > yMax) yMax = c2.y;
            if(c2.y < yMin) yMin = c2.y;
        }

        centroid.x = (xMax + xMin) / 2.0f;
        centroid.y = (yMax + yMin) / 2.0f;
        centroid.z = (zMax + zMin) / 2.0f;
        
        lightSource.x =   0.0f;
        lightSource.y =   0.0f;
        lightSource.z = 512.0f;
        
        float dCentroid = getDistance3d(lightSource.x, lightSource.y, lightSource.z, 
                                        centroid.x, centroid.y, centroid.z);
        
        getNormal2(np1, p1, centroid, p2);
        
        vectorNormalize(np1);
        //
        //  kd     the coefficient of reflection or reflectivity of the surface material
        //         highly reflective = 1, highly absorptive = 0
        //	Ip	   the intensity of the light source
        //  Ia     the ambient intensity at the surface
        //  N      The surface Normal (unit vector)
        //  L      The direction of the light source (unit vector)
        //  d      the distance between the surface and the light source
        //
        float kd = 0.65f;
        int Ip = 150;
        
        ip1 = lightModel(kd, Ip, 150, np1, lightSource, dCentroid);
        ip1 = bound(ip1, 1.0f, 255.0f);
        return (byte)ip1;
    } // getLight


    // This method came from VECTOR.CPP
    public static float lightModel(float kd, int Ip, int Ia, Point3d N, Point3d L, float d) {
        //  kd     the coefficient of reflection or reflectivity of the surface material
        //         highly reflective = 1, highly absorptive = 0
        //  Ia     the ambient intensity at the surface
        //	Ip	   the intensity of the light source
        //  d      the distance between the surface and the light source
        //  d0     a constant to keep the denominator from reaching 0  for points close to the light source
        //  N      The surface Normal (unit vector)
        //  L      The direction of the light source (unit vector)
        //  I      The intensity produced by this light model
    
        float orientationLight = Vect.dotProduct(N,L);
        float d0 = 0.5f;

        //  equation 14.4 p 279 Hearn - Baker
        float I = (kd * Ia) + ((kd * Ip) / (d + d0) * orientationLight);
        return I;
    } // lightModel


    // This method came from RENDER.CPP
    // Called from MorphDlg
    public static int renderMesh(String outputImagePath, MemImage textureImage, 
    MemImage xImage, MemImage yImage, MemImage zImage, 
    TMatrix aMatrix) {
        //  This version of renderMesh renders a mesh without the need for the
        //  renderObject that provides the context information from the graphic
        //  pipeline.
        int *xBuffer, *yBuffer,  *xTemp, *yTemp;
        float *wxBuffer, *wyBuffer, *wzBuffer, *dBuffer;
        float *wxTemp, *wyTemp, *wzTemp;
        byte *iBuffer, *iTemp, iTemp1, iTemp2, *iPrev1, *iPrev2;
        int xTemp1, yTemp1, xTemp2, yTemp2;
        int *xPrev1, *yPrev1, *xPrev2, *yPrev2;
        float *dTemp, dTemp1, dTemp2, *dPrev1, *dPrev2;
        float vx, vy, vz;

        if (
        !xImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("renderMesh: One or more quad-mesh image is not valid");
            return -1;
        }

        // Create the line buffer data structures
        xBuffer = (int)malloc(xImage.getWidth() * sizeof(int));
        if (xBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for xBuffer");
            return -1;
        }

        yBuffer = (int)malloc(yImage.getWidth() * sizeof(int));
        if (yBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for yBuffer");
            return -1;
        }

        dBuffer = (float)malloc(zImage.getWidth() * sizeof(float));
        if (dBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for Z Buffer");
            return -1;
        }

        iBuffer = (byte)malloc(textureImage.getWidth() * sizeof(BYTE));
        if (iBuffer == null) {
            Globals.statusPrint("renderMeshz: Not enough memory for intensity Buffer");
            return -1;
        }

        MemImage outputImage = new MemImage(textureImage.getHeight(), textureImage.getWidth());
        if(!outputImage.isValid()) {
            Globals.statusPrint("renderMeshZ: Not enough memory to open output image");
            return -1;
        }
        outputImage.setFileName("outputImage");

        MemImage midZImage = new MemImage(outputImage.getHeight(), outputImage.getWidth(), 32);
        if(!midZImage.isValid()) {
            Globals.statusPrint("renderMeshZ: Not enough memory to open intermediate Z image");
            return -1;
        }

        midZImage.setFileName("midZImage");
        midZImage.init32(ZBUFFERMAXVALUE);
        int imHeight  = textureImage.getHeight();
        int imWidth   = textureImage.getWidth();
        int outHeight = outputImage.getHeight();
        int outWidth  = outputImage.getWidth();

        //  Temporary - for testing
        vx = (float)outWidth/2.0f;
        vy = (float)outHeight/2.0f;
        vz = 512.0f;

        String msgText = "renderMeshz: Viewer location: vx: " + vx + ", vy: " + vy + ", vz: " + vz;
        Globals.statusPrint(msgText);
        xTemp = xBuffer;
        yTemp = yBuffer;
        iTemp = iBuffer;
        dTemp = dBuffer;

        byte i1;
        int row, col, sx1, sy1;
        float x1,y1,z1, tx, ty, tz;
        float refX, refY, refZ;
        refX = 0.0f;
        refY = 0.0f;
        refZ = 0.0f;

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                i1 = textureImage.getMPixel(col, row);

                // project to the screen
                aMatrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                  refX, refY, refZ, outHeight, outWidth, tx, ty, tz);
 
                if(row == 1) {
                    xTemp = sx1;
                    xTemp++;

                    yTemp = sy1;
                    yTemp++;

                    iTemp = i1;
                    iTemp++;

                    dTemp = Globals.getDistance3d(tx, ty, tz, vx, vy, vz);
                    dTemp++;
                }
              
                if ((row > 1) && (col == 1)) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1 = xBuffer;
                    yPrev1 = yBuffer;

                    xPrev2 = xBuffer;
                    yPrev2 = yBuffer;

                    xPrev2++;
                    yPrev2++;

                    iPrev1 = iBuffer;
                    iPrev2 = iBuffer;
                    iPrev2++;

                    dPrev1 = dBuffer;
                    dPrev2 = dBuffer;
                    dPrev2++;

                    dTemp1 = Globals.getDistance3d(tx, ty, tz, vx, vy, vz);
                }
      
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;
                    dTemp2 = Globals.getDistance3d(tx, ty, tz, vx, vy, vz);

                    outputImage.fillPolyz( 
                        xPrev1, yPrev1, iPrev1, dPrev1,
                        xPrev2, yPrev2, iPrev2, dPrev2,
                        xTemp2, yTemp2, iTemp2, dTemp2,
                        xTemp1, yTemp1, iTemp1, dTemp1, midZImage);

                    xPrev1 = xTemp1;
                    yPrev1 = yTemp1;
                    iPrev1 = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1++;
                    yPrev1++;

                    xPrev2++;
                    yPrev2++;

                    iPrev1++;
                    iPrev2++;

                    dPrev1 = dTemp1;
                    dTemp1 = dTemp2;
                    
                    dPrev1++;
                    dPrev2++;
                }
            } // for col
        } // for row

        outputImage.writeBMP(outputImagePath);

        return 0;
    } // renderMesh


    // This method came from MEMIMG32.CPP
    // Sets height, width and bitsPerPixel parameters
    public static int readBMPHeader(String psFileName, Integer height, Integer width, Integer bitsPerPixel) {
        BITMAPFILEHEADER bmFH;
        BITMAPINFOHEADER pbmIH;
        BITMAPINFO pbmInfo;
        WORD PalSize = 256;
        HANDLE fp;
        int imageSize;
        String errText;

        fp = null;
        fp = CreateFile((LPCTSTR)psFileName, GENERIC_READ, FILE_SHARE_READ, 0, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0);
        if(fp == null) {
            errText = "readBMPHeader: Can't open " + psFileName;
            Globals.statusPrint(errText);
            return 1;
        }

        DWORD numBytesRead;
        ReadFile(fp, bmFH, sizeof(BITMAPFILEHEADER), numBytesRead, null);

        if( bmFH.bfType != 0x4D42 ) {   // if type isn't "BM" ...
            errText = "readBMPHeader: Cannot open: " + psFileName;
            Globals.statusPrint(errText);
            CloseHandle(fp);
            return 2;
        }

        pbmIH = (BITMAPINFOHEADER)GlobalLock(GlobalAlloc( GMEM_FIXED, sizeof(BITMAPINFOHEADER) ));

        ReadFile(fp, pbmIH, (DWORD)sizeof(BITMAPINFOHEADER), numBytesRead, null);

        bitsPerPixel = (WORD)pbmIH.biBitCount;
        if((DWORD)pbmIH.biCompression != BI_RGB) {
            errText = "Compressed image. Not supported: " + psFileName;
            Globals.statusPrint(errText);
            CloseHandle(fp);
            return 3;
        }
        pbmInfo = (BITMAPINFO)GlobalLock(GlobalAlloc( GHND, PalSize + sizeof(BITMAPINFOHEADER) ));

        pbmInfo.bmiHeader = *pbmIH;
        GlobalUnlock( (HANDLE)pbmIH );
        GlobalFree( (HANDLE)pbmIH );
        width = (DWORD) pbmInfo.bmiHeader.biWidth;
        height = (DWORD) pbmInfo.bmiHeader.biHeight;
        imageSize = (DWORD) pbmInfo.bmiHeader.biSizeImage;
        GlobalFree((HANDLE)GlobalHandle(pbmInfo));
        CloseHandle(fp);

        return 0;
    } // readBMPHeader


    // This method came from TWEEN.CPP
    public static int tweenMesh(float aFraction, 
    MemImage aTexture, MemImage aX, MemImage aY, 
    MemImage aZ,
    MemImage bTexture, MemImage bX, MemImage bY, 
    MemImage bZ, 
    MemImage oTexture, MemImage oX, MemImage oY, 
    MemImage oZ) {
        int row, col;
        float aValue, bValue, oValue;
        byte aByte, bByte, oByte;
        byte aRedByte, bRedByte, oRedByte;
        byte aGreenByte, bGreenByte, oGreenByte;
        byte aBlueByte, bBlueByte, oBlueByte;

        if ((aFraction < 0.0f) || (aFraction > 1.0f)) {
            statusPrint("tweenMesh: aFraction must be between 0 and 1");
            return -1;
        }

        float bFraction = 1.0f - aFraction;
        int imHeight = aTexture.getHeight();
        int imWidth  = aTexture.getWidth();
        int bWidth   = bTexture.getWidth();
        if(imWidth != bWidth){
            statusPrint("tweenMesh: texture images must have same width.");
            return -2;
        }

        int bpp = aTexture.getBitsPerPixel();
        if (bpp != 8 && bpp != 24){
            statusPrint("tweenMesh: Texture image must have 8 or 24 bit pixels.");
            return -3;
        }

        for (row = 1; row <= imHeight; row++){
            for (col = 1; col <= imWidth; col++){
                aValue = aX.getMPixel32(col, row);
                bValue = bX.getMPixel32(col, row);
                oValue = (aFraction * aValue) + (bFraction * bValue);
                oX.setMPixel32(col, row, oValue);

                aValue = aY.getMPixel32(col, row);
                bValue = bY.getMPixel32(col, row);
                oValue = (aFraction * aValue) + (bFraction * bValue);
                oY.setMPixel32(col, row, oValue);

                aValue = aZ.getMPixel32(col, row);
                bValue = bZ.getMPixel32(col, row);
                oValue = (aFraction * aValue) + (bFraction * bValue);
                oZ.setMPixel32(col, row, oValue);

                switch (bpp) {
                case 8:
                    aByte = aTexture.getMPixel(col, row);
                    bByte = bTexture.getMPixel(col, row);
                    oByte = (byte)(aFraction * (float)aByte) + (bFraction * (float)bByte);
                    oTexture.setMPixel(col, row, oByte);
                    break;

                case 24:
                    aByte = aTexture.getMPixelRGB(col, row, aRedByte, aGreenByte, aBlueByte);
                    bByte = bTexture.getMPixelRGB(col, row, bRedByte, bGreenByte, bBlueByte);
                    oRedByte   = (byte)(aFraction * (float)aRedByte)   + (bFraction * (float)bRedByte);
                    oGreenByte = (byte)(aFraction * (float)aGreenByte) + (bFraction * (float)bGreenByte);
                    oBlueByte  = (byte)(aFraction * (float)aBlueByte)  + (bFraction * (float)bBlueByte);
                    oTexture.setMPixelRGB(col, row, oRedByte, oGreenByte, oBlueByte);
                    break;
                } // switch
            } // for col
        } // for row

        return 0;
    } // tweenMesh


    // Added as a wrapper for Beep
    public static void beep(int len1, int len2) {
        
    } // beep
} // class Globals