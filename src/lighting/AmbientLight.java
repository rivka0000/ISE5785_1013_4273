package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene, which is constant and directionless.
 */
public class AmbientLight extends Light {

    /**
     * Constant for no ambient light (black color)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs ambient light with a given color intensity.
     * 
     * @param intensity Color representing the ambient light intensity (IA)
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }
}
