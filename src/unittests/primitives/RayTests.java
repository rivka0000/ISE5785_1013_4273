package unittests.primitives;

import primitives.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Ray#getPoint(double)} method.
 */
public class RayTests {

    /**
     * Test method for {@link Ray#getPoint(double)}.
     * This test checks:
     * <ul>
     *     <li>Positive distance (Equivalence Partition)</li>
     *     <li>Negative distance (Equivalence Partition)</li>
     *     <li>Zero distance (Boundary Value)</li>
     * </ul>
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============

        // positive distance
        assertEquals(new Point(3, 0, 0), ray.getPoint(2), "Incorrect calculation of a point with positive distance");

        // negative distance
        assertEquals(new Point(-1, 0, 0), ray.getPoint(-2), "Incorrect calculation of a point with negative distance");

        // =============== Boundary Values Tests ==================

        // t = 0
        assertEquals(new Point(1, 0, 0), ray.getPoint(0), "Incorrect calculation of a point with t=0 (origin)");
    }
}
