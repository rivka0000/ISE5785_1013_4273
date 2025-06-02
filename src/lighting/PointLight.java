/**
 * 
 */
package lighting;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import primitives.Double3;
import static primitives.Util.isZero;

/**
 * 
 */
public class PointLight extends Light implements LightSource {
	 private final Point position;

	    private Double3 kC = Double3.ONE;      // constant attenuation
	    private Double3 kL = Double3.ZERO;     // linear attenuation
	    private Double3 kQ = Double3.ZERO;     // quadratic attenuation

	    public PointLight(Color intensity, Point position) {
	        super(intensity);
	        this.position = position;
	    }

	    public PointLight setKc(double kc) {
	        this.kC = new Double3(kc);
	        return this;
	    }

	    public PointLight setKl(double kl) {
	        this.kL = new Double3(kl);
	        return this;
	    }

	    public PointLight setKq(double kq) {
	        this.kQ = new Double3(kq);
	        return this;
	    }
	    
	    private double flatten(Double3 d3) {
	        return (d3.d1() + d3.d2() + d3.d3()) / 3.0;
	    }


	    @Override
	    public Color getIntensity(Point p) {
	    	 double d = p.distance(position);
	    	    double attenuation = flatten(kC.add(kL.scale(d)).add(kQ.scale(d * d)));

	    	    if (isZero(attenuation)) return intensity;
	    	    return intensity.scale(1.0 / attenuation);
	    	    }

	    @Override
	    public Vector getL(Point p) {
	        return p.subtract(position).normalize();
	    }
}
