package dtos;

// This is just a DTO, data transfer object.
// Used to pass output parameters to methods.
public class BoundingBox {
    public int ixBeg, ixEnd;
    public int iyBeg, iyEnd;

    public BoundingBox() {
        this.ixBeg = 0;
        this.ixEnd = 0;
        this.iyBeg = 0;
        this.iyEnd = 0;
    }
} // class BoundingBox
