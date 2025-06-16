package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents an infinite tube in 3D space, defined by an axis ray and radius.
 * This class extends RadialGeometry and provides a method for calculating
 * the normal vector to the surface at a given point.
 */
public class Tube extends RadialGeometry {

    /** The axis of the tube (central spine of the cylinder) */
    protected final Ray axisRay;

    /**
     * Constructs a tube given an axis ray and radius.
     *
     * @param axisRay the axis of the tube
     * @param radius  the radius of the tube
     */
    public Tube(Ray axisRay, double radius) {
        super(radius);
        this.axisRay = axisRay;
    }

    /**
     * Returns the normal vector to the surface of the tube at a given point.
     * The normal is perpendicular to the axis of the tube.
     *
     * @param point the point on the surface
     * @return the normal vector at the given point
     * @throws IllegalArgumentException if the point lies on the tube axis
     */
    @Override
    public Vector getNormal(Point point) {
        Point p0 = axisRay.getP0();
        Vector dir = axisRay.getDir();

        Vector p0ToPoint = point.subtract(p0);
        double t = dir.dotProduct(p0ToPoint);

        // Compute the projection point o on the axis
        Point o = p0.add(dir.scale(t));

        Vector normal = point.subtract(o);
        if (normal.lengthSquared() == 0) {
            throw new IllegalArgumentException("Point is on the tube axis, normal is undefined.");
        }

        return normal.normalize();
    }

    /**
     * Placeholder for intersection logic (not yet implemented).
     * This version does not support intersections.
     *
     * @param ray the ray to test for intersection
     * @return null (no intersections calculated)
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        return null;
    }

    /**
     * Placeholder for intersection logic with max distance.
     * Needed for shadow calculations (stage 7A).
     *
     * @param ray         the ray to test
     * @param maxDistance the maximum distance allowed
     * @return null (no intersections calculated)
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        return null;
    }
}
