package renderer;

import primitives.Point;
import primitives.Vector;
import static primitives.Util.isZero;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackBoard {

    // Enum to define the shape of the sample area
    public enum BoardShape {
        SQUARE,
        CIRCLE
    }

    private static final Random random = new Random();

    /**
     * Generates jittered samples on a board (square or disk) using polar mapping of a jittered grid.
     *
     * @param center    center of the disk
     * @param vRight    right-direction vector (X axis on disk)
     * @param vUp       up-direction vector (Y axis on disk)
     * @param radius    if boardShape is SQUARE, this is half the side length of the square,
     *                  If boardShape is CIRCLE, this is the radius of the disk.
     * @param numPoints number of jittered points (should be square number, like 81)
     * @param boardShape     shape of the board (SQUARE or CIRCLE)
     * @return list of 3D points on the disk
     */
    public static List<Point> generateJitteredSamples(Point center, Vector vRight, Vector vUp, double radius, int numPoints, BoardShape boardShape) {
        List<Point> points = new ArrayList<>();
        int gridSize = (int) Math.sqrt(numPoints);

        if (gridSize * gridSize != numPoints)
            throw new IllegalArgumentException("numPoints must be a perfect square (e.g. 81, 100, 121)");

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {

                switch (boardShape) {
                    case SQUARE -> {
                        // Jittered sample in [-1,1]^2 square
                        double x = ((i + random.nextDouble()) / gridSize) * 2 - 1;
                        double y = ((j + random.nextDouble()) / gridSize) * 2 - 1;

                        Point p = center;
                        if (!isZero(x)) p = p.add(vRight.scale(x * radius));
                        if (!isZero(y)) p = p.add(vUp.scale(y * radius));
                        points.add(p);
                    }

                    case CIRCLE -> {
                        // Compute uniform jittered samples in polar coordinates inside the unit disk

                        // u, v are jittered normalized coordinates in [0,1) along grid axes
                        double u = (i + random.nextDouble()) / gridSize;
                        double v = (j + random.nextDouble()) / gridSize;

                        // r is radius for point inside disk,
                        // Using r = u will not cover the entire disk uniformly,
                        // since the area of each "layer" increases with r.
                        // r = sqrt(u) ensures uniform distribution,
                        // by "pushing" the points away from the center, compared to a linear distribution (r = u).
                        double r = Math.sqrt(u);

                        // theta is angle, distributed uniformly in [0, 2*PI)
                        double theta = 2 * Math.PI * v;

                        // Convert polar coordinates (r, theta) to Cartesian coordinates (x,y) on unit disk
                        double x = r * Math.cos(theta);
                        double y = r * Math.sin(theta);

                        // Scale (x,y) by radius of the disk and translate by center along vRight and vUp axes
                        Point p = center;
                        if (!isZero(x)) p = p.add(vRight.scale(x * radius));
                        if (!isZero(y)) p = p.add(vUp.scale(y * radius));

                        points.add(p);
                    }
                }
            }
        }

        return points;
    }
}