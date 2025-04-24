import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    private static SoundManager instance;
    private float sfxVolume = 0.3f;

    public SoundManager() {
        // Warm up the audio system by playing a silent clip
        playSilentClip();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
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

    public float getSFXVolume() {
        return sfxVolume;
    }
}
