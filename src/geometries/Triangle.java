package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

/**
 * Represents a triangle, which is a specific type of polygon.
 * A triangle is defined by three points and inherits from the {@link Polygon} class.
 */
public class Triangle extends Polygon {
 
    /**
     * Constructs a triangle with the given three vertices.
     *
     * @param p1 the first vertex of the triangle
     * @param p2 the second vertex of the triangle
     * @param p3 the third vertex of the triangle
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }  

    /** 
     * Finds the intersection points between the triangle and the given ray.
     * Delegates the intersection logic to the {@link Polygon} implementation, or
     * implements specific triangle intersection logic if needed.
     *
     * @param ray the ray to test for intersection with the triangle
     * @return a list of intersection points, or {@code null} if there are no intersections
     */
   
    
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Point> points = super.plane.findIntersections(ray); // חיתוך עם המישור בלבד
        if (points == null) return null;

        Point p = points.get(0);
        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        Vector n = plane.getNormal(); // הוקטור האנכי למישור

        try {
            Vector v1p = p.subtract(v1);
            Vector v2p = p.subtract(v2);
            Vector v3p = p.subtract(v3);

            Vector n1 = v2.subtract(v1).crossProduct(v1p);
            Vector n2 = v3.subtract(v2).crossProduct(v2p);
            Vector n3 = v1.subtract(v3).crossProduct(v3p);

            double s1 = Util.alignZero(n.dotProduct(n1));
            double s2 = Util.alignZero(n.dotProduct(n2));
            double s3 = Util.alignZero(n.dotProduct(n3));

            if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
                return List.of(new Intersection(this, p));
            }
        } catch (IllegalArgumentException e) {
            return null;
        }

        return null;
    }


}

