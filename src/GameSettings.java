import java.awt.*;

public class GameSettings {
    private static Color snakeColor = new Color(23, 102, 31); // default color

    // set snake color
    public static void setSnakeColor(Color newColor) {
        snakeColor = newColor;
    }
    // get snake color
    public static Color getSnakeColor() {
        return snakeColor;
    }
}
