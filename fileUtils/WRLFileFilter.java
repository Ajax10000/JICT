package fileUtils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class WRLFileFilter extends FileFilter {
    public boolean accept(File file) {
        String sFileName = file.getName();
        sFileName = sFileName.toLowerCase();
        if (sFileName.endsWith(".wrl")) {
            return true;
        }

        if(file.isDirectory()) {
            return true;
        }

        return false;
    } // accept

    public String getDescription() {
        return "VRML (.wrl) Files";
    }
} // class WRLFileFilter