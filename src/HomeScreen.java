import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomeScreen extends JPanel {
    
    private JFrame frame;

    private int playButtonX;
    private int playButtonY;
    private int playButtonWidth;
    private int playButtonHeight;

    private JRadioButton hiraganaButton;
    private JRadioButton katakanaButton;
    private JRadioButton bothButton;
    private ButtonGroup modeGroup;
    private static String selectedKanaMode = "Hiragana";

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
        // Create the radio buttons
        hiraganaButton = new JRadioButton("Hiragana");
        katakanaButton = new JRadioButton("Katakana");
        bothButton = new JRadioButton("Both");

        hiraganaButton.setOpaque(false);
        hiraganaButton.setFocusPainted(false); // removes focus box
        katakanaButton.setOpaque(false);
        katakanaButton.setFocusPainted(false);
        bothButton.setOpaque(false);
        bothButton.setFocusPainted(false); 

        // Group them
        modeGroup = new ButtonGroup();
        modeGroup.add(hiraganaButton);
        modeGroup.add(katakanaButton);
        modeGroup.add(bothButton);

        // Style and position
        hiraganaButton.setFont(new Font("Ink Free", Font.BOLD, 40));
        katakanaButton.setFont(new Font("Ink Free", Font.BOLD, 40));
        bothButton.setFont(new Font("Ink Free", Font.BOLD, 40));

        // Add them to panel
        setLayout(null);
        hiraganaButton.setBounds(350, 530, 250, 50);
        katakanaButton.setBounds(350, 590, 250, 50);
        bothButton.setBounds(350, 650, 250, 50);
        add(hiraganaButton);
        add(katakanaButton);
        add(bothButton);

        switch (selectedKanaMode) {
            case "Katakana":
                katakanaButton.setSelected(true);
                break;
            case "Both":
                bothButton.setSelected(true);
                break;
            case "Hiragana":
            default:
                hiraganaButton.setSelected(true);
                break;
        }
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

        setMode();

        GamePanel gamePanel = new GamePanel(frame, selectedKanaMode);
        frame.add(gamePanel);
        frame.pack();
        gamePanel.requestFocusInWindow();
        frame.validate();
    }

    private void startMenu() {
        frame.remove(this); // remove home screen to go to menu

        setMode();

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
        drawSnakeKana(g); // SnakeKana title
        drawPlayButton(g); // play button
        drawMenuButton(g); // menu button
    }

    public void setMode() {
        if (katakanaButton.isSelected()) selectedKanaMode = "Katakana";
        else if (bothButton.isSelected()) selectedKanaMode = "Both";
        else selectedKanaMode = "Hiragana";
    }

    private void drawSnakeKana(Graphics g) {
        String text1 = "Snake";
        String text2 = "Kana";

        Font titleFont = new Font("Ink Free", Font.BOLD, 175);
        g.setFont(titleFont);

        // measure how wide they are
        FontMetrics metrics = g.getFontMetrics();
        int text1Width = metrics.stringWidth(text1);
        int text2Width = metrics.stringWidth(text2);

        int totalWidth = text1Width + text2Width;
        // center together as one phrase
        int x = (GameConstants.SCREEN_WIDTH - totalWidth) / 2;
        int y = 200;  

        // "Snake" in green
        g.setColor(new Color(23, 102, 31));
        g.drawString(text1, x, y);

        // "Kana" in white, following "Snake"
        g.setColor(Color.WHITE);
        g.drawString(text2, x + text1Width, y);
    }

    private void drawPlayButton(Graphics g) {
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free", Font.BOLD, 125));
        FontMetrics metricsPlay = g.getFontMetrics();
        playButtonX = (GameConstants.SCREEN_WIDTH - metricsPlay.stringWidth("Play")) / 2; // center on x axis
        playButtonY = (GameConstants.SCREEN_HEIGHT / 2); // y axis
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
        menuButtonY = (GameConstants.SCREEN_HEIGHT - 100); // bottom half of y axis
        menuButtonWidth = metricsMenu.stringWidth("Menu"); // width of menu text
        menuButtonHeight = metricsMenu.getHeight(); // height of menu text
        g.drawString("Menu", menuButtonX, menuButtonY); 
        menuButtonY = menuButtonY - metricsMenu.getAscent(); // make Y coord top of text not middle for clicking
    }
}