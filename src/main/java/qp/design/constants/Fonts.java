package qp.design.constants;
import java.awt.Font;
import qp.design.Generals;


/**
 *
 * @author Maira57
 */
public class Fonts {

    /**
     * Fonts names list.
     */
    private static class FNAME {

        private static final String[] list = new String[] {
            "Arial"
        };

        public static final String ARIAL = list[0];

    }



    private static Font f01;
    private static Font f02;
    private static Font f03;
    private static Font f04;
    private static Font f05;
    private static Font f06;

    private static String fontNameGeneral;
    private static int fontSizeGeneral;



    public static void initializeUI() throws Exception {
        if (System.getProperty("os.name").contains("Window")) {
            fontNameGeneral = new String(FNAME.ARIAL);

            fontSizeGeneral = 15;
        }
        else if (System.getProperty("os.name").contains("Mac")) {
            fontNameGeneral = new String(FNAME.ARIAL);

            fontSizeGeneral = 15;
        }
        else {
            fontNameGeneral = new String(FNAME.ARIAL);

            fontSizeGeneral = 15;
        }


        f01 = new Font(fontNameGeneral, Font.PLAIN, fontSizeGeneral-2);
        f02 = new Font(fontNameGeneral, Font.BOLD, fontSizeGeneral);
        f03 = new Font(fontNameGeneral, Font.BOLD, fontSizeGeneral-2);
        f04 = new Font(fontNameGeneral, Font.BOLD, fontSizeGeneral-3);
        f05 = new Font(fontNameGeneral, Font.PLAIN, fontSizeGeneral-3);
        f06 = new Font(fontNameGeneral, Font.PLAIN, fontSizeGeneral);


//        // debugging
//        f01 = f01.deriveFont(Font.ITALIC);
//        f02 = f02.deriveFont(Font.ITALIC);
//        f03 = f03.deriveFont(Font.ITALIC);
//        f04 = f04.deriveFont(Font.ITALIC);
//        f05 = f05.deriveFont(Font.ITALIC);
//        f06 = f06.deriveFont(Font.ITALIC);


        Generals.putDefault("OptionPane.font", Fonts.button());
        Generals.putDefault("Label.font", Fonts.label());

        Generals.putDefault("Button.font", Fonts.button());
        Generals.putDefault("Menu.font", Fonts.button());
        Generals.putDefault("MenuItem.font", Fonts.button());
        Generals.putDefault("RadioButton.font", Fonts.button());
        Generals.putDefault("CheckBox.font", Fonts.button());
        Generals.putDefault("Slider.font", Fonts.toolTip());
        Generals.putDefault("ToolTip.font", Fonts.toolTip());

        Generals.putDefault("ComboBox.font", Fonts.input());
        Generals.putDefault("TextField.font", Fonts.input());
        Generals.putDefault("PasswordField.font", Fonts.input());
        Generals.putDefault("Spinner.font", Fonts.input());
        Generals.putDefault("FormattedTextField.font", Fonts.input());
    }



    public static Font label() throws Exception {
        return f01;
    }

    public static Font labelSmall() throws Exception {
        return f05;
    }

    public static Font labelSmallBold() throws Exception {
        return f04;
    }

    public static Font button() throws Exception {
        return f01;
    }

    public static Font slider() throws Exception {
        return f01;
    }

    public static Font input() throws Exception {
        return f01;
    }

    public static Font toolTip() throws Exception {
        return f01;
    }

    public static Font title() throws Exception {
        return f02;
    }

    public static Font blueTitle() throws Exception {
        return f03;
    }

    public static Font checkBoxBigger() throws Exception {
        return f02;
    }

    public static Font progress() throws Exception {
        return f04;
    }

    public static Font tabContent() throws Exception {
        return f06;
    }





}
