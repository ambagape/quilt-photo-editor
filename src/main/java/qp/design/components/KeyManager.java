package qp.design.components;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.KeyStroke;
import qp.Logger;


/**
 *
 * @author Maira57
 */
public class KeyManager implements KeyEventDispatcher, KeyListener {

    public static final int KEY_PRESSED = KeyEvent.KEY_PRESSED;
    public static final int KEY_RELEASED = KeyEvent.KEY_RELEASED;


    public static final int VK_ESCAPE = KeyEvent.VK_ESCAPE;

    public static final int VK_A = KeyEvent.VK_A;
    public static final int VK_B = KeyEvent.VK_B;
    public static final int VK_I = KeyEvent.VK_I;
    public static final int VK_M = KeyEvent.VK_M;
    public static final int VK_O = KeyEvent.VK_O;
    public static final int VK_P = KeyEvent.VK_P;
    public static final int VK_R = KeyEvent.VK_R;
    public static final int VK_S = KeyEvent.VK_S;

    public static final int VK_1 = KeyEvent.VK_1;
    public static final int VK_2 = KeyEvent.VK_2;
    public static final int VK_3 = KeyEvent.VK_3;
    public static final int VK_4 = KeyEvent.VK_4;
    public static final int VK_5 = KeyEvent.VK_5;
    public static final int VK_6 = KeyEvent.VK_6;
    public static final int VK_7 = KeyEvent.VK_7;
    public static final int VK_8 = KeyEvent.VK_8;

    public static final int VK_F1 = KeyEvent.VK_F1;



    public @Override boolean dispatchKeyEvent(KeyEvent ev) {
        try {

        return dispatchEvent(
                ev.getSource(),
                ev.getID(),
                ev.getKeyCode(),
                new boolean[] { ev.isControlDown(),
                                ev.isAltDown(),
                                ev.isShiftDown() });

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public boolean dispatchEvent(
            Object source, int eventType, int key, boolean[] specialKeys)
            throws Exception
    {
        return false;
    }



    public @Override void keyTyped(KeyEvent ev) {
        try {

        if (typed(ev.getKeyCode(), ev.getKeyChar(), ev.getSource())) {
            ev.consume();
        }

        }
        catch (Exception e) { Logger.printErr(e); }
    }

    public @Override void keyPressed(KeyEvent ev) {
        try {

        if (pressed(ev.getKeyCode(), ev.getKeyChar(), ev.getSource())) {
            ev.consume();
        }

        }
        catch (Exception e) { Logger.printErr(e); }
    }

    public @Override void keyReleased(KeyEvent ev) {
        try {

        if (released(ev.getKeyCode(), ev.getKeyChar(), ev.getSource())) {
            ev.consume();
        }

        }
        catch (Exception e) { Logger.printErr(e); }
    }

    public boolean typed(
            int keyCode, char key, Object source)
            throws Exception
    {
        return false;
    }

    public boolean pressed(
            int keyCode, char key, Object source)
            throws Exception
    {
        return false;
    }

    public boolean released(
            int keyCode, char key, Object source)
            throws Exception
    {
        return false;
    }



    public static String getKeyText(int key) throws Exception {
        return KeyEvent.getKeyText(key);
    }

    public static KeyStroke getKeyStroke(String s) throws Exception {
        return KeyStroke.getKeyStroke(s);
    }

    public static KeyStroke getKeyStroke(
            int key, int modifiers)
            throws Exception
    {
        return KeyStroke.getKeyStroke(key, modifiers);
    }





}
