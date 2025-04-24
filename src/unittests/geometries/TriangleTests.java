package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;
import geometries.Triangle;
import primitives.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link geometries.Triangle} class
 * 
 * @author Ahuva Winkler
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

}
