package geometries;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 * Provides methods for normal vector and ray-plane intersection.
 */
public class Plane extends Geometry {

    /** A point on the plane */
    private Point q;

    /** The normal vector of the plane */
    private Vector normal;

    /**
     * Constructs a plane using three points on the plane.
     *
     * @param p1 First point
     * @param p2 Second point
     * @param p3 Third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        this.q = p1;
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        this.normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructs a plane using a point and a normal vector.
     *
     * @param q      A point on the plane
     * @param normal A normal vector (will be normalized)
     */
    public Plane(Point q, Vector normal) {
        this.q = q;
        this.normal = normal.normalize();
    }

    /**
     * Gets the normal vector of the plane at a given point.
     *
     * @param point1 Ignored (same normal everywhere)
     * @return the normal vector
     */
    @Override
    public Vector getNormal(Point point1) {
        return this.normal;
    }

    /**
     * Gets the normal vector of the plane.
     *
     * @return the normal vector
     */
    public Vector getNormal() {
        return this.normal;
    }

    /**
     * Finds intersection between a ray and the plane (no distance limit).
     *
     * @param ray the ray to test
     * @return list with single intersection or null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        return calculateIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds intersection between a ray and the plane, within maxDistance.
     *
     * @param ray         the ray to test
     * @param maxDistance the maximum distance allowed
     * @return list with intersection or null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.getP0();
        Vector dir = ray.getDir();

        double nv = Util.alignZero(normal.dotProduct(dir));
        if (Util.isZero(nv)) {
            return null; // Ray is parallel to plane
        }

        Vector q0_p0;
        try {
            q0_p0 = q.subtract(p0);
        } catch (IllegalArgumentException e) {
            return null; // Ray starts on the plane
        }

        double t = Util.alignZero(normal.dotProduct(q0_p0) / nv);
        if (t <= 0 || t > maxDistance) {
            return null; // Intersection behind ray origin or too far
        }

        Point intersectionPoint = ray.getPoint(t);
        return List.of(new Intersection(this, intersectionPoint));
    }
}
