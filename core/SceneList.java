package core;

import dialogs.ImageView;

import fileUtils.FileUtils;

import globals.Globals;
import globals.JICTConstants;
import globals.Preference;

import java.awt.Color;
import java.awt.image.BufferedImage;

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
    private boolean bIctDebug = false;
    public Scene mSceneListHead;       // Points to the head of the list
    public Scene mCurrentScene;        // Points to the current Scene
    public MemImage mBkgndPlateMImage;  // An optional background plate image
/*
public:
  sceneList(); - implemented
  ~sceneList(); - implemented, as method finalize
  void display(); - implemented
  int readList(char *errorText, char *pathName); - implemented in ScnFileParser.java
  int writeList(char *errorText, char *pathName); - implemented
  sceneElement *setCurrentModel(char *desiredModel); - implemented

  void setCurrentModelTransform(float rx, float ry, float rz,
  float sx, float sy, float sz, float tx, float ty, float tz); - implemented

  void getCurrentModelTransform(float *rx, float *ry, float *rz,
  float *sx, float *sy, float *sz, float *tx, float *ty, float *tz); - implemented
 
  int getViewTransform(float *viewX, float *viewY, float *viewZ,
  float *rotateX, float *rotateY, float *rotateZ); - implemented
  
  int setViewTransform(float viewX, float viewY, float viewZ,
  float rotateX, float rotateY, float rotateZ); - implemented

  int getViewPoint(float *viewX, float *viewY, float *viewZ,
  float *rotateX, float *rotateY, float *rotateZ); - implemented

  int addScene (char *theSceneName, int theSequence, int outImageCols,
    int outImageRows, int theColorMode,
    point3d *rt, point3d *tr, char *theMotionPath); - implemented

  int addSceneElement(char *mdName, char *fName,  int blendI, int theType,
    int warpI, float aScale, point3d *rt,point3d *sc, point3d *tr,
    char *motionPath, char *alphaPath, int sortLayer, 
    RGBTRIPLE anAdjustment, char *adjustmentType, char *colorAdjustedPath,
	int definedRefpoint, point3d *pointOfReference); - implemented

  void showModels(CComboBox *theCombo); - implemented
  int getSceneInfo(char *name,  int *type,
    int *cMode, int *outRows, int *outCols); - implemented

  int setSceneOutImageSize(int outRows, int outCols); - implemented
  int setModelReferencePoint(char *modelName, float centroidX, float centroidY, 
	  float centroidZ); - implemented

  int setCompoundRefPoints(); - implemented
  int copyRefPoints(); - implemented


  int listLength(); - implemented
  void clear(); - implemented

  int preview(HWND displayWindow, tMatrix *modelMatrix,
    tMatrix *viewMatrix); - implemented

  int previewStill(HWND displayWindow, tMatrix *modelMatrix,
    tMatrix *viewMatrix); - implemented

  int render (imageView *displayWindow, tMatrix *viewMatrix,
    int depthSortingEnabled, int zBufferEnabled, int antiAliasEnabled, 
  int hazeFogEnabled); - implemented

  int depthSort(sceneElement *Models[], float distances[], int *numModels,
     int depthSortingEnabled); - implemented

  void adjustTransforms(int effectType, sceneElement *theModel,
    motionNode *aMotion, bundle *xfrm); - implemented

  int calcCompoundModelRefPoint(sceneElement *theModel, int outputRows, int outputCols, 
								  float *cmCentroidx, float *cmCentroidy, float *cmCentroidz); - implemented

  void sceneList::getViewMatrix(
    tMatrix *viewMatrix, int frameCounter, scene *theScene); - implemented
*/
    public SceneList() {
        if (bIctDebug) {
            Globals.statusPrint("SceneList Constructor 1.");
        }

        Point3d rtPt = new Point3d();
        Point3d scPt = new Point3d();
        this.mSceneListHead = new Scene("sceneList", 0, 0, 0, 0, rtPt, scPt, " ");
        this.mBkgndPlateMImage = null;
        this.mCurrentScene = null;
    } // SceneList ctor


    public void finalize() {
        if (bIctDebug) {
            Globals.statusPrint("SceneList destructor - deletes a Scenelist object");
        }

        clear();  // Clear the list of models
    } // finalize


    // setCurrentModel sets the mCurrentSceneElement field of the current 
    // SceneList object to the model whose name is passed in as the agument 
    // psDesiredModel.
    //
    // Called from:
    //     ScenePreviewDlg.onSelChangeCmbModels
    public SceneElement setCurrentModel(String psDesiredModel) {
        Scene scene = this.mSceneListHead;
        SceneElement modelSE;
        scene = scene.mNextEntry; // Point to the scene Node

        modelSE = scene.mHead;
        while (modelSE != null) {
            if(modelSE.msModelName.equals(psDesiredModel)) {
                scene.mCurrentSceneElement = modelSE;  // Indicate scene's current model
                return(modelSE);
            }

            modelSE = modelSE.mNextEntry;  // point to the next model
        }

        return null;  // Model not found
    } // setCurrentModel


    // setCurrentModelTransform sets the rotation, scale, and translation transformations 
    // (i.e., mRotation, mScale, and mTranslation fields) of the current model using the 
    // nine parameters supplied.
    //
    // Called from:
    //     ScenePreviewDlg.onCmdPlus
    public void setCurrentModelTransform(float pfRx, float pfRy, float pfRz,
    float pfSx, float pfSy, float pfSz, 
    float pfTx, float pfTy, float pfTz) {
        Scene scene = this.mSceneListHead;
        SceneElement currentModelSE;
        scene = scene.mNextEntry; // Point to the scene Node
        currentModelSE = scene.mCurrentSceneElement;

        currentModelSE.mRotation.x = pfRx;
        currentModelSE.mRotation.y = pfRy;
        currentModelSE.mRotation.z = pfRz;

        currentModelSE.mScale.x = pfSx;
        currentModelSE.mScale.y = pfSy;
        currentModelSE.mScale.z = pfSz;
        
        currentModelSE.mTranslation.x = pfTx;
        currentModelSE.mTranslation.y = pfTy;
        currentModelSE.mTranslation.z = pfTz;
    } // setCurrentModelTransform


    // getCurrentModelTransform returns the rotation, scale, and translation 
    // transformations of the curent model in the nine parameters.
    // 
    // Called from:
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onSelChangeCmbModels
    public void getCurrentModelTransform(Float pFRx, Float pFRy, Float pFRz,
    Float pFSx, Float pFSy, Float pFSz, 
    Float pFTx, Float pFTy, Float pFTz) {
        Scene scene = this.mSceneListHead;
        SceneElement currentModelSE;
        scene = scene.mNextEntry; // Point to the scene Node
        currentModelSE = scene.mCurrentSceneElement;

        // Set the output parameters
        pFRx = currentModelSE.mRotation.x;
        pFRy = currentModelSE.mRotation.y;
        pFRz = currentModelSE.mRotation.z;

        pFSx = currentModelSE.mScale.x;
        pFSy = currentModelSE.mScale.y;
        pFSz = currentModelSE.mScale.z;

        pFTx = currentModelSE.mTranslation.x;
        pFTy = currentModelSE.mTranslation.y;
        pFTz = currentModelSE.mTranslation.z;
    } // getCurrentModelTransform


    // showModels places a list of all the model names contained in a 
    // scene list into the combo box object whose reference is supplied.
    //
    // Not called from within this file.
    // Called from:
    //     ScnPreviewDlg.onInitDialog
    public void showModels(JComboBox<String> pComboBox) {
        Scene scene = this.mSceneListHead;
        int iNumItems = pComboBox.getItemCount(); // Clear the present contents ofthe comboBox
        // TODO: Review, this for loop seems wrong!
        for(int i = 1; i <= iNumItems; i++) {
            pComboBox.setSelectedIndex(0);
            pComboBox.removeAllItems();
        }

        scene = scene.mNextEntry;  // Skip over the list header
        pComboBox.removeAllItems();
        SceneElement modelSE = scene.mHead;
        while (modelSE != null) {
            if(!modelSE.msFileName.equalsIgnoreCase("Output Image Rectangle")) { // don't show the output image rectangle model
                pComboBox.addItem(modelSE.msModelName);
            }
            modelSE = modelSE.mNextEntry;
        }
    } // showModels


    // listLength returns as its value the one-relative number of models 
    // in the scene list object. If the scene list has no objects, it 
    // returns 0.
    //
    // Called from:
    //     MainFrame.onToolsCreateASceneList
    public int listLength() {
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if(scene == null) {
            return(0);
        }
        int iLength = 0;

        SceneElement modelSE = scene.mHead;
        while (modelSE != null) {
            iLength++;
            modelSE = modelSE.mNextEntry;
        }
        return(iLength);
    } // listLength


    // getSceneInfo eturns the scene name, special effect type (scene or sequence), 
    // color mode (color or monochrome), and the output image size.
    //
    // Called from:
    //     preview
    //     previewStill
    //     render
    public int getSceneInfo(String psName, 
    Integer pIType, Integer pICMode, Integer pIOutRows, Integer piOutCols) {
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if (scene == null) {
            return -1;
        }

        // Set the output parameters
        psName    = scene.msSceneName;
        pIType    = scene.miSequenceType;
        pICMode   = scene.miColorMode;
        pIOutRows = scene.miOutputRows;
        piOutCols = scene.miOutputColumns;

        return 0;
    } // getSceneInfo


    // setSceneOutImageSize sets the height and width of the output image.
    //
    // Called from:
    //     readList
    public int setSceneOutImageSize(int piOutRows, int piOutCols) {
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if (scene == null) {
            return -1;
        }

        scene.miOutputRows    = piOutRows;
        scene.miOutputColumns = piOutCols;
        return 0;
    } // setSceneOutImageSize


    // getViewTransform returns the translation and rotation transformations 
    // (i.e., mTranslationPt and mRotationPt fields) of the current 
    // scene (not model - ie., not SceneElement) using the nine parameters 
    // supplied.
    //
    // Called from:
    //     depthSort
    //     MainFrame.onToolsCreateASceneList
    public int getViewTransform(
    Float pFViewX,   Float pFViewY,   Float pFViewZ, 
    Float pFRotateX, Float pFRotateY, Float pFRotateZ) {
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if (scene == null) { 
            return -1;
        }

        // Assume the default viewer location is centered in the output frame
        // Set the output parameters
        pFViewX = scene.mTranslationPt.x;
        pFViewY = scene.mTranslationPt.y;
        pFViewZ = scene.mTranslationPt.z;

        pFRotateX = scene.mRotationPt.x;
        pFRotateY = scene.mRotationPt.y;
        pFRotateZ = scene.mRotationPt.z;

        return 0;
    } // getViewTransform


    // setViewTransform sets the translation and rotation transformations 
    // (i.e., mTranslationPt and mRotationPt fields) of the current 
    // scene (not model - ie., not SceneElement) using the nine parameters 
    // supplied.
    //
    // Called from:
    //     ScenePreviewDlg.onOK
    public int setViewTransform(float pfViewX, float pfViewY, float pfViewZ,
    float pfRotateX, float pfRotateY, float pfRotateZ) {
        Scene scene = mSceneListHead;
        scene = scene.mNextEntry;  //Skip over the list header
        if (scene == null) { 
            return -1;
        }

        scene.mTranslationPt.x = pfViewX;
        scene.mTranslationPt.y = pfViewY;
        scene.mTranslationPt.z = pfViewZ;
        
        scene.mRotationPt.x = pfRotateX;
        scene.mRotationPt.y = pfRotateY;
        scene.mRotationPt.z = pfRotateZ;

        return 0;
    } // setViewTransform


    // getViewPoint returns the viewer location (pFViewX, pFViewY, pFViewZ) 
    // and orientation using the six parameters supplied
    //
    // Called from:
    //     render
    private int getViewPoint(Float pFViewX, Float pFViewY, Float pFViewZ,
    Float pFRotateX, Float pFRotateY, Float pFRotateZ) {
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if (scene == null) { 
            return -1;
        }

        // Set the output parameters
        // The default camera (viewpoint) location is centered on the origin
        // and translated 512 units along the positive z axis.
        // Add to this location any viewpoint translation and rotation 
        // the user specified.
        pFViewX =   0.0f + scene.mTranslationPt.x;
        pFViewY =   0.0f + scene.mTranslationPt.y;
        pFViewZ = 512.0f + scene.mTranslationPt.z;

        // Since the default viewer rotations are (0,0,0), just output
        // whatever the user specified in the scene file.
        pFRotateX = 0.0f + scene.mRotationPt.x;
        pFRotateY = 0.0f + scene.mRotationPt.y;
        pFRotateZ = 0.0f + scene.mRotationPt.z;

        return 0;
    } // getViewPoint


    // writeList writes the contents of the SceneList to a .scn file.
    //
    // Called from:
    //     ScenePreviewDlg.onOK
    public int writeList(String psErrorText, String psFileName) {
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header

        File sceneFile = new File(psFileName);
        BufferedWriter fileOut = new BufferedWriter(new OutputStreamWriter(sceneFile));
        
        scene.writeFile(fileOut); // Write out the scene description
        SceneElement modelSE = scene.mHead;
        boolean	bOldCompoundMember = false;

        while (
        (modelSE != null) && 
        !modelSE.msFileName.equalsIgnoreCase("Output Image Rectangle")) {
            if ((modelSE.mbCompoundModelMember == false) && (bOldCompoundMember == true)) {
                String output = "End Compound Model" + "\n" + "\n";
                fileOut.write(output);
            }
            
            modelSE.writeFile(fileOut); // Write out each model description
            bOldCompoundMember = modelSE.mbCompoundModelMember;
            modelSE = modelSE.mNextEntry;
        }
        
        fileOut.close();
        return 0;
    } // writeList


    // Not called from within this file.
    //
    // Method previewSequence previews a sequence visual effect by 
    // traversing the the list of models in the scene list, displaying 
    // on the screen the name and boundary line segments of each model
    // using an off-screen animation technique.
    //
    // Called from:
    //     ImageView.onDraw
    public int preview(BufferedImage pBuffImg, TMatrix pModelMatrix, TMatrix pViewMatrix) {
        String sMsgText;
        int iStatus = 0;
        String sSceneName;
        Integer iEffectType = 0;
        Integer iColorMode = 0;
        Integer iOutputRows = 0, iOutputColumns = 0;
        Integer iFirstFrame = 0, iLastFrame = 0;
        int iFrameCounter;
        int iModelCounter;
        Scene scene;
        SceneElement modelSE;
        MotionNode motnNode = new MotionNode();  // current model location, orientation if moving
        Bundle xfrm = new Bundle();         // create a bundle of transforms
        TMatrix viewModelMatrix = new TMatrix();

        // The following method sets all the parameters,
        // but we will not use sSceneName nor iColorMode
        getSceneInfo(sSceneName, iEffectType, iColorMode, iOutputRows, iOutputColumns);

        // Setup for smooth animation.
        MemImage tempImage = new MemImage(iOutputRows, iOutputColumns);
        /* TODO: Replace this with Java code
        hBitmap = CreateBitmap((int)iOutputColumns, (int)iOutputRows, 1,
            1, tempImage.getBytes());
        if(hBitmap == 0) {
            Globals.statusPrint("SceneList::PreviewSequence. Unable to create internal bitmap");
            return -1;
        }
        */

        RECT myRect;
        SetRect(myRect, 0, 0, iOutputColumns, iOutputRows);

        // Preview the scene models
        scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if(scene == null) {
            Globals.statusPrint("sceneList.previewSequence: Scene list has no models");
            return 0;
        }

        iFirstFrame = iLastFrame = 0;
        if(iEffectType == JICTConstants.I_SEQUENCE) {
            // The following method sets both iFirstFrame and iLastFrame (of type Integer)
            scene.mSensorMotion.getFirstLastFrame(iFirstFrame, iLastFrame);
        }

        for(iFrameCounter = iFirstFrame; iFrameCounter <= iLastFrame; iFrameCounter++) {
            // Clear the memoryDC by drawing a filled white rectangle
            //FillRect(memoryDC, myRect, GetStockObject(WHITE_BRUSH));
            modelSE = scene.mHead;
            if(iEffectType == JICTConstants.I_SEQUENCE) {
                // The following method sets parameter pViewMatrix
                getViewMatrix(pViewMatrix, iFrameCounter, scene);
            }
            iModelCounter = 0;

            while (modelSE != null) {
                // If the renderObject has not been created, create it
                iModelCounter++;
                if(modelSE.mScreenRdrObject == null) {
                    modelSE.miStatusIndicator = 0;
                    sMsgText = "PreviewSequence: Creating RenderObject: " + modelSE.msModelName;
                    Globals.statusPrint(sMsgText);
                    // Create the RenderObject
                    modelSE.mScreenRdrObject = new RenderObject(modelSE.msFileName,
                        modelSE.miModelType, modelSE.mbDefinedRefPoint, modelSE.pointOfReference);

                    if (!modelSE.mScreenRdrObject.isValid()) {
                        modelSE.miStatusIndicator = 1;  // this object could not be opened
                        modelSE.mbValid = false;
                        sMsgText = "PreviewSequence: Couldn't create renderObject: " + modelSE.msModelName;
                        Globals.statusPrint(sMsgText);
                        return -1;
                    }

                    // Setup the scene's background plate if needed
                    if(iModelCounter == 1 && 
                    modelSE.mbWarpIndicator == false &&
                    modelSE.mbBlendIndicator == false) {
                        if(mBkgndPlateMImage == null) {
                            mBkgndPlateMImage = new MemImage(modelSE.msFileName, 0, 0, 
                                JICTConstants.I_RANDOM, 'R', JICTConstants.I_GREENCOLOR);
                        }
                    }
                }

                if(modelSE.miStatusIndicator == 0) {  // If this is a valid model...
                    pModelMatrix.setIdentity();

                    // Compose the model transforms
                    if(
                    (iEffectType == JICTConstants.I_SEQUENCE) && 
                    (modelSE.mModelMotion != null)) {
                        // The following method modifies motnNode (of type MotionNode)
                        modelSE.mModelMotion.getNode(iFrameCounter, motnNode);
                    }

                    // The following method sets the fields of parameter xfrm (of type Bundle)
                    adjustTransforms(iEffectType, modelSE, motnNode, xfrm);

                    pModelMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                    float fXRadians = xfrm.rx * JICTConstants.F_DTR;
                    float fYRadians = xfrm.ry * JICTConstants.F_DTR;
                    float fZRadians = xfrm.rz * JICTConstants.F_DTR;

                    pModelMatrix.rotate(fXRadians, fYRadians, fZRadians);
                    pModelMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);

                    // Combine the model matrix with the view Matrix
                    viewModelMatrix.multiply(pViewMatrix, pModelMatrix);

                    // Apply the matrix
                    modelSE.mScreenRdrObject.transformAndProject(viewModelMatrix, iOutputRows, iOutputColumns);

                    // If this is a background plate, copy it to the screen
                    if(
                    iModelCounter == 1 && 
                    modelSE.mbWarpIndicator == false &&
                    modelSE.mbBlendIndicator == false) {
                        // Display a MemImage object in the indicated BufferedImage
                        mBkgndPlateMImage.display(iOutputColumns, iOutputRows);
                    } else {
                        // Draw the object
                        modelSE.mScreenRdrObject.drawSequence(pBuffImg, modelSE.msModelName, 
                            iOutputRows, iOutputColumns, iFrameCounter);
                    }
                } // end if valid screen object

                modelSE = modelSE.mNextEntry;
                // TODO: Replace this with Java code
                // BitBlt(0, 0, iOutputColumns, iOutputRows);
            }  // end of single frame 

            for(int i = 0; i <= 400000; i++)  {} // delay loop to approximate 30 Hz frame rate
        } // for iFrameCounter
      
        copyRefPoints();  // Copy model ref points to corresponding renderObjects

        return iStatus;
    } // preview

 
    // Method previewStill previews a single image visual effect by traversing 
    // the list of models (SceneElements) in the SceneList, displaying on the 
    // screen the name and boundary line segments of each model.
    //
    // Called from:
    //     ImageView.onDraw
    public int previewStill(BufferedImage pBuffImg, TMatrix pModelMatrix, TMatrix pViewMatrix) {
        String sMsgText;
        int iStatus = 0;
        String sSceneName = "";
        Integer iEffectType = 0;
        Integer iColorMode = 0;
        Integer iOutputRows = 0, iOutputColumns = 0;
        Integer iFirstFrame = 0, iLastFrame = 0;
        int iFrameCounter;
        int iModelCounter;
        Scene scene;
        SceneElement modelSE, saveModelSE;
        MotionNode motnNode = new MotionNode();    // current model location, orientation if moving
        boolean bFirstTime = false;                                       
        Bundle xfrm = new Bundle();           // create a bundle of transforms;
        Bundle cxfrm = new Bundle();          // create a bundle of transforms for a potential compound model;
        TMatrix viewModelMatrix = new TMatrix();

        // Holds the compound model transformation matrix
        // Used if theModel.miModelType == JICTConstants.I_COMPOUND
        TMatrix cModelMatrix = new TMatrix();  
        TMatrix tempMatrix = new TMatrix();
        
        // The following method sets all the parameters
        // but we will not use sSceneName nor iColorMode
        getSceneInfo(sSceneName, iEffectType, iColorMode, iOutputRows, iOutputColumns);
        // int xOffset = iOutputColumns / 2; // this variable is not used
        // int yOffset = iOutputRows / 2; // this variable is not used

        // Preview the scene models
        scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if(scene == null) {
            Globals.statusPrint("sceneList::previewStill: Scene list has no models");
            return 0;
        }

        iFirstFrame = iLastFrame = 0;
        if(iEffectType == JICTConstants.I_SEQUENCE) {
            // The following method sets both iFirstFrame and iLastFrame (of type Integer)
            scene.mSensorMotion.getFirstLastFrame(iFirstFrame, iLastFrame);
        }

        for(iFrameCounter = iFirstFrame; iFrameCounter <= iLastFrame; iFrameCounter++) {
            modelSE = scene.mHead;  // Point to the first model
            if(iEffectType == JICTConstants.I_SEQUENCE) {
                // The following method sets parameter pViewMatrix
                getViewMatrix(pViewMatrix, iFrameCounter, scene);
            }
            iModelCounter = 0;

            while (modelSE != null) {
                // If the RenderObject has not been created, create it
                iModelCounter++;
                if(modelSE.mScreenRdrObject == null) {
                    modelSE.miStatusIndicator = 0;
                    // Create the RenderObject
                    modelSE.mScreenRdrObject = new RenderObject(modelSE.msFileName,
                        modelSE.miModelType, modelSE.mbDefinedRefPoint, modelSE.pointOfReference);
                    if(modelSE.miModelType == JICTConstants.I_COMPOUND) {	
                        // Initialize a compound model centroid
                        modelSE.pointOfReference.x = 0.0f;
                        modelSE.pointOfReference.y = 0.0f;
                        modelSE.pointOfReference.z = 0.0f;
                    }

                    bFirstTime = true;  // This variable used to create the output image rectangle
                    if (!modelSE.mScreenRdrObject.isValid()) {
                        modelSE.miStatusIndicator = 1;  // This object could not be opened
                        sMsgText = "previewStill: Could not create renderObject: " + modelSE.msModelName;
                        Globals.statusPrint(sMsgText);
                        Globals.beep(10, 10);
                        return -1;
                    }
                } // if(modelSE.mScreenRdrObject == null)

                // Setup the scene's background plate if needed
                if(iModelCounter == 1 && 
                modelSE.mbWarpIndicator == false &&
                modelSE.mbBlendIndicator == false && 
                modelSE.miModelType != JICTConstants.I_COMPOUND) {
                    if(mBkgndPlateMImage == null) {
                        mBkgndPlateMImage = new MemImage(modelSE.msFileName, 0, 0, 
                            JICTConstants.I_RANDOM, 'R', JICTConstants.I_GREENCOLOR);
                    }
                }

                if(modelSE.miStatusIndicator == 0) {  // if this is a valid model...
                    Float fCmCentroidx = 0.0f, fCmCentroidy = 0.0f, fCmCentroidz = 0.0f;
                    pModelMatrix.setIdentity();

                    // Compose the appropriate transforms
                    if(
                    (iEffectType == JICTConstants.I_SEQUENCE) && 
                    (modelSE.mModelMotion != null)) {
                        modelSE.mModelMotion.getNode(iFrameCounter, motnNode);
                    }
                
                    if(modelSE.miModelType == JICTConstants.I_COMPOUND) {
                        // The following method sets the fields of parameter cxfrm (of type Bundle)
                        adjustTransforms(iEffectType, modelSE, motnNode, cxfrm);
                        cModelMatrix.scale(cxfrm.sx, cxfrm.sy, cxfrm.sz);
                        float fXRadians = cxfrm.rx * JICTConstants.F_DTR;
                        float fYRadians = cxfrm.ry * JICTConstants.F_DTR;
                        float fZRadians = cxfrm.rz * JICTConstants.F_DTR;
                        cModelMatrix.rotate(fXRadians, fYRadians, fZRadians);
                        cModelMatrix.translate(cxfrm.tx, cxfrm.ty, cxfrm.tz);
                    } else {
                        // The following method sets the fields of parameter xfrm (of type Bundle)
                        adjustTransforms(iEffectType, modelSE, motnNode, xfrm);
                        pModelMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                        float fXRadians = xfrm.rx * JICTConstants.F_DTR;
                        float fYRadians = xfrm.ry * JICTConstants.F_DTR;
                        float fZRadians = xfrm.rz * JICTConstants.F_DTR;
                        pModelMatrix.rotate(fXRadians, fYRadians, fZRadians);
                        pModelMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);
                    }
                    
                    // Combine the model matrix with the view Matrix
                    if(modelSE.mbCompoundModelMember) {
                        tempMatrix.setIdentity();
                        tempMatrix.multiply(pViewMatrix, pModelMatrix);
                        viewModelMatrix.multiply(cModelMatrix, tempMatrix);
                    } else {
                        // Combine the model matrix with the view matrix
                        viewModelMatrix.multiply(pViewMatrix, pModelMatrix);
                    }

                    // If this model is compound, calculate the compound model reference point
                    if(modelSE.miModelType == JICTConstants.I_COMPOUND) {
                        saveModelSE = modelSE;
                        // The following method sets parameters fCmCentroidx, fCmCentroidy, and fCmCentroidz
                        calcCompoundModelRefPoint(modelSE, iOutputRows, iOutputColumns, 
                            fCmCentroidx, fCmCentroidy, fCmCentroidz);
                        modelSE = saveModelSE;
                    }

                    //  Transform the points
                    if(modelSE.msFileName.equalsIgnoreCase("Output Image Rectangle") && 
                    modelSE.miModelType != JICTConstants.I_COMPOUND &&	     
                    modelSE.mbCompoundModelMember == false) {
                        // Apply the matrix
                        modelSE.mScreenRdrObject.transformAndProject(viewModelMatrix, 
                            iOutputRows, iOutputColumns);
                    }

                    if(
                    modelSE.msFileName.equalsIgnoreCase("Output Image Rectangle") && 
                    modelSE.miModelType != JICTConstants.I_COMPOUND &&
                    modelSE.mbCompoundModelMember) {
                        // Apply the matrix
                        modelSE.mScreenRdrObject.transformAndProject(viewModelMatrix,
                            iOutputRows, iOutputColumns, modelSE.mbCompoundModelMember, 
                            fCmCentroidx, fCmCentroidy, fCmCentroidz);
                    }

                    // Draw the points
                    //
                    // If this is a background plate, copy it to the screen
                    if(
                    iModelCounter == 1 && 
                    modelSE.miModelType != JICTConstants.I_COMPOUND && 
                    modelSE.mbWarpIndicator == false &&
                    modelSE.mbBlendIndicator == false) {
                        // Display a MemImage object in the indicated BufferedImage
                        mBkgndPlateMImage.display(iOutputColumns, iOutputRows);
                    } else {
                        // Draw the model boundary
                        if(modelSE.miModelType != JICTConstants.I_COMPOUND) {
                            modelSE.mScreenRdrObject.drawStill(pBuffImg, modelSE.msModelName, iOutputRows, iOutputColumns);
                        }
                    }
                } // end if valid screen object

                saveModelSE = modelSE;
                modelSE = modelSE.mNextEntry;
                if((modelSE == null) && (bFirstTime == true)) {
                    // This is the last model.  If the first time through this loop, add
                    // a model that displays the output image rectangle
                    Color aColor = Color.BLACK;

                    // The following creates a SceneElement object and adds it to the
                    // the list of SceneElements that SceneList maintains
                    iStatus = addSceneElement(" ", "Output Image Rectangle",  
                        false, JICTConstants.I_SHAPE, false, 
                        1.0f, null, null, null, 
                        "None", "Default", 
                        false, aColor, "None", "None", 
                        false, null);
                    modelSE = saveModelSE.mNextEntry;
                    if(modelSE != null) {
                        modelSE.mScreenRdrObject = new RenderObject(modelSE.msFileName,
                            modelSE.miModelType, modelSE.mbDefinedRefPoint, modelSE.pointOfReference);
                    }
                
                    modelSE = modelSE.mNextEntry;
                }
            }
        } // for iFrameCounter
        
        copyRefPoints();  // Copy model ref points to corresponding renderObjects

        return iStatus;
    } // previewStill


    // Method render traverses the list of models (SceneElements) in the scene list
    // appropriately rendering each photo-based model and placing it into the 
    // output image.
    //
    // Called from:
    //     MainFrame.onRenderScene
    //     MainFrame.onRenderSequence
    public int render(ImageView pDisplayWindow, TMatrix pViewMatrix,
    boolean pbDepthSortingEnabled, boolean pbZBufferEnabled, boolean pbAntiAliasEnabled, 
    boolean pbHazeFogEnabled) {
        String sOutputFileName = "", sSceneName = "";
        String sRedFileName = "", sGreenFileName = "", sBlueFileName = "", sRGBFileName = "";
        String sCurrentColor = "";
        Integer iEffectType = 0;
        Integer iColorMode = 0;
        Integer iOutputRows = 0, iOutputColumns = 0;
        int iFirstFrame, iLastFrame;
        int iFrameCounter;
        Float fVx = 0.0f, fVy = 0.0f, fVz = 0.0f;    // Viewpoint
        Float fVRx = 0.0f, fVRy = 0.0f, fVRz = 0.0f;       
        MotionNode motnNode = new MotionNode();      // Current model location and orientation if moving.
        Bundle xfrm = new Bundle();                  // Create a bundle of transforms
        Instant timeStart, timeEnd;
        timeStart = Instant.now();
        TMatrix forwardMatrix = new TMatrix();
        TMatrix viewModelMatrix = new TMatrix();

        // The following method sets all the parameters
        // but we will not use sSceneName
        getSceneInfo(sSceneName, iEffectType, iColorMode, iOutputRows, iOutputColumns);

        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;        // Skip over the list header
        if(scene == null) {
            Globals.statusPrint("SceneList.render: Scene list has no models");
            return -1;
        }

        // The following method sets fVx, fVy, fVz, 
        // and fVRx, fVRy, and fVRz as well
        getViewPoint(fVx, fVy, fVz, fVRx, fVRy, fVRz);
        SceneElement modelSE = scene.mHead;
        SceneElement[] models = new SceneElement[JICTConstants.I_MAXMODELS];
        float[] fDistances = new float[JICTConstants.I_MAXMODELS];
        Integer iNumModels = 0; 
        int iStatus = 0;
        iFirstFrame = iLastFrame = 0;

        // Open the zBuffer if necessary
        MemImage aliasMImage, zBuffMImage;
        zBuffMImage = null;
        if(pbZBufferEnabled) {
            zBuffMImage = new MemImage(iOutputRows, iOutputColumns, 32);
            if (!zBuffMImage.isValid()) {
                Globals.statusPrint("SceneList.render: Could not open Z Buffer");
                return -1;
            }

            // Initialize the zBuffer
            zBuffMImage.init32(JICTConstants.F_ZBUFFERMAXVALUE); 
        }

        if(iEffectType == JICTConstants.I_SEQUENCE) {
            // The following method sets Integers iFirstFrame and iLastFrame
            scene.mSensorMotion.getFirstLastFrame(iFirstFrame, iLastFrame);
        }

        MemImage alphaMImage, zMImage;
        alphaMImage = null;
        zMImage = null;

        for(iFrameCounter = iFirstFrame; iFrameCounter <= iLastFrame; iFrameCounter++) {
            // Depth Sort the models
            depthSort(models, fDistances, iNumModels, pbDepthSortingEnabled);
            if(iEffectType == JICTConstants.I_SEQUENCE) {
                getViewMatrix(pViewMatrix, iFrameCounter, scene);
            }

            // Setup the Color Mode
            int iFirstColor = JICTConstants.I_GREEN;
            int iLastColor  = JICTConstants.I_GREEN;
            if (iColorMode == JICTConstants.I_COLOR) {
                iFirstColor = JICTConstants.I_RED;
                iLastColor  = JICTConstants.I_BLUE;
            }

            for (int iColor = iFirstColor; iColor <= iLastColor; iColor++) {
                MemImage outputMImage = new MemImage(iOutputRows, iOutputColumns);
                if (!outputMImage.isValid()) {
                    Globals.statusPrint("SceneList.render: Could not create output image");
                    return -1;
                }

                // Loop through each model in the scene list
                for(int iCurrentModel = 0; iCurrentModel <= iNumModels - 1; iCurrentModel++) {
                    modelSE = models[iCurrentModel];
                    if (modelSE.miStatusIndicator == 0) {
                        // Use the projected cornerpoints in the renderObject to
                        // determine the warp coefficients.
                        // This approach imposes the reasonable requirement that the user preview
                        // the scene before rendering it.
                        if(modelSE.mScreenRdrObject == null) {
                            String sMsgText;
                            sMsgText = "sceneList::render: RenderObject not defined. Skipping model: " +
                                modelSE.msModelName;
                            Globals.statusPrint(sMsgText);
                            break;
                        }

                        if(iColor == JICTConstants.I_RED)   sCurrentColor = "Red";
                        if(iColor == JICTConstants.I_GREEN) sCurrentColor = "Green";
                        if(iColor == JICTConstants.I_BLUE)  sCurrentColor = "Blue";

                        String sMsgText = "Processing Frame: " + iFrameCounter + " Color: " + sCurrentColor + 
                            "  Model: " + modelSE.msModelName;
                        Globals.statusPrint(sMsgText);
                        MemImage inputMImage;
                        inputMImage = null;
                        alphaMImage = null;
                        zMImage = null;

                        // Open the input image, if the image has been color adjusted, open the adjusted image
                        String sInputPath;
                        if(!modelSE.msAdjustmentType.equalsIgnoreCase("None")) {
                            sInputPath = modelSE.msColorAdjustedPath;
                        } else {
                            sInputPath = modelSE.msFileName;
                        }

                        if(modelSE.miModelType == JICTConstants.I_SEQUENCE) {
                            getSequenceFileName(sInputPath, iFrameCounter);
                        }

                        // Open the model's image if appropriate
                        if(modelSE.miModelType != JICTConstants.I_SHAPE) {
                            inputMImage = new MemImage(sInputPath, 0, 0, JICTConstants.I_RANDOM, 'R', iColor);
                            if (!inputMImage.isValid()) {
                                sMsgText = "SceneList.Render: Can't open image: " + modelSE.msFileName;
                                Globals.statusPrint(sMsgText);
                                return -1;
                            }
                        }

                        if(modelSE.mbBlendIndicator) {
                            // Open the alpha image. 
                            // If an alpha image pathname was specified 
                            // in the scene file, use it. Otherwise set it to NULL.  In this case
                            // function iRenderz will create the alphaImage from the warped image.
                            String sAlphaName;
                            alphaMImage = null;
                            if(modelSE.miModelType == JICTConstants.I_IMAGE) {
                                if(!modelSE.msAlphaPath.equalsIgnoreCase("NONE")) {
                                    sAlphaName = modelSE.msAlphaPath;
                                    alphaMImage = new MemImage(sAlphaName, 0, 0, JICTConstants.I_RANDOM, 'R', JICTConstants.I_EIGHTBITMONOCHROME);
                                    if (!alphaMImage.isValid()) {
                                        Globals.statusPrint("SceneList.render. Can't open the custom alpha image");
                                    }
                                }
                            }
                        }

                        // Optionally open the z Image;
                        // String sZName; // variable is not used
                        if((pbZBufferEnabled) && (zMImage == null)) {
                            zMImage = new MemImage(iOutputRows, iOutputColumns, 32);
                            if (!zMImage.isValid()) {
                                Globals.statusPrint("SceneList.render: Not enough memory to open Z Image");
                                return -1;
                            } 
                            zMImage.setFileName("zImage");
                            zMImage.init32(JICTConstants.F_ZBUFFERMAXVALUE); 
                        }

                        // Get the desired transforms from either the model or the motion file
                        // Copy them to the xfrm object
                        if(
                        (iEffectType == JICTConstants.I_SEQUENCE) && 
                        (modelSE.mModelMotion != null)) {
                            modelSE.mModelMotion.getNode(iFrameCounter, motnNode);
                        }

                        adjustTransforms(iEffectType, modelSE, motnNode, xfrm);

                        // Properly render the model, based on model type and other options
                        if(pbZBufferEnabled) {
                            switch(modelSE.miModelType) {
                            case JICTConstants.I_IMAGE:
                            // case SEQUENCE: //this was causing a duplicate case error (duplicate with SHAPE)
                                iStatus = Globals.iRenderz(outputMImage, alphaMImage, inputMImage,
                                    zMImage, zBuffMImage,
                                    xfrm.rx, xfrm.ry, xfrm.rz, 
                                    xfrm.sx, xfrm.sy, xfrm.sz,
                                    xfrm.tx, xfrm.ty, xfrm.tz, 
                                    fVx,     fVy,     fVz,
                                    pViewMatrix,
                                    modelSE.mbWarpIndicator, modelSE.mbBlendIndicator, 
                                    xfrm.alpha,
                                    modelSE.pointOfReference.x,
                                    modelSE.pointOfReference.y,
                                    modelSE.pointOfReference.z);
                                break;

                            case JICTConstants.I_QUADMESH:
                                // Render the mesh.  The result is a zImage, and a temporary output image
                                // The rendered mesh is then blended into the final image using the scene ZBuffer and alpha options
                                iStatus = 0;	//this line for debugging
                                iStatus = modelSE.mScreenRdrObject.renderMeshz(outputMImage, alphaMImage, inputMImage,
                                    zBuffMImage, fVx, fVy, fVz);
                                break;

                            case JICTConstants.I_SHAPE:
                                // Build the transformation matrix
                                float fXRadians = xfrm.rx * JICTConstants.F_DTR;
                                float fYRadians = xfrm.ry * JICTConstants.F_DTR;
                                float fZRadians = xfrm.rz * JICTConstants.F_DTR;
                                forwardMatrix.setIdentity();
                                forwardMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                                forwardMatrix.rotate(fXRadians, fYRadians, fZRadians);
                                forwardMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);
                                viewModelMatrix.multiply(pViewMatrix, forwardMatrix);
                                viewModelMatrix.transformAndProject(modelSE.mScreenRdrObject.mCurrentShape,
                                    iOutputRows, iOutputColumns, true, 		   
                                    modelSE.pointOfReference.x,
                                    modelSE.pointOfReference.y,
                                    modelSE.pointOfReference.z);

                                iStatus = modelSE.mScreenRdrObject.renderShapez(outputMImage, alphaMImage,
                                    zBuffMImage, fVx, fVy, fVz);
                                break;
                            }
                        } else {            // no zbuffer
                            switch(modelSE.miModelType) {
                            case JICTConstants.I_IMAGE:
                            // case SEQUENCE: // this was causing a duplicate case error (duplicate with SHAPE)
                                iStatus = Globals.iRenderz(outputMImage, alphaMImage, inputMImage,
                                    zMImage,  zBuffMImage,
                                    xfrm.rx, xfrm.ry, xfrm.rz, 
                                    xfrm.sx, xfrm.sy, xfrm.sz,
                                    xfrm.tx, xfrm.ty, xfrm.tz, 
                                    fVx,     fVy,     fVz,
                                    pViewMatrix,
                                    modelSE.mbWarpIndicator, modelSE.mbBlendIndicator, 
                                    xfrm.alpha,
                                    modelSE.pointOfReference.x,
                                    modelSE.pointOfReference.y,
                                    modelSE.pointOfReference.z);
                                break;
    
                            case JICTConstants.I_QUADMESH:
                                iStatus = modelSE.mScreenRdrObject.renderMesh(outputMImage, inputMImage,
                                    modelSE.mbBlendIndicator);
                                break;

                            case JICTConstants.I_SHAPE:
                                iStatus = modelSE.mScreenRdrObject.renderShape(outputMImage,
                                    modelSE.mbBlendIndicator);
                                break;
                            } // end switch
                        } // end else (z buffer)


                        // Processing of one model is complete
                        // Update the display
                        outputMImage.display(iOutputColumns, iOutputRows);
                    } // if modelSE.statusIndicator == 0
                } // for iCurrentModel

                // One color channel of the scene is complete! Save it in the proper location
                String sOutputDir, sOutputPath;
                getFileName(sOutputFileName, scene.msSceneName, iFrameCounter, iColor);
                sOutputDir = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
                sOutputPath = sOutputDir + sOutputFileName;

                if (iColor == JICTConstants.I_RED)   sRedFileName   = sOutputPath;
                if (iColor == JICTConstants.I_GREEN) sGreenFileName = sOutputPath;
                if (iColor == JICTConstants.I_BLUE)  sBlueFileName  = sOutputPath;

                // Optionally anti-alias the output image
                if(pbAntiAliasEnabled) {
                    aliasMImage = new MemImage(iOutputRows, iOutputColumns);
                    FileUtils.appendFileName(sOutputFileName, sRGBFileName, sCurrentColor);
                    Globals.antiAlias(outputMImage, aliasMImage);
                    aliasMImage.copy(outputMImage, 0, 0);
                }

                String sMsgText;
                sMsgText = "Saving output image: " + sOutputPath;
                Globals.statusPrint(sMsgText);
                outputMImage.writeBMP(sOutputPath);

                // Re-initialize the zBuffer
                if(pbZBufferEnabled) {
                    zBuffMImage.init32(JICTConstants.F_ZBUFFERMAXVALUE);
                }
            } // for iColor

            // Combine the color channels together
            iStatus = 0;
            if(iColorMode == JICTConstants.I_COLOR) {
                // Prepare a pathname to the default output image location
                getFileName(sRGBFileName, scene.msSceneName, iFrameCounter, 0);
                String sRGBPath, sRGBDir;
                sRGBDir = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
                sRGBPath = sRGBDir + sRGBFileName;

                String sMsgText = "sceneList::render: Saving RGB image: " + sRGBPath;
                Globals.statusPrint(sMsgText);
                iStatus = Globals.makeRGBimage(sRedFileName, sGreenFileName, sBlueFileName, sRGBPath);
            }
        }  // for iFrameCounter
    
        timeEnd = Instant.now();
        Duration timeDiff = Duration.between(timeStart, timeEnd);

        String sMsgText = "Scene Generation Complete.  " + timeDiff.toMillis() + " milliseconds.";
        Globals.statusPrint(sMsgText);
        return iStatus;
    } // render


    // TODO: Not a method of SceneList in the original C++ code.
    // Called from:
    //     render
    private void getSequenceFileName(String psTheInputPath, int piFrameCounter) {
        String sTempPath;
        sTempPath = psTheInputPath;
        String sDrive, sDir, sFile, sExt;

        _splitpath(sTempPath, sDrive, sDir, sFile, sExt);
        
        int iLength = sFile.length();
        int iCurrentFrameNum, iNewFrameNum;
        if(iLength > 0) {
            String sFrameNum = sFile.substring(iLength - 4);
            iCurrentFrameNum = Integer.parseInt(sFrameNum);
        }

        iNewFrameNum = iCurrentFrameNum + piFrameCounter;
        if(iNewFrameNum > 9999) {
            iNewFrameNum = 9999;
            Globals.statusPrint("getSequenceFileName: Frame counter exceeded 9999.");
        }
        String sPrefix, sOutFile;
        sPrefix = sFile.substring(iLength - 4);
        char cColorChar = sFile.charAt(iLength - 1);

        //sprintf(sOutFile, "%.16s%#04d%c\0", sPrefix, iNewFrameNum, colorChar);
        sOutFile = String.format("%.16s%#04d%c", sPrefix, iNewFrameNum, cColorChar);
        _makepath(psTheInputPath, sDrive, sDir, sOutFile, sExt);
    } // getSequenceFileName


    // Not a method of SceneList in the original C++ code
    // Called from:
    //     render
    public void getFileName(String psOutputFileName, String psPrefix, 
    int piCounter, int piTheColor) {
        char cColorChar = ' ';

        if (piTheColor == JICTConstants.I_RED)   cColorChar = 'r';
        if (piTheColor == JICTConstants.I_GREEN) cColorChar = 'g';
        if (piTheColor == JICTConstants.I_BLUE)  cColorChar = 'b';
        if (piTheColor == 0)                     cColorChar = 'c';
        // sprintf(psOutputFileName, "%.16s%#04d%c.bmp\0", psPrefix, piCounter, cColorChar);
        psOutputFileName = String.format("%.16s%#04d%c.bmp", psPrefix, piCounter, cColorChar);
    } // getFileName


    // Called from:
    //     preview
    //     previewStill
    //     render
    private void adjustTransforms(int piEffectType, SceneElement pModelSE, 
    MotionNode pMotnNode, Bundle pXfrm) {
        // float viewX, viewY, viewZ, rotateX, rotateY, rotateZ; // these local variables are not used

        // Copy model transforms into a bundle object
        if((piEffectType == JICTConstants.I_SEQUENCE) && (pModelSE.mModelMotion != null)) {
            // Set output parameter xfrm
            pXfrm.rx = pMotnNode.mfRx;
            pXfrm.ry = pMotnNode.mfRy;
            pXfrm.rz = pMotnNode.mfRz;

            pXfrm.sx = pMotnNode.mfSx;
            pXfrm.sy = pMotnNode.mfSy;
            pXfrm.sz = pMotnNode.mfSz;

            pXfrm.tx = pMotnNode.mfTx;
            pXfrm.ty = pMotnNode.mfTy;
            pXfrm.tz = pMotnNode.mfTz;
            pXfrm.alpha = pMotnNode.mfAlpha;
        } else {
            // Set output parameter xfrm
            pXfrm.rx = pModelSE.mRotation.x;
            pXfrm.ry = pModelSE.mRotation.y;
            pXfrm.rz = pModelSE.mRotation.z;

            pXfrm.sx = pModelSE.mScale.x;
            pXfrm.sy = pModelSE.mScale.y;
            pXfrm.sz = pModelSE.mScale.z;

            pXfrm.tx = pModelSE.mTranslation.x;
            pXfrm.ty = pModelSE.mTranslation.y;
            pXfrm.tz = pModelSE.mTranslation.z;
            pXfrm.alpha = pModelSE.mfAlphaScale;
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
    private void getViewMatrix(TMatrix pViewMatrix, int piFrameCounter, Scene pScene) {
        MotionNode motnNode = new MotionNode();
        pViewMatrix.setIdentity();
        float fXRadians, fYRadians, fZRadians;
        // Note: F_DTR is a floating point constant, 
        // a degree to radians conversion factor

        if(pScene.mSensorMotion != null) {
            // The following method will modify parameter motnNode
            pScene.mSensorMotion.getNode(piFrameCounter, motnNode);
            fXRadians = motnNode.mfRx * JICTConstants.F_DTR;
            fYRadians = motnNode.mfRy * JICTConstants.F_DTR;
            fZRadians = motnNode.mfRz * JICTConstants.F_DTR;
        } else {
            fXRadians = pScene.mRotationPt.x * JICTConstants.F_DTR;
            fYRadians = pScene.mRotationPt.y * JICTConstants.F_DTR;
            fZRadians = pScene.mRotationPt.z * JICTConstants.F_DTR;
        }

        pViewMatrix.rotate(-fXRadians, -fYRadians, -fZRadians);
        if(pScene.mSensorMotion != null) {
            pViewMatrix.translate(-motnNode.mfTx, -motnNode.mfTy, -motnNode.mfTz);
        } else {
            pViewMatrix.translate(-pScene.mTranslationPt.x, -pScene.mTranslationPt.y, -pScene.mTranslationPt.z);
        }
    } // getViewMatrix


    // Called from:
    //     previewStill
    private int calcCompoundModelRefPoint(SceneElement pModelSE, 
    int piOutputRows, int piOutputColumns, 
    Float pFCmCentroidX, Float pFCmCentroidY, Float pFCmCentroidZ) {
        pFCmCentroidX = 0.0f;
        pFCmCentroidY = 0.0f;
        pFCmCentroidZ = 0.0f;
        // SceneElement saveModel; // this variable is not used
        float fBucketX = 0f, fBucketY = 0f, fBucketZ = 0f;
        Float fCentroidX = 0f, fCentroidY = 0f, fCentroidZ = 0f;
        boolean bPrevModelIsACompoundMember = false; // changed from int to boolean
        int iModelCounter = 0;
        TMatrix modelMatrix = new TMatrix();
        
        // pModelSceneElem is assumed to point to the compound model object.
        //
        // Each compound model component is transformed in order to get its centroid
        // These centroids are accumulated and averaged to obtain the centroid of the 
        // compound model.
        // saveModel = pModelSceneElem; // this variable is not used
        pModelSE = pModelSE.mNextEntry;
        // Process each SceneElement of the containing Scene
        while (pModelSE != null) {
            // Build the model's transformation matrix
            modelMatrix.scale(pModelSE.mScale.x, pModelSE.mScale.y, pModelSE.mScale.z);
            // Convert the rotation angles from degrees to radians.
            // Note that F_DTR is a degrees-to-radians conversion factor.
            float fXRadians = pModelSE.mRotation.x * JICTConstants.F_DTR;
            float fYRadians = pModelSE.mRotation.y * JICTConstants.F_DTR;
            float fZRadians = pModelSE.mRotation.z * JICTConstants.F_DTR;
            modelMatrix.rotate(fXRadians, fYRadians, fZRadians);
            modelMatrix.translate(pModelSE.mTranslation.x, pModelSE.mTranslation.y, pModelSE.mTranslation.z);

            // If the model's RenderObject has not been created, create it.
            if(pModelSE.mScreenRdrObject == null) {
                pModelSE.miStatusIndicator = 0;
                pModelSE.mScreenRdrObject = new RenderObject(pModelSE.msFileName,
                    pModelSE.miModelType, pModelSE.mbDefinedRefPoint, pModelSE.pointOfReference);

                if (!pModelSE.mScreenRdrObject.isValid()) {
                    pModelSE.miStatusIndicator = 1;  // this object could not be opened
                    String sMsgText = "calcCompoundModelRefPoint: Could not create renderObject: " + pModelSE.msModelName;
                    Globals.statusPrint(sMsgText);
                    return -1;
                }
            }

            // Transform the model and get its transformed centroid
            if(pModelSE.mbCompoundModelMember) {
                // Transform the individual model
                pModelSE.mScreenRdrObject.transformAndProject(modelMatrix, piOutputRows, piOutputColumns);
                pModelSE.mScreenRdrObject.mCurrentShape.getTCentroid(fCentroidX, fCentroidY, fCentroidZ);
      
                String sMsgText;
                sMsgText = "calcCmModelRefPoint. model: " + pModelSE.msModelName;
                Globals.statusPrint(sMsgText);
                sMsgText = "calcCmModelRefPoint. modelCentroid: " + fCentroidX + " " + fCentroidY + " " + fCentroidZ;
                Globals.statusPrint(sMsgText);

                fBucketX += fCentroidX;
                fBucketY += fCentroidY;
                fBucketZ += fCentroidZ;
                iModelCounter++;
            }

            if(!pModelSE.mbCompoundModelMember && bPrevModelIsACompoundMember) {
                // Set the output parameters, the compound model centroid
                // = (pFCmCentroidX, pFCmCentroidY, pFCmCentroidZ)
                pFCmCentroidX = fBucketX / iModelCounter;
                pFCmCentroidY = fBucketY / iModelCounter;
                pFCmCentroidZ = fBucketZ / iModelCounter;

                String sMsgText = "calcCmModelRefPoint. cmCentroid: " 
                    + pFCmCentroidX + " " + pFCmCentroidY + " " + pFCmCentroidZ;
                Globals.statusPrint(sMsgText);
            }

            bPrevModelIsACompoundMember = pModelSE.mbCompoundModelMember;
            pModelSE = pModelSE.mNextEntry;  // Get the pointer to next model
        } // while

        // Handle the case where a compound model is the last model in the
        // scene list.
        if(bPrevModelIsACompoundMember) {
            // Set the output parameters
            pFCmCentroidX = fBucketX / iModelCounter;
            pFCmCentroidY = fBucketY / iModelCounter;
            pFCmCentroidZ = fBucketZ / iModelCounter;
        }

        return 0;
    } // calcCompoundModelRefPoint


    // Called from:
    //     SceneFileParser.readList
    public int addScene(String psSceneName, int piType, 
    int piOutImCols, int piOutImRows, int piColorMode, 
    Point3d pRtPt, Point3d pTrPt, String psPath) {
        int iStatus = 0;
        Scene newScene = new Scene(psSceneName, piType, piOutImCols, piOutImRows,
            piColorMode, pRtPt, pTrPt, psPath);
        if (!newScene.isValid()) {
            iStatus = 1;
        }

        mSceneListHead.mNextEntry = newScene;
        newScene.mPrevEntry = this.mSceneListHead;
        this.mCurrentScene = newScene;  // Make the new scene the Current scene
        return iStatus;
    } // addScene


    // Creates a SceneElement object from the parameters and adds it to 
    // the current SceneList's list. Parameters are related to information read
    // from a scene file:
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
    // => psModelName, pbBlendI, pfScale (= <alpha>)
    // => piType (depends on choice of [Image|Shape|QuadMesh|Sequence] selected)
    // FileName <pathName>                => psFileName
    // MotionPath [None|<pathName>]       => psMotionPath
    // [AlphaImagePath [None|<pathName>]] => psAlphaPath
    // AdjustColor [Target|Relative] <R> <G> <B> => psAdjustmentType, pAdjustColor
    // Rotation <rx>, <ry>, <rz>          => pRtPt
    // Scale <sx>, <sy>, <sz>             => pScPt
    // Translation <tx>, <ty>, <tz>       => pTrPt
    //
    // Called from:
    //     previewStill
    //     ScnFileParser.readList
    public int addSceneElement(String psModlName, String psFileName, boolean pbBlendI,
    int piType, boolean pbWarpI, float pfScale, 
    Point3d pRtPt, Point3d pScPt, Point3d pTrPt, 
    String psMotionPath, String psAlphaPath,
    boolean pbSortLayer, Color pAdjustmentColor, 
    String psAdjustmentType, String psColorAdjustedPath,
    boolean pbDefinedRefPt, Point3d pRefPoint) {
        Scene scene = this.mCurrentScene;  // Add an element to the current scene
        SceneElement aModelSE = scene.mHead;

        // Create a new SceneElement from the parameters passed in
        SceneElement modelSE = new SceneElement(psModlName, psFileName, 
            pbBlendI, piType, 
            pbWarpI, pfScale, 
            pRtPt, pScPt, pTrPt, 
            psMotionPath, psAlphaPath, 
            pbSortLayer, pAdjustmentColor, 
            psAdjustmentType, psColorAdjustedPath,
            pbDefinedRefPt, pRefPoint);

        if(!modelSE.isValid()) {
            return 1;
        }

        // Add the newly created SceneElement modelSE to the list 
        // that the current SceneList maintains
        if(scene.mHead == null) {
            // Add modelSE at front of the list
            scene.mHead = modelSE;
            modelSE.mPrevEntry = null;
            modelSE.mNextEntry = null;
        } else {
            // Add modelSE at the end of the list
            aModelSE = scene.mHead;  // Find the last element
            while (aModelSE.mNextEntry != null) {
                aModelSE = aModelSE.mNextEntry;
            }
            aModelSE.mNextEntry = modelSE;
            modelSE.mNextEntry = aModelSE;
        }

        // Update the tail pointer to point to modelSE
        scene.mTail = modelSE;
        return 0;
    } // addSceneElement


    // Apparently used for debugging purposes.
    // Did not see it being called.
    public void display() {
        Scene scene = mSceneListHead;
        Scene currentScene;
        SceneElement modelSE;

        currentScene = scene.mNextEntry;
        while (currentScene != null) {
            currentScene.display(); // Scene Display
            modelSE = currentScene.mHead;
            modelSE.display();  // Model (SceneElement) Display
            currentScene = currentScene.mNextEntry;
        }
    } // display


    // Method clear removes all models from the scenelist as well as the scene 
    // node.
    //
    // Called from:
    //     finalize
    public void clear() {
        Scene scene = this.mSceneListHead;
        SceneElement modelSE, nextModelSE;

        if(scene.mNextEntry == null) {
            return; // Don't clear an empty list
        }

        scene = scene.mNextEntry;
        modelSE = scene.mHead;

        while (modelSE != null) {
            nextModelSE = modelSE.mNextEntry;  // Get the pointer to next model
            modelSE = null;                 // Before deleting current model
            modelSE = nextModelSE;
        }

        scene = this.mSceneListHead;
        scene.mNextEntry = null;

        // Reset the background plate
        if(mBkgndPlateMImage != null) {
            mBkgndPlateMImage = null;
        }
    } // clear


    // Given a model name psModelName, find a the corresponding SceneElement with the 
    // msModelName = psModelName. Once found, set its point of reference to 
    // pfCentroidX, pfCentroidY, pfCentroidZ.
    //
    // Called from:
    //     setCompoundRefPoints
    public int setModelReferencePoint(String psModelName, 
    float pfCentroidX, float pfCentroidY, float pfCentroidZ) {
        Scene scene = this.mSceneListHead;
        SceneElement modelSE;
        String sMsgText;

        if(scene.mNextEntry == null) {
            Globals.statusPrint("setModelReferencePoint: sceneList has no scene object.");
            return -2; 
        }

        scene = scene.mNextEntry;
        modelSE = scene.mHead;
        boolean bFound = false;

        while (modelSE != null) {
            if(psModelName.equalsIgnoreCase(modelSE.msModelName)) {
                modelSE.pointOfReference.x = pfCentroidX;
                modelSE.pointOfReference.y = pfCentroidY;
                modelSE.pointOfReference.z = pfCentroidZ;
                bFound = true;
            }
            modelSE = modelSE.mNextEntry;  // Get the pointer to next model
        } // while

        if(!bFound) {
            // No model (i.e., SceneElement) was found with msModelName = psModelName
            sMsgText = "setModelReferencePoint: Model Not Found: " + psModelName;
            Globals.statusPrint(sMsgText);
            return -1;
        }

        return 0;
    } // setModelReferencePoint


    // Not called from within this file
    // Could not find where this is called from
    public int setCompoundRefPoints() {
        Scene scene = this.mSceneListHead;
        SceneElement modelSE;
        String sModelName = "";
        float fBucketX = 0.0f, fBucketY = 0.0f, fBucketZ = 0.0f;
        float fCentroidX, fCentroidY, fCentroidZ;

        if(scene.mNextEntry == null) {
            Globals.statusPrint("setCompoundRefPoints: sceneList has no scene object.");
            return -2; 
        }

        scene = scene.mNextEntry;
        modelSE = scene.mHead;
        // int found = FALSE; // this variable is not used
        int modelCounter = 0;
        boolean prevModelIsACompoundMember = false;

        while (modelSE != null) {
            if(modelSE.miModelType == JICTConstants.I_COMPOUND) {
                sModelName = modelSE.msModelName;
                fBucketX = 0.0f;
                fBucketY = 0.0f;
                fBucketZ = 0.0f;
                modelCounter = 0;
            }

            if(modelSE.mbCompoundModelMember) {
                fBucketX += modelSE.pointOfReference.x;
                fBucketY += modelSE.pointOfReference.y;
                fBucketZ += modelSE.pointOfReference.z;
                modelCounter++;
            }

            if(
            (!modelSE.mbCompoundModelMember) && 
            (prevModelIsACompoundMember)) {
                fCentroidX = fBucketX / modelCounter;
                fCentroidY = fBucketY / modelCounter;
                fCentroidZ = fBucketZ / modelCounter;

                // Find the model (i.e., SceneElement) with the name of sModelName, 
                // and set its point of reference to
                // (fCentroidX, fCentroidY, fCentroidZ)
                setModelReferencePoint(sModelName, fCentroidX, fCentroidY, fCentroidZ);
            }

            prevModelIsACompoundMember = modelSE.mbCompoundModelMember;
            modelSE = modelSE.mNextEntry;  // Get the pointer to next model
        } 

        // Handle the case where a compound model is the last model in the
        // scene list.
        if(prevModelIsACompoundMember) {
            fCentroidX = fBucketX / modelCounter;
            fCentroidY = fBucketY / modelCounter;
            fCentroidZ = fBucketZ / modelCounter;

            // Find the model (i.e., SceneElement) with the name of sModelName, 
            // and set its point of reference to
            // (fCentroidX, fCentroidY, fCentroidZ)
            setModelReferencePoint(sModelName, fCentroidX, fCentroidY, fCentroidZ);
        }

        return 0;
    } // setCompoundRefPoints


    // Called from:
    //     preview
    //     previewStill
    private int copyRefPoints() {
        // Copies reference points into a model from its corresponding
        // renderObject
        Scene scene = this.mSceneListHead;
        SceneElement modelSE;
        Float fCentroidX = 0.0f, fCentroidY = 0.0f, fCentroidZ = 0.0f;

        if(scene.mNextEntry == null) {
            Globals.statusPrint("copyRefPoints: sceneList has no scene object.");
            return -2; 
        }

        scene = scene.mNextEntry;
        modelSE = scene.mHead;
        while (modelSE != null) {
            if(
            (!modelSE.mbDefinedRefPoint) && 
            (modelSE.miModelType != JICTConstants.I_COMPOUND)) {
                // The following method sets centroidX, centroidY, and centroidZ (all of type Float)
                modelSE.mScreenRdrObject.mCurrentShape.getReferencePoint(fCentroidX, fCentroidY, fCentroidZ);

                modelSE.pointOfReference.x = fCentroidX; 
                modelSE.pointOfReference.y = fCentroidY;
                modelSE.pointOfReference.z = fCentroidZ;
            }

            modelSE = modelSE.mNextEntry;  // Get the pointer to next model
        } // while modelSE != null

        return 0;
    } // copyRefPoints


    // This method originally came from DEPTHSRT.CPP
    //
    // depthSort determines the order in which the models (SceneElements)
    // are to be rendered. It calculates the distance from the center of
    // each model to the viewpoint. It then sorts the distances and causes
    // the models that are farthest away from the viewpoint to be rendered
    // first. It assumes the total number of models in the scene list will
    // not exceed the constant I_MAXMODELS defined in JICTConstants.java.
    // 
    // Called from:
    //     render
    // which in turn is called from either
    // MainFrame.onRenderScene or MainFrame.onRenderSequence
    public int depthSort(SceneElement[] paModels, float[] pafDistances,
    Integer pINumModels, boolean pbDepthSortingEnabled) {
        Float fViewX = 0f, fViewY = 0f, fViewZ = 0f;
        Float fRotateX = 0f, fRotateY = 0f, fRotateZ = 0f;
        float fCentroidX, fCentroidY, fCentroidZ;
        float fModelDistance;

        // The following method sets all the parameters
        getViewTransform(fViewX, fViewY, fViewZ, fRotateX, fRotateY, fRotateZ);

        // Preview the Scene Models
        Scene scene = this.mSceneListHead;
        scene = scene.mNextEntry;  // Skip over the list header
        if(scene == null) {
            return -1;
        }

        SceneElement modelSE = scene.mHead;
        int iModelCounter = 0;

        while (modelSE != null) {
            if(
            iModelCounter == 0 && 
            modelSE.mbWarpIndicator == false &&
            modelSE.mbBlendIndicator == false) {
                pafDistances[iModelCounter] = 999999999.9f;  // set the distance of the backdrop image
                paModels[iModelCounter] = modelSE;
                iModelCounter++;
            } else {
                if(
                modelSE.miModelType == JICTConstants.I_IMAGE || 
                modelSE.miModelType == JICTConstants.I_SHAPE) {
                    fCentroidX = modelSE.mScreenRdrObject.mCurrentShape.mfOriginX;
                    fCentroidY = modelSE.mScreenRdrObject.mCurrentShape.mfOriginY;
                    fCentroidZ = modelSE.mScreenRdrObject.mCurrentShape.mfOriginZ;
                    fModelDistance = MathUtils.getDistance3d(fViewX, fViewY, fViewZ, fCentroidX, fCentroidY, fCentroidZ);
                    pafDistances[iModelCounter] = fModelDistance;
                    paModels[iModelCounter] = modelSE;
                    iModelCounter++;
                }

                if(modelSE.miModelType == JICTConstants.I_QUADMESH) {
                    fCentroidX = 0.0f;
                    fCentroidY = 0.0f;
                    fCentroidZ = 0.0f;
                    fModelDistance = MathUtils.getDistance3d(fViewX, fViewY, fViewZ, fCentroidX, fCentroidY, fCentroidZ);
                    pafDistances[iModelCounter] = fModelDistance;
                    paModels[iModelCounter] = modelSE;
                    iModelCounter++;
                }
            }
            
            modelSE = modelSE.mNextEntry;
        }

        pINumModels = iModelCounter;
        if(pbDepthSortingEnabled) {
            Globals.insertionSort2(pafDistances, paModels, pINumModels);
        }
        
        return 0;
    } // depthSort
} // class SceneList