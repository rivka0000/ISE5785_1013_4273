package unittests.renderer;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import primitives.*;
import renderer.Camera;
import scene.Scene;
import renderer.RayTracerType;

/**
 * Unit tests for the {@link Camera} class.
 * <p>
 * This test suite verifies the correctness of ray construction and camera builder configuration,
 * including various edge and boundary cases.
 */
class CameraTest {
    /** Base builder instance used for constructing cameras in tests */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
        .setLocation(Point.ZERO)
        .setVpDistance(10)
        .setRayTracer(new Scene("test"), RayTracerType.SIMPLE); // ✅ Required since Stage 6

    private static final String BAD_RAY = "Bad ray";

    /**
     * Test method for {@link Camera#constructRay(int, int, int, int)}.
     * <p>
     * Verifies that rays are constructed correctly for given pixel coordinates,
     * using both equivalence partitioning and boundary value analysis.
     */
    @Test
    void testConstructRay() {
        cameraBuilder.setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0));
        Camera camera1 = cameraBuilder.setVpSize(8, 8).build();
        Camera camera2 = cameraBuilder.setVpSize(6, 6).build();

        // ============ Equivalence Partitions Tests ==============
        assertEquals(new Ray(Point.ZERO, new Vector(1, -1, -10)),
                camera1.constructRay(4, 4, 1, 1), BAD_RAY);

        // =============== Boundary Values Tests ==================
        assertEquals(new Ray(Point.ZERO, new Vector(3, -3, -10)),
                camera1.constructRay(4, 4, 0, 0), BAD_RAY);

        assertEquals(new Ray(Point.ZERO, new Vector(1, -3, -10)),
                camera1.constructRay(4, 4, 1, 0), BAD_RAY);

        assertEquals(new Ray(Point.ZERO, new Vector(0, 0, -10)),
                camera2.constructRay(3, 3, 1, 1), BAD_RAY);

        assertEquals(new Ray(Point.ZERO, new Vector(0, -2, -10)),
                camera2.constructRay(3, 3, 1, 0), BAD_RAY);

        assertEquals(new Ray(Point.ZERO, new Vector(2, 0, -10)),
                camera2.constructRay(3, 3, 0, 1), BAD_RAY);

        assertEquals(new Ray(Point.ZERO, new Vector(2, -2, -10)),
                camera2.constructRay(3, 3, 0, 0), BAD_RAY);
    }

    /**
     * Test method for {@link Camera.Builder}.
     * <p>
     * Verifies that the builder constructs valid cameras given proper input,
     * and throws exceptions when given invalid (non-orthogonal) directions.
     */
    @Test
    void testBuilder() {
        // EP01: Using normalized orthogonal vectors
        Vector vTo1 = new Vector(0, 0, -1); // Looking forward in -Z direction
        Vector vUp1 = new Vector(0, 1, 0);  // Up direction in Y axis

        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(vTo1, vUp1)
                .setVpSize(4, 4)
                .setVpDistance(10)
                .setResolution(2, 2)
                .setRayTracer(new Scene("TestScene"), RayTracerType.SIMPLE)
                .build();

        Ray ray1 = camera1.constructRay(2, 2, 0, 0);
        assertNotNull(ray1); // Can optionally test ray direction too

        // EP02: Another set of orthogonal vectors
        Vector vTo2 = new Vector(1, 0, 0);   // Looking forward in X direction
        Vector vUp2 = new Vector(0, 0, 1);   // Up direction in Z axis

        Camera camera2 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(vTo2, vUp2)
                .setVpSize(6, 6)
                .setVpDistance(20)
                .setResolution(3, 3)
                .setRayTracer(new Scene("TestScene"), RayTracerType.SIMPLE)
                .build();

        Ray ray2 = camera2.constructRay(3, 3, 1, 1);
        assertNotNull(ray2);

        // BV01: Using non-orthogonal vectors – should throw an exception
        Vector badVTo = new Vector(1, 0, 0);
        Vector badVUp = new Vector(1, 0, 0); // Same direction, not orthogonal

        assertThrows(IllegalArgumentException.class, () ->
            Camera.getBuilder()
                    .setLocation(Point.ZERO)
                    .setDirection(badVTo, badVUp)
                    .setVpSize(4, 4)
                    .setVpDistance(10)
                    .setResolution(2, 2)
                    .setRayTracer(new Scene("TestScene"), RayTracerType.SIMPLE)
                    .build());
    }
}
