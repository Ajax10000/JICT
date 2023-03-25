package core;

import dtos.ColorAsBytes;
import dtos.OneInt;

import fileUtils.FileUtils;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import structs.Point3d;

public class ScnFileParser {
    private SceneList mSceneList;

    // Called from MainFrame.onToolsCreateASceneList
    public ScnFileParser(SceneList sceneList) {
        this.mSceneList = sceneList;
    } // ScnFileParser ctor


    // Method readList parses the Scene and Model-related information in a .scn file
    // specified by parameter pathName. If an error occurs while parsing, the String 
    // psErrorText is set to an error message.
    // If successfully parsed, it adds the scene and model information to a 
    // doubly-linked list. The start of this list is pointed to by field sceneListHead.
    // Called from:
    //     MainFrame.onToolsCreateASceneList
    public int readList(StringBuffer psErrorText, String psPathName) {
        int iRetValue;
        try {
            iRetValue = readListReal(psErrorText, psPathName);
        } catch (Exception e) {
            Globals.statusPrint("Globals.readList: Exception occurred");
            Globals.statusPrint(e.toString());
            e.printStackTrace();
            iRetValue = -1;
        }

        return iRetValue;
    } // readList


    // Called from:
    //     readList
    private int readListReal(StringBuffer psErrorText, String psPathName) {
        StringBuffer sText = new StringBuffer();
        String sKeyWord;
        String theModelName = "", sFileName, sMotionPath;
        String sSceneName = "", sAlphaPath, sMsgBuffer;
        StringBuffer sColorAdjustedPath;
        OneInt lineCounterOI = new OneInt();
        int iNumScenes;
        int iOutImageCols = 0, iOutImageRows = 0;
        int iNotFound; // = TRUE, FALSE, or THREE_NUMBERS_NOT_FOUND
        boolean bBlend, bWarp;
        int iSequence = 0, iColorMode = 0, iType, iCompoundMMember;
        boolean bCompoundMember = false; // changed from int to boolean
        boolean bGetOutImageSizeFlag = false; // changed from int to boolean
        boolean bDefinedRefPoint = false; // changed from int to boolean
        float fAlpha;
        Point3d rtPt, scPt, trPt, pointOfReference;
        Color adjustmentColor = new Color(0, 0, 0);
        String sAdjustmentType;
        int iStatus, iMinLineSize = 4;
        ColorAsBytes midCab;
        final int FALSE = 0;
        final int TRUE = 1;
        final int THREE_NUMBERS_NOT_FOUND = 2;
        StringTokenizer strtok = new StringTokenizer("test");
        StringTokenizer numtok;

        // We will populate psErrorText, so if it has anything inside, clear it
        clearStringBuffer(psErrorText);

        final String BLANK = " ";
        // final int DUPLICATESCENE = 3; // This variable is not used

        File scnFile = new File(psPathName);
        FileReader fileReader;
        try {
            fileReader = new FileReader(scnFile);
        } catch(FileNotFoundException fnfe) {
            setStringBuffer(psErrorText, "Could not find file.");
            // psErrorText will be printed with Global.statusPrint by the caller.

            return -1;
        }
        LineNumberReader filein = new LineNumberReader(fileReader);

        sMotionPath = "None";
        sAdjustmentType = "None";
        sAlphaPath = "Default";
        lineCounterOI.i = 0;
        iNumScenes = 0; 
        iCompoundMMember = 0;
        bCompoundMember = false;
        rtPt = new Point3d();
        scPt = new Point3d();
        trPt = new Point3d();
        pointOfReference = new Point3d();
        // Assume no problems will occur and initialize psErrorText accordingly.
        setStringBuffer(psErrorText, "Scene file read successfully");
        
        bDefinedRefPoint = false;

        Globals.statusPrint("Reading a scene file");
        // Start parsing the .scn file. 
        // We exit the following "forever" loop via return statements.
        // We return -1 if we encounter an error.
        // We return 0 if no error occurs and we encounter the EOF keyword.
        while(true) {  // Get the scene components
            // The next line will populate sText with a line read in by filein, 
            // and update iLineCounter
            sKeyWord = Shape3d.getNextLine(sText, lineCounterOI, filein, iMinLineSize);

            // As long as we haven't encountered line that indicates 
            // the start of a Model description ...
            while(!sKeyWord.equalsIgnoreCase("MODEL")) {
                // Each iteration through this loop will parse one line.
                // Note that we call Shape3d.getNextLine again at the end of this loop.
                String sBase, sEffectType, sColorMode;
                String sTempImageSize;
                iNotFound = TRUE;

                // We have just read a new line from the .scn file.
                // We expect to find one of the following keywords:
                // "SCENE", "MOTIONPATH", "ROTATION", "TRANSLATION", "END", or "EOF".
                // "EOF" is a keyword generated by method Shape3d.getNextLine when
                // it finds the end of the file.
                if (sKeyWord.equalsIgnoreCase("SCENE")) {
                    Globals.statusPrint("Found a SCENE LINE");
                    // We have found the start of a Scene description, 
                    // so we will parse it. It has the following format:
                    // scene <sceneName> [Sequence|Still] <outHeight>,<outWidth> [Color|Mono]
                    // Rotation <Rx>, <Ry>, <Rz>
                    // Translation <Tx>, <Ty>, <Tz>
                    // MotionPath [None]
                    iNotFound = FALSE;
                    // Skip over the word "SCENE"
                    sBase = sText.substring(6);

                    // After the token "SCENE" we should have the scene name
                    // scene <sceneName> [Sequence|Still] <outHeight>,<outWidth> [Color|Mono]
                    strtok = new StringTokenizer(sBase, BLANK);
                    sSceneName = strtok.nextToken();
                    if(sSceneName == null) {
                        setStringBuffer(psErrorText, "A Scene must have a name. Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.
                        
                        return -1;
                    }

                    // Parse the effect type (i.e., [Sequence|Still])
                    // scene <sceneName> [Sequence|Still] <outHeight>,<outWidth> [Color|Mono]
                    sEffectType = strtok.nextToken();
                    iSequence = JICTConstants.I_STILL; // Assume "STILL"
                    if(sEffectType != null) {
                        if(sEffectType.equalsIgnoreCase("SEQUENCE")) {
                            // We assumed wrong, so we correct our assumption
                            iSequence = JICTConstants.I_SEQUENCE;
                        }
                        // TODO: Delete the following if statement?
                        if(sEffectType.equalsIgnoreCase("MORPH")) {
                            iSequence = JICTConstants.I_MORPH;
                        }
                    } else {
                        setStringBuffer(psErrorText, "A Sequence must have a name. Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    // Now parse the image dimensions (i.e., <outHeight>,<outWidth>)
                    // scene <sceneName> [Sequence|Still] <outHeight>, <outWidth> [Color|Mono]
                    // sTempImageSize is equal to the string starting with <outHeight>:
                    // <outHeight>, <outWidth> [Color|Mono]
                    sTempImageSize = strtok.nextToken();

                    // Now looking for Color or Mono:
                    // scene <sceneName> [Sequence|Still] <outHeight>,<outWidth> [Color|Mono]
                    sColorMode = strtok.nextToken();
                    // Assume "Mono"
                    iColorMode = JICTConstants.I_MONOCHROME;
                    if(sColorMode != null) {
                        if(sColorMode.equalsIgnoreCase("COLOR")) { 
                            // We assumed wrong, so we correct our assumption.
                            iColorMode = JICTConstants.I_COLOR;
                        }
                    } else {
                        // We expected Color or Mono. We didn't find anything.
                        psErrorText.append("Expected: Color or Monochrome. Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }

                    // Now parse outHeight and outWidth values (i.e., <outHeight>,<outWidth>)
                    // These should be integers.
                    // scene <sceneName> [Sequence|Still] <outHeight>,<outWidth> [Color|Mono]
                    if(sTempImageSize != null) { // Output Image Height, Width
                        numtok = new StringTokenizer(sTempImageSize, ",");
                        iOutImageRows = Integer.parseInt(numtok.nextToken());
                        iOutImageCols = Integer.parseInt(numtok.nextToken());
                        bGetOutImageSizeFlag = false;
                        if(iOutImageCols == 0 || iOutImageRows == 0) { 
                            bGetOutImageSizeFlag = true;
                        }
                    } else {
                        setStringBuffer(psErrorText, "Expected Image Height, Image Width. Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }
                } // end scene line processing

                if(sKeyWord.equalsIgnoreCase("MOTIONPATH")) {
                    Globals.statusPrint("Found a SCENE MOTIONPATH LINE");
                    // Found a MOTIONPATH line. So we parse it. 
                    // It should have the following format:
                    // MotionPath [None|<pathName>]
                    sMotionPath = sText.substring(11);
                    if(sMotionPath.length() == 0) {
                        setStringBuffer(psErrorText, "MotionPath file missing on Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        return -1;
                    }
                    Globals.statusPrint("ScnFileParser.readLine: MOTIONPATH value is " + sMotionPath);
                    iNotFound = FALSE;
                }

                if(sKeyWord.equalsIgnoreCase("ROTATION")) {
                    Globals.statusPrint("Found a SCENE ROTATION LINE");
                    // Found a ROTATION line. So we parse it. 
                    // It should have the following format:
                    // ROTATION <Rx>, <Ry>, <Rz>
                    // where Rx, Ry and Rz represent angles 
                    // expressed as floating-point numbers.

                    // theRt = strtok(TheText + 9, BLANK);
                    // Skip over the word "ROTATION "
                    String sRt = sText.substring(9);
                    
                    strtok = new StringTokenizer(sRt, BLANK);
                    sRt = strtok.nextToken();
                    if(checkFor3(sRt) == 0) {
                        iNotFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(sRt, ",");
                        rtPt.fX = Float.parseFloat(numtok.nextToken());
                        rtPt.fY = Float.parseFloat(numtok.nextToken());
                        rtPt.fZ = Float.parseFloat(numtok.nextToken());
                        iNotFound = FALSE;
                        Globals.statusPrint("ScnFileParser.Found 3 rotation angles for ROTATION line");
                        Globals.statusPrint("Rot. values are: " + rtPt.fX + ", " + rtPt.fY + ", " + rtPt.fZ);
                    }
                }

                if(sKeyWord.equalsIgnoreCase("TRANSLATION")) {
                    Globals.statusPrint("Found a SCENE TRANSLATION LINE");
                    // Found a TRANSLATION line. So we parse it. 
                    // It should have the following format:
                    // TRANSLATION <Tx>, <Ty>, <Tz>
                    // where Tx, Ty, and Tz represent translation values 
                    // expressed as floating-point numbers.
                    String sTr;

                    // theTr = strtok(TheText + 12, BLANK);
                    // Skip over the word "TRANSLATION "
                    sTr = sText.substring(12);
                    
                    strtok = new StringTokenizer(sTr, BLANK);
                    sTr = strtok.nextToken();
                    if(checkFor3(sTr) == 0) {
                        iNotFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(sTr, ",");
                        trPt.fX = Float.parseFloat(numtok.nextToken());
                        trPt.fY = Float.parseFloat(numtok.nextToken());
                        trPt.fZ = Float.parseFloat(numtok.nextToken());
                        iNotFound = FALSE;
                        Globals.statusPrint("ScnFileParser.readLine: Found 3 translation values for TRANSLATION line");
                        Globals.statusPrint("Translation values are: " + trPt.fX + ", " + trPt.fY + ", " + trPt.fZ);
                    }
                }
              
                if(sKeyWord.equalsIgnoreCase("END")) {
                    Globals.statusPrint("Found a SCENE END LINE");
                    String sToken;
                    // theToken = strtok(TheText + 4, BLANK);
                    sToken = strtok.nextToken();
                    if(sToken.equalsIgnoreCase("COMPOUND")) {
                        iCompoundMMember = 0;
                        iNotFound = FALSE;
                    }
                }

                if (sKeyWord.equalsIgnoreCase("EOF")) {
                    Globals.statusPrint("Found a SCENE EOF LINE");
                    setStringBuffer(psErrorText, "sceneFile may be corrupted or has no models");
                    // psErrorText will be printed with Global.statusPrint by the caller.

                    closeLineNumberReader(filein);
                    return -1;
                }

                // iNotFound = TRUE (1), FALSE (0), or THREE_NUMBERS_NOT_FOUND (2)
                if (iNotFound != 0) {
                    if(iNotFound == 1) {
                        setStringBuffer(psErrorText, "Unknown Keyword: " + sKeyWord + ". Line  " + lineCounterOI.i);
                    }
                    if(iNotFound == THREE_NUMBERS_NOT_FOUND) {
                        setStringBuffer(psErrorText, "Expected 3 numeric values separated by commas: " + sKeyWord + 
                            "  Line " + lineCounterOI.i);
                    }
                    // psErrorText will be printed with Global.statusPrint by the caller.

                    closeLineNumberReader(filein);
                    return -1;
                }

                sText.delete(0, sText.length());
                sKeyWord = Shape3d.getNextLine(sText, lineCounterOI, filein, iMinLineSize);
            }  // while(!TheKeyWord.equalsIgnoreCase("MODEL"))

            // Add the scene to the sceneList and read its elements.
            iNumScenes++;
            if (iNumScenes > 1) {
                setStringBuffer(psErrorText, "Only 1 scene definition permitted per scene file");
                // psErrorText will be printed with Global.statusPrint by the caller.

                closeLineNumberReader(filein);
                return -1;
            }

            iStatus = mSceneList.addScene(sSceneName, iSequence, 
                iOutImageCols, iOutImageRows, iColorMode, 
                rtPt, trPt, sMotionPath);
            if(iStatus != 0) {
                setStringBuffer(psErrorText, "Could not add Scene to Scene List. Line " + lineCounterOI.i);
                // psErrorText will be printed with Global.statusPrint by the caller.

                closeLineNumberReader(filein);
                return -1;
            }

            iType = JICTConstants.I_IMAGE;
            sMotionPath = "";
            sFileName = "";
            int iNumModels = 0;
            String sModelName, sBlend, sWarp, sScale, sScaleValue, sType;
            bBlend = true;
            bWarp = true;
            fAlpha = 1.0f;
            
            // Until we find the start of another SCENE attribute, we'll look for 
            // MODEL lines and parse the model information.
            while(!sKeyWord.equalsIgnoreCase("SCENE")) {
                // Each iteration through this loop will parse one line.
                // Note that we call Shape3d.getNextLine at the end of this loop.
                iNotFound = TRUE;

                // We expect MODEL, ROTATION, SCALE, TRANSLATION, ADJUSTCOLOR, or MOTIONPATH
                if (sKeyWord.equalsIgnoreCase("MODEL")) {
                    // The Model attributes in a scene file have the following format:
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    // FileName <pathName>
                    // MotionPath [None|<pathName>]
                    // [AlphaImagePath [None|<pathName>]]
                    // AdjustColor [Target|Relative] <R> <G> <B>
                    // Rotation <rx>, <ry>, <rz>
                    // Scale <sx>, <sy>, <sz>
                    // Translation <tx>, <ty>, <tz>
                    iNumModels++;
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL line");

                    if (iNumModels > 1) {
                        // Color adjust the previous model, if needed
                        sColorAdjustedPath = new StringBuffer("");
                        // Change the file name to point to the color corrected image.
                        // AdjustColor [Target|Relative] <R> <G> <B>
                        if(sAdjustmentType.equalsIgnoreCase("None")) {
                            // In the following line, sFileName is the name of the model, i.e., the name that
                            // follows the Model keyword in the line
                            // Model modelName ...
                            MemImage inputMImage = new MemImage(sFileName, 0, 0, 
                                JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                            if (!inputMImage.isValid()) {
                                sMsgBuffer = "ScnFileParser.readList: Can't open image for color correction: " + sFileName;
                                Globals.statusPrint(sMsgBuffer);
                                return -1;
                            }

                            // Create MemImage correctedImage, which will contain the adjusted colors.
                            // Later we will save it to a file.
                            MemImage correctedMImage = new MemImage(inputMImage);
                            Globals.statusPrint("ScnFileParser.readList: Adjusting color image for previous model " + theModelName);	    
                            midCab = new ColorAsBytes();
                            inputMImage.adjustColor(adjustmentColor.getRed(), adjustmentColor.getGreen(), adjustmentColor.getBlue(),
                                midCab, 
                                correctedMImage, sAdjustmentType, 0);

                            // The following method sets sColorAdjustedPath
                            Globals.statusPrint("ScnFileParser.readList: sFileName = " + sFileName);
                            sColorAdjustedPath = new StringBuffer();
                            FileUtils.constructPathName(sColorAdjustedPath, sFileName, 'j');     
                            sMsgBuffer = "ScnFileParser.readList: Saving adjusted color image: " + sColorAdjustedPath;
                            Globals.statusPrint(sMsgBuffer);
            
                            correctedMImage.writeBMP(sColorAdjustedPath.toString());
                            Globals.statusPrint("ScnFileParser.readList: Finished saving adjusted color image: " + sColorAdjustedPath + " for previous model " + theModelName);
                        }

                        if(iCompoundMMember == 1 && iType == JICTConstants.I_COMPOUND) bCompoundMember = false;
                        if(iCompoundMMember == 1 && iType != JICTConstants.I_COMPOUND) bCompoundMember = true;

                        Globals.statusPrint("ScnFileParser.readList: About to add a SceneElement to our SceneList");
                        iStatus = mSceneList.addSceneElement(theModelName, sFileName, 
                            bBlend, // BLEND => true, NOBLEND => false
                            iType,
                            bWarp, // WARP => true, NOWARP => false
                            fAlpha, 
                            rtPt, scPt, trPt, 
                            sMotionPath, sAlphaPath,
                            bCompoundMember, 
                            adjustmentColor, 
                            sAdjustmentType, // "TARGET" or "RELATIVE"
                            sColorAdjustedPath.toString(),
                            bDefinedRefPoint, pointOfReference);
                        Globals.statusPrint("ScnFileParser.readList: Finished adding Scene to SceneList.");
                        if(iCompoundMMember == 0) {
                            bCompoundMember = false;
                        }

                        if(iStatus != 0) {
                            setStringBuffer(psErrorText, "Could not add previous model " + theModelName + " to scene list. Line " + lineCounterOI.i);
                            // psErrorText will be printed with Global.statusPrint by the caller.

                            closeLineNumberReader(filein);
                            Globals.statusPrint("ScnFileParser.readList: Leaving due to error");
                            return -1;
                        }

                        // Reset the variables used to store values parsed from the .scn file
                        bBlend = true; 
                        bWarp = true; 
                        fAlpha = 1.0f; 
                        iType = JICTConstants.I_IMAGE;
                        bDefinedRefPoint = false;
                        sMotionPath = "";
                        sFileName = "";
                        sAdjustmentType = "None";
                        sColorAdjustedPath = new StringBuffer("None");
                    } // if (nModels > 1)

                    // Look for the model name:
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    
                    // Skip over the word "MODEL "
                    try {
                        sModelName = sText.substring(6);
                    } catch (NullPointerException npe) {
                        setStringBuffer(psErrorText, "A null pointer error occurred while trying to skip over the word 'Model '");
                        closeLineNumberReader(filein);
                        return -1;
                    }
                    strtok = new StringTokenizer(sModelName, BLANK);
                    
                    try {
                        sModelName = strtok.nextToken();
                        Globals.statusPrint("ScnFileParser.readList: Model name is " + sModelName);
                    } catch (NoSuchElementException nsee) {
                        setStringBuffer(psErrorText, "ScnFileParser.readList: No such element exception occurred (was expecting a model name)");
                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }

                    // If the modelName is ".", set it to an empty string
                    // causing the model to be displayed without a label
                    if(sModelName.equalsIgnoreCase(".")) {
                        theModelName = "";
                    } else {
                        theModelName = sModelName;
                    }
                    Globals.statusPrint("ScnFileParser.readList: Found model " + theModelName);
                    // Look for the BLEND specification (i.e., [Blend|NoBlend]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    sBlend      = strtok.nextToken();

                    // Look for the Warp/NoWarp specification (i.e., [Warp|NoWarp]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    sWarp       = strtok.nextToken();

                    // Look for the AlphaScale specification (i.e., AlphaScale <alpha>):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    sScale      = strtok.nextToken();
                    sScaleValue = strtok.nextToken();

                    // Look for the model type (i.e., for [Image|Shape|QuadMesh|Sequence]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    sType       = strtok.nextToken();

                    bBlend = true;
                    if(sBlend != null) {
                        if(sBlend.equalsIgnoreCase("NOBLEND")) { 
                            bBlend = false;
                            Globals.statusPrint("ScnFileParser.readList: BLEND/NOBLEND value is " + bBlend);
                        }
                    } else {
                        setStringBuffer(psErrorText, "Missing value or term (BLEND/NOBLEND) on Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }

                    bWarp = true;
                    if(sWarp != null) {
                        if(sWarp.equalsIgnoreCase("NOWARP")) { 
                            bWarp = false;
                            Globals.statusPrint("ScnFileParser.readList: WARP/NOWARP value is " + bWarp);
                        }
                    } else {
                        setStringBuffer(psErrorText, "Missing value or term (WARP/NOWARP) on Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }

                    fAlpha = 1.0f;
                    if(sScale != null) {
                        if(sScale.equalsIgnoreCase("ALPHASCALE")) {
                            fAlpha = Float.parseFloat(sScaleValue);
                            Globals.statusPrint("ScnFileParser.readList: Alphascale value is " + fAlpha);
                        }
                    } else {
                        setStringBuffer(psErrorText, "Missing value or term (ALPHASCALE) on Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }

                    Globals.statusPrint("MODEL image type is " + sType);
                    // Look for the model type (i.e., [Image|Shape|QuadMesh|Sequence]):
                    // Model <modelName> [Blend|NoBlend] [Warp|NoWarp] AlphaScale <alpha> [Image|Shape|QuadMesh|Sequence]
                    iType = JICTConstants.I_IMAGE;
                    if(sType != null) {
                        if(sType.equalsIgnoreCase("SHAPE"))    iType = JICTConstants.I_SHAPE;
                        if(sType.equalsIgnoreCase("QUADMESH")) iType = JICTConstants.I_QUADMESH;
                        if(sType.equalsIgnoreCase("SEQUENCE")) iType = JICTConstants.I_SEQUENCE;
                        if(sType.equalsIgnoreCase("COMPOUND")) {
                            iType = JICTConstants.I_COMPOUND;
                            iCompoundMMember = 1;
                        }
                    } else {
                        setStringBuffer(psErrorText, "Expected a model type on Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }

                    iNotFound = FALSE;
                } // if sKeyWord == "MODEL"

                if(sKeyWord.equalsIgnoreCase("REFERENCEPOINT")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL REFERENCEPOINT line");
                    // Skip over the word "REFERENCEPOINT"
                    String sRef = sText.substring(15);

                    // After the word REFERENCEPOINT we should find 3 numbers 
                    // separated by commas
                    if(checkFor3(sRef) == 0) {
                        // Remember that we didn't find 3 numbers
                        iNotFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(sRef, ",");
                        pointOfReference.fX = Float.parseFloat(numtok.nextToken());
                        pointOfReference.fY = Float.parseFloat(numtok.nextToken());
                        pointOfReference.fZ = Float.parseFloat(numtok.nextToken());
                        bDefinedRefPoint = true;
                        iNotFound = FALSE;
                        Globals.statusPrint("ScnFileParser.readList: Found 3 values for REFERENCEPOINT");
                        Globals.statusPrint("ScnFileParser.readList: Reference pt values are " + 
                            pointOfReference.fX + ", " + 
                            pointOfReference.fY + ", " +
                            pointOfReference.fZ);
                    }
                } // if sKeyWord == "REFERENCEPOINT"

                if(sKeyWord.equalsIgnoreCase("ROTATION")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL ROTATION line");
                    // Found a ROTATION line. So we parse it. 
                    // It should have the following format:
                    // ROTATION <rx>, <ry>, <rz>
                    // where rx, ry and rz are rotation degrees, 
                    // expressed as floating-point numbers.

                    // Skip over the word "ROTATION"
                    String sRt = sText.substring(9);

                    // After the word ROTATION we should find 3 numbers 
                    // separated by commas
                    if(checkFor3(sRt) == 0) {
                        // Remember that we didn't find 3 numbers
                        iNotFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(sRt, ",");
                        rtPt.fX = Float.parseFloat(numtok.nextToken());
                        rtPt.fY = Float.parseFloat(numtok.nextToken());
                        rtPt.fZ = Float.parseFloat(numtok.nextToken());
                        iNotFound = FALSE;
                        Globals.statusPrint("ScnFileParser.readList: Found 3 rotation values for ROTATION line");
                        Globals.statusPrint("ScnFileParser.readList: Rot. values are: " + rtPt.fX + ", " + rtPt.fY + ", " + rtPt.fZ);
                    }
                } // if sKeyword == "ROTATION"

                if(sKeyWord.equalsIgnoreCase("SCALE")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL SCALE line");
                    // Found a SCALE line. So we parse it. 
                    // It should have the following format:
                    // SCALE <sx>, <sy>, <sz>
                    // were sx, sy, and sz are scale values, 
                    // expressed as floating-point numbers.

                    // Skip over the word "SCALE"
                    String sSc = sText.substring(6);
                    // After the word SCALE we should find 3 numbers 
                    // separated by commas
                    if(checkFor3(sSc) == 0) {
                        // Remember that we didn't find 3 numbers
                        iNotFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(sSc, ",");
                        scPt.fX = Float.parseFloat(numtok.nextToken());
                        scPt.fY = Float.parseFloat(numtok.nextToken());
                        scPt.fZ = Float.parseFloat(numtok.nextToken());
                        iNotFound = FALSE;
                        Globals.statusPrint("ScnFileParser.readList: Found 3 scale values for SCALE line");
                        Globals.statusPrint("ScnFileParser.readList: Scale values are: " + scPt.fX + ", " + scPt.fY + ", " + scPt.fZ);
                    }
                } // if sKeyWord == "SCALE"

                if(sKeyWord.equalsIgnoreCase("TRANSLATION")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL TRANSLATION line");
                    // Found a TRANSLATION line. So we parse it. 
                    // It should have the following format:
                    // TRANSLATION <tx>, <ty>, <tz>
                    // where tx, ty, and tz are translation values, 
                    // expressed as floating-point numbers.

                    // Skip over the word "TRANSLATION"
                    String sTr = sText.substring(12);

                    // After the word TRANSLATION we should find 3 numbers 
                    // separated by commas
                    if(checkFor3(sTr) == 0) {
                        // Remember that we didn't find 3 numbers
                        iNotFound = THREE_NUMBERS_NOT_FOUND;
                    } else {
                        numtok = new StringTokenizer(sTr, ",");
                        trPt.fX = Float.parseFloat(numtok.nextToken());
                        trPt.fY = Float.parseFloat(numtok.nextToken());
                        trPt.fZ = Float.parseFloat(numtok.nextToken());
                        iNotFound = FALSE;
                        Globals.statusPrint("ScnFileParser.readList: Found 3 translation values for TRANSLATION line");
                        Globals.statusPrint("ScnFileParser.readList: Trans. values are: " + trPt.fX + ", " + trPt.fY + ", " + trPt.fZ);
                    }
                } // if sKeyWord == "TRANSLATION"

                if(sKeyWord.equalsIgnoreCase("ADJUSTCOLOR")) {
                    Globals.statusPrint("ScnFileParser.readList: Found an ADJUSTCOLOR line");
                    // Found an ADJUSTCOLOR line. So we parse it. 
                    // It should have the following format:
                    // ADJUSTCOLOR [Target|Relative] <R>, <G>, <B>
                    // where R, G, and B are RGB color values, 
                    // expressed as integers in the range from 0 to 255.
                    String sAdjustment, sColor;
                    // String adjustmentCopy; // This variable is not used
                    
                    // Skip over the word "ADJUSTCOLOR"
                    // adjustment = strtok(TheText + 12, BLANK);
                    sAdjustment = sText.substring(12);

                    strtok = new StringTokenizer(sAdjustment, BLANK);
                    // sAdjustmentType should be "TARGET" or "RELATIVE"
                    sAdjustmentType = strtok.nextToken();
                    Globals.statusPrint("ScnFileParser.readList: ADJUSTCOLOR line is of type " + sAdjustmentType);

                    // Skip over the word "TARGET " or "RELATIVE "
                    sColor = sAdjustment.substring(sAdjustmentType.length() + 1);
                    // theColor = strtok(TheText + 12 + aLength + 1, BLANK);  // move forward to the RGB color

                    // Parse the R, G, B color values
                    numtok = new StringTokenizer(sColor, ",");
                    int iR = Integer.parseInt(numtok.nextToken());
                    int iG = Integer.parseInt(numtok.nextToken());
                    int iB = Integer.parseInt(numtok.nextToken());
                    adjustmentColor = new Color(iR, iG, iB);
                    iNotFound = FALSE;
                    Globals.statusPrint("ScnFileParser.readList: Found 3 color values for ADJUSTCOLOR line");
                    Globals.statusPrint("ScnFileParser.readList: color values are " + iR + ", " + iG + ", " + iB);
                } // if sKeyWord = "ADJUSTCOLOR"

                if(sKeyWord.equalsIgnoreCase("MOTIONPATH")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL MOTIONPATH line");
                    // Found an MOTIONPATH line. So we parse it. 
                    // It should have the following format:
                    // MOTIONPATH [None|<pathName>]
                    // where pathName, if provided, is the path to a ".pth" file

                    // Skip over the word "MOTIONPATH"
                    // Now theMotionPath should be either "None" or the path
                    sMotionPath = sText.substring(11);
                    Globals.statusPrint("ScnFileParser.readList: MOTIONPATH value is " + sMotionPath);

                    if(sMotionPath.length() == 0) {
                        setStringBuffer(psErrorText, "MotionPath file missing on Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }
                    iNotFound = FALSE;
                } // if sKeyWord == "MOTIONPATH"

                if(sKeyWord.equalsIgnoreCase("ALPHAIMAGEPATH")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL ALPHAIMAGEPATH line");
                    // Found an ALPHAIMAGEPATH line. So we parse it. 
                    // It should have the following format:
                    // ALPHAIMAGEPATH [None|<pathName>]

                    // Skip over the word "ALPHAIMAGEPATH"
                    sAlphaPath = sText.substring(15);
                    Globals.statusPrint("ScnFileParser.readList: ALPHAIMAGEPATH value is " + sAlphaPath);
                    if(sAlphaPath.length() == 0) {
                        setStringBuffer(psErrorText, "Alpha Image Path file missing. Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }
                    iNotFound = FALSE;
                } // if sKeyWord = "ALPHAIMAGEPATH"

                if(sKeyWord.equalsIgnoreCase("FILENAME")) {
                    Globals.statusPrint("ScnFileParser.readList: Found a MODEL FILENAME line");
                    // Found a FILENAME line. So we parse it. 
                    // It should have the following format:
                    // FILENAME <pathName>
                    // where pathName, if provided, is the path to a file.
                    // If the model type is IMAGE or QUADMESH, then pathName
                    // should be the full path to a Windows bitmap (.bmp) file.
                    // If the model type is SHAPE, then pathName 
                    // should be the full path to a shape (.shp) file.

                    // Skip over the word "FILENAME"
                    sFileName = sText.substring(9);
                    Globals.statusPrint("ScnFileParser.readList: FILENAME value is " + sFileName);
                    // If the user previously specified either outWidth = 0 or outHeight = 0
                    // in the SCENE line ...
                    if(bGetOutImageSizeFlag == true) {
                        // Read the outWidth and outHeight values from the .bmp file
                        Integer iBpp = 0;
                        int iBmpStatus;
                        Globals.statusPrint("ScnFileParser.readList: About to read BMP header.");
                        iBmpStatus = Globals.readBMPHeader(sFileName, iOutImageRows, iOutImageCols, iBpp);
                        Globals.statusPrint("ScnFileParser.readList: Finished reading BMP header.");

                        if(iBmpStatus != 0) {
                            setStringBuffer(psErrorText, "File name not valid. Line " + lineCounterOI.i);
                            // psErrorText will be printed with Global.statusPrint by the caller.

                            closeLineNumberReader(filein);
                            Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                            return -1;
                        }
                        mSceneList.setSceneOutImageSize(iOutImageRows, iOutImageCols);
                        bGetOutImageSizeFlag = false;
                    }
                    iNotFound = FALSE;
                } // if sKeyWord = "FILENAME"

                // Look for other keywords - not model related
                if(sKeyWord.equalsIgnoreCase("END")) {
                    Globals.statusPrint("ScnFileParser.readListReal: END keyword");
                    String sToken;

                    // Skip over the word "END"
                    sToken = sText.substring(4);

                    if(sToken.equalsIgnoreCase("COMPOUND")) {
                        iCompoundMMember = 0;
                        iNotFound = FALSE;
                    }
                } // if sKeyWord == "END"

                if (sKeyWord.equalsIgnoreCase("EOF")) {
                    Globals.statusPrint("ScnFileParser.readListReal: Found EOF keyword");
                    sColorAdjustedPath = new StringBuffer("");
                    // Save the last model
                    // If the color is to be adjusted (i.e., we previously read an ADJUSTCOLOR line), 
                    // adjust it now and change the input image
                    // file name to point to the color corrected image.
                    if(sAdjustmentType.equalsIgnoreCase("None")) {
                        MemImage inputImage = new MemImage(sFileName, 0, 0, 
                            JICTConstants.I_RANDOM, 'R', JICTConstants.I_RGBCOLOR);
                        if (!inputImage.isValid()) {
                            sMsgBuffer = "sceneList.readList: Can't open image for color correction: " + sFileName;
                            Globals.statusPrint(sMsgBuffer);

                            Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                            return -1;
                        }
                        
                        MemImage correctedImage = new MemImage(inputImage);
                        Globals.statusPrint("Adjusting color image");
                        midCab = new ColorAsBytes();    
                        inputImage.adjustColor(adjustmentColor.getRed(), adjustmentColor.getGreen(), adjustmentColor.getBlue(),
                            midCab, 
                            correctedImage, sAdjustmentType, 0);
                            Globals.statusPrint("ScnFileParser.readListReal: Finished adjusting color image");

                        // The following method sets sColorAdjustedPath
                        FileUtils.constructPathName(sColorAdjustedPath, sFileName, 'j');     
                        sMsgBuffer = "Saving adjusted color image: " + sColorAdjustedPath;
                        Globals.statusPrint(sMsgBuffer);
                  
                        correctedImage.writeBMP(sColorAdjustedPath.toString());
                        Globals.statusPrint("ScnFileParser.readListReal: Finished saving adjusted color image");
                    }

                    if(iCompoundMMember == 1 && iType == JICTConstants.I_COMPOUND) bCompoundMember = false;
                    if(iCompoundMMember == 1 && iType != JICTConstants.I_COMPOUND) bCompoundMember = true;

                    Globals.statusPrint("ScnFileParser.readList: About to add a SceneElement to our SceneList");
                    iStatus = mSceneList.addSceneElement(theModelName, sFileName, 
                        bBlend, // BLEND => true, NOBLEND => false
                        iType,
                        bWarp, // WARP => true, NOWARP => false
                        fAlpha, 
                        rtPt, scPt, trPt, 
                        sMotionPath, sAlphaPath,
                        bCompoundMember, 
                        adjustmentColor, 
                        sAdjustmentType, 
                        sColorAdjustedPath.toString(),
                        bDefinedRefPoint, pointOfReference);
                    if(iCompoundMMember == 0) bCompoundMember = false;

                    if(iStatus != 0) {
                        setStringBuffer(psErrorText, "Could not add a model to scene list. Line " + lineCounterOI.i);
                        // psErrorText will be printed with Global.statusPrint by the caller.

                        closeLineNumberReader(filein);
                        Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                        return -1;
                    }
                    Globals.statusPrint("ScnFileParser.readList: Finished adding SceneElement to SceneList");

                    closeLineNumberReader(filein);
                    Globals.statusPrint("ScnFileParser.readList: Leaving normally.");
                    return 0;
                } // if sKeyWord == "EOF"
            
                if (iNotFound != 0) {
                    if(iNotFound == 1) {
                        setStringBuffer(psErrorText, "Unknown Keyword: " + sKeyWord + "  Line " + lineCounterOI.i);
                    }
                    if(iNotFound == THREE_NUMBERS_NOT_FOUND) {
                        setStringBuffer(psErrorText, "Expected 3 numeric values separated by commas: " + sKeyWord + "  Line " + lineCounterOI.i);
                    }
                    // Globals.statusPrint(psErrorText);

                    Globals.statusPrint("ScnFileParser.readList: Leaving due to error.");
                    closeLineNumberReader(filein);
                    return -1;
                } // if (notFound != 0)
                
                // Clear out the string buffer before we read the next line into it
                sText.delete(0, sText.length());
                sKeyWord = Shape3d.getNextLine(sText, lineCounterOI, filein, iMinLineSize);
            } // while(!sKeyWord.equalsIgnoreCase("SCENE"))
        } // while(true)
    } // readListReal


    // Called from:
    //     readListReal
    public int checkFor3(String psString) {
        int iRetValue;

        try {
            iRetValue = checkFor3Real(psString);
        } catch (StringIndexOutOfBoundsException sioobe) {
            Globals.statusPrint("StringIndexOutofBoundsException while parsing for 3 numbers.");
            sioobe.printStackTrace();
            iRetValue = 0;
        }

        return iRetValue;
    } // checkFor3


    // Called from:
    //     checkFor3
    private int checkFor3Real(String psString) {
        // Perform a syntax check on the input string:
        // The string must have two commas and three numeric values
        int numChars = psString.length();
        char aChar;
        
        int numCommas = 0;
        int numNumbers = 0;

        int j = 0;
        while(j < numChars) {
            aChar = psString.charAt(j);
            // status("At top", j, aChar, numCommas, numNumbers);

            // Check for commas
            if (aChar == ',') { 
                numCommas++;
                j++;
            } else if(Character.isDigit(aChar) || aChar == '.'|| aChar == '-') {
                numNumbers++;
            
                j++;
                if (j < numChars) {
                    aChar = psString.charAt(j);
                    
                    // Step through the characters until we find one that is
                    // not a digit, not a '.', and not a '-'.
                    // (a comma will stop this while loop)
                    while (
                    (j < numChars) && 
                    (Character.isDigit(aChar) || (aChar == '.') || (aChar == '-'))) {
                        j++;
                        if (j < numChars) {
                            aChar = psString.charAt(j);
                        }
                    }
                }   
            } else {
                String sMsgText = "checkFor3Real: Unexpected char found: " + aChar;
                Globals.statusPrint(sMsgText);
                return 0;
            }
        }

        if ((numCommas == 2) && (numNumbers == 3)) {
            return 1;
        } else {
            return 0;
        }
    } // checkFor3Real


    // Method status is used for debugging method checkFor3Real
    public void status(String sPreMsg, int j, char aChar, int numCommas, int numNumbers) {
        System.out.println(sPreMsg + " j = " + j + 
            " aChar = <" + aChar + ">, numCommas = " + numCommas + 
            ", numNumbers = " + numNumbers);
    }


    // Called from:
    //     readListReal
    private void closeLineNumberReader(LineNumberReader filein) {
        try {
            filein.close();
        } catch(IOException ioe) {
            // do nothing
        }
    } // closeLineNumberReader


    public static void clearStringBuffer(StringBuffer psStringBuffer) {
        if (psStringBuffer.length() > 0) {
            psStringBuffer.delete(0, psStringBuffer.length());
        }
    } // clearStringBuffer


    public static void setStringBuffer(StringBuffer psStringBuffer, String psMsg) {
        clearStringBuffer(psStringBuffer);
        psStringBuffer.append(psMsg);
    } // setStringBuffer
} // ScnFileParser