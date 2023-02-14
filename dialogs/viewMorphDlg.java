package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewMorphDlg extends JFrame {
    
    public viewMorphDlg() {
        MorphDlg morphDlg = new MorphDlg(this, true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewMorphDlg();
            }
        });
    }
}
