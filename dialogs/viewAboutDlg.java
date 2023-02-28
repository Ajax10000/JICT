package dialogs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class viewAboutDlg extends JFrame {
    
    public viewAboutDlg() {
        AboutDlg aboutDlg = new AboutDlg(this, true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new viewAboutDlg();
            }
        });
    }
}
