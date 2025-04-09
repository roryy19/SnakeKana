import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChartScreen extends JPanel{
    private JFrame frame;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;

    private int hiraX;
    private int hiraY;

    private int kataX;
    private int kataY;

    private Image backgroundImage;
    private Image hiraganaChart;
    private Image katakanaChart;
    

    public ChartScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage = backgroundIcon.getImage();

        ImageIcon hiraganaIcon = new ImageIcon(getClass().getResource("/res/images/hiragana_chart_nobg.png"));
        hiraganaChart = hiraganaIcon.getImage();

        ImageIcon katakanaIcon = new ImageIcon(getClass().getResource("/res/images/katakana_chart_nobg.png"));
        katakanaChart = katakanaIcon.getImage();

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
    }

    private void startHome() {
        frame.remove(this); // remove menu screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawHomeButton(g); // home button
        drawKanaWords(g); // hiragana and katakana words at top

        g.drawImage(hiraganaChart, 50, 125, GameConstants.SCREEN_WIDTH / 2 - 100, GameConstants.SCREEN_HEIGHT - 300, this);
        g.drawImage(katakanaChart, 500, 125, GameConstants.SCREEN_WIDTH / 2 - 100, GameConstants.SCREEN_HEIGHT - 300, this);

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 150, getWidth(), 5);

        // middle divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(getWidth() / 2, 150, 5, getHeight());

        // two top vertical lines
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(getWidth() / 2 - 75, 0, 5, 150);

        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(getWidth() / 2 + 75, 0, 5, 150);
    }

    public void drawHomeButton(Graphics g) { 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = (GameConstants.SCREEN_WIDTH - metricsHome.stringWidth("Home")) / 2; // center on x axis
        homeButtonY = 90; 
        homeButtonWidth = metricsHome.stringWidth("Home"); // width of Play text
        homeButtonHeight = metricsHome.getHeight(); // height of play text
        g.drawString("Home", homeButtonX, homeButtonY); 
        homeButtonY = homeButtonY - metricsHome.getAscent(); // make Y coord top of text not middle for clicking
    } 

    public void drawKanaWords(Graphics g) { //(ひらがな) (カタカナ)
        // hiragana
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        hiraX = 50; 
        hiraY = 110; 
        g.drawString("Hiragana", hiraX, hiraY); 

        g.setFont(new Font("Dialog", Font.PLAIN, 40)); 
        g.drawString("(ひらがな)", hiraX, hiraY - 60);
    
        // katakana
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        kataX = getWidth() / 2 + 125; 
        kataY = 110; 
        g.drawString("Katakana", kataX, kataY); 

        g.setFont(new Font("Dialog", Font.PLAIN, 40)); 
        g.drawString("(カタカナ)", kataX, kataY - 60);
    } 
}
