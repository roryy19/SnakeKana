import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KeybindsScreen extends JPanel{

    private JFrame frame;

    private int backButtonX;
    private int backButtonY;
    private int backButtonWidth;
    private int backButtonHeight;

    private Image backgroundImage;

    public KeybindsScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage = backgroundIcon.getImage();

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
                ScreenHelper.updateDimensions(getWidth(), getHeight());
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        if (GameSettings.isFullscreen()) {
            return new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight());
        }
        return new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
    }

    private void checkClick(int x, int y) {
        // home button
        if (x >= backButtonX && x <= (backButtonX + backButtonWidth) &&
            y >= backButtonY && y <= (backButtonY + backButtonHeight)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startBack();
        }
    }

    private void startBack() {
        frame.remove(this); // remove menu screen
        MenuScreen menuScreen = new MenuScreen(frame);
        frame.add(menuScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        menuScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawBackButton(g);
        drawKeybinds(g);

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 100, getWidth(), 5);
    }

    public void drawBackButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsBack = g.getFontMetrics();
        // Anchor to top-left
        backButtonX = ScreenHelper.fromLeft(10);
        backButtonY = 70;
        backButtonWidth = metricsBack.stringWidth("Back");
        backButtonHeight = metricsBack.getHeight();
        g.drawString("Back", backButtonX, backButtonY);
        backButtonY = backButtonY - metricsBack.getAscent();
    }

    public void drawKeybinds(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));

        // Center the keybinds content horizontally
        // Content width is approximately 500px (labels + values)
        int contentWidth = 500;
        int offsetX = ScreenHelper.centerX(contentWidth);
        int labelX = offsetX;
        int valueX = offsetX + 335;  // +20px more spacing from labels

        g.drawString("Up:", labelX, 140);
        g.drawString("W / Up Arrow", valueX, 140);

        g.drawString("Down:", labelX, 210);
        g.drawString("S / Down Arrow", valueX, 210);

        g.drawString("Left:", labelX, 280);
        g.drawString("A / Left Arrow", valueX, 280);

        g.drawString("Right:", labelX, 350);
        g.drawString("D / Right Arrow", valueX, 350);

        g.drawString("Pause Game:", labelX, 420);
        g.drawString("Escape", valueX, 420);

        g.drawString("Submit Answer:", labelX, 490);
        g.drawString("Enter", valueX, 490);

        g.drawString("Skip Song:", labelX, 560);
        g.drawString("> (period)", valueX, 560);

        g.drawString("Previous Song:", labelX, 630);
        g.drawString("< (comma)", valueX, 630);

        g.drawString("Toggle Fullscreen:", labelX, 700);
        g.drawString("F11", valueX, 700);
    }
}
