/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;
import geometries.Sphere;
import primitives.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link geometries.Sphere} class
 * 
 * @author Ahuva Winkler  
 */
class SphereTests {

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);
        Point p = new Point(0, 0, 1);
        Vector expected = new Vector(0, 0, 1);

        // TC01: The normal vector should point from the center to the surface point
        assertEquals(expected, sphere.getNormal(p), "Wrong normal from Sphere");

        // TC02: The normal vector should have unit length
        assertEquals(1, sphere.getNormal(p).length(), 0.00001, "Normal vector is not unit length");
    }
}
