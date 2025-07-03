import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadingScreen extends JPanel{

    private JFrame frame; 

    private Timer timer;
    private ImageIcon backgroundImage;
    private ImageIcon snakeHead;
    private ImageIcon snakeBody;
    private ImageIcon snakeTail;

    private int progress = 0;
    private boolean doneLoading = false;
    private final int SEGMENT_WIDTH = 35;
    private final int SEGMENT_HEIGHT = 35;

    public LoadingScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

        backgroundImage = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));

        snakeHead = new ImageIcon(getClass().getResource("/res/snake/head_right.png")); 
        snakeBody = new ImageIcon(getClass().getResource("/res/snake/body_horizontal.png"));
        snakeTail = new ImageIcon(getClass().getResource("/res/snake/tail_left.png"));

        startFakeLoading();
    }

    private void startFakeLoading() {
        timer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                progress++;
                if (progress == 100) {
                    timer.stop();
                    doneLoading = true;

                    Timer delayTimer = new Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            ((Timer)e.getSource()).stop();
                            frame.getContentPane().removeAll();
                            frame.add(new HomeScreen(frame));
                            frame.revalidate();
                            frame.repaint();
                        }
                    });
                    frame.repaint();
                    delayTimer.setRepeats(false);                    
                    delayTimer.start();
                } else {
                    frame.repaint();
                }
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        if (doneLoading) {
            g.drawString("Done Loading!", 250, 200);
        } else {
            g.drawString("Loading SnakeKana...", 180, 200);
        }

        int snakeX = (GameConstants.SCREEN_WIDTH - SEGMENT_WIDTH * 20) / 2;
        int snakeY = GameConstants.SCREEN_HEIGHT / 2;

        int numSegments = progress / 5;  
        for (int i = 0; i < numSegments; i++) {
            ImageIcon image;
            if (i == numSegments - 1) image = snakeHead;
            else if (i == 0) image = snakeTail;
            else image = snakeBody;
            g.drawImage(image.getImage(), snakeX + i * SEGMENT_WIDTH, snakeY, SEGMENT_WIDTH, SEGMENT_HEIGHT, this);
        }
    }
}
