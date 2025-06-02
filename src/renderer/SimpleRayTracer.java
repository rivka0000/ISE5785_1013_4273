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

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        }

        Intersection closestIntersection = ray.findClosestIntersection(intersections);
        return closestIntersection == null ? scene.background : calcColor(closestIntersection, ray);
    }

    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDir()))
            return Color.BLACK;

        Color ambient = scene.ambientLight.getIntensity().scale(intersection.material.Ka);
        Color emission = intersection.geometry.getEmission();
        Color localEffects = calcColorLocalEffects(intersection);

        return ambient.add(emission).add(localEffects);
    }

    private Color calcColorLocalEffects(Intersection intersection) {
        Color result = Color.BLACK;

        for (LightSource light : scene.lights) {
            if (!setLightSource(intersection, light))
                continue;

            // ✅ תוספת לצל: דילוג על אור אם הנקודה בצל
            if (!isUnshaded(intersection))
                continue;

            Color iL = light.getIntensity(intersection.point);
            Double3 diff = calcDiffusive(intersection);
            Double3 spec = calcSpecular(intersection);

            result = result.add(iL.scale(diff.add(spec)));
        }

        return result;
    }

    private Double3 calcSpecular(Intersection i) {
    	Vector r = i.normal.scale(i.lNormal).subtract(i.l).normalize(); // reflection vector
        double minusVR = -i.v.dotProduct(r);
        if (minusVR <= 0) return Double3.ZERO;
        return i.material.KS.scale(Math.pow(minusVR, i.material.nShininess));
    }

    private Double3 calcDiffusive(Intersection i) {
        return i.material.KD.scale(Math.abs(i.lNormal));
    }

    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return !isZero(intersection.vNormal);
    }

    private boolean setLightSource(Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return !isZero(intersection.lNormal * intersection.vNormal);
    }

    // ✅ זוהי המתודה שאחראית לבדוק האם הנקודה בצל
    private boolean isUnshaded(Intersection intersection) {
        // יוצרים קרן צל מנקודת החיתוך לכיוון מקור האור (בכיוון ההפוך מ־l)
        Ray shadowRay = new Ray(intersection.point, intersection.l.scale(-1), intersection.normal);

        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);
        if (shadowIntersections == null) return true;

        double lightDistanceSquared = intersection.point.distanceSquared(shadowRay.getPoint(1)); // ערך מקורב

        for (Intersection shadow : shadowIntersections) {
            if (shadow.point.distanceSquared(intersection.point) < lightDistanceSquared)
                return false; // יש אובייקט שמסתיר את מקור האור
        }

        return true; // אין הסתרה – הנקודה מוארת
    }
}
