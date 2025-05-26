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
        // EP01: שימוש בשני וקטורים אורתוגונליים ומנורמלים
        Vector vTo1 = new Vector(0, 0, -1); // מבט קדימה בציר Z-
        Vector vUp1 = new Vector(0, 1, 0);  // מעלה בציר Y

        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(vTo1, vUp1)
                .setVpSize(4, 4)
                .setVpDistance(10)
                .setResolution(2, 2)
                .setRayTracer(new Scene("TestScene"), RayTracerType.SIMPLE)
                .build();

        Ray ray1 = camera1.constructRay(2, 2, 0, 0);
        assertNotNull(ray1); // אפשר לבדוק גם את כיוון ה-Ray אם את רוצה

        // EP02: עוד סט של וקטורים אורתוגונליים
        Vector vTo2 = new Vector(1, 0, 0);   // מבט קדימה בציר X
        Vector vUp2 = new Vector(0, 0, 1);   // מעלה בציר Z

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

        // BV01: שני וקטורים לא אורתוגונליים -> צריך לזרוק חריגה
        Vector badVTo = new Vector(1, 0, 0);
        Vector badVUp = new Vector(1, 0, 0); // אותו כיוון בדיוק

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
