package core;

import globals.Globals;
import globals.JICTConstants;

import java.awt.image.BufferedImage;
import java.io.File;

import math.MathUtils;

import structs.Point2d;

public class MemImage {
    private boolean bIctDebug = false;
    protected File fp;
    protected String msSavedFileName;   // The last associated pathname

    // Consider making private - it is returned by method getHeight
    protected int miImageHeight;    // Height in pixels

    // Consider making private - it is returned by method getWidth
    protected int miImageWidth;     // Width in pixels

    // Consider making private - itis returned by method getBitsPerPixel
    protected int miBitsPerPixel;   // Bits per pixel
    protected int miPaddedWidth;    // Physical width of the image in bytes (multiple of 4)
    protected int miPads;           // Difference between imagewidth and paddedwidth (bytes)

    // Consider making private - it is returned by method getAccessMode
    protected int miAccessMode;     // SEQUENTIAL or RANDOM

    // Consider making private - it is returned by method getColorSpec
    protected int miColorSpec;   // Indicates the desired color mapping

    // State indicator.  1 if successful, else 0
    // I changed this from int to boolean
    // Consider making private - it is returned by method isValid
    protected boolean mbValid;

    // Consider making private - it is returned by method getBytes
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


    // This constructor originally came from MEMIMG32.CPP
    // 
    // Constructor creates a MemImage object. If any of the parameters 
    // piImHeight, piImWidth, or piColorSpec is zero, the code will read the
    // height and width from the .bmp file and store it in piImHeight and piImWidth, 
    // and if the piColorSpec is zero it will set it to the return value of
    // method mapBitsPerPixelToColorSpec.
    // 
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
        String sMsgBuffer;

        // Check for parameters that might be invalid
        if (
        piColorSpec != JICTConstants.I_ONEBITMONOCHROME && 
        piColorSpec != JICTConstants.I_REDCOLOR &&
        piColorSpec != JICTConstants.I_GREENCOLOR && 
        piColorSpec != JICTConstants.I_BLUECOLOR &&
        piColorSpec != JICTConstants.I_RGBCOLOR && 
        piColorSpec != JICTConstants.I_EIGHTBITMONOCHROME &&
        piColorSpec != 0 && 
        piColorSpec != JICTConstants.I_A32BIT) {
            sMsgBuffer = "MemImage Constructor 1: ColorSpec not valid - " + piColorSpec;
            Globals.statusPrint(sMsgBuffer);
            this.mbValid = false;
            return;
        }

        if((pcRW != 'R') && (pcRW != 'r') && (pcRW != 'W') && (pcRW != 'w')) {
            sMsgBuffer = "MemImage Constructor 1: pcRW must be R or W - " + pcRW;
            Globals.statusPrint(sMsgBuffer);
            this.mbValid = false;
            return;
        }

        if(
        (piImAccessMode != JICTConstants.I_RANDOM) && 
        (piImAccessMode != JICTConstants.I_SEQUENTIAL)) {
            sMsgBuffer = "MemImage Constructor 1: accessMode must be RANDOM or SEQUENTIAL - " + piImAccessMode;
            Globals.statusPrint(sMsgBuffer);
            this.mbValid = false;
            return;
        }

        // Check for incompatible parameters
        if(
        (pcRW == 'W' || pcRW == 'w') && 
        (piImHeight <= 0 || piImWidth <= 0 || piColorSpec == 0)) {
            Globals.statusPrint("MemImage Constructor 1: length, width and colorSpec must be > 0 for write access");
            this.mbValid = false;
            return;
        }

        // Done with parameter validity checks
        Integer iBitsPerPixel = 0; 
        int iStatus = 0;
        this.mbValid = true;
        iBitsPerPixel = mapColorSpecToBitsPerPixel(piColorSpec);
        this.msSavedFileName = psFileName;

        // Get a preview of the file 
        Integer iHeight = 0, iWidth = 0;
        if(piImHeight == 0 || piImWidth == 0 || piColorSpec == 0) {
            // The following method sets iHeight, iWidth and iBitsPerPixel
            iStatus = Globals.readBMPHeader(psFileName, iHeight, iWidth, iBitsPerPixel);
            if(iStatus != 0) {
                this.mbValid = false;
                Globals.statusPrint("MemImage Constructor 1: Unable to open BMP header for read access");
                return;
            }

            piImHeight = iHeight;
            piImWidth = iWidth;
            if(piColorSpec == 0) {
                piColorSpec = mapBitsPerPixelToColorSpec(iBitsPerPixel);
            }
        } // if(piImHeight == 0 || piImWidth == 0 || piColorSpec == 0)

        //  Assign the MemImage properties
        this.miImageHeight  = piImHeight;
        this.miImageWidth   = piImWidth;
        this.miBitsPerPixel = iBitsPerPixel;
        this.miColorSpec    = piColorSpec;
        this.miAccessMode   = piImAccessMode;

        if((pcRW == 'W') || (pcRW == 'w')) {
            int iNumRows = this.miImageHeight;
            if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
                iNumRows = 1;
            }

            allocate(iNumRows, this.miImageWidth);
            if(!isValid()) {
                Globals.statusPrint("MemImage Constructor 1: Could not allocate memory for write");
            }
        } // if if((pcRW == 'W') || (pcRW == 'w'))

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            //  Write or Read the BMP header
            if(pcRW == 'W' || pcRW == 'w') {
                iStatus = writeBMP(psFileName);
            }

            if(pcRW == 'R' || pcRW == 'r') {
                iStatus = readBMP(psFileName, piColorSpec);
            }

            if(iStatus != 0) {
                this.mbValid = false; // Indicate the file could not be opened
            }
        } // if(this.miAccessMode == JICTConstants.I_SEQUENTIAL)

        if(this.miAccessMode == JICTConstants.I_RANDOM) {
            if(
            (pcRW == 'W' || pcRW == 'w') && 
            (piColorSpec == JICTConstants.I_RGBCOLOR)) {
                Globals.statusPrint("MemImage Constructor 1: RANDOM 24 bit BMPs not supported for writing");
                this.mbValid = false;
                return;
            }
            if(
            (pcRW == 'W' || pcRW == 'w') && 
            (piColorSpec != JICTConstants.I_RGBCOLOR)) {
                iStatus = writeBMP(psFileName);
            }

            if(pcRW == 'R' || pcRW == 'r') {
                // The following method sets iHeight, iWidth and iBitsPerPixel
                iStatus = Globals.readBMPHeader(psFileName, iHeight, iWidth, iBitsPerPixel);
                if(iStatus != 0) {
                    this.mbValid = false; // Indicate that the file could not be opened
                    Globals.statusPrint("MemImage Constructor 1: Unable to open BMP header");
                    return;
                }

                readBMP(psFileName, this.miColorSpec);
                if(iStatus != 0) {
                    this.mbValid = false; // Indicate the file could not be opened
                }
            }
        } // if(this.miAccessMode == JICTConstants.I_RANDOM)
        
        if (bIctDebug) {
            Globals.statusPrint("MemImage Constructor 1");
        }
    } // MemImage ctor


    // This constructor originally came from MEMIMG32.CPP
    //
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
        this.mbValid = false;
        this.miAccessMode    = JICTConstants.I_RANDOM;
        this.miImageHeight   = piHeight;
        this.miImageWidth    = piWidth;
        this.miColorSpec     = JICTConstants.I_EIGHTBITMONOCHROME;
        this.msSavedFileName = "No Image File Name";
        this.miBitsPerPixel  = piBitsPerPixel;
        this.miColorSpec     = mapBitsPerPixelToColorSpec(this.miBitsPerPixel);
        allocate(piHeight, piWidth);

        if (bIctDebug) {
            Globals.statusPrint("MemImage Constructor 2");
        }
    } // MemImage ctor


    // This method was added to simulate a default value for a parameter.
    // In the original C++ code, the memImage constructor that takes 3 parameters
    // assigns a default value for the last parameter.
    //
    // Called from:
    //     Globals.tweenImage
    //     RenderObject.renderMeshz
    //     SceneList.preview
    //     SceneList.render
    public MemImage(int piHeight, int piWidth) {
        this(piHeight, piWidth, 8);
    } // MemImage ctor


    // This constructor originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     ScnFileParser.readList (twice)
    public MemImage(MemImage pMImage) {
        this.miImageHeight   = pMImage.miImageHeight;
        this.miImageWidth    = pMImage.miImageWidth;
        this.miBitsPerPixel  = pMImage.miBitsPerPixel;
        this.miAccessMode    = pMImage.miAccessMode;
        this.miColorSpec     = pMImage.miColorSpec;
        this.msSavedFileName = pMImage.msSavedFileName;

        if(pMImage.mbValid == true) {
            allocate(pMImage.miImageHeight, pMImage.miImageWidth);
        }

        if (bIctDebug) {
            Globals.statusPrint("MemImage Constructor 4");
        }
    } // MemImage ctor


    // This destructor originally came from MEMIMG32.CPP
    public void finalize() {
        if(bIctDebug) {
            Globals.statusPrint("MemImage Destructor");
        }
    } // finalize


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     MemImage(int height, int width, int aBitsPerPixel)
    //     MemImage(MemImage m) ctor
    //     readBMP
    protected void allocate(int piHeight, int piWidthInPixels) {
        int iTotalBytes;
        // byte[] buffer; // not used
        float fBytesPerPixel = (float)this.miBitsPerPixel/8.0f;
        float fWidthInBytes = (float)piWidthInPixels * fBytesPerPixel;  // 1,24,32 bpp
        int iWidthInBytes    = (int)fWidthInBytes;

        if (fWidthInBytes > (float)iWidthInBytes) {
            iWidthInBytes++;
        }

        this.miPaddedWidth = (iWidthInBytes/4)*4;

        if(this.miPaddedWidth != iWidthInBytes) {
            this.miPaddedWidth += 4;
        }

        this.miPads = this.miPaddedWidth - iWidthInBytes;
        iTotalBytes = this.miPaddedWidth * piHeight;

        this.mbValid = true;
        this.bytes = new byte[iTotalBytes];            
        clear();  // Clear the memory area
    } // allocate


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     allocate
    //     GPipe.reset
    //     MorphDlg.onOK
    public void clear() {
        int ix, iy;
        int iRows = this.miImageHeight;

        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            iRows = 1;
        }

        int iBytesIdx = 0;
        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miPaddedWidth; ix++) {	 // miPaddedWidth is the number of bytes per row
                bytes[iBytesIdx] = 0;
                iBytesIdx++;
            } // for ix
        } // for iy
    } // clear


    // This method originally came from MEMIMG32.CPP
    // 
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

        int ix, iy;
        int iBytesIdx = 0; // an index into array this.bytes
        int iRows = this.miImageHeight;
        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            iRows = 1;
        }

        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
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
            } // for ix

            iBytesIdx += miPads;
        } // for iy

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return 0;
    } // clearRGB


    // This method originally came from MEMIMG32.CPP
    // 
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

        int ix, iy;
        int iBytesIdx = 0;
        int iRows = this.miImageHeight;

        if (this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            iRows = 1;
        }

        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
                if(
                (bytes[iBytesIdx]   >= pbytBlueLow)  && (bytes[iBytesIdx]   <= pbytBlueHigh)  && 
                (bytes[iBytesIdx+1] >= pbytGreenLow) && (bytes[iBytesIdx+1] <= pbytGreenHigh) && 
                (bytes[iBytesIdx+2] >= pbytRedLow)   && (bytes[iBytesIdx+2] <= pbytRedHigh)) {
                    bytes[iBytesIdx]   = 0;		   // each color component must be erased
                    bytes[iBytesIdx+1] = 0;
                    bytes[iBytesIdx+2] = 0;
                }

                iBytesIdx += 3;
            } // for ix

            iBytesIdx += miPads;
        } // for iy

        // SetCursor(LoadCursor( null, IDC_ARROW ));
        // TODO: Replace line above with something like:
        // Cursor cursor = button.getCursor();
        // button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return 0;
    } // clearRGBRange


    // This method originally came from MEMIMG32.CPP
    // 
    // Method init32 will initialize a MemImage with a given floating-point value.
    // See p 104 of the book Visual Special Effects Toolkit in C++, 
    // by Tim Wittenburg.
    // 
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


    // This method originally came from MEMIMG32.CPP
    // 
    // Method scaleTo8 scales a 32-bit image to 8-bit pixel resolution in such a way that 
    // the 32-bit values can be viewed on the screen. See p 104 of the book 
    // Visual Special Effects Toolkit in C++, by Tim Wittenburg.
    // 
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
        boolean B_IGNOREMAXVALUE = true;

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
        if(B_IGNOREMAXVALUE) {
            fActMax = 0.0f;
        }

        // Determine the image min and max values
        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
                if(*fTemp < fActMin) {
                    fActMin = *fTemp;
                }
                if(B_IGNOREMAXVALUE) {
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
        byte bytScaledValue;
        float fDesValue, fActValue;

        // Scale the image
        for (iy = 1; iy <= iRows; iy++) {
            for (ix = 1; ix <= this.miImageWidth; ix++) {
                fActValue = *fTemp;
                if(B_IGNOREMAXVALUE) {
                    if(fActValue != JICTConstants.F_ZBUFFERMAXVALUE) {
                        fDesValue = ((fActValue - fActMin) * fsFactor) + fDesMin;
                        bytScaledValue = (byte)(fDesValue + 0.5f);
                        pScaledMImage.setMPixel(ix, iy, bytScaledValue);
                    }
                } else {
                    fDesValue = ((fActValue - fActMin) * fsFactor) + fDesMin;
                    bytScaledValue = (byte)(fDesValue + 0.5f);
                    pScaledMImage.setMPixel(ix, iy, bytScaledValue);
                }
                
                fTemp++;
            } // for ix
        } // for iy

        return 0;
    } // scaleTo8


    // This method originally came from MEMIMG32.CPP
    // 
    // Method display draws a MemImage on the indicated BufferedImage
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


    // This method originally came from MEMIMG32.CPP
    // 
    // TODO: Replace paramater dc with one of type Graphics2D, 
    // as this method performs graphics
    // 
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


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     Globals.tweenImage
    //     SceneList.render
    public int copy(MemImage pOutMImage, int piXoffset, int piYoffset) {
        int iX, iY;

        if(pOutMImage.miBitsPerPixel != this.miBitsPerPixel) {
            Globals.statusPrint("MemImage.copy: Destination image does not have matching pixel depth");
            return -1;
        }

        if((this.miBitsPerPixel != 8) && (this.miBitsPerPixel != 24)) {
            Globals.statusPrint("MemImage.copy: Only 8 or 24 bit images are supported");
            return -2;
        }

        byte bytPixel;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
        for (iX = 1; iX <= this.miImageWidth; iX++) {
            for (iY = 1; iY < this.miImageHeight; iY++) {
                switch (this.miBitsPerPixel) {
                case 8:
                    bytPixel = getMPixel(iX, iY);
                    if(bytPixel != 0) {
                        pOutMImage.setMPixel(iX + piXoffset, iY + piYoffset, bytPixel);
                    }
                    break;

                case 24:
                    getMPixelRGB(iX, iY, bytRed, bytGreen, bytBlue);
                    pOutMImage.setMPixelRGB(iX + piXoffset, iY + piYoffset, bytRed, bytGreen, bytBlue);
                    break;
                }
            }
        }

        return 0;
    } // copy


    // This method originally came from MEMIMG32.CPP
    // 
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

        int ia = pOutMImage.miImageWidth  - piXOffset;
        int ib = pOutMImage.miImageHeight - piYOffset;
        int ic = piXOffset + this.miImageWidth - pOutMImage.miImageWidth;

        // Now we will copy a portion of array this.bytes to array outImage.bytes
        int iOutLoc = ((piYOffset * pOutMImage.miImageWidth) + piXOffset); // not used

        // copy b rows of image data
        int i, j;
        // byte *currentPix = this.bytes;
        int iCurrentPix = 0;

        for (j = 1; j <= ib; j++) {
            for (i = 1; i <= ia; i++) {
                if(bytes[iCurrentPix] != 0) {
                    // *outLoc = *currentPix;
                    pOutMImage.bytes[iOutLoc] = bytes[iCurrentPix];
                }

                iOutLoc++; 
                iCurrentPix++;  
            } // for i

            // if image is over the edge, skip those pixels then add the pad
            iCurrentPix = iCurrentPix + ic + miPads;
            iOutLoc += (pOutMImage.miPads + piXOffset); 
        } // for j

        return 0;
    } // fastCopy


    // This method originally came from MEMIMG32.CPP
    // 
    // Not called from within this file
    // Could not find where this is called from.
    public int copy8To24(MemImage pOutMImage) {
        int iX, iY;

        if(pOutMImage.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.copy8To24: Destination image must have 24 bit pixels");
            return -1;
        }

        if(this.miBitsPerPixel != 8) {
            Globals.statusPrint("MemImage.copy8To24: Source image must have 8 bit pixels");
            return -2;
        }

        byte bytIntensity;
        for (iX = 1; iX <= this.miImageWidth; iX++) {
            for (iY = 1; iY < this.miImageHeight; iY++) {
                bytIntensity = getMPixel(iX, iY);
                pOutMImage.setMPixelRGB(iX, iY, bytIntensity, bytIntensity, bytIntensity);
            } // for iY
        } // for iX

        return 0;
    } // copy8To24


    // This method originally came from MEMIMG32.CPP
    public byte getMPixel(int piX, int piY, char pcColor) {
        // Inputs piX and piY are assumed to be 1 relative
        // Returns the desired pixel from a color image
        int iAddr;
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

        iAddr = ((piY - 1) * (int)miPaddedWidth) + ((piX - 1)*3);  // 3 bytes/color pixel
        /*
        myTemp = myTemp + addr;
        thePixel = myTemp;
        if(aColor == 'B') return *thePixel;
        if(aColor == 'G') return *(thePixel + 1);
        if(aColor == 'R') return *(thePixel + 2);
        */
        if(pcColor == 'B') return bytes[iAddr];
        if(pcColor == 'G') return bytes[iAddr + 1];
        if(pcColor == 'R') return bytes[iAddr + 2];

        Globals.statusPrint("MemImage.getMPixel: Unknown color value");
        return 0;
    } // getMPixel


    // This method originally came from MEMIMG32.CPP
    // 
    // Method getMPixelRGB sets parameters pBytRed, pBytGreen and and pBytBlue
    // 
    // Called from:
    //     adjustColor
    //     copy (if this.bitsPerPixel = 24)
    //     Globals.iwarpz
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     Globals.tweenMesh
    public int getMPixelRGB(int piX, int piY, Byte pBytRed, Byte pBytGreen, Byte pBytBlue) {
        //  Inputs piX and piY are assumed to be 1 relative
        //  Returns the desired pixel from a color image
        if(this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.getMPixelRGB: Image must be 24 bits per pixel");
            return -1;
        }

        int iAddr;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        iAddr = ((piY - 1) * this.miPaddedWidth) + ((piX - 1)*(this.miBitsPerPixel/8));  // 3 bytes/color pixel

        // Set the output parameters
        pBytBlue  = bytes[iAddr];
        pBytGreen = bytes[iAddr + 1];
        pBytRed   = bytes[iAddr + 2];

        return 0;
    } // getMPixelRGB


    // This method originally came from MEMIMG32.CPP
    // 
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

        int iAddr;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        iAddr = ((piY - 1) * miPaddedWidth) + ((piX - 1)*(this.miBitsPerPixel/8));  // 3 bytes/color pixel

        bytes[iAddr]     = pbytBlue;
        bytes[iAddr + 1] = pbytGreen;
        bytes[iAddr + 2] = pbytRed;

        return 0;
    } // setMPixelRGB


    // This method originally came from MEMIMG32.CPP
    // 
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
        int iAddr;

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

        iAddr = ((piY - 1) * miPaddedWidth) + piX - 1;
        bytes[iAddr] = pbytValue;

        return pbytValue;
    } // setMPixel


    // This method originally came from MEMIMG32.CPP
    public int setMPixelA(float pfX, float pfY, byte pbytValue) {
        // Inputs pfX and pfY are assumed to be 1 relative
        int iAddr;
        // byte *myTemp = this.bytes;
        int myTemp;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            pfY = 1.0f;
        }

        if (
        (pfY < 1) || (pfY > (float)this.miImageHeight) || 
        (pfX < 1) || (pfX > (float)this.miImageWidth)) {
            return -1;
        }

        // Calculate the weights
        float fXa, fXb, fYa, fYb;
        float fValaa, fValba, fValab, fValbb;
        byte bytChromaColor = 0;

        fXa = pfX - (int)pfX;
        fXb = 1.0f - fXa;
        fYa = pfY - (int)pfY;
        fYb = 1.0f - fYa;
        fValaa = (byte)((fXa * fYa * (float)pbytValue) + 0.5f);
        fValba = (byte)((fXb * fYa * (float)pbytValue) + 0.5f);
        fValab = (byte)((fXa * fYb * (float)pbytValue) + 0.5f);
        fValbb = (byte)((fXb * fYb * (float)pbytValue) + 0.5f);

        iAddr = (((int)pfY - 1) * miPaddedWidth) + (int)pfX - 1;

        // Set the pixel value if this is the first contribution, else add this
        // pixel to what is already present.
        /*
        if( *(myTemp + addr) == chromaColor ) {
            *(myTemp + addr) = valaa;
        } else {
            *(myTemp + addr) = *(myTemp + addr) + valaa;
        }
        */
        if(bytes[iAddr] == bytChromaColor) {
            bytes[iAddr] = fValaa;
        } else {
            bytes[iAddr] = bytes[iAddr] + fValaa;
        }

        /*
        if( *(myTemp + addr + 1) == chromaColor ) {
            *(myTemp + addr) = valba;
        } else {
            *(myTemp + addr + 1) = *(myTemp + addr + 1) + valba;
        }
        */
        if(bytes[iAddr + 1] == bytChromaColor) {
            bytes[iAddr] = fValba;
        } else {
            bytes[iAddr + 1] = bytes[iAddr + 1] + fValba;
        }

        /*
        if( *(myTemp + addr + paddedWidth) == chromaColor ) {
            *(myTemp + addr + paddedWidth) = valab;
        } else {
            *(myTemp + addr + paddedWidth) = *(myTemp + addr + paddedWidth) + valab;
        }
        */
        if(bytes[iAddr + miPaddedWidth] == bytChromaColor) {
            bytes[iAddr + miPaddedWidth] = fValab;
        } else {
            bytes[iAddr + miPaddedWidth] = bytes[iAddr + miPaddedWidth] + fValab;
        }

        /*
        if( *(myTemp + addr + paddedWidth + 1) == chromaColor ) {
            *(myTemp + addr + paddedWidth + 1) = valbb;
        } else {
            *(myTemp + addr + paddedWidth + 1) = *(myTemp + addr + paddedWidth + 1) + valbb;
        }
        */

        if(bytes[iAddr + miPaddedWidth + 1] == bytChromaColor ) {
            bytes[iAddr + miPaddedWidth + 1] = fValbb;
        } else {
            bytes[iAddr + miPaddedWidth + 1] = bytes[iAddr + miPaddedWidth + 1] + fValbb;
        }

        return pbytValue;
    } // setMPixelA


    // This method originally came from MEMIMG32.CPP
    public byte getMPixelA(float pfX, float pfY) {
        // Inputs pfX and pfY must be 1 relative
        int iAddr;
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
        float fXa, fXb, fYa, fYb;
        float fWaa, fWba, fWab, fWbb;
        float fValaa, fValab, fValba, fValbb;
        float fBucket = 0.0f;
        byte bytChromaColor = (byte)0;

        fXa = pfX - (int)pfX;
        fXb = 1.0f - fXa;

        fYa = pfY - (int)pfY;
        fYb = 1.0f - fYa;

        fWaa = fXa * fYa;
        fWba = fXb * fYa;

        fWab = fXa * fYb;
        fWbb = fXb * fYb;

        iAddr = (((int)pfY - 1) * (int)miPaddedWidth) + (int)pfX - 1;
        fValaa = myTemp[iAddr]; // TODO: Not done yet, need to assign 4 bytes
        fValab = myTemp[iAddr + (int)miPaddedWidth]; // TODO: Not done yet, need to assign 4 bytes

        fValba = myTemp[iAddr + 1]; // TODO: Not done yet, need to assign 4 bytes
        fValbb = myTemp[iAddr + (int)miPaddedWidth + 1]; // TODO: Not done yet, need to assign 4 bytes

        if(fValaa != bytChromaColor) {
            fBucket += (fValaa * fWaa);
        }
        if(fValab != bytChromaColor) {
            fBucket += (fValab * fWab);
        }
        if(fValba != bytChromaColor) {
            fBucket += (fValba * fWba);
        }
        if(fValbb != bytChromaColor) {
            fBucket += (fValbb * fWbb);
        }
        
        return (byte)(fBucket + 0.5f);
    } // getMPixelA


    // This method originally came from MEMIMG32.CPP
    // 
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
        int iAddr;
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

        iAddr = ((piY - 1) * miPaddedWidth) + piX - 1;
        iBytesIdx = iBytesIdx + iAddr;

        return this.bytes[iBytesIdx];
    } // getMPixel


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     Globals.createQMeshModel
    //     Globals.iwarpz
    //     Globals.tweenMesh
    //     Texture.createTexture
    public int setMPixel32(int piX, int piY, float pfValue) {
        // Inputs piX and piY are assumed to be 1 relative
        int iAddr;
        byte[] myTemp = this.bytes;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1;
        }

        iAddr = ((piY - 1) * (int)miPaddedWidth) + ((piX - 1) * (this.miBitsPerPixel/8));
        // myTemp = myTemp + addr;
        // pPixel = (float *)myTemp;
        // *(pPixel) = pfValue;
        myTemp[iAddr] = pfValue; // TODO: Not done yet, need to assign 4 bytes

        return 0;
    } // setMPixel32


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     Globals.iwarpz
    //     Globals.tweenMesh
    //     RenderObject.renderMeshz
    public float getMPixel32(int piX, int piY) {
        // Inputs x and y are assumed to be 1 relative
        int iAddr;
        byte[] myTemp = this.bytes;
        float fPixel;

        if(this.miAccessMode == JICTConstants.I_SEQUENTIAL) {
            piY = 1;
        }

        if (
        (piY < 1) || (piY > this.miImageHeight) || 
        (piX < 1) || (piX > this.miImageWidth)) {
            return -1.0f;
        }

        iAddr = ((piY - 1) * miPaddedWidth) + ((piX - 1) * (this.miBitsPerPixel/8));
        // myTemp = myTemp + addr;
        fPixel = (float)myTemp[iAddr]; // TODO:Not done yet, need to assign 4 bytes to pPixel

        return fPixel;
    } // getMPixel32


    // This method originally came from MEMIMG32.CPP
    //
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


    // This method originally came from MEMIMG32.CPP
    //
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


    // This method originally came from MEMIMG32.CPP
    public int getAccessMode() {
        return this.miAccessMode;
    } // getAccessMode


    // This method originally came from MEMIMG32.CPP
    public int getColorSpec() {
        return this.miColorSpec;
    } // getColorSpec


    // This method originally came from MEMIMG32.CPP
    // 
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


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     SceneList.preview
    public byte[] getBytes() {
        return this.bytes;
    } // getBytes


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     saveAs8
    //     Globals.motionBlur
    //     RenderObject.prepareCutout
    //     RenderObject.renderMeshz
    //     SceneList.render
    public boolean isValid() {
        //  mbValid = true indicates the constructor did not encounter errors.
        return this.mbValid;
    } // isValid


    // This method originally came from MEMIMG32.CPP
    // 
    // Method writeBMP writes a MemImage object into the file whose name is passed
    // as a parameter. See p 103 of Visual Special Effects Toolkit in C++.
    // 
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     saveAs8
    //     Globals.createQMeshModel
    //     Globals.motionBlur
    //     Globals.tweenImage
    //     GPipe.saveOutputImage
    //     ImageDoc.onSaveDocument
    //     ImageView.onRButtonDown
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


    public int writeBMP(StringBuffer psbFileName) {
        int iStatus = writeBMP(psbFileName.toString());
        return iStatus;
    }


    // This method originally came from MEMIMG32.CPP
    // 
    // Method readBMP reads a Windows .bmp image file into a MemImage object.
    // See p 103 of the book Visual Special Effects Toolkit in C++.
    // 
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


    // This method originally came from MEMIMG32.CPP
    // 
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


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     Globals.makeRGBimage
    //     RenderObject.maskFromShape
    //     RenderObject.prepareCutout
    public void close() {
        
    } // close


    // This method originally came from MEMIMG32.CPP
    // 
    // Changed return value from int to boolean
    // 
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


    // This method originally came from MEMIMG32.CPP
    // 
    // Not a method of MemImage in the original C++ code,
    // However it is only called from within MemImage.
    // 
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     readBMP
    //     writeBMP
    private int mapColorSpecToBitsPerPixel(int piColorSpec) {
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


    // This method originally came from MEMIMG32.CPP.
    // Not a method of MemImage in the original C++ code, but only called 
    // from within MemImage.
    //
    // Called from:
    //     The MemImage constructor that takes 6 parameters
    //     The MemImage constructor that takes 3 parameters
    private int mapBitsPerPixelToColorSpec(int piBitsPerPixel) {
        int iColorSpec = JICTConstants.I_EIGHTBITMONOCHROME;

        if(piBitsPerPixel ==  1) iColorSpec = JICTConstants.I_ONEBITMONOCHROME;
        if(piBitsPerPixel ==  8) iColorSpec = JICTConstants.I_EIGHTBITMONOCHROME;
        if(piBitsPerPixel == 24) iColorSpec = JICTConstants.I_RGBCOLOR;
        if(piBitsPerPixel == 32) iColorSpec = JICTConstants.I_A32BIT;

        return iColorSpec;
    } // mapBitsPerPixelToColorSpec


    // This method originally came from MEMIMG32.CPP
    public int getImageSizeInBytes() {
        return this.miImageHeight * this.miPaddedWidth;
    } // getImageSizeInBytes


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     Globals.iwarpz
    //     GPipe.saveZBuffer
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
        MemImage testMImage = new MemImage(iOutputRows, iOutputCols, 8);
        if (!testMImage.isValid()) {
            sMsgText = "MemImage.saveAs8: Unable to create intermediate image. " + psOutImagePathName;
            Globals.statusPrint(sMsgText);
            return -1;
        }

        scaleTo8(testMImage);
        sMsgText = "MemImage.saveAs8: Saving " + psOutImagePathName;
        Globals.statusPrint(sMsgText);

        testMImage.writeBMP(psOutImagePathName);
        sMsgText = "MemImage.saveAs8: Histogram of " + psOutImagePathName;
        Globals.statusPrint(sMsgText);
        testMImage.histogram();

        return 0;
    } // saveAs8


    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     saveAs8
    private int histogram() {
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

        // Display the histogram to the JICT log
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
    

    // This method originally came from MEMIMG32.CPP
    // 
    // For more information on this method, see pages 110 - 118 of the book
    // Visual Special Effects Toolkits in C++. 
    // Method adjustColor handles the color adjustment specified in a .scn file 
    // indicated by the line with format
    // adjustColor [Target|Relative] R G B
    //
    // Method adjustColor sets parameters pMidRed, pMidGreen and pMidBlue
    // 
    // Called from:
    //     ScnFileParser.readListReal
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
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
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
                        // The following sets parameters bytRed, bytGreen, and bytBlue to the colors
                        // stored at (iRow, iCol), as mapped to field bytes
                        getMPixelRGB(iCol, iRow, bytRed, bytGreen, bytBlue);
                        if (bytRed != 0 || bytGreen != 0 || bytBlue != 0) {
                            fRedBucket   += (bytRed);
                            fGreenBucket += (bytGreen);
                            fBlueBucket  += (bytBlue);
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
        } // if(psAdjustmentType.equalsIgnoreCase("Target"))

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
        } // if(psAdjustmentType.equalsIgnoreCase("Relative"))

        // This code executes for both adjustment types "Target" and "Relative"
        for(iRow = 1; iRow <= iNumRows; iRow++) {
            for(iCol = 1; iCol <= iNumCols; iCol++) {
                switch(iBpp) {
                case 24:
                    getMPixelRGB(iCol, iRow, bytRed, bytGreen, bytBlue);
                    if ((bytRed != 0) || (bytGreen != 0) || (bytBlue != 0)) {
                        iNewRed   = (int)(bytRed   + fAvgRed);
                        iNewGreen = (int)(bytGreen + fAvgGreen);
                        iNewBlue  = (int)(bytBlue  + fAvgBlue);

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
            } // for iCol
        } // for iRow

        // TODO: The following code appears to be a new feature that 
        // was planned but not finished.
        if(psAdjustmentType.equalsIgnoreCase("Delta")) {
            for(iRow = 1; iRow <= iNumRows; iRow++) {
                for(iCol = 1; iCol <= iNumCols; iCol++) {
                    switch(iBpp) {
                    case 24:
                        getMPixelRGB(iCol, iRow, bytRed, bytGreen, bytBlue);
                        if ((bytRed != 0) || (bytGreen != 0) || (bytBlue != 0)) {
                            iNewRed   = bytRed   + piDesiredRed;
                            iNewGreen = bytGreen + piDesiredGreen;
                            iNewBlue  = bytBlue  + piDesiredBlue;
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
                } // for iCol
            } // for iRow
        }

        return 0;
    } // adjustColor
    

    // This method originally came from MEMIMG32.CPP
    // 
    // Could not find where this method is being called from.
    // I suspect this is used only for debugging.
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
    

    // This method originally came from MEMIMG32.CPP
    // 
    // Called from:
    //     RenderObject.renderMeshz
    //     SceneList.render
    public void setFileName(String psFileName) {
        this.msSavedFileName = psFileName;
    } // setFileName


    // This method originally came from MEMIMG32.CPP
    // 
    // Could not find where this method is being called from.
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
            } // for iCol
        } // for iRow

        return 0;
    } // clearRectangle


    // This method originally came from MEMIMG32.CPP
    //
    // Method getBoundingBox will set parameters xBeg, xEnd, yBeg and yEnd
    // 
    // Could not find where this method is being called from.
    public int getBoundingBox(Integer pIXBeg, Integer pIXEnd, Integer pIYBeg, Integer pIYEnd) {
        int iX, iY;
        if((this.miBitsPerPixel != 8) && (this.miBitsPerPixel != 24)) {
            Globals.statusPrint("MemImage.getBoundingBox: Only 8 or 24 bit images are supported");
            return -2;
        }

        byte bytPixel;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
        pIXBeg = Math.max(this.miImageWidth, this.miImageHeight);
        pIYBeg = Math.max(this.miImageWidth, this.miImageHeight);
        pIXEnd = 1;
        pIYEnd = 1;

        for (iX = 1; iX <= this.miImageWidth; iX++) {
            for (iY = 1; iY < this.miImageHeight; iY++) {
                switch (this.miBitsPerPixel) {
                case 8:
                    bytPixel = getMPixel(iX, iY);
                    if(bytPixel != JICTConstants.I_CHROMAVALUE) {
                        if(iX < pIXBeg) pIXBeg = iX;
                        if(iX > pIXEnd) pIXEnd = iX;
                        if(iY < pIYBeg) pIYBeg = iX;
                        if(iY > pIYEnd) pIYEnd = iX;
                    }
                    break;

                case 24:
                    getMPixelRGB(iX, iY, bytRed, bytGreen, bytBlue);
                    if(bytRed != JICTConstants.I_CHROMARED) {
                        if(iX < pIXBeg) pIXBeg = iX;
                        if(iX > pIXEnd) pIXEnd = iX;
                        if(iY < pIYBeg) pIYBeg = iX;
                        if(iY > pIYEnd) pIYEnd = iX;
                    }
                    if(bytGreen != JICTConstants.I_CHROMAGREEN) {
                        if(iX < pIXBeg) pIXBeg = iX;
                        if(iX > pIXEnd) pIXEnd = iX;
                        if(iY < pIYBeg) pIYBeg = iX;
                        if(iY > pIYEnd) pIYEnd = iX;
                    }
                    if(bytBlue != JICTConstants.I_CHROMABLUE) {
                        if(iX < pIXBeg) pIXBeg = iX;
                        if(iX > pIXEnd) pIXEnd = iX;
                        if(iY < pIYBeg) pIYBeg = iX;
                        if(iY > pIYEnd) pIYEnd = iX;
                    }
                    break;
                } // switch
            } // for iY

            Globals.statusPrint(msSavedFileName);
            String msgText = "MemImage.getBoundingBox: xBeg: " + pIXBeg + "  xEnd: " + pIXEnd + " yBeg: " + pIYBeg + "  yEnd: " + pIYEnd; 
            Globals.statusPrint(msgText);
        } // for iX

        return 0;
    } // getBoundingBox


    // This method originally came from BLEND.CPP
    // 
    // Called from:
    //     Globals.tweenImage
    //     RenderObject.renderMeshz
    public int createAlphaImage(MemImage pOutMImage) {
        int ix, iy;
        byte bytPixel;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;

        if(pOutMImage.miBitsPerPixel != 8) {
            Globals.statusPrint("MemImage.createAlphaImage: Output image must be 8 bits per pixel");
            return -1;
        }

        for (iy = 1; iy <= miImageHeight; iy++) {
            for (ix = 1; ix < miImageWidth; ix++) {
                switch(miBitsPerPixel) {
                case 8:
                    bytPixel = getMPixel(ix, iy);
                    break;

                case 24:
                    // The following method sets parameters bytRed, bytGreen and bytBlue
                    getMPixelRGB(ix, iy, bytRed, bytGreen, bytBlue);
                    bytPixel = JICTConstants.I_CHROMAVALUE;
                    if(
                    bytRed   != JICTConstants.I_CHROMAVALUE || 
                    bytGreen != JICTConstants.I_CHROMAVALUE ||
                    bytBlue  != JICTConstants.I_CHROMAVALUE) { 
                        bytPixel = 255;
                    }
                    break;
                } // switch

                if(bytPixel != JICTConstants.I_CHROMAVALUE) {
                    pOutMImage.setMPixel(ix, iy, (byte)255);
                }
            } // for ix
        } // for iy

        return 0;
    } // createAlphaImage
    

    // This method originally came from BLEND.CPP
    // 
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

        int iX, iY;
        byte bytPackedByte;
        int iPackedBytesIdx = 0;

        // paddedwidth is a multiple of 4 bytes to satisfy the requirements of a .BMP
        // the 1 bit image was created using GetBitmapBits which assumes that an image
        // width is a multiple of 2 bytes.  We need to calculate this width
        // before proceeding
        int iWidth     = miImageWidth / 8;
        int iRemainder = miImageWidth % 8;
        if (iRemainder > 0) {
            iWidth++;
        }
        if((iWidth/2*2) != iWidth) {
            iWidth++;
        }
      
        for (iY = 1; iY <= miImageHeight; iY++) {
            int xCounter = 0;
            for (iX = 1; iX <= iWidth; iX++) {
                bytPackedByte = bytes[iPackedBytesIdx];
                for(int bitCounter = 0; bitCounter < 8; bitCounter++) {
                    xCounter++;
                    if(xCounter <= miImageWidth) {
                        if(((bytPackedByte >> (7-bitCounter)) & 0x1) != 0) {
                            pOutputMImage.setMPixel(xCounter, miImageHeight - (iY - 1), 255);
                        } else {
                            pOutputMImage.setMPixel(xCounter, miImageHeight - (iY - 1), 
                                (byte)JICTConstants.I_CHROMAVALUE);
                        }
                    }
                } // for bitCounter

                iPackedBytesIdx++;
            } // for iX
        } // for iY

        return 0;
    } // unPack
    

    // This method originally came from BLEND.CPP
    // 
    // Called from:
    //     MorphDlg.onOK
    public int adjustImageBorder(String psOutPath) {
        // Find the size of the chromakey border around an image and
        // create a new image in which the border has been removed.
        //
        // This function effectively centers rotoscoped images that came
        // from sources other than function createAlphaImage.
        int iImHeight = getHeight();
        int iImWidth  = getWidth();
        if(this.miBitsPerPixel != 24) {
            Globals.statusPrint("MemImage.adjustImageBorder: Image must have 24 bit pixels.");
            return -1;
        }

        int iRow, iCol;
        int iMinX = iImWidth;
        int iMaxX = 1;
        int iMinY = iImHeight;
        int iMaxY = 1;
        Byte bytRed = (byte)0, bytGreen = (byte)0, bytBlue = (byte)0;
      
        for (iRow = 1; iRow <= iImHeight; iRow++) {
            for (iCol = 1; iCol <= iImWidth; iCol++) {
                // The following method sets parameters bytRed, bytGreen and bytBlue
                getMPixelRGB(iCol, iRow, bytRed, bytGreen, bytBlue);
                if(
                bytRed != JICTConstants.I_CHROMARED || 
                bytGreen != JICTConstants.I_CHROMAGREEN ||
                bytBlue != JICTConstants.I_CHROMABLUE) {
                    if(iRow < iMinY) iMinY = iRow;
                    if(iCol < iMinX) iMinX = iCol;
                    if(iRow > iMaxY) iMaxY = iRow;
                    if(iCol > iMaxX) iMaxX = iCol;
                }
            } // for iCol
        } // for iRow

        int iNewImHeight = iMaxY - iMinY + 1;
        int iNewImWidth  = iMaxX - iMinX + 1;
        MemImage outMImage = new MemImage(iNewImHeight, iNewImWidth, 24);
        if(!outMImage.isValid()) {
            Globals.statusPrint("MemImage.adjustImageBorder: Unable to create output image.");
            return -2;
        }

        // Now copy the image into its new home
        int iOutRow = 1;
        int iOutCol;
        for (iRow = iMinY; iRow <= iMaxY; iRow++) {
            iOutCol = 1;
            for (iCol = iMinX; iCol <= iMaxX; iCol++) {
                getMPixelRGB(iCol, iRow, bytRed, bytGreen, bytBlue);
                outMImage.setMPixelRGB(iOutCol, iOutRow, bytRed, bytGreen, bytBlue);
                iOutCol++;
            } // for iCol

            iOutRow++;
        } // for iRow

        outMImage.writeBMP(psOutPath);
        return 0;
    } // adjustImageBorder


    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     Globals.tweenImage
    public int alphaSmooth3() {
        // Each image must be the same size.
        MemImage inMImage  = this;
        MemImage outMImage = this;

        // How can the following if statement fail if inMImage = outMImage = this?
        if(inMImage.getHeight() != outMImage.getHeight() || 
        inMImage.getWidth() != outMImage.getWidth()) {
            Globals.statusPrint("MemImage.alphaSmooth3: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inMImage.getBitsPerPixel() != outMImage.getBitsPerPixel()) ||  
        (inMImage.getBitsPerPixel() != 8)) {
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

        int iImHeight = inMImage.getHeight();
        int iImWidth  = inMImage.getWidth();
        //float x1 = 0.0f; // not used
        //float y1 = 0.0f; // not used
        //float z1 = 0.0f; // not used
        //float totalCells = 0.0f; // not used
        int iRow, iCol;
        float fSum;
        float q00, q10, q20;
        float q01, q11, q21;
        float q02, q12, q22;
        float fFactor = 1.0f/9.0f;

        for (iRow = 2; iRow <= iImHeight - 1; iRow++) {
            for (iCol = 2; iCol <= iImWidth - 1; iCol++) {
                q11 = inMImage.getMPixel(iCol, iRow);
                if(q11 != 0) {
                    // Calculate average color of 3x3 cell centered about iRow, iCol
                    q00 = inMImage.getMPixel(iCol - 1, iRow - 1);
                    q10 = inMImage.getMPixel(iCol    , iRow - 1);
                    q20 = inMImage.getMPixel(iCol + 1, iRow - 1);

                    q01 = inMImage.getMPixel(iCol - 1, iRow);
                    // q11 is calculated outside if stmt
                    q21 = inMImage.getMPixel(iCol + 1, iRow);

                    q02 = inMImage.getMPixel(iCol - 1, iRow + 1);
                    q12 = inMImage.getMPixel(iCol    , iRow + 1);
                    q22 = inMImage.getMPixel(iCol + 1, iRow + 1);

                    // Note that the sum below is the sum of 9 values,
                    // one for each cell in a 3x3 cell
                    fSum = (q00 + q10 + q20
                          + q01 + q11 + q21 
                          + q02 + q12 + q22) * fFactor; 
                
                    float fDiff = 255.0f - fSum;   // steepen the ramp
                    fSum -= (fDiff * 0.5f);
                    fSum = MathUtils.bound(fSum, 0.0f, 255.0f);
                    outMImage.setMPixel(iCol, iRow, (byte)(fSum + 0.5f));
                } // if
            } // for iCol
        } // for iRow

        return 0;
    } // alphaSmooth3
    

    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     Globals.createCutout
    //     MainFrame.onToolsCreateAlphaImage
    //     RenderObject.renderMeshz
    public int alphaSmooth5() {
        //
        // Each image must be the same size.
        //
        MemImage inMImage  = this;
        MemImage outMImage = this;

        // How can the following if statement fail if inMImage = outMImage = this?
        if(
        inMImage.getHeight() != outMImage.getHeight() || 
        inMImage.getWidth() != outMImage.getWidth()) {
            Globals.statusPrint("MemImage.alphaSmooth5: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inMImage.getBitsPerPixel() != outMImage.getBitsPerPixel()) || 
        (inMImage.getBitsPerPixel() != 8)) {
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

        int iImHeight = inMImage.getHeight();
        int iImWidth  = inMImage.getWidth();
        //float x1 = 0.0f; // not used
        //float y1 = 0.0f; // not used
        //float z1 = 0.0f; // not used
        //float totalCells = 0.0f; // not used
        int iRow, iCol;
        float fSum;
        float q00, q10, q20, q30, q40;
        float q01, q11, q21, q31, q41;
        float q02, q12, q22, q32, q42;
        float q03, q13, q23, q33, q43;
        float q04, q14, q24, q34, q44;
        float fFactor = 1.0f/25.0f;

        for (iRow = 3; iRow <= iImHeight - 2; iRow++) {
            for (iCol = 3; iCol <= iImWidth - 2; iCol++) {
                q22 = inMImage.getMPixel(iCol, iRow);
                if(q22 != 0) {
                    // Calculate average color of 5x5 cell centered about iRow, iCol
                    q00 = inMImage.getMPixel(iCol - 2, iRow - 2);
                    q10 = inMImage.getMPixel(iCol - 1, iRow - 2);
                    q20 = inMImage.getMPixel(iCol    , iRow - 2);
                    q30 = inMImage.getMPixel(iCol + 1, iRow - 2);
                    q40 = inMImage.getMPixel(iCol + 2, iRow - 2);

                    q01 = inMImage.getMPixel(iCol - 2, iRow - 1);
                    q11 = inMImage.getMPixel(iCol - 1, iRow - 1);
                    q21 = inMImage.getMPixel(iCol    , iRow - 1);
                    q31 = inMImage.getMPixel(iCol + 1, iRow - 1);
                    q41 = inMImage.getMPixel(iCol + 2, iRow - 1);

                    q02 = inMImage.getMPixel(iCol - 2, iRow);
                    q12 = inMImage.getMPixel(iCol - 1, iRow);
                    // q22 is calculated outside if stmt
                    q32 = inMImage.getMPixel(iCol + 1, iRow);
                    q42 = inMImage.getMPixel(iCol + 2, iRow);

                    q03 = inMImage.getMPixel(iCol - 2, iRow + 1);
                    q13 = inMImage.getMPixel(iCol - 1, iRow + 1);
                    q23 = inMImage.getMPixel(iCol    , iRow + 1);
                    q33 = inMImage.getMPixel(iCol + 1, iRow + 1);
                    q43 = inMImage.getMPixel(iCol + 2, iRow + 1);

                    q04 = inMImage.getMPixel(iCol - 2, iRow + 2);
                    q14 = inMImage.getMPixel(iCol - 1, iRow + 2);
                    q24 = inMImage.getMPixel(iCol    , iRow + 2);
                    q34 = inMImage.getMPixel(iCol + 1, iRow + 2);
                    q44 = inMImage.getMPixel(iCol + 2, iRow + 2);

                    // Note that the sum below is the sum of 25 values,
                    // one for each cell in a 5x5 cell
                    fSum = (q00 + q10 + q20 + q30 + q40
                          + q01 + q11 + q21 + q31 + q41 
                          + q02 + q12 + q22 + q32 + q42 
                          + q03 + q13 + q23 + q33 + q43 
                          + q04 + q14 + q24 + q34 + q44) * fFactor; 
                
                    float fDiff = 255.0f - fSum;   // steepen the ramp
                    fSum -= (fDiff * 0.5f);
                    fSum = MathUtils.bound(fSum, 0.0f, 255.0f);
                    outMImage.setMPixel(iCol, iRow, (byte)(fSum + 0.5f));
                } // if
            } // for iCol
        } // for iRow

        return 0;
    } // alphaSmooth5
    

    // This method originally came from IWARP.CPP
    // Could not find where this method is called from.
    public int alphaSmooth7() {
        // Each image must be the same size.
        MemImage inMImage  = this;
        MemImage outMImage = this;

        // How can the following if statement fail if inMImage = outMImage = this?
        if(
        inMImage.getHeight() != outMImage.getHeight() ||
        inMImage.getWidth()  != outMImage.getWidth() ) {
            Globals.statusPrint("MemImage.alphaSmooth7: Images must have equal size.");
            return -1;
        }

        // Each image must have 8 bit pixels.
        if(
        (inMImage.getBitsPerPixel() != outMImage.getBitsPerPixel()) || 
        (inMImage.getBitsPerPixel() != 8)) {
            Globals.statusPrint("MemImage.alphaSmooth7: Images must have 8 bit pixels.");
            return -2;
        }

        // int bpp = inImage.getBitsPerPixel(); not used
        int imHeight = inMImage.getHeight();
        int imWidth  = inMImage.getWidth();
        //float x1 = 0.0f; // not used
        //float y1 = 0.0f; // not used
        //float z1 = 0.0f; // not used
        //float totalCells = 0.0f; // not used
        int iRow, iCol;
        float fSum;
        float q00, q10, q20, q30, q40, q50, q60;
        float q01, q11, q21, q31, q41, q51, q61;
        float q02, q12, q22, q32, q42, q52, q62;
        float q03, q13, q23, q33, q43, q53, q63;
        float q04, q14, q24, q34, q44, q54, q64;
        float q05, q15, q25, q35, q45, q55, q65;
        float q06, q16, q26, q36, q46, q56, q66;
        float fFactor = 1.0f/49.0f;

        for (iRow = 4; iRow <= imHeight - 3; iRow++) {
            for (iCol = 4; iCol <= imWidth - 3; iCol++) {
                q33 = inMImage.getMPixel(iCol, iRow);
                if(q33 != 0) {
                    // Calculate average color of 7x7 cell centered about iRow, iCol
                    q00 = inMImage.getMPixel(iCol - 3, iRow - 3);
                    q10 = inMImage.getMPixel(iCol - 2, iRow - 3);
                    q20 = inMImage.getMPixel(iCol - 1, iRow - 3);
                    q30 = inMImage.getMPixel(iCol    , iRow - 3);
                    q40 = inMImage.getMPixel(iCol + 1, iRow - 3);
                    q50 = inMImage.getMPixel(iCol + 2, iRow - 3);
                    q60 = inMImage.getMPixel(iCol + 3, iRow - 3);

                    q01 = inMImage.getMPixel(iCol - 3, iRow - 2);
                    q11 = inMImage.getMPixel(iCol - 2, iRow - 2);
                    q21 = inMImage.getMPixel(iCol - 1, iRow - 2);
                    q31 = inMImage.getMPixel(iCol    , iRow - 2);
                    q41 = inMImage.getMPixel(iCol + 1, iRow - 2);
                    q51 = inMImage.getMPixel(iCol + 2, iRow - 2);
                    q61 = inMImage.getMPixel(iCol + 3, iRow - 2);

                    q02 = inMImage.getMPixel(iCol - 3, iRow - 1);
                    q12 = inMImage.getMPixel(iCol - 2, iRow - 1);
                    q22 = inMImage.getMPixel(iCol - 1, iRow - 1);
                    q32 = inMImage.getMPixel(iCol    , iRow - 1);
                    q42 = inMImage.getMPixel(iCol + 1, iRow - 1);
                    q52 = inMImage.getMPixel(iCol + 2, iRow - 1);
                    q62 = inMImage.getMPixel(iCol + 3, iRow - 1);

                    q03 = inMImage.getMPixel(iCol - 3, iRow );
                    q13 = inMImage.getMPixel(iCol - 2, iRow );
                    q23 = inMImage.getMPixel(iCol - 1, iRow );
                    // q33 is calculated outside if stmt
                    q43 = inMImage.getMPixel(iCol + 1, iRow );
                    q53 = inMImage.getMPixel(iCol + 2, iRow );
                    q63 = inMImage.getMPixel(iCol + 3, iRow );

                    q04 = inMImage.getMPixel(iCol - 3, iRow + 1);
                    q14 = inMImage.getMPixel(iCol - 2, iRow + 1);
                    q24 = inMImage.getMPixel(iCol - 1, iRow + 1);
                    q34 = inMImage.getMPixel(iCol    , iRow + 1);
                    q44 = inMImage.getMPixel(iCol + 1, iRow + 1);
                    q54 = inMImage.getMPixel(iCol + 2, iRow + 1);
                    q64 = inMImage.getMPixel(iCol + 3, iRow + 1);

                    q05 = inMImage.getMPixel(iCol - 3, iRow + 2);
                    q15 = inMImage.getMPixel(iCol - 2, iRow + 2);
                    q25 = inMImage.getMPixel(iCol - 1, iRow + 2);
                    q35 = inMImage.getMPixel(iCol    , iRow + 2);
                    q45 = inMImage.getMPixel(iCol + 1, iRow + 2);
                    q55 = inMImage.getMPixel(iCol + 2, iRow + 2);
                    q65 = inMImage.getMPixel(iCol + 3, iRow + 2);

                    q06 = inMImage.getMPixel(iCol - 3, iRow + 3);
                    q16 = inMImage.getMPixel(iCol - 2, iRow + 3);
                    q26 = inMImage.getMPixel(iCol - 1, iRow + 3);
                    q36 = inMImage.getMPixel(iCol    , iRow + 3);
                    q46 = inMImage.getMPixel(iCol + 1, iRow + 3);
                    q56 = inMImage.getMPixel(iCol + 2, iRow + 3);
                    q66 = inMImage.getMPixel(iCol + 3, iRow + 3);

                    // Note that the sum below is the sum of 49 values,
                    // one for each cell in a 7x7 cell
                    fSum = (q00 + q10 + q20 + q30 + q40 + q50 + q60
                          + q01 + q11 + q21 + q31 + q41 + q51 + q61 
                          + q02 + q12 + q22 + q32 + q42 + q52 + q62 
                          + q03 + q13 + q23 + q33 + q43 + q53 + q63 
                          + q04 + q14 + q24 + q34 + q44 + q54 + q64
                          + q05 + q15 + q25 + q35 + q45 + q55 + q65
                          + q06 + q16 + q26 + q36 + q46 + q56 + q66) * fFactor; 

                    float fDiff = 255.0f - fSum;   // steepen the ramp
                    fSum -= (fDiff * 0.5f);
                    fSum = MathUtils.bound(fSum, 0.0f, 255.0f);
                    outMImage.setMPixel(iCol, iRow, (byte)(fSum + 0.5f));
                } // if
            } // for iCol
        } // for iRow

        return 0;
    } // alphaSmooth7


    // This method originally came from SHADERS.CPP
    // 
    // Method fillPolyz takes 4 projected vertices and associated intensities
    // and then fills the four-sided polygon given by those vertices. It 
    // decomposes the four-sided polygon into 4 triangles and then calls 
    // the fillTriangleZ method once for each of the 4 triangles. See Visual 
    // Special Effects Toolkit in C++, p 173.
    //
    // Called from:
    //     Globals.fwarpz
    //     GPipe.addFace
    //     RenderObject.renderMesh
    //     RenderObject.renderMeshz
    //     RenderObject.renderShape
    //     RenderObject.renderShapez
    public int fillPolyz(
    int piI1x, int piI1y, float pfI1p, float pfI1d,
    int piI2x, int piI2y, float pfI2p, float pfI2d, 
    int piI3x, int piI3y, float pfI3p, float pfI3d, 
    int piI4x, int piI4y, float pfI4p, float pfI4d,
    MemImage pzBufMImage) {
        int iXMax = piI1x;
        int iYMax = piI1y;
        int iXMin = piI1x;
        int iYMin = piI1y;
        // float totalIntensity, avgIntensity; // not used
        MemImage outMImage = this;  

        // Get the bounding box
        if(piI2x > iXMax) iXMax = piI2x;
        if(piI3x > iXMax) iXMax = piI3x;
        if(piI4x > iXMax) iXMax = piI4x;

        if(piI2x < iXMin) iXMin = piI2x;
        if(piI3x < iXMin) iXMin = piI3x;
        if(piI4x < iXMin) iXMin = piI4x;

        if(piI2y > iYMax) iYMax = piI2y;
        if(piI3y > iYMax) iYMax = piI3y;
        if(piI4y > iYMax) iYMax = piI4y;

        if(piI2y < iYMin) iYMin = piI2y;
        if(piI3y < iYMin) iYMin = piI3y;
        if(piI4y < iYMin) iYMin = piI4y;

        // Handle quadrangles that consist of: a single point, horizontal or vertical line
        float fOldZ, fIntensity, fDistance; 
        // float outIntensity; // not used
        int iBpp = outMImage.getBitsPerPixel();

        // Single point
        if((iXMin == iXMax) && (iYMin == iYMax)) {
            fDistance  = (pfI1d + pfI2d + pfI3d + pfI4d)/4.0f;
            fIntensity = (pfI1p + pfI2p + pfI3p + pfI4p)/4.0f;

            if(pzBufMImage != null) {
                fOldZ = pzBufMImage.getMPixel32(iXMin, iYMin);
                if(fDistance <= fOldZ) {
                    // why is fIntensity calculated again?
                    fIntensity = (pfI1p + pfI2p + pfI3p + pfI4p)/4.0f;
                    pzBufMImage.setMPixel32(iXMin, iYMin, fDistance);

                    if(iBpp == 8)  {
                        outMImage.setMPixel(iXMin, iYMin, (byte)fIntensity);
                    }
                    if(iBpp == 24) {
                        outMImage.setMPixelRGB(iXMin, iYMin, 
                            (byte)fIntensity, (byte)fIntensity, (byte)fIntensity);
                    }
                } else {   //no zBuffer
                    if(iBpp == 8) {
                        outMImage.setMPixel(iXMin, iYMin, (byte)fIntensity);
                    }
                    if(iBpp == 24) {
                        outMImage.setMPixelRGB(iXMin, iYMin, 
                            (byte)fIntensity, (byte)fIntensity, (byte)fIntensity);
                    }
                }
            }

            return 0;
        } // if((iXMin == iXMax) && (iYMin == iYMax))

        // int minX, minY, maxX, maxY, j; // not used
        // float minI, maxI, minD, maxD; // not used
        // float nSteps, intensityStep, distanceStep; // not used
        // int row, col, denominator; // not used

        // Handle larger quadrangles
        int iXCent = (int)(((float)iXMin + ((float)iXMax - (float)iXMin) / 2.0f) + 0.5f);
        int iYCent = (int)(((float)iYMin + ((float)iYMax - (float)iYMin) / 2.0f) + 0.5f);

        // The intensity at the centroid is the weighted sum of the intensities at each vertex
        // The weights are the normalized distances between each vertex distance and the centroid
        // fill the triangle bounded by the centroid and each successive pair of vertices
        float fTotalDistance = 0.0f;
        float fD1 = MathUtils.getDistance2d((float)iXCent, (float)iYCent, (float)piI1x, (float)piI1y);
        float fD2 = MathUtils.getDistance2d((float)iXCent, (float)iYCent, (float)piI2x, (float)piI2y);
        float fD3 = MathUtils.getDistance2d((float)iXCent, (float)iYCent, (float)piI3x, (float)piI3y);
        float fD4 = MathUtils.getDistance2d((float)iXCent, (float)iYCent, (float)piI4x, (float)piI4y);
        fTotalDistance = fD1 + fD2 + fD3 + fD4;

        // Is this check necessary? fTotalDistance = 0 only if fD1 = fD2 = fD3 = fD4 = 0,
        // because MathUtils.getDistance2d returns values >= 0
        if(fTotalDistance == 0.0f) {
            // We can't continue, as we will be using fTotalDistance to divide fD1, fD2, fD3 and fD4
            Globals.statusPrint("MemImage.fillPolyZ: Sum of polygon diagonals must be > 0");
            return -1;
        }

        // Normalize the distances (so that fD1 + fD2 + fD3 + fD4 = 1.0)
        fD1 /= fTotalDistance;
        fD2 /= fTotalDistance;
        fD3 /= fTotalDistance;
        fD4 /= fTotalDistance;
        
        // Calculate the intensity at the centroid (iXCent, iYCent)
        // We'll use fD1, fD2, fD3, and fD4 as weights for calculating the intensity at the centroid
        float fiCent = (fD1 * pfI1p) + (fD2 * pfI2p) + (fD3 * pfI3p) + (fD4 * pfI4p);

        // We'll use fD1, fD2, fD3 and fD4 as weights for calculating distance from viewpoint to centroid
        float fdCent = (fD1 * pfI1d) + (fD2 * pfI2d) + (fD3 * pfI3d) + (fD4 * pfI4d);

        // Fill the polygon by subdividing it into 4 triangles and interpolatively 
        // shading each one.
        // Fill up the 2D triangle consisting of vertices (iXCent, iYCent), (piI1x, piI1y), and (piI2x, piI2y)
        Globals.fillTrianglez(
            iXCent, iYCent, fiCent, fdCent, 
            piI1x,  piI1y,  pfI1p,  pfI1d, 
            piI2x,  piI2y,  pfI2p,  pfI2d, 
            outMImage, pzBufMImage);

        // Fill up the 2D triangle consisting of vertices (iXCent, iYCent), (piI2x, piI2y), and (piI3x, piI3y)
        Globals.fillTrianglez(
            iXCent, iYCent, fiCent, fdCent, 
            piI2x,  piI2y,  pfI2p,  pfI2d, 
            piI3x,  piI3y,  pfI3p,  pfI3d, 
            outMImage, pzBufMImage);
        
        // Fill up the 2D triangle consisting of vertices (iXCent, iYCent), (piI3x, piI3y), and (piI4x, piI4y)
        Globals.fillTrianglez(
            iXCent, iYCent, fiCent, fdCent, 
            piI3x,  piI3y,  pfI3p,  pfI3d, 
            piI4x,  piI4y,  pfI4p,  pfI4d,
            outMImage, pzBufMImage);

        // Fill up the 2D triangle consisting of vertices (iXCent, iYCent), (piI4x, piI4y), and (piI1x, piI1y)
        Globals.fillTrianglez(
            iXCent, iYCent, fiCent, fdCent, 
            piI4x,  piI4y,  pfI4p,  pfI4d, 
            piI1x,  piI1y,  pfI1p,  pfI1d,
            outMImage, pzBufMImage);

        return 0;
    } // fillPolyz
} // class MemImage