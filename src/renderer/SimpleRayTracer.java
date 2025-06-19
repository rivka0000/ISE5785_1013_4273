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
    private static final double DELTA = 0.01;
    
    // Maximum recursion depth for calculating reflective/refractive effects
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    // Minimum accumulated attenuation factor - stops recursion if contribution is too small
    private static final double MIN_CALC_COLOR_K = 0.001;

    // Initial attenuation factor (no attenuation at start)
    private static final Double3 INITIAL_K = Double3.ONE;

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
        Intersection closestIntersection = findClosestIntersection(ray);
        return closestIntersection == null ? scene.background : calcColor(closestIntersection, ray);
    }

    /**
     * Computes the full color at a point of intersection,
     * including ambient light, emission, and local lighting effects
     * by invoking the recursive calcColor method with initial parameters.
     *
     * @param intersection the intersection to shade
     * @param ray          the ray that caused the intersection
     * @return the resulting color
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        // Preliminary check to ensure intersection and ray direction are valid
        if (!preprocessIntersection(intersection, ray.getDir()))
            return Color.BLACK;

        // Get emission color of the geometry at the intersection point
        Color emission = intersection.geometry.getEmission();
        
        // Calculate ambient color scaled by material ambient coefficient
        Color ambient = scene.ambientLight.getIntensity().scale(intersection.material.Ka);

        // Recursive calculation of color using max recursion level and initial attenuation
        Color localAndRecursiveColor = calcColor(intersection, ray, MAX_CALC_COLOR_LEVEL - 1, INITIAL_K);

        // Combine ambient, emission, and recursively calculated local effects
        return ambient.add(emission).add(localAndRecursiveColor);
    }

    /**
     * Calculates the color at the intersection point recursively, considering the recursion level
     * and the accumulated attenuation factor.
     *
     * @param intersection the intersection point information
     * @param incomingRay the incoming ray
     * @param level the current recursion depth
     * @param k the accumulated attenuation factor (of type Double3)
     * @return the color calculated based on local and global effects
     */
    private Color calcColor(Intersection intersection, Ray incomingRay, int level, Double3 k) {
        if (level <= 0 || k.lowerThan(MIN_CALC_COLOR_K))
            return Color.BLACK;

        Color local = calcColorLocalEffects(intersection);
        Material material = intersection.material;
        Double3 kR = material.kR;
        Double3 kT = material.kT;

        Color reflectedColor = Color.BLACK;
        Color refractedColor = Color.BLACK;

        // מחשבים רק אם יש טעם
        if (!k.product(kR).lowerThan(MIN_CALC_COLOR_K)) {
            Ray reflectedRay = constructReflectionRay(intersection, incomingRay);
            reflectedColor = calcGlobalEffect(reflectedRay, level, k, kR);
        }

        if (!k.product(kT).lowerThan(MIN_CALC_COLOR_K)) {
            Ray refractedRay = constructRefractionRay(intersection, incomingRay);
            refractedColor = calcGlobalEffect(refractedRay, level, k, kT);
        }

        // חיבור סופי של כל הצבעים
        return local
                .add(reflectedColor.scale(kR))
                .add(refractedColor.scale(kT));
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
     * Ensures the normal is oriented correctly towards the incoming ray.
     *
     * @param intersection the intersection to initialize
     * @param incomingDir  the direction vector of the incoming ray
     * @return true if valid for shading, false if v · n is zero
     */
    private boolean preprocessIntersection(Intersection intersection, Vector incomingDir) {
        intersection.v = incomingDir.normalize();
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        
        // The normal should point towards the viewer (opposite to incoming ray direction)
        // If normal and incoming ray are in the same direction, flip the normal
        double vn = intersection.v.dotProduct(intersection.normal);
        if (vn > 0) {
            intersection.normal = intersection.normal.scale(-1);
        }
        
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
     *
     * @param intersection the point to check shadows for
     * @return true if the point is not in shadow (i.e., light reaches the point)
     */
    private boolean unshaded(Intersection intersection) {
        // Construct a shadow ray from the intersection point towards the light source
        Ray shadowRay = new Ray(intersection.point, intersection.l.scale(-1), intersection.normal);
        
        // Maximum distance to consider intersections (distance to light source)
        double maxDistance = intersection.light.getDistance(intersection.point);
        
        // Find all intersections of the shadow ray with scene geometries up to maxDistance
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay, maxDistance);
        
        // If no intersections found, the point is fully lit
        if (shadowIntersections == null || shadowIntersections.isEmpty()) {
            return true;
        }
        
        // Iterate over all intersections found along the shadow ray
        for (Intersection shadowIntersection : shadowIntersections) {
            // Check the transparency factor kT of the intersected object's material
            if (shadowIntersection.material.kT.lowerThan(MIN_CALC_COLOR_K)) {
                return false; // The point is shadowed by this object
            }
        }
        
        // All intersecting objects are sufficiently transparent; no shadow occurs
        return true;
    }

    /**
     * Creates a reflection ray from the intersection point.
     * The reflected direction is calculated using the normal, and the ray is offset slightly.
     *
     * @param intersection the intersection point info
     * @param incomingRay  the incoming ray that hit the point
     * @return the reflection ray
     */
    private Ray constructReflectionRay(Intersection intersection, Ray incomingRay) {
        Vector normal = intersection.normal;
        Vector v = incomingRay.getDir().normalize();

        // Calculate reflected direction: R = V - 2 * (V · N) * N
        Vector reflectedDir = v.subtract(normal.scale(2 * v.dotProduct(normal))).normalize();

        // Offset the origin to avoid self-intersection
        Vector offsetNormal = reflectedDir.dotProduct(normal) > 0 ? normal : normal.scale(-1);
        Point offsetPoint = intersection.point.add(offsetNormal.scale(DELTA));

        return new Ray(offsetPoint, reflectedDir);
    }

    /**
     * Creates a refraction (transparency) ray from the intersection point.
     * 
     * @param intersection the intersection point info
     * @param incomingRay  the incoming ray that hit the point
     * @return the refraction ray
     */
    private Ray constructRefractionRay(Intersection intersection, Ray incomingRay) {
        Vector dir = incomingRay.getDir().normalize();
        Vector normal = intersection.normal;

        double nv = dir.dotProduct(normal);
        Vector offsetNormal = nv > 0 ? normal : normal.scale(-1); // קובע לאיזה צד להזיז את הקרן

        Point offsetPoint = intersection.point.add(offsetNormal.scale(DELTA));
        return new Ray(offsetPoint, dir);
    }
    
    /**
     * Finds the closest intersection of the ray with scene geometries.
     * 
     * @param ray the ray to check intersections for
     * @return the closest intersection or null if none found
     */
    private Intersection findClosestIntersection(Ray ray) {
        // Calculate all intersections of the ray with the scene geometries
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        // Return null if there are no intersections
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        // Get the origin point of the ray
        Point rayOrigin = ray.getP0();

        // Initialize the closest intersection with the first one in the list
        Intersection closest = intersections.get(0);
        double minDistance = closest.point.distance(rayOrigin);

        // Iterate through all intersections to find the closest one to the ray origin
        for (Intersection intersection : intersections) {
            double dist = intersection.point.distance(rayOrigin);
            if (dist < minDistance) {
                closest = intersection;
                minDistance = dist;
            }
        }

        return closest;
    }

    /**
     * Calculates the global effect color (reflection or transparency) for a secondary ray.
     * 
     * @param secondaryRay the reflection or refraction ray to trace
     * @param level        the current recursion depth
     * @param k            the accumulated attenuation factor
     * @param kEffect      the attenuation coefficient for this effect
     * @return the color contribution from the global effect
     */
    private Color calcGlobalEffect(Ray secondaryRay, int level, Double3 k, Double3 kEffect) {
        if (level <= 0 || k.lowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        Intersection intersection = findClosestIntersection(secondaryRay);
        if (intersection == null) {
            return scene.background;
        }
        
        if (!preprocessIntersection(intersection, secondaryRay.getDir())) {
            return Color.BLACK;
        }

        Double3 newK = k.product(kEffect);
        return calcColor(intersection, secondaryRay, level - 1, newK);
    }
    
    /**
     * Calculates the combined global effects (reflection and transparency) for the given intersection.
     *
     * @param intersection the intersection point to apply effects to
     * @param incomingRay the original incoming ray
     * @param level the current recursion level
     * @param k the accumulated attenuation factor
     * @return the combined color from reflection and transparency
     */
    private Color calcGlobalEffects(Intersection intersection, Ray incomingRay, int level, Double3 k) {
        Color reflectedColor = Color.BLACK;
        Color refractedColor = Color.BLACK;

        // Get material properties
        Material material = intersection.material;
        Double3 kR = material.kR; // Reflection coefficient
        Double3 kT = material.kT; // Transparency coefficient

        // Calculate reflection if kR is significant
        if (!k.product(kR).lowerThan(MIN_CALC_COLOR_K)) {
            Ray reflectedRay = constructReflectionRay(intersection, incomingRay);
            reflectedColor = calcGlobalEffect(reflectedRay, level, k, kR);
        }

        // Calculate transparency if kT is significant
        if (!k.product(kT).lowerThan(MIN_CALC_COLOR_K)) {
            Ray refractedRay = constructRefractionRay(intersection, incomingRay);
            refractedColor = calcGlobalEffect(refractedRay, level, k, kT);
        }

        // Return the sum of both effects
        return reflectedColor.add(refractedColor);
    }
}