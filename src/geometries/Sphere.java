package geometries;

import primitives.Point;
import primitives.Vector;

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
    public Vector getNormal(Point point1) {
        return null; // Normal vector calculation is not implemented yet
    }
}
