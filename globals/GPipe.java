package globals;

import core.MemImage;

import java.text.DecimalFormat;

import math.MathUtils;
import math.TMatrix;
import math.Vect;

import structs.Point2d;
import structs.Point3d;

public class GPipe {
    boolean gpdebug = false;
    String msSceneName;
    String msScenePathName;
    int miEffectType, miMode, miColorMode;
    int miOutputRows, miOutputColumns;
    boolean mbLightingEnabled;
    boolean mbZBufferEnabled;
    int miBackfaceCullingEnabled;
    MemImage mZBuffImage;
    MemImage mOutputImage;
    TMatrix mViewMatrix;        // Contains a viewpoint transformation
    TMatrix mPenMatrix;         // Contains model transformation
    TMatrix mViewPenMatrix;     // Contains the composite view, pen transformation
    Point3d mLightSource;
    Point3d mViewPoint;
    Point3d mViewAngle;
    Point3d mPenRotation;        // rotation angles in radians
    Point3d mPenScale;
    Point3d mPenTranslation;
    Point3d mRef;
    Point3d mMinBoundingBox;     // bounding box minimums
    Point3d mMaxBoundingBox;     // bounding box maximums
    boolean mbBoundingBoxInitialized; // FALSE if not initialized, else TRUE

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


    // This constructor originally came from GPIPE.CPP
    public GPipe() {
        miOutputRows = 512;
        miOutputColumns = 512;
        mbZBufferEnabled = true;
        mbLightingEnabled = true;
        miEffectType = STILL;

        msScenePathName = "d:\\ict20\\output\\gPipe.bmp";

        // This vector indicates the direction of the light source
        mLightSource = new Point3d();
        mLightSource.fX = -150.0f;
        mLightSource.fY =    0.0f;
        mLightSource.fZ = -200.0f;
        
        mViewPoint = new Point3d();
        mViewPoint.fX =   0.0f;
        mViewPoint.fY =   0.0f;
        mViewPoint.fZ = 200.0f;

        mViewAngle = new Point3d();
        mViewAngle.fX = -20.0f * F_DTR;
        mViewAngle.fY =   0.0f * F_DTR;
        mViewAngle.fZ =   0.0f;

        mViewMatrix = new TMatrix();
        setViewMatrix();

        mRef = new Point3d();
        mRef.fX = 0.0f;   //  default location of the virtual pen
        mRef.fY = 0.0f;
        mRef.fZ = 0.0f;

        mPenRotation = new Point3d();
        mPenRotation.fX = 0.0f;
        mPenRotation.fY = 0.0f;
        mPenRotation.fZ = 0.0f;
        
        mPenTranslation = new Point3d();
        mPenTranslation.fX = 0.0f;
        mPenTranslation.fY = 0.0f;
        mPenTranslation.fZ = 0.0f;
        
        mPenScale = new Point3d();
        mPenScale.fX = 1.0f;
        mPenScale.fY = 1.0f;
        mPenScale.fZ = 1.0f;

        mPenMatrix = new TMatrix();
        setPenMatrix();
        
        mbBoundingBoxInitialized = false;
        
        setZBuffer(true);
        setLighting(true);

        Point3d aLight = new Point3d();
        aLight.fX =    0.0f;
        aLight.fY =    0.0f;
        aLight.fZ = -100.0f;
        setLightSource(aLight);
    } // GPipe constructor


    // This destructor originally came from GPIPE.CPP
    public void finalize() {

    } // finalize

    
    // This method originally came from GPIPE.CPP
    public void reset() {
        mZBuffImage.init32(ZBUFFERMAXVALUE); 
        mOutputImage.clear();
        mPenRotation.fX = 0.0f;
        mPenRotation.fY = 0.0f;
        mPenRotation.fZ = 0.0f;
            
        mPenTranslation.fX = 0.0f;
        mPenTranslation.fY = 0.0f;
        mPenTranslation.fZ = 0.0f;
            
        mPenScale.fX = 1.0f;
        mPenScale.fY = 1.0f;
        mPenScale.fZ = 1.0f;
        setPenMatrix();
    } // reset


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public int initialize() {
        // Open the output image
        mOutputImage = new MemImage(miOutputRows, miOutputColumns);
        if (!mOutputImage.isValid()) {
            Globals.statusPrint("gPipe: Not enough memory to open output image");
            return -1;
        }

        // Open the zBuffer if necessary
        if(mbZBufferEnabled) {
            mZBuffImage = new MemImage(miOutputRows, miOutputColumns, 32);
            if (!mZBuffImage.isValid()) {
                Globals.statusPrint("gPipe: Not enough memory to open Z Buffer");
                return -1;
            }

            // Fill the zBuffer with maximum values
            mZBuffImage.init32(ZBUFFERMAXVALUE); 
        }

        return 0;
    } // initialize


    // This method originally came from GPIPE.CPP
    // 
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

        mViewPenMatrix.transformAndProjectPoint1(p1, sp1, mRef, miOutputRows, miOutputColumns, t1);
        if(mbZBufferEnabled) {
            dPrev1 = MathUtils.getDistance3d(t1.fX, t1.fY, t1.fZ, mViewPoint.fX, mViewPoint.fY, mViewPoint.fZ);
        }
        updateBoundingBox(p1);  // optional

        mViewPenMatrix.transformAndProjectPoint1(p2, sp2, mRef, miOutputRows, miOutputColumns, t2);
        if(mbZBufferEnabled) {
            dPrev2 = MathUtils.getDistance3d(t2.fX, t2.fY, t2.fZ, mViewPoint.fX, mViewPoint.fY, mViewPoint.fZ);
        }
        updateBoundingBox(p2);  // optional

        mViewPenMatrix.transformAndProjectPoint1(c1, sc1, mRef, miOutputRows, miOutputColumns, t3);
        if(mbZBufferEnabled) {
            dCur1 = MathUtils.getDistance3d(t3.fX, t3.fY, t3.fZ, mViewPoint.fX, mViewPoint.fY, mViewPoint.fZ);
        }
        updateBoundingBox(c1);  // optional

        dCur2 = -1.0f;

        if(c2 != null) {
            mViewPenMatrix.transformAndProjectPoint1(c2, sc2, mRef, miOutputRows, miOutputColumns, t4);
            if(mbZBufferEnabled) {
                dCur2 = MathUtils.getDistance3d(t4.fX, t4.fY, t4.fZ, mViewPoint.fX, mViewPoint.fY, mViewPoint.fZ);
            }
            updateBoundingBox(c2);  // optional
        }

        if (gpdebug) {
            mViewPoint.display("ViewPoint");
            mRef.display("Reference");
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
        if(mbLightingEnabled) {
            float xMax = t1.fX;
            float yMax = t1.fY;
            float zMax = t1.fZ;

            float xMin = t1.fX;
            float yMin = t1.fY;
            float zMin = t1.fZ;

            // Get the 3D bounding box
            if(t1.fX > xMax) xMax = t1.fX;
            if(t2.fX > xMax) xMax = t2.fX;
            if(t3.fX > xMax) xMax = t3.fX;

            if(t1.fX < xMin) xMin = t1.fX;
            if(t2.fX < xMin) xMin = t2.fX;
            if(t3.fX < xMin) xMin = t3.fX;

            if(t1.fY > yMax) yMax = t1.fY;
            if(t2.fY > yMax) yMax = t2.fY;
            if(t3.fY > yMax) yMax = t3.fY;

            if(t1.fY < yMin) yMin = t1.fY;
            if(t2.fY < yMin) yMin = t2.fY;
            if(t3.fY < yMin) yMin = t3.fY;

            if(t1.fZ > zMax) zMax = t1.fZ;
            if(t2.fZ > zMax) zMax = t2.fZ;
            if(t3.fZ > zMax) zMax = t3.fZ;

            if(t1.fZ < zMin) zMin = t1.fZ;
            if(t2.fZ < zMin) zMin = t2.fZ;
            if(t3.fZ < zMin) zMin = t3.fZ;

            if(c2 != null) {
                if(t4.fZ < zMin) zMin = t4.fZ;
                if(t4.fZ > zMax) zMax = t4.fZ;

                if(t4.fX > xMax) xMax = t4.fX;
                if(t4.fX < xMin) xMin = t4.fX;

                if(t4.fY > yMax) yMax = t4.fY;
                if(t4.fY < yMin) yMin = t4.fY;
            }

            centroid.fX = (xMax + xMin) / 2.0f;
            centroid.fY = (yMax + yMin) / 2.0f;
            centroid.fZ = (zMax + zMin) / 2.0f;
            float dCentroid = MathUtils.getDistance3d(
                mLightSource.fX, mLightSource.fY, mLightSource.fZ, 
                centroid.fX, centroid.fY, centroid.fZ);
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

            ip1 = Globals.lightModel(kd, Ip, 150, np1, mLightSource, dCentroid);
            ip1 = MathUtils.bound(ip1, 1.0f, 255.0f);
        } else {
            ip1 = 175.0f;	   // set a nominal face intensity
        }

        //  Render the face
        if (c2 != null) {
            if(mbZBufferEnabled) {
                mOutputImage.fillPolyz( 
                            (int)sp1.x, (int)sp1.y, ip1, dPrev1,
                            (int)sp2.x, (int)sp2.y, ip1, dPrev2,
                            (int)sc1.x, (int)sc1.y, ip1, dCur1,
                            (int)sc2.x, (int)sc2.y, ip1, dCur2, 
                            mZBuffImage);
            } else {
                mOutputImage.fillPolyz( 
                            (int)sp1.x, (int)sp1.y, (byte)ip1, 0.0f,
                            (int)sp2.x, (int)sp2.y, (byte)ip1, 0.0f,
                            (int)sc1.x, (int)sc1.y, (byte)ip1, 0.0f,
                            (int)sc2.x, (int)sc2.y, (byte)ip1, 0.0f, 
                            null);
            }
        } else {
            if(mbZBufferEnabled) {
                Globals.fillTrianglez( 
                            (int)sp1.x, (int)sp1.y, ip1, dPrev1,
                            (int)sp2.x, (int)sp2.y, ip1, dPrev2,
                            (int)sc1.x, (int)sc1.y, ip1, dCur1,
                            mOutputImage, mZBuffImage);
            } else {
                Globals.fillTrianglez( 
                            (int)sp1.x, (int)sp1.y, (byte)ip1, 0.0f,
                            (int)sp2.x, (int)sp2.y, (byte)ip1, 0.0f,
                            (int)sc1.x, (int)sc1.y, (byte)ip1, 0.0f,
                            mOutputImage, null);
            }
        }

        return 0;
    } // addFace


    // This method originally came from GPIPE.CPP
    public void setPenScale(float pfScaleX, float pfScaleY, float pfScaleZ) {
        mPenScale.fX = pfScaleX;
        mPenScale.fY = pfScaleY;
        mPenScale.fZ = pfScaleZ;
    } // setPenScale
    

    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public void setPenXRotation(float pfAngleRad) {
        mPenRotation.fX += pfAngleRad;
    } // setPenXRotation


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public void setPenYRotation(float pfAngleRad) {
        mPenRotation.fY += pfAngleRad;
    } // setPenYRotation


    // This method originally came from GPIPE.CPP
    public void setPenZRotation(float pfAngleRad) {
        mPenRotation.fZ += pfAngleRad;
    } // setPenZRotation


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public void setPenTranslation(float pfTranX, float pfTranY, float pfTranZ) {
        mPenTranslation.fX += pfTranX;
        mPenTranslation.fY += pfTranY;
        mPenTranslation.fZ += pfTranZ;
    } // setPenTranslation


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     constructor
    public void setPenMatrix() {
        mPenMatrix.setIdentity();
        mPenMatrix.scale(mPenScale.fX, mPenScale.fY, mPenScale.fZ);
        mPenMatrix.rotate(mPenRotation.fX, mPenRotation.fY, mPenRotation.fZ);
        mPenMatrix.translate(mPenTranslation.fX, mPenTranslation.fY, mPenTranslation.fZ);
    } // setPenMatrix


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     constructor
    public void setViewMatrix() {
        mViewMatrix.setIdentity();
        mViewMatrix.scale(1.0f, 1.0f, 1.0f);
        mViewMatrix.rotate(-mViewAngle.fX, -mViewAngle.fY, -mViewAngle.fZ);
        mViewMatrix.translate(-mViewPoint.fX, -mViewPoint.fY, -mViewPoint.fZ);
    } // setViewMatrix


    // This method originally came from GPIPE.CPP
    public void setViewPenMatrix() {
        setPenMatrix();
        setViewMatrix();
        mViewPenMatrix.multiply(mViewMatrix, mPenMatrix);
    } // setViewPenMatrix


    // This method originally came from GPIPE.CPP
    public void resetPenMatrix() {
        mPenMatrix.setIdentity();
    } // resetPenMatrix


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public int saveZBuffer(String psOutputPath) { // parameter is not used
        int iStatus = mZBuffImage.saveAs8("d:\\ict20\\output\\gPipeZBuffer8.bmp");
        return iStatus;
    } // saveZBuffer


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public int saveOutputImage(String psOutputPath) {
        int iStatus = mOutputImage.writeBMP(psOutputPath); 
        return iStatus;
    } // saveOutputImage


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public void setZBuffer(boolean pbIndicator) {
        mbZBufferEnabled = pbIndicator;
    } // setZBuffer


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public void setLighting(boolean pbIndicator) {
        mbLightingEnabled = pbIndicator;
    } // setLighting


    // This method originally came from GPIPE.CPP
    public void addCube(float pfWidth, float pfHeight, float pfDepth) { 
        Point3d p1 = new Point3d(); 
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d(); 
        Point3d p4 = new Point3d();

        p1.fX = -pfWidth/2.0f * VP;
        p1.fY = -pfHeight/2.0f * VP;
        p1.fZ =  pfDepth/2.0f * VP;

        p2.fX =  pfWidth/2.0f * VP;
        p2.fY = -pfHeight/2.0f * VP;
        p2.fZ =  pfDepth/2.0f * VP;

        p3.fX = pfWidth/2.0f * VP;
        p3.fY = pfHeight/2.0f * VP;
        p3.fZ = pfDepth/2.0f * VP;

        p4.fX = -pfWidth/2.0f * VP;
        p4.fY =  pfHeight/2.0f * VP;
        p4.fZ =  pfDepth/2.0f * VP;
        addFace(p1, p2, p3, p4); //front

        p1.fX = -pfWidth/2.0f * VP;
        p1.fY = -pfHeight/2.0f * VP;
        p1.fZ = -pfDepth/2.0f * VP;

        p2.fX =  pfWidth/2.0f * VP;
        p2.fY = -pfHeight/2.0f * VP;
        p2.fZ = -pfDepth/2.0f * VP;

        p3.fX =  pfWidth/2.0f * VP;
        p3.fY =  pfHeight/2.0f * VP;
        p3.fZ = -pfDepth/2.0f * VP;

        p4.fX = -pfWidth/2.0f * VP;
        p4.fY =  pfHeight/2.0f * VP;
        p4.fZ = -pfDepth/2.0f * VP;
        addFace(p4, p3, p2, p1); //back

        p1.fX = -pfWidth/2.0f * VP;
        p1.fY = -pfHeight/2.0f * VP;
        p1.fZ = -pfDepth/2.0f * VP;

        p2.fX = -pfWidth/2.0f * VP;
        p2.fY = -pfHeight/2.0f * VP;
        p2.fZ =  pfDepth/2.0f * VP;

        p3.fX = -pfWidth/2.0f * VP;
        p3.fY =  pfHeight/2.0f * VP;
        p3.fZ =  pfDepth/2.0f * VP;

        p4.fX = -pfWidth/2.0f * VP;
        p4.fY =  pfHeight/2.0f * VP;
        p4.fZ = -pfDepth/2.0f * VP;
        addFace(p1, p2, p3, p4); //left

        p1.fX =  pfWidth/2.0f * VP;
        p1.fY = -pfHeight/2.0f * VP;
        p1.fZ = -pfDepth/2.0f * VP;

        p2.fX =  pfWidth/2.0f * VP;
        p2.fY = -pfHeight/2.0f * VP;
        p2.fZ =  pfDepth/2.0f * VP;

        p3.fX = pfWidth/2.0f * VP;
        p3.fY = pfHeight/2.0f * VP;
        p3.fZ = pfDepth/2.0f * VP;

        p4.fX =  pfWidth/2.0f * VP;
        p4.fY =  pfHeight/2.0f * VP;
        p4.fZ = -pfDepth/2.0f * VP;
        addFace(p4, p3, p2, p1); //right

        p1.fX = -pfWidth/2.0f * VP;
        p1.fY =  pfHeight/2.0f * VP;
        p1.fZ =  pfDepth/2.0f * VP;

        p2.fX = pfWidth/2.0f * VP;
        p2.fY = pfHeight/2.0f * VP;
        p2.fZ = pfDepth/2.0f * VP;

        p3.fX =  pfWidth/2.0f * VP;
        p3.fY =  pfHeight/2.0f * VP;
        p3.fZ = -pfDepth/2.0f * VP;

        p4.fX = -pfWidth/2.0f * VP;
        p4.fY =  pfHeight/2.0f * VP;
        p4.fZ = -pfDepth/2.0f * VP;
        addFace(p4, p3, p2, p1); //top

        p1.fX = -pfWidth/2.0f * VP;
        p1.fY = -pfHeight/2.0f * VP;
        p1.fZ =  pfDepth/2.0f * VP;

        p2.fX =  pfWidth/2.0f * VP;
        p2.fY = -pfHeight/2.0f * VP;
        p2.fZ =  pfDepth/2.0f * VP;

        p3.fX =  pfWidth/2.0f * VP;
        p3.fY = -pfHeight/2.0f * VP;
        p3.fZ = -pfDepth/2.0f * VP;

        p4.fX = -pfWidth/2.0f * VP;
        p4.fY = -pfHeight/2.0f * VP;
        p4.fZ = -pfDepth/2.0f * VP;
        addFace(p1, p2, p3, p4); //bottom
    } // addCube


    // This method originally came from GPIPE.CPP
    public void addSphere(float pfRadius) { 
        float fTempAngle, fRowRadius, fYValue;
        Point3d[] prevRow =  new Point3d[NUMFACETS];
        Point3d p1 = new Point3d(); 
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d(); 
        Point3d p4 = new Point3d();
        float fAngle = 90.0f;
        float fAngleInc = 360.0f / NUMFACETS;
        // float yIncrement = 2.0f * radius * VP / NUMFACETS; // this variable is not used
        int iCol, iRow, k;

        for (iRow = 1; iRow <= NUMFACETS; iRow++) {
            fRowRadius = (float)Math.abs(pfRadius * VP * Math.cos(fAngle * F_DTR));
            fYValue = pfRadius * VP * (float)Math.sin(fAngle * F_DTR);

            if(iRow == 1) {
                fTempAngle = 0.0f;
                for(k = 0; k < NUMFACETS; k++) {
                    prevRow[k].fX = fRowRadius * (float)Math.cos(fTempAngle * F_DTR);
                    prevRow[k].fY = fYValue;
                    prevRow[k].fZ = fRowRadius * (float)Math.sin(fTempAngle * F_DTR);
                    fTempAngle += fAngleInc;
                }
            }
            fTempAngle = 0.0f;

            for (iCol = 0; iCol < NUMFACETS-1 ; iCol++) {
                p4.fX = prevRow[iCol].fX;
                p4.fY = prevRow[iCol].fY;
                p4.fZ = prevRow[iCol].fZ;

                p1.fX =  fRowRadius * (float)Math.cos(fTempAngle * F_DTR);
                p1.fZ =  fRowRadius * (float)Math.sin(fTempAngle * F_DTR);
                p1.fY =  fYValue;
                fTempAngle += fAngleInc;

                if(iRow > 1) { 
                    p3.fX = prevRow[iCol + 1].fX;
                    p3.fY = prevRow[iCol + 1].fY;
                    p3.fZ = prevRow[iCol + 1].fZ;

                    p2.fX =  fRowRadius * (float)Math.cos(fTempAngle * F_DTR);
                    p2.fZ =  fRowRadius * (float)Math.sin(fTempAngle * F_DTR);
                    p2.fY =  fYValue;

                    addFace(p4, p3, p2, p1);
                    prevRow[iCol].fX = p1.fX;
                    prevRow[iCol].fY = p1.fY;
                    prevRow[iCol].fZ = p1.fZ;
                }
            } // for iCol

            fAngle += (fAngleInc / 2.0f);
        } // for iRow
    } // addSphere


    // This method originally came from GPIPE.CPP
    public void addCylTop(float pfHeight, float pfRadius) { 
        float fTempAngle = 0.0f;
        Point3d p1 = new Point3d(); 
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d();
        p1.fX = 0.0f;
        p1.fY = VP * pfHeight/2.0f;
        p1.fZ = 0.0f;

        float fAngleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.fX = VP * pfRadius * (float)Math.cos(fTempAngle * F_DTR);
            p2.fZ = VP * pfRadius * (float)Math.sin(fTempAngle * F_DTR);
            p2.fY = VP * pfHeight/2.0f;

            fTempAngle += fAngleInc;
            p3.fX = VP * pfRadius * (float)Math.cos(fTempAngle * F_DTR);
            p3.fZ = VP * pfRadius * (float)Math.sin(fTempAngle * F_DTR);
            p3.fY = VP * pfHeight/2.0f;
            addFace(p3, p2, p1, null); 
        } // for i
    } // addCylTop


    // This method originally came from GPIPE.CPP
    public void addCylBottom(float pfHeight, float pfRadius) { 
        float fTempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d(); 
        Point3d p3 = new Point3d();
        p1.fX = 0.0f;
        p1.fY = -(VP * pfHeight/2.0f);
        p1.fZ = 0.0f;

        float fAngleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.fX = VP * pfRadius * (float)Math.cos(fTempAngle * F_DTR);
            p2.fZ = VP * pfRadius * (float)Math.sin(fTempAngle * F_DTR);
            p2.fY = -VP * pfHeight/2.0f;

            fTempAngle += fAngleInc;
            p3.fX = VP * pfRadius * (float)Math.cos(fTempAngle * F_DTR);
            p3.fZ = VP * pfRadius * (float)Math.sin(fTempAngle * F_DTR);
            p3.fY = -VP * pfHeight/2.0f;
            addFace(p3, p2, p1, null); 
        } // for i
    } // addCylBottom


    // This method originally came from GPIPE.CPP
    public void addCylSides(float pfHeight, float pfRadius) { 
        float fTempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();
        Point3d p4 = new Point3d();

        float fAngleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p1.fX = VP * pfRadius * (float)Math.cos(fTempAngle * F_DTR);
            p1.fZ = VP * pfRadius * (float)Math.sin(fTempAngle * F_DTR);
            p1.fY = VP * pfHeight/2.0f;

            p2.fX = p1.fX;
            p2.fZ = p1.fZ;
            p2.fY = -p1.fY;

            fTempAngle += fAngleInc;
            p3.fX = VP * pfRadius * (float)Math.cos(fTempAngle * F_DTR);
            p3.fZ = VP * pfRadius * (float)Math.sin(fTempAngle * F_DTR);
            p3.fY = -(VP * pfHeight/2.0f);

            p4.fX = p3.fX;
            p4.fZ = p3.fZ;
            p4.fY = -p3.fY;
            addFace(p4, p3, p2, p1); 
        } // for i
    } // addCylSides


    // This method originally came from GPIPE.CPP
    public void addConeBottom(float pfHeight, float pfBottomRadius) { 
        float fTempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();

        p1.fX = 0.0f;
        p1.fY = -(VP * pfHeight/2.0f);
        p1.fZ = 0.0f;

        float fAngleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.fX = VP * pfBottomRadius * (float)Math.cos(fTempAngle * F_DTR);
            p2.fZ = VP * pfBottomRadius * (float)Math.sin(fTempAngle * F_DTR);
            p2.fY = -VP * pfHeight/2.0f;

            fTempAngle += fAngleInc;
            p3.fX = VP * pfBottomRadius * (float)Math.cos(fTempAngle * F_DTR);
            p3.fZ = VP * pfBottomRadius * (float)Math.sin(fTempAngle * F_DTR);
            p3.fY = -VP * pfHeight/2.0f;
            addFace(p3, p2, p1, null); 
        } // for i
    } // addConeBottom


    // This method originally came from GPIPE.CPP
    public void addConeSides(float pfHeight, float pfBottomRadius) { 
        float fTempAngle = 0.0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();

        p1.fX = 0.0f;
        p1.fY = VP * pfHeight/2.0f;
        p1.fZ = 0.0f;

        // String pathBuffer; // This variable is not used
        float fAngleInc = 360.0f / NUMFACETS;
        for (int i = 1; i <= NUMFACETS; i++) {
            p2.fX =  VP * pfBottomRadius * (float)Math.cos(fTempAngle * F_DTR);
            p2.fZ =  VP * pfBottomRadius * (float)Math.sin(fTempAngle * F_DTR);
            p2.fY = -VP * pfHeight/2.0f;

            fTempAngle += fAngleInc;
            p3.fX =  VP * pfBottomRadius * (float)Math.cos(fTempAngle * F_DTR);
            p3.fZ =  VP * pfBottomRadius * (float)Math.sin(fTempAngle * F_DTR);
            p3.fY = -VP * pfHeight/2.0f;
            addFace(p3, p2, p1, null);
        } // for i
    } // addConeSides


    // This method originally came from GPIPE.CPP
    public void updateBoundingBox(Point3d pPoint) {
        if(!mbBoundingBoxInitialized) {
            mbBoundingBoxInitialized = true;
            
            mMinBoundingBox.fX = pPoint.fX;
            mMinBoundingBox.fY = pPoint.fY;
            mMinBoundingBox.fZ = pPoint.fZ;

            mMaxBoundingBox.fX = pPoint.fX;
            mMaxBoundingBox.fY = pPoint.fY;
            mMaxBoundingBox.fZ = pPoint.fZ;
        } else {
            if(pPoint.fX < mMinBoundingBox.fX) mMinBoundingBox.fX = pPoint.fX;
            if(pPoint.fY < mMinBoundingBox.fY) mMinBoundingBox.fY = pPoint.fY;
            if(pPoint.fZ < mMinBoundingBox.fZ) mMinBoundingBox.fZ = pPoint.fZ;

            if(pPoint.fX > mMaxBoundingBox.fX) mMaxBoundingBox.fX = pPoint.fX;
            if(pPoint.fY > mMaxBoundingBox.fY) mMaxBoundingBox.fY = pPoint.fY;
            if(pPoint.fZ > mMaxBoundingBox.fZ) mMaxBoundingBox.fZ = pPoint.fZ;
        }
    } // updateBoundingBox


    // This method originally came from GPIPE.CPP
    public boolean viewPointInsideBoundingBox() {
        if(
        (mViewPoint.fX >= mMinBoundingBox.fX && mViewPoint.fX <= mMaxBoundingBox.fX) &&
        (mViewPoint.fY >= mMinBoundingBox.fY && mViewPoint.fY <= mMaxBoundingBox.fY) &&
        (mViewPoint.fZ >= mMinBoundingBox.fZ && mViewPoint.fZ <= mMaxBoundingBox.fZ) ) {
            return true;
        } else {
            return false;
        }
    } // viewPointInsideBoundingBox


    // This method originally came from GPIPE.CPP
    // 
    // Called from:
    //     MainFrame.onToolsTest
    public void setLightSource(Point3d pPoint) {
        this.mLightSource.fX = pPoint.fX;
        this.mLightSource.fY = pPoint.fY;
        this.mLightSource.fZ = pPoint.fZ;
    } // setLightSource
} // class GPipe