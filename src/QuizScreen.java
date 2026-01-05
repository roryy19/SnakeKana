//import java.util.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class QuizScreen extends JPanel{
    private JFrame frame;

    private int backButtonX;
    private int backButtonY;
    private int backButtonWidth;
    private int backButtonHeight;

    private int submitButtonX;
    private int submitButtonY;
    private int submitButtonWidth;
    private int submitButtonHeight;

    private int hint1ButtonX;
    private int hint1ButtonY;
    private int hint1ButtonWidth;
    private int hint1ButtonHeight;

    private int hint2ButtonX;
    private int hint2ButtonY;
    private int hint2ButtonWidth;
    private int hint2ButtonHeight;

    private int skipButtonX;
    private int skipButtonY;
    private int skipButtonWidth;
    private int skipButtonHeight;

    private Image backgroundImage;
    private Image kanaImage;

    private int totalQuiz = 0;
    private int correctQuiz = 0;
    private int skipsQuiz = 0;
    private int hintsQuiz = 0;
    private boolean newQuizChoiceCondition = false;
    private boolean victoryQuizStatus = false;
    private boolean hintShown = false;
    private String hintText = "";

    private KanaManager kanaManager;
    private Kana randomKana;
    private String choiceString = "";
    private JTextField inputField;
    private Timer choiceQuizTimer;

    private ArrayList<Kana> gottenCorrect = new ArrayList<>();

    public QuizScreen(JFrame frame, String quizChoice) {
        this.frame = frame;
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage = backgroundIcon.getImage();

        inputField = new JTextField();
        inputField.setFont(new Font("Ink Free", Font.BOLD, 75));
        inputField.setAlignmentY(CENTER_ALIGNMENT);
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        inputField.addActionListener(e -> submitAnswer());
        this.add(inputField);
        inputField.requestFocusInWindow();

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
                layoutComponents();
                repaint();
            }
        });

        // Initial layout
        layoutComponents();

        if (quizChoice.equals("Hiragana")) {
            kanaManager = new KanaManager(quizChoice);
            hiraganaQuiz();
        }

        else if (quizChoice.equals("Katakana")) {
            kanaManager = new KanaManager(quizChoice);
            katakanaQuiz();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (GameSettings.isFullscreen()) {
            return new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight());
        }
        return new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
    }

    private void layoutComponents() {
        // Center input field horizontally
        int inputWidth = 300;
        int inputHeight = 150;
        int inputX = ScreenHelper.centerX(inputWidth);
        int inputY = ScreenHelper.centerY(0) + 50;
        inputField.setBounds(inputX, inputY, inputWidth, inputHeight);
    }

    public void hiraganaQuiz() {
        // choose random kana
        do {
            randomKana = kanaManager.randomKana();
        } while (gottenCorrect.contains(randomKana));
        kanaImage = randomKana.image;
        repaint();
    }

    public void katakanaQuiz() {
        // choose random kana
        do {
            randomKana = kanaManager.randomKana();
        } while (gottenCorrect.contains(randomKana));
        kanaImage = randomKana.image;
        repaint();
    }

    private void submitAnswer() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return; // do nothing if empty
        }

        boolean isCorrect = userInput.equalsIgnoreCase(randomKana.romaji);

        if (isCorrect) {
            gottenCorrect.add(randomKana);
            correctQuiz++;
            SoundManager.getInstance().playButtonClick("/res/sound/correct_sound.wav");
        } else {
            SoundManager.getInstance().playButtonClick("/res/sound/wrong_sound.wav");
        }

        totalQuiz++;
        inputField.setText("");

        showQuizChoiceResult(isCorrect);

        // Load next kana after delay unless quiz is over
        if (gottenCorrect.size() == kanaManager.totalPossibleKana()) {
            inputField.setVisible(false);
            victoryQuizStatus = true;
            kanaImage = null;
        } else {
            new Timer(1000, e -> {
                if (!victoryQuizStatus) {
                    if (kanaManager.getMode().equals("Hiragana")) {
                        hiraganaQuiz();
                    } else {
                        katakanaQuiz();
                    }
                }
            }) {{
                setRepeats(false);
                start();
            }};
        }
    }

    public void showFirstLetterHint() {
        disableInputForHint();
        hintText = randomKana.romaji.substring(0, 1);
    }

    public void showFullKanaHint() {
        disableInputForHint();
        hintText = randomKana.romaji;
    }

    public void disableInputForHint() {
        inputField.setEnabled(false);
        totalQuiz++;
        hintsQuiz++;
        hintShown = true;
        repaint();

        new Timer(1000, e -> {
            hintShown = false;
            hintText = "";
            inputField.setText("");
            inputField.setEnabled(true);

            if (gottenCorrect.size() == kanaManager.totalPossibleKana()) {
                inputField.setVisible(false);
                victoryQuizStatus = true;
                kanaImage = null;
            } else {
                if (kanaManager.getMode().equals("Hiragana")) {
                    hiraganaQuiz();
                } else {
                    katakanaQuiz();
                }
            }
        }) {{
            setRepeats(false);
            start();
        }};
    }

    public void skipAnswer() {
        gottenCorrect.add(randomKana);
        totalQuiz++;
        skipsQuiz++;
        inputField.setText("");

        if (gottenCorrect.size() == kanaManager.totalPossibleKana()) {
            inputField.setVisible(false);
            victoryQuizStatus = true;
            kanaImage = null;
            repaint();
        } else {
            if (kanaManager.getMode().equals("Hiragana")) {
                hiraganaQuiz();
            } else {
                katakanaQuiz();
            }
        }
    }

    public void drawKana(Graphics g) {
        int kanaSize = GameConstants.UNIT_SIZE * 5;
        int kanaX = ScreenHelper.centerX(kanaSize);
        int kanaY = 300;
        g.drawImage(kanaImage, kanaX, kanaY, kanaSize, kanaSize, this);
    }

    private void checkClick(int x, int y) {
        // back button
        if (x >= backButtonX && x <= (backButtonX + backButtonWidth) &&
            y >= backButtonY && y <= (backButtonY + backButtonHeight)) {
            startBack();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // submit button
        if (!victoryQuizStatus &&
            x >= submitButtonX && x <= (submitButtonX + submitButtonWidth) &&
            y >= submitButtonY && y <= (submitButtonY + submitButtonHeight)) {
            submitAnswer();
        }
        // hint1 button
        if (!victoryQuizStatus &&
            x >= hint1ButtonX && x <= (hint1ButtonX + hint1ButtonWidth) &&
            y >= hint1ButtonY && y <= (hint1ButtonY + hint1ButtonHeight)) {
            showFirstLetterHint();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // hint2 button
        if (!victoryQuizStatus &&
            x >= hint2ButtonX && x <= (hint2ButtonX + hint2ButtonWidth) &&
            y >= hint2ButtonY && y <= (hint2ButtonY + hint2ButtonHeight)) {
            showFullKanaHint();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
        // skip button
        if (!victoryQuizStatus &&
            x >= skipButtonX && x <= (skipButtonX + skipButtonWidth) &&
            y >= skipButtonY && y <= (skipButtonY + skipButtonHeight)) {
            skipAnswer();
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        }
    }

    private void startBack() {
        frame.remove(this); // remove quiz screen
        ChartScreen chartScreen = new ChartScreen(frame);
        frame.add(chartScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        chartScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawBackButton(g); // back button

        if (!victoryQuizStatus) {
            drawKana(g);
            drawSubmitButton(g);
            drawSkipButton(g);
            drawHintButton1(g);
            drawHintButton2(g);
            drawQuizStats(g);
        }

        if (newQuizChoiceCondition) { //show correct or wrong based on user's answer
            drawResultText(g);
        }

        if (hintShown) {
            drawHintText(g);
        }

        if (victoryQuizStatus) {
            startVictoryQuiz(g);
        }
    }

    private void drawQuizStats(Graphics g) {
        // show correct/total, accuracy, skips, hints

        // correct / total - anchor to top-right
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsCorrectTotal = getFontMetrics(g.getFont());
        String statsText = correctQuiz + " / " + totalQuiz;
        g.drawString(statsText, ScreenHelper.fromRight(25, metricsCorrectTotal.stringWidth(statsText)), 50);

        // accuracy
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        String accText = "Accuracy: " + getQuizAccuracy() + "%";
        g.drawString(accText, ScreenHelper.fromRight(25, metricsAccuracy.stringWidth(accText)), 100);
    }

    private void drawResultText(Graphics g) {
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metricsChoice = g.getFontMetrics();
        int x = ScreenHelper.centerX(metricsChoice.stringWidth(choiceString));
        int y = 90;

        // draw outline (black)
        g.setColor(Color.BLACK);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    g.drawString(choiceString, x + dx, y + dy);
                }
            }
        }
        // draw main text
        if (choiceString == "Correct!") g.setColor(Color.GREEN);
        else g.setColor(Color.RED);
        g.drawString(choiceString, x, y);
    }

    private void drawHintText(Graphics g) {
        g.setFont(new Font("Ink Free", Font.BOLD, 100));
        g.setColor(Color.WHITE);
        FontMetrics metrics = g.getFontMetrics();
        int x = ScreenHelper.centerX(metrics.stringWidth(hintText));
        int y = 90;

        // draw outline (black)
        g.setColor(Color.BLACK);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    g.drawString(hintText, x + dx, y + dy);
                }
            }
        }
        g.setColor(Color.WHITE);
        g.drawString(hintText, x, y);
    }

    public void showQuizChoiceResult(boolean choice) {
        newQuizChoiceCondition = true;
        if (choice) { // correct
            choiceString = "Correct!";
        } else { // wrong
            choiceString = "Wrong.";
        }
        repaint();

        if (choiceQuizTimer != null && choiceQuizTimer.isRunning()) {
            choiceQuizTimer.stop();
        }

        choiceQuizTimer = new Timer(1000, e -> {
            newQuizChoiceCondition = false;
            repaint();
        });
        choiceQuizTimer.setRepeats(false);
        choiceQuizTimer.start();
    }

    public int getQuizAccuracy() {
        if (totalQuiz == 0) return 0;
        double fraction = (double)correctQuiz / totalQuiz; // get decimal
        return (int)(fraction * 100);
    }

    public void startVictoryQuiz(Graphics g) {
        int centerY = ScreenHelper.centerY(0);

        // congrats text
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog",Font.PLAIN, 100));
        FontMetrics metricsCongratsJ = getFontMetrics(g2d.getFont());
        g2d.drawString("おめでとう!", ScreenHelper.centerX(metricsCongratsJ.stringWidth("おめでとう!")), centerY - 180);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 100));
        FontMetrics metricsCongrats = getFontMetrics(g.getFont());
        g.drawString("Congrats!", ScreenHelper.centerX(metricsCongrats.stringWidth("Congrats!")), centerY - 100);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 60));
        FontMetrics metricsCongrats2 = getFontMetrics(g.getFont());
        g.drawString("You finished the quiz!", ScreenHelper.centerX(metricsCongrats2.stringWidth("You finished the quiz!")), centerY);

        // accuracy
        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        String accText = "Accuracy: " + getQuizAccuracy() + "%";
        g.drawString(accText, ScreenHelper.centerX(metricsAccuracy.stringWidth(accText)), centerY + 150);

        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy2 = getFontMetrics(g.getFont());
        String correctText = "Correct: " + correctQuiz + ", Total: " + totalQuiz;
        g.drawString(correctText, ScreenHelper.centerX(metricsAccuracy2.stringWidth(correctText)), centerY + 225);

        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metricsSkipsHints = getFontMetrics(g.getFont());
        String skipsText = "Skips: " + skipsQuiz + ", Hints: " + hintsQuiz;
        g.drawString(skipsText, ScreenHelper.centerX(metricsSkipsHints.stringWidth(skipsText)), centerY + 350);

        // back button
        drawBackButton(g);
    }

    public void drawBackButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsback = g.getFontMetrics();
        // Anchor to top-left
        backButtonX = ScreenHelper.fromLeft(10);
        backButtonY = 70;
        backButtonWidth = metricsback.stringWidth("Back");
        backButtonHeight = metricsback.getHeight();
        g.drawString("Back", backButtonX, backButtonY);
        backButtonY = backButtonY - metricsback.getAscent();
    }

    public void drawSubmitButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsSubmit = g.getFontMetrics();
        submitButtonWidth = metricsSubmit.stringWidth("Submit");
        submitButtonHeight = metricsSubmit.getHeight();
        // Center horizontally, fixed distance below input (lowered for fullscreen)
        submitButtonX = ScreenHelper.centerX(submitButtonWidth);
        submitButtonY = ScreenHelper.centerY(0) + 280;
        g.drawString("Submit", submitButtonX, submitButtonY);
        submitButtonY = submitButtonY - metricsSubmit.getAscent();
    }

    public void drawSkipButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsSkip = g.getFontMetrics();
        skipButtonWidth = metricsSkip.stringWidth("Skip");
        skipButtonHeight = metricsSkip.getHeight();
        // Center horizontally, below submit button (lowered for fullscreen)
        skipButtonX = ScreenHelper.centerX(skipButtonWidth);
        skipButtonY = ScreenHelper.centerY(0) + 380;
        g.drawString("Skip", skipButtonX, skipButtonY);
        skipButtonY = skipButtonY - metricsSkip.getAscent();
    }

    public void drawHintButton1(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 23));
        FontMetrics metricsHint1 = g.getFontMetrics();
        hint1ButtonWidth = metricsHint1.stringWidth("Hint: Show First Letter");
        hint1ButtonHeight = metricsHint1.getHeight();

        // Center between input field right edge and screen right edge
        int inputRightEdge = ScreenHelper.centerX(300) + 300;  // input field is 300 wide
        int screenRight = ScreenHelper.getWidth();
        int gapCenter = inputRightEdge + (screenRight - inputRightEdge) / 2;

        hint1ButtonX = gapCenter - hint1ButtonWidth / 2;
        hint1ButtonY = ScreenHelper.centerY(0) + 75;
        g.drawString("Hint: Show First Letter", hint1ButtonX, hint1ButtonY);
        hint1ButtonY = hint1ButtonY - metricsHint1.getAscent();
    }

    public void drawHintButton2(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 23));
        FontMetrics metricsHint2 = g.getFontMetrics();
        hint2ButtonWidth = metricsHint2.stringWidth("Hint: Show Full Kana");
        hint2ButtonHeight = metricsHint2.getHeight();

        // Center between input field right edge and screen right edge
        int inputRightEdge = ScreenHelper.centerX(300) + 300;
        int screenRight = ScreenHelper.getWidth();
        int gapCenter = inputRightEdge + (screenRight - inputRightEdge) / 2;

        hint2ButtonX = gapCenter - hint2ButtonWidth / 2;
        hint2ButtonY = ScreenHelper.centerY(0) + 175;
        g.drawString("Hint: Show Full Kana", hint2ButtonX, hint2ButtonY);
        hint2ButtonY = hint2ButtonY - metricsHint2.getAscent();
    }
}
