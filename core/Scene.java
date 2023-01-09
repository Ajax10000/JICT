package core;

import globals.Globals;

import motion.MotionPath;

import structs.Point3d;

public class Scene {
    boolean ictdebug = false;

    // Name of the Scene
    // Changed from private to public as it is used by SceneList
    public String sceneName;

    // Viewpoint Path file for a sequence
    private String sensorPath;

    // Has motion path if moving viewpoint
    // Changed from private to public as it is used by SceneList
    public MotionPath sensorMotion;

    // 1 = Still, 2 = sequence
    // Changed from private to public as it is used by SceneList
    public int sequenceType;

    // 1 = Black and White, 2 = RGB Color
    // Changed from private to public as it is used by SceneList
    public int colorMode;

    // Number of rows (y) in output image
    // Changed from private to public as it is used by SceneList
    public int outputRows;

    // Number of columns(X) in output image
    // Changed from private to public as it is used by SceneList
    public int outputColumns;

    // viewPoint Angles
    // Changed from private to public as it is used by SceneList
    // Read in: 
    //     SceneList.getViewPoint
    //     SceneList.getViewTransform
    // Written to in:
    //     SceneList.setViewTransform
    public Point3d rotation;

    // viewPoint location relative to (0,0,0)
    // Changed from private to public as it is used by SceneList
    // Read in: 
    //     SceneList.getViewPoint
    //     SceneList.getViewTransform
    // Written to in:
    //     SceneList.setViewTransform
    public Point3d translation;

    // point to last Scene element
    // Changed from private to public as it is used 
    // by SceneList in method addSceneElement
    public SceneElement tail;

    // point to first Scene element
    // Changed from private to public as it is accessed by SceneList
    public SceneElement head;
    
    // point to current scene element
    // Changed from private to public as it is accessed by SceneList
    // Set in SceneList.setCurrentModel
    public SceneElement currentSceneElement; 

    // Changed from private to public as it is accessed by SceneList
    public Scene prevEntry;

    // Changed from private to public as it is accessed by SceneList
    public Scene nextEntry;

    // 1 if constructor successful
    // I changed this from int to boolean
    private boolean valid;  
    // friend class sceneList;


    // Called from:
    //     SceneList.addScene
    public Scene(String sName, int seqType, int numOutCols, int numOutRows,
    int aColorMode, Point3d rt, Point3d tr, String sensorpth) {
        if (ictdebug) {
            String msgBuffer = "Constructor. Size of scene: " + sizeLowerLimit();
            Globals.statusPrint(msgBuffer);
        }
    
        valid = true;
        this.sceneName = sName;
        this.sequenceType = seqType;
        this.outputColumns = numOutCols;
        this.outputRows = numOutRows;
        this.colorMode = aColorMode;
  
        this.rotation = new Point3d();
        this.rotation.x = rt.x;
        this.rotation.y = rt.y;
        this.rotation.z = rt.z;
    
        this.translation = new Point3d();
        this.translation.x = tr.x;
        this.translation.y = tr.y;
        this.translation.z = tr.z;
    
        this.sensorPath = sensorpth;
        this.sensorMotion = null;
        if(
        this.sensorPath.length() > 1 && 
        !this.sensorPath.equalsIgnoreCase("NONE")) {
            // The view point is moving
            this.sensorMotion = new MotionPath();
            int myStatus = this.sensorMotion.readMotion(sensorPath);
            if (myStatus != 0) {  // if the motion file could not be read,
                Globals.statusPrint("Scene: Moving View Point has invalid motion file");
                this.sensorMotion = null;
                this.valid = false;
            }
        }

        this.currentSceneElement = null;
        this.tail = null;
        this.head = null;
        this.prevEntry = null;
        this.nextEntry = null;
    } // Scene ctor


    // Called from:
    //     SceneList.addScene
    boolean isValid() {
        return this.valid;
    } // isValid


    public void finalize() {
        if (ictdebug) {
            Globals.statusPrint("Scene Destructor");
        }
    } // finalize


    // Called from:
    //     SceneList.writeList
    void writeFile(ofstream fileout) {
        String sSequenceArray, sColorArray;

        // Creates the scene portion of a scene file
        sSequenceArray = "Sequence";
        sColorArray = "Color";
        if(this.sequenceType == 1) {
            sSequenceArray = "Still";
        }
        if(this.colorMode == 1) {
            sColorArray = "Monochrome";
        }
        
        String thisObjectAsString = "scene " + this.sceneName + " " + sSequenceArray + " " 
            + this.outputRows + "," + this.outputColumns + " " + sColorArray + "\n" +
            "Rotation "    + this.rotation.x    + "," + this.rotation.y    + "," + this.rotation.z + "\n" +
            "Translation " + this.translation.x + "," + this.translation.y + "," + this.translation.z + "\n" + 
            "MotionPath "  + this.sensorPath + "\n\n";

        fileout << thisObjectAsString;
    } // writeFile


    // This method came from SCENELST.H
    // Called from:
    //     SceneList.display
    void display() {
        System.out.print(toString());
    } // display


    // I added this method
    public String toString() {
        String thisObject = "\n" + "SceneName: " + this.sceneName
            + " SeqType: " + this.sequenceType
            + " ViewPoint Rotation, Translation:" + "\n" +
            this.rotation.x    + " " + this.rotation.y    + " " + this.rotation.z +" " +
            this.translation.x + " " + this.translation.y + " " + this.translation.z + "\n"
            + "SensorPath: " + this.sensorPath
            + " Head: " + this.head
            + " Tail: " + this.tail
            + " Prev " + this.prevEntry
            + " Next " + this.nextEntry; 

        return thisObject;
    } // toString

    public int sizeLowerLimit() {
        int mySize = 0;
        int booleanFieldsSizeInBits = 0;
        int booleanFieldsSize = 0;
        int intFieldsSize = 0;
        int referenceFieldsSize = 0;

        /*
        boolean ictdebug = false;
        private boolean valid;  
        public int sequenceType;
        public int colorMode;
        public int outputRows;
        public int outputColumns;
        public String sceneName;
        private String sensorPath;
        public MotionPath sensorMotion;
        public Point3d rotation;
        public Point3d translation;
        public SceneElement tail;
        public SceneElement head;
        public SceneElement currentSceneElement; 
        public Scene prevEntry;
        public Scene nextEntry;
        */

        booleanFieldsSizeInBits = 2; // 2 booleans
        booleanFieldsSize = 1; // 2 bits fit in a byte
        intFieldsSize = 4*4; // 4 ints
        referenceFieldsSize = 10*4; // 10 references to objects
        mySize = booleanFieldsSize + intFieldsSize + referenceFieldsSize;

        return mySize;
    }
} // class Scene