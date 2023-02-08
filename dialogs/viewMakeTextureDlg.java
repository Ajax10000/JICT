package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewMakeTextureDlg extends JFrame {
    
    public viewMakeTextureDlg() {
        MakeTextureDlg makeTextureDlg = new MakeTextureDlg(this, true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewMakeTextureDlg();
            }
        });
    }
}
