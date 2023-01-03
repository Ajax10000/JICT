package motion;

public class MotionNode {
   public int nodenum;
   public float tx,ty,tz;
   public float rx,ry,rz;
   public float sx,sy,sz;
   public float alpha;

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
      String aNodeNum;
      String anrx, anry, anrz; 
      String ansx, ansy, ansz; 
      String antx, anty, antz;
      String anAlpha;
   
      aNodeNum = strtok(psBuffer, " ");
      if(aNodeNum.length() > 0)
         nodenum = Integer.parseInt(aNodeNum);
      else
         return 1;
   
      anrx = strtok(null, " ");
      if(anrx.length() > 0)
         rx = Float.parseFloat(anrx);
      else
         return 2;
   
      anry = strtok(null, " ");
      if(anry.length() > 0)
         ry = Float.parseFloat(anry);
      else
         return 3;
   
      anrz = strtok(null, " ");
      if(anrz.length() > 0)
         rz = Float.parseFloat(anrz);
      else
         return 4;
   
      ansx = strtok(null, " ");
      if(ansx.length() > 0)
         sx = Float.parseFloat(ansx);
      else
         return 5;
   
      ansy = strtok(null, " ");
      if(ansy.length() > 0)
         sy = Float.parseFloat(ansy);
      else
         return 6;
   
      ansz = strtok(null, " ");
      if(ansz.length() > 0)
         sz = Float.parseFloat(ansz);
      else
         return 7;
   
      antx = strtok(null, " ");
      if(antx.length() > 0)
         tx = Float.parseFloat(antx);
      else
         return 8;
   
      anty = strtok(null, " ");
      if(anty.length() > 0)
         ty = Float.parseFloat(anty);
      else
         return 9;
   
      antz = strtok(null, " ");
      if(antz.length() > 0)
         tz = Float.parseFloat(antz);
      else
         return 10;
   
      anAlpha = strtok(null, " ");
      if(anAlpha.length() > 0)
         alpha = Float.parseFloat(anAlpha);
      else
         return 11;
   
      return 0;
   } // read
} // class MotionNode

