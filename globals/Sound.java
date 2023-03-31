package globals;

import javax.sound.sampled.*;

// This class was found on 
// https://stackoverflow.com/questions/3780406/how-to-play-a-sound-alert-in-a-java-application/6700039#6700039
public class Sound {
    public static float F_SAMPLE_RATE = 8000f;
    
    public static void beep(int piHz, int piMSecs) {
        try {
            tone(piHz, piMSecs, 1.0);
        } catch (LineUnavailableException lue) {
            Globals.statusPrint("Sound.beep: LineUnavailableException occurred when attempting beep.");
            Globals.statusPrint("Sound.beep: Exception ignored.");
        }
    } // beep

    public static void tone(int piHz, int piMSecs, double pdVol) throws LineUnavailableException {
        byte[] buf = new byte[1];
        AudioFormat audioFmt = new AudioFormat(F_SAMPLE_RATE,8,1,true,false);     
        SourceDataLine sdl = AudioSystem.getSourceDataLine(audioFmt);
        sdl.open(audioFmt);
        sdl.start();
        for (int i=0; i < piMSecs*8; i++) {
              double fAngle = i / (F_SAMPLE_RATE / piHz) * 2.0 * Math.PI;
              buf[0] = (byte)(Math.sin(fAngle) * 127.0 * pdVol);
              sdl.write(buf,0,1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    } // tone
} // class Sound
