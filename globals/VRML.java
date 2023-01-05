package globals;

import java.io.File;

public class VRML {
    public static int readVRML(String pathName) {
        QvDB.init();
    
        QvInput	in;
        QvNode	root;
        // Reassign "stderr" 
        File aStream;
            
        aStream = freopen( Globals.ictPreference.getPath(Preference.VRMLLog), "w", stderr );
    
       if(aStream == null) {
            Globals.statusPrint("error on freopen\n");
       }
    
        File newFP = fopen(pathName, "r");
        if (!newFP) {
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
    public static int renderVRML(String inWorldPath, String outImagePath) {
        QvDB.init();
    
        QvInput	in;
        QvNode	root;
     
        // Reassign "stdout" 
        File aStream;
        aStream = freopen(Globals.ictPreference.getPath(Preference.VRMLLog), "w", stdout);
    
        if(aStream == null) {
            Globals.statusPrint("error on freopen\n");
        }
    
        File newFP = fopen(inWorldPath, "r");
        if (newFP = null) {
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
        String msgText = "d:\\ict20\\output\\VRMLImage.bmp";
        Globals.aGraphicPipe.saveOutputImage(msgText);
        Globals.statusPrint(msgText);

        Globals.aGraphicPipe.reset();  // reset the zBuffer and clear the output image
        fclose(aStream);      // close the VRML log
        fclose(newFP);        // close the VRML File
        
        return 0;
    } // renderVRML
} // class VRML