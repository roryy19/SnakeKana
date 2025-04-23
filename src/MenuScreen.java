import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScreen extends JPanel {
    
    private JFrame frame;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;

    private JCheckBox noDeathCheckBox;
    private JCheckBox infiniteCheckBox;

    private JSlider speedSlider;
    private JSlider kanaSlider;

    boolean selectedND;
    boolean selectedInf;

    private Image backgroundImage;

    private SoundManager soundManager;
    

    public MenuScreen(JFrame frame, SoundManager soundManager) {
        this.frame = frame;
        this.soundManager = soundManager;
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
            1, "Snake Speed", 50, 800, 
            GameSettings.getSnakeSpeedSliderValue(),
            GameSettings::setSnakeSpeedSliderValue
        );
        add(speedSlider);

        kanaSlider = createCustomSlider(
            0, "Amount of \"Wrong\" Kana", 50, 650, 
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
            soundManager.playButtonClick("/res/sound/button_click_sound.wav");
        });
    
        return box;
    }
    
    private JSlider createCustomSlider(int start, String label, int x, int y, int value, java.util.function.IntConsumer onValueChanged) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, start, 10, value); // goes from 1 to 10, default is 5

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

        // sound only when released (less repetitive)
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                soundManager.playButtonClick("/res/sound/button_click_sound.wav");
            }
        });

        return slider;
    }

    private void checkClick(int x, int y) {
        // home button
        if (x >= homeButtonX && x <= (homeButtonX + homeButtonWidth) && 
            y >= homeButtonY && y <= (homeButtonY + homeButtonHeight)) {
            soundManager.playButtonClick("/res/sound/button_click_sound.wav");
            startHome();
        }
    }

    private void startHome() {
        frame.remove(this); // remove menu screen
        HomeScreen homeScreen = new HomeScreen(frame, soundManager);
        frame.add(homeScreen);
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawHomeButton(g); // home button

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 170, getWidth(), 5);
    }

    public void drawHomeButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsHome = g.getFontMetrics();
        homeButtonX = 10; 
        homeButtonY = 70; 
        homeButtonWidth = metricsHome.stringWidth("Home"); 
        homeButtonHeight = metricsHome.getHeight(); 
        g.drawString("Home", homeButtonX, homeButtonY); 
        homeButtonY = homeButtonY - metricsHome.getAscent(); 
    } 
}