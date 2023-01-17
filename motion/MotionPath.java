package motion;

import globals.Globals;

public class MotionPath {

  private boolean allocated;
  // private void allocate(int);

  public int numnodes;
  public MotionNode[] nodes;
  // public int read(char *);
  // public int readMotion(char *);
  // public int write(char *);
  // public int getNode(int, motionNode *);
  // public motionPath(void);
  // public ~motionPath(void);
  // public void getFirstLastFrame(int *firstFrame, int *lastFrame);

    // This constructor came from MOTION.CPP
    public MotionPath() {
        allocated = false;
        nodes = null;
        numnodes = 0;
    } // MotionPath ctor

    // This destructor came from MOTION.CPP
    public void finalize() {
      /*
        if(allocated) delete nodes;
      */
    } // finalize

    // This method came from MOTION.CPP
    private void allocate(int num) {
        if(allocated) {
            numnodes = 0;
            allocated = false;
        }

        if(num > 0) {
            nodes = new MotionNode[num];
            if(nodes != null) {
                numnodes = num;
                allocated = true;
            }
        }
    } // allocate
  
    
    // This method came from MOTION.CPP
    // Called from:
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public void getFirstLastFrame(Integer pIFirstFrame, Integer pILastFrame) {
        pIFirstFrame = nodes[0].nodenum;
        pILastFrame = nodes[numnodes-1].nodenum;
    } // getFirstLastFrame


    // This method came from MOTION.CPP
    // Called from
    //     SceneList.getViewMatrix
    //     SceneList.preview
    //     SceneList.previewStill
    public int getNode(int frameNumber, MotionNode mn) {
        int pn = 0, nn = 0, a;
        float dist, mult, diff;

        // filter the frameNumber
        Integer firstFrame = 0, lastFrame = 0;

        // The following method sets firstFrame and lastFrame
        getFirstLastFrame(firstFrame, lastFrame);
        if(frameNumber < firstFrame) {
            frameNumber = firstFrame;
        }
        if(frameNumber > lastFrame) {
            frameNumber = lastFrame;
        }
      
        for(a = 0; a < numnodes; a++) {
            if(nodes[a].nodenum == frameNumber) {
                mn.copy(nodes[a]);
                return 0;
            }
        }

        a = 0;
        while(a < (numnodes - 1)) {
            pn = nodes[a].nodenum;
            nn = nodes[a + 1].nodenum;
            if(pn < frameNumber && nn > frameNumber) break;
            a++;
        } // while

        if(a == (numnodes-1)) {
            mn.clear();
            return -1;
        }

        diff = nn - pn;
        mult = (float)(frameNumber - pn) / diff;
      
        dist = nodes[a + 1].sx - nodes[a].sx;
        mn.sx = nodes[a].sx + dist * mult;
      
        dist = nodes[a + 1].sy - nodes[a].sy;
        mn.sy = nodes[a].sy + dist * mult;
      
        dist = nodes[a + 1].sz - nodes[a].sz;
        mn.sz = nodes[a].sz + dist * mult;
      
        dist = nodes[a + 1].rx - nodes[a].rx;
        mn.rx = nodes[a].rx + dist * mult;
      
        dist = nodes[a + 1].ry - nodes[a].ry;
        mn.ry = nodes[a].ry + dist * mult;
      
        dist = nodes[a + 1].rz - nodes[a].rz;
        mn.rz = nodes[a].rz + dist * mult;
      
        dist = nodes[a + 1].tx - nodes[a].tx;
        mn.tx = nodes[a].tx + dist * mult;
      
        dist = nodes[a + 1].ty - nodes[a].ty;
        mn.ty = nodes[a].ty + dist * mult;
      
        dist = nodes[a + 1].tz - nodes[a].tz;
        mn.tz = nodes[a].tz + dist * mult;
      
        dist = nodes[a + 1].alpha - nodes[a].alpha;
        mn.alpha = nodes[a].alpha + dist * mult;
        mn.nodenum = frameNumber;

        return 0;
    } // getNode
  
    
    // This method came from MOTION.CPP
    public int readMotion(String pathName) {
        String msgText, theText;
        String theKeyWord;
        MotionNode tempMotionNode;

        // TODO: Replace ifstream with a FileStream
        ifstream filein = new ifstream(pathName, ios.in|ios.nocreate);

        if (filein.fail()) {
            msgText = "readMotion: Unable to open file: " + pathName;
            Globals.statusPrint(msgText) ;
            return -1;
        }
        filein >> ws;
        int lineCounter = 0;
        int nodeCounter = 0;
        int myStatus = 0;
  
        theKeyWord = getNextMotionLine(theText, lineCounter, filein);
        while(!theKeyWord.equalsIgnoreCase("EOF")) {
            myStatus = tempMotionNode.read(theKeyWord);
            if(myStatus != 0) {
                msgText = "Cannot Read: " + pathName + "  Line: " + lineCounter;
                Globals.statusPrint(msgText);
                return myStatus;
            }

            nodeCounter++;
            theKeyWord = getNextMotionLine(theText, lineCounter, filein);
        }

        allocate(nodeCounter);
        if(!allocated) {
            dprintf(("motion.read: Cannot allocate memory.\n"));
            return -1;
        }
  
        filein.close();
        // TODO: Replace ifstream with a FileStream
        ifstream filein2 = new ifstream(pathName);
        nodeCounter = 0;
        theKeyWord = getNextMotionLine(theText, lineCounter, filein2);
        while(!theKeyWord.equalsIgnoreCase("EOF")) {
            myStatus = nodes[nodeCounter].read(theKeyWord);
            nodeCounter++;
            theKeyWord = getNextMotionLine(theText, lineCounter, filein2);
        }

        filein2.close();
        return myStatus;
    } // readMotion
} // class MotionPath