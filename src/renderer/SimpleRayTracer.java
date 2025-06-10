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
 * A simple implementation of a ray tracer.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a SimpleRayTracer for the given scene.
     * 
     * @param scene the scene to be rendered by this ray tracer
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray into the scene and returns the color of the closest intersection.
     * 
     * @param ray the ray to trace
     * @return the color computed by tracing the ray; background color if no intersection
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
     * Calculates the color at the intersection point including ambient, emission, and local lighting effects.
     * 
     * @param intersection the intersection to calculate the color for
     * @param ray the ray that caused the intersection
     * @return the resulting color at the intersection point
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
     * Calculates the local lighting effects (diffuse and specular) at the intersection from all light sources.
     * 
     * @param intersection the intersection to calculate lighting for
     * @return the color resulting from local lighting effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color result = Color.BLACK;

        for (LightSource light : scene.lights) {
            if (!setLightSource(intersection, light))
                continue;

            // Skip this light if the point is in shadow
            if (!isUnshaded(intersection))
                continue;

            Color iL = light.getIntensity(intersection.point);
            Double3 diff = calcDiffusive(intersection);
            Double3 spec = calcSpecular(intersection);

            result = result.add(iL.scale(diff.add(spec)));
        }

        return result;
    }

    /**
     * Calculates the specular component of lighting at the intersection point.
     * 
     * @param i the intersection containing relevant data
     * @return the specular color contribution as a Double3 vector
     */
    private Double3 calcSpecular(Intersection i) {

        // Compute reflection vector r = l - 2(n ⋅ l) * n (normalized)
        Vector r = (i.l.subtract(i.normal.scale(2 * i.lNormal))).normalize();

        double minusVR = -(i.v.dotProduct(r)); // use -v·r because v points outward
        if (Util.alignZero(minusVR) <= 0) {
            return Double3.ZERO;
        }

        // Return scaled specular intensity based on material shininess
        return i.material.KS.scale(Math.pow(minusVR, i.material.nShininess));
    }

    /**
     * Calculates the diffuse component of lighting at the intersection point.
     * 
     * @param i the intersection containing relevant data
     * @return the diffuse color contribution as a Double3 vector
     */
    private Double3 calcDiffusive(Intersection i) {
        return i.material.KD.scale(Math.abs(i.lNormal));
    }

    /**
     * Prepares intersection data for shading calculation.
     * Computes the normal vector and the dot product of view direction and normal.
     * 
     * @param intersection the intersection to preprocess
     * @param v the direction vector from the camera/viewer to the intersection point
     * @return true if the dot product of v and normal is not zero; false otherwise
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return !isZero(intersection.vNormal);
    }

    /**
     * Sets the light source related fields in the intersection and calculates
     * the dot product between light direction and normal, and between view direction and normal.
     * 
     * @param intersection the intersection to update
     * @param light the light source being considered
     * @return true if the light direction and view direction are on the same side of the surface; false otherwise
     */
    private boolean setLightSource(Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

    /**
     * Checks if the given intersection point is unshaded (i.e., not in shadow).
     * 
     * @param intersection The intersection point to check for shadows.
     * @return true if the point is not shadowed and receives direct light; false if it's in shadow.
     */
    private boolean isUnshaded(Intersection intersection) {
        // Create a shadow ray from the intersection point towards the light source (opposite direction of 'l')
        Ray shadowRay = new Ray(intersection.point, intersection.l.scale(-1), intersection.normal);

        // Calculate intersections of the shadow ray with scene geometries
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);
        if (shadowIntersections == null) return true; // No intersections means no occlusion

        // Approximate squared distance to the light source from the intersection point
        double lightDistanceSquared = intersection.point.distanceSquared(shadowRay.getPoint(1));

        // Check if any geometry blocks the light (closer than the light source)
        for (Intersection shadow : shadowIntersections) {
            if (shadow.point.distanceSquared(intersection.point) < lightDistanceSquared)
                return false; // There is an object blocking the light
        }

        return true; // No occlusion found, point is illuminated
    }

}






