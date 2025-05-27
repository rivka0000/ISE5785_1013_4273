package lighting;

import primitives.Color;

/**
 * Abstract class representing a light source with a specific intensity.
 */
abstract class Light {

    /**
     * The intensity (color) of the light source.
     */
    protected final Color intensity;

    /**
     * Protected constructor to initialize the light intensity.
     * 
     * @param intensity the color intensity of the light source
     */
    Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity (color) of the light.
     * 
     * @return the color intensity
     */
    public Color getIntensity() {
        return intensity;
    }
}
