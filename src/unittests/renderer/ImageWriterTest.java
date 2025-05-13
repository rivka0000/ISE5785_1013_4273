package unittests.renderer; 
import renderer.*;
import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit test for the {@link renderer.ImageWriter} class.
 * This test creates an image with a uniform background and a grid overlay.
 */
public class ImageWriterTest {

    /**
     * Test method for {@link renderer.ImageWriter#writePixel(int, int, Color)} and
     * {@link renderer.ImageWriter#writeToImage(String)}.
     * <p>
     * This test creates an image with a light blue background and a red grid of 16x10 squares.
     * It verifies that the {@code writePixel} method correctly sets the pixel colors
     * and that the {@code writeToImage} method outputs the image to a file.
     * </p>
     * <p>
     * The resulting image is saved as "ImageWriterTest_Grid.png" in the output directory.
     * </p>
     */
    @Test
    void testImageWriterWithGrid() {
        int width = 800;
        int height = 500;
        int gridColumns = 16;
        int gridRows = 10;
        int cellWidth = width / gridColumns;
        int cellHeight = height / gridRows;

        // Define background and grid colors
        Color backgroundColor = new Color(173, 216, 230); // Light blue
        Color gridColor = new Color(255, 0, 0); // Red

        // Create ImageWriter instance
        ImageWriter writer = new ImageWriter(width, height);

        // Fill image with background color and draw grid lines
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x % cellWidth == 0 || y % cellHeight == 0) {
                    // Draw red grid line
                    writer.writePixel(x, y, gridColor);
                } else {
                    // Fill background
                    writer.writePixel(x, y, backgroundColor);
                }
            }
        }

        // Output the image to file
        writer.writeToImage("ImageWriterTest_Grid");
    }
}