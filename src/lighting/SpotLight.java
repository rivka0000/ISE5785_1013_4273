package lighting;

import primitives.*;

/**
 * SpotLight represents a point light with a specific direction,
 * optionally supporting a narrow beam effect (bonus).
 */
public class SpotLight extends PointLight {
    private final Vector direction;

    // Narrow beam factor for sharper spotlight (bonus feature)
    private double narrowBeam = 1.0;

    /**
     * Constructs a new SpotLight.
     * @param intensity the light intensity
     * @param position the position of the spotlight
     * @param direction the direction of the beam
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets the narrow beam factor for spotlight sharpness (bonus).
     * @param narrowBeam beam concentration factor (≥ 1)
     * @return the current SpotLight object (for chaining)
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double projection = direction.dotProduct(l);
        if (projection <= 0) return Color.BLACK;

        // apply beam factor (default is 1 = no change)
        double factor = Math.pow(projection, narrowBeam);
        System.out.println("SpotLight factor = " + factor + ", projection = " + projection);
        return super.getIntensity(p).scale(factor);
    }
}
