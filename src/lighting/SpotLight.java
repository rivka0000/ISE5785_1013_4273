package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a spotlight light source in a 3D scene.
 * A spotlight emits light in a specific direction and has a narrow beam angle.
 * The intensity of the light decreases with distance and is affected by the angle of the beam.
 */
public class SpotLight extends PointLight {

    /** The direction of the light beam. */
    private final Vector direction;

    /** The narrow beam angle (default is 1, meaning no narrow beam effect). */
    private double narrowBeam = 1;

    /**
     * Constructs a spotlight source with the given intensity, position, and direction.
     *
     * @param intensity the intensity of the light
     * @param position  the position of the light source
     * @param direction the direction of the light beam
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets the distance attenuation factors for the light source.
     * @param kc the constant attenuation factor
     * @return the spotlight object itself
     */
    @Override
    public SpotLight setKc(double kc) {
        super.setKc(kc);
        return this;
    }

    /**
     * Sets the linear attenuation factor for the light source.
     * @param kl the linear attenuation factor
     * @return the spotlight object itself
     */
    @Override
    public SpotLight setKl(double kl) {
        super.setKl(kl);
        return this;
    }

    /**
     * Sets the narrow beam angle for the light source.
     * @param narrowBeam the narrow beam angle (default is 1)
     * @return the spotlight object itself
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        if (narrowBeam < 1) {
            throw new IllegalArgumentException("narrowBeam must be greater than or equal to 1");
        }
        this.narrowBeam = narrowBeam;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor for the light source.
     * @param kq the quadratic attenuation factor
     * @return the spotlight object itself
     */
    @Override
    public SpotLight setKq(double kq) {
        super.setKq(kq);
        return this;
    }

    /**
     * Gets the intensity of the light at a given point.
     *
     * @param p the point at which to calculate the intensity
     * @return the intensity of the light at the given point
     */
    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p); // direction from light to point
        double dirFactor = (direction.dotProduct(l));
        
        if (dirFactor <= 0) {
            // The angle between the light direction and the vector from the light source to the point
            // is 90 degrees or more, meaning no light reaches the point
            return Color.BLACK;
        }

        // Apply the narrow beam by raising the direction factor to the power of narrowBeam
        double dirFactorWithNarrowBeam = Math.pow(dirFactor, narrowBeam);

        // Get the intensity from the parent class and scale it according to the narrow beam
        Color pointIntensity = super.getIntensity(p);
        return pointIntensity.scale(dirFactorWithNarrowBeam);
    }
}
