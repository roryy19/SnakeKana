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

    private int chartButtonX;
    private int chartButtonY;
    private int chartButtonWidth;
    private int chartButtonHeight;

    private Image backgroundImage1;

    public HomeScreen(JFrame frame) {
        this.frame = frame;

        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

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
        add(hiraganaButton);
        add(katakanaButton);
        add(bothButton);

        hiraganaButton.addActionListener(e -> SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav"));
        katakanaButton.addActionListener(e -> SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav"));
        bothButton.addActionListener(e -> SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav"));


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

        // Handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ScreenHelper.updateDimensions(getWidth(), getHeight());
                layoutComponents();
                repaint();
            }
        });

        // Initial layout
        layoutComponents();
    }

    @Override
    public Dimension getPreferredSize() {
        if (GameSettings.isFullscreen()) {
            return new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight());
        }
        return new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
    }

    private void layoutComponents() {
        // Center radio buttons horizontally, positioned below the lowered Play button
        int radioWidth = 250;
        int radioHeight = 50;
        int radioX = ScreenHelper.centerX(radioWidth) + 25;  // Offset to align with Play button
        int centerY = ScreenHelper.centerY(0);

        hiraganaButton.setBounds(radioX, centerY + 50, radioWidth, radioHeight);
        katakanaButton.setBounds(radioX, centerY + 110, radioWidth, radioHeight);
        bothButton.setBounds(radioX, centerY + 170, radioWidth, radioHeight);
    }

    private void checkClick(int x, int y) {
        // play button
        if (x >= playButtonX && x <= (playButtonX + playButtonWidth) &&
            y >= playButtonY && y <= (playButtonY + playButtonHeight)) {
            startGame();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // menu button
        if (x >= menuButtonX && x <= (menuButtonX + menuButtonWidth) &&
            y >= menuButtonY && y <= (menuButtonY + menuButtonHeight)) {
            startMenu();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // chart/quiz button
        if (x >= chartButtonX && x <= (chartButtonX + chartButtonWidth) &&
            y >= chartButtonY && y <= (chartButtonY + chartButtonHeight)) {
            startChart();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
    }

    private void startGame() {
        frame.remove(this); // remove home screen to play game

        setMode();

        GamePanel gamePanel = new GamePanel(frame, selectedKanaMode);
        frame.add(gamePanel);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        gamePanel.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    private void startMenu() {
        frame.remove(this); // remove home screen to go to menu

        setMode();
        MenuScreen menuScreen = new MenuScreen(frame);
        frame.add(menuScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        menuScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    private void startChart() {
        frame.remove(this);

        setMode();

        ChartScreen chartchartScreen = new ChartScreen(frame);
        frame.add(chartchartScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        chartchartScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage1, 0, 0, getWidth(), getHeight(), this); // background
        drawSnakeKana(g); // SnakeKana title
        drawPlayButton(g); // play button
        drawMenuButton(g); // menu button
        drawQuizChartButton(g); // chart / quiz button
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
        int x = ScreenHelper.centerX(totalWidth);
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

        // Center Play button, lowered to avoid overlapping with title
        playButtonWidth = metricsPlay.stringWidth("Play");
        playButtonHeight = metricsPlay.getHeight();
        playButtonX = ScreenHelper.centerX(playButtonWidth);
        playButtonY = ScreenHelper.centerY(playButtonHeight) + 50;

        g.drawString("Play", playButtonX, playButtonY);
        playButtonY = playButtonY - metricsPlay.getAscent(); // make Y coord top of text not middle for clicking

        // Japanese (あそぶ)
        g.setFont(new Font("Dialog", Font.PLAIN, 40));
        FontMetrics metricsJp = g.getFontMetrics();
        int playButtonJ = metricsJp.stringWidth("(あそぶ)");
        g.drawString("(あそぶ)", ScreenHelper.centerX(playButtonJ), playButtonY);
    }

    private void drawMenuButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsMenu = g.getFontMetrics();

        menuButtonWidth = metricsMenu.stringWidth("Menu");
        menuButtonHeight = metricsMenu.getHeight();

        // Anchor to bottom-right
        menuButtonX = ScreenHelper.fromRight(50, menuButtonWidth);
        menuButtonY = ScreenHelper.fromBottom(50, 0);

        g.drawString("Menu", menuButtonX, menuButtonY);
        menuButtonY = menuButtonY - metricsMenu.getAscent(); // make Y coord top of text not middle for clicking
    }

    private void drawQuizChartButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricschart = g.getFontMetrics();

        String line1 = "Kana Charts &";
        String line2 = "Practice Quizzes";

        // Anchor to bottom-left
        chartButtonX = ScreenHelper.fromLeft(25);
        chartButtonY = ScreenHelper.fromBottom(130, 0);

        // Draw first line
        g.drawString(line1, chartButtonX, chartButtonY);

        // Draw second line slightly below the first
        int lineSpacing = metricschart.getHeight(); // spacing based on font height
        g.drawString(line2, chartButtonX, chartButtonY + lineSpacing);

        // Store button bounds for click detection
        chartButtonWidth = Math.max(metricschart.stringWidth(line1), metricschart.stringWidth(line2));
        chartButtonHeight = 2 * lineSpacing;
        chartButtonY = chartButtonY - metricschart.getAscent(); // top Y for click detection
    }
}
