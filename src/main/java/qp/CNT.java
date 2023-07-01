package qp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import javax.swing.ImageIcon;
import qp.database.CommunicationLocal;
import qp.design.Generals;


/**
 *
 * @author Maira57
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class CNT {

    // debugging constants
    public static final boolean AS_FOR_RELEASE = true;



    public static enum VERSION_TYPE {
        NORMAL,
        DEMO,
        TRIAL,
        BETA
    }

//    public static final VERSION_TYPE version = VERSION_TYPE.BETA;
//    public static final VERSION_TYPE version = VERSION_TYPE.DEMO;
    public static final VERSION_TYPE version = VERSION_TYPE.NORMAL;
//    public static final VERSION_TYPE version = VERSION_TYPE.TRIAL;



    public static enum PRODUCT_TYPE {
        DELUXE,
        PHOTO_EXPRESS,
        LANDSCAPE_EXPRESS,
        COLOR_VALUATIONS,
        STITCH_A_SKETCH
    }

//    public static final PRODUCT_TYPE product = PRODUCT_TYPE.COLOR_VALUATIONS;
//    public static final PRODUCT_TYPE product = PRODUCT_TYPE.LANDSCAPE_EXPRESS;
//    public static final PRODUCT_TYPE product = PRODUCT_TYPE.DELUXE;
//    public static final PRODUCT_TYPE product = PRODUCT_TYPE.PHOTO_EXPRESS;
    public static final PRODUCT_TYPE product = PRODUCT_TYPE.STITCH_A_SKETCH;

    
    
    /**
     * 'QPP10' indicates program version, '001' indicates file version
     */
    public static final String FORMAT_VERSION = "QPP10001";



    public static class PATHS {

        /** base path - for developer */
        private static final String base =
                new String("input\\");

        /** application settings file name - for developer */
        public static final String applicationSettingsFileName =
                new String(base + "application_settings.txt");

        /** runtime logs - for developer */
        public static final String log =
                new String(base + "log\\");

        /** fabric collection folder - for developer */
        public static final String fabricsColl =
                new String(base + "MosaicFabricCollection\\");
    
        /** temporary folder, for different image operations - for developer */
        public static File dirTmp() throws Exception {
            String s;
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
                s = "QPD";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
                s = "QPX";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
                s = "QLX";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                s = "CV";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                s = "SAS";
            }
            else {
                throw Exceptions.badIfBranch(CNT.product);
            }
            
            return new File(
                Generals.getAbsolutePath()
                + "\\MQS\\" + s);
        }

        /** fabric collection base - for developer */
        public static final String fabricsCollectionBase =
                new String("name.txt");

        /** local database - for developer */
        public static final String database =
                new String("C:/JavaProj/MosaicQuiltStudio_02/database"
                + "\\Database_QPD\\");

        /** original input image, unaltered - for developer */
        public static String original_backup_file() throws Exception {
//            return base + "tmp_orig.png";
            return dirTmp() + "\\" + "tmp_orig.png";
        }

        /** processed image, for print - for developer */
        public static String processed_print_file() throws Exception {
//            return base + "tmp_print.png";
            return dirTmp() + "\\" + "tmp_print.png";
        }

        public static String licenseFileName() throws Exception {
            String s;
            
 //           s = new String(System.getenv("SystemRoot") + "\\system32\\");
            s = dirTmp() + "\\data\\";
//            s = dirTmp() + "\\";
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
                s += "qpd";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
                s += "qpx";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
                s += "qlx";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                s += "cv";
             }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                s += "sas";
            }
            else {
                throw Exceptions.badIfBranch(CNT.product);
            }
            
            if (CNT.version.equals(CNT.VERSION_TYPE.TRIAL)) {
                s += "tm3.dat";
            }
            else if (CNT.version.equals(CNT.VERSION_TYPE.BETA)) {
                s += "bm3.dat";
            }
            else {
                throw Exceptions.badIfBranch(CNT.version);
            }
            
            return s;
        }
        


        /** base path  - for developer */
        static final String JARbase =
                new String("resources/");

        /** images path - for developer */
        static final String images =
                new String(JARbase + "images/");

        /** icons path - for developer */
        static final String icons =
                new String(JARbase + "icons/");

        /** icons path (icons for setIconImage()) - for developer */
        public static final String icons2 =
                new String(JARbase + "icons2/");

        /** patterns path - for developer */
        public static final String patterns =
                new String(JARbase + "patterns/");



        public static String getFileNameWithoutPath(
                String fileName)
                throws Exception
        {
            int index1, index2;

            index1 = fileName.lastIndexOf('/');
            index2 = fileName.lastIndexOf('\\');

            if (index1 > index2) {
                if (index1 < 0) {
                    return fileName;
                }
                else {
                    return fileName.substring(index1+1);
                }
            }
            else {
                if (index2 < 0) {
                    return fileName;
                }
                else {
                    return fileName.substring(index2+1);
                }
            }
        }

        public static String getFileNameWithoutExtension(
                String fileName)
                throws Exception
        {
            int index;

            index = fileName.lastIndexOf('.');

            if (index < 0) {
                return fileName;
            }
            return fileName.substring(0, index);
        }

        public static String getExtension(
                String fileName)
                throws Exception
        {
            return fileName.substring(fileName.lastIndexOf('.')+1);
        }

        public static String getShortFilename(
                String filename,
                int maxLetters)
                throws Exception
        {
            String s;

            s = filename;

            if (s.isEmpty()) {
                return s;
            }

            s = s.substring(s.lastIndexOf('/')+1);
            s = s.substring(s.lastIndexOf('\\')+1);
            if (s.indexOf('.')>=0) {
                s = s.substring(0, s.lastIndexOf('.'));
            }
            if (s.length() > maxLetters) {
                s = s.substring(0, maxLetters-3) + "...";
            }
            return s;
        }
        
        public static String getPreviewImageName(int index) throws Exception {
            return dirTmp().getAbsolutePath()
                    + "\\" + index + ".png";
        }

    }



    public static final Class resourcesClass = Main.class;

//    private static final BufferedImage debugBim =
//        CommunicationLocal.getImageFromJAR(
//            resourcesClass, PATHS.images + "none.png");
//
//    private static final ImageIcon debugIcon =
//        CommunicationLocal.getImageIconFromJAR(
//            resourcesClass, PATHS.images + "none.png");

    /** icons used in the interface design */
    public static class ICONS {

        public static BufferedImage LOGO() throws Exception {
            String str;
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
                str = "QPD";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
                str = "QPX";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
                str = "QLX";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                str = "CV";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                str = "SAS";
            }
            else {
                throw Exceptions.badIfBranch(CNT.product);
            }
            
            return CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons2 + "logo_" + str + ".png");
        }

        public static final BufferedImage CORNER_LOGO =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "corner_logo.png");

        public static final BufferedImage BRIGHTNESS =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "brightness.png");
        public static final BufferedImage CONTRAST =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "contrast.png");
        
        public static final BufferedImage FIT =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "fit.png");

        public static final BufferedImage SEP =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "separator.png");

        public static final ImageIcon WARNING =
            CommunicationLocal.getImageIconFromJAR(
                resourcesClass, PATHS.icons + "warning.png");
        public static final ImageIcon ERROR =
            CommunicationLocal.getImageIconFromJAR(
                resourcesClass, PATHS.icons + "error.png");
        public static final ImageIcon Q_BLUE =
            CommunicationLocal.getImageIconFromJAR(
                resourcesClass, PATHS.icons + "question_blue.png");
        public static final ImageIcon Q_RED =
            CommunicationLocal.getImageIconFromJAR(
                resourcesClass, PATHS.icons + "question_red.png");
        public static final ImageIcon INFO =
            CommunicationLocal.getImageIconFromJAR(
                resourcesClass, PATHS.icons + "information.png");

        public static final BufferedImage NEW =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "new.png");
        public static final BufferedImage OPEN =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "open.png");
        public static final BufferedImage SAVE =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "save.png");
        public static final BufferedImage PRINT =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "print.png");
        public static final BufferedImage IMPORT =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "import.png");
        public static final BufferedImage ADJUST =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "adjust.png");
        public static final BufferedImage PROCESS =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "process.png");
        public static final BufferedImage PALETTE =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "palette.png");
        public static final BufferedImage HELP =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.icons + "help.png");


//        public static final BufferedImage LOGO = debugBim;
//        
//        public static final BufferedImage BRIGHTNESS = debugBim;
//        public static final BufferedImage CONTRAST = debugBim;
//        
//        public static final BufferedImage FIT = debugBim;
//        
//        public static final BufferedImage SEP = debugBim;
//
//        public static final ImageIcon WARNING = debugIcon;
//        public static final ImageIcon ERROR = debugIcon;
//        public static final ImageIcon Q_BLUE = debugIcon;
//        public static final ImageIcon Q_RED = debugIcon;
//        public static final ImageIcon INFO = debugIcon;

//        public static final BufferedImage NEW = debugBim;
//        public static final BufferedImage OPEN = debugBim;
//        public static final BufferedImage PRINT = debugBim;
//        public static final BufferedImage SAVE = debugBim;
//        public static final BufferedImage IMPORT = debugBim;
//        public static final BufferedImage ADJUST = debugBim;
//        public static final BufferedImage PROCESS = debugBim;
//        public static final BufferedImage PALETTE = debugBim;
//        public static final BufferedImage HELP = debugBim;

    }



    /** images used in the interface design */
    public static class IMAGES {

        /// when re-rendering these images (and probably others too)
        /// keep in mind that they have to be generated through Photoshop
        /// (and not Paint or IrfanView)

        public static final BufferedImage BIG_LOGO_A =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.images + "big_logo_a.png");
        public static final BufferedImage BIG_LOGO_B =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.images + "big_logo_b.png");
        public static final BufferedImage BIG_LOGO_C =
            CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.images + "big_logo_c.png");
        
        public static BufferedImage SPLASH() throws Exception {
            String str;
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
                str = "QPD";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
                str = "QPX";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
                str = "QLX";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                str = "CV";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                str = "SAS";
            }
            else {
                throw Exceptions.badIfBranch(CNT.product);
            }

            return CommunicationLocal.getImageFromJAR(
                resourcesClass, PATHS.images + "splash_" + str + ".png");
        }

    }



    public static final String importPathDefault =
            new String("C:/JavaProj/MosaicQuiltStudio_02/sample_images");
    public static final String openPathDefault =
            new String(PATHS.database);
    public static final String savePathDefault =
            new String(PATHS.database);
    public static final String exportPathDefault =
            new String(System.getProperty("user.home") + "\\Desktop");



    public static class FORMAT {

        private static final String[] formatChars = new String[] {
            "; ",
            " ",
            "[\\(\\)\\ \\t\\;]+"
        };

        public static final String ELEM_SEP_MAIN = formatChars[0];
        public static final String ELEM_SEP_3RD = formatChars[1];

        public static final String ELEM_SPACES = formatChars[2];
        
        public static final String PARAMS_SEP = " ";

        public static final String HTML = new String("text/html");
        public static final String HTTP_HEADER = new String("http");
        
        public static final String BTN_NAME = "%d";
        public static final String PALETTE_INDEX = "%d";
        public static final String PALETTE_INDEX_EXT = "%d (%s)";
        public static final String SLIDER = "(%d)";
        
        public static final String SPINNER_INT_3_DEC = new String("###");
        public static final String SPINNER_CHARS =
                new String("0123456789");
        public static final String SPINNER_CHARS_WITH_DECIMAL_POINT =
                new String("0123456789.");
        
        public static final String PRINT_PAPER_SIZE = "%.1f";
        public static final String PRINT_PAGES_COUNT = "%d";
        public static final String PRINT_DESIGN_INFO = "%1.2f in. x %1.2f in.";

        public static final String NULL = new String("null");
        public static final String STR_NULL = null;


        public static String getDateString(Calendar date) throws Exception {
            String dateString;

            dateString = String.format("%d-%d-%d %d:%d:%d",
                        date.get(Calendar.YEAR),
                        (date.get(Calendar.MONTH) + 1),
                        date.get(Calendar.DAY_OF_MONTH),
                        date.get(Calendar.HOUR_OF_DAY),
                        date.get(Calendar.MINUTE),
                        date.get(Calendar.SECOND));

            return dateString;
        }

    }

    public static class FORMAT_COMMUNICATION {

        public static final String COMMENT = new String("//");

        public static final String NEW_LINE =
            System.getProperty("line.separator");

        
        public static final String HTTP_HEADER = new String("http");

        
        public static final String EXT_POINT = new String(".");

        public static final String EXT_PNG = new String("png");
        public static final String EXT_JPG = new String("jpg");
        public static final String EXT_QPD = new String("qpd");
        
        public static boolean extensionAcceptable(String fname) throws Exception {
            if (!fname.endsWith("jpg") && !fname.endsWith("jpeg") &&
                    !fname.endsWith("bmp") && !fname.endsWith("png"))
            {
                return false;
            }
            
            return true;
        }

    }



    public static final class INTERNAL_WARNINGS {

        public static final String UNCHECKED = "unchecked";

    }



    /** 1 second time (in milliseconds), for Thread.sleep() */
    public static final int SECOND = 1000;



}


