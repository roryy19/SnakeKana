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
        frame.pack();
        menuScreen.requestFocusInWindow();
        frame.validate();
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
        backButtonX = 10; 
        backButtonY = 70; 
        backButtonWidth = metricsBack.stringWidth("Back"); 
        backButtonHeight = metricsBack.getHeight(); 
        g.drawString("Back", backButtonX, backButtonY); 
        backButtonY = backButtonY - metricsBack.getAscent(); 
    } 

    public void drawKeybinds(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));

        g.drawString("Up:", 10, 140);
        g.drawString("W / Up Arrow", 325, 140);

        g.drawString("Down:", 10, 210);
        g.drawString("S / Down Arrow", 325, 210);

        g.drawString("Left:", 10, 280);
        g.drawString("A / Left Arrow", 325, 280);

        g.drawString("Right:", 10, 350);
        g.drawString("D / Right Arrow", 325, 350);

        g.drawString("Pause Game:", 10, 420);
        g.drawString("Escape", 325, 420);

        g.drawString("Submit Answer:", 10, 490);
        g.drawString("Enter", 325, 490);

        g.drawString("Skip Song:", 10, 560);
        g.drawString("> (period)", 325, 560);

        g.drawString("Previous Song:", 10, 630);
        g.drawString("< (comma)", 325, 630);
    }
}
