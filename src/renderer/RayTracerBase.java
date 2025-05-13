package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracers.
 * Provides the scene context for tracing rays and requires implementation of the traceRay method.
 */
public abstract class RayTracerBase {

    /**
     * The scene to be used for ray tracing.
     * Accessible to subclasses only.
     */
    protected final Scene scene;

    /**
     * Constructs a ray tracer with the given scene.
     *
     * @param scene the scene to use for ray tracing
     */
    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces a given ray and computes the color resulting from its intersection with the scene.
     *
     * @param ray the ray to trace
     * @return the color at the intersection point
     */
    public abstract Color traceRay(Ray ray);
}
