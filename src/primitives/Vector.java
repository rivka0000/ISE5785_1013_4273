package primitives;

/**
 * Represents a vector in 3D space.
 * A vector is a directed quantity with magnitude and direction.
 * It is derived from the Point class, but with additional vector operations.
 */
public class Vector extends Point {

	public static final Vector AXIS_X = new Vector(1, 0, 0);
	public static final Vector AXIS_Y = new Vector(0, 1, 0);
	public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize a vector with specified x, y, and z coordinates.
     * Ensures the vector is not a zero vector.
     * 
     * @param x The x-coordinate of the vector
     * @param y The y-coordinate of the vector
     * @param z The z-coordinate of the vector
     * @throws IllegalArgumentException if the vector is a zero vector
     */
    public Vector(double x, double y, double z) {
        super(x, y, z); // Initialize the vector using the Point constructor
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed"); // Ensure the vector is not a zero vector
        }
    }

    /**
     * Constructor to initialize a vector with a specified Double3 object.
     * Ensures the vector is not a zero vector.
     * 
     * @param xyz The coordinates of the vector as a Double3 object
     * @throws IllegalArgumentException if the vector is a zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz); // Initialize the vector using the Point constructor
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed"); // Ensure the vector is not a zero vector
        }
    }

    /**
     * Adds two vectors together.
     * 
     * @param other The vector to add to this vector
     * @return A new vector representing the sum of this vector and the other vector
     */
    public Vector add(Vector other) {
        return new Vector(this.xyz.add(other.xyz)); // Add the corresponding coordinates of the two vectors
    }

    /**
     * Scales the vector by a scalar value.
     * 
     * @param scalar The scalar to multiply the vector by
     * @return A new vector that is scaled by the scalar
     */
    public Vector scale(double scalar) {
        return new Vector(this.xyz.scale(scalar)); // Scale the vector by the scalar value
    }

    /**
     * Computes the dot product of this vector and another vector.
     * 
     * @param other The other vector to compute the dot product with
     * @return The dot product of the two vectors
     */
    public double dotProduct(Vector other) {
        return this.xyz.d1() * other.xyz.d1() + 
               this.xyz.d2() * other.xyz.d2() + 
               this.xyz.d3() * other.xyz.d3(); // Calculate the dot product of the two vectors
    }

    /**
     * Computes the cross product of this vector and another vector.
     * 
     * @param other The other vector to compute the cross product with
     * @return A new vector representing the cross product of the two vectors
     */
    public Vector crossProduct(Vector other) {
        return new Vector(
            this.xyz.d2() * other.xyz.d3() - this.xyz.d3() * other.xyz.d2(), // Calculate the components of the cross product
            this.xyz.d3() * other.xyz.d1() - this.xyz.d1() * other.xyz.d3(),
            this.xyz.d1() * other.xyz.d2() - this.xyz.d2() * other.xyz.d1()
        );
    }

    /**
     * Computes the squared length (magnitude squared) of the vector.
     * 
     * @return The squared length of the vector
     */
    public double lengthSquared() {
        return this.dotProduct(this); // The squared length is the dot product of the vector with itself
    }

    /**
     * Computes the length (magnitude) of the vector.
     * 
     * @return The length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared()); // The length is the square root of the squared length
    }

    /**
     * Normalizes the vector to make it a unit vector (length = 1).
     * 
     * @return A new vector that is normalized
     * @throws IllegalArgumentException if the vector is a zero vector
     */
    public Vector normalize() {
        double len = length(); // Get the length of the vector
        if (len == 0) {
            throw new IllegalArgumentException("Cannot normalize zero vector"); // Ensure the vector is not zero-length
        }
        return scale(1 / len); // Scale the vector to make it a unit vector
    }
}


