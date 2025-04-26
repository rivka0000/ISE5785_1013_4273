package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import geometries.Tube;
import primitives.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link geometries.Tube} class
 * 
 * @author Ahuva Winkler, Rivka Azrad
 */
class TubeTests {

    /**
     * Test method for {@link geometries.Tube#getNormal(primitives.Point)}.
     */
	@Test
	void testGetNormal() {
	    // =============== Equivalence Partitions Tests ===============

	    // TC01: Point on the side of the tube – should return a normal perpendicular to the axis
	    Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
	    Tube tube = new Tube(axisRay, 1);

	    Point point = new Point(0, 1, 5);
	    Vector expectedNormal = new Vector(0, 1, 0);
	    assertEquals(expectedNormal, tube.getNormal(point), "TC01: Tube getNormal() returned wrong normal");

	    // =============== Boundary Values Tests ===============

	    // TC11: Point exactly on the tube axis – should throw an exception
	    Point pointOnAxis = new Point(0, 0, 5); // lies on axisRay
	    assertThrows(IllegalArgumentException.class, () -> tube.getNormal(pointOnAxis),
	        "TC11: Expected exception for point on the tube axis");

	    // TC12: Point very close to the axis, within floating point tolerance
	    Point pointNearAxis = new Point(0.1, 1, 10); // slightly offset from axis
	    Vector expectedNormalNear = new Vector(0.1, 1, 0).normalize();
	    Vector actualNormalNear = tube.getNormal(pointNearAxis);

	    assertEquals(1, actualNormalNear.length(), 0.00001, "TC12: Normal vector is not unit length");
	    assertEquals(expectedNormalNear, actualNormalNear, "TC12: Tube getNormal() returned incorrect normal near axis");
	}
}
