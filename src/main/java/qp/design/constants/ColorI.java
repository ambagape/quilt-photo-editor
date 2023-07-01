package qp.design.constants;
import java.awt.Color;
import qp.Logger;


/**
 *
 * @author Maira57
 */
public class ColorI {

    private Color m_color;



    public ColorI() throws Exception {
        m_color = new Color(0, 0, 0, 255);
    }

    public ColorI(ColorI color) throws Exception {
        this(color.getColor());
    }

    /**
     *
     * Does not throw Exception because it is used for constants
     *
     * @param r red component
     * @param g green component
     * @param b blue component
     * @param a alpha (transparency) component
     */
    public ColorI(int r, int g, int b, int a) {
        m_color = new Color(r, g, b, a);
    }

    public ColorI(Color color) {
        m_color = color;
    }

    public void dispose() throws Exception {
        m_color = null;
    }



    /** CAREFUL: setColor(...) will create a new m_color object */
    public void setColor(int r, int g, int b, int a) throws Exception {
        m_color = new Color(r, g, b, a);
    }

    /**
     *
     * Does not throw Exception because it is used for constants
     *
     * @return the Color value of this ColorI object.
     */
    public Color getColor() {
        return m_color;
    }

    public float[] getColorfv() throws Exception {
        return new float[] {
                    m_color.getRed()/255.0f,
                    m_color.getGreen()/255.0f,
                    m_color.getBlue()/255.0f,
                    m_color.getAlpha()/255.0f };
    }

    public void setAlpha(int a) throws Exception {
        m_color = new Color(
                        m_color.getRed(),
                        m_color.getGreen(),
                        m_color.getBlue(),
                        a);
    }

    public boolean isEqualTo(ColorI c) {
        try {

        Color c1, c2;

        c1 = m_color;
        c2 = c.m_color;
        return ((c1.getRed() == c2.getRed()) &&
                (c1.getGreen() == c2.getGreen()) &&
                (c1.getBlue() == c2.getBlue()) &&
                (c1.getAlpha() == c2.getAlpha()));

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }





}
