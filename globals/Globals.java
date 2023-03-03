package globals;

import apps.IctApp;

import core.MemImage;
import core.RenderObject;
import core.SceneElement;
import core.Shape3d;

import fileUtils.FileUtils;

import globals.JICTConstants;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Random;
import java.util.prefs.Preferences;

import javax.swing.JLabel;

import math.MathUtils;
import math.TMatrix;
import math.Vect;

import structs.Point3d;

public class Globals {
    private static JLabel lblStatus = null;

    private static boolean bIctDebug = false;

    // This variable came from ICT20.CPP
    private static Preferences prefs = Preferences.userNodeForPackage(IctApp.class);  // declare a global preference object

    // This variable came from MAINFRM.CPP
    public static GPipe aGraphicPipe = new GPipe();  // a globally defined graphic pipeline for VRML viewing


    // This method came from UTILS.CPP
    public static void statusPrint(String psMessage) {
        boolean bErrWriting = false;
        File logFile;
        String sLogFileName;
        FileOutputStream logFileOutputStream;

        // If the mainframe window is not open, post the message to the log file.
        // else display the message on the status bar and post it to the log file.
        sLogFileName = prefs.get("LogPath", "logs/");
        sLogFileName = sLogFileName + "JICT.log";
        logFile = new File(sLogFileName);

        // Open the log file
        try {
            logFileOutputStream = new FileOutputStream(logFile, true);
        } catch (FileNotFoundException fnfe) {
            System.out.println("Could not find file " + sLogFileName);
            lblStatus.setText("statusPrint: Unable to open the JICT log file jict.log");
            return;
        }
        DataOutputStream logFileDataOutputStream = new DataOutputStream(logFileOutputStream);

        // We were able to open the log file, so write the msg to it
        try {
            logFileDataOutputStream.writeChars(psMessage);
        } catch (IOException ioe) {
            bErrWriting = true;
            String sErrMsg = "statusPrint:IOException while trying to write to file " + sLogFileName;
            System.out.println(sErrMsg);
            lblStatus.setText(sErrMsg);
        }

        try {
            logFileDataOutputStream.close();
        } catch(IOException ioe) {
            String sErrMsg = "statusPrint: IOException while trying to close file " + sLogFileName;
            System.out.println(sErrMsg);
            lblStatus.setText(sErrMsg);
            if (bErrWriting) {
                return;
            }
        }

        // Display the message immediately on the status bar
        lblStatus.setText(psMessage);
    } // statusPrint


    // This method came from DEPTHSRT.CPP
    // Called from:
    //     SceneList.depthSort
    // which in turn is called from SceneList.render, 
    // which in turn is called from either
    // MainFrame.onRenderScene or MainFrame.onRenderSequence
    public static void insertionSort2(float pafItems[], SceneElement paItemDataSEs[], int piNumItems) {
        float itemTemp, theValue;
        SceneElement itemDataTemp;
        int index, indexTmp;

        // Sort theItems array into decending order for depth sorting
        for(index = 0; index < piNumItems; index++) {
            itemTemp     = pafItems[index];
            itemDataTemp = paItemDataSEs[index];
            theValue     = pafItems[index];

            for(indexTmp = index; indexTmp > 0; indexTmp--) {
                if(pafItems[indexTmp - 1] < theValue) {
                    pafItems[indexTmp] = pafItems[indexTmp - 1];
                    paItemDataSEs[indexTmp] = paItemDataSEs[indexTmp - 1];
                } else {
                    break;
                }
            } // for indexTmp

            // Insert the original item in the temporary position.
            pafItems[indexTmp] = itemTemp;
            paItemDataSEs[indexTmp] = itemDataTemp;
        } // for index
    } // insertionSort2


    // This method came from BLEND.CPP
    // Called from:
    //     iRender
    // However, I could not find where iRender is being called from (or if it is being called).
    public static int blend(MemImage pInMImage, MemImage pMaskMImage, MemImage pOutMImage, 
    float pfAlphaScale) {
        // Blend over the common area in input and mask images
        int iInputRows  = pInMImage.getHeight();
        int iInputCols  = pInMImage.getWidth();

        int iMaskRows   = pMaskMImage.getHeight();
        int iMaskCols   = pMaskMImage.getWidth();

        int iCommonRows = Math.min(iInputRows, iMaskRows);
        int iCommonCols = Math.min(iInputCols, iMaskCols);

        // Each MemImage is assumed to be opened for random access
        int ix, iy;
        byte maskPixel, inPixel, outPixel, addedPixel;
        float fInWeight, fOutWeight;

        for(iy = 1; iy <= iCommonRows; iy++) {
            for(ix = 1; ix <= iCommonCols; ix++) {
                maskPixel = pMaskMImage.getMPixel(ix, iy);
                inPixel   = pInMImage.getMPixel(ix, iy);
                if(maskPixel > 0 && inPixel > 0) {
                    outPixel = pOutMImage.getMPixel(ix, iy);
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

                    pOutMImage.setMPixel(ix, iy, addedPixel);
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
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    //     RenderObject.renderMeshz
    // and renderMeshz in turn is called from SceneList.render,
    // which in turn is called from MainFrame.onRenderScene and MainFrame.onRenderSequence
    public static int blendz(MemImage pInMImage, MemImage pMatteMImage, 
    MemImage pZMImage, MemImage pZBufMImage,
    MemImage pOutMImage,
    float pfAlphaScale) {
        // pZImage is the rendered model's zBuffer image
        // pZBuffer is the effect frame's zBuffer image
        // Both of these need to be considered in a zBuffer render operation since each zBuffered
        // model contributes to the rendered effect frame's zBuffer
        //
        // Blend over the common area in input and matte images
        int iInputRows  = pInMImage.getHeight();
        int iInputCols  = pInMImage.getWidth();

        int iMatteRows  = pMatteMImage.getHeight();
        int iMatteCols  = pMatteMImage.getWidth();

        int iCommonRows = Math.min(iInputRows, iMatteRows);
        int iCommonCols = Math.min(iInputCols, iMatteCols);

        int iBpp = pInMImage.getBitsPerPixel();
        int iOutBPP = pOutMImage.getBitsPerPixel();
        if(iOutBPP != iBpp) {
            String sMsgText = "blendz: inImage bpp: " + iBpp + " must match outImage bpp: " + iOutBPP;
            statusPrint(sMsgText);
            return -1;
        }

        int iMatteBPP = pMatteMImage.getBitsPerPixel();
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
        if((pZMImage != null) && (pZBufMImage != null)) {
            usingZBuffer = true;
        }
    
        for(iy = 1; iy <= iCommonRows; iy++) {
            for(ix = 1; ix <= iCommonCols; ix++) {
                mattePixel = pMatteMImage.getMPixel(ix, iy);
                switch(iBpp) {  // Optionally blend in color or monochrome
                case 8:
                    inPixel = pInMImage.getMPixel(ix, iy);
                    if((mattePixel > JICTConstants.I_CHROMAVALUE) && (inPixel > JICTConstants.I_CHROMAVALUE)) {
                        outPixel = pOutMImage.getMPixel(ix, iy );
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
                            if(pZMImage.getMPixel32(ix, iy) < pZBufMImage.getMPixel32(ix, iy)) { 
                                pZBufMImage.setMPixel32(ix, iy, pZMImage.getMPixel32(ix, iy));
                                pOutMImage.setMPixel(ix, iy, addedPixel);
                            }
                        } else {
                            pOutMImage.setMPixel(ix, iy, addedPixel);
                        }
                    } // end if non-zero values
                    break;
        
                case 24:                           // RGB Blend with Z-Buffer
                    pInMImage.getMPixelRGB(ix, iy, inRed, inGreen, inBlue);
                    if((mattePixel > JICTConstants.I_CHROMAVALUE) && (inGreen > JICTConstants.I_CHROMAVALUE)) {
                        outPixel = (byte)pOutMImage.getMPixelRGB(ix, iy, outRed, outGreen, outBlue);
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
                            if(pZMImage.getMPixel32(ix, iy) < pZBufMImage.getMPixel32(ix, iy)) { 
                                pZBufMImage.setMPixel32(ix, iy, pZMImage.getMPixel32(ix, iy));
                                pOutMImage.setMPixelRGB(ix, iy, addedRed, addedGreen, addedBlue);
                            }
                        } else {
                            pOutMImage.setMPixelRGB(ix, iy, addedRed, addedGreen, addedBlue);
                        }
                    } // end if non zero values
                    break;
                } // switch
            } // for ix
        } // for iy

        return 0;
    } // blendz
  

    // This method originally came from BLEND.CPP
    // 
    // Called from:
    //     RenderObject.prepareCutout
    public static int createCutout(MemImage pOrigMImage, MemImage pMaskMImage,
    String psCutoutName, Shape3d pShape) {
        String sMsgText;

        // Create the cutout image and translate the shape to coincide with the cutout.
        // Assumes the mask image is an unpacked (8 bit) mask image opened RANDOM,
        // The original must be opened for sequential access.
        // cutoutName: name of cutout image and shape file without the suffix
        if(pOrigMImage.getAccessMode() != JICTConstants.I_SEQUENTIAL) {
            statusPrint("createCutout: original image access mode must be SEQUENTIAL");
            return 1;
        }

        if (pOrigMImage.getColorSpec() == JICTConstants.I_ONEBITMONOCHROME) {
            statusPrint("createCutout: original image colorSpec cannot be ONEBITMONOCHROME");
            return 2;
        }

        // A cutout version of both the mask and original
        // image is created in which the zero pixel border is removed.
        String sCutoutRImage = "", sCutoutGImage = "", sCutoutBImage = "", sCutoutMImage = "";
        String sCutoutRGBImage = "";

        // Prepare pathnames for mask and cutout images
        String sCutoutDir, sMaskDir;
        sCutoutDir = prefs.get("InputDir", "CUTOUT/");
        sMaskDir   = prefs.get("MaskDir", "MASK/");
        String sCutoutPath, sMaskPath;
    
        FileUtils.appendFileName(sCutoutRImage,   psCutoutName, "r");
        FileUtils.appendFileName(sCutoutGImage,   psCutoutName, "g");
        FileUtils.appendFileName(sCutoutBImage,   psCutoutName, "b");
        FileUtils.appendFileName(sCutoutMImage,   psCutoutName, "a");
        FileUtils.appendFileName(sCutoutRGBImage, psCutoutName, "c");
        sCutoutPath = sCutoutDir + sCutoutRGBImage;
        sMaskPath   = sMaskDir + sCutoutMImage;
    
        int iMaskHeight, iMaskWidth;
        iMaskHeight = pMaskMImage.getHeight();
        iMaskWidth  = pMaskMImage.getWidth();
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
        sShapeDir  = prefs.get("ShapeDir", "SHAPE/");
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
        boolean bColor = false;
        if(pOrigMImage.getColorSpec() == JICTConstants.I_RGBCOLOR) {
            bColor = true;
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
        if(bColor) {
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
            pOrigMImage.readNextRow();
        }

        int iyCounter = 1;
        for (iy = iMaskHeight - iNewMaxY; iy <= iMaskHeight - iNewMinY; iy++) {
            pOrigMImage.readNextRow();
            int ixCounter = 0;

            for(int ix = iNewMinX; ix <= iNewMaxX; ix++) {
                ixCounter++;
                byte theMaskValue = pMaskMImage.getMPixel(ix, iy);

                if(theMaskValue > 0) {
                    cutoutMImg.setMPixel(ixCounter, iyCounter, theMaskValue);
                    if(!bColor) {
                        cutoutGImg.setMPixel(ixCounter, iyCounter, pOrigMImage.getMPixel(ix, 1));
                    }
            
                    if(bColor) {
                        cutoutRImg.setMPixel(ixCounter, iyCounter, pOrigMImage.getMPixel(ix, 1, 'R'));
                        cutoutGImg.setMPixel(ixCounter, iyCounter, pOrigMImage.getMPixel(ix, 1, 'G'));
                        cutoutBImg.setMPixel(ixCounter, iyCounter, pOrigMImage.getMPixel(ix, 1, 'B'));
                    }
                } else {
                    cutoutMImg.setMPixel(ixCounter, iyCounter, (byte)0);
                    cutoutGImg.setMPixel(ixCounter, iyCounter, (byte)0);
                    if(bColor) {
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
    
        if(bColor) {
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

    // These are the direction values:
    // 0	right
    // 1	right and up
    // 2	up
    // 3	left and up
    // 4	left
    // 5	left and down
    // 6	down
    // 7	right and down		

    // This method originally came from BLEND.CPP
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
    //     shapeFromImage
    public static boolean in_boundary(MemImage pMImage, int piX, int piY) {
        int iImHeight = pMImage.getHeight();
        int iImWidth  = pMImage.getWidth();
        int iBpp      = pMImage.getBitsPerPixel();
        Byte bytRed = 0, bytGreen = 0, bytBlue = 0;

        if (
        (piX < 1) || (piX > iImWidth) || 
        (piY < 1) || (piY > iImHeight)) {
            return false;
        }

        switch(iBpp) {
        case 8:
            if (pMImage.getMPixel(piX, piY) != JICTConstants.I_CHROMAVALUE) {
                return true;
            } else {
                return false;
            }

        case 24:
            // The following method sets parameters red, green, and blue
            pMImage.getMPixelRGB(piX, piY, bytRed, bytGreen, bytBlue);
            if (
            (bytRed   != JICTConstants.I_CHROMARED) || 
            (bytGreen != JICTConstants.I_CHROMAGREEN) ||
            (bytBlue  != JICTConstants.I_CHROMABLUE)) {
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
    //
    // Called from:
    //     neighbor
    //     shapeFromImage
    public static boolean probe(MemImage pMImage, int piX, int piY, int piDir, 
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
        return (in_boundary(pMImage, piX, piY));
    } // probe


    // This method originally came from BLEND.CPP
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
    // Called from:
    //     shapeFromImage
    public static int neighbor(MemImage pMImage, 
    int piX, int piY, // these two are only used as parameters to method probe
    int piLastDir, // last direction
    Integer pINewX, Integer pINewY) {
        int	i;
        int	iNewDir; // new direction

        // Figure out where to start looking for a neighbor --
        // always look ahead and to the left of the last direction

        // if the last vector was 0
        // then start looking at  1

        // if the last vector was 1
        // then start looking at  3

        // if the last vector was 2
        // then start looking at  3

        // if the last vector was 3
        // then start looking at  5

        // if the last vector was 4
        // then start looking at  5

        // if the last vector was 5
        // then start looking at  7

        // if the last vector was 6
        // then start looking at  7

        // if the last vector was 7
        // then start looking at  1

        if ((piLastDir & 0x01) != 0) {
            // last dir is odd -- add 2 to it
            iNewDir = piLastDir + 2;
        } else {
            // last dir is even -- add 1 to it
            iNewDir = piLastDir + 1;
        }

        // Keep iNewDir in the range 0 through 7
        if (iNewDir > 7) {
            iNewDir -= 8;
        }

        // Probe the neighbors, looking for one on the edge
        for (i = 0; i < 8; i++) {
            // The following method sets parameters pINewX and PINewY
            if (probe(pMImage, piX, piY, iNewDir, pINewX, pINewY)) {
                // Found the next clockwise edge neighbor --
                // its coordinates have already been
                // stuffed into piNewX, piNewY

                return(iNewDir);
            } else {
                // Check the next clockwise neighbor
                if (--iNewDir < 0) {
                    iNewDir += 8;
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
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    public static int shapeFromImage(MemImage pMImage, Shape3d pShape) {
        int	iX, iY;
        Integer	iNewX = 0, iNewY = 0;
        int	iDir, iLastDir;
        int iStartX = 1;
        int iStartY = 1;
        int iRow, iCol;
        // int counter = 0; // this variable is not used

        int iImHeight = pMImage.getHeight();
        int iImWidth  = pMImage.getWidth();
        int iBpp      = pMImage.getBitsPerPixel();
        if((iBpp != 8) && (iBpp != 24)) {
            statusPrint("shapeFromImage: Binary image must have 8 or 24 bit pixels.");
            return -1;
        }

        // Find the first point on the boundary
        iStartX = -1;
        iStartY = -1;

        // Start at the left-top corner of the image, scanning from left to right
        for (iRow = iImHeight; iRow >= 1; iRow--) {
            for (iCol = 1; iCol <= iImWidth; iCol++) {
                if(in_boundary(pMImage, iCol, iRow)) {
                    iStartX = iCol;
                    iStartY = iRow;
                    // goto nextStep;
                    break;
                }
            }
        }

        if((iStartX < 0) || (iStartY < 0)) {
            statusPrint("shapeFromImage: Binary image has no non-zero pixels");
            return -1;
        }

        // Go left in the starting row until out of the boundary
        while (in_boundary(pMImage, iStartX, iStartY)) {
            --iStartX;
        }
        
        // Move back right one point, to the leftmost edge
        // in the boundary, in that row
        iStartX++;

        // Check if the starting point has no neighbors in the boundary --
        // the starting direction to check is arbitrary	*/
        iX = iStartX;
        iY = iStartY;

        iDir = 0;

        for ( ; ; ) {
            if (probe(pMImage, iX, iY, iDir, iNewX, iNewY)) {
                // Found a neighbor in that direction (its coordinates are in iNewX, iNewY
                // but we don't use them here)

                break;
            }

            // Try next direction
            if (++iDir == 8) {
                // Starting point has no neighbors -- make the chain one vector long
                
                // Fill in the vector -- the direction is arbitrary,
                // since the point is isolated
                pShape.addWorldVertex((float)iNewX, (float)iNewY, 0.0f);

                return 0;
            }
        }

        // Get ready to follow the edge -- since we are at the left edge,
        // force initial probe to be to upper left by initializing last_dir to 1
        iLastDir = 1;

        // Follow the edge clockwise
        for ( ; ; ) {
            // Get the next point on the edge and the vector to it
            iDir = neighbor(pMImage, iX, iY, iLastDir, iNewX, iNewY);

            // Add the new point
            if(iDir != iLastDir) {
                pShape.addWorldVertex((float)iNewX, (float)iNewY, 0.0f);
            }

            // Maybe done with boundary
            if ( (iNewX == iStartX) && (iNewY == iStartY) ) {
                return 0;
            }

            // Else get ready to continue following the edge
            iX = iNewX;
            iY = iNewY;
            iLastDir = iDir;
        } // for
    } // shapeFromImage


    // This method came from MOTION.CPP
    // TODO: Replace parameter filein with a FileStream
    // Called from:
    //     MotionPath.readMotion
    public static String getNextMotionLine(String psText, Integer pILineNumber, 
    LineNumberReader filein) {
        boolean bComment = true;
        // int theLength = 80; // This variable is no longer used
        String sKeyWord;
      
        while (bComment) {
            // filein.getline(psText, theLength);  // Ignore comments and near empty lines
            try {
                psText = filein.readLine();
            } catch (IOException ioe) {
                Globals.statusPrint("getNextMotionLine: IOException while reading from file");
            }

            if(psText == null) {
                psText = "EOF";
                sKeyWord = psText;
                return(sKeyWord);
            }

            pILineNumber++;
            if (psText.startsWith("//") || psText.length() < 2) // Single C/R
                bComment = true;
            else
                bComment = false;
        }

        sKeyWord = psText;
        return(sKeyWord);
    } // getNextMotionLine


    // This method originally came from MOTION.CPP
    // Called from:
    //     MotionBlur.onOK
    public static int motionBlur(String psFirstImagePath, String psOutputDir, 
    int piNumFrames, int piBlurDepth) {
        String sMsgText;
        MemImage[] aMImages = new MemImage[32]; 
        MemImage outMImage;
        String sDirectory = "", sFileName = "", sPrefix = "", sInSuffix = "";
        String sCurrentPath = "";
        // String inPath; // This variable is not used
        String sOutPath = "", sOutSuffix;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
        int iBlur, iNumOpenImages, iBucket, iRedBucket, iGreenBucket, iBlueBucket;
        int iFrameNum = 0, i, j, iStatus;
        Integer iImHeight = 0, iImWidth = 0, iBpp = 0;
        int iFrameCounter, iRow, iCol;

        if(piBlurDepth > 15) {
            statusPrint("motionBlur: blurDepth cannot be > 15");
            return -1;
        }

        // The directory includes the drive letter
        iStatus = FileUtils.getPathPieces(psFirstImagePath, sDirectory, sFileName, sPrefix, 
            iFrameNum, sInSuffix);
        if(iStatus != 0) {
            statusPrint("motionBlur: Check the first image pathname");
            return -2;
        }

        // The following method reads the bmp file psFirstImagePath and sets 
        // parameters imHeight, imWidth, and iBpp
        // We assume iBpp will have the value 8 or 24
        iStatus = readBMPHeader(psFirstImagePath, iImHeight, iImWidth, iBpp);
        if(iStatus != 0) {
            sMsgText = "motionBlur: Cannot open: " + psFirstImagePath;
            statusPrint(sMsgText);
            return -3;
        }

        iNumOpenImages = 2 * piBlurDepth + 1;

        for (iFrameCounter = iFrameNum + piBlurDepth; iFrameCounter <= iFrameNum + piNumFrames - piBlurDepth; iFrameCounter++) {
            // Open and close the appropriate images
            if(iFrameCounter == iFrameNum + piBlurDepth) {
                for(i = -piBlurDepth; i <= piBlurDepth; i++) { // open the first blurDepth images
                    // The following method sets parameter sCurrentPath
                    FileUtils.makePath(sCurrentPath, sDirectory, sPrefix, iFrameCounter + i, sInSuffix);
                    switch(iBpp) {
                    case 8:
                        aMImages[i + piBlurDepth] = new MemImage(sCurrentPath, 0, 0, 
                            JICTConstants.I_RANDOM, 'R', JICTConstants.I_EIGHTBITMONOCHROME);
                        break;

                    case 24:
                        aMImages[i + piBlurDepth] = new MemImage(sCurrentPath, 0, 0, 
                            JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                        break;
                    } // switch

                    if(!aMImages[i + piBlurDepth].isValid()) {
                        sMsgText = "motionBlur: Unable to open image: " + sCurrentPath;
                        statusPrint(sMsgText);
                        return -4;
                    }
                }
            } else {
                for (j = 0; j < iNumOpenImages - 1; j++) { // Move the image pointers
                    aMImages[j] = aMImages[j + 1];
                }

                // Open new image
                // The following method sets parameter sCurrentPath
                FileUtils.makePath(sCurrentPath, sDirectory, sPrefix, iFrameCounter + piBlurDepth, sInSuffix);
                switch(iBpp) {
                case 8:
                    aMImages[iNumOpenImages - 1] = new MemImage(sCurrentPath, 0, 0, 
                        JICTConstants.I_RANDOM, 'R', JICTConstants.I_EIGHTBITMONOCHROME);
                    break;

                case 24:
                    aMImages[iNumOpenImages - 1] = new MemImage(sCurrentPath, 0, 0, 
                        JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                    break;
                } // switch

                if(!aMImages[iNumOpenImages - 1].isValid()) {
                    sMsgText = "motionBlur: Unable to open image 2: " + sCurrentPath;
                    statusPrint(sMsgText);
                    return -4;
                }
            }

            // Blur the images
            float fAvgBucket, fAvgRedBucket, fAvgGreenBucket, fAvgBlueBucket;

            sOutSuffix = "b";
            // The following method sets parameter sOutPath
            FileUtils.makePath(sOutPath, psOutputDir, sPrefix, iFrameCounter, sOutSuffix);
            outMImage = new MemImage(iImHeight, iImWidth, iBpp);

            // We will now read from aMImages[iBlur] and with the avg data we collect, 
            // write to outMImage
            for (iRow = 1; iRow < iImHeight; iRow++) {
                for (iCol = 1; iCol < iImWidth; iCol++) {
                    iBucket = 0;
                    iRedBucket   = 0;
                    iGreenBucket = 0;
                    iBlueBucket  = 0;

                    for (iBlur = 0; iBlur < iNumOpenImages; iBlur++) {
                        switch (iBpp) {
                        case 8:
                            iBucket += aMImages[iBlur].getMPixel(iCol, iRow);
                            break;

                        case 24:
                            aMImages[iBlur].getMPixelRGB(iCol, iRow, bytRed, bytGreen, bytBlue);
                            iRedBucket   += bytRed;
                            iGreenBucket += bytGreen;
                            iBlueBucket  += bytBlue;
                            break;

                        default:
                            statusPrint("motionBlur: image must be 8 or 24 bits per pixel");
                            return -1;
                        }  // switch
                    } // for blur

                    switch(iBpp) {
                    case 8:
                        fAvgBucket = iBucket / iNumOpenImages;
                        outMImage.setMPixel(iCol, iRow, (byte)(fAvgBucket + 0.5));
                        break;

                    case 24:
                        fAvgRedBucket   = iRedBucket   / iNumOpenImages;
                        fAvgGreenBucket = iGreenBucket / iNumOpenImages;
                        fAvgBlueBucket  = iBlueBucket  / iNumOpenImages;
                        outMImage.setMPixelRGB(iCol, iRow, 
                            (byte)(fAvgRedBucket   + 0.5f),
                            (byte)(fAvgGreenBucket + 0.5f),
                            (byte)(fAvgBlueBucket  + 0.5f));
                        break;
                    } // switch
                } // for iCol
            } // for iRow

            // Save the blurred image
            sMsgText = "Saving: " + sOutPath;
            statusPrint(sMsgText);
            outMImage.writeBMP(sOutPath);
        } // for iFrameCounter

        return 0;
    } // motionBlur


    // This method originally came from IWARP.CPP
    //
    // This method performs planar texture mapping.
    // See p 157 - 160 of Visual Special Effects Toolkit in C++.
    //
    // Called from:
    //     tweenImage
    //     MainFrame.onToolsWarpImage
    // Note that tweenImage in turn is called by MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    public static int iwarpz(MemImage pInMImage, MemImage pOutMImage, MemImage pZMImage,
    float pfRx, float pfRy, float pfRz, 
    float pfSx, float pfSy, float pfSz,
    float pfTx, float pfTy, float pfTz, 
    float pfVx, float pfVy, float pfVz,
    TMatrix pViewMatrix,
    float pfRefPointX, float pfRefPointY, float pfRefPointZ) {
        // To use this function without a zBuffer, call with zImage = null.
        // in this case, vx, vy, and vz are ignored
        String sMsgText;
        int iX, iY;
        int iStatus;
        Integer iNumXCoordsFound;
        // ia => integer array
        int[] iaScreenXCoords = new int[JICTConstants.I_MAXWVERTICES];
        // fa => float array
        float[] faZCoords = new float[JICTConstants.I_MAXWVERTICES]; 
        float[] faXCoords = new float[JICTConstants.I_MAXWVERTICES]; 
        float[] faYCoords = new float[JICTConstants.I_MAXWVERTICES];

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if(bIctDebug) {
            statusPrint("iwarpz input arguments");

            sMsgText = String.format("rx: %6.2f  ry: %6.2f  rz: %6.2f", pfRx, pfRy, pfRz);
            statusPrint(sMsgText);

            sMsgText = String.format("sx: %6.2f  sy: %6.2f  sz: %6.2f", pfSx, pfSy, pfSz);
            statusPrint(sMsgText);

            sMsgText = String.format("tx: %6.2f  ty: %6.2f  tz: %6.2f", pfTx, pfTy, pfTz);
            statusPrint(sMsgText);

            sMsgText = String.format("refx: %6.2f  refy: %6.2f  refz: %6.2f", 
                pfRefPointX, pfRefPointY, pfRefPointZ);
            statusPrint(sMsgText);
        }

        // Step 1. Calculated the forward composite transformation matrix
        // from the desired rotation, scale and translation transformations
        // Build the forward and inverse transformation matrices
        TMatrix forwardMatrix = new TMatrix();

        // F_DTR = floating-point (F) degree to radian (DTR) conversion factor
        float fXRadians = pfRx * JICTConstants.F_DTR;
        float fYRadians = pfRy * JICTConstants.F_DTR;
        float fZRadians = pfRz * JICTConstants.F_DTR;

        forwardMatrix.scale(pfSx, pfSy, pfSz);
        forwardMatrix.rotate(fXRadians, fYRadians, fZRadians);
        forwardMatrix.translate(pfTx, pfTy, pfTz);

        TMatrix viewModelMatrix = new TMatrix();
        viewModelMatrix.multiply(pViewMatrix, forwardMatrix);

        // Step 2. Calculate the inverse transformation matrix.
        TMatrix inverseMatrix = new TMatrix(viewModelMatrix);  // copy the forward transform
        inverseMatrix.invertg();                               // and invert it

        if(bIctDebug) {
            forwardMatrix.display("Forward Matrix:");
            inverseMatrix.display("Inverse Matrix:");
        }

        int iBpp       = pInMImage.getBitsPerPixel();
        int iInHeight  = pInMImage.getHeight();
        int iInWidth   = pInMImage.getWidth();
        int iOutHeight = pOutMImage.getHeight();
        int iOutWidth  = pOutMImage.getWidth();
        float fHalfInHeight = iInHeight / 2.0f;
        float fHalfInWidth  = iInWidth / 2.0f;

        float fXCentOffset = (iOutWidth - iInWidth) / 2.0f;
        float fYCentOffset = (iOutHeight - iInHeight) / 2.0f;

        if(bIctDebug) {
            sMsgText = "iWarpz: Viewer location: vx: " + pfVx + ", vy: " + pfVy + ", vz: " + pfVz;
            statusPrint(sMsgText);
        }

        // iwarpz uses a reference point defined in pixel space.
        // Convert it now.
        float fIntRefPointX = pfRefPointX + fHalfInWidth;
        float fIntRefPointY = pfRefPointY + fHalfInHeight;
        float fIntRefPointZ = pfRefPointZ;

        // Step 4. Create a Shape3d object from the screen corner points calculated in Step 3.
        // Load a shape object with the original image boundary coordinates
        Shape3d shape = new Shape3d(4);
        shape.addWorldVertex(      1.0f,         1.0f, 0.0f);
        shape.addWorldVertex((float)iInWidth,        1.0f, 0.0f);
        shape.addWorldVertex((float)iInWidth, (float)iInHeight, 0.0f);
        shape.addWorldVertex(       1.0f, (float)iInHeight, 0.0f);

        // Transform and project the image coords, taking into account the reference point
        viewModelMatrix.transformAndProject(shape, iOutHeight, iOutWidth, 
            true, fIntRefPointX, fIntRefPointY, fIntRefPointZ);

        if(bIctDebug) {
            shape.printShape("Transformed Image Boundary:");
        }

        shape.screenBoundingBox();
        float fMinY = shape.mfMinY;
        float fMaxY = shape.mfMaxY;
        // float fMinX = shape.mfMinX; // this variable is not used
        // float fMaxX = shape.mfMaxX; // this variable is not used
        
        shape.transformBoundingBox();

        if (bIctDebug) {
            // Inverse check. Map transformed shape cornerpoints into original image
            shape.initCurrentVertex();
            Float fXo = 0.0f, fYo = 0.0f, fZo = 0.0f;

            for (int index = 1; index <= shape.getNumVertices(); index++) {
                float fAnX = shape.mCurrentVertex.tx;
                float fAnY = shape.mCurrentVertex.ty;
                float fAnZ = shape.mCurrentVertex.tz;

                // The following method sets fXo, fYo, and fZo
                inverseMatrix.transformPoint (fAnX, fAnY, fAnZ, fXo, fYo, fZo);
                // aShape.iCurrVtxIdx++;
                shape.incCurrentVertex();

                sMsgText = String.format("transformed: %6.2f %6.2f %6.2f texture: %6.2f %6.2f %6.2f",
                    fAnX, fAnY, fAnZ, 
                    fXo + fHalfInWidth, fYo + fHalfInHeight, fZo);
                statusPrint(sMsgText);
            }

            sMsgText = "read offsets: halfInWidth: " + fHalfInWidth + "  halfInHeight: " + fHalfInHeight;
            statusPrint(sMsgText);

            sMsgText = "write offsets: xCentOffset: " + fXCentOffset + "  yCentOffset: " + fYCentOffset;
            statusPrint(sMsgText);
        } // if (bIctDebug)

        float fXIn, fYIn, fZIn;
        float fXOut, fYOut, fZOut;
        // float xOut1, yOut1, zOut1; // these variables are not used
        // float xOut2, yOut2, zOut2; // these variables are not used
        // float xOut3, yOut3, zOut3; // these variables are not used
        // float xOut4, yOut4, zOut4; // these variables are not used
        float fIntensity, fXIncrement, fYIncrement, fZIncrement;
        float fDx, fDy, fDz;
        float fD, fW, theZ, fDist;
        fD = -512.0f;
        byte bytIntensity, bytRed, bytGreen, bytBlue;
        //int xMin, xMax; // these variables are not used
        //int yMin, yMax; // these variables are not used
        //int zMin, zMax; // these variables are not used
        int iNumSteps;

        // Loop through the screen coordinates, filling in with inverse mapped pixels
        for (iY = (int)fMinY; iY <= (int)fMaxY; iY++) {
            iStatus = getIntervals(shape, iY, iNumXCoordsFound, JICTConstants.I_MAXWVERTICES,
                iaScreenXCoords, faXCoords, faYCoords, faZCoords);

            if (iStatus != 0) {
                sMsgText = "iwarp: getInterval error: " + iStatus;
                statusPrint(sMsgText);
                return 2;
            }

            if (bIctDebug) {
                statusPrint("y:\tsx  \ttx  \tty  \ttz");
                for(int i = 0; i < iNumXCoordsFound; i++) {
                    sMsgText = String.format("%d\t%d\t%6.2f\t%6.2f\t%6.2f" , 
                        iY, iaScreenXCoords[i],
                        faXCoords[i], faYCoords[i], faZCoords[i]);
                    statusPrint(sMsgText);
                } // for i
            } // if (bIctDebug)

            // The call to method getIntervals above set variable iNumXCoordsFound
            if (iNumXCoordsFound != 2) {
                sMsgText = "iWarp: numCoords <> 2. y: " + iY + " numCoords " + iNumXCoordsFound;
                statusPrint(sMsgText);
                for(int i = 0; i < iNumXCoordsFound; i++) {
                    sMsgText = String.format("%d\t%d\t%6.2f\t%6.2f\t%6.2f", 
                        iY, iaScreenXCoords[i],
                        faXCoords[i], faYCoords[i], faZCoords[i]);
                    statusPrint(sMsgText);
                    // Why is this goto in a for loop? You can't actually execute the goto multiple times
                    // from within a single for loop. Once you goto, you're out of the for loop.
                    goto nextScanLine;
                } // for i
            }

            fDx = faXCoords[1] - faXCoords[0];
            fDy = faYCoords[1] - faYCoords[0];
            fDz = faZCoords[1] - faZCoords[0];
            iNumSteps = (int)iaScreenXCoords[1] - (int)iaScreenXCoords[0] + 1;
            
            // Initialize fXIncrement, fYIncrement, and fZIncrement
            // fXIncrement will be used to modify fXIn
            // fYIncrement will be used to modify fYIn
            // fZIncrement will be used to modify fZIn
            if (iNumSteps - 1.0 > 0.0) {
                fXIncrement = fDx/(float)(iNumSteps - 1);
                fYIncrement = fDy/(float)(iNumSteps - 1);
                fZIncrement = fDz/(float)(iNumSteps - 1);
            } else {
                fXIncrement = 0.0f;
                fYIncrement = 0.0f;
                fZIncrement = 0.0f;
            }

            fXIn = faXCoords[0];
            fYIn = faYCoords[0];
            fZIn = faZCoords[0];

            /* These variables are not actually used
            float dpx, dpy; 
            dpx = 1.0f / pfSx;
            dpy = 1.0f / pfSy;
            if(dpx > 0.5f) dpx = 0.5f;
            if(dpy > 0.5f) dpy = 0.5f;
            */

            // Loop through a single scan line
            for(iX = (int)iaScreenXCoords[0]; iX <= (int)iaScreenXCoords[1]; iX++) {
                // Determine the transformed x, y by inverting the true perspective
                // projection
                fW = (fZIn + fD) / fD;
                fXIn = (iX - fHalfInWidth) * fW;
                fYIn = (iY - fHalfInHeight)* fW;

                // The following method sets fXOut, fYOut and fZOut
                inverseMatrix.transformPoint(fXIn, fYIn, fZIn, fXOut, fYOut, fZOut);

                if(bIctDebug) {
                    if(
                    (iX == (int)iaScreenXCoords[0]) || 
                    (iX == (int)iaScreenXCoords[1])) {
                        sMsgText = String.format(
                            "scanLine: %2d xi: %6.2f yi: %6.2f zi: %6.2f xo: %6.2f yo: %6.2f zo: %6.2f",
                            iY, fXIn, fYIn, fZIn, fXOut, fYOut, fZOut);
                        statusPrint(sMsgText);
                    }
                }

                // if (TRUE) // no super-sampling
                // if (sx <= 1.0 && sy <= 1.0 && sz <= 1.0) {  // super-sample expansions only
                switch(iBpp) {
                case 8:
                    bytIntensity = pInMImage.getMPixel((int)(fXOut + fHalfInWidth + 1), (int)(fYOut + fHalfInHeight + 1));
                    break;

                case 24:
                    pInMImage.getMPixelRGB((int)(fXOut + fHalfInWidth + 1), (int)(fYOut + fHalfInHeight + 1), 
                        bytRed, bytGreen, bytBlue);
                    break;
                } // switch

                if(pZMImage != null) {
                    theZ = pZMImage.getMPixel32((int)(iX + fXCentOffset), (int)(iY + fYCentOffset));
                    fDist = MathUtils.getDistance3d(fXIn, fYIn, fZIn, pfVx, pfVy, pfVz);

                    // Update the zbuffer if a smaller distance and non transparent color
                    if((fDist < theZ) && ((int)bytIntensity != JICTConstants.I_CHROMAVALUE)) {
                        pZMImage.setMPixel32((int)(iX + fXCentOffset), (int)(iY + fYCentOffset), 
                            fDist);
                        switch(iBpp) {
                        case 8:
                            pOutMImage.setMPixel((int)(iX + fXCentOffset), (int)(iY + fYCentOffset), 
                                bytIntensity);
                            break;

                        case 24:
                            pOutMImage.setMPixelRGB((int)(iX + fXCentOffset), (int)(iY + fYCentOffset), 
                                bytRed, bytGreen, bytBlue);
                            break;
                        }
                    }
                } else {
                    switch(iBpp) {
                    case 8:
                        pOutMImage.setMPixel((int)(iX + fXCentOffset), (int)(iY + fYCentOffset), 
                            bytIntensity);
                        break;

                    case 24:
                        pOutMImage.setMPixelRGB((int)(iX + fXCentOffset), (int)(iY + fYCentOffset), 
                            bytRed, bytGreen, bytBlue);
                        break;
                    }
                }

                fXIn += fXIncrement;
                fYIn += fYIncrement;
                fZIn += fZIncrement;
            } // for iX, end of column loop

            nextScanLine:  continue;
        } // for iY, end of scan line loop

        if(bIctDebug) {
            if(pZMImage != null) {
                statusPrint("iwarpz: Writing zBuffer -  d:\\ict20\\output\\rawWarpz.bmp");
                pZMImage.saveAs8("d:\\ict20\\output\\Warpz8.bmp");
            }
        }

        return 0;
    } // iwarpz


    // This method originally came from IWARP.CPP
    //
    // Called from: 
    //     iwarpz
    // and iwarpz in turn is called from tweenImage
    // which in turn is called by MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    // and iwarpz is also called from MainFrame.onWarpParamDlgClosed
    public static int getIntervals(Shape3d pShape, int piY, Integer piNumCoords,
    int piNumAllocatedXCoords, // this parameter is not used
    int[] piaScreenXCoords,
    float[] pfaXCoords, float[] pfaYCoords, float[] pfaZCoords) {
        // Scan Conversion. 
        // For the indicated scan line y, find all screen x coords
        // where the shape crosses scan line y.  
        // Sort the resulting screen x coordinate array.
        // For each screen x, find the corresponding tx, ty, and tz by interpolating 
        // from the two cornerpoints.
        String sMsgText;
        int[] iaTempScreenXCoords = new int[4];
        int[] intDistance = new int[4];
        float[] faTempXCoords = new float[4];
        float[] faTempYCoords = new float[4];
        float[] faTempZCoords = new float[4];
        int iTempIndex = 0;

        int iNumShapeVertices = pShape.getNumVertices();
        if(iNumShapeVertices != 4) {
            statusPrint("getIntervals: numShapeVertices must = 4");
            return -1;
        }

        Float fM = 0f, fB = 0f;
        int i, index, iNewX;
        Boolean bHorzFlag = false, bVertFlag = false;
        int iCurrentScreenXIdx; // index into array screenXCoords
        // float *currenttX, *currenttY, *currenttZ; 
        int iTxCoordsIdx, iTyCoordsIdx, iTzCoordsIdx;
        float fX;
        iCurrentScreenXIdx = 0;
        iTxCoordsIdx = 0;
        iTyCoordsIdx = 0;
        iTzCoordsIdx = 0;
        piNumCoords   = 0;
        int iSx1, iSy1, iSx2, iSy2;
        int iMinX, iMaxX, iMinY, iMaxY;
        float fTx1, fTy1, fTz1;
        float fTx2, fTy2, fTz2;
        float fPartialDistance, fTotalDistance, fRatio;

        pShape.initCurrentVertex();
        for (index = 1; index <= iNumShapeVertices; index++) {
            iSx1 = (int)pShape.mCurrentVertex.sx;
            iSy1 = (int)pShape.mCurrentVertex.sy;
            
            fTx1 = pShape.mCurrentVertex.tx;
            fTy1 = pShape.mCurrentVertex.ty;
            fTz1 = pShape.mCurrentVertex.tz;
            // theShape.currentVertex++;
            pShape.incCurrentVertex();

            // If this is the last line segment, circle around to the beginning
            if(index == iNumShapeVertices) {
                pShape.initCurrentVertex();
            }
            iSx2 = (int)pShape.mCurrentVertex.sx;  // Can't use (currentVertex+1).x
            iSy2 = (int)pShape.mCurrentVertex.sy;
            
            fTx2 = pShape.mCurrentVertex.tx;
            fTy2 = pShape.mCurrentVertex.ty;	 
            fTz2 = pShape.mCurrentVertex.tz;
            pShape.decCurrentVertex();

            iMinX = Math.min(iSx1, iSx2);
            iMaxX = Math.max(iSx1, iSx2);
            iMinY = Math.min(iSy1, iSy2);
            iMaxY = Math.max(iSy1, iSy2);

            // The following method sets variables fM, fB, bHorzFlag and bVertFlag
            MathUtils.getLineEquation(iSx1, iSy1, iSx2, iSy2, fM, fB, bHorzFlag, bVertFlag);
            fX = 0.0f;
            if(fM != 0.0f) {
                fX = ((float)piY - fB) / fM;
            }
            iNewX = (int)fX;
            
            if(bIctDebug) {
                sMsgText = "getIntervals: sx1: " + iSx1 + "  sx2: " + iSx2 + 
                    "  sy1: " + iSy1 + " sy2: " + iSy2;
                statusPrint(sMsgText);

                sMsgText = "getIntervals: index: " + index + " newX: " + iNewX + 
                    "  Horz: " + bHorzFlag + "  vert: " + bVertFlag; 
                statusPrint(sMsgText);
            }
            
            if (!(bHorzFlag || bVertFlag)) {
                // Determine z by interpolating between screen line segment endpoints
                fTotalDistance   = MathUtils.getDistance2d(iSx1,  iSy1, iSx2, iSy2);
                fPartialDistance = MathUtils.getDistance2d(iNewX, piY, iSx1, iSy1);
                // This is a ratio of screen coordinates
                if(fTotalDistance != 0.0f) {
                    fRatio = fPartialDistance/fTotalDistance; // 0 <= ratio <= 1
                } else {
                    statusPrint("getIntervals: totalDistance cannot equal 0");
                    return -1;
                }
                
                fRatio = 1.0f - fRatio;
                
                if (
                (iNewX >= iMinX && iNewX <= iMaxX) && 
                (piY >= iMinY && piY <= iMaxY)) {
                    piaScreenXCoords[iCurrentScreenXIdx] = iNewX;
                    pfaXCoords[iTxCoordsIdx] = fTx2 + (fRatio * (fTx1 - fTx2));
                    pfaYCoords[iTyCoordsIdx] = fTy2 + (fRatio * (fTy1 - fTy2));	
                    pfaZCoords[iTzCoordsIdx] = fTz2 + (fRatio * (fTz1 - fTz2));
                    if(bIctDebug) {
                        statusPrint("diagPoint");
                    }

                    iTxCoordsIdx++;
                    iTyCoordsIdx++;
                    iTzCoordsIdx++;
                    iCurrentScreenXIdx++;
                    intDistance[index-1] = MathUtils.intervalDistance(iMinX, iMaxX, (int)fX);
                    piNumCoords++;
                    // end if between sx1 and sx2
                } else { 
                    // Store the point for possible later use
                    iaTempScreenXCoords[iTempIndex] = (int)fX;
                    faTempXCoords[iTempIndex] = fTx2 + (fRatio * (fTx1 - fTx2));
                    faTempYCoords[iTempIndex] = fTy2 + (fRatio * (fTy1 - fTy2));	
                    faTempZCoords[iTempIndex] = fTz2 + (fRatio * (fTz1 - fTz2));
                    intDistance[iTempIndex] = MathUtils.intervalDistance(iMinX, iMaxX, (int)fX);
                    iTempIndex++;

                    if(bIctDebug) {
                        statusPrint("non diagPoint");
                    }
                }
                // end if not horizontal or vertical
            } else {
                // Handle horizontal and vertical lines
                if (bVertFlag) {
                    fTotalDistance   = Math.abs(iSy2 - iSy1);
                    fPartialDistance = Math.abs(piY - iSy1);		
                    if(fTotalDistance != 0.0f) {
                        fRatio = fPartialDistance/fTotalDistance; // 0 <= ratio <= 1
                    } else {
                        statusPrint("getIntervals: totalDistance cannot equal 0");
                        return -1;
                    }

                    fRatio = 1.0f - fRatio;
                    if (piY >= iMinY && piY <= iMaxY) {
                        piaScreenXCoords[iCurrentScreenXIdx] = iSx1;
                        pfaXCoords[iTxCoordsIdx] = fTx1;
                        pfaYCoords[iTyCoordsIdx] = fTy2 + (fRatio * (fTy1 - fTy2));	
                        pfaZCoords[iTzCoordsIdx] = fTz2 + (fRatio * (fTz1 - fTz2));

                        iCurrentScreenXIdx++;
                        iTxCoordsIdx++;
                        iTyCoordsIdx++;
                        iTzCoordsIdx++;
                        intDistance[index-1] = MathUtils.intervalDistance(iMinY, iMaxY, piY);
                        piNumCoords++;
                        if(bIctDebug) {
                            statusPrint("vertPoint");
                        }
                    } else {
                        // Store the point for possible later use
                        iaTempScreenXCoords[iTempIndex] = iSx1;
                        faTempXCoords[iTempIndex] = fTx1;
                        faTempYCoords[iTempIndex] = fTy2 + (fRatio * (fTy1 - fTy2));	
                        faTempZCoords[iTempIndex] = fTz2 + (fRatio * (fTz1 - fTz2));
                        intDistance[iTempIndex] = MathUtils.intervalDistance(iMinY, iMaxY, piY);
                        iTempIndex++;
                    }
                } // if bVertFlag
            }

            // theShape.currentVertex++;
            pShape.miCurrVtxIdx++;
        }

        // Sort the found x coordinates in ascending order
        insertionSort(piaScreenXCoords, pfaXCoords, pfaYCoords, pfaZCoords, piNumCoords);
        removeDuplicates(piaScreenXCoords, pfaXCoords, pfaYCoords, pfaZCoords, piNumCoords);

        if(piNumCoords > 2) {
            removeSimilar(piaScreenXCoords, pfaXCoords, pfaYCoords, pfaZCoords, piNumCoords, 2);
        }

        int iMinIntDist = 999999999;
        int iCol = 0;

        if (piNumCoords == 1) {
            for(i = 0; i < iTempIndex; i++) {
                if(intDistance[i] < iMinIntDist) {
                    iCol = i;
                    iMinIntDist = intDistance[i];
                }
            } // for i

            // Correct missed points due to roundoff
            if(iMinIntDist < 3) {
                piNumCoords++;
                pfaXCoords[1] = faTempXCoords[iCol];
                pfaYCoords[1] = faTempYCoords[iCol];
                pfaZCoords[1] = faTempZCoords[iCol];
                piaScreenXCoords[1] = iaTempScreenXCoords[iCol];
                insertionSort(piaScreenXCoords, pfaXCoords, pfaYCoords, pfaZCoords, piNumCoords);
            } else {
                piNumCoords++;
                pfaXCoords[1] = pfaXCoords[0];
                pfaYCoords[1] = pfaYCoords[0];
                pfaZCoords[1] = pfaZCoords[0];
                piaScreenXCoords[1] = piaScreenXCoords[0];
            }
        } // if numCoords == 1

        if(bIctDebug) {
            statusPrint("getIntervals Found: intdist\t sx  \t tx  \t ty  \t tz");
            for(i = 0; i < piNumCoords; i++) {
                sMsgText = String.format("\t%d\t%d\t%6.2f\t%6.2f\t%6.2f", 
                    intDistance[i], piaScreenXCoords[i], pfaXCoords[i], pfaYCoords[i], pfaZCoords[i]);
                statusPrint(sMsgText);
            }
        }
        
        return 0;
    } // getIntervals


    // This method originally came from IWARP.CPP
    // There is another method named insertionSort that takes 6 parameters.
    // This method here takes 5 parameters.
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


    // This method originally came from IWARP.CPP
    // There is another method named insertionSort that takes 5 parameters.
    // This method here takes 6 parameters.
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


    // This method originally came from IWARP.CPP
    // There is another method named removeDuplicates that takes 6 parameters.
    // This method here takes 5 parameters.
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


    // This method originally came from IWARP.CPP
    // There is another method named removeDuplicates that takes 5 parameters.
    // This method here takes 6 parameters.
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
    // Could not find where this is being called from.
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
    public static int iRenderz(
    MemImage outImage, MemImage matteImage, MemImage inImage,
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


    // This method originally came from IWARP.CPP
    //
    // Called from:
    //     MainFrame.onToolsWarpImage
    //     SceneList.render
    public static int antiAlias(MemImage pInMImage, MemImage pOutMImage) {
        // Each image must be the same size.
        if(
        pInMImage.getHeight() != pOutMImage.getHeight() || 
        pInMImage.getWidth() != pOutMImage.getWidth() ) {
            statusPrint("antiAlias: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (pInMImage.getBitsPerPixel() != pOutMImage.getBitsPerPixel()) || 
        (pInMImage.getBitsPerPixel() != 8)) {
            statusPrint("antiAlias: images must have 8 or 24 bit pixels.");
            return -2;
        }

        int iBpp = pInMImage.getBitsPerPixel();
        float[][] weight = new float[3][3];
        weight[0][0] = 0.05f;    // impulse function
        weight[0][1] = 0.05f;
        weight[0][2] = 0.05f;
        weight[1][0] = 0.05f;
        weight[1][1] = 0.60f; // Notice that this has the largest weight
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
        int iImHeight = pInMImage.getHeight();
        int iImWidth  = pInMImage.getWidth();
        /* These variables are not used
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        */
        int iRow, iCol;
        float fSum;
        float fq00, fq10, fq20;
        float fq01, fq11, fq21;
        float fq02, fq12, fq22;
        float fSumR;
        float fq00r, fq10r, fq20r = 0.0f;
        float fq11r = 0.0f, fq21r = 0.0f; // fq01r?
        float fq12r = 0.0f, fq22r = 0.0f; // fq02r?
        float fSumG;
        float fq00g, fq10g, fq20g = 0.0f;
        float fq11g = 0.0f, fq21g = 0.0f; // fq01g?
        float fq12g = 0.0f, fq22g = 0.0f; // fq02g?
        float fSumB;
        float fq00b, fq10b, fq20b = 0.0f;
        float fq11b = 0.0f, fq21b = 0.0f; // fq01b?
        float fq12b = 0.0f, fq22b = 0.0f; // fq02b?
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;

        for (iRow = 2; iRow <= iImHeight - 1; iRow++) {
            for (iCol = 2; iCol <= iImWidth - 1; iCol++) {
                switch(iBpp) {
                case 8:
                    // This case is similar to MemImage.alphaSmooth3
                    fq00 = pInMImage.getMPixel(iCol - 1, iRow - 1) * weight[0][0];
                    fq10 = pInMImage.getMPixel(iCol,     iRow - 1) * weight[1][0]; // used twice
                    fq20 = pInMImage.getMPixel(iCol + 1, iRow - 1) * weight[2][0]; // used twice

                    fq01 = pInMImage.getMPixel(iCol - 1, iRow)     * weight[0][1]; // not used
                    fq11 = pInMImage.getMPixel(iCol,     iRow)     * weight[1][1]; // this has the largest weight
                    fq21 = pInMImage.getMPixel(iCol + 1, iRow)     * weight[2][1];

                    fq02 = pInMImage.getMPixel(iCol - 1, iRow + 1) * weight[0][2]; // not used
                    fq12 = pInMImage.getMPixel(iCol,     iRow + 1) * weight[1][2];
                    fq22 = pInMImage.getMPixel(iCol + 1, iRow + 1) * weight[2][2];

                    // TODO: I believe the following line has a few errors.
                    // It adds fq10 twice (probably meant to use fq10 once and fq01 once)
                    // It adds fq20 twice (probably meant to use fq20 once and fq02 once)
                    // Notice that fSum is the sum of 9 color values, one for each 
                    // cell in a 3x3 matrix
                    fSum = fq00 + fq10 + fq20 + fq10 + fq11 + fq12 + fq20 + fq21 + fq22;
                    // Ensure that fSum has a value beween 0.0 and 255.0, inclusive
                    fSum = MathUtils.bound(fSum, 0.0f, 255.0f);

                    // Set the color at iCol, iRow to our average (fSum)
                    pOutMImage.setMPixel(iCol, iRow, (byte)(fSum + 0.5f));
                    break;

                case 24:
                    pInMImage.getMPixelRGB(iCol - 1, iRow - 1, bytRed, bytGreen, bytBlue);
                    fq00r = (float)bytRed   * weight[0][0];
                    fq00g = (float)bytGreen * weight[0][0];
                    fq00b = (float)bytBlue  * weight[0][0];

                    pInMImage.getMPixelRGB(iCol, iRow - 1, bytRed, bytGreen, bytBlue);
                    fq10r = (float)bytRed   * weight[1][0];
                    fq10g = (float)bytGreen * weight[1][0];
                    fq10b = (float)bytBlue  * weight[1][0];

                    /*
                    // The following values for the variables fq20, fq01, fq11, fq21 are not used
                    fq20 = pInMImage.getMPixel(iCol + 1, iRow - 1) * weight[2][0];
                    fq01 = pInMImage.getMPixel(iCol - 1, iRow)     * weight[0][1];
                    fq11 = pInMImage.getMPixel(iCol,     iRow)     * weight[1][1];
                    fq21 = pInMImage.getMPixel(iCol + 1, iRow)     * weight[2][1];

                    // The following values for the variables fq02, fq12, and fq22 are not used
                    fq02 = pInMImage.getMPixel(iCol - 1, iRow + 1) * weight[0][2];
                    fq12 = pInMImage.getMPixel(iCol,     iRow + 1) * weight[1][2];
                    fq22 = pInMImage.getMPixel(iCol + 1, iRow + 1) * weight[2][2];
                    */

                    // I believe the following line has a few errors.
                    // It uses q10r twice and q20r twice.
                    // Also only q00r and q10r have non-zero values.
                    fSumR = fq00r + fq10r + fq20r + fq10r + fq11r + fq12r + fq20r + fq21r + fq22r;

                    // I believe the following line has a few errors.
                    // It uses fq10g twice and fq20g twice.
                    // Also only fq00g and fq10g have non-zero values.
                    fSumG = fq00g + fq10g + fq20g + fq10g + fq11g + fq12g + fq20g + fq21g + fq22g;

                    // I believe the following line as a few errors.
                    // It uses fq10b twice and fq20b twice.
                    // Also only fq00b and fq10b have non-zero values.
                    fSumB = fq00b + fq10b + fq20b + fq10b + fq11b + fq12b + fq20b + fq21b + fq22b;

                    fSumR = MathUtils.bound(fSumR, 0.0f, 255.0f);
                    fSumG = MathUtils.bound(fSumG, 0.0f, 255.0f);
                    fSumB = MathUtils.bound(fSumB, 0.0f, 255.0f);

                    pOutMImage.setMPixelRGB(iCol, iRow, 
                        (byte)(fSumR + 0.5f), (byte)(fSumG + 0.5f), (byte)(fSumB + 0.5f));
                    break;
                } // switch
            } // for iCol
        } // for iRow

        return 0;
    } // antiAlias


    // This method originally came from IWARP.CPP
    public static int fWarp1(MemImage pInMImage, MemImage pOutMImage,
    float pfRx, float pfRy, float pfRz, 
    float pfSx, float pfSy, float pfSz,
    float pfTx, float pfTy, float pfTz, 
    TMatrix pViewMatrix,
    float pfRefPointX, float pfRefPointY, float pfRefPointZ) {
        // Project the points to the screen and copy from the input image 
        //
        // The reference point is a point in the texture image's 
        // original(i.e. initial, default) position in Cartesian space.
        // about which the image is rotated and scaled.
        // For example, the reference point 0,0,0 is the center of the 
        // texture image.
        String sMsgText;
        float fX, fY;
        /* These variables are not used
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES];
        float[] tXCoords = new float[I_MAXWVERTICES]; 
        float[] tYCoords = new float[I_MAXWVERTICES];
        */

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if (bIctDebug) {
            statusPrint("fWarp inputs:");
            sMsgText = String.format("rx: %6.2f  ry: %6.2f  rz: %6.2f", pfRx, pfRy, pfRz);
            statusPrint(sMsgText);

            sMsgText = String.format("sx: %6.2f  sy: %6.2f  sz: %6.2f", pfSx, pfSy, pfSz);
            statusPrint(sMsgText);

            sMsgText = String.format("tx: %6.2f  ty: %6.2f  tz: %6.2f", pfTx, pfTy, pfTz);
            statusPrint(sMsgText);

            sMsgText = String.format("refx: %6.2f  refy: %6.2f  refz: %6.2f", pfRefPointX, pfRefPointY, pfRefPointZ);
            statusPrint(sMsgText);
        }

        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        float fXRadians = pfRx * JICTConstants.F_DTR;
        float fYRadians = pfRy * JICTConstants.F_DTR;
        float fZRadians = pfRz * JICTConstants.F_DTR;
        forwardMatrix.scale(pfSx, pfSy, pfSz);
        forwardMatrix.rotate(fXRadians, fYRadians, fZRadians);
        forwardMatrix.translate(pfTx, pfTy, pfTz);
        TMatrix viewModelMatrix = new TMatrix();
        viewModelMatrix.multiply(pViewMatrix, forwardMatrix);
    
        if (bIctDebug) {
            forwardMatrix.display("Forward Matrix:");
        }
    
        int iInHeight  = pInMImage.getHeight();
        int iInWidth   = pInMImage.getWidth();
        int iOutHeight = pOutMImage.getHeight();
        int iOutWidth  = pOutMImage.getWidth();
        
        float fHalfHeight = iInHeight / 2.0f;
        float fHalfWidth  = iInWidth / 2.0f;

        // This algorithm actually uses a reference point defined in pixel space
        // therefore we convert it now.
        float fIntRefPointX = pfRefPointX + fHalfWidth;
        float fIntRefPointY = pfRefPointY + fHalfHeight;
        // float intRefPointZ = refPointZ; // this variable is not used
        fHalfWidth  -= (fHalfWidth  - fIntRefPointX);
        fHalfHeight -= (fHalfHeight - fIntRefPointY);

        // Calculate offsets that will center the warped image in the output image
        int iXOffset = (int)(iOutWidth / 2.0);
        int iYOffset = (int)(iOutHeight/ 2.0);
        
        // Shortcut: if no rotation or scale, just copy the image
        if(
        pfRx == 0.0f && pfRy == 0.0f && pfRz == 0.0f && 
        pfSx == 1.0f && pfSy == 1.0f && pfSz == 1.0f && 
        pfTz == 0.0f) {
            pInMImage.copy(pOutMImage, (int)pfTx + iXOffset, (int)pfTy + iYOffset);
            statusPrint("fWarp: shortcut");
            return 0;
        }
        
        float fXIn, fYIn, fZIn; 
        Integer xOut = 0, yOut = 0;
        byte bytIntensity;

        // Loop through the texture coordinates, projecting to the screen
        fZIn = 0.0f;
        float fAtx = 0.0f, fAty = 0.0f, fAtz = 0.0f;
        float fIncrement = 1.0f;
    
        for (fY = 1; fY <= iInHeight; fY += fIncrement) {
            fYIn = fY - fHalfHeight;
        
            for(fX = 1; fX < iInWidth; fX += fIncrement) {
                bytIntensity = pInMImage.getMPixel((int)fX, (int)fY);
                fXIn = fX - fHalfWidth;
                forwardMatrix.transformAndProjectPoint(fXIn, fYIn, fZIn, xOut, yOut, 
                    pfRefPointX, pfRefPointY, pfRefPointZ, 
                    iOutHeight, iOutWidth, 
                    fAtx, fAty, fAtz);
        
                pOutMImage.setMPixel((int)xOut, (int)yOut, bytIntensity);
            } // for fX
        } // for fY
    
        if (bIctDebug) {
            statusPrint("fWarp1: Writing output -  d:\\ict20\\output\\rawfWarp.bmp");
            pOutMImage.writeBMP("d:\\ict20\\output\\rawfWarp.bmp");
        }
    
        return 0;
    } // fWarp1
  
  
    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     iRender (called twice, but I couldn't find where iRender is being called from)
    //     iRenderz (called twice)
    public static int fwarpz(MemImage pInMImage, MemImage pOutMImage, MemImage pZMImage,
    float pfRx, float pfRy, float pfRz, 
    float pfSx, float pfSy, float pfSz, 
    float pfTx, float pfTy, float pfTz, 
    float pfVx, float pfVy, float pfVz, 
    TMatrix pViewMatrix,
    float pfRefPointX, float pfRefPointY, float pfRefPointZ) {
        // The reference point is a point in the texture image's 
        // original (i.e. initial, default) position in Cartesian space,
        // about which the image is rotated and scaled.
        // For example, the reference point 0,0,0 is the center of the 
        // texture image.

        String sMsgText;
        float fX, fY;
        /* These variables are not used
        int myStatus, numXCoordsFound;
        int[] screenXCoords = new int[I_MAXWVERTICES];
        float[] tZCoords = new float[I_MAXWVERTICES]; 
        float[] tXCoords = new float[I_MAXWVERTICES];
        float[] tYCoords = new float[I_MAXWVERTICES];
        */

        // The shape object contains the projected 4 sided polygon and a z coordinate
        // at each of the projected vertices.
        if (bIctDebug) {
            statusPrint("fWarp inputs:");
            sMsgText = String.format("rx: %6.2f  ry: %6.2f  rz: %6.2f", pfRx, pfRy, pfRz);
            statusPrint(sMsgText);

            sMsgText = String.format("sx: %6.2f  sy: %6.2f  sz: %6.2f", pfSx, pfSy, pfSz);
            statusPrint(sMsgText);

            sMsgText = String.format("tx: %6.2f  ty: %6.2f  tz: %6.2f", pfTx, pfTy, pfTz);
            statusPrint(sMsgText);

            sMsgText = String.format("refx: %6.2f  refy: %6.2f  refz: %6.2f", pfRefPointX, pfRefPointY, pfRefPointZ);
            statusPrint(sMsgText);
        }

        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        TMatrix viewModelMatrix = new TMatrix();
        float fXRadians = pfRx * JICTConstants.F_DTR;
        float fYRadians = pfRy * JICTConstants.F_DTR;
        float fZRadians = pfRz * JICTConstants.F_DTR;
        forwardMatrix.scale(pfSx, pfSy, pfSz);
        forwardMatrix.rotate(fXRadians, fYRadians, fZRadians);
        forwardMatrix.translate(pfTx, pfTy, pfTz);
        viewModelMatrix.multiply(pViewMatrix, forwardMatrix);
    
        if (bIctDebug) {
            forwardMatrix.display("Forward Matrix:");
        }
    
        int iInHeight  = pInMImage.getHeight();
        int iInWidth   = pInMImage.getWidth();
        int iOutHeight = pOutMImage.getHeight();
        int iOutWidth  = pOutMImage.getWidth();
    
        float fHalfHeight = iInHeight / 2.0f;
        float fHalfWidth  = iInWidth / 2.0f;
        int iBpp = pInMImage.getBitsPerPixel();

        // int xOffset = (int)(outWidth / 2.0); // this variable is not used
        // int yOffset = (int)(outHeight/ 2.0); // this variable is not used
    
        // Shortcut: if no rotation or scale, just copy the image
        if(
        pfRx == 0.0f && pfRy == 0.0f && pfRz == 0.0f && 
        pfSx == 1.0f && pfSy == 1.0f && pfSz == 1.0f && 
        pfTz == 0.0f) {
            pInMImage.copy(pOutMImage, (int)(pfTx + fHalfWidth), (int)(pfTy + fHalfHeight));
            statusPrint("fWarpz: shortcut");
            return 0;
        }
        
        float fXIn, fYIn, fZIn; 
        Integer iXOut1 = 0, iYOut1 = 0;
        Integer iXOut2 = 0, iYOut2 = 0;
        Integer iXOut3 = 0, iYOut3 = 0;
        Integer iXOut4 = 0, iYOut4 = 0;
        Byte bytIntensity1 = (byte)0;
        Byte bytIntensity2 = (byte)0;
        Byte bytIntensity3 = (byte)0;
        Byte bytIntensity4 = (byte)0;

        // Loop through the texture coordinates, projecting to the screen
        fZIn = 0.0f;
        Float fAtx = 0.0f, fAty = 0.0f, fAtz = 0.0f;
        float fIncr = 0.5f;                  // oversample 2:1
        float fInvIncr = 1.0f / fIncr;
        float fD1 = 0.0f, fD2 = 0.0f, fD3 = 0.0f, fD4 = 0.0f;
        Byte bytRed1  = (byte)0;
        Byte bytRed2  = (byte)0;
        Byte bytRed3  = (byte)0;
        Byte bytRed4  = (byte)0;
        Byte bytBlue1 = (byte)0;
        Byte bytBlue2 = (byte)0;
        Byte bytBlue3 = (byte)0;
        Byte bytBlue4 = (byte)0;
        
        // Note fInvIncr * fIncr = 1.0f
        for (fY = fInvIncr * fIncr; fY <= iInHeight; fY += fIncr) {
            fYIn = fY - fHalfHeight;
        
            for(fX = fInvIncr * fIncr; fX <= iInWidth; fX += fIncr) {
                if(iBpp == 8) {
                    // We'll use bytIntensity1, bytIntensity2, bytIntensity3, and bytIntensity4, 
                    // later on, as parameters to pOutMImage.fillPolyz
                    bytIntensity1 = pInMImage.getMPixel((int)(fX - fIncr), (int)fY);
                    bytIntensity2 = pInMImage.getMPixel(          (int)fX, (int)fY);
                    bytIntensity3 = pInMImage.getMPixel(          (int)fX, (int)(fY - fIncr));
                    bytIntensity4 = pInMImage.getMPixel((int)(fX - fIncr), (int)(fY - fIncr));
                } else if (iBpp == 24) {
                    // From the method calls below, we will only use the output parameters 
                    // bytIntensity1, bytIntensity2, bytIntensity3, and bytIntensity4, 
                    // later on, as parameters to pOutMImage.fillPolyz
                    pInMImage.getMPixelRGB((int)(fX - fIncr),           (int)fY, bytRed1, bytIntensity1, bytBlue1);
                    pInMImage.getMPixelRGB(          (int)fX,           (int)fY, bytRed2, bytIntensity2, bytBlue2);
                    pInMImage.getMPixelRGB(          (int)fX, (int)(fY - fIncr), bytRed3, bytIntensity3, bytBlue3);
                    pInMImage.getMPixelRGB((int)(fX - fIncr), (int)(fY - fIncr), bytRed4, bytIntensity4, bytBlue4);
                }

                fXIn = fX - fHalfWidth;
                // Note that 4 calls to forwardMatrix.transformAndProjectPoint follow.
                // All of them use the same parameters except for the first two and 
                // the fourth and fifth parameters.
                // Note also that the last 3 parameters (fAtx, fAty, and fAtz) are 
                // output parameters.
                forwardMatrix.transformAndProjectPoint(fXIn - fIncr, fYIn, fZIn, 
                    iXOut1, iYOut1, 
                    pfRefPointX, pfRefPointY, pfRefPointZ, 
                    iOutHeight, iOutWidth, 
                    fAtx, fAty, fAtz);
                if(pZMImage != null) {
                    // fD1 = distance between viewpoint (pfVx, pfVy, pfVz) and 
                    // transformed/projected point (fXIn - fIncr, fYIn, fZIn) => (fAtx, fAty, fAtz)
                    fD1 = MathUtils.getDistance3d(pfVx, pfVy, pfVz, fAtx, fAty, fAtz);
                }
            
                forwardMatrix.transformAndProjectPoint(fXIn, fYIn, fZIn, 
                    iXOut2, iYOut2, 
                    pfRefPointX, pfRefPointY, pfRefPointZ, 
                    iOutHeight, iOutWidth, 
                    fAtx, fAty, fAtz);
                if(pZMImage != null) {
                    // fD2 = distance between viewpoint (pfVx, pfVy, pfVz) and 
                    // transformed/projected point (fXIn, fYIn, fZIn) => (fAtx, fAty, fAtz)
                    fD2 = MathUtils.getDistance3d(pfVx, pfVy, pfVz, fAtx, fAty, fAtz);
                }
            
                forwardMatrix.transformAndProjectPoint(fXIn, fYIn - fIncr, fZIn, 
                    iXOut3, iYOut3, 
                    pfRefPointX, pfRefPointY, pfRefPointZ, 
                    iOutHeight, iOutWidth, 
                    fAtx, fAty, fAtz);
                if(pZMImage != null) {
                    // fD3 = distance between viewpoint (pfVx, pfVy, pfVz) and 
                    // transformed/projected point (fXIn, fYIn - fIncr, fZIn) => (fAtx, fAty, fAtz)
                    fD3 = MathUtils.getDistance3d(pfVx, pfVy, pfVz, fAtx, fAty, fAtz);
                }
            
                forwardMatrix.transformAndProjectPoint(fXIn - fIncr, fYIn - fIncr, fZIn, 
                    iXOut4, iYOut4, 
                    pfRefPointX, pfRefPointY, pfRefPointZ, 
                    iOutHeight, iOutWidth, 
                    fAtx, fAty, fAtz);
                if(pZMImage != null) {
                    // fD4 = distance between viewpoint (pfVx, pfVy, pfVz) and 
                    // transformed/projected point (fXIn - fIncr, fYIn - fIncr, fZIn) => (fAtx, fAty, fAtz)
                    fD4 = MathUtils.getDistance3d(pfVx, pfVy, pfVz, fAtx, fAty, fAtz);
                }
            
                pOutMImage.fillPolyz(
                    iXOut1, iYOut1, bytIntensity1, fD1, 
                    iXOut2, iYOut2, bytIntensity2, fD2, 
                    iXOut3, iYOut3, bytIntensity3, fD3, 
                    iXOut4, iYOut4, bytIntensity4, fD4, 
                    pZMImage);
            }
        }
    
        if (bIctDebug) {
            pZMImage.writeBMP("d:\\ict20\\output\\zBuffer32.bmp");
            statusPrint("fWarp3: Writing z output - d:\\ict20\\output\\zBuffer32.bmp");

            pZMImage.saveAs8("d:\\ict20\\output\\zBuffer8.bmp");
            statusPrint("fWarp3: Writing z output - d:\\ict20\\output\\zBuffer8.bmp");
        
            statusPrint("fWarp3: Writing output -  c:\\ict\\output\\rawfWarp.bmp");
            pOutMImage.writeBMP("c:\\ict\\output\\rawfWarp.bmp");
        }
    
        return 0;
    } // fwarpz


    // This method originally came from IWARP.CPP
    public static int fwarpz2(MemImage pInputMImage, MemImage pOutputMImage, MemImage zBufMImage, 
    float pfRx, float pfRy, float pfRz, 
    float pfSx, float pfSy, float pfSz, 
    Float pfTx, Float pfTy, Float pfTz, 
    float pfVx, float pfVy, float pfVz, 
    TMatrix pViewMatrix, 
    float pfRefPointX, float pfRefPointY, float pfRefpointZ) { // the last 3 parameters are not used
        String sMsgText;

        // Create the line buffer data structures
        int[] iaXBuffer;
        int iXBufferIdx; // index into iaXBuffer
        int iXPrev1Idx = 0, iXPrev2Idx = 0; // indices into iaXBuffer

        int[] iaYBuffer; 
        int iYBufferIdx; // index into iaYBuffer
        int iYPrev1Idx = 0, iYPrev2Idx = 0; // indices into iaYBuffer

        
        float[] faDBuffer;
        int iDBufferIdx; // index into faDBuffer
        int iDPrev1Idx = 0, iDPrev2Idx = 0; // indices into faDBuffer
        
        byte[] iaIBuffer;
        int iIBufferIdx; // index into iaIBuffer
        int iPrev1Idx = 0, iPrev2Idx = 0; // indices into iaIBuffer

        byte bytTemp1 = (byte)0, bytTemp2;
        int iXTemp1 = 0, iYTemp1 = 0, iXTemp2 = 0, iYTemp2;
        float fDTemp1 = 0.0f, fDTemp2 = 0.0f; // These two will stay at 0.0 unless zBufMImage != null
        // float *wxBuffer, *wyBuffer, *wzBuffer; // these variables are not used
        // float *wxTemp, *wyTemp, *wzTemp; // these variables are not used
        
        // Build the forward transformation matrix
        TMatrix forwardMatrix = new TMatrix();
        float fXRadians = pfRx * JICTConstants.F_DTR;
        float fYRadians = pfRy * JICTConstants.F_DTR;
        float fZRadians = pfRz * JICTConstants.F_DTR;
        forwardMatrix.scale(pfSx, pfSy, pfSz);
        forwardMatrix.rotate(fXRadians, fYRadians, fZRadians);
        forwardMatrix.translate(pfTx, pfTy, pfTz);
        TMatrix viewModelMatrix = new TMatrix();
        viewModelMatrix.multiply(pViewMatrix, forwardMatrix);
    
        if (bIctDebug) {
            forwardMatrix.display("Forward Matrix:");
        }
    
        int iBpp       = pInputMImage.getBitsPerPixel();
        int iInHeight  = pInputMImage.getHeight();
        int iInWidth   = pInputMImage.getWidth();
        int iOutHeight = pOutputMImage.getHeight();
        int iOutWidth  = pOutputMImage.getWidth();
        float fHalfHeight = iInHeight / 2.0f;
        float fHalfWidth  = iInWidth / 2.0f;
        
        float fIncrement = 0.5f;
        float fInverseInc = 1.0f / fIncrement;
        int iNumCalcs = (int)(iInWidth * fInverseInc);
    
        iaXBuffer = new int[iNumCalcs];
        /* if iaXBuffer is null, the JVM would have thrown an OutOfMemoryException
        if (iaXBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for iaXBuffer");
            return -1;
        }
        */

        iaYBuffer = new int[iNumCalcs];
        /* if iaYBuffer is null, the JVM would have thrown an OutOfMemoryException
        if (iaYBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for iaYBuffer");
            return -1;
        }
        */
    
        faDBuffer = new float[iNumCalcs];
        /* if faDBuffer is null, the JVM would have thrown an OutOfMemoryException
        if (faDBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for distance Buffer");
            return -1;
        }
        */
    
        iaIBuffer = new byte[iNumCalcs];
        /* if iaBuffer is null, the JVM would have thrown an OutOfMemoryException
        if (iaBuffer == null) {
            statusPrint("fwarpz2: Not enough memory for iaBuffer");
            return -1;
        }
        */
    
        /*
        // Temporary - for testing
        pfVx = (float)iOutWidth/2.0f;
        pfVy = (float)iOutHeight/2.0f;
        pfVz = 512.0f;
        */
    
        sMsgText = String.format("fwarpz2: Viewer location: vx: %f, vy: %f, vz: %f", pfVx, pfVy, pfVz);
        statusPrint(sMsgText);
        iXBufferIdx = 0;
        iYBufferIdx = 0;
        iIBufferIdx = 0;
        iDBufferIdx = 0;

        float fRow, fCol;
        float fX1, fY1, fZ1;
        Byte byt1 = (byte)0, bytRed1 = (byte)0, bytBlue1 = (byte)0;
        // byte bytGreen1; // not used
        Integer iSx1 = 0, iSy1 = 0;
        float fRefX = 0.0f, fRefY = 0.0f, fRefZ = 0.0f;
    
        // fInverseInc * fIncrement = 1.0
        for (fRow = fInverseInc * fIncrement; fRow <= iInHeight; fRow += fIncrement) {
            for (fCol = fInverseInc * fIncrement; fCol <= iInWidth; fCol += fIncrement) {
                fX1 = fCol - fHalfWidth;
                fY1 = fRow - fHalfHeight;
                fZ1 = 0.0f;
                if(iBpp == 8) {
                    // The following returns the color at (fCol, fRow)
                    byt1 = pInputMImage.getMPixel((int)fCol, (int)fRow);
                }
                if(iBpp == 24) {
                    // The following method sets bytRed1, byt1, and bytBlue1. We'll only use byt1.
                    pInputMImage.getMPixelRGB((int)fCol, (int)fRow, bytRed1, byt1, bytBlue1);
                }
    
                // Project to the screen
                // The following method sets parameters iSx1, iSy1, pfTx, pfTy, and pfTz
                viewModelMatrix.transformAndProjectPoint(fX1, fY1, fZ1, iSx1, iSy1, 
                    fRefX, fRefY, fRefZ, iOutHeight, iOutWidth, pfTx, pfTy, pfTz);
                if(fRow == 1.0f) {
                    iaXBuffer[iXBufferIdx] = iSx1.byteValue();
                    iXBufferIdx++;

                    iaYBuffer[iYBufferIdx] = iSy1.byteValue();
                    iYBufferIdx++;

                    iaIBuffer[iIBufferIdx] = byt1;
                    iIBufferIdx++;

                    faDBuffer[iDBufferIdx] = MathUtils.getDistance3d(pfTx, pfTy, pfTz, pfVx, pfVy, pfVz);
                    iDBufferIdx++;
                }
            
                if ((fRow > 1.0f) && (fCol == 1.0f)) {
                    iXTemp1 = iSx1;
                    iYTemp1 = iSy1;
                    bytTemp1 = byt1;

                    iXPrev1Idx = 0;
                    iYPrev1Idx = 0;

                    iXPrev2Idx = 0;
                    iYPrev2Idx = 0;
                    iXPrev2Idx++;
                    iYPrev2Idx++;

                    iPrev1Idx = 0;
                    iPrev2Idx = 0;
                    iPrev2Idx++;
        
                    iDPrev1Idx = 0;
                    iDPrev2Idx = 0;
                    iDPrev2Idx++;

                    if(zBufMImage != null) {
                        fDTemp1 = MathUtils.getDistance3d(pfTx, pfTy, pfTz, pfVx, pfVy, pfVz);
                    }
                }
    
                if ((fRow > 1) && (fCol > 1)) {
                    iXTemp2 = iSx1;
                    iYTemp2 = iSy1;
                    bytTemp2 = byt1;
                    if(zBufMImage != null) {
                        fDTemp2 = MathUtils.getDistance3d(pfTx, pfTy, pfTz, pfVx, pfVy, pfVz);
                    }
         
                    // Render the quadrangle intensities
                    //                     
                    // Render the quadrangle distances and update the intermediate zBuffer
                    pOutputMImage.fillPolyz( 
                        iaXBuffer[iXPrev1Idx], iaYBuffer[iYPrev1Idx], iaIBuffer[iPrev1Idx], faDBuffer[iDPrev1Idx],
                        iaXBuffer[iXPrev2Idx], iaYBuffer[iYPrev2Idx], iaIBuffer[iPrev2Idx], faDBuffer[iDPrev2Idx],
                        iXTemp2,               iYTemp2,               bytTemp2,             fDTemp2,
                        iXTemp1,               iYTemp1,               bytTemp1,             fDTemp1, 
                        zBufMImage);
        
                    iaXBuffer[iXPrev1Idx] = iXTemp1;
                    iaYBuffer[iYPrev1Idx] = iYTemp1;
                    iaIBuffer[iPrev1Idx] = bytTemp1;

                    iXTemp1 = iXTemp2;
                    iYTemp1 = iYTemp2;
                    bytTemp1 = bytTemp2;

                    iXPrev1Idx++;
                    iYPrev1Idx++;

                    iXPrev2Idx++;
                    iYPrev2Idx++;

                    iPrev1Idx++;
                    iPrev2Idx++;
        
                    faDBuffer[iDPrev1Idx] = fDTemp1;
                    fDTemp1 = fDTemp2;

                    iDPrev1Idx++;
                    iDPrev2Idx++;
                } // if ((fRow > 1) && (fCol > 1))
            } // for fCol
        } // for fRow
    
        return 0;
    } // fwarpz2


    // This method originally came from QMESHMODEL.CPP
    // 
    // Called from:
    //     QuadMeshDlg.onOK
    public static int createQMeshModel(String psInputImagePath, String psDestinationDir, 
    int piModelType) {  
        float fPi = 3.1415926f;
        int iRow, iCol; 
        float fX, fY, fZ;
        float fSizeFactor, fYValue;
        // float theDimension; // assigned a value, but not read
 
        // float randMax = (float)RAND_MAX; //This variable is no longer used
        // TODO: What is the significance of the following 3 values?
        float fXMagnitude = 2.4f;
        float fYMagnitude = 1.5f;
        // Used later (in switch stmt, in case JICTConstants.I_WHITENOISE) as scaling factors:
        // xMImage.setMPixel32(iCol, iRow, (float)iCol + (random.nextFloat() * fXMagnitude));
        // yMImage.setMPixel32(iCol, iRow, (float)iRow + (random.nextFloat() * fYMagnitude));
        // float fZMagnitude = 5.7f; // Not used
 
        // float smallerSide, cubeFaceSize; // not used
        // int v1, v2, v3, v4; // not used
 
        Integer iImHeight = 0, iImWidth = 0, iBitsPerPixel = 0;
        int iStatus;
        iStatus = readBMPHeader(psInputImagePath, iImHeight, iImWidth, iBitsPerPixel);
        if(iStatus != 0) {
            statusPrint("createQMeshModel: Unable to open texture image");
            return -1;
        }

        MemImage inputMImage = new MemImage(psInputImagePath, 0, 0, 
            JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
        MemImage xMImage = new MemImage(iImHeight, iImWidth, 32);
        MemImage yMImage = new MemImage(iImHeight, iImWidth, 32);
        MemImage zMImage = new MemImage(iImHeight, iImWidth, 32);
         
        float fRadius;
        // float startTheta, stopTheta; // assigned values, but not read
        float fAngleTemp, fAngularInc; // fAngularInc is used to increment fAngleTemp
        float fAngle, fAngleInc; // fAngleInc is used to increment fAngle
        float fXCent, fYCent; // Used in calculating fDistance
        float fDistance;
        String sMsgText;
        
        fSizeFactor = (float)iImWidth;
 
        switch(piModelType) {
        case JICTConstants.I_CYLINDER:
            // Uses fX and fY
            fRadius = (float)iImWidth/(2.0f * fPi);
            /* startTheta and stopTheta are assigned values, but are not read
            startTheta =   0.0f;
            stopTheta  = 360.0f;
            */
            fAngularInc = 360.0f / (float)iImWidth;
            fAngleTemp  =   0.0f;
            for (iRow = 1; iRow <= iImHeight; iRow++) {
                fAngleTemp = 0.0f;

                for (iCol = 1; iCol <= iImWidth; iCol++) {
                    fX = fRadius * (float)Math.cos(fAngleTemp * JICTConstants.F_DTR);
                    fY = fRadius * (float)Math.sin(fAngleTemp * JICTConstants.F_DTR);
                    fAngleTemp += fAngularInc;
                    xMImage.setMPixel32(iCol, iRow, fX);
                    yMImage.setMPixel32(iCol, iRow, (float)iRow);
                    zMImage.setMPixel32(iCol, iRow, fY);
                } // for iCol
            } // for iRow
            break;
 
        case JICTConstants.I_PLANAR:
            // Uses fX and fY
            for (iRow = 1; iRow <= iImHeight; iRow++) {
                for (iCol = 1; iCol <= iImWidth; iCol++) {
                    fX = (float)iCol;
                    fY = (float)iRow;
                    xMImage.setMPixel32(iCol, iRow, fX);
                    yMImage.setMPixel32(iCol, iRow, fY);
                    zMImage.setMPixel32(iCol, iRow, 0.0f);
                } // for iCol
            } // for iRow
            break;
 
        case JICTConstants.I_SPHERE:
            // Uses fX and fZ
            /* startTheta and stopTheta are assigned values, but not read
            startTheta = 0.0f;
            stopTheta = 360.0f;
            */
            fAngle = 0.0f;
            /* theDimension is assigned a value, but not read
            theDimension = (float)iImHeight;  // Set the horz and vert dims equal to the max of the rectangle sides
            if (iImWidth > iImHeight) {
                theDimension = (float)iImWidth;
            }
            */

            fAngleInc = 180.0f / (float)iImHeight; // The height traces out a hemispherical arc
            fAngularInc = 360.0f / (float)iImWidth;
            fAngleTemp  =   0.0f;

            for (iRow = 1; iRow <= iImHeight; iRow++) {
                fRadius = (float)Math.sin(fAngle * JICTConstants.F_DTR) * fSizeFactor;
                fAngleTemp = 0.0f;

                for (iCol = 1; iCol <= iImWidth; iCol++) {
                    fX = (fRadius * (float)Math.cos(fAngleTemp * JICTConstants.F_DTR) ) + fSizeFactor; //put the left most edge in the positive quadrant
                    fZ = (fRadius * (float)Math.sin(fAngleTemp * JICTConstants.F_DTR) ) + fSizeFactor;
                    xMImage.setMPixel32(iCol, iRow, fX);
                    zMImage.setMPixel32(iCol, iRow, fZ);

                    fYValue = (float)Math.acos(fAngle * JICTConstants.F_DTR) * fSizeFactor;
                    yMImage.setMPixel32(iCol, iRow, fYValue);

                    fAngleTemp += fAngularInc;
                } // for iCol
                fAngle += fAngleInc;
            } // for iRow
            break;
 
        case JICTConstants.I_SINE1D:
            // Uses fZ
            fAngularInc = 1.0f;
            fRadius = 100.0f;
            for (iRow = 1; iRow <= iImHeight; iRow++) {
                fAngleTemp = 0.0f;

                for (iCol = 1; iCol <= iImWidth; iCol++) {
                    fZ = fRadius * (float)Math.sin(fAngleTemp * JICTConstants.F_DTR);
                    xMImage.setMPixel32(iCol, iRow, (float)iCol);
                    yMImage.setMPixel32(iCol, iRow, fZ);
                    zMImage.setMPixel32(iCol, iRow, (float)iRow);
                    fAngleTemp += fAngularInc;
                } // for iCol
            } // for iRow
            break;
 
        case JICTConstants.I_SINE2D:
            //  Circumference = 2*Pi*radius
            fRadius = (float)(iImWidth/(2.0f * fPi)/3.0f);   // Make r small enough for three sinusoidal rotations
            // fAngularInc = 360.0f / (float)iImWidth; // not needed for this case
            fXCent = (float)iImWidth/2.0f;
            fYCent = (float)iImHeight/2.0f;

            for (iRow = 1; iRow <= iImHeight; iRow++) {
                // fAngleTemp = 0.0f; // not needed for this case

                for (iCol = 1; iCol <= iImWidth; iCol++) {
                    xMImage.setMPixel32(iCol, iRow, (float)iCol);
                    zMImage.setMPixel32(iCol, iRow, (float)iRow);
                    fDistance = (float)Math.sqrt(((iCol - fXCent) * (iCol - fXCent)) + ((iRow - fYCent) * (iRow - fYCent)));           
                    yMImage.setMPixel32(iCol, iRow, fRadius * (float)Math.sin(5.0f * fDistance  * JICTConstants.F_DTR));
                } // for iCol
            } // for iRow
            break;
 
        case JICTConstants.I_WHITENOISE:
            // By default the Random class is seeded with current time
            // the first time it is used.
            // So the numbers will be different every time we run.
            Random random = new Random();
 
            for (iRow = 1; iRow <= iImHeight; iRow++) {
                for (iCol = 1; iCol <= iImWidth; iCol++) {
                    xMImage.setMPixel32(iCol, iRow, (float)iCol + (random.nextFloat() * fXMagnitude));
                    yMImage.setMPixel32(iCol, iRow, (float)iRow + (random.nextFloat() * fYMagnitude));
                    zMImage.setMPixel32(iCol, iRow, 0.0f);
                } // for iCol
            } // for iRow
            break;
 
        default:
            break;
        } // switch 
 
        // Generate the output path names and save the results
        //
        // Apply a file naming convention:
        // first determine the input file name, then substitute the last letter with x, y, or z
        String sDrive, sDir, sFile, sExt;
        String sDdrive, sDdir, sDfile, sDext;
        String sOutPath, sXPath, sYPath, sZPath;
 
        // Get the filename and extension from psInputImagePath
        //_splitpath(psInputImagePath, sDrive, sDir, sFile, sExt);
        // int theLength = sFile.length(); // not used
        File inputImageFile = new File(psInputImagePath);
        String sFilenameWExt = inputImageFile.getName();

        // Create sOutPath as the psDestinationDir, with the 
        // file name we extrcted from psInputImagePath
        //_splitpath(psDestinationDir, sDdrive, sDdir, sDfile, sDext);
        //_makepath(sOutPath, sDdrive, sDdir, sFile, sExt);
        File destinationDirFile = new File(psDestinationDir);
        if (destinationDirFile.isDirectory()) {
            // append the file name
            sOutPath = psDestinationDir + File.pathSeparator + sFilenameWExt;
        } else {
            statusPrint("createQMeshModel: Destination dir is not a directory: " + psDestinationDir);
            return -2;
        }
        

        // As a matter of convenience copy the texture image to the same directory
        // in which the surface images reside, if it isn't there already.
        if(!FileUtils.fileExists(sOutPath)) {
            sMsgText = "createQMeshModel: Copying QMesh Model Texture Image to: " + sOutPath;
            statusPrint(sMsgText);
            CopyFile(psInputImagePath, sOutPath, 1);
        }
 
        FileUtils.constructPathName(sXPath, sOutPath, 'x');
        FileUtils.constructPathName(sYPath, sOutPath, 'y');
        FileUtils.constructPathName(sZPath, sOutPath, 'z');

        // Insure that a generated path is not the same as the texture path
        if(
        sXPath.equalsIgnoreCase(psInputImagePath) ||
        sYPath.equalsIgnoreCase(psInputImagePath) ||
        sZPath.equalsIgnoreCase(psInputImagePath)) {
            statusPrint("createQMeshModel: A surface image may not have the same name as the texture image.");
            sMsgText = "textureImage: " + psInputImagePath;
            statusPrint(sMsgText);

            sMsgText = "xImage: " + sXPath;
            statusPrint(sMsgText);

            sMsgText = "yImage: " + sYPath;
            statusPrint(sMsgText);

            sMsgText = "zImage: " + sZPath;
            statusPrint(sMsgText);

            return -1;
        }

        float fX1, fY1, fZ1;
        float fRefX, fRefY, fRefZ;
        float fXBucket, fYBucket, fZBucket;

        float fXMin, fXMax, fYMin, fYMax, fZMin, fZMax;
        fXMin = xMImage.getMPixel32(1, 1);
        fXMax = fXMin;

        fYMin = yMImage.getMPixel32(1, 1);
        fYMax = fYMin;

        fZMin = zMImage.getMPixel32(1, 1);
        fZMax = fZMin;

        // Get an approximate model centroid, bounding box and display it.
        statusPrint("createQMeshModel: Calculating approximate mesh centroid and bounding box");
        fXBucket = 0.0f;
        fYBucket = 0.0f;
        fZBucket = 0.0f;
        float fTotalCells = 0.0f; // used to calculate fRefX, fRefY, and fRefZ
        int iMeshIncrement = 3;
 
        for (iRow = 1; iRow <= iImHeight; iRow += iMeshIncrement) {
            for (iCol = 1; iCol <= iImWidth; iCol += iMeshIncrement) {
                fX1 = xMImage.getMPixel32(iCol, iRow);
                fY1 = yMImage.getMPixel32(iCol, iRow);
                fZ1 = zMImage.getMPixel32(iCol, iRow);

                if(fX1 > fXMax) fXMax = fX1;
                if(fX1 < fXMin) fXMin = fX1;

                if(fY1 > fYMax) fYMax = fY1;
                if(fY1 < fYMin) fYMin = fY1;

                if(fZ1 > fZMax) fZMax = fZ1;
                if(fZ1 < fZMin) fZMin = fZ1;

                fXBucket += fX1;
                fYBucket += fY1;
                fZBucket += fZ1;
                fTotalCells++;
            } // for iCol
        } // for iRow

        // fRefX, fRefY, and fRefZ are displayed via statusPrint, but otherwise not used
        fRefX = fXBucket/fTotalCells;
        fRefY = fYBucket/fTotalCells;
        fRefZ = fZBucket/fTotalCells;

        sMsgText = "QuadMesh centroid x: " + fRefX + " y: " + fRefY + " z: " + fRefZ;
        statusPrint(sMsgText);

        sMsgText = "QuadMesh BBox Mins x: " + fXMin + " y: " + fYMin + " z: " + fZMin;
        statusPrint(sMsgText);

        sMsgText = "QuadMesh BBox Maxs x: " + fXMax + " y: " + fYMax + " z: " + fZMax;
        statusPrint(sMsgText);
 
        sMsgText = "Saving QMesh: " + sXPath;
        statusPrint(sMsgText);
        xMImage.writeBMP(sXPath);

        sMsgText = "Saving QMesh: " + sYPath;
        statusPrint(sMsgText);
        yMImage.writeBMP(sYPath);

        sMsgText = "Saving QMesh: " + sZPath;
        statusPrint(sMsgText);
        zMImage.writeBMP(sZPath);
        
        MemImage xMImage8 = new MemImage(iImHeight, iImWidth, 8);
        MemImage yMImage8 = new MemImage(iImHeight, iImWidth, 8);
        MemImage zMImage8 = new MemImage(iImHeight, iImWidth, 8);
       
        xMImage.scaleTo8(xMImage8);
        yMImage.scaleTo8(yMImage8);
        zMImage.scaleTo8(zMImage8);
 
        xMImage8.writeBMP("d:\\ict20\\output\\meshx8.bmp");
        yMImage8.writeBMP("d:\\ict20\\output\\meshy8.bmp");
        zMImage8.writeBMP("d:\\ict20\\output\\meshz8.bmp");
 
        return 0;
    } // createQMeshModel
 

    // This method came from QMESHMODEL.CPP
    // Called from:
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    public static int getMeshCentroid(MemImage pxMImage, MemImage pyMImage, MemImage pzMImage,
    Float pFCentroidX, Float pFCentroidY, Float pFCentroidZ) {
        String sMsgText;
        statusPrint("Calculating mesh centroid");

        // Each image must be the same size.
        if(
        pxMImage.getHeight() != pyMImage.getHeight() || 
        pyMImage.getHeight() != pzMImage.getHeight() || 
        pxMImage.getWidth()  != pyMImage.getWidth()  ||  
        pyMImage.getWidth()  != pzMImage.getWidth()) {
            statusPrint("getMeshCentroid: Surface images must have equal size.");
            return -1;
        }
    
        // Each image must have 32 bit pixels.
        if(
        pxMImage.getBitsPerPixel() != 32 || 
        pyMImage.getBitsPerPixel() != 32 ||
        pzMImage.getBitsPerPixel() != 32) {
            statusPrint("getMeshCentroid: Surface images must have 32 bit pixels.");
            return -2;
        }
    
        int iImHeight = pxMImage.getHeight();
        int iImWidth  = pxMImage.getWidth();
        float fX1 = 0.0f;
        float fY1 = 0.0f;
        float fZ1 = 0.0f;
        float fTotalCells = 0.0f;
        int iMeshIncrement = 1;  // increase for greater speed, but less accuracy
        int iRow, iCol;
    
        for (iRow = 1; iRow <= iImHeight; iRow += iMeshIncrement) {
            for (iCol = 1; iCol <= iImWidth; iCol += iMeshIncrement) {
                fX1 += pxMImage.getMPixel32(iCol, iRow);
                fY1 += pyMImage.getMPixel32(iCol, iRow);
                fZ1 += pzMImage.getMPixel32(iCol, iRow);
                fTotalCells++;
            } // for iCol
        } // for iRow

        // Set the output parameters
        pFCentroidX = fX1/fTotalCells;
        pFCentroidY = fY1/fTotalCells;
        pFCentroidZ = fZ1/fTotalCells;

        sMsgText = "Mesh centroid calculated: " + pFCentroidX + " " + pFCentroidY + " " + pFCentroidZ;
        statusPrint(sMsgText);

        return 0;
    } // getMeshCentroid
  

    // This method originally came from QMESHMODEL.CPP
    //
    // Called from:
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    public static int translateMesh(MemImage pxMImage, MemImage pyMImage, MemImage pzMImage,
    float pfOffsetX, float pfOffsetY, float pfOffsetZ) {
        statusPrint("Translating mesh.");

        // Each image must be the same size.
        if(
        pxMImage.getHeight() != pyMImage.getHeight() || 
        pyMImage.getHeight() != pzMImage.getHeight() ||
        pxMImage.getWidth()  != pyMImage.getWidth()  || 
        pyMImage.getWidth()  != pzMImage.getWidth()) {
            statusPrint("translateMesh: Surface images must have equal size.");
            return -1;
        }
    
        // Each image must have 32 bit pixels.
        if(
        pxMImage.getBitsPerPixel() != 32 || 
        pyMImage.getBitsPerPixel() != 32 ||
        pzMImage.getBitsPerPixel() != 32) {
            statusPrint("translateMesh: Surface images must have 32 bit pixels.");
            return -2;
        }
    
        int iImHeight = pxMImage.getHeight();
        int iImWidth  = pxMImage.getWidth();
        float fX1 = 0.0f;
        float fY1 = 0.0f;
        float fZ1 = 0.0f;
        int iMeshIncrement = 1;  // increase for greater speed, but less accuracy
        int iRow, iCol;
    
        for (iRow = 1; iRow <= iImHeight; iRow += iMeshIncrement) {
            for (iCol = 1; iCol <= iImWidth; iCol += iMeshIncrement) {
                fX1 = pxMImage.getMPixel32(iCol, iRow);
                fX1 += pfOffsetX;
                pxMImage.setMPixel32(iCol, iRow, fX1);

                fY1 = pyMImage.getMPixel32(iCol, iRow);
                fY1 += pfOffsetY;
                pyMImage.setMPixel32(iCol, iRow, fY1);

                fZ1 = pzMImage.getMPixel32(iCol, iRow);
                fZ1 += pfOffsetZ;
                pzMImage.setMPixel32(iCol, iRow, fZ1);
            } // for col
        } // for row

        statusPrint("Mesh translated."); 
        return 0;
    } // translateMesh


    // This method originally came from MEMIMG32.CPP
    //
    // Called from:
    //     SceneList.render
    public static int makeRGBimage(String psRedImage, String psGreenImage, String psBlueImage, 
    String psOutFileName) {
        String sMsgBuffer;

        // Combine separate color channels into one RGB BMP
        int iRHeight, iRWidth;
        int iGHeight, iGWidth;
        int iBHeight, iBWidth;

        MemImage redMImage = new MemImage(psRedImage, 0, 0, 
            JICTConstants.I_SEQUENTIAL, 'R', JICTConstants.I_REDCOLOR);
        if (!redMImage.isValid()) {
            sMsgBuffer = "makeRGBImage: Unable to open Red image: " + psRedImage;
            statusPrint(sMsgBuffer);
            return 1;
        }

        MemImage greenMImage = new MemImage(psGreenImage, 0, 0, 
            JICTConstants.I_SEQUENTIAL,'R', JICTConstants.I_GREENCOLOR);
        if (!greenMImage.isValid()) {
            sMsgBuffer = "makeRGBImage: Unable to open Green image: " + psGreenImage;
            statusPrint(sMsgBuffer);
            return 1;
        }

        MemImage blueMImage = new MemImage(psBlueImage, 0, 0, 
            JICTConstants.I_SEQUENTIAL,'R', JICTConstants.I_BLUECOLOR);
        if (!blueMImage.isValid()) {
            sMsgBuffer = "makeRGBImage: Unable to open Blue image: %s" + psBlueImage;
            statusPrint(sMsgBuffer);
            return 1;
        }

        iRHeight = redMImage.getHeight();
        iRWidth  = redMImage.getWidth();

        iGHeight = greenMImage.getHeight();
        iGWidth  = greenMImage.getWidth();

        iBHeight = blueMImage.getHeight();
        iBWidth  = blueMImage.getWidth();

        if (!(iRWidth == iGWidth && iGWidth == iBWidth && iRWidth == iBWidth)) {
            statusPrint("makeRGBImage: R,G, and B image widths are not equal.");
            return 1;
        }
        if (!(iRHeight == iGHeight && iGHeight == iBHeight && iRHeight == iBHeight)) {
            statusPrint("makeRGBImage: R,G, and B image heights are not equal.");
            return 1;
        }

        MemImage RGBMImage = new MemImage(psOutFileName, iGHeight, iGWidth, 
            JICTConstants.I_SEQUENTIAL, 'W', JICTConstants.I_RGBCOLOR);
        if (!RGBMImage.isValid()) {
            statusPrint("makeRGBImage: Unable to open RGB image.");

            redMImage.close();
            greenMImage.close();
            blueMImage.close();
            return 1;
        }

        // byta = > byte array
        byte[] bytaRedPixels, bytaGreenPixels, bytaBluePixels, bytaRgbPixels;
        int iRStatus, iGStatus, iBStatus;

        for (int iY = 1; iY <= iGHeight; iY++) {
            iRStatus = redMImage.readNextRow();
            if (iRStatus != 0) {
                statusPrint("makeRGBImage: red readNextRow error.");

                redMImage.close();
                greenMImage.close();
                blueMImage.close();
                return 1;
            }

            iGStatus = greenMImage.readNextRow();
            if (iGStatus != 0) {
                statusPrint("makeRGBImage: green readNextRow error.");

                redMImage.close();
                greenMImage.close();
                blueMImage.close();
                return 1;
            }

            iBStatus = blueMImage.readNextRow();
            if (iBStatus != 0) {
                statusPrint("makeRGBImage: blue readNextRow error.");

                redMImage.close();
                greenMImage.close();
                blueMImage.close();
                return 1;
            }

            bytaRedPixels   = redMImage.getBytes();
            int redPixelIdx = 0;
            bytaGreenPixels = greenMImage.getBytes();
            int greenPixelIdx = 0;
            bytaBluePixels  = blueMImage.getBytes();
            int bluePixelIdx = 0;
            bytaRgbPixels   = RGBMImage.getBytes();
            int rgbPixelIdx = 0;

            for (int iX = 1; iX <= iGWidth; iX++) {
                bytaRgbPixels[rgbPixelIdx] = bytaBluePixels[bluePixelIdx];
                rgbPixelIdx++;

                bytaRgbPixels[rgbPixelIdx] = bytaGreenPixels[greenPixelIdx];
                rgbPixelIdx++;

                bytaRgbPixels[rgbPixelIdx] = bytaRedPixels[redPixelIdx];
                rgbPixelIdx++;

                redPixelIdx++;
                greenPixelIdx++;
                bluePixelIdx++;
            } // for iX

            // Write the output
            RGBMImage.writeNextRow();
        } // for iY

        // Close the files and destroy the objects
        redMImage.close();
        greenMImage.close();
        blueMImage.close();
        RGBMImage.close();

        FileUtils.deleteFile(psRedImage);     // to conserve disk space, remove the
        FileUtils.deleteFile(psGreenImage);   // input files
        FileUtils.deleteFile(psBlueImage);
        return 0;
    } // makeRGBimage


    // This method came from TWEEN.CPP
    // Called from:
    //     tweenImage
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    public static int getRowIntervals(MemImage pMImage, int piRow, 
    int[] piaIntervalList, Integer pINumIntervals) {
        int iImWidth = pMImage.getWidth();
        int iBpp = pMImage.getBitsPerPixel();
        int iCol; 
        int iIntervalStatus = 0;
        int iCounter = 0;
        byte bytValue;
        Byte bytRed = 0, bytGreen = 0, bytBlue = 0;
        
        for(iCol = 1; iCol <= iImWidth; iCol++) {
            // Set aValue
            switch(iBpp) {
            case 8:
                bytValue = pMImage.getMPixel(iCol, piRow);
                break;
                
            case 24:
                // The following method sets parameters bytRed, bytGreen and bytBlue
                pMImage.getMPixelRGB(iCol, piRow, bytRed, bytGreen, bytBlue);
                if(
                bytRed   != JICTConstants.I_CHROMARED || 
                bytGreen != JICTConstants.I_CHROMAGREEN || 
                bytBlue  != JICTConstants.I_CHROMABLUE) {
                    bytValue = 255;
                } else {
                    bytValue = JICTConstants.I_CHROMAVALUE;
                }
                break;
            } // switch

            if(
            (iIntervalStatus == 0) && (
            bytValue != JICTConstants.I_CHROMAVALUE)) {   // interval start
                piaIntervalList[iCounter] = iCol;
                iCounter++;
                iIntervalStatus = 1;
            }

            if(
            (iIntervalStatus == 1) && 
            (bytValue == JICTConstants.I_CHROMAVALUE)) {  // interval stop
                piaIntervalList[iCounter] = iCol;
                iCounter++;
                iIntervalStatus = 0;
            }
        } // for iCol

        if(iIntervalStatus == 1) {                      // catch end of line
            piaIntervalList[iCounter] = iImWidth;
            iCounter++;
        }

        pINumIntervals = iCounter / 2;
        return 0;
    } // getRowIntervals


    // This method originally came from TWEEN.CPP
    //
    // Called from:
    //     tweenImage
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    public static int getTotalIntervalLength(int[] piaIntervalList, int piNumIntervals) {
        int iTotalLength = 0;
        int i;

        if(piNumIntervals == 0) {
            return iTotalLength;
        }

        for (i = 0; i < piNumIntervals * 2; i += 2) {
            iTotalLength += (piaIntervalList[i + 1] - piaIntervalList[i] + 1);
        }

        return iTotalLength;
    } // getTotalIntervalLength


    // This method came from TWEEN.CPP
    // Called from:
    //     tweenImage
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    public static int indexToCoord(int piIndex, int[] piaIntervalList, int piNumIntervals) {
        // Map index into the interval list
        String sMsgText;
        int iCount, iRunningCount, iCountDelta;
        int iCoord, i;

        iRunningCount = 0;
        if(piNumIntervals == 0) {
            statusPrint("indexToCoord: numIntervals is 0");
            return -1;
        }

        int iMaxIndex = getTotalIntervalLength(piaIntervalList, piNumIntervals);
        if(piIndex > iMaxIndex) {
            sMsgText = "indexToCoord: Index: " + piIndex + " > maxIndex: " + iMaxIndex;
            statusPrint(sMsgText);
            return -2;
        }

        for(i = 0; i < piNumIntervals * 2; i += 2) {
            iCount = piaIntervalList[i + 1] - piaIntervalList[i] + 1;
            iRunningCount = iRunningCount + iCount;

            if(piIndex <= iRunningCount) {
                iCountDelta = iRunningCount - piIndex;
                iCoord = piaIntervalList[i + 1] - iCountDelta;
                return iCoord;
            }
        } // for i

        sMsgText = "indexToCoord: Coord not found. Index: " + piIndex;
        statusPrint(sMsgText);

        for(i = 0; i < piNumIntervals * 2; i += 2) {
            sMsgText = "i: " + i + " B: " + piaIntervalList[i] + " E: " + piaIntervalList[i + 1];
            statusPrint(sMsgText);
        } // for i

        return -3;
    } // indexToCoord


    // This method originally came from SHADERS.CPP
    //
    // This method smooth shades a triangle given 3 vertices (x1, y1), (x2, y2)
    // and (x3, y3) on the triangle, and an intensity at each vertex (i1, i2, and i3).
    // See p 173 of Visual Special Effects Toolkit in C++.
    //
    // Called from:
    //     GPipe.addFace
    //     MemImage.fillPolyz
    public static int fillTrianglez(
    int piX1, int piY1, float pfI1, float pfD1,
    int piX2, int piY2, float pfI2, float pfD2,
    int piX3, int piY3, float pfI3, float pfD3,
    MemImage pOutMImage, MemImage pZMImage) {
        // zImage contains the distance values.
        // To use this function without a z-buffer, call with zImage equal to NULL.

        // Pixels are written to outImage only if the new distance (derived 
        // from d1, d2, d3) is less than the corresponding distance in the zImage.
        //
        // The assumption is made here that sets of points describing 
        // vertical or horizontal lines have been handled elsewhere.

        int iMidPoint = 0;
        int iMinX = 0, iMinY = 0;
        int iMaxX = 0, iMaxY = 0;
        int iMidX = 0, iMidY = 0;
        int iDenominator;
        float fMinI = 0.0f, fMaxI = 0.0f, fMidI = 0.0f;
        float fMinD = 0.0f, fMaxD = 0.0f, fMidD = 0.0f;
        float fIntensity, fIntensityStep, fDistance, fDistanceStep;
        float id1 = 0.0f, id2 = 0.0f, fOldZ;

        int iBpp = pOutMImage.getBitsPerPixel();
        if(pZMImage != null) {
            if (pZMImage.getBitsPerPixel() != 32) {
                statusPrint("fillTrianglez: z image must be 32 bits/pixel");
                return -1;
            }
        }

        iMidY = -1;
        int iYMax = piY1;
        int iYMin = piY1;
        int iMinPoint = 1;
        int iMaxPoint = 1;

        if(piY2 > iYMax) { 
            iYMax = piY2;
            iMaxPoint = 2;
        }

        if(piY3 > iYMax) {
            iYMax = piY3;
            iMaxPoint = 4;
        }

        if(piY2 < iYMin) { 
            iYMin = piY2;
            iMinPoint = 2;
        }

        if(piY3 < iYMin) {
            iYMin = piY3;
            iMinPoint = 4;
        }

        // Calculat iMidPoint (used later in a switch stmt)
        if ((iMinPoint + iMaxPoint) == 3) iMidPoint = 4;
        if ((iMinPoint + iMaxPoint) == 5) iMidPoint = 2;
        if ((iMinPoint + iMaxPoint) == 6) iMidPoint = 1;

        switch (iMinPoint) {
        case 1:
            iMinX = piX1;
            iMinY = piY1;
            fMinI = pfI1;
            fMinD = pfD1;
            break;

        case 2:
            iMinX = piX2;
            iMinY = piY2;
            fMinI = pfI2;
            fMinD = pfD2;
            break;

        case 4:
            iMinX = piX3;
            iMinY = piY3;
            fMinI = pfI3;
            fMinD = pfD3;
            break;
        } // switch

        switch (iMaxPoint) {
        case 1:
            iMaxX = piX1;
            iMaxY = piY1;
            fMaxI = pfI1;
            fMaxD = pfD1;
            break;

        case 2:
            iMaxX = piX2;
            iMaxY = piY2;
            fMaxI = pfI2;
            fMaxD = pfD2;
            break;

        case 4:
            iMaxX = piX3;
            iMaxY = piY3;
            fMaxI = pfI3;
            fMaxD = pfD3;
            break;
        } // switch

        switch (iMidPoint) {
        case 1:
            iMidX = piX1;
            iMidY = piY1;
            fMidI = pfI1;
            fMidD = pfD1;
            break;

        case 2:
            iMidX = piX2;
            iMidY = piY2;
            fMidI = pfI2;
            fMidD = pfD2;
            break;

        case 4:
            iMidX = piX3;
            iMidY = piY3;
            fMidI = pfI3;
            fMidD = pfD3;
            break;
        } // switch

        int iRow, iCol, iFirstX, iLastX, iTriangleType;
        int ix1 = 0, ix2 = 0, ip1 = 0, ip2 = 0, iSteps;

        iTriangleType = JICTConstants.I_POINTONSIDE;
        if(iMidY == iMaxY) iTriangleType = JICTConstants.I_POINTONTOP;
        if(iMinY == iMidY) iTriangleType = JICTConstants.I_POINTONBOTTOM;

        // Now we have a rotationally independant situation.  Interpolate rows from 
        // minY to midY then from midY to maxY
        if (iMidY == -1) {
            iMidY = iMaxY;
            return -1;
        }

        if(iTriangleType == JICTConstants.I_POINTONSIDE) {
            for(iRow = iMinY; iRow <= iMidY; iRow++) {
                // Interpolate the x interval and the intensities at the interval boundary
                ix1 = (int)MathUtils.interpolate((float)iMinX, (float)iMaxX, (float)iMinY, (float)iMaxY, (float)iRow);
                ix2 = (int)MathUtils.interpolate((float)iMinX, (float)iMidX, (float)iMinY, (float)iMidY, (float)iRow);
                ip1 = (int)MathUtils.interpolate(       fMinI,        fMaxI, (float)iMinY, (float)iMaxY, (float)iRow);
                ip2 = (int)MathUtils.interpolate(       fMinI,        fMidI, (float)iMinY, (float)iMidY, (float)iRow);
                if(pZMImage != null) {
                    id1 = MathUtils.interpolate(fMinD, fMaxD, (float)iMinY, (float)iMaxY, (float)iRow);
                    id2 = MathUtils.interpolate(fMinD, fMidD, (float)iMinY, (float)iMidY, (float)iRow);
                }

                iSteps = Math.abs(ix2 - ix1);
                if(ix1 <= ix2) {
                    iFirstX = ix1;
                    iLastX = ix2;
                    fIntensity = ip1;
                    fDistance = id1;
                    iDenominator = iSteps + 1;
                    if(iDenominator > 0) {
                        fIntensityStep = (ip2 - ip1)/iDenominator;
                        fDistanceStep  = (id2 - id1)/iDenominator;
                    } else {
                        fIntensityStep = 0.0f;
                        fDistanceStep  = 0.0f;
                    }
                } else {
                    iFirstX = ix2;
                    iLastX = ix1;
                    fIntensity = ip2;
                    fDistance = id2;
                    iDenominator = iSteps + 1;

                    if(iDenominator > 0) {
                        fIntensityStep = (ip1 - ip2)/iDenominator;
                        fDistanceStep  = (id1 - id2)/iDenominator;
                    } else {
                        fIntensityStep = 0.0f;
                        fDistanceStep  = 0.0f;
                    }
                } 

                for (iCol = iFirstX; iCol <= iLastX; iCol++) {
                    if(pZMImage != null) {  
                        // Render with a Z Buffer
                        fOldZ = pZMImage.getMPixel32(iCol, iRow);

                        if(fDistance <= fOldZ) {
                            if(fDistance <= 1.0f) {
                                fDistance = 1.0f;
                            }
                            fIntensity = MathUtils.bound(fIntensity, 0.0f, 255.0f);
                            pZMImage.setMPixel32(iCol, iRow, fDistance);

                            if(iBpp == 8)  pOutMImage.setMPixel(iCol, iRow, (byte)fIntensity);
                            if(iBpp == 24) pOutMImage.setMPixelRGB(iCol, iRow, (byte)fIntensity,
                                (byte)fIntensity, (byte)fIntensity);
                        }
                    } else {
                        // Render without a Z Buffer
                        fIntensity = MathUtils.bound(fIntensity, 1.0f, 255.0f);
                        if(iBpp == 8)  pOutMImage.setMPixel(iCol, iRow, (byte)fIntensity);
                        if(iBpp == 24) pOutMImage.setMPixelRGB(iCol, iRow, (byte)fIntensity,
                            (byte)fIntensity, (byte)fIntensity);
                    }

                    fIntensity += fIntensityStep;
                    fDistance  += fDistanceStep;
                }
            }

            // Handle the second half of the pointonside case
            for(iRow = iMidY; iRow <= iMaxY; iRow++) {
                // Interpolate the x interval and the intensities at the interval boundary
                ix1 = (int)MathUtils.interpolate((float)iMinX, (float)iMaxX, (float)iMinY, (float)iMaxY, (float)iRow);
                ix2 = (int)MathUtils.interpolate((float)iMidX, (float)iMaxX, (float)iMidY, (float)iMaxY, (float)iRow);
                ip1 = (int)MathUtils.interpolate(       fMinI,        fMaxI, (float)iMinY, (float)iMaxY, (float)iRow);
                ip2 = (int)MathUtils.interpolate(       fMidI,        fMaxI, (float)iMidY, (float)iMaxY, (float)iRow);
                if(pZMImage != null) {
                    id1 = MathUtils.interpolate(fMinD, fMaxD, (float)iMinY, (float)iMaxY, (float)iRow);
                    id2 = MathUtils.interpolate(fMidD, fMaxD, (float)iMidY, (float)iMaxY, (float)iRow);
                }

                iSteps = Math.abs(ix2 - ix1);
                if(ix1 <= ix2) {
                    iFirstX = ix1;
                    iLastX = ix2;
                    fIntensity = ip1;
                    fDistance = id1;
                    iDenominator = iSteps + 1;
                    if(iDenominator > 0.0) {
                        fIntensityStep = (ip2 - ip1)/iDenominator;
                        fDistanceStep  = (id2 - id1)/iDenominator;
                    } else {
                        fIntensityStep = 0.0f;
                        fDistanceStep  = 0.0f;
                    }
                } else {
                    iFirstX = ix2;
                    iLastX = ix1;
                    fIntensity = ip2;
                    fDistance = id2;
                    iDenominator = iSteps + 1;
                    if(iDenominator > 0) {
                        fIntensityStep = (ip1 - ip2)/iDenominator;
                        fDistanceStep  = (id1 - id2)/iDenominator;
                    } else {
                        fIntensityStep = 0.0f;
                        fDistanceStep  = 0.0f;
                    }
                }

                for (iCol = iFirstX; iCol <= iLastX; iCol++) {
                    if(pZMImage != null) {
                        fOldZ = pZMImage.getMPixel32(iCol, iRow);
                        if(fDistance <= fOldZ) {
                            if(fDistance <= 1.0f) {
                                fDistance = 1.0f;
                            }
                            fIntensity = MathUtils.bound(fIntensity, 0.0f, 255.0f);
                            pZMImage.setMPixel32(iCol, iRow, fDistance);
                            if(iBpp == 8)  pOutMImage.setMPixel(iCol, iRow, (byte)fIntensity);
                            if(iBpp == 24) pOutMImage.setMPixelRGB(iCol, iRow, (byte)fIntensity,
                                (byte)fIntensity, (byte)fIntensity);
                        }
                    } else {
                        fIntensity = MathUtils.bound(fIntensity, 0.0f, 255.0f);
                        if(iBpp == 8)  pOutMImage.setMPixel(iCol, iRow, (byte)fIntensity);
                        if(iBpp == 24) pOutMImage.setMPixelRGB(iCol, iRow, (byte)fIntensity,
                            (byte)fIntensity, (byte)fIntensity);
                    }
                    fIntensity += fIntensityStep;
                    fDistance  += fDistanceStep;
                } // for iCol
            } // for iRow
        } else {
            // Handle pointontop, pointonbottom cases
            for(iRow = iMinY; iRow <= iMaxY; iRow++) {
                // Interpolate the x interval and the intensities at the interval boundary
                if(iTriangleType == JICTConstants.I_POINTONTOP) {
                    ix1 = (int)MathUtils.interpolate((float)iMinX, (float)iMaxX, (float)iMinY, (float)iMaxY, (float)iRow);
                    ix2 = (int)MathUtils.interpolate((float)iMinX, (float)iMidX, (float)iMinY, (float)iMidY, (float)iRow);
                    ip1 = (int)MathUtils.interpolate(       fMinI,        fMaxI, (float)iMinY, (float)iMaxY, (float)iRow);
                    ip2 = (int)MathUtils.interpolate(       fMinI,        fMidI, (float)iMinY, (float)iMidY, (float)iRow);
                    if(pZMImage != null) {
                        id1 = MathUtils.interpolate(fMinD, fMaxD, (float)iMinY, (float)iMaxY, (float)iRow);
                        id2 = MathUtils.interpolate(fMinD, fMidD, (float)iMinY, (float)iMidY, (float)iRow);
                    }
                }
                if(iTriangleType == JICTConstants.I_POINTONBOTTOM) {
                    ix1 = (int)MathUtils.interpolate((float)iMinX, (float)iMaxX, (float)iMinY, (float)iMaxY, (float)iRow);
                    ix2 = (int)MathUtils.interpolate((float)iMidX, (float)iMaxX, (float)iMidY, (float)iMaxY, (float)iRow);
                    ip1 = (int)MathUtils.interpolate(       fMinI,        fMaxI, (float)iMinY, (float)iMaxY, (float)iRow);
                    ip2 = (int)MathUtils.interpolate(       fMidI,        fMaxI, (float)iMidY, (float)iMaxY, (float)iRow);
                    if(pZMImage != null) {
                        id1 = MathUtils.interpolate(fMinD, fMaxD, (float)iMinY, (float)iMaxY, (float)iRow);
                        id2 = MathUtils.interpolate(fMidD, fMaxD, (float)iMidY, (float)iMaxY, (float)iRow);
                    }
                }

                iSteps = Math.abs(ix2 - ix1);
                if(ix1 <= ix2) {
                    iFirstX = ix1;
                    iLastX = ix2;
                    fIntensity = ip1;
                    fDistance = id1;
                    iDenominator = iSteps + 1;
                    if(iDenominator > 0) {
                        fIntensityStep = (ip2 - ip1)/iDenominator;
                        fDistanceStep  = (id2 - id1)/iDenominator;
                    } else {
                        fIntensityStep = 0.0f;
                        fDistanceStep  = 0.0f;
                    }
                } else {
                    iFirstX = ix2;
                    iLastX = ix1;
                    fIntensity = ip2;
                    fDistance = id2;
                    iDenominator = iSteps  + 1;
                    if(iDenominator > 0) {
                        fIntensityStep = (ip1 - ip2)/iDenominator;
                        fDistanceStep  = (id1 - id2)/iDenominator;
                    } else {
                        fIntensityStep = 0.0f;
                        fDistanceStep  = 0.0f;
                    }
                }   

                for (iCol = iFirstX; iCol <= iLastX; iCol++) {
                    if(pZMImage != null) {
                        fOldZ = pZMImage.getMPixel32(iCol, iRow);
                        if(fDistance <= fOldZ) {
                            if(fDistance <= 1.0f) {
                                fDistance = 1.0f;
                            }
                            fIntensity = MathUtils.bound(fIntensity, 0.0f, 255.0f);
                            pZMImage.setMPixel32(iCol, iRow, fDistance);
                            if(iBpp == 8)  pOutMImage.setMPixel(iCol, iRow, (byte)fIntensity);
                            if(iBpp == 24) pOutMImage.setMPixelRGB(iCol, iRow, (byte)fIntensity,
                                (byte)fIntensity, (byte)fIntensity);
                        }
                    } else {
                        fIntensity = MathUtils.bound(fIntensity, 0.0f, 255.0f);
                        if(iBpp == 8)  pOutMImage.setMPixel(iCol, iRow, (byte)fIntensity);
                        if(iBpp == 24) pOutMImage.setMPixelRGB(iCol, iRow, (byte)fIntensity,
                            (byte)fIntensity, (byte)fIntensity);
                    }

                    fIntensity += fIntensityStep;
                    fDistance  += fDistanceStep;
                } // for iCol
            } // for iRow
        }

        return 0;
    } // fillTrianglez


    // This method originally came from SHADERS.CPP
    //
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
        
        if(bIctDebug) {
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
    //     GPipe.addFace
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
    // Called from:
    //     MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
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
    // This method originally came from TWEEN.CPP
    //
    // Called from:
    //     tweenImage
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
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


    // This originally method came from TWEEN.CPP
    // 
    // Called from:
    //     tweenImage
    // which in turn is called from MorphDlg.onOK (when morph type = JICTConstants.I_TWOD)
    public static int createTweenableShapes(Shape3d pInShape1, Shape3d pInShape2, 
    Shape3d pOutShapeA, Shape3d pOutShapeB) {
        int iNumVertices1 = pInShape1.getNumVertices();
        int iNumVertices2 = pInShape2.getNumVertices();
        
        int iNumVerticesDiff = Math.abs(iNumVertices1 - iNumVertices2);
        int iStatus;
        Shape3d outShape1, outShape2;

        // Add vertices to the shape having the fewer vertices
        if(iNumVertices1 < iNumVertices2) {
            outShape1 = pInShape1.copyAndExpand(iNumVerticesDiff);
            for(int i = 1; i <= iNumVerticesDiff; i++) {
                iStatus = outShape1.divideLongestArc();
                if(iStatus != 0) {
                    statusPrint("createTweenableShapes: divideLongestArc error");
                    return iStatus;
                }

                // Set output parameter outShapeA
                pOutShapeA = outShape1;
            } // for i
        } else { // iNumVertices1 >= iNumVertices2
            outShape2 = pInShape2.copyAndExpand(iNumVerticesDiff);
            for(int i = 1; i <= iNumVerticesDiff; i++) {
                iStatus = outShape2.divideLongestArc();
                if(iStatus != 0) {
                    statusPrint("createTweenableShapes: divideLongestArc error");
                    return iStatus;
                }

                // Set output parameter outShapeB
                pOutShapeB = outShape2;
            } // for i
        }

        return 0;
    } // createTweenableShapes


    // This method originally came from TWEEN.CPP
    // Called from:
    //     MorphDlg.onOK (when morph type = JICTConstants.I_THREED)
    public static int tweenMesh(float pfFraction, 
    MemImage aTexture, MemImage aX, MemImage aY, MemImage aZ,
    MemImage bTexture, MemImage bX, MemImage bY, MemImage bZ, 
    MemImage oTexture, MemImage oX, MemImage oY, MemImage oZ) {
        int iRow, iCol;
        float aValue, bValue, oValue;
        byte aByte, bByte, oByte;
        byte aRedByte = (byte)0, bRedByte = (byte)0, oRedByte;
        byte aGreenByte = (byte)0, bGreenByte = (byte)0, oGreenByte;
        byte aBlueByte = (byte)0, bBlueByte = (byte)0, oBlueByte;

        if ((pfFraction < 0.0f) || (pfFraction > 1.0f)) {
            statusPrint("tweenMesh: aFraction must be between 0 and 1");
            return -1;
        }

        float bFraction = 1.0f - pfFraction;
        int iImHeight = aTexture.getHeight();
        int iImWidth  = aTexture.getWidth();
        int bWidth    = bTexture.getWidth();
        if(iImWidth != bWidth){
            statusPrint("tweenMesh: texture images must have same width.");
            return -2;
        }

        int iBpp = aTexture.getBitsPerPixel();
        if ((iBpp != 8) && (iBpp != 24)) {
            statusPrint("tweenMesh: Texture image must have 8 or 24 bit pixels.");
            return -3;
        }

        for (iRow = 1; iRow <= iImHeight; iRow++){
            for (iCol = 1; iCol <= iImWidth; iCol++){
                aValue = aX.getMPixel32(iCol, iRow);
                bValue = bX.getMPixel32(iCol, iRow);
                oValue = (pfFraction * aValue) + (bFraction * bValue);
                oX.setMPixel32(iCol, iRow, oValue);

                aValue = aY.getMPixel32(iCol, iRow);
                bValue = bY.getMPixel32(iCol, iRow);
                oValue = (pfFraction * aValue) + (bFraction * bValue);
                oY.setMPixel32(iCol, iRow, oValue);

                aValue = aZ.getMPixel32(iCol, iRow);
                bValue = bZ.getMPixel32(iCol, iRow);
                oValue = (pfFraction * aValue) + (bFraction * bValue);
                oZ.setMPixel32(iCol, iRow, oValue);

                switch (iBpp) {
                case 8:
                    aByte = aTexture.getMPixel(iCol, iRow);
                    bByte = bTexture.getMPixel(iCol, iRow);
                    oByte = (byte)Math.round((pfFraction * (float)aByte) + (bFraction * (float)bByte));
                    oTexture.setMPixel(iCol, iRow, oByte);
                    break;

                case 24:
                    aByte      = (byte)aTexture.getMPixelRGB(iCol, iRow, aRedByte, aGreenByte, aBlueByte);
                    bByte      = (byte)bTexture.getMPixelRGB(iCol, iRow, bRedByte, bGreenByte, bBlueByte);
                    oRedByte   = (byte)Math.round((pfFraction * (float)aRedByte)   + (bFraction * (float)bRedByte));
                    oGreenByte = (byte)Math.round((pfFraction * (float)aGreenByte) + (bFraction * (float)bGreenByte));
                    oBlueByte  = (byte)Math.round((pfFraction * (float)aBlueByte)  + (bFraction * (float)bBlueByte));
                    oTexture.setMPixelRGB(iCol, iRow, oRedByte, oGreenByte, oBlueByte);
                    break;
                } // switch
            } // for iCol
        } // for iRow

        return 0;
    } // tweenMesh


    // Added as a wrapper for Beep
    public static void beep(int len1, int len2) {
        
    } // beep

    public static void setLblStatus(JLabel stat) {
        lblStatus = stat;
    } // setLblStatus
} // class Globals