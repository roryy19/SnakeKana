import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PauseOverlay extends JPanel{

    private final GamePanel gamePanel;

    private int continueButtonX;
    private int continueButtonY;
    private int continueButtonWidth;
    private int continueButtonHeight;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;

    public PauseOverlay(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setOpaque(false);
        setFocusable(false);
        setVisible(false);
        setLayout(null); 

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });

        // Handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fill entire overlay area
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        drawText(g);
    }

    public void drawText(Graphics g) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // pause text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 100));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Paused", centerX - metricsGameOver.stringWidth("Paused") / 2, centerY - 200);

        // continue button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsContinue = getFontMetrics(g.getFont());
        continueButtonX = centerX - metricsContinue.stringWidth("Continue") / 2;
        continueButtonY = centerY;
        continueButtonWidth = metricsContinue.stringWidth("Continue"); // width of text
        continueButtonHeight = metricsContinue.getHeight();   // height of text
        g.drawString("Continue", continueButtonX, continueButtonY);

        // home button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = getFontMetrics(g.getFont());
        homeButtonX = centerX - metricsHome.stringWidth("Home") / 2;
        homeButtonY = centerY + 200;
        homeButtonWidth = metricsHome.stringWidth("Home"); // width of text
        homeButtonHeight = metricsHome.getHeight();   // height of text
        g.drawString("Home", homeButtonX, homeButtonY);
    }

    public void checkClick(int x, int y) {
        //continue button
        if (withinBounds(x, y, continueButtonX, continueButtonY, continueButtonWidth, continueButtonHeight)) {
            setVisible(false);
            gamePanel.resumeGame(); // resumes timer, etc.
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // home button
        if (withinBounds(x, y, homeButtonX, homeButtonY, homeButtonWidth, homeButtonHeight)) {
            gamePanel.startHome(); // switch screen
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
    }
    private boolean withinBounds(int x, int y, int btnX, int btnY, int width, int height) {
        return x >= btnX && x <= btnX + width && y >= btnY - height && y <= btnY;
    }
}
