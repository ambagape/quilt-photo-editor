package qp.design.components;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import qp.Logger;
import qp.control.MainController;
import qp.design.Generals;
import qp.design.constants.ColorI;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class JButtonColored extends JButton {

    private static final ColorI gradientColor = Colors.WHITE;



    private String text;
    private Image image;

    private boolean useGradient = false;

    private boolean isMouseOver = false;
    private boolean isMousePressed = false;



    public JButtonColored(String text, boolean gradient) throws Exception {
        super();

        setBackground(Colors.gray_fill);
        useGradient = gradient;
        this.text = text;

        setCallbacks();
    }

    public JButtonColored(
            String text,
            Image image,
            boolean gradient)
            throws Exception
    {
        super();

        setBackground(Colors.gray_fill);
        useGradient = gradient;
        this.text = text;
        this.image = image;

        setCallbacks();
    }



    protected @Override void paintComponent(Graphics g) {
        try {

        super.paintComponent(g);

        int w, h;
        ColorI c_back;
        Graphics2D g2d;
        int dx, dy;

        g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        w = getPreferredSize().width;
        h = getPreferredSize().height;

        c_back = new ColorI(
            getBackground().getRed(),
            getBackground().getGreen(),
            getBackground().getBlue(),
            255);
        
        if (useGradient) {
            if (h == 25) {
                g2d.setPaint(new GradientPaint(
                    0, 0, gradientColor.getColor(),
                    0, h, c_back.getColor(), false));
            }
            else {
                g2d.setPaint(new GradientPaint(
                    0, 0, c_back.getColor(),
                    0, h, gradientColor.getColor(), false));
            }
            g2d.fillRect(0, 0, w, h);

            g2d.setColor(getForeground());
        }
        else {
            if (isMouseOver && !isMousePressed) {
                g2d.setPaint(new GradientPaint(
                    0, h/3, c_back.getColor(),
                    0, 0, Colors.white, false));
                g2d.fillRect(0, 0, w, h);

                g2d.setPaint(new GradientPaint(
                    0, h/3, Colors.white,
                    0, h, c_back.getColor(), false));
                g2d.fillRect(0, 0, w, h);

                g2d.setColor(getForeground());
            }
            else {
                g2d.setColor(c_back.getColor());
                g2d.fillRect(0, 0, w, h);

                g2d.setColor(getForeground());
            }
        }

        if (isMousePressed) {
            dx = 0;
            dy = +2;
        }
        else {
            dx = 0;
            dy = 0;
        }

        if (image != null) {
            g2d.drawImage(image,
                            dx + getWidth()/2 - image.getWidth(this)/2,
                            dy + 5,
                            this);
        }

        // '-2' from the third parameter is an adjustion, on vertical,
        // that makes the text align nice vertically
        if (h == 25) {
            g2d.drawString(
                    text,
                    w/2 - g.getFontMetrics().stringWidth(text)/2,
                    h - g.getFontMetrics().getHeight()/2 + 0);
        }
        else {
            g2d.drawString(
                    text,
                    w/2 - g.getFontMetrics().stringWidth(text)/2,
                    h - g.getFontMetrics().getHeight()/2 - 2);
        }

        if (!this.isEnabled()) {
            g2d.setColor(new ColorI(
                Colors.gray_light.getRed(),
                Colors.gray_light.getGreen(),
                Colors.gray_light.getBlue(),
                100
                ).getColor());
            g2d.fillRect(0, 0, w, h);
        }

        c_back.dispose();

        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.fatalErrorsOccured(true);
        }
    }



    public void setImage(Image image) throws Exception {
        this.image = image;
    }

    public void setDesiredSize(Dimension size) throws Exception {
        setPreferredSize(size);
        setMaximumSize(size);
    }



    private void setCallbacks() throws Exception {

        addMouseListener(new MouseAdapter() {
            public @Override void mousePressed(MouseEvent ev) {
                try {

                if (!Generals.isLeftMouseButton(ev)) {
                    return;
                }

                isMousePressed = true;

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseReleased(MouseEvent ev) {
                try {

                if (!Generals.isLeftMouseButton(ev)) {
                    return;
                }

                isMousePressed = false;

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseEntered(MouseEvent ev) {
                try {

                isMouseOver = true;

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseExited(MouseEvent ev) {
                try {

                isMouseOver = false;

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

    }





}
