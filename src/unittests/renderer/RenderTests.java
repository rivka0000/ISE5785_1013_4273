package unittests.renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.AmbientLight;
import primitives.*;
import renderer.*;
import scene.Scene;
import lighting.SpotLight;


/**
 * Test rendering a basic image
 * @author Dan
 */
public class RenderTests {
   /** Default constructor to satisfy JavaDoc generator */
   public RenderTests() { /* to satisfy JavaDoc generator */ }

   /** Camera builder of the tests */
   private final Camera.Builder camera = Camera.getBuilder() //
      .setLocation(Point.ZERO).setDirection(new Point(0, 0, -1), Vector.AXIS_Y) //
      .setVpDistance(100) //
      .setVpSize(500, 500);

   /**
    * Produce a scene with basic 3D model and render it into a png image with a
    * grid
    */
   @Test
   public void renderTwoColorTest() {
      Scene scene = new Scene("Two color").setBackground(new Color(75, 127, 90))
         .setAmbientLight(new AmbientLight(new Color(255, 191, 191)));
      scene.geometries //
         .add(// center
              new Sphere(new Point(0, 0, -100), 50d),
              // up left
              new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)),
              // down left
              new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100)),
              // down right
              new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100)));

      camera //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .setResolution(1000, 1000) //
         .build() //
         .renderImage() //
         .printGrid(new Color(YELLOW),100) //
         .writeToImage("Two color render test");
   }

   @Test
   public void renderMultiColorTest() {
      Scene scene = new Scene("Multi color")
         .setAmbientLight(new AmbientLight(new Color(51, 51, 51)));

      scene.geometries.add(
         new Sphere(new Point(0, 0, -100), 50),
         new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
            .setEmission(new Color(GREEN)),
         new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
            .setEmission(new Color(RED)),
         new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
            .setEmission(new Color(BLUE))
      );

      camera
         .setRayTracer(scene, RayTracerType.SIMPLE)
         .setResolution(1000, 1000)
         .build()
         .renderImage()
         .printGrid(new Color(WHITE), 100)
         .writeToImage("color render test");
   }


   /** Test for XML based scene - for bonus */
   @Test
   public void basicRenderXml() {
      Scene scene = new Scene("Using XML");
      // enter XML file name and parse from XML file into scene object instead of the
      // new Scene above,
      // Use the code you added in appropriate packages
      // ...
      // NB: unit tests is not the correct place to put XML parsing code

      camera //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .setResolution(1000, 1000) //
         .build() //
         .renderImage() //
         .printGrid( new Color(YELLOW),100) //
         .writeToImage("xml render test");
   }

   /** Test for JSON based scene - for bonus */
   @Test
   public void basicRenderJson() {
      Scene scene = new Scene("Using Json");
      // enter XML file name and parse from JSON file into scene object instead of the
      // new Scene above,
      // Use the code you added in appropriate packages
      // ...
      // NB: unit tests is not the correct place to put XML parsing code

      camera //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .setResolution(1000, 1000) //
         .build() //
         .renderImage() //
         .printGrid( new Color(YELLOW),100) //
         .writeToImage("xml render test");
   }
   
   @Test
   public void renderMultiColorTest2() {
      Scene scene = new Scene("Multi color")
         .setAmbientLight(new AmbientLight(new Color(WHITE)));

      scene.geometries.add(
         new Sphere(new Point(0, 0, -100), 50).setMaterial(new Material().setK(new Double3(0.4))),
         new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
         .setMaterial(new Material().setK(new Double3(0, 0.8, 0))),
         new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
         .setMaterial(new Material().setK(new Double3(0.8, 0, 0))),
         new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
         .setMaterial(new Material().setK(new Double3(0, 0, 0.8)))
      );

      camera
         .setRayTracer(scene, RayTracerType.SIMPLE)
         .setResolution(1000, 1000)
         .build()
         .renderImage()
         .printGrid(new Color(WHITE), 100)
         .writeToImage("color render test material");
   }
   
   @Test
   public void renderSphereWithVisibleSpotLight() {
       Scene scene = new Scene("Sphere lit from right front")
           .setAmbientLight(new AmbientLight(new Color(20, 20, 20))); 

       scene.geometries.add(
           new Sphere(new Point(0, 0, -100), 50)
               .setEmission(new Color(30, 30, 30)) 
               .setMaterial(new Material()
                   .setKD(0.6)     
                   .setKS(0.9)     
                   .setShininess(300)) 
       );

       
       
       scene.lights.add(
    		    new SpotLight(
    		        new Color(0, 255, 0),       // צבע האור הימני
    		        new Point(100, 0, 0),            // מיקום האור הימני
    		        new Vector(-1, 0, -2))            // כיוון האור הימני אל הכדור
    		        .setNarrowBeam(15)
    		        .setKl(0.0005)
    		        .setKq(0.00005)
    		);

    		scene.lights.add(
    		    new SpotLight(
    		        new Color(255, 0, 0),       // צבע האור הקדמי
    		        new Point(0, 0,0),              // מיקום האור - בול מקדימה של הכדור
    		        new Vector(0,0,-2 )    )       // כיוון האור - ישר אל מרכז הכדור
    		       // .setNarrowBeam(15)                // צר, כמו האור הימני
    		        .setKl(0.0005)
    		        .setKq(0.00005)
    		);



       
       


       camera
           .setRayTracer(scene, RayTracerType.SIMPLE)
           .setResolution(1000, 1000)
           .build()
           .renderImage()
           .printGrid(new Color(WHITE), 100)
           .writeToImage("sphere_with_strong_spotlight2");
   } 




}
