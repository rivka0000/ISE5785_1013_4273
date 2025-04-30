package unittests.renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;
import renderer.Camera;

/**
 * Integration tests between the Camera class and geometric objects.
 * <p>
 * These tests validate the correct behavior of ray construction from the
 * camera through the view plane and the resulting intersections with various
 * geometries in the scene.
 * </p>
 * <p>
 * The tests include counting the total number of intersection points between
 * the rays (constructed by the camera) and the provided geometry, across a
 * 3x3 view plane.
 * </p>
 */
public class CameraIntersectionsIntegrationTests {

    /**
     * Helper method to count total intersections between all rays constructed by a
     * camera through a view plane of size nX x nY and a given geometry.
     *
     * @param camera   the camera used to construct rays
     * @param geometry the geometry to check intersections with
     * @param nX       number of columns in the view plane
     * @param nY       number of rows in the view plane
     * @return the total number of intersection points found
     */
    private int countIntersections(Camera camera, Intersectable geometry, int nX, int nY) {
        int count = 0;
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                Ray ray = camera.constructRay(nX, nY, j, i);
                List<Point> intersections = geometry.findIntersections(ray);
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }
        return count;
    }

    /**
     * Test for ray-sphere intersections. A small sphere is centered in front of
     * the camera.
     * Expected: 2 intersection points.
     */
    @Test
    void testSphereIntersections() {
        Camera camera = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(3, 3)
            .build();

        Sphere sphere = new Sphere(new Point(0, 0, -3), 1d);
        int count = countIntersections(camera, sphere, 3, 3);
        assertEquals(2, count, "Wrong number of intersections with sphere");
    }

    /**
     * Test for ray-plane intersections. A plane is placed orthogonal to the camera's view direction.
     * Expected: 9 intersection points (one per ray).
     */
    @Test
    void testPlaneIntersections() {
        Camera camera = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(3, 3)
            .build();

        Plane plane = new Plane(new Point(0, 0, -5), new Vector(0, 0, 1));
        int count = countIntersections(camera, plane, 3, 3);
        assertEquals(9, count, "Wrong number of intersections with plane");
    }

    /**
     * Test for ray-triangle intersections. A small triangle is positioned such that only the center ray hits it.
     * Expected: 1 intersection point.
     */
    @Test
    void testTriangleIntersections() {
        Camera camera = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(3, 3)
            .build();

        Triangle triangle = new Triangle(
            new Point(0, 1, -2),
            new Point(1, -1, -2),
            new Point(-1, -1, -2)
        );
        int count = countIntersections(camera, triangle, 3, 3);
        assertEquals(1, count, "Wrong number of intersections with triangle");
    }
}