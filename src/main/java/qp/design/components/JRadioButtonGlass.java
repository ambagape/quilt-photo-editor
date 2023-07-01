package qp.design.components;
import javax.swing.JRadioButton;


/**
 *
 * @author Maira57
 */
public class JRadioButtonGlass extends JRadioButton {

    private Object tag;


    
    public JRadioButtonGlass(String text) throws Exception {
        super(text);

        this.setOpaque(false);
    }

    public JRadioButtonGlass(String text, Object tag) throws Exception {
        super(text);

        this.setOpaque(false);
        this.tag = tag;
    }



    public Object getTag() throws Exception { return tag; }




}
