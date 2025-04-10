import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScreen extends JPanel {
    
    private JFrame frame;

    private int chooseX;
    private int chooseY;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;
    
    private int greenButtonX;
    private int greenButtonY;
    private int greenButtonWidth;
    private int greenButtonHeight;

    private int redButtonX;
    private int redButtonY;
    private int redButtonWidth;
    private int redButtonHeight;

    private int blueButtonX;
    private int blueButtonY;
    private int blueButtonWidth;
    private int blueButtonHeight;

    private JCheckBox noDeathCheckBox;
    private JCheckBox infiniteCheckBox;

    private JSlider speedSlider;
    private JSlider kanaSlider;

    boolean selectedND;
    boolean selectedInf;

    private Image backgroundImage;
    

    public MenuScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));

        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage = backgroundIcon.getImage();

        Icon checkedIcon = new ImageIcon(getClass().getResource("/res/images/checked_checkbox.png"));
        Icon uncheckedIcon = new ImageIcon(getClass().getResource("/res/images/unchecked_checkbox.png"));

        // No Death check box
        noDeathCheckBox = createCustomCheckbox(
            "No-Death Mode",
            GameConstants.SCREEN_WIDTH - 400, 20,
            GameSettings.isNoDeathMode(),
            () -> GameSettings.setDeathMode(noDeathCheckBox.isSelected()),
            checkedIcon, uncheckedIcon
        );
        add(noDeathCheckBox);

        // Infinite mode check box
        infiniteCheckBox = createCustomCheckbox(
            "Infinite Mode",
            GameConstants.SCREEN_WIDTH - 400, 100,
            GameSettings.isInfiniteMode(),
            () -> GameSettings.setInfiniteMode(infiniteCheckBox.isSelected()),
            checkedIcon, uncheckedIcon
        );
        add(infiniteCheckBox);

        // Snake speed slider
        speedSlider = createCustomSlider(
            "Snake Speed", 50, 800, 
            GameSettings.getSnakeSpeedSliderValue(),
            GameSettings::setSnakeSpeedSliderValue
        );
        add(speedSlider);

        kanaSlider = createCustomSlider(
            "Amount of \"Wrong\" Kana", 50, 650, 
            GameSettings.getWrongKanaAmount(),
            GameSettings::setWrongKanaAmount
        );
        add(kanaSlider);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
    }

    private JCheckBox createCustomCheckbox(String label, int x, int y, boolean selected, Runnable onToggle, Icon checkedIcon, Icon uncheckedIcon) {
        JCheckBox box = new JCheckBox(label);
        box.setBounds(x, y, 450, 80);
        box.setFont(new Font("Ink Free", Font.BOLD, 50));
        box.setOpaque(false);
        box.setFocusPainted(false);
        box.setVerticalTextPosition(SwingConstants.TOP);
        box.setSelected(selected);
        box.setIcon(selected ? checkedIcon : uncheckedIcon);
        box.setForeground(selected ? Color.WHITE : Color.RED);
    
        box.addActionListener(e -> {
            boolean isSelected = box.isSelected();
            box.setIcon(isSelected ? checkedIcon : uncheckedIcon);
            box.setForeground(isSelected ? Color.WHITE : Color.RED);
            onToggle.run(); // apply the setting change
        });
    
        return box;
    }
    
    private JSlider createCustomSlider(String label, int x, int y, int value, java.util.function.IntConsumer onValueChanged) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 10, value); // goes from 1 to 10, default is 5

        int centerX = (GameConstants.SCREEN_WIDTH - 800) / 2;
        slider.setBounds(x, y, 800, 100);
        slider.setFont(new Font("Ink Free", Font.BOLD, 50));
        slider.setOpaque(false);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JLabel speedLabel = new JLabel(label, SwingConstants.CENTER);
        speedLabel.setFont(new Font("Ink Free", Font.BOLD, 60));
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setBounds(centerX, y - 60, 800, 90); // Positioned above slider

        add(speedLabel);

        slider.addChangeListener(e -> {
            int sliderValue = slider.getValue();
            onValueChanged.accept(sliderValue);
        });

        return slider;
    }

    private void checkClick(int x, int y) {
        // home button
        if (x >= homeButtonX && x <= (homeButtonX + homeButtonWidth) && 
            y >= homeButtonY && y <= (homeButtonY + homeButtonHeight)) {
            startHome();
        }/*
        // snake green
        if (x >= greenButtonX && x <= (greenButtonX + greenButtonWidth) && 
            y >= greenButtonY && y <= (greenButtonY + greenButtonHeight)) {
            changeColor(new Color(23, 102, 31));
        }
        // snake red
        if (x >= redButtonX && x <= (redButtonX + redButtonWidth) && 
            y >= redButtonY && y <= (redButtonY + redButtonHeight)) {
            changeColor(Color.RED);
        }
        // snake blue
        if (x >= blueButtonX && x <= (blueButtonX + blueButtonWidth) && 
            y >= blueButtonY && y <= (blueButtonY + blueButtonHeight)) {
            changeColor(Color.BLUE);
        }*/
    }

    private void startHome() {
        frame.remove(this); // remove menu screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
    }

    private void changeColor(Color color) {
        GameSettings.setSnakeColor(color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawHomeButton(g); // home button
        drawSnakeColorButtons(g); // choose snake color buttons

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 170, getWidth(), 5);
    }

    public void drawHomeButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = 10; // center on x axis
        homeButtonY = 70; // center on y axis
        homeButtonWidth = metricsHome.stringWidth("Home"); // width of Play text
        homeButtonHeight = metricsHome.getHeight(); // height of play text
        g.drawString("Home", homeButtonX, homeButtonY); 
        homeButtonY = homeButtonY - metricsHome.getAscent(); // make Y coord top of text not middle for clicking
    } 

    private void drawSnakeColorButtons(Graphics g) {
        /*
        // choose snake text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metricsChoose = g.getFontMetrics();
        chooseX = (GameConstants.SCREEN_WIDTH - metricsChoose.stringWidth("Choose Snake Color:")) / 2;; 
        chooseY = 300; 
        g.drawString("Choose Snake Color:", chooseX, chooseY); 

        // green snake button
        g.setColor(new Color(23, 102, 31));
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsGreen = g.getFontMetrics();
        greenButtonX = (GameConstants.SCREEN_WIDTH - metricsGreen.stringWidth("Green")) / 2;; 
        greenButtonY = 400; 
        greenButtonWidth = metricsGreen.stringWidth("Green");
        greenButtonHeight = metricsGreen.getHeight(); 
        g.drawString("Green", greenButtonX, greenButtonY); 
        greenButtonY = greenButtonY - metricsGreen.getAscent(); 

        // red snake button
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsRed = g.getFontMetrics();
        redButtonX = (GameConstants.SCREEN_WIDTH - metricsRed.stringWidth("Red")) / 2;; 
        redButtonY = 500; 
        redButtonWidth = metricsRed.stringWidth("Red");
        redButtonHeight = metricsRed.getHeight(); 
        g.drawString("Red", redButtonX, redButtonY); 
        redButtonY = redButtonY - metricsRed.getAscent();

        // blue snake button
        g.setColor(Color.BLUE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsBlue = g.getFontMetrics();
        blueButtonX = (GameConstants.SCREEN_WIDTH - metricsBlue.stringWidth("Blue")) / 2;; 
        blueButtonY = 600; 
        blueButtonWidth = metricsBlue.stringWidth("Blue");
        blueButtonHeight = metricsBlue.getHeight(); 
        g.drawString("Blue", blueButtonX, blueButtonY); 
        blueButtonY = blueButtonY - metricsBlue.getAscent();
        */
    }
}