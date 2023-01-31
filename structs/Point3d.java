package structs;

import globals.Globals;

public class Point3d {
    public float x, y, z;

    // This method came from VECTOR.CPP
    public void display(String message) {
        String msgText = message + "  x: " + x + "  y: " + y + "  z: " + z;
        Globals.statusPrint(msgText);
    } // display
    

    public Point3d() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    } // Point3d ctor
} // class Point3d