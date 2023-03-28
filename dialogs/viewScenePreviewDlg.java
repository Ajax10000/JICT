package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewScenePreviewDlg extends JFrame {
    
    public viewScenePreviewDlg() {
        new ScenePreviewDlg(this, true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewScenePreviewDlg();
            }
        });
    }
}
