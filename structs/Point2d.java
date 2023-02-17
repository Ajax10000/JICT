package structs;

import globals.Globals;

public class Point2d{
    public float x, y;

    // This method originally came from VECTOR.CPP
    void display(String psMessage) {
        String sMsgText = psMessage + " x: " + x + "  y: " + y;
        Globals.statusPrint(sMsgText);
    }
}