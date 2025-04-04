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
        Point p0 = axisRay.getP0();
        Vector dir = axisRay.getDir();
        Vector p0ToPoint = point.subtract(p0);
        double t = dir.dotProduct(p0ToPoint);

        if (t <= 0) {
            // בסיס תחתון
            return dir.scale(-1);
        }
        if (t >= height) {
            // בסיס עליון
            return dir;
        }

        // מעטפת צדדית
        Point o = p0.add(dir.scale(t));
        return point.subtract(o).normalize();
    }
}
