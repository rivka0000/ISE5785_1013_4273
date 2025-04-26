package geometries;
import primitives.Point;
import primitives.*;

import java.util.LinkedList;
import java.util.List;
import java.util.List;
import java.util.List;
import java.util.List;


/**
 * Represents a sphere in 3D space, inheriting from the RadialGeometry class.
 * The sphere is defined by its center point and radius.
 */ 
public class Sphere extends RadialGeometry {
    /** The center point of the sphere */
    private final Point center;
 
    /**
     * Constructor to initialize a sphere with a given center and radius.
     * 
     * @param center1 The center point of the sphere
     * @param radius The radius of the sphere
     */
    public Sphere(Point center1, double radius) {
        super(radius);  // Initialize the radius from the parent class
        this.center = center1;  // Set the center point of the sphere
    }

    /**
     * Gets the normal vector of the sphere at a specific point.
     * Currently returns null as the method is not yet implemented.
     * 
     * @param point1 The point on the sphere where the normal is needed
     * @return The normal vector at the given point (currently returns null)
     */
    public Vector getNormal(Point point) {
    	return point.subtract(center).normalize();
    }
    
    /**
     * Finds the intersection points between the triangle and the given ray.
     * Delegates the intersection logic to the {@link Polygon} implementation, or
     * implements specific triangle intersection logic if needed.
     *
     * @param ray the ray to test for intersection with the triangle
     * @return a list of intersection points, or {@code null} if there are no intersections
     */
    
    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getP0();
        Vector dir = ray.getDir();
        Point o = center;

        Vector u;
        try {
            u = o.subtract(p0);
        } catch (IllegalArgumentException e) {
            // הקרן מתחילה בדיוק במרכז הכדור
            return List.of(ray.getPoint(radius));
        }

        double tm = dir.dotProduct(u);
        double dSquared = u.lengthSquared() - tm * tm;
        double rSquared = radius * radius;

        if (Util.alignZero(dSquared - rSquared) >= 0) {
            return null;
        }

        double th = Math.sqrt(rSquared - dSquared);
        double t1 = Util.alignZero(tm - th);
        double t2 = Util.alignZero(tm + th);

        // ❗ המקרה הקריטי: הקרן בתוך הכדור, בכיוון הצידה (אורתוגונלית), אין חיתוך ממשי
        if (u.lengthSquared() < rSquared && Util.alignZero(tm) == 0) {
            return null;
        }

        if (t1 > 0 && t2 > 0) {
            return t1 < t2
                ? List.of(ray.getPoint(t1), ray.getPoint(t2))
                : List.of(ray.getPoint(t2), ray.getPoint(t1));
        }

        if (t1 > 0) {
            return List.of(ray.getPoint(t1));
        }

        if (t2 > 0) {
            return List.of(ray.getPoint(t2));
        }

        return null;
    }

}
