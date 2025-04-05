import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomeScreen extends JPanel {

    //static final int SCREEN_WIDTH = 600;
    //static final int SCREEN_HEIGHT = 600;
    
    private JFrame frame;
    private int playButtonX;
    private int playButtonY;
    private int playButtonWidth;
    private int playButtonHeight;

    private int menuButtonX;
    private int menuButtonY;
    private int menuButtonWidth;
    private int menuButtonHeight;

    private Image backgroundImage1;

    public HomeScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

        //ImageIcon backgroundIcon = new ImageIcon("src/res/images/dirt-background.PNG");
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage1 = backgroundIcon.getImage();
        setBackground(Color.BLACK);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
        
    }
    private void checkClick(int x, int y) {
        // play button
        if (x >= playButtonX && x <= (playButtonX + playButtonWidth) && 
            y >= playButtonY && y <= (playButtonY + playButtonHeight)) {
            startGame();
        }
        // menu button
        if (x >= menuButtonX && x <= (menuButtonX + menuButtonWidth) &&
            y >= menuButtonY && y <= (menuButtonY + menuButtonHeight)) {
            startMenu();
        }
    }
    private void startGame() {
        frame.remove(this); // remove home screen to play game
        GamePanel gamePanel = new GamePanel(frame);
        frame.add(gamePanel);
        frame.pack();
        gamePanel.requestFocusInWindow();
        frame.validate();
    }
    private void startMenu() {
        frame.remove(this); // remove home screen to go to menu
        MenuScreen menuScreen = new MenuScreen(frame);
        frame.add(menuScreen);
        frame.pack();
        menuScreen.requestFocusInWindow();
        frame.validate();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage1, 0, 0, getWidth(), getHeight(), this); // dirt background
        /*g.drawImage(backgroundImage, backgroundImage.getWidth(this) - 10, 0, this); 
        g.drawImage(backgroundImage, 0, backgroundImage.getHeight(this) - 10, this);
        g.drawImage(backgroundImage, backgroundImage.getWidth(this) - 10, backgroundImage.getHeight(this) - 10, this);*/
        drawPlayButton(g); // play button
        drawMenuButton(g); // menu button
    }
    private void drawPlayButton(Graphics g) {
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free", Font.BOLD, 150));
        FontMetrics metricsPlay = g.getFontMetrics();
        playButtonX = (GameConstants.SCREEN_WIDTH - metricsPlay.stringWidth("Play")) / 2; // center on x axis
        playButtonY = (GameConstants.SCREEN_HEIGHT / 2) - 50; // center on y axis
        playButtonWidth = metricsPlay.stringWidth("Play"); // width of Play text
        playButtonHeight = metricsPlay.getHeight(); // height of play text
        g.drawString("Play", playButtonX, playButtonY); 
        playButtonY = playButtonY - metricsPlay.getAscent(); // make Y coord top of text not middle for clicking
    }
    private void drawMenuButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsMenu = g.getFontMetrics();
        menuButtonX = (GameConstants.SCREEN_WIDTH - metricsMenu.stringWidth("Menu")) / 2; // center on x axis
        menuButtonY = (GameConstants.SCREEN_HEIGHT - 150); // bottom half of y axis
        menuButtonWidth = metricsMenu.stringWidth("Menu"); // width of menu text
        menuButtonHeight = metricsMenu.getHeight(); // height of menu text
        g.drawString("Menu", menuButtonX, menuButtonY); 
        menuButtonY = menuButtonY - metricsMenu.getAscent(); // make Y coord top of text not middle for clicking
    }
}