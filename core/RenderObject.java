package core;

import dtos.ScreenVertex;

import fileUtils.FileUtils;

import globals.Globals;
import globals.JICTConstants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import math.MathUtils;
import math.TMatrix;
import math.Vect;

import structs.Point2d;
import structs.Point3d;

public class RenderObject {
    private boolean bIctDebug = false;
    public int miModelType;		// a copy of the sceneElement model Type
    public Shape3d mCurrentShape;
    public Shape3d mLastShape;

    // These memImages contain a quadrilateral mesh model
    // Set in the constructor that takes a file name parameter.
    // It is set if miModelType = QUADMESH
    // Read in:
    //     previewMesh
    protected MemImage mTextureImage;
    
    // Set in the constructor that takes a file name parameter.
    // They are set if miModelType = QUADMESH
    // Read in:
    //     previewMesh
    //     renderMesh
    //     renderMeshz
    //     transformAndProject, the one that takes 7 parameters
    private MemImage mXImage, mYImage, mZImage;

    // The transformation matrix
    // Initialized in both constructors.
    // Used in:
    //     previewMesh
    //     renderMesh
    //     renderMeshz
    private TMatrix mMatrix; 

    // Set to 1 if the renderObject was successfully created
    // Initialized in both constructors
    // Read in:
    //     isValid
    private boolean mbValid;

/*
public:
  renderObject (point3d *UL, point3d *UR, point3d *LR, point3d *LL); - implemented

  renderObject (char *fileName, int modelType, int userPOR, point3d *POR); - implemented
  
  virtual ~renderObject (); - implemented as method finalize
  void drawSequence(HDC theDC, char *modelName, int screenHeight, int screenWidth,
    int frameCounter); - implemented
  void drawStill(HWND theWindow, char *modelName, int screenHeight, int screenWidth); - implemented
  
  void previewMesh(HDC theDC, char *modelName,
    float xOff, float yOff, 
	int screenHeight, int screenWidth); - implemented
  int renderMesh(memImage *outputImage, memImage *inputImage, int blendIndicator); - implemented
  int renderMeshz(memImage *outputImage, memImage *maskImage, 
    memImage *inputImage, memImage *zBuffer,float vx, float vy, 
	float vz); - implemented
  int renderShape(memImage *outputImage, int blendIndicator); - implemented
  int renderShapez(memImage *outputImage, memImage *alphaImage, memImage *zBuffer, 
      float vx, float vy, float vz); - implemented
 
  int shadeMeshz(memImage *outputImage, memImage *maskImage, 
    memImage *inputImage, memImage *zBuffer, 
    float vx, float vy, float vz); ==== NOT IMPLEMENTED ====
	  
void transformAndProject (tMatrix *aMatrix, int outHeight, int outWidth,
					      int externalCentroid=0,
					      float centroidX=0, float centroidY=0, float centroidZ=0); - implemented
  int isValid();
protected:
// These memImages contain a quadrilateral mesh model
  memImage *textureImage, *xImage, *yImage, *zImage;
  tMatrix *m_matrix;    //the transformation matrix
  int valid;          // Set to 1 if the renderObject was successfully created
*/


    // Could not find where this constructor is being called from.
    public RenderObject(Point3d pULPt, Point3d pURPt, Point3d pLRPt, Point3d pLLPt) {
        this.mCurrentShape = null;
        this.mLastShape = null;
        if(bIctDebug) {
            Globals.statusPrint("RenderObject Constructor 1");
        }

        this.mCurrentShape = new Shape3d(pULPt, pURPt, pLRPt, pLLPt);
        Float fCentroidX = 0f, fCentroidY = 0f, fCentroidZ = 0f;
        // The following method sets all 3 parameters
        this.mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);

        // Make certain the shape is centered in the X-Y plane.
        if(
        (fCentroidX > 0.5f || fCentroidX < -0.5f) ||
        (fCentroidY > 0.5f || fCentroidY < -0.5f) ||
        (fCentroidZ > 0.5f || fCentroidZ < -0.5f) ) {
            String sMsgText = String.format("RenderObject Constructor 1: Centering shape - xCent: %f, yCent: %f zCent: %f",
                fCentroidX, fCentroidY, fCentroidZ);
            Globals.statusPrint(sMsgText);
            mCurrentShape.translateW(-fCentroidX, -fCentroidY, -fCentroidZ);   
            mCurrentShape.floor();   
            mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);
        }

        mCurrentShape.setReferencePoint(fCentroidX, fCentroidY, fCentroidZ);
        this.mLastShape = new Shape3d(4); // 4 vertex shape with coords set to 0
        this.mMatrix = new TMatrix();
        this.mbValid = true;
    } // RenderObject ctor


    // Called from:
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.preview
    //     SceneList.previewStill
    public RenderObject(String psFileName, int piModelType, boolean pbUserPOR, Point3d POR) {
        boolean bValidCurrentShape = true;
        boolean bValidLastShape = true;
        Float fCentroidX = 0f, fCentroidY = 0f, fCentroidZ = 0f;

        this.mCurrentShape = null;
        this.mLastShape = null;
        this.mTextureImage = null;
        this.mXImage = null;
        this.mYImage = null;
        this.mZImage = null;
        this.miModelType = piModelType;  // set data members
        this.mMatrix = new TMatrix();

        if (bIctDebug) {
            Globals.statusPrint("RenderObject Constructor 2");
        }
        this.mbValid = true;

        switch(this.miModelType) {
        case JICTConstants.I_QUADMESH:
            String sTexturePath;
            StringBuffer sbXPath = new StringBuffer();
            StringBuffer sbYPath = new StringBuffer();
            StringBuffer sbZPath = new StringBuffer();
            sTexturePath = psFileName;
            // The following method sets parameter sXPath
            assembleName(sTexturePath, 'x', sbXPath);

            // The following method sets parameter sYPath
            assembleName(sTexturePath, 'y', sbYPath);

            // The following method sets parameter sZPath
            assembleName(sTexturePath, 'z', sbZPath);

            mTextureImage = new MemImage(psFileName, 0, 0, 
                JICTConstants.I_RANDOM, 'R', 0);
            mXImage = new MemImage(sbXPath, 0, 0, 
                JICTConstants.I_RANDOM, 'R', 0);
            mYImage = new MemImage(sbYPath, 0, 0, 
                JICTConstants.I_RANDOM, 'R', 0);
            mZImage = new MemImage(sbZPath, 0, 0, 
                JICTConstants.I_RANDOM, 'R', 0);

            // Make certain the QuadMesh is centered in the X-Y plane.
            Globals.getMeshCentroid(mXImage, mYImage, mZImage, fCentroidX, fCentroidY, fCentroidZ);
            if(
            (fCentroidX > 0.5f || fCentroidX < -0.5f) ||
            (fCentroidY > 0.5f || fCentroidY < -0.5f) ||
            (fCentroidZ > 0.5f || fCentroidZ < -0.5f) ) {
                String msgText = "RenderObject Constructor 2: Centering QuadMesh - " + 
                    "xCent: "   + fCentroidX + 
                    ", yCent: " + fCentroidY + 
                    ", zCent: " + fCentroidZ;
                Globals.statusPrint(msgText);
                Globals.translateMesh(mXImage, mYImage, mZImage, -fCentroidX, -fCentroidY, -fCentroidZ);
            }

            // Create shape objects. store the quadmesh centroid in the currentshape
            mCurrentShape = new Shape3d(psFileName, miModelType);
            if(!mCurrentShape.isValid()) {
                bValidCurrentShape = false;
            } else {
                mCurrentShape.setReferencePoint(0.0f, 0.0f, 0.0f);
            }

            // Create an n vertex shape with coords set to 0
            mLastShape = new Shape3d(mCurrentShape.getNumVertices());
            if(!mLastShape.isValid()) {
                bValidLastShape = false;
            }

            // The Shape3d constructor will issue a message if either of these
            // objects are not successfully created.
            if(!bValidCurrentShape || !bValidLastShape) {
                this.mbValid = false;
            }
            break;

        case JICTConstants.I_IMAGE: 
            mCurrentShape = new Shape3d(psFileName, this.miModelType);
            if(!mCurrentShape.isValid()) {
                bValidCurrentShape = false;
            } else {
                if(pbUserPOR) {  // If the user has defined a Point of Reference
                  mCurrentShape.setReferencePoint(POR.x, POR.y, POR.z);
                } else {
                    // Make certain the shape is centered in the X-Y plane.
                    mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);
                    if(
                    (fCentroidX > 0.5f || fCentroidX < -0.5f) ||
                    (fCentroidY > 0.5f || fCentroidY < -0.5f) ||
                    (fCentroidZ > 0.5f || fCentroidZ < -0.5f) ) {
                        String msgText = "RenderObject Constructor 2: Centering Shape - " + 
                            "xCent: "   + fCentroidX + 
                            ", yCent: " + fCentroidY + 
                            ", zCent: " + fCentroidZ;
                        Globals.statusPrint(msgText);
                        mCurrentShape.translateW(-fCentroidX, -fCentroidY, -fCentroidZ);   
                    }
                    mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);
                    mCurrentShape.setReferencePoint(fCentroidX, fCentroidY, fCentroidZ);
                }
            }

            // Create an n vertex shape with coords set to 0
            mLastShape = new Shape3d(mCurrentShape.getNumVertices());
            if(!mLastShape.isValid()) {
                bValidLastShape = false;
            }

            // The Shape3d constructor will issue a message if either of these
            // objects are not successfully created.
            if(!bValidCurrentShape|| !bValidLastShape) {
                this.mbValid = false;
            }
            break;

        case JICTConstants.I_SHAPE: 
            mCurrentShape = new Shape3d(psFileName, miModelType);
            if(!mCurrentShape.isValid()) {
                bValidCurrentShape = false;
            } else {
                if(pbUserPOR) {  // If the user has defined a Point of Reference
                    mCurrentShape.setReferencePoint(POR.x, POR.y, POR.z);
                } else {
                    mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);

                    // Make certain the shape is centered in the X-Y plane.
                    mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);
                    if(
                    (fCentroidX > 0.5f || fCentroidX < -0.5f) ||
                    (fCentroidY > 0.5f || fCentroidY < -0.5f) ||
                    (fCentroidZ > 0.5f || fCentroidZ < -0.5f) ) {
                        String sMsgText = "RenderObject Constructor 2: Centering Shape: " + 
                            "xCent: "   + fCentroidX + 
                            ", yCent: " + fCentroidY + 
                            ", zCent: " + fCentroidZ;
                        Globals.statusPrint(sMsgText);
                        mCurrentShape.translateW(-fCentroidX, -fCentroidY, -fCentroidZ);   
                    }
                    mCurrentShape.getWCentroid(fCentroidX, fCentroidY, fCentroidZ);
                    mCurrentShape.setReferencePoint(fCentroidX, fCentroidY, fCentroidZ);
                }
            }

            // Create an n vertex shape with coords set to 0
            mLastShape = new Shape3d(mCurrentShape.getNumVertices());
            if(!mLastShape.isValid()) {
                bValidLastShape = false;
            }

            // The Shape3d constructor will issue a message if either of these
            // objects are not successfully created.
            if(!bValidCurrentShape || !bValidLastShape) {
                this.mbValid = false;
            }
            break;

        case JICTConstants.I_COMPOUND: 
            break;
        }  // switch
    } // RenderObject ctor


    public void finalize() {
        if(bIctDebug) {
          Globals.statusPrint("RenderObject Destructor");
        }
    } // finalize


    // Called from:
    //     SceneList.preview
    public void drawSequence(BufferedImage pBuffImg, String psModelName, 
    int piScreenHeight, int piScreenWidth, int piFrameCounter) {
        Graphics2D graphics2D = pBuffImg.createGraphics();
        Color blackColor;
        // boolean highlightVertices = false; // variable is not used
        int iPt1X, iPt1Y;
        int iPt2X = 0, iPt2Y = 0;
        // float referenceX, referenceY, referenceZ; // These variables are not used

        mCurrentShape.initCurrentVertex();
        if(mCurrentShape.getNumVertices() == 0) { 
            return;
        }

        mLastShape.initCurrentVertex();
        Color oldColor = graphics2D.getColor();
        int iXOffset = piScreenWidth / 2;
        int iYOffset = piScreenHeight / 2;
      
        if (this.miModelType == JICTConstants.I_QUADMESH) {
            previewMesh(graphics2D, psModelName, iXOffset, iYOffset, piScreenHeight, piScreenWidth);
            return;
        }

        // Draw the new border
        blackColor = Color.BLACK;
        graphics2D.setColor(blackColor);

        ScreenVertex firstVtx;
        ScreenVertex nextVtx;
        int index;

        if(mCurrentShape.getNumFaces() == 0) {
            int iFirstx = (int)mCurrentShape.mCurrentVertex.sx;
            int iFirsty = (int)(piScreenHeight - mCurrentShape.mCurrentVertex.sy);
            iPt1X = iFirstx + iXOffset;
            iPt1Y = iFirsty - iYOffset;
            for (index = 1; index < mCurrentShape.getNumVertices(); index++) {
                // currentShape.currentVertex++;
                mCurrentShape.incCurrentVertex();
                iPt2X = (int)mCurrentShape.mCurrentVertex.sx + iXOffset;
                iPt2Y = piScreenHeight - (int)mCurrentShape.mCurrentVertex.sy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
            } // for index
            
            // Now draw a line from the last point to the first point.
            graphics2D.drawLine(iPt2X, iPt2Y, iFirstx + iXOffset, iFirsty - iYOffset);
        } else {  // the model has faces
            mCurrentShape.initCurrentFace();
            for (index = 1; index <= mCurrentShape.getNumFaces(); index++) {
                firstVtx = new ScreenVertex();
                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i1, firstVtx);

                nextVtx = new ScreenVertex();
                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i2, nextVtx);

                iPt1X = firstVtx.iSx + iXOffset;
                iPt1Y = piScreenHeight - firstVtx.iSy - iYOffset;
                iPt2X = nextVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - nextVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i3, nextVtx);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - nextVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i4, nextVtx);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - nextVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                // Now draw a line from the last point to the first point.
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = firstVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - firstVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                // currentShape.currentFace++;
                mCurrentShape.incCurrentFace();
            } // for index
        }

        // Display the model's name
        float fX, fY;
        fX = mCurrentShape.averageX() + mCurrentShape.mfMinX;
        fY = piScreenHeight - (mCurrentShape.averageY() + mCurrentShape.mfMinY);
        graphics2D.setColor(Color.RED); // red
        graphics2D.drawString(psModelName, (int)fX + iXOffset, (int)fY - iYOffset);

        // Put the frame number on the screen
        String sFrameString = piFrameCounter + "";

        // 10, 10 refers to screen coordinates
        graphics2D.drawString(sFrameString, 10, 10);
        graphics2D.setColor(oldColor);
    } // drawSequence


    // TODO: a method?
    // Called from:
    //     SceneList.previewStill
    // which in turn is called from ImageView.onDraw
    public void drawStill(BufferedImage pBuffImg, String psModelName, 
    int piScreenHeight, int piScreenWidth) {
        Graphics2D graphics2D = pBuffImg.createGraphics();
        boolean bHighlightVertices = false;  // Controls whether vertices are highlighted

        int iXOffset = piScreenWidth / 2;
        int iYOffset = piScreenHeight / 2;
        
        if (miModelType == JICTConstants.I_QUADMESH) {
            previewMesh(graphics2D, psModelName, iXOffset, iYOffset, piScreenHeight, piScreenWidth);
            return;
        }
        
        mCurrentShape.initCurrentVertex();
        if(mCurrentShape.getNumVertices() == 0) {
            Globals.statusPrint("RenderObject.drawStill: No vertices to draw");
            return;
        }

        // float referenceX, referenceY, referenceZ; // these variables are not used
        Color backgroundColor = graphics2D.getBackground();

        int index;
        float fX, fY;
        
        ScreenVertex firstVtx;
        ScreenVertex nextVtx;
        int iPt1X, iPt1Y;
        int iPt2X = 0, iPt2Y = 0;

        mCurrentShape.initCurrentVertex();

        // Draw the new border
        // ROP2 not activated when background plate is not used.
        // so models are visible on grey window background.
        Color origColor = Color.BLACK;
        Color penColor = new Color(0, 200, 0); // green pen

        // If the shape has no faces, the vertices describe a planar element
        // If the shape has faces, draw them
        if(mCurrentShape.getNumFaces() == 0) {
            int iFirstx = (int)mCurrentShape.mCurrentVertex.sx;
            int iFirsty = (int)(piScreenHeight - mCurrentShape.mCurrentVertex.sy);
            if(bHighlightVertices) {
                drawBox(graphics2D, penColor, origColor, 
                    iFirstx + iXOffset, 
                    iFirsty - iYOffset);
            }

            iPt1X = iFirstx + iXOffset;
            iPt1Y = iFirsty - iYOffset;
            for (index = 1; index < mCurrentShape.getNumVertices(); index++) {
                // currentShape.iCurrVtxIdx++;
                mCurrentShape.incCurrentVertex();
                iPt2X = (int)mCurrentShape.mCurrentVertex.sx + iXOffset;
                iPt2Y = piScreenHeight - (int)mCurrentShape.mCurrentVertex.sy - iXOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;

                if(bHighlightVertices) {
                     drawBox(graphics2D, penColor, origColor, 
                        iPt2X, iPt2Y);
                }
            } // for
            
            // Now draw a line from the last point to the first point
            graphics2D.drawLine(iPt2X, iPt2Y, iFirstx + iXOffset, iFirsty - iYOffset);
        } else {  // The model has faces
            mCurrentShape.initCurrentFace();
            for (index = 1; index <= mCurrentShape.getNumFaces(); index++) {
                firstVtx = new ScreenVertex();
                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i1, firstVtx);
              
                if(bHighlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        firstVtx.iSx + iXOffset, 
                        firstVtx.iSy - iYOffset);
                }
                iPt1X = firstVtx.iSx + iXOffset;
                iPt1Y = piScreenHeight - firstVtx.iSy - iYOffset;
              
                nextVtx = new ScreenVertex();
                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i2, nextVtx);
                iPt2X = nextVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - nextVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                if(bHighlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        nextVtx.iSx + iXOffset, 
                        piScreenHeight - nextVtx.iSy - iYOffset);
                }

                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i3, nextVtx);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - nextVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                if(bHighlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        nextVtx.iSx + iXOffset, 
                        piScreenHeight - nextVtx.iSy - iYOffset);
                }

                mCurrentShape.getScreenVertex(mCurrentShape.mCurrentFace.i4, nextVtx);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - nextVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                if(bHighlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        nextVtx.iSx + iXOffset, 
                        piScreenHeight - nextVtx.iSy - iYOffset);
                }

                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = firstVtx.iSx + iXOffset;
                iPt2Y = piScreenHeight - firstVtx.iSy - iYOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                // currentShape.currentFace++;
                mCurrentShape.incCurrentFace();
            } // for index
        } // if

        // Display the model's name
        fX = mCurrentShape.averageX() + mCurrentShape.mfMinX;
        fY = piScreenHeight - (mCurrentShape.averageY() + mCurrentShape.mfMinY);
        graphics2D.setColor(Color.RED);   // red pen
        graphics2D.setBackground(backgroundColor); 
        if(!psModelName.equals(" ")) {
            graphics2D.drawString(psModelName, (int)fX + iXOffset, (int)fY - iYOffset);
        }
    } // drawStill


    // TODO: Not a method of RenderObject in the original C++ code
    // Not called from within this file.
    // Could not find where this is called from.
    /*
    public void setPalette() {
        LPLOGPALETTE myPalette = 0;
        HPALETTE hNewPal;
        int i, nColors = 256;

        // Allocate and lock memory for palette
        myPalette = (LPLOGPALETTE)GlobalLock(GlobalAlloc(GHND, sizeof(LOGPALETTE) + ((nColors - 1) * sizeof(PALETTEENTRY))));
        myPalette.palNumEntries = nColors;  // fill in size and
        myPalette.palVersion = 0x0300;  // version (3.1)
        for( i = 0; i < nColors; i++ ) {  // set the colors
            myPalette.palPalEntry[i].peRed   = i;  // Monochrome RGB palette
            myPalette.palPalEntry[i].peGreen = i;
            myPalette.palPalEntry[i].peBlue  = i;
            myPalette.palPalEntry[i].peFlags = 0;
        }

        hNewPal = CreatePalette(myPalette);

        // Activate the new palette
        SelectPalette(hNewPal, false);
        RealizePalette();
    } // setPalette
    */


    // TODO: Not a method of RenderObject in the original C++ code
    // Not called from within this file
    // Could not find where this is called from.
    //
    // Globals.java has 3 insertion methods
    // public static void insertionSort2(float theItems[], SceneElement itemData[], int numItems)
    // public static void insertionSort(int theItems[], float itemData1[], float itemData2[], float itemData3[], int numItems)
    // public static void insertionSort(int theItems[], int itemData1[], float itemData2[], float itemData3[], float itemData4[], int numItems)
    void insertionSort(int paiItems[], int piNumItems) {
        int iTemp;
        int i, iIdxTmp, iValue;

        for(i = 0; i < piNumItems; i++) {
            // Try to find the right index iIdxTmp at which to store iTemp
            iTemp = paiItems[i];
            iValue = paiItems[i];
            for(iIdxTmp = i; iIdxTmp > 0; iIdxTmp-- ) {
                if( paiItems[iIdxTmp - 1] > iValue ) {
                    paiItems[iIdxTmp] = paiItems[iIdxTmp - 1];
                } else {
                    break;
                }
            }
            paiItems[iIdxTmp] = iTemp;
        }
    } // insertionSort


    // TODO: Not a method of RenderObject in the original C++ code
    // Could not find where this method is called from.
    //
    // Shapes3d.java also has a removeDuplicates method:
    // public int removeDuplicates()
    // Globals.java also has two removeDuplicates method:
    // public static int removeDuplicates(int theList[], float theItemData1[], float theItemData2[], float theItemData3[], Integer listLength)
    // public static int removeDuplicates(int theList[], int theItemData1[], float theItemData2[], float theItemData3[], float theItemData4[], Integer listLength)
    void removeDuplicates(int paiList[], int piListLength) {
        int iNumVertices = piListLength;

        if ((iNumVertices / 2) * 2 == iNumVertices) {
            return;
        }
        if (piListLength <= 2) {
            return;
        }

        for (int index = 0; index + 1 < piListLength; index++) {
            if (paiList[index + 1] == paiList[index]) {
                for (int index2 = index; index2 < piListLength; index2++) {
                    paiList[index2] = paiList[index2 + 1];
                }

                piListLength--;
                index--;
            }
        }
    } // removeDuplicates


    // TODO: Not a method of RenderObject in the original C++ code
    //
    // See p 187 - 195 and 324 - 327 of the book 
    // Visual Special Effects Toolkit in C++
    //
    // Called from:
    //     ImageView.onLButtonDblClk
    public static int prepareCutout(Shape3d pShape, BufferedImage pBuffImg, 
    String psImageFileName, String psCutoutName, 
    int piImageWidth, int piImageHeight) {
        // Creates a cutout image, alpha image, and shape file from
        // a boundary traced by the user
        int iNumVertices = pShape.getNumVertices();

        // Variable thePoints will later be used as a parameter to MemImage.drawMask,
        // where it will then be used as a parameter to the Polygon function
        Point2d[] thePoints = new Point2d[iNumVertices];
        int iPointsIdx = 0;

        pShape.initCurrentVertex();
        // Create the points for a polygon that will become a cutout boundary
        for(int myIndex = 0; myIndex < iNumVertices; myIndex++) {
            thePoints[iPointsIdx].x = pShape.mCurrentVertex.x;
            thePoints[iPointsIdx].y = pShape.mCurrentVertex.y;
            // aShape.currentVertex++;
            pShape.incCurrentVertex();
            iPointsIdx++;
        }

        // Create a 1-bit Windows .bmp file named "OneBit.bmp"
        MemImage maskMImage = new MemImage("OneBit.bmp", 
            piImageHeight, piImageWidth, 
            JICTConstants.I_RANDOM, 'W', JICTConstants.I_ONEBITMONOCHROME);
        if(!maskMImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: Couldn't create 1 bit mask image");
            return 1;
        }

        // drawMask calls graphics2D.drawPolygon
        int iStatus = maskMImage.drawMask(thePoints, iNumVertices);
        if (iStatus != 0) {
            String sMsgText = "RenderObject.prepareCutout: Couldn't create 1 bit mask " + iStatus;
            Globals.statusPrint(sMsgText);
            maskMImage.close();
            return 2;
        }

        // Create an 8 bit mask image
        Globals.statusPrint("RenderObject.prepareCutout: Unpacking Mask Image...");
        MemImage unpackedMaskMImage = new MemImage(piImageHeight, piImageWidth);
        if(!unpackedMaskMImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: Not Enough memory to create unpacked mask image");
            return 1;
        }
        // Unpack the 1-bit Windows bitmap to create an 8-bit Windows bitmap
        maskMImage.unPack(unpackedMaskMImage);
        if(!unpackedMaskMImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: unpack image operation was aborted");
            return 1;
        }
        maskMImage.close();
        // Delete the file we created earlier for maskImage
        FileUtils.deleteFile("OneBit.bmp");
        
        Globals.statusPrint("RenderObject.prepareCutout: Removing borders from mask image...");
        MemImage originalMImage = new MemImage(psImageFileName, 0, 0, 
            JICTConstants.I_SEQUENTIAL, 'R', 0);
        if(!originalMImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: Unable to open original image");
            return 1;
        }

        // The following calls Shape3d.writeShape(sShapeName) to save the shape file
        iStatus = Globals.createCutout(originalMImage, unpackedMaskMImage, psCutoutName, pShape);
        if(iStatus != 0) {
            Globals.statusPrint("RenderObject.prepareCutout: Unable to prepare mask and image cutouts");
            return 1;
        }

        return 0;
    } // prepareCutout


    // TODO: Not a method of RenderObject in the original C++ code
    // Called from:
    //     Globals.tweenImage
    public static int maskFromShape(Shape3d pInShape, MemImage pMaskMImage) {
        // Create a mask image from a 2D boundary.
        // The generated image is displayed on the Cmainframe window.
        // This function could be modified use a memory DC instead.
        int iImageHeight = pMaskMImage.getHeight();
        int iImageWidth  = pMaskMImage.getWidth();
        int iNumVertices = pInShape.getNumVertices();
        
        // CWnd theWindow = AfxGetMainWnd();
        // HWND HWindow = theWindow.m_hWnd;

        // Copy the shape vertices into a structure compatible with the Windows
        // drawing functions

        // Variable thePoints will be used later as a parameter to MemImage.drawMask, 
        // where it will then be used as a parameter to the Polygon function
        Point2d[] thePoints = new Point2d[iNumVertices];
        int thePointsIdx = 0;
        pInShape.initCurrentVertex();

        // Populate array thePoints. This array will later be used as a parameter
        // to MemImage.drawMask, where it will be used to draw a polygon.
        for(int myIndex = 0; myIndex < iNumVertices; myIndex++) {
            thePoints[thePointsIdx].x = pInShape.mCurrentVertex.x;
            thePoints[thePointsIdx].y = pInShape.mCurrentVertex.y;
            // inShape.currentVertex++;
            pInShape.incCurrentVertex();
            thePointsIdx++;
        }

        MemImage tempMaskMImage = new MemImage("OneBit.bmp", 
            iImageHeight, iImageWidth,
            JICTConstants.I_RANDOM, 'W', JICTConstants.I_ONEBITMONOCHROME);  //Memory, write, 1 bit
        if(!tempMaskMImage.isValid()) {
            Globals.statusPrint("RenderObject.maskFromShape: Couldn't create 1 bit mask image");
            return 1;
        }

        // Use the Graphics2D.drawPolygon method to draw the points
        int iStatus = tempMaskMImage.drawMask(thePoints, iNumVertices);

        if (iStatus != 0) {
            String sMsgText = "RenderObject.maskFromShape: Couldn't create 1 bit mask " + iStatus;
            Globals.statusPrint(sMsgText);
            tempMaskMImage.close();
            return 2;
        }

        // Create an unpacked (8 bit) mask image
        // statusPrint("maskFromShape: Unpacking Mask Image...");
        tempMaskMImage.unPack(pMaskMImage);
        FileUtils.deleteFile("OneBit.bmp");

        return 0;
    } // maskFromShape


    // Called from:
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.preview
    //     SceneList.previewStill
    public boolean isValid() {
        return this.mbValid;
    } // isValid


    // TODO: Not a method of RenderObject in the original C++ code
    // Called from:
    //     Ctor that takes 4 parameters: a String, int, boolean and Point3d
    void assembleName(String psInputName, char pcSuffix, StringBuffer psbOutputName) {
        File inputName = new File(psInputName);

        // sFileWExt = file name with extension at end of path psInputName
        String sFileWExt = inputName.getName();
        // Now strip the extension from sFileWExt
        String sBaseFile = sFileWExt.substring(0, sFileWExt.lastIndexOf('.'));
        String sExt = sFileWExt.substring(sFileWExt.lastIndexOf('.'));

        int iFileNameLength = sBaseFile.length();
        if(iFileNameLength > 0) {
            char[] charArray = new char[1];
            charArray[0] = pcSuffix;
            sBaseFile.concat(new String(charArray)); // Substitute a suffix
        }

        // Set the output parameter psOutputName
        String sParent = inputName.getParent();
        psbOutputName.append(sParent);
        psbOutputName.append(sBaseFile).append(sExt);
    } // assembleName


    // Draws the boundary of each quadrilateral in the mesh to produce a 
    // wireframe view of the model.
    // See p 172 of Visual Special Effects Toolkit in C++.
    // Called from:
    //     drawStill
    private void previewMesh(Graphics2D pGraphics2D, String psModelName, 
    float pfXOff, float pfYOff, // are these 2 parameters used?
    int piScreenHeight, int piScreenWidth) {
        // Create the line buffer data structure
        int[] xBuffer, yBuffer;
        int xBufferIdx, yBufferIdx;
        byte[] iBuffer;
        int iBufferIdx;
        int iPrev1Idx = 0, iPrev2Idx = 0;
      
        byte iTemp1 = (byte)0, iTemp2;
        int xTemp1 = 0, yTemp1 = 0, xTemp2, yTemp2;

        int xPrev1Idx = 0, yPrev1Idx = 0;
        int xPrev2Idx = 0, yPrev2Idx = 0;
        int sxMin = 0, sxMax = 0, syMin = 0, syMax = 0;	 // Projected mesh bounding box

        if (
        !mXImage.isValid() ||
        !mTextureImage.isValid() ||
        !mYImage.isValid() ||
        !mZImage.isValid()) {
            Globals.statusPrint("RenderObject.previewMesh: One or more poly-mesh image is not valid");
            return;
        }

        // Later in a double for loop, buffer xBuffer will be populated
        xBuffer = new int[mXImage.getWidth()];
        /* Dead code, per compiler
        if (xBuffer == null) {
            Globals.statusPrint("RenderObject.previewMesh: Not enough memory for xBuffer");
            return;
        }
        */

        // Later in a double for loop, buffer yBuffer will be populated
        yBuffer = new int[mYImage.getWidth()];
        /* Dead code, per compiler 
        if (yBuffer == null) {
            Globals.statusPrint("RenderObject.previewMesh: Not enough memory for yBuffer");
            return;
        }
        */

        // Later in a double for loop, buffer iBuffer will be populated
        iBuffer = new byte[mTextureImage.getWidth()];
        /* Dead code, per compiler
        if (iBuffer == null) {
            Globals.statusPrint("RenderObject.previewMesh: Not enough memory for iBuffer");
            return;
        }
        */

        xBufferIdx = 0;
        yBufferIdx = 0;
        iBufferIdx = 0;

        pGraphics2D.setColor(Color.BLACK);

        // int xOffset = (int)(xOff + 0.5f); // this variable is not used
        // int yOffset = (int)(yOff + 0.5f); // this variable is not used

        int imHeight = mTextureImage.getHeight();
        int imWidth  = mTextureImage.getWidth();
        int row, col;
        float x1, y1, z1;
        float tx = 0.0f, ty = 0.0f, tz = 0.0f;
        byte i1;
        Integer sx1 = 0, sy1 = 0;
        Float refX = 0f, refY = 0f, refZ = 0f;
        int meshIncrement = 6;

        // Get the model's referencePoint
        mCurrentShape.getReferencePoint(refX, refY, refZ);

        // Preview the mesh
        for (row = 1; row <= imHeight; row += meshIncrement) {
            for (col = 1; col <= imWidth; col += meshIncrement) {
                x1 = mXImage.getMPixel32(col, row);
                y1 = mYImage.getMPixel32(col, row);
                z1 = mZImage.getMPixel32(col, row);
                i1 = mTextureImage.getMPixel(col, row);

                // Project to the screen
                mMatrix.transformAndProjectPoint(x1, y1, z1, 
                    sx1, sy1, 
                    refX, refY, refZ, 
                    piScreenHeight, piScreenWidth, 
                    tx, ty, tz);
                
                if((row == 1) && (col == 1)) {	// Initialize the projected mesh bounding box
                    sxMin = sxMax = sx1;
                    syMin = syMax = sy1;
                }
                
                if(row == 1) {	 // Load up the x, y, and iTemp buffers on the first row
                    xBuffer[xBufferIdx] = sx1;
                    xBufferIdx++;

                    yBuffer[yBufferIdx] = sy1;
                    yBufferIdx++;

                    iBuffer[iBufferIdx] = i1;
                    iBufferIdx++;

                    if (sx1 < sxMin) sxMin = sx1;
                    if (sx1 > sxMax) sxMax = sx1;
                    if (sy1 < syMin) syMin = sy1;
                    if (sy1 > syMax) syMax = sy1;
                }
                
                if ((row > 1) && (col == 1)) {  // First column of every row after the first row
                    xTemp1 = sx1;
                    yTemp1 = sy1;

                    xPrev1Idx = 0;
                    yPrev1Idx = 0;

                    xPrev2Idx = 0;
                    yPrev2Idx = 0;

                    xPrev2Idx++;
                    yPrev2Idx++;
                    iTemp1 = i1;
                    
                    iPrev1Idx = 0;
                    iPrev2Idx = 0;
                    iPrev2Idx++;

                    if (sx1 < sxMin) sxMin = sx1;
                    if (sx1 > sxMax) sxMax = sx1;
                    if (sy1 < syMin) syMin = sy1;
                    if (sy1 > syMax) syMax = sy1;
                }

                // After the first row and after the first column:
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;  

                    // Draw only if the texture is not transparent 
                    if((iPrev2Idx != 0) && (iTemp2 != 0) && (iTemp1 != 0)) {
                          if(row == (meshIncrement + 1)) {   // draw the first row
                              pGraphics2D.drawLine(
                                  xPrev1Idx, piScreenHeight - (yPrev1Idx),
                                  xPrev2Idx, piScreenHeight - (yPrev2Idx));
                          }
                          if(col == (meshIncrement + 1)) {  // draw the first column
                              pGraphics2D.drawLine(
                                  xPrev1Idx, piScreenHeight - (yPrev1Idx),
                                  xTemp1, piScreenHeight - yTemp1);
                          }

                          pGraphics2D.drawLine(
                              xPrev2Idx, piScreenHeight - (yPrev2Idx),
                              xTemp2, piScreenHeight - yTemp2);
                          pGraphics2D.drawLine(
                              xTemp2, piScreenHeight - yTemp2,
                              xTemp1, piScreenHeight - yTemp1);
                    }

                    xBuffer[xPrev1Idx] = xTemp1;
                    yBuffer[yPrev1Idx] = yTemp1;
                    iBuffer[iPrev1Idx] = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1Idx++;
                    yPrev1Idx++;
                    iPrev1Idx++;

                    xPrev2Idx++;
                    yPrev2Idx++;
                    iPrev2Idx++;

                    if (sx1 < sxMin) sxMin = sx1;
                    if (sx1 > sxMax) sxMax = sx1;
                    if (sy1 < syMin) syMin = sy1;
                    if (sy1 > syMax) syMax = sy1;
                }
            } // for col
        } // for row

        // Display the name in the center of the projected model
        int iXText = sxMin + ((sxMax - sxMin)/2);
        int iYText = piScreenHeight - (syMin + ((syMax - syMin)/2));
        pGraphics2D.setColor(Color.RED); // red
        pGraphics2D.drawString(psModelName, iXText, iYText);
    } // previewMesh


    // Renders quadrilateral mesh models.
    // See p 172 of Visual Special Effects Toolkit in C++.
    // Class Globals also has a renderMesh method, but that one takes 
    // 6 parameters: a String, 4 MemImage ojbects, and a TMatrix.
    // Called from:
    //     SceneList.render
    public int renderMesh(MemImage pOutputMImage, MemImage pInputMImage, 
    boolean pbBlendIndicator) { // parameter pbBlendIndicator is not used
        // Create the line buffer data structure
        int[] xBuffer, yBuffer;
        int xBufferIdx, yBufferIdx;
        byte[] iBuffer;
        int iBufferIdx;
        int iTemp1 = 0, iTemp2 = 0;
        int iPrev1Idx = 0, iPrev2Idx = 0;
        int xTemp1 = 0, yTemp1 = 0;
        int xTemp2 = 0, yTemp2 = 0;
        int xPrev1Idx = 0, yPrev1Idx = 0;
        int xPrev2Idx = 0, yPrev2Idx = 0;
        // char msgBuffer[80]; // not used

        if (
        !mXImage.isValid() ||
        !mYImage.isValid() ||
        !mZImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMesh: One or more poly-mesh image is not valid");
            return -1;
        }

        xBuffer = new int[mXImage.getWidth()];
        /* Dead code, per the compiler
        if (xBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for xBuffer");
            return -1;
        }
        */

        yBuffer = new int[mYImage.getWidth()];
        /* Dead code, per the compiler
        if (yBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for yBuffer");
            return -1;
        }
        */

        iBuffer = new byte[pInputMImage.getWidth()];
        /* Dead code, per the compiler
        if (iBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for iBuffer");
            return -1;
        }
        */

        xBufferIdx = 0;
        yBufferIdx = 0;
        iBufferIdx = 0;

        int imHeight  = pInputMImage.getHeight();
        int imWidth   = pInputMImage.getWidth();
        int outHeight = pOutputMImage.getHeight();
        int outWidth  = pOutputMImage.getWidth();
        int row, col;
        float x1, y1, z1;
        Float tx = 0f, ty = 0f, tz = 0f;
        byte i1;
        Integer sx1 = 0, sy1 = 0;
        Float refX = 0f, refY = 0f, refZ = 0f;
        // int meshIncrement = 6; // variable not used

        // Get the model's referencePoint
        mCurrentShape.getReferencePoint(refX, refY, refZ);

        // Render the mesh
        mMatrix.display("RenderObject.renderMesh");

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = mXImage.getMPixel32(col, row);
                y1 = mYImage.getMPixel32(col, row);
                z1 = mZImage.getMPixel32(col, row);
                i1 = pInputMImage.getMPixel(col, row);

                // Project to the screen
                mMatrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                    refX, refY, refZ, 
                    outHeight, outWidth, 
                    tx, ty, tz);
                if(row == 1) {
                    xBuffer[xBufferIdx] = sx1;
                    xBufferIdx++;

                    yBuffer[yBufferIdx] = sy1;
                    yBufferIdx++;

                    iBuffer[iBufferIdx] = i1;
                    iBufferIdx++;
                }
              
                // int xTemp1, yTemp1, xTemp2, yTemp2;
                // int *xPrev1, *yPrev1, *xPrev2, *yPrev2;
                // byte iTemp1, iTemp2, *iPrev1, *iPrev2;
                if ((row > 1) && (col == 1)) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1Idx = 0;
                    yPrev1Idx = 0;

                    xPrev2Idx = 0;
                    yPrev2Idx = 0;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx = 0;
                    iPrev2Idx = 0;
                    iPrev2Idx++;
                }
      
                // int xTemp1, yTemp1, xTemp2, yTemp2;
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;
           
                    // Render the quadrangle
                    pOutputMImage.fillPolyz(
                        xPrev1Idx, yPrev1Idx, iPrev1Idx, 0.0f,
                        xPrev2Idx, yPrev2Idx, iPrev2Idx, 0.0f,
                        xTemp2,    yTemp2,    iTemp2,    0.0f,
                        xTemp1,    yTemp1,    iTemp1,    0.0f, 
                        null); 

                    xPrev1Idx = xTemp1;
                    yPrev1Idx = yTemp1;
                    iPrev1Idx = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1Idx++;
                    yPrev1Idx++;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx++;
                    iPrev2Idx++;
                } // if ((row > 1) && (col > 1))
            } // for col
        } // for row

        return 0;
    } // renderMesh


    // Called from:
    //     SceneList.render
    public int renderMeshz(MemImage pOutputMImage, 
    MemImage pMaskMImage, // this parameter is not used
    MemImage pInputMImage, MemImage pZBuffMImage, float pfVx, float pfVy, float pfVz) {
        String msgText;

        //  create the line buffer data structures
        int[] xBuffer, yBuffer;
        int xBufferIdx, yBufferIdx; // formerly int *xTemp, *yTemp;
        // float *wxBuffer, *wyBuffer, *wzBuffer; // these variables are not used
        float[] dBuffer;
        int dBufferIdx; // formerly *dTemp,
        // float *wxTemp, *wyTemp, *wzTemp; // these variables are not used
        byte[] iBuffer;
        int iBufferIdx; // formerly byte *iTemp;
        byte iTemp1 = (byte)0, iTemp2 = (byte)0;
        int iPrev1Idx = 0, iPrev2Idx = 0;
        int xTemp1 = 0, yTemp1 = 0, xTemp2 = 0, yTemp2 = 0;
        int xPrev1Idx = 0, yPrev1Idx = 0, xPrev2Idx = 0, yPrev2Idx = 0;
        float dTemp1 = 0.0f, dTemp2 = 0.0f;
        int dPrev1Idx = 0, dPrev2Idx = 0;

        if (
        !mXImage.isValid() ||
        !mYImage.isValid() ||
        !mZImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMeshz: One or more poly-mesh image is not valid");
            return -1;
        }

        xBuffer = new int[mXImage.getWidth()];
        /* Dead code, per compiler
        if (xBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for xBuffer");
            return -1;
        }
        */

        yBuffer = new int[mYImage.getWidth()];
        /* Dead code, per compiler
        if (yBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for yBuffer");
            return -1;
        }
        */

        dBuffer = new float[mZImage.getWidth()];
        /* Dead code, per compiler
        if (dBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for distance Buffer");
            return -1;
        }
        */

        iBuffer = new byte[pInputMImage.getWidth()];
        /* Dead code, per compiler
        if (iBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for iBuffer");
            return -1;
        }
        */

        MemImage midImage = new MemImage(pOutputMImage.getHeight(), pOutputMImage.getWidth());
        if(!midImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMeshZ: Not enough memory to open intermediate image");
            return -1;
        }

        midImage.setFileName("midImage");
        MemImage midZImage = new MemImage(pOutputMImage.getHeight(), pOutputMImage.getWidth(), 32);
        if(!midZImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMeshZ: Not enough memory to open intermediate Z image");
            return -1;
        }

        midZImage.setFileName("midZImage");
        midZImage.init32(JICTConstants.F_ZBUFFERMAXVALUE);
        int imHeight  = pInputMImage.getHeight();
        int imWidth   = pInputMImage.getWidth();
        int outHeight = pOutputMImage.getHeight();
        int outWidth  = pOutputMImage.getWidth();

        //  Temporary - for testing
        //pfVx = (float)outWidth/2.0f;
        //pfVy = (float)outHeight/2.0f;
        //pfVz = 512.0f;

        msgText = String.format("RenderObject.renderMeshz: Viewer location - vx: %f, vy: %f, vz: %f", pfVx, pfVy, pfVz);
        Globals.statusPrint(msgText);
        xBufferIdx = 0;
        yBufferIdx = 0;
        iBufferIdx = 0;
        dBufferIdx = 0;

        int row, col;
        float x1, y1, z1;
        byte i1;
        Integer sx1 = 0, sy1 = 0;
        Float refX = 0f, refY = 0f, refZ = 0f;
        Point3d c1 = new Point3d();
        Point3d c2 = new Point3d();
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d centroid = new Point3d(); 
        Point3d lightSource = new Point3d();  // A lightsource location
        lightSource.x = 100.0f;
        lightSource.y = 255.0f;
        lightSource.z =  20.0f;
        Vect.vectorNormalize(lightSource);

        Point3d np1 = new Point3d();
        Point3d np2 = new Point3d();
        Point3d nc1 = new Point3d();
        Point3d nc2 = new Point3d();;

        // Get the model's referencePoint
        mCurrentShape.getReferencePoint(refX, refY, refZ);
        float tx = 0.0f, ty = 0.0f, tz = 0.0f;

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = mXImage.getMPixel32(col, row);
                y1 = mYImage.getMPixel32(col, row);
                z1 = mZImage.getMPixel32(col, row);
                i1 = pInputMImage.getMPixel(col, row);

                // Project to the screen
                mMatrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                    refX, refY, refZ, 
                    outHeight, outWidth, 
                    tx, ty, tz);

                if(row == 1) {
                    xBuffer[xBufferIdx] = sx1;
                    xBufferIdx++;

                    yBuffer[yBufferIdx] = sy1;
                    yBufferIdx++;

                    iBuffer[iBufferIdx] = i1;
                    iBufferIdx++;

                    dBuffer[dBufferIdx] = MathUtils.getDistance3d(tx, ty, tz, pfVx, pfVy, pfVz);
                    dBufferIdx++;
                }
              
                if (row > 1 && col == 1) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1Idx = 0;
                    yPrev1Idx = 0;

                    xPrev2Idx = 0;
                    yPrev2Idx = 0;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx = 0;
                    iPrev2Idx = 0;
                    iPrev2Idx++;

                    dPrev1Idx = 0;
                    dPrev2Idx = 0;
                    dPrev2Idx++;

                    dTemp1 = MathUtils.getDistance3d(tx, ty, tz, pfVx, pfVy, pfVz);
                }
      
                if (row > 1 && col > 1) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;

                    dTemp2 = MathUtils.getDistance3d(tx, ty, tz, pfVx, pfVy, pfVz);
                                  
                    // Render the quadrangle intensities
                    //                     
                    // Render the quadrangle distances and update the intermediate zBuffer

                    ///////////////////////////////////////////////
                    //  Gouraud Shading
                    ///////////////////////////////////////////////
                    boolean shading = false;
                    if(shading) {
                        c2.x = mXImage.getMPixel32(col, row);
                        c2.y = mYImage.getMPixel32(col, row);
                        c2.z = mZImage.getMPixel32(col, row);
          
                        c1.x = mXImage.getMPixel32(col-1, row);
                        c1.y = mYImage.getMPixel32(col-1, row);
                        c1.z = mZImage.getMPixel32(col-1, row);
                  
                        p1.x = mXImage.getMPixel32(col-1, row-1);
                        p1.y = mYImage.getMPixel32(col-1, row-1);
                        p1.z = mZImage.getMPixel32(col-1, row-1);
          
                        p2.x = mXImage.getMPixel32(col, row-1);
                        p2.y = mYImage.getMPixel32(col, row-1);
                        p2.z = mZImage.getMPixel32(col, row-1);

                        float xMax = c1.x;
                        float yMax = c1.y;
                        float zMax = c1.z;
                        float xMin = c1.x;
                        float yMin = c1.y;
                        float zMin = c1.z;

                        // Get the 3D bounding box
                        if(c2.x > xMax) xMax = c2.x;
                        if(p1.x > xMax) xMax = p1.x;
                        if(p2.x > xMax) xMax = p2.x;

                        if(c2.x < xMin) xMin = c2.x;
                        if(p1.x < xMin) xMin = p1.x;
                        if(p2.x < xMin) xMin = p2.x;

                        if(c2.y > yMax) yMax = c2.y;
                        if(p1.y > yMax) yMax = p1.y;
                        if(p2.y > yMax) yMax = p2.y;

                        if(c2.y < yMin) yMin = c2.y;
                        if(p1.y < yMin) yMin = p1.y;
                        if(p2.y < yMin) yMin = p2.y;

                        if(c2.z > zMax) zMax = c2.z;
                        if(p1.z > zMax) zMax = p1.z;
                        if(p2.z > zMax) zMax = p2.z;

                        if(c2.z < zMin) zMin = c2.z;
                        if(p1.z < zMin) zMin = p1.z;
                        if(p2.z < zMin) zMin = p2.z;

                        // Use the 3D bounding box to calculate the point centroid
                        centroid.x = (xMax + xMin) / 2.0f;
                        centroid.y = (yMax + yMin) / 2.0f;
                        centroid.z = (zMax + zMin) / 2.0f;

                        // Get the 3-dimensional distance between points lightSource and centroid
                        float dCentroid = MathUtils.getDistance3d(
                            lightSource.x, lightSource.y, lightSource.z, 
                            centroid.x,    centroid.y,    centroid.z);

                        // Calculate the normals np1, np2, nc1 and nc2
                        Vect.getNormal2(np1, p1, centroid, p2);
                        Vect.getNormal2(np2, p2, centroid, c2);
                        Vect.getNormal2(nc1, c1, centroid, c2);
                        Vect.getNormal2(nc2, p1, centroid, c1);

                        // Now that we have the normals np1, np2, nc1, and nc2, 
                        // we scale them to have unit length
                        Vect.vectorNormalize(np1);
                        Vect.vectorNormalize(np2);
                        Vect.vectorNormalize(nc1);
                        Vect.vectorNormalize(nc2);

                        // kd     the coefficient of reflection or reflectivity of the surface material
                        //        highly reflective = 1, highly absorptive = 0
                        // Ip	  the intensity of the light source
                        // Ia     the ambient intensity at the surface
                        // N      The surface Normal (unit vector)
                        // L      The direction of the light source (unit vector)
                        // d      the distance between the surface and the light source
                        float kd = 0.95f;
                        int Ip = 200;
                        float ip2, ic2;
                        // float ip1, ic1; // The value of these variables are not used

                              Globals.lightModel(kd, Ip, 100, np1, lightSource, dCentroid);
                        ip2 = Globals.lightModel(kd, Ip, 100, np2, lightSource, dCentroid);
                              Globals.lightModel(kd, Ip, 100, nc1, lightSource, dCentroid);
                        ic2 = Globals.lightModel(kd, Ip, 100, nc2, lightSource, dCentroid);

                        midImage.fillPolyz( 
                            xPrev1Idx, yPrev1Idx, ip2, dPrev1Idx,
                            xPrev2Idx, yPrev2Idx, ip2, dPrev2Idx,
                            xTemp2,    yTemp2,    ic2, dTemp2,
                            xTemp1,    yTemp1,    ic2, dTemp1, 
                            midZImage);
                        // end shading
                    } else {
                        midImage.fillPolyz( 
                            xPrev1Idx, yPrev1Idx, iPrev1Idx, dPrev1Idx,
                            xPrev2Idx, yPrev2Idx, iPrev2Idx, dPrev2Idx,
                            xTemp2,    yTemp2,    iTemp2,    dTemp2,
                            xTemp1,    yTemp1,    iTemp1,    dTemp1, 
                            midZImage);
                    }

                    xPrev1Idx = xTemp1;
                    yPrev1Idx = yTemp1;
                    iPrev1Idx = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1Idx++;
                    yPrev1Idx++;

                    xPrev2Idx++;
                    yPrev2Idx++;

                    iPrev1Idx++;
                    iPrev2Idx++;

                    dBuffer[dPrev1Idx] = dTemp1;
                    dTemp1 = dTemp2;
                    dPrev1Idx++;
                    dPrev2Idx++;
                } // if (row > 1 && col > 1)
            } // for col
        } // for row

        //
        // Composite the rendered quad mesh into the output scene
        //

        // First, create the matte image
        MemImage matte = new MemImage(pOutputMImage.getHeight(), pOutputMImage.getWidth());
        midImage.createAlphaImage(matte);
        Globals.statusPrint("RenderObject.renderMeshz: Creating a matte for the rendered quad mesh");
        matte.alphaSmooth5();

        float alphaScale = 1.0f;
        Globals.blendz(midImage, matte, midZImage, pZBuffMImage, pOutputMImage, alphaScale);

        return 0;
    } // renderMeshz


    // Called from:
    //     transformAndProject (the method which takes 3 parameters)
    //     SceneList.previewStill
    public void transformAndProject(TMatrix pTMatrix, 
    int piOutHeight, int piOutWidth,
    boolean pbExternalCentroid,
    float pfCentroidX, float pfCentroidY, float pfCentroidZ) {
        if(
        (this.miModelType == JICTConstants.I_SHAPE) || 
        (this.miModelType == JICTConstants.I_IMAGE)) {
            // The following method modifies parameter mCurrentShape
            pTMatrix.transformAndProject(mCurrentShape, piOutHeight, piOutWidth, 
                pbExternalCentroid,
                pfCentroidX, pfCentroidY, pfCentroidZ);
        } else {
            // Copy the transformation matrix for later use
            mMatrix.copy(pTMatrix);
        }
    } // transformAndProject


    // Called from:
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.preview
    //     SceneList.previewStill
    public void transformAndProject(TMatrix pTMatrix, int piOutHeight, int piOutWidth) {
        this.transformAndProject(pTMatrix, piOutHeight, piOutWidth, 
            false, 
            0.0f, 0.0f, 0.0f);
    } // transformAndProject


    // TODO: Not a method of RenderObject in the original C++ code
    // 
    // Could not find where this method is called from.
    // TODO: Parameters piOutHeight and piOutWidth are not used.
    void transformAndProjectPoint2(TMatrix pTMatrix, 
    float pfX, float pfY, float pfZ, 
    ScreenVertex pScreenVtx, 
    float pfRefX, float pfRefY, float pfRefZ, 
    int piOutHeight, int piOutWidth) {
        // Note that parameters piOutHeight and piOutWidth are not used.
        // Use Wein87 projection, described in the book 
        // Computer Graphcs: Principles and Practice, 2nd ed.,
        // by Foley, van Dam, Feiner and Hughes, p 256
        Float tx = 0.0f, ty = 0.0f, tz = 0.0f;
        pfX -= pfRefX;
        pfY -= pfRefY;
        pfZ -= pfRefZ;
        // The following will set parameters tx, ty and tz
        pTMatrix.transformPoint(pfX, pfY, pfZ, tx, ty, tz);

        // Undo the previous translation by pfRefX, pfRefY and pfRefZ (why?)
        pfX += pfRefX;
        pfY += pfRefY;
        pfZ += pfRefZ;

        // Define the Center of Projection (COP)
        Point3d COP = new Point3d();
        COP.x =    0.0f;
        COP.y =    0.0f;
        COP.z = -512.0f;

        // viewplane (0,0,zp)
        Point3d p = new Point3d();
        p.x = 0.0f;
        p.y = 0.0f;
        p.z = 0.0f;

        Point3d d = new Point3d();
        // Calculate d = COP - p
        Vect.vectorSubtract(d, COP, p);

        // Calculate Q = 3-dimensional distance between points COP and p
        float Q = MathUtils.getDistance3d(COP.x, COP.y, COP.z, p.x, p.y, p.z);
        float fDenom = ((p.z - tz) / (Q * d.z)) + 1.0f;
        float fXp = (tx - (tz * d.x / d.z) + (p.z * d.x / d.z)) / fDenom;
        float fYp = (ty - (tz * d.y / d.z) + (p.z * d.y / d.z)) / fDenom;

        // Set output parameters sx and sy
        pScreenVtx.iSx = (int)(fXp + 0.5f);
        pScreenVtx.iSy = (int)(fYp + 0.5f);
    } // transformAndProjectPoint2


    // Not a method of RenderObject in the original C++ code.
    // However it is only called from within RenderObject, so it makes
    // sense to make it a method of RenderObject.
    // 
    // Called from:
    //     drawStill
    private void drawBox(Graphics2D pGraphics2D, Color pPenColor, Color pOrigColor, int piX, int piY) {
        //  Draw a box 2 * offset + 1 pixels wide and high around the point x,y
        int iOffset = 2;
        int iPt1X, iPt1Y;
        int iPt2X, iPt2Y;
        pGraphics2D.setColor(pPenColor);

        iPt1X = piX - iOffset;
        iPt1Y = piY + iOffset;
        iPt2X = piX + iOffset;
        iPt2Y = piY + iOffset;
        pGraphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        iPt1X = iPt2X;
        iPt1Y = iPt2Y;
        iPt2X = piX + iOffset;
        iPt2Y = piY - iOffset;
        pGraphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        iPt1X = iPt2X;
        iPt1Y = iPt2Y;
        iPt2X = piX - iOffset;
        iPt2Y = piY - iOffset;
        pGraphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        iPt1X = iPt2X;
        iPt1Y = iPt2Y;
        iPt2X = piX - iOffset;
        iPt2Y = piY + iOffset;
        pGraphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        // MoveToEx(x, y); // this doesn't actually draw anything
        pGraphics2D.setColor(pOrigColor);
    } // drawBox


    // Not called from within this file
    // Called from:
    //     SceneList.render
    // TODO: Parameter pbBlendIndicator is not used
    public int renderShape(MemImage pOutputMImage, boolean pbBlendIndicator) {
        if(mCurrentShape.getNumFaces() == 0) {
            Globals.statusPrint("RenderObject.renderShape: Shape has no faces - cannot be rendered");
            return 0;
        }

        ScreenVertex sv1;
        ScreenVertex sv2;
        ScreenVertex sv3;
        ScreenVertex sv4 = new ScreenVertex();
        byte I1p = 0, I2p = 0, I3p = 0, I4p = 0; 
        int index, index1, index2, index3, index4;

        mCurrentShape.initCurrentFace();
        for (index = 1; index <= mCurrentShape.getNumFaces(); index++) {
            index1 = mCurrentShape.mCurrentFace.i1; 
            index2 = mCurrentShape.mCurrentFace.i2; 
            index3 = mCurrentShape.mCurrentFace.i3; 
            index4 = mCurrentShape.mCurrentFace.i4; 

            sv1 = new ScreenVertex();
            mCurrentShape.getScreenVertex(index1, sv1);

            sv2 = new ScreenVertex();
            mCurrentShape.getScreenVertex(index2, sv2);

            sv3 = new ScreenVertex();
            mCurrentShape.getScreenVertex(index3, sv3);

            if (index4 > 0) {
                sv4 = new ScreenVertex();
                mCurrentShape.getScreenVertex(index4, sv4);
            }

            // Draw the face
            if (index4 > 0) {
                pOutputMImage.fillPolyz(
                    sv1.iSx, sv1.iSy, I1p, 0.0f, 
                    sv2.iSx, sv2.iSy, I2p, 0.0f, 
                    sv3.iSx, sv3.iSy, I3p, 0.0f, 
                    sv4.iSx, sv4.iSy, I4p, 0.0f, 
                    null);
            } else { 
                // its a triangle
            }
            // currentShape.currentFace++;
            mCurrentShape.incCurrentFace();
        } // for index

        return 0;
    } // renderShape


    // Not called from within this file
    // Called from:
    //     SceneList.render
    // TODO: Parameter pAlphaMImage is not used
    public int renderShapez(MemImage pOutputMImage, 
    MemImage pAlphaMImage, MemImage pZBufMImage, 
    float vx, float vy, float vz) {
        
        // The shape object is already transformed upon entry to this procedure
        ScreenVertex sv1;
        ScreenVertex sv2;
        ScreenVertex sv3;
        ScreenVertex sv4 = new ScreenVertex();
        byte I1p;
        // byte I2p, I3p, I4p; // these variables are not used
        float I1d, I2d, I3d, I4d;
        int index, index1, index2, index3, index4;
        // int myStatus; // this variable is not used
        Float tx1 = 0f, ty1 = 0f, tz1 = 0f;
        Float tx2 = 0f, ty2 = 0f, tz2 = 0f; 
        Float tx3 = 0f, ty3 = 0f, tz3 = 0f; 
        Float tx4 = 0f, ty4 = 0f, tz4 = 0f;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d p3 = new Point3d();
        Point3d p4 = new Point3d();

        if(mCurrentShape.getNumFaces() == 0) {
            Globals.statusPrint("RenderObject.renderShapez: Shape has no faces - cannot be rendered");
            return 0;
        }
        int yo = pOutputMImage.getHeight() / 2;
        int xo = pOutputMImage.getWidth() / 2;

        mCurrentShape.initCurrentFace();
        for (index = 1; index <= mCurrentShape.getNumFaces(); index++) {
            index1 = mCurrentShape.mCurrentFace.i1; 
            index2 = mCurrentShape.mCurrentFace.i2; 
            index3 = mCurrentShape.mCurrentFace.i3; 
            index4 = mCurrentShape.mCurrentFace.i4; 

            sv1 = new ScreenVertex();
            mCurrentShape.getScreenVertex(index1, sv1);

            sv2 = new ScreenVertex();
            mCurrentShape.getScreenVertex(index2, sv2);

            sv3 = new ScreenVertex();
            mCurrentShape.getScreenVertex(index3, sv3);

            if(index4 > 0) {
                sv4 = new ScreenVertex();
                mCurrentShape.getScreenVertex(index4, sv4);
            }

            // Draw the face
            if(index4 > 0) {
                // The following method sets parameters tx1, ty1, and tz1
                mCurrentShape.getTransformedVertex(index1, tx1, ty1, tz1);

                // The following method sets parameters tx2, ty2, and tz2
                mCurrentShape.getTransformedVertex(index2, tx2, ty2, tz2);

                // The following method sets parameters tx3, ty3, and tz3
                mCurrentShape.getTransformedVertex(index3, tx3, ty3, tz3);

                // The following method sets parameters tx4, ty4, and tz4
                mCurrentShape.getTransformedVertex(index4, tx4, ty4, tz4);

                // I1d, I2d, I3d, and I4d will be used later as parameters to MemImage.fillPolyz
                I1d = MathUtils.getDistance3d(vx, vy, vz, tx1, ty1, tz1);
                I2d = MathUtils.getDistance3d(vx, vy, vz, tx2, ty2, tz2);
                I3d = MathUtils.getDistance3d(vx, vy, vz, tx3, ty3, tz3);
                I4d = MathUtils.getDistance3d(vx, vy, vz, tx4, ty4, tz4);
                p1.x = tx1; p1.y = ty1; p1.z = tz1;
                p2.x = tx2; p2.y = ty2; p2.z = tz2;
                p3.x = tx3; p3.y = ty3; p3.z = tz3;
                p4.x = tx4; p4.y = ty4; p4.z = tz4;
                I1p = Globals.getLight(p1, p2, p3, p4);

                pOutputMImage.fillPolyz(
                    sv1.iSx + xo, sv1.iSy + yo, I1p, I1d,
                    sv2.iSx + xo, sv2.iSy + yo, I1p, I2d, 
                    sv3.iSx + xo, sv3.iSy + yo, I1p, I3d, 
                    sv4.iSx + xo, sv4.iSy + yo, I1p, I4d,
                    pZBufMImage);
            } else { 
                // its a triangle
            }
            // currentShape.currentFace++;
            mCurrentShape.incCurrentFace();
        } // for index

        return 0;
    } // renderShapez
} // class RenderObject