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
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getP0();        
        Vector dir = ray.getDir();     
 
        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        Vector edge1 = v2.subtract(v1);
        Vector edge2 = v3.subtract(v1);
        Vector normal = edge1.crossProduct(edge2).normalize();

        double nv = Util.alignZero(normal.dotProduct(dir));
        if (Util.isZero(nv)) {
            return null; 
        }

        Vector q0_p0 = v1.subtract(p0);
        double t = Util.alignZero(normal.dotProduct(q0_p0) / nv);
        if (t <= 0) {
            return null; 
        }

        Point p = ray.getPoint(t);

        // מניעת מקרה של נקודה בדיוק על אחד הקודקודים
        if (p.equals(v1) || p.equals(v2) || p.equals(v3)) {
            return null;
        }

        // מניעת מקרה של נקודה בדיוק על צלע (לפי כיוון ווקטורים)
        try {
            Vector v1p = p.subtract(v1);
            Vector v2p = p.subtract(v2);
            Vector v3p = p.subtract(v3);

            Vector n1 = (v2.subtract(v1)).crossProduct(v1p);
            Vector n2 = (v3.subtract(v2)).crossProduct(v2p);
            Vector n3 = (v1.subtract(v3)).crossProduct(v3p);

            double s1 = Util.alignZero(normal.dotProduct(n1));
            double s2 = Util.alignZero(normal.dotProduct(n2));
            double s3 = Util.alignZero(normal.dotProduct(n3));

            if (s1 > 0 && s2 > 0 && s3 > 0 || s1 < 0 && s2 < 0 && s3 < 0) {
                return List.of(p);
            }
        } catch (IllegalArgumentException e) {
            // נלכד וקטור אפס => קרן עברה על צלע => גבול => null
            return null;
        }

        return null;
    }

}

