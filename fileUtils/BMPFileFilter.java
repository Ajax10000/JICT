package fileUtils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class BMPFileFilter extends FileFilter {
    public boolean accept(File file) {
        String sFileName = file.getName();
        sFileName = sFileName.toLowerCase();
        if (sFileName.endsWith(".bmp")) {
            return true;
        }

        if(file.isDirectory()) {
            return true;
        }

        return false;
    } // accept

    public String getDescription() {
        return "BMP Files";
    }
} // class BMPFileFilter