package qp.design.components;
import java.awt.Dimension;
import javax.swing.JPanel;


/**
 *
 * @author Maira57
 */
public class JPanelGlass extends JPanel {



    private void init() throws Exception {
        this.setOpaque(false);
    }

    public JPanelGlass() throws Exception {
        super();

        init();
    }

    public JPanelGlass(int width, int height) throws Exception {
        super();

        init();
        this.setMaximumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
    }

    public JPanelGlass(Dimension d) throws Exception {
        super();

        init();
        this.setMaximumSize(d);
        this.setPreferredSize(d);
    }





}
