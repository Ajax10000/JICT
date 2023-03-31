package math;

import dtos.LineEqn;

public class MathUtils {
    // This method originally came from UTILS.CPP
    //
    // Called from:
    //     Globals.fillTrianglez
    //     Shape3d.getWorldVertex
    public static float interpolate(float pfDesired1, float pfDesired2, 
    float pfRef1, float pfRef2, float pRefCurr) {
        if(pfRef1 == pfRef2) { 
            return pfDesired1;
        }

        pfDesired1 = pfDesired1 - (pfDesired1 - pfDesired2) * 
            ((pfRef1 - pRefCurr) / (pfRef1 - pfRef2));
        return pfDesired1;
    } // interpolate


    // This method originally came from UTILS.CPP
    // 
    // Method fPolar returns an angle such that the angle is >= 0 and < 360.
    // It assumes the angle passed in is in degrees.
    //
    // Called from:
    //     ScenePreviewDlg.onCmdPlus
    public static float fPolar(float pfAngle) {
        if(pfAngle > 0.0f) {
            while(pfAngle >= 360.0f) {
                pfAngle -= 360.0f;
            }
        } else {
            while(pfAngle <= 0.0f) {
                pfAngle += 360.0f;
            }
        }

        if(pfAngle == 360.0f) {
            pfAngle = 0.0f;
        }

        return pfAngle;
    } // fPolar


    // This method originally came from UTILS.CPP
    // 
    // Called from:
    //     Shape3d.addVertices
    //     Shape3d.getBoundaryPoint
    public static float polarAtan(float pfRun, float pfRise) {
        //  This arcTangent returns a result between 0 and 2Pi radians;
        float fRayAngle = (float)Math.atan2(pfRise, pfRun);
        if(fRayAngle < 0.0f) {
            fRayAngle = 3.1415926f + (3.1415926f + fRayAngle);
        }

        return fRayAngle;
    } // polarAtan


    // This method originally came from UTILS.CPP
    // 
    // Called from:
    //     Globals.antiAlias
    //     Globals.fillTrianglez (called 6 times)
    //     Globals.getLight
    //     GPipe.addFace
    //     MemImage.alphaSmooth3
    //     MemImage.alphaSmooth5
    //     MemImage.alphaSmooth7
    public static float bound(float pfValue, float pfMinValue, float pfMaxValue) {
        if (pfValue < pfMinValue) {
            pfValue = pfMinValue;
        }

        if(pfValue > pfMaxValue) {
            pfValue = pfMaxValue;
        }

        return pfValue;
    } // bound


    // This method originally came from DEPTHSRT.CPP
    // 
    // Called from:
    //     getIntervals
    //     Shape3d.divideLongestArc
    //     Shape3d.getBoundaryPoint
    public static float getDistance2d(float pfX1, float pfY1, float pfX2, float pfY2) {
        float fXDiffSqrd = (pfX1 - pfX2) * (pfX1 - pfX2);
        float fYDiffSqrd = (pfY1 - pfY2) * (pfY1 - pfY2);
        float fDist = (float)Math.sqrt(fXDiffSqrd + fYDiffSqrd);

        return fDist;
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
    public static float getDistance3d(float pfX1, float pfY1, float pfZ1, 
    float pfX2, float pfY2, float pfZ2) {
        float fXDiffSqrd = (pfX1 - pfX2) * (pfX1 - pfX2);
        float fYDiffSqrd = (pfY1 - pfY2) * (pfY1 - pfY2);
        float fZDiffSqrd = (pfZ1 - pfZ2) * (pfZ1 - pfZ2);
        float fDist = (float)Math.sqrt(fXDiffSqrd + fYDiffSqrd + fZDiffSqrd);

        return fDist;
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
    public static void getFLineEquation(float pfX1, float pfY1, float pfX2, float pfY2, 
    LineEqn pLineEqn) {
        // Determine the line equation y = mx + b from 2 (float) points on the line
        float fRise = (pfY2 - pfY1);
        float fRun  = (pfX2 - pfX1);

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
            pLineEqn.fB = pfY2 - (m * pfX2);
        }
    } // getFLineEquation


    // This method originally came from IWARP.CPP
    // 
    // Called from:
    //     getIntervals
    public static int intervalDistance(int piA, int piB, int piC) {
        // Returns 0 if c is inside the interval (a,b).  i.e. a <= c <= b
        // else returns distance between c and interval (a,b)
        int iB1 = piA - piC;
        int iB2 = piC - piB;
        int iB3 = Math.max(iB1, iB2);

        if(iB3 < 0) {
            return 0;
        } else {
            return iB3;
        }
    } // intervalDistance
} // class MathUtils