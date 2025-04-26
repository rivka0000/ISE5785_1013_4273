package geometries;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 * The class provides methods for retrieving the normal vector of the plane.
 */
public class Plane extends Geometry {
    /** A point on the plane */
    private Point q;
    /** The normal vector of the plane */
    private Vector normal;

    /**
     * Constructor to create a plane using three points (p1, p2, p3).
     * The normal vector will be calculated from the three points.
     * 
     * @param p1 The first point on the plane 
     * @param p2 The second point on the plane
     * @param p3 The third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
    	 this.q = p1;
    	  Vector v1 = p2.subtract(p1);
    	  Vector v2 = p3.subtract(p1);
    	  this.normal = v1.crossProduct(v2).normalize(); 
    }

    /**
     * Constructor to create a plane using a point and a normal vector.
     * The normal vector is normalized.
     * 
     * @param q The point on the plane
     * @param normal The normal vector of the plane
     */
    public Plane(Point q, Vector normal) {
        this.q = q;
        this.normal = normal.normalize(); // Normalize the normal vector
    }
    
    /**
     * Gets the normal vector of the plane at a specific point.
     * 
     * @param point1 The point on the plane (not used here but kept for method signature consistency)
     * @return The normal vector of the plane
     */
    public Vector getNormal(Point point1) {
        return this.normal;
    }
    
    /**
     * Gets the normal vector of the plane.
     * 
     * @return The normal vector of the plane
     */
    public Vector getNormal() {
        return this.normal;
    }
    
    
    /**
     * Finds the intersection points between the plane and the given ray.
     *
     * @param ray the ray to test for intersection with the plane
     * @return a list of intersection points, or {@code null} if there are no intersections
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
    	Point p0 = ray.getP0();
        Vector dir = ray.getDir();
        Vector n = normal;

        double nv = Util.alignZero(n.dotProduct(dir));

        // הקרן מקבילה למישור – אין חיתוך
        if (Util.isZero(nv)) {
            return null;
        }

        Vector q0_p0;
        try {
            q0_p0 = q.subtract(p0); // וקטור מהמוצא של הקרן אל נקודה על המישור
        } catch (IllegalArgumentException e) {
            // הקרן מתחילה בדיוק על המישור => אין חיתוך כי זה לא קדימה על הקרן
            return null;
        }

        double t = Util.alignZero(n.dotProduct(q0_p0) / nv);

        // אם t שלילי או אפס – החיתוך הוא מאחורי הקרן או בראש הקרן
        if (t <= 0) {
            return null;
        }

        // חיתוך חוקי אחד קדימה על הקרן
        return List.of(ray.getPoint(t));
    }
}
