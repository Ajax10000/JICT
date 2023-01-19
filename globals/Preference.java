package globals;

public class Preference {
    private String processLogPath;
    private String sceneFileDir;
    private String shapeFileDir;
    private String sequenceFileDir;
    private String outputImageDir;
    private String inputImageDir;
    private String maskDir;
    private String VRMLDir;
    private String VRMLLogPath;
    private String warpTestPath;

    public static final int ProcessLog            =  1;
    public static final int SceneFileDirectory    =  2;
    public static final int ShapeFileDirectory    =  3;
    public static final int SequenceFileDirectory =  4;
    public static final int OutputImageDirectory  =  5;
    public static final int InputImageDirectory   =  6;
    public static final int MaskImageDirectory    =  7;
    public static final int WarpTestPath          =  8;
    public static final int VRMLDirectory         =  9;
    public static final int VRMLLog               = 10;
/*
public:
  preference();
  ~preference();
  char *getPath(int pathIndicator);
  void setPath(int pathIndicator, char *pathName);
  int preference::checkPaths();
*/

    // This constructor came from UTILS.CPP
    public Preference() {
        // Read the default pathnames from the ict20.ini file
        GetPrivateProfileString("Directories", "InputDir", ",,,", inputImageDir,
            inputImageDir.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Directories", "OutputDir", ",,,", outputImageDir,
            outputImageDir.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Directories", "SceneDir", ",,,", sceneFileDir,
            sceneFileDir.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Directories", "SequenceDir", ",,,", sequenceFileDir,
            sequenceFileDir.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Directories", "ShapeDir", ",,,", shapeFileDir,
            shapeFileDir.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Directories", "MaskDir", ",,,", maskDir,
            maskDir.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Directories", "VRMLDir", ",,,", VRMLDir,
            VRMLDir.length(), "c:\\windows\\ict20.ini");
        
        GetPrivateProfileString("Paths", "LogPath", ",,,", processLogPath,
            processLogPath.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Paths", "WarpTestPath",",,,", warpTestPath,
            warpTestPath.length(), "c:\\windows\\ict20.ini");
        GetPrivateProfileString("Paths", "VRMLLogPath",",,,", VRMLLogPath,
            VRMLLogPath.length(), "c:\\windows\\ict20.ini");
    } // Preference ctor
      
    
    // This method came from UTILS.CPP
    // Called from:
    //     MainFrame.onToolsRenderVrmlFile
    public String getPath(int pathIndicator){
        switch(pathIndicator) {
        case ProcessLog:
            return processLogPath;
      
        case SceneFileDirectory:
            return sceneFileDir;
      
        case ShapeFileDirectory:
            return shapeFileDir;
      
        case SequenceFileDirectory:
            return sequenceFileDir;
      
        case OutputImageDirectory:
            return outputImageDir;
      
        case InputImageDirectory:
            return inputImageDir;
      
        case MaskImageDirectory:
            return maskDir;
      
        case VRMLDirectory:
            return VRMLDir;
      
        case WarpTestPath:
            return warpTestPath;
      
        case VRMLLog:
            return VRMLLogPath;
      
        default:
            String msgText = "getPath: unknown option. " + pathIndicator;
            Globals.statusPrint(msgText);
            return "";
        } // switch
    } // getPath


    // This method came from UTILS.CPP
    public void setPath(int pathIndicator, String thePath) {
        switch(pathIndicator){
        case ProcessLog:
            processLogPath = new String(thePath);
            return;
      
        case SceneFileDirectory:
            sceneFileDir = new String(thePath);
            return;
      
        case ShapeFileDirectory:
            shapeFileDir = new String(thePath);
            return;
      
        case SequenceFileDirectory:
            sequenceFileDir = new String(thePath);
            return;
      
        case OutputImageDirectory:
            outputImageDir = new String(thePath);
            return;
      
        case InputImageDirectory:
            inputImageDir = new String(thePath);
            return;
      
        case MaskImageDirectory:
            maskDir = new String(thePath);
            return;
      
        default:
            String msgText = "setPath: unknown option. " + pathIndicator;
            Globals.statusPrint(msgText);
            return;
        } // switch
    } // setPath
} // class Preference