import java.awt.*;
import java.awt.event.*;

public class GlobalKeyDispatcher implements KeyEventDispatcher {
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_PERIOD:
                    MusicManager.getInstance().skip();
                    break;
                case KeyEvent.VK_COMMA:
                    MusicManager.getInstance().skipBackward(); 
                    break;
                case KeyEvent.VK_F11:
                    GameFrame.getInstance().toggleFullscreen();
                    break;
            }
        }
        return false; // Let other components still process the key
    }
}
