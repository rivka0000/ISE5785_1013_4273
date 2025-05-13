package renderer;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
 * Currently, this implementation is not supported and will throw an exception upon use.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a simple ray tracer for the given scene.
     *
     * @param scene the scene to be used by the ray tracer
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    
    /**
     * Traces a ray through the scene and returns the resulting color at the intersection point.
     * This implementation finds the closest intersection point and calculates the color based on the ambient light.
     *
     * @param ray the ray to trace through the scene
     * @return the color at the closest intersection point, or the background color if no intersection occurs
     */
    @Override
    public Color traceRay(Ray ray) {
        // Find all intersections of the ray with the geometries in the scene
        List<Point> intersections = scene.geometries.findIntersections(ray);

        // If there are no intersections, return the background color of the scene
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        }

        // Find the closest intersection point to the ray's origin
        Point closestPoint = ray.findClosestPoint(intersections);

        // Calculate and return the color at the closest intersection point
        return calcColor(closestPoint);
    }

    
    /**
     * Calculates the color for a given point by computing the ambient light intensity from the scene.
     * At this stage, it only returns the ambient light color.
     *
     * @param point the 3D point in space (currently not used for further calculations)
     * @return the calculated color based on ambient light intensity in the scene
     */
    private Color calcColor(Point point) {
        // Assume we have access to the scene's ambient light
        // Here, we simply return the ambient light color as a placeholder for future implementations

        // For now, just return the ambient light color
        return scene.ambientLight.getIntensity();
    }

}

