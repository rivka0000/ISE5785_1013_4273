package primitives;

/**
 * Material class representing material properties.
 * This class is a Plain Data Structure (PDS) with reflection coefficients for Phong model.
 */
public class Material {

    // Ambient reflection coefficient (KA)
    public Double3 Ka = Double3.ONE;

    // Diffuse reflection coefficient (KD)
    public Double3 KD = Double3.ZERO;

    // Specular reflection coefficient (KS)
    public Double3 KS = Double3.ZERO;

    // Shininess factor (n)
    public int nShininess = 0;

    /**
     * Default constructor for Material.
     */
    public Material() {}

    /**
     * Sets the ambient reflection coefficient (KA).
     * @param ka the ambient coefficient
     * @return the current material object (for chaining)
     */
    public Material setK(Double3 ka) {
        this.Ka = ka;
        return this;
    }

    /**
     * Sets the ambient reflection coefficient (KA) using a scalar.
     * @param ka the ambient coefficient as scalar
     * @return the current material object (for chaining)
     */
    public Material setK(double ka) {
        this.Ka = new Double3(ka);
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient (KD).
     * @param kD the diffuse coefficient
     * @return the current material object (for chaining)
     */
    public Material setKD(Double3 kD) {
        this.KD = kD;
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient (KD) using a scalar.
     * @param kD the diffuse coefficient as scalar
     * @return the current material object (for chaining)
     */
    public Material setKD(double kD) {
        this.KD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular reflection coefficient (KS).
     * @param kS the specular coefficient
     * @return the current material object (for chaining)
     */
    public Material setKS(Double3 kS) {
        this.KS = kS;
        return this;
    }

    /**
     * Sets the specular reflection coefficient (KS) using a scalar.
     * @param kS the specular coefficient as scalar
     * @return the current material object (for chaining)
     */
    public Material setKS(double kS) {
        this.KS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess factor.
     * @param nShininess the shininess factor
     * @return the current material object (for chaining)
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}
