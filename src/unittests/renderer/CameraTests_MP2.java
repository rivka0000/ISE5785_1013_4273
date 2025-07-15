package unittests.renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import lighting.*;
import primitives.*;
import renderer.BlackBoard.BoardShape;
import renderer.Camera;
import renderer.Camera.Builder;
import renderer.RayTracerType;
import scene.Scene;
import geometries.*;

public class CameraTests_MP2 {

    /**
     * Simple test scene with one red sphere and one point light
     */
    private Scene createSimpleScene() {
        Scene scene = new Scene("TestScene").setBackground(new Color(50, 50, 50));

        scene.geometries.add(
            new Sphere(new Point(0, 0, -100), 30)
                .setEmission(new Color(200, 0, 0))
                .setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(100))
        );

        scene.lights.add(
            new PointLight(new Color(500, 500, 500), new Point(50, 50, 0))
                .setKq(0.0001).setKl(0.0005)
        );

        return scene;
    }

    /**
     * Returns base builder for camera with DOF enabled (ASS optional).
     */
    private Builder baseCamera(Scene scene) {
        return Camera.getBuilder()
            .setLocation(new Point(0, 0, 0))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setResolution(200, 200)
            .setVpSize(100, 100)
            .setVpDistance(100)
            .setRayTracer(scene, RayTracerType.SIMPLE)
            .setUseDOF(true)
            .setAmountOfRays_DOF(25)
            .setApertureRadius(2.0)
            .setDepthOfField(100);
    }

    @Test
    void test00_noASS_noMT() {
        Scene scene = createSimpleScene();
        Camera camera = baseCamera(scene)
            .setMultithreading(0)
            .setAdaptiveDOF(false, 0, 0, 0)
            .build();

        long start = System.nanoTime();
        camera.renderImage().writeToImage("test00_noASS_noMT");
        long end = System.nanoTime();
        System.out.println("test00_noASS_noMT: " + (end - start)/1e9 + " sec");
    }

    @Test
    void test01_noASS_yesMT() {
        Scene scene = createSimpleScene();
        Camera camera = baseCamera(scene)
            .setMultithreading(-1)
            .setAdaptiveDOF(false, 0, 0, 0)
            .build();

        long start = System.nanoTime();
        camera.renderImage().writeToImage("test01_noASS_yesMT");
        long end = System.nanoTime();
        System.out.println("test01_noASS_yesMT: " + (end - start)/1e9 + " sec");
    }

    @Test
    void test02_yesASS_noMT() {
        Scene scene = createSimpleScene();
        Camera camera = baseCamera(scene)
            .setMultithreading(0)
            .setAdaptiveDOF(true, 4, 3, 25) // 4 samples, depth 3, threshold 25
            .build();

        long start = System.nanoTime();
        camera.renderImage().writeToImage("test02_yesASS_noMT");
        long end = System.nanoTime();
        System.out.println("test02_yesASS_noMT: " + (end - start)/1e9 + " sec");
    }

    @Test
    void test03_yesASS_yesMT() {
        Scene scene = createSimpleScene();
        Camera camera = baseCamera(scene)
            .setMultithreading(-2)
            .setAdaptiveDOF(true, 4, 3, 25)
            .build();

        long start = System.nanoTime();
        camera.renderImage().writeToImage("test03_yesASS_yesMT");
        long end = System.nanoTime();
        System.out.println("test03_yesASS_yesMT: " + (end - start)/1e9 + " sec");
    }
    
 
    @Test
    public void FinalTest_WindowWithMoon() {
        Scene scene = new Scene("Final Scene - Window with Moon");

        // רקע ותאורה כללית
        scene.setBackground(new Color(10, 10, 30));
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30).scale(0.2)));

        // אובייקטים:
        // ירח - כדור לבן זוהר
        scene.geometries.add(new Sphere(
                new Point(90, 90, -400),
                30)
                .setEmission(new Color(255, 255, 230))
                .setMaterial(new Material().setKt(0.5).setShininess(150))
        );

        // אדמה
        scene.geometries.add(new Plane(new Point(0, -30, 0), new Vector(0, 1, 0))
                .setEmission(new Color(20, 40, 20))
                .setMaterial(new Material().setKr(0.3)));

        // שולחן (קופסה כהה עם רפלקציה)
        scene.geometries.add(
                new Sphere(new Point(0, -20, -100), 15)
                        .setEmission(new Color(50, 20, 20))
                        .setMaterial(new Material().setKr(0.3).setShininess(100))
        );

        // כדור שקוף על השולחן
        scene.geometries.add(
                new Sphere(new Point(-5, -5, -95), 7)
                        .setEmission(new Color(20, 50, 50))
                        .setMaterial(new Material().setKt(0.8).setShininess(150))
        );

        // כדור מבריק נוסף
        scene.geometries.add(
                new Sphere(new Point(10, -5, -105), 7)
                        .setEmission(new Color(80, 10, 10))
                        .setMaterial(new Material().setKr(0.6).setShininess(200))
        );

        // חלון – 3 שורות של 5 משבצות = 15 ריבועים עם שקיפות קלה
        double windowSize = 10;
        double gap = 2;
        double startX = -25;
        double startY = 20;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                double x = startX + col * (windowSize + gap);
                double y = startY - row * (windowSize + gap);
                scene.geometries.add(
                        new Polygon(
                                new Point(x, y, -149),
                                new Point(x + windowSize, y, -149),
                                new Point(x + windowSize, y - windowSize, -149),
                                new Point(x, y - windowSize, -149))
                                .setEmission(new Color(20, 20, 60))
                                .setMaterial(new Material().setKt(0.7))
                );
            }
        }

        // אור חם מתוך החדר
        scene.lights.add(new SpotLight(new Color(800, 600, 500),
                new Point(0, 0, 0), new Vector(0, -1, -1))
                .setNarrowBeam(20).setKl(0.0002).setKq(0.00001));

        // מצלמה
        new Camera.Builder()
                .setLocation(new Point(0, 0, 100))
                .setDirection(new Point(0, 0, -200))
                .setVpSize(200, 200)
                .setVpDistance(100)
                .setResolution(800, 800)
                .setBoardShape(BoardShape.CIRCLE)
                .setUseDOF(true)
                .setAmountOfRays_DOF(81)
                .setApertureRadius(5)
                .setDepthOfField(200)
                .setUseAA(true)
                .setAmountOfRays_AA(64)
                .setAdaptiveDOF(true, 4, 3, 10)
                .setMultithreading(-2)
                .setPrintInterval(1)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("final_WindowWithMoon");
    }


}
