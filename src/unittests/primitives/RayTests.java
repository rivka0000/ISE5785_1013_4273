package unittests.primitives;

import primitives.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
    
    @Test
    void testFindClosestPoint() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        Point p2 = new Point(2, 2, 3); // closest
        Point p3 = new Point(3, 3, 3);
        Point p4 = new Point(4, 4, 4);

        // ============ Equivalence Partition Test ==============
        // TC01: the point in the middle of the list is the closest
        assertEquals(p2, ray.findClosestPoint(List.of(p3, p2, p4)), "TC01: Failed when middle point is closest");

        // =============== Boundary Value Tests ==================
        // TC02: the list is empty -> expect null
        assertNull(ray.findClosestPoint(List.of()), "TC02: Failed with empty list");

        // TC03: the point in the beginning of the list is the closest
        assertEquals(p2, ray.findClosestPoint(List.of(p2, p3, p4)), "TC03: Failed when first point is closest");

        // TC04: the point in the end of the list is the closest
        assertEquals(p2, ray.findClosestPoint(List.of(p4, p3, p2)), "TC04: Failed when last point is closest");
    }

}
