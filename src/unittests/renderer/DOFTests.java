package unittests.renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import geometries.Sphere;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;
import renderer.BlackBoard.BoardShape;
import renderer.Camera;
import renderer.RayTracerType;

class DOFTests {
	 private final Camera.Builder cameraBuilder = Camera.getBuilder();



	 /**
	     * Test method for advanced depth of field effects
	     */
	    @Test
	    public void DepthOfFieldTest1() {

	        Scene scene = new Scene("advanced depth of field test");

	        Material mat = new Material().setKD(0.5).setKS(0.5).setShininess(100);

	        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(20, 20, 20))); // Increased light intensity
	        scene.geometries.add(
	                new Sphere(new Point(10, 15, -80), 5.0).setEmission(new Color(100, 50, 50)).setMaterial(mat),
	                new Sphere(new Point(5, 10, -40), 5.0).setEmission(new Color(100, 150, 50)).setMaterial(mat),
	                new Sphere(new Point(0, 5, 0), 5.0).setEmission(new Color(50, 50, 100)).setMaterial(mat),
	                new Sphere(new Point(-5, 0, 40), 5.0).setEmission(new Color(50, 100, 50)).setMaterial(mat),
	                new Sphere(new Point(-10, -5, 80), 5.0).setEmission(new Color(50, 100, 100)).setMaterial(mat)
	        );

	        cameraBuilder
	                .setLocation(new Point(-5, 0, 200))
	                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
	                .setVpDistance(150)
	                .setVpSize(40, 40)
	                .setBoardShape(BoardShape.SQUARE)
	               // .setUseAA(true)
	               .setUseDOF(true)
	                .setDepthOfField(160)
	                .setApertureRadius(2)
	                .setAmountOfRays_DOF(36);
	                //.setMultithreading(-1);

	        cameraBuilder
	                .setRayTracer(scene, RayTracerType.SIMPLE) //
	                .setResolution(1000, 1000) //
	                .build()
	                .renderImage()
	                .writeToImage("dofTestdof");
	    }
	    
	    /**
	     * Test method for advanced depth of field effects
	     */
	    @Test
	    public void DepthOfFieldTest2() {

	        Scene scene = new Scene("advanced depth of field test");

	        Material mat = new Material().setKD(0.5).setKS(0.5).setShininess(100);

	        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(20, 20, 20))); // Increased light intensity
	        scene.geometries.add(
	                new Sphere(new Point(10, 15, -80), 5.0).setEmission(new Color(100, 50, 50)).setMaterial(mat),
	                new Sphere(new Point(5, 10, -40), 5.0).setEmission(new Color(100, 150, 50)).setMaterial(mat),
	                new Sphere(new Point(0, 5, 0), 5.0).setEmission(new Color(50, 50, 100)).setMaterial(mat),
	                new Sphere(new Point(-5, 0, 40), 5.0).setEmission(new Color(50, 100, 50)).setMaterial(mat),
	                new Sphere(new Point(-10, -5, 80), 5.0).setEmission(new Color(50, 100, 100)).setMaterial(mat)
	        );

	        cameraBuilder
	                .setLocation(new Point(-5, 0, 200))
	                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
	                .setVpDistance(150)
	                .setVpSize(40, 40);
	        cameraBuilder
	                .setRayTracer(scene, RayTracerType.SIMPLE)
	                .setResolution(1000, 1000)
	                .build()
	                .renderImage()
	                .writeToImage("dof_testnone");
	    }
	    
	    /**
	     * Test method for advanced depth of field effects
	     */
	    @Test
	    public void DepthOfFieldTest3() {

	        Scene scene = new Scene("advanced depth of field test");

	        Material mat = new Material().setKD(0.5).setKS(0.5).setShininess(100);

	        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(20, 20, 20))); // Increased light intensity
	        scene.geometries.add(
	                new Sphere(new Point(10, 15, -80), 5.0).setEmission(new Color(100, 50, 50)).setMaterial(mat),
	                new Sphere(new Point(5, 10, -40), 5.0).setEmission(new Color(100, 150, 50)).setMaterial(mat),
	                new Sphere(new Point(0, 5, 0), 5.0).setEmission(new Color(50, 50, 100)).setMaterial(mat),
	                new Sphere(new Point(-5, 0, 40), 5.0).setEmission(new Color(50, 100, 50)).setMaterial(mat),
	                new Sphere(new Point(-10, -5, 80), 5.0).setEmission(new Color(50, 100, 100)).setMaterial(mat)
	        );

	        cameraBuilder
	                .setLocation(new Point(-5, 0, 200))
	                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
	                .setVpDistance(150)
	                .setVpSize(40, 40)
	                .setBoardShape(BoardShape.SQUARE)
	                .setUseAA(true)
	                .setAmountOfRays_AA(64);
	        cameraBuilder
	                .setRayTracer(scene, RayTracerType.SIMPLE)
	                .setResolution(1000, 1000)
	                .build()
	                .renderImage()
	                .writeToImage("dof_testaa");
	    }
}
