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
public class JButtonRounded extends JButton {

    private static final ColorI disabledBackcolor = Colors.GRAY_DARK;
    
    private static final int mg = 20;



    private String text;
    
    private Color highlightColor;

    private boolean isMouseOver = false;
    private boolean isMousePressed = false;



    public JButtonRounded(String text) throws Exception {
        super();

        setBackground(Colors.gray_fill);
        setBorderPainted(false);
        this.text = text;
        
        highlightColor = Colors.white;

        setCallbacks();
    }



    protected @Override void paintComponent(Graphics g) {
        try {

        int w, h;
        ColorI c_back;
        ColorI c2;
        Graphics2D g2d;

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
        c2 = new ColorI(
                c_back.getColor().getRed() - 20,
                c_back.getColor().getGreen() - 20,
                c_back.getColor().getBlue(),
                c_back.getColor().getAlpha()
                );
        
        if (isMouseOver) {
            if (isEnabled()) {
                if (isMousePressed) {
                    g2d.setColor(c2.getColor());
                    g2d.fillRoundRect(0, 0, w, h, mg, mg);
                }
                else {
                    g2d.setPaint(new GradientPaint(
                        0, h/3, c_back.getColor(),
                        0, 0, highlightColor, false));
                    g2d.fillRoundRect(0, 0, w, h, mg, mg);

                    g2d.setPaint(new GradientPaint(
                        0, h/3, highlightColor,
                        0, h, c_back.getColor(), false));
                    g2d.fillRoundRect(0, 0, w, h, mg, mg);
                }
            }
            else {
                g2d.setColor(c_back.getColor());
                g2d.fillRoundRect(0, 0, w, h, mg, mg);
            }
        }
        else {
            g2d.setColor(c_back.getColor());
            g2d.fillRoundRect(0, 0, w, h, mg, mg);
        }
        g2d.setColor(c2.getColor());
        g2d.drawRoundRect(1, 1, w-2, h-2, mg, mg);

        g2d.setColor(getForeground());
        
        // '-2' from the third parameter is an adjustion, on vertical,
        // that makes the text align nice vertically
        g2d.drawString(
                text,
                w/2 - g.getFontMetrics().stringWidth(text)/2,
                h - g.getFontMetrics().getHeight()/2 - (h == 15 ? -7 : 2));

        if (!this.isEnabled()) {
            g2d.setColor(new ColorI(
                Colors.gray_light.getRed(),
                Colors.gray_light.getGreen(),
                Colors.gray_light.getBlue(),
                100
                ).getColor());
            g2d.fillRoundRect(0, 0, w, h, mg, mg);
        }
        
        c_back.dispose();

        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.fatalErrorsOccured(true);
        }
    }



    public void setDesiredSize(Dimension size) throws Exception {
        setPreferredSize(size);
        setMaximumSize(size);
    }
    
    public void setHightlightColor(Color color) throws Exception {
        highlightColor = color;
    }

    
    
    private void setCallbacks() throws Exception {

        addMouseListener(new MouseAdapter() {
            public @Override void mousePressed(MouseEvent ev) {
                try {

                if (!Generals.isLeftMouseButton(ev)) {
                    return;
                }

                isMousePressed = true;
                repaint();

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseReleased(MouseEvent ev) {
                try {

                if (!Generals.isLeftMouseButton(ev)) {
                    return;
                }

                isMousePressed = false;
                repaint();

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseEntered(MouseEvent ev) {
                try {

                isMouseOver = true;
                repaint();

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseExited(MouseEvent ev) {
                try {

                isMouseOver = false;
                repaint();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

    }





}
