import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        // Handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ScreenHelper.updateDimensions(getWidth(), getHeight());
                repaint();
            }
        });

        startFakeLoading();
    }

    @Override
    public Dimension getPreferredSize() {
        if (GameSettings.isFullscreen()) {
            return new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight());
        }
        return new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
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

        // Background fills entire screen
        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metrics = g.getFontMetrics();

        // Center text horizontally
        if (doneLoading) {
            String text = "Done Loading!";
            int textX = ScreenHelper.centerX(metrics.stringWidth(text));
            g.drawString(text, textX, 200);
        } else {
            String text = "Loading SnakeKana...";
            int textX = ScreenHelper.centerX(metrics.stringWidth(text));
            g.drawString(text, textX, 200);
        }

        // Center snake animation horizontally and vertically
        int snakeWidth = SEGMENT_WIDTH * 20;
        int snakeX = ScreenHelper.centerX(snakeWidth);
        int snakeY = ScreenHelper.centerY(SEGMENT_HEIGHT);

        int numSegments = progress / 5;
        for (int i = 0; i < numSegments; i++) {
            ImageIcon image;
            if (i == numSegments - 1) image = snakeHead;
            else if (i == 0) image = snakeTail;
            else image = snakeBody;
            g.drawImage(image.getImage(), snakeX + i * SEGMENT_WIDTH, snakeY, SEGMENT_WIDTH, SEGMENT_HEIGHT, this);
        }

        // F11 fullscreen hint at bottom
        drawFullscreenHint(g);
    }

    private void drawFullscreenHint(Graphics g) {
        g.setFont(new Font("Ink Free", Font.BOLD, 35));
        FontMetrics hintMetrics = g.getFontMetrics();

        String part1 = "Press ";
        String part2 = "F11";
        String part3 = " for ";
        String part4 = "fullscreen";
        String part5 = " mode!";

        int totalWidth = hintMetrics.stringWidth(part1 + part2 + part3 + part4 + part5);
        int hintX = ScreenHelper.centerX(totalWidth);
        int hintY = ScreenHelper.fromBottom(100, 0);

        g.setColor(Color.WHITE);
        g.drawString(part1, hintX, hintY);
        hintX += hintMetrics.stringWidth(part1);

        g.setColor(new Color(23, 102, 31));  // Same dark green as "Snake" in SnakeKana
        g.drawString(part2, hintX, hintY);
        hintX += hintMetrics.stringWidth(part2);

        g.setColor(Color.WHITE);
        g.drawString(part3, hintX, hintY);
        hintX += hintMetrics.stringWidth(part3);

        g.setColor(new Color(23, 102, 31));  // Same dark green as "Snake" in SnakeKana
        g.drawString(part4, hintX, hintY);
        hintX += hintMetrics.stringWidth(part4);

        g.setColor(Color.WHITE);
        g.drawString(part5, hintX, hintY);
    }
}
