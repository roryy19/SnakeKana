import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    private JFrame frame;
    static final int topBarHeight = 100;
    int playAreaHeight;
    int GAME_UNITS; // amount of units that can fit on screen

    // body of snake
    final int x[] = new int[1000]; // snake wont be bigger than game
    final int y[] = new int[1000];
    int bodyParts = 6; // initial # of body parts

    int score;
    int total;
    int kanaX;
    int kanaY;

    boolean newLevelCondition = true;
    String newLevelString = "";

    final int[] LEVEL_DELAYS = {75, 65, 55, 45, 35};
    final String[] LEVEL_MESSAGES = {
    "Level 1: Speed = 10",
    "Level 2: Speed = 20",
    "Level 3: Speed = 30",
    "Level 4: Speed = 40",
    "*FINAL LEVEL*: Speed = 50"
    };
    int currentLevel = 0;

    int retryButtonX;
    int retryButtonY;
    int retryButtonWidth;
    int retryButtonHeight;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;

    char direction = 'R'; // snake begins game going right

    boolean running = false;
    boolean isPaused = false;
    boolean victoryStatus = false;
    Timer timer;
    Random random;

    private Image backgroundImage1;

    int highScore;
    boolean movedThisTick = false;

    boolean chooseHiragana;
    boolean chooseKatakana;
    Kana correctKana;
    Kana wrongKana;
    ArrayList<Kana> gottenCorrect = new ArrayList<>();
    boolean newFuriganaCondition = false;
    private Timer furiganaTimer;
    String furiganaString = "";
    private Timer choiceTimer;
    boolean newChoiceCondition = false;
    String choiceString = "";

    private KanaManager kanaManager;
    private PauseOverlay pauseOverlay;

    private ArrayList<PlacedKana> placedKanas = new ArrayList<>();

    // images of snake
    ImageIcon bodyBottomLeftIcon = new ImageIcon(getClass().getResource("/res/snake/body_bottomleft.png"));
    ImageIcon bodyBottomRightIcon = new ImageIcon(getClass().getResource("/res/snake/body_bottomright.png"));
    ImageIcon bodyHorizontalIcon = new ImageIcon(getClass().getResource("/res/snake/body_horizontal.png"));
    ImageIcon bodyTopLeftIcon = new ImageIcon(getClass().getResource("/res/snake/body_topleft.png"));
    ImageIcon bodyTopRightIcon = new ImageIcon(getClass().getResource("/res/snake/body_topright.png"));
    ImageIcon bodyVerticalIcon = new ImageIcon(getClass().getResource("/res/snake/body_vertical.png"));

    ImageIcon headLeftIcon = new ImageIcon(getClass().getResource("/res/snake/head_left.png"));
    ImageIcon headRightIcon = new ImageIcon(getClass().getResource("/res/snake/head_right.png"));
    ImageIcon headUpIcon = new ImageIcon(getClass().getResource("/res/snake/head_up.png"));
    ImageIcon headDownIcon = new ImageIcon(getClass().getResource("/res/snake/head_down.png"));

    ImageIcon tailLeftIcon = new ImageIcon(getClass().getResource("/res/snake/tail_left.png"));
    ImageIcon tailRightIcon = new ImageIcon(getClass().getResource("/res/snake/tail_right.png"));
    ImageIcon tailUpIcon = new ImageIcon(getClass().getResource("/res/snake/tail_up.png"));
    ImageIcon tailDownIcon = new ImageIcon(getClass().getResource("/res/snake/tail_down.png"));


    public GamePanel(JFrame frame, String kanaMode) {
        this.frame = frame;
        random = new Random();
        this.setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage1 = backgroundIcon.getImage();

        kanaManager = new KanaManager(kanaMode);

        pauseOverlay = new PauseOverlay(this);
        pauseOverlay.setVisible(false);
        this.setLayout(null);
        this.add(pauseOverlay);

        // Handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ScreenHelper.updateDimensions(getWidth(), getHeight());
                // Update pause overlay bounds to fill entire screen
                pauseOverlay.setBounds(0, 0, getWidth(), getHeight());
                repaint();
            }
        });

        startGame();
    }

    @Override
    public Dimension getPreferredSize() {
        if (GameSettings.isFullscreen()) {
            return new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight());
        }
        return new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
    }

    // Get the game area dimensions (fixed 900x900)
    private int getGameWidth() {
        return GameConstants.SCREEN_WIDTH;
    }

    private int getGameHeight() {
        return GameConstants.SCREEN_HEIGHT;
    }

    // Get the offset for centering the game area
    private int getOffsetX() {
        return ScreenHelper.getGameAreaX();
    }

    private int getOffsetY() {
        return ScreenHelper.getGameAreaY();
    }

    public void startGame() {
        // Wait for layout sizing if needed
        if (getWidth() == 0 || getHeight() == 0) {
            SwingUtilities.invokeLater(() -> startGame());
            return;
        }

        // Use fixed game dimensions for calculations
        playAreaHeight = getGameHeight() - topBarHeight;
        int widthUnits = getGameWidth() / GameConstants.UNIT_SIZE;
        int heightUnits = getGameHeight() / GameConstants.UNIT_SIZE;
        GAME_UNITS = widthUnits * heightUnits;

        // Initialize snake position in game coordinates (not screen coordinates)
        x[0] = getGameWidth() / 2;
        y[0] = (getGameHeight() + GamePanel.topBarHeight) / 2;

        newKanas();
        running = true;
        timer = new Timer(GameSettings.getSnakeSpeed(), this);
        showNewLevelText();
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Background fills entire screen
        g.drawImage(backgroundImage1, 0, 0, getWidth(), getHeight(), this);
        draw(g);

        int offsetX = getOffsetX();
        int offsetY = getOffsetY();

        if (newFuriganaCondition) { // show furigana text
            g.setFont(new Font("Ink Free", Font.BOLD, 60));
            FontMetrics metricsFuri = g.getFontMetrics();
            int textX = offsetX + (getGameWidth() - metricsFuri.stringWidth(furiganaString)) / 2;
            int textY = offsetY + (getGameHeight() / 2 - metricsFuri.getAscent());

            // draw outline (black)
            g.setColor(Color.BLACK);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0) {
                        g.drawString(furiganaString, textX + dx, textY + dy);
                    }
                }
            }
            // draw main text (yellow)
            g.setColor(Color.YELLOW);
            g.drawString(furiganaString, textX, textY);
        }

        if (newChoiceCondition) { // show result of user's kana choice
            g.setFont(new Font("Ink Free", Font.BOLD, 60));
            FontMetrics metricsChoice = g.getFontMetrics();
            int textX = offsetX + (getGameWidth() - metricsChoice.stringWidth(choiceString)) / 2;
            int textY = offsetY + 150;

            // draw outline (black)
            g.setColor(Color.BLACK);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0) {
                        g.drawString(choiceString, textX + dx, textY + dy);
                    }
                }
            }
            // draw main text (yellow)
            if (choiceString == "Correct!") g.setColor(Color.GREEN);
            else g.setColor(Color.RED);
            g.drawString(choiceString, textX, textY);
        }
    }

    public void drawHeaderBar(Graphics g) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        int gameWidth = getGameWidth();

        // Background - spans the game area width
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(offsetX, offsetY, gameWidth, topBarHeight);

        // Centered romaji prompt
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metrics = g.getFontMetrics();
        String prompt = "Find: " + correctKana.romaji;
        int promptX = offsetX + (gameWidth - metrics.stringWidth(prompt)) / 2;
        g.drawString(prompt, promptX, offsetY + 70);

        // Score (left of game area)
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        g.drawString("Score: " + score, offsetX + 10, offsetY + 50);

        // Accuracy (right of game area)
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics accMetrics = g.getFontMetrics();
        String accText = "Accuracy: " + getAccuracy() + "%";
        g.drawString(accText, offsetX + gameWidth - accMetrics.stringWidth(accText) - 10, offsetY + 50);
    }

    public int getAccuracy() {
        if (total == 0) return 0;
        double fraction = (double)score / total; // get decimal
        return (int)(fraction * 100);
    }

    public void draw(Graphics g) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();

        if (running) {
            drawHeaderBar(g);

            // draw kanas (correct and wrong) with offset
            for (PlacedKana pk : placedKanas) {
                g.drawImage(pk.kana.image, offsetX + pk.x - 10, offsetY + pk.y - 15, GameConstants.UNIT_SIZE * 2, GameConstants.UNIT_SIZE * 2, this);
            }

            drawSnake(g);

            // Draw border around game area (3px thick)
            drawGameBorder(g);
        } else if (victoryStatus) {
            startVictory(g);
        } else {
            gameOver(g);
        }
    }

    private void drawGameBorder(Graphics g) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        int gameWidth = getGameWidth();
        int gameHeight = getGameHeight();

        g.setColor(Color.BLACK);
        // Draw 3 rectangles for a 3px thick border
        g.drawRect(offsetX, offsetY + topBarHeight, gameWidth - 1, gameHeight - topBarHeight - 1);
        g.drawRect(offsetX + 1, offsetY + topBarHeight + 1, gameWidth - 3, gameHeight - topBarHeight - 3);
        g.drawRect(offsetX + 2, offsetY + topBarHeight + 2, gameWidth - 5, gameHeight - topBarHeight - 5);
    }

    public void drawSnake(Graphics g) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();

        for (int i = 0; i < bodyParts; i++) {
            // Apply offset for screen rendering
            int xPos = offsetX + x[i];
            int yPos = offsetY + y[i];

            if (i == 0) { //HEAD
                switch (direction) {
                    case 'U':
                        g.drawImage(headUpIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                        break;
                    case 'D':
                        g.drawImage(headDownIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                        break;
                    case 'R':
                        g.drawImage(headRightIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                        break;
                    case 'L':
                        g.drawImage(headLeftIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                        break;
                    default:
                        break;
                }
            } else if (i == bodyParts - 1) { // TAIL
                int xPosBefore = x[i - 1];
                int yPosBefore = y[i - 1];

                if (x[i] == xPosBefore && y[i] < yPosBefore) { // UP
                    g.drawImage(tailUpIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if (x[i] == xPosBefore && y[i] > yPosBefore) { // DOWN
                    g.drawImage(tailDownIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if (x[i] > xPosBefore && y[i] == yPosBefore) { // RIGHT
                    g.drawImage(tailRightIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else { // LEFT
                    g.drawImage(tailLeftIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
            } else { // REST OF BODY
                String directionFromBefore = getDirection(x[i-1], y[i-1], x[i], y[i]);
                String directionToAfter = getDirection(x[i], y[i], x[i+1], y[i+1]);

                if ((directionFromBefore.equals("UP") && directionToAfter.equals("RIGHT")) ||
                    (directionFromBefore.equals("LEFT") && directionToAfter.equals("DOWN"))) { // UP and RIGHT turn or LEFT and DOWN turn
                    g.drawImage(bodyTopRightIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if ((directionFromBefore.equals("UP") && directionToAfter.equals("LEFT")) ||
                    (directionFromBefore.equals("RIGHT") && directionToAfter.equals("DOWN"))) { // UP and LEFT turn or RIGHT and DOWN turn
                    g.drawImage(bodyTopLeftIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if ((directionFromBefore.equals("DOWN") && directionToAfter.equals("RIGHT")) ||
                (directionFromBefore.equals("LEFT") && directionToAfter.equals("UP"))) { // DOWN and RIGHT turn or LEFT and UP turn
                    g.drawImage(bodyBottomRightIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if ((directionFromBefore.equals("DOWN") && directionToAfter.equals("LEFT")) ||
                (directionFromBefore.equals("RIGHT") && directionToAfter.equals("UP"))){ // DOWN and LEFT turn or RIGHT and UP turn
                    g.drawImage(bodyBottomLeftIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if (directionFromBefore.equals(directionToAfter)) {
                    if (directionFromBefore.equals("UP") || directionFromBefore.equals("DOWN")) {
                        g.drawImage(bodyVerticalIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                    } else {
                        g.drawImage(bodyHorizontalIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                    }
                }
            }
        }
    }

    String getDirection(int beforeX, int beforeY, int afterX, int afterY) {
        int unit = GameConstants.UNIT_SIZE;
        int gameWidth = getGameWidth();
        int gameHeight = getGameHeight();
        int topLimit = GamePanel.topBarHeight;

        // Corrected Wraparound (horizontal)
    if (beforeX == gameWidth - unit && afterX == 0) return "LEFT";  // RIGHT -> LEFT
    if (beforeX == 0 && afterX == gameWidth - unit) return "RIGHT"; // LEFT -> RIGHT

    // Corrected Wraparound (vertical)
    if (beforeY == gameHeight - unit && afterY == topLimit) return "DOWN";    // DOWN -> TOP
    if (beforeY == topLimit && afterY == gameHeight - unit) return "UP";  // UP -> BOTTOM

        if (beforeX == afterX && beforeY > afterY) return "UP";
        if (beforeX == afterX && beforeY < afterY) return "DOWN";
        if (beforeX > afterX && beforeY == afterY) return "RIGHT";
        if (beforeX < afterX && beforeY == afterY) return "LEFT";
        return "NONE";
    }

    public void newKanas() {

        // if got all kanas
        if (gottenCorrect.size() == kanaManager.totalPossibleKana()) {
            victoryStatus = true;
            running = false;
            return;
        }
        // clear list for new kanas to fill with
        placedKanas.clear();

        // **** CORRECT KANA *****
        getCoords();

        // do not pick kana already gotten correct
        do {
            correctKana = kanaManager.randomKana();
        } while (gottenCorrect.contains(correctKana));

        // add to array list
        PlacedKana correctPK = new PlacedKana(kanaX, kanaY, correctKana, true);
        placedKanas.add(correctPK);

        // ***** INCORRECT KANA ******

        // loop through amount of wrong kana that will show up
        // make sure coords and kana have not been used yet
        for(int i = 0; i < GameSettings.getWrongKanaAmount(); i++) {
            int tempX, tempY;
            do {
                getCoords();
                tempX = kanaX;
                tempY = kanaY;
                wrongKana = kanaManager.randomKanaExcludingRomaji(correctKana.romaji);
            } while (isOccupiedOrUsed(tempX, tempY, wrongKana));

            PlacedKana wrongPK = new PlacedKana(kanaX, kanaY, wrongKana, false);
            placedKanas.add(wrongPK);
        }
        showFurigana();
    }

    public void getCoords() {
        // Use fixed game dimensions for coordinate calculations
        int gameWidth = getGameWidth();
        int gameHeight = getGameHeight();

        int unitsWide = gameWidth / GameConstants.UNIT_SIZE;
        int unitsHigh = gameHeight / GameConstants.UNIT_SIZE;

        int minX = 1; // skip 0th column
        int maxX = unitsWide - 2; // skip last column
        int minY = (topBarHeight / GameConstants.UNIT_SIZE) + 1; // skip top bar
        int maxY = unitsHigh - 2; // skip bottom row

        int attempts = 0;

        // random kana
        do {
            kanaX = (random.nextInt(maxX - minX + 1) + minX) * GameConstants.UNIT_SIZE;
            kanaY = (random.nextInt(maxY - minY + 1) + minY) * GameConstants.UNIT_SIZE;
            attempts++;
            if (attempts > 100) break; // avoid inf loop
        } while (isOnSnakeOrKana(kanaX, kanaY));
    }

    // checks if the x and y is used for one kana OR the kana is used
    public boolean isOccupiedOrUsed(int x, int y, Kana wrongKana) {
        for (PlacedKana pk : placedKanas) {
            if ((pk.x == x && pk.y == y) || (pk.kana == wrongKana)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnSnakeOrKana(int x, int y) {
        // doesn't let kana be placed where snake body is
        for (int i = 0; i < bodyParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        // don't place kana too close to snake head (5 units)
        int distanceX = Math.abs(x - this.x[0]);
        int distanceY = Math.abs(y - this.y[0]);

        if ((distanceX <= GameConstants.UNIT_SIZE * 5) && (distanceY <= GameConstants.UNIT_SIZE * 5)) {
            return true;
        }

        // don't place kana right next to another kana
        for (PlacedKana pk : placedKanas) {
            int distanceKanaX = Math.abs(x - pk.x);
            int distanceKanaY = Math.abs(y - pk.y);

            if (distanceKanaX <= GameConstants.UNIT_SIZE && distanceKanaY <= GameConstants.UNIT_SIZE) {
                return true;
            }
        }

        return false; // good coords
    }

    public void move() {
        movedThisTick = true;
        // shifting body parts of snake
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        // change direction of snake
        switch(direction) {
            case 'U':
                y[0] = y[0] - GameConstants.UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + GameConstants.UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - GameConstants.UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + GameConstants.UNIT_SIZE;
                break;
        }
    }

    public void checkKana () {
        // head postion == kana postion
        for (PlacedKana pk : placedKanas) {
            if ((x[0] == pk.x) && (y[0] == pk.y)) {
                // chose correct kana
                if (pk.correct) {
                    showChoiceResult(true);
                    if (!GameSettings.isInfiniteMode()) {
                        gottenCorrect.add(correctKana);
                    }
                    score++;
                    total++;
                    bodyParts++;
                    SoundManager.getInstance().playButtonClick("/res/sound/correct_sound.wav");
                // chose incorrect kana
                } else {
                    showChoiceResult(false);
                    total++;
                    SoundManager.getInstance().playButtonClick("/res/sound/wrong_sound.wav");
                }
                newKanas();
                return;
            }
        }
    }

    public void showNewLevelText() {
        /*
        LEVEL   DELAY
        1       75
        2       65
        3       55
        4       45
        5       35
         */
        newLevelCondition = true;
        newLevelString = LEVEL_MESSAGES[currentLevel];
        repaint();
        Timer clearNewLevelText = new Timer(2000, e -> { // clear flag after 1 second
            newLevelCondition = false;
            repaint(); // remove text
        });
        clearNewLevelText.setRepeats(false); // timer only triggers once
        clearNewLevelText.start();
    }

    public void showFurigana() {
        newFuriganaCondition = true;
        furiganaString = correctKana.romaji;
        repaint();

        if (furiganaTimer != null && furiganaTimer.isRunning()) {
            furiganaTimer.stop();
        }

        furiganaTimer = new Timer(1500, e -> {
            newFuriganaCondition = false;
            repaint();
        });
        furiganaTimer.setRepeats(false);
        furiganaTimer.start();
    }

    public void showChoiceResult(boolean choice) {
        newChoiceCondition = true;
        if (choice) { // correct
            choiceString = "Correct!";
        } else { // wrong
            choiceString = "Wrong.";
        }
        repaint();

        if (choiceTimer != null && choiceTimer.isRunning()) {
            choiceTimer.stop();
        }

        choiceTimer = new Timer(1500, e -> {
            newChoiceCondition = false;
            repaint();
        });
        choiceTimer.setRepeats(false);
        choiceTimer.start();
    }

    public void checkCollision() {
        // Use fixed game dimensions for collision detection
        int gameWidth = getGameWidth();
        int gameHeight = getGameHeight();

        // no death mode ON, no collions are ignored
        if (GameSettings.isNoDeathMode()) {
            if (x[0] < 0) {
                x[0] = gameWidth - GameConstants.UNIT_SIZE;
            }
            if (x[0] > gameWidth - GameConstants.UNIT_SIZE) {
                x[0] = 0;
            }
            if (y[0] < topBarHeight) {
                y[0] = gameHeight - GameConstants.UNIT_SIZE;
            }
            if (y[0] > gameHeight - GameConstants.UNIT_SIZE) {
                y[0] = topBarHeight;
            }
            return;
        }

        // no death mode OFF, so collisions CAN happen
        // check if head collides w/ body, iterate through body parts
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) { // one part of body collided w/ head
                running = false; // end game
            }
        }
        // check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // check if head touches right border
        if (x[0] > gameWidth - GameConstants.UNIT_SIZE) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < topBarHeight) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] > gameHeight - GameConstants.UNIT_SIZE) {
            running = false;
        }
        if (!running) {
            timer.stop();
            SoundManager.getInstance().playButtonClick("/res/sound/game_over_sound.wav");
        }
    }

    public void checkClick(int clickX, int clickY) {
        // play again button
        if (!running) { // top left origin
            //retry button
            if (clickX >= retryButtonX && clickX <= (retryButtonX + retryButtonWidth) // within x coords
                && clickY >= retryButtonY && clickY <= (retryButtonY + retryButtonHeight)) { // within y coords
                resetGame();
                SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            }
            // home button
            if (clickX >= homeButtonX && clickX <= (homeButtonX + homeButtonWidth) &&
            clickY >= homeButtonY && clickY <= (homeButtonY + homeButtonHeight)) {
                startHome();
                SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            }
        }
    }

    public void resetGame() {
        score = 0;
        total = 0;
        gottenCorrect.clear();
        bodyParts = 6;
        for (int i = 0; i < bodyParts; i++) { // make previous snake not stay on screen
            x[i] = -1;
            y[i] = -1;
        }
        direction = 'R';
        x[0] = 0; // set snake back to top left
        y[0] = 0;
        currentLevel = 0; // reset level

        startGame();
    }

    public void startHome() {
        frame.remove(this); // remove game screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        homeScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    public void pauseGame() {
        timer.stop();
        pauseOverlay.setVisible(true);
        pauseOverlay.requestFocusInWindow();
    }

    public void resumeGame() {
        pauseOverlay.setVisible(false);
        timer.start();
    }

    public void startVictory(Graphics g) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        int gameWidth = getGameWidth();
        int gameHeight = getGameHeight();
        int centerY = offsetY + gameHeight / 3;

        // congrats text
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog",Font.PLAIN, 100));
        FontMetrics metricsCongratsJ = getFontMetrics(g2d.getFont());
        g2d.drawString("おめでとう!", offsetX + (gameWidth - metricsCongratsJ.stringWidth("おめでとう!")) / 2, centerY - 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 100));
        FontMetrics metricsCongrats = getFontMetrics(g.getFont());
        g.drawString("Congrats!", offsetX + (gameWidth - metricsCongrats.stringWidth("Congrats!")) / 2, centerY);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 60));
        FontMetrics metricsCongrats2 = getFontMetrics(g.getFont());
        g.drawString("You got all the characters!", offsetX + (gameWidth - metricsCongrats2.stringWidth("You got all the characters!")) / 2, centerY + 100);

        // accuracy
        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        String accText = "Accuracy: " + getAccuracy() + "%";
        g.drawString(accText, offsetX + (gameWidth - metricsAccuracy.stringWidth(accText)) / 2, offsetY + 600);

        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy2 = getFontMetrics(g.getFont());
        String correctText = "Correct: " + score + ", Total: " + total;
        g.drawString(correctText, offsetX + (gameWidth - metricsAccuracy2.stringWidth(correctText)) / 2, offsetY + 675);


        // home button - anchor to top-left
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = ScreenHelper.fromLeft(10);
        homeButtonY = 100;
        homeButtonWidth = metricsHome.stringWidth("Home");
        homeButtonHeight = metricsHome.getHeight();
        g.drawString("Home", homeButtonX, homeButtonY);
        homeButtonY = homeButtonY - metricsHome.getAscent();
    }

    public void gameOver(Graphics g) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        int gameWidth = getGameWidth();
        int gameHeight = getGameHeight();
        int centerY = offsetY + gameHeight / 3;

        // game over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", offsetX + (gameWidth - metricsGameOver.stringWidth("Game Over")) / 2, centerY + 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 50));

        // play again button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 85));
        FontMetrics metricsPlayAgain = getFontMetrics(g.getFont());
        retryButtonX = offsetX + (gameWidth - metricsPlayAgain.stringWidth("Play Again")) / 2;
        retryButtonY = offsetY + (gameHeight / 2 + metricsPlayAgain.getHeight());
        retryButtonWidth = metricsPlayAgain.stringWidth("Play Again"); // width of text
        retryButtonHeight = metricsPlayAgain.getHeight();   // height of text
        g.drawString("Play Again", retryButtonX, retryButtonY);
        retryButtonY = retryButtonY - metricsPlayAgain.getAscent(); // gets top of highest character, for mouse click

        // stats
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        String accText = "Accuracy: " + getAccuracy() + "%";
        g.drawString(accText, offsetX + (gameWidth - metricsAccuracy.stringWidth(accText)) / 2, offsetY + 725);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy2 = getFontMetrics(g.getFont());
        String correctText = "Correct: " + score + ", Total: " + total;
        g.drawString(correctText, offsetX + (gameWidth - metricsAccuracy2.stringWidth(correctText)) / 2, offsetY + 800);

        // home button - anchor to top-left
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = ScreenHelper.fromLeft(10);
        homeButtonY = 100;
        homeButtonWidth = metricsHome.stringWidth("Home");
        homeButtonHeight = metricsHome.getHeight();
        g.drawString("Home", homeButtonX, homeButtonY);
        homeButtonY = homeButtonY - metricsHome.getAscent();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkKana ();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!movedThisTick) return;

            // check arrow keys
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: // to go left
                case KeyEvent.VK_A:
                    if (direction != 'R') { // condtionals so user cant do a 180 which would make the head hit the body
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT: // to go right
                case KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP: // to go up
                case KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN: // to go left
                case KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if(running) {
                        pauseGame();
                        return;
                    }
                    break;
            }
            movedThisTick = false; // blocks multiple changes within one tick
        }
    }
}
