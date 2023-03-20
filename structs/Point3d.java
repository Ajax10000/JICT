package structs;

import globals.Globals;

public class Point3d {
    public float fX, fY, fZ;

    // This method originally came from VECTOR.CPP
    public void display(String psMessage) {
        String sMsgText = psMessage + "  x: " + fX + "  y: " + fY + "  z: " + fZ;
        Globals.statusPrint(sMsgText);
    } // display
    

    public Point3d() {
        this.fX = 0.0f;
        this.fY = 0.0f;
        this.fZ = 0.0f;
    } // Point3d ctor
} // class Point3d