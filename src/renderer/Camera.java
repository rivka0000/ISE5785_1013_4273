package renderer;

import primitives.*;
import scene.Scene;

import  static primitives.Util.*;
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
    public Ray constructRay(int nX, int nY, int j, int i) {
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
        // Loop through all the pixels in the ViewPlane (image resolution nX and nY)
        for (int x = 0; x < nX; x++) {
            for (int y = 0; y < nY; y++) {
                // Call the castRay method for each pixel (x, y)
                castRay(x, y);
            }
        }
        
        // Return the Camera object itself to allow method chaining
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
     * Casts a ray from the camera through the given pixel coordinates (x, y).
     * The method uses the pixel index to calculate the ray's direction and
     * other necessary parameters. This method is used to simulate the ray-tracing
     * process and determine the color for the pixel.
     *
     * @param x the X-coordinate of the pixel
     * @param y the Y-coordinate of the pixel
     */
    private void castRay(int x, int y) {
        //  Calculate the direction of the ray through the pixel (x, y)
    	Ray ray = constructRay(nX, nY, x, y);


        //  Perform ray tracing to determine the light intensity for the ray
        Color pixelColor = rayTracer.traceRay(ray);

        //  Set the pixel color in the image using the ImageWriter
        imageWriter.writePixel(x, y, pixelColor);
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
