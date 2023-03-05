package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewQuadMeshDlg extends JFrame {
    
    public viewQuadMeshDlg() {
        new QuadMeshDlg(this, true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewQuadMeshDlg();
            }
        });
    }
}
