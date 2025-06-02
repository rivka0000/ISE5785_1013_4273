package scene;

import lighting.AmbientLight;
import primitives.Color;
import geometries.Geometries;
import lighting.LightSource;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a 3D scene containing background color, ambient light, and geometries.
 */
public class Scene {

    // Name of the scene
    public String name;

    // Background color (default: black)
    public Color background = Color.BLACK;

    // Ambient light (default: none)
    public AmbientLight ambientLight = AmbientLight.NONE;

    // 3D geometries model (default: empty)
    public Geometries geometries = new Geometries();
    
    // List of external light sources (point, directional, spot)
    public final List<LightSource> lights = new LinkedList<>();
   
    /**
     * Adds a light source to the scene.
     * @param light the light source to add
     * @return the current scene object (for chaining)
     */
    public Scene addLightSource(LightSource light) {
        lights.add(light);
        return this;
    }

    
    /**
     * Constructor – initializes the scene with a name only.
     * @param name The name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the background color.
     * @param background the background color
     * @return the current scene object (for chaining)
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light.
     * @param ambientLight the ambient light
     * @return the current scene object (for chaining)
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries.
     * @param geometries the geometries model
     * @return the current scene object (for chaining)
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}
