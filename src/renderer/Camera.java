package renderer;

import primitives.*;
import renderer.BlackBoard.BoardShape;
import scene.Scene;

import static primitives.Util.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

/**
 * Class Camera represents a virtual pinhole camera in 3D Cartesian space.
 * The camera constructs rays through pixels on a view plane and serves
 * as the starting point for ray tracing in a scene.
 *
 * The camera supports configuration via a Builder design pattern, enabling
 * flexible setup of parameters such as position, orientation, view plane size,
 * resolution, and distance from the view plane.
 *
 * This class also supports advanced features like Depth of Field (DOF),
 * Adaptive Super Sampling (ASS), Anti-Aliasing (AA), and Multithreading.
 */
public class Camera implements Cloneable {

    private Point p0 = new Point(0, 0, 0);
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

    private BoardShape boardShape = BoardShape.SQUARE;
    private boolean useDOF = false;
    private int amountOfRays_DOF = 0;
    private double apertureRadius = 0;
    private double depthOfField = 0;
    private boolean useAA = false;
    private int amountOfRays_AA = 0;
    private int threadsCount = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval = 0;
    private PixelManager pixelManager;
    private boolean useAdaptiveSuperSamplingForDOF = false;
    private int numOfSubAreaSamplesAdaptiveDOF = 0;
    private int maxSamplesAdaptiveDOF = 0;
    private double colorThresholdAdaptiveDOF = -1;

    public Camera() {}

    public Ray constructRay(int nX, int nY, int j, int i) {
        Point pc = p0.add(vTo.scale(distance));
        double rX = width / nX;
        double rY = height / nY;
        double xj = (j - (nX - 1) / 2.0) * rX;
        double yi = -(i - (nY - 1) / 2.0) * rY;

        Point pij = pc;
        if (!isZero(xj)) pij = pij.add(vRight.scale(xj));
        if (!isZero(yi)) pij = pij.add(vUp.scale(yi));

        Vector dir = pij.subtract(p0);
        return new Ray(p0, dir);
    }

    public Ray constructRay(int j, int i) {
        return constructRay(this.nX, this.nY, j, i);
    }

    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j)
                castRay(j, i);
        return this;
    }

    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        for (int i = 0; i < threadsCount; ++i) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        }

        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}
        return this;
    }

    public void castRay(int j, int i) {
        Color color;

        if (useAA && useDOF) {
            color = AAandDOFCombinedColor(j, i);
        } else if (useAA) {
            List<Ray> rays = constructAARays(j, i);
            color = averageRays(rays);
        } else if (useDOF) {
            color = dofColorFromPixelIndices(j, i);
        } else {
            Ray ray = constructRay(j, i);
            color = rayTracer.traceRay(ray);
        }

        imageWriter.writePixel(j, i, color);
        pixelManager.pixelDone();
    }

    private Color dofColorFromPixelIndices(int j, int i) {
        Point pixelCenter = calculatePixelCenter(j, i);
        Vector direction = pixelCenter.subtract(p0).normalize();
        return dofColorFromDirection(direction);
    }

    private Color dofColorFromDirection(Vector direction) {
        if (useAdaptiveSuperSamplingForDOF) {
            Point focalPoint = p0.add(direction.scale(depthOfField));
            return adaptiveDofColorForSubArea(p0, apertureRadius, focalPoint, 0);
        } else {
            return nonAdaptiveDofColor(direction);
        }
    }

    private Color nonAdaptiveDofColor(Vector direction) {
        ArrayList<Ray> DOFrays = constructDOFRaysFromDirection(direction);
        return averageRays(DOFrays);
    }

    private Color adaptiveDofColorForSubArea(Point center, double radius, Point focalPoint, int depth) {
        List<Point> apertureSamples = BlackBoard.generateJitteredSamples(
                center, vRight, vUp, radius, numOfSubAreaSamplesAdaptiveDOF, boardShape);

        List<Color> sampleColors = new ArrayList<>();

        for (Point apertureSample : apertureSamples) {
            Vector dir = focalPoint.subtract(apertureSample).normalize();
            Ray ray = new Ray(apertureSample, dir);
            sampleColors.add(rayTracer.traceRay(ray));
        }

        if (numOfSubAreaSamplesAdaptiveDOF * Math.pow(4, depth) >= maxSamplesAdaptiveDOF ||
                colorsAreSimilar(sampleColors, colorThresholdAdaptiveDOF)) {
            return averageColors(sampleColors);
        }

        double halfRadius = radius / 2.0;
        Color totalColor = Color.BLACK;
        double[] offsets = {-halfRadius, halfRadius};

        for (double dx : offsets)
            for (double dy : offsets)
                totalColor = totalColor.add(adaptiveDofColorForSubArea(
                        center.add(vRight.scale(dx)).add(vUp.scale(dy)), halfRadius, focalPoint, depth + 1));

        return totalColor.reduce(4);
    }

    private boolean colorsAreSimilar(List<Color> colors, double threshold) {
        for (int i = 0; i < colors.size(); i++)
            for (int j = i + 1; j < colors.size(); j++)
                if (colors.get(i).distance(colors.get(j)) > threshold)
                    return false;
        return true;
    }

    private Color averageColors(List<Color> colors) {
        Color total = Color.BLACK;
        for (Color c : colors) total = total.add(c);
        return total.reduce(colors.size());
    }

    private Color averageRays(List<Ray> rays) {
        if (rays.isEmpty()) return Color.BLACK;
        Color total = Color.BLACK;
        for (Ray ray : rays) total = total.add(rayTracer.traceRay(ray));
        return total.reduce(rays.size());
    }

    private ArrayList<Ray> constructDOFRaysFromDirection(Vector direction) {
        ArrayList<Ray> rays = new ArrayList<>();
        if (isZero(depthOfField)) return rays;

        Point focalPoint = p0.add(direction.scale(depthOfField));
        List<Point> aperturePoints = BlackBoard.generateJitteredSamples(
                p0, vRight, vUp, apertureRadius, amountOfRays_DOF, boardShape);

        for (Point aperturePoint : aperturePoints) {
            try {
                Vector dir = focalPoint.subtract(aperturePoint);
                if (isZero(dir.lengthSquared())) continue;
                rays.add(new Ray(aperturePoint, dir.normalize()));
            } catch (IllegalArgumentException ignored) {}
        }
        return rays;
    }

    private ArrayList<Vector> getAAVectors(int j, int i) {
        ArrayList<Vector> vectors = new ArrayList<>();
        Point pij = calculatePixelCenter(j, i);
        double pixelRadius = (width / nX) / 2.0;

        List<Point> samples = BlackBoard.generateJitteredSamples(
                pij, vRight, vUp, pixelRadius, amountOfRays_AA, boardShape);

        for (Point sample : samples) {
            vectors.add(sample.subtract(p0).normalize());
        }
        return vectors;
    }

    private ArrayList<Ray> constructAARays(int j, int i) {
        ArrayList<Ray> rays = new ArrayList<>();
        for (Vector dir : getAAVectors(j, i)) {
            rays.add(new Ray(p0, dir));
        }
        return rays;
    }

    private Color AAandDOFCombinedColor(int j, int i) {
        List<Vector> aaVectors = getAAVectors(j, i);
        List<Color> averagedColors = new ArrayList<>();

        for (Vector vec : aaVectors) {
            List<Ray> dofRays = constructDOFRaysFromDirection(vec);
            averagedColors.add(averageRays(dofRays));
        }

        return averageColors(averagedColors);
    }

    private Point calculatePixelCenter(int j, int i) {
        Point pc = p0.add(vTo.scale(distance));
        double rX = width / nX;
        double rY = height / nY;
        double xJ = (j - (nX - 1) / 2.0) * rX;
        double yI = -(i - (nY - 1) / 2.0) * rY;

        Point pij = pc;
        if (!isZero(xJ)) pij = pij.add(vRight.scale(xJ));
        if (!isZero(yI)) pij = pij.add(vUp.scale(yI));

        return pij;
    }
} 
