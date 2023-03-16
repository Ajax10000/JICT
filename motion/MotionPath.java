package motion;

import globals.Globals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

// A MotionPath object consists of a list of MotionNode objects stored in field mNodes.
// The field miNumNodes contains the number of MotionNode (aka keyframes) in the MotionPath.
// See pages 123, 127, and 131 of the book Visual Special Effects Toolkit in C++.
// 
// MotionNode and MotionPath objects are used when a scene file (with extension .scn)
// is read that contains a Model with a MOTIONPATH line, with a non-null path name.
// The path file name should have the .pth file extension:
// Model <modelNamae> [Blend|NoBlend] [Warp|NoWarp] AlphaScale alpha [Image|Shape|QuaMesh|SEquence]
// ...
// MotionPath [None|<pathName>]
public class MotionPath {

    private boolean mbAllocated;
    // private void allocate(int);

    public int miNumNodes;
    public MotionNode[] mNodes;
    
    // public int read(char *); ---- NOT IMPLEMENTED, couldn't find in C++ code ----
    // public int readMotion(char *); - implemented
    // public int write(char *); ---- NOT IMPLEMENTED, couldn't find in C++ code ----
    // public int getNode(int, motionNode *); - implemented
    // public motionPath(void); - implemented
    // public ~motionPath(void); - implemented (as method finalize)
    // public void getFirstLastFrame(int *firstFrame, int *lastFrame); - implemented

    // This constructor originally came from MOTION.CPP
    // 
    // Called from:
    //     SceneElement ctor
    public MotionPath() {
        mbAllocated = false;
        mNodes = null;
        miNumNodes = 0;
    } // MotionPath ctor


    // This destructor originally came from MOTION.CPP
    public void finalize() {
      /*
        if(allocated) delete nodes;
      */
    } // finalize

    
    // This method originally came from MOTION.CPP
    // Called from:
    //     readMotion
    private void allocate(int piNum) {
        if(mbAllocated) {
            miNumNodes = 0;
            mbAllocated = false;
        }

        if(piNum > 0) {
            mNodes = new MotionNode[piNum];
            if(mNodes != null) {
                miNumNodes = piNum;
                mbAllocated = true;
            }
        }
    } // allocate
  
    
    // This method originally came from MOTION.CPP
    // 
    // Returns the first (pIFirstFrame) and last (pILastFrame) frame 
    // numbers identified in the motion file. See p 131 of 
    // Visual Special Effects Toolkit in C++
    //
    // Called from:
    //     getNode
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public void getFirstLastFrame(Integer pIFirstFrame, Integer pILastFrame) {
        // Set the output parameters
        pIFirstFrame = mNodes[0].miNodeNum;
        pILastFrame = mNodes[miNumNodes - 1].miNodeNum;
    } // getFirstLastFrame


    // This method originally came from MOTION.CPP
    //
    // Returns an interpolated MotionNode object pMtnNode, 
    // given any frame number piFrameNumber that lies 
    // between the first and last frame numbers. See p 131 of
    // Visual Special Effects Toolkit in C++
    //
    // Called from
    //     SceneList.getViewMatrix
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public int getNode(int piFrameNumber, MotionNode pMtnNode) {
        int iPrevNode = 0, iNextNode = 0, i;
        float fDist, fMult, fDiff;

        // Filter the frameNumber
        Integer firstFrame = 0, lastFrame = 0;

        // The following method sets firstFrame and lastFrame
        getFirstLastFrame(firstFrame, lastFrame);

        // Ensure that firstFrame <= piFrameNumber <= lastFrame, 
        // by changing piFrameNumber if necessary
        if(piFrameNumber < firstFrame) {
            piFrameNumber = firstFrame;
        }
        if(piFrameNumber > lastFrame) {
            piFrameNumber = lastFrame;
        }
      
        for(i = 0; i < miNumNodes; i++) {
            // If piFrameNumber is the same as the node number of an existing node in
            // array mNodes ...
            if(mNodes[i].miNodeNum == piFrameNumber) {
                // Set pMtnNode to be a copy of mNodes[i]
                pMtnNode.copy(mNodes[i]);
                return 0;
            }
        }

        // We'll have to create an "interpolated" MotionNode pMtnNode
        i = 0;
        while(i < (miNumNodes - 1)) {
            iPrevNode = mNodes[i].miNodeNum;
            iNextNode = mNodes[i + 1].miNodeNum;
            if(iPrevNode < piFrameNumber && iNextNode > piFrameNumber) break;
            i++;
        } // while

        if(i == (miNumNodes - 1)) {
            pMtnNode.clear();
            return -1;
        }

        fDiff = iNextNode - iPrevNode;
        fMult = (float)(piFrameNumber - iPrevNode) / fDiff;
      
        // Set the interpolated MotionNode's scale values mfSx, mfSy, and mfSz
        fDist = mNodes[i + 1].mfSx - mNodes[i].mfSx;
        pMtnNode.mfSx = mNodes[i].mfSx + fDist * fMult;
      
        fDist = mNodes[i + 1].mfSy - mNodes[i].mfSy;
        pMtnNode.mfSy = mNodes[i].mfSy + fDist * fMult;
      
        fDist = mNodes[i + 1].mfSz - mNodes[i].mfSz;
        pMtnNode.mfSz = mNodes[i].mfSz + fDist * fMult;
      
        // Set the interpolated MotionNode's rotation values mfRx, mfRy and mfRz
        fDist = mNodes[i + 1].mfRx - mNodes[i].mfRx;
        pMtnNode.mfRx = mNodes[i].mfRx + fDist * fMult;
      
        fDist = mNodes[i + 1].mfRy - mNodes[i].mfRy;
        pMtnNode.mfRy = mNodes[i].mfRy + fDist * fMult;
      
        fDist = mNodes[i + 1].mfRz - mNodes[i].mfRz;
        pMtnNode.mfRz = mNodes[i].mfRz + fDist * fMult;
      
        // Set the interpolated MotionNode's translation values mfTx, mfTy and mfTz
        fDist = mNodes[i + 1].mfTx - mNodes[i].mfTx;
        pMtnNode.mfTx = mNodes[i].mfTx + fDist * fMult;
      
        fDist = mNodes[i + 1].mfTy - mNodes[i].mfTy;
        pMtnNode.mfTy = mNodes[i].mfTy + fDist * fMult;
      
        fDist = mNodes[i + 1].mfTz - mNodes[i].mfTz;
        pMtnNode.mfTz = mNodes[i].mfTz + fDist * fMult;
      
        // Set the interpolated MotionNode's alpha value mfAlpha
        fDist = mNodes[i + 1].mfAlpha - mNodes[i].mfAlpha;
        pMtnNode.mfAlpha = mNodes[i].mfAlpha + fDist * fMult;

        // Set the interpolated MotionNode's node number miNodeNum
        pMtnNode.miNodeNum = piFrameNumber;

        return 0;
    } // getNode
  
    
    // This method originally came from MOTION.CPP
    //
    // This method reads a motion path file. These files are text files
    // with a '.pth' extension. See p 127 and 129 - 130 of the book
    // Visual Special Effects Toolkit in C++.
    //
    // Called from:
    //     SceneElement ctor
    public int readMotion(String psPathName) {
        String sMsgText, sText = "";
        String sKeyWord;
        MotionNode tempMotionNode;

        File mtnPathFile = new File(psPathName);
        FileReader fileReader;
        try {
            fileReader = new FileReader(mtnPathFile);
        } catch(FileNotFoundException fnfe) {
            sMsgText = "readMotion: Unable to open file: " + psPathName;
            Globals.statusPrint(sMsgText) ;
            return -1;
        }

        LineNumberReader filein = new LineNumberReader(fileReader);
        Integer iLineCounter = 0;
        int iNodeCounter = 0;
        int iStatus = 0;
        tempMotionNode = new MotionNode();
  
        // The next method reads the next non-comment line from filein and 
        // places it in String sText, and updates the line counter iLineCounter
        sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein);
        
        while(!sKeyWord.equalsIgnoreCase("EOF")) {
            // The following method will set the fields of object tempMotionNode
            // with data read from the string sKeyWord (NOT from a file). 
            // If everything was read and set correctly, it will return the value 0.
            iStatus = tempMotionNode.read(sKeyWord);
            if(iStatus != 0) {
                sMsgText = "Cannot Read: " + psPathName + "  Line: " + iLineCounter;
                Globals.statusPrint(sMsgText);
                return iStatus;
            }

            iNodeCounter++;
            // The next method reads the next non-comment line from filein and 
            // places it in String sText, and updates the line counter iLineCounter
            sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein);
        } // while

        allocate(iNodeCounter);
        if(!mbAllocated) {
            Globals.statusPrint("readMotion: Cannot allocate memory.\n");
            return -1;
        }
  
        try {
            filein.close();
        } catch (IOException ioe) {
            Globals.statusPrint("readMotion: Could not close file " + psPathName);
        }

        FileReader fileReader2;
        try {
            fileReader2 = new FileReader(mtnPathFile);
        } catch(FileNotFoundException fnfe) {
            sMsgText = "readMotion: Unable to open file: " + psPathName;
            Globals.statusPrint(sMsgText) ;
            return -1;
        }
        
        LineNumberReader filein2 = new LineNumberReader(fileReader2);
        iNodeCounter = 0;
        sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein2);
        while(!sKeyWord.equalsIgnoreCase("EOF")) {
            iStatus = mNodes[iNodeCounter].read(sKeyWord);
            iNodeCounter++;
            sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein2);
        }

        try {
            filein2.close();
        } catch (IOException ioe) {
            Globals.statusPrint("readMotion: Could not close file " + psPathName);
        }
        
        return iStatus;
    } // readMotion
} // class MotionPath