package qp.design.components;
import javax.swing.JCheckBox;


/**
 *
 * @author Maira57
 */
public class JCheckBoxGlass extends JCheckBox {

    private Object tag;



    public JCheckBoxGlass(String text) throws Exception {
        super(text);

        this.setOpaque(false);
    }

    public JCheckBoxGlass(String text, Object tag) throws Exception {
        super(text);

        this.setOpaque(false);
        this.tag = tag;
    }



    public Object getTag() throws Exception {
        return tag;
    }




}
