package math;

import dtos.OneFloat;

import structs.Point3d;

public class Vect {
    // This method originally came from VECTOR.CPP
    public static void vectorAdd(Point3d result, Point3d p1, Point3d p2) {
        // Set the output parameter
        result.fX = p1.fX + p2.fX;
        result.fY = p1.fY + p2.fY;
        result.fZ = p1.fZ + p2.fZ;
    } // vectorAdd
    

    // This method originally came from VECTOR.CPP
    // 
    // Called from:
    //     RenderObject.transformAndProjectPoint2
    public static void vectorSubtract(Point3d result, Point3d p1, Point3d p2) {
        // Set the output parameter
        result.fX = p1.fX - p2.fX;
        result.fY = p1.fY - p2.fY;
        result.fZ = p1.fZ - p2.fZ;
    } // vectorSubtract
    

    // This method originally came from VECTOR.CPP
    public static float vectorMagnitude2(Point3d p1, Point3d p2) {
        float fResult;
        fResult = (float)Math.sqrt((p1.fX * p2.fX) + (p1.fY * p2.fY) + (p1.fZ * p2.fZ));

        return fResult;
    } // vectorMagnitude2
    

    // This method originally came from VECTOR.CPP
    public static float vectorMagnitude1(Point3d p1) {
        float fResult;
        fResult = (float)Math.sqrt((p1.fX * p1.fX) + (p1.fY * p1.fY) + (p1.fZ * p1.fZ));

        return fResult;
    } // vectorMagnitude1
    

    // This method originally came from VECTOR.CPP
    // 
    // Called from:
    //     RenderObject.renderMeshz
    public static void vectorNormalize(Point3d p1) {
        float fMag = vectorMagnitude1(p1);
        if(fMag > 1.0f) {
            p1.fX /= fMag;
            p1.fY /= fMag;
            p1.fZ /= fMag;
        }
    } // vectorNormalize
    

    // This method originally came from VECTOR.CPP
    public static void crossProduct(Point3d result, Point3d p1, Point3d p2, Point3d p3) {
        //  Watt's definition. p 15. P2 is the origin common to p1 and p3.
        float fV1 = p3.fX - p2.fX;
        float fV2 = p3.fY - p2.fY;
        float fV3 = p3.fZ - p2.fZ;
        
        float fW1 = p1.fX - p2.fX;
        float fW2 = p1.fY - p2.fY;
        float fW3 = p1.fZ - p2.fZ;
        
        // Set the output parameter
        result.fX = (fV2 * fW3) - (fV3 * fW2);
        result.fY = (fV3 * fW1) - (fV1 * fW3);
        result.fZ = (fV1 * fW2) - (fV2 * fW1);
    } // crossProduct
    

    // This method originally came from VECTOR.CPP
    public static float dotProduct(Point3d p1, Point3d p2) {
        float fResult;
        fResult = (p1.fX * p2.fX) + (p1.fY * p2.fY) + (p1.fZ * p2.fZ);

        return fResult;
    } // dotProduct
    

    // This method originally came from VECTOR.CPP
    // Could not find where this method is called from.
    public static void getNormal1(
    float x0, float y0, float z0, 
    float x1, float y1, float z1, 
    float x2, float y2, float z2, 
    OneFloat xNOF, OneFloat yNOF, OneFloat zNOF) {
        //  Compute the surface normal of the three input points
        float xv = x1 - x0;
        float yv = y1 - y0;
        float zv = z1 - z0;
        
        float xw = x2 - x1;
        float yw = y2 - y1;
        float zw = z2 - z1;
        
        // Set the output parameters
        xNOF.f = (yv * zw) - (zv * yw);
        yNOF.f = (zv * xw) - (xv * zw);
        zNOF.f = (xv * yw) - (yv * xw);
    } // getNormal1
    

    // This method originally came from VECTOR.CPP
    // 
    // Called from:
    //     RenderObject.renderMeshz
    public static void getNormal2(Point3d result, 
    Point3d p0, Point3d p1, Point3d p2) {
        //  Compute the surface normal of the three input points
        float xv = p1.fX - p0.fX;
        float yv = p1.fY - p0.fY;
        float zv = p1.fZ - p0.fZ;
        
        float xw = p2.fX - p1.fX;
        float yw = p2.fY - p1.fY;
        float zw = p2.fZ - p1.fZ;
        
        // Set the output parameter
        result.fX = (yv * zw) - (zv * yw);
        result.fY = (zv * xw) - (xv * zw);
        result.fZ = (xv * yw) - (yv * xw);
    } // getNormal2
} // class Vect