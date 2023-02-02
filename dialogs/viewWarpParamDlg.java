package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewWarpParamDlg extends JFrame {
    
    public viewWarpParamDlg() {
        WarpParamDlg warpParamDlg = new WarpParamDlg(this, true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewWarpParamDlg();
            }
        });
    }
}
