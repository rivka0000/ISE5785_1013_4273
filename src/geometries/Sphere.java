package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import geometries.Intersectable.Intersection;
import static primitives.Util.alignZero;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a sphere in 3D space, inheriting from the RadialGeometry class.
 * The sphere is defined by its center point and radius.
 */
public class Sphere extends RadialGeometry {

    /** The center point of the sphere */
    private final Point center;

    /**
     * Constructs a sphere from a center point and a radius.
     *
     * @param center The center of the sphere
     * @param radius The radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    /**
     * Gets the normal vector to the surface of the sphere at a given point.
     *
     * @param point The point on the surface
     * @return Normalized normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return point.subtract(center).normalize();
    }

    /**
     * Computes intersections of a ray with the sphere (no distance limit).
     *
     * @param ray the ray to intersect
     * @return list of intersection points or null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        return calculateIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Computes intersections of a ray with the sphere, up to a maximum distance.
     *
     * @param ray         the ray to intersect
     * @param maxDistance the maximum allowed distance
     * @return list of intersections or null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.getP0();
        Vector dir = ray.getDir();

        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts at the center
            if (radius > maxDistance) return null;
            return List.of(new Intersection(this, ray.getPoint(radius)));
        }

        double tm = dir.dotProduct(u);
        double dSquared = u.lengthSquared() - tm * tm;
        double rSquared = radius * radius;

        if (alignZero(dSquared - rSquared) >= 0) return null;

        double th = Math.sqrt(rSquared - dSquared);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        List<Intersection> result = new LinkedList<>();
        if (t1 > 0 && t1 <= maxDistance)
            result.add(new Intersection(this, ray.getPoint(t1)));
        if (t2 > 0 && t2 <= maxDistance)
            result.add(new Intersection(this, ray.getPoint(t2)));

        return result.isEmpty() ? null : result;
    }
}
