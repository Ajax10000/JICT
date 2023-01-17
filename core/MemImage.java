package core;

import globals.Globals;

public class MemImage {
    boolean ictdebug = false;
    protected HANDLE fp;            // Used with memimg32.cpp
    protected String savedFileName;   // The last associated pathname
    protected int imageHeight;    // Height in pixels
    protected int imageWidth;     // Width in pixels
    protected int bitsPerPixel;   // Bits per pixel
    protected int paddedWidth;    // Physical width of the image in bytes (multiple of 4)
    protected int pads;           // Difference between imagewidth and paddedwidth (bytes)
    protected int accessMode;     // SEQUENTIAL or RANDOM
    protected int theColorSpec;   // Indicates the desired color mapping

    // State indicator.  1 if successful, else 0
    // I changed this from int to boolean
    protected boolean valid;          
    protected byte[] bytes;         // Pointer to the image bytes

    // These were defined in MEMIMAGE.H
    public static final int REDCOLOR = 1;
    public static final int GREENCOLOR = 2;
    public static final int BLUECOLOR = 3;
    public static final int EIGHTBITMONOCHROME = 2;
    public static final int A32BIT = 4;
    public static final int RGBCOLOR = 5;
    public static final int ONEBITMONOCHROME = 6;
    
    public static final int CHROMAVALUE  = 0;
    public static final int CHROMARED    = 0;
    public static final int CHROMAGREEN  = 0;
    public static final int CHROMABLUE   = 0;

    // These were defined in MEMIMAGE.H
    // Used in: 
    //     the constructor that takes 6 parameters (to check parameter imAccessMode)
    //     finalize
    //     clear
    //     clearRGB
    //     clearRGBRange
    //     init32
    //     scaleTo8
    //     getMPixel(int x, int y, char aColor)
    //     getMPixelRGB
    //     setMPixelRGB
    //     setMPixel
    //     setMPixelA
    //     getMPixelA
    //     getMPixel(int x, int y)
    //     setMPixel32
    //     getMPixel32
    //     readBMP
    public static final int SEQUENTIAL = 1;

    // Used in: 
    // the constructor that takes 6 parameters
    public static final int RANDOM = 0;

    // This value came from ICT20.H
    // Used in: 
    //     scaleTo8
    public static final float ZBUFFERMAXVALUE = 2.0E31f;
/*
protected:
  void allocate(int height, int widthInPixels);
*/

/*
public:
  memImage (int height, int width, int bitsPerPixel=8);
  memImage (memImage *);
  memImage (char *fileName, int imLength, int imWidth,
            int imAccessMode, char rw, int colorSpec);
  int readNextRow();
  int writeNextRow();
  void clear();
  void init32(float aValue);
  void display(HDC dc, int outWidth, int outHeight);
  void close();
  int saveAs8(char *OutImagePathName);
  int scaleTo8(memImage *scaledImage);
  int histogram();

  HBITMAP getBmp();
  int getHeight();
  int getWidth();
  int getAccessMode();
  int getColorSpec();
  int getBitsPerPixel();
  unsigned int getImageSizeInBytes();
  BYTE  *getBytes();
  int isValid();
  int alphaSmooth3();
  int alphaSmooth5();
  int alphaSmooth7();
  int createAlphaImage(memImage *outImage);
  int copy(memImage *outImage, int xoffset, int yoffset);
  int fastCopy(memImage *outImage, int xOffset, int yOffset);
  int copy8To24(memImage *outImage);

  int drawMask(HDC dc, POINT far *thePoints, int numVertices);
  int unPack(memImage *unpackedImage);
  BYTE getMPixel(int x, int y);
  BYTE getAverageMPixel (int x, int y);
  BYTE getMPixel(int x, int y, char aColor);  
  float getMPixel32 (int x, int y);
  int setMPixel32 (int x, int y, float aValue);
  int setMPixel(int x, int y, BYTE value);
  int setMPixelA (float x, float y, BYTE value);
  BYTE getMPixelA (float x, float y);
  int getMPixelRGB(int x, int y, BYTE *red, BYTE *green, BYTE *blue);
  int setMPixelRGB(int x, int y, BYTE red, BYTE green, BYTE blue);
  int clearRGB(BYTE red, BYTE green, BYTE blue);
  int clearRGBRange(BYTE redLow, BYTE redHigh, BYTE greenLow, BYTE greenHigh, 
                      BYTE blueLow, BYTE blueHigh);
  int writeBMP(char *fileName);
  int readBMP(char *fileName, int theColor);
  int fillPolyz(int I1x, int I1y, float I1p, float I1d, 
                       int I2x, int I2y, float I2p, float I2d, 
                       int I3x, int I3y, float I3p, float I3d, 
                       int I4x, int I4y, float I4p, float I4d,
                       memImage *zBuffer);
  int adjustColor(BYTE desiredRed, BYTE desiredGreen, BYTE desiredBlue,
   BYTE *midRed, BYTE *midGreen, BYTE *midBlue, memImage *outImagechar,
   char *adjustmentType, int inputImageColor);
  int adjustImageBorder(char *outPath);
  int printValue(int x, int y);
  void setFileName(char *fileName);
  int clearRectangle(int startX, int startY, int endX, int endY);
  int getBoundingBox(int *xBeg, int *xEnd, int *yBeg, int *yEnd);
 virtual ~memImage();
 */


    // Called from:
    //     Globals.motionBlur
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    //     RenderObject.prepareCutout
    //     SceneList.previewStill
    //     MainFrame.onToolsWarpImage
    public MemImage(String psFileName, int imHeight, int imWidth, int imAccessMode, 
    char rw, int piColorSpec) {
        String msgBuffer;

        if (
        piColorSpec != ONEBITMONOCHROME && 
        piColorSpec != REDCOLOR &&
        piColorSpec != GREENCOLOR && 
        piColorSpec != BLUECOLOR &&
        piColorSpec != RGBCOLOR && 
        piColorSpec != EIGHTBITMONOCHROME &&
        piColorSpec != 0 && 
        piColorSpec != A32BIT) {
            msgBuffer = "MemImage Constructor 1: ColorSpec not valid - " + piColorSpec;
            Globals.statusPrint(msgBuffer);
            this.valid = false;
            return;
        }

        if((rw != 'R') && (rw != 'r') && (rw != 'W'() && (rw != 'w')) {
            msgBuffer = "MemImage Constructor 1: rw must be R or W - " + rw;
            Globals.statusPrint(msgBuffer);
            this.valid = false;
            return;
        }

        if((imAccessMode != RANDOM) && (imAccessMode != SEQUENTIAL)) {
            msgBuffer = "MemImage Constructor 1: accessMode must be RANDOM or SEQUENTIAL - " + imAccessMode;
            Globals.statusPrint(msgBuffer);
            this.valid = false;
            return;
        }
        if(
        (rw == 'W' || rw == 'w') && 
        (imHeight <= 0 || imWidth <= 0 || piColorSpec == 0)) {
            Globals.statusPrint("MemImage Constructor 1: length, width and colorSpec must be > 0 for write access");
            this.valid = false;
            return;
        }

        Integer myBitsPerPixel = 0; 
        int myStatus;
        this.valid = true;
        myBitsPerPixel = mapColorSpecToBitsPerPixel(piColorSpec);
        this.savedFileName = psFileName;

        // Get a preview of the file 
        Integer myHeight = 0, myWidth = 0;
        if(imHeight == 0 || imWidth == 0 || piColorSpec == 0) {
            myStatus = Globals.readBMPHeader(psFileName, myHeight, myWidth, myBitsPerPixel);
            if(myStatus != 0) {
                this.valid = false;
                Globals.statusPrint("MemImage Constructor 1: Unable to open BMP header for read access");
                return;
            }

            imHeight = myHeight;
            imWidth = myWidth;
            if(piColorSpec == 0) {
                piColorSpec = mapBitsPerPixelToColorSpec(myBitsPerPixel);
            }
        }

        //  Assign the MemImage properties
        this.imageHeight = imHeight;
        this.imageWidth = imWidth;
        this.bitsPerPixel = myBitsPerPixel;
        this.theColorSpec = piColorSpec;
        this.accessMode = imAccessMode;

        if((rw == 'W') || (rw == 'w')) {
            int numRows = this.imageHeight;
            if (this.accessMode == SEQUENTIAL) {
                numRows = 1;
            }

            allocate(numRows, this.imageWidth);
            if(!isValid()) {
                Globals.statusPrint("MemImage Constructor 1: Could not allocate memory for write");
            }
        }

        if(this.accessMode == SEQUENTIAL) {
            //  Write or Read the BMP header
            if(rw == 'W' || rw == 'w') {
                myStatus = writeBMP(psFileName);
            }

            if(rw == 'R' || rw == 'r') {
                myStatus = readBMP(psFileName, piColorSpec);
            }

            if(myStatus != 0) {
                this.valid = false; // Indicate the file could not be opened
            }
        }

        if(this.accessMode == RANDOM) {
            if((rw == 'W' || rw == 'w') && (piColorSpec == RGBCOLOR)) {
                Globals.statusPrint("MemImage Constructor 1: RANDOM 24 bit BMPs not supported for writing");
                this.valid = false;
                return;
            }
            if((rw == 'W' || rw == 'w') && (piColorSpec != RGBCOLOR)) {
                myStatus = writeBMP(psFileName);
            }

            if(rw == 'R' || rw == 'r') {
                myStatus = Globals.readBMPHeader(psFileName, myHeight, myWidth, myBitsPerPixel);
                if(myStatus != 0) {
                    this.valid = false; // Indicate that the file could not be opened
                    Globals.statusPrint("MemImage Constructor 1: Unable to open BMP header");
                    return;
                }

                readBMP(psFileName, this.theColorSpec);
                if(myStatus != 0) {
                    this.valid = false; // Indicate the file could not be opened
                }
            }
        }
        
        if (ictdebug) {
            msgBuffer = "MemImage Constructor 1: Size of MemImage: " + sizeofLowerLimit();
            Globals.statusPrint(msgBuffer);
        }
    } // MemImage ctor


    // Called from: 
    //     saveAs8
    //     Globals.motionBlur
    //     SceneList.render
    //     MainFrame.onToolsWarpImage
    //     RenderObject.renderMeshz
    //     Texture.createTexture
    public MemImage(int height, int width, int aBitsPerPixel) {
        //  Allocates a memory resident 8 bit image by default.
        this.valid = false;
        this.accessMode    = RANDOM;
        this.imageHeight   = height;
        this.imageWidth    = width;
        this.theColorSpec  = EIGHTBITMONOCHROME;
        this.savedFileName = "No Image File Name";
        this.bitsPerPixel  = aBitsPerPixel;
        this.theColorSpec  = mapBitsPerPixelToColorSpec(this.bitsPerPixel);
        allocate(height, width);

        if (ictdebug) {
            String msgBuffer = "MemImage Constructor 2: Size of MemImage: " + sizeofLowerLimit();
            Globals.statusPrint(msgBuffer);
        }
    } // MemImage ctor


    // Called from:
    //     RenderObject.renderMeshz
    public MemImage(int height, int width) {
        this(height, width, 8);
    } // MemImage ctor


    public MemImage(MemImage m) {
        this.imageHeight   = m.imageHeight;
        this.imageWidth    = m.imageWidth;
        this.bitsPerPixel  = m.bitsPerPixel;
        this.accessMode    = m.accessMode;
        this.theColorSpec  = m.theColorSpec;
        this.savedFileName = m.savedFileName;

        if(m.valid == true) {
            allocate(m.imageHeight, m.imageWidth);
        }

        if (ictdebug) {
            String msgBuffer;
            msgBuffer = "MemImage Constructor 4: Size of MemImage: " + sizeofLowerLimit();
            Globals.statusPrint(msgBuffer);
        }
    } // MemImage ctor


    public void finalize() {
        if((this.accessMode == SEQUENTIAL) && (this.fp > 0)) {
            CloseHandle(this.fp);
        }

        if(ictdebug) {
            Globals.statusPrint("MemImage Destructor");
        }
    } // finalize


    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     MemImage(int height, int width, int aBitsPerPixel)
    //     MemImage(MemImage m) ctor
    //     readBMP
    protected void allocate(int piHeight, int piWidthInPixels) {
        int totalBytes;
        byte[] buffer;
        float bytesPerPixel = (float)this.bitsPerPixel/8.0f;
        float fWidthInBytes = (float)piWidthInPixels * bytesPerPixel;  // 1,24,32 bpp
        int widthInBytes    = (int)fWidthInBytes;

        if (fWidthInBytes > (float)widthInBytes) {
            widthInBytes++;
        }

        this.paddedWidth = (widthInBytes/4)*4;

        if(this.paddedWidth != widthInBytes) {
            this.paddedWidth += 4;
        }

        this.pads = this.paddedWidth - widthInBytes;
        totalBytes = this.paddedWidth * piHeight;

        this.valid = true;
        this.bytes = new byte[totalBytes];            
        clear();  // Clear the memory area
    } // allocate


    // Called from:
    //     allocate
    public void clear() {
        int x, y;
        int rows = this.imageHeight;

        if (this.accessMode == SEQUENTIAL) {
            rows = 1;
        }

        int iBytesIdx = 0;
        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.paddedWidth; x++) {	 // paddedWidth is the number of bytes per row
                bytes[iBytesIdx] = 0;
                iBytesIdx++;
            } // for x
        } // for y
    } // clear


    public int clearRGB(byte red, byte green, byte blue) {
        // Clear all pixels whose colors match the specified color
        // Use the fastest image traversal method
        if (this.bitsPerPixel != 24) {
            Globals.statusPrint("MemImage.clearRGB: Bits per pixel must = 24");
            return -1;
        }

        // SetCursor(LoadCursor( null, IDC_WAIT ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int x, y;
        int iBytesIdx = 0; // an index into array this.bytes
        int rows = this.imageHeight;
        if (this.accessMode == SEQUENTIAL) {
            rows = 1;
        }

        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.imageWidth; x++) {
                if(
                (bytes[iBytesIdx]     == blue)  && 
                (bytes[iBytesIdx + 1] == green) && 
                (bytes[iBytesIdx + 2] == red)) {
                    bytes[iBytesIdx]     = 0;
                    bytes[iBytesIdx + 1] = 0;
                    bytes[iBytesIdx + 2] = 0;
                    //counter++;
                }

                iBytesIdx +=3;
            } // for x

            iBytesIdx += pads;
        } // for y

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return 0;
    } // clearRGB


    public int clearRGBRange(byte redLow, byte redHigh, byte greenLow, byte greenHigh, byte blueLow, byte blueHigh) {
        // Clear all pixels whose colors match the specified color
        // Use the fastest traversal method
        if (this.bitsPerPixel != 24) {
            Globals.statusPrint("MemImage.clearRGBRange: Bits per pixel must = 24");
            return -1;
        }

        // SetCursor(LoadCursor( null, IDC_WAIT ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int x, y;
        int iBytesIdx = 0;
        int rows = this.imageHeight;

        if (this.accessMode == SEQUENTIAL) {
            rows = 1;
        }

        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.imageWidth; x++) {
                if(
                (bytes[iBytesIdx]   >= blueLow)  && (bytes[iBytesIdx]   <= blueHigh)  && 
                (bytes[iBytesIdx+1] >= greenLow) && (bytes[iBytesIdx+1] <= greenHigh) && 
                (bytes[iBytesIdx+2] >= redLow)   && (bytes[iBytesIdx+2] <= redHigh)) {
                    bytes[iBytesIdx]   = 0;		   // each color component must be erased
                    bytes[iBytesIdx+1] = 0;
                    bytes[iBytesIdx+2] = 0;
                }

                iBytesIdx += 3;
            } // for x

            iBytesIdx += pads;
        } // for y

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return 0;
    } // clearRGBRange


    // Called from:
    //     RenderObject.renderMeshz
    //     SceneList.render
    public void init32(float aValue) {
        //  Initialize a 32 bit image by setting each pixel to a designated value
        int x, y;
        float *myTemp;
        myTemp = (float *)bytes;

        // Don't need paddedWidth here because each row
        // is by definition a multiple of 4 bytes
        int rows = this.imageHeight;
        int cols = this.imageWidth;  
                                
        if (this.accessMode == SEQUENTIAL) {
            rows = 1;
        }

        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.imageWidth; x++) {
                *myTemp = aValue;
                myTemp++;
            } // for x
        } // for y
    } // init32


    // Called from:
    //     saveAs8
    public int scaleTo8(MemImage scaledImage) {
        // Scale a 32 bit memimage to 8 bits and copy it to the memImage passed in.
        // This version scales to the maximum dynamic range permitted by 8 bits of resolution
        // Consequently, depending on the range of values in the 32 bit image, contrast stretching
        // may occur.  This is desired for the purpose of visualizing a model's range image.
        //
        // Scaling is performed in such a way as to eliminate the effects of the default 
        // max ZBuffer value
        String msgText;
        boolean IGNOREMAXVALUE = true;

        if((scaledImage.bitsPerPixel != 8) || (this.bitsPerPixel != 32)) {
            Globals.statusPrint("MemImage.scaleTo8: Bits per pixel mismatch");
            return 1;
        }
        
        if((scaledImage.getHeight() != getHeight()) || (scaledImage.getWidth() != getWidth())) {
            Globals.statusPrint("MemImage.scaleTo8: Image sizes must be equal");
            return 2;
        }

        int x, y, rows;
        float myTemp, actMax, actMin, desMax, desMin, sFactor;
        myTemp = (float *)this.bytes;

        // Don't need paddedWidth here because each row width
        // is by definition a multiple of 4 bytes
        rows = this.imageHeight;
        desMin = 1.0f;
        desMax = 255.0f;
        if (this.accessMode == SEQUENTIAL) {
            rows = 1;
        }
        
        actMax = actMin = *myTemp;
        if(IGNOREMAXVALUE) {
            actMax = 0.0f;
        }

        // Determine the image min and max values
        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.imageWidth; x++) {
                if(*myTemp < actMin) {
                    actMin = *myTemp;
                }
                if(IGNOREMAXVALUE) {
                    if((*myTemp > actMax) && (*(myTemp) != ZBUFFERMAXVALUE)) {
                        actMax = *myTemp;
                    }
                } else {
                    if(*myTemp > actMax) { 
                        actMax = *myTemp;
                    }
                }

                myTemp++;
            } // for x
        } // for y
        
        msgText = "MemImage.scaleTo8: actual Max: " + actMax + " Min: " + actMin;
        Globals.statusPrint(msgText);

        sFactor = (desMax - desMin) / (actMax - actMin);
        myTemp = (float *)this.bytes;
        byte scaledValue;
        float desValue, actValue;

        // Scale the image
        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.imageWidth; x++) {
                actValue = *myTemp;
                if(IGNOREMAXVALUE) {
                    if(actValue != ZBUFFERMAXVALUE) {
                        desValue = ((actValue - actMin) * sFactor) + desMin;
                        scaledValue = (byte)(desValue + 0.5f);
                        scaledImage.setMPixel(x, y, scaledValue);
                    }
                } else {
                    desValue = ((actValue - actMin) * sFactor) + desMin;
                    scaledValue = (byte)(desValue + 0.5f);
                    scaledImage.setMPixel(x, y, scaledValue);
                }
                
                myTemp++;
            }
        }

        return 0;
    } // scaleTo8


    // TODO: Replace paramater dc with one of type Graphics2D, 
    // as this method performs graphics
    // Called from:
    //     SceneList.previewStill
    public void display(HDC dc, int piOutWidth, int piOutHeight) {
        HBITMAP hBitmap, holdBitmap;
        HDC newdc;
        HANDLE hloc;
        PBITMAPINFO pbmi;
        HBITMAP hbm;
        RGBQUAD pal = new RGBQUAD[256];

        hloc = LocalAlloc(LMEM_ZEROINIT | LMEM_MOVEABLE, sizeof(BITMAPINFOHEADER) + (sizeof(RGBQUAD) * 256));
        pbmi = LocalLock(hloc);

        for(int a = 0; a < 256; a++) {
            pal[a].rgbRed = a;
            pal[a].rgbGreen = a;
            pal[a].rgbBlue = a;
            pal[a].rgbReserved = 0;
        }

        pbmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
        pbmi.bmiHeader.biWidth = this.imageWidth;
        pbmi.bmiHeader.biHeight = this.imageHeight;
        pbmi.bmiHeader.biPlanes = 1;
        pbmi.bmiHeader.biBitCount = this.bitsPerPixel;
        pbmi.bmiHeader.biCompression = BI_RGB;

        memcpy(pbmi.bmiColors, pal, sizeof(RGBQUAD) * 256);

        // Create a bitmap data structure containing the MemImage bits
        hBitmap = CreateDIBitmap(dc, pbmi, CBM_INIT, this.bytes, pbmi, DIB_RGB_COLORS);
        LocalFree(hloc);

        // Create a memory DC
        newdc = CreateCompatibleDC(dc);

        // Select the bitmap into the memory DC.
        // Save the old bitmap that SelectObject returns so that we can restore it later.
        holdBitmap = SelectObject(newdc, hBitmap);

        int localHeight = this.imageHeight;
        int localWidth = this.imageWidth;

        if (localHeight > piOutHeight) {
            localHeight = piOutHeight;
        }
        if (localWidth > piOutWidth) {
            localWidth = piOutWidth;
        }

        int yDelta = this.imageHeight - localHeight;
        int xDelta = this.imageWidth - localWidth;
        if(yDelta < 0) {
            yDelta = 0;
        }
        if(xDelta < 0) {
            xDelta = 0;
        }

        BitBlt(dc, 0, 0, localWidth, localHeight, newdc, 0, yDelta, SRCCOPY);

        // Restore the old bitmap
        SelectObject(newdc, holdBitmap);
    } // display


    // TODO: Replace paramater dc with one of type Graphics2D, 
    // as this method performs graphics
    // Called from:
    //     RenderObject.prepareCutout
    public int drawMask(POINT[] thePoints, int numVertices) {
        HBITMAP hBitmap, holdBitmap;
        HDC newdc;

        hBitmap = CreateBitmap(this.imageWidth, this.imageHeight, 1, 1, bytes);
        if(hBitmap == 0) {
            Globals.statusPrint("MemImage.drawMask: Unable to create internal bitmap");
            return 1;
        }

        // Create a memory DC
        newdc = CreateCompatibleDC(dc);

        // Select the bitmap into the memory DC.
        // Save the old bitmap that SelectObject returns so we can restore it later.
        holdBitmap = SelectObject(newdc, hBitmap);

        // Clear the memory dc by drawing a filled black rectangle
        RECT myRect;
        SetRect(myRect, 0, 0, this.imageWidth, this.imageHeight);
        FillRect(newdc, myRect, GetStockObject(BLACK_BRUSH));

        // Create the image mask by drawing a filled white polygon
        HPEN hpen = CreatePen(PS_SOLID, 1, 0xFFFFFFFFL);
        SelectObject(newdc, hpen);
        SelectObject(newdc, GetStockObject(WHITE_BRUSH));
        SetPolyFillMode(newdc, WINDING);
        graphics2d.drawPolygon(newdc, thePoints, numVertices);

        // Display the mask
        BitBlt(0, 0, this.imageWidth, this.imageHeight, newdc, 0, 0, SRCCOPY);

        // Copy the completed mask image back to the MemImage buffer
        int dwCount = this.paddedWidth * this.imageHeight;

        // The bitmap is stored using a width that is a 2 byte multiple
        GetBitmapBits(hBitmap, dwCount, this.bytes);

        // Restore the old bitmap
        SelectObject(newdc, holdBitmap);

        return 0;
    } // drawMask


    public int copy(MemImage outImage, int piXoffset, int piYoffset) {
        int x, y;

        if(outImage.bitsPerPixel != this.bitsPerPixel) {
            Globals.statusPrint("MemImage.copy: Destination image does not have matching pixel depth");
            return -1;
        }

        if((this.bitsPerPixel != 8) && (this.bitsPerPixel != 24)) {
            Globals.statusPrint("MemImage.copy: Only 8 or 24 bit images are supported");
            return -2;
        }

        byte thePixel, red, green, blue;
        for (x = 1; x <= this.imageWidth; x++) {
            for (y = 1; y < this.imageHeight; y++) {
                switch (this.bitsPerPixel) {
                case 8:
                    thePixel = getMPixel(x, y);
                    if(thePixel != 0) {
                        outImage.setMPixel(x + piXoffset, y + piYoffset, thePixel);
                    }
                    break;

                case 24:
                    getMPixelRGB(x, y, red, green, blue);
                    outImage.setMPixelRGB(x + piXoffset, y + piYoffset, red, green, blue);
                    break;
                }
            }
        }

        return 0;
    } // copy


    public int fastCopy(MemImage outImage, int piXOffset, int piYOffset) {
        if(outImage.bitsPerPixel != this.bitsPerPixel) {
            Globals.statusPrint("MemImage.fastCopy: Destination image does not have matching pixel depth");
            return -1;
        }

        if(this.bitsPerPixel != 8) {
            Globals.statusPrint("MemImage.fastCopy: Only 8 bit images are supported");
            return -2;
        }

        if ((piXOffset > outImage.imageWidth) || (piYOffset > outImage.imageHeight)) {
            Globals.statusPrint("MemImage.fastCopy: 1 or more offsets are larger than the output image");
            return -3;
        }

        int a = outImage.imageWidth  - piXOffset;
        int b = outImage.imageHeight - piYOffset;
        int c = piXOffset + this.imageWidth - outImage.imageWidth;

        // Although outLoc is written to, it is not read
        // byte *outLoc = outImage.bytes + ((piYOffset * outImage.imageWidth) + piXOffset);
        int outLoc = ((piYOffset * outImage.imageWidth) + piXOffset);

        // copy b rows of image data
        int i, j;
        // byte *currentPix = this.bytes;
        int currentPix = 0;

        for (j = 1; j <= b; j++) {
            for (i = 1; i <= a; i++) {
                if(bytes[currentPix] != 0) {
                    // *outLoc = *currentPix;
                    outLoc = bytes[currentPix];
                }

                outLoc++;
                currentPix++;  
            } // for i

            // if image is over the edge, skip those pixels then add the pad
            currentPix = currentPix + c + pads;
            outLoc += (outImage.pads + piXOffset);
        } // for j

        return 0;
    } // fastCopy


    public int copy8To24(MemImage outImage) {
        int x, y;

        if(outImage.bitsPerPixel != 24) {
            Globals.statusPrint("MemImage.copy8To24: Destination image must have 24 bit pixels");
            return -1;
        }

        if(this.bitsPerPixel != 8) {
            Globals.statusPrint("MemImage.copy8To24: Source image must have 8 bit pixels");
            return -2;
        }

        byte intensity;
        for (x = 1; x <= this.imageWidth; x++) {
            for (y = 1; y < this.imageHeight; y++) {
                intensity = getMPixel(x, y);
                outImage.setMPixelRGB(x, y, intensity, intensity, intensity);
            } // for y
        } // for x

        return 0;
    } // copy8To24


    public byte getMPixel(int x, int y, char aColor) {
        // Inputs x and y are assumed to be 1 relative
        // Returns the desired pixel from a color image
        int addr;
        // byte *thePixel;
        // byte *myTemp = bytes;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return -1;
        }

        addr = ((y - 1) * (int)paddedWidth) + ((x - 1)*3);  // 3 bytes/color pixel
        /*
        myTemp = myTemp + addr;
        thePixel = myTemp;
        if(aColor == 'B') return *thePixel;
        if(aColor == 'G') return *(thePixel + 1);
        if(aColor == 'R') return *(thePixel + 2);
        */
        if(aColor == 'B') return bytes[addr];
        if(aColor == 'G') return bytes[addr + 1];
        if(aColor == 'R') return bytes[addr + 2];

        Globals.statusPrint("MemImage.getMPixel: Unknown color value");
        return 0;
    } // getMPixel


    // Sets parameters red, green and blue
    // Called from:
    //     adjustColor
    //     copy (if this.bitsPerPixel = 24)
    //     Globals.iwarpz
    //     Globals.motionBlur
    public int getMPixelRGB(int x, int y, Byte red, Byte green, Byte blue) {
        //  Inputs x and y are assumed to be 1 relative
        //  Returns the desired pixel from a color image
        if(this.bitsPerPixel != 24) {
            Globals.statusPrint("MemImage.getMPixelRGB: Image must be 24 bits per pixel");
            return -1;
        }

        int addr;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return -1;
        }

        addr = ((y - 1) * this.paddedWidth) + ((x - 1)*(this.bitsPerPixel/8));  // 3 bytes/color pixel

        // Set the output parameters
        blue  = bytes[addr];
        green = bytes[addr + 1];
        red   = bytes[addr + 2];

        return 0;
    } // getMPixelRGB


    // Called from:
    //     adjustColor
    //     copy (if this.bitsPerPixel = 24)
    //     copy8To24
    //     Globals.iwarpz
    //     Globals.motionBlur
    public int setMPixelRGB(int x, int y, byte red, byte green, byte blue) {
        // Inputs x and y are assumed to be 1 relative
        // Returns the desired pixel from a color image
        if(this.bitsPerPixel != 24) {
            Globals.statusPrint("MemImage.setMPixelRGB: Image must be 24 bits per pixel");
            return -1;
        }

        int addr;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return -1;
        }

        addr = ((y - 1) * paddedWidth) + ((x - 1)*(this.bitsPerPixel/8));  // 3 bytes/color pixel

        bytes[addr] = blue;
        bytes[addr + 1] = green;
        bytes[addr + 2] = red;

        return 0;
    } // setMPixelRGB


    // Called from:
    //     adjustColor
    //     clearRectangle
    //     copy (if this.bitsPerPixel = 8)
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Texture.createPlasma
    //     Texture.createTexture
    public int setMPixel(int x, int y, byte value) {
        // Inputs x and y are assumed to be 1 relative
        int addr;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        // If setMPixel is called with coordinates that are located outside 
        // the bounds of the associted image array, no action is taken.
        // See p 119 of Visual Special Effects Toolkit in C++.
        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return -1;
        }

        addr = ((y - 1) * paddedWidth) + x - 1;
        bytes[addr] = value;

        return value;
    } // setMPixel


    public int setMPixelA(float x, float y, byte value) {
        // Inputs x and y are assumed to be 1 relative
        int addr;
        // byte *myTemp = this.bytes;
        int myTemp;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        if (
        (y < 1) || (y > (float)this.imageHeight) || 
        (x < 1) || (x > (float)this.imageWidth)) {
            return -1;
        }

        // Calculate the weights
        float xa, xb, ya, yb, valaa, valba, valab, valbb;
        byte chromaColor = 0;

        xa = x - (int)x;
        xb = 1.0f - xa;
        ya = y - (int)y;
        yb = 1.0f - ya;
        valaa = (byte)((xa * ya * (float)value) + 0.5f);
        valba = (byte)((xb * ya * (float)value) + 0.5f);
        valab = (byte)((xa * yb * (float)value) + 0.5f);
        valbb = (byte)((xb * yb * (float)value) + 0.5f);

        addr = (((int)y - 1) * paddedWidth) + (int)x - 1;

        // Set the pixel value if this is the first contribution, else add this
        // pixel to what is already present.
        /*
        if( *(myTemp + addr) == chromaColor ) {
            *(myTemp + addr) = valaa;
        } else {
            *(myTemp + addr) = *(myTemp + addr) + valaa;
        }
        */
        if(bytes[addr] == chromaColor) {
            bytes[addr] = valaa;
        } else {
            bytes[addr] = bytes[addr] + valaa;
        }

        /*
        if( *(myTemp + addr + 1) == chromaColor ) {
            *(myTemp + addr) = valba;
        } else {
            *(myTemp + addr + 1) = *(myTemp + addr + 1) + valba;
        }
        */
        if(bytes[addr + 1] == chromaColor) {
            bytes[addr] = valba;
        } else {
            bytes[addr + 1] = bytes[addr + 1] + valba;
        }

        /*
        if( *(myTemp + addr + paddedWidth) == chromaColor ) {
            *(myTemp + addr + paddedWidth) = valab;
        } else {
            *(myTemp + addr + paddedWidth) = *(myTemp + addr + paddedWidth) + valab;
        }
        */
        if(bytes[addr + paddedWidth] == chromaColor) {
            bytes[addr + paddedWidth] = valab;
        } else {
            bytes[addr + paddedWidth] = bytes[addr + paddedWidth] + valab;
        }

        /*
        if( *(myTemp + addr + paddedWidth + 1) == chromaColor ) {
            *(myTemp + addr + paddedWidth + 1) = valbb;
        } else {
            *(myTemp + addr + paddedWidth + 1) = *(myTemp + addr + paddedWidth + 1) + valbb;
        }
        */

        if(bytes[addr + paddedWidth + 1] == chromaColor ) {
            bytes[addr + paddedWidth + 1] = valbb;
        } else {
            bytes[addr + paddedWidth + 1] = bytes[addr + paddedWidth + 1] + valbb;
        }

        return value;
    } // setMPixelA


    public byte getMPixelA(float x, float y) {
        // Inputs x and y must be 1 relative
        int addr;
        byte *myTemp = this.bytes;

        if(this.accessMode == SEQUENTIAL) {
            y = (byte)1;
        }

        if (
        (y < 1) || (y > (float)this.imageHeight) || 
        (x < 1) || (x > (float)this.imageWidth)) {
            return (byte)0;
        }

        // Calculate the Weights
        float xa, xb, ya, yb, waa, wba, wab, wbb;
        float valaa, valab, valba, valbb;
        float bucket = 0.0f;
        byte chromaColor = (byte)0;
        xa = x - (int)x;
        xb = 1.0f - xa;
        ya = y - (int)y;
        yb = 1.0f - ya;
        waa = xa * ya;
        wba = xb * ya;
        wab = xa * yb;
        wbb = xb * yb;

        addr = (((int)y - 1) * (int)paddedWidth) + (int)x - 1;
        valaa = *(myTemp + addr);
        valab = *(myTemp + addr + (int)paddedWidth);
        valba = *(myTemp + addr + 1);
        valbb = *(myTemp + addr + (int)paddedWidth + 1);

        if(valaa != chromaColor) {
            bucket += (valaa * waa);
        }
        if(valab != chromaColor) {
            bucket += (valab * wab);
        }
        if(valba != chromaColor) {
            bucket += (valba * wba);
        }
        if(valbb != chromaColor) {
            bucket += (valbb * wbb);
        }
        
        return (byte)(bucket + 0.5f);
    } // getMPixelA


    // Called from:
    //     adjustColor
    //     copy (if this.bitsPerPixel = 8)
    //     copy8To24
    //     histogram
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Texture.plasma
    public byte getMPixel(int x, int y) {
        // Inputs x and y are assumed to be 1 relative
        int addr;
        int iBytesIdx = 0; // index into this.bytes

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        // If getMPixel is called with coordinates that are located outside 
        // the bounds of the associted image array, no action is taken.
        // See p 119 of Visual Special Effects Toolkit in C++.
        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return 0;
        }

        addr = ((y - 1) * paddedWidth) + x - 1;
        iBytesIdx = iBytesIdx + addr;

        return this.bytes[iBytesIdx];
    } // getMPixel


    // Called from:
    //     Globals.createQMeshModel
    //     Globals.iwarpz
    //     Texture.createTexture
    public int setMPixel32(int x, int y, float aValue) {
        // Inputs x and y are assumed to be 1 relative
        int addr;
        byte *myTemp = this.bytes;
        float *pPixel;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return -1;
        }

        addr = ((y - 1) * (int)paddedWidth) + ((x - 1) * (this.bitsPerPixel/8));
        myTemp = myTemp + addr;
        pPixel = (float *)myTemp;
        *(pPixel) = aValue;

        return 0;
    } // setMPixel32


    // Called from:
    //     Globals.iwarpz
    //     RenderObject.renderMeshz
    public float getMPixel32(int x, int y) {
        // Inputs x and y are assumed to be 1 relative
        int addr;
        byte *myTemp = this.bytes;
        float *pPixel;

        if(this.accessMode == SEQUENTIAL) {
            y = 1;
        }

        if (
        (y < 1) || (y > this.imageHeight) || 
        (x < 1) || (x > this.imageWidth)) {
            return -1.0f;
        }

        addr = ((y - 1) * (int)paddedWidth) + ((x - 1) * (this.bitsPerPixel/8));
        myTemp = myTemp + addr;
        pPixel = (float *)myTemp;

        return *pPixel;
    } // getMPixel32


    // Called from:
    //     adjustColor
    //     histogram
    //     printValue
    //     saveAs8
    //     scaleTo8
    //     RenderObject.renderMeshz
    //     RenderObject.renderShapez
    public int getHeight() {
        return this.imageHeight;
    } // getHeight


    // Called from:
    //     adjustColor
    //     histogram
    //     printValue
    //     saveAs8
    //     scaleTo8
    //     RenderObject.renderMeshz
    public int getWidth() {
        return this.imageWidth;
    } // getWidth


    public int getAccessMode() {
        return this.accessMode;
    } // getAccessMode


    public int getColorSpec() {
        return this.theColorSpec;
    } // getColorSpec


    // Called from:
    //     adjustColor
    //     histogram
    //     printValue
    //     saveAs8
    public int getBitsPerPixel() {
        return this.bitsPerPixel;
    } // getBitsPerPixel


    public byte[] getBytes() {
        return this.bytes;
    } // getBytes


    // Called from:
    //     saveAs8
    //     Globals.motionBlur
    //     RenderObject.prepareCutout
    //     RenderObject.renderMeshz
    //     SceneList.render
    public boolean isValid() {
        //  valid = 1 indicates the constructor did not encounter errors.
        return this.valid;
    } // isValid


    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     saveAs8
    //     Globals.motionBlur
    //     MainFrame.onToolsWarpImage
    //     Texture.createTexture
    public int writeBMP(String fileName) {
        String msgText;
        BITMAPFILEHEADER bf;
        BITMAPINFOHEADER bi;
        RGBQUAD[] palinfo = new RGBQUAD[256];
        int a;
        this.valid = true;

        bi.biSize = (int)sizeof(BITMAPINFOHEADER);
        bi.biWidth = this.imageWidth;
        bi.biHeight = this.imageHeight;
        bi.biPlanes = 1;
        if (theColorSpec == RGBCOLOR) {
            bi.biBitCount = 24;
        } else if(theColorSpec == ONEBITMONOCHROME) {
            bi.biBitCount = 1;
        } else if(theColorSpec == A32BIT) {
            bi.biBitCount = 32;
        } else {
            bi.biBitCount = 8;
        }
        
        bi.biBitCount = mapColorSpecToBitsPerPixel(theColorSpec);

        bi.biCompression = BI_RGB;
        bi.biSizeImage = this.imageHeight*this.paddedWidth;
        bi.biXPelsPerMeter = 1;
        bi.biYPelsPerMeter = 1;
        bi.biClrUsed = 256;
        bi.biClrImportant = 256;

        bf.bfType = 0x4d42;
        bf.bfSize = ((int)sizeof(BITMAPFILEHEADER) + (int)bi.biSize + (int)bi.biSizeImage)/4;
        bf.bfReserved1 = 0;
        bf.bfReserved2 = 0;
        bf.bfOffBits = (int)sizeof(BITMAPFILEHEADER) + (int)sizeof(BITMAPINFOHEADER) + (int)sizeof(RGBQUAD)*256;

        for(a = 0; a < 256; a++) {
            palinfo[a].rgbRed = a;
            palinfo[a].rgbGreen = a;
            palinfo[a].rgbBlue = a;
            palinfo[a].rgbReserved = a;
        }

        fp = CreateFile(fileName, GENERIC_WRITE, 0, 0, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, 0);
        if(fp == null) {
            msgText = "MemImage.writeBMP: Couldn't open output image " + fileName;
            Globals.statusPrint(msgText);
            this.valid = false;
            return 1;
        }

        int numBytesWritten;
        WriteFile(fp, bf, sizeof(BITMAPFILEHEADER), numBytesWritten, null);
        if(numBytesWritten != sizeof(BITMAPFILEHEADER)) {
            msgText = "MemImage.writeBMP: WriteFile error 1. numBytesWritten " + numBytesWritten + " " + fileName;
            Globals.statusPrint(msgText);
            CloseHandle(fp);
            this.valid = false;
            return 1;
        }

        WriteFile(fp, bi, sizeof(BITMAPINFOHEADER), numBytesWritten, null);
        if(numBytesWritten != sizeof(BITMAPINFOHEADER)) {
            msgText = "MemImage.writeBMP: WriteFile error 2. numBytesWritten " + numBytesWritten + " " + fileName;
            Globals.statusPrint(msgText);
            CloseHandle(fp);
            this.valid = false;
            return 1;
        }

        WriteFile(fp, palinfo, sizeof(RGBQUAD) * 256, numBytesWritten, null);
        if(numBytesWritten != sizeof(RGBQUAD) * 256) {
            msgText = "MemImage.writeBMP: WriteFile error 3. numBytesWritten " + numBytesWritten + " " + fileName;
            Globals.statusPrint(msgText);
            CloseHandle(fp);
            this.valid = false;
            return 1;
        }

        boolean writeStatus;
        int myIndex;
        // byte *theBytes = bytes;
        int iBytesIdx = 0;
        if (this.accessMode == RANDOM) {
            for(myIndex = 1; myIndex <= this.imageHeight; myIndex++) {
                writeStatus = WriteFile(fp, bytes[iBytesIdx], this.paddedWidth, numBytesWritten, null);

                if(numBytesWritten != this.paddedWidth) {
                    msgText= "MemImage.writeBMP: WriteFile error 4. numBytesWritten " + numBytesWritten + " " + fileName;
                    Globals.statusPrint(msgText);
                    CloseHandle(fp);
                    this.valid = false;
                    return 1;
                }
                // theBytes += paddedWidth;
                iBytesIdx += paddedWidth;
            } // for myIndex
        }

        if (accessMode == RANDOM) {
            CloseHandle(fp);
        }

        return 0;
    } // writeBMP


    // Called from:
    //     The MemImage constructor that takes 6 parameters
    public int readBMP(String psFileName, int piColorSpec) {
        BITMAPFILEHEADER bmFH;
        BITMAPINFOHEADER pbmIH;
        BITMAPINFO pbmInfo;
        WORD PalSize = 256;
        int x,y,index,pad24,pad8;
        /* unsigned */int bmWidth, bmHeight, bmImgSize, bmScanWidth, bmScanWidth8;
        String msgText;

        this.valid = true;
        fp = CreateFile(psFileName, GENERIC_READ, FILE_SHARE_READ, 0, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0);
        if(fp == null) {
            msgText = "MemImage.readBMP: Couldn't open image " + psFileName;
            Globals.statusPrint(msgText);
            this.valid = false;
            return 2;
        }

        // SetCursor(LoadCursor( null, IDC_WAIT ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        int numBytesRead;
        ReadFile(fp, bmFH, sizeof(BITMAPFILEHEADER), numBytesRead, null);
        if(bmFH.bfType != 0x4D42) {   // if type isn't "BM" ...
            msgText = "MemImage.readBMP: Not a .BMP image. " + psFileName;
            Globals.statusPrint(msgText);
            CloseHandle(fp);
            this.valid = false;
            return 3;
        }

        pbmIH = GlobalLock(GlobalAlloc(GMEM_FIXED, sizeof(BITMAPINFOHEADER)));
        ReadFile(fp, pbmIH, sizeof(BITMAPINFOHEADER), numBytesRead, null);
        this.bitsPerPixel = mapColorSpecToBitsPerPixel(piColorSpec);
        int fileBitsPerPixel = pbmIH.biBitCount;

        if(pbmIH.biCompression != BI_RGB) {
            msgText = "MemImage.readBMP: Compressed images not supported. " + psFileName;
            Globals.statusPrint(msgText);
            CloseHandle(fp);
            return 5;
        }

        pbmInfo = GlobalLock(GlobalAlloc( GHND, PalSize + sizeof(BITMAPINFOHEADER) ));
        pbmInfo.bmiHeader = *pbmIH;

        GlobalUnlock(pbmIH);
        GlobalFree(pbmIH);

        bmWidth  = pbmInfo.bmiHeader.biWidth;
        bmHeight = pbmInfo.bmiHeader.biHeight;
        this.imageHeight = bmHeight;
        this.imageWidth  = bmWidth;
        bmImgSize = pbmInfo.bmiHeader.biSizeImage;
        // must be an even WORD size !!!
        bmScanWidth8 = ((bmWidth*8  + 31)/32)*4;
        bmScanWidth  = ((bmWidth*24 + 31)/32)*4;

        int widthInBytes = bmWidth * 3;
        int remainder    = widthInBytes % 4;
        pad24 = 0;
        if (remainder > 0) {
            pad24 = 4 - remainder;
        }

        // We are reading from a 24 bit BMP in order to create an 8 bit BMP
        // calculate the number of pads needed for the 8 bit BMP
        int remainder8 = bmWidth % 4;
        pad8 = 0;
        if(remainder8 > 0) {
            pad8 = 4 - remainder8;
        }

        bmImgSize= bmScanWidth8*bmHeight;
        pbmInfo.bmiHeader.biSizeImage = bmImgSize;
        
        int numRows = bmHeight;
        if (this.accessMode == SEQUENTIAL) {
            numRows = 1;
        }

        allocate(numRows, bmWidth);
        if(this.valid == false) {
            msgText = "MemImage.readBMP: Couldn't allocate memory. " + psFileName;
            Globals.statusPrint(msgText);
            CloseHandle(fp);
            return 6;
        }

        SetFilePointer(fp, bmFH.bfOffBits, 0, FILE_BEGIN);
        if (this.accessMode == RANDOM) {
            byte *theBytes = bytes;
            int numItems, myIndex;
            if(this.bitsPerPixel == fileBitsPerPixel) {  // We are reading a mono image
                for(myIndex = 1; myIndex <= this.imageHeight; myIndex++) {
                    ReadFile(fp, theBytes, paddedWidth, numBytesRead, null);

                    if(numItems == -1) {
                        msgText = "MemImage.readBMP: ReadFile error. " + psFileName;
                        Globals.statusPrint(msgText);
                        CloseHandle(fp);
                        this.valid = false;
                        return 1;
                    }
                    theBytes += this.paddedWidth;
                }
            } else {      // Assume we are reading 8 bpp from a 24 bpp file
                index = 0;
                int rowWidth = widthInBytes + pad24;
                char transfer = new char[rowWidth + 1];
                for(y = 0; y < bmHeight; y++) {
                    ReadFile(fp, transfer, rowWidth, numBytesRead, null);
                    for(x = 0; x < bmWidth * 3; x += 3) {
                        // Get the desired color,
                        // Blue: theColor = 3
                        // Red:  theColor = 1
                        bytes[index] = transfer[x + (4 - piColorSpec - 1)]; 
                        index++;                                        
                    } // for x                                                

                    index += pad8;
                } // for y
            } // end else
        }

        GlobalFree((GlobalHandle( pbmInfo ));
        if (this.accessMode == RANDOM) {
            CloseHandle(fp);
        }

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return 0;
    } // readBMP


    public int readNextRow() {
        boolean myStatus;
        int numBytesRead;

        myStatus = ReadFile(this.fp, this.bytes, this.paddedWidth, numBytesRead, null);
        if (myStatus) {
            return 1;
        } else {
            return 0;
        }
    } // readNextRow

    public void close() {
        CloseHandle(this.fp);
        this.fp = 0;
    } // close


    // Changed return value from int to boolean
    public boolean writeNextRow() {
        boolean myStatus;
        int numBytesWritten;

        myStatus = WriteFile(this.fp, this.bytes, this.paddedWidth, numBytesWritten, null);
        if (myStatus) {
            return true;
        } else {
            return false;
        }
    } // writeNextRow


    // TODO: Not a method
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     readBMP
    //     writeBMP
    int mapColorSpecToBitsPerPixel(int piColorSpec) {
        int bpp = -1;

        if(
        piColorSpec == REDCOLOR ||
        piColorSpec == GREENCOLOR ||
        piColorSpec == BLUECOLOR ||
        piColorSpec == EIGHTBITMONOCHROME ) {
            bpp = 8;
        }

        if(piColorSpec == RGBCOLOR ) {
            bpp = 24;
        }
        if(piColorSpec == ONEBITMONOCHROME ) {
            bpp = 1;
        }
        if(piColorSpec == A32BIT ) {
            bpp = 32;
        }

        return bpp;
    } // mapColorSpecToBitsPerPixel


    // TODO: Not a method
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     The MemImage constructor that takes 3 parameters
    int mapBitsPerPixelToColorSpec(int piBitsPerPixel) {
        int colorSpec = EIGHTBITMONOCHROME;

        if(piBitsPerPixel ==  1) colorSpec = ONEBITMONOCHROME;
        if(piBitsPerPixel ==  8) colorSpec = EIGHTBITMONOCHROME;
        if(piBitsPerPixel == 24) colorSpec = RGBCOLOR;
        if(piBitsPerPixel == 32) colorSpec = A32BIT;

        return colorSpec;
    } // mapBitsPerPixelToColorSpec


    public int getImageSizeInBytes() {
        return this.imageHeight * this.paddedWidth;
    } // getImageSizeInBytes


    // Called from:
    //     Globals.iwarpz
    public int saveAs8(String outImagePathName) {
        //  A utility to save a 32 bit image in a viewable 8 bit form
        String msgText;
        int bpp = getBitsPerPixel();

        if(bpp != 32) {
            msgText = "MemImage.saveAs8: Input image must be 32 bits per pixel. " + outImagePathName;
            Globals.statusPrint(msgText);
            return -1;
        }

        int outputRows = getHeight();
        int outputCols = getWidth();
        MemImage testImage = new MemImage(outputRows, outputCols, 8);
        if (!testImage.isValid()) {
            msgText = "MemImage.saveAs8: Unable to create intermediate image. " + outImagePathName;
            Globals.statusPrint(msgText);
            return -1;
        }

        scaleTo8(testImage);
        msgText = "MemImage.saveAs8: Saving " + outImagePathName;
        Globals.statusPrint(msgText);

        testImage.writeBMP(outImagePathName);
        msgText = "MemImage.saveAs8: Histogram of " + outImagePathName;
        Globals.statusPrint(msgText);
        testImage.histogram();

        return 0;
    } // saveAs8


    // Called from:
    //     saveAs8
    public int histogram() {
        String msgBuffer;

        if(getBitsPerPixel() != 8) {
            Globals.statusPrint("MemImage.histogram: Only 8 bit pixel images are supported");
            return -1;
        }

        int[] histogArray = new int[256];
        int numRows = getHeight();
        int numCols = getWidth();
        int i, j;
        int thePixel;

        for (i = 0; i <= 255; i++) {
            histogArray[i] = 0;
        }

        for (i = 1; i <= numRows; i++) {
            for (j = 1; j <= numCols; j++) {
                thePixel = (int)getMPixel(j, i);
                histogArray[thePixel]++;
            } // for j
        } // for i

        // Display the histogram to the ict log
        msgBuffer = "MemImage.histogram: Histogram of " + savedFileName;
        Globals.statusPrint(msgBuffer);
        for(i = 0; i <= 240; i+=16) {
            msgBuffer = String.format("%3d: %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d",
                i, 
                histogArray[i+ 0], histogArray[i+ 1], histogArray[i+ 2], histogArray[i+ 3], 
                histogArray[i+ 4], histogArray[i+ 5], histogArray[i+ 6], histogArray[i+ 7], 
                histogArray[i+ 8], histogArray[i+ 9], histogArray[i+10], histogArray[i+11], 
                histogArray[i+12], histogArray[i+13], histogArray[i+14], histogArray[i+15]);
            Globals.statusPrint(msgBuffer);
        } // for i

        return 0;
    } // histogram
    

    // Sets midRed, midGreen and midBlue
    public int adjustColor(int desiredRed, int desiredGreen, int desiredBlue,
    byte midRed, byte midGreen, byte midBlue, MemImage outImage, 
    String adjustmentType, int inputImageColor) {
        // Calculate the average color in the image excluding zeros
        // Compute the color difference vector between the average color and the desired color
        // Create a new image by adjusting each color by the difference vector, clipping outliers
        float redBucket, greenBucket, blueBucket;
        float avgRed, avgGreen, avgBlue;
        int bpp = getBitsPerPixel();
        if((bpp != 24) && (bpp != 8)) {
            Globals.statusPrint("MemImage.adjustColor: Input image must be 8 or 24 bits per pixel");
            return -1;
        }
        int numRows = getHeight();
        int numCols = getWidth();
        int row, col;
        byte red, green, blue;
        redBucket   = 0.0f;
        greenBucket = 0.0f;
        blueBucket  = 0.0f;
        int totalPixels = 0;
        int newRed, newGreen, newBlue;
        int aValue, newValue;

        if(adjustmentType.equalsIgnoreCase("Target")) {
            for(row = 1; row <= numRows; row++) {
                for(col = 1; col <= numCols; col++) {
                    switch(bpp) {
                    case 24:
                        getMPixelRGB(col, row, red, green, blue);
                        if (red != 0 || green != 0 || blue != 0) {
                            redBucket += (red);
                            greenBucket += (green);
                            blueBucket += (blue);
                            totalPixels++;
                        }
                        break;

                    case 8:
                        aValue = getMPixel(col, row);
                        if (aValue != 0) {
                            greenBucket += (aValue);
                            totalPixels++;
                        }
                        break;
                    } // switch
                } // for col
            } // for row

            if(totalPixels == 0) {
                Globals.statusPrint("MemImage.adjustColor: Cannot adjust image color. No non-zero pixels!");
                return -1;
            }

            redBucket   /= (float)totalPixels;
            greenBucket /= (float)totalPixels;
            blueBucket  /= (float)totalPixels;
            midRed   = (byte)(redBucket   + 0.5f);
            midGreen = (byte)(greenBucket + 0.5f);
            midBlue  = (byte)(blueBucket  + 0.5f);

            //  calculate the color difference vector
            switch(bpp) {
            case 24:
                avgRed   = (float)desiredRed   - redBucket;
                avgGreen = (float)desiredGreen - greenBucket;
                avgBlue  = (float)desiredBlue  - blueBucket;
                break;

            case 8:
                if(inputImageColor == REDCOLOR)   avgGreen = (float)desiredRed   - greenBucket;
                if(inputImageColor == GREENCOLOR) avgGreen = (float)desiredGreen - greenBucket;
                if(inputImageColor == BLUECOLOR)  avgGreen = (float)desiredBlue  - greenBucket;
                break;
            } // switch
        } 

        if(adjustmentType.equalsIgnoreCase("Relative")) {
            switch(bpp) {
            case 24:
                avgRed   = (float)desiredRed;
                avgGreen = (float)desiredGreen;
                avgBlue  = (float)desiredBlue;
                break;

            case 8:
                if (inputImageColor == REDCOLOR)   avgGreen = desiredRed;
                if (inputImageColor == GREENCOLOR) avgGreen = desiredGreen;
                if (inputImageColor == BLUECOLOR)  avgGreen = desiredBlue;
                break;
            } // switch
        } // if(strcmpi(adjustmentType, "Relative") == 0)

        for(row = 1; row <= numRows; row++) {
            for(col = 1; col <= numCols; col++) {
                switch(bpp) {
                case 24:
                    getMPixelRGB(col, row, red, green, blue);
                    if ((red != 0) || (green != 0) || (blue != 0)) {
                        newRed   = (int)(red + avgRed);
                        newGreen = (int)(green + avgGreen);
                        newBlue  = (int)(blue + avgBlue);

                        if (newRed < 1)     newRed   = 1;
                        if (newRed > 255)   newRed   = 255;
                        if (newGreen < 1)   newGreen = 1;
                        if (newGreen > 255) newGreen = 255;
                        if (newBlue < 1)    newBlue  = 1;
                        if (newBlue > 255)  newBlue  = 255;

                        outImage.setMPixelRGB(col, row, 
                            (byte)newRed, (byte)newGreen, (byte)newBlue);
                    }
                    break;

                case 8:
                    aValue = getMPixel(col, row);
                    if (aValue != 0) {
                        newValue = aValue + (int)avgGreen;
                        if (newValue < 1) {
                            newValue = 1;
                        }
                        if (newValue > 255) {
                            newValue = 255;
                        }
                        outImage.setMPixel(col, row, (byte)newValue);
                    }
                    break;
                } // switch
            } // for col
        } // for row

        if(adjustmentType.equalsIgnoreCase("Delta")) {
            for(row = 1; row <= numRows; row++) {
                for(col = 1; col <= numCols; col++) {
                    switch(bpp) {
                    case 24:
                        getMPixelRGB(col, row, red, green, blue);
                        if ((red != 0) || (green != 0) || (blue != 0)) {
                            newRed   = red   + desiredRed;
                            newGreen = green + desiredGreen;
                            newBlue  = blue  + desiredBlue;
                            outImage.setMPixelRGB(col, row, (byte)newRed, (byte)newGreen, (byte)newBlue);
                        }
                        break;

                    case 8:
                        aValue = getMPixel(col, row);
                        if (aValue != 0) {
                            newGreen = aValue + desiredGreen;
                            outImage.setMPixel(col, row, (byte)newGreen);
                        }
                        break;
                    } // switch
                } // for col
            } // for row
        }

        return 0;
    } // adjustColor
    

    public int printValue(int x, int y) {
        String msgBuffer;
        int bpp = getBitsPerPixel();

        if((bpp != 8) && (bpp != 32)) {
            Globals.statusPrint("MemImage.printValue: Only 8 or 32 bit images are supported");
            return -1;
        }

        if((x > getWidth()) || (y > getHeight())) {
            msgBuffer = "MemImage.printValue: Either x or y are > image bounds. x: " + x + "  y: " + y;
            Globals.statusPrint(msgBuffer);
            return -1;
        }

        byte[] lineBuffer   = new byte[32];
        float[] fLineBuffer = new float[32];

        msgBuffer = "MemImage.printValue: Display of " + savedFileName;
        Globals.statusPrint(msgBuffer);
        msgBuffer = String.format("   %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d",
                x,     x + 1, x +  2, x +  3, 
                x + 4, x + 5, x +  6, x +  7, 
                x + 8, x + 9, x + 10, x + 11);
        Globals.statusPrint(msgBuffer);

        int i,j;
        int numRows = 10;
        int numCols = 12;
        if ((y + numRows) > getHeight()) {
            numRows = getHeight() - y;
        }

        if ((x + numCols) > getWidth()) {
            numCols = getWidth() - x;
        }

        for (i = y; i <= y + numRows; i++) {
            // display the image row to the ict log
            switch(bpp) {
            case 8:
                for(j = 0; j <= numCols; j++) {
                    lineBuffer[j] = getMPixel(x + j, i);
                }

                msgBuffer = String.format("%3d: %4d %4d %4d %4d %4d %4d %4d %4d %4d %4d %4d %4d",
                    i, 
                    lineBuffer[0], lineBuffer[1], lineBuffer[2],
                    lineBuffer[3], lineBuffer[4], lineBuffer[5], 
                    lineBuffer[6], lineBuffer[7], lineBuffer[8], 
                    lineBuffer[9], lineBuffer[10], lineBuffer[11]);
                Globals.statusPrint(msgBuffer);
                break;

            case 32:
                for(j = 0; j <= numCols; j++) {
                    fLineBuffer[j] = getMPixel32(x + j, i);
                }

                msgBuffer = String.format("%3d: %6g %6g %6g %6g %6g %6g %6g %6g %6g %6g %6g %6g",
                    i, 
                    fLineBuffer[0], fLineBuffer[1], fLineBuffer[2],
                    fLineBuffer[3], fLineBuffer[4], fLineBuffer[5], 
                    fLineBuffer[6], fLineBuffer[7], fLineBuffer[8], 
                    fLineBuffer[9], fLineBuffer[10], fLineBuffer[11]);
                Globals.statusPrint(msgBuffer);
                break;
            } // switch
        } // for i

        return 0;
    } // printValue
    

    // Called from:
    //     RenderObject.renderMeshz
    public void setFileName(String psFileName) {
        this.savedFileName = psFileName;
    } // setFileName


    public int clearRectangle(int startX, int startY, int endX, int endY) {
        int pixelDepth = getBitsPerPixel();
        int row, col;

        for (row = startY; row <= endY; row++) {
            for (col = startX; col <= endX; col++) {
                switch (pixelDepth) {
                case 8:
                    setMPixel(col, row, (byte)0);
                    break;

                case 24:
                    setMPixelRGB(col, row, (byte)0, (byte)0, (byte)0);
                    break;

                case 32:
                    setMPixel32(col, row, 0.0f);
                    break;

                default:
                    Globals.statusPrint("MemImage.clearRectangle: Unsupported pixel depth");
                    return -1;
                    break;
                } // switch
            } // for col
        } // for row

        return 0;
    } // clearRectangle


    // This method will set parameters xBeg, xEnd, yBeg and yEnd
    public int getBoundingBox(Integer xBeg, Integer xEnd, Integer yBeg, Integer yEnd) {
        int x, y;
        if((this.bitsPerPixel != 8) && (this.bitsPerPixel != 24)) {
            Globals.statusPrint("MemImage.getBoundingBox: Only 8 or 24 bit images are supported");
            return -2;
        }

        byte thePixel, red, green, blue;
        xBeg = Math.max(this.imageWidth, this.imageHeight);
        yBeg = Math.max(this.imageWidth, this.imageHeight);
        xEnd = 1;
        yEnd = 1;

        for (x = 1; x <= this.imageWidth; x++) {
            for (y = 1; y < this.imageHeight; y++) {
                switch (this.bitsPerPixel) {
                case 8:
                    thePixel = getMPixel(x, y);
                    if(thePixel != CHROMAVALUE) {
                        if(x < xBeg) xBeg = x;
                        if(x > xEnd) xEnd = x;
                        if(y < yBeg) yBeg = x;
                        if(y > yEnd) yEnd = x;
                    }
                    break;

                case 24:
                    getMPixelRGB(x, y, red, green, blue);
                    if(red != CHROMARED) {
                        if(x < xBeg) xBeg = x;
                        if(x > xEnd) xEnd = x;
                        if(y < yBeg) yBeg = x;
                        if(y > yEnd) yEnd = x;
                    }
                    if(green != CHROMAGREEN) {
                        if(x < xBeg) xBeg = x;
                        if(x > xEnd) xEnd = x;
                        if(y < yBeg) yBeg = x;
                        if(y > yEnd) yEnd = x;
                    }
                    if(blue != CHROMABLUE) {
                        if(x < xBeg) xBeg = x;
                        if(x > xEnd) xEnd = x;
                        if(y < yBeg) yBeg = x;
                        if(y > yEnd) yEnd = x;
                    }
                    break;
                } // switch
            } // for y

            Globals.statusPrint(savedFileName);
            String msgText = "MemImage.getBoundingBox: xBeg: " + xBeg + "  xEnd: " + xEnd + " yBeg: " + yBeg + "  yEnd: " + yEnd; 
            Globals.statusPrint(msgText);
        } // for x

        return 0;
    } // getBoundingBox


    // This method came from BLEND.CPP
    // Called from:
    //     RenderObject.renderMeshz
    public int createAlphaImage(MemImage outImage) {
        int x, y;
        byte thePixel, aRed, aGreen, aBlue;
        if(outImage.bitsPerPixel != 8) {
            Globals.statusPrint("MemImage.createAlphaImage: Output image must be 8 bits per pixel");
            return -1;
        }

        for (y = 1; y <= imageHeight; y++) {
            for (x = 1; x < imageWidth; x++) {
                switch(bitsPerPixel) {
                case 8:
                    thePixel = getMPixel(x, y);
                    break;

                case 24:
                    getMPixelRGB(x, y, aRed, aGreen, aBlue);
                    thePixel = CHROMAVALUE;
                    if(
                    aRed != CHROMAVALUE || 
                    aGreen != CHROMAVALUE ||
                    aBlue != CHROMAVALUE) { 
                        thePixel = 255;
                    }
                    break;
                } // switch

                if(thePixel != CHROMAVALUE) {
                    outImage.setMPixel(x, y ,(BYTE)255);
                }
            }
        }

        return 0;
    } // createAlphaImage
    

    // This method came from BLEND.CPP
    public int unPack(MemImage outputImage) {
        // Convert a one bit memImage to an 8 bit memImage
        //
        // The input image must be one bit per pixel
        // The output image must be 8 bits per pixel
        // The output image must be opened for RANDOM access
        if (theColorSpec != ONEBITMONOCHROME) {
            Globals.statusPrint("MemImage.unPack: Input image colorSpec must be ONEBITMONOCHROME");
            return 1;
        }
      
        if(outputImage.accessMode != RANDOM) {
            Globals.statusPrint("MemImage.unPack: Output image access mode must be RANDOM");
            return 2;
        }

        if (
        outputImage.theColorSpec == RGBCOLOR ||
        outputImage.theColorSpec == ONEBITMONOCHROME) {
            Globals.statusPrint("MemImage.unPack: Output image colorSpec must be REDCOLOR, GREENCOLOR, or BLUECOLOR");
            return 3;
        }

        int x, y;
        byte packedByte;
        byte *packedBytes = bytes;

        // paddedwidth is a multiple of 4 bytes to satisfy the requirements of a .BMP
        // the 1 bit image was created using GetBitmapBits which assumes that an image
        // width is a multiple of 2 bytes.  We need to calculate this width
        // before proceeding
        int myWidth   = imageWidth / 8;
        int remainder = imageWidth % 8;
        if (remainder > 0) {
            myWidth++;
        }
        if((myWidth/2*2) != myWidth) {
            myWidth++;
        }
      
        for (y = 1; y <= imageHeight; y++) {
            int xCounter = 0;
            for (x = 1; x <= myWidth; x++) {
                packedByte = *packedBytes;
                for(int bitCounter = 0; bitCounter < 8; bitCounter++) {
                    xCounter++;
                    if(xCounter <=imageWidth) {
                        if((packedByte >> (7-bitCounter)) & 0x1) {
                            outputImage.setMPixel(xCounter, imageHeight-(y-1), 255);
                        } else {
                            outputImage.setMPixel(xCounter, imageHeight-(y-1), CHROMAVALUE);
                        }
                    }
                } // for bitCounter

                packedBytes++;
            } // for x
        } // for y

        return 0;
    } // unPack
    

    // This method came from BLEND.CPP
    public int adjustImageBorder(String outPath) {
        // Find the size of the chromakey border around an image and
        // create a new image in which the border has been removed.
        //
        // This function effectively centers rotoscoped images that came
        // from sources other than function createAlphaImage.
        int imHeight = getHeight();
        int imWidth  = getWidth();
        if(this.bitsPerPixel != 24) {
            Globals.statusPrint("MemImage.adjustImageBorder: Image must have 24 bit pixels.");
            return -1;
        }

        int row, col;
        int minX = imWidth;
        int maxX = 1;
        int minY = imHeight;
        int maxY = 1;
        byte aRed, aGreen, aBlue;
      
        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                getMPixelRGB(col, row, aRed, aGreen, aBlue);
                if(
                aRed != CHROMARED || 
                aGreen != CHROMAGREEN ||
                aBlue != CHROMABLUE) {
                    if(row < minY) minY = row;
                    if(col < minX) minX = col;
                    if(row > maxY) maxY = row;
                    if(col > maxX) maxX = col;
                }
            }
        }

        int newImHeight = maxY - minY + 1;
        int newImWidth  = maxX - minX + 1;
        MemImage outImage = new MemImage(newImHeight, newImWidth, 24);
        if(!outImage.isValid()) {
            Globals.statusPrint("MemImage.adjustImageBorder: Unable to create output image.");
            return -2;
        }

        // Now copy the image into its new home
        int outRow = 1;
        int outCol;
        for (row = minY; row <= maxY; row++) {
            outCol = 1;
            for (col = minX; col <= maxX; col++) {
                getMPixelRGB(col, row, aRed, aGreen, aBlue);
                outImage.setMPixelRGB(outCol, outRow, aRed, aGreen, aBlue);
                outCol++;
            }

            outRow++;
        }

        outImage.writeBMP(outPath);
        return 0;
    } // adjustImageBorder


    // This method came from IWARP.CPP
    public int alphaSmooth3() {
        // Each image must be the same size.
        MemImage inImage  = this;
        MemImage outImage = this;

        if(inImage.getHeight() == outImage.getHeight() && 
        inImage.getWidth() == outImage.getWidth()) {
            // do nothing?
        } else {
            Globals.statusPrint("MemImage.alphaSmooth3: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() == outImage.getBitsPerPixel()) && 
        (inImage.getBitsPerPixel() == 8)) {
            // do nothing?
        } else {
            Globals.statusPrint("MemImage.alphaSmooth3: Images must have 8 or 24 bit pixels.");
            return -2;
        }
        int bpp = inImage.getBitsPerPixel();
        float[][] weight = new float[5][5];
        weight[0][0] = 0.05f;    // impulse function
        weight[0][1] = 0.05f;
        weight[0][2] = 0.05f;

        weight[1][0] = 0.05f;
        weight[1][1] = 0.60f;
        weight[1][2] = 0.05f;

        weight[2][0] = 0.05f;
        weight[2][1] = 0.05f;
        weight[2][2] = 0.05f;

        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        int row, col;
        float sum, q00,q10,q20;
        float      q01,q11,q21;
        float      q02,q12,q22;
        float factor = 1.0f/9.0f;

        for (row = 2; row <= imHeight - 1; row++) {
            for (col = 2; col <= imWidth - 1; col++) {
                q11 = inImage.getMPixel(col, row);
                if(q11 != 0) {
                    q00 = inImage.getMPixel(col - 1, row - 1);
                    q10 = inImage.getMPixel(col    , row - 1);
                    q20 = inImage.getMPixel(col + 1, row - 1);

                    q01 = inImage.getMPixel(col - 1, row);
                    q11 = inImage.getMPixel(col    , row);
                    q21 = inImage.getMPixel(col + 1, row);

                    q02 = inImage.getMPixel(col - 1, row + 1);
                    q12 = inImage.getMPixel(col    , row + 1);
                    q22 = inImage.getMPixel(col + 1, row + 1);

                    sum = (q00 + q10 + q20
                        + q01 + q11 + q21 
                        + q02 + q12 + q22) * factor; 
                
                    float diff = 255.0f - sum;   //steepen the ramp to 0
                    sum -= (diff * 0.5f);
                    sum = Globals.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col, row, (byte)(sum + 0.5f));
                }
            }
        }

        return 0;
    } // alphaSmooth3
    

    // This method came from IWARP.CPP
    // Called from:
    //     RenderObject.renderMeshz
    public int alphaSmooth5() {
        //
        // Each image must be the same size.
        //
        MemImage inImage = this;
        MemImage outImage = this;

        if(
        inImage.getHeight() == outImage.getHeight() && 
        inImage.getWidth() == outImage.getWidth()) {
            // do nothing?
        } else {
            Globals.statusPrint("MemImage.alphaSmooth5: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() == outImage.getBitsPerPixel()) && 
        (inImage.getBitsPerPixel() == 8)) {
            // do nothing?
        } else {
            Globals.statusPrint("MemImage.alphaSmooth5: Images must have 8 or 24 bit pixels.");
            return -2;
        }

        int bpp = inImage.getBitsPerPixel();
        float[][] weight = new float[5][5];
        weight[0][0] = 0.05f;    // impulse function
        weight[0][1] = 0.05f;
        weight[0][2] = 0.05f;

        weight[1][0] = 0.05f;
        weight[1][1] = 0.60f;
        weight[1][2] = 0.05f;

        weight[2][0] = 0.05f;
        weight[2][1] = 0.05f;
        weight[2][2] = 0.05f;

        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        int row, col;
        float sum, q00,q10,q20, q30, q40;
        float      q01,q11,q21, q31, q41;
        float      q02,q12,q22, q32, q42;
        float      q03,q13,q23, q33, q43;
        float      q04,q14,q24, q34, q44;
        float factor = 1.0f/25.0f;

        for (row = 3; row <= imHeight - 2; row++) {
            for (col = 3; col <= imWidth - 2; col++) {
                q22 = inImage.getMPixel(col, row);
                if(q22 != 0) {
                    q00 = inImage.getMPixel(col - 2, row - 2);
                    q10 = inImage.getMPixel(col - 1, row - 2);
                    q20 = inImage.getMPixel(col    , row - 2);
                    q30 = inImage.getMPixel(col + 1, row - 2);
                    q40 = inImage.getMPixel(col + 2, row - 2);

                    q01 = inImage.getMPixel(col - 2, row - 1);
                    q11 = inImage.getMPixel(col - 1, row - 1);
                    q21 = inImage.getMPixel(col    , row - 1);
                    q31 = inImage.getMPixel(col + 1, row - 1);
                    q41 = inImage.getMPixel(col + 2, row - 1);

                    q02 = inImage.getMPixel(col - 2, row);
                    q12 = inImage.getMPixel(col - 1, row);

                    q32 = inImage.getMPixel(col + 1, row);
                    q42 = inImage.getMPixel(col + 2, row);

                    q03 = inImage.getMPixel(col - 2, row + 1);
                    q13 = inImage.getMPixel(col - 1, row + 1);
                    q23 = inImage.getMPixel(col    , row + 1);
                    q33 = inImage.getMPixel(col + 1, row + 1);
                    q43 = inImage.getMPixel(col + 2, row + 1);

                    q04 = inImage.getMPixel(col - 2, row + 2);
                    q14 = inImage.getMPixel(col - 1, row + 2);
                    q24 = inImage.getMPixel(col    , row + 2);
                    q34 = inImage.getMPixel(col + 1, row + 2);
                    q44 = inImage.getMPixel(col + 2, row + 2);

                    sum = (q00 + q10 + q20 + q30 + q40
                        + q01 + q11 + q21 + q31 + q41 
                        + q02 + q12 + q22 + q32 + q42 
                        + q03 + q13 + q23 + q33 + q43 
                        + q04 + q14 + q24 + q34 + q44) * factor; 
                
                    float diff = 255.0f - sum;   //steepen the ramp to 0
                    sum -= (diff * 0.5f);
                    sum = Globals.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col,row, (byte)(sum + 0.5f));
                }
            }
        }

        return 0;
    } // alphaSmooth5
    

    // This method came from IWARP.CPP
    public int alphaSmooth7() {
        // Each image must be the same size.
        MemImage inImage  = this;
        MemImage outImage = this;

        if(
        inImage.getHeight() == outImage.getHeight() && 
        inImage.getWidth()  == outImage.getWidth() ) {
            // do nothing?
        } else {
            Globals.statusPrint("MemImage.alphaSmooth7: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() == outImage.getBitsPerPixel()) && 
        (inImage.getBitsPerPixel() == 8)) {
            // do nothing?
        } else {
            Globals.statusPrint("MemImage.alphaSmooth7: Images must have 8 or 24 bit pixels.");
            return -2;
        }
        int bpp = inImage.getBitsPerPixel();

        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        float x1 = 0.0f;
        float y1 = 0.0f;
        float z1 = 0.0f;
        float totalCells = 0.0f;
        int row, col;
        float sum;
        float q00, q10, q20, q30, q40, q50, q60;
        float q01, q11, q21, q31, q41, q51, q61;
        float q02, q12, q22, q32, q42, q52, q62;
        float q03, q13, q23, q33, q43, q53, q63;
        float q04, q14, q24, q34, q44, q54, q64;
        float q05, q15, q25, q35, q45, q55, q65;
        float q06, q16, q26, q36, q46, q56, q66;
        float factor = 1.0f/49.0f;

        for (row = 4; row <= imHeight - 3; row++) {
            for (col = 4; col <= imWidth - 3; col++) {
                q33 = inImage.getMPixel(col, row);
                if(q33 != 0) {
                    q00 = inImage.getMPixel(col - 3, row - 3);
                    q10 = inImage.getMPixel(col - 2, row - 3);
                    q20 = inImage.getMPixel(col - 1, row - 3);
                    q30 = inImage.getMPixel(col    , row - 3);
                    q40 = inImage.getMPixel(col + 1, row - 3);
                    q50 = inImage.getMPixel(col + 2, row - 3);
                    q60 = inImage.getMPixel(col + 3, row - 3);

                    q01 = inImage.getMPixel(col - 3, row - 2);
                    q11 = inImage.getMPixel(col - 2, row - 2);
                    q21 = inImage.getMPixel(col - 1, row - 2);
                    q31 = inImage.getMPixel(col    , row - 2);
                    q41 = inImage.getMPixel(col + 1, row - 2);
                    q51 = inImage.getMPixel(col + 2, row - 2);
                    q61 = inImage.getMPixel(col + 3, row - 2);

                    q02 = inImage.getMPixel(col - 3, row - 1);
                    q12 = inImage.getMPixel(col - 2, row - 1);
                    q22 = inImage.getMPixel(col - 1, row - 1);
                    q32 = inImage.getMPixel(col    , row - 1);
                    q42 = inImage.getMPixel(col + 1, row - 1);
                    q52 = inImage.getMPixel(col + 2, row - 1);
                    q62 = inImage.getMPixel(col + 3, row - 1);

                    q03 = inImage.getMPixel(col - 3, row );
                    q13 = inImage.getMPixel(col - 2, row );
                    q23 = inImage.getMPixel(col - 1, row );

                    q43 = inImage.getMPixel(col + 1, row );
                    q53 = inImage.getMPixel(col + 2, row );
                    q63 = inImage.getMPixel(col + 3, row );

                    q04 = inImage.getMPixel(col - 3, row + 1);
                    q14 = inImage.getMPixel(col - 2, row + 1);
                    q24 = inImage.getMPixel(col - 1, row + 1);
                    q34 = inImage.getMPixel(col    , row + 1);
                    q44 = inImage.getMPixel(col + 1, row + 1);
                    q54 = inImage.getMPixel(col + 2, row + 1);
                    q64 = inImage.getMPixel(col + 3, row + 1);

                    q05 = inImage.getMPixel(col - 3, row + 2);
                    q15 = inImage.getMPixel(col - 2, row + 2);
                    q25 = inImage.getMPixel(col - 1, row + 2);
                    q35 = inImage.getMPixel(col    , row + 2);
                    q45 = inImage.getMPixel(col + 1, row + 2);
                    q55 = inImage.getMPixel(col + 2, row + 2);
                    q65 = inImage.getMPixel(col + 3, row + 2);

                    q06 = inImage.getMPixel(col - 3, row + 3);
                    q16 = inImage.getMPixel(col - 2, row + 3);
                    q26 = inImage.getMPixel(col - 1, row + 3);
                    q36 = inImage.getMPixel(col    , row + 3);
                    q46 = inImage.getMPixel(col + 1, row + 3);
                    q56 = inImage.getMPixel(col + 2, row + 3);
                    q66 = inImage.getMPixel(col + 3, row + 3);

                    sum = (q00 + q10 + q20 + q30 + q40 + q50 + q60
                        + q01 + q11 + q21 + q31 + q41 + q51 + q61 
                        + q02 + q12 + q22 + q32 + q42 + q52 + q62 
                        + q03 + q13 + q23 + q33 + q43 + q53 + q63 
                        + q04 + q14 + q24 + q34 + q44 + q54 + q64
                        + q05 + q15 + q25 + q35 + q45 + q55 + q65
                        + q06 + q16 + q26 + q36 + q46 + q56 + q66)
                        * factor; 

                    float diff = 255.0f - sum;   //steepen the ramp to 0
                    sum -= (diff * 0.5f);
                    sum = Globals.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col, row, (byte)(sum + 0.5f));
                }
            }
        }

        return 0;
    } // alphaSmooth7


    // This method came from SHADERS.CPP
    // Called from:
    //     RenderObject.renderMesh
    //     RenderObject.renderMeshz
    //     RenderObject.renderShape
    //     RenderObject.renderShapez
    public int fillPolyz(
    int I1x, int I1y, float I1p, float I1d,
    int I2x, int I2y, float I2p, float I2d, 
    int I3x, int I3y, float I3p, float I3d, 
    int I4x, int I4y, float I4p, float I4d,
    MemImage zBuffer) {
        // this	 -	output image
        // outImage	 - zBuffer	
        int xMax = I1x;
        int yMax = I1y;
        int xMin = I1x;
        int yMin = I1y;
        float totalIntensity, avgIntensity;
        MemImage outImage = this;  

        // Get the bounding box
        if(I2x > xMax) xMax = I2x;
        if(I3x > xMax) xMax = I3x;
        if(I4x > xMax) xMax = I4x;

        if(I2x < xMin) xMin = I2x;
        if(I3x < xMin) xMin = I3x;
        if(I4x < xMin) xMin = I4x;

        if(I2y > yMax) yMax = I2y;
        if(I3y > yMax) yMax = I3y;
        if(I4y > yMax) yMax = I4y;

        if(I2y < yMin) yMin = I2y;
        if(I3y < yMin) yMin = I3y;
        if(I4y < yMin) yMin = I4y;

        // Handle quadrangles that consist of: a single point, horizontal or vertical line
        float oldZ, intensity, outIntensity, distance; 
        int bpp = outImage.getBitsPerPixel();

        // Single point
        if((xMin == xMax) && (yMin == yMax)) {
            distance  = (I1d + I2d + I3d + I4d)/4.0f;
            intensity = (I1p + I2p + I3p + I4p)/4.0f;

            if(zBuffer != null) {
                oldZ = zBuffer.getMPixel32(xMin, yMin);
                if(distance <= oldZ) {
                    intensity = (I1p + I2p + I3p + I4p)/4.0f;
                    zBuffer.setMPixel32(xMin, yMin, distance);

                    if(bpp == 8)  outImage.setMPixel(xMin, yMin, (byte)intensity);
                    if(bpp == 24) {
                        outImage.setMPixelRGB(xMin, yMin, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }
                } else {   //no zBuffer
                    if(bpp == 8)  outImage.setMPixel(xMin, yMin, (byte)intensity);
                    if(bpp == 24) {
                        outImage.setMPixelRGB(xMin, yMin, (byte)intensity,
                            (byte)intensity, (byte)intensity);
                    }
                }
            }

            return 0;
        }

        int minX, minY, maxX, maxY, j;
        float minI, maxI, minD, maxD;
        float nSteps, intensityStep, distanceStep;
        int row, col, denominator;

        // Handle larger quadrangles
        int xCent = (int)(((float)xMin + ((float)xMax - (float)xMin) / 2.0f) + 0.5f);
        int yCent = (int)(((float)yMin + ((float)yMax - (float)yMin) / 2.0f) + 0.5f);

        // The intensity at the centroid is the weighted sum of the intensities at each vertex
        // The weights are the normalized distances between each vertex distance and the centroid
        // fill the triangle bounded by the centroid and each successive pair of vertices
        float totalDistance = 0.0f;
        float d1 = Globals.getDistance2d((float)xCent, (float)yCent, (float)I1x, (float)I1y);
        float d2 = Globals.getDistance2d((float)xCent, (float)yCent, (float)I2x, (float)I2y);
        float d3 = Globals.getDistance2d((float)xCent, (float)yCent, (float)I3x, (float)I3y);
        float d4 = Globals.getDistance2d((float)xCent, (float)yCent, (float)I4x, (float)I4y);
        totalDistance = d1 + d2 + d3 + d4;
        if(totalDistance == 0.0f) {
            Globals.statusPrint("MemImage.fillPolyZ: Sum of polygon diagonals must be > 0");
            return -1;
        }

        //  Normalize the distances:
        d1 /= totalDistance;
        d2 /= totalDistance;
        d3 /= totalDistance;
        d4 /= totalDistance;
        
        // Calculate the intensity at the centroid
        float iCent = (d1 * I1p) + (d2 * I2p) + (d3 * I3p) + (d4 * I4p);
        float dCent = (d1 * I1d) + (d2 * I2d) + (d3 * I3d) + (d4 * I4d);

        // Fill the polygon by subdividing it into 4 triangles and interpolatively 
        // shading each one.
        Globals.fillTrianglez(xCent, yCent, iCent, dCent, 
            I1x, I1y, I1p, I1d, 
            I2x, I2y, I2p, I2d, 
            outImage, zBuffer);
        Globals.fillTrianglez(xCent, yCent, iCent, dCent, 
            I2x, I2y, I2p, I2d, 
            I3x, I3y, I3p, I3d, 
            outImage, zBuffer);
        Globals.fillTrianglez(xCent, yCent, iCent, dCent, 
            I3x, I3y, I3p, I3d, 
            I4x, I4y, I4p, I4d,
            outImage, zBuffer);
        Globals.fillTrianglez(xCent, yCent, iCent, dCent, 
            I4x, I4y, I4p, I4d, 
            I1x, I1y, I1p, I1d,
            outImage, zBuffer);
        return 0;
    } // fillPolyz


    public int sizeofLowerLimit() {
        int mySize = 0;
        int booleanFieldsSizeInBits = 0;
        int booleanFieldsSize = 0;
        int intFieldsSize = 0;
        int floatFieldsSize = 0;
        int referenceFieldsSize = 0;

        /*
        protected boolean valid;
        protected int imageHeight;
        protected int imageWidth;
        protected int bitsPerPixel;
        protected int paddedWidth;
        protected int pads;
        protected int accessMode;
        protected int theColorSpec;
        protected byte[] bytes;
        protected HANDLE fp;
        protected String savedFileName;
        */

        booleanFieldsSizeInBits = 1; // 1 booleans
        booleanFieldsSize = 1; // 1 bit fits in a byte
        intFieldsSize = 7*4; // 7 ints
        floatFieldsSize = 0*4; // 0 floats
        referenceFieldsSize = 3*4; // 3 references to objects
        mySize = booleanFieldsSize + intFieldsSize + floatFieldsSize + referenceFieldsSize;

        return mySize;
    }
} // class MemImage