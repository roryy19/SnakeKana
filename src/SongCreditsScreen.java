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

    private int currentTrackIndex = -1;
    private boolean showNowPlayingOverlay = false;
    private Timer overlayTimer;

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
        // update the track index every second
        Timer updateTimer = new Timer(100, e -> {
            int newIndex = MusicManager.getInstance().getCurrentTrackIndex();
            if (newIndex != currentTrackIndex) {
                currentTrackIndex = newIndex;
                showNowPlayingOverlay = true;

                if (overlayTimer != null) {
                    overlayTimer.stop();
                }

                // hide overlay after 2 seconds
                overlayTimer = new Timer(2000, evt -> {
                    showNowPlayingOverlay = false;
                    repaint();
                });
                overlayTimer.setRepeats(false);
                overlayTimer.start();

                repaint();
            }
        });
        updateTimer.start();
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

        // show current song playing
        if (showNowPlayingOverlay && currentTrackIndex >= 0) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, getHeight() / 2 - 30, getWidth(), 60);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            g.drawString("Now Playing: " + (currentTrackIndex + 1) + ". " +
                getSongTitle(currentTrackIndex), 30, getHeight() / 2 + 10);
        }
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
        g.setFont(new Font("Ink Free", Font.BOLD, 30));

        String[] titles = {
            "Apple Tree", "Concierge Lounge", "Early Morning In Winter", "Flower Cup", "Green Symphony",
            "Hello", "I Snowboard", "Jay", "Spaceship", "Tea Cozy",
            "Train Covered In White", "Until Late At Night", "Vintage Store", "Wintry Street", "Wooden Table"
        };

        for (int i = 0; i < titles.length; i++) {
            if (i == currentTrackIndex) {
                g.setColor(Color.YELLOW); // highlight current song
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString((i + 1) + ". " + titles[i] + " by Lukrembo", 10, 130 + (45 * i));
        }

        g.setColor(Color.WHITE);
        g.drawString("Source: https://freetouse.com/music", 10, 850);
        g.drawString("Free To Use Music for Videos", 10, 880);
    }


    private String getSongTitle(int index) {
        String[] titles = {
            "Apple Tree", "Concierge Lounge", "Early Morning In Winter", "Flower Cup", "Green Symphony",
            "Hello", "I Snowboard", "Jay", "Spaceship", "Tea Cozy",
            "Train Covered In White", "Until Late At Night", "Vintage Store", "Wintry Street", "Wooden Table"
        };
        return titles[index];
    }
    
}
