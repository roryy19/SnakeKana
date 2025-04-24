import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SongCreditsScreen extends JPanel{

    private JFrame frame;

    private int backButtonX;
    private int backButtonY;
    private int backButtonWidth;
    private int backButtonHeight;

    private Image backgroundImage;

    public SongCreditsScreen(JFrame frame) {
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
        drawCredits(g);

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
    
    public void drawCredits(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));

        g.drawString("1. 'In Dreamland'", 10, 130);
        g.drawString("Chillpeach", 300, 130);

        g.drawString("2. 'Loading'", 10, 170);
        g.drawString("Chillpeach", 300, 170);

        g.drawString("3. '2:00 AM'", 10, 210);
        g.drawString("Chillpeach", 300, 210);

    }
}
