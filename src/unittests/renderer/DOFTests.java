package unittests.renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
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
	    
	    /**
	     * Test for advanced depth of field rendering
	     * This test creates a scene with multiple spheres at different distances
	     * and applies depth of field rendering to create a realistic focus effect.
	     */
	    @Test
	    public void Depth_Of_Field_Test() {
	        Scene scene = new Scene("DOF with transparent triangles on bottom row");

	        Material sphereMat = new Material()
	                .setKD(0.3)
	                .setKS(0.7)
	                .setShininess(500);

	        // 3 מקורות אור
	        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(40, 40, 40)));
	        scene.lights.add(new PointLight(new Color(300, 150, 150), new Point(-50, 60, 100)));
	        scene.lights.add(new DirectionalLight(new Color(200, 200, 250), new Vector(-1, -1, -1)));

	        // גריד
	        int rows = 4;
	        int columns = 7;
	        double spacing = 10;
	        int radius = 5;
	        double startX = -columns / 2.0 * spacing;
	        double startY = -rows / 2.0 * spacing;

	        for (int row = 0; row < rows; row++) {
	            for (int col = 0; col < columns; col++) {
	                double x = startX + col * spacing;
	                double y = startY + row * spacing;
	                double z = -120 + col * 40;

	                int r = (50 + (row * 80)) % 256;
	                int g = (100 + (col * 60)) % 256;
	                int b = (150 + (row * 30 + col * 20)) % 256;

	                Color color = new Color(r, g, b);

	                if (row == rows - 1) {
	                    // 🔷 רק השורה האחרונה → משולשים שקופים
	                    scene.geometries.add(
	                        new Triangle(
	                            new Point(x, y, z),
	                            new Point(x + 3, y + 5, z + 2),
	                            new Point(x - 3, y + 5, z + 2)
	                        ).setEmission(color)
	                         .setMaterial(new Material()
	                             .setKD(0.3)
	                             .setKS(0.5)
	                             .setShininess(100)
	                             .setKt(0.4)) // ✅ שקיפות מתונה יותר
	                    );
	                } else {
	                    // שאר השורות → כדורים
	                    scene.geometries.add(
	                        new Sphere(new Point(x, y, z), radius)
	                            .setEmission(color)
	                            .setMaterial(sphereMat)
	                    );
	                }
	            }
	        }

	        // רצפה
	        scene.geometries.add(
	            new Plane(new Point(0, -25, 0), new Vector(0, 1, 0))
	                .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
	                .setEmission(new Color(100, 100, 100))
	        );

	        // מצלמה עם DOF ו-81 קרניים
	        Camera.getBuilder()
	                .setResolution(1000, 1000)
	                .setBoardShape(BoardShape.SQUARE)
	                .setRayTracer(scene, RayTracerType.SIMPLE)
	                .setVpDistance(100)
	                .setVpSize(40, 40)
	                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
	                .setLocation(new Point(0, 0, 200))
	                .setDepthOfField(100)
	                .setAmountOfRays_DOF(81)
	                .setApertureRadius(0.6)
	                .setUseDOF(true)
	                .build()
	                .renderImage()
	                .writeToImage("Depth_Of_Field");
	    }

	    
	    
	    /**
	     * Same scene as Depth_Of_Field_Test but without DOF effect
	     * Required for MP1 to show comparison between ON/OFF
	     */
	    @Test
	    public void Depth_Of_Field_Test_OFF() {
	        Scene scene = new Scene("DOF OFF - transparent triangles on bottom row");

	        Material sphereMat = new Material()
	                .setKD(0.3)
	                .setKS(0.7)
	                .setShininess(500);

	        // 3 מקורות אור
	        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(40, 40, 40)));
	        scene.lights.add(new PointLight(new Color(300, 150, 150), new Point(-50, 60, 100)));
	        scene.lights.add(new DirectionalLight(new Color(200, 200, 250), new Vector(-1, -1, -1)));

	        int rows = 4;
	        int columns = 7;
	        double spacing = 10;
	        int radius = 5;
	        double startX = -columns / 2.0 * spacing;
	        double startY = -rows / 2.0 * spacing;

	        for (int row = 0; row < rows; row++) {
	            for (int col = 0; col < columns; col++) {
	                double x = startX + col * spacing;
	                double y = startY + row * spacing;
	                double z = -120 + col * 40;

	                int r = (50 + (row * 80)) % 256;
	                int g = (100 + (col * 60)) % 256;
	                int b = (150 + (row * 30 + col * 20)) % 256;

	                Color color = new Color(r, g, b);

	                if (row == rows - 1) {
	                    // משולשים שקופים – שורה אחרונה
	                    scene.geometries.add(
	                        new Triangle(
	                            new Point(x, y, z),
	                            new Point(x + 3, y + 5, z + 2),
	                            new Point(x - 3, y + 5, z + 2)
	                        ).setEmission(color)
	                         .setMaterial(new Material()
	                             .setKD(0.3)
	                             .setKS(0.5)
	                             .setShininess(100)
	                             .setKt(0.4))
	                    );
	                } else {
	                    // כדורים – שורות עליונות
	                    scene.geometries.add(
	                        new Sphere(new Point(x, y, z), radius)
	                            .setEmission(color)
	                            .setMaterial(sphereMat)
	                    );
	                }
	            }
	        }

	        scene.geometries.add(
	            new Plane(new Point(0, -25, 0), new Vector(0, 1, 0))
	                .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
	                .setEmission(new Color(100, 100, 100))
	        );

	        // מצלמה זהה – ללא DOF
	        Camera.getBuilder()
	                .setResolution(1000, 1000)
	                .setBoardShape(BoardShape.SQUARE)
	                .setRayTracer(scene, RayTracerType.SIMPLE)
	                .setVpDistance(100)
	                .setVpSize(40, 40)
	                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
	                .setLocation(new Point(0, 0, 200))
	                .setUseDOF(false) // ❌ DOF כבוי
	                .build()
	                .renderImage()
	                .writeToImage("Depth_Of_Field_OFF");
	    }

	    
}
