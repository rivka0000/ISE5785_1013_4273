package unittests.geometries;

import geometries.*;
import primitives.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class GeometriesTests {
	
	@Test
	void testFindIntersections() {
	    // ======================= BVA =======================

	    // אוסף גופים ריק
	    Geometries emptyGeometries = new Geometries();
	    assertNull(emptyGeometries.findIntersections(
	        new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))), 
	        "Empty collection should return null"
	    );

	    // אף צורה לא נחתכת
	    Sphere sphere = new Sphere(new Point(1, 0, 0), 4.0);
	    Triangle triangle = new Triangle(
	        new Point(-1, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
	    Plane plane = new Plane(new Point(1, 0, 0), new Vector(0, 0, 1));
	    Geometries gr1 = new Geometries(sphere, triangle, plane);
	    assertNull(gr1.findIntersections(
	        new Ray(new Point(-4, 6, 2), new Vector(1, 0, 0))),
	        "Ray should not intersect any shape"
	    );

	    // רק צורה אחת נחתכת
	    assertEquals(1,
	        gr1.findIntersections(
	            new Ray(new Point(-3, -4, -1), new Vector(4, -3, 1))
	        ).size(),
	        "Only one shape should intersect"
	    );

	    // כל הצורות נחתכות
	    assertEquals(4,
	        gr1.findIntersections(
	            new Ray(new Point(0, 0.5, 6), new Vector(0, 0, -12))
	        ).size(),
	        "All shapes should intersect"
	    );

	    // ======================= EP =======================

	    // כמה צורות (אך לא כולן) נחתכות
	    assertEquals(3,
	        gr1.findIntersections(
	            new Ray(new Point(0, 3, 6), new Vector(0, 0, -12))
	        ).size(),
	        "Only some shapes should intersect"
	    );
	}


}
