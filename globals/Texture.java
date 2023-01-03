package globals;

import core.MemImage;

public class Texture {
    // These values came from ICT20.H
    //  Texture Types
    public static int CONSTANT     = 1;
    public static int CHECKERBOARD = 2;
    public static int HORZRAMP     = 3;
    public static int VERTRAMP     = 4;
    public static int PLASMA       = 5;
    public static int COUNTER      = 6;

    public static int createTexture(String texturePath, String outDirectory, 
    int textureType, int imageType, 
    int foreColor, int backColor, 
    int numRows, int numColumns) { 

        int row, col; 
        MemImage newImage;
        int bitsPerPixel = 0;

        if(imageType == 1) bitsPerPixel = 8;   
        if(imageType == 2) bitsPerPixel = 24;   
        if(imageType == 3) bitsPerPixel = 32;

        if( bitsPerPixel > 0)	
            newImage = new MemImage(numRows, numColumns, bitsPerPixel);
        else {
            Globals.statusPrint("createTexture: Unknown imageType. Cannot open texture image");
            return -1;
        }

        byte loValue    = 10;
        byte hiValue    = 250;
        byte checkValue = loValue;

        int cellWidth = 32;
        int cellHeight = 32;
        int colCounter = 0;
        int rowCounter = 0;
        int i, j;
        int counter;

        switch(textureType) {
        case CONSTANT:
            for (row = 1; row <= numRows; row++) {
                for (col = 1; col <= numColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, foreColor);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)foreColor);
                    }
                }
            }
            break;

        case CHECKERBOARD:
            for(j = 1; j<= numRows; j++) {
                rowCounter++;
                if(rowCounter == cellHeight) {
                    rowCounter = 0;
                    if(checkValue == loValue) {
                        checkValue = hiValue;
                    } else {
                        checkValue = loValue;
                    }
                }

                for(i = 1; i<= numColumns; i++) {
                    newImage.setMPixel(i, j,checkValue);
                    colCounter++;
                    if(colCounter == cellWidth) {
                        colCounter = 0;
                        if(checkValue == loValue) {
                            checkValue = hiValue;
                        } else {
                            checkValue = loValue;
                        }
                    }
                }
            }
            break;

        case HORZRAMP:
            for (row = 1; row <= numRows; row++) {
                for (col = 1; col <= numColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)col);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)col);
                    }
                }
            }
            break;

        case VERTRAMP:
            for (row = 1; row <= numRows; row++) {
                for (col = 1; col <= numColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)row);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)row);
                    }
                }
            }
            break;

        case COUNTER:
            counter = 0;
            for (row = 1; row <= numRows; row++) {
                for (col = 1; col <= numColumns; col++) {
                    if(bitsPerPixel == 8) {
                        newImage.setMPixel(col, row, (byte)counter);
                    }

                    if(bitsPerPixel == 32) {
                        newImage.setMPixel32(col, row, (float)row);
                    }

                    counter++;
                    counter = counter % 256;
                }
            }
            break;

        case PLASMA:
            createPlasma(newImage, numRows, numColumns);
            break;

        default:
            Globals.statusPrint("createTexture: Unknown texture type");
            break;
        } // switch

        // Generate the output path name and save the image
        String drive, dir, file, ext;
        String ddrive, ddir, dfile, dext;
        String outPath;

        _splitpath(texturePath,drive,dir,file,ext);
        int theLength = file.length();

        _splitpath(outDirectory,ddrive,ddir,dfile,dext);
        _makepath(outPath, ddrive, ddir, file, ext);

        String msgText;
        msgText = "Saving Texture Image: " + outPath;
        Globals.statusPrint(msgText);
        newImage.writeBMP(outPath);

        return 0;
    }


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
                anImage.setMPixel(x,b,(byte)g);
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

            plasma(anImage, x, y, a,   b);
            plasma(anImage, a, y, x1,  b);
            plasma(anImage, x, b, a,  y1);
            plasma(anImage, a, b, x1, y1);
        }
    }

    public static void createPlasma(MemImage anImage, int numRows, int numColumns) {
        srand( (unsigned)time( null ) );	   //seed the random number generator

        anImage.setMPixel(1,       1,     (byte)myRand(64));
        anImage.setMPixel(1,       numRows,  (byte)myRand(64));
        anImage.setMPixel(numColumns, 1,     (byte)myRand(64));
        anImage.setMPixel(numColumns, numRows, (byte)myRand(64));
        plasma(anImage, 1, 1, numColumns, numRows);
    }

    public static int myRand(int maxVal) {
        return (int)((float)rand()/(float)RAND_MAX * maxVal);
    }
} // class Texture