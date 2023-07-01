package qp.control.types;


/**
 *
 * @author Maira57
 */
public class QPattern {

    public short[] p;
    public short[] e;
    public int w;
    public int h;
    public double scale;
    public double x_scale;
    public double shapes_divisor;
    

        
    public QPattern() {
        p = new short[0];
        e = new short[0];
        
        w = 0;
        h = 0;
        scale = 0.0;
        x_scale = 0.0;
        shapes_divisor = 0.0;
    }
    
    public QPattern(
            Object[] obj,
            double s, double x_s, double div)
    {
        p = (short[])obj[0];
        e = (short[])obj[1];
        
        w = (Integer)obj[2];
        h = (Integer)obj[3];
        scale = s;
        x_scale = x_s;
        shapes_divisor = div;
    }
    
    
    
    public static short[] mirror_tb(short[] p_, int width, int half_h) {
        short num_shapes = 0;
        short[] new_p;
        
        new_p = new short[width * (2*half_h)];
        
        System.arraycopy(p_, 0, new_p, 0, half_h*width);
        
        for (int i=0; i<half_h*width; i++) {
            if (p_[i] >= num_shapes) num_shapes = p_[i];
        }
        num_shapes += 1;
        
        for (int j=0; j<half_h; j++) {
            for (int i=0; i<width; i++) {
                new_p[(j+half_h)*width+i] = (short)(p_[(half_h-1-j)*width+i] + num_shapes);
            }
        }
        
        return new_p;
    }
    
    
    
    
    
}
