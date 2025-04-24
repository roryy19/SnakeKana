import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScreen extends JPanel {
    
    private JFrame frame;

    private int homeButtonX;
    private int homeButtonY;
    private int homeButtonWidth;
    private int homeButtonHeight;

    private int keybindButtonX;
    private int keybindButtonY;
    private int keybindButtonWidth;
    private int keybindButtonHeight;

    private int creditsButtonX;
    private int creditsButtonY;
    private int creditsButtonWidth;
    private int creditsButtonHeight;

    private int helpButtonX;
    private int helpButtonY;
    private int helpButtonWidth;
    private int helpButtonHeight;

    private JCheckBox noDeathCheckBox;
    private JCheckBox infiniteCheckBox;

    private JSlider speedSlider;
    private JSlider kanaSlider;
    private JSlider sfxSlider;
    private JSlider musicSlider;

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

        kanaSlider = createCustomSlider(
            0, "Amount of \"Wrong\" Kana", 150, 225, 
            GameSettings.getWrongKanaAmount(),
            GameSettings::setWrongKanaAmount
        );
        add(kanaSlider);

        // Snake speed slider
        speedSlider = createCustomSlider(
            1, "Snake Speed", 150, 375, 
            GameSettings.getSnakeSpeedSliderValue(),
            GameSettings::setSnakeSpeedSliderValue
        );
        add(speedSlider);

        // SFX and music volume slider
        sfxSlider = createCustomSlider(
            0, "Sound Effects Volume", 150, 525, 
            (int)(SoundManager.getInstance().getSFXVolume() * 10),
            (val) -> SoundManager.getInstance().setSFXVolume(val / 10f)
        );
        add(sfxSlider);

        musicSlider = createCustomSlider(
            0, "Music Volume", 150, 675, 
            (int)(MusicManager.getInstance().getVolume() * 10),
            (val) -> MusicManager.getInstance().setVolume(val / 10f)
        );
        add(musicSlider);

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
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
        });
    
        return box;
    }
    
    private JSlider createCustomSlider(int start, String label, int x, int y, int value, java.util.function.IntConsumer onValueChanged) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, start, 10, value); // goes from 1 to 10, default is 5

        int centerX = (GameConstants.SCREEN_WIDTH - 600) / 2;
        slider.setBounds(x, y, 600, 80);
        slider.setFont(new Font("Ink Free", Font.BOLD, 40));
        slider.setOpaque(false);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JLabel speedLabel = new JLabel(label, SwingConstants.CENTER);
        speedLabel.setFont(new Font("Ink Free", Font.BOLD, 40));
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setBounds(centerX, y - 60, 600, 80); // Positioned above slider

        add(speedLabel);

        slider.addChangeListener(e -> {
            int sliderValue = slider.getValue();
            onValueChanged.accept(sliderValue);
        });

        // sound only when released (less repetitive)
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            }
        });

        return slider;
    }

    private void checkClick(int x, int y) {
        // home button
        if (x >= homeButtonX && x <= (homeButtonX + homeButtonWidth) && 
            y >= homeButtonY && y <= (homeButtonY + homeButtonHeight)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startHome();
        }
        // keybinds button
        if (x >= keybindButtonX && x <= (keybindButtonX + keybindButtonWidth) && 
            y >= keybindButtonY && y <= (keybindButtonY + keybindButtonHeight)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startKeybinds();
        }
        // song credits button
        if (x >= creditsButtonX && x <= (creditsButtonX + creditsButtonWidth) && 
            y >= creditsButtonY && y <= (creditsButtonY + creditsButtonHeight)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startSongCredits();
        }
        // help/how to play button
        if (x >= helpButtonX && x <= (helpButtonX + helpButtonWidth) && 
            y >= helpButtonY && y <= (helpButtonY + helpButtonHeight)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startHelp();
        }
    }

    private void startHome() {
        frame.remove(this); // remove menu screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        frame.pack();
        homeScreen.requestFocusInWindow();
        frame.validate();
    }

    private void startKeybinds() {
        frame.remove(this); // remove menu screen
        KeybindsScreen keybindsScreen = new KeybindsScreen(frame);
        frame.add(keybindsScreen);
        frame.pack();
        keybindsScreen.requestFocusInWindow();
        frame.validate();
    }

    private void startSongCredits() {
        frame.remove(this); // remove menu screen
        SongCreditsScreen songCreditsScreen = new SongCreditsScreen(frame);
        frame.add(songCreditsScreen);
        frame.pack();
        songCreditsScreen.requestFocusInWindow();
        frame.validate();
    }

    private void startHelp() {
        frame.remove(this); // remove menu screen
        HelpScreen helpScreen = new HelpScreen(frame);

        JScrollPane scrollPane = new JScrollPane(
            helpScreen,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        // smoother scrolling
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(8);

        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);

        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null); // center again
        frame.revalidate();
        helpScreen.requestFocusInWindow();
        frame.validate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawHomeButton(g); // home button
        drawKeybindsButton(g); 
        drawSongCreditsButton(g);
        drawHelpButton(g);

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 160, getWidth(), 5);

        // middle divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 450, getWidth(), 5);

        // bottom divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 775, getWidth(), 5);
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

    public void drawKeybindsButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 45));
        FontMetrics metricsKeybind = g.getFontMetrics();
        keybindButtonX = 10; 
        keybindButtonY = 850; 
        keybindButtonWidth = metricsKeybind.stringWidth("Keybinds"); 
        keybindButtonHeight = metricsKeybind.getHeight(); 
        g.drawString("Keybinds", keybindButtonX, keybindButtonY); 
        keybindButtonY = keybindButtonY - metricsKeybind.getAscent();
    }

    public void drawSongCreditsButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 45));
        FontMetrics metricsCredits = g.getFontMetrics();
        creditsButtonY = 850; 
        creditsButtonWidth = metricsCredits.stringWidth("Song Credits"); 
        creditsButtonX = (GameConstants.SCREEN_WIDTH - creditsButtonWidth) / 2;
        creditsButtonHeight = metricsCredits.getHeight(); 
        g.drawString("Song Credits", creditsButtonX, creditsButtonY); 
        creditsButtonY = creditsButtonY - metricsCredits.getAscent();
    }

    public void drawHelpButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 45));
        FontMetrics metricsHelp = g.getFontMetrics();
        helpButtonY = 850; 
        helpButtonWidth = metricsHelp.stringWidth("How to Play"); 
        helpButtonX = GameConstants.SCREEN_WIDTH - helpButtonWidth - 10;
        helpButtonHeight = metricsHelp.getHeight(); 
        g.drawString("How to Play", helpButtonX, helpButtonY); 
        helpButtonY = keybindButtonY - metricsHelp.getAscent();
    }
}