package unittests.renderer;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.*;
import renderer.Camera;
import scene.Scene;
import renderer.RayTracerType;

/**
 * Testing Camera Class
 */
class CameraTest {
    /** Camera builder base */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
        .setLocation(Point.ZERO)
        .setVpDistance(10)
        .setRayTracer(new Scene("test"), RayTracerType.SIMPLE); // ✅ חובה לפי שלב 6

    private static final String BAD_RAY = "Bad ray";

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

    @Test
    void testBuilder() {
        cameraBuilder.setVpSize(4, 4).setResolution(2, 2);

        // EP01: set to a target point with up vector
        Point target1 = new Point(10, -10, 0);
        Camera camera1 = cameraBuilder
                .setDirection(target1, new Vector(0, 0, 1)) // ✅ שימוש ב־up תקין
                .build();
        Point center1 = target1.subtract(Point.ZERO).normalize().scale(10);
        Vector right1 = Vector.AXIS_Z;
        Vector up1 = new Vector(1, 1, 0).normalize();
        Vector direction1 = center1.add(up1).subtract(right1).subtract(Point.ZERO);
        assertEquals(new Ray(Point.ZERO, direction1), camera1.constructRay(2, 2, 0, 0));

        // EP02: set to a target point with up vector
        Point target2 = new Point(0, 5, 0);
        Camera camera2 = cameraBuilder
                .setDirection(target2, new Vector(0, 1, 1))
                .build();
        Point center2 = new Point(0, 10, 0);
        Vector right2 = Vector.AXIS_X;
        Vector up2 = Vector.AXIS_Z;
        Vector direction2 = center2.add(up2).subtract(right2).subtract(Point.ZERO);
        assertEquals(new Ray(Point.ZERO, direction2), camera2.constructRay(2, 2, 0, 0));

        // BV01: invalid because up and to not orthogonal
        assertThrows(IllegalArgumentException.class, () ->
                cameraBuilder.setDirection(new Point(0, 10, 0)).build());
    }
}
