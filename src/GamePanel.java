import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
//import javax.sound.sampled.*;
//import java.io.IOException;
//import java.net.URL;

public class GamePanel extends JPanel implements ActionListener{

    private JFrame frame;
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // amount of units that can fit on screen
    int DELAY = 75;
    
    // body of snake
    final int x[] = new int[GAME_UNITS]; // snake wont be bigger than game
    final int y[] = new int[GAME_UNITS]; 
    int bodyParts = 6; // initial # of body parts
    
    int applesEaten;    
    int appleX;
    int appleY;
    
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

    private Image appleImage;
    private Image dirtImage;

    int highScore;

    public GamePanel(JFrame frame) {
        this.frame = frame;
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
        /*ImageIcon grassIcon = new ImageIcon("src/res/images/grass-background.jpg");
        grassImage = grassIcon.getImage();*/
        //ImageIcon appleIcon = new ImageIcon("src/res/images/apple-png.png");
        ImageIcon appleIcon = new ImageIcon(getClass().getResource("/res/kana/a_hira.png"));
        appleImage = appleIcon.getImage();
        //ImageIcon dirtIcon = new ImageIcon("src/res/images/dirt-background.PNG");
        /*ImageIcon dirtIcon = new ImageIcon(getClass().getResource("/res/images/dirt-background.PNG"));
        dirtImage = dirtIcon.getImage();
        ImageIcon liamIcon = new ImageIcon("src/res/images/liam-snake-head.JPG");
        liamSnakeImage = liamIcon.getImage();*/
        
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        showNewLevelText();
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*g.drawImage(dirtImage, 0, 0, this); // dirt background
        g.drawImage(dirtImage, dirtImage.getWidth(this) - 10, 0, this); 
        g.drawImage(dirtImage, 0, dirtImage.getHeight(this) - 10, this);
        g.drawImage(dirtImage, dirtImage.getWidth(this) - 10, dirtImage.getHeight(this) - 10, this);*/
        draw(g);

        if (newLevelCondition) { // new level text
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metricsNewLevel = g.getFontMetrics();
            int x = (getWidth() - metricsNewLevel.stringWidth(newLevelString)) / 2;
            int y = (getHeight() / 2 - metricsNewLevel.getAscent());
            g.drawString(newLevelString, x, y);
        }
    }
    public void draw(Graphics g) {
        if (running) {
            // draw apple
            g.drawImage(appleImage, appleX - 10, appleY - 15, UNIT_SIZE * 2, UNIT_SIZE * 2, this);

            // iterate through all body parts of snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // head of snake
                    g.setColor(GameSettings.getSnakeColor());
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    //g.drawImage(liamSnakeImage, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                } else { // body of snake
                    g.setColor(GameSettings.getSnakeColor());
                    //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); // random snake color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free",Font.BOLD, 25)); // current/max score and level
            FontMetrics metricsScore = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metricsScore.stringWidth("Score : "+applesEaten)) / 2, g.getFont().getSize()); // middle
            g.setColor(Color.CYAN);
            g.setFont(new Font("Ink Free",Font.BOLD, 25));
            FontMetrics metricsHighScore = getFontMetrics(g.getFont());
            g.drawString("High Score: "+highScore, (SCREEN_WIDTH - metricsHighScore.stringWidth("Score : "+highScore)) / 2 - 185, g.getFont().getSize()); // left
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 25));
            FontMetrics metricsLevel = getFontMetrics(g.getFont());
            g.drawString("Level: " + (currentLevel + 1), (SCREEN_WIDTH - metricsLevel.stringWidth("Level: " + currentLevel + 1)) / 2 + 125, g.getFont().getSize()); //right
        } else {
            gameOver(g); 
        }   
    }
    public void newApple() { 
        // generate coords of new apple
        do {
            appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } while (isOnSnake(appleX, appleY));
    }
    private boolean isOnSnake(int x, int y) {
        // doesn't let apple be placed where snake body is
        for (int i = 0; i < bodyParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }
    public void move() {
        // shifting body parts of snake
        for (int i = bodyParts; i > 0; i--) { 
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        // change direction of snake
        switch(direction) { 
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    public void checkApple() {
        // head postion == apple postion
        if ((x[0] == appleX) && (y[0] == appleY)) { 
            bodyParts++;
            applesEaten++;
            highScore = Math.max(highScore, applesEaten);
            if ((applesEaten % 10 )== 0 && DELAY > 40) { // every 10 apples eaten, increase delay as long as it greater than 30
                currentLevel++;
                DELAY -= 10;
                timer.stop();
                timer = new Timer(DELAY, this);
                timer.start();
                showNewLevelText();
            }
            newApple();
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
        if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
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
        applesEaten = 0;
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
        g.drawString("Game Over", (SCREEN_WIDTH - metricsGameOver.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 3 + 50);
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free",Font.BOLD, 25)); 
        
        // current/max score and level
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metricsScore.stringWidth("Score : "+applesEaten)) / 2, g.getFont().getSize()); // middle
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free",Font.BOLD, 25));
        FontMetrics metricsHighScore = getFontMetrics(g.getFont());
        g.drawString("High Score: "+highScore, (SCREEN_WIDTH - metricsHighScore.stringWidth("Score : "+highScore)) / 2 - 185, g.getFont().getSize()); // left
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        FontMetrics metricsLevel = getFontMetrics(g.getFont());
        g.drawString("Level: " + (currentLevel + 1), (SCREEN_WIDTH - metricsLevel.stringWidth("Level: " + currentLevel + 1)) / 2 + 125, g.getFont().getSize()); //right

        // play again button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 85));
        FontMetrics metricsPlayAgain = getFontMetrics(g.getFont());
        retryButtonX = (SCREEN_WIDTH - metricsPlayAgain.stringWidth("Play Again")) / 2;
        retryButtonY = (SCREEN_HEIGHT / 2 + metricsPlayAgain.getHeight());
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
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
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
        }
    }

}