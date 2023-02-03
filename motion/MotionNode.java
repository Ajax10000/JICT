package motion;

import java.util.StringTokenizer;

// A MotionNode object represents a single keyframe in a motion path.
// See p 131 of Visual Special Effects Toolkit in C++
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


   // Called from:
   //     SceneList.getViewMatrix
   public MotionNode() {
      clear();
   }


   // This method originally came from MOTION.CPP
   // 
   // Called from:
   //     MotionPath.getNode
   public void copy(MotionNode mn) {
      this.miNodeNum = mn.miNodeNum;

      this.mfTx = mn.mfTx;
      this.mfTy = mn.mfTy;
      this.mfTz = mn.mfTz;

      this.mfRx = mn.mfRx;
      this.mfRy = mn.mfRy;
      this.mfRz = mn.mfRz;

      this.mfSx = mn.mfSx;
      this.mfSy = mn.mfSy;
      this.mfSz = mn.mfSz;

      this.mfAlpha = mn.mfAlpha;
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
      String aNodeNum;
      String anrx, anry, anrz; 
      String ansx, ansy, ansz; 
      String antx, anty, antz;
      String anAlpha;
   
      strTok = new StringTokenizer(psBuffer, " ");
      aNodeNum = strTok.nextToken(" ");
      if(aNodeNum.length() > 0) {
         miNodeNum = Integer.parseInt(aNodeNum);
      } else {
         return 1;
      }
   
      anrx = strTok.nextToken(" ");
      if(anrx.length() > 0) {
         mfRx = Float.parseFloat(anrx);
      } else {
         return 2;
      }
   
      anry = strTok.nextToken(" ");
      if(anry.length() > 0) {
         mfRy = Float.parseFloat(anry);
      } else {
         return 3;
      }
   
      anrz = strTok.nextToken(" ");
      if(anrz.length() > 0) {
         mfRz = Float.parseFloat(anrz);
      } else {
         return 4;
      }
   
      ansx = strTok.nextToken(" ");
      if(ansx.length() > 0) {
         mfSx = Float.parseFloat(ansx);
      } else {
         return 5;
      }
   
      ansy = strTok.nextToken(" ");
      if(ansy.length() > 0) {
         mfSy = Float.parseFloat(ansy);
      } else {
         return 6;
      }
   
      ansz = strTok.nextToken(" ");
      if(ansz.length() > 0) {
         mfSz = Float.parseFloat(ansz);
      } else {
         return 7;
      }
   
      antx = strTok.nextToken(" ");
      if(antx.length() > 0) {
         mfTx = Float.parseFloat(antx);
      } else {
         return 8;
      }
   
      anty = strTok.nextToken(" ");
      if(anty.length() > 0) {
         mfTy = Float.parseFloat(anty);
      } else {
         return 9;
      }
   
      antz = strTok.nextToken(" ");
      if(antz.length() > 0) {
         mfTz = Float.parseFloat(antz);
      } else {
         return 10;
      }
   
      anAlpha = strTok.nextToken(" ");
      if(anAlpha.length() > 0) {
         mfAlpha = Float.parseFloat(anAlpha);
      } else {
         return 11;
      }
   
      return 0;
   } // read
} // class MotionNode

