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

    private JLabel kanaLabel;
    private JLabel speedLabel;
    private JLabel sfxLabel;
    private JLabel musicLabel;

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
            GameSettings.isNoDeathMode(),
            () -> GameSettings.setDeathMode(noDeathCheckBox.isSelected()),
            checkedIcon, uncheckedIcon
        );
        add(noDeathCheckBox);

        // Infinite mode check box
        infiniteCheckBox = createCustomCheckbox(
            "Infinite Mode",
            GameSettings.isInfiniteMode(),
            () -> GameSettings.setInfiniteMode(infiniteCheckBox.isSelected()),
            checkedIcon, uncheckedIcon
        );
        add(infiniteCheckBox);

        kanaSlider = createCustomSlider(
            0, "Amount of \"Wrong\" Kana",
            GameSettings.getWrongKanaAmount(),
            GameSettings::setWrongKanaAmount
        );
        kanaLabel = createSliderLabel("Amount of \"Wrong\" Kana");
        add(kanaLabel);
        add(kanaSlider);

        // Snake speed slider
        speedSlider = createCustomSlider(
            1, "Snake Speed",
            GameSettings.getSnakeSpeedSliderValue(),
            GameSettings::setSnakeSpeedSliderValue
        );
        speedLabel = createSliderLabel("Snake Speed");
        add(speedLabel);
        add(speedSlider);

        // SFX and music volume slider
        sfxSlider = createCustomSlider(
            0, "Sound Effects Volume",
            (int)(SoundManager.getInstance().getSFXVolume() * 10),
            (val) -> SoundManager.getInstance().setSFXVolume(val / 10f)
        );
        sfxLabel = createSliderLabel("Sound Effects Volume");
        add(sfxLabel);
        add(sfxSlider);

        musicSlider = createCustomSlider(
            0, "Music Volume",
            (int)(MusicManager.getInstance().getVolume() * 10),
            (val) -> MusicManager.getInstance().setVolume(val / 10f)
        );
        musicLabel = createSliderLabel("Music Volume");
        add(musicLabel);
        add(musicSlider);

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
    }

    @Override
    public Dimension getPreferredSize() {
        if (GameSettings.isFullscreen()) {
            return new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight());
        }
        return new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
    }

    private void layoutComponents() {
        int centerX = ScreenHelper.centerX(0);

        // Checkboxes - anchor to top-right corner (wider width to show full text)
        int checkboxWidth = 400;
        int checkboxX = ScreenHelper.fromRight(30, checkboxWidth);
        noDeathCheckBox.setBounds(checkboxX, 10, checkboxWidth, 80);
        infiniteCheckBox.setBounds(checkboxX, 90, checkboxWidth, 80);

        // Sliders - center horizontally
        int sliderWidth = 600;
        int sliderX = ScreenHelper.centerX(sliderWidth);

        kanaSlider.setBounds(sliderX, 225, sliderWidth, 80);
        kanaLabel.setBounds(sliderX, 165, sliderWidth, 80);

        speedSlider.setBounds(sliderX, 375, sliderWidth, 80);
        speedLabel.setBounds(sliderX, 315, sliderWidth, 80);

        sfxSlider.setBounds(sliderX, 525, sliderWidth, 80);
        sfxLabel.setBounds(sliderX, 465, sliderWidth, 80);

        musicSlider.setBounds(sliderX, 675, sliderWidth, 80);
        musicLabel.setBounds(sliderX, 615, sliderWidth, 80);
    }

    private JLabel createSliderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Ink Free", Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JCheckBox createCustomCheckbox(String label, boolean selected, Runnable onToggle, Icon checkedIcon, Icon uncheckedIcon) {
        JCheckBox box = new JCheckBox(label);
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

    private JSlider createCustomSlider(int start, String label, int value, java.util.function.IntConsumer onValueChanged) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, start, 10, value);
        slider.setFont(new Font("Ink Free", Font.BOLD, 40));
        slider.setOpaque(false);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

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
            y >= helpButtonY + 25 && y <= (helpButtonY + helpButtonHeight + 25)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startHelp();
        }
    }

    private void startHome() {
        frame.remove(this); // remove menu screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        homeScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    private void startKeybinds() {
        frame.remove(this); // remove menu screen
        KeybindsScreen keybindsScreen = new KeybindsScreen(frame);
        frame.add(keybindsScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        keybindsScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    private void startSongCredits() {
        frame.remove(this); // remove menu screen
        SongCreditsScreen songCreditsScreen = new SongCreditsScreen(frame);
        frame.add(songCreditsScreen);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
        }
        songCreditsScreen.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }

    private void startHelp() {
        frame.remove(this); // remove menu screen
        HelpScreen helpScreen = new HelpScreen(frame);

        JScrollPane scrollPane = new JScrollPane(
            helpScreen,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        if (GameSettings.isFullscreen()) {
            scrollPane.setPreferredSize(new Dimension(ScreenHelper.getWidth(), ScreenHelper.getHeight()));
        } else {
            scrollPane.setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        }

        // smoother scrolling
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(8);

        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);

        frame.add(scrollPane);
        if (!GameSettings.isFullscreen()) {
            frame.pack();
            frame.setLocationRelativeTo(null); // center again
        }
        frame.revalidate();
        helpScreen.requestFocusInWindow();
        frame.repaint();
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
        // Anchor to top-left
        homeButtonX = ScreenHelper.fromLeft(10);
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
        keybindButtonWidth = metricsKeybind.stringWidth("Keybinds");
        keybindButtonHeight = metricsKeybind.getHeight();
        // Anchor to bottom-left with more margin
        keybindButtonX = ScreenHelper.fromLeft(25);
        keybindButtonY = ScreenHelper.fromBottom(70, 0);
        g.drawString("Keybinds", keybindButtonX, keybindButtonY);
        keybindButtonY = keybindButtonY - metricsKeybind.getAscent();
    }

    public void drawSongCreditsButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 45));
        FontMetrics metricsCredits = g.getFontMetrics();
        creditsButtonWidth = metricsCredits.stringWidth("Song Credits");
        creditsButtonHeight = metricsCredits.getHeight();
        // Center horizontally at bottom with more margin
        creditsButtonX = ScreenHelper.centerX(creditsButtonWidth);
        creditsButtonY = ScreenHelper.fromBottom(70, 0);
        g.drawString("Song Credits", creditsButtonX, creditsButtonY);
        creditsButtonY = creditsButtonY - metricsCredits.getAscent();
    }

    public void drawHelpButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 45));
        FontMetrics metricsHelp = g.getFontMetrics();
        helpButtonWidth = metricsHelp.stringWidth("How to Play");
        helpButtonHeight = metricsHelp.getHeight();
        // Anchor to bottom-right with more margin
        helpButtonX = ScreenHelper.fromRight(25, helpButtonWidth);
        helpButtonY = ScreenHelper.fromBottom(70, 0);
        g.drawString("How to Play", helpButtonX, helpButtonY);
        helpButtonY = helpButtonY - metricsHelp.getAscent();
    }
}
