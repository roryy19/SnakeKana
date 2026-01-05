import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Centralized positioning utilities for anchor-based layout.
 * Provides methods to position UI elements relative to screen edges
 * without scaling - elements keep their original pixel sizes.
 */
public class ScreenHelper {
    public static final int BASE_WIDTH = 900;
    public static final int BASE_HEIGHT = 900;

    private static int currentWidth = BASE_WIDTH;
    private static int currentHeight = BASE_HEIGHT;

    /**
     * Update the current screen dimensions.
     * Called when window is resized or fullscreen is toggled.
     */
    public static void updateDimensions(int width, int height) {
        currentWidth = width;
        currentHeight = height;
    }

    public static int getWidth() {
        return currentWidth;
    }

    public static int getHeight() {
        return currentHeight;
    }

    // ==================== Anchor Calculations ====================

    /**
     * Center an element horizontally.
     * @param elementWidth Width of the element to center
     * @return X coordinate for centered position
     */
    public static int centerX(int elementWidth) {
        return (currentWidth - elementWidth) / 2;
    }

    /**
     * Center an element vertically.
     * @param elementHeight Height of the element to center
     * @return Y coordinate for centered position
     */
    public static int centerY(int elementHeight) {
        return (currentHeight - elementHeight) / 2;
    }

    /**
     * Position element from the right edge.
     * @param margin Distance from right edge
     * @param elementWidth Width of the element
     * @return X coordinate
     */
    public static int fromRight(int margin, int elementWidth) {
        return currentWidth - margin - elementWidth;
    }

    /**
     * Position element from the bottom edge.
     * @param margin Distance from bottom edge
     * @param elementHeight Height of the element
     * @return Y coordinate
     */
    public static int fromBottom(int margin, int elementHeight) {
        return currentHeight - margin - elementHeight;
    }

    /**
     * Position element from the left edge.
     * @param margin Distance from left edge
     * @return X coordinate
     */
    public static int fromLeft(int margin) {
        return margin;
    }

    /**
     * Position element from the top edge.
     * @param margin Distance from top edge
     * @return Y coordinate
     */
    public static int fromTop(int margin) {
        return margin;
    }

    // ==================== Game Area Calculations ====================

    /**
     * Get X offset for centering the 900x900 game area.
     * @return X offset to apply to game coordinates
     */
    public static int getGameAreaX() {
        return (currentWidth - BASE_WIDTH) / 2;
    }

    /**
     * Get Y offset for centering the 900x900 game area.
     * @return Y offset to apply to game coordinates
     */
    public static int getGameAreaY() {
        return (currentHeight - BASE_HEIGHT) / 2;
    }

    /**
     * Check if we're in fullscreen mode (screen larger than base size).
     * @return true if current dimensions exceed base dimensions
     */
    public static boolean isLargerThanBase() {
        return currentWidth > BASE_WIDTH || currentHeight > BASE_HEIGHT;
    }
}
