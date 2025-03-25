package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube geometry, inheriting from the RadialGeometry class.
 * A tube is defined by its axis (a Ray) and radius.
 */
public class Tube extends RadialGeometry {
    /** The axis of the tube (represented by a Ray) */
    protected Ray axis;

    /**
     * Constructor to initialize the tube with a given axis and radius.
     * 
     * @param axis The axis of the tube
     * @param radius The radius of the tube
     */
    public Tube(Ray axis, double radius) {
        super(radius);  // Initialize the radius from the parent class
        this.axis = axis;  // Set the axis of the tube
    }

    /**
     * Gets the normal vector of the tube at a specific point.
     * Currently returns null as the method is not yet implemented.
     * 
     * @param point1 The point on the tube where the normal is needed
     * @return The normal vector at the given point (currently returns null)
     */
    public Vector getNormal(Point point1) {
        return null; // Normal vector calculation is not implemented yet
    }
}
