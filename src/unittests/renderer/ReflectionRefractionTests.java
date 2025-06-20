package unittests.renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import renderer.*;
import scene.Scene;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 * @author Dan Zilberstein
 */
class ReflectionRefractionTests {
   /** Default constructor to satisfy JavaDoc generator */
   ReflectionRefractionTests() { /* to satisfy JavaDoc generator */ }

   /** Scene for the tests */
   private final Scene          scene         = new Scene("Test scene");
   /** Camera builder for the tests with triangles */
   private final Camera.Builder cameraBuilder = Camera.getBuilder()     //
      .setRayTracer(scene, RayTracerType.SIMPLE);

   /** Produce a picture of a sphere lighted by a spot light */
   @Test
   void twoSpheres() {
      scene.geometries.add( //
                           new Sphere(new Point(0, 0, -50), 50d).setEmission(new Color(BLUE)) //
                              .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(100).setKt(0.3)), //
                           new Sphere(new Point(0, 0, -50), 25d).setEmission(new Color(RED)) //
                              .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))); //
      scene.lights.add( //
                       new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                          .setKl(0.0004).setKq(0.0000006));

      cameraBuilder
         .setLocation(new Point(0, 0, 1000)) //
         .setDirection(Point.ZERO, Vector.AXIS_Y) //
         .setVpDistance(1000).setVpSize(150, 150) //
         .setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("refractionTwoSpheres");
   }
   /** Produce a picture of a sphere lighted by a spot light */
   @Test
   void twoSpheresOnMirrors() {
      scene.geometries.add( //
                           new Sphere(new Point(-950, -900, -1000), 400d).setEmission(new Color(0, 50, 100)) //
                              .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20) //
                                 .setKt(new Double3(0.5, 0, 0))), //
                           new Sphere(new Point(-950, -900, -1000), 200d).setEmission(new Color(100, 50, 20)) //
                              .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20)), //
                           new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                                        new Point(670, 670, 3000)) //
                              .setEmission(new Color(20, 20, 20)) //
                              .setMaterial(new Material().setKr(1.0)), //
                           new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                                        new Point(-1500, -1500, -2000)) //
                              .setEmission(new Color(20, 20, 20)) //
                              .setMaterial(new Material().setKr(new Double3(0.5, 0, 0.4))));
      scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26)));
      scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
         .setKl(0.00001).setKq(0.000005));

      cameraBuilder
         .setLocation(new Point(0, 0, 10000)) //
         .setDirection(Point.ZERO, Vector.AXIS_Y) //
         .setVpDistance(10000).setVpSize(2500, 2500) //
         .setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("reflectionTwoSpheresMirrored");
   }
   
   /**
    * Produce a picture of a two triangles lighted by a spot light with a
    * partially
    * transparent Sphere producing partial shadow
    */
   @Test
   void trianglesTransparentSphere() {
      scene.geometries.add(
                           new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                                        new Point(75, 75, -150))
                              .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                           new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                              .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                           new Sphere(new Point(60, 50, -50), 30d).setEmission(new Color(BLUE))
                              .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKt(0.6)));
      scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
      scene.lights.add(
                       new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                          .setKl(4E-5).setKq(2E-7));

      cameraBuilder
         .setLocation(new Point(0, 0, 1000)) //
         .setDirection(Point.ZERO, Vector.AXIS_Y) //
         .setVpDistance(1000).setVpSize(200, 200) //
         .setResolution(600, 600) //
         .build() //
         .renderImage() //
         .writeToImage("refractionShadow");
   }
   
   /**
    * Final refined rendering test for submission.
    * This version combines all visual effects in a clean, aesthetic, and professional way.
    * Includes: transparency, reflection, partial shadow, multiple light sources, and well-spaced geometries.
    */
   @Test
   void finalProfessionalScene() {
       Scene scene = new Scene("Professional Combined Effects Scene");

       // Transparent cyan rectangle in the front (no overlap with other objects)
       scene.geometries.add(
           new Polygon(
               new Point(-80, -40, -120), new Point(80, -40, -120),
               new Point(80, 40, -120), new Point(-80, 40, -120))
               .setEmission(new Color(0, 255, 255))
               .setMaterial(new Material().setKt(0.6).setKD(0.1).setKS(0.3).setShininess(100))
       );

       // Orange sphere offset slightly backwards and down
       scene.geometries.add(
           new Sphere(new Point(0, -20, -170), 35)
               .setEmission(new Color(255, 140, 0))
               .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(120))
       );

       // Reflective triangle mirror angled on the side
       scene.geometries.add(
           new Triangle(
               new Point(60, -60, -180), new Point(160, 80, -250), new Point(60, 100, -180))
               .setEmission(new Color(50, 50, 50))
               .setMaterial(new Material().setKr(1.0))
       );

       // Reflective green floor under everything
       scene.geometries.add(
           new Polygon(
               new Point(-150, -80, -250), new Point(150, -80, -250),
               new Point(150, -75, -250), new Point(-150, -75, -250))
               .setEmission(new Color(0, 100, 0))
               .setMaterial(new Material().setKr(new Double3(0.3)).setKD(0.3).setKS(0.3).setShininess(50))
       );

       // Ambient light
       scene.setAmbientLight(new AmbientLight(new Color(40, 40, 40)));

       // Main spot light from top right
       scene.lights.add(
           new SpotLight(new Color(1000, 600, 600), new Point(150, 150, 150), new Vector(-1.5, -1.5, -2))
               .setKl(4E-5).setKq(2E-7)
       );

       // Additional point light from below left
       scene.lights.add(
           new PointLight(new Color(250, 150, 250), new Point(-100, -100, 150))
               .setKl(1E-5).setKq(1E-6)
       );

       // Directional light for soft balance
       scene.lights.add(
           new DirectionalLight(new Color(100, 100, 150), new Vector(1, -0.3, -1))
       );

       // Camera setup
       Camera camera = Camera.getBuilder()
           .setLocation(new Point(0, 0, 600))
           .setDirection(new Vector(0, 0, -1))
           .setVpSize(250, 250)
           .setVpDistance(600)
           .setResolution(800, 800)
           .setRayTracer(scene, RayTracerType.SIMPLE)
           .build();

       camera.renderImage();
       camera.writeToImage("finalProfessionalScene");
   }


}
