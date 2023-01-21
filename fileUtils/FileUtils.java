package fileUtils;

import java.io.File;

public class FileUtils {
    // This method came from UTILS.CPP
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
    

    // This method came from UTILS.CPP
    // Called from:
    //     motionBlur
    public static void makePath(String currentPath, String inPath, String prefix, int frameCounter, String inSuffix) {
        sprintf(currentPath, "%s%.31s%#04d%s.bmp\0", inPath, prefix, frameCounter, inSuffix);
    } // makePath
      

    // This method came from UTILS.CPP
    // Called from:
    //     motionBlur
    public static int getPathPieces(String firstImagePath, String psDirectory, 
    String psFileName, String psPrefix, Integer pIFrameNum, String psInSuffix) {
        String sDdrive, sDext, sFrameNum, sTempDirectory;
        char aDot;
        aDot = '.';
      
        // The following sets output parameter psFileName
       _splitpath(firstImagePath, sDdrive, psDirectory, psFileName, sDext);

       // Assumed input:   xxxxx0000c
       // Set output parameter psDirectory
       sTempDirectory = sDdrive + psDirectory;
       psDirectory = sTempDirectory;
       
       // Set output parameter psInSuffix (to c, assuming input xxxxx0000c)
       psInSuffix = psFileName.substring(psFileName.length() - 1);
      
       // Set output parameter pIFrameNum (to 0000, assuming input xxxxx0000c)
       sFrameNum = psFileName.substring(5, 4);
       pIFrameNum = Integer.parseInt(sFrameNum);
       
       // Set output parameter psPrefix (to xxxxx, assuming input xxxxx0000c)
       psPrefix = psFileName.substring(0, 4);
      
        return 0;
    } // getPathPieces


    // This method came from SceneList
    // Called from:
    //     createCutout
    public static void appendFileName(String psOutputFileName, String psPrefix, String psSuffix) {
        sprintf(psOutputFileName, "%.31s%s.bmp\0", psPrefix, psSuffix);
    } // appendFileName


    public static void constructPathName(String outPath, String inPath, char lastLetter) {
        String sDrive, sDir, sFile, sExt;
        _splitpath(inPath, sDrive, sDir, sFile, sExt);
        int iLength = sFile.length();

        if(iLength > 0) {
            char[] charArray = new char[1];
            charArray[0] = lastLetter;
            String sLastLetter = new String(charArray);
            sFile.concat(sLastLetter);  // Substitute a letter
        }

        _makepath(outPath, sDrive, sDir, sFile, sExt);
    } // constructPathName
} // class FileUtils