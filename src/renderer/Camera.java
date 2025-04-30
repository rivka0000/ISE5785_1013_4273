package renderer;

import primitives.*;
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

    private int nX = 0;
    private int nY = 0;

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
     * Creates a shallow clone of the camera.
     *
     * @return a cloned Camera object
     * @throws AssertionError if cloning is not supported
     */
    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed unexpectedly", e);
        }
    }

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

            vTo = vTo.normalize();
            vUp = vUp.normalize();

            if (Math.abs(vTo.dotProduct(vUp)) > 1e-10)
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");

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
         * Sets the resolution of the view plane in terms of number of pixels.
         *
         * @param nX number of columns (horizontal resolution)
         * @param nY number of rows (vertical resolution)
         * @return this builder for method chaining
         */
        public Builder setResolution(int nX, int nY) {
            if (nX <= 0 || nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Validates and finalizes the camera setup.
         * If any values are missing, it assigns default values.
         *
         * @return a new Camera instance based on the configured parameters
         */
        public Camera build() {
            if (camera.p0 == null) camera.p0 = Point.ZERO;

            if (camera.vTo == null || camera.vUp == null) {
                camera.vTo = new Vector(0, 0, -1);
                camera.vUp = new Vector(0, -1, 0);
                camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            }

            if (camera.width <= 0) camera.width = 1;
            if (camera.height <= 0) camera.height = 1;
            if (camera.distance <= 0) camera.distance = 1;
            if (camera.nX <= 0) camera.nX = 1;
            if (camera.nY <= 0) camera.nY = 1;

            return (Camera) camera.clone();
        }
    }
}
