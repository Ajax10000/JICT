package structs;

import globals.Globals;

public class Point3d {
    public float x, y, z;

    // This method came from VECTOR.CPP
    public void display(String message) {
        String msgText;
        sprintf(msgText,"%s x: %f  y: %f  z: %f", message, x, y, z);
        Globals.statusPrint(msgText);
    }
} // class Point3d