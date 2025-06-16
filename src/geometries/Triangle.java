package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

/**
 * Represents a triangle defined by three vertices.
 * Inherits from {@link Polygon}, but provides optimized intersection logic.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a triangle from three vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    /**
     * Computes ray-triangle intersection using plane and inside-triangle check.
     *
     * @param ray the ray to intersect
     * @return list containing a single intersection, or null if none
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        return calculateIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Computes ray-triangle intersection using plane and inside-triangle check,
     * and only returns the intersection if it is closer than maxDistance.
     *
     * @param ray         the ray to intersect
     * @param maxDistance maximum allowed distance from ray origin
     * @return list containing the intersection if within range, or null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        // First find intersection with the plane
        List<Point> planePoints = super.plane.findIntersections(ray);
        if (planePoints == null) return null;

        Point p = planePoints.get(0);

        // Check if point is within max distance
        if (ray.getP0().distance(p) > maxDistance) return null;

        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        Vector n = plane.getNormal();

        try {
            Vector v1p = p.subtract(v1);
            Vector v2p = p.subtract(v2);
            Vector v3p = p.subtract(v3);

            Vector n1 = v2.subtract(v1).crossProduct(v1p);
            Vector n2 = v3.subtract(v2).crossProduct(v2p);
            Vector n3 = v1.subtract(v3).crossProduct(v3p);

            double s1 = Util.alignZero(n.dotProduct(n1));
            double s2 = Util.alignZero(n.dotProduct(n2));
            double s3 = Util.alignZero(n.dotProduct(n3));

            if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
                return List.of(new Intersection(this, p));
            }
        } catch (IllegalArgumentException e) {
            // Degenerate triangle or exact edge hit
            return null;
        }

        return null;
    }
}
