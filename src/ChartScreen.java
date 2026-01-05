import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChartScreen extends JPanel{
    private JFrame frame;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;

    private int hiraButtonX;
    private int hiraButtonY;
    private int hiraButtonWidth;
    private int hiraButtonHeight;

    private int kataButtonX;
    private int kataButtonY;
    private int kataButtonWidth;
    private int kataButtonHeight;

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
        if (x >= homeButtonX && x <= (homeButtonX + homeButtonWidth) &&
            y >= homeButtonY && y <= (homeButtonY + homeButtonHeight)) {
            startHome();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // hiragana quiz
        if (x >= hiraButtonX && x <= (hiraButtonX + hiraButtonWidth) &&
            y >= hiraButtonY && y <= (hiraButtonY + hiraButtonHeight)) {
            startQuiz("Hiragana");
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // katakana quiz
        if (x >= kataButtonX && x <= (kataButtonX + kataButtonWidth) &&
            y >= kataButtonY && y <= (kataButtonY + kataButtonHeight)) {
            startQuiz("Katakana");
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
    }

    private void startHome() {
        frame.remove(this); // remove chart screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        homeScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    private void startQuiz(String quizChoice) {
        frame.remove(this); // remove menu screen
        QuizScreen quizScreen = new QuizScreen(frame, quizChoice);
        frame.add(quizScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        quizScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawHomeButton(g); // home button
        drawKanaWords(g); // hiragana and katakana words at top
        drawQuizButtons(g);

        // Chart dimensions
        int chartWidth = GameConstants.SCREEN_WIDTH / 2 - 100;
        int chartHeight = GameConstants.SCREEN_HEIGHT - 300;

        // Center charts in their respective halves
        int centerX = ScreenHelper.centerX(0);
        int leftHalfCenter = centerX / 2;
        int rightHalfCenter = centerX + centerX / 2;

        int hiraChartX = leftHalfCenter - chartWidth / 2;
        int kataChartX = rightHalfCenter - chartWidth / 2;

        g.drawImage(hiraganaChart, hiraChartX, 125, chartWidth, chartHeight, this);
        g.drawImage(katakanaChart, kataChartX, 125, chartWidth, chartHeight, this);

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 150, getWidth(), 5);

        // middle divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(centerX, 150, 5, getHeight());

        // two top vertical lines around the Home button
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(centerX - 75, 0, 5, 150);

        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(centerX + 75, 0, 5, 150);
    }

    public void drawHomeButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonWidth = metricsHome.stringWidth("Home");
        homeButtonHeight = metricsHome.getHeight();
        // Center horizontally
        homeButtonX = ScreenHelper.centerX(homeButtonWidth);
        homeButtonY = 90;
        g.drawString("Home", homeButtonX, homeButtonY);
        homeButtonY = homeButtonY - metricsHome.getAscent(); // make Y coord top of text not middle for clicking
    }

    public void drawQuizButtons(Graphics g) {
        int centerX = ScreenHelper.centerX(0);
        int quizY = ScreenHelper.fromBottom(100, 0);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHira = g.getFontMetrics();
        hiraButtonWidth = metricsHira.stringWidth("Quiz");
        hiraButtonHeight = metricsHira.getHeight();
        // Center in left half
        hiraButtonX = centerX / 2 - hiraButtonWidth / 2;
        hiraButtonY = quizY;
        g.drawString("Quiz", hiraButtonX, hiraButtonY);
        hiraButtonY = hiraButtonY - metricsHira.getAscent();

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsKata = g.getFontMetrics();
        kataButtonWidth = metricsKata.stringWidth("Quiz");
        kataButtonHeight = metricsKata.getHeight();
        // Center in right half
        kataButtonX = centerX + centerX / 2 - kataButtonWidth / 2;
        kataButtonY = quizY;
        g.drawString("Quiz", kataButtonX, kataButtonY);
        kataButtonY = kataButtonY - metricsKata.getAscent();
    }

    public void drawKanaWords(Graphics g) { //(ひらがな) (カタカナ)
        int centerX = ScreenHelper.centerX(0);
        int leftHalfCenter = centerX / 2;
        int rightHalfCenter = centerX + centerX / 2;

        // Hiragana - center in left half
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metricsHira = g.getFontMetrics();
        int hiraTextWidth = metricsHira.stringWidth("Hiragana");
        hiraX = leftHalfCenter - hiraTextWidth / 2;
        hiraY = 110;
        g.drawString("Hiragana", hiraX, hiraY);

        g.setFont(new Font("Dialog", Font.PLAIN, 40));
        FontMetrics metricsHiraJ = g.getFontMetrics();
        int hiraJWidth = metricsHiraJ.stringWidth("(ひらがな)");
        g.drawString("(ひらがな)", leftHalfCenter - hiraJWidth / 2, hiraY - 60);

        // Katakana - center in right half
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metricsKata = g.getFontMetrics();
        int kataTextWidth = metricsKata.stringWidth("Katakana");
        kataX = rightHalfCenter - kataTextWidth / 2;
        kataY = 110;
        g.drawString("Katakana", kataX, kataY);

        g.setFont(new Font("Dialog", Font.PLAIN, 40));
        FontMetrics metricsKataJ = g.getFontMetrics();
        int kataJWidth = metricsKataJ.stringWidth("(カタカナ)");
        g.drawString("(カタカナ)", rightHalfCenter - kataJWidth / 2, kataY - 60);
    }
}
