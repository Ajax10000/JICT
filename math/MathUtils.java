package math;

public class MathUtils {
    // This method originally came from UTILS.CPP
    //
    // Called from:
    //     Globals.fillTrianglez
    //     Shape3d.getWorldVertex
    public static float interpolate(float desired1, float desired2, float reference1, float reference2, float referenceCurrent) {
        if(reference1 == reference2) { 
            return desired1;
        }

        return desired1 - (desired1 - desired2) * ((reference1 - referenceCurrent) / 
                          (reference1 - reference2));
    } // interpolate


    // This method originally came from UTILS.CPP
    // 
    // Method fPolar returns an angle such that the angle is >= 0 and < 360.
    // It assumes the angle passed in is in degrees.
    //
    // Called from:
    //     ScenePreviewDlg.onCmdPlus
    public static float fPolar(float angle) {
        if(angle > 0.0f) {
            while(angle >= 360.0f) {
                angle -= 360.0f;
            }
        } else {
            while(angle <= 0.0f) {
                angle += 360.0f;
            }
        }

        if(angle == 360.0f) {
            angle = 0.0f;
        }

        return angle;
    } // fPolar


    // This method originally came from UTILS.CPP
    // 
    // Called from:
    //     Shape3d.addVertices
    //     Shape3d.getBoundaryPoint
    public static float polarAtan(float run, float rise) {
        //  This arcTangent returns a result between 0 and 2Pi radians;
        float rayAngle = (float)Math.atan2(rise, run);
        if(rayAngle < 0.0f) {
            rayAngle = 3.1415926f + (3.1415926f + rayAngle);
        }

        return rayAngle;
    } // polarAtan


    // This method originally came from UTILS.CPP
    public static float bound(float value, float minValue, float maxValue) {
        if (value < minValue) {
            value = minValue;
        }

        if(value > maxValue) {
            value = maxValue;
        }

        return value;
    } // bound


    // This method originally came from DEPTHSRT.CPP
    // 
    // Called from:
    //     getIntervals
    //     Shape3d.divideLongestArc
    //     Shape3d.getBoundaryPoint
    public static float getDistance2d(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    } // getDistance2d
      

    // This method originally came from DEPTHSRT.CPP
    // 
    // Called from: 
    //     iwarpz
    //     RenderObject.renderMeshz
    //     RenderObject.renderShapez
    //     RenderObject.transformAndProjectPoint2
    //     SceneList.depthSort
    //     Shape3d.getWorldDistance
    public static float getDistance3d(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float)Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)) +
          ((z1 - z2) * (z1 - z2)));
    } // getDistance3d


    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     getIntervals
    public static void getLineEquation(int x1, int y1, int x2, int y2, 
    Float m, Float b, 
    Boolean horzFlag, Boolean vertFlag) {
        // Determine the line equation y = mx + b from 2 (integer) points on the line
        m = 0.0f;
        b = 0.0f;
        horzFlag = false;
        vertFlag = false;
        float rise = (y2 - y1);
        float run  = (x2 - x1);

        // Set output parameters horzFlag, vertFlag, m and b
        if (rise == 0.0f) horzFlag = true;
        if (run == 0.0f)  vertFlag = true;
        if (!(vertFlag || horzFlag)) {
            m = rise / run;
            b = (float)y2 - (m * ((float)x2));
        }
    } // getLineEquation


    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     Shape3d.getBoundaryPoint
    public static void getFLineEquation(float x1, float y1, float x2, float y2, 
    Float m, Float b, 
    Boolean horzFlag, Boolean vertFlag) {
        // Determine the line equation y = mx + b from 2 (float) points on the line
        m = 0.0f;
        b = 0.0f;
        horzFlag = false;
        vertFlag = false;

        float rise = (y2 - y1);
        float run  = (x2 - x1);

        // Set the output parameters horzFlag, vertFlag, m and b
        if (rise == 0.0f) {
            horzFlag = true;
        }
        if (run == 0.0f) {
            vertFlag = true;
        }

        if (!(vertFlag || horzFlag)) {
            m = rise / run;
            b = y2 - (m * x2);
        }
    } // getFLineEquation


    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     getIntervals
    public static int intervalDistance(int a, int b, int c) {
        // Returns 0 if c is inside the interval (a,b).  i.e. a <= c <= b
        // else returns distance between c and interval (a,b)
        int b1 = a - c;
        int b2 = c - b;
        int b3 = Math.max(b1, b2);

        if(b3 < 0) {
            return 0;
        } else {
            return b3;
        }
    } // intervalDistance
} // class MathUtils