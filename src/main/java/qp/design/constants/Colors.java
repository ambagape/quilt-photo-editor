package qp.design.constants;
import java.awt.Color;
import qp.CNT.INTERNAL_WARNINGS;
import qp.design.Generals;


/**
 *
 * @author Maira57
 */
public class Colors {

    public static final ColorI RED = new ColorI(245, 0, 0, 255);
    public static final ColorI ORANGE = new ColorI(255, 128, 0, 255);
    public static final ColorI YELLOWISH = new ColorI(221, 219, 149, 255);
    public static final ColorI YELLOWISH2 = new ColorI(215, 215, 149, 255);

    public static final ColorI BLUISH = new ColorI(196, 235, 244, 255);
    public static final ColorI BLUE_WEAK = new ColorI(196, 225, 224, 255);
    public static final ColorI BLUE_LIGHT = new ColorI(151, 204, 202, 255);
    public static final ColorI BLUE_DARK = new ColorI(66, 57, 142, 255);
    public static final ColorI BLUE_STRONG = new ColorI(75, 151, 217, 255);
    
    public static final ColorI WHITE = new ColorI(255, 255, 255, 255);
    public static final ColorI WHITE_SEMI = new ColorI(255, 255, 255, 150);
    public static final ColorI GRAY_LIGHT = new ColorI(240, 240, 240, 255);
    public static final ColorI GRAY_DARK = new ColorI(200, 200, 200, 255);
    public static final ColorI BLACK = new ColorI(0, 0, 0, 255);

    public static final ColorI TRANSPARENT = new ColorI(0, 0, 0, 0);

    public static final ColorI INPUT_TEXT = new ColorI(80, 80, 80, 255);
    public static final ColorI INPUT_FILL = WHITE;
    public static final ColorI BUTTON_TEXT = new ColorI(51, 51, 51, 255);
    public static final ColorI GRAY_FILL = new ColorI(238, 238, 238, 255);
    public static final ColorI LABEL_TEXT = new ColorI(51, 51, 51, 255);
    public static final ColorI GRAY = new ColorI(245, 245, 245, 255);
    public static final ColorI DISABLED_TEXT = new ColorI(153, 153, 153, 255);



//    // debugging
//
//    /**
//     * pink_ - background (gradient color 1)
//     * green_ = background (gradient color 2) AND 'white'_labels
//     * yellow_ - input fill
//     * magenta_ - label AND disabled_text
//     * blue_ - input AND button
//     */
//    private static final ColorI pink_ = new ColorI(255, 128, 192, 255);
//    private static final ColorI green_ = new ColorI(0, 164, 0, 255);
//    private static final ColorI yellow_ = new ColorI(Color.yellow);
//    private static final ColorI magenta_ = new ColorI(Color.magenta);
//    private static final ColorI blue_ = new ColorI(Color.blue);
//
//    public static final ColorI RED = pink_;
//    public static final ColorI ORANGE = pink_;
//    public static final ColorI YELLOWISH = pink_;
//    public static final ColorI YELLOWISH2 = pink_;
//
//    public static final ColorI BLUISH = pink_;
//    public static final ColorI BLUE_WEAK = pink_;
//    public static final ColorI BLUE_LIGHT = pink_;
//    public static final ColorI BLUE_DARK = pink_;
//    public static final ColorI BLUE_STRONG = pink_;
//
//    public static final ColorI WHITE = green_;
//    public static final ColorI WHITE_SEMI = green_;
//    public static final ColorI GRAY_LIGHT = green_;
//    public static final ColorI GRAY_DARK = green_;
//    public static final ColorI BLACK = green_;
//
//    public static final ColorI TRANSPARENT = new ColorI(0, 0, 0, 0);
//
//    public static final ColorI INPUT_TEXT = blue_;
//    public static final ColorI INPUT_FILL = yellow_;
//    public static final ColorI BUTTON_TEXT = blue_;
//    public static final ColorI GRAY_FILL = yellow_;
//    public static final ColorI LABEL_TEXT = magenta_;
//    public static final ColorI GRAY = magenta_;
//    public static final ColorI DISABLED_TEXT = magenta_;



    public static final Color red = RED.getColor();
    public static final Color orange = ORANGE.getColor();
    public static final Color yellowish = YELLOWISH.getColor();
    public static final Color yellowish2 = YELLOWISH2.getColor();

    public static final Color bluish = BLUISH.getColor();
    public static final Color blue_weak = BLUE_WEAK.getColor();
    public static final Color blue_light = BLUE_LIGHT.getColor();
    public static final Color blue_dark = BLUE_DARK.getColor();
    public static final Color blue_strong = BLUE_STRONG.getColor();

    public static final Color white = WHITE.getColor();
    public static final Color white_semi = WHITE_SEMI.getColor();
    public static final Color gray_light = GRAY_LIGHT.getColor();
    public static final Color gray_dark = GRAY_DARK.getColor();
    public static final Color black = BLACK.getColor();

    public static final Color transparent = TRANSPARENT.getColor();

    public static final Color input_text = INPUT_TEXT.getColor();
    public static final Color input_fill = INPUT_FILL.getColor();
    public static final Color button_text = BUTTON_TEXT.getColor();
    public static final Color gray_fill = GRAY_FILL.getColor();
    public static final Color label_text = LABEL_TEXT.getColor();
    public static final Color gray = GRAY.getColor();
    public static final Color disabled_text = DISABLED_TEXT.getColor();



    @SuppressWarnings(INTERNAL_WARNINGS.UNCHECKED)
    public static void initializeUI() throws Exception {
        Generals.putDefault("ScrollBar.background", gray_fill);
        Generals.putDefault("CheckBox.background", gray_fill);

        Generals.putDefault("TextField.background", input_fill);
        Generals.putDefault("TextArea.background", input_fill);
        Generals.putDefault("Spinner.background", input_fill);
        Generals.putDefault("FormattedTextField.background", input_fill);


        Generals.putDefault("OptionPane.messageForeground", label_text);
        Generals.putDefault("Label.foreground", label_text);
        Generals.putDefault("Label.disabledForeground", disabled_text);
        Generals.putDefault("ComboBox.disabledForeground", disabled_text);

        Generals.putDefault("Button.foreground", button_text);
        Generals.putDefault("RadioButton.foreground", label_text);
        Generals.putDefault("CheckBox.foreground", button_text);
        Generals.putDefault("Slider.foreground", button_text);
        Generals.putDefault("ToolTip.foreground", label_text);

        Generals.putDefault("ComboBox.foreground", input_text);
        Generals.putDefault("TextField.foreground", input_text);
        Generals.putDefault("TextArea.foreground", input_text);
        Generals.putDefault("Spinner.foreground", input_text);
        Generals.putDefault("FormattedTextField.foreground", input_text);
        
        Generals.putDefault("TabbedPane.selected", blue_light);
        Generals.putDefault("TabbedPane.contentAreaColor", blue_light);
    }





}
