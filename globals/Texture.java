package globals;

import core.MemImage;

public class Texture {
    // These values came from ICT20.H
    // Texture Types
    public static int CONSTANT     = 1;
    public static int CHECKERBOARD = 2;
    public static int HORZRAMP     = 3;
    public static int VERTRAMP     = 4;
    public static int PLASMA       = 5;
    public static int COUNTER      = 6;


    public static int createTexture(String psTexturePath, String psOutDirectory, 
    int piTextureType, int piImageType, 
    int piForeColor, int piBackColor, // piBackColor is not used
    int piNumRows, int piNumColumns) { 

        int row, col; 
        MemImage newImage;
        int bitsPerPixel = 0;

        if(piImageType == 1) bitsPerPixel = 8;   
        if(piImageType == 2) bitsPerPixel = 24;   
        if(piImageType == 3) bitsPerPixel = 32;

        if(bitsPerPixel > 0) {
            newImage = new MemImage(piNumRows, piNumColumns, bitsPerPixel);
        } else {
            Globals.statusPrint("createTexture: Unknown imageType. Cannot open texture image");
            return -1;
        }

        byte loValue    = 10;
        byte hiValue    = 250;
        byte checkValue = loValue;

        final int iCellWidth = 32;
        final int iCellHeight = 32;
        int iColCounter = 0;
        int iRowCounter = 0;
        int i, j;
        int iCounter;

        switch(piTextureType) {
        case CONSTANT:
            for (row = 1; row <= piNumRows; row++) {
                for (col = 1; col <= piNumColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)piForeColor);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)piForeColor);
                    }
                } // for col
            } // for row
            break;

        case CHECKERBOARD:
            for(j = 1; j <= piNumRows; j++) {
                iRowCounter++;
                if(iRowCounter == iCellHeight) {
                    iRowCounter = 0;
                    if(checkValue == loValue) {
                        checkValue = hiValue;
                    } else {
                        checkValue = loValue;
                    }
                }

                for(i = 1; i <= piNumColumns; i++) {
                    newImage.setMPixel(i, j, checkValue);
                    iColCounter++;
                    if(iColCounter == iCellWidth) {
                        iColCounter = 0;
                        if(checkValue == loValue) {
                            checkValue = hiValue;
                        } else {
                            checkValue = loValue;
                        }
                    }
                } // for i
            } // for j
            break;

        case HORZRAMP:
            for (row = 1; row <= piNumRows; row++) {
                for (col = 1; col <= piNumColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)col);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)col);
                    }
                } // for col
            } // for row
            break;

        case VERTRAMP:
            for (row = 1; row <= piNumRows; row++) {
                for (col = 1; col <= piNumColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)row);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)row);
                    }
                } // for col
            } // for row
            break;

        case COUNTER:
            iCounter = 0;
            for (row = 1; row <= piNumRows; row++) {
                for (col = 1; col <= piNumColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)iCounter);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)row);
                    }

                    iCounter++;
                    iCounter = iCounter % 256;
                } // for col
            } // for row
            break;

        case PLASMA:
            createPlasma(newImage, piNumRows, piNumColumns);
            break;

        default:
            Globals.statusPrint("createTexture: Unknown texture type");
            break;
        } // switch

        // Generate the output path name and save the image
        String sDrive, sDir, sFile, sExt;
        String sDdrive, sDdir, sDfile, sDext;
        String sOutPath;

        _splitpath(psTexturePath, sDrive, sDir, sFile, sExt);
        int theLength = sFile.length(); // the value of theLength is not used

        _splitpath(psOutDirectory, sDdrive, sDdir, sDfile, sDext);
        _makepath(sOutPath, sDdrive, sDdir, sFile, sExt);

        String msgText = "Saving Texture Image: " + sOutPath;
        Globals.statusPrint(msgText);

        // Write the bitmap out to file indicatd by sOutPath
        newImage.writeBMP(sOutPath);

        return 0;
    } // createTexture


    // Called from:
    //     createPlasma
    public static void plasma(MemImage anImage, int x, int y, int x1, int y1) {
        /* unsigned */int a, b, c, d, e, f, g, i, j;
        int w, h;

        w = (x1 - x)/2;
        h = (y1 - y)/2;
        if((w >= 1) || (h >= 1)) {
            a = x + w;
            b = y + h;
            c = anImage.getMPixel(x,   y);
            d = anImage.getMPixel(x1,  y);
            e = anImage.getMPixel(x,  y1);
            f = anImage.getMPixel(x1, y1);
            i = (h + 1)*2;
            j = (h + 1)*3/2;

            g = anImage.getMPixel(a,y);
            if(g == 0) {
                g = (c + d + myRand(i))/2;
                anImage.setMPixel(a, y, (byte)g);
            }

            g = anImage.getMPixel(x,b);
            if(g == 0) {
                g = (c + e + myRand(i))/2;
                anImage.setMPixel(x, b, (byte)g);
            }

            g = anImage.getMPixel(a, b);
            if(g == 0) {
                g = (c + d + e + f + myRand(j))/4;
                anImage.setMPixel(a, b, (byte)g);
            }

            g = anImage.getMPixel(x1, b);
            if(g == 0) {
                g = (d + f + myRand(i))/2;
                anImage.setMPixel(x1, b, (byte)g);
            }

            g = anImage.getMPixel(a, y1);
            if(g == 0) {
                g = (e + f + myRand(i))/2;
                anImage.setMPixel(a, y1, (byte)g);
            }

            // Recursive calls
            plasma(anImage, x, y, a,   b);
            plasma(anImage, a, y, x1,  b);
            plasma(anImage, x, b, a,  y1);
            plasma(anImage, a, b, x1, y1);
        }
    } // plasma


    // Called from:
    //     createTexure
    public static void createPlasma(MemImage anImage, int numRows, int numColumns) {
        srand( (unsigned)time( null ) );	   //seed the random number generator

        anImage.setMPixel(1,       1,     (byte)myRand(64));
        anImage.setMPixel(1,       numRows,  (byte)myRand(64));
        anImage.setMPixel(numColumns, 1,     (byte)myRand(64));
        anImage.setMPixel(numColumns, numRows, (byte)myRand(64));
        plasma(anImage, 1, 1, numColumns, numRows);
    } // createPlasma


    // Called from:
    //     createPlasma
    //     plasma
    public static int myRand(int maxVal) {
        return (int)((float)rand()/(float)RAND_MAX * maxVal);
    } // myRand
} // class Texture