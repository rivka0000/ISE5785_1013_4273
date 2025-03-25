package primitives;

/**
 * Represents a point in 3D space.
 * A point is defined by its coordinates (x, y, z), stored in a Double3 object.
 */
public class Point {
    /** The coordinates of the point, represented as a Double3 object */
    protected final Double3 xyz;
    
    /** The origin point (0, 0, 0) */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructor to initialize a point with the given coordinates (x, y, z).
     * 
     * @param xyz The coordinates of the point
     */
    public Point(Double3 xyz) {
        this.xyz = xyz; // Set the coordinates of the point
    }

    /**
     * Constructor to initialize a point with the given x, y, and z values.
     * 
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @param z The z-coordinate of the point
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z); // Create a Double3 object for the coordinates
    }

    /**
     * Subtracts another point from this point, returning the resulting vector.
     * 
     * @param other The point to subtract
     * @return A vector representing the difference between the two points
     */
    public Vector subtract(Point other) {
        return new Vector(this.xyz.subtract(other.xyz)); // Return the vector from this point to the other point
    }

    /**
     * Adds a vector to this point, returning the resulting point.
     * 
     * @param vector The vector to add
     * @return A new point resulting from adding the vector to this point
     */
    public Point add(Vector vector) {
        return new Point(this.xyz.add(vector.xyz)); // Add the vector to the point's coordinates
    }

    /**
     * Calculates the squared distance between this point and another point.
     * 
     * @param other The other point
     * @return The squared distance between the two points
     */
    public double distanceSquared(Point other) {
        double dx = this.xyz.d1() - other.xyz.d1(); // Difference in x-coordinates
        double dy = this.xyz.d2() - other.xyz.d2(); // Difference in y-coordinates
        double dz = this.xyz.d3() - other.xyz.d3(); // Difference in z-coordinates
        return dx * dx + dy * dy + dz * dz; // Squared distance formula
    }

    /**
     * Calculates the distance between this point and another point.
     * 
     * @param other The other point
     * @return The distance between the two points
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other)); // Return the square root of the squared distance
    }

    /**
     * Returns a string representation of the point.
     * 
     * @return A string representing the point's coordinates
     */
    @Override
    public String toString() {
        return this.xyz.toString(); // Return the string representation of the coordinates
    }

    /**
     * Checks if this point is equal to another object.
     * Two points are equal if their coordinates are the same.
     * 
     * @param obj The object to compare with
     * @return true if the points are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check if they are the same object
        return (obj instanceof Point other) && this.xyz.equals(other.xyz); // Check if the coordinates are the same
    }
}
