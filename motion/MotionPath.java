package motion;

import globals.Globals;

public class MotionPath {

  private boolean mbAllocated;
  // private void allocate(int);

  public int miNumNodes;
  public MotionNode[] mNodes;
  // public int read(char *);
  // public int readMotion(char *);
  // public int write(char *);
  // public int getNode(int, motionNode *);
  // public motionPath(void);
  // public ~motionPath(void);
  // public void getFirstLastFrame(int *firstFrame, int *lastFrame);

    // This constructor came from MOTION.CPP
    public MotionPath() {
        mbAllocated = false;
        mNodes = null;
        miNumNodes = 0;
    } // MotionPath ctor

    // This destructor came from MOTION.CPP
    public void finalize() {
      /*
        if(allocated) delete nodes;
      */
    } // finalize

    // This method came from MOTION.CPP
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
  
    
    // This method came from MOTION.CPP
    // Called from:
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public void getFirstLastFrame(Integer pIFirstFrame, Integer pILastFrame) {
        pIFirstFrame = mNodes[0].nodenum;
        pILastFrame = mNodes[miNumNodes - 1].nodenum;
    } // getFirstLastFrame


    // This method came from MOTION.CPP
    // Called from
    //     SceneList.getViewMatrix
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public int getNode(int piFrameNumber, MotionNode pMtnNode) {
        int iPrevNode = 0, iNextNode = 0, i;
        float fDist, fMult, fDiff;

        // filter the frameNumber
        Integer firstFrame = 0, lastFrame = 0;

        // The following method sets firstFrame and lastFrame
        getFirstLastFrame(firstFrame, lastFrame);
        if(piFrameNumber < firstFrame) {
            piFrameNumber = firstFrame;
        }
        if(piFrameNumber > lastFrame) {
            piFrameNumber = lastFrame;
        }
      
        for(i = 0; i < miNumNodes; i++) {
            if(mNodes[i].nodenum == piFrameNumber) {
                pMtnNode.copy(mNodes[i]);
                return 0;
            }
        }

        i = 0;
        while(i < (miNumNodes - 1)) {
            iPrevNode = mNodes[i].nodenum;
            iNextNode = mNodes[i + 1].nodenum;
            if(iPrevNode < piFrameNumber && iNextNode > piFrameNumber) break;
            i++;
        } // while

        if(i == (miNumNodes - 1)) {
            pMtnNode.clear();
            return -1;
        }

        fDiff = iNextNode - iPrevNode;
        fMult = (float)(piFrameNumber - iPrevNode) / fDiff;
      
        // Process scale point
        fDist = mNodes[i + 1].sx - mNodes[i].sx;
        pMtnNode.sx = mNodes[i].sx + fDist * fMult;
      
        fDist = mNodes[i + 1].sy - mNodes[i].sy;
        pMtnNode.sy = mNodes[i].sy + fDist * fMult;
      
        fDist = mNodes[i + 1].sz - mNodes[i].sz;
        pMtnNode.sz = mNodes[i].sz + fDist * fMult;
      
        // Process rotation point
        fDist = mNodes[i + 1].rx - mNodes[i].rx;
        pMtnNode.rx = mNodes[i].rx + fDist * fMult;
      
        fDist = mNodes[i + 1].ry - mNodes[i].ry;
        pMtnNode.ry = mNodes[i].ry + fDist * fMult;
      
        fDist = mNodes[i + 1].rz - mNodes[i].rz;
        pMtnNode.rz = mNodes[i].rz + fDist * fMult;
      
        // Process translation point
        fDist = mNodes[i + 1].tx - mNodes[i].tx;
        pMtnNode.tx = mNodes[i].tx + fDist * fMult;
      
        fDist = mNodes[i + 1].ty - mNodes[i].ty;
        pMtnNode.ty = mNodes[i].ty + fDist * fMult;
      
        fDist = mNodes[i + 1].tz - mNodes[i].tz;
        pMtnNode.tz = mNodes[i].tz + fDist * fMult;
      
        // Process alpha
        fDist = mNodes[i + 1].alpha - mNodes[i].alpha;
        pMtnNode.alpha = mNodes[i].alpha + fDist * fMult;
        pMtnNode.nodenum = piFrameNumber;

        return 0;
    } // getNode
  
    
    // This method came from MOTION.CPP
    // Called from:
    //     SceneElement ctor
    public int readMotion(String psPathName) {
        String sMsgText, sText;
        String sKeyWord;
        MotionNode tempMotionNode;

        // TODO: Replace ifstream with a FileStream
        ifstream filein = new ifstream(psPathName, ios.in|ios.nocreate);

        if (filein.fail()) {
            sMsgText = "readMotion: Unable to open file: " + psPathName;
            Globals.statusPrint(sMsgText) ;
            return -1;
        }
        filein >> ws;
        Integer iLineCounter = 0;
        int iNodeCounter = 0;
        int iStatus = 0;
  
        sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein);
        while(!sKeyWord.equalsIgnoreCase("EOF")) {
            iStatus = tempMotionNode.read(sKeyWord);
            if(iStatus != 0) {
                sMsgText = "Cannot Read: " + psPathName + "  Line: " + iLineCounter;
                Globals.statusPrint(sMsgText);
                return iStatus;
            }

            iNodeCounter++;
            sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein);
        }

        allocate(iNodeCounter);
        if(!mbAllocated) {
            dprintf(("motion.read: Cannot allocate memory.\n"));
            return -1;
        }
  
        filein.close();
        // TODO: Replace ifstream with a FileStream
        ifstream filein2 = new ifstream(psPathName);
        iNodeCounter = 0;
        sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein2);
        while(!sKeyWord.equalsIgnoreCase("EOF")) {
            iStatus = nodes[iNodeCounter].read(sKeyWord);
            iNodeCounter++;
            sKeyWord = Globals.getNextMotionLine(sText, iLineCounter, filein2);
        }

        filein2.close();
        return iStatus;
    } // readMotion
} // class MotionPath