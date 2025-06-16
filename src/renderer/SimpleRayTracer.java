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

        // Recursive calculation of color using max recursion level and initial attenuation
        Color localAndRecursiveColor = calcColor(intersection, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K);


        // Calculate ambient color scaled by material ambient coefficient
        Color ambient = scene.ambientLight.getIntensity().scale(intersection.material.Ka);

        // Get emission color of the geometry at the intersection point
        Color emission = intersection.geometry.getEmission();

        // Combine ambient, emission, and recursively calculated local effects
        return ambient.add(emission).add(localAndRecursiveColor);
    }

    
    /**
     * Calculates the color at the intersection point recursively, considering the recursion level
     * and the accumulated attenuation factor.
     * 
     * At this stage, only local lighting effects (diffuse, specular, ambient) are calculated.
     * 
     * @param intersection the intersection point information
     * @param level the current recursion depth
     * @param k the accumulated attenuation factor (of type Double3)
     * @return the color calculated based on local effects only
     */
    private Color calcColor(Intersection intersection, Ray ray, int level, Double3 k) {
    	preprocessIntersection(intersection, ray.getDir());

    	if (level <= 0 || k.lowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }
    	

        Color localEffects = calcColorLocalEffects(intersection);
        Color globalEffects = calcGlobalEffects(intersection, ray, level - 1, k);

        return localEffects.add(globalEffects);
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
     * A point is considered shadowed only if there exists an intersection along the shadow ray
     * whose material has a transparency factor lower than K_COLOR_CALC_MIN,
     * meaning the object is sufficiently opaque to block the light.
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
            // If it is lower than the threshold, the object is opaque enough to cast a shadow
            if (shadowIntersection.material.kT.lowerThan(MIN_CALC_COLOR_K)) {
                return false; // The point is shadowed by this object
            }
            // Otherwise, the object is sufficiently transparent, so continue checking
        }
        
        // All intersecting objects are sufficiently transparent; no shadow occurs
        return true;
    }

    
    /**
     * Creates a reflection ray from the intersection point,
     * offset in the direction of the normal to avoid self intersection.
     * 
     * @param intersection the intersection point info (point and normal)
     * @param incomingRay  the incoming ray that hit the point
     * @return the reflection ray
     */
    private Ray constructReflectionRay(Intersection intersection, Ray incomingRay) {
        Vector normal = intersection.normal;
        Vector v = incomingRay.getDir();

        // Calculate reflected direction: R = V - 2 * (V · N) * N
        Vector reflectedDir = v.subtract(normal.scale(2 * v.dotProduct(normal))).normalize();

        // Determine offset direction to avoid self intersection
        Point point = intersection.point;
        Point offsetPoint = point;

        if (reflectedDir.dotProduct(normal) > 0) {
            offsetPoint = point.add(normal.scale(DELTA));
        } else {
            offsetPoint = point.subtract(normal.scale(DELTA));
        }

        return new Ray(offsetPoint, reflectedDir);
    }

    /**
     * Creates a refraction (transparency) ray from the intersection point,
     * offset in the direction of the normal to avoid self intersection.
     * 
     * @param intersection the intersection point info (point and normal)
     * @param incomingRay  the incoming ray that hit the point
     * @return the refraction ray
     */
    private Ray constructRefractionRay(Intersection intersection, Ray incomingRay) {
        Vector normal = intersection.normal;
        Vector dir = incomingRay.getDir().normalize();

        // Refraction direction is the same as incoming direction (assuming no bending)
        Vector refractedDir = dir;

        Point point = intersection.point;
        Point offsetPoint = point;

        if (refractedDir.dotProduct(normal) > 0) {
            offsetPoint = point.add(normal.scale(DELTA));
        } else {
            offsetPoint = point.subtract(normal.scale(DELTA));
        }

        return new Ray(offsetPoint, refractedDir);
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

        // Calculate the distance from the ray origin to this closest intersection point
        double minDistance = closest.point.distance(rayOrigin);

        // Iterate through all intersections to find the closest one to the ray origin
        for (Intersection intersection : intersections) {
            double dist = intersection.point.distance(rayOrigin);
            if (dist < minDistance) {
                closest = intersection;   // Update closest intersection
                minDistance = dist;       // Update minimum distance found
            }
        }

        // Return the closest intersection found
        return closest;
    }


    
    /**
     * Calculates the global lighting effect (reflection or refraction) resulting from a secondary ray.
     * This method is called recursively to compute the color contribution of a ray that was generated
     * from a reflective or transparent surface.
     *
     * @param secondaryRay the secondary ray (reflected or refracted) to trace
     * @param level        the current recursion depth; used to limit the depth of recursive tracing
     * @param k            the accumulated attenuation factor from previous recursive levels
     * @param kEffect      the attenuation coefficient for this specific effect (reflection or transparency)
     * @return the color contribution from the secondary ray, or black if no contribution is significant
     */
    private Color calcGlobalEffect(Ray secondaryRay, int level, Double3 k, Double3 kEffect) {

    	// Stop recursion if max depth reached or contribution is too small
        if (level <= 0 || k.lowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        // Find the closest intersection of the secondary ray
        Intersection intersection = findClosestIntersection(secondaryRay);
        if (intersection == null) {
            return Color.BLACK; // No geometry hit by the secondary ray
        }

        // Initialize intersection data (direction vector, normal, etc.)
        preprocessIntersection(intersection, secondaryRay.getDir());

        // Update the attenuation factor by multiplying with the current effect coefficient
        Double3 newK = k.product(kEffect);

        // Recursively compute the color at the new intersection point
        return calcColor(intersection, secondaryRay, level - 1, newK);
    }

    
    /**
     * Calculates the combined global effects of reflection and transparency.
     * 
     * @param intersection the current intersection
     * @param ray          the incoming ray
     * @param level        the current recursion depth
     * @param k            the accumulated attenuation factor
     * @return the combined color from reflection and transparency
     */
    private Color calcGlobalEffects(Intersection intersection, Ray ray, int level, Double3 k) {
    	System.out.println("📣 calcGlobalEffects called - kR=" + intersection.material.kR + ", kT=" + intersection.material.kT);

    	Color reflectionColor = Color.BLACK;
        Color refractionColor = Color.BLACK;

        // Reflection effect
        if (!intersection.material.kR.equals(Double3.ZERO)) {
            Ray reflectedRay = constructReflectionRay(intersection, ray);
            reflectionColor = calcGlobalEffect(reflectedRay, level, k, intersection.material.kR);
        }

        // Refraction (transparency) effect
        if (!intersection.material.kT.equals(Double3.ZERO)) {
            Ray refractedRay = constructRefractionRay(intersection, ray);
            refractionColor = calcGlobalEffect(refractedRay, level, k, intersection.material.kT);
        }

        return reflectionColor.add(refractionColor);
    }



}
