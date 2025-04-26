package unittests.geometries;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import geometries.Triangle;
import primitives.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link geometries.Triangle} class
 * 
 * @author Ahuva Winkler, Rivka Azrad
 */
class TriangleTests {

    /**
     * Test method for {@link geometries.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // Arrange: Create three non-collinear points
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);

        // Act: Create triangle and get normal
        Triangle triangle = new Triangle(p1, p2, p3);
        Vector normal = triangle.getNormal(p1);

        // TC01: The normal should not be null
        assertNotNull(normal, "getNormal() returned null");

        // TC02: The normal vector should have unit length
        assertEquals(1, normal.length(), 0.00001, "Normal vector is not unit length");

        // TC03: The normal should be orthogonal to two edge vectors of the triangle
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        assertEquals(0, normal.dotProduct(v1), 0.00001, "Normal is not orthogonal to vector1");
        assertEquals(0, normal.dotProduct(v2), 0.00001, "Normal is not orthogonal to vector2");

        // =============== Boundary Values Tests ==================

        // TC10: Check normal at a point very close to vertex
        Point nearVertex = new Point(0.0000001, 0, 1);
        Vector normalNearVertex = triangle.getNormal(nearVertex);
        assertEquals(1, normalNearVertex.length(), 0.00001, "Normal near vertex is not unit length");
        assertEquals(normal, normalNearVertex, "Normal near vertex is not consistent with normal at vertex");

        // TC11: Check normal at a point on an edge
        Point onEdge = new Point(0.5, 0.5, 0.5);
        Vector normalOnEdge = triangle.getNormal(onEdge);
        assertEquals(1, normalOnEdge.length(), 0.00001, "Normal on edge is not unit length");
        assertEquals(normal, normalOnEdge, "Normal on edge is not consistent with normal at vertex");
    }
    
    /**
     * Test method for {@link geometries.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        // Triangle in the XY plane
        Triangle triangle = new Triangle(
            new Point(0, 1, 0),
            new Point(1, -1, 0),
            new Point(-1, -1, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the triangle (1 point)
        Ray ray1 = new Ray(new Point(0, 0, 1), new Vector(0, 0, -1));
        Point expected = new Point(0, 0, 0.0001); // Slight offset to avoid (0,0,0)
        var result1 = triangle.findIntersections(ray1);
        assertNotNull(result1, "Ray should intersect triangle");
        assertEquals(1, result1.size(), "Wrong number of points");
        assertEquals(List.of(new Point(0, 0, 0)), result1, "Wrong intersection point");

        // TC02: Ray misses the triangle against an edge (0 points)
        Ray ray2 = new Ray(new Point(2, 0, 1), new Vector(0, 0, -1));
        assertNull(triangle.findIntersections(ray2), "Ray misses the triangle");

        // TC03: Ray misses the triangle against a vertex (0 points)
        Ray ray3 = new Ray(new Point(0, 2, 1), new Vector(0, 0, -1));
        assertNull(triangle.findIntersections(ray3), "Ray misses the triangle at vertex");

        // =============== Boundary Values Tests ==================

        // TC11: Ray lies exactly on an edge - should return null
        Ray ray4 = new Ray(new Point(0.5, -1, 1), new Vector(0, 0, -1));
        assertNull(triangle.findIntersections(ray4), "Ray on edge of triangle");

        // TC12: Ray passes through a vertex - should return null
        Ray ray5 = new Ray(new Point(0, 1, 1), new Vector(0, 0, -1));
        assertNull(triangle.findIntersections(ray5), "Ray through vertex of triangle");

        // TC13: Ray is outside the triangle but on the plane - should return null
        Ray ray6 = new Ray(new Point(2, 2, 0), new Vector(1, 0, 0));
        assertNull(triangle.findIntersections(ray6), "Ray on plane but outside triangle");

        // TC14: Ray originates on the plane but outside triangle - should return null
        Ray ray7 = new Ray(new Point(2, 0, 0), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray7), "Ray starts on plane but outside triangle");

        // TC15: Ray starts at triangle plane at intersection point - should return null
        Ray ray8 = new Ray(new Point(0.1, 0, 0), new Vector(0, 0, -1)); // offset from (0,0,0)
        assertNull(triangle.findIntersections(ray8), "Ray starts at intersection point on triangle");
    }


}
