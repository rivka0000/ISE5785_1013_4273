package geometries;

import java.util.List;
import primitives.Point;
import primitives.Ray;

/**
 * Interface representing objects that can be intersected by a ray.
 * Any geometric shape that wants to allow intersection computations
 * should implement this interface.
 */
public interface Intersectable {

    /**
     * Finds all intersection points between the geometry and the given ray.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of intersection points, or {@code null} if there are no intersections
     */
    List<Point> findIntersections(Ray ray);
}

