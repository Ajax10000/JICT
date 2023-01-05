package structs;

import globals.Globals;

public class Point2d{
    public float x, y;

    // This method came from VECTOR.CPP
    void display(String message) {
        String msgText = message + " x: " + x + "  y: " + y;
        Globals.statusPrint(msgText);
    }
}