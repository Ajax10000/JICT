package core;

import dialogs.ImageView;

import fileUtils.FileUtils;

import globals.Globals;
import globals.Preference;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.StringTokenizer;

import javax.swing.JComboBox;

import math.MathUtils;
import math.TMatrix;

import motion.MotionNode;
import motion.MotionPath;

import structs.Bundle;
import structs.Point3d;

/*  Project ict

Copyright � 1997 J. Wiley & Sons and Tim Wittenburg.  All Rights Reserved.

SUBSYSTEM: ict.exe Application
FILE:      sceneLst.cpp
AUTHOR:    Tim Wittenburg


	 OVERVIEW
	 ========
	 Implementation of classes sceneList, scene, and sceneElement.
*/

/*
#include "ict20.h"
#include "sceneLst.h"
#include "imageVw.h"
#include "prefrnce.h"
//#define ICTDEBUG 1
#define THREE_NUMBERS_NOT_FOUND 2


extern char g_msgText[MESSAGEMAX];

//int iRender(memImage *outImage, memImage *maskImage, memImage *inImage,
//  float rx, float ry, float rz, float sx, float sy, float sz,
//  float tx, float ty, float tz, tMatrix *viewMatrix,
//  int warpIndicator, int blendIndicator, float alphaScale,
//  float refPointX, float refPointY, float refPointZ);


extern preference *ictPreference;  // declare a global preference object
*/

public class SceneList implements ISceneList {
    boolean bIctDebug = false;
    public Scene sceneListHead;       // Points to the head of the list
    public Scene currentScene;        // Points to the current Scene
    public MemImage backgroundPlate;  // An optional background plate image

    // This was originally defined in ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f;

    // These were originally defined in SCENELST.H
    public static final int RED   = 1;
    public static final int GREEN = 2;
    public static final int BLUE  = 3;
    
    // These were originally defined in SCENELST.H
    public static final int MONOCHROME = 1;
    public static final int COLOR      = 2;

    // Effect Types
    // These were originally defined in ICT20.H
    public static final int STILL    = 1;
    public static final int SEQUENCE = 2;
    public static final int MORPH    = 3;

    // Model Types
    // These were originally defined in ICT20.H
    public static final int I_IMAGE       = 1;
    public static final int I_SHAPE       = 2;
    public static final int I_QUADMESH    = 3;
    public static final int I_COMPOUND    = 4;
    public static final int I_LIGHTSOURCE = 5; // not used in this file

    // These were originally defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;

    // These were originally defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 0;

    // This was originally defined in ICT20.H
    public static final float ZBUFFERMAXVALUE = 2.0E31f;

    // This was originally defined in SCENELST.H
    // Size of model array for depth sorting
    // Increase if > MAXMODELS are to be used in a scene
    public static final int MAXMODELS = 256;
    
    private static final int THREE_NUMBERS_NOT_FOUND = 2;

    public SceneList() {
        if (bIctDebug) {
            String msgBuffer;
            msgBuffer = "Constructor 1. Size of sceneList: " + sizeofLowerLimit();
            Globals.statusPrint(msgBuffer);
        }

        Point3d rt = new Point3d();
        Point3d sc = new Point3d();
        this.sceneListHead = new Scene("sceneList", 0, 0, 0, 0, rt, sc, " ");
        this.backgroundPlate = null;
        this.currentScene = null;
    } // SceneList ctor


    public void finalize() {
        if (bIctDebug) {
            String sMsgBuffer;
            Globals.statusPrint("SceneList destructor - deletes a Scenelist object");
            sMsgBuffer = "Size of SceneList: " + sizeofLowerLimit();
            Globals.statusPrint(sMsgBuffer);
        }

        clear();  // Clear the list of models
    } // finalize


    // Called from:
    //     ScenePreviewDlg.onSelChangeCmbModels
    public SceneElement setCurrentModel(String desiredModel) {
        Scene theScene = this.sceneListHead;
        SceneElement aModel;
        theScene = theScene.mNextEntry; // Point to the scene Node

        aModel = theScene.mHead;
        while (aModel != null) {
            if(aModel.msModelName.equals(desiredModel)) {
                theScene.mCurrentSceneElement = aModel;  // Indicate scene's current model
                return(aModel);
            }

            aModel = aModel.mNextEntry;  // point to the next model
        }

        return null;  // Model not found
    } // setCurrentModel


    // Called from:
    //     ScenePreviewDlg.onCmdPlus
    public void setCurrentModelTransform(float rx, float ry, float rz,
    float sx, float sy, float sz, 
    float tx, float ty, float tz) {
        Scene theScene = this.sceneListHead;
        SceneElement currentModel;
        theScene = theScene.mNextEntry; // Point to the scene Node
        currentModel = theScene.mCurrentSceneElement;

        currentModel.mRotation.x = rx;
        currentModel.mRotation.y = ry;
        currentModel.mRotation.z = rz;

        currentModel.mScale.x = sx;
        currentModel.mScale.y = sy;
        currentModel.mScale.z = sz;
        
        currentModel.mTranslation.x = tx;
        currentModel.mTranslation.y = ty;
        currentModel.mTranslation.z = tz;
    } // setCurrentModelTransform


    // Called from:
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onSelChangeCmbModels
    public void getCurrentModelTransform(Float rx, Float ry, Float rz,
    Float sx, Float sy, Float sz, 
    Float tx, Float ty, Float tz) {
        Scene theScene = this.sceneListHead;
        SceneElement currentModel;
        theScene = theScene.mNextEntry; // Point to the scene Node
        currentModel = theScene.mCurrentSceneElement;

        // Set the output parameters
        rx = currentModel.mRotation.x;
        ry = currentModel.mRotation.y;
        rz = currentModel.mRotation.z;

        sx = currentModel.mScale.x;
        sy = currentModel.mScale.y;
        sz = currentModel.mScale.z;

        tx = currentModel.mTranslation.x;
        ty = currentModel.mTranslation.y;
        tz = currentModel.mTranslation.z;
    } // getCurrentModelTransform


    // Not called from within this file.
    // Called from:
    //     ScnPreviewDlg.onInitDialog
    public void showModels(JComboBox<String> theCombo) {
        Scene theScene = this.sceneListHead;
        int numItems = theCombo.getItemCount(); // Clear the present contents ofthe comboBox
        for(int i = 1; i <= numItems; i++) {
            theCombo.setSelectedIndex(0);
            theCombo.removeAllItems();
        }

        theScene = theScene.mNextEntry;  //Skip over the list header
        theCombo.removeAllItems();
        SceneElement theModel = theScene.mHead;
        while (theModel != null) {
            if(!theModel.msFileName.equalsIgnoreCase("Output Image Rectangle")) {     //don't show the output image rectangle model
                theCombo.addItem(theModel.msModelName);
            }
            theModel = theModel.mNextEntry;
        }
    } // showModels


    // Called from:
    //     MainFrame.onToolsCreateASceneList
    public int listLength() {
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if(theScene == null) {
            return(0);
        }
        int iLength = 0;

        SceneElement theModel = theScene.mHead;
        while (theModel != null) {
            iLength++;
            theModel = theModel.mNextEntry;
        }
        return(iLength);
    } // listLength


    // Called from:
    //     preview
    //     previewStill
    //     render
    public int getSceneInfo(String psName, 
    Integer pIType, Integer pICMode, Integer pIOutRows, Integer piOutCols) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if (theScene == null) {
            return -1;
        }

        // Set the output parameters
        psName    = theScene.msSceneName;
        pIType    = theScene.miSequenceType;
        pICMode   = theScene.miColorMode;
        pIOutRows = theScene.miOutputRows;
        piOutCols = theScene.miOutputColumns;

        return 0;
    } // getSceneInfo


    // Called from:
    //     readList
    public int setSceneOutImageSize(int piOutRows, int piOutCols) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if (theScene == null) {
            return -1;
        }

        theScene.miOutputRows    = piOutRows;
        theScene.miOutputColumns = piOutCols;
        return 0;
    } // setSceneOutImageSize


    // Called from:
    //     depthSort
    public int getViewTransform(
    Float pFViewX,   Float pFViewY,   Float pFViewZ, 
    Float pFRotateX, Float pFRotateY, Float pFRotateZ) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if (theScene == null) { 
            return -1;
        }

        // Assume the default viewer location is centered in the output frame
        // Set the output parameters
        pFViewX = theScene.mTranslationPt.x;
        pFViewY = theScene.mTranslationPt.y;
        pFViewZ = theScene.mTranslationPt.z;

        pFRotateX = theScene.mRotationPt.x;
        pFRotateY = theScene.mRotationPt.y;
        pFRotateZ = theScene.mRotationPt.z;

        return 0;
    } // getViewTransform


    // Called from:
    //     ScenePreviewDlg.onOK
    public int setViewTransform(float pfViewX, float pfViewY, float pfViewZ,
    float pfRotateX, float pfRotateY, float pfRotateZ) {
        Scene theScene = sceneListHead;
        theScene = theScene.mNextEntry;  //Skip over the list header
        if (theScene == null) { 
            return -1;
        }

        theScene.mTranslationPt.x = pfViewX;
        theScene.mTranslationPt.y = pfViewY;
        theScene.mTranslationPt.z = pfViewZ;
        
        theScene.mRotationPt.x = pfRotateX;
        theScene.mRotationPt.y = pfRotateY;
        theScene.mRotationPt.z = pfRotateZ;

        return 0;
    } // setViewTransform


    // Called from:
    //     render
    public int getViewPoint(Float viewX, Float viewY, Float viewZ,
    Float rotateX, Float rotateY, Float rotateZ) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if (theScene == null) { 
            return -1;
        }

        // Set the output parameters
        // The default camera (viewpoint) location is centered on the origin
        // and translated 512 units along the positive z axis.
        // Add to this location any viewpoint translation and rotation 
        // the user specified.
        viewX =   0.0f + theScene.mTranslationPt.x;
        viewY =   0.0f + theScene.mTranslationPt.y;
        viewZ = 512.0f + theScene.mTranslationPt.z;

        // Since the default viewer rotations are (0,0,0), just output
        // whatever the user specified in the scene file.
        rotateX = 0.0f + theScene.mRotationPt.x;
        rotateY = 0.0f + theScene.mRotationPt.y;
        rotateZ = 0.0f + theScene.mRotationPt.z;

        return 0;
    } // getViewPoint


    // Writes the contents of the SceneList to a .scn file.
    // Called from:
    //     ScenePreviewDlg.onOK
    public int writeList(String psErrorText, String psFileName) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header

        File sceneFile = new File(psFileName);
        BufferedWriter fileOut = new BufferedWriter(new OutputStreamWriter(sceneFile));
        
        theScene.writeFile(fileOut); // Write out the scene description
        SceneElement theModel = theScene.mHead;
        boolean	oldCompoundMember = false;

        while (
        (theModel != null) && 
        !theModel.msFileName.equalsIgnoreCase("Output Image Rectangle")) {
            if ((theModel.mbCompoundModelMember == false) && (oldCompoundMember == true)) {
                String output = "End Compound Model" + "\n" + "\n";
                fileOut.write(output);
            }
            
            theModel.writeFile(fileOut); // Write out each model description
            oldCompoundMember = theModel.mbCompoundModelMember;
            theModel = theModel.mNextEntry;
        }
        
        fileOut.close();
        return 0;
    } // writeList


    // Not called from within this file.
    // Called from:
    //     ImageView.onDraw
    public int preview(HWND theWindow, TMatrix modelMatrix, TMatrix viewMatrix) {
        String msgText;
        int myStatus = 0;
        String sceneName, g_msgText;
        int effectType, colorMode;
        int outputRows, outputColumns;
        Integer firstFrame, lastFrame;
        int frameCounter;
        MotionNode aMotion;  // current model location, orientation if moving
        Bundle xfrm;         // create a bundle of transforms
        TMatrix viewModelMatrix;

        getSceneInfo(sceneName, effectType, colorMode, outputRows, outputColumns);

        // Setup for smooth animation.  Create a memory DC
        HBITMAP hBitmap, hOldBitmap;
        HDC memoryDC, dc;
        dc = GetDC(theWindow);
        MemImage tempImage = new MemImage(outputRows, outputColumns);
        hBitmap = CreateBitmap((int)outputColumns, (int)outputRows, 1,
            1, tempImage.getBytes());
        if(hBitmap == 0) {
            Globals.statusPrint("SceneList::PreviewSequence. Unable to create internal bitmap");
            return -1;
        }

        memoryDC = CreateCompatibleDC(dc);
        hOldBitmap = SelectObject(memoryDC, hBitmap);
        RECT myRect;
        SetRect(myRect, 0, 0, outputColumns, outputRows);

        // Preview the scene models
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if(theScene == null) {
            Globals.statusPrint("sceneList.previewSequence: Scene list has no models");
            return 0;
        }

        firstFrame = lastFrame = 0;
        if(effectType == SEQUENCE) {
            // The following method sets both firstFrame and lastFrame
            theScene.mSensorMotion.getFirstLastFrame(firstFrame, lastFrame);
        }
        int modelCounter;
        boolean eraseOldBoundary; // variable is set, but not read

        for(frameCounter = firstFrame; frameCounter <= lastFrame; frameCounter++) {
            // Clear the memoryDC by drawing a filled white rectangle
            FillRect(memoryDC, myRect, GetStockObject(WHITE_BRUSH));
            SceneElement theModel = theScene.mHead;
            modelCounter = 0;
            eraseOldBoundary = true;
            if(effectType == SEQUENCE) {
                // The following method sets viewMatrix
                getViewMatrix(viewMatrix, frameCounter, theScene);
            }

            while (theModel != null) {
                // If the renderObject has not been created, create it
                modelCounter++;
                if(theModel.mScreenRdrObject == null) {
                    theModel.miStatusIndicator = 0;
                    msgText = "PreviewSequence: Creating RenderObject: " + theModel.msModelName;
                    Globals.statusPrint(msgText);
                    theModel.mScreenRdrObject = new RenderObject(theModel.msFileName,
                        theModel.miModelType, theModel.mbDefinedRefPoint, theModel.pointOfReference);

                    if (!theModel.mScreenRdrObject.isValid()) {
                        theModel.miStatusIndicator = 1;  // this object could not be opened
                        theModel.mbValid = false;
                        msgText = "PreviewSequence: Couldn't create renderObject: " + theModel.msModelName;
                        Globals.statusPrint(msgText);
                        return -1;
                    }

                    // Get the scene's background plate if needed
                    if(modelCounter == 1 && 
                    theModel.mbWarpIndicator == false &&
                    theModel.mbBlendIndicator == false) {
                        if(backgroundPlate == null) {
                            backgroundPlate = new MemImage(theModel.msFileName, 0, 0, RANDOM, 'R', GREENCOLOR);
                        }
                    }
                }

                if(theModel.miStatusIndicator == 0) {  // If this is a valid model...
                    modelMatrix.setIdentity();

                    // Compose the model transforms
                    if(
                    (effectType == SEQUENCE) && 
                    (theModel.mModelMotion != null)) {
                        // The following method modifies aMotion (of type MotionNode)
                        theModel.mModelMotion.getNode(frameCounter, aMotion);
                    }

                    // The following method modifies xfrm (of type Bundle)
                    adjustTransforms(effectType, theModel, aMotion, xfrm);

                    modelMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                    float xRadians = xfrm.rx * F_DTR;
                    float yRadians = xfrm.ry * F_DTR;
                    float zRadians = xfrm.rz * F_DTR;

                    modelMatrix.rotate(xRadians, yRadians, zRadians);
                    modelMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);

                    // Combine the model matrix with the view Matrix
                    viewModelMatrix.multiply(viewMatrix, modelMatrix);

                    // Apply the matrix
                    theModel.mScreenRdrObject.transformAndProject(viewModelMatrix, outputRows, outputColumns);

                    // If this is a background plate, copy it to the screen
                    if(
                    modelCounter == 1 && 
                    theModel.mbWarpIndicator == false &&
                    theModel.mbBlendIndicator == false) {
                        HDC theDC = GetDC(theWindow);
                        backgroundPlate.display(theDC, outputColumns, outputRows);
                        ReleaseDC(theWindow, theDC);
                        eraseOldBoundary = false;
                    } else {
                        // Draw the object
                        theModel.mScreenRdrObject.drawSequence(memoryDC, theModel.msModelName, 
                            outputRows, outputColumns, frameCounter);
                    }
                } // end if valid screen object

                theModel = theModel.mNextEntry;
                BitBlt(dc, 0, 0, outputColumns, outputRows, memoryDC, 0, 0, SRCCOPY);
            }  // end of single frame 

            for(int i = 0; i <= 400000; i++)  {} // delay loop to approximate 30 Hz frame rate
        } // end of sequence 
      
        copyRefPoints();  // Copy model ref points to corresponding renderObjects
        // clean up the memoryDC
        SelectObject(memoryDC, hOldBitmap);
        /*
        DeleteDC(memoryDC);
        ReleaseDC(theWindow, dc);
        DeleteObject(hBitmap);
        */
        return myStatus;
    } // preview

 
    // Called from:
    //     ImageView.onDraw
    public int previewStill(HWND theWindow, TMatrix modelMatrix, TMatrix viewMatrix) {
        int myStatus = 0;
        String sceneName;
        int effectType, colorMode;
        int outputRows, outputColumns;
        int firstFrame, lastFrame, frameCounter;
        int modelCounter;
        boolean eraseOldBoundary; // variable is set, but not read
        Scene theScene;
        SceneElement theModel, saveModel;
        MotionNode aMotion;    // current model location, orientation if moving
        boolean firstTime = false;                                       
        Bundle xfrm;           // create a bundle of transforms;
        Bundle cxfrm;          // create a bundle of transforms for a potential compound model;
        TMatrix viewModelMatrix;
        TMatrix cModelMatrix;  // holds the compound model transformation matrix
        TMatrix tempMatrix;
              
        getSceneInfo(sceneName, effectType, colorMode, outputRows, outputColumns);
        int xOffset = outputColumns / 2;	  
        int yOffset = outputRows / 2;

        // Preview the scene models
        theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if(theScene == null) {
            Globals.statusPrint("sceneList::previewStill: Scene list has no models");
            return 0;
        }

        firstFrame = lastFrame = 0;
        if(effectType == SEQUENCE) {
            theScene.mSensorMotion.getFirstLastFrame(firstFrame, lastFrame);
        }
        eraseOldBoundary = false;

        for(frameCounter = firstFrame; frameCounter <= lastFrame; frameCounter++) {
            theModel = theScene.mHead;  // Point to the first model
            if(effectType == SEQUENCE) {
                getViewMatrix(viewMatrix, frameCounter, theScene);
            }
            modelCounter = 0;

            while (theModel != null) {
                // If the renderObject has not been created, create it
                modelCounter++;
                if(theModel.mScreenRdrObject == null) {
                    theModel.miStatusIndicator = 0;
                    theModel.mScreenRdrObject = new RenderObject(theModel.msFileName,
                        theModel.miModelType, theModel.mbDefinedRefPoint, theModel.pointOfReference);
                    if(theModel.miModelType == I_COMPOUND) {	// Initialize a compound model centroid
                        theModel.pointOfReference.x = 0.0f;
                        theModel.pointOfReference.y = 0.0f;
                        theModel.pointOfReference.z = 0.0f;
                    }

                    firstTime = true;  // This variable used to create the output image rectangle
                    if (!theModel.mScreenRdrObject.isValid()) {
                        theModel.miStatusIndicator = 1;  // This object could not be opened
                        String msgText = "previewStill: Could not create renderObject: " + theModel.msModelName;
                        Globals.statusPrint(msgText);
                        Globals.beep(10, 10);
                        return -1;
                    }
                }

                // Setup the scene's background plate if needed
                if(modelCounter == 1 && 
                theModel.mbWarpIndicator == false &&
                theModel.mbBlendIndicator == false && theModel.miModelType != I_COMPOUND) {
                    if(backgroundPlate == null) {
                        backgroundPlate = new MemImage(theModel.msFileName, 0, 0, RANDOM, 'R', GREENCOLOR);
                    }
                }

                if(theModel.miStatusIndicator == 0) {  // if this is a valid model...
                    float cmCentroidx, cmCentroidy, cmCentroidz;
                    modelMatrix.setIdentity();

                    // Compose the appropriate transforms
                    if((effectType == SEQUENCE) && (theModel.mModelMotion != null)) {
                        theModel.mModelMotion.getNode(frameCounter, aMotion);
                    }
                
                    if(theModel.miModelType == I_COMPOUND) {
                        adjustTransforms(effectType, theModel, aMotion, cxfrm);
                        cModelMatrix.scale(cxfrm.sx, cxfrm.sy, cxfrm.sz);
                        float xRadians = cxfrm.rx * F_DTR;
                        float yRadians = cxfrm.ry * F_DTR;
                        float zRadians = cxfrm.rz * F_DTR;
                        cModelMatrix.rotate(xRadians, yRadians, zRadians);
                        cModelMatrix.translate(cxfrm.tx, cxfrm.ty, cxfrm.tz);
                    } else {
                        adjustTransforms(effectType, theModel, aMotion, xfrm);
                        modelMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                        float xRadians = xfrm.rx * F_DTR;
                        float yRadians = xfrm.ry * F_DTR;
                        float zRadians = xfrm.rz * F_DTR;
                        modelMatrix.rotate(xRadians, yRadians, zRadians);
                        modelMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);
                    }
                    
                    // Combine the model matrix with the view Matrix
                    if(theModel.mbCompoundModelMember) {
                        tempMatrix.setIdentity();
                        tempMatrix.multiply(viewMatrix, modelMatrix);
                        viewModelMatrix.multiply(cModelMatrix, tempMatrix);
                    } else {
                        // Combine the model matrix with the view Matrix
                        viewModelMatrix.multiply(viewMatrix, modelMatrix);
                    }

                    // If this model is compound, calculate the compound model reference Point
                    if(theModel.miModelType == I_COMPOUND) {
                        saveModel = theModel;
                        calcCompoundModelRefPoint(theModel, outputRows, outputColumns, cmCentroidx, cmCentroidy, cmCentroidz);
                        theModel = saveModel;
                    }

                    //  Transform the points
                    if(theModel.msFileName.equalsIgnoreCase("Output Image Rectangle") && 
                    theModel.miModelType != I_COMPOUND &&	     
                    theModel.mbCompoundModelMember == false) {    
                        theModel.mScreenRdrObject.transformAndProject(viewModelMatrix, outputRows, outputColumns);
                    }

                    if(
                    theModel.msFileName.equalsIgnoreCase("Output Image Rectangle") && 
                    theModel.miModelType != I_COMPOUND &&
                    theModel.mbCompoundModelMember) {   
                        theModel.mScreenRdrObject.transformAndProject(viewModelMatrix,
                            outputRows, outputColumns, theModel.mbCompoundModelMember, cmCentroidx, cmCentroidy, cmCentroidz);
                    }

                    // Draw the points
                    //
                    // If this is a background plate, blt it to the screen
                    if(
                    modelCounter == 1 && 
                    theModel.miModelType != I_COMPOUND && 
                    theModel.mbWarpIndicator == false &&
                    theModel.mbBlendIndicator == false) {
                        HDC theDC = GetDC(theWindow);
                        backgroundPlate.display(theDC, outputColumns, outputRows);
                        ReleaseDC(theWindow, theDC);
                        eraseOldBoundary = false;
                    } else {
                        // Draw the model boundary
                        if(theModel.miModelType != I_COMPOUND) {
                            theModel.mScreenRdrObject.drawStill(theWindow, theModel.msModelName, outputRows, outputColumns);
                        }
                    }
                } // end if valid screen object

                saveModel = theModel;
                theModel = theModel.mNextEntry;
                if((theModel == null) && (firstTime == true)) {
                    // This is the last model.  If the first time through this loop, add
                    // a model that displays the output image rectangle
                    Color aColor;
                    myStatus = addSceneElement(" ", "Output Image Rectangle",  
                        false, I_SHAPE, false, 
                        1.0f, null, null, null, 
                        "None", "Default", 
                        false, aColor, "None", "None", 
                        false, null);
                    theModel = saveModel.mNextEntry;
                    if(theModel != null) {
                        theModel.mScreenRdrObject = new RenderObject(theModel.msFileName,
                            theModel.miModelType, theModel.mbDefinedRefPoint, theModel.pointOfReference);
                    }
                
                    theModel = theModel.mNextEntry;
                }
            }
        }  // frame
        
        copyRefPoints();  // Copy model ref points to corresponding renderObjects
        return myStatus;
    } // previewStill


    // Called from:
    //     MainFrame.onRenderScene
    //     MainFrame.onRenderSequence
    public int render(ImageView displayWindow, TMatrix viewMatrix,
    boolean depthSortingEnabled, boolean zBufferEnabled, boolean antiAliasEnabled, 
    boolean hazeFogEnabled) {
        String outputFileName, sceneName;
        String redFileName, greenFileName, blueFileName, RGBFileName, currentColor;
        int effectType, colorMode;
        int outputRows, outputColumns;
        int firstFrame, lastFrame, frameCounter;
        Float vx = 0.0f, vy = 0.0f, vz = 0.0f;    // Viewpoint
        Float vrx = 0.0f, vry = 0.0f, vrz = 0.0f;       
        MotionNode aMotion;                    // Current model location and orientation if moving.
        Bundle xfrm;           // Create a bundle of transforms
        Instant timeStart, timeEnd;
        timeStart = Instant.now();
        TMatrix forwardMatrix, viewModelMatrix;

        getSceneInfo(sceneName, effectType, colorMode, outputRows, outputColumns);
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;        // Skip over the list header
        if(theScene == null) {
            Globals.statusPrint("sceneList::render: Scene list has no models");
            return -1;
        }

        // The following method sets vx, vy, vz, 
        // and vrx, vry, and vrz as well
        getViewPoint(vx, vy, vz, vrx, vry, vrz);
        SceneElement theModel = theScene.mHead;
        SceneElement[] models = new SceneElement[MAXMODELS];
        float[] distances = new float[MAXMODELS];
        Integer numModels; 
        int myStatus;
        firstFrame = lastFrame = 0;

        // Open the zBuffer if necessary
        MemImage aliasImage, zBuffer;
        zBuffer = null;
        if(zBufferEnabled) {
            zBuffer = new MemImage(outputRows, outputColumns, 32);
            if (!zBuffer.isValid()) {
                Globals.statusPrint("sceneList::render: Not enough memory to open Z Buffer");
                return -1;
            }

            // Initialize the zBuffer
            zBuffer.init32(ZBUFFERMAXVALUE); 
        }

        if(effectType == SEQUENCE) {
            // The following method sets Integers firstFrame and lastFrame
            theScene.mSensorMotion.getFirstLastFrame(firstFrame, lastFrame);
        }

        MemImage alphaImage, zImage;
        alphaImage = null;
        zImage = null;

        for(frameCounter = firstFrame; frameCounter <= lastFrame; frameCounter++) {
            // Depth Sort the models
            depthSort(models, distances, numModels, depthSortingEnabled);
            if(effectType == SEQUENCE) {
                getViewMatrix(viewMatrix, frameCounter, theScene);
            }

            // Setup the Color Mode
            int firstColor = GREEN;
            int lastColor = GREEN;
            if (colorMode == COLOR) {
                firstColor = RED;
                lastColor = BLUE;
            }

            for (int theColor = firstColor; theColor <= lastColor; theColor++) {
                MemImage outputImage = new MemImage(outputRows, outputColumns);
                if (outputImage == null) {
                    String msgText;
                    Globals.statusPrint("sceneList::render: Not enough memory to open output image");
                    return -1;
                }

                // Loop through each model in the scene list
                for(int currentModel = 0; currentModel <= numModels - 1; currentModel++) {
                    theModel = models[currentModel];
                    if (theModel.miStatusIndicator == 0) {
                        // Use the projected cornerpoints in the renderObject to
                        // determine the warp coefficients.
                        // This approach imposes the reasonable requirement that the user preview
                        // the scene before rendering it.
                        if(theModel.mScreenRdrObject == null) {
                            String msgText;
                            msgText = "sceneList::render: RenderObject not defined. Skipping model: " +
                                theModel.msModelName;
                            Globals.statusPrint(msgText);
                            break;
                        }

                        if(theColor == RED)   currentColor = "Red";
                        if(theColor == GREEN) currentColor = "Green";
                        if(theColor == BLUE)  currentColor = "Blue";

                        String msgText = "Processing Frame: " + frameCounter + " Color: " + currentColor + 
                            "  Model: " + theModel.msModelName;
                        Globals.statusPrint(msgText);
                        MemImage inputImage;
                        inputImage = null;
                        alphaImage = null;
                        zImage = null;

                        // Open the input image, if the image has been color adjusted, open the adjusted image
                        String theInputPath;
                        if(!theModel.msAdjustmentType.equalsIgnoreCase("None")) {
                            theInputPath = theModel.msColorAdjustedPath;
                        } else {
                            theInputPath = theModel.msFileName;
                        }

                        if(theModel.miModelType == SEQUENCE) {
                            getSequenceFileName(theInputPath, frameCounter);
                        }

                        // Open the model's image if appropriate
                        if(theModel.miModelType != I_SHAPE) {
                            inputImage = new MemImage(theInputPath, 0, 0, RANDOM, 'R', theColor);
                            if (!inputImage.isValid()) {
                                msgText = "sceneList.Render: Can't open image: " + theModel.msFileName;
                                Globals.statusPrint(msgText);
                                return -1;
                            }
                        }

                        if(theModel.mbBlendIndicator) {
                            // Open the alpha image. 
                            // If an alpha image pathname was specified 
                            // in the scene file, use it. Otherwise set it to NULL.  In this case
                            // function iRenderz will create the alphaImage from the warped image.
                            String alphaName;
                            alphaImage = null;
                            if(theModel.miModelType == I_IMAGE) {
                                if(!theModel.msAlphaPath.equalsIgnoreCase("NONE")) {
                                    alphaName = theModel.msAlphaPath;
                                    alphaImage = new MemImage(alphaName, 0, 0, RANDOM, 'R', EIGHTBITMONOCHROME);
                                    if (!alphaImage.isValid()) {
                                        Globals.statusPrint("sceneList::Render. Can't open the custom alpha image");
                                    }
                                }
                            }
                        }

                        // Optionally open the z Image;
                        String zName;
                        if((zBufferEnabled) && (zImage == null)) {
                            zImage = new MemImage(outputRows, outputColumns, 32);
                            if (!zImage.isValid()) {
                                Globals.statusPrint("sceneList::render: Not enough memory to open Z Image");
                                return -1;
                            } 
                            zImage.setFileName("zImage");
                            zImage.init32(ZBUFFERMAXVALUE); 
                        }

                        // Get the desired transforms from either the model or the motion file
                        // Copy them to the xfrm object
                        if((effectType == SEQUENCE) && (theModel.mModelMotion != null)) {
                            theModel.mModelMotion.getNode(frameCounter, aMotion);
                        }

                        adjustTransforms(effectType, theModel, aMotion, xfrm);

                        // Properly render the model, based on model type and other options
                        if(zBufferEnabled) {
                            switch(theModel.miModelType) {
                            case I_IMAGE:
                            // case SEQUENCE: //this was causing a duplicate case error (duplicate with SHAPE)
                                myStatus = Globals.iRenderz(outputImage, alphaImage, inputImage,
                                    zImage, zBuffer,
                                    xfrm.rx, xfrm.ry, xfrm.rz, xfrm.sx, xfrm.sy, xfrm.sz,
                                    xfrm.tx, xfrm.ty, xfrm.tz, vx, vy, vz,
                                    viewMatrix,
                                    theModel.mbWarpIndicator, theModel.mbBlendIndicator, xfrm.alpha,
                                    theModel.pointOfReference.x,
                                    theModel.pointOfReference.y,
                                    theModel.pointOfReference.z);
                                break;

                            case I_QUADMESH:
                                // Render the mesh.  The result is a zImage, and a temporary output image
                                // The rendered mesh is then blended into the final image using the scene ZBuffer and alpha options
                                myStatus = 0;	//this line for debugging
                                myStatus = theModel.mScreenRdrObject.renderMeshz(outputImage, alphaImage, inputImage,
                                    zBuffer, vx, vy, vz);
                                break;

                            case I_SHAPE:
                                // Build the transformation matrix
                                float XRadians = xfrm.rx * F_DTR;
                                float YRadians = xfrm.ry * F_DTR;
                                float ZRadians = xfrm.rz * F_DTR;
                                forwardMatrix.setIdentity();
                                forwardMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                                forwardMatrix.rotate(XRadians, YRadians, ZRadians);
                                forwardMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);
                                viewModelMatrix.multiply(viewMatrix, forwardMatrix);
                                viewModelMatrix.transformAndProject(theModel.mScreenRdrObject.currentShape,
                                    outputRows, outputColumns, true, 		   
                                    theModel.pointOfReference.x,
                                    theModel.pointOfReference.y,
                                    theModel.pointOfReference.z);

                                myStatus = theModel.mScreenRdrObject.renderShapez(outputImage, alphaImage,
                                    zBuffer, vx, vy, vz);
                                break;
                            }
                        } else {            //no zbuffer
                            switch(theModel.miModelType) {
                            case I_IMAGE:
                            // case SEQUENCE: // this was causing a duplicate case error (duplicate with SHAPE)
                                myStatus = Globals.iRenderz(outputImage, alphaImage, inputImage,
                                    zImage,  zBuffer,
                                    xfrm.rx, xfrm.ry, xfrm.rz, 
                                    xfrm.sx, xfrm.sy, xfrm.sz,
                                    xfrm.tx, xfrm.ty, xfrm.tz, 
                                    vx,      vy,      vz,
                                    viewMatrix,
                                    theModel.mbWarpIndicator, theModel.mbBlendIndicator, xfrm.alpha,
                                    theModel.pointOfReference.x,
                                    theModel.pointOfReference.y,
                                    theModel.pointOfReference.z);
                                break;
    
                            case I_QUADMESH:
                                myStatus = theModel.mScreenRdrObject.renderMesh(outputImage, inputImage,
                                    theModel.mbBlendIndicator);
                                break;

                            case I_SHAPE:
                                myStatus = theModel.mScreenRdrObject.renderShape(outputImage,
                                    theModel.mbBlendIndicator);
                                break;
                            } // end switch
                        } // end else (z buffer)


                        // Processing of one model is complete
                        // Update the display
                        HWND hwnd = displayWindow.getImageWindowHandle();
                        HDC theDC = GetDC(hwnd);
                        outputImage.display(theDC, outputColumns, outputRows);
                        ReleaseDC(hwnd, theDC);
                    } // if theModel.statusIndicator == 0
                } // for currentModel

                // One color channel of the scene is complete! Save it in the proper location
                String outputDir, outputPath;
                getFileName(outputFileName, theScene.msSceneName, frameCounter, theColor);
                outputDir = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
                outputPath = outputDir + outputFileName;

                if (theColor == RED)   redFileName   = outputPath;
                if (theColor == GREEN) greenFileName = outputPath;
                if (theColor == BLUE)  blueFileName  = outputPath;

                // Optionally anti-alias the output image
                if(antiAliasEnabled) {
                    aliasImage = new MemImage(outputRows, outputColumns);
                    FileUtils.appendFileName(outputFileName, RGBFileName, currentColor);
                    Globals.antiAlias(outputImage, aliasImage);
                    aliasImage.copy(outputImage, 0, 0);
                }

                String msgText;
                msgText = "Saving output image: " + outputPath;
                Globals.statusPrint(msgText);
                outputImage.writeBMP(outputPath);

                // Re-initialize the zBuffer
                if(zBufferEnabled) {
                    zBuffer.init32(ZBUFFERMAXVALUE);
                }
            } // for theColor

            // Combine the color channels together
            myStatus = 0;
            if(colorMode == COLOR) {
                // Prepare a pathname to the default output image location
                getFileName(RGBFileName, theScene.msSceneName, frameCounter, 0);
                String RGBPath, RGBDir;
                RGBDir = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
                RGBPath = RGBDir + RGBFileName;

                String msgText = "sceneList::render: Saving RGB image: " + RGBPath;
                Globals.statusPrint(msgText);
                myStatus = Globals.makeRGBimage(redFileName, greenFileName, blueFileName, RGBPath);
            }
        }  // End of Sequence Loop
    
        timeEnd = Instant.now();
        Duration timeDiff = Duration.between(timeStart, timeEnd);

        String msgText = "Scene Generation Complete.  " + timeDiff.toMillis() + " milliseconds.";
        Globals.statusPrint(msgText);
        return myStatus;
    } // render


    // Called from:
    //     render
    public void getSequenceFileName(String psTheInputPath, int piFrameCounter) {
        String sTempPath;
        sTempPath = psTheInputPath;
        String sDrive, sDir, sFile, sExt;

        _splitpath(sTempPath, sDrive, sDir, sFile, sExt);
        
        int theLength = sFile.length();
        int iCurrentFrameNum, iNewFrameNum;
        if(theLength > 0) {
            String sFrameNum = sFile.substring(theLength - 4);
            iCurrentFrameNum = Integer.parseInt(sFrameNum);
        }

        iNewFrameNum = iCurrentFrameNum + piFrameCounter;
        if(iNewFrameNum > 9999) {
            iNewFrameNum = 9999;
            Globals.statusPrint("getSequenceFileName: Frame counter exceeded 9999.");
        }
        String sPrefix, sOutFile;
        sPrefix = sFile.substring(theLength - 4);
        char colorChar = sFile.charAt(theLength - 1);

        sprintf(sOutFile, "%.16s%#04d%c\0", sPrefix, iNewFrameNum, colorChar);
        _makepath(psTheInputPath, sDrive, sDir, sOutFile, sExt);
    } // getSequenceFileName


    // Called from:
    //     render
    public void getFileName(String psOutputFileName, String psPrefix, 
    int piCounter, int piTheColor) {
        char cColorChar;

        if (piTheColor == RED)   cColorChar = 'r';
        if (piTheColor == GREEN) cColorChar = 'g';
        if (piTheColor == BLUE)  cColorChar = 'b';
        if (piTheColor == 0)     cColorChar = 'c';
        sprintf(psOutputFileName, "%.16s%#04d%c.bmp\0", psPrefix, piCounter, cColorChar);
    } // getFileName


    // Called from:
    //     preview
    //     previewStill
    //     render
    public void adjustTransforms(int piEffectType, SceneElement theModel, 
    MotionNode aMotion, Bundle xfrm) {
        // float viewX, viewY, viewZ, rotateX, rotateY, rotateZ; // these local variables are not used

        // Copy model transforms into a bundle object
        if((piEffectType == SEQUENCE) && (theModel.mModelMotion != null)) {
            // Set output parameter xfrm
            xfrm.rx = aMotion.mfRx;
            xfrm.ry = aMotion.mfRy;
            xfrm.rz = aMotion.mfRz;

            xfrm.sx = aMotion.mfSx;
            xfrm.sy = aMotion.mfSy;
            xfrm.sz = aMotion.mfSz;

            xfrm.tx = aMotion.mfTx;
            xfrm.ty = aMotion.mfTy;
            xfrm.tz = aMotion.mfTz;
            xfrm.alpha = aMotion.mfAlpha;
        } else {
            // Set output parameter xfrm
            xfrm.rx = theModel.mRotation.x;
            xfrm.ry = theModel.mRotation.y;
            xfrm.rz = theModel.mRotation.z;

            xfrm.sx = theModel.mScale.x;
            xfrm.sy = theModel.mScale.y;
            xfrm.sz = theModel.mScale.z;

            xfrm.tx = theModel.mTranslation.x;
            xfrm.ty = theModel.mTranslation.y;
            xfrm.tz = theModel.mTranslation.z;
            xfrm.alpha = theModel.mfAlphaScale;
        }
    } // adjustTransforms


    // This method sets parameter pViewMatrix.
    // MainFrame also has a getViewMatrix method, but it takes a single parameter of 
    // type TMatrix.
    //
    // Called from:
    //     preview
    //     previewStill
    //     render
    public void getViewMatrix(TMatrix pViewMatrix, int piFrameCounter, Scene pTheScene) {
        MotionNode aMotion = new MotionNode();
        pViewMatrix.setIdentity();
        float xRadians, yRadians, zRadians;
        // Note: F_DTR is a floating point constant, 
        // a degree to radians conversion factor

        if(pTheScene.mSensorMotion != null) {
            pTheScene.mSensorMotion.getNode(piFrameCounter, aMotion);
            xRadians = aMotion.mfRx * F_DTR;
            yRadians = aMotion.mfRy * F_DTR;
            zRadians = aMotion.mfRz * F_DTR;
        } else {
            xRadians = pTheScene.mRotationPt.x * F_DTR;
            yRadians = pTheScene.mRotationPt.y * F_DTR;
            zRadians = pTheScene.mRotationPt.z * F_DTR;
        }

        pViewMatrix.rotate(-xRadians, -yRadians, -zRadians);
        if(pTheScene.mSensorMotion != null) {
            pViewMatrix.translate(-aMotion.mfTx, -aMotion.mfTy, -aMotion.mfTz);
        } else {
            pViewMatrix.translate(-pTheScene.mTranslationPt.x, -pTheScene.mTranslationPt.y, -pTheScene.mTranslationPt.z);
        }
    } // getViewMatrix


    // Called from:
    //     previewStill
    public int calcCompoundModelRefPoint(SceneElement theModel, 
    int outputRows, int outputColumns, 
    Float cmCentroidX, Float cmCentroidY, Float cmCentroidZ) {
        cmCentroidX = 0.0f;
        cmCentroidY = 0.0f;
        cmCentroidZ = 0.0f;
        // SceneElement saveModel; // this variable is not used
        float bucketX = 0f, bucketY = 0f, bucketZ = 0f;
        Float mCentroidX = 0f, mCentroidY = 0f, mCentroidZ = 0f;
        boolean prevModelIsACompoundMember = false; // changed from int to boolean
        int modelCounter = 0;
        TMatrix modelMatrix = new TMatrix();
        
        // theModel is assumed to point to the compound model object.
        //
        // Each compound model component is transformed in order to get its centroid
        // These centroids are accumulated and averaged to obtain the centroid of the 
        // compound model.
        // saveModel = theModel; // this variable is not used
        theModel = theModel.mNextEntry;

        while (theModel != null) {
            // Build the model's transformation matrix
            modelMatrix.scale(theModel.mScale.x, theModel.mScale.y, theModel.mScale.z);
            float xRadians = theModel.mRotation.x * F_DTR;
            float yRadians = theModel.mRotation.y * F_DTR;
            float zRadians = theModel.mRotation.z * F_DTR;
            modelMatrix.rotate(xRadians, yRadians, zRadians);
            modelMatrix.translate(theModel.mTranslation.x, theModel.mTranslation.y, theModel.mTranslation.z);

            // If the model's RenderObject has not been created, create it.
            if(theModel.mScreenRdrObject == null) {
                theModel.miStatusIndicator = 0;
                theModel.mScreenRdrObject = new RenderObject(theModel.msFileName,
                    theModel.miModelType, theModel.mbDefinedRefPoint, theModel.pointOfReference);

                if (!theModel.mScreenRdrObject.isValid()) {
                    theModel.miStatusIndicator = 1;  // this object could not be opened
                    String msgText = "calcCompoundModelRefPoint: Could not create renderObject: " + theModel.msModelName;
                    Globals.statusPrint(msgText);
                    return -1;
                }
            }

            // Transform the model and get its transformed centroid
            if(theModel.mbCompoundModelMember) {
                // Transform the individual model
                theModel.mScreenRdrObject.transformAndProject(modelMatrix, outputRows, outputColumns);
                theModel.mScreenRdrObject.currentShape.getTCentroid(mCentroidX, mCentroidY, mCentroidZ);
      
                String msgText;
                msgText = "calcCmModelRefPoint. model: " + theModel.msModelName;
                Globals.statusPrint(msgText);
                msgText = "calcCmModelRefPoint. modelCentroid: " + mCentroidX + " " + mCentroidY + " " + mCentroidZ;
                Globals.statusPrint(msgText);

                bucketX += mCentroidX;
                bucketY += mCentroidY;
                bucketZ += mCentroidZ;
                modelCounter++;
            }

            if(!theModel.mbCompoundModelMember && prevModelIsACompoundMember) {
                // Set the output parameters
                cmCentroidX = bucketX / modelCounter;
                cmCentroidY = bucketY / modelCounter;
                cmCentroidZ = bucketZ / modelCounter;

                String msgText = "calcCmModelRefPoint. cmCentroid: " + cmCentroidX + " " + cmCentroidY + " " + cmCentroidZ;
                Globals.statusPrint(msgText);
            }

            prevModelIsACompoundMember = theModel.mbCompoundModelMember;
            theModel = theModel.mNextEntry;  // Get the pointer to next model
        } // while

        // Handle the case where a compound model is the last model in the
        // scene list.
        if(prevModelIsACompoundMember) {
            // Set the output parameters
            cmCentroidX = bucketX / modelCounter;
            cmCentroidY = bucketY / modelCounter;
            cmCentroidZ = bucketZ / modelCounter;
        }

        return 0;
    } // calcCompoundModelRefPoint


    // Called from:
    //     readList
    public int addScene(String theSceneName, int theType, 
    int outImCols, int outImRows, int theColorMode, 
    Point3d rt, Point3d tr, String thePath) {
        int status = 0;
        Scene newScene = new Scene(theSceneName, theType, outImCols, outImRows,
            theColorMode, rt, tr, thePath);
        if (!newScene.isValid()) {
            status = 1;
        }

        sceneListHead.mNextEntry = newScene;
        newScene.mPrevEntry = this.sceneListHead;
        this.currentScene = newScene;  // Make the new scene the Current scene
        return status;
    } // addScene


    // Called from:
    //     previewStill
    //     readList
    public int addSceneElement(String mdName, String fName, boolean blendI,
    int theType, boolean warpI, float aScale, 
    Point3d rt, Point3d sc, Point3d tr, 
    String motionPath, String theAlphaPath,
    boolean theSortLayer, Color anAdjustment, 
    String adjustmentType, String colorAdjustedPath,
    boolean definedRefPt, Point3d refPoint) {
        Scene aScene = currentScene;  //Add an element to the current scene
        SceneElement aModel = aScene.mHead;
        int myStatus = 0;

        SceneElement theModel = new SceneElement(mdName, fName, blendI, 
            theType, warpI, aScale, 
            rt, sc, tr, 
            motionPath, theAlphaPath, 
            theSortLayer, anAdjustment, 
            adjustmentType, colorAdjustedPath,
            definedRefPt, refPoint);

        if(!theModel.isValid()) {
            return 1;
        }

        if(aScene.mHead == null) {
            aScene.mHead = theModel;
            theModel.mPrevEntry = null;
            theModel.mNextEntry = null;
        } else {
            aModel = aScene.mHead;  // Find the last element
            while (aModel.mNextEntry != null) {
                aModel = aModel.mNextEntry;
            }
            aModel.mNextEntry = theModel;
            theModel.mNextEntry = aModel;
        }

        aScene.mTail = theModel;
        return myStatus;
    } // addSceneElement


    public void display() {
        Scene aScene = sceneListHead;
        Scene currentScene;
        SceneElement model;

        currentScene = aScene.mNextEntry;
        while (currentScene != null) {
            currentScene.display(); // Scene Display
            model = currentScene.mHead;
            model.display();  // Model Display
            currentScene = currentScene.mNextEntry;
        }
    } // display


    // Called from:
    //     finalize
    public void clear() {
        Scene aScene = this.sceneListHead;
        SceneElement theModel, nextModel;

        if(aScene.mNextEntry == null) {
            return; // Don't clear an empty list
        }

        aScene = aScene.mNextEntry;
        theModel = aScene.mHead;

        while (theModel != null) {
            nextModel = theModel.mNextEntry;  // Get the pointer to next model
            theModel = null;                 // Before deleting current model
            theModel = nextModel;
        }

        aScene = this.sceneListHead;
        aScene.mNextEntry = null;

        // Reset the background plate
        if(backgroundPlate != null) {
            backgroundPlate = null;
        }
    } // clear


    // Called from:
    //     setCompoundRefPoints
    public int setModelReferencePoint(String psModelName, 
    float pfCentroidX, float pfCentroidY, float pfCentroidZ) {
        Scene aScene = this.sceneListHead;
        SceneElement theModel;
        String msgText;

        if(aScene.mNextEntry == null) {
            Globals.statusPrint("setModelReferencePoint: sceneList has no scene object.");
            return -2; 
        }

        aScene = aScene.mNextEntry;
        theModel = aScene.mHead;
        boolean found = false;

        while (theModel != null) {
            if(psModelName.equalsIgnoreCase(theModel.msModelName)) {
                theModel.pointOfReference.x = pfCentroidX;
                theModel.pointOfReference.y = pfCentroidY;
                theModel.pointOfReference.z = pfCentroidZ;
                found = true;
            }
            theModel = theModel.mNextEntry;  // Get the pointer to next model
        } // while

        if(!found) {
            msgText = "setModelReferencePoint: Model Not Found: " + psModelName;
            Globals.statusPrint(msgText);
            return -1;
        }

        return 0;
    } // setModelReferencePoint


    // Not called from within this file
    public int setCompoundRefPoints() {
        Scene aScene = this.sceneListHead;
        SceneElement theModel;
        String modelName = "";
        float bucketX = 0.0f, bucketY = 0.0f, bucketZ = 0.0f;
        float centroidX, centroidY, centroidZ;

        if(aScene.mNextEntry == null) {
            Globals.statusPrint("setCompoundRefPoints: sceneList has no scene object.");
            return -2; 
        }

        aScene = aScene.mNextEntry;
        theModel = aScene.mHead;
        // int found = FALSE; // this variable is not used
        int modelCounter = 0;
        boolean prevModelIsACompoundMember = false;

        while (theModel != null) {
            if(theModel.miModelType == I_COMPOUND) {
                modelName = theModel.msModelName;
                bucketX = 0.0f;
                bucketY = 0.0f;
                bucketZ = 0.0f;
                modelCounter = 0;
            }

            if(theModel.mbCompoundModelMember) {
                bucketX += theModel.pointOfReference.x;
                bucketY += theModel.pointOfReference.y;
                bucketZ += theModel.pointOfReference.z;
                modelCounter++;
            }

            if((!theModel.mbCompoundModelMember) && (prevModelIsACompoundMember)) {
                centroidX = bucketX / modelCounter;
                centroidY = bucketY / modelCounter;
                centroidZ = bucketZ / modelCounter;
                setModelReferencePoint(modelName, centroidX, centroidY, centroidZ);
            }

            prevModelIsACompoundMember = theModel.mbCompoundModelMember;
            theModel = theModel.mNextEntry;  // Get the pointer to next model
        } 

        // Handle the case where a compound model is the last model in the
        // scene list.
        if(prevModelIsACompoundMember) {
            centroidX = bucketX / modelCounter;
            centroidY = bucketY / modelCounter;
            centroidZ = bucketZ / modelCounter;
            setModelReferencePoint(modelName, centroidX, centroidY, centroidZ);
        }

        return 0;
    } // setCompoundRefPoints


    // Called from:
    //     preview
    //     previewStill
    public int copyRefPoints() {
        // Copies reference points into a model from its corresponding
        // renderObject
        Scene aScene = this.sceneListHead;
        SceneElement theModel;
        Float centroidX = 0.0f, centroidY = 0.0f, centroidZ = 0.0f;

        if(aScene.mNextEntry == null) {
            Globals.statusPrint("copyRefPoints: sceneList has no scene object.");
            return -2; 
        }

        aScene = aScene.mNextEntry;
        theModel = aScene.mHead;
        while (theModel != null) {
            if((!theModel.mbDefinedRefPoint) && (theModel.miModelType != I_COMPOUND)) {
                // The following method sets centroidX, centroidY, and centroidZ (all of type Float)
                theModel.mScreenRdrObject.currentShape.getReferencePoint(centroidX, centroidY, centroidZ);

                theModel.pointOfReference.x = centroidX; 
                theModel.pointOfReference.y = centroidY;
                theModel.pointOfReference.z = centroidZ;
            }

            theModel = theModel.mNextEntry;  // Get the pointer to next model
        } // while theModel != null

        return 0;
    } // copyRefPoints


    // This method came from DEPTHSRT.CPP
    // Called from:
    //     render
    public int depthSort(SceneElement[] models, float[] distances,
    Integer numModels, boolean depthSortingEnabled) {
        Float viewX = 0f, viewY = 0f, viewZ = 0f;
        Float rotateX = 0f, rotateY = 0f, rotateZ = 0f;
        float centroidX, centroidY, centroidZ;
        float modelDistance;

        getViewTransform(viewX, viewY, viewZ, rotateX, rotateY, rotateZ);

        // Preview the Scene Models
        Scene theScene = this.sceneListHead;
        theScene = theScene.mNextEntry;  // Skip over the list header
        if(theScene == null) {
            return -1;
        }

        SceneElement theModel = theScene.mHead;
        int modelCounter = 0;

        while (theModel != null) {
            if(
            modelCounter == 0 && 
            theModel.mbWarpIndicator == false &&
            theModel.mbBlendIndicator == false) {
                distances[modelCounter] = 999999999.9f;  // set the distance of the backdrop image
                models[modelCounter] = theModel;
                modelCounter++;
            } else {
                if(theModel.miModelType == I_IMAGE || theModel.miModelType == I_SHAPE) {
                    centroidX = theModel.mScreenRdrObject.currentShape.mfOriginX;
                    centroidY = theModel.mScreenRdrObject.currentShape.mfOriginY;
                    centroidZ = theModel.mScreenRdrObject.currentShape.mfOriginZ;
                    modelDistance = MathUtils.getDistance3d(viewX, viewY, viewZ, centroidX, centroidY, centroidZ);
                    distances[modelCounter] = modelDistance;
                    models[modelCounter] = theModel;
                    modelCounter++;
                }

                if(theModel.miModelType == I_QUADMESH) {
                    centroidX = 0.0f;
                    centroidY = 0.0f;
                    centroidZ = 0.0f;
                    modelDistance = MathUtils.getDistance3d(viewX, viewY, viewZ, centroidX, centroidY, centroidZ);
                    distances[modelCounter] = modelDistance;
                    models[modelCounter] = theModel;
                    modelCounter++;
                }
            }
            
            theModel = theModel.mNextEntry;
        }

        numModels = modelCounter;
        if(depthSortingEnabled) {
            Globals.insertionSort2(distances, models, numModels);
        }
        
        return 0;
    } // depthSort


    // Called from:
    //     constructor
    //     finalize
    public int sizeofLowerLimit() {
        int mySize = 0;
        int booleanFieldsSizeInBits = 0;
        int booleanFieldsSize = 0;
        int intFieldsSize = 0;
        int floatFieldsSize = 0;
        int referenceFieldsSize = 0;

        /*
        boolean ictdebug = false;
        public Scene sceneListHead;
        public Scene currentScene;
        public MemImage backgroundPlate;
        */

        booleanFieldsSizeInBits = 0; // 6 booleans
        booleanFieldsSize = 0; // 0 bits fit in a byte
        intFieldsSize = 1*4; // 1 ints
        floatFieldsSize = 0*4; // 0 floats
        referenceFieldsSize = 3*4; // 3 references to objects
        mySize = booleanFieldsSize + intFieldsSize + floatFieldsSize + referenceFieldsSize;

        return mySize;
    }
} // class SceneList