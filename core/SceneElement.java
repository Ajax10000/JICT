package core;

import globals.Globals;

import java.awt.Color;

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

public class SceneElement {
    public boolean ictdebug = false;

    // Changed from private to public as it is used by SceneList
    // Name of the Model
    public  String modelName;
    
    // Motion file for the model
    private String modelMotionPath;
    
    // Contains motion path if moving model
    // Changed from private to public as it is used by SceneList
    public MotionPath modelMotion;

    // 1 = image, 2 = shape, 3 = quadMesh, 4 = compound (see ict.h for more)
    // Changed from private to public as it is used by SceneList
    public int modelType;

    // 1 if this model is a member of a compount model, 0 if not.
    // Changed from private to public as it is used by SceneList
    // Changed from int to boolean
    public boolean compoundModelMember;

    // TRUE if the reference point is user defined.
    // (the centroid is used by default) if defaultRepoint3d is FALSE, then
    // it is specified in the next data member.
    // Changed from int to boolean
    // Changed from private to public as it is used by SceneList
    public boolean definedRefPoint;	  

    // The model's point of reference. Default is the model's centroid.
    // This is the point about which the model is transformed. i.e. its origin.
    // Changed from private to public as it is used by SceneList
    public Point3d pointOfReference;   

    // Path Name of image
    // Changed from private to public as it is used by SceneList
    public String fileName;

    // Path Name of an optional color adjusted image
    // Changed from private to public as it is used by SceneList
    public String colorAdjustedPath;

    // 1 indicates bad file name ==> object ignored
    // Changed from private to public as it is used by SceneList
    public int statusIndicator;

    // pointer to screen renderable representation
    // Changed from private to public as it is used by SceneList
    public RenderObject screenObject;

    // 1 = use alpha blending, 0 = no alpha blending
    // Changed from int to boolean
    // Changed from private to public as it is used by SceneList
    public boolean blendIndicator;

    // 1 = warp image, 0 = do not warp image
    // Changed from private to public as it is used by SceneList
    // Changed from int to boolean
    public boolean warpIndicator;

    // Default = 1.0 (used for shadows)
    // Changed from private to public as it is used 
    // by SceneList in method adjustTransforms
    public float alphaScale;

    // Rotation in Angles
    // Changed from private to public as it is used by SceneList
    public Point3d rotation;
    
    // Scale factor. < 1 = contraction.  > 1 = expansion.
    // Changed from private to public as it is used by SceneList
    public Point3d scale;

    // Translation in Pixels
    // Changed from private to public as it is used by SceneList
    public Point3d translation;

    // Optional. Alpha image pathname
    // Changed from private to public as it is used by SceneList
    public String alphaPath;

    // If adjustment type is 'Relative', the RGB value is added to
    // the model's image prior to compositing
    // If the adjustment type is: 'Target' then the image colors are 'moved' to the target RGB value
    // Only set in the constructor, and otherwise not used.
    private Color colorAdjustment; 

    // 'Relative' or 'Target'
    // Changed from private to public as it is used by SceneList
    public String adjustmentType;

    // 1 if constructor successful
    // Changed from int to boolean
    private boolean valid;
    
    // Point to previous Scene element
    // Changed from private to public as it is used 
    // by SceneList in method addSceneElement
    public SceneElement prevEntry;

    // Point to next Scene element
    // Changed from private to public as it is used
    // by SceneList in method addSceneElement
    public SceneElement nextEntry;
  // friend class sceneList;

    // Model Types
    public static final int IMAGE        = 1;
    public static final int SHAPE        = 2;
    public static final int QUADMESH     = 3;
    public static final int COMPOUND     = 4;
    public static final int LIGHTSOURCE  = 5;


    // Called from:
    //     SceneList.addSceneElement
    public SceneElement(String mName, String fName, boolean blendI,
    int theType, boolean warpI, float aScale, 
    Point3d rt, Point3d sc, Point3d tr, 
    String theMotionPath, String theAlphaPath,
    boolean compoundMMember, Color anAdjustment, 
    String adjType, String colorAdjPath,
    boolean definedRefPt, Point3d refPoint) {

        this.statusIndicator = 0;
        this.modelName = mName;
        this.fileName = fName;
        this.blendIndicator = blendI;
        this.modelType = theType;
        this.warpIndicator = warpI;
        this.alphaScale = aScale;
        this.adjustmentType = adjType;
        this.colorAdjustedPath = colorAdjPath;
        this.colorAdjustment = new Color(anAdjustment.getRed(), anAdjustment.getGreen(), anAdjustment.getBlue());
        this.rotation = new Point3d();
        this.pointOfReference = new Point3d();

        this.definedRefPoint = definedRefPt;
        if (this.definedRefPoint) {
            this.pointOfReference.x = refPoint.x;
            this.pointOfReference.y = refPoint.y;
            this.pointOfReference.z = refPoint.z;
        }

        if(rt != null) {				 //accomodate the case where NULL is passed in
            rotation.x = rt.x;
            rotation.y = rt.y;
            rotation.z = rt.z;
        } else {
            rotation.x = 0.0f;
            rotation.y = 0.0f;
            rotation.z = 0.0f;
        }

        scale = new Point3d();
        if(sc != null) {
            scale.x = sc.x;
            scale.y = sc.y;
            scale.z = sc.z;
        } else {
            scale.x = 0.0f;
            scale.y = 0.0f;
            scale.z = 0.0f;
        }

        translation = new Point3d();
        if(tr != null) {
            translation.x = tr.x;
            translation.y = tr.y;
            translation.z = tr.z;
        } else {
            translation.x = 0.0f;
            translation.y = 0.0f;
            translation.z = 0.0f;
        }
    
        this.screenObject = null;

        // Handle moving models
        this.valid = true;
        this.modelMotionPath = theMotionPath;
        this.modelMotion = null;

        if(modelMotionPath.length() > 1 && !modelMotionPath.equalsIgnoreCase("NONE")) {
            // The model is moving
            this.modelMotion = new MotionPath();
            int myStatus = this.modelMotion.readMotion(modelMotionPath);
            if (myStatus != 0) {  // if the motion file could not be read,
                Globals.statusPrint("SceneList.ReadList: Moving Model has invalid motion file");
                this.modelMotion = null;
                this.valid = false;
            }
        }

        this.alphaPath = theAlphaPath;
        this.compoundModelMember = compoundMMember;
        this.prevEntry = null;
        this.nextEntry = null;
        if (ictdebug) {
            String msgBuffer;
            msgBuffer = "Constructor. Size of sceneElement: " + sizeof(SceneElement);
            Globals.statusPrint(msgBuffer);
        }
    } // SceneElement ctor


    public void display() {
        System.out.println(toString());
    } // display


    // I added this method
    public String toString() {
        String thisObject = "\n"
        + "Model: " + modelName	+ "modelType: " + modelType + "\n"
        + " Centroid: " + pointOfReference.x + ", " + pointOfReference.y + ", " + pointOfReference.z + "\n"
        + " Blend: " + blendIndicator +
        " Warp: " + warpIndicator +
        " aScale: " + alphaScale +
        " File: " + fileName + " Motion: " + modelMotionPath + "\n" +
        " R S T: " +
        rotation.x    + " " + rotation.y    + " " + rotation.z    + " " +
        scale.x       + " " + scale.y       + " " + scale.z       + " " +
        translation.x + " " + translation.y + " " + translation.z + "\n"
        + " prev " + prevEntry + " next " + nextEntry;

        return thisObject;
    } // toString


    public boolean isValid() {
        return this.valid;
    }  // isValid

    void fshowlist() {  // Display scene's elements traversing
        SceneElement next = this;     // the list in the forward direction.
        while (next != null) {
            next.display();
            next = next.nextEntry;
        } // while
    } // fshowlist


    public void finalize() {
        if (ictdebug) {
            Globals.statusPrint("sceneElement Destructor");
        }
    } // finalize


    void writeFile(ofstream fileout) {
        // Creates the model portion of a scene file
        String sBlendArray, sWarpArray, sModelTypeArray;
        String sModelNameArray;
        sBlendArray = "Blend";
        sWarpArray = "Warp";

        // If the model name is blank, write out a . character
        sModelNameArray = this.modelName;
        if(this.modelName.equals(" ")) {
            sModelNameArray = ".";
        }

        sModelTypeArray = "Image";
        if(this.blendIndicator == false) sBlendArray = "NoBlend";
        if(this.warpIndicator == 0) sWarpArray = "NoWarp";
        if(this.modelType == IMAGE) sModelTypeArray = "Image";
        if(this.modelType == SHAPE) sModelTypeArray = "Shape";
        if(this.modelType == QUADMESH) sModelTypeArray = "QuadMesh";
        if(this.modelType == COMPOUND) sModelTypeArray = "Compound";
        if(this.modelType == LIGHTSOURCE) sModelTypeArray = "LightSource";

        // Write out the sceneElement information, the reference point is optionally saved
        if(definedRefPoint) {
            String thisObjectAsString = "Model " + sModelNameArray + 
                " " + sBlendArray + " " + sWarpArray +
                " AlphaScale " + this.alphaScale + " " + sModelTypeArray + "\n" +
                "FileName " + this.fileName + "\n" +
                "MotionPath " + this.modelMotionPath + "\n" +
                "AlphaImagePath " + this.alphaPath + "\n" +
                "Rotation "    + this.rotation.x    + "," + this.rotation.y    + "," + this.rotation.z + "\n" +
                "Scale "       + this.scale.x       + "," + this.scale.y       + "," + this.scale.z + "\n" +
                "Translation " + this.translation.x + "," + this.translation.y + "," + this.translation.z + "\n" +
                "ReferencePoint " + this.pointOfReference.x + "," + this.pointOfReference.y + 
                "," + this.pointOfReference.z + "\n\n";

            fileout << thisObjectAsString;
        } else {
            String thisObjectAsString = "Model " + sModelNameArray + 
                " " + sBlendArray + " " + sWarpArray +
                " AlphaScale " + this.alphaScale + " " + sModelTypeArray + "\n" +
                "FileName " + this.fileName + "\n" +
                "MotionPath " + this.modelMotionPath + "\n" +
                "AlphaImagePath " + this.alphaPath + "\n" +
                "Rotation "    + this.rotation.x    + "," + this.rotation.y    + "," + this.rotation.z + "\n" +
                "Scale "       + this.scale.x       + "," + this.scale.y       + "," + this.scale.z + "\n" +
                "Translation " + this.translation.x + "," + this.translation.y + "," + this.translation.z + "\n\n";

            fileout << thisObjectAsString;
        }
    } // writeFile
} // class SceneElement