package fileUtils;

import java.io.File;

public class FileUtils {
    // This method originally came from UTILS.CPP
    public static boolean fileExists(String psPathName) {
        File file = new File(psPathName);

        return file.exists();
    } // fileExists
    

    // Called from:
    //     RenderObject.maskFromShape
    public static boolean deleteFile(String psPathName) {
        File file = new File(psPathName);

        if(file.exists()) {
            return file.delete();
        }

        return false;
    } // deleteFile
    

    // This method originally came from UTILS.CPP
    // Called from:
    //     Globals.motionBlur
    public static void makePath(StringBuffer psbCurrentPath, String psInPath, String psPrefix, int piFrameCounter, String psInSuffix) {
        // sprintf(currentPath, "%s%.31s%#04d%s.bmp\0", inPath, prefix, frameCounter, inSuffix);
        String sPath = String.format("%s%.31s%04d%s.bmp", psInPath, psPrefix, piFrameCounter, psInSuffix);
        psbCurrentPath.append(sPath);
    } // makePath
      

    // This method originally came from UTILS.CPP
    // Called from:
    //     Globals.motionBlur
    public static int getPathPieces(String psFirstImagePath, String psDirectory, 
    String psFileName, String psPrefix, Integer pIFrameNum, String psInSuffix) {
        String sFrameNum;
      
        // _splitpath(psFirstImagePath, sDdrive, psDirectory, psFileName, sDext);
        File inputName = new File(psFirstImagePath);

        // sFileWExt = file name with extension at end of path psFirstImagePath
        String sFileWExt = inputName.getName();

        // Now strip the extension from sFileWExt
        // Note the following sets output parameter psFileName
        psFileName = sFileWExt.substring(0, sFileWExt.lastIndexOf('.'));

        // Set output parameter psDirectory
        psDirectory = inputName.getParent();
        
       // Assumed input:   xxxxx0000c
       // Set output parameter psInSuffix (to c, assuming input xxxxx0000c)
       psInSuffix = psFileName.substring(psFileName.length() - 1);
      
       // Set output parameter pIFrameNum (to 0000, assuming input xxxxx0000c)
       sFrameNum = psFileName.substring(5, 4);
       pIFrameNum = Integer.parseInt(sFrameNum);
       
       // Set output parameter psPrefix (to xxxxx, assuming input xxxxx0000c)
       psPrefix = psFileName.substring(0, 4);
      
        return 0;
    } // getPathPieces


    // This method originally came from SCENELST.CPP
    // Called from:
    //     Globals.createCutout (called 5 times, with psSuffix = "r", "g", "b", "a" and "c")
    //     SceneList.render
    public static void appendFileName(StringBuffer psbOutputFileName, String psPrefix, String psSuffix) {
        // sprintf(psOutputFileName, "%.31s%s.bmp\0", psPrefix, psSuffix);
         String sAppendedFileName = String.format("%.31s%s.bmp", psPrefix, psSuffix);
         psbOutputFileName.append(sAppendedFileName);
    } // appendFileName


    // This method originally came from SCENELST.CPP
    // Called from:
    //     Globals.createQMeshModel
    //     MainFrame.onToolsCreateAlphaImage
    //     MorphDlg.onOK
    //     ScnFileParser.readList
    public static void constructPathName(StringBuffer psbOutPath, String psInPath, char pcLastLetter) {
        String sFileWExt, sFile, sExt;
        
        File inputFile = new File(psInPath);
        sFileWExt = inputFile.getName();

        // sFile = file name without extension
        sFile = sFileWExt.substring(0, sFileWExt.lastIndexOf('.'));

        // sExt includes the leading "."
        sExt = sFileWExt.substring(sFileWExt.lastIndexOf('.'));
        int iLength = sFile.length();

        if(iLength > 0) {
            // Modify sFile (not the extension!)
            char[] charArray = new char[1];
            charArray[0] = pcLastLetter;
            String sLastLetter = new String(charArray);
            sFile = sFile.concat(sLastLetter);  // Append a letter
        }

        String sOutPath = inputFile.getParent() + File.separator + sFile + sExt;

        // Set the output parameter
        psbOutPath.append(sOutPath);
    } // constructPathName
} // class FileUtils