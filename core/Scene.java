package core;

import globals.Globals;

import java.io.BufferedWriter;
import java.io.IOException;

import motion.MotionPath;

import structs.Point3d;

public class Scene {
    private boolean bIctDebug = false;

    // Name of the Scene
    // Changed from private to public as it is used by SceneList
    public String msSceneName;

    // Viewpoint Path file for a sequence
    private String msSensorPath;

    // Has motion path if moving viewpoint
    // Changed from private to public as it is used by SceneList
    // Affects SceneList.getViewMatrix
    public MotionPath mSensorMotion;

    // 1 = Still, 2 = sequence
    // Changed from private to public as it is used by SceneList
    public int miSequenceType;

    // 1 = Black and White, 2 = RGB Color
    // Changed from private to public as it is used by SceneList
    public int miColorMode;

    // Number of rows (y) in output image
    // Changed from private to public as it is used by SceneList
    public int miOutputRows;

    // Number of columns(X) in output image
    // Changed from private to public as it is used by SceneList
    public int miOutputColumns;

    // viewPoint Angles
    // Changed from private to public as it is used by SceneList
    // Read in: 
    //     SceneList.getViewPoint
    //     SceneList.getViewTransform
    // Written to in:
    //     SceneList.setViewTransform
    public Point3d mRotationPt;

    // viewPoint location relative to (0,0,0)
    // Changed from private to public as it is used by SceneList
    // Read in: 
    //     SceneList.getViewPoint
    //     SceneList.getViewTransform
    // Written to in:
    //     SceneList.setViewTransform
    public Point3d mTranslationPt;

    // points to last Scene element
    // Changed from private to public as it is used 
    // by SceneList in method addSceneElement
    public SceneElement mTail;

    // points to first Scene element
    // Changed from private to public as it is accessed by SceneList
    public SceneElement mHead;
    
    // points to current scene element
    // Changed from private to public as it is accessed by SceneList
    // Set in SceneList.setCurrentModel
    public SceneElement mCurrentSceneElement; 

    // Changed from private to public as it is accessed by SceneList
    public Scene mPrevEntry;

    // Changed from private to public as it is accessed by SceneList
    public Scene mNextEntry;

    // 1 if constructor successful
    // I changed this from int to boolean
    private boolean mbIsValid;  
    // friend class sceneList;


    // Called from:
    //     SceneList constructor
    //     SceneList.addScene
    public Scene(String psName, int piSeqType, int piNumOutCols, int piNumOutRows,
    int piColorMode, Point3d pRtPt, Point3d pTrPt, String psSensorpth) {
        if (bIctDebug) {
            Globals.statusPrint("Scene Constructor.");
        }
    
        mbIsValid = true;
        this.msSceneName = psName;
        this.miSequenceType = piSeqType;
        this.miOutputColumns = piNumOutCols;
        this.miOutputRows = piNumOutRows;
        this.miColorMode = piColorMode;
  
        this.mRotationPt = new Point3d();
        this.mRotationPt.x = pRtPt.x;
        this.mRotationPt.y = pRtPt.y;
        this.mRotationPt.z = pRtPt.z;
    
        this.mTranslationPt = new Point3d();
        this.mTranslationPt.x = pTrPt.x;
        this.mTranslationPt.y = pTrPt.y;
        this.mTranslationPt.z = pTrPt.z;
    
        this.msSensorPath = psSensorpth;
        this.mSensorMotion = null;

        if(
        this.msSensorPath.length() > 1 && 
        !this.msSensorPath.equalsIgnoreCase("NONE")) {
            // The view point is moving
            this.mSensorMotion = new MotionPath();
            int iStatus = this.mSensorMotion.readMotion(msSensorPath);
            if (iStatus != 0) {  // if the motion file could not be read,
                Globals.statusPrint("Scene: Moving View Point has invalid motion file");
                this.mSensorMotion = null;
                this.mbIsValid = false;
            }
        }

        this.mCurrentSceneElement = null;
        this.mTail = null;
        this.mHead = null;
        this.mPrevEntry = null;
        this.mNextEntry = null;
    } // Scene ctor


    // Called from:
    //     SceneList.addScene
    public boolean isValid() {
        return this.mbIsValid;
    } // isValid


    public void finalize() {
        if (bIctDebug) {
            Globals.statusPrint("Scene Destructor");
        }
    } // finalize


    // Class SceneElement also has a writeFile method. 
    // Both Scene.writeFile and SceneElement.writeFile are called from 
    // SceneList.writeList.
    // Called from:
    //     SceneList.writeList
    public void writeFile(BufferedWriter pFileout) {
        String sSequenceArray, sColorArray;

        // Creates the scene portion of a scene file
        sSequenceArray = "Sequence";
        sColorArray = "Color";
        if(this.miSequenceType == 1) {
            sSequenceArray = "Still";
        }
        if(this.miColorMode == 1) {
            sColorArray = "Monochrome";
        }
        
        String thisObjectAsString = "scene " + this.msSceneName + " " + sSequenceArray + " " 
            + this.miOutputRows + "," + this.miOutputColumns + " " + sColorArray + "\n" +
            "Rotation "    + this.mRotationPt.x    + "," + this.mRotationPt.y    + "," + this.mRotationPt.z + "\n" +
            "Translation " + this.mTranslationPt.x + "," + this.mTranslationPt.y + "," + this.mTranslationPt.z + "\n" + 
            "MotionPath "  + this.msSensorPath + "\n\n";

        try {
            pFileout.write(thisObjectAsString);
        } catch(IOException ioe) {
            // TODO: Need to return an error indicator!
        }
    } // writeFile


    // This method came from SCENELST.H
    // Apparently used for debugging purposes.
    // Called from:
    //     SceneList.display
    public void display() {
        System.out.print(toString());
    } // display


    // I added this method
    public String toString() {
        String thisObject = "\n" + "SceneName: " + this.msSceneName
            + " SeqType: " + this.miSequenceType
            + " ViewPoint Rotation, Translation:" + "\n" +
            this.mRotationPt.x    + " " + this.mRotationPt.y    + " " + this.mRotationPt.z +" " +
            this.mTranslationPt.x + " " + this.mTranslationPt.y + " " + this.mTranslationPt.z + "\n"
            + "SensorPath: " + this.msSensorPath
            + " Head: " + this.mHead
            + " Tail: " + this.mTail
            + " Prev " + this.mPrevEntry
            + " Next " + this.mNextEntry; 

        return thisObject;
    } // toString
} // class Scene