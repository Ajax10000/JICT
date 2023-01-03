package math;

import structs.Point3d;

public class Vect {
    public static void vectorAdd(Point3d result, Point3d p1, Point3d p2) {
        result.x = p1.x + p2.x;
        result.y = p1.y + p2.y;
        result.z = p1.z + p2.z;
        return;
    }
    
    public static void vectorSubtract(Point3d result, Point3d p1, Point3d p2) {
        result.x = p1.x - p2.x;
        result.y = p1.y - p2.y;
        result.z = p1.z - p2.z;

        return;
    }
    
    public static float vectorMagnitude2(Point3d p1, Point3d p2) {
        float result;
        result = (float)Math.sqrt((p1.x * p2.x) + (p1.y * p2.y) + (p1.z * p2.z));

        return result;
    }
    
    public static float vectorMagnitude1(Point3d p1) {
        float result;
        result = (float)Math.sqrt((p1.x * p1.x) + (p1.y * p1.y) + (p1.z * p1.z));

        return result;
    }
    
    public static void vectorNormalize(Point3d p1) {
        float mag = vectorMagnitude1(p1);
        if(mag > 1.0f) {
            p1.x /= mag;
            p1.y /= mag;
            p1.z /= mag;
        }

        return;
    }
    
    public static void crossProduct(Point3d result, Point3d p1, Point3d p2, Point3d p3) {
        //  Watt's definition. p 15. P2 is the origin common to p1 and p3.
        float v1 = p3.x - p2.x;
        float v2 = p3.y - p2.y;
        float v3 = p3.z - p2.z;
        
        float w1 = p1.x - p2.x;
        float w2 = p1.y - p2.y;
        float w3 = p1.z - p2.z;
        
        result.x = (v2 * w3) - (v3 * w2);
        result.y = (v3 * w1) - (v1 * w3);
        result.z = (v1 * w2) - (v2 * w1);
    
        return;
    }
    
    public static float dotProduct(Point3d p1, Point3d p2) {
        float result;
        result = (p1.x * p2.x) + (p1.y * p2.y) + (p1.z * p2.z);
        return result;
    }
    
    public static void getNormal1(float x0, float y0, float z0, float x1, float y1, float z1, 
              float x2, float y2, float z2, Float xN, Float yN, Float zN) {
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
    }
    
    public static void getNormal2(Point3d result, Point3d p0, Point3d p1, Point3d p2) {
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
    }
} // class Vect