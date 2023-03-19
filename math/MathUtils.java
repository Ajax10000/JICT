package math;

import dtos.LineEqn;

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
    public static void getLineEquation(int piX1, int piY1, int piX2, int piY2, 
    LineEqn pLineEqn) {
        // Determine the line equation y = mx + b from 2 (integer) points on the line
        float fRise = (piY2 - piY1);
        float fRun  = (piX2 - piX1);

        // Set output parameter pLineEqn
        if (fRise == 0.0f) {
            pLineEqn.bHorzFlag = true;
        }
        if (fRun == 0.0f) {
            pLineEqn.bVertFlag = true;
        }

        if (!(pLineEqn.bVertFlag || pLineEqn.bHorzFlag)) {
            pLineEqn.fM = fRise / fRun;
            float m = pLineEqn.fM;
            pLineEqn.fB = (float)piY2 - (m * ((float)piX2));
        }
    } // getLineEquation


    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     Shape3d.getBoundaryPoint
    public static void getFLineEquation(float x1, float y1, float x2, float y2, 
    LineEqn pLineEqn) {
        // Determine the line equation y = mx + b from 2 (float) points on the line
        float fRise = (y2 - y1);
        float fRun  = (x2 - x1);

        // Set the output parameter pLineEqn
        if (fRise == 0.0f) {
            pLineEqn.bHorzFlag = true;
        }
        if (fRun == 0.0f) {
            pLineEqn.bVertFlag = true;
        }

        if (!(pLineEqn.bVertFlag || pLineEqn.bHorzFlag)) {
            pLineEqn.fM = fRise / fRun;
            float m = pLineEqn.fM;
            pLineEqn.fB = y2 - (m * x2);
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