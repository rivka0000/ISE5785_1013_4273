package unittests.renderer;


import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import renderer.BlackBoard;
import renderer.BlackBoard.BoardShape;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BlackBoard} class.
 */
class BlackBoardTests {

    /**
     * Test method for {@link BlackBoard#generateJitteredSamples} - checks that points are generated correctly.
     * Validates that all generated points lie within the unit disk.
     */
    @Test
    void testPointsAreWithinDisk() {
        Point center = new Point(0, 0, 0);
        Vector vRight = new Vector(1, 0, 0); // X axis
        Vector vUp = new Vector(0, 1, 0);    // Y axis
        double radius = 1.0;
        int numPoints = 100; // 10x10

        List<Point> samples = BlackBoard.generateJitteredSamples(center, vRight, vUp, radius, numPoints, BoardShape.CIRCLE);

        for (Point p : samples) {
            Vector fromCenter = p.subtract(center);
            double x = fromCenter.dotProduct(vRight.normalize());
            double y = fromCenter.dotProduct(vUp.normalize());
            double distanceSquared = x * x + y * y;

            assertTrue(distanceSquared <= radius * radius + 1e-10,
                    "Point outside disk: " + p);
        }
    }

    /**
     * Test method for {@link BlackBoard#generateJitteredSamples} - size of output list.
     */
    @Test
    void testPointCountWithinReasonableRange() {
        Point center = new Point(0, 0, 0);
        Vector vRight = new Vector(1, 0, 0);
        Vector vUp = new Vector(0, 1, 0);
        double radius = 1.0;
        int numPoints = 100;

        List<Point> points = BlackBoard.generateJitteredSamples(center, vRight, vUp, radius, numPoints, BoardShape.CIRCLE);

        assertNotNull(points, "Returned list should not be null");
        assertTrue(points.size() <= numPoints,
                "Too many points returned");
        assertTrue(points.size() >= 70,
                "Too many points were filtered out (expected at least 70)");
    }

    /**
     * Test method for invalid number of points (not a perfect square).
     */
    @Test
    void testInvalidNumPoints() {
        Point center = new Point(0, 0, 0);
        Vector vRight = new Vector(1, 0, 0);
        Vector vUp = new Vector(0, 1, 0);
        double radius = 1.0;

        assertThrows(IllegalArgumentException.class, () ->
                        BlackBoard.generateJitteredSamples(center, vRight, vUp, radius, 50, BoardShape.CIRCLE),
                "Expected exception for non-square number of points");
    }

    /**
     * Test method for return type is not null.
     */
    @Test
    void testNotNullReturn() {
        Point center = new Point(0, 0, 0);
        Vector vRight = new Vector(1, 0, 0);
        Vector vUp = new Vector(0, 1, 0);
        double radius = 2.0;
        int numPoints = 81;

        List<Point> result = BlackBoard.generateJitteredSamples(center, vRight, vUp, radius, numPoints, BoardShape.CIRCLE);
        assertNotNull(result, "Method should return non-null list");
    }
}
