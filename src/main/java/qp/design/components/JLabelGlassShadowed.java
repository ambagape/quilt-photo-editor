package qp.design.components;
import java.awt.Graphics;
import javax.swing.JLabel;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class JLabelGlassShadowed extends JLabel {

    
    
    public JLabelGlassShadowed(String text) throws Exception {
        super(text);
    }
    
    
    
    public @Override void paintComponent(Graphics g) {
        g.setColor(Colors.yellowish);
        g.drawString(getText(), 2, 15);
        
        super.paintComponent(g);
    }
    
    
    
    
    
}
