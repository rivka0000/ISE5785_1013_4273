package geometries;

import java.util.LinkedList;
import java.util.List;

import primitives.Ray;

/**
 * Composite class representing a collection of geometries.
 */
public class Geometries extends Intersectable {

    /** A list of intersectable geometric bodies */
    private final List<Intersectable> geometries = new LinkedList<>();

    /** Default constructor */
    public Geometries() {}

    /**
     * Constructor that takes a variable number of geometries to add.
     * 
     * @param geometries the geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the collection.
     * 
     * @param geometries the geometries to add
     */
    public void add(Intersectable... geometries) {
        for (Intersectable geometry : geometries) {
            this.geometries.add(geometry);
        }
    }

    /**
     * Implementation of the abstract method from Intersectable.
     * Returns all intersections between the given ray and the geometries in the collection.
     *
     * @param ray the ray to intersect with the geometries
     * @return list of intersections, or null if none
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> result = null;

        for (Intersectable geometry : geometries) {
            List<Intersection> itemIntersections = geometry.calculateIntersections(ray);
            if (itemIntersections != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(itemIntersections);
            }
        }

        return result;
    }
}
