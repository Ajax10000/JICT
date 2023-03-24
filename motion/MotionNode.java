package motion;

import java.util.StringTokenizer;

// A MotionNode object represents a single keyframe in a motion path.
// See p 131 of Visual Special Effects Toolkit in C++
// MotionPath objects have a field which is an array of MotionNode objects.
// 
// MotionNode and MotionPath objects are used when a scene file (with extension .scn)
// is read that contains a Model with a MOTIONPATH line, with a non-null path name.
// The path file name should have the .pth file extension:
// Model <modelNamae> [Blend|NoBlend] [Warp|NoWarp] AlphaScale alpha [Image|Shape|QuaMesh|SEquence]
// ...
// MotionPath [None|<pathName>]
public class MotionNode {
   // Populated in methods copy, clear, read
   // Set in:
   //     MotionPath.getNode
   // Used in:
   //     SceneList.adjustTransforms
   public int miNodeNum;

   // Populated in methods copy, clear, read
   // Set in:
   //     MotionPath.getNode
   // Used in:
   //     SceneList.adjustTransforms
   //     SceneList.getViewMatrix
   public float mfTx, mfTy, mfTz;

   // Populated in methods copy, clear, read
   // Set in:
   //     MotionPath.getNode
   // Used in:
   //     SceneList.adjustTransforms
   //     SceneList.getViewMatrix
   public float mfRx, mfRy, mfRz;

   // Populated in methods copy, clear, read
   // Set in:
   //     MotionPath.getNode
   // Used in:
   //     SceneList.adjustTransforms
   public float mfSx, mfSy, mfSz;

   // Populated in methods copy, clear, read
   // Set in:
   //     MotionPath.getNode
   // Used in:
   //     SceneList.adjustTransforms
   public float mfAlpha;

/*
  int read(char *); - implemented
  int read2(char *); ---- NOT IMPLEMENTED - could not find in the C++ code
  void copy(motionNode); - implemented
  void clear(void); - implemented 
 */

 
   // Called from:
   //     SceneList.getViewMatrix
   public MotionNode() {
      clear();
   }


   // This method originally came from MOTION.CPP
   // 
   // Called from:
   //     MotionPath.getNode
   public void copy(MotionNode pMtnNode) {
      this.miNodeNum = pMtnNode.miNodeNum;

      this.mfTx = pMtnNode.mfTx;
      this.mfTy = pMtnNode.mfTy;
      this.mfTz = pMtnNode.mfTz;

      this.mfRx = pMtnNode.mfRx;
      this.mfRy = pMtnNode.mfRy;
      this.mfRz = pMtnNode.mfRz;

      this.mfSx = pMtnNode.mfSx;
      this.mfSy = pMtnNode.mfSy;
      this.mfSz = pMtnNode.mfSz;

      this.mfAlpha = pMtnNode.mfAlpha;
   } // copy
  

   // This method originally came from MOTION.CPP
   //
   // Called from:
   //     MotionPath.getNode
   public void clear() {
      miNodeNum = 0;

      mfTx = 0.0f;  
      mfTy = 0.0f;  
      mfTz = 0.0f;

      mfRx = 0.0f;  
      mfRy = 0.0f;  
      mfRz = 0.0f;

      mfSx = 0.0f;  
      mfSy = 0.0f;  
      mfSz = 0.0f;

      mfAlpha = 0.0f;
   } // clear


   // This method originally came from MOTION.CPP
   //
   // Called from:
   //     MotionPath.readMotion
   public int read(String psBuffer) {
      StringTokenizer strTok;
      String sNodeNum;
      String sRx, sRy, sRz; 
      String sSx, sSy, sSz; 
      String sTx, sTy, sTz;
      String sAlpha;
   
      strTok = new StringTokenizer(psBuffer, " ");
      sNodeNum = strTok.nextToken(" ");
      if(sNodeNum.length() > 0) {
         this.miNodeNum = Integer.parseInt(sNodeNum);
      } else {
         return 1;
      }
   
      sRx = strTok.nextToken(" ");
      if(sRx.length() > 0) {
         this.mfRx = Float.parseFloat(sRx);
      } else {
         return 2;
      }
   
      sRy = strTok.nextToken(" ");
      if(sRy.length() > 0) {
         this.mfRy = Float.parseFloat(sRy);
      } else {
         return 3;
      }
   
      sRz = strTok.nextToken(" ");
      if(sRz.length() > 0) {
         this.mfRz = Float.parseFloat(sRz);
      } else {
         return 4;
      }
   
      sSx = strTok.nextToken(" ");
      if(sSx.length() > 0) {
         this.mfSx = Float.parseFloat(sSx);
      } else {
         return 5;
      }
   
      sSy = strTok.nextToken(" ");
      if(sSy.length() > 0) {
         this.mfSy = Float.parseFloat(sSy);
      } else {
         return 6;
      }
   
      sSz = strTok.nextToken(" ");
      if(sSz.length() > 0) {
         this.mfSz = Float.parseFloat(sSz);
      } else {
         return 7;
      }
   
      sTx = strTok.nextToken(" ");
      if(sTx.length() > 0) {
         this.mfTx = Float.parseFloat(sTx);
      } else {
         return 8;
      }
   
      sTy = strTok.nextToken(" ");
      if(sTy.length() > 0) {
         this.mfTy = Float.parseFloat(sTy);
      } else {
         return 9;
      }
   
      sTz = strTok.nextToken(" ");
      if(sTz.length() > 0) {
         this.mfTz = Float.parseFloat(sTz);
      } else {
         return 10;
      }
   
      sAlpha = strTok.nextToken(" ");
      if(sAlpha.length() > 0) {
         this.mfAlpha = Float.parseFloat(sAlpha);
      } else {
         return 11;
      }
   
      return 0;
   } // read
} // class MotionNode

