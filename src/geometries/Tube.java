package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

import primitives.*;

/**
 * Represents a tube geometry, inheriting from the RadialGeometry class.
 * A tube is defined by its axis (a Ray) and radius.
 */
public class Tube extends RadialGeometry {
    /** The axis of the tube (represented by a Ray) */
    protected Ray axisRay;

    /**
     * Constructor to initialize the tube with a given axis and radius.
     * 
     * @param axis The axis of the tube
     * @param radius The radius of the tube
     */
    public Tube(Ray axisRay, double radius) {
        super(radius);  // Initialize the radius from the parent class
        this.axisRay = axisRay;  // Set the axis of the tube
    }

    /**
     * Gets the normal vector of the tube at a specific point.
     * Currently returns null as the method is not yet implemented.
     * 
     * @param point1 The point on the tube where the normal is needed
     * @return The normal vector at the given point (currently returns null)
     */
    @Override
    public Vector getNormal(Point point) {
        Point p0 = axisRay.getP0();
        Vector dir = axisRay.getDir();

        Vector p0ToPoint = point.subtract(p0);
        double t = dir.dotProduct(p0ToPoint);

        Point o = p0.add(dir.scale(t));

        Vector normal = point.subtract(o);

        if (normal.lengthSquared() == 0) {
            throw new IllegalArgumentException("Point is on the tube axis, normal is undefined (zero vector)");
        }

        return normal.normalize();
    }

	@Override
	public List<Point> findIntersections(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}
}
