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
        inputField.setBounds(GameConstants.SCREEN_WIDTH / 2 - 150, 500, 300, 150); // x, y, width, height
        inputField.setFont(new Font("Ink Free", Font.BOLD, 75));
        inputField.setAlignmentY(CENTER_ALIGNMENT);
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        inputField.addActionListener(e -> submitAnswer());
        this.setLayout(null);
        this.add(inputField);
        inputField.requestFocusInWindow();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });

        if (quizChoice.equals("Hiragana")) {
            kanaManager = new KanaManager(quizChoice);
            hiraganaQuiz();
        }

        else if (quizChoice.equals("Katakana")) {
            kanaManager = new KanaManager(quizChoice);
            katakanaQuiz();
        }
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
        g.drawImage(kanaImage, (GameConstants.SCREEN_WIDTH - GameConstants.UNIT_SIZE*5) / 2, 300, GameConstants.UNIT_SIZE * 5, GameConstants.UNIT_SIZE * 5, this);
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
        frame.pack();
        chartScreen.requestFocusInWindow();
        frame.validate();
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

        // correct / total
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsCorrectTotal = getFontMetrics(g.getFont());
        g.drawString(correctQuiz + " / " + totalQuiz, getWidth() - metricsCorrectTotal.stringWidth(correctQuiz + " / " + totalQuiz) - 25, 50);

        // accuracy
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        g.drawString("Accuracy: " + getQuizAccuracy() + "%", getWidth() - metricsAccuracy.stringWidth("Accuracy: " + getQuizAccuracy() + "%") - 25, 100);
    }

    private void drawResultText(Graphics g) {
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
            FontMetrics metricsChoice = g.getFontMetrics();
            int x = (getWidth() - metricsChoice.stringWidth(choiceString)) / 2;
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
        int x = (getWidth() - metrics.stringWidth(hintText)) / 2;
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
        // congrats text
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog",Font.PLAIN, 100));
        FontMetrics metricsCongratsJ = getFontMetrics(g2d.getFont());
        g2d.drawString("おめでとう!", (getWidth() - metricsCongratsJ.stringWidth("おめでとう!")) / 2, getHeight() / 3 - 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 100));
        FontMetrics metricsCongrats = getFontMetrics(g.getFont());
        g.drawString("Congrats!", (getWidth() - metricsCongrats.stringWidth("Congrats!")) / 2, getHeight() / 3);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 60));
        FontMetrics metricsCongrats2 = getFontMetrics(g.getFont());
        g.drawString("You finished the quiz!", (getWidth() - metricsCongrats2.stringWidth("You finished the quiz!")) / 2, getHeight() / 3 + 100);

        // accuracy
        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        g.drawString("Accuracy: " + getQuizAccuracy() + "%", (getWidth() - metricsAccuracy.stringWidth("Accuracy: " + getQuizAccuracy() + "%")) / 2, 600);
        
        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy2 = getFontMetrics(g.getFont());
        g.drawString("Correct: " + correctQuiz + ", Total: " + totalQuiz, (getWidth() - metricsAccuracy2.stringWidth("Correct: " + correctQuiz + "   Total: " + totalQuiz)) / 2, 675);

        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metricsSkipsHints = getFontMetrics(g.getFont());
        g.drawString("Skips: " + skipsQuiz + ", Hints: " + hintsQuiz, (getWidth() - metricsSkipsHints.stringWidth("Skips: " + skipsQuiz + "   Hints: " + hintsQuiz)) / 2, 800);

        // back button
        drawBackButton(g);
    }

    public void drawBackButton(Graphics g) { 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsback = g.getFontMetrics();
        backButtonX = 10;
        backButtonY = 70; 
        backButtonWidth = metricsback.stringWidth("Back"); // width of back text
        backButtonHeight = metricsback.getHeight(); // height of back text
        g.drawString("Back", backButtonX, backButtonY); 
        backButtonY = backButtonY - metricsback.getAscent(); 
    } 

    public void drawSubmitButton(Graphics g) { 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsSubmit = g.getFontMetrics();
        submitButtonY = 750; 
        submitButtonWidth = metricsSubmit.stringWidth("Submit"); // width of submit text
        submitButtonHeight = metricsSubmit.getHeight(); // height of submit text
        submitButtonX = (GameConstants.SCREEN_WIDTH - submitButtonWidth) / 2;
        g.drawString("Submit", submitButtonX, submitButtonY); 
        submitButtonY = submitButtonY - metricsSubmit.getAscent(); 
    }

    public void drawSkipButton(Graphics g) { 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsSkip = g.getFontMetrics();
        skipButtonY = 850; 
        skipButtonWidth = metricsSkip.stringWidth("Skip"); // width of submit text
        skipButtonHeight = metricsSkip.getHeight(); // height of submit text
        skipButtonX = (GameConstants.SCREEN_WIDTH - skipButtonWidth) / 2;
        g.drawString("Skip", skipButtonX, skipButtonY); 
        skipButtonY = skipButtonY - metricsSkip.getAscent(); 
    }

    public void drawHintButton1(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 23));
        FontMetrics metricsHint1 = g.getFontMetrics();
        hint1ButtonY = 525; 
        hint1ButtonWidth = metricsHint1.stringWidth("(Hint: Show First Letter)"); // width of hint1 text
        hint1ButtonHeight = metricsHint1.getHeight(); // height of hint1 text
        hint1ButtonX = (GameConstants.SCREEN_WIDTH - hint1ButtonWidth) - 10;
        g.drawString("Hint: Show First Letter", hint1ButtonX, hint1ButtonY); 
        hint1ButtonY = hint1ButtonY - metricsHint1.getAscent(); 
    }

    public void drawHintButton2(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 23));
        FontMetrics metricsHint2 = g.getFontMetrics();
        hint2ButtonY = 625; 
        hint2ButtonWidth = metricsHint2.stringWidth("(Hint: Show Full Kana)"); // width of hint2 text
        hint2ButtonHeight = metricsHint2.getHeight(); // height of hint2 text
        hint2ButtonX = (GameConstants.SCREEN_WIDTH - hint2ButtonWidth) - 25;
        g.drawString("Hint: Show Full Kana", hint2ButtonX, hint2ButtonY); 
        hint2ButtonY = hint2ButtonY - metricsHint2.getAscent(); 
    }
}
