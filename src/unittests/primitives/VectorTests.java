package unittests.primitives;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for {@link primitives.Vector} class
 * @author Ahuva Winkler
 */
class VectorTests {

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAddVector() {
        // ============ Boundary Values Tests ==================

        // TC11: Adding two opposite vectors → should throw exception (zero vector)
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2),
            "TC11: Expected exception for zero vector result not thrown");
    }

    /**
     * Test method for {@link primitives.Vector#subtract(primitives.Vector)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Subtracting two different vectors
        Vector v1 = new Vector(2, 3, 4);
        Vector v2 = new Vector(1, 1, 1);
        assertEquals(new Vector(1, 2, 3), v1.subtract(v2), "TC01: Wrong result from subtract()");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Scaling a vector by a positive number
        Vector v = new Vector(1, 2, 3);
        assertEquals(new Vector(2, 4, 6), v.scale(2), "TC01: Wrong result for scale()");

        // ============ Boundary Values Tests ==================

        // TC11: Scaling a vector by zero → should throw exception
        assertThrows(IllegalArgumentException.class, () -> v.scale(0), "TC11: Expected exception for scaling by zero");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Regular dot product
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, -5, 6);
        double expected = 1*4 + 2*(-5) + 3*6; // 4 -10 +18 = 12
        assertEquals(expected, v1.dotProduct(v2), 0.00001, "TC01: Wrong dot product");

        // TC02: Orthogonal vectors → dot product should be 0
        Vector v3 = new Vector(1, 0, 0);
        Vector v4 = new Vector(0, 1, 0);
        assertEquals(0, v3.dotProduct(v4), 0.00001, "TC02: Dot product of orthogonal vectors should be zero");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Cross product of orthogonal vectors
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(0, 1, 0);
        Vector cross = v1.crossProduct(v2);
        assertEquals(new Vector(0, 0, 1), cross, "TC01: Wrong cross product");

        // ============ Boundary Values Tests ==================

        // TC11: Cross product of parallel vectors → should throw exception
        assertThrows(IllegalArgumentException.class,
            () -> v1.crossProduct(new Vector(2, 0, 0)),
            "TC11: Expected exception for crossProduct of parallel vectors");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Regular vector length squared calculation
        Vector v = new Vector(1, 2, 2);
        assertEquals(9, v.lengthSquared(), 0.00001, "TC01: Wrong result from lengthSquared()");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Regular vector length calculation
        Vector v = new Vector(0, 3, 4);
        assertEquals(5, v.length(), 0.00001, "TC01: Wrong result from length()");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        Vector v = new Vector(0, 3, 4);
        Vector normalized = v.normalize();

        // TC01: Length of normalized vector should be 1
        assertEquals(1, normalized.length(), 0.00001, "TC01: Normalized vector is not unit length");

        // TC02: Normalized vector should be parallel to the original → cross product should fail
        assertThrows(IllegalArgumentException.class,
            () -> v.crossProduct(normalized),
            "TC02: Normalized vector should be parallel to original vector");
    }
}
