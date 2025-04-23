import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GlobalKeyDispatcher implements KeyEventDispatcher {
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_L:
                    MusicManager.getInstance().skip();
                    break;
                case KeyEvent.VK_J:
                    MusicManager.getInstance().skipBackward(); // if you added it
                    break;
            }
        }
        return false; // Let other components still process the key
    }
}
