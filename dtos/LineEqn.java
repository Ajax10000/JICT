package dtos;

// This is just a DTO, data transfer object.
// Used to pass output parameters to methods.
public class LineEqn {
    public float fM; // slope
    public float fB; // y-intercept
    public boolean bHorzFlag; // if true, line is horizontal
    public boolean bVertFlag; // if true, line is vertical

    public LineEqn() {
        this.fM = 0.0f;
        this.fB = 0.0f;
        this.bHorzFlag = false;
        this.bVertFlag = false;
    }
} // class LineEqn
