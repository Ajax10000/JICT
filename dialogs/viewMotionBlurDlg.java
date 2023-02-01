package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewMotionBlurDlg extends JFrame {
    
    public viewMotionBlurDlg() {
        MotionBlurDlg motionBlurDlg = new MotionBlurDlg(this, true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewMotionBlurDlg();
            }
        });
    }
}
