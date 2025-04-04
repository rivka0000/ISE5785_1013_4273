package geometries;

/**
 * Abstract class representing a radial geometry (geometry with a radius).
 * This class stores the radius value and provides it to subclasses.
 */
public abstract class RadialGeometry extends Geometry {
    /** The radius of the radial geometry */
    protected final double radius;

    /**
     * Constructor to initialize the radial geometry with a given radius.
     *
     * @param radius The radius of the geometry
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
    }

    /**
     * Returns the radius of the geometry.
     *
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }
}

