/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tts_en;
import com.sun.speech.freetts.*;
/**
 *
 * @author Kiner Shah
 */
public class TextToSpeech {
    /*
    * @param args command line arguments
    */
    private final String VOICENAME = "kevin16";
    public void speak(String text) {
        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice(VOICENAME);
        voice.allocate();
        try {
            voice.setDetailedMetrics(true);
            voice.setPitchShift(7);
            voice.setPitch(20);
            voice.setDurationStretch((float) 1.3);
            voice.speak(text);
        }
        catch(Exception e) { e.printStackTrace(); }
    }
    /*public static void main(String[] args) {
        TextToSpeech tts = new TextToSpeech();
        tts.speak("Hello, I am Kiner Shah.");
    }*/
}