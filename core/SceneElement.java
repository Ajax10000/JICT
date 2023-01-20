package core;

import globals.Globals;

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
    public boolean ictdebug = false;

    // Changed from private to public as it is used by SceneList
    // Name of the Model
    // Model <modelName>
    public  String modelName;
    
    // Motion file for the model
    private String modelMotionPath;
    
    // Contains motion path if moving model
    // Changed from private to public as it is used by SceneList
    public MotionPath modelMotion;

    // 1 = image, 2 = shape, 3 = quadMesh, 4 = compound (see ict.h for more)
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
    // Changed from private to public as it is used by SceneList
    public int modelType;

    // 1 if this model is a member of a compound model, 0 if not.
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
    // Set in SceneList.copyRefPoints
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

    // Pointer to screen renderable representation
    // Changed from private to public as it is used by SceneList
    public RenderObject screenObject;

    // 1 = use alpha blending, 0 = no alpha blending
    // Model <modelName> [Blend|NoBlend]
    // Changed from int to boolean
    // Changed from private to public as it is used by SceneList
    public boolean blendIndicator;

    // 1 = warp image, 0 = do not warp image
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp]
    // Changed from private to public as it is used by SceneList
    // Changed from int to boolean
    public boolean warpIndicator;

    // Default = 1.0 (used for shadows)
    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha>
    // Changed from private to public as it is used 
    // by SceneList in method adjustTransforms
    public float alphaScale;

    // Rotation in Angles
    // Rotation <rx>, <ry>, <rz>
    // Changed from private to public as it is used by SceneList
    public Point3d rotation;
    
    // Scale factor. < 1 = contraction.  > 1 = expansion.
    // Scale <sx>, <sy>, <sz>
    // Changed from private to public as it is used by SceneList
    public Point3d scale;

    // Translation in Pixels
    // Translation <tx>, <ty>, <tz>
    // Changed from private to public as it is used by SceneList
    public Point3d translation;

    // Optional. Alpha image pathname
    // [AlphaImagePath [None|<pathName>]]
    // Changed from private to public as it is used by SceneList
    public String alphaPath;

    // AdjustColor [Target|Relative] <R> <G> <B>
    // If adjustment type is 'Relative', the RGB value is added to
    // the model's image prior to compositing
    // If the adjustment type is: 'Target' then the image colors are 'moved' to the target RGB value
    // Only set in the constructor, and otherwise not used.
    private Color colorAdjustment; 

    // AdjustColor [Target|Relative] <R> <G> <B>
    // 'Relative' or 'Target'
    // Changed from private to public as it is used by SceneList
    public String adjustmentType;

    // 1 if constructor successful
    // Changed from int to boolean
    // Changed fromprivate to public as it is used
    // by SceneList in method preview
    public boolean valid;
    
    // Point to previous SceneElement
    // Changed from private to public as it is used 
    // by SceneList in method addSceneElement
    public SceneElement prevEntry;

    // Point to next SceneElement
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
            msgBuffer = "Constructor. Size of sceneElement: " + sizeLowerLimit();
            Globals.statusPrint(msgBuffer);
        }
    } // SceneElement ctor


    // Called from:
    //     SceneList.display
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


    // Called from:
    //     SceneList.addSceneElement
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


    // Class Scene also has a writeFile method. 
    // Both Scene.writeFile and SceneElement.writeFile are called from 
    // SceneList.writeList.
    // Called from:
    //     SceneList.writeList
    // TODO: Replace ofstream with a FileStream
    void writeFile(BufferedWriter fileout) {
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
        if(!this.blendIndicator) sBlendArray = "NoBlend";
        if(!this.warpIndicator) sWarpArray = "NoWarp";
        if(this.modelType == IMAGE) sModelTypeArray = "Image";
        if(this.modelType == SHAPE) sModelTypeArray = "Shape";
        if(this.modelType == QUADMESH) sModelTypeArray = "QuadMesh";
        if(this.modelType == COMPOUND) sModelTypeArray = "Compound";
        if(this.modelType == LIGHTSOURCE) sModelTypeArray = "LightSource";

        // Write out the sceneElement information, the reference point is optionally saved
        String thisObjectAsString;
        if(definedRefPoint) {
            thisObjectAsString = "Model " + sModelNameArray + 
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
        } else {
            thisObjectAsString = "Model " + sModelNameArray + 
                " " + sBlendArray + " " + sWarpArray +
                " AlphaScale " + this.alphaScale + " " + sModelTypeArray + "\n" +
                "FileName " + this.fileName + "\n" +
                "MotionPath " + this.modelMotionPath + "\n" +
                "AlphaImagePath " + this.alphaPath + "\n" +
                "Rotation "    + this.rotation.x    + "," + this.rotation.y    + "," + this.rotation.z + "\n" +
                "Scale "       + this.scale.x       + "," + this.scale.y       + "," + this.scale.z + "\n" +
                "Translation " + this.translation.x + "," + this.translation.y + "," + this.translation.z + "\n\n";
        }
        
        try {
            fileout.write(thisObjectAsString);
        } catch(IOException ioe) {
            // TODO: Need to return an error indicator
        }
    } // writeFile


    // This method returns an estimate of the size of the SceneElement object
    // It does not take into account static field values, as those would be 
    // part of the SceneElement class, not a SceneElement object
    public int sizeLowerLimit() {
        int mySize = 0;
        int booleanFieldsSizeInBits = 0;
        int booleanFieldsSize = 0;
        int intFieldsSize = 0;
        int floatFieldsSize = 0;
        int referenceFieldsSize = 0;

        /*
        public boolean ictdebug = false;
        public boolean compoundModelMember;
        public boolean definedRefPoint;	 
        public boolean blendIndicator;
        public boolean warpIndicator; 
        private boolean valid;
        public int modelType;
        public int statusIndicator;
        public float alphaScale;
        public  String modelName;
        private String modelMotionPath;
        public String fileName;
        public String colorAdjustedPath;
        public String alphaPath;
        public String adjustmentType;
        private Color colorAdjustment; 
        public MotionPath modelMotion;
        public Point3d pointOfReference;   
        public Point3d rotation;
        public Point3d scale;
        public Point3d translation;
        public RenderObject screenObject;
        public SceneElement prevEntry;
        public SceneElement nextEntry;
        */

        booleanFieldsSizeInBits = 6; // 6 booleans
        booleanFieldsSize = 1; // 6 bits fit in a byte
        intFieldsSize = 2*4; // 2 ints
        floatFieldsSize = 1*4; // 1 float
        referenceFieldsSize = 15*4; // 15 references to objects
        mySize = booleanFieldsSize + intFieldsSize + floatFieldsSize + referenceFieldsSize;

        return mySize;
    } // sizeLowerLimit
} // class SceneElement