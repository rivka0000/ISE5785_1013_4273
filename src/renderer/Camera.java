package renderer;

import primitives.*;
import renderer.BlackBoard.BoardShape;
import scene.Scene;


import  static primitives.Util.*;
import java.util.stream.IntStream;


import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * Class Camera represents a virtual pinhole camera in 3D Cartesian space.
 * The camera constructs rays through pixels on a view plane and serves
 * as the starting point for ray tracing in a scene.
 *
 * The camera supports configuration via a Builder design pattern, enabling
 * flexible setup of parameters such as position, orientation, view plane size,
 * resolution, and distance from the view plane.
 *
 * This class also supports TDD practices and is used in integration with geometries
 * for ray-object intersection calculations.
 */
public class Camera implements Cloneable {

    private Point p0 = new Point(0, 0, 0);  // Camera position
    private Vector vTo = null;
    private Vector vUp = null;
    private Vector vRight = null;

    private double width = 0;
    private double height = 0;
    private double distance = 0;

    private int nX = 1;
    private int nY = 1;
    
    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;
    
 // ================= Multithreading =================
    private int threadsCount = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval = 0;
    private PixelManager pixelManager;

    // ================= Adaptive Super Sampling for DOF =================
    private boolean useAdaptiveSuperSamplingForDOF = false;
    private int numOfSubAreaSamplesAdaptiveDOF = 0;
    private int maxSamplesAdaptiveDOF = 0;
    private double colorThresholdAdaptiveDOF = -1;
    
    /** The shape of the board for jittered sampling (SQUARE or CIRCLE). */
    private BoardShape boardShape = BoardShape.SQUARE;

    /** Field that detects whether to use Depth of Field (DOF) effect or not. */
    private boolean useDOF = false;

    /** Number of rays for Depth of Field (DOF) effect. */
    private int amountOfRays_DOF = 0;

    /** the radius of the aperture for Depth of Field (DOF) effect. */
    private double apertureRadius = 0;

    /** the distance from the camera to the focal plane for Depth of Field (DOF) effect. */
    private double depthOfField = 0;

    /** Field that detects whether to use Anti-Aliasing (AA) effect or not. */
    private boolean useAA = false;

    /** Number of rays for Anti-Aliasing (AA) effect. */
    private int amountOfRays_AA = 0;

    /**
     * Default constructor for Camera.
     */
    public Camera() {}

    /**
     * Static method to obtain a new Builder instance for Camera.
     * @return new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of the specified pixel (j,i) in the view plane.
     *
     * @param nX number of pixels in the view plane along the X-axis (columns)
     * @param nY number of pixels in the view plane along the Y-axis (rows)
     * @param j pixel index along the X-axis (column)
     * @param i pixel index along the Y-axis (row)
     * @return a ray from the camera location through the center of the specified pixel
     */
    public Ray constructRay(int nX,int nY, int j, int i) {
        Point pc = p0.add(vTo.scale(distance));
        double rX = width / nX;
        double rY = height / nY;
        double xj = (j - (nX - 1) / 2.0) * rX;
        double yi = -(i - (nY - 1) / 2.0) * rY;

        Point pij = pc;
        if (xj != 0)
            pij = pij.add(vRight.scale(xj));
        if (yi != 0)
            pij = pij.add(vUp.scale(yi));

        Vector dir = pij.subtract(p0);
        return new Ray(p0, dir);
    }
    
    /**
     * Renders the image by casting rays through all pixels in the view plane.
     * This method loops through each pixel (x, y) and calls the castRay method
     * to simulate ray-tracing for the pixel, determining the color for each pixel.
     *
     * @return the Camera object itself for method chaining
     * @throws UnsupportedOperationException if the method is not yet implemented
     */
    public Camera renderImage() {
    	
    	    pixelManager = new PixelManager(nY, nX, printInterval);
    	   
    	    return switch (threadsCount) {
    	        case 0 -> {  
    	            yield renderImageNoThreads();
    	        }
    	        case -1 -> {
    	            yield renderImageStream();
    	        }
    	        default -> {
    	            yield renderImageRawThreads();
    	        }
    	    };
    }

    private Camera renderImageNoThreads() {
        for (int x = 0; x < nX; x++)
            for (int y = 0; y < nY; y++)
                castRay(x, y);
        return this;
    }

    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
            .forEach(y -> IntStream.range(0, nX).parallel()
            .forEach(x -> castRay(x, y)));
        return this;
    }

    private Camera renderImageRawThreads() {
        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null) {
                    castRay(pixel.col(), pixel.row());
                }
            });
        }

        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignore) {}
        }

        return this;
    }

    /**
     * Draws a grid of lines on the image, marking the borders of the pixels.
     * Only the grid lines are colored, while other pixels remain unchanged.
     * This method will not overwrite the existing image content.
     *
     * @param color the color of the grid lines
     * @param interval the interval between the grid lines (i.e., how often to draw a line)
     */
    public Camera printGrid(Color color, int interval) {
        // Loop over the entire width (nX) and height (nY) of the image
        for (int x = 0; x < nX; x++) {
            for (int y = 0; y < nY; y++) {
                // If the current pixel is at an interval for the grid lines
                if (x % interval == 0 || y % interval == 0) {
                    // Set the pixel color to the specified grid line color
                    imageWriter.writePixel(x, y, color);
                }
            }
        }

        // Return the Camera object itself for method chaining (as required)
        return this;
    }

    /**
     * Writes the current image to a file with the given name.
     * The method delegates the task of saving the image to the imageWriter.
     * 
     * @param fileName the name of the file to save the image (without the file extension)
     * @return the current Camera object
     */
    public Camera writeToImage(String fileName) {
        // Delegate the task to the imageWriter to save the image to the specified file
        imageWriter.writeToImage(fileName);
        return this;
    }
    
    /**
     * Casts a ray through a specific pixel and writes the resulting color to the image.
     * @param j the horizontal pixel index
     * @param i the vertical pixel index
     */
    public void castRay(int j, int i) {
        Color color;
        boolean useAA = amountOfRays_AA > 1;
        boolean useDOF = amountOfRays_DOF > 1 && apertureRadius != 0;

        if (useAdaptiveSuperSamplingForDOF) {
            color = adaptiveDofColor(j, i);
        } else if (useAA) {
            List<Ray> rays = constructAARays(j, i);
            color = averageRays(rays);
        } else if (useDOF) {
            List<Ray> rays = constructDOFRays(j, i);
            color = averageRays(rays);
        } else {
            Ray ray = constructRay(nX , nY,j, i);
            color = rayTracer.traceRay(ray);
        }

        imageWriter.writePixel(j, i, color);
        //pixelManager.pixelDone();
    }


    private Color adaptiveDofColor(int j, int i) {
        Point pixelCenter = calculatePixelCenter(j, i);
        return adaptiveDofRecursive(pixelCenter, numOfSubAreaSamplesAdaptiveDOF, 0);
    }

    private Color adaptiveDofRecursive(Point pixelCenter, int subSamples, int depth) {
        if (depth >= maxSamplesAdaptiveDOF) { 
            Vector dir = pixelCenter.subtract(p0).normalize();
            ArrayList<Ray> rays = constructDOFRaysFromDirection(dir);
            return averageRays(rays);
        }

        List<Color> colors = new ArrayList<>();
        List<Point> samplePoints = BlackBoard.generateJitteredSamples(
                pixelCenter, vRight, vUp, (width / nX) / 2.0, subSamples, boardShape);

        for (Point p : samplePoints) {
            Vector dir = p.subtract(p0).normalize();
            ArrayList<Ray> rays = constructDOFRaysFromDirection(dir);
            colors.add(averageRays(rays));
        }

        if (colorsAreSimilar(colors, colorThresholdAdaptiveDOF)) {
            return averageColors(colors);
        }

        Color total = Color.BLACK;
        for (Point p : samplePoints) {
            total = total.add(adaptiveDofRecursive(p, subSamples, depth + 1));
        }
        return total.reduce(samplePoints.size());
    }
    
    private boolean colorsAreSimilar(List<Color> colors, double threshold) {
        for (int i = 0; i < colors.size(); i++) {
            for (int j = i + 1; j < colors.size(); j++) {
                if (colors.get(i).distance(colors.get(j)) > threshold) {
                    return false;
                }
            }
        }
        return true;
    }

    private Color averageColors(List<Color> colors) {
        Color total = Color.BLACK;
        for (Color c : colors) {
            total = total.add(c);
        }
        return total.reduce(colors.size());
    }

    /**
     * Constructs Depth of Field (DOF) rays for a specific pixel.
     * This method is used when Depth of Field (DOF) effect is enabled.
     *
     * @param j the horizontal pixel index
     * @param i the vertical pixel index
     * @return a list of DOF rays
     */
    private ArrayList<Ray> constructDOFRays(int j, int i) {
        Point pixelCenter = calculatePixelCenter(j, i);
        Vector direction = pixelCenter.subtract(p0).normalize();  
        return constructDOFRaysFromDirection(direction);
    }

    /**
     * Constructs Depth of Field (DOF) rays in a specific direction.
     * This method is used when Depth of Field (DOF) effect is enabled.
     *
     * @param direction the direction vector for the DOF rays
     * @return a list of DOF rays
     */
    private ArrayList<Ray> constructDOFRaysFromDirection(Vector direction) {
        ArrayList<Ray> DOFRays = new ArrayList<>();
        
        if (Util.isZero(depthOfField)) {
            return DOFRays;
        }

        Point focalPoint = p0.add(direction.scale(depthOfField));  

        List<Point> aperturePoints = BlackBoard.generateJitteredSamples(
                p0, vRight, vUp, apertureRadius, amountOfRays_DOF, boardShape);  

        for (Point aperturePoint : aperturePoints) {
        	 try {
                 Vector dir = focalPoint.subtract(aperturePoint);
                 if (Util.isZero(dir.lengthSquared())) continue; // הגנה מהווקטור האפס
                 dir = dir.normalize();
                 DOFRays.add(new Ray(aperturePoint, dir));
             } catch (IllegalArgumentException e) {
                 // וקטור אפס – דלגי
                 continue;
             }
        }

        return DOFRays;
    }

    /**
     * Constructs Anti-Aliasing (AA) rays for a specific pixel.
     *
     * @param j the horizontal pixel index
     * @param i the vertical pixel index
     * @return a list of AA rays
     */
    private ArrayList<Ray> constructAARays(int j, int i) {
        ArrayList<Ray> AARays = new ArrayList<>();
        ArrayList<Vector> AAVectors = getAAVectors(j, i);

        for (Vector AAVector : AAVectors) {
            AARays.add(new Ray(p0, AAVector));
        }

        return AARays;
    }

    /**
     * Generates Anti-Aliasing (AA) vectors for a specific pixel.
     * These vectors are used to create rays for anti-aliasing effects.
     *
     * @param j the horizontal pixel index
     * @param i the vertical pixel index
     * @return a list of AA vectors
     */
    private ArrayList<Vector> getAAVectors(int j, int i) {
        ArrayList<Vector> AAVectors = new ArrayList<>();

        Point pij = calculatePixelCenter(j, i);
        double pixelRadius = (width / nX) / 2.0;  

        List<Point> pixelPoints = BlackBoard.generateJitteredSamples(
                pij, vRight, vUp, pixelRadius, amountOfRays_AA, boardShape);

        for (Point pixelPoint : pixelPoints) {
            Vector dir = pixelPoint.subtract(p0).normalize();  
            AAVectors.add(dir);
        }

        return AAVectors;
    }

    /**
     * Calculates the center point of a pixel in the view plane.
     *
     * @param j the horizontal pixel index
     * @param i the vertical pixel index
     * @return the center point of the pixel
     */
    private Point calculatePixelCenter(int j, int i) {
        Point pc = p0.add(vTo.scale(distance));  
        double rX = width / nX;                  
        double rY = height / nY;                 
        double xJ = (j - (nX - 1) / 2.0) * rX;
        double yI = -(i - (nY - 1) / 2.0) * rY;

        Point pij = pc;
        if (!Util.isZero(xJ)) pij = pij.add(vRight.scale(xJ));
        if (!Util.isZero(yI)) pij = pij.add(vUp.scale(yI));

        return pij;
    }

    /**
     * Combines Anti-Aliasing (AA) and Depth of Field (DOF) effects for a specific pixel.
     * For a given pixel, the algorithm uses getAAVectors to get a list
     * of the Anti-Aliasing vectors (the directions of the AA rays).
     * For each of those vectors, it constructs a beam of DOF rays in the vector direction using constructDOFRaysWithDirection,
     * and calculates the average color of the rays in that beam using averageRays.
     * Then it calculates the average color of all those average colors,
     * and returns it as the final color for the pixel.
     *
     * @param j the horizontal pixel index
     * @param i the vertical pixel index
     * @return the combined color from AA and DOF effects
     */
   /* private Color AAandDOFCombinedColor(int j, int i) {

        List<Vector> AAVectors;
        Color color;

        if (useAdaptiveSuperSamplingForAA) {
            // the pixelColorForAdaptiveSuperSamplingAA method can handle both regular AA and adaptive super sampling for AA
            color = pixelColorForAdaptiveSuperSamplingAA(j, i);
        } else {
            Color totalColor = Color.BLACK;
            AAVectors = getAAVectors(j, i);

            for (Vector AAVector : AAVectors) {
                ArrayList<Ray> DOFrays = constructDOFRaysFromDirection(AAVector);
                totalColor = totalColor.add(averageRays(DOFrays));
            }
            color = totalColor.reduce(AAVectors.size());
        }

        return color;
    }*/
    
    private Color averageRays(List<Ray> rays) {
        if (rays.isEmpty()) {
            return Color.BLACK;
        }
        Color total = Color.BLACK;
        for (Ray ray : rays) {
            total = total.add(rayTracer.traceRay(ray));
        }
        return total.reduce(rays.size());
    }

    /**
     * Creates a shallow clone of the camera.
     *
     * @return a cloned Camera object
     * @throws AssertionError if cloning is not supported
     */
//    @Override
//    protected Object clone() {
//        try {
//            return super.clone();
//        } catch (CloneNotSupportedException e) {
//            throw new AssertionError("Cloning failed unexpectedly", e);
//        }
//    }

    /**
     * Builder class for constructing Camera objects step by step.
     * <p>
     * This class enforces the use of the Builder design pattern to
     * initialize a Camera instance safely and clearly.
     * </p>
     */
    public static class Builder {

        private final Camera camera;
        
        /**
         * Sets the RayTracer for the camera.
         * <p>
         * This method updates the rayTracer field based on the provided RayTracerType.
         * Currently, only the SIMPLE RayTracerType is supported.
         * </p>
         *
         * @param scene the scene to be used by the RayTracer
         * @param type  the type of RayTracer (e.g., SIMPLE)
         * @return this builder for method chaining
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene);  // Create SimpleRayTracer with the provided scene
            } else {
                camera.rayTracer = null; // Set rayTracer to null for unsupported types
            }
            return this; // Return the builder for method chaining
        }
        
 

        /**
         * Constructs a new Builder instance initializing an empty Camera.
         */
        public Builder() {
            camera = new Camera();
        }

        /**
         * Sets the camera location (p0).
         *
         * @param location the 3D point where the camera is placed
         * @return this builder for method chaining
         */
        public Builder setLocation(Point location) {
            if (location == null)
                throw new IllegalArgumentException("Location cannot be null");
            camera.p0 = location;
            
            return this;
        }

        /**
         * Sets the camera orientation using forward and upward vectors.
         * Ensures the vectors are orthogonal and normalized.
         *
         * @param vTo the direction the camera is facing (must not be null)
         * @param vUp the upward direction relative to the camera (must not be null)
         * @return this builder for method chaining
         * @throws IllegalArgumentException if vectors are null or not orthogonal
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null)
                throw new IllegalArgumentException("Direction vectors cannot be null");
            if ( !isZero(vTo.dotProduct(vUp) ))
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");

            vTo = vTo.normalize();
            vUp = vUp.normalize();

           
            camera.vTo = vTo;
            camera.vUp = vUp;
            camera.vRight = vTo.crossProduct(vUp).normalize();
            return this;
        }

        /**
         * Sets the camera orientation using a target point and an approximate up vector.
         * Calculates the orientation vectors based on the difference between the target and location.
         *
         * @param target the point the camera should look at
         * @param upHint an approximate upward vector
         * @return this builder for method chaining
         */
        public Builder setDirection(Point target, Vector upHint) {
            if (target == null || upHint == null)
                throw new IllegalArgumentException("Target and up vector cannot be null");

            Vector vTo = target.subtract(camera.p0).normalize();
            Vector vRight = vTo.crossProduct(upHint).normalize();
            Vector vUp = vRight.crossProduct(vTo).normalize();

            camera.vTo = vTo;
            camera.vRight = vRight;
            camera.vUp = vUp;
            return this;
        }

        /**
         * Sets the camera orientation to look at a target point.
         * Uses the Y-axis as the default up vector.
         *
         * @param target the point the camera should look at
         * @return this builder for method chaining
         */
        public Builder setDirection(Point target) {
            return setDirection(target, Vector.AXIS_Y);
        }

        /**
         * Sets the physical dimensions of the view plane.
         *
         * @param width  the width of the view plane
         * @param height the height of the view plane
         * @return this builder for method chaining
         */
        public Builder setVpSize(double width, double height) {
            if (width <= 0 || height <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance the distance to the view plane
         * @return this builder for method chaining
         */
        public Builder setVpDistance(double distance) {
            if (distance <= 0)
                throw new IllegalArgumentException("View plane distance must be positive");
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the resolution for the camera.
         * This method ensures that both resolution values (nX, nY) are positive.
         * If the values are valid, it initializes the ImageWriter with the given resolution.
         * 
         * @param nX the width of the resolution (number of pixels in the X dimension)
         * @param nY the height of the resolution (number of pixels in the Y dimension)
         * @return the builder instance for method chaining
         * @throws IllegalArgumentException if either nX or nY is non-positive
         */
        public Builder setResolution(int nX, int nY) {
            
            camera.nX = nX;
            camera.nY = nY;

            return this;
        }
        



        /**
         * Sets the shape of the sample area for jittered sampling.
         * @param boardShape the shape of the sample area (SQUARE or CIRCLE)
         * @return the builder instance
         */
        public Builder setBoardShape(BoardShape boardShape) {
            if (boardShape == null) {
                throw new IllegalArgumentException("sampleShape cannot be null");
            }
            this.camera.boardShape = boardShape;
            return this;
        }

        /**
         * Sets whether to use Depth of Field (DOF) effect or not.
         * @param useDOF true to enable DOF, false to disable
         * @return the builder instance
         */
        public Builder setUseDOF(boolean useDOF) {
            this.camera.useDOF = useDOF;
            return this;
        }

        /**
         * Sets the number of rays for Depth of Field (DOF) effect.
         * @param amountOfRays the number of rays to use for DOF
         * @return the builder instance
         * @throws IllegalArgumentException if amountOfRays is less than or equal to zero
         */
        public Builder setAmountOfRays_DOF(int amountOfRays) {
            if (amountOfRays <= 0) {
                throw new IllegalArgumentException("amountOfRays must be greater than zero");
            }
            this.camera.amountOfRays_DOF = amountOfRays;
            return this;
        }

        /**
         * Sets the aperture radius for Depth of Field (DOF) effect.
         * @param apertureRadius the radius of the aperture
         * @return the builder instance
         * @throws IllegalArgumentException if aperture is less than or equal to zero
         */
        public Builder setApertureRadius(double apertureRadius) {
            if (apertureRadius <= 0) {
                throw new IllegalArgumentException("aperture must be greater than zero");
            }
            this.camera.apertureRadius = apertureRadius;
            return this;
        }

        /**
         * Sets the distance from the camera to the focal plane for Depth of Field (DOF) effect.
         * @param depthOfField the distance to the focal plane
         * @return the builder instance
         * @throws IllegalArgumentException if depthOfField is less than or equal to zero
         */
        public Builder setDepthOfField(double depthOfField) {
            if (depthOfField <= 0) {
                throw new IllegalArgumentException("depthOfField must be greater than zero");
            }
            this.camera.depthOfField = depthOfField;
            return this;
        }

        /**
         * Sets whether to use Anti-Aliasing (AA) effect or not.
         * @param useAA true to enable AA, false to disable
         * @return the builder instance
         */
        public Builder setUseAA(boolean useAA) {
            this.camera.useAA = useAA;
            return this;
        }

        /**
         * Sets the number of rays for Anti-Aliasing (AA) effect.
         * @param amountOfRays the number of rays to use for AA
         * @return the builder instance
         * @throws IllegalArgumentException if amountOfRays is less than or equal to zero
         */
        public Builder setAmountOfRays_AA(int amountOfRays) {
            if (amountOfRays <= 0) {
                throw new IllegalArgumentException("amountOfRays must be greater than zero");
            }
            this.camera.amountOfRays_AA = amountOfRays;
            return this;
        }

        
        /**
         * Sets multithreading level.
         * -2: use all cores minus spare
         * -1: use parallel stream
         * 0: no multithreading (default)
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("threads must be >= -2");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = cores <= 2 ? 1 : cores;
            } else {
                camera.threadsCount = threads;
            }
            return this;
        }

        public Builder setPrintInterval(double intervalSeconds) {
            camera.printInterval = intervalSeconds;
            return this;
        }

        public Builder setAdaptiveDOF(boolean useAdaptive, int subSamples, int maxSamples, double threshold) {
            camera.useAdaptiveSuperSamplingForDOF = useAdaptive;
            camera.numOfSubAreaSamplesAdaptiveDOF = subSamples;
            camera.maxSamplesAdaptiveDOF = maxSamples;
            camera.colorThresholdAdaptiveDOF = threshold;
            return this;
        }

        /**
         * Validates and finalizes the camera setup.
         * This method ensures that all the required fields for the camera are set correctly.
         * If any values are missing or invalid, appropriate exceptions are thrown.
         * <p>
         * After validation, the method calculates the right vector (`vRight`) by performing the cross product 
         * of the `vTo` and `vUp` vectors, ensuring that all three vectors (`vTo`, `vUp`, `vRight`) are orthogonal and normalized.
         * </p>
         *
         * @return a new Camera instance based on the configured parameters
         * @throws MissingResourceException if any required field is not set
         * @throws IllegalArgumentException if any vector is not orthogonal or normalized, or if width, height, or distance are invalid
         * @throws RuntimeException if cloning the camera fails
         */
        public Camera build() {
        	final String className = "Camera";
            final String description = "values not set: ";

            if(camera.p0 == null)
                throw new MissingResourceException(description, className, "p0");
            if(camera.vUp == null)
                throw new MissingResourceException(description, className, "vUp");
            if(camera.vTo == null)
                throw new MissingResourceException(description, className, "vTo");
            if(camera.width == 0d)
                throw new MissingResourceException(description, className, "width");
            if(camera.height == 0d)
                throw new MissingResourceException(description, className, "height");
            if(camera.distance == 0d)
                throw new MissingResourceException(description, className, "distance");
            if(camera.rayTracer == null)
                throw new MissingResourceException(description, className, "rayTracer");

            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            
         // Checking if both resolution values are positive, if not, throw an exception
            if (camera.nX <= 0 || camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }

            // Initializing the imageWriter with the resolution values
            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
            if(camera.imageWriter == null)
                throw new MissingResourceException(description, className, "imageWriter");

            // If the rayTracer is null, create a SimpleRayTracer with an empty scene (null)
            if (camera.rayTracer == null) {
                camera.rayTracer = new SimpleRayTracer(null);
            }


            if (!Util.isZero(camera.vTo.dotProduct(camera.vRight)) ||
                    !Util.isZero(camera.vTo.dotProduct(camera.vUp)) ||
                    !Util.isZero(camera.vRight.dotProduct(camera.vUp)))
                throw new IllegalArgumentException("vTo, vUp and vRight must be orthogonal");

            if (camera.vTo.length() != 1 || camera.vUp.length() != 1 || camera.vRight.length() != 1)
                throw new IllegalArgumentException("vTo, vUp and vRight must be normalized");

            if (camera.width <= 0 || camera.height <= 0)
                throw new IllegalArgumentException("width and height must be positive");

            if (camera.distance <= 0)
                throw new IllegalArgumentException("distance from camera to view must be positive");

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
}