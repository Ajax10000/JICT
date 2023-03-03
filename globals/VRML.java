package globals;

import apps.IctApp;
import java.io.File;
import java.util.prefs.Preferences;

public class VRML {
    private static Preferences prefs = Preferences.userNodeForPackage(IctApp.class);

    public static int readVRML(String psPathName) {
        QvDB.init();
    
        QvInput	in;
        QvNode	root;
        // Reassign "stderr" 
        File aStream;
            
        aStream = freopen(prefs.get(Preference.VRMLLog), "w", stderr );
    
       if(aStream == null) {
            Globals.statusPrint("error on freopen\n");
       }
    
        File newFP = fopen(psPathName, "r");
        if (newFP != null) {
            Globals.statusPrint("VRML file not found");
            return -1;
        }
        in.setFilePointer(newFP);
    
        if (QvDB.read(in, root) && (root != null)) {
            Globals.statusPrint("Read was ok\n");
            fclose(newFP);
            fclose(aStream);
            return 0;
        } else {
            Globals.statusPrint("Read was bad\n");
            fclose(newFP);
            fclose(aStream);
            return 1;
        }
    
        fclose(newFP);
        return 0;
    } // readVRML
    

    // Called from:
    //     MainFrame.onToolsRenderVrmlFile
    public static int renderVRML(String psInWorldPath, String psOutImagePath) {
        QvDB.init();
    
        QvInput	in;
        QvNode	root;
     
        // Reassign "stdout" 
        File aStream;
        aStream = freopen(prefs.get(Preference.VRMLLog), "w", stdout);
    
        if(aStream == null) {
            Globals.statusPrint("error on freopen\n");
        }
    
        File newFP = fopen(psInWorldPath, "r");
        if (newFP == null) {
            Globals.statusPrint("VRML file not found");
            return -1;
        }
        in.setFilePointer(newFP);
    
        if (QvDB.read(in, root) && (root != null)) {
            Globals.statusPrint("renderVRML: VRML read was ok");
        } else {
            Globals.statusPrint("renderVRML: VRML read was bad");
            return 1;
        }
    
        QvState state;
        //
        // Traverse the VRML graph and send vrml shapes 
        // and indexedFaceSets to the gPipe object
        //
        root.traverse(state);
        fclose(aStream);
    
        if(Globals.aGraphicPipe.viewPointInsideBoundingBox()) {
            Globals.beep(10, 10);
            Globals.statusPrint("ViewPoint is inside object bounding box.");
        } else {
            Globals.statusPrint("ViewPoint is outside object bounding box.");
        }
      
        Globals.aGraphicPipe.saveZBuffer("d:\\ict20\\output\\gPipeZBuffer8.bmp");
        String sMsgText = "d:\\ict20\\output\\VRMLImage.bmp";
        Globals.aGraphicPipe.saveOutputImage(sMsgText);
        Globals.statusPrint(sMsgText);

        Globals.aGraphicPipe.reset();  // reset the zBuffer and clear the output image
        fclose(aStream);      // close the VRML log
        fclose(newFP);        // close the VRML File
        
        return 0;
    } // renderVRML
} // class VRML