package fileUtils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class SCNFileFilter extends FileFilter {
    public boolean accept(File file) {
        if (file.getName().endsWith(".scn")) {
            return true;
        }

        if(file.isDirectory()) {
            return true;
        }

        return false;
    } // accept

    public String getDescription() {
        return "Scene (.scn) Files";
    }
} // class SCNFileFilter