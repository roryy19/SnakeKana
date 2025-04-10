import java.awt.*;

public class GameSettings {
    private static Color snakeColor = new Color(23, 102, 31); // default color
    private static boolean noDeathMode = false;
    private static boolean infiniteMode = false;
    private static int snakeSpeedSliderValue = 5;
    private static int wrongKanaAmount = 3;

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

    // Infinite mode
    public static void setInfiniteMode(boolean value) {
        infiniteMode = value;
    }
    public static boolean isInfiniteMode() {
        return infiniteMode;
    }

    // Speed slider
    public static void setSnakeSpeedSliderValue(int sliderValue) {
        snakeSpeedSliderValue = sliderValue;
    }
    public static int getSnakeSpeedSliderValue() {
        return snakeSpeedSliderValue;
    }
    public static int getSnakeSpeed() {
        return 125 - (snakeSpeedSliderValue * 10); // convert to delay
    }

    // Wrong Kana Amount slider
    public static void setWrongKanaAmount(int value) {
        wrongKanaAmount = value;
    }
    public static int getWrongKanaAmount() {
        return wrongKanaAmount;
    }
}
