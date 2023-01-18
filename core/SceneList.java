package core;

import dialogs.ImageView;

import globals.Globals;
import globals.Preference;

import java.awt.Color;

import java.util.StringTokenizer;

import javax.swing.JComboBox;

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

public class SceneList {
    boolean ictdebug = false;
    public Scene sceneListHead;       // Points to the head of the list
    public Scene currentScene;        // Points to the current Scene
    public MemImage backgroundPlate;  // An optional background plate image

    // This value came from ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f;

    // These were defined in SCENELST.H
    public static final int RED   = 1;
    public static final int GREEN = 2;
    public static final int BLUE  = 3;
    
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

    // This was defined in ICT20.H
    public static final float ZBUFFERMAXVALUE = 2.0E31f;

    // This was defined in SCENELST.H
    // Size of model array for depth sorting
    // Increase if > MAXMODELS are to be used in a scene
    public static final int MAXMODELS = 256;
    
    private static final int THREE_NUMBERS_NOT_FOUND = 2;

    public SceneList() {
        if (ictdebug) {
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
        if (ictdebug) {
            String msgBuffer;
            Globals.statusPrint("SceneList destructor - deletes a Scenelist object");
            msgBuffer = "Size of SceneList: " + sizeofLowerLimit();
            Globals.statusPrint(msgBuffer);
        }

        clear();  // Clear the list of models
    } // finalize


    // Called from:
    //     ScenePreviewDlg.onSelChangeCmbModels
    public SceneElement setCurrentModel(String desiredModel) {
        Scene theScene = this.sceneListHead;
        SceneElement aModel;
        theScene = theScene.nextEntry; // Point to the scene Node

        aModel = theScene.head;
        while (aModel != null) {
            if(aModel.modelName.equals(desiredModel)) {
                theScene.currentSceneElement = aModel;  // Indicate scene's current model
                return(aModel);
            }

            aModel = aModel.nextEntry;  // point to the next model
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
        theScene = theScene.nextEntry; // Point to the scene Node
        currentModel = theScene.currentSceneElement;

        currentModel.rotation.x = rx;
        currentModel.rotation.y = ry;
        currentModel.rotation.z = rz;

        currentModel.scale.x = sx;
        currentModel.scale.y = sy;
        currentModel.scale.z = sz;
        
        currentModel.translation.x = tx;
        currentModel.translation.y = ty;
        currentModel.translation.z = tz;
    } // setCurrentModelTransform


    // Called from:
    //     ScenePreviewDlg.chooseModel
    //     ScenePreviewDlg.onSelChangeCmbModels
    public void getCurrentModelTransform(Float rx, Float ry, Float rz,
    Float sx, Float sy, Float sz, 
    Float tx, Float ty, Float tz) {
        Scene theScene = this.sceneListHead;
        SceneElement currentModel;
        theScene = theScene.nextEntry; // Point to the scene Node
        currentModel = theScene.currentSceneElement;

        // Set the output parameters
        rx = currentModel.rotation.x;
        ry = currentModel.rotation.y;
        rz = currentModel.rotation.z;

        sx = currentModel.scale.x;
        sy = currentModel.scale.y;
        sz = currentModel.scale.z;

        tx = currentModel.translation.x;
        ty = currentModel.translation.y;
        tz = currentModel.translation.z;
    } // getCurrentModelTransform



    // Method readList parses the Scene and Model-related information in a .scn file
    // specified by parameter pathName. If an error occurs while parsing, the String 
    // errorText is set to an error message.
    // It then adds the scene and model information to a doubly-linked list. The start
    // of this list is pointed to by field sceneListHead.
    // Called from:
    //     MainFrame.onToolsCreateASceneList
    public int readList(String errorText, String pathName) {
        String TheText, TheKeyWord;
        String theModelName,theFileName, theMotionPath;
        String theSceneName, theAlphaPath, msgBuffer;
        String colorAdjustedPath;
        int lineCounter, numScenes, outImageCols, outImageRows;
        int notFound; // = TRUE, FALSE, or THREE_NUMBERS_NOT_FOUND
        boolean theBlend, theWarp;
        int theSequence, theColorMode, theType,
        compoundMMember;
        boolean compoundMember = false; // changed from int to boolean
        boolean getOutImageSizeFlag = false; // changed from int to boolean
        boolean definedRefPoint = false; // changed from int to boolean
        float theAlpha;
        Point3d rt, sc, tr, pointOfReference;
        Color anAdjustment;
        String adjustmentType;
        int myStatus, minLineSize = 4;
        byte midRed, midGreen, midBlue;
        final int FALSE = 0;
        final int TRUE = 1;
        final int THREE_NUMBERS_NOT_FOUND = 2;
        StringTokenizer strtok;

        final String BLANK = " ";
        final int DUPLICATESCENE = 3;

        ifstream filein;
        filein.open(pathName);
        if (filein.fail()) {
            errorText = "Unable to open scene file: " + pathName;
            // Why is errorText not printed with Global.statusPrint?
            return -1;
        }
        filein >> ws;		// Turn off linefeeds

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
        errorText = "Scene file read successfully";
        // Why is errorText not printed with Global.statusPrint?

        definedRefPoint = false;

        // Start parsing the .scn file. 
        // We exit the following "forever" loop via return statements.
        // We return -1 if we encounter an error.
        // We return 0 if no error occurs and we encounter the EOF keyword.
        while(true) {  // Get the scene components
            TheKeyWord = getNextLine(TheText, lineCounter, filein, minLineSize);
            // As long as we haven't encountered line that indicates 
            // the start of a Model description ...
            while(!TheKeyWord.equalsIgnoreCase("MODEL")) {
                String aBase, aSceneName, effectType, aColorMode;
                String tempImageSize;
                notFound = TRUE;

                if (TheKeyWord.equalsIgnoreCase("SCENE")) {
                    // We have found the start of a Scene description, 
                    // so we will parse it. It has the following format:
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    // Rotation <Rx>, <Ry>, <Rz>
                    // Translation <Tx>, <Ty>, <Tz>
                    // MotionPath [None]
                    notFound = FALSE;
                    aBase = TheText + 6;

                    // After the token "SCENE" we should have the scene name
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    strtok = new StringTokenizer(aBase, BLANK);
                    aSceneName = strtok.nextToken();
                    if(aSceneName != null) {
                        theSceneName = aSceneName;
                    } else {
                        errorText = "A Scene must have a name. Line " + lineCounter;
                        // Why is errorText not printed with Global.statusPrint?

                        filein.close();
                        return -1;
                    }

                    // Parse the effect type (i.e., [Sequence|Still])
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    effectType = strtok(null, BLANK);
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

                        filein.close();
                        return -1;
                    }

                    // Now parse the image dimensions (i.e., <outHeight>, <outWidth>)
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    // tempImageSize is equal to the string starting with <outHeight>:
                    // <outHeight>, <outWidth> [Color|Mono]
                    tempImageSize = strtok(null, BLANK);

                    // Now looking for Color or Mono:
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    aColorMode = strtok(null, BLANK);
                    // Assume "Mono"
                    theColorMode = MONOCHROME;
                    if(aColorMode != null) {
                        if(aColorMode.equalsIgnoreCase("COLOR")) { 
                            // We assumed wrong, so we correct our assumption
                            theColorMode = COLOR;
                        }
                    } else {
                        // We expected Color or Mono. Didn't find anything.
                        errorText = "Expected: Color or Monochrome. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        filein.close();
                        return -1;
                    }

                    // Now parse outHeight and outWidth values (i.e., <outHeight>, <outWidth>)
                    // These should be integers.
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    if(tempImageSize != null) { // Output Image Height, Width
                        outImageRows = Integer.parseInt(strtok(tempImageSize, ","));
                        outImageCols = Integer.parseInt(strtok(null, BLANK));
                        getOutImageSizeFlag = false;
                        if(outImageCols == 0 || outImageRows == 0) { 
                            getOutImageSizeFlag = true;
                        }
                    } else {
                        errorText = "Expected Image Height, Image Width. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        filein.close();
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

                        filein.close();
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
                    theRt = strtok(TheText + 9, BLANK);
                    localRt = theRt;
                    if(checkFor3(localRt) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        rt.x = Float.parseFloat(strtok(localRt, ","));
                        rt.y = Float.parseFloat(strtok(null, ","));
                        rt.z = Float.parseFloat(strtok(null, ","));
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
                    theTr = strtok(TheText + 12, BLANK);
                    localTr = theTr;
                    if(checkFor3(localTr) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        tr.x = Float.parseFloat(strtok(localTr, ","));
                        tr.y = Float.parseFloat(strtok(null, ","));
                        tr.z = Float.parseFloat(strtok(null, ","));
                        notFound = FALSE;
                    }
                }
              
                if(TheKeyWord.equalsIgnoreCase("END")) {
                    String theToken, localToken;
                    theToken = strtok(TheText + 4, BLANK);
                    localToken = theToken;
                    if(localToken.equalsIgnoreCase("COMPOUND")) {
                        compoundMMember = 0;
                        notFound = FALSE;
                    }
                }

                if (TheKeyWord.equalsIgnoreCase("EOF")) {
                    errorText = "sceneFile may be corrupted or has no models";
                    // errorText will be printed with Global.statusPrint by the caller.

                    filein.close();
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

                    filein.close();
                    return -1;
                }

                TheKeyWord = getNextLine(TheText, lineCounter, filein, minLineSize);
            }  // while(!TheKeyWord.equalsIgnoreCase("MODEL"))

            // Add the scene to the sceneList and read its elements.
            numScenes++;
            if (numScenes > 1) {
                errorText = "Only 1 scene definition permitted per scene file";
                // errorText will be printed with Global.statusPrint by the caller.

                filein.close();
                return -1;
            }

            myStatus = addScene(theSceneName, theSequence, outImageCols, outImageRows, theColorMode, rt, tr, theMotionPath);
            if(myStatus != 0) {
                errorText = "Could not add Scene to Scene List. Line " + lineCounter;
                // errorText will be printed with Global.statusPrint by the caller.

                filein.close();
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

                            Globals.constructPathName(colorAdjustedPath, theFileName, 'j');     
                            msgBuffer = "sceneList.readList: Saving adjusted color image: " + colorAdjustedPath;
                            Globals.statusPrint(msgBuffer);
            
                            correctedImage.writeBMP(colorAdjustedPath);
                        }

                        if(compoundMMember == 1 && theType == COMPOUND) compoundMember = false;
                        if(compoundMMember == 1 && theType != COMPOUND) compoundMember = true;

                        myStatus = addSceneElement(theModelName, theFileName, theBlend, theType,
                            theWarp, theAlpha, rt, sc, tr, theMotionPath, theAlphaPath,
                            compoundMember, anAdjustment, adjustmentType, colorAdjustedPath,
                            definedRefPoint, pointOfReference);
                        if(compoundMMember == 0) {
                            compoundMember = false;
                        }

                        if(myStatus != 0) {
                            errorText = "Could not add model to scene list. Line " + lineCounter;
                            // errorText will be printed with Global.statusPrint by the caller.

                            filein.close();
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
                    aModelName = strtok(TheText+6, BLANK);

                    // If the modelName is ".", set it to an empty string
                    // causing the model to be displayed without a label
                    if(aModelName.equalsIgnoreCase(".")) {
                        theModelName = "";
                    } else {
                        theModelName = aModelName;
                    }

                    // Look for the BLEND specification (i.e., [Blend|NoBlend]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aBlend      = strtok(null, BLANK);

                    // Look for the Warp/NoWarp specification (i.e., [Warp|NoWarp]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aWarp       = strtok(null, BLANK);

                    // Look for the AlphaScale specification (i.e., AlphaScale <alpha>):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aScale      = strtok(null, BLANK);
                    aScaleValue = strtok(null, BLANK);

                    // Look for the model type (i.e., for [Image|Shape|QuadMesh|Sequence]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    aType       = strtok(null, BLANK);

                    theBlend = true;
                    if(aBlend != null) {
                        if(aBlend.equalsIgnoreCase("NOBLEND")) { 
                            theBlend = false;
                        }
                    } else {
                        errorText = "Missing value or term on Line " + lineCounter;
                        // // errorText will be printed with Global.statusPrint by the caller.

                        filein.close();
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

                        filein.close();
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

                        filein.close();
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

                        filein.close();
                        return -1;
                    }

                    notFound = FALSE;
                } // if (strcmpi (TheKeyWord, "MODEL")

                if(TheKeyWord.equalsIgnoreCase("REFERENCEPOINT")) {
                    String theRef, localRef;
                    // Skip over the word "REFERENCEPOINT"
                    theRef = strtok(TheText + 15, BLANK);
                    localRef = theRef;

                    if(checkFor3(localRef) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        pointOfReference.x = Float.parseFloat(strtok(localRef, ","));
                        pointOfReference.y = Float.parseFloat(strtok(null, ","));
                        pointOfReference.z = Float.parseFloat(strtok(null, ","));
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
                    theRt = strtok(TheText + 9, BLANK);
                    localRt = theRt;

                    if(checkFor3(localRt) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        rt.x = Float.parseFloat(strtok(localRt, ","));
                        rt.y = Float.parseFloat(strtok(null, ","));
                        rt.z = Float.parseFloat(strtok(null, ","));
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
                    theSc = strtok(TheText + 6, BLANK);
                    localSc = theSc;

                    if(checkFor3(localSc) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        sc.x = Float.parseFloat(strtok(localSc, ","));
                        sc.y = Float.parseFloat(strtok(null, ","));
                        sc.z = Float.parseFloat(strtok(null, ","));
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
                    theTr = strtok(TheText + 12, BLANK);
                    localTr = theTr;

                    if(checkFor3(localTr) == 0) {
                        notFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        tr.x = Float.parseFloat(strtok(localTr, ","));
                        tr.y = Float.parseFloat(strtok(null, ","));
                        tr.z = Float.parseFloat(strtok(null, ","));
                        notFound = FALSE;
                    }
                }

                if(TheKeyWord.equalsIgnoreCase("ADJUSTCOLOR")) {
                    // Found an ADJUSTCOLOR line. So we parse it. 
                    // It should have the following format:
                    // ADJUSTCOLOR [Target|Relative] <R>, <G>, <B>
                    // where R, G, and B are RGB color values, 
                    // expressed as integers in the range from 0 to 255.
                    String adjustment, adjustmentCopy, theColor;
                    // Skip over the word "ADJUSTCOLOR"
                    adjustment = strtok(TheText + 12, BLANK);
                    // adjustmentType should be "TARGET" or "RELATIVE"
                    adjustmentType = adjustment;
                    int aLength = adjustment.length();
                    // Skip over both the words "ADJUSTCOLOR" and 
                    // "TARGET" or "RELATIVE"
                    theColor = strtok(TheText + 12 + aLength + 1, BLANK);  // move forward to the RGB color

                    // Parse the R, G, B color values
                    anAdjustment.setRed(  Integer.parseInt(strtok(theColor, ",")));
                    anAdjustment.setGreen(Integer.parseInt(strtok(null, ",")));
                    anAdjustment.setBlue( Integer.parseInt(strtok(null, ",")));
                    notFound = FALSE;
                }

                if(TheKeyWord.equalsIgnoreCase("MOTIONPATH")) {
                    // Found an MOTIONPATH line. So we parse it. 
                    // It should have the following format:
                    // MOTIONPATH [None|<pathName>]
                    // where pathName, if provided, is the path to a ".pth" file
                    // expressed as integers in the range from 0 to 255.

                    // Skip over the word "MOTIONPATH"
                    // Now theMotionPath should be either "None" or the path
                    theMotionPath = TheText.substring(11);

                    if(theMotionPath.length() == 0) {
                        errorText = "MotionPath file missing on Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        filein.close();
                        return -1;
                    }
                    notFound = FALSE;
                }

                if(TheKeyWord.equalsIgnoreCase("ALPHAIMAGEPATH")) {
                    // Found an ALPHAIMAGEPATH line. So we parse it. 
                    // It should have the following format:
                    // ALPHAIMAGEPATH [None|<pathName>]
                    theAlphaPath = TheText.substring(15);

                    if(theAlphaPath.length() == 0) {
                        errorText = "Alpha Image Path file missing. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        filein.close();
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
                    theFileName = TheText.substring(9);

                    // If the user previously specified either outWidth = 0 or outHeight = 0
                    // in the SCENE line ...
                    if(getOutImageSizeFlag == true) {
                        // Read the outWidth and outHeight values from the .bmp file
                        int bpp, bmpStatus;
                        bmpStatus = Globals.readBMPHeader(theFileName, outImageRows, outImageCols, bpp);
                        if(bmpStatus != 0) {
                            errorText = "File name not valid. Line " + lineCounter;
                            // errorText will be printed with Global.statusPrint by the caller.

                            filein.close();
                            return -1;
                        }
                        setSceneOutImageSize(outImageRows, outImageCols);
                        getOutImageSizeFlag = false;
                    }
                    notFound = FALSE;
                }

                // Look for other keywords - not model related
                if(TheKeyWord.equalsIgnoreCase("END")) {
                    String theToken, localToken;
                    theToken = strtok(TheText + 4, BLANK);
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

                        Globals.constructPathName(colorAdjustedPath, theFileName, 'j');     
                        msgBuffer = "Saving adjusted color image: " + colorAdjustedPath;
                        Globals.statusPrint(msgBuffer);
                  
                        correctedImage.writeBMP(colorAdjustedPath);
                    }

                    if(compoundMMember == 1 && theType == COMPOUND) compoundMember = false;
                    if(compoundMMember == 1 && theType != COMPOUND) compoundMember = true;

                    myStatus = addSceneElement(theModelName, theFileName, theBlend, theType,
                        theWarp, theAlpha, rt, sc, tr, theMotionPath, theAlphaPath,
                        compoundMember, anAdjustment, adjustmentType, colorAdjustedPath,
                        definedRefPoint, pointOfReference);
                    if(compoundMMember == 0) compoundMember = false;

                    if(myStatus != 0) {
                        errorText = "Could not add a model to scene list. Line " + lineCounter;
                        // errorText will be printed with Global.statusPrint by the caller.

                        filein.close();
                        return -1;
                    }

                    filein.close();
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

                    filein.close();
                    return -1;
                } // if (notFound != 0)
                
                TheKeyWord = getNextLine(TheText, lineCounter, filein, minLineSize);
            } // while(!TheKeyWord.equalsIgnoreCase("SCENE"))
        } // while(true)

        Globals.statusPrint("Abnormal SceneList Exit.");
        filein.close();
        return -1;
    } // readList


    public void showModels(JComboBox<String> theCombo) {
        Scene theScene = this.sceneListHead;
        int numItems = theCombo.getItemCount(); // Clear the present contents ofthe comboBox
        for(int i = 1; i <= numItems; i++) {
            theCombo.setSelectedIndex(0);
            theCombo.removeAllItems();
        }

        theScene = theScene.nextEntry;  //Skip over the list header
        theCombo.removeAllItems();
        SceneElement theModel = theScene.head;
        while (theModel != null) {
            if(!theModel.fileName.equalsIgnoreCase("Output Image Rectangle")) {     //don't show the output image rectangle model
                theCombo.addItem(theModel.modelName);
            }
            theModel = theModel.nextEntry;
        }
    } // showModels


    // Called from:
    //     MainFrame.onToolsCreateASceneList
    public int listLength() {
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;  // Skip over the list header
        if(theScene == null) {
            return(0);
        }
        int theLength = 0;

        SceneElement theModel = theScene.head;
        while (theModel != null) {
            theLength++;
            theModel = theModel.nextEntry;
        }
        return(theLength);
    } // listLength


    // Called from:
    //     preview
    //     previewStill
    //     render
    public int getSceneInfo(String psName, 
    Integer pIType, Integer pICMode, Integer pIOutRows, Integer piOutCols) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;  // Skip over the list header
        if (theScene == null) {
            return -1;
        }

        // Set the output parameters
        psName    = theScene.sceneName;
        pIType    = theScene.sequenceType;
        pICMode   = theScene.colorMode;
        pIOutRows = theScene.outputRows;
        piOutCols = theScene.outputColumns;

        return 0;
    } // getSceneInfo


    // Called from:
    //     readList
    public int setSceneOutImageSize(int piOutRows, int piOutCols) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;  // Skip over the list header
        if (theScene == null) {
            return -1;
        }

        theScene.outputRows    = piOutRows;
        theScene.outputColumns = piOutCols;
        return 0;
    } // setSceneOutImageSize


    // Called from:
    //     depthSort
    public int getViewTransform(Float pFViewX, Float pFViewY, Float pFViewZ, 
    Float pFRotateX, Float pFRotateY, Float pFRotateZ) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;  //Skip over the list header
        if (theScene == null) { 
            return -1;
        }

        // Assume the default viewer location is centered in the output frame
        // Set the output parameters
        pFViewX = theScene.translation.x;
        pFViewY = theScene.translation.y;
        pFViewZ = theScene.translation.z;

        pFRotateX = theScene.rotation.x;
        pFRotateY = theScene.rotation.y;
        pFRotateZ = theScene.rotation.z;

        return 0;
    } // getViewTransform


    // Called from:
    //     ScenePreviewDlg.onOK
    public int setViewTransform(float pfViewX, float pfViewY, float pfViewZ,
    float pfRotateX, float pfRotateY, float pfRotateZ) {
        Scene theScene = sceneListHead;
        theScene = theScene.nextEntry;  //Skip over the list header
        if (theScene == null) { 
            return -1;
        }

        theScene.translation.x = pfViewX;
        theScene.translation.y = pfViewY;
        theScene.translation.z = pfViewZ;
        
        theScene.rotation.x = pfRotateX;
        theScene.rotation.y = pfRotateY;
        theScene.rotation.z = pfRotateZ;

        return 0;
    } // setViewTransform


    // Called from:
    //     render
    public int getViewPoint(Float viewX, Float viewY, Float viewZ,
    Float rotateX, Float rotateY, Float rotateZ) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;  // Skip over the list header
        if (theScene == null) { 
            return -1;
        }

        // Set the output parameters
        // The default camera (viewpoint) location is centered on the origin
        // and translated 512 units along the positive z axis.
        // Add to this location any viewpoint translation and rotation 
        // the user specified.
        viewX =   0.0f + theScene.translation.x;
        viewY =   0.0f + theScene.translation.y;
        viewZ = 512.0f + theScene.translation.z;

        // Since the default viewer rotations are (0,0,0), just output
        // whatever the user specified in the scene file.
        rotateX = 0.0f + theScene.rotation.x;
        rotateY = 0.0f + theScene.rotation.y;
        rotateZ = 0.0f + theScene.rotation.z;

        return 0;
    } // getViewPoint


    // Called from:
    //     ScenePreviewDlg.onOK
    public int writeList(String psErrorText, String psFileName) {
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;  // Skip over the list header

        // TODO: Replace fileOut with a FileStream
        ofstream fileOut = new ofstream(psFileName);
        if (fileOut.fail()) {
            psErrorText = "Unable to open file: " + psFileName;
            return -1;
        }
        
        theScene.writeFile(fileOut); // Write out the scene description
        SceneElement theModel = theScene.head;
        boolean	oldCompoundMember = false;

        while ((theModel != null) && !theModel.fileName.equalsIgnoreCase("Output Image Rectangle")) {
            if ((theModel.compoundModelMember == false) && (oldCompoundMember == true)) {
                String output = "End Compound Model" + "\n" + "\n";
                fileOut << output;
            }
            
            theModel.writeFile(fileOut); // Write out each model description
            oldCompoundMember = theModel.compoundModelMember;
            theModel = theModel.nextEntry;
        }
        
        fileOut.close();
        return 0;
    } // writeList


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

        getSceneInfo(sceneName, effectType,  colorMode, outputRows, outputColumns);

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
        theScene = theScene.nextEntry;  //Skip over the list header
        if(theScene == null) {
            Globals.statusPrint("sceneList.previewSequence: Scene list has no models");
            return 0;
        }

        firstFrame = lastFrame = 0;
        if(effectType == SEQUENCE) {
            // The following method sets both firstFrame and lastFrame
            theScene.sensorMotion.getFirstLastFrame(firstFrame, lastFrame);
        }
        int modelCounter;
        boolean eraseOldBoundary; // variable is set, but not read

        for(frameCounter = firstFrame; frameCounter <= lastFrame; frameCounter++) {
            // Clear the memoryDC by drawing a filled white rectangle
            FillRect(memoryDC, myRect, GetStockObject(WHITE_BRUSH));
            SceneElement theModel = theScene.head;
            modelCounter = 0;
            eraseOldBoundary = true;
            if(effectType == SEQUENCE) {
                // The following method sets viewMatrix
                getViewMatrix(viewMatrix, frameCounter, theScene);
            }

            while (theModel != null) {
                // If the renderObject has not been created, create it
                modelCounter++;
                if(theModel.screenObject == null) {
                    theModel.statusIndicator = 0;
                    msgText = "PreviewSequence: Creating RenderObject: " + theModel.modelName;
                    Globals.statusPrint(msgText);
                    theModel.screenObject = new RenderObject(theModel.fileName,
                        theModel.modelType, theModel.definedRefPoint, theModel.pointOfReference);

                    if (!theModel.screenObject.isValid()) {
                        theModel.statusIndicator = 1;  // this object could not be opened
                        theModel.valid = false;
                        msgText = "PreviewSequence: Couldn't create renderObject: " + theModel.modelName;
                        Globals.statusPrint(msgText);
                        return -1;
                    }

                    // Get the scene's background plate if needed
                    if(modelCounter == 1 && 
                    theModel.warpIndicator == false &&
                    theModel.blendIndicator == false) {
                        if(backgroundPlate == null) {
                            backgroundPlate = new MemImage(theModel.fileName, 0, 0, RANDOM, 'R', GREENCOLOR);
                        }
                    }
                }

                if(theModel.statusIndicator == 0) {  // If this is a valid model...
                    modelMatrix.setIdentity();

                    // Compose the model transforms
                    if((effectType == SEQUENCE) && (theModel.modelMotion != null)) {
                        // The following method modifies aMotion (of type MotionNode)
                        theModel.modelMotion.getNode(frameCounter, aMotion);
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
                    theModel.screenObject.transformAndProject(viewModelMatrix, outputRows, outputColumns);

                    // If this is a background plate, copy it to the screen
                    if(
                    modelCounter == 1 && 
                    theModel.warpIndicator == false &&
                    theModel.blendIndicator == false) {
                        HDC theDC = GetDC(theWindow);
                        backgroundPlate.display(theDC, outputColumns, outputRows);
                        ReleaseDC(theWindow, theDC);
                        eraseOldBoundary = false;
                    } else {
                        // Draw the object
                        theModel.screenObject.drawSequence(memoryDC, theModel.modelName, 
                            outputRows, outputColumns, frameCounter);
                    }
                } // end if valid screen object

                theModel = theModel.nextEntry;
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
        theScene = theScene.nextEntry;  // Skip over the list header
        if(theScene == null) {
            Globals.statusPrint("sceneList::previewStill: Scene list has no models");
            return 0;
        }

        firstFrame = lastFrame = 0;
        if(effectType == SEQUENCE) {
            theScene.sensorMotion.getFirstLastFrame(firstFrame, lastFrame);
        }
        eraseOldBoundary = false;

        for(frameCounter = firstFrame; frameCounter <= lastFrame; frameCounter++) {
            theModel = theScene.head;  // Point to the first model
            if(effectType == SEQUENCE) {
                getViewMatrix(viewMatrix, frameCounter, theScene);
            }
            modelCounter = 0;

            while (theModel != null) {
                // If the renderObject has not been created, create it
                modelCounter++;
                if(theModel.screenObject == null) {
                    theModel.statusIndicator = 0;
                    theModel.screenObject = new RenderObject(theModel.fileName,
                        theModel.modelType, theModel.definedRefPoint, theModel.pointOfReference);
                    if(theModel.modelType == COMPOUND) {	// Initialize a compound model centroid
                        theModel.pointOfReference.x = 0.0f;
                        theModel.pointOfReference.y = 0.0f;
                        theModel.pointOfReference.z = 0.0f;
                    }

                    firstTime = true;  // This variable used to create the output image rectangle
                    if (!theModel.screenObject.isValid()) {
                        theModel.statusIndicator = 1;  // This object could not be opened
                        String msgText = "previewStill: Could not create renderObject: " + theModel.modelName;
                        Globals.statusPrint(msgText);
                        Globals.beep(10, 10);
                        return -1;
                    }
                }

                // Setup the scene's background plate if needed
                if(modelCounter == 1 && 
                theModel.warpIndicator == false &&
                theModel.blendIndicator == false && theModel.modelType != COMPOUND) {
                    if(backgroundPlate == null) {
                        backgroundPlate = new MemImage(theModel.fileName, 0, 0, RANDOM, 'R', GREENCOLOR);
                    }
                }

                if(theModel.statusIndicator == 0) {  // if this is a valid model...
                    float cmCentroidx, cmCentroidy, cmCentroidz;
                    modelMatrix.setIdentity();

                    // Compose the appropriate transforms
                    if((effectType == SEQUENCE) && (theModel.modelMotion != null)) {
                        theModel.modelMotion.getNode(frameCounter, aMotion);
                    }
                
                    if(theModel.modelType == COMPOUND) {
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
                    if(theModel.compoundModelMember) {
                        tempMatrix.setIdentity();
                        tempMatrix.multiply(viewMatrix, modelMatrix);
                        viewModelMatrix.multiply(cModelMatrix, tempMatrix);
                    } else {
                        // Combine the model matrix with the view Matrix
                        viewModelMatrix.multiply(viewMatrix, modelMatrix);
                    }

                    // If this model is compound, calculate the compound model reference Point
                    if(theModel.modelType == COMPOUND) {
                        saveModel = theModel;
                        calcCompoundModelRefPoint(theModel, outputRows, outputColumns, cmCentroidx, cmCentroidy, cmCentroidz);
                        theModel = saveModel;
                    }

                    //  Transform the points
                    if(theModel.fileName.equalsIgnoreCase("Output Image Rectangle") && 
                    theModel.modelType != COMPOUND &&	     
                    theModel.compoundModelMember == false) {    
                        theModel.screenObject.transformAndProject(viewModelMatrix, outputRows, outputColumns);
                    }

                    if(
                    theModel.fileName.equalsIgnoreCase("Output Image Rectangle") && 
                    theModel.modelType != COMPOUND &&
                    theModel.compoundModelMember) {   
                        theModel.screenObject.transformAndProject(viewModelMatrix,
                            outputRows, outputColumns, theModel.compoundModelMember, cmCentroidx, cmCentroidy, cmCentroidz);
                    }

                    // Draw the points
                    //
                    // If this is a background plate, blt it to the screen
                    if(
                    modelCounter == 1 && 
                    theModel.modelType != COMPOUND && 
                    theModel.warpIndicator == false &&
                    theModel.blendIndicator == false) {
                        HDC theDC = GetDC(theWindow);
                        backgroundPlate.display(theDC, outputColumns, outputRows);
                        ReleaseDC(theWindow, theDC);
                        eraseOldBoundary = false;
                    } else {
                        // Draw the model boundary
                        if(theModel.modelType != COMPOUND) {
                            theModel.screenObject.drawStill(theWindow, theModel.modelName, outputRows, outputColumns);
                        }
                    }
                } // end if valid screen object

                saveModel = theModel;
                theModel = theModel.nextEntry;
                if((theModel == null) && (firstTime == true)) {
                    // This is the last model.  If the first time through this loop, add
                    // a model that displays the output image rectangle
                    Color aColor;
                    myStatus = addSceneElement(" ", "Output Image Rectangle",  
                        false, SHAPE, false, 
                        1.0f, null, null, null, 
                        "None", "Default", 
                        false, aColor, "None", "None", 
                        false, null);
                    theModel = saveModel.nextEntry;
                    if(theModel != null) {
                        theModel.screenObject = new RenderObject(theModel.fileName,
                            theModel.modelType, theModel.definedRefPoint, theModel.pointOfReference);
                    }
                
                    theModel = theModel.nextEntry;
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
        time_t time1, time2;
        time(time1);
        TMatrix forwardMatrix, viewModelMatrix;

        getSceneInfo(sceneName, effectType, colorMode, outputRows, outputColumns);
        Scene theScene = this.sceneListHead;
        theScene = theScene.nextEntry;        // Skip over the list header
        if(theScene == null) {
            Globals.statusPrint("sceneList::render: Scene list has no models");
            return -1;
        }

        // The following method sets vx, vy, vz, 
        // and vrx, vry, and vrz as well
        getViewPoint(vx, vy, vz, vrx, vry, vrz);
        SceneElement theModel = theScene.head;
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
            theScene.sensorMotion.getFirstLastFrame(firstFrame, lastFrame);
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
                    if (theModel.statusIndicator == 0) {
                        // Use the projected cornerpoints in the renderObject to
                        // determine the warp coefficients.
                        // This approach imposes the reasonable requirement that the user preview
                        // the scene before rendering it.
                        if(theModel.screenObject == null) {
                            String msgText;
                            msgText = "sceneList::render: RenderObject not defined. Skipping model: " +
                                theModel.modelName;
                            Globals.statusPrint(msgText);
                            break;
                        }

                        if(theColor == RED)   currentColor = "Red";
                        if(theColor == GREEN) currentColor = "Green";
                        if(theColor == BLUE)  currentColor = "Blue";

                        String msgText = "Processing Frame: " + frameCounter + " Color: " + currentColor + 
                            "  Model: " + theModel.modelName;
                        Globals.statusPrint(msgText);
                        MemImage inputImage;
                        inputImage = null;
                        alphaImage = null;
                        zImage = null;

                        // Open the input image, if the image has been color adjusted, open the adjusted image
                        String theInputPath;
                        if(!theModel.adjustmentType.equalsIgnoreCase("None")) {
                            theInputPath = theModel.colorAdjustedPath;
                        } else {
                            theInputPath = theModel.fileName;
                        }

                        if(theModel.modelType == SEQUENCE) {
                            getSequenceFileName(theInputPath, frameCounter);
                        }

                        // Open the model's image if appropriate
                        if(theModel.modelType != SHAPE) {
                            inputImage = new MemImage(theInputPath, 0, 0, RANDOM, 'R', theColor);
                            if (!inputImage.isValid()) {
                                msgText = "sceneList.Render: Can't open image: " + theModel.fileName;
                                Globals.statusPrint(msgText);
                                return -1;
                            }
                        }

                        if(theModel.blendIndicator) {
                            // Open the alpha image. 
                            // If an alpha image pathname was specified 
                            // in the scene file, use it. Otherwise set it to NULL.  In this case
                            // function iRenderz will create the alphaImage from the warped image.
                            String alphaName;
                            alphaImage = null;
                            if(theModel.modelType == IMAGE) {
                                if(!theModel.alphaPath.equalsIgnoreCase("NONE")) {
                                    alphaName = theModel.alphaPath;
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
                        if((effectType == SEQUENCE) && (theModel.modelMotion != null)) {
                            theModel.modelMotion.getNode(frameCounter, aMotion);
                        }

                        adjustTransforms(effectType, theModel, aMotion, xfrm);

                        // Properly render the model, based on model type and other options
                        if(zBufferEnabled) {
                            switch(theModel.modelType) {
                            case IMAGE:
                            // case SEQUENCE: //this was causing a duplicate case error (duplicate with SHAPE)
                                myStatus = Globals.iRenderz(outputImage, alphaImage, inputImage,
                                    zImage, zBuffer,
                                    xfrm.rx, xfrm.ry, xfrm.rz, xfrm.sx, xfrm.sy, xfrm.sz,
                                    xfrm.tx, xfrm.ty, xfrm.tz, vx, vy, vz,
                                    viewMatrix,
                                    theModel.warpIndicator, theModel.blendIndicator, xfrm.alpha,
                                    theModel.pointOfReference.x,
                                    theModel.pointOfReference.y,
                                    theModel.pointOfReference.z);
                                break;

                            case QUADMESH:
                                // Render the mesh.  The result is a zImage, and a temporary output image
                                // The rendered mesh is then blended into the final image using the scene ZBuffer and alpha options
                                myStatus = 0;	//this line for debugging
                                myStatus = theModel.screenObject.renderMeshz(outputImage, alphaImage, inputImage,
                                    zBuffer, vx, vy, vz);
                                break;

                            case SHAPE:
                                // Build the transformation matrix
                                float XRadians = xfrm.rx * F_DTR;
                                float YRadians = xfrm.ry * F_DTR;
                                float ZRadians = xfrm.rz * F_DTR;
                                forwardMatrix.setIdentity();
                                forwardMatrix.scale(xfrm.sx, xfrm.sy, xfrm.sz);
                                forwardMatrix.rotate(XRadians, YRadians, ZRadians);
                                forwardMatrix.translate(xfrm.tx, xfrm.ty, xfrm.tz);
                                viewModelMatrix.multiply(viewMatrix, forwardMatrix);
                                viewModelMatrix.transformAndProject(theModel.screenObject.currentShape,
                                    outputRows, outputColumns, true, 		   
                                    theModel.pointOfReference.x,
                                    theModel.pointOfReference.y,
                                    theModel.pointOfReference.z);

                                myStatus = theModel.screenObject.renderShapez(outputImage, alphaImage,
                                    zBuffer, vx, vy, vz);
                                break;
                            }
                        } else {            //no zbuffer
                            switch(theModel.modelType) {
                            case IMAGE:
                            // case SEQUENCE: // this was causing a duplicate case error (duplicate with SHAPE)
                                myStatus = Globals.iRenderz(outputImage, alphaImage, inputImage,
                                    zImage,  zBuffer,
                                    xfrm.rx, xfrm.ry, xfrm.rz, 
                                    xfrm.sx, xfrm.sy, xfrm.sz,
                                    xfrm.tx, xfrm.ty, xfrm.tz, 
                                    vx,      vy,      vz,
                                    viewMatrix,
                                    theModel.warpIndicator, theModel.blendIndicator, xfrm.alpha,
                                    theModel.pointOfReference.x,
                                    theModel.pointOfReference.y,
                                    theModel.pointOfReference.z);
                                break;
    
                            case QUADMESH:
                                myStatus = theModel.screenObject.renderMesh(outputImage, inputImage,
                                    theModel.blendIndicator);
                                break;

                            case SHAPE:
                                myStatus = theModel.screenObject.renderShape(outputImage,
                                    theModel.blendIndicator);
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
                getFileName(outputFileName, theScene.sceneName, frameCounter, theColor);
                outputDir = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
                outputPath = outputDir + outputFileName;

                if (theColor == RED)   redFileName   = outputPath;
                if (theColor == GREEN) greenFileName = outputPath;
                if (theColor == BLUE)  blueFileName  = outputPath;

                // Optionally anti-alias the output image
                if(antiAliasEnabled) {
                    aliasImage = new MemImage(outputRows, outputColumns);
                    Globals.appendFileName(outputFileName, RGBFileName, currentColor);
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
                getFileName(RGBFileName, theScene.sceneName, frameCounter, 0);
                String RGBPath, RGBDir;
                RGBDir = Globals.ictPreference.getPath(Preference.OutputImageDirectory);
                RGBPath = RGBDir + RGBFileName;

                String msgText = "sceneList::render: Saving RGB image: " + RGBPath;
                Globals.statusPrint(msgText);
                myStatus = Globals.makeRGBimage(redFileName, greenFileName, blueFileName, RGBPath);
            }
        }  // End of Sequence Loop
    
        time(time2);
        time_t timeDiff = (int)difftime(time2, time1);

        String msgText = "Scene Generation Complete.  " + timeDiff + " seconds.";
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
    public void adjustTransforms(int piEffectType, SceneElement theModel, 
    MotionNode aMotion, Bundle xfrm) {
        // float viewX, viewY, viewZ, rotateX, rotateY, rotateZ; // these local variables are not used

        // Copy model transforms into a bundle object
        if((piEffectType == SEQUENCE) && (theModel.modelMotion != null)) {
            // Set output parameter xfrm
            xfrm.rx = aMotion.rx;
            xfrm.ry = aMotion.ry;
            xfrm.rz = aMotion.rz;

            xfrm.sx = aMotion.sx;
            xfrm.sy = aMotion.sy;
            xfrm.sz = aMotion.sz;

            xfrm.tx = aMotion.tx;
            xfrm.ty = aMotion.ty;
            xfrm.tz = aMotion.tz;
            xfrm.alpha = aMotion.alpha;
        } else {
            // Set output parameter xfrm
            xfrm.rx = theModel.rotation.x;
            xfrm.ry = theModel.rotation.y;
            xfrm.rz = theModel.rotation.z;

            xfrm.sx = theModel.scale.x;
            xfrm.sy = theModel.scale.y;
            xfrm.sz = theModel.scale.z;

            xfrm.tx = theModel.translation.x;
            xfrm.ty = theModel.translation.y;
            xfrm.tz = theModel.translation.z;
            xfrm.alpha = theModel.alphaScale;
        }
    } // adjustTransforms


    // This method sets parameter pViewMatrix.
    // MainFrame also has a getViewMatrix method, but it takes a single parameter of 
    // type TMatrix.
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

        if(pTheScene.sensorMotion != null) {
            pTheScene.sensorMotion.getNode(piFrameCounter, aMotion);
            xRadians = aMotion.rx * F_DTR;
            yRadians = aMotion.ry * F_DTR;
            zRadians = aMotion.rz * F_DTR;
        } else {
            xRadians = pTheScene.rotation.x * F_DTR;
            yRadians = pTheScene.rotation.y * F_DTR;
            zRadians = pTheScene.rotation.z * F_DTR;
        }

        pViewMatrix.rotate(-xRadians, -yRadians, -zRadians);
        if(pTheScene.sensorMotion != null) {
            pViewMatrix.translate(-aMotion.tx, -aMotion.ty, -aMotion.tz);
        } else {
            pViewMatrix.translate(-pTheScene.translation.x, -pTheScene.translation.y, -pTheScene.translation.z);
        }
    } // getViewMatrix


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
        theModel = theModel.nextEntry;

        while (theModel != null) {
            // Build the model's transformation matrix
            modelMatrix.scale(theModel.scale.x, theModel.scale.y, theModel.scale.z);
            float xRadians = theModel.rotation.x * F_DTR;
            float yRadians = theModel.rotation.y * F_DTR;
            float zRadians = theModel.rotation.z * F_DTR;
            modelMatrix.rotate(xRadians, yRadians, zRadians);
            modelMatrix.translate(theModel.translation.x, theModel.translation.y, theModel.translation.z);

            // If the model's RenderObject has not been created, create it.
            if(theModel.screenObject == null) {
                theModel.statusIndicator = 0;
                theModel.screenObject = new RenderObject(theModel.fileName,
                    theModel.modelType, theModel.definedRefPoint, theModel.pointOfReference);

                if (!theModel.screenObject.isValid()) {
                    theModel.statusIndicator = 1;  // this object could not be opened
                    String msgText = "calcCompoundModelRefPoint: Could not create renderObject: " + theModel.modelName;
                    Globals.statusPrint(msgText);
                    return -1;
                }
            }

            // Transform the model and get its transformed centroid
            if(theModel.compoundModelMember) {
                // Transform the individual model
                theModel.screenObject.transformAndProject(modelMatrix, outputRows, outputColumns);
                theModel.screenObject.currentShape.getTCentroid(mCentroidX, mCentroidY, mCentroidZ);
      
                String msgText;
                msgText = "calcCmModelRefPoint. model: " + theModel.modelName;
                Globals.statusPrint(msgText);
                msgText = "calcCmModelRefPoint. modelCentroid: " + mCentroidX + " " + mCentroidY + " " + mCentroidZ;
                Globals.statusPrint(msgText);

                bucketX += mCentroidX;
                bucketY += mCentroidY;
                bucketZ += mCentroidZ;
                modelCounter++;
            }

            if(!theModel.compoundModelMember && prevModelIsACompoundMember) {
                // Set the output parameters
                cmCentroidX = bucketX / modelCounter;
                cmCentroidY = bucketY / modelCounter;
                cmCentroidZ = bucketZ / modelCounter;

                String msgText = "calcCmModelRefPoint. cmCentroid: " + cmCentroidX + " " + cmCentroidY + " " + cmCentroidZ;
                Globals.statusPrint(msgText);
            }

            prevModelIsACompoundMember = theModel.compoundModelMember;
            theModel = theModel.nextEntry;  // Get the pointer to next model
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

        sceneListHead.nextEntry = newScene;
        newScene.prevEntry = this.sceneListHead;
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
        SceneElement aModel = aScene.head;
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

        if(aScene.head == null) {
            aScene.head = theModel;
            theModel.prevEntry = null;
            theModel.nextEntry = null;
        } else {
            aModel = aScene.head;  // Find the last element
            while (aModel.nextEntry != null) {
                aModel = aModel.nextEntry;
            }
            aModel.nextEntry = theModel;
            theModel.prevEntry = aModel;
        }

        aScene.tail = theModel;
        return myStatus;
    } // addSceneElement


    public void display() {
        Scene aScene = sceneListHead;
        Scene currentScene;
        SceneElement model;

        currentScene = aScene.nextEntry;
        while (currentScene != null) {
            currentScene.display(); // Scene Display
            model = currentScene.head;
            model.display();  // Model Display
            currentScene = currentScene.nextEntry;
        }
    } // display


    // Called from:
    //     finalize
    public void clear() {
        Scene aScene = this.sceneListHead;
        SceneElement theModel, nextModel;

        if(aScene.nextEntry == null) {
            return; // Don't clear an empty list
        }

        aScene = aScene.nextEntry;
        theModel = aScene.head;

        while (theModel != null) {
            nextModel = theModel.nextEntry;  // Get the pointer to next model
            theModel = null;                 // Before deleting current model
            theModel = nextModel;
        }

        aScene = this.sceneListHead;
        aScene.nextEntry = null;

        // Reset the background plate
        if(backgroundPlate != null) {
            backgroundPlate = null;
        }
    } // clear


    // Called from:
    //     setCompoundRefPoints
    public int setModelReferencePoint(String psModelName, float pfCentroidX, float pfCentroidY, float pfCentroidZ) {
        Scene aScene = this.sceneListHead;
        SceneElement theModel;
        String msgText;

        if(aScene.nextEntry == null) {
            Globals.statusPrint("setModelReferencePoint: sceneList has no scene object.");
            return -2; 
        }

        aScene = aScene.nextEntry;
        theModel = aScene.head;
        boolean found = false;

        while (theModel != null) {
            if(psModelName.equalsIgnoreCase(theModel.modelName)) {
                theModel.pointOfReference.x = pfCentroidX;
                theModel.pointOfReference.y = pfCentroidY;
                theModel.pointOfReference.z = pfCentroidZ;
                found = true;
            }
            theModel = theModel.nextEntry;  // Get the pointer to next model
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

        if(aScene.nextEntry == null) {
            Globals.statusPrint("setCompoundRefPoints: sceneList has no scene object.");
            return -2; 
        }

        aScene = aScene.nextEntry;
        theModel = aScene.head;
        // int found = FALSE; // this variable is not used
        int modelCounter = 0;
        boolean prevModelIsACompoundMember = false;

        while (theModel != null) {
            if(theModel.modelType == COMPOUND) {
                modelName = theModel.modelName;
                bucketX = 0.0f;
                bucketY = 0.0f;
                bucketZ = 0.0f;
                modelCounter = 0;
            }

            if(theModel.compoundModelMember) {
                bucketX += theModel.pointOfReference.x;
                bucketY += theModel.pointOfReference.y;
                bucketZ += theModel.pointOfReference.z;
                modelCounter++;
            }

            if((!theModel.compoundModelMember) && (prevModelIsACompoundMember)) {
                centroidX = bucketX / modelCounter;
                centroidY = bucketY / modelCounter;
                centroidZ = bucketZ / modelCounter;
                setModelReferencePoint(modelName, centroidX, centroidY, centroidZ);
            }

            prevModelIsACompoundMember = theModel.compoundModelMember;
            theModel = theModel.nextEntry;  // Get the pointer to next model
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

        if(aScene.nextEntry == null) {
            Globals.statusPrint("copyRefPoints: sceneList has no scene object.");
            return -2; 
        }

        aScene = aScene.nextEntry;
        theModel = aScene.head;
        while (theModel != null) {
            if((!theModel.definedRefPoint) && (theModel.modelType != COMPOUND)) {
                // The following method sets centroidX, centroidY, and centroidZ (all of type Float)
                theModel.screenObject.currentShape.getReferencePoint(centroidX, centroidY, centroidZ);

                theModel.pointOfReference.x = centroidX; 
                theModel.pointOfReference.y = centroidY;
                theModel.pointOfReference.z = centroidZ;
            }

            theModel = theModel.nextEntry;  // Get the pointer to next model
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
        theScene = theScene.nextEntry;  // Skip over the list header
        if(theScene == null) {
            return -1;
        }

        SceneElement theModel = theScene.head;
        int modelCounter = 0;

        while (theModel != null) {
            if(
            modelCounter == 0 && 
            theModel.warpIndicator == false &&
            theModel.blendIndicator == false) {
                distances[modelCounter] = 999999999.9f;  // set the distance of the backdrop image
                models[modelCounter] = theModel;
                modelCounter++;
            } else {
                if(theModel.modelType == IMAGE || theModel.modelType == SHAPE) {
                    centroidX = theModel.screenObject.currentShape.originX;
                    centroidY = theModel.screenObject.currentShape.originY;
                    centroidZ = theModel.screenObject.currentShape.originZ;
                    modelDistance = Globals.getDistance3d(viewX, viewY, viewZ, centroidX, centroidY, centroidZ);
                    distances[modelCounter] = modelDistance;
                    models[modelCounter] = theModel;
                    modelCounter++;
                }

                if(theModel.modelType == QUADMESH) {
                    centroidX = 0.0f;
                    centroidY = 0.0f;
                    centroidZ = 0.0f;
                    modelDistance = Globals.getDistance3d(viewX, viewY, viewZ, centroidX, centroidY, centroidZ);
                    distances[modelCounter] = modelDistance;
                    models[modelCounter] = theModel;
                    modelCounter++;
                }
            }
            
            theModel = theModel.nextEntry;
        }

        numModels = modelCounter;
        if(depthSortingEnabled) {
            Globals.insertionSort2(distances, models, numModels);
        }
        
        return 0;
    } // depthSort

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