package unittests.geometries;

import geometries.Plane;
import primitives.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for {@link geometries.Plane} class
 * 
 * @author Ahuva Winkler, Rivka Azrad
 */
class PlaneTests {

    /**
     * Test method for {@link geometries.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormalPoint() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: The normal vector should be of unit length
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);
        Plane pl = new Plane(p1, p2, p3);

        Vector normal = pl.getNormal(p1);
        assertEquals(1, normal.length(), 0.00001, "Normal vector is not unit length");

        // TC02: The normal should be orthogonal to both vectors on the plane
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        assertEquals(0, normal.dotProduct(v1), 0.00001, "Normal is not orthogonal to vector1");
        assertEquals(0, normal.dotProduct(v2), 0.00001, "Normal is not orthogonal to vector2");
    }

    /**
     * Test method for {@link geometries.Plane#getNormal()}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal returned should match the one given in constructor (normalized)
        Point q0 = new Point(0, 0, 1);
        Vector n = new Vector(0, 0, 1);
        Plane pl = new Plane(q0, n);
        assertEquals(n.normalize(), pl.getNormal(), "Wrong normal returned");

        // TC02: Normal vector should be of unit length
        assertEquals(1, pl.getNormal().length(), 0.00001, "Normal is not unit length");
    }
    
    /**
     * Test method for {@link geometries.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        // Plane: z = 0 (XY plane)
        Plane plane = new Plane(
            new Point(0, 0, 0),
            new Vector(0, 0, 1)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the plane (1 point)
        Ray ray1 = new Ray(new Point(0, 0, 1), new Vector(0, 0, -1));
        Point expected = new Point(0, 0, 0);
        var result1 = plane.findIntersections(ray1);
        assertNotNull(result1, "Ray should intersect plane");
        assertEquals(1, result1.size(), "Wrong number of points");
        assertEquals(List.of(expected), result1, "Ray intersects plane at wrong point");

        // TC02: Ray does not intersect the plane (ray goes away)
        Ray ray2 = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        assertNull(plane.findIntersections(ray2), "Ray goes away from plane");

        // =============== Boundary Values Tests ==================

        // TC11: Ray is parallel and outside the plane – no intersection
        Ray ray3 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0));
        assertNull(plane.findIntersections(ray3), "Ray is parallel and above plane");

        // TC12: Ray is parallel and included in the plane – no intersection
        Ray ray4 = new Ray(new Point(1, 1, 0), new Vector(1, 1, 0));
        assertNull(plane.findIntersections(ray4), "Ray lies in plane");

        // TC13: Ray is orthogonal and starts before the plane (1 point)
        Ray ray5 = new Ray(new Point(0, 0, 1), new Vector(0, 0, -1));
        assertEquals(List.of(new Point(0, 0, 0)), plane.findIntersections(ray5), "Orthogonal ray from above");

        // TC14: Ray is orthogonal and starts on the plane – no intersection
        Ray ray6 = new Ray(new Point(0.1, 0.1, 0), new Vector(0, 0, -1));
        assertNull(plane.findIntersections(ray6), "Orthogonal ray from plane");

        // TC15: Ray is orthogonal and starts after the plane – no intersection
        Ray ray7 = new Ray(new Point(0, 0, -1), new Vector(0, 0, -1));
        assertNull(plane.findIntersections(ray7), "Orthogonal ray away from plane");

        // TC16: Ray starts at the intersection point – no intersection (head-on)
        Ray ray8 = new Ray(new Point(0.1, 0.1, 0), new Vector(0, 0, -1));
        assertNull(plane.findIntersections(ray8), "Ray starts at intersection point on plane");
    }

}
