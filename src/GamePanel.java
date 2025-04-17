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
    // int DELAY = 75; // higher = slower game
    
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
        pauseOverlay.setBounds(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        pauseOverlay.setVisible(false);
        this.setLayout(null);
        this.add(pauseOverlay);

        startGame();
    }
    
    public void startGame() {
        // Wait for layout sizing if needed
        if (getWidth() == 0 || getHeight() == 0) {
            SwingUtilities.invokeLater(() -> startGame());
            return;
        }

        playAreaHeight = getHeight() - topBarHeight;
        int widthUnits = getWidth() / GameConstants.UNIT_SIZE;
        int heightUnits = getHeight() / GameConstants.UNIT_SIZE;
        GAME_UNITS = widthUnits * heightUnits;

        x[0] = GameConstants.SCREEN_WIDTH / 2;
        y[0] = (GameConstants.SCREEN_HEIGHT + GamePanel.topBarHeight) / 2;

        newKanas();
        running = true;
        timer = new Timer(GameSettings.getSnakeSpeed(), this);
        showNewLevelText();
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage1, 0, 0, getWidth(), getHeight(), this); //background
        draw(g);

        if (newFuriganaCondition) { // show furigana text
            g.setFont(new Font("Ink Free", Font.BOLD, 60));
            FontMetrics metricsFuri = g.getFontMetrics();
            int x = (getWidth() - metricsFuri.stringWidth(furiganaString)) / 2;
            int y = (getHeight() / 2 - metricsFuri.getAscent());

            // draw outline (black)
            g.setColor(Color.BLACK);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0) {
                        g.drawString(furiganaString, x + dx, y + dy);
                    }
                }
            }
            // draw main text (yellow)
            g.setColor(Color.YELLOW);
            g.drawString(furiganaString, x, y);
        }

        if (newChoiceCondition) { // show result of user's kana choice
            g.setFont(new Font("Ink Free", Font.BOLD, 60));
            FontMetrics metricsChoice = g.getFontMetrics();
            int x = (getWidth() - metricsChoice.stringWidth(choiceString)) / 2;
            int y = 150;

            // draw outline (black)
            g.setColor(Color.BLACK);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0) {
                        g.drawString(choiceString, x + dx, y + dy);
                    }
                }
            }
            // draw main text (yellow)
            if (choiceString == "Correct!") g.setColor(Color.GREEN);
            else g.setColor(Color.RED);
            g.drawString(choiceString, x, y);
        }
    }

    public void drawHeaderBar(Graphics g) {
        int screenWidth = getWidth();
    
        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenWidth, topBarHeight);

        // Centered romaji prompt
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metrics = g.getFontMetrics();
        String prompt = "Find: " + correctKana.romaji;
        int x = (screenWidth - metrics.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 70);

        // Score (left)
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.drawString("Score: " + score, 10, 30);

        // Accuracy (right)
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.drawString("Accuracy: " + getAccuracy() + "%", screenWidth - 235, 30);
    }

    public int getAccuracy() {
        if (total == 0) return 0;
        double fraction = (double)score / total; // get decimal
        return (int)(fraction * 100);
    }

    public void draw(Graphics g) {
        if (running) {
            drawHeaderBar(g);

            // draw kanas (correct and wrong)
            for (PlacedKana pk : placedKanas) {
                g.drawImage(pk.kana.image, pk.x - 10, pk.y - 15, GameConstants.UNIT_SIZE * 2, GameConstants.UNIT_SIZE * 2, this);
            }
            
            drawSnake(g);

            g.setColor(Color.CYAN);
            g.setFont(new Font("Ink Free",Font.BOLD, 25));
            FontMetrics metricsHighScore = getFontMetrics(g.getFont());
            g.drawString("High Score: "+highScore, (getWidth() - metricsHighScore.stringWidth("Score : "+highScore)) / 2 - 185, g.getFont().getSize()); // left
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 25));
            FontMetrics metricsLevel = getFontMetrics(g.getFont());
            g.drawString("Level: " + (currentLevel + 1), (getWidth() - metricsLevel.stringWidth("Level: " + currentLevel + 1)) / 2 + 125, g.getFont().getSize()); //right
        } else if (victoryStatus) {
            startVictory(g);
        } else {
            gameOver(g); 
        }   
    }

    public void drawSnake(Graphics g) {
        for (int i = 0; i < bodyParts; i++) {
            int xPos = x[i];
            int yPos = y[i];

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

                if (xPos == xPosBefore && yPos < yPosBefore) { // UP
                    g.drawImage(tailUpIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if (xPos == xPosBefore && yPos > yPosBefore) { // DOWN
                    g.drawImage(tailDownIcon.getImage(), xPos, yPos, GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, this);
                }
                else if (xPos > xPosBefore && yPos == yPosBefore) { // RIGHT
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
        int unitsWide = getWidth() / GameConstants.UNIT_SIZE;
        int unitsHigh = getHeight() / GameConstants.UNIT_SIZE;

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
                // chose incorrect kana
                } else {
                    showChoiceResult(false);
                    total++;
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
        // no death mode ON, no collions are ignored
        if (GameSettings.isNoDeathMode()) {
            if (x[0] < 0) {
                x[0] = getWidth() - GameConstants.UNIT_SIZE;
            }
            if (x[0] > getWidth() - GameConstants.UNIT_SIZE) {
                x[0] = 0;
            }
            if (y[0] < topBarHeight) {
                y[0] = getHeight() - GameConstants.UNIT_SIZE;
            }
            if (y[0] > getHeight() - GameConstants.UNIT_SIZE) {
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
        if (x[0] > getWidth() - GameConstants.UNIT_SIZE) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < topBarHeight) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] > getHeight() - GameConstants.UNIT_SIZE) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void checkClick(int x, int y) {
        // play again button
        if (!running) { // top left origin
            //retry button
            if (x >= retryButtonX && x <= (retryButtonX + retryButtonWidth) // within x coords
                && y >= retryButtonY && y <= (retryButtonY + retryButtonHeight)) { // within y coords
                resetGame();
            }
            // home button
            if (x >= homeButtonX && x <= (homeButtonX + homeButtonWidth) && 
            y >= homeButtonY && y <= (homeButtonY + homeButtonHeight)) {
            startHome();
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
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
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
        g.drawString("You got all the characters!", (getWidth() - metricsCongrats2.stringWidth("You got all the characters!")) / 2, getHeight() / 3 + 100);

        // accuracy
        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy = getFontMetrics(g.getFont());
        g.drawString("Accuracy: " + getAccuracy() + "%", (getWidth() - metricsAccuracy.stringWidth("Accuracy: " + getAccuracy() + "%")) / 2, 600);
        
        g.setColor(new Color(0, 153, 153));
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsAccuracy2 = getFontMetrics(g.getFont());
        g.drawString("Correct: " + score + ", Total: " + total, (getWidth() - metricsAccuracy2.stringWidth("Correct: " + score + "   Total: " + total)) / 2, 675);


        // home button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = 10; 
        homeButtonY = 100; 
        homeButtonWidth = metricsHome.stringWidth("Home"); 
        homeButtonHeight = metricsHome.getHeight(); 
        g.drawString("Home", homeButtonX, homeButtonY); 
        homeButtonY = homeButtonY - metricsHome.getAscent();
    }

    public void gameOver(Graphics g) {
        // game over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", (getWidth() - metricsGameOver.stringWidth("Game Over")) / 2, getHeight() / 3 + 50);
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free",Font.BOLD, 25)); 
        
        // current/max score and level
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        g.drawString("Score: "+score, (getWidth() - metricsScore.stringWidth("Score : "+score)) / 2, g.getFont().getSize()); // middle
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free",Font.BOLD, 25));
        FontMetrics metricsHighScore = getFontMetrics(g.getFont());
        g.drawString("High Score: "+highScore, (getWidth() - metricsHighScore.stringWidth("Score : "+highScore)) / 2 - 185, g.getFont().getSize()); // left
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        FontMetrics metricsLevel = getFontMetrics(g.getFont());
        g.drawString("Level: " + (currentLevel + 1), (getWidth() - metricsLevel.stringWidth("Level: " + currentLevel + 1)) / 2 + 125, g.getFont().getSize()); //right

        // play again button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 85));
        FontMetrics metricsPlayAgain = getFontMetrics(g.getFont());
        retryButtonX = (getWidth() - metricsPlayAgain.stringWidth("Play Again")) / 2;
        retryButtonY = (getHeight() / 2 + metricsPlayAgain.getHeight());
        retryButtonWidth = metricsPlayAgain.stringWidth("Play Again"); // width of text
        retryButtonHeight = metricsPlayAgain.getHeight();   // height of text
        g.drawString("Play Again", retryButtonX, retryButtonY);
        retryButtonY = retryButtonY - metricsPlayAgain.getAscent(); // gets top of highest character, for mouse click
    
        // home button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = 10; 
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