package qp.design.components;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import qp.Logger;
import qp.control.MainController;
import qp.design.constants.ColorI;


/**
 *
 * @author Maira57
 */
public class JLabelGlass extends JLabel {

    private ColorI desiredForeground;

    

    private void init(ColorI desiredForeground) throws Exception {
        this.setOpaque(false);
        this.desiredForeground = desiredForeground;
    }

    public JLabelGlass() throws Exception {
        super();

        init(new ColorI(getForeground()));
    }

    public JLabelGlass(String text) throws Exception {
        super(text);

        init(new ColorI(getForeground()));
    }

    public JLabelGlass(String text, Font font) throws Exception {
        super(text);

        init(new ColorI(getForeground()));
        this.setFont(font);
    }

    public JLabelGlass(ImageIcon image) throws Exception {
        super(image);

        init(new ColorI(getForeground()));
    }



    public void setDesiredForeground(ColorI foreground) throws Exception {
        this.desiredForeground = foreground;
    }

    public @Override void setEnabled(boolean value) {
        try {

        super.setEnabled(value);

        if (getClientProperty(BasicHTML.propertyKey) != null) {
            setForeground((isEnabled()) ?
                desiredForeground.getColor() :
                SystemColor.textInactiveText);
        }

        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.fatalErrorsOccured(true);
        }
    }



}
