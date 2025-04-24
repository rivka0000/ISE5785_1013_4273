/**
 * 
 */
package unittests.geometries;

import geometries.Cylinder;
import primitives.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Cylinder} class  
 * 
 * @author Ahuva Winkler
 */
class CylinderTests {

    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cyl = new Cylinder(axis, 2, 5);

        // TC01: Point on the side surface of the cylinder
        // Expected normal is perpendicular to the axis
        Point p1 = new Point(0, 2, 2);
        Vector expectedNormal1 = new Vector(0, 1, 0);
        Vector actualNormal1 = cyl.getNormal(p1);
        assertEquals(expectedNormal1, actualNormal1, "Bad normal to cylinder side");

        // =============== Boundary Values Tests ==================

        // TC02: Point on the bottom base of the cylinder
        // Expected normal is in the opposite direction of the axis
        Point p2 = new Point(1, 1, 0);
        Vector expectedNormal2 = new Vector(0, 0, -1);
        Vector actualNormal2 = cyl.getNormal(p2);
        assertEquals(expectedNormal2, actualNormal2, "Bad normal to bottom base");

        // TC03: Point on the top base of the cylinder
        // Expected normal is in the same direction as the axis
        Point p3 = new Point(1, 1, 5);
        Vector expectedNormal3 = new Vector(0, 0, 1);
        Vector actualNormal3 = cyl.getNormal(p3);
        assertEquals(expectedNormal3, actualNormal3, "Bad normal to top base");
    }
}