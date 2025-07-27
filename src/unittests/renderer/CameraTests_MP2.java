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
 
    
	private Builder baseCamera( Scene scene) {
	  //  long startTime = System.nanoTime();

	   return  Camera.getBuilder()
	            .setResolution(1000, 1000)
	            .setBoardShape(BoardShape.SQUARE)
	            .setRayTracer(scene, RayTracerType.SIMPLE)
	            .setVpDistance(100)
	            .setVpSize(40, 40)
	            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
	            .setLocation(new Point(0, 0, 200))
	           .setDepthOfField(180)
	           .setAmountOfRays_DOF(81)
	           .setApertureRadius(1.0)
	            .setUseDOF(true);
	}
	
	
	@Test
	public void testFinalScene_noASS_noMT() {
		
		 Scene scene = createScene();
	        Camera camera = baseCamera(scene)
	            .setMultithreading(0)
	            .setAdaptiveDOF(false, 0, 0, 0)
	            .build();

	        long start = System.nanoTime();
	        camera.renderImage().writeToImage("Finall01_noASS_noMT");
	        long end = System.nanoTime();
	        System.out.println("Finall01_noASS_noMT: " + (end - start)/1e9 + " sec");
	}

	@Test
	public void testFinalScene_noASS_yesMT() {
		Scene scene = createScene();
        Camera camera = baseCamera(scene)
            .setMultithreading(-1)
            .setAdaptiveDOF(false, 0, 0, 0)
            .build();

        long start = System.nanoTime();
        camera.renderImage().writeToImage("Finall02_noASS_yesMT");
        long end = System.nanoTime();
        System.out.println("Finall02_noASS_yesMT: " + (end - start)/1e9 + " sec");
	}

	@Test
	public void testFinalScene_yesASS_yesMT() {
		Scene scene = createScene();
        Camera camera = baseCamera(scene)
            .setMultithreading(-2)
            .setAdaptiveDOF(true, 9, 5, 2)
            .build();

        long start = System.nanoTime();
        camera.renderImage().writeToImage("Finall03_yesASS_yesMT");
        long end = System.nanoTime();
        System.out.println("Finall03_yesASS_yesMT: " + (end - start)/1e9 + " sec");
	}

	@Test
	public void testFinalScene_yesASS_noMT() {
		 Scene scene = createScene();
	        Camera camera = baseCamera(scene)
	            .setMultithreading(0)
	            .setAdaptiveDOF(true, 9, 5, 2) 
	            .build();

	        long start = System.nanoTime();
	        camera.renderImage().writeToImage("Finall04_yesASS_noMT");
	        long end = System.nanoTime();
	        System.out.println("Finall04_yesASS_noMT: " + (end - start)/1e9 + " sec");	}
	
	private Scene createScene() {
        Scene scene = new Scene("Final MP2 Scene");

        Material sphereMat = new Material()
                .setKD(0.3)
                .setKS(0.7)
                .setShininess(500);

        // מקורות אור 
        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(40, 40, 40)));
        scene.lights.add(new PointLight(new Color(300, 150, 150), new Point(-50, 60, 100)));
        scene.lights.add(new DirectionalLight(new Color(200, 200, 250), new Vector(-1, -1, -1)));
        scene.lights.add(
            new SpotLight(
                new Color(0, 1500, 0),                  // אור ירוק עוצמתי
                new Point(15, 50, 160),                 // מקור האור – מעל ומאחורי הכדור
                new Vector(0, -50, -80).normalize()     // כיוון אל הכדור במדויק
            )
            .setKl(0)                                  // ללא עמעום
            .setKq(0)
            .setNarrowBeam(330)                          // אלומה צרה
        );
        
     // ספוט אור אדום - מלפני הגריד (בין המצלמה לכדורים)
        scene.lights.add(
            new SpotLight(
                new Color(999999, 0, 0),                      // אור אדום עוצמתי מאוד
                new Point(10, 5, 50),                       // מקור האור - בין המצלמה (Z=200) לגריד (Z=-200)
                new Vector(-0.1, -0.05, -1).normalize()    // כיוון ישר אל הכדורים
            )
            .setKl(0)                                      
            .setKq(0)                                      
            .setNarrowBeam(200)                           // אלומה צרה מאוד מאוד!
        );

        // גריד של גופים - מוזז אחורה
        int rows = 4;
        int columns = 7;
        double spacing = 10;
        int radius = 5;
        double startX = -columns / 2.0 * spacing;
        double startY = -rows / 2.0 * spacing;
        
        // 🔄 שינוי: הגריד מתחיל מ-Z=-200 במקום -120 כדי להזיז אותו אחורה
        double baseZ = -200; // הוזז 80 יחידות אחורה

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                double x = startX + col * spacing;
                double y = startY + row * spacing;
                double z = baseZ + col * 40; // עכשיו מתחיל מ-200 במקום -120

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
                             .setKt(0.0)) 
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

     // משולש מראה שחור על הפינה - מוגדל משמעותית לכיסוי פינה גדולה מהרצפה
        scene.geometries.add(
           new Triangle(
               new Point(-90, -24.8, -120),    // פינה שמאלית אחורית - מורחק הרבה יותר
               new Point(-90, -24.8, 120),     // פינה שמאלית קדמית - מגיע עד קרוב לכדורים
               new Point(40, -24.8, 120)       // פינה ימנית קדמית - הרחבה משמעותית ימינה
           )
           .setEmission(new Color(0, 0, 0))   
           .setMaterial(new Material()
               .setKD(0.02)          
               .setKS(0.05)          
               .setShininess(5)      
               .setKr(0.95))      
        );
        
     // קוביה משולשת (טטראהדרון) שקופה שחורה - קטנה ונמוכה יותר
        Point tetraBase1 = new Point(-15, -10, 120);
        Point tetraBase2 = new Point(-5, -10, 120);  
        Point tetraBase3 = new Point(-10, -10, 128);
        Point tetraTop = new Point(-10, -2, 124);

        // חומר לטטראהדרון - שחור שקוף
        Material tetraMaterial = new Material()
               .setKD(0.1)
               .setKS(0.3)
               .setShininess(50)
               .setKt(0.7) // שקיפות גבוהה
               .setKr(0.1);

        // פאות הטטראהדרון
        scene.geometries.add(
           new Triangle(tetraBase1, tetraBase2, tetraBase3)
               .setEmission(new Color(20, 20, 20))
               .setMaterial(tetraMaterial)
        );
        scene.geometries.add(
           new Triangle(tetraBase1, tetraBase2, tetraTop)
               .setEmission(new Color(20, 20, 20))
               .setMaterial(tetraMaterial)
        );
        scene.geometries.add(
           new Triangle(tetraBase2, tetraBase3, tetraTop)
               .setEmission(new Color(20, 20, 20))
               .setMaterial(tetraMaterial)
        );
        scene.geometries.add(
           new Triangle(tetraBase3, tetraBase1, tetraTop)
               .setEmission(new Color(20, 20, 20))
               .setMaterial(tetraMaterial)
        );

        // כדורים קטנים בתוך הטטראהדרון - שחור ולבן מבריק
        Material blackBrightMat = new Material()
               .setKD(0.2)
               .setKS(0.8)
               .setShininess(800);

        Material whiteBrightMat = new Material()
               .setKD(0.2)
               .setKS(0.8)
               .setShininess(800);

        // 12 כדורים קטנים בפנים - מחושבים כדי להישאר בטטראהדרון
        // שכבה תחתונה (Y=-9) - קרוב לבסיס
        scene.geometries.add(
           new Sphere(new Point(-11, -9, 122), 0.5)
               .setEmission(new Color(0, 0, 0))
               .setMaterial(blackBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-9, -9, 122), 0.5)
               .setEmission(new Color(255, 255, 255))
               .setMaterial(whiteBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-10, -9, 124), 0.5)
               .setEmission(new Color(0, 0, 0))
               .setMaterial(blackBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-12, -9, 124), 0.5)
               .setEmission(new Color(255, 255, 255))
               .setMaterial(whiteBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-8, -9, 124), 0.5)
               .setEmission(new Color(0, 0, 0))
               .setMaterial(blackBrightMat)
        );

        // שכבה אמצעית (Y=-6.5)
        scene.geometries.add(
           new Sphere(new Point(-10.5, -6.5, 122.5), 0.5)
               .setEmission(new Color(255, 255, 255))
               .setMaterial(whiteBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-9.5, -6.5, 122.5), 0.5)
               .setEmission(new Color(0, 0, 0))
               .setMaterial(blackBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-11, -6.5, 125), 0.5)
               .setEmission(new Color(255, 255, 255))
               .setMaterial(whiteBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-9, -6.5, 125), 0.5)
               .setEmission(new Color(0, 0, 0))
               .setMaterial(blackBrightMat)
        );

        // שכבה עליונה (Y=-4) - קרוב לקודקוד
        scene.geometries.add(
           new Sphere(new Point(-10.2, -4, 123.2), 0.5)
               .setEmission(new Color(255, 255, 255))
               .setMaterial(whiteBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-9.8, -4, 123.2), 0.5)
               .setEmission(new Color(0, 0, 0))
               .setMaterial(blackBrightMat)
        );
        scene.geometries.add(
           new Sphere(new Point(-10, -4, 124.5), 0.5)
               .setEmission(new Color(255, 255, 255))
               .setMaterial(whiteBrightMat)
        );
        
     // דובי חמוד בפינה השמאלית העליונה - מיקום מתוקן ונמוך יותר
        // המצלמה ב (0,0,200), שדה ראייה 40x40, מרחק 100 מהמצלמה
        Material teddyMaterial = new Material()
                .setKD(0.4)
                .setKS(0.6)
                .setShininess(200);

        // גוף הדובי - כדור כחול במיקום נמוך יותר
        scene.geometries.add(
            new Sphere(new Point(-15, 12, 100), 2.0) // הורד ב-3 יחידות
                .setEmission(new Color(70, 130, 200)) // כחול יפה
                .setMaterial(teddyMaterial)
        );

        // ראש הדובי - כדור תכלת למעלה
        scene.geometries.add(
            new Sphere(new Point(-15, 15, 102), 1.6) // הורד ב-3 יחידות
                .setEmission(new Color(100, 180, 220)) // תכלת בהיר
                .setMaterial(teddyMaterial)
        );

        // אוזניים - כדורים גדולים יותר כחול כהה
        scene.geometries.add(
            new Sphere(new Point(-16.2, 16.2, 101.5), 0.8) // הגדלה מ-0.5 ל-0.8
                .setEmission(new Color(40, 80, 150)) // כחול כהה
                .setMaterial(teddyMaterial)
        );
        scene.geometries.add(
            new Sphere(new Point(-13.8, 16.2, 101.5), 0.8) // הגדלה מ-0.5 ל-0.8
                .setEmission(new Color(40, 80, 150)) // כחול כהה
                .setMaterial(teddyMaterial)
        );

        // עיניים - כדורים לבנים גדולים יותר עם עיגולים שחורים בפנים
        // עין שמאלית - כדור לבן
        scene.geometries.add(
            new Sphere(new Point(-15.8, 15.2, 103.5), 0.4) // הורד ב-3, הגדלה פי 2
                .setEmission(new Color(255, 255, 255)) // לבן
                .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(500))
        );
        // עין שמאלית - כדור שחור בפנים
        scene.geometries.add(
            new Sphere(new Point(-15.8, 15.2, 103.8), 0.2)
                .setEmission(new Color(0, 0, 0))
                .setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(1000))
        );
        
        // עין ימנית - כדור לבן
        scene.geometries.add(
            new Sphere(new Point(-14.2, 15.2, 103.5), 0.4) // הורד ב-3, הגדלה פי 2
                .setEmission(new Color(255, 255, 255)) // לבן
                .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(500))
        );
        // עין ימנית - כדור שחור בפנים
        scene.geometries.add(
            new Sphere(new Point(-14.2, 15.2, 103.8), 0.2)
                .setEmission(new Color(0, 0, 0))
                .setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(1000))
        );

        // אף - משולש תכלת קטן
        scene.geometries.add(
            new Triangle(
                new Point(-15, 14.7, 104), // הורד ב-3 יחידות
                new Point(-14.8, 14.5, 103.8),
                new Point(-15.2, 14.5, 103.8)
            )
            .setEmission(new Color(150, 200, 240)) // תכלת בהיר מאוד
            .setMaterial(teddyMaterial)
        );

        // פה אדום וחמוד - משולש קטן
        scene.geometries.add(
            new Triangle(
                new Point(-15, 14.0, 104),
                new Point(-14.7, 14.2, 103.9),
                new Point(-15.3, 14.2, 103.9)
            )
            .setEmission(new Color(220, 50, 80)) // אדום חמוד
            .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))
        );

     // רגליים צמודות לגוף - כדורים כחולים ממוקמים ישר מתחת לגוף וקרובים לבטן
        scene.geometries.add(
            new Sphere(new Point(-15.8, 10.0, 100.5), 1.0) // קרוב לגוף משמאל, הועלה ל-10.0
                .setEmission(new Color(60, 120, 180)) // כחול בינוני
                .setMaterial(teddyMaterial)
        );
        scene.geometries.add(
            new Sphere(new Point(-14.2, 10.0, 100.5), 1.0) // קרוב לגוף מימין, הועלה ל-10.0
                .setEmission(new Color(60, 120, 180)) // כחול בינוני
                .setMaterial(teddyMaterial)
        );

        // ידיים - כדורים תכלת קטנים
        scene.geometries.add(
            new Sphere(new Point(-17.0, 12.5, 99.5), 0.8) // הורד ב-3 יחידות
                .setEmission(new Color(90, 160, 210)) // תכלת
                .setMaterial(teddyMaterial)
        );
        scene.geometries.add(
            new Sphere(new Point(-13.0, 12.5, 99.5), 0.8) // הורד ב-3 יחידות
                .setEmission(new Color(90, 160, 210)) // תכלת
                .setMaterial(teddyMaterial)
        );

        // בטן - כדור כחול בהיר
        scene.geometries.add(
            new Sphere(new Point(-15, 12, 102), 1.2) // הורד ב-3 יחידות
                .setEmission(new Color(120, 170, 230)) // כחול בהיר
                .setMaterial(teddyMaterial)
        );
        
        
     // ✨ הוספת המילה "LOVE" בכדורים קטנים בצבע חום בהיר לימין הדובי
        Material loveMaterial = new Material()
               .setKD(0.4)
               .setKS(0.6)
               .setShininess(300);

        Color loveColor = new Color(160, 120, 80); // חום בהיר יפה
        double letterRadius = 0.3; // רדיוס הכדורים
        double letterSpacing = 2.5; // מרווח בין אותיות
        double startX1 = -5; // הזזזה ימינה (במקום -10)
        double textY = 14; // באותו גובה של הדובי
        double textZ = 105; // קדימה מהדובי

        // אות L
        double lX = startX1;
        // קו אנכי של L
        scene.geometries.add(new Sphere(new Point(lX, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(lX, textY - 0.6, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(lX, textY - 1.2, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(lX, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        // קו אופקי של L
        scene.geometries.add(new Sphere(new Point(lX + 0.6, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(lX + 1.2, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));

        // אות O - מעגל מלא ויפה יותר
        double oX = startX1 + letterSpacing;
        // קווים עליונים ותחתונים
        scene.geometries.add(new Sphere(new Point(oX + 0.3, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 0.6, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 0.9, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 0.3, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 0.6, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 0.9, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        // קווים צדדיים
        scene.geometries.add(new Sphere(new Point(oX, textY - 0.3, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX, textY - 0.9, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX, textY - 1.5, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 1.2, textY - 0.3, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 1.2, textY - 0.9, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(oX + 1.2, textY - 1.5, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));

        // אות V
        double vX = startX1 + letterSpacing * 2;
        // קווים אלכסוניים של V
        scene.geometries.add(new Sphere(new Point(vX, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(vX + 1.2, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(vX + 0.2, textY - 0.6, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(vX + 1.0, textY - 0.6, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(vX + 0.4, textY - 1.2, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(vX + 0.8, textY - 1.2, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(vX + 0.6, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));

        // אות E
        double eX = startX1 + letterSpacing * 3;
        // קו אנכי של E
        scene.geometries.add(new Sphere(new Point(eX, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX, textY - 0.6, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX, textY - 1.2, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        // קווים אופקיים של E
        scene.geometries.add(new Sphere(new Point(eX + 0.6, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX + 1.2, textY, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX + 0.6, textY - 0.9, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX + 0.6, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        scene.geometries.add(new Sphere(new Point(eX + 1.2, textY - 1.8, textZ), letterRadius).setEmission(loveColor).setMaterial(loveMaterial));
        
        
        // רצפה
        scene.geometries.add(
            new Plane(new Point(0, -25, 0), new Vector(0, 1, 0))
                .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
                .setEmission(new Color(100, 100, 100))
        );

        return scene;
    }



    


}
