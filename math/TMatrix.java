package math;

import core.Shape3d;

import globals.Globals;

import java.text.DecimalFormat;

import structs.Point2d;
import structs.Point3d;


public class TMatrix {
    boolean ictdebug = false;
    protected float[][] theMatrix = new float[4][4];    // Composite transformation matrix
    protected float[][] rxMat = new float[4][4];			 // X rotation matrix
    protected float[][] ryMat = new float[4][4];			 // Y rotation matrix
    protected float[][] rzMat = new float[4][4];			 // Z rotation matrix
    protected float[][] scMat = new float[4][4];			 // Scaling matrix
    protected float[][] trMat = new float[4][4];			 // Translation matrix

/*
public:
  void scale(float sx, float sy, float sz);
  void rotate(float rx,float ry,float rz);
  void translate(float tx, float ty, float tz);
  void multiply(tMatrix *matrix1, tMatrix *matrix2);
  void setIdentity();
  void transformPoint(float xIn, float yIn, float zIn, float *xOut,
    float *yOut, float *zOut);
  void transformPoint1(point3d *in, point3d *out);

void transformAndProject (shape3d *aShape, int outHeight, int outWidth,
	   int externalCentroid = 0,
	   float centroidX = 0, float centroidY = 0, float centroidZ = 0);

void transformAndProjectPoint(float x, float y, float z, int *sx, 
  int *sy, float refX, float refY, float refZ, 
  int outHeight, int outWidth, float *tx, float *ty, float *tz);

void tMatrix::transformAndProjectPoint1(point3d *p, point2d *s, point3d *ref, 
							  int outHeight, int outWidth, point3d *t);

  int invertg();
  void transpose();
  tMatrix();
  tMatrix(tMatrix *aMatrix);
  void copy(tMatrix *aMatrix);
  tMatrix(tMatrix *matrix1, tMatrix *matrix2);
  ~tMatrix();
  void display(char *);
*/

    // Called from:
    //     Gloals.iwarpz
    //     MainFrame.onToolsWarpImage
    //     RenderObject ctor that takes 4 Point3d parameters
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    public TMatrix() {
        if (ictdebug) {
            String msgText = String.format("TMatrix constructor 1.  Sizeof TMatrix: %d", sizeofLowerLimit());
            Globals.statusPrint(msgText);
        }
        setIdentity();
    } // TMatrix ctor
    
    
    // Called from:
    //     Globals.iwarpz
    public TMatrix(TMatrix aMatrix) {
        if(ictdebug) {
            String msgText = String.format("TMatrix constructor 2.  Sizeof TMatrix: %d", sizeofLowerLimit());
            Globals.statusPrint(msgText);
        }
        matcopy(theMatrix, aMatrix.theMatrix);
    } // TMatrix ctor
    
    
    // Called from:
    //     Globals.iwarpz
    //     SceneList.previewStill
    public void multiply(TMatrix matrix1, TMatrix matrix2) {
        setIdentity();

        // Set theMatrix = matrix1.theMatrix * matrix2.theMatrix
        matmult(theMatrix, matrix1.theMatrix, matrix2.theMatrix);
    } // multiply
    

    // Called from:
    //     RenderObject.transformAndProject
    public void copy(TMatrix matrix) {
        setIdentity();
        matcopy(theMatrix, matrix.theMatrix);
    } // copy
    

    // Called from:
    //     MainFrame.getViewMatrix
    //     SceneList.previewStill
    //     ScenePreviewDlg.onCmdPlus
    public void setIdentity() {
        theMatrix[0][0]= 1.0f; theMatrix[1][0]= 0.0f; theMatrix[2][0]= 0.0f; theMatrix[3][0]= 0.0f;
        theMatrix[0][1]= 0.0f; theMatrix[1][1]= 1.0f; theMatrix[2][1]= 0.0f; theMatrix[3][1]= 0.0f;
        theMatrix[0][2]= 0.0f; theMatrix[1][2]= 0.0f; theMatrix[2][2]= 1.0f; theMatrix[3][2]= 0.0f;
        theMatrix[0][3]= 0.0f; theMatrix[1][3]= 0.0f; theMatrix[2][3]= 0.0f; theMatrix[3][3]= 1.0f;

        matcopy(rxMat, theMatrix);
        matcopy(ryMat, theMatrix);
        matcopy(rzMat, theMatrix);
        matcopy(scMat, theMatrix);
        matcopy(trMat, theMatrix);
    } // setIdentity


    public void finalize() { // no objects were declared with new ==> nothing to free
        if (ictdebug) {
            Globals.statusPrint("TMatrix destructor");
        }
    } // finalize


    // Called from:
    //     Globals.iwarpz
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.previewStill
    public void scale(float sx, float sy, float sz) {
        float[][] mat = new float[4][4];

        // Setup the scale matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        scMat[0][0]= sx;   scMat[1][0]= 0.0f; scMat[2][0]= 0.0f; scMat[3][0]= 0.0f;
        scMat[0][1]= 0.0f; scMat[1][1]= sy;   scMat[2][1]= 0.0f; scMat[3][1]= 0.0f;
        scMat[0][2]= 0.0f; scMat[1][2]= 0.0f; scMat[2][2]= sz;   scMat[3][2]= 0.0f;
        scMat[0][3]= 0.0f; scMat[1][3]= 0.0f; scMat[2][3]= 0.0f; scMat[3][3]= 1.0f;

        // Set mat = scMat * theMatrix
        matmult(mat, scMat, theMatrix);
        matcopy(theMatrix, mat);
    } // scale


    // Called from:
    //     Globals.iwarpz
    //     MainFrame.getViewMatrix
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.previewStill
    //     ScenePreviewDlg.onCmdPlus
    public void translate(float tx, float ty, float tz) {
        float[][] mat = new float[4][4];
        
        // Setup the translation matrix.
        // See p 71 of the book Visual Special Effects Toolkit in C++.
        trMat[0][0]= 1.0f; trMat[1][0]= 0.0f; trMat[2][0]= 0.0f; trMat[3][0]= tx;
        trMat[0][1]= 0.0f; trMat[1][1]= 1.0f; trMat[2][1]= 0.0f; trMat[3][1]= ty;
        trMat[0][2]= 0.0f; trMat[1][2]= 0.0f; trMat[2][2]= 1.0f; trMat[3][2]= tz;
        trMat[0][3]= 0.0f; trMat[1][3]= 0.0f; trMat[2][3]= 0.0f; trMat[3][3]= 1.0f;

        // Set mat = trMat * theMatrix
        matmult(mat, trMat, theMatrix);
        matcopy(theMatrix, mat);
    } // translate
    

    // Called from:
    //     Globals.iwarpz
    //     MainFrame.getViewMatrix
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.peviewStill
    //     ScenePreviewDlg.onCmdPlus
    public void rotate(float rx, float ry, float rz) {
        float[][] mat1 = new float[4][4];
        float[][] mat2 = new float[4][4];
    
        // Setup x-axis rotation matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        rxMat[0][0]= 1.0f; rxMat[1][0]= 0.0f;                rxMat[2][0]= 0.0f;                rxMat[3][0]= 0.0f;
        rxMat[0][1]= 0.0f; rxMat[1][1]= (float)Math.cos(rx); rxMat[2][1]=-(float)Math.sin(rx); rxMat[3][1]= 0.0f;
        rxMat[0][2]= 0.0f; rxMat[1][2]= (float)Math.sin(rx); rxMat[2][2]= (float)Math.cos(rx); rxMat[3][2]= 0.0f;
        rxMat[0][3]= 0.0f; rxMat[1][3]= 0.0f;                rxMat[2][3]= 0.0f;                rxMat[3][3]= 1.0f;

        // Set mat1 = rxMat * theMatrix
        matmult(mat1, rxMat, theMatrix);
    
        // Setup y-axis rotation matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        ryMat[0][0]= (float)Math.cos(ry); ryMat[1][0]= 0.0f; ryMat[2][0]=(float)Math.sin(ry); ryMat[3][0]= 0.0f;
        ryMat[0][1]= 0.0f;                ryMat[1][1]= 1.0f; ryMat[2][1]= 0.0f;               ryMat[3][1]= 0.0f;
        ryMat[0][2]=-(float)Math.sin(ry); ryMat[1][2]= 0.0f; ryMat[2][2]=(float)Math.cos(ry); ryMat[3][2]= 0.0f;
        ryMat[0][3]= 0.0f;                ryMat[1][3]= 0.0f; ryMat[2][3]= 0.0f;               ryMat[3][3]= 1.0f;

        // Set mat2 = ryMat * mat1 
        //          = ryMat * rxMat * theMatrix
        matmult(mat2, ryMat, mat1);
    
        // Setup z-axis rotation matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        rzMat[0][0]=(float)Math.cos(rz); rzMat[1][0]=-(float)Math.sin(rz); rzMat[2][0]= 0.0f; rzMat[3][0]= 0.0f;
        rzMat[0][1]=(float)Math.sin(rz); rzMat[1][1]= (float)Math.cos(rz); rzMat[2][1]= 0.0f; rzMat[3][1]= 0.0f;
        rzMat[0][2]= 0.0f;               rzMat[1][2]= 0.0f;                rzMat[2][2]= 1.0f; rzMat[3][2]= 0.0f;
        rzMat[0][3]= 0.0f;               rzMat[1][3]= 0.0f;                rzMat[2][3]= 0.0f; rzMat[3][3]= 1.0f;

        // Set theMatrix = rzMat * mat2
        //               = rzMat * ryMat * rxMat * theMatrix
        matmult(theMatrix, rzMat, mat2);
    } // rotate
    

    public static void matmult(float[][] result, float[][] mat1, float[][] mat2) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[j][i] = 0.0f;
                for (int k = 0; k < 4; k++) {
                    result[j][i] += mat1[k][i] * mat2[j][k];  //row = row x column
                } // for k
            } // for j
        } // for i
    } // matmult
    

    public static void matcopy(float[][] dest, float[][] source) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                dest[j][i] = source[j][i];
            }
        }
    } // matcopy
    
    
    public void transpose() {
        float[][] mat1 = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                mat1[i][j] = theMatrix[j][i];
            }
        }

        matcopy(theMatrix, mat1);
    } // transpose
    
    
    // Called from:
    //     Globals.iwarpz
    //     RenderObject.transformAndProjectPoint2
    public void transformPoint(float xIn, float yIn, float zIn, 
    Float xOut, Float yOut, Float zOut) {
        xOut = (xIn * theMatrix[0][0]) + (yIn * theMatrix[1][0]) + (zIn * theMatrix[2][0]) + theMatrix[3][0];
        yOut = (xIn * theMatrix[0][1]) + (yIn * theMatrix[1][1]) + (zIn * theMatrix[2][1]) + theMatrix[3][1];
        zOut = (xIn * theMatrix[0][2]) + (yIn * theMatrix[1][2]) + (zIn * theMatrix[2][2]) + theMatrix[3][2];
    } // transformPoint
    

    public void transformPoint1(Point3d in, Point3d out) {
        out.x = (in.x * theMatrix[0][0]) + (in.y * theMatrix[1][0]) + (in.z * theMatrix[2][0]) + theMatrix[3][0];
        out.y = (in.x * theMatrix[0][1]) + (in.y * theMatrix[1][1]) + (in.z * theMatrix[2][1]) + theMatrix[3][1];
        out.z = (in.x * theMatrix[0][2]) + (in.y * theMatrix[1][2]) + (in.z * theMatrix[2][2]) + theMatrix[3][2];
    } // transformPoint1
    

    // Called from:
    //     Globals.iwarpz
    public void display(String heading) {
        String msgText;

        Globals.statusPrint(heading);
        DecimalFormat sixDotTwo = new DecimalFormat("####.##");

        msgText = 
            sixDotTwo.format(theMatrix[0][0]) + "\t" + 
            sixDotTwo.format(theMatrix[1][0]) + "\t" + 
            sixDotTwo.format(theMatrix[2][0]) + "\t" + 
            sixDotTwo.format(theMatrix[3][0]) + "\t";
        Globals.statusPrint(msgText);
    
        msgText = 
            sixDotTwo.format(theMatrix[0][1]) + "\t" + 
            sixDotTwo.format(theMatrix[1][1]) + "\t" + 
            sixDotTwo.format(theMatrix[2][1]) + "\t" + 
            sixDotTwo.format(theMatrix[3][1]) + "\t";
        Globals.statusPrint(msgText);
    
        msgText = 
            sixDotTwo.format(theMatrix[0][2]) + "\t" + 
            sixDotTwo.format(theMatrix[1][2]) + "\t" + 
            sixDotTwo.format(theMatrix[2][2]) + "\t" + 
            sixDotTwo.format(theMatrix[3][2]) + "\t";
        Globals.statusPrint(msgText);
    
        msgText = 
            sixDotTwo.format(theMatrix[0][3]) + "\t" + 
            sixDotTwo.format(theMatrix[1][3]) + "\t" + 
            sixDotTwo.format(theMatrix[2][3]) + "\t" + 
            sixDotTwo.format(theMatrix[3][3]) + "\t";
        Globals.statusPrint(msgText);
    } // display
    

    // Called from:
    //     Globals.iwarpz
    public int invertg() {
        final int MAXROWS = 4;
        final int MAXCOLS = 4;        // MAXCOLS defined for readability

        // Invert a TMatrix object.
        // Approach:  Augment the forward graphic transformation matrix with four
        // b vectors which collectively make up the 4x4 identity matrix.
        // The augmented b vectors are contained in the TMatrix object bVector.
        // Solve the system using Gaussian elimination and partial pivoting.
        // Back-substitute each of the four processed b vectors to obtain the inverse.
        // Gaussian elimination is described in more detail in:
        //
        // Numerical Methods for Scientists and Engineers, J. D. Hoffman,
        // McGraw-Hill, 1992, Section 1.3.
        int i, j, k, maxValue;
        float aTemp;
        String msgText;
        TMatrix bVector = new TMatrix();
    
        for (i = 0; i < MAXROWS; i++) {
            maxValue = i;
            for (j = i + 1; j < MAXROWS; j++) {  
                // Partial pivot (swap rows) if necessary
                if(Math.abs(theMatrix[i][j]) > Math.abs(theMatrix[i][maxValue])) {
                    maxValue = j;
                }
            }

            if(maxValue != i) {
                for (k = i; k < MAXCOLS; k++) {
                    aTemp = theMatrix[k][i];
                    theMatrix[k][i] = theMatrix[k][maxValue];
                    theMatrix[k][maxValue] = aTemp;
                }
                for (k = 0; k < MAXCOLS; k++) {
                    aTemp = bVector.theMatrix[k][i];
                    bVector.theMatrix[k][i] = bVector.theMatrix[k][maxValue];
                    bVector.theMatrix[k][maxValue] = aTemp;
                }
            }

            for (j = i + 1; j < MAXROWS; j++) {
                float aFactor = theMatrix[i][j] / theMatrix[i][i];
                for (k = MAXCOLS - 1; k >= i; k--) {
                    if(Math.abs(theMatrix[i][i]) < 1.0E-06) {
                        msgText = "invertg: i: " + i + " j: " + j + " k: " + k + " pivot element cannot be zero!";
                        Globals.statusPrint(msgText);
                        return -1;
                    }
                    theMatrix[k][j] -= theMatrix[k][i] * aFactor;
                }

                for (k = MAXCOLS - 1; k >= 0; k--) {
                    bVector.theMatrix[k][j] -= bVector.theMatrix[k][i] * aFactor;
                }
            }
        }

        // Backsubstitute the augmented b vectors to obtain the inverse
        int col, row;
        float aSum;
    
        for (col = 0; col < MAXCOLS; col++) {
            for (j = MAXROWS - 1; j >= 0; j--) {
                aSum = 0.0f;
                if(j != MAXROWS - 1) {
                    for (k = j + 1; k < MAXCOLS; k++) {
                        aSum += (theMatrix[k][j] * bVector.theMatrix[col][k]);
                    }
                }

                if(Math.abs(theMatrix[j][j]) < 1.0E-06f) {
                    Globals.statusPrint("invertg: Zero Diagonal Not Allowed. Exiting");
                    return -1;
                }
                bVector.theMatrix[col][j] = (bVector.theMatrix[col][j] - aSum) / theMatrix[j][j];
            }
        }

        // Copy the inverse into theMatrix
        for (col = 0; col < MAXCOLS; col++) {
            for(row = 0; row < MAXROWS; row++) {
                theMatrix[col][row] = bVector.theMatrix[col][row];
            }
        }

        return 0;
    } // invertg
    

    // Called from:
    //     RenderObject.renderMeshz
    public void transformAndProjectPoint(float x, float y, float z, 
    Integer sx, Integer sy, 
    float refX, float refY, float refZ, 
    int outHeight, int outWidth, 
    Float tx, Float ty, Float tz) {
        x -= refX;    // move the reference point to the origin
        y -= refY;
        z -= refZ;
        transformPoint(x, y, z, tx, ty, tz);
        //x += refX;    // move the point back
        //y += refY;
        //z += refZ;
    
        // Project to the screen
        float d = -512.0f; // Distance from screen to center of projection: (0,0,-d)
        float w = (d / (tz + d));
        sx = (int)(((tx * w) + refX) + (outWidth/2) ); //offset to output image)
        sy = (int)(((ty * w) + refY) + (outHeight/2) );
        //
        // output points are transformed, projected to the screen and then 
        // transformed into image space
        //
    } // transformAndProjectPoint
    

    public void transformAndProjectPoint1(Point3d p, Point2d s, Point3d ref, 
    int outHeight, int outWidth, Point3d t) {
        p.x -= ref.x;
        p.y -= ref.y;
        p.z -= ref.z;
        transformPoint1(p, t);
        
        // Project to the screen
        float d = -512.0f; // Distance from screen to center of projection: (0,0,-d)
        float w = (d / (t.z + d));
        s.x = ((t.x * w) + ref.x) + (outWidth/2); // Center in output image
        s.y = ((t.y * w) + ref.y) + (outHeight/2);
    } // transformAndProjectPoint1
    
    
    // Called from:
    //     Globals.iwarpz
    //     RenderObject.transformAndProject
    public void transformAndProject(Shape3d aShape, int outHeight,
    int outWidth, boolean useExternalCentroid,
    float centroidX, float centroidY, float centroidZ) {
        // Default behavior is to rotate the object about its own centroid.
        // See p 66 - 68 of the book Visual Special Effects Toolkit in C++.
        // If useExternalCentroid is true then the object is rotated about
        // the point (centroidX, centroidY, centroidZ).
        Float cX = 0f, cY = 0f, cZ = 0f;	   //  The translation that moves the shape to the origin
        int sx, sy;
    
        if(!useExternalCentroid) {
            aShape.getReferencePoint(cX, cY, cZ);
        } else {
            cX = centroidX;
            cY = centroidY;
            cZ = centroidZ;
        }
        aShape.translateW(-cX, -cY, -cZ);
    
        aShape.initCurrentVertex();
        float maxtX = 0f, maxtY = 0f, maxtZ = 0f;
        float mintX = 0f, mintY = 0f, mintZ = 0f;

        // Transform the shape using the perspective matrix
        for (int index = 0; index < aShape.getNumVertices(); index++) {
            transformPoint(
                (float)aShape.currentVertex.x, 
                (float)aShape.currentVertex.y, 
                (float)aShape.currentVertex.z,
                aShape.currentVertex.tx, 
                aShape.currentVertex.ty, 
                aShape.currentVertex.tz);
            
            if(index == 0) {
                maxtX = mintX = aShape.currentVertex.tx;
                maxtY = mintY = aShape.currentVertex.ty;
                maxtZ = mintZ = aShape.currentVertex.tz;
            }

            // Calculate the transformed object centroid for depth sorting later
            if(aShape.currentVertex.tx > maxtX) maxtX = aShape.currentVertex.tx;
            if(aShape.currentVertex.tx < mintX) mintX = aShape.currentVertex.tx;
            if(aShape.currentVertex.ty > maxtY) maxtY = aShape.currentVertex.ty;
            if(aShape.currentVertex.ty < mintY) mintY = aShape.currentVertex.ty;
            if(aShape.currentVertex.tz > maxtZ) maxtZ = aShape.currentVertex.tz;
            if(aShape.currentVertex.tz < mintZ) mintZ = aShape.currentVertex.tz;
            // aShape.currentVertex++;
            aShape.incCurrentVertex();
        }

        aShape.originX = mintX + (maxtX - mintX)/2.0f;
        aShape.originY = mintY + (maxtY - mintY)/2.0f;
        aShape.originZ = mintZ + (maxtZ - mintZ)/2.0f;

        // Project to the screen
        aShape.initCurrentVertex();
        float d = -512.0f; // Distance from screen to center of projection: (0,0,-d)
        for (int index = 0; index < aShape.getNumVertices(); index++) {
            float w = (d / (aShape.currentVertex.tz + d));
            aShape.currentVertex.sx = (aShape.currentVertex.tx) * w;
            aShape.currentVertex.sy = (aShape.currentVertex.ty) * w;

            // Round the  projected coordinate
            //sx = (int) (aShape.currentVertex.sx + 0.5);
            //sy = (int) (aShape.currentVertex.sy + 0.5);
            sx = (int)(aShape.currentVertex.sx);
            sy = (int)(aShape.currentVertex.sy);
            aShape.currentVertex.sx = (float)sx;
            aShape.currentVertex.sy = (float)sy;
            // aShape.currentVertex++;
            aShape.incCurrentVertex();
        }
    
        aShape.translateW(cX, cY, cZ);    // Move world coords back
        aShape.translateS((int)cX.floatValue(), (int)cY.floatValue()); // Move screen coords back
    } // transformAndProject


    public int sizeofLowerLimit() {
        int mySize = 0;
        int booleanFieldsSizeInBits = 0;
        int booleanFieldsSize = 0;
        int intFieldsSize = 0;
        int floatFieldsSize = 0;
        int referenceFieldsSize = 0;

        /*
        boolean ictdebug = false;
        protected float[][] theMatrix = new float[4][4];    // Composite transformation matrix
        protected float[][] rxMat = new float[4][4];			 // X rotation matrix
        protected float[][] ryMat = new float[4][4];			 // Y rotation matrix
        protected float[][] rzMat = new float[4][4];			 // Z rotation matrix
        protected float[][] scMat = new float[4][4];			 // Scaling matrix
        protected float[][] trMat = new float[4][4];			 // Translation matrix
        */

        booleanFieldsSizeInBits = 1; // 1 booleans
        booleanFieldsSize = 1; // 1 bit fits in a byte
        intFieldsSize = 0*4; // 0 ints
        floatFieldsSize = 0*4; // 0 floats
        referenceFieldsSize = 6*4; // 6 references to objects (6 matrices)
        mySize = booleanFieldsSize + intFieldsSize + floatFieldsSize + referenceFieldsSize;

        return mySize;
    } // sizeofLowerLimit
} // class TMatrix