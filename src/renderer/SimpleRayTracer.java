package renderer;

import java.util.List;

import geometries.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
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
     * Traces a given ray through the scene and computes the resulting color at the closest intersection point.
     *
     * @param ray the ray to be traced through the scene
     * @return the color at the closest intersection point; returns the background color if no intersection is found
     */
    @Override
    public Color traceRay(Ray ray) {
        //  Calculate all intersections of the ray with the geometries in the scene
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        // If no intersections are found, return the background color of the scene
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        }

        //  Find the closest intersection point among all intersections
        Intersection closestIntersection = ray.findClosestIntersection(intersections);

        // If no closest intersection is found (should be rare), return the background color
        if (closestIntersection == null) {
            return scene.background;
        }

        // Calculate and return the color at the closest intersection point (currently ambient light only)
        return calcColor(closestIntersection);
    }


    /**
     * Calculates the color for a given intersection using ambient light only.
     *
     * @param intersection the intersection to calculate color for
     * @return the calculated color based on ambient light
     */
    private Color calcColor(Intersection intersection) {
        return intersection.geometry.getEmission()
               .add(scene.ambientLight.getIntensity()
               .scale(intersection.geometry.getMaterial().Ka));
    }

}
