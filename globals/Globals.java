package globals;

import core.MemImage;
import core.RenderObject;
import core.SceneElement;
import core.Shape3d;

import fileUtils.FileUtils;

import globals.JICTConstants;

import java.io.File;
import java.io.LineNumberReader;
import java.util.Random;

import javax.swing.JLabel;

import math.MathUtils;
import math.TMatrix;
import math.Vect;

import structs.Point3d;

public class Globals {
    private static JLabel lblStatus = null;

    private static boolean ictdebug = false;

    // This variable came from ICT20.CPP
    public static Preference ictPreference = new Preference();  // declare a global preference object

    // This variable came from MAINFRM.CPP
    public static GPipe aGraphicPipe = new GPipe();  // a globally defined graphic pipeline for VRML viewing


    // This method came from UTILS.CPP
    public static void statusPrint(String psMessage) {
        File theLog;

        // If the mainframe window is not open, post the message to the log file.
        // else display the message on the status bar and post it to the log file.

        // Open the log file
        theLog = fopen(ictPreference.getPath(Preference.ProcessLog), "a+");

        // If we can't open the log file ...
        if (theLog == null) {
            if(lblStatus != null) {
                lblStatus.setText("statusPrint: Unable to open the ICT log file ict.log");
            }
            return;
        }

        // We were able to open the log file, so write the msg to it
        fwrite(psMessage, psMessage.length(), 1, theLog);
        fclose(theLog);

        // Display the message immediately on the status bar
        lblStatus.setText(psMessage);
    } // statusPrint


    // This method came from DEPTHSRT.CPP
    // Called from:
    //     SceneList.depthSort
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
    public static int blend(MemImage pInImage, MemImage pMaskImage, MemImage pOutImage, 
    float pfAlphaScale) {
        // Blend over the common area in input and mask images
        int iInputRows  = pInImage.getHeight();
        int iInputCols  = pInImage.getWidth();
        int iMaskRows   = pMaskImage.getHeight();
        int iMaskCols   = pMaskImage.getWidth();
        int iCommonRows = Math.min(iInputRows, iMaskRows);
        int iCommonCols = Math.min(iInputCols, iMaskCols);

        // Each MemImage is assumed to be opened for random access
        int ix, iy;
        byte maskPixel, inPixel, outPixel, addedPixel;
        float fInWeight, fOutWeight;

        for(iy = 1; iy <= iCommonRows; iy++) {
            for(ix = 1; ix <= iCommonCols; ix++) {
                maskPixel = pMaskImage.getMPixel(ix, iy);
                inPixel   = pInImage.getMPixel(ix, iy);
                if(maskPixel > 0 && inPixel > 0) {
                    outPixel = pOutImage.getMPixel(ix, iy);
                    fInWeight = (float)maskPixel / 255.0f * pfAlphaScale;
                    fOutWeight = 1.0f - fInWeight;

                    if(pfAlphaScale > 0.0f) {
                        addedPixel = (byte)((fInWeight * (float)inPixel) + (fOutWeight * (float)outPixel) + 0.5f);
                    } else {
                        addedPixel = (byte)((float)outPixel + (fInWeight * (float)inPixel) + 0.5f);
                        // Make certain shadows won't produce negative values
                        if (addedPixel > outPixel) {
                            addedPixel = outPixel;
                        }
                    }

                    if (addedPixel < 1) {
                        addedPixel = (byte)1;
                    }

                    if (pfAlphaScale == 0.0f) {
                        addedPixel = (byte)0;
                    }

                    pOutImage.setMPixel(ix, iy, addedPixel);
                }
            } // for ix
        } // for iy

        return 0;
    } // blend
  

    // This method came from BLEND.CPP
    // This method implements an alpha scale factor which can be used to create 
    // fadein and fadeout effects. See p 95 - 96 of the book 
    // Visual Special Effects Toolkit in C++, by Tim Wittenburg
    // Called from:
    //     tweenImage
    //     RenderObject.renderMeshz
    public static int blendz(MemImage pInImage, MemImage pMatteImage, 
    MemImage pZImage, MemImage pZBuffer,
    MemImage pOutImage,
    float pfAlphaScale) {
        // pZImage is the rendered model's zBuffer image
        // pZBuffer is the effect frame's zBuffer image
        // Both of these need to be considered in a zBuffer render operation since each zBuffered
        // model contributes to the rendered effect frame's zBuffer
        //
        // Blend over the common area in input and matte images
        int iInputRows  = pInImage.getHeight();
        int iInputCols  = pInImage.getWidth();
        int iMatteRows  = pMatteImage.getHeight();
        int iMatteCols  = pMatteImage.getWidth();
        int iCommonRows = Math.min(iInputRows, iMatteRows);
        int iCommonCols = Math.min(iInputCols, iMatteCols);

        int iBpp = pInImage.getBitsPerPixel();
        int iOutBPP = pOutImage.getBitsPerPixel();
        if(iOutBPP != iBpp) {
            String sMsgText = "blendz: inImage bpp: " + iBpp + " must match outImage bpp: " + iOutBPP;
            statusPrint(sMsgText);
            return -1;
        }

        int iMatteBPP = pMatteImage.getBitsPerPixel();
        if(iMatteBPP != 8) {
            statusPrint("blendz: Matte image must be 8 bits per pixel");
            return -2;
        }

        // Each image is assumed to be opened for random access
        int ix, iy;
        byte mattePixel, inPixel, outPixel, addedPixel;
        byte inRed = (byte)0, inGreen = (byte)0, inBlue = (byte)0;
        byte outRed = (byte)0, outGreen = (byte)0, outBlue = (byte)0;
        byte addedRed, addedGreen, addedBlue;
        float fInWeight, fOutWeight;
    
        boolean usingZBuffer = false;
        if((pZImage != null) && (pZBuffer != null)) {
            usingZBuffer = true;
        }
    
        for(iy = 1; iy <= iCommonRows; iy++) {
            for(ix = 1; ix <= iCommonCols; ix++) {
                mattePixel = pMatteImage.getMPixel(ix, iy);
                switch(iBpp) {  // Optionally blend in color or monochrome
                case 8:
                    inPixel = pInImage.getMPixel(ix, iy);
                    if((mattePixel > JICTConstants.I_CHROMAVALUE) && (inPixel > JICTConstants.I_CHROMAVALUE)) {
                        outPixel = pOutImage.getMPixel(ix, iy );
                        fInWeight = (float)mattePixel / 255.0f * pfAlphaScale;
                        fOutWeight = 1.0f - fInWeight;

                        if(pfAlphaScale > 0.0f) {
                            addedPixel = (byte)((fInWeight * (float)inPixel) + (fOutWeight *(float)outPixel) + 0.5f);
                        } else {
                            addedPixel = (byte)((float)outPixel + (fInWeight *(float)inPixel) + 0.5f);
                            // Make certain shadows won't produce negative intensities
                            if (addedPixel > outPixel) {
                                addedPixel = outPixel;
                            }
                        }

                        if (addedPixel < 1) {
                            addedPixel = (byte)1;
                        }
                        if (pfAlphaScale == 0.0f) {
                            addedPixel = (byte)0;
                        }

                        if(usingZBuffer) {
                            if(pZImage.getMPixel32(ix, iy) < pZBuffer.getMPixel32(ix, iy)) { 
                                pZBuffer.setMPixel32(ix, iy, pZImage.getMPixel32(ix, iy));
                                pOutImage.setMPixel(ix, iy, addedPixel);
                            }
                        } else {
                            pOutImage.setMPixel(ix, iy, addedPixel);
                        }
                    } // end if non-zero values
                    break;
        
                case 24:                           // RGB Blend with Z-Buffer
                    pInImage.getMPixelRGB(ix, iy, inRed, inGreen, inBlue);
                    if((mattePixel > JICTConstants.I_CHROMAVALUE) && (inGreen > JICTConstants.I_CHROMAVALUE)) {
                        outPixel = (byte)pOutImage.getMPixelRGB(ix, iy, outRed, outGreen, outBlue);
                        fInWeight  = (float)mattePixel / 255.0f * pfAlphaScale;
                        fOutWeight = 1.0f - fInWeight;

                        if(pfAlphaScale > 0.0f) {
                            addedRed   = (byte)((fInWeight * (float)inRed)   + (fOutWeight *(float)outRed)   + 0.5f);
                            addedGreen = (byte)((fInWeight * (float)inGreen) + (fOutWeight *(float)outGreen) + 0.5f);
                            addedBlue  = (byte)((fInWeight * (float)inBlue)  + (fOutWeight *(float)outBlue)  + 0.5f);
                        } else {  // shadow
                            addedRed   = (byte)((float)outRed   + (fInWeight *(float)inRed)   + 0.5f);
                            addedGreen = (byte)((float)outGreen + (fInWeight *(float)inGreen) + 0.5f);
                            addedBlue  = (byte)((float)outBlue  + (fInWeight *(float)inBlue)  + 0.5f);

                            // Make certain shadows won't produce negative intensities
                            if (addedRed > outRed)     addedRed = outRed;
                            if (addedGreen > outGreen) addedGreen = outGreen;
                            if (addedBlue > outBlue)   addedBlue = outBlue;
                        }

                        if (addedRed < 1)   addedRed   = (byte)1;
                        if (addedGreen < 1) addedGreen = (byte)1;
                        if (addedBlue < 1)  addedBlue  = (byte)1;
                        if (pfAlphaScale == 0.0f) {
                            addedRed   = (byte)0;
                            addedGreen = (byte)0;
                            addedBlue  = (byte)0;
                        }

                        if(usingZBuffer) {
                            if(pZImage.getMPixel32(ix, iy) < pZBuffer.getMPixel32(ix, iy)) { 
                                pZBuffer.setMPixel32(ix, iy, pZImage.getMPixel32(ix, iy));
                                pOutImage.setMPixelRGB(ix, iy, addedRed, addedGreen, addedBlue);
                            }
                        } else {
                            pOutImage.setMPixelRGB(ix, iy, addedRed, addedGreen, addedBlue);
                        }
                    } // end if non zero values
                    break;
                } // switch
            } // for ix
        } // for iy

        return 0;
    } // blendz
  

    // This method came from BLEND.CPP
    // Called from:
    //     RenderObject.prepareCutout
    public static int createCutout(MemImage pOriginalImage, MemImage pMaskImage,
    String psCutoutName, Shape3d pShape) {
        String sMsgText;

        // Create the cutout image and translate the shape to coincide with the cutout.
        // Assumes the mask image is an unpacked (8 bit) mask image opened RANDOM,
        // The original must be opened for sequential access.
        // cutoutName: name of cutout image and shape file without the suffix
        if(pOriginalImage.getAccessMode() != JICTConstants.I_SEQUENTIAL) {
            statusPrint("createCutout: original image access mode must be SEQUENTIAL");
            return 1;
        }

        if (pOriginalImage.getColorSpec() == JICTConstants.I_ONEBITMONOCHROME) {
            statusPrint("createCutout: original image colorSpec cannot be ONEBITMONOCHROME");
            return 2;
        }

        // A cutout version of both the mask and original
        // image is created in which the zero pixel border is removed.
        String sCutoutRImage = "", sCutoutGImage = "", sCutoutBImage = "", sCutoutMImage = "";
        String sCutoutRGBImage = "";

        // Prepare pathnames for mask and cutout images
        String sCutoutDir, sMaskDir;
        sCutoutDir = ictPreference.getPath(Preference.InputImageDirectory);
        sMaskDir   = ictPreference.getPath(Preference.MaskImageDirectory);
        String sCutoutPath, sMaskPath;
    
        FileUtils.appendFileName(sCutoutRImage,   psCutoutName, "r");
        FileUtils.appendFileName(sCutoutGImage,   psCutoutName, "g");
        FileUtils.appendFileName(sCutoutBImage,   psCutoutName, "b");
        FileUtils.appendFileName(sCutoutMImage,   psCutoutName, "a");
        FileUtils.appendFileName(sCutoutRGBImage, psCutoutName, "c");
        sCutoutPath = sCutoutDir + sCutoutRGBImage;
        sMaskPath   = sMaskDir + sCutoutMImage;
    
        int iMaskHeight, iMaskWidth;
        iMaskHeight = pMaskImage.getHeight();
        iMaskWidth  = pMaskImage.getWidth();
        int iy, iMinX, iMaxX, iMinY, iMaxY;
    
        if ((pShape != null) && (pShape.getNumVertices() > 0)) {
            pShape.worldBoundingBox();
            iMinY = (int)pShape.mfMinY;
            iMaxY = (int)pShape.mfMaxY;
            iMinX = (int)pShape.mfMinX;
            iMaxX = (int)pShape.mfMaxX;
        } else {
            statusPrint("createCutout: Shape object not supplied or has 0 vertices");
            return -1;
        }

        // Calculate the size of the new images.
        // Use the shape and enlarge by 1 pixel on each side to account for
        // round off error in the centroid calculation.
        int iNewMinY = iMinY - 1;
        if (iNewMinY < 1) {
            iNewMinY = 1;
        }

        int iNewMaxY = iMaxY + 1;
        if (iNewMaxY > iMaskHeight) {
            iNewMaxY = iMaskHeight;
        }

        int iNewMinX = iMinX - 1;
        if (iNewMinX < 1) {
            iNewMinX = 1;
        }

        int iNewMaxX = iMaxX + 1;
        if (iNewMaxX > iMaskWidth) {
            iNewMaxX = iMaskWidth;
        }

        // Translate the boundary so it is centered within the cutout image
        int iCutoutHeight = iNewMaxY - iNewMinY + 1;
        int iCutoutWidth  = iNewMaxX - iNewMinX + 1;
        int iDx = -(iMinX - 3);  // screen coords are 0 relative,
        int iDy = -(iMinY - 2);  // memImage coords are 1 relative
        pShape.translateW(iDx, iDy, 0.0f);

        // Create the shape file name and write out the translated shape file
        String sShapeDir;
        String sShapeName;
        sShapeDir  = ictPreference.getPath(Preference.ShapeFileDirectory);
        sShapeName = sShapeDir + psCutoutName + ".shp";

        sMsgText = "createCutout: Saving shape file: " + sShapeName;
        statusPrint(sMsgText);

        pShape.invertY(iCutoutHeight);
        int iStatus = pShape.writeShape(sShapeName);
        if(iStatus != 0) {
            sMsgText = "createCutout: Unable to save the shape file. " + iStatus;
            statusPrint(sMsgText);
            return -1;
        }

        // Open two new output images
        boolean color = false;
        if(pOriginalImage.getColorSpec() == JICTConstants.I_RGBCOLOR) {
            color = true;
        }

        MemImage cutoutMImg = new MemImage(iCutoutHeight, iCutoutWidth);
        if (!cutoutMImg.isValid()) {
            sMsgText = "createCutout: Unable to open cutout alpha image: " + sCutoutMImage;
            statusPrint(sMsgText);
            return 3;
        }
    
        MemImage cutoutGImg = new MemImage(iCutoutHeight, iCutoutWidth);
        if (!cutoutGImg.isValid()) {
            sMsgText = "createCutout: Unable to open cutout g image: " + sCutoutGImage;
            statusPrint(sMsgText);
            return 4;
        }

        MemImage cutoutRImg = new MemImage(1, 1);
        MemImage cutoutBImg = new MemImage(1, 1);
        if(color) {
            cutoutRImg = new MemImage(iCutoutHeight, iCutoutWidth);
            if (!cutoutRImg.isValid()) {
                sMsgText = "createCutout: Unable to open cutout r image: " + sCutoutRImage;
                statusPrint(sMsgText);
                return 5;
            }

            cutoutBImg = new MemImage(iCutoutHeight, iCutoutWidth);
            if (!cutoutBImg.isValid()) {
                sMsgText = "createCutout: Unable to open cutout b image: " + sCutoutBImage;
                statusPrint(sMsgText);
                return 6;
            }
        }
    
        for(iy = 1; iy < iMaskHeight - iNewMaxY; iy++) {
            pOriginalImage.readNextRow();
        }

        int iyCounter = 1;
        for (iy = iMaskHeight - iNewMaxY; iy <= iMaskHeight - iNewMinY; iy++) {
            pOriginalImage.readNextRow();
            int ixCounter = 0;

            for(int ix = iNewMinX; ix <= iNewMaxX; ix++) {
                ixCounter++;
                byte theMaskValue = pMaskImage.getMPixel(ix, iy);

                if(theMaskValue > 0) {
                    cutoutMImg.setMPixel(ixCounter, iyCounter, theMaskValue);
                    if(!color) {
                        cutoutGImg.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(ix, 1));
                    }
            
                    if(color) {
                        cutoutRImg.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(ix, 1, 'R'));
                        cutoutGImg.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(ix, 1, 'G'));
                        cutoutBImg.setMPixel(ixCounter, iyCounter, pOriginalImage.getMPixel(ix, 1, 'B'));
                    }
                } else {
                    cutoutMImg.setMPixel(ixCounter, iyCounter, (byte)0);
                    cutoutGImg.setMPixel(ixCounter, iyCounter, (byte)0);
                    if(color) {
                        cutoutRImg.setMPixel(ixCounter, iyCounter, (byte)0);
                        cutoutBImg.setMPixel(ixCounter, iyCounter, (byte)0);
                    }
                }
            } // for ix

            iyCounter++;
        } // for iy

        // Smooth the mask
        statusPrint("createCutout: Smoothing the cutout mask");
        cutoutMImg.alphaSmooth5();
    
        cutoutGImg.writeBMP(sCutoutGImage);
        sMsgText = "createCutout: Saving alpha image: " + sMaskPath;
        statusPrint(sMsgText);
        cutoutMImg.writeBMP(sMaskPath);
    
        if(color) {
            cutoutRImg.writeBMP(sCutoutRImage);
            cutoutBImg.writeBMP(sCutoutBImage);
            sMsgText = "createCutout: Saving color cutout image: " + sCutoutPath;
            statusPrint(sMsgText);
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
    // in_boundary(x, y)
    //
    // Determines whether the intensity at 'x, y' is within the boundary
    // being outlined. Points outside of the array of intensities are not
    // in the boundary.
    //
    // Returns 0 if the point is not in the boundary.
    // Returns 1 if the point is     in the boundary.
    // 
    // Called from:
    //     probe
    public static boolean in_boundary(MemImage pImage, int piX, int piY) {
        int imHeight = pImage.getHeight();
        int imWidth  = pImage.getWidth();
        int iBpp     = pImage.getBitsPerPixel();
        Byte red = 0, green = 0, blue = 0;

        if ((piX < 1) || (piX > imWidth) || (piY < 1) || (piY > imHeight)) {
            return false;
        }

        switch(iBpp) {
        case 8:
            if (pImage.getMPixel(piX, piY) != JICTConstants.I_CHROMAVALUE) {
                return true;
            } else {
                return false;
            }

        case 24:
            // The following method sets parameters red, green, and blue
            pImage.getMPixelRGB(piX, piY, red, green, blue);
            if (
            (red   != JICTConstants.I_CHROMARED) || 
            (green != JICTConstants.I_CHROMAGREEN) ||
            (blue  != JICTConstants.I_CHROMABLUE)) {
                return true;
            } else {
                return false;
            }

        default:
            statusPrint("in_boundary: Image must have 8 or 24 bit pixels");
            return false;
        } // switch
    } // in_boundary


    // This method came from BLEND.CPP
    //
    // probe(x, y, dir, new_x, new_y)
    //
    // Checks a sample neighboring 'x, y' to see if it is in the boundary
    // being outlined.  'dir' specifies which neighboring sample to check.
    // 'new_x, new_y' always get the coordinates of the neighbor.
    //
    // Returns 0 if the neighbor is not in the boundary.
    // Returns 1 if the neighbor is     in the boundary.
    public static boolean probe(MemImage pImage, int piX, int piY, int piDir, 
    Integer pINewX, Integer pINewY) {
        // Figure out coordinates of neighbor
        if ((piDir < 2) || (piDir > 6)) {
            ++piX;
        }

        if ((piDir > 2) && (piDir < 6)) {
            --piX;
        }

        if ((piDir > 0) && (piDir < 4)) {
            ++piY;
        }

        if (piDir > 4) {
            --piY;
        }

        // Always return the new coordinates
        pINewX = piX;
        pINewY = piY;

        // Determine if the new sample point is in the boundary
        return (in_boundary(pImage, piX, piY));
    } // probe


    // This method came from BLEND.CPP
    //
    // neighbor(x, y, last_dir, new_x, new_y)
    //
    // Finds a neighbor of the sample at 'x, y' that is in the same
    // boundary.  Always follows the boundary in a clockwise direction.
    // 'last_dir' is the direction that was used to get to 'x, y'
    // when it was found.  'new_x, new_y' always get the coordinates
    // of the neighbor.
    //
    // This procedure should only be called for a sample that has at
    // least one neighbor in the same boundary.
    //
    // Returns the direction to the neighbor.
    public static int neighbor(MemImage anImage, int x, int y, int last_dir, 
    Integer new_x, Integer new_y) {
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
            if (probe(anImage, x, y, new_dir, new_x, new_y)) {
                // Found the next clockwise edge neighbor --
                // its coordinates have already been
                // stuffed into new_x, new_y

                return(new_dir);
            } else {
                // Check the next clockwise neighbor
                if (--new_dir < 0) {
                    new_dir += 8;
                }
            }
        }

        return 0;
    } // neighbor


    // This method came from BLEND.CPP
    //
    // shapeFromImage
    //
    // Builds a shape object that describes the boundary of the first contigous 
    // blob of non-zero pixels it finds.  Always follows the boundary
    // in a clockwise direction.  Uses 'start_x, start_y' as the
    // starting point. 
    //
    // Returns 0 if successful.
    // Returns 1 if unsuccessful.
    //
    // Called from:
    //     tweenImage
    public static int shapeFromImage(MemImage anImage, Shape3d aShape) {
        int	x, y;
        Integer	new_x, new_y;
        int	dir, last_dir;
        int start_x = 1;
        int start_y = 1;
        int row, col;
        int counter = 0;

        int imHeight = anImage.getHeight();
        int imWidth  = anImage.getWidth();
        int bpp      = anImage.getBitsPerPixel();
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

        // Go left in the starting row until out of the boundary
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
            if (probe(anImage, x, y, dir, new_x, new_y)) {
                // Found a neighbor in that direction (its coordinates are in new_x, new_y
                // but we don't use them here)

                break;
            }

            // Try next direction
            if (++dir == 8) {
                // Starting point has no neighbors -- make the chain one vector long
                
                // Fill in the vector -- the direction is arbitrary,
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

            // Maybe done with boundary
            if ( (new_x == start_x) && (new_y == start_y) ) {
                return 0;
            }

            // Else get ready to continue following the edge
            x = new_x;
            y = new_y;
            last_dir = dir;
        }

        return 0;
    } // shapeFromImage


    // This method came from MOTION.CPP
    // TODO: Replace parameter filein with a FileStream
    // Called from:
    //     MotionPath.readMotion
    public static String getNextMotionLine(String theText, Integer lineNumber, 
    LineNumberReader filein) {
        boolean aComment = true;
        int theLength = 80;
        String theKeyWord;
      
        while (aComment) {
            filein.getline(theText, theLength);  // Ignore comments and near empty lines
            if(filein.eof()) {
                theText = "EOF";
                theKeyWord = theText;
                return(theKeyWord);
            }

            lineNumber++;
            if (theText.startsWith("//") || theText.length() < 2) // Single C/R
                aComment = true;
            else
                aComment = false;
        }

        theKeyWord = theText;
        return(theKeyWord);
    } // getNextMotionLine


    // This method came from MOTION.CPP
    public static int motionBlur(String firstImagePath, String outputDir, 
    int numFrames, int blurDepth) {
        String msgText;
        MemImage[] images = new MemImage[32]; 
        MemImage outImage;
        String directory = "", fileName = "", prefix = "", inSuffix = "";
        String currentPath = "";
        // String inPath; // This variable is not used
        String outPath = "", outSuffix;
        byte red = (byte)0, green = (byte)0, blue = (byte)0;
        int blur, numOpenImages, bucket, redBucket, greenBucket, blueBucket;
        int frameNum = 0, i, j, status;
        Integer imHeight = 0, imWidth = 0, bpp = 0;
        int frameCounter, row, col;

        if(blurDepth > 15) {
            statusPrint("motionBlur: blurDepth cannot be > 15");
            return -1;
        }

        // The directory includes the drive letter
        status = FileUtils.getPathPieces(firstImagePath, directory, fileName, prefix, 
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
            // Open and close the appropriate images
            if(frameCounter == frameNum + blurDepth) {
                for(i = -blurDepth; i <= blurDepth; i++) { // open the first blurDepth images
                    FileUtils.makePath(currentPath, directory, prefix, frameCounter + i, inSuffix);
                    switch(bpp) {
                    case 8:
                        images[i + blurDepth] = new MemImage(currentPath, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_EIGHTBITMONOCHROME);
                        break;

                    case 24:
                        images[i + blurDepth] = new MemImage(currentPath, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                        break;
                    } // switch

                    if(!images[i + blurDepth].isValid()) {
                        msgText = "motionBlur: Unable to open image: " + currentPath;
                        statusPrint(msgText);
                        return -4;
                    }
                }
            } else {
                for (j = 0; j < numOpenImages - 1; j++) { // Move the image pointers
                    images[j] = images[j + 1];
                }

                // Open new image
                FileUtils.makePath(currentPath, directory, prefix, frameCounter + blurDepth, inSuffix);
                switch(bpp) {
                case 8:
                    images[numOpenImages - 1] = new MemImage(currentPath, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_EIGHTBITMONOCHROME);
                    break;

                case 24:
                    images[numOpenImages - 1] = new MemImage(currentPath, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
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
            FileUtils.makePath(outPath, outputDir, prefix, frameCounter, outSuffix);
            outImage = new MemImage(imHeight, imWidth, bpp);

            for (row = 1; row < imHeight; row++) {
                for (col = 1; col < imWidth; col++) {
                    bucket = 0;
                    redBucket   = 0;
                    greenBucket = 0;
                    blueBucket  = 0;

                    for (blur = 0; blur < numOpenImages; blur++) {
                        switch (bpp) {
                        case 8:
                            bucket += images[blur].getMPixel(col, row);
                            break;

                        case 24:
                            images[blur].getMPixelRGB(col, row, red, green, blue);
                            redBucket   += red;
                            greenBucket += green;
                            blueBucket  += blue;
                            break;

                        default:
                            statusPrint("motionBlur: image must be 8 or 24 bits per pixel");
                            return -1;
                        }  // switch
                    } // for blur

                    switch(bpp) {
                    case 8:
                        avgBucket = bucket / numOpenImages;
                        outImage.setMPixel(col, row, (byte)(avgBucket + 0.5));
                        break;

                    case 24:
                        avgRedBucket   = redBucket / numOpenImages;
                        avgGreenBucket = greenBucket / numOpenImages;
                        avgBlueBucket  = blueBucket / numOpenImages;
                        outImage.setMPixelRGB(col, row, 
                            (byte)(avgRedBucket + 0.5f),
                            (byte)(avgGreenBucket + 0.5f),
                            (byte)(avgBlueBucket + 0.5f));
                        break;
                    } // switch
                } // for col
            } // for row

            // Save the blurred image
            msgText = "Saving: " + outPath;
            statusPrint(msgText);
            outImage.writeBMP(outPath);
        } // for frameCounter

        return 0;
    } // motionBlur


    // This method performs planar texture mapping.
    // See p 157 - 160 of Visual Special Effects Toolkit in C++.
    // This method came from IWARP.CPP
    // Called from:
    //     Globals.tweenImage
    //     MainFrame.onToolsWarpImage
    public static int iwarpz(MemImage inImage, MemImage outImage, MemImage zImage,
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    float vx, float vy, float vz,
    TMatrix viewMatrix,
    float pfRefPointX, float pfRefPointY, float pfRefPointZ) {
        // To use this function without a zBuffer, call with zImage = null.
        // in this case, vx, vy, and vz are ignored
        String msgText;
        int x, y;
        int myStatus;
        Integer numXCoordsFound;
        int[] screenXCoords = new int[JICTConstants.I_MAXWVERTICES];
        float[] tZCoords = new float[JICTConstants.I_MAXWVERTICES]; 
        float[] tXCoords = new float[JICTConstants.I_MAXWVERTICES]; 
        float[] tYCoords = new float[JICTConstants.I_MAXWVERTICES];

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if(ictdebug) {
            statusPrint("iwarpz input arguments");

            msgText = String.format("rx: %6.2f  ry: %6.2f  rz: %6.2f", rx, ry, rz);
            statusPrint(msgText);

            msgText = String.format("sx: %6.2f  sy: %6.2f  sz: %6.2f", sx, sy, sz);
            statusPrint(msgText);

            msgText = String.format("tx: %6.2f  ty: %6.2f  tz: %6.2f", tx, ty, tz);
            statusPrint(msgText);

            msgText = String.format("refx: %6.2f  refy: %6.2f  refz: %6.2f", 
                pfRefPointX, pfRefPointY, pfRefPointZ);
            statusPrint(msgText);
        }

        //  Build the forward and inverse transformation matrices
        TMatrix forwardMatrix = new TMatrix();

        // F_DTR = floating-point degree to radian conversion factor
        float XRadians = rx * JICTConstants.F_DTR;
        float YRadians = ry * JICTConstants.F_DTR;
        float ZRadians = rz * JICTConstants.F_DTR;

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
        float intRefPointX = pfRefPointX + halfInWidth;
        float intRefPointY = pfRefPointY + halfInHeight;
        float intRefPointZ = pfRefPointZ;

        // Load a shape object with the original image boundary coordinates
        Shape3d aShape = new Shape3d(4);
        aShape.addWorldVertex(          1.0f,            1.0f, 0.0f);
        aShape.addWorldVertex((float)inWidth,            1.0f, 0.0f);
        aShape.addWorldVertex((float)inWidth, (float)inHeight, 0.0f);
        aShape.addWorldVertex(          1.0f, (float)inHeight, 0.0f);

        // Transform and project the image coords, taking into account the reference point
        viewModelMatrix.transformAndProject(aShape, outHeight, outWidth, 
            true, intRefPointX, intRefPointY, intRefPointZ);

        if(ictdebug) {
            aShape.printShape("Transformed Image Boundary:");
        }

        aShape.screenBoundingBox();
        float minY = aShape.mfMinY;
        float maxY = aShape.mfMaxY;
        float minX = aShape.mfMinX;
        float maxX = aShape.mfMaxX;
        
        aShape.transformBoundingBox();

        if (ictdebug) {
            // Inverse check. Map transformed shape cornerpoints into original image
            aShape.initCurrentVertex();
            float xo, yo, zo;

            for (int index = 1; index <= aShape.getNumVertices(); index++) {
                float anX = aShape.mCurrentVertex.tx;
                float anY = aShape.mCurrentVertex.ty;
                float anZ = aShape.mCurrentVertex.tz;
                inverseMatrix.transformPoint (anX, anY, anZ, xo, yo, zo);
                // aShape.iCurrVtxIdx++;
                aShape.incCurrentVertex();

                msgText = String.format("transformed: %6.2f %6.2f %6.2f texture: %6.2f %6.2f %6.2f",
                    anX, anY, anZ, 
                    xo + halfInWidth, yo + halfInHeight, zo);
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
            myStatus = getIntervals(aShape, y, numXCoordsFound, JICTConstants.I_MAXWVERTICES,
                screenXCoords, tXCoords, tYCoords, tZCoords);

            if (myStatus != 0) {
                msgText = "iwarp: getInterval error: " + myStatus;
                statusPrint(msgText);
                return 2;
            }

            if (ictdebug) {
                statusPrint("y:\tsx  \ttx  \tty  \ttz");
                for(int i = 0; i < numXCoordsFound; i++) {
                    msgText = String.format("%d\t%d\t%6.2f\t%6.2f\t%6.2f" , y, screenXCoords[i],
                        tXCoords[i], tYCoords[i], tZCoords[i]);
                    statusPrint(msgText);
                }
            }

            if (numXCoordsFound != 2) {
                msgText = "iWarp: numCoords <> 2. y: " + y + " numCoords " + numXCoordsFound;
                statusPrint(msgText);
                for(int i = 0; i < numXCoordsFound; i++) {
                    msgText = String.format("%d\t%d\t%6.2f\t%6.2f\t%6.2f" , y, screenXCoords[i],
                        tXCoords[i], tYCoords[i], tZCoords[i]);
                    statusPrint(msgText);
                    goto nextScanLine;
                }
            }

            dx = tXCoords[1] - tXCoords[0];
            dy = tYCoords[1] - tYCoords[0];
            dz = tZCoords[1] - tZCoords[0];
            numSteps = (int)screenXCoords[1] - (int)screenXCoords[0] + 1;
            
            // Initialize xIncrement, yIncrement, and zIncrement
            // xIncrement will be used to modify xIn
            // yIncrement will be used to modify yIn
            // zIncrement will be used to modify zIn
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
            if(dpx > 0.5f) dpx = 0.5f;
            if(dpy > 0.5f) dpy = 0.5f;

            // Loop through a single scan line
            for(x = (int)screenXCoords[0];x <= (int)screenXCoords[1]; x++) {
                // Determine the transformed x, y by inverting the true perspective
                // projection
                w = (zIn + d) / d;
                xIn = (x - halfInWidth) * w;
                yIn = (y - halfInHeight)* w;

                // The following method sets xOut, yOut and zOut
                inverseMatrix.transformPoint(xIn, yIn, zIn, xOut, yOut, zOut);

                if(ictdebug) {
                    if(
                    (x == (int)screenXCoords[0]) || 
                    (x == (int)screenXCoords[1])) {
                        msgText = String.format("scanLine: %2d xi: %6.2f yi: %6.2f zi: %6.2f xo: %6.2f yo: %6.2f zo: %6.2f",
                            y, xIn, yIn, zIn, xOut, yOut, zOut);
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
                    theZ = zImage.getMPixel32((int)(x + xCentOffset), (int)(y + yCentOffset));
                    aDist = MathUtils.getDistance3d(xIn, yIn, zIn, vx, vy, vz);

                    // Update the zbuffer if a smaller distance and non transparent color
                    if((aDist < theZ) && ((int)intensity != JICTConstants.I_CHROMAVALUE)) {
                        zImage.setMPixel32((int)(x + xCentOffset), (int)(y + yCentOffset), aDist);
                        switch(bpp) {
                        case 8:
                            outImage.setMPixel((int)(x + xCentOffset), (int)(y + yCentOffset), intensity);
                            break;

                        case 24:
                            outImage.setMPixelRGB((int)(x + xCentOffset), (int)(y + yCentOffset), red, green, blue);
                            break;
                        }
                    }
                } else {
                    switch(bpp) {
                    case 8:
                        outImage.setMPixel((int)(x + xCentOffset), (int)(y + yCentOffset), intensity);
                        break;

                    case 24:
                        outImage.setMPixelRGB((int)(x + xCentOffset), (int)(y + yCentOffset), red, green, blue);
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
    // Called from: 
    //     iwarpz
    public static int getIntervals(Shape3d theShape, int y, Integer numCoords,
    int numAllocatedXCoords, int[] screenXCoords,
    float[] tXCoords, float[] tYCoords, float[] tZCoords) {
        // Scan Conversion. 
        // For the indicated scan line y, find all screen x coords
        // where the shape crosses scan line y.  
        // Sort the resulting screen x coordinate array.
        // For each screen x, find the corresponding tx, ty, and tz by interpolating 
        // from the two cornerpoints.
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

        Float m = 0f, b = 0f;
        int i, index, newX;
        Boolean horzFlag = false, vertFlag = false;
        int iCurrentScreenXIdx; // index into array screenXCoords
        // float *currenttX, *currenttY, *currenttZ; 
        int tXCoordsIdx, tYCoordsIdx, tZCoordsIdx;
        float theX;
        iCurrentScreenXIdx = 0;
        tXCoordsIdx = 0;
        tYCoordsIdx = 0;
        tZCoordsIdx = 0;
        numCoords   = 0;
        int sx1, sy1, sx2, sy2, minx, maxx, miny, maxy;
        float tx1, ty1, tz1, tx2, ty2, tz2;
        float partialDistance, totalDistance, ratio;

        theShape.initCurrentVertex();
        for (index = 1; index <= numShapeVertices; index++) {
            sx1 = (int)theShape.mCurrentVertex.sx;
            sy1 = (int)theShape.mCurrentVertex.sy;
            
            tx1 = theShape.mCurrentVertex.tx;
            ty1 = theShape.mCurrentVertex.ty;
            tz1 = theShape.mCurrentVertex.tz;
            // theShape.currentVertex++;
            theShape.incCurrentVertex();

            // If this is the last line segment, circle around to the beginning
            if(index == numShapeVertices) {
                theShape.initCurrentVertex();
            }
            sx2 = (int)theShape.mCurrentVertex.sx;  // Can't use (currentVertex+1).x
            sy2 = (int)theShape.mCurrentVertex.sy;
            
            tx2 = theShape.mCurrentVertex.tx;
            ty2 = theShape.mCurrentVertex.ty;	 
            tz2 = theShape.mCurrentVertex.tz;
            theShape.decCurrentVertex();

            minx = Math.min(sx1, sx2);
            maxx = Math.max(sx1, sx2);
            miny = Math.min(sy1, sy2);
            maxy = Math.max(sy1, sy2);

            // The following method sets variables m, b, horzFlag and vertFlag
            MathUtils.getLineEquation(sx1, sy1, sx2, sy2, m, b, horzFlag, vertFlag);
            theX = 0.0f;
            if(m != 0.0f) {
                theX = ((float)y - b) / m;
            }
            newX = (int)theX;
            
            if(ictdebug) {
                msgText = "getIntervals: sx1: " + sx1 + "  sx2: " + sx2 + 
                    "  sy1: " + sy1 + " sy2: " + sy2;
                statusPrint(msgText);

                msgText = "getIntervals: index: " + index + " newX: " + newX + 
                    "  Horz: " + horzFlag + "  vert: " + vertFlag; 
                statusPrint(msgText);
            }
            
            if (!(horzFlag || vertFlag)) {
                // Determine z by interpolating between screen line segment endpoints
                totalDistance   = MathUtils.getDistance2d(sx1, sy1, sx2, sy2);
                partialDistance = MathUtils.getDistance2d(newX,  y, sx1, sy1);
                // This is a ratio of screen coordinates
                if(totalDistance != 0.0f) {
                    ratio = partialDistance/totalDistance; // 0 <= ratio <= 1
                } else {
                    statusPrint("getIntervals: totalDistance cannot equal 0");
                    return -1;
                }
                
                ratio = 1.0f - ratio;
                
                if ((newX >= minx && newX <= maxx) && (y >= miny && y <= maxy)) {
                    screenXCoords[iCurrentScreenXIdx] = newX;
                    tXCoords[tXCoordsIdx] = tx2 + (ratio * (tx1 - tx2));
                    tYCoords[tYCoordsIdx] = ty2 + (ratio * (ty1 - ty2));	
                    tZCoords[tZCoordsIdx] = tz2 + (ratio * (tz1 - tz2));
                    if(ictdebug) {
                        statusPrint("diagPoint");
                    }

                    tXCoordsIdx++;
                    tYCoordsIdx++;
                    tZCoordsIdx++;
                    iCurrentScreenXIdx++;
                    intDistance[index-1] = MathUtils.intervalDistance(minx, maxx, (int)theX);
                    numCoords++;
                    // end if between sx1 and sx2
                } else { 
                    // Store the point for possible later use
                    tempScreenXCoords[tempIndex] = (int)theX;
                    tempXCoords[tempIndex] = tx2 + (ratio * (tx1 - tx2));
                    tempYCoords[tempIndex] = ty2 + (ratio * (ty1 - ty2));	
                    tempZCoords[tempIndex] = tz2 + (ratio * (tz1 - tz2));
                    intDistance[tempIndex] = MathUtils.intervalDistance(minx, maxx, (int)theX);
                    tempIndex++;

                    if(ictdebug) {
                        statusPrint("non diagPoint");
                    }
                }
                // end if not horizontal or vertical
            } else {
                // Handle horizontal and vertical lines
                if (vertFlag) {
                    totalDistance   = Math.abs(sy2 - sy1);
                    partialDistance = Math.abs(y - sy1);		
                    if(totalDistance != 0.0f) {
                        ratio = partialDistance/totalDistance; // 0 <= ratio <= 1
                    } else {
                        statusPrint("getIntervals: totalDistance cannot equal 0");
                        return -1;
                    }

                    ratio = 1.0f - ratio;
                    if (y >= miny && y <= maxy) {
                        screenXCoords[iCurrentScreenXIdx] = sx1;
                        tXCoords[tXCoordsIdx] = tx1;
                        tYCoords[tYCoordsIdx] = ty2 + (ratio * (ty1 - ty2));	
                        tZCoords[tZCoordsIdx] = tz2 + (ratio * (tz1 - tz2));

                        iCurrentScreenXIdx++;
                        tXCoordsIdx++;
                        tYCoordsIdx++;
                        tXCoordsIdx++;
                        intDistance[index-1] = MathUtils.intervalDistance(miny, maxy, y);
                        numCoords++;
                        if(ictdebug) {
                            statusPrint("vertPoint");
                        }
                    } else {
                        // Store the point for possible later use
                        tempScreenXCoords[tempIndex] = sx1;
                        tempXCoords[tempIndex] = tx1;
                        tempYCoords[tempIndex] = ty2 + (ratio * (ty1 - ty2));	
                        tempZCoords[tempIndex] = tz2 + (ratio * (tz1 - tz2));
                        intDistance[tempIndex] = MathUtils.intervalDistance(miny, maxy, y);
                        tempIndex++;
                    }
                } // if vertFlag
            }

            // theShape.currentVertex++;
            theShape.miCurrVtxIdx++;
        }

        // Sort the found x coordinates in ascending order
        insertionSort(screenXCoords, tXCoords, tYCoords, tZCoords, numCoords);
        removeDuplicates(screenXCoords, tXCoords, tYCoords, tZCoords, numCoords);

        if(numCoords > 2) {
            removeSimilar(screenXCoords, tXCoords, tYCoords, tZCoords, numCoords, 2);
        }

        int minIntDist = 999999999;
        int aCol = 0;

        if (numCoords == 1) {
            for(i = 0; i < tempIndex; i++) {
                if(intDistance[i] < minIntDist) {
                    aCol = i;
                    minIntDist = intDistance[i];
                }
            } // for i

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
        } // if numCoords == 1

        if(ictdebug) {
            statusPrint("getIntervals Found: intdist\t sx  \t tx  \t ty  \t tz");
            for(i = 0; i < numCoords; i++) {
                msgText = String.format("\t%d\t%d\t%6.2f\t%6.2f\t%6.2f", 
                    intDistance[i], screenXCoords[i], tXCoords[i], tYCoords[i], tZCoords[i]);
                statusPrint(msgText);
            }
        }
        
        return 0;
    } // getIntervals


    // This method came from IWARP.CPP
    // Called from:
    //     getIntervals
    public static void insertionSort(int theItems[], 
    float itemData1[], float itemData2[], float itemData3[], 
    int numItems) {
        // Sort theItems into ascending order, carrying along the three optional 
        // itemData arrays.
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
    public static void insertionSort(int theItems[], 
    int itemData1[], float itemData2[], float itemData3[], float itemData4[], 
    int numItems) {
        // Sort theItems into ascending order, carrying along the four optional 
        // itemData arrays.
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
            } // for indexTmp

            theItems[indexTmp]  = itemTemp;
            itemData1[indexTmp] = itemData1Temp;
            itemData2[indexTmp] = itemData2Temp;
            itemData3[indexTmp] = itemData3Temp;
            itemData4[indexTmp] = itemData4Temp;
        } // for index

        return;
    } // insertionSort


    // This method came from IWARP.CPP
    // Called from:
    //     getIntervals
    public static int removeDuplicates(int theList[], 
    float theItemData1[], float theItemData2[], float theItemData3[], 
    Integer listLength) {
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
                } // for index2

                listLength--;
                index--;
            }
        } // for index

        return 0;
    } // removeDuplicates


    // This method came from IWARP.CPP
    public static int removeDuplicates(int theList[], int theItemData1[], 
    float theItemData2[], float theItemData3[], float theItemData4[], 
    Integer listLength) {
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
                    theItemData4[index2] = theItemData4[index2 + 1];
                } // for index2

                listLength--;
                index--;
            }
        } // for index

        return 0;
    } // removeDuplicates


    // This method came from IWARP.CPP
    // Called from:
    //     getIntervals
    public static int removeSimilar(int theList[], 
    float theItemData1[], float theItemData2[], float theItemData3[], 
    Integer listLength, int difference) {
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
                } // for index2

                listLength--;
                index--;
            }
        } // for index

        return 0;
    } // removeSimilar


    // This method came from IWARP.CPP
    public static int iRender(MemImage outImage, MemImage maskImage, MemImage inImage,
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    TMatrix viewMatrix,
    boolean warpIndicator, boolean blendIndicator, 
    float alphaScale,
    float refPointX, float refPointY, float refPointZ) {
        int outputRows = outImage.getHeight();
        int outputCols = outImage.getWidth();

        MemImage midImage = new MemImage(1, 1);
        MemImage midMaskImage = new MemImage(1, 1);
        /* These variables are not used
        int xOffset = (int)tx; // Set these for the noblend nowarp case
        int yOffset = (int)ty;
        int imXOffset, imYOffset, msXOffset, msYOffset;
        */
        float vx = 0.0f, vy = 0.0f, vz = 0.0f;

        /* The following code sets xOffset and yOffset, but their values are there
           after not used.
        if(!(warpIndicator || blendIndicator)) { // Background plate
            xOffset = 0;
            yOffset = 0;
        }
        */

        if(warpIndicator) {
            midImage = new MemImage(outputRows, outputCols); // Open intermediate image
            if (!midImage.isValid()) {
                statusPrint("Unable to open intermediate warp image");
                return -1;
            }

            fwarpz(inImage, midImage, null, 
                rx, ry, rz, 
                sx, sy, sz, 
                tx, ty, tz,
                vx, vy, vz,
                viewMatrix,
                refPointX, refPointY, refPointZ);
        } // if warpIndicator

        if(warpIndicator && blendIndicator) {
            // Open intermediate matte image
            midMaskImage = new MemImage(outputRows, outputCols);
            if (!midMaskImage.isValid()) {
                statusPrint("Unable to open intermediate warp mask image");
                return -1;
            }
            fwarpz(maskImage, midMaskImage, null, 
                rx, ry, rz, 
                sx, sy, sz, 
                tx, ty, tz,
                vx, vy, vz,
                viewMatrix,
                refPointX, refPointY, refPointZ);
        } // if warpIndicator && blendIndicator

        // Composite the cutout image into the output scene
        int myStatus = 0;
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
    // Called from:
    //     SceneList.render
    public static int iRenderz(MemImage outImage, MemImage matteImage, MemImage inImage,
    MemImage zImage, MemImage zBuffer,
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    float vx, float vy, float vz,
    TMatrix viewMatrix,
    boolean warpIndicator, boolean blendIndicator, 
    float alphaScale,
    float refPointX, float refPointY, float refPointZ) {
        int outputRows = outImage.getHeight();
        int outputCols = outImage.getWidth();

        MemImage midImage = new MemImage(1, 1);
        MemImage alphaImage = new MemImage(1, 1);
        int xOffset = (int)tx; // Set these for the blend nowarp case
        int yOffset = (int)ty;
        // int imXOffset, imYOffset, msXOffset, msYOffset; // these variables are not used

        // If matteImage (same as alpha image) is NULL, the alpha image is created
        // from the warped model image. 
        // If matteImage is not NULL, compositing process uses the supplied image
        //
        // Use of the ZBuffer is handled in the rendering/blending functions called 
        // by iRenderz

        // Controls whether texture mapping occurs with forward
        // or inverse warp procedures. Quadmesh models are unaffected
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
        } // if warpIndicator

        if(blendIndicator) {
            alphaImage = new MemImage(outputRows, outputCols);
            if (!alphaImage.isValid()) {
                statusPrint("iRenderz: Unable to open intermediate alpha image");
                return -1;
            }

            if (matteImage != null) {     // Its been supplied by the user, warp it
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

                // Generate the alpha image
            } else { 
                // Create the alpha image
                midImage.createAlphaImage(alphaImage);
                alphaImage.alphaSmooth3();
            }
        } // if blendIndicator

        // Composite the element into the background plate
        int myStatus = 0;
        if(blendIndicator) {
            // Blend but no warp
            if(!warpIndicator) {
                myStatus = blendz(inImage, alphaImage, zImage, zBuffer, outImage, alphaScale);
            } else {
                // Blend and warp
                myStatus = blendz(midImage, alphaImage, zImage, zBuffer, outImage, alphaScale);
            }
        } else {
            if(warpIndicator) { 
                // Copy warped image to background plate
                midImage.copy(outImage, 0, 0);
            } else {
                // Copy input image to output
                inImage.copy(outImage, xOffset, yOffset);
            }
        }

        return myStatus;
    } // iRenderz


    // This method came from IWARP.CPP
    // Called from:
    //     MainFrame.onToolsWarpImage
    //     SceneList.render
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
        weight[0][0] = -0.01;    // sinc function
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
        /* These variables are not used
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        */
        int row, col;
        float sum;
        float q00, q10, q20;
        float q01, q11, q21;
        float q02, q12, q22;
        float sumr;
        float q00r, q10r, q20r = 0.0f;
        float q11r = 0.0f, q21r = 0.0f;
        float q12r = 0.0f, q22r = 0.0f;
        float sumg;
        float q00g, q10g, q20g = 0.0f;
        float q11g = 0.0f, q21g = 0.0f;
        float q12g = 0.0f, q22g = 0.0f;
        float sumb;
        float q00b, q10b, q20b = 0.0f;
        float q11b = 0.0f, q21b = 0.0f;
        float q12b = 0.0f, q22b = 0.0f;
        Byte red = (byte)0, green = (byte)0, blue = (byte)0;

        for (row = 2; row <= imHeight - 1; row++) {
            for (col = 2; col <= imWidth - 1; col++) {
                switch(bpp) {
                case 8:
                    q00 = inImage.getMPixel(col - 1, row - 1) * weight[0][0];
                    q10 = inImage.getMPixel(col,     row - 1) * weight[1][0];
                    q20 = inImage.getMPixel(col + 1, row - 1) * weight[2][0];

                    q01 = inImage.getMPixel(col - 1, row)     * weight[0][1];
                    q11 = inImage.getMPixel(col,     row)     * weight[1][1];
                    q21 = inImage.getMPixel(col + 1, row)     * weight[2][1];

                    q02 = inImage.getMPixel(col - 1, row + 1) * weight[0][2];
                    q12 = inImage.getMPixel(col,     row + 1) * weight[1][2];
                    q22 = inImage.getMPixel(col + 1, row + 1) * weight[2][2];

                    // TODO: I believe the following line has a few errors.
                    // It adds q10 twice (probably meant to use q10 once and q01 once)
                    // It adds q20 twice (probably meant to use q20 once and q02 once)
                    sum = q00 + q10 + q20 + q10 + q11 + q12 + q20 + q21 + q22;
                    sum = MathUtils.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col, row, (byte)(sum + 0.5f));
                    break;

                case 24:
                    inImage.getMPixelRGB(col - 1, row - 1, red, green, blue);
                    q00r = (float)red   * weight[0][0];
                    q00g = (float)green * weight[0][0];
                    q00b = (float)blue  * weight[0][0];

                    inImage.getMPixelRGB(col, row - 1, red, green, blue);
                    q10r = (float)red   * weight[1][0];
                    q10g = (float)green * weight[1][0];
                    q10b = (float)blue  * weight[1][0];

                    q20 = inImage.getMPixel(col + 1, row - 1) * weight[2][0];
                    q01 = inImage.getMPixel(col - 1, row)     * weight[0][1];
                    q11 = inImage.getMPixel(col,     row)     * weight[1][1];
                    q21 = inImage.getMPixel(col + 1, row)     * weight[2][1];

                    q02 = inImage.getMPixel(col - 1, row + 1) * weight[0][2];
                    q12 = inImage.getMPixel(col,     row + 1) * weight[1][2];
                    q22 = inImage.getMPixel(col + 1, row + 1) * weight[2][2];

                    sumr = q00r + q10r + q20r + q10r + q11r + q12r + q20r + q21r + q22r;
                    sumg = q00g + q10g + q20g + q10g + q11g + q12g + q20g + q21g + q22g;
                    sumb = q00b + q10b + q20b + q10b + q11b + q12b + q20b + q21b + q22b;

                    sumr = MathUtils.bound(sumr, 0.0f, 255.0f);
                    sumg = MathUtils.bound(sumg, 0.0f, 255.0f);
                    sumb = MathUtils.bound(sumb, 0.0f, 255.0f);

                    outImage.setMPixelRGB(col, row, 
                        (byte)(sumr + 0.5f), (byte)(sumg + 0.5f), (byte)(sumb + 0.5f));
                    break;
                } // switch
            } // for col
        } // for row

        return 0;
    } // antiAlias


    // This method came from IWARP.CPP
    public static int fWarp1(MemImage inImage, MemImage outImage,
    float rx, float ry, float rz, 
    float sx, float sy, float sz,
    float tx, float ty, float tz, 
    TMatrix viewMatrix,
    float refPointX, float refPointY, float refPointZ) {
        // Project the points to the screen and copy from the input image 
        //
        // The reference point is a point in the texture image's 
        // original(i.e. initial, default) position in Cartesian space.
        // about which the image is rotated and scaled.
        // For example, the reference point 0,0,0 is the center of the 
        // texture image.
        String msgText;
        float x, y;
        /* These variables are not used
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES];
        float[] tXCoords = new float[I_MAXWVERTICES]; 
        float[] tYCoords = new float[I_MAXWVERTICES];
        */

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if (ictdebug) {
            statusPrint("fWarp inputs:");
            msgText = String.format("rx: %6.2f  ry: %6.2f  rz: %6.2f", rx, ry, rz);
            statusPrint(msgText);

            msgText = String.format("sx: %6.2f  sy: %6.2f  sz: %6.2f", sx, sy, sz);
            statusPrint(msgText);

            msgText = String.format("tx: %6.2f  ty: %6.2f  tz: %6.2f", tx, ty, tz);
            statusPrint(msgText);

            msgText = String.format("refx: %6.2f  refy: %6.2f  refz: %6.2f", refPointX, refPointY, refPointZ);
            statusPrint(msgText);
        }

        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        float XRadians = rx * JICTConstants.F_DTR;
        float YRadians = ry * JICTConstants.F_DTR;
        float ZRadians = rz * JICTConstants.F_DTR;
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
        // float intRefPointZ = refPointZ; // this variable is not used
        halfWidth  -= (halfWidth - intRefPointX);
        halfHeight -= (halfHeight - intRefPointY);

        // Calculate offsets that will center the warped image in the output image
        int xOffset = (int)(outWidth / 2.0);
        int yOffset = (int)(outHeight/ 2.0);
        
        // Shortcut: if no rotation or scale, just copy the image
        if(
        rx == 0.0f && ry == 0.0f && rz == 0.0f && 
        sx == 1.0f && sy == 1.0f && sz == 1.0f && 
        tz == 0.0f) {
            inImage.copy(outImage, (int)tx + xOffset, (int)ty + yOffset);
            statusPrint("fWarp: shortcut");
            return 0;
        }
        
        float xIn, yIn, zIn; 
        Integer xOut = 0, yOut = 0;
        byte intensity;

        // Loop through the texture coordinates, projecting to the screen
        zIn = 0.0f;
        float atx = 0.0f, aty = 0.0f, atz = 0.0f;
        float increment = 1.0f;
    
        for (y = 1; y <= inHeight; y += increment) {
            yIn = y - halfHeight;
        
            for(x = 1; x < inWidth; x += increment) {
                intensity = inImage.getMPixel((int)x, (int)y);
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
        // original (i.e. initial, default) position in Cartesian space,
        // about which the image is rotated and scaled.
        // For example, the reference point 0,0,0 is the center of the 
        // texture image.

        String msgText;
        float x, y;
        /* These variables are not used
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES]; 
        float[] tXCoords = new float[I_MAXWVERTICES];
        float[] tYCoords = new float[I_MAXWVERTICES];
        */

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if (ictdebug) {
            statusPrint("fWarp inputs:");
            msgText = String.format("rx: %6.2f  ry: %6.2f  rz: %6.2f", rx, ry, rz);
            statusPrint(msgText);

            msgText = String.format("sx: %6.2f  sy: %6.2f  sz: %6.2f", sx, sy, sz);
            statusPrint(msgText);

            msgText = String.format("tx: %6.2f  ty: %6.2f  tz: %6.2f", tx, ty, tz);
            statusPrint(msgText);

            msgText = String.format("refx: %6.2f  refy: %6.2f  refz: %6.2f", refPointX, refPointY, refPointZ);
            statusPrint(msgText);
        }

        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        TMatrix viewModelMatrix = new TMatrix();
        float XRadians = rx * JICTConstants.F_DTR;
        float YRadians = ry * JICTConstants.F_DTR;
        float ZRadians = rz * JICTConstants.F_DTR;
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

        // int xOffset = (int)(outWidth / 2.0); // this variable is not used
        // int yOffset = (int)(outHeight/ 2.0); // this variable is not used
    
        // Shortcut: if no rotation or scale, just copy the image
        if(
        rx == 0.0f && ry == 0.0f && rz == 0.0f && 
        sx == 1.0f && sy == 1.0f && sz == 1.0f && 
        tz == 0.0f) {
            inImage.copy(outImage, (int)(tx + halfWidth), (int)(ty + halfHeight));
            statusPrint("fWarpz: shortcut");
            return 0;
        }
        
        float xIn, yIn, zIn; 
        Integer xOut1 = 0, yOut1 = 0;
        Integer xOut2 = 0, yOut2 = 0;
        Integer xOut3 = 0, yOut3 = 0;
        Integer xOut4 = 0, yOut4 = 0;
        byte intensity1 = (byte)0, intensity2 = (byte)0, intensity3 = (byte)0, intensity4 = (byte)0;

        // Loop through the texture coordinates, projecting to the screen
        zIn = 0.0f;
        Float atx = 0.0f, aty = 0.0f, atz = 0.0f;
        float increment = 0.5f;                  // oversample 2:1
        float inverseInc = 1.0f / increment;
        float d1 = 0.0f, d2 = 0.0f, d3 = 0.0f, d4 = 0.0f;
        byte red1  = (byte)0;
        byte red2  = (byte)0;
        byte red3  = (byte)0;
        byte red4  = (byte)0;
        byte blue1 = (byte)0;
        byte blue2 = (byte)0;
        byte blue3 = (byte)0;
        byte blue4 = (byte)0;
        
        for (y = inverseInc * increment; y <= inHeight; y += increment) {
            yIn = y - halfHeight;
        
            for(x = inverseInc * increment; x <= inWidth; x += increment) {
                if(bpp == 8) intensity1 = inImage.getMPixel((int)(x - increment), (int)y);
                if(bpp == 8) intensity2 = inImage.getMPixel(              (int)x, (int)y);
                if(bpp == 8) intensity3 = inImage.getMPixel(              (int)x, (int)(y - increment));
                if(bpp == 8) intensity4 = inImage.getMPixel((int)(x - increment), (int)(y - increment));
            
                if(bpp == 24) inImage.getMPixelRGB((int)(x - increment),               (int)y, red1, intensity1, blue1);
                if(bpp == 24) inImage.getMPixelRGB(              (int)x,               (int)y, red2, intensity2, blue2);
                if(bpp == 24) inImage.getMPixelRGB(              (int)x, (int)(y - increment), red3, intensity3, blue3);
                if(bpp == 24) inImage.getMPixelRGB((int)(x - increment), (int)(y - increment), red4, intensity4, blue4);

                xIn = x - halfWidth;
                forwardMatrix.transformAndProjectPoint(xIn - increment, yIn, zIn, 
                    xOut1, yOut1, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d1 = MathUtils.getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                forwardMatrix.transformAndProjectPoint(xIn, yIn, zIn, 
                    xOut2, yOut2, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d2 = MathUtils.getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                forwardMatrix.transformAndProjectPoint(xIn, yIn - increment, zIn, 
                    xOut3, yOut3, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d3 = MathUtils.getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                forwardMatrix.transformAndProjectPoint(xIn-increment, yIn - increment, zIn, 
                    xOut4, yOut4, 
                    refPointX, refPointY, refPointZ, outHeight, outWidth, 
                    atx, aty, atz);
                if(zImage != null) {
                    d4 = MathUtils.getDistance3d(vx, vy, vz, atx, aty, atz);
                }
            
                outImage.fillPolyz(
                    xOut1, yOut1, intensity1, d1, 
                    xOut2, yOut2, intensity2, d2, 
                    xOut3, yOut3, intensity3, d3, 
                    xOut4, yOut4, intensity4, d4, 
                    zImage);
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
        int[] xBuffer, yBuffer; 
        int xBufferIdx, yBufferIdx;
        // float *wxBuffer, *wyBuffer, *wzBuffer; // these variables are not used
        float[] dBuffer;
        // float *wxTemp, *wyTemp, *wzTemp; // these variables are not used
        byte[] iBuffer;
        int iBufferIdx;
        byte iTemp1, iTemp2;
        int iPrev1Idx, iPrev2Idx; // indices into iBuffer
        int xTemp1, yTemp1, xTemp2, yTemp2;
        int xPrev1Idx, yPrev1Idx; // indices into xBuffer and yBuffer, respectively
        int xPrev2Idx, yPrev2Idx; // indices into xBuffer and yBuffer, respectively
        int dBufferIdx;
        float dTemp1, dTemp2;
        int dPrev1Idx, dPrev2Idx; // indices into dBuffer
        
        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        float XRadians = rx * JICTConstants.F_DTR;
        float YRadians = ry * JICTConstants.F_DTR;
        float ZRadians = rz * JICTConstants.F_DTR;
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
        float halfWidth  = inWidth / 2.0f;
        
        float increment = 0.5f;
        float inverseInc = 1.0f / increment;
        int numCalcs = (int)(inWidth * inverseInc);
    
        xBuffer = new int[numCalcs];
        if (xBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for xBuffer");
            return -1;
        }

        yBuffer = new int[numCalcs];
        if (yBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for yBuffer");
            return -1;
        }
    
        dBuffer = new float[numCalcs];
        if (dBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for distance Buffer");
            return -1;
        }
    
        iBuffer = new byte[numCalcs];
        if (iBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for iBuffer");
            return -1;
        }
    
        // Temporary - for testing
        vx = (float)outWidth/2.0f;
        vy = (float)outHeight/2.0f;
        vz = 512.0f;
    
        msgText = String.format("fwarpz2: Viewer location: vx: %f, vy: %f, vz: %f", vx, vy, vz);
        statusPrint(msgText);
        xBufferIdx = 0;
        yBufferIdx = 0;
        iBufferIdx = 0;
        dBufferIdx = 0;

        float row, col;
        float x1, y1, z1;
        Byte i1 = (byte)0, red1 = (byte)0, blue1 = (byte)0;
        byte green1;
        int sx1, sy1;
        float refX, refY, refZ;
    
        for (row = inverseInc * increment; row <= inHeight; row+= increment) {
            for (col = inverseInc * increment; col <= inWidth; col+= increment) {
                x1 = col - halfWidth;
                y1 = row - halfHeight;
                z1 = 0.0f;
                if(bpp == 8) {
                    i1 = inputImage.getMPixel((int)col, (int)row);
                }
                if(bpp == 24) {
                    i1 = (int)inputImage.getMPixelRGB((int)col, (int)row, red1, i1, blue1);
                }
    
                // Project to the screen
                viewModelMatrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                    refX, refY, refZ, outHeight, outWidth, tx, ty, tz);
                if(row == 1.0f) {
                    xBuffer[xBufferIdx] = sx1;
                    xBufferIdx++;

                    yBuffer[yBufferIdx] = sy1;
                    yBufferIdx++;

                    iBuffer[iBufferIdx] = i1;
                    iBufferIdx++;

                    dBuffer[dBufferIdx] = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                    dBufferIdx++;
                }
            
                if ((row > 1.0f) && (col == 1.0f)) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1Idx = 0;
                    yPrev1Idx = 0;

                    xPrev2Idx = 0;
                    yPrev2Idx = 0;
                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx = 0;
                    iPrev2Idx = 0;
                    iPrev2Idx++;
        
                    dPrev1Idx = 0;
                    dPrev2Idx = 0;
                    dPrev2Idx++;

                    if(zBuffer != null) {
                        dTemp1 = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                    }
                }
    
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;
                    if(zBuffer != null) {
                        dTemp2 = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                    }
         
                    // Render the quadrangle intensities
                    //                     
                    // Render the quadrangle distances and update the intermediate zBuffer
                    outputImage.fillPolyz( 
                        xBuffer[xPrev1Idx], yBuffer[yPrev1Idx], iBuffer[iPrev1Idx], dBuffer[dPrev1Idx],
                        xBuffer[xPrev2Idx], yBuffer[yPrev2Idx], iBuffer[iPrev2Idx], dBuffer[dPrev2Idx],
                        xTemp2,             yTemp2,             iTemp2,             dTemp2,
                        xTemp1,             yTemp1,             iTemp1,             dTemp1, 
                        zBuffer);
        
                    xBuffer[xPrev1Idx] = xTemp1;
                    yBuffer[yPrev1Idx] = yTemp1;
                    iBuffer[iPrev1Idx] = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1Idx++;
                    yPrev1Idx++;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx++;
                    iPrev2Idx++;
        
                    dBuffer[dPrev1Idx] = dTemp1;
                    dTemp1 = dTemp2;

                    dPrev1Idx++;
                    dPrev2Idx++;
                }
            }
        }
    
        return 0;
    } // fwarpz2


    // This method came from QMESHMODEL.CPP
    // Called from:
    //     QuadMeshDlg.onOK
    public static int createQMeshModel(String inputImagePath, String destinationDir, int modelType) {  
        float Pi = 3.1415926f;
        int row, col; 
        float x, y, z, sizeFactor, theDimension, yValue;
 
        // float randMax = (float)RAND_MAX; //This variable is no longer used
        float xMagnitude = 2.4f;
        float yMagnitude = 1.5f;
        float zMagnitude = 5.7f;
 
        float smallerSide, cubeFaceSize;
        int v1, v2, v3, v4;
 
        Integer imHeight = 0, imWidth = 0, bitsPerPixel = 0;
        int aStatus;
        aStatus = readBMPHeader(inputImagePath, imHeight, imWidth, bitsPerPixel);
        if(aStatus != 0) {
            statusPrint("createQMeshModel: Unable to open texture image");
            return -1;
        }

        MemImage inputImage = new MemImage(inputImagePath, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
        MemImage xImage = new MemImage(imHeight, imWidth, 32);
        MemImage yImage = new MemImage(imHeight, imWidth, 32);
        MemImage zImage = new MemImage(imHeight, imWidth, 32);
         
        float radius, startTheta, stopTheta, angularInc, angleTemp, asAngle, asAngleInc;
        float xCent, yCent, distance;
        String msgText;
        
        sizeFactor = (float)imWidth;
 
        switch(modelType) {
        case JICTConstants.I_CYLINDER:
            radius = (float)imWidth/(2.0f * Pi);
            startTheta =   0.0f;
            stopTheta  = 360.0f;
            angularInc = 360.0f / (float)imWidth;
            angleTemp  =   0.0f;
            for (row = 1; row <= imHeight; row++) {
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    x = radius * (float)Math.cos(angleTemp * JICTConstants.F_DTR);
                    y = radius * (float)Math.sin(angleTemp * JICTConstants.F_DTR);
                    angleTemp += angularInc;
                    xImage.setMPixel32(col, row, x);
                    yImage.setMPixel32(col, row, (float)row);
                    zImage.setMPixel32(col, row, y);
                } // for col
            } // for row
            break;
 
        case JICTConstants.I_PLANAR:
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
 
        case JICTConstants.I_SPHERE:
            startTheta = 0.0f;
            stopTheta = 360.0f;
            asAngle = 0.0f;
            theDimension = (float)imHeight;  // Set the horz and vert dims equal to the max of the rectangle sides
            if (imWidth > imHeight) {
                theDimension = (float)imWidth;
            }

            asAngleInc = 180.0f / (float)imHeight; // The height traces out a hemispherical arc
            angularInc = 360.0f / (float)imWidth;
            angleTemp  =   0.0f;

            for (row = 1; row <= imHeight; row++) {
                radius = (float)Math.sin(asAngle * JICTConstants.F_DTR) * sizeFactor;
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    x = (radius * (float)Math.cos(angleTemp * JICTConstants.F_DTR) ) + sizeFactor; //put the left most edge in the positive quadrant
                    z = (radius * (float)Math.sin(angleTemp * JICTConstants.F_DTR) ) + sizeFactor;
                    xImage.setMPixel32(col, row, x);
                    zImage.setMPixel32(col, row, z);

                    yValue = (float)Math.acos(asAngle * JICTConstants.F_DTR) * sizeFactor;
                    yImage.setMPixel32(col, row, yValue);

                    angleTemp += angularInc;
                } // for col
                asAngle += asAngleInc;
            } // for row
            break;
 
        case JICTConstants.I_SINE1D:
            angularInc = 1.0f;
            radius = 100.0f;
            for (row = 1; row <= imHeight; row++) {
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    z = radius * (float)Math.sin(angleTemp * JICTConstants.F_DTR);
                    xImage.setMPixel32(col, row, (float)col);
                    yImage.setMPixel32(col, row, z);
                    zImage.setMPixel32(col, row, (float)row);
                    angleTemp += angularInc;
                } // for col
            } // for row
            break;
 
        case JICTConstants.I_SINE2D:
            //  Circumference = 2*Pi*radius
            radius = (float)(imWidth/(2.0f * Pi)/3.0f);   // Make r small enough for three sinusoidal rotations
            angularInc = 360.0f / (float)imWidth;
            xCent = (float)imWidth/2.0f;
            yCent = (float)imHeight/2.0f;

            for (row = 1; row <= imHeight; row++) {
                angleTemp = 0.0f;

                for (col = 1; col <= imWidth; col++) {
                    xImage.setMPixel32(col, row, (float)col);
                    zImage.setMPixel32(col, row, (float)row);
                    distance = (float)Math.sqrt(((col - xCent) * (col - xCent)) + ((row - yCent) * (row - yCent)));           
                    yImage.setMPixel32(col, row, radius * (float)Math.sin(5.0f * distance  * JICTConstants.F_DTR));
                } // for col
            } // for row
            break;
 
        case JICTConstants.I_WHITENOISE:
            // By default the Random class is seeded with current time
            // the first time it is used.
            // So the numbers will be different every time we run.
            Random random = new Random();
 
            for (row = 1; row <= imHeight; row++) {
                for (col = 1; col <= imWidth; col++) {
                    xImage.setMPixel32(col, row, (float)col + (random.nextFloat() * xMagnitude));
                    yImage.setMPixel32(col, row, (float)row + (random.nextFloat() * yMagnitude));
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
        int theLength = file.length();
  
        _splitpath(destinationDir, ddrive, ddir, dfile, dext);
        _makepath(outPath, ddrive, ddir, file, ext);

        // As a matter of convenience copy the texture image to the same directory
        // in which the surface images reside, if it isn't there already.
        if(!FileUtils.fileExists(outPath)) {
            msgText = "Copying QMesh Model Texture Image to: " + outPath;
            statusPrint(msgText);
            CopyFile(inputImagePath, outPath, 1);
        }
 
        FileUtils.constructPathName(xPath, outPath, 'x');
        FileUtils.constructPathName(yPath, outPath, 'y');
        FileUtils.constructPathName(zPath, outPath, 'z');

        // Insure that a generated path is not the same as the texture path
        if(
        xPath.equalsIgnoreCase(inputImagePath) ||
        yPath.equalsIgnoreCase(inputImagePath) ||
        zPath.equalsIgnoreCase(inputImagePath)) {
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
        statusPrint("Calculating approximate mesh centroid and bounding box");
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
    // Called from:
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
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
            // Do nothing cause everything's OK
        } else {
            statusPrint("getMeshCentroid: Surface images must have equal size.");
            return -1;
        }
    
        // Each image must have 32 bit pixels.
        if(
        xImage.getBitsPerPixel() == 32 && 
        yImage.getBitsPerPixel() == 32 &&
        zImage.getBitsPerPixel() == 32) {
            // Do nothing cause everything's OK
        } else {
            statusPrint("getMeshCentroid: Surface images must have 32 bit pixels.");
            return -2;
        }
    
        int imHeight = xImage.getHeight();
        int imWidth  = xImage.getWidth();
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

        // Set the output parameters
        centroidX = x1/totalCells;
        centroidY = y1/totalCells;
        centroidZ = z1/totalCells;

        msgText = "Mesh centroid calculated: " + centroidX + " " + centroidY + " " + centroidZ;
        statusPrint(msgText);

        return 0;
    } // getMeshCentroid
  

    // This method came from QMESHMODEL.CPP
    // Called from:
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    public static int translateMesh(MemImage xImage, MemImage yImage, MemImage zImage,
    float offsetX, float offsetY, float offsetZ) {
        statusPrint("Translating mesh.");

        // Each image must be the same size.
        if(
        xImage.getHeight() == yImage.getHeight() && 
        yImage.getHeight() == zImage.getHeight() &&
        xImage.getWidth() == yImage.getWidth() && 
        yImage.getWidth() == zImage.getWidth()) {
            // Do nothing cause everything's OK
        } else {
            statusPrint("translateMesh: Surface images must have equal size.");
            return -1;
        }
    
        // Each image must have 32 bit pixels.
        if(
        xImage.getBitsPerPixel() == 32 && 
        yImage.getBitsPerPixel() == 32 &&
        zImage.getBitsPerPixel() == 32) {
            // Do nothing cause everything's OK
        } else {
            statusPrint("translateMesh: Surface images must have 32 bit pixels.");
            return -2;
        }
    
        int imHeight = xImage.getHeight();
        int imWidth  = xImage.getWidth();
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
    // Called from:
    //     SceneList.render
    public static int makeRGBimage(String redImage, String greenImage, String blueImage, String outFileName) {
        String msgBuffer;

        // Combine separate color channels into one RGB BMP
        int rHeight, rWidth, gHeight, gWidth, bHeight, bWidth;

        MemImage theRed = new MemImage(redImage, 0, 0, JICTConstants.I_SEQUENTIAL, 'R', JICTConstants.I_REDCOLOR);
        if (!theRed.isValid()) {
            msgBuffer = "makeRGBIMage: Unable to open Red image: " + redImage;
            statusPrint(msgBuffer);
            return 1;
        }

        MemImage theGreen = new MemImage(greenImage, 0, 0, JICTConstants.I_SEQUENTIAL,'R', JICTConstants.I_GREENCOLOR);
        if (!theGreen.isValid()) {
            msgBuffer = "makeRGBIMage: Unable to open Green image: " + greenImage;
            statusPrint(msgBuffer);
            return 1;
        }

        MemImage theBlue = new MemImage(blueImage, 0, 0, JICTConstants.I_SEQUENTIAL,'R', JICTConstants.I_BLUECOLOR);
        if (!theBlue.isValid()) {
            msgBuffer = "makeRGBIMage: Unable to open Blue image: %s" + blueImage;
            statusPrint(msgBuffer);
            return 1;
        }

        rHeight = theRed.getHeight();
        rWidth  = theRed.getWidth();

        gHeight = theGreen.getHeight();
        gWidth  = theGreen.getWidth();

        bHeight = theBlue.getHeight();
        bWidth  = theBlue.getWidth();

        if (!(rWidth == gWidth && gWidth == bWidth && rWidth == bWidth)) {
            statusPrint("makeRGBIMage: R,G, and B image widths are not equal.");
            return 1;
        }
        if (!(rHeight == gHeight && gHeight == bHeight && rHeight == bHeight)) {
            statusPrint("makeRGBIMage: R,G, and B image heights are not equal.");
            return 1;
        }

        MemImage theRGB = new MemImage(outFileName, gHeight, gWidth, JICTConstants.I_SEQUENTIAL, 'W', JICTConstants.I_RGBCOLOR);
        if (!theRGB.isValid()) {
            statusPrint("makeRGBIMage: Unable to open RGB image.");

            theRed.close();
            theGreen.close();
            theBlue.close();
            return 1;
        }

        byte[] redPixel, greenPixel, bluePixel, rgbPixel;
        int rStatus, gStatus, bStatus;

        for (int y = 1; y <= gHeight; y++) {
            rStatus = theRed.readNextRow();
            if (rStatus != 0) {
                statusPrint("makeRGBImage: red readNextRow error.");

                theRed.close();
                theGreen.close();
                theBlue.close();
                return 1;
            }

            gStatus = theGreen.readNextRow();
            if (gStatus != 0) {
                statusPrint("makeRGBImage: green readNextRow error.");

                theRed.close();
                theGreen.close();
                theBlue.close();
                return 1;
            }

            bStatus = theBlue.readNextRow();
            if (bStatus != 0) {
                statusPrint("makeRGBImage: blue readNextRow error.");

                theRed.close();
                theGreen.close();
                theBlue.close();
                return 1;
            }

            redPixel   = theRed.getBytes();
            int redPixelIdx = 0;
            greenPixel = theGreen.getBytes();
            int greenPixelIdx = 0;
            bluePixel  = theBlue.getBytes();
            int bluePixelIdx = 0;
            rgbPixel   = theRGB.getBytes();
            int rgbPixelIdx = 0;

            for (int x = 1; x <= gWidth; x++) {
                rgbPixel[rgbPixelIdx] = bluePixel[bluePixelIdx];
                rgbPixelIdx++;

                rgbPixel[rgbPixelIdx] = greenPixel[greenPixelIdx];
                rgbPixelIdx++;

                rgbPixel[rgbPixelIdx] = redPixel[redPixelIdx];
                rgbPixelIdx++;

                redPixelIdx++;
                greenPixelIdx++;
                bluePixelIdx++;
            } // for x

            // Write the output
            theRGB.writeNextRow();
        } // for y

        // Close the files and destroy the objects
        theRed.close();
        theGreen.close();
        theBlue.close();
        theRGB.close();

        FileUtils.deleteFile(redImage);     // to conserve disk space, remove the
        FileUtils.deleteFile(greenImage);   // input files
        FileUtils.deleteFile(blueImage);
        return 0;
    } // makeRGBimage


    // This method came from TWEEN.CPP
    // Called from:
    //     tweenImage
    public static int getRowIntervals(MemImage anImage, int row, 
    int[] intervalList, Integer numIntervals) {
        int imWidth = anImage.getWidth();
        int bpp = anImage.getBitsPerPixel();
        int col; 
        int intervalStatus = 0;
        int counter = 0;
        byte aValue, red, green, blue;
        
        for(col = 1; col <= imWidth; col++) {
            // Set aValue
            switch(bpp) {
            case 8:
                aValue = anImage.getMPixel(col, row);
                break;
                
            case 24:
                anImage.getMPixelRGB(col, row, red, green, blue);
                if(
                red   != JICTConstants.I_CHROMARED || 
                green != JICTConstants.I_CHROMAGREEN || 
                blue  != JICTConstants.I_CHROMABLUE) {
                    aValue = 255;
                } else {
                    aValue = JICTConstants.I_CHROMAVALUE;
                }
                break;
            } // switch

            if(intervalStatus == 0 && aValue != JICTConstants.I_CHROMAVALUE) {  // interval start
                intervalList[counter] = col;
                counter++;
                intervalStatus = 1;
            }

            if(intervalStatus == 1 && aValue == JICTConstants.I_CHROMAVALUE) {  // interval stop
                intervalList[counter] = col;
                counter++;
                intervalStatus = 0;
            }
        } // for col

        if(intervalStatus == 1) {                            // catch end of line
            intervalList[counter] = imWidth;
            counter++;
        }

        numIntervals = counter / 2;
        return 0;
    } // getRowIntervals


    // This method came from TWEEN.CPP
    // Called from:
    //     tweenImage
    public static int getTotalIntervalLength(int[] intervalList, int numIntervals) {
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
    // Called from:
    //     tweenImage
    public static int indexToCoord(int index, int[] intervalList, int numIntervals) {
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


    // This method smooth shades a triangle given 3 vertices (x1, y1), (x2, y2)
    // and (x3, y3) on the triangle, and an intensity at each vertex (i1, i2, and i3).
    // See p 173 of Visual Special Effects Toolkit in C++.
    // This method came from SHADERS.CPP
    // Called from:
    //     MemImage.fillPolyz
    public static int fillTrianglez(int x1, int y1, float i1, float d1,
    int x2, int y2, float i2, float d2,
    int x3, int y3, float i3, float d3,
    MemImage outImage, MemImage zImage) {
        // zImage contains the distance values.
        // To use this function without a z-buffer, call with zImage equal to NULL.

        // Pixels are written to outImage only if the new distance (derived 
        // from d1, d2, d3) is less than the corresponding distance in the zImage.
        //
        // The assumption is made here that sets of points describing 
        // vertical or horizontal lines have been handled elsewhere.

        int midPoint = 0;
        int minX = 0, minY = 0;
        int maxX = 0, maxY = 0;
        int midX = 0, midY = 0;
        int denominator;
        float minI = 0.0f, maxI = 0.0f, midI = 0.0f;
        float minD = 0.0f, maxD = 0.0f, midD = 0.0f;
        float intensity, intensityStep, distance, distanceStep;
        float id1 = 0.0f, id2 = 0.0f, oldZ;

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
        int ix1 = 0, ix2 = 0, ip1 = 0, ip2 = 0, nSteps;

        triangleType = JICTConstants.I_POINTONSIDE;
        if(midY == maxY) triangleType = JICTConstants.I_POINTONTOP;
        if(minY == midY) triangleType = JICTConstants.I_POINTONBOTTOM;

        // Now we have a rotationally independant situation.  Interpolate rows from 
        // minY to midY then from midY to maxY
        if (midY == -1) {
            midY = maxY;
            return -1;
        }

        if(triangleType == JICTConstants.I_POINTONSIDE) {
            for(row = minY; row <= midY; row++) {
                // Interpolate the x interval and the intensities at the interval boundary
                ix1 = (int)MathUtils.interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                ix2 = (int)MathUtils.interpolate((float)minX, (float)midX, (float)minY, (float)midY, (float)row);
                ip1 = (int)MathUtils.interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                ip2 = (int)MathUtils.interpolate(       minI,        midI, (float)minY, (float)midY, (float)row);
                if(zImage != null) {
                    id1 = MathUtils.interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                    id2 = MathUtils.interpolate(minD, midD, (float)minY, (float)midY, (float)row);
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
                    if(zImage != null) {  
                        // Render with a Z Buffer
                        oldZ = zImage.getMPixel32(col, row);

                        if(distance <= oldZ) {
                            if(distance <= 1.0f) {
                                distance = 1.0f;
                            }
                            intensity = MathUtils.bound(intensity, 0.0f, 255.0f);
                            zImage.setMPixel32(col, row, distance);

                            if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                            if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                                (byte)intensity, (byte)intensity);
                        }
                    } else {
                        // Render without a Z Buffer
                        intensity = MathUtils.bound(intensity, 1.0f, 255.0f);
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
                ix1 = (int)MathUtils.interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                ix2 = (int)MathUtils.interpolate((float)midX, (float)maxX, (float)midY, (float)maxY, (float)row);
                ip1 = (int)MathUtils.interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                ip2 = (int)MathUtils.interpolate(       midI,        maxI, (float)midY, (float)maxY, (float)row);
                if(zImage != null) {
                    id1 = MathUtils.interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                    id2 = MathUtils.interpolate(midD, maxD, (float)midY, (float)maxY, (float)row);
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
                            intensity = MathUtils.bound(intensity, 0.0f, 255.0f);
                            zImage.setMPixel32(col, row, distance);
                            if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                            if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                                (byte)intensity, (byte)intensity);
                        }
                    } else {
                        intensity = MathUtils.bound(intensity, 0.0f, 255.0f);
                        if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                        if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }
                    intensity += intensityStep;
                    distance  += distanceStep;
                } // for col
            } // for row
        } else {
            // Handle pointontop, pointonbottom cases
            for(row = minY; row <= maxY; row++) {
                // Interpolate the x interval and the intensities at the interval boundary
                if(triangleType == JICTConstants.I_POINTONTOP) {
                    ix1 = (int)MathUtils.interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                    ix2 = (int)MathUtils.interpolate((float)minX, (float)midX, (float)minY, (float)midY, (float)row);
                    ip1 = (int)MathUtils.interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                    ip2 = (int)MathUtils.interpolate(       minI,        midI, (float)minY, (float)midY, (float)row);
                    if(zImage != null) {
                        id1 = MathUtils.interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                        id2 = MathUtils.interpolate(minD, midD, (float)minY, (float)midY, (float)row);
                    }
                }
                if(triangleType == JICTConstants.I_POINTONBOTTOM) {
                    ix1 = (int)MathUtils.interpolate((float)minX, (float)maxX, (float)minY, (float)maxY, (float)row);
                    ix2 = (int)MathUtils.interpolate((float)midX, (float)maxX, (float)midY, (float)maxY, (float)row);
                    ip1 = (int)MathUtils.interpolate(       minI,        maxI, (float)minY, (float)maxY, (float)row);
                    ip2 = (int)MathUtils.interpolate(       midI,        maxI, (float)midY, (float)maxY, (float)row);
                    if(zImage != null) {
                        id1 = MathUtils.interpolate(minD, maxD, (float)minY, (float)maxY, (float)row);
                        id2 = MathUtils.interpolate(midD, maxD, (float)midY, (float)maxY, (float)row);
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
                            intensity = MathUtils.bound(intensity, 0.0f, 255.0f);
                            zImage.setMPixel32(col, row, distance);
                            if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                            if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                                (byte)intensity, (byte)intensity);
                        }
                    } else {
                        intensity = MathUtils.bound(intensity, 0.0f, 255.0f);
                        if(bpp == 8)  outImage.setMPixel(col, row, (byte)intensity);
                        if(bpp == 24) outImage.setMPixelRGB(col, row, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }

                    intensity += intensityStep;
                    distance  += distanceStep;
                } // for col
            } // for row
        }

        return 0;
    } // fillTrianglez


    // This method came from SHADERS.CPP
    // Called from:
    //     RenderObject.renderShapez
    public static byte getLight(Point3d p1, Point3d p2, Point3d c1, Point3d c2) {
        // Input points are oriented counterclockwise from the first point
        Point3d centroid = new Point3d();
        Point3d np1 = new Point3d(); // lighting normal
        // Point3d *np2, *nc1, *nc2;   // these local variables are not used
        float ip1; // intensity at face cornerpoints
        // float ip2, ic1, ic2;		// these local variables are not used  
        Point3d lightSource = new Point3d();
        
        if(ictdebug) {
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
        
        float dCentroid = MathUtils.getDistance3d(lightSource.x, lightSource.y, lightSource.z, 
            centroid.x, centroid.y, centroid.z);
        
        Vect.getNormal2(np1, p1, centroid, p2);
        
        Vect.vectorNormalize(np1);

        // kd     the coefficient of reflection or reflectivity of the surface material
        //        highly reflective = 1, highly absorptive = 0
        // Ip	  the intensity of the light source
        // Ia     the ambient intensity at the surface
        // N      The surface Normal (unit vector)
        // L      The direction of the light source (unit vector)
        // d      the distance between the surface and the light source
        float kd = 0.65f;
        int Ip = 150;
        
        ip1 = lightModel(kd, Ip, 150, np1, lightSource, dCentroid);
        ip1 = MathUtils.bound(ip1, 1.0f, 255.0f);

        return (byte)ip1;
    } // getLight


    // This method came from VECTOR.CPP
    // Called from:
    //     RenderObject.renderMeshz
    public static float lightModel(float kd, int Ip, int Ia, Point3d N, Point3d L, float d) {
        // kd     the coefficient of reflection or reflectivity of the surface material
        //        highly reflective = 1, highly absorptive = 0
        // Ia     the ambient intensity at the surface
        // Ip	  the intensity of the light source
        // d      the distance between the surface and the light source
        // d0     a constant to keep the denominator from reaching 0  for points close to the light source
        // N      The surface Normal (unit vector)
        // L      The direction of the light source (unit vector)
        // I      The intensity produced by this light model
        float orientationLight = Vect.dotProduct(N,L);
        float d0 = 0.5f;

        // Equation 14.4 p 279 Hearn - Baker
        float I = (kd * Ia) + ((kd * Ip) / (d + d0) * orientationLight);
        return I;
    } // lightModel


    // This method came from RENDER.CPP
    // Class RenderObject also has a renderMesh method, but that one takes 
    // 3 parameters, 2 MemImages and a blendIndicator.
    // Called from: 
    //     MorphDlg
    public static int renderMesh(String outputImagePath, MemImage textureImage, 
    MemImage xImage, MemImage yImage, MemImage zImage, 
    TMatrix aMatrix) {
        // This version of renderMesh renders a mesh without the need for the
        // renderObject that provides the context information from the graphic
        // pipeline.
        int[] xBuffer, yBuffer;
        int xBufferIdx, yBufferIdx; // previously named xTemp, yTemp;
        // float *wxBuffer, *wyBuffer, *wzBuffer; // these variables are not used
        float[] dBuffer;
        // float *wxTemp, *wyTemp, *wzTemp; // these variables are not used
        byte[] iBuffer;
        int iBufferIdx; // previously byte *iTemp;
        byte iTemp1 = 0, iTemp2;
        int iPrev1Idx = 0, iPrev2Idx = 0; // indices into iBuffer
        int xTemp1 = 0, yTemp1 = 0, xTemp2 = 0, yTemp2 = 0;
        int xPrev1Idx = 0, yPrev1Idx = 0; // indices into xBuffer and yBuffer, respectively
        int xPrev2Idx = 0, yPrev2Idx = 0; // indices into xBuffer and yBuffer, respectively
        int dBufferIdx; // previously float *dTemp;
        float dTemp1 = 0, dTemp2;
        int dPrev1Idx = 0, dPrev2Idx = 0;
        float vx, vy, vz;

        if (
        !xImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            statusPrint("renderMesh: One or more quad-mesh image is not valid");
            return -1;
        }

        // Create the line buffer data structures
        xBuffer = new int[xImage.getWidth()];
        /* Dead code, per the compiler
        if (xBuffer == null) {
            statusPrint("renderMesh: Not enough memory for xBuffer");
            return -1;
        }
        */

        yBuffer = new int[yImage.getWidth()];
        /* Dead code, per the compiler
        if (yBuffer == null) {
            statusPrint("renderMesh: Not enough memory for yBuffer");
            return -1;
        }
        */

        dBuffer = new float[zImage.getWidth()];
        /* Dead code, per the compiler
        if (dBuffer == null) {
            statusPrint("renderMesh: Not enough memory for Z Buffer");
            return -1;
        }
        */

        iBuffer = new byte[textureImage.getWidth()];
        /* Dead code, per the compiler
        if (iBuffer == null) {
            statusPrint("renderMeshz: Not enough memory for intensity Buffer");
            return -1;
        }
        */

        MemImage outputImage = new MemImage(textureImage.getHeight(), textureImage.getWidth());
        if(!outputImage.isValid()) {
            statusPrint("renderMeshZ: Not enough memory to open output image");
            return -1;
        }
        outputImage.setFileName("outputImage");

        MemImage midZImage = new MemImage(outputImage.getHeight(), outputImage.getWidth(), 32);
        if(!midZImage.isValid()) {
            statusPrint("renderMeshZ: Not enough memory to open intermediate Z image");
            return -1;
        }

        midZImage.setFileName("midZImage");
        midZImage.init32(JICTConstants.F_ZBUFFERMAXVALUE);
        int imHeight  = textureImage.getHeight();
        int imWidth   = textureImage.getWidth();
        int outHeight = outputImage.getHeight();
        int outWidth  = outputImage.getWidth();

        //  Temporary - for testing
        vx = (float)outWidth/2.0f;
        vy = (float)outHeight/2.0f;
        vz = 512.0f;

        String msgText = "renderMeshz: Viewer location: vx: " + vx + ", vy: " + vy + ", vz: " + vz;
        statusPrint(msgText);
        xBufferIdx = 0; // index into xBuffer
        yBufferIdx = 0; // index into yBuffer
        iBufferIdx = 0; // index into iBuffer
        dBufferIdx = 0; // index into dBuffer

        byte i1;
        int row, col;
        Integer sx1 = 0, sy1 = 0;
        float x1, y1, z1;
        Float tx = 0.0f, ty = 0.0f, tz = 0.0f;
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

                // Project to the screen
                // The following method sets sx1, sy1, tx, ty, and tz
                aMatrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                  refX, refY, refZ, outHeight, outWidth, tx, ty, tz);
 
                if(row == 1) {
                    xBuffer[xBufferIdx] = sx1;
                    xBufferIdx++;

                    yBuffer[yBufferIdx] = sy1;
                    yBufferIdx++;

                    iBuffer[iBufferIdx] = i1;
                    iBufferIdx++;

                    dBuffer[dBufferIdx] = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                    dBufferIdx++;
                }
              
                if ((row > 1) && (col == 1)) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1Idx = 0;
                    yPrev1Idx = 0;

                    xPrev2Idx = 0;
                    yPrev2Idx = 0;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx = 0;
                    iPrev2Idx = 0;
                    iPrev2Idx++;

                    dPrev1Idx = 0;
                    dPrev2Idx = 0;
                    dPrev2Idx++;

                    dTemp1 = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                }
      
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;
                    dTemp2 = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);

                    outputImage.fillPolyz( 
                        xBuffer[xPrev1Idx], yBuffer[yPrev1Idx], iBuffer[iPrev1Idx], dBuffer[dPrev1Idx],
                        xBuffer[xPrev2Idx], yBuffer[yPrev2Idx], iBuffer[iPrev2Idx], dBuffer[dPrev2Idx],
                        xTemp2, yTemp2, iTemp2, dTemp2,
                        xTemp1, yTemp1, iTemp1, dTemp1, 
                        midZImage);

                    xBuffer[xPrev1Idx] = xTemp1;
                    yBuffer[yPrev1Idx] = yTemp1;
                    iBuffer[iPrev1Idx] = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1Idx++;
                    yPrev1Idx++;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx++;
                    iPrev2Idx++;

                    dBuffer[dPrev1Idx] = dTemp1;
                    dTemp1 = dTemp2;
                    
                    dPrev1Idx++;
                    dPrev2Idx++;
                }
            } // for col
        } // for row

        outputImage.writeBMP(outputImagePath);

        return 0;
    } // renderMesh


    // This method came from MEMIMG32.CPP
    // Sets height, width and bitsPerPixel parameters
    // Called from:
    //     createQMeshModel
    //     motionBlur
    //     MainFrame.onToolsWarpImage
    //     MemImage constructor that takes 6 parameters
    //     Shape3d.shapeFromBMP
    public static int readBMPHeader(String psFileName, Integer height, Integer width, Integer bitsPerPixel) {
        // TODO: Rewrite this.
        return 0;
        /*
        BITMAPFILEHEADER bmFH;
        BITMAPINFOHEADER pbmIH;
        BITMAPINFO pbmInfo;
        int PalSize = 256;
        HANDLE fp;
        int imageSize;
        String errText;

        fp = null;
        // With the OPEN_EXISTING parameter, we open the existing file psFileName
        fp = CreateFile(psFileName, GENERIC_READ, FILE_SHARE_READ, 0, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0);
        if(fp == null) {
            errText = "readBMPHeader: Can't open " + psFileName;
            statusPrint(errText);
            return 1;
        }

        int numBytesRead;
        ReadFile(fp, bmFH, sizeof(BITMAPFILEHEADER), numBytesRead, null);

        if( bmFH.bfType != 0x4D42 ) {   // if type isn't "BM" ...
            errText = "readBMPHeader: Cannot open: " + psFileName;
            statusPrint(errText);
            CloseHandle(fp);
            return 2;
        }

        pbmIH = GlobalLock(GlobalAlloc(GMEM_FIXED, sizeof(BITMAPINFOHEADER)));

        ReadFile(fp, pbmIH, sizeof(BITMAPINFOHEADER), numBytesRead, null);

        // Set the output parameter bitsPerPixel
        bitsPerPixel = (int)pbmIH.biBitCount;
        if(pbmIH.biCompression != BI_RGB) {
            errText = "Compressed image. Not supported: " + psFileName;
            statusPrint(errText);
            CloseHandle(fp);
            return 3;
        }
        pbmInfo = GlobalLock(GlobalAlloc( GHND, PalSize + sizeof(BITMAPINFOHEADER) ));

        pbmInfo.bmiHeader = *pbmIH;

        // Set the output parameters width and height
        width = (int) pbmInfo.bmiHeader.biWidth;
        height = (int) pbmInfo.bmiHeader.biHeight;
        imageSize = pbmInfo.bmiHeader.biSizeImage;
        CloseHandle(fp);

        return 0;
        */
    } // readBMPHeader


    // This method came from TWEEN.CPP
    // Morph two rotoscoped images
    public static int tweenImage(float aFraction, 
    MemImage inImageA, MemImage inImageB, 
    String imagePath, String shapePath) {
        String msgText;
        Shape3d inShapeA, inShapeB;
        inShapeA = new Shape3d(8192);
        inShapeB = new Shape3d(8192);

        int mStatus;
        int aStatus = shapeFromImage(inImageA, inShapeA);
        if(aStatus != 0) {
            statusPrint("tweenImage: shapeFromImage returned non-zero status. inImageA");
            return -1;
        }
        
        aStatus = shapeFromImage(inImageB, inShapeB);
        if(aStatus != 0) {
            statusPrint("tweenImage: shapeFromImage returned non-zero status. inImageB");
            return -2;
        }

        int numvertsA = inShapeA.getNumVertices();
        int numvertsB = inShapeB.getNumVertices();

        Shape3d outShapeA, outShapeB;
        Shape3d tempShapeA, tempShapeB;

        outShapeA = null;
        outShapeB = null;
        tempShapeA = null;
        tempShapeB = null;
        int bpp = inImageA.getBitsPerPixel();

        // Equalize the number of vertices in each shape
        // The following method sets either outShapeA or outShapeB
        aStatus = createTweenableShapes(inShapeA, inShapeB, 
            outShapeA, outShapeB);
        if(aStatus != 0) {
            statusPrint("tweenImage: Could not create tweenable shapes.");
            return -3;
        }

        if(outShapeA != null) {
            tempShapeA = outShapeA;
            tempShapeB = inShapeB;
        } else {
            tempShapeA = inShapeA;
            tempShapeB = outShapeB;
        }

        numvertsA = tempShapeA.getNumVertices();
        numvertsB = tempShapeB.getNumVertices();
        if(numvertsA != numvertsB) {
            statusPrint("tweenImage: tweenShape could not create the target boundary.");
            return -3;
        }

        Shape3d outShape = null;

        // outShape is the morphed boundary
        // The following method sets outShape
        aStatus = tweenShape(aFraction, outShape, tempShapeA, tempShapeB);
        // Why isn't aStatus inspected to see if an error occurred?

        // Round down to integer coords and
        // remove duplicates
        outShape.initCurrentVertex();
        int numVertices = outShape.getNumVertices();
        for (int i = 1; i <= numVertices; i++){
            int temp = (int)(outShape.mCurrentVertex.x + 0.5f);
            outShape.mCurrentVertex.x = (float)temp;

            temp = (int)(outShape.mCurrentVertex.y + 0.5f);
            outShape.mCurrentVertex.y = (float)temp;

            temp = (int)(outShape.mCurrentVertex.z + 0.5f);
            outShape.mCurrentVertex.z = (float)temp;

            // outShape.currentVertex++;
            outShape.incCurrentVertex();
        }

        outShape.removeDuplicates();
    
        inShapeA = null;
        inShapeB = null;
        if(outShapeA != null) outShapeA = null;
        if(outShapeB != null) outShapeB = null;
    
        outShape.worldBoundingBox();  // BB (bounding box) of tweened boundary

        float oMinX = outShape.mfMinX;
        float oMinY = outShape.mfMinY;
        float oMaxX = outShape.mfMaxX;
        float oMaxY = outShape.mfMaxY;
        int   numXO = (int)(oMaxX - oMinX) + 1;
        int   numYO = (int)(oMaxY - oMinY) + 1;

        // Generate a mask image from the tweened boundary.
        outShape.invertY(numYO);
        outShape.translateW(-1.0f, 0.0f, 0.0f);
        MemImage maskImage = new MemImage(numYO, numXO);
        aStatus = RenderObject.maskFromShape(outShape, maskImage);
        if(aStatus != 0) {
            statusPrint("tweenImage: Could not generate tweened mask image.");
            maskImage = null;
            outShape = null;
            return -3;
        }

        int numXA = inImageA.getWidth();
        int numYA = inImageA.getHeight();
        int numXB = inImageB.getWidth();
        int numYB = inImageB.getHeight();
        if(
        numXA == 0 || numYA == 0 ||
        numXB == 0 || numYB == 0) {
            statusPrint("tweenImage: One of the images to be tweened has no rows or columns.");
            maskImage = null;
            outShape = null;
            return -4;
        }
      
        int numOutX = Math.max(numXA, numXB);
        int numOutY = Math.max(numYA, numYB);
        float xScaleAtoO = (float)numXO / (float)numXA;
        float yScaleAtoO = (float)numYO / (float)numYA;
        float xScaleBtoO = (float)numXO / (float)numXB;
        float yScaleBtoO = (float)numYO / (float)numYB;
    
        TMatrix aViewMatrix = new TMatrix(); // initialized to the identity matrix
        MemImage tempImageA = new MemImage(numYO, numXO, bpp); 
        int warpStatus = iwarpz(inImageA, tempImageA, null,
            0.0f, 0.0f, 0.0f, 
            xScaleAtoO, yScaleAtoO, 1.0f,
            0.0f, 0.0f, 0.0f, 
            0.0f, 0.0f, 0.0f,
            aViewMatrix,
            0.0f, 0.0f, 0.0f);
        // why is warpStatus not inspected for an error code?

        MemImage tempImageB = new MemImage(numYO, numXO, bpp); 
        warpStatus = iwarpz(inImageB, tempImageB, null,
            0.0f, 0.0f, 0.f, 
            xScaleBtoO, yScaleBtoO, 1.0f,
            0.0f, 0.0f, 0.0f, 
            0.0f, 0.0f, 0.0f,
            aViewMatrix,
            0.0f, 0.0f, 0.0f);
        // why is warpStatus not inspected for an error code?

        // blend the image intensities to produce the morphed image
        final int I_NUMALLOCATEDCOORDS = 128;

        int row, col;
        int aIntervalList[] = new int[I_NUMALLOCATEDCOORDS];
        int bIntervalList[] = new int[I_NUMALLOCATEDCOORDS];
        int mIntervalList[] = new int[I_NUMALLOCATEDCOORDS];
        Integer numAIntervals = 0;
        Integer numMIntervals = 0;
        Integer numBIntervals = 0;
        int totalALength, totalMLength;
        // int aCoord, bCoord, j; // these variables are not used
        Byte red = 0, green = 0, blue = 0;
        float aRatio;
    
        // Copy the world coords to the screen portion of the shape object
        // for use by getIntervals
        MemImage preBlendA = new MemImage(numYO, numXO, bpp);

        // int numOutIntervals; // this variable is not used
        int inIndex, inCoord, outCoord;
        // int minCoord, maxCoord; // these variables are not used
        aStatus = 0;
        mStatus = 0;

        // Morph image A
        for(row = 1; row < numYO; row++) {
            aStatus = getRowIntervals(tempImageA, row, aIntervalList, numAIntervals);
            if(aStatus != 0){
                msgText = String.format( 
                    "tweenImage: getRowIntervals error (image A) at row: %d", row);
                statusPrint(msgText);
                return -9;
            }

            totalALength = getTotalIntervalLength(aIntervalList, numAIntervals);

            // Get mask image intervals
            mStatus = getRowIntervals(maskImage, row, mIntervalList, numMIntervals);
            if(mStatus != 0) {
                msgText = String.format(
                    "tweenImage: getRowIntervals error (mask image) at row: %d", row);
                statusPrint(msgText);
                return -10;
            }

            totalMLength = getTotalIntervalLength(mIntervalList, numMIntervals);

            if(numMIntervals > 0 && numAIntervals > 0) {
                aRatio = (float)totalALength / (float)totalMLength;  // corresponding index in AList
                for(col = 1; col < totalMLength; col++) {
                    // inverse mapping: get the source location for each morphed pixel location
                    inIndex  = (int)(aRatio * (float)col + 0.5f);
                    inCoord  = indexToCoord(inIndex, aIntervalList, numAIntervals);
                    outCoord = indexToCoord(col, mIntervalList, numMIntervals);

                    switch(bpp) {
                    case 8:
                        green = tempImageA.getMPixel(inCoord, row);
                        preBlendA.setMPixel(outCoord, row,  green);
                        break;
                    case 24:
                        tempImageA.getMPixelRGB(inCoord, row, red, green, blue);
                        preBlendA.setMPixelRGB(outCoord, row, red, green, blue);
                      break;
                    } // switch
                } // for col
            }
        } // for row
        // end morph image A

        // Morph image B
        aStatus = 0;
        int totalBLength;
        MemImage preBlendB = new MemImage(numYO, numXO, bpp);
        for(row = 1; row < numYO; row++) {
            aStatus = getRowIntervals(tempImageB, row, bIntervalList, numBIntervals);
            if(aStatus != 0) {
                msgText = String.format("tweenImage: getRowIntervals error (image B) at row: %d", row);
                statusPrint(msgText);
                return -9;
            }

            totalBLength = getTotalIntervalLength(bIntervalList, numBIntervals);

            // Get mask image intervals
            mStatus = getRowIntervals(maskImage, row, mIntervalList, numMIntervals);
            if(mStatus != 0) {
                msgText = String.format("tweenImage: getRowIntervals error (mask image) at row: %d", row);
                statusPrint(msgText);
                return -10;
            }

            totalMLength = getTotalIntervalLength(mIntervalList, numMIntervals);

            if(numMIntervals > 0 && numBIntervals > 0) {
                aRatio = (float)totalBLength / (float)totalMLength;  // corresponding index in AList
                for(col = 1; col < totalMLength; col++) {
                    // inverse mapping:get the source location for each morphed pixel location
                    inIndex  = (int)(aRatio * (float)col + 0.5f);
                    inCoord  = indexToCoord(inIndex, bIntervalList, numBIntervals);
                    outCoord = indexToCoord(col, mIntervalList, numMIntervals);

                    switch(bpp) {
                    case 8:
                        green = tempImageB.getMPixel(inCoord, row);
                        preBlendB.setMPixel(outCoord, row,  green);
                        break;

                    case 24:
                        tempImageB.getMPixelRGB(inCoord, row, red, green, blue);
                        preBlendB.setMPixelRGB(outCoord, row, red, green, blue);
                        break;
                    } // switch
                } // for col
            } else {

            }
        } // for row
        // end morph image B

        tempImageA = null;
        tempImageB = null;

        // Blend the morphants together
        MemImage morphImage = new MemImage(numYO, numXO, bpp);
        MemImage matteImage = new MemImage(numYO, numXO, 8);
        preBlendA.createAlphaImage(matteImage);
        matteImage.alphaSmooth3();

        blendz(preBlendA, matteImage, null, null, morphImage, 1.0f - aFraction);
        blendz(preBlendB, matteImage, null, null, morphImage, aFraction);

        preBlendA = null;
        preBlendB = null;
        maskImage = null;  //the image corresponding to the tweened boundary.
        matteImage = null;

        // int xBeg, xEnd, yBeg, yEnd; // these variables are not used

        // center the morphed image in an output image of constant dimensions.
        MemImage outImage = new MemImage(numOutY, numOutX, bpp);
      
        int xMiddle, yMiddle, xTranslation, yTranslation;

        if(aFraction == 0.0f) {
            xMiddle = (int)(numXA / 2.0f);
            yMiddle = (int)(numYA / 2.0f);
            xTranslation = (int)((numOutX / 2.0f) - xMiddle);
            yTranslation = (int)((numOutY / 2.0f) - yMiddle);
            inImageA.copy(outImage, xTranslation, yTranslation);
        } else if (1.0f - aFraction < 0.005f) {
            xMiddle = (int)(numXB / 2.0f);
            yMiddle = (int)(numYB / 2.0f);
            xTranslation = (int)((numOutX / 2.0f) - xMiddle);
            yTranslation = (int)((numOutY / 2.0f) - yMiddle);
            inImageB.copy(outImage, xTranslation, yTranslation);
        } else {
            xMiddle = (int)(numXO / 2.0f);
            yMiddle = (int)(numYO / 2.0f);
            xTranslation = (int)((numOutX / 2.0f) - xMiddle);
            yTranslation = (int)((numOutY / 2.0f) - yMiddle);
            morphImage.copy(outImage, xTranslation, yTranslation);
        }

        outImage.writeBMP(imagePath);
        morphImage = null;
        outImage = null;
        return 0;
    } // tweenImage


    // This method sets parameter pOutShape
    // This method came from TWEEN.CPP
    // Called from:
    //     tweenImage
    public static int tweenShape(float fraction, Shape3d pOutShape, 
    Shape3d shape1, Shape3d shape2) {
        // tween shape1 into shape2. 
        // shape1 and shape2 must have the same number of vertices
        int numverts1 = shape1.getNumVertices();
        int numverts2 = shape2.getNumVertices();
        if(numverts1 != numverts2) {
            statusPrint("tweenShape: shape1 and shape2 must have the same number of vertices");
            return -1;
        }

        Shape3d outputShape;
        outputShape = new Shape3d(numverts1);
        int j;
        float x1, y1, x2, y2, outX, outY;

        shape1.initCurrentVertex();
        shape2.initCurrentVertex();
        outputShape.initCurrentVertex();

        for (j = 1; j <= numverts1; j++) {
            x1 = (float)shape1.mCurrentVertex.x;
            y1 = (float)shape1.mCurrentVertex.y;

            x2 = (float)shape2.mCurrentVertex.x;
            y2 = (float)shape2.mCurrentVertex.y;

            outX = (fraction * x2) + (1.0f - fraction) * x1;
            outY = (fraction * y2) + (1.0f - fraction) * y1;

            outputShape.addWorldVertex(outX + 0.5f, outY + 0.5f, 0.0f);

            //shape1.currentVertex++;
            //shape2.currentVertex++;
            shape1.incCurrentVertex();
            shape2.incCurrentVertex();
        } // for j

        // Set the output parameter
        pOutShape = outputShape;
        return 0;
    } // tweenShape


    // This method came from TWEEN.CPP
    // Called from:
    //     tweenImage
    public static int createTweenableShapes(Shape3d inShape1, Shape3d inShape2, 
    Shape3d outShapeA, Shape3d outShapeB) {
        int numVertices1 = inShape1.getNumVertices();
        int numVertices2 = inShape2.getNumVertices();
        
        int numVerticesDiff = Math.abs(numVertices1 - numVertices2);
        int aStatus;
        Shape3d outShape1, outShape2;

        // Add vertices to the shape having the fewer vertices
        if(numVertices1 < numVertices2) {
            outShape1 = inShape1.copyAndExpand(numVerticesDiff);
            for(int i = 1; i <= numVerticesDiff; i++) {
                aStatus = outShape1.divideLongestArc();
                if(aStatus != 0) {
                    statusPrint("createTweenableShapes: divideLongestArc error");
                    return aStatus;
                }

                // Set output parameter outShapeA
                outShapeA = outShape1;
            } // for i
        } else {
            outShape2 = inShape2.copyAndExpand(numVerticesDiff);
            for(int i = 1; i <= numVerticesDiff; i++) {
                aStatus = outShape2.divideLongestArc();
                if(aStatus != 0) {
                    statusPrint("createTweenableShapes: divideLongestArc error");
                    return aStatus;
                }

                // Set output parameter outShapeB
                outShapeB = outShape2;
            } // for i
        }

        return 0;
    } // createTweenableShapes


    // This method came from TWEEN.CPP
    public static int tweenMesh(float aFraction, 
    MemImage aTexture, MemImage aX, MemImage aY, MemImage aZ,
    MemImage bTexture, MemImage bX, MemImage bY, MemImage bZ, 
    MemImage oTexture, MemImage oX, MemImage oY, MemImage oZ) {
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
        if ((bpp != 8) && (bpp != 24)) {
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
                    aByte = (byte)aTexture.getMPixelRGB(col, row, aRedByte, aGreenByte, aBlueByte);
                    bByte = (byte)bTexture.getMPixelRGB(col, row, bRedByte, bGreenByte, bBlueByte);
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

    public static void setLblStatus(JLabel stat) {
        lblStatus = stat;
    } // setLblStatus
} // class Globals