package geometries;
import primitives.Point;
/**
 * Represents a triangle, which is a specific type of polygon.
 * Inherits from the Polygon class.
 */
public class Triangle extends Polygon {
	public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }
}
