package core;

import fileUtils.FileUtils;

import globals.Globals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import math.MathUtils;
import math.TMatrix;
import math.Vect;

import structs.FaceSet;
import structs.Point2d;
import structs.Point3d;
import structs.VertexSet;

public class RenderObject {
    boolean ictdebug = false;
    public int modelType;		// a copy of the sceneElement model Type
    public Shape3d currentShape;
    public Shape3d lastShape;

    // These memImages contain a quadrilateral mesh model
    protected MemImage textureImage, xImage, yImage, zImage;

    //the transformation matrix
    protected TMatrix m_matrix; 

    // Set to 1 if the renderObject was successfully created
    protected boolean valid;

    // Model Types
    public static final int IMAGE       = 1;
    public static final int SHAPE       = 2;
    public static final int QUADMESH    = 3;
    public static final int COMPOUND    = 4;
    public static final int LIGHTSOURCE = 5;

    // SEQUENTIAL and RANDOM were defined in MEMIMAGE.H
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM     = 0;

    // These were defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;

    // This value came from ICT20.H
    public static final float ZBUFFERMAXVALUE = 2.0E31f;
/*
public:
  renderObject (point3d *UL, point3d *UR, point3d *LR, point3d *LL);

  renderObject (char *fileName, int modelType, int userPOR, point3d *POR);
  
  virtual ~renderObject ();
  void drawSequence(HDC theDC, char *modelName, int screenHeight, int screenWidth,
    int frameCounter);
  void drawStill(HWND theWindow, char *modelName, int screenHeight, int screenWidth);
  
  void previewMesh(HDC theDC, char *modelName,
    float xOff, float yOff, 
	int screenHeight, int screenWidth);
  int renderMesh(memImage *outputImage, memImage *inputImage, int blendIndicator);
  int renderMeshz(memImage *outputImage, memImage *maskImage, 
    memImage *inputImage, memImage *zBuffer,float vx, float vy, 
	float vz);
  int renderShape(memImage *outputImage, int blendIndicator);
  int renderShapez(memImage *outputImage, memImage *alphaImage, memImage *zBuffer, 
      float vx, float vy, float vz);
 
  int shadeMeshz(memImage *outputImage, memImage *maskImage, 
    memImage *inputImage, memImage *zBuffer, 
    float vx, float vy, float vz);
	  
void transformAndProject (tMatrix *aMatrix, int outHeight, int outWidth,
					      int externalCentroid=0,
					      float centroidX=0, float centroidY=0, float centroidZ=0);
  int isValid();
protected:
// These memImages contain a quadrilateral mesh model
  memImage *textureImage, *xImage, *yImage, *zImage;
  tMatrix *m_matrix;    //the transformation matrix
  int valid;          // Set to 1 if the renderObject was successfully created
*/


    public RenderObject(Point3d aUL, Point3d aUR, Point3d aLR, Point3d aLL) {
        this.currentShape = null;
        this.lastShape = null;
        if(ictdebug) {
            String msgBuffer = "RenderObject Constructor 1: Size of renderObject: " + sizeofLowerLimit();
            Globals.statusPrint(msgBuffer);
        }

        this.currentShape = new Shape3d(aUL, aUR, aLR, aLL);
        Float centroidX = 0f, centroidY = 0f, centroidZ = 0f;
        this.currentShape.getWCentroid(centroidX, centroidY, centroidZ);

        // Make certain the shape is centered in the X-Y plane.
        if(
        (centroidX > 0.5f || centroidX < -0.5f) ||
        (centroidY > 0.5f || centroidY < -0.5f) ||
        (centroidZ > 0.5f || centroidZ < -0.5f) ) {
            String msgText = String.format("RenderObject Constructor 1: Centering shape - xCent: %f, yCent: %f zCent: %f",
                centroidX, centroidY, centroidZ);
            Globals.statusPrint(msgText);
            currentShape.translateW(-centroidX, -centroidY, -centroidZ);   
            currentShape.floor();   
            currentShape.getWCentroid(centroidX, centroidY, centroidZ);
        }

        currentShape.setReferencePoint(centroidX, centroidY, centroidZ);
        this.lastShape = new Shape3d(4); // 4 vertex shape with coords set to 0
        this.m_matrix = new TMatrix();
        this.valid = true;
    } // RenderObject ctor


    // Called from:
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.previewStill
    public RenderObject(String fileName, int aModelType, boolean userPOR, Point3d POR) {
        boolean validCurrentShape = true;
        boolean validLastShape = true;
        Float centroidX = 0f, centroidY = 0f, centroidZ = 0f;

        this.currentShape = null;
        this.lastShape = null;
        this.textureImage = null;
        this.xImage = null;
        this.yImage = null;
        this.zImage = null;
        this.modelType = aModelType;  // set data members
        this.m_matrix = new TMatrix();

        if (ictdebug) {
            String msgText;
            msgText = "RenderObject Constructor 2: Size of renderObject: " + sizeofLowerLimit();
            Globals.statusPrint(msgText);
        }
        this.valid = true;

        switch(this.modelType) {
        case QUADMESH:
            String texturePath, xPath = "", yPath = "", zPath = "";
            texturePath = fileName;
            assembleName(texturePath, 'x', xPath);
            assembleName(texturePath, 'y', yPath);
            assembleName(texturePath, 'z', zPath);

            textureImage = new MemImage(fileName, 0, 0, RANDOM, 'R', 0);
            xImage = new MemImage(xPath, 0, 0, RANDOM, 'R', 0);
            yImage = new MemImage(yPath, 0, 0, RANDOM, 'R', 0);
            zImage = new MemImage(zPath, 0, 0, RANDOM, 'R', 0);

            // Make certain the QuadMesh is centered in the X-Y plane.
            Globals.getMeshCentroid(xImage, yImage, zImage, centroidX, centroidY, centroidZ);
            if(
            (centroidX > 0.5f || centroidX < -0.5f) ||
            (centroidY > 0.5f || centroidY < -0.5f) ||
            (centroidZ > 0.5f || centroidZ < -0.5f) ) {
                String msgText = "RenderObject Constructor 2: Centering QuadMesh - " + 
                    "xCent: " + centroidX + 
                    ", yCent: " + centroidY + 
                    ", zCent: " + centroidZ;
                Globals.statusPrint(msgText);
                Globals.translateMesh(xImage, yImage, zImage, -centroidX, -centroidY, -centroidZ);
            }

            // Create shape objects. store the quadmesh centroid in the currentshape
            currentShape = new Shape3d(fileName, modelType);
            if(!currentShape.isValid()) {
                validCurrentShape = false;
            } else {
                currentShape.setReferencePoint(0.0f, 0.0f, 0.0f);
            }

            // Create an n vertex shape with coords set to 0
            lastShape = new Shape3d(currentShape.getNumVertices());
            if(!lastShape.isValid()) {
                validLastShape = false;
            }

            // The Shape3d constructor will issue a message if either of these
            // objects are not successfully created.
            if(!validCurrentShape || !validLastShape) {
                this.valid = false;
            }
            break;

        case IMAGE: 
            currentShape = new Shape3d(fileName, this.modelType);
            if(!currentShape.isValid()) {
                validCurrentShape = false;
            } else {
                if(userPOR) {  // If the user has defined a Point of Reference
                  currentShape.setReferencePoint(POR.x, POR.y, POR.z);
                } else {
                    // Make certain the shape is centered in the X-Y plane.
                    currentShape.getWCentroid(centroidX, centroidY, centroidZ);
                    if(
                    (centroidX > 0.5f || centroidX < -0.5f) ||
                    (centroidY > 0.5f || centroidY < -0.5f) ||
                    (centroidZ > 0.5f || centroidZ < -0.5f) ) {
                        String msgText = "RenderObject Constructor 2: Centering Shape - " + 
                            "xCent: " + centroidX + 
                            ", yCent: " + centroidY + 
                            ", zCent: " + centroidZ;
                        Globals.statusPrint(msgText);
                        currentShape.translateW(-centroidX, -centroidY, -centroidZ);   
                    }
                    currentShape.getWCentroid(centroidX, centroidY, centroidZ);
                    currentShape.setReferencePoint(centroidX, centroidY, centroidZ);
                }
            }

            // Create an n vertex shape with coords set to 0
            lastShape = new Shape3d(currentShape.getNumVertices());
            if(!lastShape.isValid()) {
                validLastShape = false;
            }

            // The Shape3d constructor will issue a message if either of these
            // objects are not successfully created.
            if(!validCurrentShape|| !validLastShape) {
                this.valid = false;
            }
            break;

        case SHAPE: 
            currentShape = new Shape3d(fileName, modelType);
            if(!currentShape.isValid()) {
                validCurrentShape = false;
            } else {
                if(userPOR) {  // If the user has defined a Point of Reference
                  currentShape.setReferencePoint(POR.x, POR.y, POR.z);
                } else {
                    currentShape.getWCentroid(centroidX, centroidY, centroidZ);

                    // Make certain the shape is centered in the X-Y plane.
                    currentShape.getWCentroid(centroidX, centroidY, centroidZ);
                    if(
                    (centroidX > 0.5f || centroidX < -0.5f) ||
                    (centroidY > 0.5f || centroidY < -0.5f) ||
                    (centroidZ > 0.5f || centroidZ < -0.5f) ) {
                        String msgText = "RenderObject Constructor 2: Centering Shape: " + 
                            "xCent: " + centroidX + 
                            ", yCent: " + centroidY + 
                            ", zCent: " + centroidZ;
                        Globals.statusPrint(msgText);
                        currentShape.translateW(-centroidX, -centroidY, -centroidZ);   
                    }
                    currentShape.getWCentroid(centroidX, centroidY, centroidZ);
                    currentShape.setReferencePoint(centroidX, centroidY, centroidZ);
                }
            }

            // Create an n vertex shape with coords set to 0
            lastShape = new Shape3d(currentShape.getNumVertices());
            if(!lastShape.isValid()) {
                validLastShape = false;
            }

            // The Shape3d constructor will issue a message if either of these
            // objects are not successfully created.
            if(!validCurrentShape || !validLastShape) {
                this.valid = false;
            }
            break;

        case COMPOUND: 
            break;
        }  // switch
    } // RenderObject ctor


    public void finalize() {
        if(ictdebug) {
          Globals.statusPrint("RenderObject Destructor ");
        }
    } // finalize


    public void drawSequence(BufferedImage pBuffImg, String modelName, 
    int screenHeight, int screenWidth, int frameCounter) {
        Graphics2D graphics2D = pBuffImg.createGraphics();
        Color blackColor;
        boolean highlightVertices = false;
        int iPt1X, iPt1Y;
        int iPt2X = 0, iPt2Y = 0;
        // float referenceX, referenceY, referenceZ; // These variables are not used

        currentShape.initCurrentVertex();
        if(currentShape.getNumVertices() == 0) { 
            return;
        }

        lastShape.initCurrentVertex();
        Color oldColor = graphics2D.getColor();
        int xOffset = screenWidth / 2;
        int yOffset = screenHeight / 2;
      
        if (this.modelType == QUADMESH) {
            previewMesh(graphics2D, modelName, xOffset, yOffset, screenHeight, screenWidth);
            return;
        }

        // Draw the new border
        blackColor = Color.BLACK;
        graphics2D.setColor(blackColor);

        Integer firstx = 0, firsty = 0, nextx = 0, nexty = 0;
        int index;

        if(currentShape.getNumFaces() == 0) {
            firstx = (int)currentShape.currentVertex.sx;
            firsty = (int)(screenHeight - currentShape.currentVertex.sy);
            iPt1X = firstx + xOffset;
            iPt1Y = firsty - yOffset;
            for (index = 1; index < currentShape.getNumVertices(); index++) {
                // currentShape.currentVertex++;
                currentShape.incCurrentVertex();
                iPt2X = (int)currentShape.currentVertex.sx + xOffset;
                iPt2Y = screenHeight - (int)currentShape.currentVertex.sy - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
            }
            
            // Now draw a line from the last point to the first point.
            graphics2D.drawLine(iPt2X, iPt2Y, firstx + xOffset, firsty - yOffset);
        } else {  // the model has faces
            currentShape.initCurrentFace();
            for (index = 1; index <= currentShape.getNumFaces(); index++) {
                currentShape.getScreenVertex(currentShape.currentFace.i1, firstx, firsty);
                currentShape.getScreenVertex(currentShape.currentFace.i2, nextx, nexty);
                iPt1X = firstx + xOffset;
                iPt1Y = screenHeight - firsty - yOffset;
                iPt2X = nextx + xOffset;
                iPt2Y = screenHeight - nexty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                currentShape.getScreenVertex(currentShape.currentFace.i3, nextx, nexty);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextx + xOffset;
                iPt2Y = screenHeight - nexty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                currentShape.getScreenVertex(currentShape.currentFace.i4, nextx, nexty);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextx + xOffset;
                iPt2Y = screenHeight - nexty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                // Now draw a line from the last point to the first point.
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = firstx + xOffset;
                iPt2Y = screenHeight - firsty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                // currentShape.currentFace++;
                currentShape.incCurrentFace();
            }
        }

        // Display the model's name
        float ax, ay;
        ax = currentShape.averageX() + currentShape.minX;
        ay = screenHeight - (currentShape.averageY() + currentShape.minY);
        graphics2D.setColor(Color.RED); // red
        graphics2D.drawString(modelName, (int)ax + xOffset, (int)ay - yOffset);

        // Put the frame number on the screen
        String frameString = frameCounter + "";

        // 10, 10 refers to screen coordinates
        graphics2D.drawString(frameString, 10, 10);
        graphics2D.setColor(oldColor);
    } // drawSequence


    // TODO: a method?
    // Called from:
    //     SceneList.previewStill
    void drawStill(BufferedImage pBuffImg, String psModelName, 
    int piScreenHeight, int piScreenWidth) {
        Graphics2D graphics2D = pBuffImg.createGraphics();
        boolean highlightVertices = false;  // Controls whether vertices are highlighted

        int xOffset = piScreenWidth / 2;
        int yOffset = piScreenHeight / 2;
        
        if (modelType == QUADMESH) {
            previewMesh(graphics2D, psModelName, xOffset, yOffset, piScreenHeight, piScreenWidth);
            return;
        }
        
        currentShape.initCurrentVertex();
        if(currentShape.getNumVertices() == 0) {
            Globals.statusPrint("RenderObject.drawStill: No vertices to draw");
            return;
        }

        // float referenceX, referenceY, referenceZ; // these variables are not used
        Color backgroundColor = graphics2D.getBackground();

        int index;
        float ax, ay;
        int firstx = 0, firsty = 0, nextx = 0, nexty = 0;
        int iPt1X, iPt1Y;
        int iPt2X = 0, iPt2Y = 0;

        currentShape.initCurrentVertex();

        // Draw the new border
        // ROP2 not activated when background plate is not used.
        // so models are visible on grey window background.
        Color origColor = Color.BLACK;
        Color penColor = new Color(0, 200, 0); // green pen

        // If the shape has no faces, the vertices describe a planar element
        // If the shape has faces, draw them
        if(currentShape.getNumFaces() == 0) {
            firstx = (int)currentShape.currentVertex.sx;
            firsty = (int)(piScreenHeight - currentShape.currentVertex.sy);
            if(highlightVertices) {
                drawBox(graphics2D, penColor, origColor, 
                    firstx + xOffset, 
                    firsty - yOffset);
            }

            iPt1X = firstx + xOffset;
            iPt1Y = firsty - yOffset;
            for (index = 1; index < currentShape.getNumVertices(); index++) {
                // currentShape.iCurrVtxIdx++;
                currentShape.incCurrentVertex();
                iPt2X = (int)currentShape.currentVertex.sx + xOffset;
                iPt2Y = piScreenHeight - (int)currentShape.currentVertex.sy - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;

                if(highlightVertices) {
                     drawBox(graphics2D, penColor, origColor, 
                        iPt2X, iPt2Y);
                }
            } // for
            
            // Now draw a line from the last point to the first point
            graphics2D.drawLine(iPt2X, iPt2Y, firstx + xOffset, firsty - yOffset);
        } else {  // The model has faces
            currentShape.initCurrentFace();
            for (index = 1; index <= currentShape.getNumFaces(); index++) {
                currentShape.getScreenVertex(currentShape.currentFace.i1, firstx, firsty);
              
                if(highlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        firstx + xOffset, 
                        firsty - yOffset);
                }
                iPt1X = firstx + xOffset;
                iPt1Y = piScreenHeight - firsty - yOffset;
              
                currentShape.getScreenVertex(currentShape.currentFace.i2, nextx, nexty);
                iPt2X = nextx + xOffset;
                iPt2Y = piScreenHeight - nexty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                if(highlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        nextx + xOffset, 
                        piScreenHeight - nexty - yOffset);
                }

                currentShape.getScreenVertex(currentShape.currentFace.i3, nextx, nexty);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextx + xOffset;
                iPt2Y = piScreenHeight - nexty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                if(highlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        nextx + xOffset, 
                        piScreenHeight - nexty - yOffset);
                }

                currentShape.getScreenVertex(currentShape.currentFace.i4, nextx, nexty);
                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = nextx + xOffset;
                iPt2Y = piScreenHeight - nexty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                if(highlightVertices) {
                    drawBox(graphics2D, penColor, origColor, 
                        nextx + xOffset, 
                        piScreenHeight - nexty - yOffset);
                }

                iPt1X = iPt2X;
                iPt1Y = iPt2Y;
                iPt2X = firstx + xOffset;
                iPt2Y = piScreenHeight - firsty - yOffset;
                graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

                // currentShape.currentFace++;
                currentShape.incCurrentFace();
            } // for
        } // if

        // Display the model's name
        ax = currentShape.averageX() + currentShape.minX;
        ay = piScreenHeight - (currentShape.averageY() + currentShape.minY);
        graphics2D.setColor(Color.RED);   // red pen
        graphics2D.setBackground(backgroundColor); 
        if(!psModelName.equals(" ")) {
            graphics2D.drawString(psModelName, (int)ax + xOffset, (int)ay - yOffset);
        }
    } // drawStill


    // TODO: Not a method
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


    // TODO: Not a method
    void insertionSort(int theItems[], int numItems) {
        int itemTemp;
        int index, indexTmp, theValue;

        for( index = 0; index < numItems; index++ ) {
            itemTemp = theItems[index];
            theValue = theItems[index];
            for(indexTmp = index; indexTmp > 0; indexTmp-- ) {
                if( theItems[indexTmp - 1] > theValue ) {
                    theItems[indexTmp] = theItems[indexTmp - 1];
                } else {
                    break;
                }
            }
            theItems[indexTmp] = itemTemp;
        }
    } // insertionSort


    // TODO: Not a method
    void removeDuplicates(int theList[], int listLength) {
        int numVertices = listLength;

        if ((numVertices / 2) * 2 == numVertices) {
            return;
        }
        if (listLength <= 2) {
            return;
        }

        for (int index = 0; index + 1 < listLength; index++) {
            if (theList[index + 1] == theList[index]) {
                for (int index2 = index; index2 < listLength; index2++) {
                    theList[index2] = theList[index2 + 1];
                }
                listLength--;
                index--;
            }
        }
    } // removeDuplicates


    // TODO: Not a method
    int prepareCutout(Shape3d aShape, HWND HWindow, String imageFileName,
    String cutoutName, int imageWidth, int imageHeight) {
        // Creates a cutout image, alpha image, and shape file from
        // a boundary traced by the user
        int numVertices = aShape.getNumVertices();

        // Variable thePoints will later be used as a parameter to MemImage.drawMask,
        // where it will then be used as a parameter to the Polygon function
        Point2d[] thePoints = new Point2d[numVertices];
        int thePointsIdx = 0;

        aShape.initCurrentVertex();
        for(int myIndex = 0; myIndex < numVertices; myIndex++) {
            thePoints[thePointsIdx].x = aShape.currentVertex.x;
            thePoints[thePointsIdx].y = aShape.currentVertex.y;
            // aShape.currentVertex++;
            aShape.incCurrentVertex();
            thePointsIdx++;
        }

        // Memory, write, 1 bit
        MemImage maskImage = new MemImage("OneBit.bmp", imageHeight, imageWidth, RANDOM, 'W', ONEBITMONOCHROME);
        if(!maskImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: Couldn't create 1 bit mask image");
            return 1;
        }

        int myStatus = maskImage.drawMask(thePoints, numVertices);
        if (myStatus != 0) {
            String msgText = "RenderObject.prepareCutout: Couldn't create 1 bit mask " + myStatus;
            Globals.statusPrint(msgText);
            maskImage.close();
            return 2;
        }

        // Create an unpacked (8 bit) mask image
        Globals.statusPrint("RenderObject.prepareCutout: Unpacking Mask Image...");
        MemImage unpackedMaskImage = new MemImage(imageHeight, imageWidth);
        if(!unpackedMaskImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: Not Enough memory to create unpacked mask image");
            return 1;
        }

        maskImage.unPack(unpackedMaskImage);
        if(!unpackedMaskImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: unpack image operation was aborted");
            return 1;
        }

        maskImage.close();
        FileUtils.deleteFile("OneBit.bmp");
        
        Globals.statusPrint("RenderObject.prepareCutout: Removing borders from mask image...");
        MemImage originalImage = new MemImage(imageFileName, 0, 0, SEQUENTIAL, 'R', 0);
        if(!originalImage.isValid()) {
            Globals.statusPrint("RenderObject.prepareCutout: Unable to open original image");
            return 1;
        }

        myStatus = Globals.createCutout(originalImage, unpackedMaskImage, cutoutName, aShape);
        if(myStatus != 0) {
            Globals.statusPrint("RenderObject.prepareCutout: Unable to prepare mask and image cutouts");
            return 1;
        }

        return 0;
    } // prepareCutout


    // TODO: Not A method
    // Called from:
    //     Globals.tweenImage
    public static int maskFromShape(Shape3d inShape, MemImage maskImage) {
        // Create a mask image from a 2D boundary.
        // The generated image is displayed on the Cmainframe window.
        // This function could be modified use a memory DC instead.
        int imageHeight = maskImage.getHeight();
        int imageWidth  = maskImage.getWidth();
        int numVertices = inShape.getNumVertices();
        
        // CWnd theWindow = AfxGetMainWnd();
        // HWND HWindow = theWindow.m_hWnd;

        // Copy the shape vertices into a structure compatible with the Windows
        // drawing functions

        // Variable thePoints will be used later as a parameter to MemImage.drawMask, 
        // where it will then be used as a parameter to the Polygon function
        Point2d[] thePoints = new Point2d[numVertices];
        int thePointsIdx = 0;
        inShape.initCurrentVertex();

        // Populate array thePoints. This array will later be used as a parameter
        // to MemImage.drawMask, where it will be used to draw a polygon.
        for(int myIndex = 0; myIndex < numVertices; myIndex++) {
            thePoints[thePointsIdx].x = inShape.currentVertex.x;
            thePoints[thePointsIdx].y = inShape.currentVertex.y;
            // inShape.currentVertex++;
            inShape.incCurrentVertex();
            thePointsIdx++;
        }

        MemImage tempMaskImage = new MemImage("OneBit.bmp", imageHeight, imageWidth,
            RANDOM, 'W', ONEBITMONOCHROME);  //Memory, write, 1 bit
        if(!tempMaskImage.isValid()) {
            Globals.statusPrint("RenderObject.maskFromShape: Couldn't create 1 bit mask image");
            return 1;
        }

        // HDC memoryDC = CreateCompatibleDC();

        // Use the Graphics2D.drawPolygon method to draw the points
        int myStatus = tempMaskImage.drawMask(thePoints, numVertices);

        if (myStatus != 0) {
            String msgText = "RenderObject.maskFromShape: Couldn't create 1 bit mask " + myStatus;
            Globals.statusPrint(msgText);
            tempMaskImage.close();
            return 2;
        }

        // Create an unpacked (8 bit) mask image
        // statusPrint("maskFromShape: Unpacking Mask Image...");
        tempMaskImage.unPack(maskImage);
        FileUtils.deleteFile("OneBit.bmp");

        return 0;
    } // maskFromShape


    // Called from:
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.previewStill
    public boolean isValid() {
        return this.valid;
    } // isValid


    // TODO: Not a method
    // Called from:
    //     Ctor that takes 4 parameters: a String, int, boolean and Point3d
    void assembleName(String psInputName, char pcSuffix, String psOutputName) {
        String sDrive, sDir, sFile, sExt;

        // Break a pathname into its components, add a suffix then put it back together again
        _splitpath(psInputName, sDrive, sDir, sFile, sExt);
        int iFileNameLength = sFile.length();
        if(iFileNameLength > 0) {
            char[] charArray = new char[1];
            charArray[0] = pcSuffix;
            sFile.concat(new String(charArray)); // Substitute a suffix
        }

        // Set the output parameter psOutputName
        _makepath(psOutputName, sDrive, sDir, sFile, sExt);
    } // assembleName


    // Draws the boundary of each quadrilateral in the mesh to produce a 
    // wireframe view of the model.
    // See p 172 of Visual Special Effects Toolkit in C++.
    // Called from:
    //     drawStill
    void previewMesh(Graphics2D graphics2D, String modelName, 
    float xOff, float yOff, 
    int screenHeight, int screenWidth) {
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
        !xImage.isValid() ||
        !textureImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("RenderObject.previewMesh: One or more poly-mesh image is not valid");
            return;
        }

        // Later in a double for loop, buffer xBuffer will be populated
        xBuffer = new int[xImage.getWidth()];
        /* Dead code, per compiler
        if (xBuffer == null) {
            Globals.statusPrint("RenderObject.previewMesh: Not enough memory for xBuffer");
            return;
        }
        */

        // Later in a double for loop, buffer yBuffer will be populated
        yBuffer = new int[yImage.getWidth()];
        /* Dead code, per compiler 
        if (yBuffer == null) {
            Globals.statusPrint("RenderObject.previewMesh: Not enough memory for yBuffer");
            return;
        }
        */

        // Later in a double for loop, buffer iBuffer will be populated
        iBuffer = new byte[textureImage.getWidth()];
        /* Dead code, per compiler
        if (iBuffer == null) {
            Globals.statusPrint("RenderObject.previewMesh: Not enough memory for iBuffer");
            return;
        }
        */

        xBufferIdx = 0;
        yBufferIdx = 0;
        iBufferIdx = 0;

        graphics2D.setColor(Color.BLACK);

        // int xOffset = (int)(xOff + 0.5f); // this variable is not used
        // int yOffset = (int)(yOff + 0.5f); // this variable is not used

        int imHeight = textureImage.getHeight();
        int imWidth  = textureImage.getWidth();
        int row, col;
        float x1, y1, z1;
        float tx = 0.0f, ty = 0.0f, tz = 0.0f;
        byte i1;
        Integer sx1 = 0, sy1 = 0;
        Float refX = 0f, refY = 0f, refZ = 0f;
        int meshIncrement = 6;

        // Get the model's referencePoint
        currentShape.getReferencePoint(refX, refY, refZ);

        // Preview the mesh
        for (row = 1; row <= imHeight; row += meshIncrement) {
            for (col = 1; col <= imWidth; col += meshIncrement) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                i1 = textureImage.getMPixel(col, row);

                // Project to the screen
                m_matrix.transformAndProjectPoint(x1, y1, z1, 
                    sx1, sy1, 
                    refX, refY, refZ, 
                    screenHeight, screenWidth, 
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
                              graphics2D.drawLine(
                                  xPrev1Idx, screenHeight - (yPrev1Idx),
                                  xPrev2Idx, screenHeight - (yPrev2Idx));
                          }
                          if(col == (meshIncrement + 1)) {  // draw the first column
                              graphics2D.drawLine(
                                  xPrev1Idx, screenHeight - (yPrev1Idx),
                                  xTemp1, screenHeight - yTemp1);
                          }

                          graphics2D.drawLine(
                              xPrev2Idx, screenHeight - (yPrev2Idx),
                              xTemp2, screenHeight - yTemp2);
                          graphics2D.drawLine(
                              xTemp2, screenHeight - yTemp2,
                              xTemp1, screenHeight - yTemp1);
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
        int xText = sxMin + ((sxMax - sxMin)/2);
        int yText = screenHeight - (syMin + ((syMax - syMin)/2));
        graphics2D.setColor(Color.RED); // red
        graphics2D.drawString(modelName, xText, yText);
    } // previewMesh


    // Renders quadrilateral mesh models.
    // See p 172 of Visual Special Effects Toolkit in C++.
    // Class Globals also has a renderMesh method, but that one takes 
    // 6 parameters: a String, 4 MemImage ojbects, and a TMatrix.
    public int renderMesh(MemImage outputImage, MemImage inputImage, 
    boolean blendIndicator) {
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
        !xImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMesh: One or more poly-mesh image is not valid");
            return -1;
        }

        xBuffer = new int[xImage.getWidth()];
        /* Dead code, per the compiler
        if (xBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for xBuffer");
            return -1;
        }
        */

        yBuffer = new int[yImage.getWidth()];
        /* Dead code, per the compiler
        if (yBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for yBuffer");
            return -1;
        }
        */

        iBuffer = new byte[inputImage.getWidth()];
        /* Dead code, per the compiler
        if (iBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for iBuffer");
            return -1;
        }
        */

        xBufferIdx = 0;
        yBufferIdx = 0;
        iBufferIdx = 0;

        int imHeight  = inputImage.getHeight();
        int imWidth   = inputImage.getWidth();
        int outHeight = outputImage.getHeight();
        int outWidth  = outputImage.getWidth();
        int row, col;
        float x1, y1, z1;
        Float tx = 0f, ty = 0f, tz = 0f;
        byte i1;
        Integer sx1 = 0, sy1 = 0;
        Float refX = 0f, refY = 0f, refZ = 0f;
        // int meshIncrement = 6; // variable not used

        // Get the model's referencePoint
        currentShape.getReferencePoint(refX, refY, refZ);

        // Render the mesh
        m_matrix.display("RenderObject.renderMesh");

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                i1 = inputImage.getMPixel(col, row);

                // Project to the screen
                m_matrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
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
                    outputImage.fillPolyz(
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


    public int renderMeshz(MemImage outputImage, MemImage maskImage, 
    MemImage inputImage, MemImage zBuffer, float vx, float vy, float vz) {
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
        !xImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMeshz: One or more poly-mesh image is not valid");
            return -1;
        }

        xBuffer = new int[xImage.getWidth()];
        /* Dead code, per compiler
        if (xBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for xBuffer");
            return -1;
        }
        */

        yBuffer = new int[yImage.getWidth()];
        /* Dead code, per compiler
        if (yBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for yBuffer");
            return -1;
        }
        */

        dBuffer = new float[zImage.getWidth()];
        /* Dead code, per compiler
        if (dBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for distance Buffer");
            return -1;
        }
        */

        iBuffer = new byte[inputImage.getWidth()];
        /* Dead code, per compiler
        if (iBuffer == null) {
            Globals.statusPrint("RenderObject.renderMeshz: Not enough memory for iBuffer");
            return -1;
        }
        */

        MemImage midImage = new MemImage(outputImage.getHeight(), outputImage.getWidth());
        if(!midImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMeshZ: Not enough memory to open intermediate image");
            return -1;
        }

        midImage.setFileName("midImage");
        MemImage midZImage = new MemImage(outputImage.getHeight(), outputImage.getWidth(), 32);
        if(!midZImage.isValid()) {
            Globals.statusPrint("RenderObject.renderMeshZ: Not enough memory to open intermediate Z image");
            return -1;
        }

        midZImage.setFileName("midZImage");
        midZImage.init32(ZBUFFERMAXVALUE);
        int imHeight  = inputImage.getHeight();
        int imWidth   = inputImage.getWidth();
        int outHeight = outputImage.getHeight();
        int outWidth  = outputImage.getWidth();

        //  Temporary - for testing
        vx = (float)outWidth/2.0f;
        vy = (float)outHeight/2.0f;
        vz = 512.0f;

        msgText = String.format("RenderObject.renderMeshz: Viewer location - vx: %f, vy: %f, vz: %f", vx, vy, vz);
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
        currentShape.getReferencePoint(refX, refY, refZ);
        float tx = 0.0f, ty = 0.0f, tz = 0.0f;

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                i1 = inputImage.getMPixel(col, row);

                // Project to the screen
                m_matrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
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

                    dBuffer[dBufferIdx] = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
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

                    dTemp1 = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                }
      
                if (row > 1 && col > 1) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;

                    dTemp2 = MathUtils.getDistance3d(tx, ty, tz, vx, vy, vz);
                                  
                    // Render the quadrangle intensities
                    //                     
                    // Render the quadrangle distances and update the intermediate zBuffer

                    ///////////////////////////////////////////////
                    //  Gouraud Shading
                    ///////////////////////////////////////////////
                    boolean shading = false;
                    if(shading) {
                        c2.x = xImage.getMPixel32(col, row);
                        c2.y = yImage.getMPixel32(col, row);
                        c2.z = zImage.getMPixel32(col, row);
          
                        c1.x = xImage.getMPixel32(col-1, row);
                        c1.y = yImage.getMPixel32(col-1, row);
                        c1.z = zImage.getMPixel32(col-1, row);
                  
                        p1.x = xImage.getMPixel32(col-1, row-1);
                        p1.y = yImage.getMPixel32(col-1, row-1);
                        p1.z = zImage.getMPixel32(col-1, row-1);
          
                        p2.x = xImage.getMPixel32(col, row-1);
                        p2.y = yImage.getMPixel32(col, row-1);
                        p2.z = zImage.getMPixel32(col, row-1);

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
        MemImage matte = new MemImage(outputImage.getHeight(), outputImage.getWidth());
        midImage.createAlphaImage(matte);
        Globals.statusPrint("RenderObject.renderMeshz: Creating a matte for the rendered quad mesh");
        matte.alphaSmooth5();

        float alphaScale = 1.0f;
        Globals.blendz(midImage, matte, midZImage, zBuffer, outputImage, alphaScale);

        return 0;
    } // renderMeshz


    // Called from:
    //     transformAndProject (the method which takes 3 parameters)
    //     SceneList.previewStill
    public void transformAndProject(TMatrix pTMatrix, int piOutHeight, int piOutWidth,
    boolean externalCentroid,
    float pfCentroidX, float pfCentroidY, float pfCentroidZ) {
        if((this.modelType == SHAPE) || (this.modelType == IMAGE)) {
            pTMatrix.transformAndProject(currentShape, piOutHeight, piOutWidth, 
                externalCentroid,
                pfCentroidX, pfCentroidY, pfCentroidZ);
        } else {
            // Copy the transformation matrix for later use
            m_matrix.copy(pTMatrix);
        }
    } // transformAndProject


    // Called from:
    //     SceneList.calcCompoundModelRefPoint
    //     SceneList.previewStill
    public void transformAndProject(TMatrix pTMatrix, int piOutHeight, int piOutWidth) {
        this.transformAndProject(pTMatrix, piOutHeight, piOutWidth, 
            false, 
            0.0f, 0.0f, 0.0f);
    }

    // TODO: Not a method
    void transformAndProjectPoint2(TMatrix pTMatrix, float pfX, float pfY, float pfZ, 
    Integer sx, Integer sy, 
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
        sx = (int)(fXp + 0.5f);
        sy = (int)(fYp + 0.5f);
    } // transformAndProjectPoint2


    // TODO: Not a method
    // Called from:
    //     drawStill
    void drawBox(Graphics2D graphics2D, Color pPenColor, Color pOrigColor, int x, int y) {
        //  Draw a box 2 * offset + 1 pixels wide and high around the point x,y
        int offset = 2;
        int iPt1X, iPt1Y;
        int iPt2X, iPt2Y;
        graphics2D.setColor(pPenColor);

        iPt1X = x - offset;
        iPt1Y = y + offset;
        iPt2X = x + offset;
        iPt2Y = y + offset;
        graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        iPt1X = iPt2X;
        iPt1Y = iPt2Y;
        iPt2X = x + offset;
        iPt2Y = y - offset;
        graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        iPt1X = iPt2X;
        iPt1Y = iPt2Y;
        iPt2X = x - offset;
        iPt2Y = y - offset;
        graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        iPt1X = iPt2X;
        iPt1Y = iPt2Y;
        iPt2X = x - offset;
        iPt2Y = y + offset;
        graphics2D.drawLine(iPt1X, iPt1Y, iPt2X, iPt2Y);

        // MoveToEx(x, y); // this doesn't actually draw anything
        graphics2D.setColor(pOrigColor);
    } // drawBox


    // Not called from within this file
    public int renderShape(MemImage outputImage, boolean blendIndicator) {
        if(currentShape.getNumFaces() == 0) {
            Globals.statusPrint("RenderObject.renderShape: Shape has no faces - cannot be rendered");
            return 0;
        }

        Integer sx1 = 0, sy1 = 0; 
        Integer sx2 = 0, sy2 = 0;
        Integer sx3 = 0, sy3 = 0;
        Integer sx4 = 0, sy4 = 0;
        byte I1p = 0, I2p = 0, I3p = 0, I4p = 0; 
        int index, index1, index2, index3, index4;

        currentShape.initCurrentFace();
        for (index = 1; index <= currentShape.getNumFaces(); index++) {
            index1 = currentShape.currentFace.i1; 
            index2 = currentShape.currentFace.i2; 
            index3 = currentShape.currentFace.i3; 
            index4 = currentShape.currentFace.i4; 

            currentShape.getScreenVertex(index1, sx1, sy1);
            currentShape.getScreenVertex(index2, sx2, sy2);
            currentShape.getScreenVertex(index3, sx3, sy3);
            if (index4 > 0) {
                currentShape.getScreenVertex(index4, sx4, sy4);
            }

            // Draw the face
            if (index4 > 0) {
                outputImage.fillPolyz(
                    sx1, sy1, I1p, 0.0f, 
                    sx2, sy2, I2p, 0.0f, 
                    sx3, sy3, I3p, 0.0f, 
                    sx4, sy4, I4p, 0.0f, 
                    null);
            } else { 
                //its a triangle
            }
            // currentShape.currentFace++;
            currentShape.incCurrentFace();
        } 

        return 0;
    } // renderShape


    // Not called from within this file
    public int renderShapez(MemImage outputImage, 
    MemImage alphaImage, MemImage zBuffer, 
    float vx, float vy, float vz) {
        
        //  The shape object is already transformed upon entry to this procedure
        Integer sx1 = 0, sy1 = 0;
        Integer sx2 = 0, sy2 = 0;
        Integer sx3 = 0, sy3 = 0;
        Integer sx4 = 0, sy4 = 0;
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

        if(currentShape.getNumFaces() == 0) {
            Globals.statusPrint("RenderObject.renderShapez: Shape has no faces - cannot be rendered");
            return 0;
        }
        int yo = outputImage.getHeight() / 2;
        int xo = outputImage.getWidth() / 2;

        currentShape.initCurrentFace();
        for (index = 1; index <= currentShape.getNumFaces(); index++) {
            index1 = currentShape.currentFace.i1; 
            index2 = currentShape.currentFace.i2; 
            index3 = currentShape.currentFace.i3; 
            index4 = currentShape.currentFace.i4; 

            currentShape.getScreenVertex(index1, sx1, sy1);
            currentShape.getScreenVertex(index2, sx2, sy2);
            currentShape.getScreenVertex(index3, sx3, sy3);
            if(index4 > 0) {
                currentShape.getScreenVertex(index4, sx4, sy4);
            }

            // Draw the face
            if(index4 > 0) {
                currentShape.getTransformedVertex(index1, tx1, ty1, tz1);
                currentShape.getTransformedVertex(index2, tx2, ty2, tz2);
                currentShape.getTransformedVertex(index3, tx3, ty3, tz3);
                currentShape.getTransformedVertex(index4, tx4, ty4, tz4);

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

                outputImage.fillPolyz(
                    sx1 + xo, sy1 + yo, I1p, I1d,
                    sx2 + xo, sy2 + yo, I1p, I2d, 
                    sx3 + xo, sy3 + yo, I1p, I3d, 
                    sx4 + xo, sy4 + yo, I1p, I4d,
                    zBuffer);
            } else { 
                //its a triangle
            }
            // currentShape.currentFace++;
            currentShape.incCurrentFace();
        }

        return 0;
    } // renderShapez


    public int sizeofLowerLimit() {
        int mySize = 0;
        int booleanFieldsSizeInBits = 0;
        int booleanFieldsSize = 0;
        int intFieldsSize = 0;
        int floatFieldsSize = 0;
        int referenceFieldsSize = 0;

        /*
        boolean ictdebug = false;
        boolean valid;
        int modelType;		// a copy of the sceneElement model Type
        Shape3d currentShape;
        Shape3d lastShape;
        MemImage textureImage, xImage, yImage, zImage;
        TMatrix m_matrix; 
        */

        booleanFieldsSizeInBits = 2; // 2 booleans
        booleanFieldsSize = 1; // 2 bits fit in a byte
        intFieldsSize = 1*4; // 1 ints
        floatFieldsSize = 0*4; // 0 floats
        referenceFieldsSize = 7*4; // 7 references to objects
        mySize = booleanFieldsSize + intFieldsSize + floatFieldsSize + referenceFieldsSize;

        return mySize;
    } // sizeofLowerLimit
} // class RenderObject