package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract class representing a geometric shape.
 * This class provides the abstract method for calculating the normal vector at a given point.
 */
public abstract class Geometry {
    
    /**
     * Abstract method to get the normal vector of the geometry at a specific point.
     * 
     * @param point The point on the geometry where the normal is needed
     * @return The normal vector at the given point
     */
    public abstract Vector getNormal(Point point);
}
