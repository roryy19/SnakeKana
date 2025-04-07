import java.awt.*;

public class GameSettings {
    private static Color snakeColor = new Color(23, 102, 31); // default color
    private static boolean noDeathMode = false;

    // snake color
    public static void setSnakeColor(Color newColor) {
        snakeColor = newColor;
    }
    public static Color getSnakeColor() {
        return snakeColor;
    }

    // No Death mode
    public static void setDeathMode(boolean value) {
        noDeathMode = value;
    }
    public static boolean isNoDeathMode() {
        return noDeathMode;
    }
}
