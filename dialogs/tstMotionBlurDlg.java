package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class tstMotionBlurDlg extends JFrame {
    
    public tstMotionBlurDlg() {
        MotionBlurDlg motionBlurDlg = new MotionBlurDlg(this, true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new tstMotionBlurDlg();
            }
        });
    }
}
