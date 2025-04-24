package unittests.primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for {@link primitives.Point} class
 * @author Ahuva Winkler
 */
class PointTests {

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Subtracting two different points → should return the vector between them
        Point p1 = new Point(2, 3, 4);
        Point p2 = new Point(1, 1, 1);
        Vector expected = new Vector(1, 2, 3);
        assertEquals(expected, p1.subtract(p2), "TC01: subtract() returned wrong result");

        // =============== Boundary Values Tests ==================

        // TC11: Subtracting a point from itself → should throw an exception (zero vector)
        assertThrows(IllegalArgumentException.class,
            () -> p1.subtract(p1),
            "TC11: subtract() should throw exception when subtracting the same point");
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Adding a vector to a point → should return the translated point
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(1, -1, 4);
        Point expected = new Point(2, 1, 7);
        assertEquals(expected, p.add(v), "TC01: add() returned wrong result");
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Calculating squared distance between two different points
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 3); // dx = 3, dy = 4, dz = 0
        double expected = 3 * 3 + 4 * 4; // 9 + 16 = 25
        assertEquals(expected, p1.distanceSquared(p2), 0.00001, "TC01: distanceSquared() returned wrong result");
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Calculating distance between two points
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 3);
        double expected = 5.0; // sqrt(25)
        assertEquals(expected, p1.distance(p2), 0.00001, "TC01: distance() returned wrong result");
    }
}
