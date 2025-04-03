import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 1000;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // amount of units that can fit on screen
    int DELAY = 80; // lower = faster snake
    
    // body of snake
    final int x[] = new int[GAME_UNITS]; // snake wont be bigger than game
    final int y[] = new int[GAME_UNITS]; 
    int bodyParts = 6; // initial # of body parts
    
    int applesEaten;    
    int appleX;
    int appleY;

    int retryButtonX;
    int retryButtonY;
    int retryButtonWidth;
    int retryButtonHeight;
    
    char direction = 'R'; // snake begins game going right

    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
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

        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        if (running) {
            // draw apple
            g.setColor(Color.RED);
            //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); // random apple color
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // iterate through all body parts of snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // head of snake
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else { // body of snake
                    g.setColor(new Color(45,180,0));
                    //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); // random snake color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free",Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score : "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score : "+applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g); 
        }   
    }
    public void newApple() { 
        // generate coords of new apple
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE; 
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
            newApple();
        }
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
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }
    public void checkClick(int x, int y) {
        // play again button
        if (!running) { // top left origin
            if (x >= retryButtonX && x <= (retryButtonX + retryButtonWidth) // within x coords
                && y >= retryButtonY && y <= (retryButtonY + retryButtonHeight)) { // within y coords
                resetGame();
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

        startGame();
    }
    public void gameOver(Graphics g) {
        // game over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metricsGameOver.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 3);
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free",Font.BOLD, 20));
        FontMetrics metricsScoreEnd = getFontMetrics(g.getFont());
        g.drawString("Score : "+applesEaten, (SCREEN_WIDTH - metricsScoreEnd.stringWidth("Score : "+applesEaten)) / 2, g.getFont().getSize());

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
    }
    @Override
    public void actionPerformed(ActionEvent key) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent key) {
            // check arrow keys or WASD
            switch (key.getKeyCode()) { 
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