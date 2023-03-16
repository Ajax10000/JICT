package math;

import structs.Point3d;

public class Vect {
    // This method originally came from VECTOR.CPP
    public static void vectorAdd(Point3d result, Point3d p1, Point3d p2) {
        result.x = p1.x + p2.x;
        result.y = p1.y + p2.y;
        result.z = p1.z + p2.z;
        return;
    } // vectorAdd
    

    // This method originally came from VECTOR.CPP
    // 
    // Called from:
    //     RenderObject.transformAndProjectPoint2
    public static void vectorSubtract(Point3d result, Point3d p1, Point3d p2) {
        result.x = p1.x - p2.x;
        result.y = p1.y - p2.y;
        result.z = p1.z - p2.z;

        return;
    } // vectorSubtract
    

    // This method originally came from VECTOR.CPP
    public static float vectorMagnitude2(Point3d p1, Point3d p2) {
        float fResult;
        fResult = (float)Math.sqrt((p1.x * p2.x) + (p1.y * p2.y) + (p1.z * p2.z));

        return fResult;
    } // vectorMagnitude2
    

    // This method originally came from VECTOR.CPP
    public static float vectorMagnitude1(Point3d p1) {
        float fResult;
        fResult = (float)Math.sqrt((p1.x * p1.x) + (p1.y * p1.y) + (p1.z * p1.z));

        return fResult;
    } // vectorMagnitude1
    

    // This method originally came from VECTOR.CPP
    // 
    // Called from:
    //     RenderObject.renderMeshz
    public static void vectorNormalize(Point3d p1) {
        float fMag = vectorMagnitude1(p1);
        if(fMag > 1.0f) {
            p1.x /= fMag;
            p1.y /= fMag;
            p1.z /= fMag;
        }

        return;
    } // vectorNormalize
    

    // This method originally came from VECTOR.CPP
    public static void crossProduct(Point3d result, Point3d p1, Point3d p2, Point3d p3) {
        //  Watt's definition. p 15. P2 is the origin common to p1 and p3.
        float fV1 = p3.x - p2.x;
        float fV2 = p3.y - p2.y;
        float fV3 = p3.z - p2.z;
        
        float fW1 = p1.x - p2.x;
        float fW2 = p1.y - p2.y;
        float fW3 = p1.z - p2.z;
        
        result.x = (fV2 * fW3) - (fV3 * fW2);
        result.y = (fV3 * fW1) - (fV1 * fW3);
        result.z = (fV1 * fW2) - (fV2 * fW1);
    
        return;
    } // crossProduct
    

    // This method originally came from VECTOR.CPP
    public static float dotProduct(Point3d p1, Point3d p2) {
        float fResult;
        fResult = (p1.x * p2.x) + (p1.y * p2.y) + (p1.z * p2.z);

        return fResult;
    } // dotProduct
    

    // This method originally came from VECTOR.CPP
    public static void getNormal1(float x0, float y0, float z0, 
    float x1, float y1, float z1, 
    float x2, float y2, float z2, 
    Float xN, Float yN, Float zN) {
        //  Compute the surface normal of the three input points
        float xv = x1 - x0;
        float yv = y1 - y0;
        float zv = z1 - z0;
        
        float xw = x2 - x1;
        float yw = y2 - y1;
        float zw = z2 - z1;
        
        xN = (yv * zw) - (zv * yw);
        yN = (zv * xw) - (xv * zw);
        zN = (xv * yw) - (yv * xw);
    } // getNormal1
    

    // This method originally came from VECTOR.CPP
    // 
    // Called from:
    //     RenderObject.renderMeshz
    public static void getNormal2(Point3d result, 
    Point3d p0, Point3d p1, Point3d p2) {
        //  Compute the surface normal of the three input points
        float xv = p1.x - p0.x;
        float yv = p1.y - p0.y;
        float zv = p1.z - p0.z;
        
        float xw = p2.x - p1.x;
        float yw = p2.y - p1.y;
        float zw = p2.z - p1.z;
        
        result.x = (yv * zw) - (zv * yw);
        result.y = (zv * xw) - (xv * zw);
        result.z = (xv * yw) - (yv * xw);
        return;
    } // getNormal2
} // class Vect