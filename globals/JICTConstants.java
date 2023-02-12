package globals;

public class JICTConstants {
    // Originally defined in ICT20.H
    // Model Types
    public static final int I_IMAGE        = 1;
    public static final int I_SHAPE        = 2;
    public static final int I_QUADMESH     = 3;
    public static final int I_COMPOUND     = 4;
    public static final int I_LIGHTSOURCE  = 5;
    
    // Originally defined in ICT20.H
    // Effect Types
    public static final int I_STILL    = 1;
    public static final int I_SEQUENCE = 2;
    public static final int I_MORPH    = 3;

    // Originally defined in ICT20.H
    public static final int I_MAXPATH           = 80;
    public static final int I_MESSAGEMAX        = 132; // Length of the global message string g_msgText
    public static final float F_ZBUFFERMAXVALUE = 2.0E31f;

    // Originally defined in ICT20.H
    // Quadrilateral Mesh Model Sub-Types
    public static final int I_CYLINDER   = 1;
    public static final int I_SPHERE     = 2;
    public static final int I_PLANAR     = 3;
    public static final int I_SINE1D     = 4;
    public static final int I_SINE2D     = 5;
    public static final int I_CHECKER    = 6;
    public static final int I_WHITENOISE = 7;

    // Originally defined in ICT20.H
    // Texture Types
    public static final int I_CONSTANT     = 1;
    public static final int I_CHECKERBOARD = 2;
    public static final int I_HORZRAMP     = 3;
    public static final int I_VERTRAMP     = 4;
    public static final int I_PLASMA       = 5;
    public static final int I_COUNTER      = 6;

    // Originally defined in ICT20.H
    public static final int I_CHROMAVALUE  = 0;
    public static final int I_CHROMARED    = 0;
    public static final int I_CHROMAGREEN  = 0;
    public static final int I_CHROMABLUE   = 0;

    // Originally defined in ICT20.H
    public static final float F_DTR = 3.1415926f/180.0f; // Degrees to Radians
    public static final float F_RTD = 180.0f/3.1415926f; // Radians to Degrees

    // Originally defined in ShapeList
    public static final int I_TEMPVERTICES = 32;
    public static final int I_WITHOUTFACES = 1;
    public static final int I_WITHFACES    = 2;

    // Originally defined in MEMIMAGE.H
    public static final int I_REDCOLOR           = 1;
    public static final int I_GREENCOLOR         = 2;
    public static final int I_BLUECOLOR          = 3;
    public static final int I_EIGHTBITMONOCHROME = 2;
    public static final int I_A32BIT             = 4;
    public static final int I_RGBCOLOR           = 5;
    public static final int I_ONEBITMONOCHROME   = 6;

    // Originally defined in MEMIMAGE.H
    public static final int I_SEQUENTIAL = 1;
    public static final int I_RANDOM     = 0;

    // Originally defined in SCENELST.H
    public static final int I_MAXMODELS = 256;     //size of model array for depth sorting
    //increase if > MAXMODELS are to be used in a scene

    // Originally defined in SCENELST.H
    public static final int I_RED   = 1;
    public static final int I_GREEN = 2;
    public static final int I_BLUE  = 3;

    // Originally defined in SCENELST.H
    public static final int I_MONOCHROME = 1;
    public static final int I_COLOR      = 2;

    // These values came from SHADERS.CPP
    // Define three standard types of triangle
    public static final int I_POINTONSIDE = 1;
    public static final int I_POINTONTOP = 2;
    public static final int I_POINTONBOTTOM = 3;

    // Originally defined in IWARP.CPP
    public static final int I_MAXWVERTICES = 8;
    
    // Originally defined in SCENELST.CPP
    // #define THREE_NUMBERS_NOT_FOUND 2

    // Originally defined in MORPHDIALOG.H
    // #define twoD 2
    // #define threeD 3
}