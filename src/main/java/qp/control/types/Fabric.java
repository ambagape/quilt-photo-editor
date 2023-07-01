package qp.control.types;
import java.awt.image.BufferedImage;


/**
 *
 * @author Maira57
 */
public class Fabric {

    public BufferedImage img;
    public RGB8 color;
    public String fname;
    
    
    
    public Fabric() {
        img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        color = new RGB8();
        fname = new String();
    }
    
    
    
    public boolean isBrighter(Fabric fab) throws Exception {
        int x = 30*color.r + 59*color.g + 11*color.b;
        int y = 30*fab.color.r + 59*fab.color.g + 11*fab.color.b;

        return x > y;
    }

    
    
    
    
}
