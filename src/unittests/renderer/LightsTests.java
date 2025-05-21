package unittests.renderer;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import renderer.*;
import scene.Scene;


/**
 * Test rendering a basic image
 * @author Dan Zilberstein
 */
class LightsTests {
   /** Default constructor to satisfy JavaDoc generator */
   LightsTests() { /* to satisfy JavaDoc generator */ }

   /** First scene for some of tests */
   private final Scene scene1 = new Scene("Test scene");

   /** Second scene for some of tests */
   private final Scene scene2 = new Scene("Test scene")
      .setAmbientLight(new AmbientLight(new Color(38, 38, 38)));

   /** First camera builder for some of tests */
   private final Camera.Builder camera1 = Camera.getBuilder()
      .setRayTracer(scene1, RayTracerType.SIMPLE)
      .setLocation(new Point(0, 0, 1000))
      .setDirection(Point.ZERO, Vector.AXIS_Y)
      .setVpSize(150, 150).setVpDistance(1000);

   /** Second camera builder for some of tests */
   private final Camera.Builder camera2 = Camera.getBuilder()
      .setRayTracer(scene2, RayTracerType.SIMPLE)
      .setLocation(new Point(0, 0, 1000))
      .setDirection(Point.ZERO, Vector.AXIS_Y)
      .setVpSize(200, 200).setVpDistance(1000);

   private static final int SHININESS = 301;
   private static final double KD = 0.5;
   private static final Double3 KD3 = new Double3(0.2, 0.6, 0.4);
   private static final double KS = 0.5;
   private static final Double3 KS3 = new Double3(0.2, 0.4, 0.3);

   private final Material material = new Material().setKD(KD3).setKS(KS3).setShininess(SHININESS);
   private final Color trianglesLightColor = new Color(800, 500, 250);
   private final Color sphereLightColor = new Color(800, 500, 0);
   private final Color sphereColor = new Color(BLUE).reduce(2);

   private final Point sphereCenter = new Point(0, 0, -50);
   private static final double SPHERE_RADIUS = 50d;

   private final Point[] vertices = {
      new Point(-110, -110, -150),
      new Point(95, 100, -150),
      new Point(110, -110, -150),
      new Point(-75, 78, 100)
   };

   private final Point sphereLightPosition = new Point(-50, -50, 25);
   private final Vector sphereLightDirection = new Vector(1, 1, -0.5);
   private final Point trianglesLightPosition = new Point(30, 10, -100);
   private final Vector trianglesLightDirection = new Vector(-2, -2, -2);

   private final Geometry sphere = new Sphere(sphereCenter, SPHERE_RADIUS)
      .setEmission(sphereColor)
      .setMaterial(new Material().setKD(KD).setKS(KS).setShininess(SHININESS));

   private final Geometry triangle1 = new Triangle(vertices[0], vertices[1], vertices[2])
      .setMaterial(material);

   private final Geometry triangle2 = new Triangle(vertices[0], vertices[1], vertices[3])
      .setMaterial(material);

   @Test
   void sphereDirectional() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new DirectionalLight(sphereLightColor, sphereLightDirection));

      camera1.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightSphereDirectional");
   }

   @Test
   void spherePoint() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new PointLight(sphereLightColor, sphereLightPosition)
         .setKl(0.001).setKq(0.0002));

      camera1.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightSpherePoint");
   }

   @Test
   void sphereSpot() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, sphereLightDirection)
         .setKl(0.001).setKq(0.0001));

      camera1.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightSphereSpot");
   }

   @Test
   void trianglesDirectional() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new DirectionalLight(trianglesLightColor, trianglesLightDirection));

      camera2.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightTrianglesDirectional");
   }

   @Test
   void trianglesPoint() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new PointLight(trianglesLightColor, trianglesLightPosition)
         .setKl(0.001).setKq(0.0002));

      camera2.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightTrianglesPoint");
   }

   @Test
   void trianglesSpot() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection)
         .setKl(0.001).setKq(0.0001));

      camera2.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightTrianglesSpot");
   }

   @Test
   void sphereSpotSharp() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, new Vector(1, 1, -0.5))
         .setKl(0.001).setKq(0.00004).setNarrowBeam(10));

      camera1.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightSphereSpotSharp");
   }

   @Test
   void trianglesSpotSharp() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection)
         .setKl(0.001).setKq(0.00004).setNarrowBeam(10));

      camera2.setResolution(500, 500)
         .build()
         .renderImage()
         .writeToImage("lightTrianglesSpotSharp");
   }
}
