package lighting;

import primitives.Color; // נניח שהמחלקה Color נמצאת בחבילה primitives

/**
 * Represents ambient light in a scene, which is constant and directionless.
 */
public class AmbientLight {

    // Final field for ambient light intensity (RGB components)
    private final Color intensity;

    /**
     * Constant for no ambient light (black color)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs ambient light with a given color intensity.
     * @param intensity Color representing the ambient light intensity (IA)
     */
    public AmbientLight(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of the ambient light.
     * @return Color representing the intensity
     */
    public Color getIntensity() {
        return intensity;
    }
}
