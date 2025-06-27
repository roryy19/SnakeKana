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
        tracks.add("/res/music/Lukrembo - Apple Tree.wav");
        tracks.add("/res/music/Lukrembo - Concierge Lounge.wav");
        tracks.add("/res/music/Lukrembo - Early Morning In Winter.wav");
        tracks.add("/res/music/Lukrembo - Flower Cup.wav");
        tracks.add("/res/music/Lukrembo - Green Symphony.wav");

        tracks.add("/res/music/Lukrembo - Hello.wav");
        tracks.add("/res/music/Lukrembo - I Snowboard.wav");
        tracks.add("/res/music/Lukrembo - Jay.wav");
        tracks.add("/res/music/Lukrembo - Spaceship.wav");
        tracks.add("/res/music/Lukrembo - Tea Cozy.wav");

        tracks.add("/res/music/Lukrembo - Train Covered In White.wav");
        tracks.add("/res/music/Lukrembo - Until Late At Night.wav");
        tracks.add("/res/music/Lukrembo - Vintage Store.wav");
        tracks.add("/res/music/Lukrembo - Wintry Street.wav");
        tracks.add("/res/music/Lukrembo - Wooden Table.wav");
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

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
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
