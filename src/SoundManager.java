import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {

    private float sfxVolume = 0.5f;
    private float musicVolume = 0.5f;
    private Clip musicClip;
    private FloatControl musicVolumeControl;

    public SoundManager() {
        // Warm up the audio system by playing a silent clip
        playSilentClip();
    }

    private void playSilentClip() {
        try {
            URL resource = getClass().getResource("/res/sound/silence.wav");
            if (resource == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start(); // very short silent sound
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playButtonClick(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource == null) {
                System.err.println("Sound file not found: " + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(resource);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(ais);
            setSFXVolume(clip);

            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSFXVolume(Clip clip) {
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(sfxVolume == 0 ? 0.0001f : sfxVolume) * 20);
            control.setValue(dB);
        } catch (Exception ignored) {}
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (musicVolumeControl != null) {
            float dB = (float) (Math.log10(musicVolume == 0 ? 0.0001f : musicVolume) * 20);
            musicVolumeControl.setValue(dB);
        }
    }

    public void playBackgroundMusic(String path) {
        try {
            if (musicClip != null) {
                musicClip.stop();
                musicClip.close();
            }

            URL resource = getClass().getResource(path);
            if (resource == null) {
                System.err.println("Music file not found: " + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(resource);
            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            musicVolumeControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            setMusicVolume(musicVolume);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
