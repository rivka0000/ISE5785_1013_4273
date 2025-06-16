package geometries;

import primitives.Point;
import primitives.Ray;
import lighting.LightSource;
import primitives.Material;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all geometries that can be intersected by a ray.
 */
public abstract class Intersectable {

    /**
     * Passive data structure representing a point of intersection between a ray and a geometry.
     */
    public static class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;

        // Used for shading (Phong model)
        public Vector v;         // Ray direction
        public Vector normal;    // Geometry normal
        public double vNormal;   // v · n

        public LightSource light; // Light source used
        public Vector l;          // Light direction
        public double lNormal;    // l · n

        /**
         * Constructs an Intersection object with the given geometry and point.
         * Also initializes the material from the geometry.
         *
         * @param geometry the intersected geometry
         * @param point    the intersection point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? null : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Intersection other = (Intersection) obj;
            return geometry == other.geometry && Objects.equals(point, other.point);
        }

        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    '}';
        }
    }

    /**
     * Main method that calls helper to find all intersections of a ray with this geometry.
     *
     * @param ray the ray to intersect
     * @return list of intersections, or null if none
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Overloaded method: finds intersections up to a maximum distance from ray origin.
     *
     * @param ray         the ray to intersect
     * @param maxDistance the maximum distance allowed for intersections
     * @return list of intersections within range, or null if none
     */
    public final List<Intersection> calculateIntersections(Ray ray, double maxDistance) {
        return calculateIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Helper method to be implemented by concrete geometries.
     *
     * @param ray the ray to intersect
     * @return list of intersections, or null
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Optional overload to support distance-based shadow filtering.
     * Default implementation falls back to non-filtered version.
     *
     * @param ray         the ray to intersect
     * @param maxDistance the max distance to consider
     * @return list of intersections, or null
     */
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Utility method that extracts only the intersection points.
     *
     * @param ray the ray to intersect
     * @return list of points (without geometry info), or null
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(inter -> inter.point).toList();
    }
}
