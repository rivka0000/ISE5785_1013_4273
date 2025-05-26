package primitives;

import java.util.List;
import geometries.Intersectable.Intersection;


/**
 * Represents a ray in 3D space.
 * A ray is defined by its origin point and direction vector.
 */
public class Ray {
    /** The origin point of the ray */
    private final Point head;
    
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
        this.head = origin; // Set the origin of the ray
        this.direction = direction.normalize();  // Normalize the direction vector to ensure it has unit length
    }

    /**
     * Gets the origin point of the ray.
     * @return the origin point
     */
    public Point getP0() {
    	return head;
    }
    
    
    /**
     * Getter for the point on the ray at a certain distance from the head
     * @param t the distance from the head
     * @return the point on the ray at the distance t from the head
     */
    public Point getPoint(double t) {
        // if t is zero, return the head point
        if(Util.isZero(t))
            return head;
        return head.add(direction.scale(t));
    }

    /**
     * Gets the direction vector of the ray.
     * @return the direction vector
     */
    public Vector getDir() {
        return direction;
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
        return (obj instanceof Ray other) && this.head.equals(other.head) && this.direction.equals(other.direction); // Compare the origin and direction
    }

    /**
     * Returns a string representation of the ray.
     * 
     * @return A string representing the ray's origin and direction
     */
    @Override
    public String toString() {
        return "head=" + head + ", direction=" + direction; // Return a string with both the origin and direction
    }
    
    /**
     * Finds the closest point to the ray's start from a list of points.
     * @param points List of Point objects
     * @return The closest point to the ray's origin, or null if list is empty or null
     */
    public Point findClosestPoint(List<Point> points) {
    	if (points == null || points.isEmpty()) {
            return null; // Handle null or empty list safely
        }

        Point closest = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Point point : points) {
            double distanceSquared = head.distanceSquared(point);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = point;
            }
        }

        return closest;
    }
    
    

    /**
     * Finds the closest intersection point to the ray's origin from a list of intersections.
     *
     * @param intersections List of Intersection objects
     * @return The closest Intersection, or null if list is null or empty
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        double minDistanceSquared = Double.POSITIVE_INFINITY;
        Intersection closest = null;

        for (Intersection intersection : intersections) {
            double distanceSquared = head.distanceSquared(intersection.point);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = intersection;
            }
        }

        return closest;
    }
    
    
}
