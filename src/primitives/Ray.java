package primitives;

/**
 * Represents a ray in 3D space.
 * A ray is defined by its origin point and direction vector.
 */
public class Ray {
    /** The origin point of the ray */
    private final Point origin;
    
    /** The direction vector of the ray */
    private final Vector direction;

    /**
     * Constructor to initialize a ray with a given origin and direction.
     * 
     * @param origin The origin point of the ray
     * @param direction The direction vector of the ray
     * @throws IllegalArgumentException if either the origin or direction is null
     */
    public Ray(Point origin, Vector direction) {
        if (origin == null || direction == null) {
            throw new IllegalArgumentException("Origin or direction cannot be null"); // Ensure no null values are passed
        }
        this.origin = origin; // Set the origin of the ray
        this.direction = direction.normalize();  // Normalize the direction vector to ensure it has unit length
    }

    /**
     * Checks if this ray is equal to another object.
     * Two rays are equal if their origin and direction are the same.
     * 
     * @param obj The object to compare with
     * @return true if the rays are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check if they are the same object
        return (obj instanceof Ray other) && this.origin.equals(other.origin) && this.direction.equals(other.direction); // Compare the origin and direction
    }

    /**
     * Returns a string representation of the ray.
     * 
     * @return A string representing the ray's origin and direction
     */
    @Override
    public String toString() {
        return "origin=" + origin + ", direction=" + direction; // Return a string with both the origin and direction
    }
}
