package globals;

import core.MemImage;

import java.text.DecimalFormat;

import math.TMatrix;
import math.Vect;

import structs.Point2d;
import structs.Point3d;

public class GPipe {
    boolean gpdebug = false;
    String sceneName, scenePathName;
    int effectType, mode, colorMode;
    int outputRows, outputColumns;
    boolean lightingEnabled;
    boolean zBufferEnabled;
    int backfaceCullingEnabled;
    MemImage zBuffer;
    MemImage outputImage;
    TMatrix viewMatrix;        // Contains a viewpoint transformation
    TMatrix penMatrix;         // Contains model transformation
    TMatrix viewPenMatrix;     // Contains the composite view, pen transformation
    Point3d lightSource;
    Point3d viewPoint;
    Point3d viewAngle;
    Point3d penRotation;        // rotation angles in radians
    Point3d penScale;
    Point3d penTranslation;
    Point3d ref;
    Point3d minBoundingBox;     // bounding box minimums
    Point3d maxBoundingBox;     // bounding box maximums
    boolean boundingBoxInitialized; // FALSE if not initialized, else TRUE

    // These values came from GPIPE.H
    public static final int NUMFACETS = 20;
    public static final int PIXELSPERUNIT = 40;

    // This value came from GPIPE.CPP
    public static final float VP = 40.0f;

    // This value came from ICT20.H
    public static final float ZBUFFERMAXVALUE = 2.0E31f;

    // This value came from ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f;

    // Effect Types
    public static final int STILL    = 1;
    public static final int SEQUENCE = 2;
    public static final int MORPH    = 3;

/*
//
//  VRML defaults and constants
#define PIXELSPERUNIT 40
#define NUMFACETS 20


class gPipe {
private:
  char sceneName[MAXPATH], scenePathName[MAXPATH];
  int effectType, mode, colorMode;
  int outputRows, outputColumns;
  int lightingEnabled;
  int zBufferEnabled;
  int backfaceCullingEnabled;
  memImage *zBuffer;
  memImage *outputImage;
  tMatrix viewMatrix;        // Contains a viewpoint transformation
  tMatrix penMatrix;         // Contains model transformation
  tMatrix viewPenMatrix;     // Contains the composite view, pen transformation
  point3d lightSource;
  point3d viewPoint;
  point3d viewAngle;
  point3d penRotation;        // rotation angles in radians
  point3d penScale;
  point3d penTranslation;
  point3d ref;
  point3d minBoundingBox;     // bounding box minimums
  point3d maxBoundingBox;     // bounding box maximums
  int boundingBoxInitialized; // FALSE if not initialized, else TRUE
public:
   gPipe();
   ~gPipe();
  int initialize();	  // Opens zBuffer, output image
  int addFace(point3d *p1, point3d *p2, point3d *p3, point3d *p4);
  int addText(char *theText, point3d *aLocation);
  int addLine(point3d *p1, point3d *p2);
  void setPenScale(float scaleX, float scaleY, float scaleZ);
  void setPenXRotation(float angleX);
  void setPenYRotation(float angleY);
  void setPenZRotation(float angleZ);
  void setPenTranslation(float tranX, float tranY, float tranZ);
  void setPenMatrix();
  void setViewMatrix();
  void setViewPenMatrix();
  void setLightSource(point3d *aPoint);
  void resetPenMatrix();
  int saveZBuffer(char *outputPath);
  int saveOutputImage(char *outputPath);
  void setZBuffer(int indicator);
  void setLighting(int indicator);

  void addCube(float width, float height, float depth);
  void addSphere(float radius);
  void addCylTop(float height, float radius);
  void addCylBottom(float height, float radius);
  void addCylSides(float height, float radius);
  void addConeBottom(float height, float bottomRadius);
  void addConeSides(float height, float bottomRadius);
  void reset();
  void updateBoundingBox(point3d *point);
  int viewPointInsideBoundingBox();

};  
 */

    // This constructor came from GPIPE.CPP
    public GPipe() {
        outputRows = 512;
        outputColumns = 512;
        zBufferEnabled = true;
        lightingEnabled = true;
        effectType = STILL;

        scenePathName = "d:\\ict20\\output\\gPipe.bmp";

        //  this vector indicates the direction of the light source
        lightSource.x = -150.0f;
        lightSource.y =    0.0f;
        lightSource.z = -200.0f;
        
        viewPoint.x =   0.0f;
        viewPoint.y =   0.0f;
        viewPoint.z = 200.0f;

        viewAngle.x = -20.0f * F_DTR;
        viewAngle.y =   0.0f * F_DTR;
        viewAngle.z =   0.0f;
        setViewMatrix();

        ref.x = 0.0f;   //  default location of the virtual pen
        ref.y = 0.0f;
        ref.z = 0.0f;

        penRotation.x = 0.0f;
        penRotation.y = 0.0f;
        penRotation.z = 0.0f;
        
        penTranslation.x = 0.0f;
        penTranslation.y = 0.0f;
        penTranslation.z = 0.0f;
        
        penScale.x = 1.0f;
        penScale.y = 1.0f;
        penScale.z = 1.0f;
        setPenMatrix();
        
        boundingBoxInitialized = false;
        
        setZBuffer(true);
        setLighting(true);
        Point3d aLight = new Point3d();
        aLight.x =    0.0f;
        aLight.y =    0.0f;
        aLight.z = -100.0f;
        setLightSource(aLight);
    }


    // This destructor came from GPIPE.CPP
    public void finalize() {

    }

    // This method came from GPIPE.CPP
    public void reset() {
        zBuffer.init32(ZBUFFERMAXVALUE); 
        outputImage.clear();
        penRotation.x = 0.0f;
        penRotation.y = 0.0f;
        penRotation.z = 0.0f;
            
        penTranslation.x = 0.0f;
        penTranslation.y = 0.0f;
        penTranslation.z = 0.0f;
            
        penScale.x = 1.0f;
        penScale.y = 1.0f;
        penScale.z = 1.0f;
        setPenMatrix();
    }

    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public int initialize() {
        // Open the output image
        outputImage = new MemImage(outputRows, outputColumns);
        if (!outputImage.isValid()) {
            Globals.statusPrint("gPipe: Not enough memory to open output image");
            return -1;
        }

        // Open the zBuffer if necessary
        if( zBufferEnabled) {
            zBuffer = new MemImage(outputRows, outputColumns, 32);
            if (!zBuffer.isValid()) {
                Globals.statusPrint("gPipe: Not enough memory to open Z Buffer");
                return -1;
            }

            // fill the zBuffer with maximum values
            zBuffer.init32(ZBUFFERMAXVALUE); 
        }

        return 0;
    } // initialize


    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public int addFace(Point3d p1, Point3d p2, Point3d c1, Point3d c2) {
        // Input points are assumed to be oriented counter-clockwise from 
        // the first point.
        Point3d centroid = new Point3d();
        // Point3d *np1, Point3d *np2, *nc1, *nc2;  // lighting normals // these variables are not used
        float ip1;
        // float ip2, ic1, ic2;		 // intensities at face cornerpoints, but not used

        if (gpdebug) {
            Globals.statusPrint("-----------------addFace--------------------");
            p1.display("p1");
            p2.display("p2");
            c1.display("c1");
            if(c2 == null) 
                Globals.statusPrint("c2 is NULL");
            else
                c2.display("c2");
        }

        setViewPenMatrix();

        //
        //  Project the four points to the screen
        //
        Point2d sp1 = new Point2d();
        Point2d sp2 = new Point2d();
        Point2d sc1 = new Point2d(); 
        Point2d sc2 = new Point2d();;
        float dPrev1 = 0f, dPrev2 = 0f, dCur1 = 0f, dCur2 = 0f;
        Point3d t1 = new Point3d();
        Point3d t2 = new Point3d(); 
        Point3d t3 = new Point3d(); 
        Point3d t4 = new Point3d();

        viewPenMatrix.transformAndProjectPoint1(p1, sp1, ref, outputRows, outputColumns, t1);
        if(zBufferEnabled) {
            dPrev1 = Globals.getDistance3d(t1.x, t1.y, t1.z, viewPoint.x, viewPoint.y, viewPoint.z);
        }
        updateBoundingBox(p1);  //optional

        viewPenMatrix.transformAndProjectPoint1(p2, sp2, ref, outputRows, outputColumns, t2);
        if(zBufferEnabled) {
            dPrev2 = Globals.getDistance3d(t2.x, t2.y, t2.z, viewPoint.x, viewPoint.y, viewPoint.z);
        }
        updateBoundingBox(p2);  //optional

        viewPenMatrix.transformAndProjectPoint1(c1, sc1, ref, outputRows, outputColumns, t3);
        if(zBufferEnabled) {
            dCur1 = Globals.getDistance3d(t3.x, t3.y, t3.z, viewPoint.x, viewPoint.y, viewPoint.z);
        }
        updateBoundingBox(c1);  //optional

        dCur2 = -1.0f;

        if(c2 != null) {
            viewPenMatrix.transformAndProjectPoint1(c2, sc2, ref, outputRows, outputColumns, t4);
            if(zBufferEnabled) {
                dCur2 = Globals.getDistance3d(t4.x, t4.y, t4.z, viewPoint.x, viewPoint.y, viewPoint.z);
            }
            updateBoundingBox(c2);  //optional
        }

        if (gpdebug) {
            viewPoint.display("ViewPoint");
            ref.display("Reference");
            // np1.display("Normal"); // np1 has not yet been set
            DecimalFormat sixDotTwo = new DecimalFormat("####.##");
            String msgText = 
                "dPrev1: " + sixDotTwo.format(dPrev1) + 
                " dPrev2 " + sixDotTwo.format(dPrev2) + 
                " dCur1: " + sixDotTwo.format(dCur1) + 
                " dCur2: " + sixDotTwo.format(dCur2); 
            Globals.statusPrint(msgText);
        }

        //  Lambertian Shading
        //
        //  Assume a face is planar.  Thus only one surface normal
        if(lightingEnabled) {
            float xMax = t1.x;
            float yMax = t1.y;
            float zMax = t1.z;
            float xMin = t1.x;
            float yMin = t1.y;
            float zMin = t1.z;

            // Get the 3D bounding box
            if(t1.x > xMax) xMax = t1.x;
            if(t2.x > xMax) xMax = t2.x;
            if(t3.x > xMax) xMax = t3.x;

            if(t1.x < xMin) xMin = t1.x;
            if(t2.x < xMin) xMin = t2.x;
            if(t3.x < xMin) xMin = t3.x;

            if(t1.y > yMax) yMax = t1.y;
            if(t2.y > yMax) yMax = t2.y;
            if(t3.y > yMax) yMax = t3.y;

            if(t1.y < yMin) yMin = t1.y;
            if(t2.y < yMin) yMin = t2.y;
            if(t3.y < yMin) yMin = t3.y;

            if(t1.z > zMax) zMax = t1.z;
            if(t2.z > zMax) zMax = t2.z;
            if(t3.z > zMax) zMax = t3.z;

            if(t1.z < zMin) zMin = t1.z;
            if(t2.z < zMin) zMin = t2.z;
            if(t3.z < zMin) zMin = t3.z;

            if(c2 != null) {
                if(t4.z < zMin) zMin = t4.z;
                if(t4.z > zMax) zMax = t4.z;
                if(t4.x > xMax) xMax = t4.x;
                if(t4.x < xMin) xMin = t4.x;
                if(t4.y > yMax) yMax = t4.y;
                if(t4.y < yMin) yMin = t4.y;
            }
            centroid.x = (xMax + xMin) / 2.0f;
            centroid.y = (yMax + yMin) / 2.0f;
            centroid.z = (zMax + zMin) / 2.0f;
            float dCentroid = Globals.getDistance3d(lightSource.x, lightSource.y, lightSource.z, 
                                            centroid.x, centroid.y, centroid.z);
            Point3d np1 = new Point3d();

            // The following method will set np1
            Vect.getNormal2(np1, t1, centroid, t2);

            Vect.vectorNormalize(np1);
            //
            //  kd     the coefficient of reflection or reflectivity of the surface material
            //         highly reflective = 1, highly absorptive = 0
            //	Ip	   the intensity of the light source
            //  Ia     the ambient intensity at the surface
            //  N      The surface Normal (unit vector)
            //  L      The direction of the light source (unit vector)
            //  d      the distance between the surface and the light source
            //
            float kd = 0.85f;
            int Ip = 160;

            ip1 = Globals.lightModel(kd, Ip, 150, np1, lightSource, dCentroid);
            ip1 = Globals.bound(ip1, 1.0f, 255.0f);
        } else {
            ip1 = 175.0f;	   // set a nominal face intensity
        }

        //  Render the face
        if (c2 != null) {
            if(zBufferEnabled) {
                outputImage.fillPolyz( 
                            (int)sp1.x, (int)sp1.y, ip1, dPrev1,
                            (int)sp2.x, (int)sp2.y, ip1, dPrev2,
                            (int)sc1.x, (int)sc1.y, ip1, dCur1,
                            (int)sc2.x, (int)sc2.y, ip1, dCur2, zBuffer);
            } else {
                outputImage.fillPolyz( 
                            (int)sp1.x, (int)sp1.y, (byte)ip1, 0.0f,
                            (int)sp2.x, (int)sp2.y, (byte)ip1, 0.0f,
                            (int)sc1.x, (int)sc1.y, (byte)ip1, 0.0f,
                            (int)sc2.x, (int)sc2.y, (byte)ip1, 0.0f, null);
            }
        } else {
            if(zBufferEnabled) {
                Globals.fillTrianglez( 
                            (int)sp1.x, (int)sp1.y, ip1, dPrev1,
                            (int)sp2.x, (int)sp2.y, ip1, dPrev2,
                            (int)sc1.x, (int)sc1.y, ip1, dCur1,
                            outputImage, zBuffer);
            } else {
                Globals.fillTrianglez( 
                            (int)sp1.x, (int)sp1.y, (byte)ip1, 0.0f,
                            (int)sp2.x, (int)sp2.y, (byte)ip1, 0.0f,
                            (int)sc1.x, (int)sc1.y, (byte)ip1, 0.0f,
                            outputImage, null);
            }
        }

        return 0;
    } // addFace


    // This method came from GPIPE.CPP
    public void setPenScale(float scaleX, float scaleY, float scaleZ) {
        penScale.x = scaleX;
        penScale.y = scaleY;
        penScale.z = scaleZ;
    } // setPenScale
    

    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public void setPenXRotation(float angleRad) {
        penRotation.x += angleRad;
    } // setPenXRotation


    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public void setPenYRotation(float angleRad) {
        penRotation.y += angleRad;
    } // setPenYRotation


    // This method came from GPIPE.CPP
    public void setPenZRotation(float angleRad) {
        penRotation.z += angleRad;
    } // setPenZRotation

    
    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public void setPenTranslation(float tranX, float tranY, float tranZ) {
        penTranslation.x += tranX;
        penTranslation.y += tranY;
        penTranslation.z += tranZ;
    } // setPenTranslation

    
    // This method came from GPIPE.CPP
    public void setPenMatrix() {
        penMatrix.setIdentity();
        penMatrix.scale(penScale.x, penScale.y, penScale.z);
        penMatrix.rotate(penRotation.x, penRotation.y, penRotation.z);
        penMatrix.translate(penTranslation.x, penTranslation.y, penTranslation.z);
    } // setPenMatrix


    // This method came from GPIPE.CPP
    public void setViewMatrix() {
        viewMatrix.setIdentity();
        viewMatrix.scale(1.0f, 1.0f, 1.0f);
        viewMatrix.rotate(-viewAngle.x, -viewAngle.y, -viewAngle.z);
        viewMatrix.translate(-viewPoint.x, -viewPoint.y, -viewPoint.z);
    } // setViewMatrix
    

    // This method came from GPIPE.CPP
    public void setViewPenMatrix() {
        setPenMatrix();
        setViewMatrix();
        viewPenMatrix.multiply(viewMatrix, penMatrix);
    } // setViewPenMatrix


    // This method came from GPIPE.CPP
    public void resetPenMatrix() {
        penMatrix.setIdentity();
    } // resetPenMatrix


    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public int saveZBuffer(String outputPath) {
        int status = zBuffer.saveAs8("d:\\ict20\\output\\gPipeZBuffer8.bmp");
        return status;
    } // saveZBuffer

    
    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public int saveOutputImage(String outputPath) {
        int status = outputImage.writeBMP(outputPath); 
        return status;
    } // saveOutputImage
    

    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public void setZBuffer(boolean indicator) {
        zBufferEnabled = indicator;
    } // setZBuffer

    
    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public void setLighting(boolean indicator) {
        lightingEnabled = indicator;
    } // setLighting


    // This method came from GPIPE.CPP
    public void addCube(float width, float height, float depth) { 
        Point3d p1 = new Point3d(); 
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d(); 
        Point3d p4 = new Point3d();

        p1.x = -width/2.0f * VP;
        p1.y = -height/2.0f * VP;
        p1.z = depth/2.0f * VP;

        p2.x = width/2.0f * VP;
        p2.y = -height/2.0f * VP;
        p2.z = depth/2.0f * VP;

        p3.x = width/2.0f * VP;
        p3.y = height/2.0f * VP;
        p3.z = depth/2.0f * VP;

        p4.x = -width/2.0f * VP;
        p4.y = height/2.0f * VP;
        p4.z = depth/2.0f * VP;
        addFace(p1, p2, p3, p4); //front

        p1.x = -width/2.0f * VP;
        p1.y = -height/2.0f * VP;
        p1.z = -depth/2.0f * VP;

        p2.x = width/2.0f * VP;
        p2.y = -height/2.0f * VP;
        p2.z = -depth/2.0f * VP;

        p3.x = width/2.0f * VP;
        p3.y = height/2.0f * VP;
        p3.z = -depth/2.0f * VP;

        p4.x = -width/2.0f * VP;
        p4.y = height/2.0f * VP;
        p4.z = -depth/2.0f * VP;
        addFace(p4, p3, p2, p1); //back

        p1.x = -width/2.0f * VP;
        p1.y = -height/2.0f * VP;
        p1.z = -depth/2.0f * VP;

        p2.x = -width/2.0f * VP;
        p2.y = -height/2.0f * VP;
        p2.z = depth/2.0f * VP;

        p3.x = -width/2.0f * VP;
        p3.y = height/2.0f * VP;
        p3.z = depth/2.0f * VP;

        p4.x = -width/2.0f * VP;
        p4.y = height/2.0f * VP;
        p4.z = -depth/2.0f * VP;
        addFace(p1, p2, p3, p4); //left

        p1.x = width/2.0f * VP;
        p1.y = -height/2.0f * VP;
        p1.z = -depth/2.0f * VP;

        p2.x = width/2.0f * VP;
        p2.y = -height/2.0f * VP;
        p2.z = depth/2.0f * VP;

        p3.x = width/2.0f * VP;
        p3.y = height/2.0f * VP;
        p3.z = depth/2.0f * VP;

        p4.x = width/2.0f * VP;
        p4.y = height/2.0f * VP;
        p4.z = -depth/2.0f * VP;
        addFace(p4, p3, p2, p1); //right

        p1.x = -width/2.0f * VP;
        p1.y = height/2.0f * VP;
        p1.z = depth/2.0f * VP;

        p2.x = width/2.0f * VP;
        p2.y = height/2.0f * VP;
        p2.z = depth/2.0f * VP;

        p3.x = width/2.0f * VP;
        p3.y = height/2.0f * VP;
        p3.z = -depth/2.0f * VP;

        p4.x = -width/2.0f * VP;
        p4.y = height/2.0f * VP;
        p4.z = -depth/2.0f * VP;
        addFace(p4, p3, p2, p1); //top

        p1.x = -width/2.0f * VP;
        p1.y = -height/2.0f * VP;
        p1.z = depth/2.0f * VP;

        p2.x = width/2.0f * VP;
        p2.y = -height/2.0f * VP;
        p2.z = depth/2.0f * VP;

        p3.x = width/2.0f * VP;
        p3.y = -height/2.0f * VP;
        p3.z = -depth/2.0f * VP;

        p4.x = -width/2.0f * VP;
        p4.y = -height/2.0f * VP;
        p4.z = -depth/2.0f * VP;
        addFace(p1, p2, p3, p4); //bottom
    } // addCube

    
    // This method came from GPIPE.CPP
    public void addSphere(float radius) { 
        float tempAngle, rowRadius, yValue;
        Point3d[] prevRow =  new Point3d[NUMFACETS];
        Point3d p1 = new Point3d(); 
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d(); 
        Point3d p4 = new Point3d();
        float asAngle = 90.0f;
        float angleInc = 360.0f / NUMFACETS;
        float yIncrement = 2.0f * radius * VP / NUMFACETS;
        int col, row, k;

        for (row = 1; row <= NUMFACETS; row++) {
            rowRadius = (float)Math.abs(radius * VP * Math.cos(asAngle * F_DTR));
            yValue = radius * VP * (float)Math.sin(asAngle * F_DTR);

            if(row == 1) {
                tempAngle = 0.0f;
                for(k = 0; k < NUMFACETS; k++) {
                    prevRow[k].x = rowRadius * (float)Math.cos(tempAngle * F_DTR);
                    prevRow[k].y = yValue;
                    prevRow[k].z = rowRadius * (float)Math.sin(tempAngle * F_DTR);
                    tempAngle += angleInc;
                }
            }
            tempAngle = 0.0f;

            for (col = 0; col < NUMFACETS-1 ; col++) {
                p4.x = prevRow[col].x;
                p4.y = prevRow[col].y;
                p4.z = prevRow[col].z;

                p1.x =  rowRadius * (float)Math.cos(tempAngle * F_DTR);
                p1.z =  rowRadius * (float)Math.sin(tempAngle * F_DTR);
                p1.y =  yValue;
                tempAngle += angleInc;

                if(row > 1) { 
                    p3.x = prevRow[col + 1].x;
                    p3.y = prevRow[col + 1].y;
                    p3.z = prevRow[col + 1].z;

                    p2.x =  rowRadius * (float)Math.cos(tempAngle * F_DTR);
                    p2.z =  rowRadius * (float)Math.sin(tempAngle * F_DTR);
                    p2.y =  yValue;

                    addFace(p4, p3, p2, p1);
                    prevRow[col].x = p1.x;
                    prevRow[col].y = p1.y;
                    prevRow[col].z = p1.z;
                }
            }

            asAngle += (angleInc / 2.0f);
        }
    } // addSphere


    // This method came from GPIPE.CPP
    public void addCylTop(float height, float radius) { 
        float tempAngle = 0.0f;
        Point3d p1 = new Point3d(); 
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d();
        p1.x = 0.0f;
        p1.y = VP * height/2.0f;
        p1.z = 0.0f;

        float angleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.x = VP * radius * (float)Math.cos(tempAngle * F_DTR);
            p2.z = VP * radius * (float)Math.sin(tempAngle * F_DTR);
            p2.y = VP * height/2.0f;

            tempAngle += angleInc;
            p3.x = VP * radius * (float)Math.cos(tempAngle * F_DTR);
            p3.z = VP * radius * (float)Math.sin(tempAngle * F_DTR);
            p3.y = VP * height/2.0f;
            addFace(p3, p2, p1, null); 
        }
    } // addCylTop

    
    // This method came from GPIPE.CPP
    public void addCylBottom(float height, float radius) { 
        float tempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d();
        p1.x = 0.0f;
        p1.y = -(VP * height/2.0f);
        p1.z = 0.0f;

        float angleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.x = VP * radius * (float)Math.cos(tempAngle * F_DTR);
            p2.z = VP * radius * (float)Math.sin(tempAngle * F_DTR);
            p2.y = -VP * height/2.0f;

            tempAngle += angleInc;
            p3.x = VP * radius * (float)Math.cos(tempAngle * F_DTR);
            p3.z = VP * radius * (float)Math.sin(tempAngle * F_DTR);
            p3.y = -VP * height/2.0f;
            addFace(p3, p2, p1, null); 
        }
    } // addCylBottom

    
    // This method came from GPIPE.CPP
    public void addCylSides(float height, float radius) { 
        float tempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();
        Point3d p4 = new Point3d();

        float angleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p1.x = VP * radius * (float)Math.cos(tempAngle * F_DTR);
            p1.z = VP * radius * (float)Math.sin(tempAngle * F_DTR);
            p1.y = VP * height/2.0f;

            p2.x = p1.x;
            p2.z = p1.z;
            p2.y = -p1.y;

            tempAngle += angleInc;
            p3.x = VP * radius * (float)Math.cos(tempAngle * F_DTR);
            p3.z = VP * radius * (float)Math.sin(tempAngle * F_DTR);
            p3.y = -(VP * height/2.0f);

            p4.x = p3.x;
            p4.z = p3.z;
            p4.y = -p3.y;
            addFace(p4, p3, p2, p1); 
        }
    } // addCylSides

    
    // This method came from GPIPE.CPP
    public void addConeBottom(float height, float bottomRadius) { 
        float tempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();

        p1.x = 0.0f;
        p1.y = -(VP * height/2.0f);
        p1.z = 0.0f;

        float angleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.x = VP * bottomRadius * (float)Math.cos(tempAngle * F_DTR);
            p2.z = VP * bottomRadius * (float)Math.sin(tempAngle * F_DTR);
            p2.y = -VP * height/2.0f;

            tempAngle += angleInc;
            p3.x = VP * bottomRadius * (float)Math.cos(tempAngle * F_DTR);
            p3.z = VP * bottomRadius * (float)Math.sin(tempAngle * F_DTR);
            p3.y = -VP * height/2.0f;
            addFace(p3, p2, p1, null); 
        }
    } // addConeBottom


    // This method came from GPIPE.CPP
    public void addConeSides(float height, float bottomRadius) { 
        float tempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();

        p1.x = 0.0f;
        p1.y = VP * height/2.0f;
        p1.z = 0.0f;
        int i;
        String pathBuffer;
        float angleInc = 360.0f / NUMFACETS;
        for (i = 1; i <= NUMFACETS; i++) {
            p2.x =  VP * bottomRadius * (float)Math.cos(tempAngle * F_DTR);
            p2.z =  VP * bottomRadius * (float)Math.sin(tempAngle * F_DTR);
            p2.y = -VP * height/2.0f;

            tempAngle += angleInc;
            p3.x =  VP * bottomRadius * (float)Math.cos(tempAngle * F_DTR);
            p3.z =  VP * bottomRadius * (float)Math.sin(tempAngle * F_DTR);
            p3.y = -VP * height/2.0f;
            addFace(p3, p2, p1, null);
        }
    } // addConeSides


    // This method came from GPIPE.CPP
    public void updateBoundingBox(Point3d point) {
        if(!boundingBoxInitialized) {
            boundingBoxInitialized = true;
            minBoundingBox.x = point.x;
            minBoundingBox.y = point.y;
            minBoundingBox.z = point.z;

            maxBoundingBox.x = point.x;
            maxBoundingBox.y = point.y;
            maxBoundingBox.z = point.z;
        } else {
            if(point.x < minBoundingBox.x) minBoundingBox.x = point.x;
            if(point.y < minBoundingBox.y) minBoundingBox.y = point.y;
            if(point.z < minBoundingBox.z) minBoundingBox.z = point.z;

            if(point.x > maxBoundingBox.x) maxBoundingBox.x = point.x;
            if(point.y > maxBoundingBox.y) maxBoundingBox.y = point.y;
            if(point.z > maxBoundingBox.z) maxBoundingBox.z = point.z;
        }
    } // updateBoundingBox


    // This method came from GPIPE.CPP
    public boolean viewPointInsideBoundingBox() {
        if(
        (viewPoint.x >= minBoundingBox.x && viewPoint.x <= maxBoundingBox.x) &&
        (viewPoint.y >= minBoundingBox.y && viewPoint.y <= maxBoundingBox.y) &&
        (viewPoint.z >= minBoundingBox.z && viewPoint.z <= maxBoundingBox.z) ) {
            return true;
        } else {
            return false;
        }
    } // viewPointInsideBoundingBox


    // This method came from GPIPE.CPP
    // Called from:
    //     MainFrame.onToolsTest
    public void setLightSource(Point3d aPoint) {
        this.lightSource.x = aPoint.x;
        this.lightSource.y = aPoint.y;
        this.lightSource.z = aPoint.z;
    } // setLightSource
} // class GPipe