package geometries;

import java.util.LinkedList;
import java.util.List;

import primitives.Point;
import primitives.Ray;

public class Geometries implements Intersectable {
	
	    // A list of intersectable geometric bodies (cannot be reassigned, only modified)
	    private final List<Intersectable> geometries = new LinkedList<>();
	    
	    // בנאי ברירת מחדל (ריק)
	    public Geometries() {}

	    // בנאי עם החתימה שניתנה
	    public Geometries(Intersectable... geometries) {
	        add(geometries); // שימוש במתודת add להוספת הגופים לאגד
	    }

	    // מתודה להוספת גופים לאגד
	    public void add(Intersectable... geometries) {
	        for (Intersectable geometry : geometries) {
	            this.geometries.add(geometry); // הוספת כל גוף לרשימה
	        }
	    }

	    // מימוש מתודת findIntersections (כרגע מחזירה null)
	    @Override
	    public List<Point> findIntersections(Ray ray) {
	        List<Point> intersections = null;

	        for (Intersectable item : geometries) {
	            List<Point> itemIntersections = item.findIntersections(ray);
	            if (itemIntersections != null) {
	                if (intersections == null) {
	                    // אתחול ראשון של הרשימה ברגע שמוצאים חיתוך ראשון
	                    intersections = new LinkedList<>();
	                }
	                intersections.addAll(itemIntersections);
	            }
	        }

	        return intersections;
	    }


}
