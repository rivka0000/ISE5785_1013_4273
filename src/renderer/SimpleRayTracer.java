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
     * Traces a ray through the scene and returns the resulting color at the intersection point.
     *
     * @param ray the ray to trace through the scene
     * @return the color at the closest intersection point, or the background color if no intersection occurs
     */
    @Override
    public Color traceRay(Ray ray) {
        // שלב 1: חישוב כל החיתוכים עם הסצנה
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        // אם אין בכלל חיתוכים – מחזירים צבע רקע
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        }

        // שלב 2: איתור החיתוך הקרוב ביותר
        Intersection closestIntersection = ray.findClosestIntersection(intersections);
        if (closestIntersection == null) {
            return scene.background;
        }

        // שלב 3: חישוב צבע – כעת רק לפי אור סביבתי
        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color for a given intersection using ambient light only.
     *
     * @param intersection the intersection to calculate color for
     * @return the calculated color based on ambient light
     */
    private Color calcColor(Intersection intersection) {
        return scene.ambientLight.getIntensity();
    }
}
