import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScreen extends JPanel {
    
    private JFrame frame;
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;

    private int chooseX;
    private int chooseY;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;
    
    private int greenButtonX;
    private int greenButtonY;
    private int greenButtonWidth;
    private int greenButtonHeight;

    private int redButtonX;
    private int redButtonY;
    private int redButtonWidth;
    private int redButtonHeight;

    private int blueButtonX;
    private int blueButtonY;
    private int blueButtonWidth;
    private int blueButtonHeight;

    private Image dirtImage;
    

    public MenuScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        ImageIcon dirtIcon = new ImageIcon(getClass().getResource("/res/images/dirt-background.PNG"));
        dirtImage = dirtIcon.getImage();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
    }
    private void checkClick(int x, int y) {
        // home button
        if (x >= homeButtonX && x <= (homeButtonX + homeButtonWidth) && 
            y >= homeButtonY && y <= (homeButtonY + homeButtonHeight)) {
            startHome();
        }
        // snake green
        if (x >= greenButtonX && x <= (greenButtonX + greenButtonWidth) && 
            y >= greenButtonY && y <= (greenButtonY + greenButtonHeight)) {
            changeColor(new Color(23, 102, 31));
        }
        // snake red
        if (x >= redButtonX && x <= (redButtonX + redButtonWidth) && 
            y >= redButtonY && y <= (redButtonY + redButtonHeight)) {
            changeColor(Color.RED);
        }
        // snake blue
        if (x >= blueButtonX && x <= (blueButtonX + blueButtonWidth) && 
            y >= blueButtonY && y <= (blueButtonY + blueButtonHeight)) {
            changeColor(Color.BLUE);
        }
    }
    private void startHome() {
        frame.remove(this); // remove menu screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
    }
    private void changeColor(Color color) {
        GameSettings.setSnakeColor(color);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(dirtImage, 0, 0, this); // dirt background
        g.drawImage(dirtImage, dirtImage.getWidth(this) - 10, 0, this); 
        g.drawImage(dirtImage, 0, dirtImage.getHeight(this) - 10, this);
        g.drawImage(dirtImage, dirtImage.getWidth(this) - 10, dirtImage.getHeight(this) - 10, this);
        drawHomeButton(g); // home button
        drawSnakeColorButtons(g); // choose snake color buttons
    }
    public void drawHomeButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = 10; // center on x axis
        homeButtonY = 70; // center on y axis
        homeButtonWidth = metricsHome.stringWidth("Home"); // width of Play text
        homeButtonHeight = metricsHome.getHeight(); // height of play text
        g.drawString("Home", homeButtonX, homeButtonY); 
        homeButtonY = homeButtonY - metricsHome.getAscent(); // make Y coord top of text not middle for clicking
    } 
    private void drawSnakeColorButtons(Graphics g) {
        // choose snake text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsChoose = g.getFontMetrics();
        chooseX = (SCREEN_WIDTH - metricsChoose.stringWidth("Choose Snake Color:")) / 2;; 
        chooseY = 200; 
        g.drawString("Choose Snake Color:", chooseX, chooseY); 

        // green snake button
        g.setColor(new Color(23, 102, 31));
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsGreen = g.getFontMetrics();
        greenButtonX = (SCREEN_WIDTH - metricsGreen.stringWidth("Green")) / 2;; 
        greenButtonY = 300; 
        greenButtonWidth = metricsGreen.stringWidth("Green");
        greenButtonHeight = metricsGreen.getHeight(); 
        g.drawString("Green", greenButtonX, greenButtonY); 
        greenButtonY = greenButtonY - metricsGreen.getAscent(); 

        // red snake button
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsRed = g.getFontMetrics();
        redButtonX = (SCREEN_WIDTH - metricsRed.stringWidth("Red")) / 2;; 
        redButtonY = 400; 
        redButtonWidth = metricsRed.stringWidth("Red");
        redButtonHeight = metricsRed.getHeight(); 
        g.drawString("Red", redButtonX, redButtonY); 
        redButtonY = redButtonY - metricsRed.getAscent();

        // blue snake button
        g.setColor(Color.BLUE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsBlue = g.getFontMetrics();
        blueButtonX = (SCREEN_WIDTH - metricsBlue.stringWidth("Blue")) / 2;; 
        blueButtonY = 500; 
        blueButtonWidth = metricsBlue.stringWidth("Blue");
        blueButtonHeight = metricsBlue.getHeight(); 
        g.drawString("Blue", blueButtonX, blueButtonY); 
        blueButtonY = blueButtonY - metricsBlue.getAscent();
    }
}