package qp;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import qp.design.dialogs.FileSelector.SELECTOR_TYPE;


/**
 *
 * @author Maira57
 */
public class DS {



    static String applicationName() throws Exception {
        String str;
        
        if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
            str = "Quilted Photo Deluxe";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
            str = "Quilted Photo Xpress";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            str = "Quilted Landscape Xpress";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            str = "Color Valuations";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            str = "Stitch-A-Sketch";
        }
        else {
            throw Exceptions.badIfBranch(CNT.product);
        }
        
        return str;
    };
        
    public static String getVersion() throws Exception {
        if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
            return "2.0";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
            return "5.0";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            return "1.0";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            return "2.0";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            return "1.0";
        }
        else {
            throw Exceptions.badIfBranch(CNT.product);
        }
    };
    
    private static String website() throws Exception {
        if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
            return "www.QuiltedPhoto.com";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
            return "www.QuiltedPhoto.com";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            return "www.QuiltedLandscape.com";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            return "www.ColorValuations.com";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            return "www.Stitch-A-Sketch.com";
        }
        else {
            throw Exceptions.badIfBranch(CNT.product);
        }
    };
    
    private static String expiredBetaLink() throws Exception {
        String s;
        
        s = new String("http://TammieBowser.com/beta");
        
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
        
        return s;
    };

    public static String startFile() throws Exception {
        String str;

        if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
            str = "Mona_lisa.qpd";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
            str = "Mona_lisa.qpd";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            str = "Leaf.qpd";
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            str = new String();
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            str = "Sketch.qpd";
        }
        else {
            throw Exceptions.badIfBranch(CNT.product);
        }
        
        if (str.isEmpty()) {
            return str;
        }
        else {
            return "input\\" + str;
        }
    }
    
    static final String fabricNowApp = new String(
        "FABRIC NOW<sup>TM</sup>"
    );
    
    private static String tmpFileName;
    
    public static void setTmpFileName(String value) throws Exception {
        tmpFileName = value;
    }
    public static String getTmpFileName() throws Exception {
        return tmpFileName;
    }



    public static class MAIN {

        public static String getTitle() throws Exception {
            String str;
            
            str = applicationName() + " " + getVersion();
            
            if (CNT.version.equals(CNT.VERSION_TYPE.DEMO)) {
                return str + " (Demo Version)";
            }
            else if (CNT.version.equals(CNT.VERSION_TYPE.TRIAL)) {
                return str + " (Trial Version)";
            }
            else if (CNT.version.equals(CNT.VERSION_TYPE.BETA)) {
                return str + " (Beta Version)";
            }
            else {
                return str;
            }
        }
        
        public static String[] menuStr() throws Exception {
            String[] str;
            
            str = new String[] {
                "File",
                "New",
                "Open...",
                "Save",
                "Save As...",
                "Export to Jpeg...",
                "Page Setup",
                "Print Preview",
                "Print...",
                "Exit",
                
                "View",
                "<html><center>Pixel Shapes",
                "<html><center>Contoured Technique",
                "<html><center>Quilted Landscape Technique",
                "<html><center>Fabric \"Valuations\"",
                "<html><center>" + fabricNowApp + " Fabric Collections",
                
                "Help",
                "Video Help",
                "About",
            };
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                str[12] = "Stitck-A-Sketch";
            }
            
            return str;
        }

        public static char[] menuMnm() throws Exception {
            return new char[] {
                'F',
                'N',
                'O',
                'S',
                'A',
                'J',
                'e',
                'v',
                'p',
                'x',
                
                'V',
                'P',
                'C',
                'r',
                'V',
                'F',
                
                'H',
                'H',
                'A',
            };
        }

        public static String helpLink() throws Exception {
            String s;
            
            s = new String("http://www.FreeQuiltClass.com/");
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
                s += "qpd2";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
                s += "qpx5";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
                s += "qlx1";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                s += "cv2";
            }
            else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                s += "sas1";
            }
            else {
                throw Exceptions.badIfBranch(CNT.product);
            }
            
            if (CNT.version.equals(CNT.VERSION_TYPE.NORMAL)) {
            }
            else if (CNT.version.equals(CNT.VERSION_TYPE.DEMO)) {
                s += "demo";
            }
            else if (CNT.version.equals(CNT.VERSION_TYPE.TRIAL)) {
                s += "trial";
            }
            else if (CNT.version.equals(CNT.VERSION_TYPE.BETA)) {
                s += "beta";
            }
            else {
                throw Exceptions.badIfBranch(CNT.version);
            }
            
            return s;
        }
        
        
        public static String[] mainButtons() throws Exception {
            return new String[] {
                "New project",
                "Open project",
                "Save project",
                
                "Print",
                
                "Import photo",
                "Adjust Colors",
                
                "Process Image",
                
                "Print Palette",
                
                "Video Help"
            };
        }

        public static String[] panelCentral() throws Exception {
            return new String[] {
                "Original Photograph",
                "Restore Original",
                "<html><center><div width=175>"
                    + " * Hold down left mouse button and drag cursor"
                    + " to crop the original photograph.",
                
                "Processed Photograph"
            };
        }

        public static String[] pixelEditor() throws Exception {
            return new String[] {
                "Photograph Pixel Editor",
                
                "Photo Detail",
                "Fabric Quantity (maximum)",
                
                "Color Effect",
                "<html>Realistic color",
                "<html>Grayscale",
                "<html>Sepia",
                "<html>" + fabricNowApp + " <br/>Fabric Collections",
                
                "Grid Angle",
                "<html>0 *",
                "<html>30 *",
                "<html>45 *",
                
                "Select Palette..."
            };
        }

        public static String output_detail(int value) throws Exception {
            return String.format("%d pieces", value);
        }

        public static String output_fabric(int value) throws Exception {
            return String.format("%d fabrics", value);
        }
        

        public static final String panelTabsMainTitle
            = new String("Quilting Technique Editor");
        
        public static String[] panelTabTitles() throws Exception {
            String[] str;
            
            str = new String[] {
                "<html><center>Pixel Shapes",
                "<html><center>Contoured Technique",
                "<html><center>Quilted Landscape Technique",
                "<html><center>Fabric \"Valuations\"",
                "<html><center>" + fabricNowApp + " Fabric Collections",
                "<html><center>Video Help"
            };
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                str[1] = "Stitck-A-Sketch";
            }
            
            return str;
        }

        public static String[] panelTabFunctions() throws Exception {
            return new String[] {
                "-",
                "+"
            };
        }
        
        public static String[] panelTab01() throws Exception {
            return new String[] {
                "<html><div width=350>"
                    + "'Quilted Photography' is based on pixels. The pixels"
                    + " you use for your quilts do not have to be"
                    + " square. Please select one of the pixel"
                    + " shapes on the right for your quilt."
                    + "<p><p>"
                    + "To learn more about pixels and 'Quilted"
                    + " Photography' go to the 'Video Help' tab"
                    + " for a video lesson!",
            };
        }

        public static String[] panelTab02() throws Exception {
            String[] str;
            
            str = new String[] {
                "<html><div width=350>"
                    + "For this technique, the geometric shapes will merge"
                    + " together to form irregular, organic shapes."
                    + " Go to the 'Video Help' tab to learn how"
                    + " to make unbelievable 'Contoured' quilts!",
                    
                "Use this feature",
                
                "Shape Smoothing:  ",
                "Increase to make shapes more rounded and curvy."
            };
            
            if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                str[0] = 
                    "<html><div width=350>"
                        + "Use this tool to adjust the shapes to your"
                        + " liking. Go to the 'Video Help' tab to learn"
                        + " how to correctly use software.";
            }
            
            return str;
        }

        public static String[] panelTab03() throws Exception {
            return new String[] {
                "<html><div width=350>"
                    + "Do you like crazy quilts? Or foundation paper"
                    + " piecing? Well, you can have both! The 'Quilted"
                    + " Landscape' technique is the best way to make"
                    + " quilts of landscapes or flowers!"
                    + "<p><p>"
                    + "Go to the 'Video Help' tab to learn how to"
                    + " make amazing quilts using the"
                    + " 'Quilted Landscape Technique'.",
                    
                "Use this feature"
            };
        }

        public static String[] panelTab04() throws Exception {
            return new String[] {
                "Load Fabrics",
                "Sort By Value",
                "Remove All",
                "Print Fabrics",
                
                "Use this feature",
                
                "Go to the 'Video Help' tab to learn how to use"
                    + " 'Valuations' to sort your fabric and"
                    + " preview your quilt!"
            };
        }

        public static String[] panelTab05() throws Exception {
            return new String[] {
                "<html><div width=450>"
                    + "These fabric collections are designed to print"
                    + " directly on plain fabric... You can create your"
                    + " own custom fabric collection with " + fabricNowApp
                    + " software."
                    + "<p><p>"
                    + "Load a collection from right, then click"
                    + " \"Process Photo\" to see the results in the"
                    + " \"Processed Photograph\" window."
                    + "<p><p>"
                    + "Go to the 'Video Help' tab to learn how to use"
                    + " '" + fabricNowApp + " Fabric Collections' to print"
                    + " your quilts and print your fabric!",
                
                "Import ...",
                "Remove selected"
            };
        }
        
    }

    public static class PAGE_SETUP_DIALOG {

        public static String getTitle() throws Exception {
            return "Page Setup";
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Fill Pattern Cells",
                "Conserve Paper",
                
                "Quilted Landscape Technique (blocks / page):",
                "1",
                "2",
                "4",
                "6",

                "Print",
                "Print Preview",
                "Ok",
                "Cancel"
            };
        }

        public static String[] strScaling() throws Exception {
            return new String[] {
                "Scaling",

                "Size of Unit:",
                "inches",
                
                "Sheets:",
                "Design size:"
            };
        }

        public static String[] strRange() throws Exception {
            return new String[] {
                "Range",

                "All Sheets",
                "Sheets:",
                "to",
                
                "Row, Col",
                "Col, Row"
            };
        }

        public static String[] strOrientation() throws Exception {
            return new String[] {
                "Orientation",

                "Portrait",
                "Landscape"
            };
        }

        public static String[] strCopies() throws Exception {
            return new String[] {
                "Copies",

                "Number of Copies:",
                
                "Collate",
                
                "Total # of Sheets:"
            };
        }

        public static String[] strPaperSize() throws Exception {
            return new String[] {
                "Paper Size",

                "Width:",
                "Length:",
                "inches",
                "inches"
            };
        }

    }

    public static class FABRIC_PAGE_SETUP_DIALOG {

        public static String getTitle() throws Exception {
            return "Fabric Sheets Printing Setup";
        }

        public static String[] str() throws Exception {
            return new String[] {
                "spaces between squares",

                "Print",
                "Cancel"
            };
        }

        public static String[] strScaling() throws Exception {
            return new String[] {
                "Scaling",

                "Size of Unit:",
                "inches"
            };
        }

        public static String[] strPaperSize() throws Exception {
            return new String[] {
                "Paper Size",

                "Width:",
                "Length:",
                "inches",
                "inches"
            };
        }

    }

    public static class PREVIEW_DIALOG {

        public static String getTitle() throws Exception {
            return "Print Preview";
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Page:",
                
                "Print",
                "Cancel"
            };
        }

    }

    public static class PROCESS_PROGRESS_DIALOG {

        public static String getTitle() throws Exception {
            return new String();
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Processing..."
            };
        }

    }

    public static class PRINT_PROGRESS_DIALOG {

        public static String getTitle() throws Exception {
            return new String();
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Generating printable pattern, please wait"
            };
        }

    }

    public static class PALETTE_DIALOG {

        public static String getTitle() throws Exception {
            return "Palette";
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Print entire palette",
                "Print selected color/fabric",
                "Done"
            };
        }

    }

    public static class ADJUST_DIALOG {

        public static String getTitle() throws Exception {
            return "Adjust colors";
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Original Image",
                "Adjusted Image",

                "Controls",
                "Brightness",
                "Contrast",

                "Ok",
                "Cancel"
            };
        }

    }

    public static class SELECTOR_DIALOG {

        public static String getTitle(SELECTOR_TYPE type) throws Exception {
            switch (type) {
                case IMPORT_FABRIC: return "Choose one or more fabric images";

                case IMPORT_FABRIC_COLL: return "Choose a collection";

                case IMPORT_PHOTO: return "Choose a photo";

                case OPEN_PROJECT: return "Open project";

                case SAVE_PROJECT: return "Save project";

                case EXPORT_TO_JPG: return "Export to Jpeg";

                default: throw Exceptions.badSwitchBranch(type);
            }
        }

        public static String getApproveButtonText(
                SELECTOR_TYPE type)
                throws Exception
        {
            switch (type) {
                case IMPORT_FABRIC: return "Import";

                case IMPORT_FABRIC_COLL: return "Import";

                case IMPORT_PHOTO: return "Import";

                case OPEN_PROJECT: return "Open";

                case SAVE_PROJECT: return "Save";

                case EXPORT_TO_JPG: return "Export";

                default: throw Exceptions.badSwitchBranch(type);
            }
        }

        public static FileFilter[] getFilter(SELECTOR_TYPE type) throws Exception {
            switch (type) {
                case IMPORT_FABRIC:
                case IMPORT_PHOTO:
                    return new FileFilter[] {
                        new FileNameExtensionFilter(
                            "Image files (jpg,jpeg,bmp,png)",
                            "jpg",
                            "jpeg",
                            "bmp",
                            "png"
                        ),
                        new FileNameExtensionFilter(
                            "Windows Bitmap (bmp)",
                            "bmp"
                        ),
                        new FileNameExtensionFilter(
                            "Portable Network Graphics (png)",
                            "png"
                        ),
                        new FileNameExtensionFilter(
                            "JPEG images (jpg,jpeg)",
                            "jpg",
                            "jpeg"
                        )
                    };

                case IMPORT_FABRIC_COLL:
                    return new FileFilter[] {
                    };

                case OPEN_PROJECT:
                case SAVE_PROJECT:
                    return new FileFilter[] {
                        new FileNameExtensionFilter(
                            "Quilted Photo Deluxe project file (\"qpd\")",
                            "qpd"
                        )
                    };

                case EXPORT_TO_JPG:
                    return new FileFilter[] {
                        new FileNameExtensionFilter(
                            "JPEG images (jpg,jpeg)",
                            "jpg",
                            "jpeg"
                        )
                    };

                default: throw Exceptions.badSwitchBranch(type);
            }
        }

    }

    public static class ABOUT_DIALOG {

        public static String getTitle() throws Exception {
            return "About " + MAIN.getTitle();
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Ok"
            };
        }

        public static String getText() throws Exception {
            return
                "<html><div width=350><center>"

                + "<font size=\"+1\"><b>" + applicationName() + "</b></font><br/>"
                + "<b>Version " + getVersion() + "</b><p>"

                + "Software and design © 2006-2012 Mosaic Quilt Studio.<br/>"
                + " All rights reserved.<p>"

                + "Companion books and video lessons are available at:"
                + " <a href=\"http://" + website() + "\">"
                + "" + website() + "</a>"
                + " and <a href=\"http://www.TammieBowser.com\">"
                + "www.TammieBowser.com</a><p>"

                + "<b>Mosaic Quilt Studio</b>";
        }

    }

    public static class HELP_DIALOG {

        public static String getTitle() throws Exception {
            return applicationName() + " - Video Help";
        }

    }

    public static class LINK_DIALOG {

        public static String[] str() throws Exception {
            return new String[] {
                "Ok"
            };
        }

    }

    public static class PALETTE_SELECTOR_DIALOG {

        public static String getTitle() throws Exception {
            return "Select Palette";
        }

        public static String[] str() throws Exception {
            return new String[] {
                "Click on a swatch to remove from the palette.",
                
                "Regenerate pattern",
                "Restore original palette",
                "Done"
            };
        }

    }

    

    public static class PROCESS_CONTROLLER {
        
        public static final String FF_FILL = new String("fill");
        public static final String FF_NEXT_PIXEL = new String("next_pixel");
        public static final String FF_PREV_PIXEL = new String("previous_pixel");
        
        public static String[] patternLabels() throws Exception {
            return new String[] {
                "A", "B", "C", "D", "E", "F"
            };
        }
        
        public static String patternLabelSimple = new String("%d");
        public static String patternLabelExtended = new String("%s%d");
        
    }
    
    public static class PRINT_CONTROLLER {

        public static final String initParamsPattern = new String(
                "1.0 true 1 1 true true 1 false 2");

        public static final String initParamsFabrics = new String(
                "1.0 2 false");
        
        public static String defaultJobName = new String("Default");
        
        public static String headerPatternInfo(
                String fileName)
                throws Exception
        {
            return String.format(
                    "Image: %s"
                        + "    "
                        + website(),
                    fileName);
        }

        public static String headerPatternContent(
                String fileName,
                int row,
                int column)
                throws Exception
        {
            return String.format(
                 "Image: %s"
                     + "    "
                     + "Row: %d, Column: %d"
                     + "    "
                     + website(),
                fileName.isEmpty() ? "(default)" : fileName, row, column
            );
        }

        public static String headerFabricSingle(
                int fabricIndex)
                throws Exception
        {
            return String.format(
                applicationName() + " "
                    + "    "
                    + "Fabric %d"
                    + "    "
                    + website(),
                fabricIndex
            );
        }

        public static String headerFabricAll() throws Exception {
            return String.format(
                applicationName() + " "
                    + "    "
                    + "All fabrics"
                    + "    "
                    + website()
            );
        }

        public static String getFooter() throws Exception {
            return new String(
                    "Go to " + website() + " for more creative,"
                        + " innovative and exclusive ideas.");
        }
        
        public static String[] patternGeneralInfo() throws Exception {
            return new String[] {
                "Estimated finished size (not including seam allowances):",
                "%d\" x %d\"",
                
                "Yardage Usage Chart",
                "Use this chart to help determine the amount of fabric",
                "to prepare for your Quilted Photo. The yardage chart is",
                "based on 44\"/45\" wide fabrics. The numbers in the chart",
                "give the maximum number of squares the yardage will yield.",
                
                "                         | 1/16 yd | 1/8 yd | 1/4 yd | 1/2 yd",
                "    3/4\" Squares |   117    |  235    |  470    |  940",
                "       1\" Squares |    94     |  188    |  376    |  752",
                " 1 1/4\" Squares |    55     |  110    |  220    |  440",
                " 1 1/2\" Squares |    46     |   92     |  184    |  368",
                "       2\" Squares |    23     |   46     |   92     |  184"
            };
        }
        
        public static String[] patternFabricsInfo() throws Exception {
            return new String[] {
                "Fabric",
                "Area (in )",
                "Area (in    ",
                "2",
                "% 5d"
            };
        }

    }
    


    public static class DIALOGS {

        private static final String[] titles = new String[] {
            "Warning",
            "Error",
            "Information",
            "Check"
        };

        public static final String titleWarning = titles[0];
        public static final String titleError = titles[1];
        public static final String titleInformation = titles[2];
        public static final String titleCheck = titles[3];


        public static String[] texts() throws Exception {
            return new String[] {
                "Major errors have been encountered! "
                    + "Application will now exit.",
                "<html><center>"
                    + "<b>TRIAL HAS EXPIRED</b><br/>"
                    + "Thank you for trying out this 7 day trial of "
                    + applicationName() + " software."
                    + " I hope you enjoyed learning "
                    + (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)
                        ? "these techniques" : "'‘Quilted Photography’")
                    + ".<br/>"
                    + "<p>"
                    + "If you want to get the full version of the software follow this link: "
                    + "<a href=\""
                    + "http://QuiltedPhoto.com/products/software/"
                    + "\">http://QuiltedPhoto.com/products/software/</a>"
                    + "<p>"
                    + "To get FREE SHIPPING<br/>"
                    + "enter coupon code: "
                    + "<font color=\"red\"> <b> FREESHIP </b> </font>",
                "<html><center>"
                    + "<b>BETA TEST HAS ENDED</b><br/>"
                    + "Thank you for participating in my 14 day,"
                    + " software beta program."
                    + " I hope you enjoyed learning ‘Quilted Photography’.<br/>"
                    + "<p>"
                    + "I'm looking forward to reading your feedback."
                    + " It will help me make future versions of "
                    + applicationName() + " software even better!<br/>"
                    + "<p>"
                    + "To enter your feedback and to get your Free gift,"
                    + " please go to this link: "
                    + "<a href=\""
                    + expiredBetaLink()
                    + "\">" + expiredBetaLink() + "</a>",

                "Error loading file! Please try again.",
                "File does not exist.",
                "Error saving file! Please try again.",
                "Some application files were not installed properly.\n"
                    + "Please uninstall the application and then install"
                    + " it again.",
                
                "Unknown file version encountered. File not loaded.",
                "The saved project uses a fabric collection that"
                    + " is not installed ('" + tmpFileName + "')."
                    + "  The project will be loaded, but will"
                    + " not use the fabric collection.",

                "Collection \"" + tmpFileName + "\" already exists!"
                    + " Do you want to overwrite it?",
                "<html>Are you sure you want to permanently delete this "
                    + fabricNowApp +  " fabric collection?",

                "There are no fabrics loaded.",
                "No printers installed on this computer.",
                
                "This is a demo version. Printing is not available.",
                "This is a demo version. Saving is not available.",
                
                "Last changes have not been saved. Proceed anyway?",
                "No image loaded.",
                "Printing not allowed for User Fabrics.",
                "Are you sure you want to remove all fabrics?",
                "File \"" + tmpFileName + "\" already exists!"
                    + " Do you want to overwrite it?",
                "Process failed!\n"
                    + "Try to reduce 'Photo Detail' and/or"
                    + " 'Fabric Quantity' and then click"
                    + " 'Process image' again."
            };
        }

    }





}
