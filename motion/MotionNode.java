package motion;

import java.util.StringTokenizer;

public class MotionNode {
   public int nodenum;
   public float tx, ty, tz;
   public float rx, ry, rz;
   public float sx, sy, sz;
   public float alpha;


   // Called from:
   //     SceneList.getViewMatrix
   public void MotionNode() {
      clear();
   }


   // This method came from MOTION.CPP
   public void copy(MotionNode mn) {
      this.nodenum = mn.nodenum;

      this.tx = mn.tx;
      this.ty = mn.ty;
      this.tz = mn.tz;

      this.rx = mn.rx;
      this.ry = mn.ry;
      this.rz = mn.rz;

      this.sx = mn.sx;
      this.sy = mn.sy;
      this.sz = mn.sz;

      this.alpha = mn.alpha;
   } // copy
  

   // This method came from MOTION.CPP
   public void clear() {
      nodenum = 0;

      tx = 0.0f;  
      ty = 0.0f;  
      tz = 0.0f;

      rx = 0.0f;  
      ry = 0.0f;  
      rz = 0.0f;

      sx = 0.0f;  
      sy = 0.0f;  
      sz = 0.0f;

      alpha = 0.0f;
   } // clear


   // This method came from MOTION.CPP
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
         nodenum = Integer.parseInt(aNodeNum);
      } else {
         return 1;
      }
   
      anrx = strTok.nextToken(" ");
      if(anrx.length() > 0) {
         rx = Float.parseFloat(anrx);
      } else {
         return 2;
      }
   
      anry = strTok.nextToken(" ");
      if(anry.length() > 0) {
         ry = Float.parseFloat(anry);
      } else {
         return 3;
      }
   
      anrz = strTok.nextToken(" ");
      if(anrz.length() > 0) {
         rz = Float.parseFloat(anrz);
      } else {
         return 4;
      }
   
      ansx = strTok.nextToken(" ");
      if(ansx.length() > 0) {
         sx = Float.parseFloat(ansx);
      } else {
         return 5;
      }
   
      ansy = strTok.nextToken(" ");
      if(ansy.length() > 0) {
         sy = Float.parseFloat(ansy);
      } else {
         return 6;
      }
   
      ansz = strTok.nextToken(" ");
      if(ansz.length() > 0) {
         sz = Float.parseFloat(ansz);
      } else {
         return 7;
      }
   
      antx = strTok.nextToken(" ");
      if(antx.length() > 0) {
         tx = Float.parseFloat(antx);
      } else {
         return 8;
      }
   
      anty = strTok.nextToken(" ");
      if(anty.length() > 0) {
         ty = Float.parseFloat(anty);
      } else {
         return 9;
      }
   
      antz = strTok.nextToken(" ");
      if(antz.length() > 0) {
         tz = Float.parseFloat(antz);
      } else {
         return 10;
      }
   
      anAlpha = strTok.nextToken(" ");
      if(anAlpha.length() > 0) {
         alpha = Float.parseFloat(anAlpha);
      } else {
         return 11;
      }
   
      return 0;
   } // read
} // class MotionNode

