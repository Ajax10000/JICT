package globals;

import core.MemImage;

import java.io.File;
import java.nio.file.Path;

import java.util.Random;

public class Texture {
    // These values came from ICT20.H
    // Texture Types
    // These constants are used in method createTexture
    public static final int I_CONSTANT     = 1;
    public static final int I_CHECKERBOARD = 2;
    public static final int I_HORZRAMP     = 3;
    public static final int I_VERTRAMP     = 4;
    public static final int I_PLASMA       = 5;
    public static final int I_COUNTER      = 6;

    public static Random random = new Random();


    // Called from:
    //     MakeTexture.onOK
    // This method came from TEXTURE.CPP
    public static int createTexture(String psTexturePath, String psOutDirectory, 
    int piTextureType, int piImageType, 
    int piForeColor, int piBackColor, // piBackColor is not used
    int piNumRows, int piNumColumns) { 

        int iRow, iCol; 
        MemImage newImage;
        int iBitsPerPixel = 0;

        if(piImageType == 1) iBitsPerPixel = 8;   
        if(piImageType == 2) iBitsPerPixel = 24;   
        if(piImageType == 3) iBitsPerPixel = 32;

        if(iBitsPerPixel > 0) {
            newImage = new MemImage(piNumRows, piNumColumns, iBitsPerPixel);
        } else {
            Globals.statusPrint("createTexture: Unknown imageType. Cannot open texture image");
            return -1;
        }

        byte loValue    = 10;
        byte hiValue    = 250; // TODO: Fix this. Range of Java byte is -128 to 127
        byte checkValue = loValue;

        final int iCellWidth = 32;
        final int iCellHeight = 32;
        int iColCounter = 0;
        int iRowCounter = 0;
        int i, j;
        int iCounter;

        switch(piTextureType) {
        case I_CONSTANT:
            for (iRow = 1; iRow <= piNumRows; iRow++) {
                for (iCol = 1; iCol <= piNumColumns; iCol++) {
                    if(iBitsPerPixel == 8) {
                        newImage.setMPixel(iCol, iRow, (byte)piForeColor);
                    }

                    if(iBitsPerPixel == 32) {
                        newImage.setMPixel32(iCol, iRow, (float)piForeColor);
                    }
                } // for iCol
            } // for iRow
            break;

        case I_CHECKERBOARD:
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

        case I_HORZRAMP:
            for (iRow = 1; iRow <= piNumRows; iRow++) {
                for (iCol = 1; iCol <= piNumColumns; iCol++) {
                    if(iBitsPerPixel == 8) {
                        newImage.setMPixel(iCol, iRow, (byte)iCol);
                    }

                    if(iBitsPerPixel == 32) {
                        newImage.setMPixel32(iCol, iRow, (float)iCol);
                    }
                } // for iCol
            } // for iRow
            break;

        case I_VERTRAMP:
            for (iRow = 1; iRow <= piNumRows; iRow++) {
                for (iCol = 1; iCol <= piNumColumns; iCol++) {
                    if(iBitsPerPixel == 8) {
                        newImage.setMPixel(iCol, iRow, (byte)iRow);
                    }

                    if(iBitsPerPixel == 32) {
                        newImage.setMPixel32(iCol, iRow, (float)iRow);
                    }
                } // for iCol
            } // for iRow
            break;

        case I_COUNTER:
            iCounter = 0;
            for (iRow = 1; iRow <= piNumRows; iRow++) {
                for (iCol = 1; iCol <= piNumColumns; iCol++) {
                    if(iBitsPerPixel == 8) {
                        newImage.setMPixel(iCol, iRow, (byte)iCounter);
                    }

                    if(iBitsPerPixel == 32) {
                        newImage.setMPixel32(iCol, iRow, (float)iRow);
                    }

                    iCounter++;
                    iCounter = iCounter % 256;
                } // for iCol
            } // for iRow
            break;

        case I_PLASMA:
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

        // _splitpath(psTexturePath, sDrive, sDir, sFile, sExt);
        File textureFile = new File(psTexturePath);
        Path texturePath = textureFile.toPath();

        File outDirectory = new File(psOutDirectory);
        Path outPath = outDirectory.toPath();
        //_splitpath(psOutDirectory, sDdrive, sDdir, sDfile, sDext);

        // Construct sOutPath, the output path at which to write a bitmap file.
        // It is the same as psOutDirectory, except for the file and extension.
        // We will use sOutPath as a parameter to MemImage.writeBMP
        String fileName = texturePath.getFileName().toString();
        if (outDirectory.isDirectory()) {
            // Just append the filename to the psOutDirectory
            sOutPath = outDirectory + fileName;
        } else {
            sOutPath = outPath.getParent().toString() + fileName;
        }
        // _makepath(sOutPath, sDdrive, sDdir, sFile, sExt);

        String msgText = "Saving Texture Image: " + sOutPath;
        Globals.statusPrint(msgText);

        // Write the bitmap out to file indicatd by sOutPath
        newImage.writeBMP(sOutPath);

        return 0;
    } // createTexture


    // Called from:
    //     createPlasma
    // This method came from TEXTURE.CPP
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
    // This method came from TEXTURE.CPP
    public static void createPlasma(MemImage pImage, int piNumRows, int piNumColumns) {
        pImage.setMPixel(         1,      1,  (byte)myRand(64));
        pImage.setMPixel(         1, piNumRows, (byte)myRand(64));
        pImage.setMPixel(piNumColumns,       1, (byte)myRand(64));
        pImage.setMPixel(piNumColumns,  piNumRows, (byte)myRand(64));

        plasma(pImage, 1, 1, piNumColumns, piNumRows);
    } // createPlasma


    // Called from:
    //     createPlasma
    //     plasma
    // This method came from TEXTURE.CPP
    public static int myRand(int piMaxVal) {
        // random.nextFloat returns a value between 0.0 and 1.0
        // myRand returns a value between 0 and maxVal.
        return (int)(random.nextFloat() * piMaxVal);
    } // myRand
} // class Texture