package math;

import core.Shape3d;

import dtos.OneFloat;
import dtos.OneInt;

import globals.Globals;

import java.text.DecimalFormat;

import structs.Point2d;
import structs.Point3d;


public class TMatrix {
    private boolean bIctDebug = false;
    protected float[][] mMatrix = new float[4][4];    // Composite transformation matrix
    protected float[][] rxMat = new float[4][4];	         // X rotation matrix
    protected float[][] ryMat = new float[4][4];			 // Y rotation matrix
    protected float[][] rzMat = new float[4][4];			 // Z rotation matrix
    protected float[][] scMat = new float[4][4];			 // Scaling matrix
    protected float[][] trMat = new float[4][4];			 // Translation matrix

/*
public:
  void scale(float sx, float sy, float sz); - implemented
  void rotate(float rx,float ry,float rz); - implemented
  void translate(float tx, float ty, float tz); - implemented
  void multiply(tMatrix *matrix1, tMatrix *matrix2); - implemented
  void setIdentity(); - implemented
  void transformPoint(float xIn, float yIn, float zIn, float *xOut,
    float *yOut, float *zOut); - implemented
  void transformPoint1(point3d *in, point3d *out); - implemented

void transformAndProject (shape3d *aShape, int outHeight, int outWidth,
	   int externalCentroid = 0,
	   float centroidX = 0, float centroidY = 0, float centroidZ = 0); - implemented

void transformAndProjectPoint(float x, float y, float z, int *sx, 
  int *sy, float refX, float refY, float refZ, 
  int outHeight, int outWidth, float *tx, float *ty, float *tz); - implemented

void tMatrix::transformAndProjectPoint1(point3d *p, point2d *s, point3d *ref, 
							  int outHeight, int outWidth, point3d *t); - implemented

  int invertg(); - implemented
  void transpose(); - implemented
  tMatrix(); - implemented
  tMatrix(tMatrix *aMatrix); - implemented
  void copy(tMatrix *aMatrix); - implemented
  tMatrix(tMatrix *matrix1, tMatrix *matrix2); ----- NOT IMPLEMENTED, not found in TMATRIX.CPP ----
  ~tMatrix(); - implemented, in the form of method finalize
  void display(char *); - implemented
*/

    // Called from:
    //     Gloals.iwarpz
    //     Globals.tweenImage
    //     MainFrame.onToolsWarpImage
    //     RenderObject ctor that takes 4 Point3d parameters
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    //     SceneList.calcCompoundModelRefPoint
    public TMatrix() {
        Globals.statusPrint(bIctDebug, "TMatrix ctor 1.");

        setIdentity();
    } // TMatrix ctor
    
    
    // Called from:
    //     Globals.iwarpz
    public TMatrix(TMatrix pMatrix) {
        Globals.statusPrint(bIctDebug, "TMatrix ctor 2.");

        matcopy(mMatrix, pMatrix.mMatrix);
    } // TMatrix ctor
    
    
    // Called from:
    //     Globals.iwarpz
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public void multiply(TMatrix pMatrix1, TMatrix pMatrix2) {
        setIdentity();

        // Set mMatrix = pMatrix1.mMatrix * pMatrix2.mMatrix
        matmult(mMatrix, pMatrix1.mMatrix, pMatrix2.mMatrix);
    } // multiply
    

    // Called from:
    //     RenderObject.transformAndProject
    public void copy(TMatrix pMatrix) {
        setIdentity();
        matcopy(mMatrix, pMatrix.mMatrix);
    } // copy
    

    // Called from:
    //     MainFrame.getViewMatrix
    //     SceneList.getViewMatrix
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    //     ScenePreviewDlg.onCmdPlus
    public void setIdentity() {
        mMatrix[0][0]= 1.0f; mMatrix[1][0]= 0.0f; mMatrix[2][0]= 0.0f; mMatrix[3][0]= 0.0f;
        mMatrix[0][1]= 0.0f; mMatrix[1][1]= 1.0f; mMatrix[2][1]= 0.0f; mMatrix[3][1]= 0.0f;
        mMatrix[0][2]= 0.0f; mMatrix[1][2]= 0.0f; mMatrix[2][2]= 1.0f; mMatrix[3][2]= 0.0f;
        mMatrix[0][3]= 0.0f; mMatrix[1][3]= 0.0f; mMatrix[2][3]= 0.0f; mMatrix[3][3]= 1.0f;

        // Set our fields
        matcopy(rxMat, mMatrix);
        matcopy(ryMat, mMatrix);
        matcopy(rzMat, mMatrix);
        matcopy(scMat, mMatrix);
        matcopy(trMat, mMatrix);
    } // setIdentity


    public void finalize() { // no objects were declared with new ==> nothing to free
        Globals.statusPrint(bIctDebug, "TMatrix dtor");
    } // finalize


    // Called from:
    //     Globals.iwarpz
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public void scale(float pfSx, float pfSy, float pfSz) {
        float[][] mat = new float[4][4];

        // Setup the scale matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        scMat[0][0]= pfSx; scMat[1][0]= 0.0f; scMat[2][0]= 0.0f; scMat[3][0]= 0.0f;
        scMat[0][1]= 0.0f; scMat[1][1]= pfSy; scMat[2][1]= 0.0f; scMat[3][1]= 0.0f;
        scMat[0][2]= 0.0f; scMat[1][2]= 0.0f; scMat[2][2]= pfSz; scMat[3][2]= 0.0f;
        scMat[0][3]= 0.0f; scMat[1][3]= 0.0f; scMat[2][3]= 0.0f; scMat[3][3]= 1.0f;

        // Set mat = scMat * mMatrix
        matmult(mat, scMat, mMatrix);
        matcopy(mMatrix, mat);
    } // scale


    // Called from:
    //     Globals.iwarpz
    //     MainFrame.getViewMatrix
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.getViewMatrix
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    //     ScenePreviewDlg.onCmdPlus
    public void translate(float pfTx, float pfTy, float pfTz) {
        float[][] mat = new float[4][4];
        
        // Setup the translation matrix.
        // See p 71 of the book Visual Special Effects Toolkit in C++.
        trMat[0][0]= 1.0f; trMat[1][0]= 0.0f; trMat[2][0]= 0.0f; trMat[3][0]= pfTx;
        trMat[0][1]= 0.0f; trMat[1][1]= 1.0f; trMat[2][1]= 0.0f; trMat[3][1]= pfTy;
        trMat[0][2]= 0.0f; trMat[1][2]= 0.0f; trMat[2][2]= 1.0f; trMat[3][2]= pfTz;
        trMat[0][3]= 0.0f; trMat[1][3]= 0.0f; trMat[2][3]= 0.0f; trMat[3][3]= 1.0f;

        // Set mat = trMat * mMatrix
        matmult(mat, trMat, mMatrix);
        matcopy(mMatrix, mat);
    } // translate
    

    // Called from:
    //     Globals.iwarpz
    //     MainFrame.getViewMatrix
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.getViewMatrix
    //     SceneList.preview
    //     SceneList.peviewStill
    //     SceneList.render
    //     ScenePreviewDlg.onCmdPlus
    public void rotate(float pfRx, float pfRy, float pfRz) {
        float[][] mat1 = new float[4][4];
        float[][] mat2 = new float[4][4];
    
        // Setup x-axis rotation matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        float fCosRx = (float)Math.cos(pfRx);
        float fSinRx = (float)Math.sin(pfRx);
        rxMat[0][0]= 1.0f; rxMat[1][0]= 0.0f;   rxMat[2][0]= 0.0f;   rxMat[3][0]= 0.0f;
        rxMat[0][1]= 0.0f; rxMat[1][1]= fCosRx; rxMat[2][1]=-fSinRx; rxMat[3][1]= 0.0f;
        rxMat[0][2]= 0.0f; rxMat[1][2]= fSinRx; rxMat[2][2]= fCosRx; rxMat[3][2]= 0.0f;
        rxMat[0][3]= 0.0f; rxMat[1][3]= 0.0f;   rxMat[2][3]= 0.0f;   rxMat[3][3]= 1.0f;

        // Set mat1 = rxMat * mMatrix
        matmult(mat1, rxMat, mMatrix);
    
        // Setup y-axis rotation matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        float fCosRy = (float)Math.cos(pfRy);
        float fSinRy = (float)Math.sin(pfRy);
        ryMat[0][0]= fCosRy; ryMat[1][0]= 0.0f; ryMat[2][0]= fSinRy; ryMat[3][0]= 0.0f;
        ryMat[0][1]= 0.0f;   ryMat[1][1]= 1.0f; ryMat[2][1]= 0.0f;   ryMat[3][1]= 0.0f;
        ryMat[0][2]=-fSinRy; ryMat[1][2]= 0.0f; ryMat[2][2]= fCosRy; ryMat[3][2]= 0.0f;
        ryMat[0][3]= 0.0f;   ryMat[1][3]= 0.0f; ryMat[2][3]= 0.0f;   ryMat[3][3]= 1.0f;

        // Set mat2 = ryMat * mat1 
        //          = ryMat * rxMat * mMatrix
        matmult(mat2, ryMat, mat1);
    
        // Setup z-axis rotation matrix.
        // See p 72 of the book Visual Special Effects Toolkit in C++.
        float fCosRz = (float)Math.cos(pfRz);
        float fSinRz = (float)Math.sin(pfRz);
        rzMat[0][0]= fCosRz; rzMat[1][0]=-fSinRz; rzMat[2][0]= 0.0f; rzMat[3][0]= 0.0f;
        rzMat[0][1]= fSinRz; rzMat[1][1]= fCosRz; rzMat[2][1]= 0.0f; rzMat[3][1]= 0.0f;
        rzMat[0][2]= 0.0f;   rzMat[1][2]= 0.0f;   rzMat[2][2]= 1.0f; rzMat[3][2]= 0.0f;
        rzMat[0][3]= 0.0f;   rzMat[1][3]= 0.0f;   rzMat[2][3]= 0.0f; rzMat[3][3]= 1.0f;

        // Set mMatrix = rzMat * mat2
        //             = rzMat * ryMat * rxMat * mMatrix
        matmult(mMatrix, rzMat, mat2);
    } // rotate
    

    // Called from:
    //     multiply
    //     rotate
    //     scale
    //     translate
    public static void matmult(float[][] pfaResult, float[][] pfaMat1, float[][] pfaMat2) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pfaResult[j][i] = 0.0f;
                for (int k = 0; k < 4; k++) {
                    pfaResult[j][i] += pfaMat1[k][i] * pfaMat2[j][k];  //row = row x column
                } // for k
            } // for j
        } // for i
    } // matmult
    

    // Called from:
    //     TMatrix constructor that takes a TMatrix parameter
    //     copy
    //     scale
    //     setIdentity
    //     translate
    //     transpose
    public static void matcopy(float[][] pfaDest, float[][] pfaSrc) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pfaDest[j][i] = pfaSrc[j][i];
            } // for j
        } // for i
    } // matcopy
    
    
    public void transpose() {
        float[][] faMat1 = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                faMat1[i][j] = mMatrix[j][i];
            }
        }

        matcopy(mMatrix, faMat1);
    } // transpose
    
    
    // Called from:
    //     transformAndProject
    //     Globals.iwarpz (called twice)
    //     RenderObject.transformAndProjectPoint2
    public void transformPoint(float pfXIn, float pfYIn, float pfZIn, 
    OneFloat pXOutOF, OneFloat pYOutOF, OneFloat pZOutOF) {
        // Set the output parameters
        pXOutOF.f = (pfXIn * mMatrix[0][0]) + (pfYIn * mMatrix[1][0]) + (pfZIn * mMatrix[2][0]) + mMatrix[3][0];
        pYOutOF.f = (pfXIn * mMatrix[0][1]) + (pfYIn * mMatrix[1][1]) + (pfZIn * mMatrix[2][1]) + mMatrix[3][1];
        pZOutOF.f = (pfXIn * mMatrix[0][2]) + (pfYIn * mMatrix[1][2]) + (pfZIn * mMatrix[2][2]) + mMatrix[3][2];
    } // transformPoint
    

    // Called from:
    //     transformAndProjectPoint1
    public void transformPoint1(Point3d pIn, Point3d pOut) {
        // Set our output parameter
        pOut.fX = (pIn.fX * mMatrix[0][0]) + (pIn.fY * mMatrix[1][0]) + (pIn.fZ * mMatrix[2][0]) + mMatrix[3][0];
        pOut.fY = (pIn.fX * mMatrix[0][1]) + (pIn.fY * mMatrix[1][1]) + (pIn.fZ * mMatrix[2][1]) + mMatrix[3][1];
        pOut.fZ = (pIn.fX * mMatrix[0][2]) + (pIn.fY * mMatrix[1][2]) + (pIn.fZ * mMatrix[2][2]) + mMatrix[3][2];
    } // transformPoint1
    

    // Called from:
    //     Globals.iwarpz
    public void display(String psHeading) {
        String sMsgText;

        Globals.statusPrint(psHeading);
        DecimalFormat sixDotTwo = new DecimalFormat("####.##");

        sMsgText = 
            sixDotTwo.format(mMatrix[0][0]) + "\t" + 
            sixDotTwo.format(mMatrix[1][0]) + "\t" + 
            sixDotTwo.format(mMatrix[2][0]) + "\t" + 
            sixDotTwo.format(mMatrix[3][0]) + "\t";
        Globals.statusPrint(sMsgText);
    
        sMsgText = 
            sixDotTwo.format(mMatrix[0][1]) + "\t" + 
            sixDotTwo.format(mMatrix[1][1]) + "\t" + 
            sixDotTwo.format(mMatrix[2][1]) + "\t" + 
            sixDotTwo.format(mMatrix[3][1]) + "\t";
        Globals.statusPrint(sMsgText);
    
        sMsgText = 
            sixDotTwo.format(mMatrix[0][2]) + "\t" + 
            sixDotTwo.format(mMatrix[1][2]) + "\t" + 
            sixDotTwo.format(mMatrix[2][2]) + "\t" + 
            sixDotTwo.format(mMatrix[3][2]) + "\t";
        Globals.statusPrint(sMsgText);
    
        sMsgText = 
            sixDotTwo.format(mMatrix[0][3]) + "\t" + 
            sixDotTwo.format(mMatrix[1][3]) + "\t" + 
            sixDotTwo.format(mMatrix[2][3]) + "\t" + 
            sixDotTwo.format(mMatrix[3][3]) + "\t";
        Globals.statusPrint(sMsgText);
    } // display
    

    // Called from:
    //     Globals.iwarpz
    public int invertg() {
        final int I_MAXROWS = 4;
        final int I_MAXCOLS = 4;        // MAXCOLS defined for readability

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
        int i, j, k, iMaxValue;
        float fTemp;
        String sMsgText;
        TMatrix bVector = new TMatrix();
    
        for (i = 0; i < I_MAXROWS; i++) {
            iMaxValue = i;
            for (j = i + 1; j < I_MAXROWS; j++) {  
                // Partial pivot (swap rows) if necessary
                if(Math.abs(mMatrix[i][j]) > Math.abs(mMatrix[i][iMaxValue])) {
                    iMaxValue = j;
                }
            } // for j

            if(iMaxValue != i) {
                for (k = i; k < I_MAXCOLS; k++) {
                    fTemp = mMatrix[k][i];
                    mMatrix[k][i] = mMatrix[k][iMaxValue];
                    mMatrix[k][iMaxValue] = fTemp;
                }
                for (k = 0; k < I_MAXCOLS; k++) {
                    fTemp = bVector.mMatrix[k][i];
                    bVector.mMatrix[k][i] = bVector.mMatrix[k][iMaxValue];
                    bVector.mMatrix[k][iMaxValue] = fTemp;
                }
            }

            for (j = i + 1; j < I_MAXROWS; j++) {
                float fFactor = mMatrix[i][j] / mMatrix[i][i];
                for (k = I_MAXCOLS - 1; k >= i; k--) {
                    if(Math.abs(mMatrix[i][i]) < 1.0E-06) {
                        sMsgText = "invertg: i: " + i + " j: " + j + " k: " + k + " pivot element cannot be zero!";
                        Globals.statusPrint(sMsgText);
                        return -1;
                    }
                    mMatrix[k][j] -= mMatrix[k][i] * fFactor;
                }

                for (k = I_MAXCOLS - 1; k >= 0; k--) {
                    bVector.mMatrix[k][j] -= bVector.mMatrix[k][i] * fFactor;
                }
            }
        }

        // Backsubstitute the augmented b vectors to obtain the inverse
        int iCol, iRow;
        float fSum;
    
        for (iCol = 0; iCol < I_MAXCOLS; iCol++) {
            for (j = I_MAXROWS - 1; j >= 0; j--) {
                fSum = 0.0f;
                if(j != I_MAXROWS - 1) {
                    for (k = j + 1; k < I_MAXCOLS; k++) {
                        fSum += (mMatrix[k][j] * bVector.mMatrix[iCol][k]);
                    }
                }

                if(Math.abs(mMatrix[j][j]) < 1.0E-06f) {
                    Globals.statusPrint("invertg: Zero Diagonal Not Allowed. Exiting");
                    return -1;
                }
                bVector.mMatrix[iCol][j] = (bVector.mMatrix[iCol][j] - fSum) / mMatrix[j][j];
            }
        }

        // Copy the inverse into mMatrix
        for (iCol = 0; iCol < I_MAXCOLS; iCol++) {
            for(iRow = 0; iRow < I_MAXROWS; iRow++) {
                mMatrix[iCol][iRow] = bVector.mMatrix[iCol][iRow];
            }
        }

        return 0;
    } // invertg
    

    // Called from:
    //     Globals.fwarp1
    //     Globals.fwarpz (called 4 times from here)
    //     Globals.fwarpz2
    //     Globals.renderMesh
    //     RenderObject.renderMeshz
    public void transformAndProjectPoint(float pfX, float pfY, float pfZ, 
    OneInt pSxOI, OneInt pSyOI, 
    float pfRefX, float pfRefY, float pfRefZ, 
    int piOutHeight, int piOutWidth, 
    OneFloat pTxOF, OneFloat pTyOF, OneFloat pTzOF) {
        pfX -= pfRefX;    // move the reference point to the origin
        pfY -= pfRefY;
        pfZ -= pfRefZ;

        // The following method sets parameters pTxOF, pTyOF, and pTzOF
        transformPoint(pfX, pfY, pfZ, pTxOF, pTyOF, pTzOF);
        //x += refX;    // move the point back
        //y += refY;
        //z += refZ;
    
        // Project to the screen
        float fD = -512.0f; // Distance from screen to center of projection: (0,0,-d)
        float fW = (fD / (pTzOF.f + fD));

        // Set output parameters pSxOI and pSyOI
        pSxOI.i = (int)(((pTxOF.f * fW) + pfRefX) + (piOutWidth/2) ); //offset to output image)
        pSyOI.i = (int)(((pTyOF.f * fW) + pfRefY) + (piOutHeight/2) );
        //
        // output points are transformed, projected to the screen and then 
        // transformed into image space
        //
    } // transformAndProjectPoint
    

    public void transformAndProjectPoint1(Point3d pP, Point2d pS, Point3d pRef, 
    int piOutHeight, int piOutWidth, Point3d pT) {
        pP.fX -= pRef.fX;
        pP.fY -= pRef.fY;
        pP.fZ -= pRef.fZ;
        transformPoint1(pP, pT);
        
        // Project to the screen
        float fD = -512.0f; // Distance from screen to center of projection: (0,0,-d)
        float fW = (fD / (pT.fZ + fD));
        pS.x = ((pT.fX * fW) + pRef.fX) + (piOutWidth/2); // Center in output image
        pS.y = ((pT.fY * fW) + pRef.fY) + (piOutHeight/2);
    } // transformAndProjectPoint1
    
    
    // Called from:
    //     Globals.iwarpz
    //     RenderObject.transformAndProject
    //     SceneList.render
    public void transformAndProject(Shape3d pShape, 
    boolean pbUseExternalCentroid,
    float pfCentroidX, float pfCentroidY, float pfCentroidZ) {
        // Default behavior is to rotate the object about its own centroid.
        // See p 66 - 68 of the book Visual Special Effects Toolkit in C++.
        // If useExternalCentroid is true then the object is rotated about
        // the point (pfCentroidX, pfCentroidY, pfCentroidZ).
        Point3d refPt = new Point3d();	   //  The translation that moves the shape to the origin
        int iSx, iSy;
    
        if(!pbUseExternalCentroid) {
            pShape.getReferencePoint(refPt);
        } else {
            refPt.fX = pfCentroidX;
            refPt.fY = pfCentroidY;
            refPt.fZ = pfCentroidZ;
        }
        pShape.translateW(-refPt.fX, -refPt.fY, -refPt.fZ);
    
        pShape.initCurrentVertex();
        float fMaxtX = 0f, fMaxtY = 0f, fMaxtZ = 0f;
        float fMintX = 0f, fMintY = 0f, fMintZ = 0f;
        OneFloat txOF = new OneFloat();
        OneFloat tyOF = new OneFloat();
        OneFloat tzOF = new OneFloat();

        // Transform the shape using the perspective matrix
        for (int index = 0; index < pShape.getNumVertices(); index++) {
            // The following method modifies the last 3 parameters
            transformPoint(
                pShape.mCurrentVertex.x, 
                pShape.mCurrentVertex.y, 
                pShape.mCurrentVertex.z,
                txOF, tyOF, tzOF);
            
            pShape.mCurrentVertex.tx = txOF.f;
            pShape.mCurrentVertex.ty = tyOF.f;
            pShape.mCurrentVertex.tz = tzOF.f;
            
            if(index == 0) {
                fMaxtX = fMintX = pShape.mCurrentVertex.tx;
                fMaxtY = fMintY = pShape.mCurrentVertex.ty;
                fMaxtZ = fMintZ = pShape.mCurrentVertex.tz;
            }

            // Calculate the transformed object centroid for depth sorting later
            if(pShape.mCurrentVertex.tx > fMaxtX) fMaxtX = pShape.mCurrentVertex.tx;
            if(pShape.mCurrentVertex.tx < fMintX) fMintX = pShape.mCurrentVertex.tx;

            if(pShape.mCurrentVertex.ty > fMaxtY) fMaxtY = pShape.mCurrentVertex.ty;
            if(pShape.mCurrentVertex.ty < fMintY) fMintY = pShape.mCurrentVertex.ty;

            if(pShape.mCurrentVertex.tz > fMaxtZ) fMaxtZ = pShape.mCurrentVertex.tz;
            if(pShape.mCurrentVertex.tz < fMintZ) fMintZ = pShape.mCurrentVertex.tz;
            // aShape.currentVertex++;
            pShape.incCurrentVertex();
        }

        pShape.mfOriginX = fMintX + (fMaxtX - fMintX)/2.0f;
        pShape.mfOriginY = fMintY + (fMaxtY - fMintY)/2.0f;
        pShape.mfOriginZ = fMintZ + (fMaxtZ - fMintZ)/2.0f;

        // Project to the screen
        pShape.initCurrentVertex();
        float fD = -512.0f; // Distance from screen to center of projection: (0,0,-d)
        for (int index = 0; index < pShape.getNumVertices(); index++) {
            float fW = (fD / (pShape.mCurrentVertex.tz + fD));
            pShape.mCurrentVertex.sx = (pShape.mCurrentVertex.tx) * fW;
            pShape.mCurrentVertex.sy = (pShape.mCurrentVertex.ty) * fW;

            // Round the  projected coordinate
            //sx = (int) (aShape.mCurrentVertex.sx + 0.5);
            //sy = (int) (aShape.mCurrentVertex.sy + 0.5);
            iSx = (int)(pShape.mCurrentVertex.sx);
            iSy = (int)(pShape.mCurrentVertex.sy);
            pShape.mCurrentVertex.sx = (float)iSx;
            pShape.mCurrentVertex.sy = (float)iSy;
            // aShape.currentVertex++;
            pShape.incCurrentVertex();
        }
    
        // Move world coords back
        pShape.translateW(refPt.fX, refPt.fY, refPt.fZ); 

        // Move screen coords back
        pShape.translateS((int)refPt.fX, (int)refPt.fY); 
    } // transformAndProject
} // class TMatrix