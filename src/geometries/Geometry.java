package geometries;

import primitives.Point;
import primitives.Vector;
import primitives.Color;

/**
 * Abstract class representing a geometric shape.
 * This class provides the abstract method for calculating the normal vector at a given point.
 */
public abstract class Geometry extends Intersectable {
    
	/** Emission color of the geometry – default is black */
	protected Color emission= Color.BLACK;
	
	/**
     * Getter for the emission color of the geometry.
     * 
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Setter for the emission color (builder-style).
     * 
     * @param emission the color to set
     * @return the current geometry instance (for chaining)
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Abstract method to get the normal vector of the geometry at a specific point.
     * 
     * @param point The point on the geometry where the normal is needed
     * @return The normal vector at the given point
     */
    public abstract Vector getNormal(Point point);
}
