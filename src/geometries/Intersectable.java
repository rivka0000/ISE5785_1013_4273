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

	    // נתונים לחישובי מודל פונג (יאוכלסו ע"י SimpleRayTracer)
	    public Vector v;         // כיוון הקרן
	    public Vector normal;    // הנורמל לפני הגוף
	    public double vNormal;   // v · n

	    public LightSource light; // מקור האור
	    public Vector l;          // כיוון ממקור האור לנקודת החיתוך
	    public double lNormal;    // l · n

	    /**
	     * Constructs an Intersection object with the given geometry and point.
	     * Also initializes the material from the geometry.
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
     * Template method that delegates the actual intersection computation to a subclass.
     * 
     * @param ray the ray to intersect with
     * @return a list of intersections, or null if none
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Abstract method that must be implemented by subclasses to calculate intersections.
     * 
     * @param ray the ray to intersect with
     * @return a list of intersections, or null if none
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Convenience method that extracts only the intersection points from the Intersection data.
     * 
     * @param ray the ray to check for intersections
     * @return a list of intersection points, or null if none
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(inter -> inter.point).toList();
    }
}
