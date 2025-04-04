package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 * The class provides methods for retrieving the normal vector of the plane.
 */
public class Plane {
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
}
