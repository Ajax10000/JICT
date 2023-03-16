package core;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;

import motion.MotionPath;

import structs.Point3d;

/*  Project ict

 Copyright � 1997 J. Wiley & Sons and Tim Wittenburg.  All Rights Reserved.

 SUBSYSTEM: ict.exe Application
 FILE:      model.cpp
 AUTHOR:    Tim Wittenburg


	 OVERVIEW
	 ========
	 Continuation of SceneList, Scene and SceneElement member functions.
*/

// #include "ict20.h"
// extern char g_msgText[MESSAGEMAX];
// void statusPrint(char *message);

/* 
    The SceneElement class data members correspond to the model attributes in a 
    scene (.scn) file. The Model attributes in a scene file have the following 
    format:
    Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
    FileName <pathName>
    MotionPath [None|<pathName>]
    [AlphaImagePath [None|<pathName>]]
    AdjustColor [Target|Relative] <R> <G> <B>
    Rotation <rx>, <ry>, <rz>
    Scale <sx>, <sy>, <sz>
    Translation <tx>, <ty>, <tz>
*/
public class SceneElement {
    private boolean bIctDebug = false;

    // Changed from private to public as it is used by SceneList
    // Name of the Model
    // Model <modelName>
    public  String msModelName;
    
    // Motion file for the model
    private String msModelMotionPath;
    
    // Contains motion path if moving model
    // Changed from private to public as it is used by SceneList
    public MotionPath mModelMotion;

    // 1 = image, 2 = shape, 3 = quadMesh, 4 = compound (see ict.h for more)
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
    // Changed from private to public as it is used by SceneList
    public int miModelType;

    // 1 if this model is a member of a compound model, 0 if not.
    // Changed from private to public as it is used by SceneList
    // Changed from int to boolean
    public boolean mbCompoundModelMember;

    // TRUE if the reference point is user defined.
    // (the centroid is used by default) if defaultRepoint3d is FALSE, then
    // it is specified in the next data member.
    // Changed from int to boolean
    // Changed from private to public as it is used by SceneList
    public boolean mbDefinedRefPoint;	  

    // The model's point of reference. Default is the model's centroid.
    // This is the point about which the model is transformed. i.e. its origin.
    // Changed from private to public as it is used by SceneList
    // Set in SceneList.copyRefPoints
    public Point3d pointOfReference;   

    // Path Name of image
    // Changed from private to public as it is used by SceneList
    public String msFileName;

    // Path Name of an optional color adjusted image
    // Changed from private to public as it is used by SceneList
    public String msColorAdjustedPath;

    // 1 indicates bad file name ==> object ignored
    // Changed from private to public as it is used by SceneList
    public int miStatusIndicator;

    // Pointer to screen renderable representation
    // Changed from private to public as it is used by SceneList
    public RenderObject mScreenRdrObject;

    // 1 = use alpha blending, 0 = no alpha blending
    // Model <modelName> [Blend|NoBlend]
    // Changed from int to boolean
    // Changed from private to public as it is used by SceneList
    public boolean mbBlendIndicator;

    // 1 = warp image, 0 = do not warp image
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp]
    // Changed from private to public as it is used by SceneList
    // Changed from int to boolean
    public boolean mbWarpIndicator;

    // Default = 1.0 (used for shadows)
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha>
    // Changed from private to public as it is used 
    // by SceneList in method adjustTransforms
    public float mfAlphaScale;

    // Rotation in Angles
    // Rotation <rx>, <ry>, <rz>
    // Changed from private to public as it is used by SceneList
    public Point3d mRotation;
    
    // Scale factor. < 1 = contraction.  > 1 = expansion.
    // Scale <sx>, <sy>, <sz>
    // Changed from private to public as it is used by SceneList
    public Point3d mScale;

    // Translation in Pixels
    // Translation <tx>, <ty>, <tz>
    // Changed from private to public as it is used by SceneList
    public Point3d mTranslation;

    // Optional. Alpha image pathname
    // [AlphaImagePath [None|<pathName>]]
    // Changed from private to public as it is used by SceneList
    public String msAlphaPath;

    // AdjustColor [Target|Relative] <R> <G> <B>
    // If adjustment type is 'Relative', the RGB value is added to
    // the model's image prior to compositing
    // If the adjustment type is: 'Target' then the image colors are 'moved' to the target RGB value
    // Only set in the constructor, and otherwise not used.
    private Color mAdjustmentColor; 

    // AdjustColor [Target|Relative] <R> <G> <B>
    // 'Relative' or 'Target'
    // Changed from private to public as it is used by SceneList
    public String msAdjustmentType;

    // 1 if constructor successful
    // Changed from int to boolean
    // Changed from private to public as it is used
    // by SceneList in method preview
    public boolean mbValid;
    
    // Point to previous SceneElement
    // Changed from private to public as it is used 
    // by SceneList in method addSceneElement
    public SceneElement mPrevEntry;

    // Point to next SceneElement
    // Changed from private to public as it is used
    // by SceneList in method addSceneElement
    public SceneElement mNextEntry;
  // friend class sceneList;


    // This constructor originally came from MODEL.CPP
    // Called from:
    //     SceneList.addSceneElement
    public SceneElement(String psmName, String psfName, boolean pbBlendI,
    int piType, boolean pbWarpI, float pfScale, 
    Point3d pRtPt, Point3d pScPt, Point3d pTrPt, 
    String psMotionPath, String psAlphaPath,
    boolean pbCompoundMMember, Color pAdjustmentColor, 
    String psAdjType, String psColorAdjPath,
    boolean pbDefinedRefPt, Point3d pRefPoint) {

        this.miStatusIndicator = 0;
        this.msModelName = psmName;
        this.msFileName = psfName;
        this.mbBlendIndicator = pbBlendI;
        this.miModelType = piType;
        this.mbWarpIndicator = pbWarpI;
        this.mfAlphaScale = pfScale;
        this.msAdjustmentType = psAdjType;
        this.msColorAdjustedPath = psColorAdjPath;
        this.mAdjustmentColor = new Color(pAdjustmentColor.getRed(), pAdjustmentColor.getGreen(), pAdjustmentColor.getBlue());

        this.pointOfReference = new Point3d(); // Constructor sets x, y and z members to 0.0f
        this.mbDefinedRefPoint = pbDefinedRefPt;
        if (this.mbDefinedRefPoint) {
            this.pointOfReference.x = pRefPoint.x;
            this.pointOfReference.y = pRefPoint.y;
            this.pointOfReference.z = pRefPoint.z;
        }

        mRotation = new Point3d(); // Constructor sets x, y and z members to 0.0f
        if(pRtPt != null) { 
            mRotation.x = pRtPt.x;
            mRotation.y = pRtPt.y;
            mRotation.z = pRtPt.z;
        }

        mScale = new Point3d(); // Constructor sets x, y and z members to 0.0f
        if(pScPt != null) {
            mScale.x = pScPt.x;
            mScale.y = pScPt.y;
            mScale.z = pScPt.z;
        }

        mTranslation = new Point3d(); // Constructor sets x, y and z members to 0.0f
        if(pTrPt != null) {
            mTranslation.x = pTrPt.x;
            mTranslation.y = pTrPt.y;
            mTranslation.z = pTrPt.z;
        }
    
        this.mScreenRdrObject = null;

        // Handle moving models
        this.mbValid = true;
        this.msModelMotionPath = psMotionPath;
        this.mModelMotion = null;

        if(
        (msModelMotionPath.length() > 1) && 
        !msModelMotionPath.equalsIgnoreCase("NONE")) {
            // The model is moving
            this.mModelMotion = new MotionPath();
            int iStatus = this.mModelMotion.readMotion(msModelMotionPath);
            if (iStatus != 0) {  // if the motion file could not be read,
                Globals.statusPrint("SceneList.ReadList: Moving Model has invalid motion file");
                this.mModelMotion = null;
                this.mbValid = false;
            }
        }

        this.msAlphaPath = psAlphaPath;
        this.mbCompoundModelMember = pbCompoundMMember;
        this.mPrevEntry = null;
        this.mNextEntry = null;

        if (bIctDebug) {
            Globals.statusPrint("SceneElement constructor.");
        }
    } // SceneElement ctor


    // This method originally came from SCENELST.H
    // Apparently used for debugging purposes.
    // Called from:
    //     fShowList
    //     SceneList.display
    public void display() {
        System.out.println(toString());
    } // display


    // I added this method
    public String toString() {
        String thisObject = "\n"
        + "Model: " + msModelName	+ "modelType: " + miModelType + "\n"
        + " Centroid: " + pointOfReference.x + ", " + pointOfReference.y + ", " + pointOfReference.z + "\n"
        + " Blend: " + mbBlendIndicator +
        " Warp: " + mbWarpIndicator +
        " aScale: " + mfAlphaScale +
        " File: " + msFileName + " Motion: " + msModelMotionPath + "\n" +
        " R S T: " +
        mRotation.x    + " " + mRotation.y    + " " + mRotation.z    + " " +
        mScale.x       + " " + mScale.y       + " " + mScale.z       + " " +
        mTranslation.x + " " + mTranslation.y + " " + mTranslation.z + "\n"
        + " prev " + mPrevEntry + " next " + mNextEntry;

        return thisObject;
    } // toString


    // This method originally came from MODEL.CPP
    // Called from:
    //     SceneList.addSceneElement
    public boolean isValid() {
        return this.mbValid;
    }  // isValid


    void fshowlist() {  // Display scene's elements traversing
        SceneElement nextSE = this;     // the list in the forward direction.
        while (nextSE != null) {
            nextSE.display();
            nextSE = nextSE.mNextEntry;
        } // while
    } // fshowlist


    // This method (destructor) originally came from MODEL.CPP
    public void finalize() {
        if (bIctDebug) {
            Globals.statusPrint("sceneElement Destructor");
        }
    } // finalize


    // This method originally came from MODEL.CPP
    // Class Scene also has a writeFile method. 
    // Both Scene.writeFile and SceneElement.writeFile are called from 
    // SceneList.writeList.
    // Called from:
    //     SceneList.writeList
    void writeFile(BufferedWriter pFileout) {
        // Creates the model portion of a scene file
        String sBlendArray, sWarpArray, sModelTypeArray;
        String sModelNameArray;
        sBlendArray = "Blend";
        sWarpArray = "Warp";

        // If the model name is blank, write out a . character
        sModelNameArray = this.msModelName;
        if(this.msModelName.equals(" ")) {
            sModelNameArray = ".";
        }

        sModelTypeArray = "Image";
        if(!this.mbBlendIndicator) sBlendArray = "NoBlend";
        if(!this.mbWarpIndicator) sWarpArray = "NoWarp";
        if(this.miModelType == JICTConstants.I_IMAGE) sModelTypeArray = "Image";
        if(this.miModelType == JICTConstants.I_SHAPE) sModelTypeArray = "Shape";
        if(this.miModelType == JICTConstants.I_QUADMESH) sModelTypeArray = "QuadMesh";
        if(this.miModelType == JICTConstants.I_COMPOUND) sModelTypeArray = "Compound";
        if(this.miModelType == JICTConstants.I_LIGHTSOURCE) sModelTypeArray = "LightSource";

        // Write out the sceneElement information, the reference point is optionally saved
        String thisObjectAsString;
        if(mbDefinedRefPoint) {
            thisObjectAsString = "Model " + sModelNameArray + 
                " " + sBlendArray + " " + sWarpArray +
                " AlphaScale " + this.mfAlphaScale + " " + sModelTypeArray + "\n" +
                "FileName " + this.msFileName + "\n" +
                "MotionPath " + this.msModelMotionPath + "\n" +
                "AlphaImagePath " + this.msAlphaPath + "\n" +
                "Rotation "    + this.mRotation.x    + "," + this.mRotation.y    + "," + this.mRotation.z + "\n" +
                "Scale "       + this.mScale.x       + "," + this.mScale.y       + "," + this.mScale.z + "\n" +
                "Translation " + this.mTranslation.x + "," + this.mTranslation.y + "," + this.mTranslation.z + "\n" +
                "ReferencePoint " + this.pointOfReference.x + "," + this.pointOfReference.y + 
                "," + this.pointOfReference.z + "\n\n";
        } else {
            thisObjectAsString = "Model " + sModelNameArray + 
                " " + sBlendArray + " " + sWarpArray +
                " AlphaScale " + this.mfAlphaScale + " " + sModelTypeArray + "\n" +
                "FileName " + this.msFileName + "\n" +
                "MotionPath " + this.msModelMotionPath + "\n" +
                "AlphaImagePath " + this.msAlphaPath + "\n" +
                "Rotation "    + this.mRotation.x    + "," + this.mRotation.y    + "," + this.mRotation.z + "\n" +
                "Scale "       + this.mScale.x       + "," + this.mScale.y       + "," + this.mScale.z + "\n" +
                "Translation " + this.mTranslation.x + "," + this.mTranslation.y + "," + this.mTranslation.z + "\n\n";
        }
        
        try {
            pFileout.write(thisObjectAsString);
        } catch(IOException ioe) {
            // TODO: Need to return an error indicator
        }
    } // writeFile
} // class SceneElement