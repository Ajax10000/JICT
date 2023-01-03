package structs;

import globals.Globals;

public class Point2d{
    public float x, y;

    // This method came from VECTOR.CPP
    void display(String message) {
        String msgText;
        sprintf(msgText,"%s x: %f  y: %f", message, x, y);
        Globals.statusPrint(msgText);
    }
}