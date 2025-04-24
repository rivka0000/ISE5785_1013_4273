package unittests.geometries;

import geometries.Plane;
import primitives.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Plane} class
 * 
 * @author Ahuva Winkler
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
}
