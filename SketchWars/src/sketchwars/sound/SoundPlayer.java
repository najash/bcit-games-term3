/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchwars.sound;

import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
import java.util.ArrayList;

/**
 *
 * @author David Ly <ly_nekros@hotmail.com>
 */
public final class SoundPlayer {
    
    private static final ArrayList<Clip> bgmList = new ArrayList<Clip>();
    private static final ArrayList<String> sfxList = new ArrayList<String>();
    public SoundPlayer() {
      
    }
    
    public static void loadSound(){     
            sfxList.add("content/bgm/mainTheme.wav");
            sfxList.add("content/sfx/bluntAttack.wav");
            sfxList.add("content/sfx/rifleShot.wav");
            sfxList.add("content/sfx/throwingObject.wav");
            
            try{
            Clip clip = AudioSystem.getClip();
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File("content/sfx/throwingObject.wav"));
            clip.open(soundStream);
            bgmList.add(clip);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
    }
    
    public static void playSFX(int refNumber, boolean autostart, float gain) throws Exception {
        /*Loading new audiostream for each call allows for multiple instances of the same sound to occur
        ie: Two players shoot a rocket at the same time
        */
        AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File(sfxList.get(refNumber)));
        Clip clip = AudioSystem.getClip();
        clip.open(soundStream);
        clip.setFramePosition(0);

        // values have min/max values, for now don't check for outOfBounds values
        FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);

        if(autostart) clip.start();
    }
    
    public static void playMusic(int refNumber, boolean loop, float gain) throws Exception {
        Clip clip = bgmList.get(refNumber);
        clip.setFramePosition(0);

        // values have min/max values, for now don't check for outOfBounds values
        FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);

        if(loop) 
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        /*
        if(autoplay)
            clip.start(); */
        
    }
    
    public static void pause(int refNumber)
    {
        bgmList.get(refNumber).stop();
    }
    
    public static void resume(int refNumber)
    {
        bgmList.get(refNumber).start();
    }
    

}