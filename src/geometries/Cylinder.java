package geometries;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;

/**
 * Represents a cylinder geometry, inheriting from the Tube class.
 * This class stores the height of the cylinder and provides methods for operations.
 */
public class Cylinder extends Tube {
    /** The height of the cylinder */
    private double height;

    /**
     * Constructor to initialize the cylinder with an axis, radius, and height.
     * 
     * @param axis The axis ray of the cylinder
     * @param radius The radius of the cylinder
     * @param height The height of the cylinder
     */
    public Cylinder(Ray axis, double radius, double height) {
        super(axis, radius);  
        this.height = height;
    } 
     
    /**
     * Gets the normal vector of the cylinder at a specific point.
     * 
     * @param point1 The point on the cylinder where the normal is needed
     * @return The normal vector at the given point (currently returns null)
     */
    @Override
    public Vector getNormal(Point point) {
        double t = axisRay.getDir().dotProduct(point.subtract(axisRay.getP0()));

        if (t <= 0) {
            // בסיס תחתון
            return axisRay.getDir().scale(-1);
        }
        if (t >= height) {
            // בסיס עליון
            return axisRay.getDir();
        }

        // מעטפת צדדית
        Point o = axisRay.getPoint(t);
        return point.subtract(o).normalize();
    }

}
