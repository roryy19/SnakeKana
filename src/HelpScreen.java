import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HelpScreen extends JPanel{

    private int backButtonX;
    private int backButtonY;
    private int backButtonWidth;
    private int backButtonHeight;

    private Image backgroundImage;

    public HelpScreen(JFrame frame) {
        setPreferredSize(new Dimension(1300, 1300));

        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/images/background1.jpg"));
        backgroundImage = backgroundIcon.getImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
    } 

    private void checkClick(int x, int y) {
        // home button
        if (x >= backButtonX && x <= (backButtonX + backButtonWidth) && 
            y >= backButtonY && y <= (backButtonY + backButtonHeight)) {
            SoundManager.getInstance().playButtonClick("/res/sound/button_click_sound.wav");
            startBack();
        }
    }

    private void startBack() {
        Container container = this.getParent();
        while (container != null && !(container instanceof JScrollPane)) {
            container = container.getParent();
        }

        if (container instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) container;
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            topFrame.remove(scrollPane);

            MenuScreen menuScreen = new MenuScreen(topFrame);
            topFrame.add(menuScreen);
            topFrame.pack();
            topFrame.setLocationRelativeTo(null);
            topFrame.revalidate();
            topFrame.repaint();
            menuScreen.requestFocusInWindow();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // background
        drawBackButton(g);
        drawHelpText(g);

        // top divider line
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 100, getWidth(), 5);
    }

    public void drawBackButton(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsBack = g.getFontMetrics();
        backButtonX = 10; 
        backButtonY = 70; 
        backButtonWidth = metricsBack.stringWidth("Back"); 
        backButtonHeight = metricsBack.getHeight(); 
        g.drawString("Back", backButtonX, backButtonY); 
        backButtonY = backButtonY - metricsBack.getAscent(); 
    } 
    
    public void drawHelpText(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 35));

        g.drawString("Snake Game:", 10, 140);
        g.drawString("Eat the correct characters.", 350, 140);

        g.drawString("Goal:", 10, 210);
        g.drawString("Choose all correct kana (based on mode).", 350, 210);

        g.drawString("Accuracy:", 10, 280);
        g.drawString("Correct / Total chosen characters.", 350, 280);

        g.drawString("No Death Mode:", 10, 350);
        g.drawString("Go through your body and screen edges.", 350, 350);

        g.drawString("Infinite Mode:", 10, 420);
        g.drawString("Play forever unless you die or quit.", 350, 420);

        g.drawString("\"Wrong\" Kana:", 10, 490);
        g.drawString("Adds up to 10 fake kana to confuse you.", 350, 490);

        g.drawString("Snake Speed:", 10, 560);
        g.drawString("1 = slowest, 10 = fastest.", 350, 560);

        g.drawString("Kana Quiz:", 10, 630);
        g.drawString("Choose Hiragana or Katakana.", 350, 630);

        g.drawString("Answer:", 10, 700);
        g.drawString("Type in English and hit Enter or Submit.", 350, 700);

        g.drawString("Skip:", 10, 770);
        g.drawString("Removes kana from current quiz.", 350, 770);

        g.drawString("Hint (First Letter):", 10, 840);
        g.drawString("Shows first letter briefly, does not remove from quiz.", 350, 840);

        g.drawString("Hint (Full Kana):", 10, 910);
        g.drawString("Shows full kana briefly, does not remove from quiz.", 350, 910);
    }
}
