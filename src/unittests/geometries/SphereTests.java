/**
 * 
 */
package unittests.geometries;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import geometries.Sphere;
import primitives.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link geometries.Sphere} class
 * 
 * @author Ahuva Winkler , Rivka Azrad 
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
    
    /** A point used in some tests */ 
    private final Point  p001 = new Point(0, 0, 1); 
    /** A point used in some tests */ 
    private final Point  p100 = new Point(1, 0, 0); 
    /** A vector used in some tests */ 
    private final Vector v001 = new Vector(0, 0, 1); 

    /** 
     * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}. 
     */ 
    @Test 
    public void testFindIntersections() { 
       Sphere       sphere = new Sphere(p100, 1d); 

       final Point  gp1    = new Point(0.0651530771650466, 0.355051025721682, 0); 
       final Point  gp2    = new Point(1.53484692283495, 0.844948974278318, 0); 
       final var    exp    = List.of(gp1, gp2); 

       final Vector v310   = new Vector(3, 1, 0); 
       final Vector v110   = new Vector(1, 1, 0); 

       final Point  p01    = new Point(-1, 0, 0); 

       // ============ Equivalence Partitions Tests ============== 
       // TC01: Ray's line is outside the sphere (0 points) 
       assertNull(sphere.findIntersections(new Ray(p01, v110)), "Ray's line out of sphere"); 

       // TC02: Ray starts before and crosses the sphere (2 points) 
       final var result1 = sphere.findIntersections(new Ray(p01, v310)); 
       assertNotNull(result1, "Can't be empty list"); 
       assertEquals(2, result1.size(), "Wrong number of points"); 
       assertEquals(exp, result1, "Ray crosses sphere"); 

    // TC03: Ray starts inside the sphere (1 point)
       Point pIn = new Point(0.5, 0, 0);  // Start point of the ray inside the sphere
       Vector vOut = new Vector(1, 1, 0);  // Direction of the ray (will be normalized internally)
       Point gpIn = new Point(1.4114378277661475, 0.9114378277661476, 0.0);  // Correct intersection point after normalization

       assertEquals(List.of(gpIn), sphere.findIntersections(new Ray(pIn, vOut)), "Ray from inside sphere");

       // TC04: Ray starts after the sphere (0 points) 
       assertNull(sphere.findIntersections(new Ray(new Point(3, 1, 0), v310)), "Ray starts after sphere");

       // =============== Boundary Values Tests ================== 

       // **** Group 1: Ray's line crosses the sphere (but not the center) 
       // TC11: Ray starts at sphere and goes inside (1 point) 
       assertEquals(List.of(gp2), sphere.findIntersections(new Ray(gp1, v310)), "Ray starts at surface and enters");

       // TC12: Ray starts at sphere and goes outside (0 points) 
       assertNull(sphere.findIntersections(new Ray(gp1, new Vector(-3, -1, 0))), "Ray starts at surface and exits");

       // **** Group 2: Ray's line goes through the center 
       // TC21: Ray starts before the sphere (2 points) 
       Point gp3 = new Point(0, 0, 0); 
       Point gp4 = new Point(2, 0, 0); 
       var exp2 = List.of(gp3, gp4); 
       var res2 = sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0))); 
       assertEquals(2, res2.size(), "Wrong number of points"); 
       assertEquals(exp2, res2, "Ray through center");

       // TC22: Ray starts at sphere and goes inside (1 point) 
       Point p22 = new Point(2, 0, 0); 
       Point gp22 = new Point(0, 0, 0); 
       assertEquals(List.of(gp22), sphere.findIntersections(new Ray(p22, new Vector(-1, 0, 0))), "Ray from surface to center");

       // TC23: Ray starts inside (1 point) 
       Point p23 = new Point(1.5, 0, 0); 
       Point gp23 = new Point(2, 0, 0); 
       assertEquals(List.of(gp23), sphere.findIntersections(new Ray(p23, new Vector(1, 0, 0))), "Ray from inside through center");

       // TC24: Ray starts at the center (1 point) 
       Point gp24 = new Point(2, 0, 0); 
       assertEquals(List.of(gp24), sphere.findIntersections(new Ray(p100, new Vector(1, 0, 0))), "Ray from center outward");

       // TC25: Ray starts at sphere and goes outside (0 points) 
       assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 0))), "Ray starts on surface and exits");

       // TC26: Ray starts after sphere (0 points) 
       assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))), "Ray after sphere along diameter");

       // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points) 
       // TC31: Ray starts before the tangent point 
       assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))), "Ray before tangent");

       // TC32: Ray starts at the tangent point 
       assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))), "Ray at tangent");

       // TC33: Ray starts after the tangent point 
       assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))), "Ray after tangent");

       // **** Group 4: Special cases 
       // TC41: Ray's line is outside sphere, ray is orthogonal to ray start to sphere's center line 
       assertNull(sphere.findIntersections(new Ray(new Point(0, 0, 3), new Vector(1, 0, 0))), "Ray orthogonal outside sphere");

    // TC42: Ray starts inside the sphere, orthogonal to the radius – expecting no intersections
       assertNull(sphere.findIntersections(
           new Ray(new Point(0.5, 0, 0), new Vector(0, 1, 0))),
           "Ray inside orthogonal to radius");

    }

}
