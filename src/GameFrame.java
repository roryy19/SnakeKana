import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public class GameFrame extends JFrame {

	private static GameFrame instance;
	private boolean isFullscreen = false;

	GameFrame() {
		instance = this;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new GlobalKeyDispatcher());
		SoundManager.getInstance();
		MusicManager.getInstance().play();
		this.add(new LoadingScreen(this));
		this.setTitle("SnakeKana");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);

		// Update ScreenHelper when window is resized
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				ScreenHelper.updateDimensions(getWidth(), getHeight());
			}
		});
	}

	public static GameFrame getInstance() {
		return instance;
	}

	public void toggleFullscreen() {
		isFullscreen = !isFullscreen;
		GameSettings.setFullscreen(isFullscreen);

		dispose();  // Required before changing undecorated

		if (isFullscreen) {
			setUndecorated(true);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			setUndecorated(false);
			setExtendedState(JFrame.NORMAL);
			setSize(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
			setLocationRelativeTo(null);  // Re-center
			// Explicitly reset ScreenHelper to base dimensions
			ScreenHelper.updateDimensions(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
		}

		setVisible(true);

		// Force the content pane to update its layout
		if (getContentPane().getComponentCount() > 0) {
			Component currentScreen = getContentPane().getComponent(0);
			currentScreen.setSize(getContentPane().getSize());
			if (currentScreen instanceof JPanel) {
				((JPanel) currentScreen).revalidate();
			}
		}

		revalidate();
		repaint();
	}

	public boolean isFullscreen() {
		return isFullscreen;
	}
}
