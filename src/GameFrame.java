import javax.swing.JFrame;

public class GameFrame extends JFrame{

	private SoundManager soundManager;

	GameFrame(){
		soundManager = new SoundManager();
		this.add(new HomeScreen(this, soundManager));
		this.setTitle("SnakeKana");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);		
	}
}