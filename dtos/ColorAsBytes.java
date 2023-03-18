package dtos;

// This is just a DTO, data transfer object.
// Used to pass output parameters to methods.
public class ColorAsBytes {
    public byte bytRed, bytGreen, bytBlue;

    public ColorAsBytes() {
        this.bytRed = (byte)0;
        this.bytGreen = (byte)0;
        this.bytBlue = (byte)0;
    }
} // class ColorAsBytes
