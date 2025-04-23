import javax.swing.JFrame;

import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;


public class GameFrame extends JFrame{

	GameFrame(){
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new GlobalKeyDispatcher());
		SoundManager.getInstance();
		MusicManager.getInstance().play();
		this.add(new HomeScreen(this));
		this.setTitle("SnakeKana");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);		
	}
}