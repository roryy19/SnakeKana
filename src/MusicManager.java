import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class MusicManager {
    private static MusicManager instance;
    private List<String> tracks = new ArrayList<>();
    private int currentTrackIndex = 0;
    private Clip currentClip;
    private FloatControl volumeControl;
    private float volume = 0.3f;
    private boolean skipRequested = false;
    private LineListener currentListener;

    private MusicManager() {
        // add music file paths 
        tracks.add("/res/music/track1_In_Dreamland.wav");
        tracks.add("/res/music/track2_Loading.wav");
        tracks.add("/res/music/track3_2_00AM.wav");
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void play() {
        stop();
        try {
            String path = tracks.get(currentTrackIndex);
            AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource(path));
            currentClip = AudioSystem.getClip();
            currentClip.open(stream);
    
            if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(volume);
            } else {
                volumeControl = null;
            }

            if (currentListener != null) {
                currentClip.removeLineListener(currentListener); // cleanup just in case
            }
    
            // Set and add the new listener
            currentListener = e -> {
                if (e.getType() == LineEvent.Type.STOP && !skipRequested && currentClip != null && !currentClip.isRunning()) {
                    skip(); // Only skip if it finished naturally
                }
            };
    
            currentClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP && !skipRequested && currentClip != null && !currentClip.isRunning()) {
                    skip(); // only skip if it naturally finishes
                }
            });
    
            skipRequested = false; // reset flag after listener is set
            currentClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skip() {
        skipRequested = true;
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size();
        stop();
        play();
    }

    public void skipBackward() {
        skipRequested = true;
        currentTrackIndex = (currentTrackIndex - 1 + tracks.size()) % tracks.size();
        stop();
        play();
    }    

    public void setVolume(float vol) {
        volume = vol;
        if (volumeControl != null) {
            float dB = (float)(Math.log10(vol == 0 ? 0.0001 : vol) * 20);
            volumeControl.setValue(dB);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void stop() {
        if (currentClip != null) {
            if (currentListener != null) {
                currentClip.removeLineListener(currentListener);
                currentListener = null;
            }
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }
}
