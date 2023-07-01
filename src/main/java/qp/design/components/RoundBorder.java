package qp.design.components;
import java.awt.*;
import javax.swing.border.AbstractBorder;


/**
 *
 * @author Maira57
 */
public class RoundBorder extends AbstractBorder {

    private final static int MARGIN = 10;
    private Color color;

    
    
    public RoundBorder(Color clr) {
        color = clr;
    }

    
    
    public void setColor(Color clr) {
        color = clr;
    }
   
    public @Override void paintBorder(
            Component c, Graphics g, int x, int y, int width, int height)
    {
        Graphics2D g2d;
        
        g2d = (Graphics2D)g;
        
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        
        g2d.drawRoundRect(x+2, y+2, width-5, height-5, MARGIN*2, MARGIN*2);
    }

    public @Override Insets getBorderInsets(Component c) {
        return new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
    }

    public @Override Insets getBorderInsets(Component c, Insets insets) {
        insets.left = MARGIN;
        insets.top = MARGIN;
        insets.right = MARGIN;
        insets.bottom = MARGIN;
        return insets;
    }
    
    
    
    
    
}
