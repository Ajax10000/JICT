package core;

import globals.Globals;

import math.TMatrix;
import math.Vect;

import structs.FaceSet;
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
            String msgBuffer = "Constructor 1. Size of renderObject: " + sizeof(RenderObject);
            Globals.statusPrint(msgBuffer);
        }

        this.currentShape = new Shape3d(aUL, aUR, aLR, aLL);
        Float centroidX, centroidY, centroidZ;
        this.currentShape.getWCentroid(centroidX, centroidY, centroidZ);

        // Make certain the shape is centered in the X-Y plane.
        if(
        (centroidX > 0.5f || centroidX < -0.5f) ||
        (centroidY > 0.5f || centroidY < -0.5f) ||
        (centroidZ > 0.5f || centroidZ < -0.5f) ) {
            String msgText;
            sprintf(msgText, "renderObject 1. Centering shape: xCent: %f, yCent: %f zCent: %f",
                centroidX, centroidY, centroidZ);
            Globals.statusPrint(msgText);
            currentShape.translateW(-centroidX, -centroidY, -centroidZ);   
            currentShape.floor();   
            currentShape.getWCentroid(centroidX, centroidY, centroidZ);
        }

        currentShape.setReferencePoint(centroidX, centroidY, centroidZ);
        this.lastShape = new Shape3d(4); //4 vertex shape with coords set to 0
        this.m_matrix = new TMatrix();
        this.valid = true;
    } // RenderObject ctor


    public RenderObject(String fileName, int aModelType, boolean userPOR, Point3d POR) {
        boolean validCurrentShape = true;
        boolean validLastShape = true;
        Float centroidX, centroidY, centroidZ;

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
            msgText = "Constructor 2. Size of renderObject: " + sizeof(RenderObject);
            Globals.statusPrint(msgText);
        }
        this.valid = true;

        switch(this.modelType) {
        case QUADMESH:
            String texturePath, xPath, yPath, zPath;
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
            (centroidX > 0.5 || centroidX < -0.5) ||
            (centroidY > 0.5 || centroidY < -0.5) ||
            (centroidZ > 0.5 || centroidZ < -0.5) ) {
                String msgText = "renderObject 2. Centering QuadMesh. xCent: " + centroidX + 
                    ", yCent: " + centroidY + ", zCent: " + centroidZ;
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
                    (centroidX > 0.5 || centroidX < -0.5) ||
                    (centroidY > 0.5 || centroidY < -0.5) ||
                    (centroidZ > 0.5 || centroidZ < -0.5) ) {
                        String msgText = "renderObject 2a. Centering Shape: xCent: " + centroidX + 
                            ", yCent: " + centroidY + ", zCent: " + centroidZ;
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
                        String msgText = "renderObject 2b. centering Shape: xCent: " + centroidX + 
                            ", yCent: " + centroidY + ", zCent: " + centroidZ;
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


    public void drawSequence(HDC theDC, String modelName, int screenHeight, int screenWidth, int frameCounter) {
        HPEN hBlackPen, hWhitePen;
        int highlightVertices = 0;
        float referenceX, referenceY, referenceZ;

        SetMapMode(theDC, MM_TEXT); // Logical units = physical units = pixel
        currentShape.initCurrentVertex();
        if(currentShape.getNumVertices() == 0) { 
            return;
        }

        lastShape.initCurrentVertex();
        HGDIOBJ hOldPen = SelectObject(theDC, hWhitePen);  //save the current object
        int xOffset = screenWidth / 2;
        int yOffset = screenHeight / 2;
      
        if (this.modelType == QUADMESH) {
            previewMesh(theDC, modelName, xOffset, yOffset, screenHeight, screenWidth);
            return;
        }

        // Draw the new border
        hBlackPen = CreatePen(PS_SOLID, 1, RGB(0, 0, 0));
        SelectObject(theDC, hBlackPen);
        DeleteObject(hWhitePen);
        Integer firstx, firsty, nextx, nexty;
        int index;

        if(currentShape.getNumFaces() == 0) {
            firstx = (int)currentShape.currentVertex.sx;
            firsty = (int)(screenHeight - currentShape.currentVertex.sy);
            MoveToEx(theDC, firstx + xOffset, firsty - yOffset, 0L);
            for (index = 1; index < currentShape.getNumVertices(); index++) {
                currentShape.currentVertex++;
                LineTo(theDC, currentShape.currentVertex.sx + xOffset, screenHeight - currentShape.currentVertex.sy - yOffset);
            }
            LineTo(theDC, firstx + xOffset, firsty - yOffset);
        } else {  // the model has faces
            currentShape.initCurrentFace();
            for (index = 1; index <= currentShape.getNumFaces(); index++) {
                currentShape.getScreenVertex(currentShape.currentFace.i1, firstx, firsty);
                MoveToEx(theDC, firstx + xOffset, screenHeight - firsty - yOffset, 0L);
                
                currentShape.getScreenVertex(currentShape.currentFace.i2, nextx, nexty);
                LineTo(theDC, nextx + xOffset, screenHeight - nexty - yOffset);

                currentShape.getScreenVertex(currentShape.currentFace.i3, nextx, nexty);
                LineTo(theDC, nextx + xOffset, screenHeight - nexty - yOffset);

                currentShape.getScreenVertex(currentShape.currentFace.i4, nextx, nexty);
                LineTo(theDC, nextx + xOffset, screenHeight - nexty - yOffset);

                LineTo(theDC, firstx + xOffset, screenHeight - firsty - yOffset);
                currentShape.currentFace++;
            }
        }

        // Display the model's name
        float ax, ay;
        ax = currentShape.averageX() + currentShape.minX;
        ay = screenHeight - (currentShape.averageY() + currentShape.minY);
        SetTextColor(theDC, RGB(255, 0, 0)); // red
        TextOut(theDC,(int)ax + xOffset, (int)ay - yOffset, modelName, modelName.length());

        // Put the frame number on the screen
        String frameString = frameCounter + "";

        // 10, 10 refers to screen coordinates
        TextOut(theDC, 10, 10, frameString, frameString.length());
        SelectObject(theDC, hOldPen);   // reselect the previous object
        DeleteObject(hBlackPen);
    } // drawSequence


    // TODO: a method?
    void drawStill(HWND theWindow, String modelName, int screenHeight, int screenWidth) {
        int highlightVertices = 0;  // Controls whether vertices are highlighted

        int xOffset = screenWidth / 2;
        int yOffset = screenHeight / 2;
        
        if (modelType == QUADMESH) {
            HDC theDC = GetDC(theWindow);
            previewMesh(theDC, modelName, xOffset, yOffset, screenHeight, screenWidth);
            ReleaseDC(theWindow, theDC);
            return;
        }

        HDC theDC = GetDC(theWindow);
        int nDrawMode = GetROP2(theDC);
        HPEN hBlackPen, hWhitePen, hPointPen;
        SetMapMode(theDC, MM_TEXT); // Logical units=physical units = pixel
        
        currentShape.initCurrentVertex();
        if(currentShape.getNumVertices() == 0) {
            Globals.statusPrint("drawStill: no vertices to draw");
            return;
        }
        float referenceX, referenceY, referenceZ;

        COLORREF backgroundColor = GetBkColor(theDC);

        hWhitePen = CreatePen(PS_SOLID, 1, backgroundColor);      
        int index;
        float ax, ay;
        int firstx, firsty, nextx, nexty;

        currentShape.initCurrentVertex();

        // Draw the new border
        // ROP2 not activated when background plate is not used.
        // so models are visible on grey window background.
        hBlackPen = CreatePen(PS_SOLID, 1, RGB(0, 0, 0));
        SelectObject(theDC, hBlackPen);
        hPointPen = CreatePen(PS_SOLID, 1, RGB(0, 200, 0)); //green pen

        // If the shape has no faces, the vertices describe a planar element
        // If the shape has faces, draw them
        if(currentShape.getNumFaces() == 0) {
            firstx = (int)currentShape.currentVertex.sx;
            firsty = (int)(screenHeight - currentShape.currentVertex.sy);
            if(highlightVertices == 1) {
                drawBox(theDC, hPointPen, hBlackPen,firstx + xOffset, firsty - yOffset);
            }
            MoveToEx(theDC,firstx + xOffset, firsty - yOffset, 0L);

            for (index = 1; index < currentShape.getNumVertices(); index++) {
                currentShape.currentVertex++;
                LineTo(theDC, currentShape.currentVertex.sx + xOffset, screenHeight - currentShape.currentVertex.sy - yOffset);
                if(highlightVertices == 1) {
                     drawBox(theDC, hPointPen, hBlackPen, currentShape.currentVertex.sx + xOffset, 
                        screenHeight - currentShape.currentVertex.sy - yOffset);
                }
            }
            LineTo(theDC, firstx + xOffset, firsty - yOffset);
        } else {  // The model has faces
            currentShape.initCurrentFace();
            for (index = 1; index <= currentShape.getNumFaces(); index++) {
                currentShape.getScreenVertex(currentShape.currentFace.i1, firstx, firsty);
              
                if(highlightVertices == 1) {
                    drawBox(theDC, hPointPen, hBlackPen,firstx + xOffset, firsty - yOffset);
                }
                MoveToEx(theDC,firstx + xOffset, screenHeight - firsty - yOffset, 0L);
              
                currentShape.getScreenVertex(currentShape.currentFace.i2, nextx, nexty);
                LineTo(theDC,nextx + xOffset, screenHeight - nexty - yOffset);
                if(highlightVertices == 1) {
                    drawBox(theDC, hPointPen, hBlackPen,nextx + xOffset, screenHeight - nexty - yOffset);
                }

                currentShape.getScreenVertex(currentShape.currentFace.i3, nextx, nexty);
                LineTo(theDC, nextx + xOffset, screenHeight - nexty - yOffset);
                if(highlightVertices == 1) {
                    drawBox(theDC, hPointPen, hBlackPen,nextx + xOffset, screenHeight - nexty - yOffset);
                }

                currentShape.getScreenVertex(currentShape.currentFace.i4, nextx, nexty);
                LineTo(theDC, nextx + xOffset, screenHeight - nexty - yOffset);
                if(highlightVertices == 1) {
                    drawBox(theDC, hPointPen, hBlackPen, nextx + xOffset, screenHeight - nexty - yOffset);
                }
                LineTo(theDC, firstx + xOffset, screenHeight - firsty - yOffset);

                currentShape.currentFace++;
            }
        } // if

        // Display the model's name
        ax = currentShape.averageX() + currentShape.minX;
        ay = screenHeight - (currentShape.averageY() + currentShape.minY);
        SetTextColor(theDC, RGB(255,0,0));   // red pen
        SetBkColor(theDC, backgroundColor); 
        if(!modelName.equals(" ")) {
            TextOut(theDC,(int)ax + xOffset, (int)ay - yOffset, modelName, modelName.length());
        }

        ReleaseDC(theWindow,theDC);
        DeleteObject(hBlackPen);
        DeleteObject(hWhitePen);
        DeleteObject(hPointPen);

        SetROP2(theDC, nDrawMode);
    } // drawStill


    // TODO: Not a method
    public void setPalette() {
        LPLOGPALETTE myPalette = 0;
        HDC hdc;
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
        GlobalUnlock((HANDLE)myPalette);

        // Activate the new palette
        hdc = GetDC(GetActiveWindow());
        SelectPalette( hdc, hNewPal, false );
        RealizePalette( hdc );
        ReleaseDC(GetActiveWindow(), hdc);
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
        // creates a cutout image, alpha image, and shape file from
        // a boundary traced by the user
        int numVertices = aShape.getNumVertices();
        void *nullPointer;
        nullPointer = calloc(numVertices, sizeof(POINT));
        POINT thePoints = (POINT)nullPointer;

        aShape.initCurrentVertex();
        for(int myIndex = 0; myIndex < numVertices; myIndex++) {
            thePoints.x = aShape.currentVertex.x;
            thePoints.y = aShape.currentVertex.y;
            aShape.currentVertex++;
            thePoints++;
        }

        thePoints =(POINT)nullPointer;
        // Memory, write, 1 bit
        MemImage maskImage = new MemImage("OneBit.bmp", imageHeight, imageWidth, RANDOM, 'W', ONEBITMONOCHROME);
        if(!maskImage.isValid()) {
            Globals.statusPrint("prepareCutout: Couldn't create 1 bit mask image");
            return 1;
        }

        HDC hdc = GetDC(HWindow);
        int myStatus = maskImage.drawMask(hdc, thePoints, numVertices);
        ReleaseDC(HWindow, hdc);

        if (myStatus != 0) {
            String msgText;
            msgText = "prepareCutout: Couldn't create 1 bit mask " + myStatus;
            Globals.statusPrint(msgText);
            maskImage.close();
            return 2;
        }

        // Create an unpacked (8 bit) mask image
        Globals.statusPrint("prepareCutout: Unpacking Mask Image...");
        MemImage unpackedMaskImage = new MemImage(imageHeight, imageWidth);
        if(!unpackedMaskImage.isValid()) {
            Globals.statusPrint("makeMask: Not Enough memory to create unpacked mask image");
            return 1;
        }

        maskImage.unPack(unpackedMaskImage);
        if(!unpackedMaskImage.isValid()) {
            Globals.statusPrint("prepareCutout: unpack image operation was aborted");
            return 1;
        }

        maskImage.close();
        remove("OneBit.bmp");
        
        Globals.statusPrint("Removing borders from mask image...");
        MemImage originalImage = new MemImage(imageFileName, 0, 0, SEQUENTIAL, 'R', 0);
        if(!originalImage.isValid()) {
            Globals.statusPrint("prepareCutout: Unable to open original image");
            return 1;
        }

        myStatus = Globals.createCutout(originalImage, unpackedMaskImage, cutoutName, aShape);
        if(myStatus != 0) {
            Globals.statusPrint("prepareCutout: Unable to prepare mask and image cutouts");
            return 1;
        }

        return 0;
    } // prepareCutout


    // TODO: Not A method
    int maskFromShape(Shape3d inShape, MemImage maskImage) {
        // Create a mask image from a 2D boundary.
        // The generated image is displayed on the Cmainframe window.
        // This function could be modified use a memory DC instead.
        int imageHeight = maskImage.getHeight();
        int imageWidth  = maskImage.getWidth();
        int numVertices = inShape.getNumVertices();
        
        CWnd theWindow = AfxGetMainWnd();
        HWND HWindow = theWindow.m_hWnd;

        // Copy the shape vertices into a structure compatible with the Windows
        // drawing functions
        void *nullPointer;
        nullPointer = calloc(numVertices, sizeof(POINT));
        POINT thePoints = (POINT)nullPointer;
        inShape.initCurrentVertex();

        for(int myIndex = 0; myIndex < numVertices; myIndex++) {
            thePoints.x = inShape.currentVertex.x;
            thePoints.y = inShape.currentVertex.y;
            inShape.currentVertex++;
            thePoints++;
        }
        thePoints =(POINT)nullPointer;

        MemImage tempMaskImage = new MemImage("OneBit.bmp", imageHeight, imageWidth,
          RANDOM, 'W', ONEBITMONOCHROME);  //Memory, write, 1 bit
        if(!tempMaskImage.isValid()) {
            Globals.statusPrint("maskFromShape: Couldn't create 1 bit mask image");
            return 1;
        }

        HDC hdc = GetDC(HWindow);
        if(hdc == null) {
            Globals.statusPrint("maskFromShape: Unable to get device context");
            return -1;
        }

        HDC memoryDC = CreateCompatibleDC(hdc);

        // Use the Windows Polygon function to draw the points into the DC
        int myStatus = tempMaskImage.drawMask(memoryDC, thePoints, numVertices);
        DeleteDC(memoryDC);
        ReleaseDC(HWindow, hdc);

        if (myStatus != 0) {
            String msgText;
            msgText = "maskFromShape: Couldn't create 1 bit mask " + myStatus;
            Globals.statusPrint(msgText);
            tempMaskImage.close();
            return 2;
        }

        // Create an unpacked (8 bit) mask image
        // statusPrint("maskFromShape: Unpacking Mask Image...");
        tempMaskImage.unPack(maskImage);
        remove("OneBit.bmp");
        return 0;
    } // maskFromShape


    public boolean isValid() {
        return this.valid;
    } // isValid


    // TODO: Not a method
    void assembleName(String inputName, char theSuffix, String outputName) {
        String drive, dir, file, ext;

        //break a pathname into its components, add a suffix then put it back together again
        _splitpath(inputName, drive, dir, file, ext);
        int theLength = file.length();
        if(theLength > 0) {
            *(file+theLength-1) = theSuffix;  // Substitute a suffix
        }

        _makepath(outputName, drive, dir, file, ext);
    } // assembleName


    void previewMesh(HDC theDC, String modelName, float xOff, float yOff, 
    int screenHeight, int screenWidth) {
        // Create the line buffer data structure
        int *xBuffer, *yBuffer,  *xTemp, *yTemp;
        byte *iBuffer, *iTemp, *iPrev1, *iPrev2;
      
        byte iTemp1, iTemp2;
        int xTemp1, yTemp1, xTemp2, yTemp2;

        int *xPrev1, *yPrev1, *xPrev2, *yPrev2;
        int sxMin, sxMax, syMin, syMax;	 //projected mesh bounding box

        if (
        !xImage.isValid() ||
        !textureImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("previewMesh: One or more poly-mesh image is not valid");
            return;
        }

        xBuffer = (int)malloc(xImage.getWidth() * sizeof(int));
        if (xBuffer == null) {
            Globals.statusPrint("previewMesh: Not enough memory for xBuffer");
            return;
        }

        yBuffer = (int)malloc(yImage.getWidth() * sizeof(int));
        if (yBuffer == null) {
            Globals.statusPrint("previewMesh: Not enough memory for yBuffer");
            return;
        }

        iBuffer = (byte)malloc(textureImage.getWidth() * sizeof(byte));
        if (iBuffer == null) {
            Globals.statusPrint("previewMesh: Not enough memory for iBuffer");
            return;
        }

        xTemp = xBuffer;
        yTemp = yBuffer;
        iTemp = iBuffer;

        HPEN hPen;
        SetMapMode(theDC, MM_TEXT); //Logical units=physical units = pixel
        hPen = CreatePen(PS_SOLID, 1, RGB(0, 0, 0)); //this grey pen matches the Win95 background color
        SelectObject(theDC, hPen);
        int xOffset = (int)(xOff + 0.5f);
        int yOffset = (int)(yOff + 0.5f);

        int imHeight = textureImage.getHeight();
        int imWidth  = textureImage.getWidth();
        int row, col;
        float x1,y1,z1, tx, ty, tz;
        byte i1;
        int sx1, sy1;
        float refX, refY, refZ;
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

                // project to the screen
                m_matrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                    refX, refY, refZ, screenHeight, screenWidth, tx, ty, tz);
                
                if((row == 1) && (col == 1)) {	//initialize the projected mesh bounding box
                    sxMin = sxMax = sx1;
                    syMin = syMax = sy1;
                }
                
                if(row == 1) {	 //load up the x, y, and iTemp buffers on the first row
                    *xTemp = sx1;
                    xTemp++;
                    *yTemp = sy1;
                    yTemp++;
                    *iTemp = i1;
                    iTemp++;

                    if (sx1 < sxMin) sxMin = sx1;
                    if (sx1 > sxMax) sxMax = sx1;
                    if (sy1 < syMin) syMin = sy1;
                    if (sy1 > syMax) syMax = sy1;
                }
                
                if ((row > 1) && (col == 1)) {  //first column of every row after the first row
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    xPrev1 = xBuffer;
                    yPrev1 = yBuffer;
                    xPrev2 = xBuffer;
                    yPrev2 = yBuffer;
                    xPrev2++;
                    yPrev2++;
                    iTemp1 = i1;
                    iPrev1 = iBuffer;
                    iPrev2 = iBuffer;
                    iPrev2++;

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
                    if((iPrev2 != 0) && (iTemp2 != 0) && (iTemp1 != 0)) {
                          if(row == (meshIncrement + 1)) {   // draw the first row
                              MoveToEx(theDC, xPrev1, screenHeight - (yPrev1), 0L);
                              LineTo(theDC, xPrev2, screenHeight - (yPrev2));
                          }
                          if(col == (meshIncrement + 1)) {  // draw the first column
                              MoveToEx(theDC, xPrev1, screenHeight - (yPrev1), 0L);
                              LineTo(theDC, xTemp1, screenHeight - yTemp1);
                          }

                          MoveToEx(theDC, xPrev2, screenHeight - (yPrev2), 0L);
                          LineTo(theDC, xTemp2, screenHeight - yTemp2);
                          LineTo(theDC, xTemp1, screenHeight - yTemp1);
                    }

                    *xPrev1 = xTemp1;		//advance pointers
                    *yPrev1 = yTemp1;
                    *iPrev1 = iTemp1;
                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;
                    xPrev1++;
                    yPrev1++;
                    iPrev1++;
                    xPrev2++;
                    yPrev2++;
                    iPrev2++;

                    if (sx1 < sxMin) sxMin = sx1;
                    if (sx1 > sxMax) sxMax = sx1;
                    if (sy1 < syMin) syMin = sy1;
                    if (sy1 > syMax) syMax = sy1;
                }
            }
        }

        //  display the Name in the center of the projected model
        int xText = sxMin + ((sxMax - sxMin)/2);
        int yText = screenHeight - (syMin + ((syMax - syMin)/2));
        SetTextColor(theDC, RGB(255, 0, 0)); // red
        TextOut(theDC, xText, yText, modelName, modelName.length());

        DeleteObject(hPen);
    } // previewMesh


    public int renderMesh(MemImage outputImage, MemImage inputImage, boolean blendIndicator) {
        // Create the line buffer data structure
        int *xBuffer, *yBuffer,  *xTemp, *yTemp;
        byte *iBuffer, *iTemp, iTemp1, iTemp2, *iPrev1, *iPrev2;
        int xTemp1, yTemp1, xTemp2, yTemp2;
        int *xPrev1, *yPrev1, *xPrev2, *yPrev2;
        char msgBuffer[80];

        if (
        !xImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("renderMesh: One or more poly-mesh image is not valid");
            return -1;
        }

        xBuffer = (int)malloc(xImage.getWidth() * sizeof(int));
        if (xBuffer == NULL) {
            Globals.statusPrint("renderMesh: Not enough memory for xBuffer");
            return -1;
        }

        yBuffer = (int)malloc(yImage.getWidth() * sizeof(int));
        if (yBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for yBuffer");
            return -1;
        }

        iBuffer = (byte)malloc(inputImage.getWidth() * sizeof(byte));
        if (iBuffer == null) {
            Globals.statusPrint("renderMesh: Not enough memory for iBuffer");
            return -1;
        }

        xTemp = xBuffer;
        yTemp = yBuffer;
        iTemp = iBuffer;

        int imHeight  = inputImage.getHeight();
        int imWidth   = inputImage.getWidth();
        int outHeight = outputImage.getHeight();
        int outWidth  = outputImage.getWidth();
        int row, col;
        float x1,y1,z1, tx, ty, tz;
        byte i1;
        int sx1, sy1;
        float refX, refY, refZ;
        int meshIncrement = 6;

        // Get the model's referencePoint
        currentShape.getReferencePoint(refX, refY, refZ);

        // Render the mesh
        m_matrix.display("renderMesh");

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                i1 = inputImage.getMPixel(col, row);

                // project to the screen
                m_matrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, 
                  refX, refY, refZ, outHeight, outWidth, tx, ty, tz);
                if(row == 1) {
                    *xTemp = sx1;
                    xTemp++;

                    *yTemp = sy1;
                    yTemp++;

                    *iTemp = i1;
                    iTemp++;
                }
              
                if ((row > 1) && (col == 1)) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1 = xBuffer;
                    yPrev1 = yBuffer;

                    xPrev2 = xBuffer;
                    yPrev2 = yBuffer;

                    xPrev2++;
                    yPrev2++;

                    iPrev1 = iBuffer;
                    iPrev2 = iBuffer;
                    iPrev2++;
                }
      
                if ((row > 1) && (col > 1)) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;
                    //                     
                    // render the quadrangle
                    outputImage.fillPolyz(xPrev1, yPrev1, iPrev1, 0.0,
                                          xPrev2, yPrev2, iPrev2, 0.0,
                                          xTemp2, yTemp2, iTemp2, 0.0,
                                          xTemp1, yTemp1, iTemp1, 0.0, null); 
                    *xPrev1 = xTemp1;
                    *yPrev1 = yTemp1;
                    *iPrev1 = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1++;
                    yPrev1++;

                    xPrev2++;
                    yPrev2++;

                    iPrev1++;
                    iPrev2++;
                }
            }
            continue;           //  this line for debugging purposes
        }

        return 0;
    } // renderMesh


    public int renderMeshz(MemImage outputImage, MemImage maskImage, 
    MemImage inputImage, MemImage zBuffer, float vx, float vy, float vz) {
        String msgText;

        //  create the line buffer data structures
        int *xBuffer, *yBuffer,  *xTemp, *yTemp;
        float *wxBuffer, *wyBuffer, *wzBuffer, *dBuffer;
        float *wxTemp, *wyTemp, *wzTemp;
        byte *iBuffer, *iTemp, iTemp1, iTemp2, *iPrev1, *iPrev2;
        int xTemp1, yTemp1, xTemp2, yTemp2;
        int *xPrev1, *yPrev1, *xPrev2, *yPrev2;
        float *dTemp, dTemp1, dTemp2, *dPrev1, *dPrev2;

        if (
        !xImage.isValid() ||
        !yImage.isValid() ||
        !zImage.isValid()) {
            Globals.statusPrint("renderMeshz: One or more poly-mesh image is not valid");
            return -1;
        }

        xBuffer = (int)malloc(xImage.getWidth() * sizeof(int));
        if (xBuffer == null) {
            Globals.statusPrint("renderMeshz: Not enough memory for xBuffer");
            return -1;
        }

        yBuffer = (int)malloc(yImage.getWidth() * sizeof(int));
        if (yBuffer == null) {
            Globals.statusPrint("renderMeshz: Not enough memory for yBuffer");
            return -1;
        }

        dBuffer = (float)malloc(zImage.getWidth() * sizeof(float));
        if (dBuffer == null) {
            Globals.statusPrint("renderMeshz: Not enough memory for distance Buffer");
            return -1;
        }

        iBuffer = (byte)malloc(inputImage.getWidth() * sizeof(BYTE));
        if (iBuffer == null) {
            Globals.statusPrint("renderMeshz: Not enough memory for iBuffer");
            return -1;
        }

        MemImage midImage = new MemImage(outputImage.getHeight(), outputImage.getWidth());
        if(!midImage.isValid()) {
            Globals.statusPrint("renderMeshZ: Not enough memory to open intermediate image");
            return -1;
        }

        midImage.setFileName("midImage");
        MemImage midZImage = new MemImage(outputImage.getHeight(), outputImage.getWidth(), 32);
        if(!midZImage.isValid()) {
            Globals.statusPrint("renderMeshZ: Not enough memory to open intermediate Z image");
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

        sprintf(msgText, "renderMeshz: Viewer location: vx: %f, vy: %f, vz: %f", vx, vy, vz);
        statusPrint(msgText);
        xTemp = xBuffer;
        yTemp = yBuffer;
        iTemp = iBuffer;
        dTemp = dBuffer;

        int row, col;
        float x1,y1,z1;
        byte i1;
        int sx1, sy1;
        float refX, refY, refZ;
        Point3d c1 = new Point3d();
        Point3d c2 = new Point3d();
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        Point3d centroid = new Point3d(); 
        Point3d lightSource = new Point3d();  //a lightsource location
        lightSource.x = 100.0f;
        lightSource.y = 255.0f;
        lightSource.z =  20.0f;
        Vect.vectorNormalize(lightSource);
        Point3d np1, np2, nc1, nc2;

        // Get the model's referencePoint
        currentShape.getReferencePoint(refX, refY, refZ);
        float tx, ty, tz;

        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                x1 = xImage.getMPixel32(col, row);
                y1 = yImage.getMPixel32(col, row);
                z1 = zImage.getMPixel32(col, row);
                i1 = inputImage.getMPixel(col, row);

                // Project to the screen
                m_matrix.transformAndProjectPoint(x1, y1, z1, sx1, sy1, refX, refY, refZ, outHeight, outWidth, tx, ty, tz);

                if(row == 1) {
                    *xTemp = sx1;
                    xTemp++;

                    *yTemp = sy1;
                    yTemp++;

                    *iTemp = i1;
                    iTemp++;

                    *dTemp = Globals.getDistance3d(tx, ty, tz, vx, vy, vz);
                    dTemp++;
                }
              
                if (row > 1 && col == 1) {
                    xTemp1 = sx1;
                    yTemp1 = sy1;
                    iTemp1 = i1;

                    xPrev1 = xBuffer;
                    yPrev1 = yBuffer;

                    xPrev2 = xBuffer;
                    yPrev2 = yBuffer;

                    xPrev2++;
                    yPrev2++;

                    iPrev1 = iBuffer;
                    iPrev2 = iBuffer;
                    iPrev2++;

                    dPrev1 = dBuffer;
                    dPrev2 = dBuffer;
                    dPrev2++;

                    dTemp1 = Globals.getDistance3d(tx, ty, tz, vx, vy, vz);
                }
      
                if (row > 1 && col > 1) {
                    xTemp2 = sx1;
                    yTemp2 = sy1;
                    iTemp2 = i1;

                    dTemp2 = Globals.getDistance3d(tx, ty, tz, vx, vy, vz);
                                  
                    // render the quadrangle intensities
                    //                     
                    // render the quadrangle distances and update the intermediate zBuffer

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

                        centroid.x = (xMax + xMin) / 2.0f;
                        centroid.y = (yMax + yMin) / 2.0f;
                        centroid.z = (zMax + zMin) / 2.0f;
                        float dCentroid = Globals.getDistance3d(lightSource.x, lightSource.y, lightSource.z, 
                            centroid.x, centroid.y, centroid.z);

                        Vect.getNormal2(np1, p1, centroid, p2);
                        Vect.getNormal2(np2, p2, centroid, c2);
                        Vect.getNormal2(nc1, c1, centroid, c2);
                        Vect.getNormal2(nc2, p1, centroid, c1);
                        Vect.vectorNormalize(np1);
                        Vect.vectorNormalize(np2);
                        Vect.vectorNormalize(nc1);
                        Vect.vectorNormalize(nc2);

                        //  kd     the coefficient of reflection or reflectivity of the surface material
                        //         highly reflective = 1, highly absorptive = 0
                        //	Ip	   the intensity of the light source
                        //  Ia     the ambient intensity at the surface
                        //  N      The surface Normal (unit vector)
                        //  L      The direction of the light source (unit vector)
                        //  d      the distance between the surface and the light source
                        float kd = 0.95f;
                        int Ip = 200;
                        float ip1, ip2, ic1, ic2;

                        ip1 = Gloals.lightModel(kd, Ip, 100.0, np1, lightSource, dCentroid);
                        ip2 = Gloals.lightModel(kd, Ip, 100.0, np2, lightSource, dCentroid);
                        ic1 = Gloals.lightModel(kd, Ip, 100.0, nc1, lightSource, dCentroid);
                        ic2 = Gloals.lightModel(kd, Ip, 100.0, nc2, lightSource, dCentroid);

                        midImage.fillPolyz( 
                            xPrev1, yPrev1, ip2, dPrev1,
                            xPrev2, yPrev2, ip2, dPrev2,
                            xTemp2, yTemp2, ic2, dTemp2,
                            xTemp1, yTemp1, ic2, dTemp1, midZImage);
                        // end shading
                    }	else {
                        midImage.fillPolyz( 
                            xPrev1, yPrev1, iPrev1, dPrev1,
                            xPrev2, yPrev2, iPrev2, dPrev2,
                            xTemp2, yTemp2, iTemp2, dTemp2,
                            xTemp1, yTemp1, iTemp1, dTemp1, midZImage);
                    }


                    *xPrev1 = xTemp1;
                    *yPrev1 = yTemp1;
                    *iPrev1 = iTemp1;

                    xTemp1 = xTemp2;
                    yTemp1 = yTemp2;
                    iTemp1 = iTemp2;

                    xPrev1++;
                    yPrev1++;

                    xPrev2++;
                    yPrev2++;

                    iPrev1++;
                    iPrev2++;

                    *dPrev1 = dTemp1;
                    dTemp1 = dTemp2;
                    dPrev1++;
                    dPrev2++;
                }
            }
        }

        //
        // Composite the rendered quad mesh into the output scene
        //

        // First, create the matte image
        MemImage matte = new MemImage(outputImage.getHeight(), outputImage.getWidth());
        midImage.createAlphaImage(matte);
        Globals.statusPrint("Creating a matte for the rendered quad mesh");
        matte.alphaSmooth5();

        int myStatus;
        float alphaScale = 1.0f;

        myStatus = Globals.blendz(midImage, matte, midZImage, zBuffer, outputImage, alphaScale);

        return 0;
    } // renderMeshz


    public void transformAndProject(TMatrix aMatrix, int outHeight, int outWidth,
    boolean externalCentroid,
    float centroidX, float centroidY, float centroidZ) {
        if((this.modelType == SHAPE) || (this.modelType == IMAGE)) {
            aMatrix.transformAndProject(currentShape, outHeight, outWidth, 
              externalCentroid,
              centroidX, centroidY, centroidZ);
        } else {
            // Copy the transformation matrix for later use
            m_matrix.copy(aMatrix);
        }
    } // transformAndProject


    public void transformAndProject(TMatrix aMatrix, int outHeight, int outWidth) {
        this.transformAndProject(aMatrix, outHeight, outWidth, 0, 0.0f, 0.0f, 0.0f);
    }

    // TODO: Not a method
    void transformAndProjectPoint2(TMatrix aMatrix, float x, float y, float z, 
    Integer sx, Integer sy, 
    float refX, float refY, float refZ, 
    int outHeight, int outWidth) {

        //  Use Wein87 projection. described in Foley pp256)
        Float tx = 0.0f, ty = 0.0f, tz = 0.0f;
        x -= refX;
        y -= refY;
        z -= refZ;
        aMatrix.transformPoint(x, y, z, tx, ty, tz);
        x += refX;
        y += refY;
        z += refZ;

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

        Point3d d;
        Vect.vectorSubtract(d, COP, p);
        float Q = Globals.getDistance3d(COP.x, COP.y, COP.z, p.x, p.y, p.z);
        float denom = ((p.z - tz) / (Q * d.z)) + 1.0f;
        float xp = (tx - (tz * d.x / d.z) + (p.z * d.x / d.z)) / denom;
        float yp = (ty - (tz * d.y / d.z) + (p.z * d.y / d.z)) / denom;
        sx = (int)(xp + 0.5f);
        sy = (int)(yp + 0.5f);
    } // transformAndProjectPoint2


    // TODO: Not a method
    void drawBox(HDC theDC, HPEN hPointPen, HPEN hBlackPen, int x, int y) {
        //  Draw a box 2 * offset + 1 pixels wide and high around the point x,y
        //
        int offset = 2;
        SelectObject(theDC, hPointPen);
        MoveToEx(theDC,x - offset, y + offset, 0L);
        LineTo(theDC, x + offset, y + offset);
        LineTo(theDC, x + offset, y - offset);
        LineTo(theDC, x - offset, y - offset);
        LineTo(theDC, x - offset, y + offset);
        MoveToEx(theDC, x, y, 0L);
        SelectObject(theDC, hBlackPen);
    } // drawBox


    public int renderShape(MemImage outputImage, boolean blendIndicator) {
        if(currentShape.getNumFaces() == 0) {
            Globals.statusPrint("renderShape: shape has no faces - cannot be rendered");
            return 0;
        }

        int sx1, sy1, sx2, sy2, sx3, sy3, sx4, sy4;
        byte I1p, I2p, I3p, I4p;
        int index, index1, index2, index3, index4, myStatus;

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
              myStatus = outputImage.fillPolyz(sx1, sy1, I1p, 0.0f, 
                          sx2, sy2, I2p, 0.0f, 
                          sx3, sy3, I3p, 0.0f, 
                          sx4, sy4, I4p, 0.0f, null);
            } else { 
                //its a triangle
            }
            currentShape.currentFace++;
        } 

        return 0;
    } // renderShape


    public int renderShapez(MemImage outputImage, 
    MemImage alphaImage, MemImage zBuffer, 
    float vx, float vy, float vz) {
        
        //  The shape object is already transformed upon entry to this procedure
        int sx1, sy1, sx2, sy2, sx3, sy3, sx4, sy4;
        byte I1p, I2p, I3p, I4p;
        float I1d, I2d, I3d, I4d;
        int index, index1, index2, index3, index4, myStatus;
        float tx1, ty1, tz1, tx2, ty2, tz2, tx3, ty3, tz3, tx4, ty4, tz4;
        Point3d p1, p2, p3, p4;

        if(currentShape.getNumFaces() == 0) {
            Globals.statusPrint("renderShapez: shape has no faces - cannot be rendered");
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
                I1d = Globals.getDistance3d(vx, vy, vz, tx1, ty1, tz1);
                I2d = Globals.getDistance3d(vx, vy, vz, tx2, ty2, tz2);
                I3d = Globals.getDistance3d(vx, vy, vz, tx3, ty3, tz3);
                I4d = Globals.getDistance3d(vx, vy, vz, tx4, ty4, tz4);
                p1.x = tx1; p1.y = ty1; p1.z = tz1;
                p2.x = tx2; p2.y = ty2; p2.z = tz2;
                p3.x = tx3; p3.y = ty3; p3.z = tz3;
                p4.x = tx4; p4.y = ty4; p4.z = tz4;
                I1p = Globals.getLight(p1, p2, p3, p4);

                myStatus = outputImage.fillPolyz(
                                    sx1 + xo, sy1 + yo, I1p, I1d,
                                    sx2 + xo, sy2 + yo, I1p, I2d, 
                                    sx3 + xo, sy3 + yo, I1p, I3d, 
                                    sx4 + xo, sy4 + yo, I1p, I4d,
                                    zBuffer);
            } else { 
                //its a triangle
            }
            currentShape.currentFace++;
        }

        return 0;
    } // renderShapez
} // class RenderObject