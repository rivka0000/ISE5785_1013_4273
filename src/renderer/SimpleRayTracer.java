package renderer;

import java.util.List;

import geometries.Intersectable;
import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * A simple implementation of a ray tracer that includes basic lighting and shadow handling.
 */
public class SimpleRayTracer extends RayTracerBase {

    // A small delta used to offset rays slightly to avoid self-intersection (shadow acne)
    private static final double DELTA = 0.1;

    /**
     * Constructor
     *
     * @param scene the scene to render
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a single ray and returns the calculated color based on intersections in the scene.
     *
     * @param ray the ray to trace
     * @return the computed color
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        }

        Intersection closestIntersection = ray.findClosestIntersection(intersections);
        return closestIntersection == null ? scene.background : calcColor(closestIntersection, ray);
    }

    /**
     * Computes the full color at a point of intersection,
     * including ambient light, emission, and local lighting effects.
     *
     * @param intersection the intersection to shade
     * @param ray          the ray that caused the intersection
     * @return the resulting color
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDir()))
            return Color.BLACK;

        Color ambient = scene.ambientLight.getIntensity().scale(intersection.material.Ka);
        Color emission = intersection.geometry.getEmission();
        Color localEffects = calcColorLocalEffects(intersection);

        return ambient.add(emission).add(localEffects);
    }

    /**
     * Calculates the local diffuse and specular effects for all light sources at the intersection.
     *
     * @param intersection the point to shade
     * @return the color from local lighting effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color result = Color.BLACK;

        for (LightSource light : scene.lights) {
            if (!setLightSource(intersection, light))
                continue;

            // Shadow check: skip light if point is in shadow
            if (!unshaded(intersection))
                continue;

            Color iL = light.getIntensity(intersection.point);
            Double3 diff = calcDiffusive(intersection);
            Double3 spec = calcSpecular(intersection);

            result = result.add(iL.scale(diff.add(spec)));
        }

        return result;
    }

    /**
     * Calculates the diffuse component of lighting.
     *
     * @param i the intersection data
     * @return diffuse color contribution
     */
    private Double3 calcDiffusive(Intersection i) {
        return i.material.KD.scale(Math.abs(i.lNormal));
    }

    /**
     * Calculates the specular component of lighting using the Phong model.
     *
     * @param i the intersection data
     * @return specular color contribution
     */
    private Double3 calcSpecular(Intersection i) {
        Vector r = (i.l.subtract(i.normal.scale(2 * i.lNormal))).normalize();
        double minusVR = -(i.v.dotProduct(r));

        if (alignZero(minusVR) <= 0) {
            return Double3.ZERO;
        }

        return i.material.KS.scale(Math.pow(minusVR, i.material.nShininess));
    }

    /**
     * Initializes fields in the intersection object: normal and v · n.
     *
     * @param intersection the intersection to initialize
     * @param v            the direction vector of the incoming ray
     * @return true if valid for shading, false if v · n is zero
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return !isZero(intersection.vNormal);
    }

    /**
     * Sets the light-related vectors and computes dot products for lighting calculations.
     *
     * @param intersection the intersection to update
     * @param light        the current light source
     * @return true if light and view are on the same side of the surface
     */
    private boolean setLightSource(Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

    /**
     * Checks whether a given point is directly lit by a light source or is shadowed.
     * Uses a shadow ray and considers only intersections up to the light source distance.
     *
     * @param intersection the point to check
     * @return true if the point is not in shadow
     */
    private boolean unshaded(Intersection intersection) {
        // Build a shadow ray from the point toward the light
        Ray shadowRay = new Ray(intersection.point, intersection.l.scale(-1), intersection.normal);
        double maxDistance = intersection.light.getDistance(intersection.point);

        // Intersect the shadow ray with scene geometries up to the light source
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay, maxDistance);
        return shadowIntersections == null || shadowIntersections.isEmpty();
    }
}
