package qp.control.types;


/**
 *
 * @author Maira57
 */
public class RGBd {

    public double r, g, b;

    
    
    public RGBd() {
        r = 0;
        g = 0;
        b = 0;
    }
    
    public RGBd(double ri, double gi, double bi) {
        r = ri;
        g = gi;
        b = bi;
    }
    
    public RGBd(double[] d) {
        r = d[0];
        g = d[1];
        b = d[2];
    }

    
    
    public void add(RGBd x) { r+=x.r; g+=x.g; b+=x.b; }
    
    public void add(double ri, double gi, double bi) { r+=ri; g+=gi; b+=bi; }
    
    public void sub(RGBd x) { r-=x.r; g-=x.g; b-=x.b; }
    
    public void mul(double x) { r*=x; g*=x; b*=x; }
    
    public RGBd getAdd(RGBd x) {
        return new RGBd(r+x.r, g+x.g, b+x.b);
    }
    
    public RGBd getMul(double w) {
        return new RGBd(w*r, w*g, w*b);
    }

    
    
    
    
}
