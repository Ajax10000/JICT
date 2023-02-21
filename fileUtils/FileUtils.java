package fileUtils;

import java.io.File;

public class FileUtils {
    // This method originally came from UTILS.CPP
    public static boolean fileExists(String psPathName) {
        File file = new File(psPathName);

        return file.exists();
    } // fileExists
    

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
    public static void makePath(String currentPath, String inPath, String prefix, int frameCounter, String inSuffix) {
        // sprintf(currentPath, "%s%.31s%#04d%s.bmp\0", inPath, prefix, frameCounter, inSuffix);
        currentPath = String.format("%s%.31s%#04d%s.bmp", inPath, prefix, frameCounter, inSuffix);
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
    //     Globals.createCutout
    //     SceneList.render
    public static void appendFileName(String psOutputFileName, String psPrefix, String psSuffix) {
        // sprintf(psOutputFileName, "%.31s%s.bmp\0", psPrefix, psSuffix);
        psOutputFileName = String.format("%.31s%s.bmp", psPrefix, psSuffix);
    } // appendFileName


    // This method originally came from SCENELST.CPP
    public static void constructPathName(String psOutPath, String psInPath, char pcLastLetter) {
        String sDrive, sDir, sFile, sExt;
        _splitpath(psInPath, sDrive, sDir, sFile, sExt);
        int iLength = sFile.length();

        if(iLength > 0) {
            char[] charArray = new char[1];
            charArray[0] = pcLastLetter;
            String sLastLetter = new String(charArray);
            sFile.concat(sLastLetter);  // Substitute a letter
        }

        _makepath(psOutPath, sDrive, sDir, sFile, sExt);
    } // constructPathName
} // class FileUtils