import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    private JFrame frame;
    static final int topBarHeight = 100;
    int playAreaHeight;
    int GAME_UNITS; // amount of units that can fit on screen
    int DELAY = 75;
    
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
    Timer timer;
    Random random;

    private Image backgroundImage1;

    int highScore;
    boolean movedThisTick = false;

    boolean chooseHiragana;
    boolean chooseKatakana;
    Kana correctKana;
    boolean newFuriganaCondition = false;
    String furiganaString = "";

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

        // choose kana that will be used
        this.chooseHiragana = kanaMode.equals("Hiragana") || kanaMode.equals("Both");
        this.chooseKatakana = kanaMode.equals("Katakana") || kanaMode.equals("Both");

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

        newKana();
        running = true;
        timer = new Timer(DELAY, this);
        showNewLevelText();
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage1, 0, 0, getWidth(), getHeight(), this); //background
        draw(g);

        if (newFuriganaCondition) { // show furigana text
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Ink Free", Font.BOLD, 60));
            FontMetrics metricsNewLevel = g.getFontMetrics();
            int x = (getWidth() - metricsNewLevel.stringWidth(furiganaString)) / 2;
            int y = (getHeight() / 2 - metricsNewLevel.getAscent());
            g.drawString(furiganaString, x, y);
        }
        /*if (newLevelCondition) { // new level text
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metricsNewLevel = g.getFontMetrics();
            int x = (getWidth() - metricsNewLevel.stringWidth(newLevelString)) / 2;
            int y = (getHeight() / 2 - metricsNewLevel.getAscent());
            g.drawString(newLevelString, x, y);
        }*/
    }
    public void drawHeaderBar(Graphics g) {
        int screenWidth = getWidth();
    
        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenWidth, topBarHeight);

        // Centered romaji prompt
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = g.getFontMetrics();
        String prompt = "Find: " + correctKana.romaji;
        int x = (screenWidth - metrics.stringWidth(prompt)) / 2;
        g.drawString(prompt, x, 50);

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
        return score / total;
    }
    public void draw(Graphics g) {
        if (running) {
            drawHeaderBar(g);
            // draw kana
            g.drawImage(correctKana.image, kanaX - 10, kanaY - 15, GameConstants.UNIT_SIZE * 2, GameConstants.UNIT_SIZE * 2, this);

            // iterate through all body parts of snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // head of snake
                    g.setColor(GameSettings.getSnakeColor());
                    g.fillRect(x[i], y[i], GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE);
                    //g.drawImage(liamSnakeImage, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                } else { // body of snake
                    g.setColor(GameSettings.getSnakeColor());
                    //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); // random snake color
                    g.fillRect(x[i], y[i], GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE);
                }
            }
            g.setColor(Color.CYAN);
            g.setFont(new Font("Ink Free",Font.BOLD, 25));
            FontMetrics metricsHighScore = getFontMetrics(g.getFont());
            g.drawString("High Score: "+highScore, (getWidth() - metricsHighScore.stringWidth("Score : "+highScore)) / 2 - 185, g.getFont().getSize()); // left
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 25));
            FontMetrics metricsLevel = getFontMetrics(g.getFont());
            g.drawString("Level: " + (currentLevel + 1), (getWidth() - metricsLevel.stringWidth("Level: " + currentLevel + 1)) / 2 + 125, g.getFont().getSize()); //right
        } else {
            gameOver(g); 
        }   
    }
    public void newKana() { 
        int unitsWide = getWidth() / GameConstants.UNIT_SIZE;
        int unitsHigh = getHeight() / GameConstants.UNIT_SIZE;

        int minX = 1; // skip 0th column
        int maxX = unitsWide - 2; // skip last column
        int minY = (topBarHeight / GameConstants.UNIT_SIZE) + 1; // skip top bar
        int maxY = unitsHigh - 2; // skip bottom row

        do {
            kanaX = (random.nextInt(maxX - minX + 1) + minX) * GameConstants.UNIT_SIZE;
            kanaY = (random.nextInt(maxY - minY + 1) + minY) * GameConstants.UNIT_SIZE;
        } while (isOnSnake(kanaX, kanaY));

        // random kana
        randomKana();
    }
    private boolean isOnSnake(int x, int y) {
        // doesn't let kana be placed where snake body is
        for (int i = 0; i < bodyParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }
    public void randomKana() {
        if (chooseHiragana && chooseKatakana) { // hiragana and katakana
            int temp = random.nextInt(2); // 0 = hiragana, 1 = katakana
            if (temp == 0) correctKana = Kana.hiragana.get(random.nextInt(Kana.hiragana.size())); // hiragana
            else correctKana = Kana.katakana.get(random.nextInt(Kana.katakana.size())); // katakana
        } 
        else if (chooseKatakana) { // only katakana
            correctKana = Kana.katakana.get(random.nextInt(Kana.katakana.size()));
        }
        else { // only hiragana
            correctKana = Kana.hiragana.get(random.nextInt(Kana.hiragana.size()));
        }
        showFurigana();
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
        if ((x[0] == kanaX) && (y[0] == kanaY)) { 
            bodyParts++;
            score++;
            highScore = Math.max(highScore, score);
            if ((score % 10 )== 0 && DELAY > 40) { // every 10 kanas correct, increase delay as long as it greater than 30
                currentLevel++;
                DELAY -= 10;
                timer.stop();
                timer = new Timer(DELAY, this);
                timer.start();
                showNewLevelText();
            }
            newKana();
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
        Timer clearFuriganaText = new Timer(1000, e -> {
            newFuriganaCondition = false;
            repaint();
        });
        clearFuriganaText.setRepeats(false);
        clearFuriganaText.start();
    }
    public void checkCollision() {
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
        bodyParts = 6;
        for (int i = 0; i < bodyParts; i++) { // make previous snake not stay on screen
            x[i] = -1;
            y[i] = -1;
        }
        direction = 'R';
        x[0] = 0; // set snake back to top left
        y[0] = 0;
        DELAY = 75; // reset snake speed
        currentLevel = 0; // reset level

        startGame();
    }
    private void startHome() {
        frame.remove(this); // remove game screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
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
            }
            movedThisTick = false; // blocks multiple changes within one tick
        }
    }

}