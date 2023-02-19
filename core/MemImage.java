package core;

import globals.Globals;
import globals.JICTConstants;

import java.awt.image.BufferedImage;
import java.io.File;

import math.MathUtils;

import structs.Point2d;

public class MemImage {
    boolean ictdebug = false;
    protected File fp;
    protected String msSavedFileName;   // The last associated pathname
    protected int miImageHeight;    // Height in pixels
    protected int miImageWidth;     // Width in pixels
    protected int miBitsPerPixel;   // Bits per pixel
    protected int miPaddedWidth;    // Physical width of the image in bytes (multiple of 4)
    protected int miPads;           // Difference between imagewidth and paddedwidth (bytes)
    protected int miAccessMode;     // SEQUENTIAL or RANDOM
    protected int miColorSpec;   // Indicates the desired color mapping

    // State indicator.  1 if successful, else 0
    // I changed this from int to boolean
    protected boolean valid;          
    protected byte[] bytes;         // Pointer to the image bytes
    protected BufferedImage mBuffImg;

    
/*
protected:
  void allocate(int height, int widthInPixels); - implemented
*/

/*
public:
  memImage (int height, int width, int bitsPerPixel=8); - implemented
  memImage (memImage *); - implemented
  memImage (char *fileName, int imLength, int imWidth,
            int imAccessMode, char rw, int colorSpec); - implemented
  int readNextRow(); - implemented
  int writeNextRow(); - implemented
  void clear(); - implemented
  void init32(float aValue); - implemented
  void display(HDC dc, int outWidth, int outHeight); - implemented
  void close(); - implemented
  int saveAs8(char *OutImagePathName); - implemented
  int scaleTo8(memImage *scaledImage); - iimplemented
  int histogram(); - implemented

  HBITMAP getBmp(); ---- NOT IMPLEMENTED ----
  int getHeight(); - implemented
  int getWidth(); - implemented 
  int getAccessMode(); - implemented
  int getColorSpec(); - implemented
  int getBitsPerPixel(); - implemented
  unsigned int getImageSizeInBytes(); - implemented
  BYTE  *getBytes(); - implemented
  int isValid(); - implemented
  int alphaSmooth3(); - implemented
  int alphaSmooth5(); - implemented
  int alphaSmooth7(); - implemented
  int createAlphaImage(memImage *outImage); - implemented
  int copy(memImage *outImage, int xoffset, int yoffset); - implemented
  int fastCopy(memImage *outImage, int xOffset, int yOffset); - implemented
  int copy8To24(memImage *outImage); - implemented

  int drawMask(HDC dc, POINT far *thePoints, int numVertices); - implemented
  int unPack(memImage *unpackedImage); - implemented
  BYTE getMPixel(int x, int y); - implemented
  BYTE getAverageMPixel (int x, int y); ---- NOT IMPLEMENTED ----
  BYTE getMPixel(int x, int y, char aColor);  - implemented
  float getMPixel32 (int x, int y); - implemented
  int setMPixel32 (int x, int y, float aValue); - implemented
  int setMPixel(int x, int y, BYTE value); - implemented
  int setMPixelA (float x, float y, BYTE value); - implemented
  BYTE getMPixelA (float x, float y); - implemented
  int getMPixelRGB(int x, int y, BYTE *red, BYTE *green, BYTE *blue); - implemented
  int setMPixelRGB(int x, int y, BYTE red, BYTE green, BYTE blue); - implemented
  int clearRGB(BYTE red, BYTE green, BYTE blue); - implemented
  int clearRGBRange(BYTE redLow, BYTE redHigh, BYTE greenLow, BYTE greenHigh, 
                      BYTE blueLow, BYTE blueHigh); - implemented
  int writeBMP(char *fileName); - implemented
  int readBMP(char *fileName, int theColor); - implemented
  int fillPolyz(int I1x, int I1y, float I1p, float I1d, 
                       int I2x, int I2y, float I2p, float I2d, 
                       int I3x, int I3y, float I3p, float I3d, 
                       int I4x, int I4y, float I4p, float I4d,
                       memImage *zBuffer); - implemented
  int adjustColor(BYTE desiredRed, BYTE desiredGreen, BYTE desiredBlue,
   BYTE *midRed, BYTE *midGreen, BYTE *midBlue, memImage *outImagechar,
   char *adjustmentType, int inputImageColor); - implemented
  int adjustImageBorder(char *outPath); - implemented
  int printValue(int x, int y); - implemented
  void setFileName(char *fileName); - implemented
  int clearRectangle(int startX, int startY, int endX, int endY); - implemented
  int getBoundingBox(int *xBeg, int *xEnd, int *yBeg, int *yEnd); - implemented
 virtual ~memImage(); - implemented (as finalize)
 */


    // Called from:
    //     Globals.motionBlur
    //     RenderObject ctor that takes 4 parameters: a String, int, boolean and Point3d
    //     RenderObject.prepareCutout
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    //     MainFrame.onToolsWarpImage
    public MemImage(String psFileName, int piImHeight, int piImWidth, int piImAccessMode, 
    char pcRW, int piColorSpec) {
        String msgBuffer;

        if (
        piColorSpec != JICTConstants.I_ONEBITMONOCHROME && 
        piColorSpec != JICTConstants.I_REDCOLOR &&
        piColorSpec != JICTConstants.I_GREENCOLOR && 
        piColorSpec != JICTConstants.I_BLUECOLOR &&
        piColorSpec != JICTConstants.I_RGBCOLOR && 
        piColorSpec != JICTConstants.I_EIGHTBITMONOCHROME &&
        piColorSpec != 0 && 
        piColorSpec != JICTConstants.I_A32BIT) {
            msgBuffer = "MemImage Constructor 1: ColorSpec not valid - " + piColorSpec;
            Globals.statusPrint(msgBuffer);
            this.valid = false;
            return;
        }

        if((pcRW != 'R') && (pcRW != 'r') && (pcRW != 'W') && (pcRW != 'w')) {
            msgBuffer = "MemImage Constructor 1: pcRW must be R or W - " + pcRW;
            Globals.statusPrint(msgBuffer);
            this.valid = false;
            return;
        }

        if(
        (piImAccessMode != JICTConstants.I_RANDOM) && 
        (piImAccessMode != JICTConstants.I_SEQUENTIAL)) {
            msgBuffer = "MemImage Constructor 1: accessMode must be RANDOM or SEQUENTIAL - " + piImAccessMode;
            Globals.statusPrint(msgBuffer);
            this.valid = false;
            return;
        }
        if(
        (pcRW == 'W' || pcRW == 'w') && 
        (piImHeight <= 0 || piImWidth <= 0 || piColorSpec == 0)) {
            Globals.statusPrint("MemImage Constructor 1: length, width and colorSpec must be > 0 for write access");
            this.valid = false;
            return;
        }

        Integer myBitsPerPixel = 0; 
        int iStatus = 0;
        this.valid = true;
        myBitsPerPixel = mapColorSpecToBitsPerPixel(piColorSpec);
        this.msSavedFileName = psFileName;

        // Get a preview of the file 
        Integer myHeight = 0, myWidth = 0;
        if(piImHeight == 0 || piImWidth == 0 || piColorSpec == 0) {
            iStatus = Globals.readBMPHeader(psFileName, myHeight, myWidth, myBitsPerPixel);
            if(iStatus != 0) {
                this.valid = false;
                Globals.statusPrint("MemImage Constructor 1: Unable to open BMP header for read access");
                return;
            }

            piImHeight = myHeight;
            piImWidth = myWidth;
            if(piColorSpec == 0) {
                piColorSpec = mapBitsPerPixelToColorSpec(myBitsPerPixel);
            }
        }

        //  Assign the MemImage properties
        this.miImageHeight = piImHeight;
        this.miImageWidth = piImWidth;
        this.miBitsPerPixel = myBitsPerPixel;
        this.miColorSpec = piColorSpec;
        this.miAccessMode = piImAccessMode;

        if((pcRW == 'W') || (pcRW == 'w')) {
            int numRows = this.miImageHeight;
            if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
                numRows = 1;
            }

            allocate(numRows, this.miImageWidth);
            if(!isValid()) {
                Globals.statusPrint("MemImage Constructor 1: Could not allocate memory for write");
            }
        }

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            //  Write or Read the BMP header
            if(pcRW == 'W' || pcRW == 'w') {
                iStatus = writeBMP(psFileName);
            }

            if(pcRW == 'R' || pcRW == 'r') {
                iStatus = readBMP(psFileName, piColorSpec);
            }

            if(iStatus != 0) {
                this.valid = false; // Indicate the file could not be opened
            }
        }

        if(this.miAccessMode == JICTConstants.I_RANDOM) {
            if(
            (pcRW == 'W' || pcRW == 'w') && 
            (piColorSpec == JICTConstants.I_RGBCOLOR)) {
                Globals.statusPrint("MemImage Constructor 1: RANDOM 24 bit BMPs not supported for writing");
                this.valid = false;
                return;
            }
            if(
            (pcRW == 'W' || pcRW == 'w') && 
            (piColorSpec != JICTConstants.I_RGBCOLOR)) {
                iStatus = writeBMP(psFileName);
            }

            if(pcRW == 'R' || pcRW == 'r') {
                iStatus = Globals.readBMPHeader(psFileName, myHeight, myWidth, myBitsPerPixel);
                if(iStatus != 0) {
                    this.valid = false; // Indicate that the file could not be opened
                    Globals.statusPrint("MemImage Constructor 1: Unable to open BMP header");
                    return;
                }

                readBMP(psFileName, this.miColorSpec);
                if(iStatus != 0) {
                    this.valid = false; // Indicate the file could not be opened
                }
            }
        }
        
        if (ictdebug) {
            Globals.statusPrint("MemImage Constructor 1");
        }
    } // MemImage ctor


    // Called from: 
    //     saveAs8
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     SceneList.render
    //     MainFrame.onToolsWarpImage
    //     RenderObject.renderMeshz
    //     Texture.createTexture
    public MemImage(int piHeight, int piWidth, int piBitsPerPixel) {
        //  Allocates a memory resident 8 bit image by default.
        this.valid = false;
        this.miAccessMode    = JICTConstants.I_RANDOM;
        this.miImageHeight   = piHeight;
        this.miImageWidth    = piWidth;
        this.miColorSpec     = JICTConstants.I_EIGHTBITMONOCHROME;
        this.msSavedFileName = "No Image File Name";
        this.miBitsPerPixel  = piBitsPerPixel;
        this.miColorSpec     = mapBitsPerPixelToColorSpec(this.miBitsPerPixel);
        allocate(piHeight, piWidth);

        if (ictdebug) {
            Globals.statusPrint("MemImage Constructor 2");
        }
    } // MemImage ctor


    // Called from:
    //     Globals.tweenImage
    //     RenderObject.renderMeshz
    //     SceneList.preview
    //     SceneList.render
    public MemImage(int piHeight, int piWidth) {
        this(piHeight, piWidth, 8);
    } // MemImage ctor


    // Called from:
    //     ScnFileParser.readList (twice)
    public MemImage(MemImage pMImage) {
        this.miImageHeight   = pMImage.miImageHeight;
        this.miImageWidth    = pMImage.miImageWidth;
        this.miBitsPerPixel  = pMImage.miBitsPerPixel;
        this.miAccessMode    = pMImage.miAccessMode;
        this.miColorSpec     = pMImage.miColorSpec;
        this.msSavedFileName = pMImage.msSavedFileName;

        if(pMImage.valid == true) {
            allocate(pMImage.miImageHeight, pMImage.miImageWidth);
        }

        if (ictdebug) {
            Globals.statusPrint("MemImage Constructor 4");
        }
    } // MemImage ctor


    public void finalize() {
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
        // byte[] buffer; // not used
        float bytesPerPixel = (float)this.miBitsPerPixel/8.0f;
        float fWidthInBytes = (float)piWidthInPixels * bytesPerPixel;  // 1,24,32 bpp
        int widthInBytes    = (int)fWidthInBytes;

        if (fWidthInBytes > (float)widthInBytes) {
            widthInBytes++;
        }

        this.miPaddedWidth = (widthInBytes/4)*4;

        if(this.miPaddedWidth != widthInBytes) {
            this.miPaddedWidth += 4;
        }

        this.miPads = this.miPaddedWidth - widthInBytes;
        totalBytes = this.miPaddedWidth * piHeight;

        this.valid = true;
        this.bytes = new byte[totalBytes];            
        clear();  // Clear the memory area
    } // allocate


    // Called from:
    //     allocate
    //     GPipe.reset
    //     MorphDlg.onOK
    public void clear() {
        int x, y;
        int rows = this.miImageHeight;

        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            rows = 1;
        }

        int iBytesIdx = 0;
        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.miPaddedWidth; x++) {	 // paddedWidth is the number of bytes per row
                bytes[iBytesIdx] = 0;
                iBytesIdx++;
            } // for x
        } // for y
    } // clear


    // Could not find where this is called from
    public int clearRGB(byte pbytRed, byte pbytGreen, byte pbytBlue) {
        // Clear all pixels whose colors match the specified color
        // Use the fastest image traversal method
        if (this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.clearRGB: Bits per pixel must = 24");
            return -1;
        }

        // SetCursor(LoadCursor( null, IDC_WAIT ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int x, y;
        int iBytesIdx = 0; // an index into array this.bytes
        int rows = this.miImageHeight;
        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            rows = 1;
        }

        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.miImageWidth; x++) {
                if(
                (bytes[iBytesIdx]     == pbytBlue)  && 
                (bytes[iBytesIdx + 1] == pbytGreen) && 
                (bytes[iBytesIdx + 2] == pbytRed)) {
                    bytes[iBytesIdx]     = 0;
                    bytes[iBytesIdx + 1] = 0;
                    bytes[iBytesIdx + 2] = 0;
                    //counter++;
                }

                iBytesIdx +=3;
            } // for x

            iBytesIdx += miPads;
        } // for y

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return 0;
    } // clearRGB


    // Called from:
    //     ImageView.onLButtonDown
    public int clearRGBRange(byte pbytRedLow, byte pbytRedHigh, 
    byte pbytGreenLow, byte pbytGreenHigh, 
    byte pbytBlueLow, byte pbytBlueHigh) {
        // Clear all pixels whose colors match the specified color
        // Use the fastest traversal method
        if (this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.clearRGBRange: Bits per pixel must = 24");
            return -1;
        }

        // SetCursor(LoadCursor( null, IDC_WAIT ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int x, y;
        int iBytesIdx = 0;
        int rows = this.miImageHeight;

        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            rows = 1;
        }

        for (y = 1; y <= rows; y++) {
            for (x = 1; x <= this.miImageWidth; x++) {
                if(
                (bytes[iBytesIdx]   >= pbytBlueLow)  && (bytes[iBytesIdx]   <= pbytBlueHigh)  && 
                (bytes[iBytesIdx+1] >= pbytGreenLow) && (bytes[iBytesIdx+1] <= pbytGreenHigh) && 
                (bytes[iBytesIdx+2] >= pbytRedLow)   && (bytes[iBytesIdx+2] <= pbytRedHigh)) {
                    bytes[iBytesIdx]   = 0;		   // each color component must be erased
                    bytes[iBytesIdx+1] = 0;
                    bytes[iBytesIdx+2] = 0;
                }

                iBytesIdx += 3;
            } // for x

            iBytesIdx += miPads;
        } // for y

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return 0;
    } // clearRGBRange


    // This method will initialize a MemImage with a given floating-point value.
    // See p 104 of the book Visual Special Effects Toolkit in C++, 
    // by Tim Wittenburg.
    // Called from:
    //     Globals.renderMesh
    //     GPipe.initialize
    //     GPipe.reset
    //     RenderObject.renderMeshz
    //     SceneList.render
    public void init32(float pfValue) {
        //  Initialize a 32 bit image by setting each pixel to a designated value
        int ix, iy;
        float[] fTemp;
        fTemp = (float *)bytes;

        // Don't need paddedWidth here because each row
        // is by definition a multiple of 4 bytes
        int iRows = this.miImageHeight;
        // int iCols = this.imageWidth;  // not used
                                
        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            iRows = 1;
        }

        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
                *fTemp = pfValue;
                fTemp++;
            } // for ix
        } // for iy
    } // init32


    // This method scales a 32-bit image to 8-bit pixel resolution in such a way that 
    // the 32-bit values can be viewed on the screen. See p 104 of the book 
    // Visual Special Effects Toolkit in C++, by Tim Wittenburg.
    // Called from:
    //     saveAs8
    //     Globals.createQMeshModel
    public int scaleTo8(MemImage pScaledMImage) {
        // Scale a 32 bit memimage to 8 bits and copy it to the memImage passed in.
        // This version scales to the maximum dynamic range permitted by 8 bits of resolution
        // Consequently, depending on the range of values in the 32 bit image, contrast stretching
        // may occur.  This is desired for the purpose of visualizing a model's range image.
        //
        // Scaling is performed in such a way as to eliminate the effects of the default 
        // max ZBuffer value
        String sMsgText;
        boolean IGNOREMAXVALUE = true;

        if((pScaledMImage.miBitsPerPixel != 8) || (this.miBitsPerPixel != 32)) {
            Globals.statusPrint("MemImage.scaleTo8: Bits per pixel mismatch");
            return 1;
        }
        
        if((pScaledMImage.getHeight() != getHeight()) || (pScaledMImage.getWidth() != getWidth())) {
            Globals.statusPrint("MemImage.scaleTo8: Image sizes must be equal");
            return 2;
        }

        int ix, iy, iRows;
        float fTemp, fActMax, fActMin;
        float fDesMax, fDesMin, fsFactor;
        fTemp = (float *)this.bytes;

        // Don't need paddedWidth here because each row width
        // is by definition a multiple of 4 bytes
        iRows = this.miImageHeight;
        fDesMin = 1.0f;
        fDesMax = 255.0f;
        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            iRows = 1;
        }
        
        fActMax = fActMin = *fTemp;
        if(IGNOREMAXVALUE) {
            fActMax = 0.0f;
        }

        // Determine the image min and max values
        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
                if(*fTemp < fActMin) {
                    fActMin = *fTemp;
                }
                if(IGNOREMAXVALUE) {
                    if((*fTemp > fActMax) && (*(fTemp) != JICTConstants.F_ZBUFFERMAXVALUE)) {
                        fActMax = *fTemp;
                    }
                } else {
                    if(*fTemp > fActMax) { 
                        fActMax = *fTemp;
                    }
                }

                fTemp++;
            } // for ix
        } // for iy
        
        sMsgText = "MemImage.scaleTo8: actual Max: " + fActMax + " Min: " + fActMin;
        Globals.statusPrint(sMsgText);

        fsFactor = (fDesMax - fDesMin) / (fActMax - fActMin);
        fTemp = (float *)this.bytes;
        byte scaledValue;
        float desValue, actValue;

        // Scale the image
        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
                actValue = *fTemp;
                if(IGNOREMAXVALUE) {
                    if(actValue != JICTConstants.F_ZBUFFERMAXVALUE) {
                        desValue = ((actValue - fActMin) * fsFactor) + fDesMin;
                        scaledValue = (byte)(desValue + 0.5f);
                        pScaledMImage.setMPixel(ix, iy, scaledValue);
                    }
                } else {
                    desValue = ((actValue - fActMin) * fsFactor) + fDesMin;
                    scaledValue = (byte)(desValue + 0.5f);
                    pScaledMImage.setMPixel(ix, iy, scaledValue);
                }
                
                fTemp++;
            } // for x
        } // for y

        return 0;
    } // scaleTo8


    // Draws a MemImage on the indicated BufferedImage
    //
    // Called from:
    //     SceneList.preview
    //     SceneList.previewStill
    //     SceneList.render
    public void display(int piOutWidth, int piOutHeight) {
        // TODO: Rewrite this
        BufferedImage buffImg = this.mBuffImg;
        return;
        // TODO: Derive a Graphics2D from the BufferedImage to perform graphics
        /*
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
        */
    } // display


    // TODO: Replace paramater dc with one of type Graphics2D, 
    // as this method performs graphics
    // Called from:
    //     RenderObject.maskFromShape
    //     RenderObject.prepareCutout
    public int drawMask(Point2d[] paPoint2ds, int piNumVertices) {
        BufferedImage buffImage = this.mBuffImg;
        // TODO: Rewrite this
        return 0;
        
        /*
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
        graphics2D.drawPolygon(newdc, thePoints, numVertices);

        // Display the mask
        BitBlt(0, 0, this.imageWidth, this.imageHeight, newdc, 0, 0, SRCCOPY);

        // Copy the completed mask image back to the MemImage buffer
        int dwCount = this.paddedWidth * this.imageHeight;

        // The bitmap is stored using a width that is a 2 byte multiple
        GetBitmapBits(hBitmap, dwCount, this.bytes);

        // Restore the old bitmap
        SelectObject(newdc, holdBitmap);

        return 0;
        */
    } // drawMask


    // Called from:
    //     Globals.tweenImage
    //     SceneList.render
    public int copy(MemImage pOutMImage, int piXoffset, int piYoffset) {
        int x, y;

        if(pOutMImage.miBitsPerPixel != this.miBitsPerPixel) {
            Globals.statusPrint("MemImage.copy: Destination image does not have matching pixel depth");
            return -1;
        }

        if((this.miBitsPerPixel != 8) && (this.miBitsPerPixel != 24)) {
            Globals.statusPrint("MemImage.copy: Only 8 or 24 bit images are supported");
            return -2;
        }

        byte thePixel;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
        for (x = 1; x <= this.miImageWidth; x++) {
            for (y = 1; y < this.miImageHeight; y++) {
                switch (this.miBitsPerPixel) {
                case 8:
                    thePixel = getMPixel(x, y);
                    if(thePixel != 0) {
                        pOutMImage.setMPixel(x + piXoffset, y + piYoffset, thePixel);
                    }
                    break;

                case 24:
                    getMPixelRGB(x, y, bytRed, bytGreen, bytBlue);
                    pOutMImage.setMPixelRGB(x + piXoffset, y + piYoffset, bytRed, bytGreen, bytBlue);
                    break;
                }
            }
        }

        return 0;
    } // copy


    // Not called from within this file.
    // Could not find where this is being called from.
    public int fastCopy(MemImage pOutMImage, int piXOffset, int piYOffset) {
        if(pOutMImage.miBitsPerPixel != this.miBitsPerPixel) {
            Globals.statusPrint("MemImage.fastCopy: Destination image does not have matching pixel depth");
            return -1;
        }

        if(this.miBitsPerPixel != 8) {
            Globals.statusPrint("MemImage.fastCopy: Only 8 bit images are supported");
            return -2;
        }

        if ((piXOffset > pOutMImage.miImageWidth) || (piYOffset > pOutMImage.miImageHeight)) {
            Globals.statusPrint("MemImage.fastCopy: 1 or more offsets are larger than the output image");
            return -3;
        }

        int a = pOutMImage.miImageWidth  - piXOffset;
        int b = pOutMImage.miImageHeight - piYOffset;
        int c = piXOffset + this.miImageWidth - pOutMImage.miImageWidth;

        // Now we will copy a portion of array this.bytes to array outImage.bytes
        int outLoc = ((piYOffset * pOutMImage.miImageWidth) + piXOffset); // not used

        // copy b rows of image data
        int i, j;
        // byte *currentPix = this.bytes;
        int currentPix = 0;

        for (j = 1; j <= b; j++) {
            for (i = 1; i <= a; i++) {
                if(bytes[currentPix] != 0) {
                    // *outLoc = *currentPix;
                    pOutMImage.bytes[outLoc] = bytes[currentPix];
                }

                outLoc++; 
                currentPix++;  
            } // for i

            // if image is over the edge, skip those pixels then add the pad
            currentPix = currentPix + c + miPads;
            outLoc += (pOutMImage.miPads + piXOffset); 
        } // for j

        return 0;
    } // fastCopy


    // Not called from within this file
    // Could not find where this is called from.
    public int copy8To24(MemImage pOutMImage) {
        int x, y;

        if(pOutMImage.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.copy8To24: Destination image must have 24 bit pixels");
            return -1;
        }

        if(this.miBitsPerPixel != 8) {
            Globals.statusPrint("MemImage.copy8To24: Source image must have 8 bit pixels");
            return -2;
        }

        byte intensity;
        for (x = 1; x <= this.miImageWidth; x++) {
            for (y = 1; y < this.miImageHeight; y++) {
                intensity = getMPixel(x, y);
                pOutMImage.setMPixelRGB(x, y, intensity, intensity, intensity);
            } // for y
        } // for x

        return 0;
    } // copy8To24


    public byte getMPixel(int piX, int piY, char pcColor) {
        // Inputs piX and piY are assumed to be 1 relative
        // Returns the desired pixel from a color image
        int addr;
        // byte *thePixel;
        // byte *myTemp = bytes;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        addr = ((piY - 1) * (int)miPaddedWidth) + ((piX - 1)*3);  // 3 bytes/color pixel
        /*
        myTemp = myTemp + addr;
        thePixel = myTemp;
        if(aColor == 'B') return *thePixel;
        if(aColor == 'G') return *(thePixel + 1);
        if(aColor == 'R') return *(thePixel + 2);
        */
        if(pcColor == 'B') return bytes[addr];
        if(pcColor == 'G') return bytes[addr + 1];
        if(pcColor == 'R') return bytes[addr + 2];

        Globals.statusPrint("MemImage.getMPixel: Unknown color value");
        return 0;
    } // getMPixel


    // Sets parameters red, green and blue
    // Called from:
    //     adjustColor
    //     copy (if this.bitsPerPixel = 24)
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     Globals.tweenMesh
    public int getMPixelRGB(int piX, int piY, Byte pbytRed, Byte pbytGreen, Byte pbytBlue) {
        //  Inputs piX and piY are assumed to be 1 relative
        //  Returns the desired pixel from a color image
        if(this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.getMPixelRGB: Image must be 24 bits per pixel");
            return -1;
        }

        int addr;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        addr = ((piY - 1) * this.miPaddedWidth) + ((piX - 1)*(this.miBitsPerPixel/8));  // 3 bytes/color pixel

        // Set the output parameters
        pbytBlue  = bytes[addr];
        pbytGreen = bytes[addr + 1];
        pbytRed   = bytes[addr + 2];

        return 0;
    } // getMPixelRGB


    // Called from:
    //     adjustColor
    //     copy (if this.bitsPerPixel = 24)
    //     copy8To24
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     Globals.tweenMesh
    public int setMPixelRGB(int piX, int piY, byte pbytRed, byte pbytGreen, byte pbytBlue) {
        // Inputs piX and piY are assumed to be 1 relative
        // Returns the desired pixel from a color image
        if(this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.setMPixelRGB: Image must be 24 bits per pixel");
            return -1;
        }

        int addr;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        addr = ((piY - 1) * miPaddedWidth) + ((piX - 1)*(this.miBitsPerPixel/8));  // 3 bytes/color pixel

        bytes[addr]     = pbytBlue;
        bytes[addr + 1] = pbytGreen;
        bytes[addr + 2] = pbytRed;

        return 0;
    } // setMPixelRGB


    // Called from:
    //     adjustColor
    //     clearRectangle
    //     copy (if this.bitsPerPixel = 8)
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     Globals.tweenMesh
    //     Texture.createPlasma
    //     Texture.createTexture
    public int setMPixel(int piX, int piY, byte pbytValue) {
        // Inputs piX and piY are assumed to be 1 relative
        int addr;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        // If setMPixel is called with coordinates that are located outside 
        // the bounds of the associated image array, no action is taken.
        // See p 119 of Visual Special Effects Toolkit in C++.
        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        addr = ((piY - 1) * miPaddedWidth) + piX - 1;
        bytes[addr] = pbytValue;

        return pbytValue;
    } // setMPixel


    public int setMPixelA(float pfX, float pfY, byte value) {
        // Inputs pfX and pfY are assumed to be 1 relative
        int addr;
        // byte *myTemp = this.bytes;
        int myTemp;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            pfY = 1;
        }

        if (
        (pfY < 1) || (pfY > (float)this.miImageHeight) || 
        (pfX < 1) || (pfX > (float)this.miImageWidth)) {
            return -1;
        }

        // Calculate the weights
        float xa, xb, ya, yb, valaa, valba, valab, valbb;
        byte chromaColor = 0;

        xa = pfX - (int)pfX;
        xb = 1.0f - xa;
        ya = pfY - (int)pfY;
        yb = 1.0f - ya;
        valaa = (byte)((xa * ya * (float)value) + 0.5f);
        valba = (byte)((xb * ya * (float)value) + 0.5f);
        valab = (byte)((xa * yb * (float)value) + 0.5f);
        valbb = (byte)((xb * yb * (float)value) + 0.5f);

        addr = (((int)pfY - 1) * miPaddedWidth) + (int)pfX - 1;

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
        if(bytes[addr + miPaddedWidth] == chromaColor) {
            bytes[addr + miPaddedWidth] = valab;
        } else {
            bytes[addr + miPaddedWidth] = bytes[addr + miPaddedWidth] + valab;
        }

        /*
        if( *(myTemp + addr + paddedWidth + 1) == chromaColor ) {
            *(myTemp + addr + paddedWidth + 1) = valbb;
        } else {
            *(myTemp + addr + paddedWidth + 1) = *(myTemp + addr + paddedWidth + 1) + valbb;
        }
        */

        if(bytes[addr + miPaddedWidth + 1] == chromaColor ) {
            bytes[addr + miPaddedWidth + 1] = valbb;
        } else {
            bytes[addr + miPaddedWidth + 1] = bytes[addr + miPaddedWidth + 1] + valbb;
        }

        return value;
    } // setMPixelA


    public byte getMPixelA(float pfX, float pfY) {
        // Inputs pfX and pfY must be 1 relative
        int addr;
        byte[] myTemp = this.bytes;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            pfY = (byte)1;
        }

        if (
        (pfY < 1) || (pfY > (float)this.miImageHeight) || 
        (pfX < 1) || (pfX > (float)this.miImageWidth)) {
            return (byte)0;
        }

        // Calculate the Weights
        float xa, xb, ya, yb, waa, wba, wab, wbb;
        float valaa, valab, valba, valbb;
        float bucket = 0.0f;
        byte chromaColor = (byte)0;
        xa = pfX - (int)pfX;
        xb = 1.0f - xa;
        ya = pfY - (int)pfY;
        yb = 1.0f - ya;
        waa = xa * ya;
        wba = xb * ya;
        wab = xa * yb;
        wbb = xb * yb;

        addr = (((int)pfY - 1) * (int)miPaddedWidth) + (int)pfX - 1;
        valaa = myTemp[addr]; // TODO: Not done yet, need to assign 4 bytes
        valab = myTemp[addr + (int)miPaddedWidth]; // TODO: Not done yet, need to assign 4 bytes
        valba = myTemp[addr + 1]; // TODO: Not done yet, need to assign 4 bytes
        valbb = myTemp[addr + (int)miPaddedWidth + 1]; // TODO: Not done yet, need to assign 4 bytes

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
    //     alphaSmooth3
    //     alphaSmooth5
    //     alphaSmooth7
    //     copy (if this.bitsPerPixel = 8)
    //     copy8To24
    //     histogram
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     Globals.tweenMesh
    //     Texture.plasma
    public byte getMPixel(int piX, int piY) {
        // Inputs piX and piY are assumed to be 1 relative
        int addr;
        int iBytesIdx = 0; // index into this.bytes

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        // If getMPixel is called with coordinates that are located outside 
        // the bounds of the associted image array, no action is taken.
        // See p 119 of Visual Special Effects Toolkit in C++.
        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return 0;
        }

        addr = ((piY - 1) * miPaddedWidth) + piX - 1;
        iBytesIdx = iBytesIdx + addr;

        return this.bytes[iBytesIdx];
    } // getMPixel


    // Called from:
    //     Globals.createQMeshModel
    //     Globals.iwarpz
    //     Globals.tweenMesh
    //     Texture.createTexture
    public int setMPixel32(int piX, int piY, float pfValue) {
        // Inputs piX and piY are assumed to be 1 relative
        int addr;
        byte[] myTemp = this.bytes;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        addr = ((piY - 1) * (int)miPaddedWidth) + ((piX - 1) * (this.miBitsPerPixel/8));
        // myTemp = myTemp + addr;
        // pPixel = (float *)myTemp;
        // *(pPixel) = pfValue;
        myTemp[addr] = pfValue; // TODO: Not done yet, need to assign 4 bytes

        return 0;
    } // setMPixel32


    // Called from:
    //     Globals.iwarpz
    //     Globals.tweenMesh
    //     RenderObject.renderMeshz
    public float getMPixel32(int piX, int piY) {
        // Inputs x and y are assumed to be 1 relative
        int addr;
        byte[] myTemp = this.bytes;
        float pPixel;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1.0f;
        }

        addr = ((piY - 1) * miPaddedWidth) + ((piX - 1) * (this.miBitsPerPixel/8));
        // myTemp = myTemp + addr;
        pPixel = (float)myTemp[addr]; // TODO:Not done yet, need to assign 4 bytes to pPixel

        return pPixel;
    } // getMPixel32


    // Called from:
    //     adjustColor
    //     histogram
    //     printValue
    //     saveAs8
    //     scaleTo8
    //     Globals.tweenImage
    //     Globals.tweenMesh
    //     RenderObject.renderMeshz
    //     RenderObject.renderShapez
    public int getHeight() {
        return this.miImageHeight;
    } // getHeight


    // Called from:
    //     adjustColor
    //     histogram
    //     printValue
    //     saveAs8
    //     scaleTo8
    //     Globals.tweenImage
    //     Globals.tweenMesh
    //     RenderObject.renderMeshz
    public int getWidth() {
        return this.miImageWidth;
    } // getWidth


    public int getAccessMode() {
        return this.miAccessMode;
    } // getAccessMode


    public int getColorSpec() {
        return this.miColorSpec;
    } // getColorSpec


    // Called from:
    //     adjustColor
    //     histogram
    //     printValue
    //     saveAs8
    //     Globals.tweenImage
    //     Globals.tweenMesh
    public int getBitsPerPixel() {
        return this.miBitsPerPixel;
    } // getBitsPerPixel


    // Called from:
    //     SceneList.preview
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


    // Method writeBMP writes a MemImage object into the file whose name is passed
    // as a parameter. See p 103 of Visual Special Effects Toolkit in C++.
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     saveAs8
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     MainFrame.onToolsWarpImage
    //     SceneList.render
    //     Texture.createTexture
    public int writeBMP(String psFileName) {
        // TODO: Rewrite this
        // TODO: Here we need to set fp, of type File
        return 0;

        /*
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
        */
    } // writeBMP


    // This method reads a Windows .bmp image file into a MemImage object.
    // See p 103 of the book Visual Special Effects Toolkit in C++.
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    public int readBMP(String psFileName, int piColorSpec) {
        // TODO: Rewrite this
        return 0;
        /*
        BITMAPFILEHEADER bmFH;
        BITMAPINFOHEADER pbmIH;
        BITMAPINFO pbmInfo;
        WORD PalSize = 256;
        int x,y,index,pad24,pad8;
        int bmWidth, bmHeight, bmImgSize, bmScanWidth, bmScanWidth8; // originally unsigned
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
        */
    } // readBMP


    // Called from:
    //     Globals.createCutout
    //     Globals.makeRGBimage
    public int readNextRow() {
        boolean myStatus;
        int numBytesRead;

        myStatus = ReadFile(this.fp, this.bytes, this.miPaddedWidth, numBytesRead, null);
        if (myStatus) {
            return 1;
        } else {
            return 0;
        }
    } // readNextRow


    // Called from:
    //     Globals.makeRGBimage
    //     RenderObject.maskFromShape
    //     RenderObject.prepareCutout
    public void close() {
        
    } // close


    // Changed return value from int to boolean
    // Called from:
    //     Globals.makeRGBimage
    public boolean writeNextRow() {
        boolean myStatus;
        int numBytesWritten;

        myStatus = WriteFile(this.fp, this.bytes, this.miPaddedWidth, numBytesWritten, null);
        if (myStatus) {
            return true;
        } else {
            return false;
        }
    } // writeNextRow


    // TODO: Not a method of MemImage in the original C++ code
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     readBMP
    //     writeBMP
    int mapColorSpecToBitsPerPixel(int piColorSpec) {
        int iBpp = -1;

        if(
        piColorSpec == JICTConstants.I_REDCOLOR ||
        piColorSpec == JICTConstants.I_GREENCOLOR ||
        piColorSpec == JICTConstants.I_BLUECOLOR ||
        piColorSpec == JICTConstants.I_EIGHTBITMONOCHROME ) {
            iBpp = 8;
        }

        if(piColorSpec == JICTConstants.I_RGBCOLOR ) {
            iBpp = 24;
        }
        if(piColorSpec == JICTConstants.I_ONEBITMONOCHROME ) {
            iBpp = 1;
        }
        if(piColorSpec == JICTConstants.I_A32BIT ) {
            iBpp = 32;
        }

        return iBpp;
    } // mapColorSpecToBitsPerPixel


    // TODO: Not a method of MemImage in the original C++ code
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     The MemImage constructor that takes 3 parameters
    int mapBitsPerPixelToColorSpec(int piBitsPerPixel) {
        int iColorSpec = JICTConstants.I_EIGHTBITMONOCHROME;

        if(piBitsPerPixel ==  1) iColorSpec = JICTConstants.I_ONEBITMONOCHROME;
        if(piBitsPerPixel ==  8) iColorSpec = JICTConstants.I_EIGHTBITMONOCHROME;
        if(piBitsPerPixel == 24) iColorSpec = JICTConstants.I_RGBCOLOR;
        if(piBitsPerPixel == 32) iColorSpec = JICTConstants.I_A32BIT;

        return iColorSpec;
    } // mapBitsPerPixelToColorSpec


    public int getImageSizeInBytes() {
        return this.miImageHeight * this.miPaddedWidth;
    } // getImageSizeInBytes


    // Called from:
    //     Globals.iwarpz
    public int saveAs8(String psOutImagePathName) {
        //  A utility to save a 32 bit image in a viewable 8 bit form
        String sMsgText;
        int iBpp = getBitsPerPixel();

        if(iBpp != 32) {
            sMsgText = "MemImage.saveAs8: Input image must be 32 bits per pixel. " + psOutImagePathName;
            Globals.statusPrint(sMsgText);
            return -1;
        }

        int iOutputRows = getHeight();
        int iOutputCols = getWidth();
        MemImage testImage = new MemImage(iOutputRows, iOutputCols, 8);
        if (!testImage.isValid()) {
            sMsgText = "MemImage.saveAs8: Unable to create intermediate image. " + psOutImagePathName;
            Globals.statusPrint(sMsgText);
            return -1;
        }

        scaleTo8(testImage);
        sMsgText = "MemImage.saveAs8: Saving " + psOutImagePathName;
        Globals.statusPrint(sMsgText);

        testImage.writeBMP(psOutImagePathName);
        sMsgText = "MemImage.saveAs8: Histogram of " + psOutImagePathName;
        Globals.statusPrint(sMsgText);
        testImage.histogram();

        return 0;
    } // saveAs8


    // Called from:
    //     saveAs8
    public int histogram() {
        String sMsgBuffer;

        if(getBitsPerPixel() != 8) {
            Globals.statusPrint("MemImage.histogram: Only 8 bit pixel images are supported");
            return -1;
        }

        int[] histogArray = new int[256];
        int iNumRows = getHeight();
        int iNumCols = getWidth();
        int i, j;
        int iPixel;

        for (i = 0; i <= 255; i++) {
            histogArray[i] = 0;
        } // for i

        for (i = 1; i <= iNumRows; i++) {
            for (j = 1; j <= iNumCols; j++) {
                iPixel = (int)getMPixel(j, i);
                histogArray[iPixel]++;
            } // for j
        } // for i

        // Display the histogram to the ict log
        sMsgBuffer = "MemImage.histogram: Histogram of " + msSavedFileName;
        Globals.statusPrint(sMsgBuffer);
        for(i = 0; i <= 240; i += 16) {
            sMsgBuffer = String.format("%3d: %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d",
                i, 
                histogArray[i+ 0], histogArray[i+ 1], histogArray[i+ 2], histogArray[i+ 3], 
                histogArray[i+ 4], histogArray[i+ 5], histogArray[i+ 6], histogArray[i+ 7], 
                histogArray[i+ 8], histogArray[i+ 9], histogArray[i+10], histogArray[i+11], 
                histogArray[i+12], histogArray[i+13], histogArray[i+14], histogArray[i+15]);
            Globals.statusPrint(sMsgBuffer);
        } // for i

        return 0;
    } // histogram
    

    // For more information on this method, see pages 110 - 118 of the book
    // Visual Special Effects Toolkits in C++
    // Sets pMidRed, pMidGreen and pMidBlue
    public int adjustColor(int piDesiredRed, int piDesiredGreen, int piDesiredBlue,
    Byte pbytMidRed, Byte pbytMidGreen, Byte pbytMidBlue, 
    MemImage pOutMImage, 
    String psAdjustmentType, int piInputImageColor) {
        // Calculate the average color in the image excluding zeros
        // Compute the color difference vector between the average color and the desired color
        // Create a new image by adjusting each color by the difference vector, clipping outliers
        float fRedBucket, fGreenBucket, fBlueBucket;
        float fAvgRed = 0.0f, fAvgGreen = 0.0f, fAvgBlue = 0.0f;
        int iBpp = getBitsPerPixel();
        if((iBpp != 24) && (iBpp != 8)) {
            Globals.statusPrint("MemImage.adjustColor: Input image must be 8 or 24 bits per pixel");
            return -1;
        }

        int iNumRows = getHeight();
        int iNumCols = getWidth();
        int iRow, iCol;
        Byte red = (byte)0, green = (byte)0, blue = (byte)0;
        fRedBucket   = 0.0f;
        fGreenBucket = 0.0f;
        fBlueBucket  = 0.0f;
        int iTotalPixels = 0;
        int iNewRed, iNewGreen, iNewBlue;
        int aValue, newValue;

        if(psAdjustmentType.equalsIgnoreCase("Target")) {
            for(iRow = 1; iRow <= iNumRows; iRow++) {
                for(iCol = 1; iCol <= iNumCols; iCol++) {
                    switch(iBpp) {
                    case 24:
                        // The following sets parameters red, green, and blue to the colors
                        // stored at (row, col), as mapped to field bytes
                        getMPixelRGB(iCol, iRow, red, green, blue);
                        if (red != 0 || green != 0 || blue != 0) {
                            fRedBucket += (red);
                            fGreenBucket += (green);
                            fBlueBucket += (blue);
                            iTotalPixels++;
                        }
                        break;

                    case 8:
                        aValue = getMPixel(iCol, iRow);
                        if (aValue != 0) {
                            fGreenBucket += (aValue);
                            iTotalPixels++;
                        }
                        break;
                    } // switch
                } // for col
            } // for row

            if(iTotalPixels == 0) {
                Globals.statusPrint("MemImage.adjustColor: Cannot adjust image color. No non-zero pixels!");
                return -1;
            }

            fRedBucket   /= (float)iTotalPixels;
            fGreenBucket /= (float)iTotalPixels;
            fBlueBucket  /= (float)iTotalPixels;
            pbytMidRed   = (byte)(fRedBucket   + 0.5f);
            pbytMidGreen = (byte)(fGreenBucket + 0.5f);
            pbytMidBlue  = (byte)(fBlueBucket  + 0.5f);

            // Calculate the color difference vector
            switch(iBpp) {
            case 24:
                fAvgRed   = (float)piDesiredRed   - fRedBucket;
                fAvgGreen = (float)piDesiredGreen - fGreenBucket;
                fAvgBlue  = (float)piDesiredBlue  - fBlueBucket;
                break;

            case 8:
                if(piInputImageColor == JICTConstants.I_REDCOLOR) {
                    fAvgGreen = (float)piDesiredRed   - fGreenBucket;
                }  
                if(piInputImageColor == JICTConstants.I_GREENCOLOR) {
                    fAvgGreen = (float)piDesiredGreen - fGreenBucket;
                }
                if(piInputImageColor == JICTConstants.I_BLUECOLOR) {
                    fAvgGreen = (float)piDesiredBlue  - fGreenBucket;
                }
                break;
            } // switch
        } 

        if(psAdjustmentType.equalsIgnoreCase("Relative")) {
            switch(iBpp) {
            case 24:
                fAvgRed   = (float)piDesiredRed;
                fAvgGreen = (float)piDesiredGreen;
                fAvgBlue  = (float)piDesiredBlue;
                break;

            case 8:
                if (piInputImageColor == JICTConstants.I_REDCOLOR) {
                    fAvgGreen = piDesiredRed;
                }
                if (piInputImageColor == JICTConstants.I_GREENCOLOR) {
                    fAvgGreen = piDesiredGreen;
                }
                if (piInputImageColor == JICTConstants.I_BLUECOLOR) {
                    fAvgGreen = piDesiredBlue;
                }
                break;
            } // switch
        } // if(strcmpi(adjustmentType, "Relative") == 0)

        for(iRow = 1; iRow <= iNumRows; iRow++) {
            for(iCol = 1; iCol <= iNumCols; iCol++) {
                switch(iBpp) {
                case 24:
                    getMPixelRGB(iCol, iRow, red, green, blue);
                    if ((red != 0) || (green != 0) || (blue != 0)) {
                        iNewRed   = (int)(red + fAvgRed);
                        iNewGreen = (int)(green + fAvgGreen);
                        iNewBlue  = (int)(blue + fAvgBlue);

                        if (iNewRed < 1)     iNewRed   = 1;
                        if (iNewRed > 255)   iNewRed   = 255;
                        if (iNewGreen < 1)   iNewGreen = 1;
                        if (iNewGreen > 255) iNewGreen = 255;
                        if (iNewBlue < 1)    iNewBlue  = 1;
                        if (iNewBlue > 255)  iNewBlue  = 255;

                        pOutMImage.setMPixelRGB(iCol, iRow, 
                            (byte)iNewRed, (byte)iNewGreen, (byte)iNewBlue);
                    }
                    break;

                case 8:
                    aValue = getMPixel(iCol, iRow);
                    if (aValue != 0) {
                        newValue = aValue + (int)fAvgGreen;
                        if (newValue < 1) {
                            newValue = 1;
                        }
                        if (newValue > 255) {
                            newValue = 255;
                        }
                        pOutMImage.setMPixel(iCol, iRow, (byte)newValue);
                    }
                    break;
                } // switch
            } // for col
        } // for row

        if(psAdjustmentType.equalsIgnoreCase("Delta")) {
            for(iRow = 1; iRow <= iNumRows; iRow++) {
                for(iCol = 1; iCol <= iNumCols; iCol++) {
                    switch(iBpp) {
                    case 24:
                        getMPixelRGB(iCol, iRow, red, green, blue);
                        if ((red != 0) || (green != 0) || (blue != 0)) {
                            iNewRed   = red   + piDesiredRed;
                            iNewGreen = green + piDesiredGreen;
                            iNewBlue  = blue  + piDesiredBlue;
                            pOutMImage.setMPixelRGB(iCol, iRow, (byte)iNewRed, (byte)iNewGreen, (byte)iNewBlue);
                        }
                        break;

                    case 8:
                        aValue = getMPixel(iCol, iRow);
                        if (aValue != 0) {
                            iNewGreen = aValue + piDesiredGreen;
                            pOutMImage.setMPixel(iCol, iRow, (byte)iNewGreen);
                        }
                        break;
                    } // switch
                } // for col
            } // for row
        }

        return 0;
    } // adjustColor
    

    public int printValue(int piX, int piY) {
        String sMsgBuffer;
        int iBpp = getBitsPerPixel();

        if((iBpp != 8) && (iBpp != 32)) {
            Globals.statusPrint("MemImage.printValue: Only 8 or 32 bit images are supported");
            return -1;
        }

        if((piX > getWidth()) || (piY > getHeight())) {
            sMsgBuffer = "MemImage.printValue: Either x or y are > image bounds. x: " + piX + "  y: " + piY;
            Globals.statusPrint(sMsgBuffer);
            return -1;
        }

        byte[] lineBuffer   = new byte[32];
        float[] fLineBuffer = new float[32];

        sMsgBuffer = "MemImage.printValue: Display of " + msSavedFileName;
        Globals.statusPrint(sMsgBuffer);
        sMsgBuffer = String.format("   %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d %6d",
                piX,     piX + 1, piX +  2, piX +  3, 
                piX + 4, piX + 5, piX +  6, piX +  7, 
                piX + 8, piX + 9, piX + 10, piX + 11);
        Globals.statusPrint(sMsgBuffer);

        int i, j;
        int iNumRows = 10;
        int iNumCols = 12;
        if ((piY + iNumRows) > getHeight()) {
            iNumRows = getHeight() - piY;
        }

        if ((piX + iNumCols) > getWidth()) {
            iNumCols = getWidth() - piX;
        }

        for (i = piY; i <= piY + iNumRows; i++) {
            // display the image row to the ict log
            switch(iBpp) {
            case 8:
                for(j = 0; j <= iNumCols; j++) {
                    lineBuffer[j] = getMPixel(piX + j, i);
                }

                sMsgBuffer = String.format("%3d: %4d %4d %4d %4d %4d %4d %4d %4d %4d %4d %4d %4d",
                    i, 
                    lineBuffer[0], lineBuffer[ 1], lineBuffer[ 2],
                    lineBuffer[3], lineBuffer[ 4], lineBuffer[ 5], 
                    lineBuffer[6], lineBuffer[ 7], lineBuffer[ 8], 
                    lineBuffer[9], lineBuffer[10], lineBuffer[11]);
                Globals.statusPrint(sMsgBuffer);
                break;

            case 32:
                for(j = 0; j <= iNumCols; j++) {
                    fLineBuffer[j] = getMPixel32(piX + j, i);
                }

                sMsgBuffer = String.format("%3d: %6g %6g %6g %6g %6g %6g %6g %6g %6g %6g %6g %6g",
                    i, 
                    fLineBuffer[0], fLineBuffer[ 1], fLineBuffer[ 2],
                    fLineBuffer[3], fLineBuffer[ 4], fLineBuffer[ 5], 
                    fLineBuffer[6], fLineBuffer[ 7], fLineBuffer[ 8], 
                    fLineBuffer[9], fLineBuffer[10], fLineBuffer[11]);
                Globals.statusPrint(sMsgBuffer);
                break;
            } // switch
        } // for i

        return 0;
    } // printValue
    

    // Called from:
    //     RenderObject.renderMeshz
    //     SceneList.render
    public void setFileName(String psFileName) {
        this.msSavedFileName = psFileName;
    } // setFileName


    public int clearRectangle(int piStartX, int piStartY, int piEndX, int piEndY) {
        int iPixelDepth = getBitsPerPixel();
        int iRow, iCol;

        for (iRow = piStartY; iRow <= piEndY; iRow++) {
            for (iCol = piStartX; iCol <= piEndX; iCol++) {
                switch (iPixelDepth) {
                case 8:
                    setMPixel(iCol, iRow, (byte)0);
                    break;

                case 24:
                    setMPixelRGB(iCol, iRow, (byte)0, (byte)0, (byte)0);
                    break;

                case 32:
                    setMPixel32(iCol, iRow, 0.0f);
                    break;

                default:
                    Globals.statusPrint("MemImage.clearRectangle: Unsupported pixel depth");
                    return -1;
                } // switch
            } // for col
        } // for row

        return 0;
    } // clearRectangle


    // This method will set parameters xBeg, xEnd, yBeg and yEnd
    public int getBoundingBox(Integer pIXBeg, Integer pIXEnd, Integer pIYBeg, Integer pIYEnd) {
        int x, y;
        if((this.miBitsPerPixel != 8) && (this.miBitsPerPixel != 24)) {
            Globals.statusPrint("MemImage.getBoundingBox: Only 8 or 24 bit images are supported");
            return -2;
        }

        byte thePixel;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
        pIXBeg = Math.max(this.miImageWidth, this.miImageHeight);
        pIYBeg = Math.max(this.miImageWidth, this.miImageHeight);
        pIXEnd = 1;
        pIYEnd = 1;

        for (x = 1; x <= this.miImageWidth; x++) {
            for (y = 1; y < this.miImageHeight; y++) {
                switch (this.miBitsPerPixel) {
                case 8:
                    thePixel = getMPixel(x, y);
                    if(thePixel != JICTConstants.I_CHROMAVALUE) {
                        if(x < pIXBeg) pIXBeg = x;
                        if(x > pIXEnd) pIXEnd = x;
                        if(y < pIYBeg) pIYBeg = x;
                        if(y > pIYEnd) pIYEnd = x;
                    }
                    break;

                case 24:
                    getMPixelRGB(x, y, bytRed, bytGreen, bytBlue);
                    if(bytRed != JICTConstants.I_CHROMARED) {
                        if(x < pIXBeg) pIXBeg = x;
                        if(x > pIXEnd) pIXEnd = x;
                        if(y < pIYBeg) pIYBeg = x;
                        if(y > pIYEnd) pIYEnd = x;
                    }
                    if(bytGreen != JICTConstants.I_CHROMAGREEN) {
                        if(x < pIXBeg) pIXBeg = x;
                        if(x > pIXEnd) pIXEnd = x;
                        if(y < pIYBeg) pIYBeg = x;
                        if(y > pIYEnd) pIYEnd = x;
                    }
                    if(bytBlue != JICTConstants.I_CHROMABLUE) {
                        if(x < pIXBeg) pIXBeg = x;
                        if(x > pIXEnd) pIXEnd = x;
                        if(y < pIYBeg) pIYBeg = x;
                        if(y > pIYEnd) pIYEnd = x;
                    }
                    break;
                } // switch
            } // for y

            Globals.statusPrint(msSavedFileName);
            String msgText = "MemImage.getBoundingBox: xBeg: " + pIXBeg + "  xEnd: " + pIXEnd + " yBeg: " + pIYBeg + "  yEnd: " + pIYEnd; 
            Globals.statusPrint(msgText);
        } // for x

        return 0;
    } // getBoundingBox


    // This method came from BLEND.CPP
    // Called from:
    //     Globals.tweenImage
    //     RenderObject.renderMeshz
    public int createAlphaImage(MemImage pOutMImage) {
        int ix, iy;
        byte thePixel;
        Byte aRed = (byte)0, aGreen = (byte)0, aBlue = (byte)0;

        if(pOutMImage.miBitsPerPixel != 8) {
            Globals.statusPrint("MemImage.createAlphaImage: Output image must be 8 bits per pixel");
            return -1;
        }

        for (iy = 1; iy <= miImageHeight; iy++) {
            for (ix = 1; ix < miImageWidth; ix++) {
                switch(miBitsPerPixel) {
                case 8:
                    thePixel = getMPixel(ix, iy);
                    break;

                case 24:
                    // The following method sets parameters aRed, aGreen and aBlue
                    getMPixelRGB(ix, iy, aRed, aGreen, aBlue);
                    thePixel = JICTConstants.I_CHROMAVALUE;
                    if(
                    aRed != JICTConstants.I_CHROMAVALUE || 
                    aGreen != JICTConstants.I_CHROMAVALUE ||
                    aBlue != JICTConstants.I_CHROMAVALUE) { 
                        thePixel = 255;
                    }
                    break;
                } // switch

                if(thePixel != JICTConstants.I_CHROMAVALUE) {
                    pOutMImage.setMPixel(ix, iy, (byte)255);
                }
            } // for ix
        } // for iy

        return 0;
    } // createAlphaImage
    

    // This method came from BLEND.CPP
    // Called from:
    //     RenderObject.maskFromShape
    //     RenderObject.prepareCutout
    public int unPack(MemImage pOutputMImage) {
        // Convert a one bit MemImage to an 8 bit MemImage
        //
        // The input image must be one bit per pixel
        // The output image must be 8 bits per pixel
        // The output image must be opened for RANDOM access
        if (miColorSpec != JICTConstants.I_ONEBITMONOCHROME) {
            Globals.statusPrint("MemImage.unPack: Input image colorSpec must be ONEBITMONOCHROME");
            return 1;
        }
      
        if(pOutputMImage.miAccessMode != JICTConstants.I_RANDOM) {
            Globals.statusPrint("MemImage.unPack: Output image access mode must be RANDOM");
            return 2;
        }

        if (
        pOutputMImage.miColorSpec == JICTConstants.I_RGBCOLOR ||
        pOutputMImage.miColorSpec == JICTConstants.I_ONEBITMONOCHROME) {
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
        int myWidth   = miImageWidth / 8;
        int remainder = miImageWidth % 8;
        if (remainder > 0) {
            myWidth++;
        }
        if((myWidth/2*2) != myWidth) {
            myWidth++;
        }
      
        for (y = 1; y <= miImageHeight; y++) {
            int xCounter = 0;
            for (x = 1; x <= myWidth; x++) {
                packedByte = *packedBytes;
                for(int bitCounter = 0; bitCounter < 8; bitCounter++) {
                    xCounter++;
                    if(xCounter <= miImageWidth) {
                        if((packedByte >> (7-bitCounter)) & 0x1) {
                            pOutputMImage.setMPixel(xCounter, miImageHeight-(y-1), 255);
                        } else {
                            pOutputMImage.setMPixel(xCounter, miImageHeight-(y-1), (byte)JICTConstants.I_CHROMAVALUE);
                        }
                    }
                } // for bitCounter

                packedBytes++;
            } // for x
        } // for y

        return 0;
    } // unPack
    

    // This method came from BLEND.CPP
    // Called from:
    //     MorphDlg.onOK
    public int adjustImageBorder(String psOutPath) {
        // Find the size of the chromakey border around an image and
        // create a new image in which the border has been removed.
        //
        // This function effectively centers rotoscoped images that came
        // from sources other than function createAlphaImage.
        int imHeight = getHeight();
        int imWidth  = getWidth();
        if(this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.adjustImageBorder: Image must have 24 bit pixels.");
            return -1;
        }

        int row, col;
        int minX = imWidth;
        int maxX = 1;
        int minY = imHeight;
        int maxY = 1;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
      
        for (row = 1; row <= imHeight; row++) {
            for (col = 1; col <= imWidth; col++) {
                getMPixelRGB(col, row, bytRed, bytGreen, bytBlue);
                if(
                bytRed != JICTConstants.I_CHROMARED || 
                bytGreen != JICTConstants.I_CHROMAGREEN ||
                bytBlue != JICTConstants.I_CHROMABLUE) {
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
                getMPixelRGB(col, row, bytRed, bytGreen, bytBlue);
                outImage.setMPixelRGB(outCol, outRow, bytRed, bytGreen, bytBlue);
                outCol++;
            }

            outRow++;
        }

        outImage.writeBMP(psOutPath);
        return 0;
    } // adjustImageBorder


    // This method came from IWARP.CPP
    // Called from:
    //     Globals.tweenImage
    public int alphaSmooth3() {
        // Each image must be the same size.
        MemImage inImage  = this;
        MemImage outImage = this;

        if(inImage.getHeight() != outImage.getHeight() || 
        inImage.getWidth() != outImage.getWidth()) {
            Globals.statusPrint("MemImage.alphaSmooth3: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() != outImage.getBitsPerPixel()) ||  
        (inImage.getBitsPerPixel() != 8)) {
            Globals.statusPrint("MemImage.alphaSmooth3: Images must have 8 bit pixels.");
            return -2;
        }

        // int bpp = inImage.getBitsPerPixel(); // not used
        /* Not used
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
        */

        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        //float x1 = 0.0f; // not used
        //float y1 = 0.0f; // not used
        //float z1 = 0.0f; // not used
        //float totalCells = 0.0f; // not used
        int row, col;
        float sum;
        float q00, q10, q20;
        float q01, q11, q21;
        float q02, q12, q22;
        float factor = 1.0f/9.0f;

        for (row = 2; row <= imHeight - 1; row++) {
            for (col = 2; col <= imWidth - 1; col++) {
                q11 = inImage.getMPixel(col, row);
                if(q11 != 0) {
                    // Calculate average color of 3x3 cell centered about row, col
                    q00 = inImage.getMPixel(col - 1, row - 1);
                    q10 = inImage.getMPixel(col    , row - 1);
                    q20 = inImage.getMPixel(col + 1, row - 1);

                    q01 = inImage.getMPixel(col - 1, row);
                    // q11 is calculated outside if stmt
                    q21 = inImage.getMPixel(col + 1, row);

                    q02 = inImage.getMPixel(col - 1, row + 1);
                    q12 = inImage.getMPixel(col    , row + 1);
                    q22 = inImage.getMPixel(col + 1, row + 1);

                    // Note that the sum below is the sum of 9 values,
                    // one for each cell in a 3x3 cell
                    sum = (q00 + q10 + q20
                        + q01 + q11 + q21 
                        + q02 + q12 + q22) * factor; 
                
                    float diff = 255.0f - sum;   // steepen the ramp
                    sum -= (diff * 0.5f);
                    sum = MathUtils.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col, row, (byte)(sum + 0.5f));
                } // if
            } // for col
        } // for row

        return 0;
    } // alphaSmooth3
    

    // This method came from IWARP.CPP
    // Called from:
    //     Globals.createCutout
    //     MainFrame.onToolsCreateAlphaImage
    //     RenderObject.renderMeshz
    public int alphaSmooth5() {
        //
        // Each image must be the same size.
        //
        MemImage inImage = this;
        MemImage outImage = this;

        if(
        inImage.getHeight() != outImage.getHeight() || 
        inImage.getWidth() != outImage.getWidth()) {
            Globals.statusPrint("MemImage.alphaSmooth5: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() != outImage.getBitsPerPixel()) || 
        (inImage.getBitsPerPixel() != 8)) {
            Globals.statusPrint("MemImage.alphaSmooth5: Images must have 8 bit pixels.");
            return -2;
        }

        // int bpp = inImage.getBitsPerPixel(); // not used
        /* Not used
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
        */

        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        //float x1 = 0.0f; // not used
        //float y1 = 0.0f; // not used
        //float z1 = 0.0f; // not used
        //float totalCells = 0.0f; // not used
        int row, col;
        float sum;
        float q00, q10, q20, q30, q40;
        float q01, q11, q21, q31, q41;
        float q02, q12, q22, q32, q42;
        float q03, q13, q23, q33, q43;
        float q04, q14, q24, q34, q44;
        float factor = 1.0f/25.0f;

        for (row = 3; row <= imHeight - 2; row++) {
            for (col = 3; col <= imWidth - 2; col++) {
                q22 = inImage.getMPixel(col, row);
                if(q22 != 0) {
                    // Calculate average color of 5x5 cell centered about row, col
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
                    // q22 is calculated outside if stmt
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

                    // Note that the sum below is the sum of 25 values,
                    // one for each cell in a 5x5 cell
                    sum = (q00 + q10 + q20 + q30 + q40
                        + q01 + q11 + q21 + q31 + q41 
                        + q02 + q12 + q22 + q32 + q42 
                        + q03 + q13 + q23 + q33 + q43 
                        + q04 + q14 + q24 + q34 + q44) * factor; 
                
                    float diff = 255.0f - sum;   // steepen the ramp
                    sum -= (diff * 0.5f);
                    sum = MathUtils.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col, row, (byte)(sum + 0.5f));
                } // if
            } // for col
        } // for row

        return 0;
    } // alphaSmooth5
    

    // This method originally came from IWARP.CPP
    // Could not find where this method is called from.
    public int alphaSmooth7() {
        // Each image must be the same size.
        MemImage inImage  = this;
        MemImage outImage = this;

        if(
        inImage.getHeight() != outImage.getHeight() ||
        inImage.getWidth()  != outImage.getWidth() ) {
            Globals.statusPrint("MemImage.alphaSmooth7: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inImage.getBitsPerPixel() != outImage.getBitsPerPixel()) || 
        (inImage.getBitsPerPixel() != 8)) {
            Globals.statusPrint("MemImage.alphaSmooth7: Images must have 8 bit pixels.");
            return -2;
        }

        // int bpp = inImage.getBitsPerPixel(); not used
        int imHeight = inImage.getHeight();
        int imWidth  = inImage.getWidth();
        //float x1 = 0.0f; // not used
        //float y1 = 0.0f; // not used
        //float z1 = 0.0f; // not used
        //float totalCells = 0.0f; // not used
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
                    // Calculate average color of 7x7 cell centered about row, col
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
                    // q33 is calculated outside if stmt
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

                    // Note that the sum below is the sum of 49 values,
                    // one for each cell in a 7x7 cell
                    sum = (q00 + q10 + q20 + q30 + q40 + q50 + q60
                        + q01 + q11 + q21 + q31 + q41 + q51 + q61 
                        + q02 + q12 + q22 + q32 + q42 + q52 + q62 
                        + q03 + q13 + q23 + q33 + q43 + q53 + q63 
                        + q04 + q14 + q24 + q34 + q44 + q54 + q64
                        + q05 + q15 + q25 + q35 + q45 + q55 + q65
                        + q06 + q16 + q26 + q36 + q46 + q56 + q66)
                        * factor; 

                    float diff = 255.0f - sum;   // steepen the ramp
                    sum -= (diff * 0.5f);
                    sum = MathUtils.bound(sum, 0.0f, 255.0f);
                    outImage.setMPixel(col, row, (byte)(sum + 0.5f));
                } // if
            } // for col
        } // for row

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
        // float totalIntensity, avgIntensity; // not used
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
        float oldZ, intensity, distance; 
        // float outIntensity; // not used
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

        // int minX, minY, maxX, maxY, j; // not used
        // float minI, maxI, minD, maxD; // not used
        // float nSteps, intensityStep, distanceStep; // not used
        // int row, col, denominator; // not used

        // Handle larger quadrangles
        int xCent = (int)(((float)xMin + ((float)xMax - (float)xMin) / 2.0f) + 0.5f);
        int yCent = (int)(((float)yMin + ((float)yMax - (float)yMin) / 2.0f) + 0.5f);

        // The intensity at the centroid is the weighted sum of the intensities at each vertex
        // The weights are the normalized distances between each vertex distance and the centroid
        // fill the triangle bounded by the centroid and each successive pair of vertices
        float totalDistance = 0.0f;
        float d1 = MathUtils.getDistance2d((float)xCent, (float)yCent, (float)I1x, (float)I1y);
        float d2 = MathUtils.getDistance2d((float)xCent, (float)yCent, (float)I2x, (float)I2y);
        float d3 = MathUtils.getDistance2d((float)xCent, (float)yCent, (float)I3x, (float)I3y);
        float d4 = MathUtils.getDistance2d((float)xCent, (float)yCent, (float)I4x, (float)I4y);
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
} // class MemImage