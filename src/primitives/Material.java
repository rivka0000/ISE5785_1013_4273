package primitives;

/**
 * Material class representing material properties.
 * This class is a Plain Data Structure (PDS) with a single public field 'k'.
 * No getter is provided for the field.
 */
public class Material {

    /**
     * The 'k' coefficient as a Double3 vector.
     * Initialized by default to Double3.ONE.
     */
    public Double3 Ka = Double3.ONE;

    /**
     * Default constructor for Material.
     * Explicitly declared to prevent javadoc warnings.
     */
    public Material() {}

    /**
     * Setter method for 'k' using a Double3 parameter.
     * Updates the 'k' field and returns the current Material instance for chaining.
     *
     * @param k the new Double3 value to set
     * @return the current Material instance
     */
    public Material setK(Double3 ka) {
        this.Ka = ka;
        return this;
    }

    /**
     * Setter method for 'k' using a double parameter.
     * Converts the double to a Double3 and updates the 'k' field.
     * Returns the current Material instance for chaining.
     *
     * @param k the new double value to set (converted to Double3)
     * @return the current Material instance
     */
    public Material setK(double ka) {
        this.Ka = new Double3(ka);
        return this;
    }
}

