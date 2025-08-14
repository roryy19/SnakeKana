import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ExitOverlay extends JPanel{

    private final HomeScreen homeScreen;

    private int yesButtonX;
    private int yesButtonY;
    private int yesButtonWidth;
    private int yesButtonHeight;

    private int noButtonX;
    private int noButtonY;
    private int noButtonWidth;
    private int noButtonHeight;

    public ExitOverlay(HomeScreen homeScreen) {
        this.homeScreen = homeScreen;
        setOpaque(false);
        setFocusable(false);
        setVisible(false);
        setLayout(null); 

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        drawText(g);
    }

    public void drawText(Graphics g) {
        // exit game text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD, 100));
        FontMetrics metricsExitGame = getFontMetrics(g.getFont());
        g.drawString("Exit Game?", (getWidth() - metricsExitGame.stringWidth("Exit Game?")) / 2, (getHeight() / 2) - 200);

        // yes button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsYes = getFontMetrics(g.getFont());
        yesButtonX = (getWidth() - metricsYes.stringWidth("Yes")) / 2;
        yesButtonY = (getHeight() / 2);
        yesButtonWidth = metricsYes.stringWidth("Yes"); // width of text
        yesButtonHeight = metricsYes.getHeight();   // height of text
        g.drawString("Yes", yesButtonX, yesButtonY);
    
        // no button
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsNo = getFontMetrics(g.getFont());
        noButtonX = (getWidth() - metricsNo.stringWidth("No")) / 2;
        noButtonY = (getHeight() / 2) + 200;
        noButtonWidth = metricsNo.stringWidth("No"); // width of text
        noButtonHeight = metricsNo.getHeight();   // height of text
        g.drawString("No", noButtonX, noButtonY);
    }

    public void checkClick(int x, int y) {
        //yes button
        if (withinBounds(x, y, yesButtonX, yesButtonY, yesButtonWidth, yesButtonHeight)) {
            setVisible(false);
            homeScreen.exitGame(); // resumes timer, etc.
        }
        // no button
        if (withinBounds(x, y, noButtonX, noButtonY, noButtonWidth, noButtonHeight)) {
            homeScreen.resumeHome(); // switch screen
        }
    }
    private boolean withinBounds(int x, int y, int btnX, int btnY, int width, int height) {
        return x >= btnX && x <= btnX + width && y >= btnY - height && y <= btnY;
    }
}
