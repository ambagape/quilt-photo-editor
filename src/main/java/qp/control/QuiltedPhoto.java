package qp.control;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import qp.CNT;
import qp.CNT.FORMAT;
import qp.CNT.FORMAT_COMMUNICATION;
import qp.CNT.PATHS;
import qp.DS;
import qp.Exceptions;
import qp.Logger;
import qp.control.types.Fabric;
import qp.control.types.GraphicLabel;
import qp.control.types.RGB8;
import qp.control.types.RGBd;
import qp.database.CommunicationLocal;
import qp.database.Patterns;
import qp.design.Generals;
import qp.design.Messenger;
import qp.design.Messenger.MSG;
import qp.design.constants.ColorI;
import qp.design.dialogs.FileSelector.SELECTOR_TYPE;
import qp.design.dialogs.*;


/**
 *
 * @author Maira57
 */
public class QuiltedPhoto {
    
    private static final char dir_sep = '/';

    

    private static PrintController printController;
    private static FabricPrintController fabricPrintController;
    
    private static ProcessProgressDialog processProgressDialog;

    
    
    private static BufferedImage original_image;
    private static BufferedImage processed_image;
    
    private static String filename;
    private static ArrayList<String> collections_dir;
    private static int selectedFabricCollIndex;
    
    private static ArrayList<int[]> crops;
    
    private static boolean use_qbnpa;
    private static boolean use_cfqp;
    private static int num_colors;
    private static int num_x_tiles;
    private static int grid_angle;
    private static int pattern_index;
    private static int shape_smoothing;
    private static boolean use_grayscale;
    private static boolean use_sepia;
    private static boolean use_user_fabric;
    private static boolean use_fabric_collection;
    
    private static int processed_w, processed_h;
    
    private static boolean processed_qbnpa;
    private static boolean processed_cfqp;
    private static int processed_colors;
    private static int processed_detail;
    private static int processed_grid_angle;
    private static int processed_pattern;
    private static int processed_shape_smoothing;
    private static boolean processed_grayscale;
    private static boolean processed_sepia;
    private static boolean processed_user_fabric;
    private static boolean processed_fabric_collection;
    
    private static ArrayList<Fabric> user_fabrics;
    private static ArrayList<Fabric> fabric_collection;
    private static ArrayList<Fabric> processed_fabrics;
    private static ArrayList<Fabric> processed_collection;
    
    private static ArrayList<RGB8> palette;
    
    private static double[] fabric_areas;
    private static short[][] color_indices;
    private static short[][] pattern_indices;
    private static GraphicLabel[] order_labels;

    private static ArrayList<RGB8> palette_refined;
    private static ArrayList<Fabric> processed_fabrics_refined;
    private static ArrayList<Fabric> processed_collection_refined;
    private static short[][] color_indices_refined;
    private static short[] indicesCorresp;
    private static int num_total_pieces;
    private static int[] processed_num_tiles;
    private static ArrayList<Boolean> fabricsSelected;
    private static int[][] pattern_ext;
    
    private static Object[] olds;
    private static ArrayList<Fabric> fabrics_for_regeneration;
    private static boolean doRegenerate;
    private static boolean afterRegeneratePeriod;
    
    private static boolean changesMade;
    private static boolean imageLoaded;
    private static int brightness, contrast;

    

    public static void startup() throws Exception {
        processProgressDialog = new ProcessProgressDialog();
        
        printController = new PrintController();
        printController.start();
        
        fabricPrintController = new FabricPrintController();
        fabricPrintController.start();


        init_fabric_collections();

        
        new_file(false);
    }
    
    public static void test() throws Exception {
    }

    
    
    public static void new_file(boolean specialNew) throws Exception {
        int old_orig_w, old_orig_h, old_proc_w, old_proc_h;
        
        if (original_image != null) {
            old_orig_w = original_image.getWidth();
            old_orig_h = original_image.getHeight();
        }
        else {
            old_orig_w = 150;
            old_orig_h = 50;
        }
        
        if (processed_image != null) {
            old_proc_w = processed_image.getWidth();
            old_proc_h = processed_image.getHeight();
        }
        else {
            old_proc_w = 150;
            old_proc_h = 50;
        }
        
        
        user_fabrics = new ArrayList<Fabric>();
        fabric_collection = new ArrayList<Fabric>();
        processed_fabrics = new ArrayList<Fabric>();
        processed_collection = new ArrayList<Fabric>();
        
        palette = new ArrayList<RGB8>();
        palette_refined = new ArrayList<RGB8>();
        
        olds = new Object[] { };
        fabrics_for_regeneration = new ArrayList<Fabric>();
        doRegenerate = false;
        afterRegeneratePeriod = false;

        
        filename = new String();
        
        crops = new ArrayList<int[]>();
        
        use_qbnpa = false;
        use_cfqp = false;
        num_colors = 24;
        num_x_tiles = 30;
        grid_angle = 0;
        pattern_index = 0;
        shape_smoothing = 5;
        use_grayscale = true;
        use_sepia = false;
        use_user_fabric = false;
        use_fabric_collection = false;

        original_image = new BufferedImage(old_orig_w, old_orig_h,
                                            BufferedImage.TYPE_INT_ARGB);
        imageLoaded = false;
        CommunicationLocal.saveImage(PATHS.original_backup_file(), original_image);
    
        processed_image = new BufferedImage(old_proc_w, old_proc_h,
                                            BufferedImage.TYPE_INT_ARGB);

        
        MainController.getMainParent().selectFabricCollection(0);
        
        MainController.getMainParent().setProcessedImage(processed_image);
        
        MainController.getMainParent().setParameters(new Object[] {
            original_image,
            use_qbnpa,
            use_cfqp,
            num_colors,
            num_x_tiles,
            grid_angle,
            shape_smoothing,
            use_grayscale,
            use_sepia,
            use_fabric_collection,
            use_user_fabric,
        });

        if (!CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            MainController.getMainParent().setPatternSelected(pattern_index, true);
        }
        
        MainController.getMainParent().setEnabledProcessButton(false);
        MainController.getMainParent().setEnabledRestoreButton(false);
        MainController.getMainParent().setEnabledPrintButton(false);

        
        changesMade = false;
        
        brightness = 0;
        contrast = 0;
        
        
        if (specialNew) {
            load_file(DS.startFile(), false);
            setFilename(new String());
            
            original_image = new BufferedImage(old_orig_w, old_orig_h,
                                                BufferedImage.TYPE_INT_ARGB);
            imageLoaded = false;
            CommunicationLocal.saveImage(PATHS.original_backup_file(), original_image);
            MainController.getMainParent().setOriginalImage(original_image);

            processed_image = new BufferedImage(old_proc_w, old_proc_h,
                                                BufferedImage.TYPE_INT_ARGB);
            MainController.getMainParent().setProcessedImage(processed_image);
            
            MainController.getMainParent().setEnabledProcessButton(false);
            MainController.getMainParent().setEnabledRestoreButton(false);
            MainController.getMainParent().setEnabledPrintButton(false);
            
            changesMade = false;
        }
    }
    
    public static void load_file() throws Exception {
        FileSelector fs;
        Object[] data;
        String fname;
        int returnState;

        fs = new FileSelector(SELECTOR_TYPE.OPEN_PROJECT);
        data = fs.display(SELECTOR_TYPE.OPEN_PROJECT, MainController.getMainParent());
        fname = (String)data[0];
        returnState = (Integer)data[1];

        switch (returnState) {
            case JFileChooser.APPROVE_OPTION:
                break;

            case JFileChooser.CANCEL_OPTION:
                return;

            case JFileChooser.ERROR_OPTION:
                Messenger.show(MSG.FILE_OPEN_FAILED);
                return;

            default: throw Exceptions.badSwitchBranch(returnState);
        }

        load_file(fname, true);
    }
    
    public static void load_file(String fname, boolean withProcess) throws Exception {
        FileInputStream fileIn;
        ObjectInputStream in;
        String version;
        ArrayList<int[]> crops_;
        int use_qbnpa_,
            use_cfqp_,
            num_colors_,
            detail_level_,
            grid_angle_,
            pattern_index_,
            shape_smoothing_,
            use_grayscale_,
            use_sepia_,
            tmp;
        boolean use_user_fabrics_, use_fabric_collection_;
        ArrayList<Fabric> fabrics;
        BufferedImage img;

        try {
            
        fileIn = new FileInputStream(fname);
        in = new ObjectInputStream(fileIn);
        
        version = (String) in.readObject();
        if (!version.equals(CNT.FORMAT_VERSION)) {
            Messenger.show(MSG.FORMAT_UKNOWN_VERSION);
            return;
        }

        crops_ = new ArrayList<int[]>();
        int num_crops;
        num_crops = (Integer) in.readObject();
        for (int i=0; i<num_crops; i++) {
            crops_.add((int[]) in.readObject());
        }

        use_qbnpa_ = (Integer) in.readObject();

        use_cfqp_ = (Integer) in.readObject();
            
        num_colors_ = (Integer) in.readObject();

        detail_level_ = (Integer) in.readObject();

        grid_angle_ = (Integer) in.readObject();

        pattern_index_ = (Integer) in.readObject();

        shape_smoothing_ = (Integer) in.readObject();

        use_grayscale_ = (Integer) in.readObject();

        use_sepia_ = (Integer) in.readObject();

        tmp = (Integer) in.readObject();
        use_user_fabrics_ = (tmp == 1);
        use_fabric_collection_ = (tmp == 2);

        fabrics = new ArrayList<Fabric>();

        if (use_user_fabrics_) {
            int num_user_fabrics;
            
            num_user_fabrics = (Integer) in.readObject();
            
            for (int i=0; i<num_user_fabrics; i++) {
                fabrics.add((Fabric) in.readObject());
            }
        }
        else if (use_fabric_collection_) {
        }
        
        // finally, read the source image
        ByteArrayInputStream bais;
        byte[] bytes;
        
        bytes = (byte[]) in.readObject();
        bais = new ByteArrayInputStream(bytes);
        img = ImageIO.read(bais);

        
        in.close();
        fileIn.close();
        
        }
        catch (Exception e) {
            Logger.printErr(e);
            Messenger.show(MSG.FILE_OPEN_FAILED);
            return;
        }
        
        
        
        // everything loaded, now let's set the global values
        for (int i=0; i<Patterns.patterns.length; i++) {
            MainController.getMainParent().setPatternSelected(i, false);
        }

        
        original_image = img;
        imageLoaded = true;
        CommunicationLocal.saveImage(PATHS.original_backup_file(), original_image);

        crops = crops_;
        
        use_qbnpa = (use_qbnpa_ == 1) ? true : false;
        use_cfqp = (use_cfqp_ == 1) ? true : false;
        num_colors = num_colors_;
        num_x_tiles = detail_level_;
        grid_angle = grid_angle_;
        pattern_index = pattern_index_;
        shape_smoothing = shape_smoothing_;
        use_grayscale = (use_grayscale_ == 1) ? true : false;
        use_sepia = (use_sepia_ == 1) ? true : false;
        use_user_fabric = use_user_fabrics_;
        use_fabric_collection = use_fabric_collection_;

        
        MainController.getMainParent().setParameters(new Object[] {
            original_image,
            use_qbnpa,
            use_cfqp,
            num_colors,
            num_x_tiles,
            grid_angle,
            shape_smoothing,
            use_grayscale,
            use_sepia,
            use_fabric_collection,
            use_user_fabric,
        });

        if (pattern_index >= Patterns.patterns.length) {
            pattern_index = 0;
        }
        
        MainController.getMainParent().setPatternSelected(pattern_index, true);

        if (use_user_fabric) {
            user_fabrics = fabrics;
            MainController.getMainParent().updateFabrics(getFabrics());
        }
        else if (use_fabric_collection) {
        }
        
        filename = fname;
        
        cropImageByArray();

        if (withProcess) {
            process_image();
        }
    }
    
    public static void save_file() {
        try {
            
        if (CNT.version == CNT.VERSION_TYPE.DEMO) {
            Messenger.show(MSG.RESTRICTION_DEMO_SAVE);
            return;
        }

        if (filename.isEmpty()) {
            save_as();
            return;
        }

        process_image();
        
        FileOutputStream fileOut;
        ObjectOutputStream out;

        fileOut = new FileOutputStream(filename);
        out = new ObjectOutputStream(fileOut);

        try {

        // Write some file version info.
        out.writeObject(CNT.FORMAT_VERSION);
        
        out.writeObject(crops.size());
        for (int i=0; i<crops.size(); i++) {
            out.writeObject(crops.get(i));
        }

        out.writeObject(processed_qbnpa ? 1 : 0);

        out.writeObject(processed_cfqp ? 1 : 0);

        out.writeObject(processed_colors);

        out.writeObject(processed_detail);

        out.writeObject(processed_grid_angle);

        out.writeObject(processed_pattern);

        out.writeObject(processed_shape_smoothing);

        out.writeObject(processed_grayscale ? 1 : 0);

        out.writeObject(processed_sepia ? 1 : 0);

        // next byte indicates mode: 0 = using program colors, 1 = user-supplied fabrics, 2 = fabric_collection
        out.writeObject(processed_user_fabric ? 1 : (processed_fabric_collection ? 2 : 0));

        if (processed_user_fabric) {
            // write all of the user-supplied fabrics
            out.writeObject(processed_fabrics.size());
            
            for (int i=0; i<processed_fabrics.size(); i++) {
                out.writeObject(processed_fabrics.get(i));
            }
        }
        else if (processed_fabric_collection) {
        }

        ByteArrayOutputStream baos;
        byte[] bytes;
        
        baos = new ByteArrayOutputStream();
        ImageIO.write(CommunicationLocal.getImageFromLocal(PATHS.original_backup_file()),
                        FORMAT_COMMUNICATION.EXT_PNG,
                        baos);
        bytes = baos.toByteArray();
        out.writeObject(bytes);
        
        setChangesMade(false);

        }
        catch (Exception e) {
            Logger.printErr(e);
        }
        finally {
            out.close();
            fileOut.close();
        }

        }
        catch (Exception e) {
            Logger.printErr(e);
            Messenger.show(MSG.FILE_SAVE_FAILED);
        }
    }

    public static void save_as() throws Exception {
        if (CNT.version == CNT.VERSION_TYPE.DEMO) {
            Messenger.show(MSG.RESTRICTION_DEMO_SAVE);
            return;
        }

        FileSelector fs;
        Object[] data;
        String fname;
        int returnState;

        fs = new FileSelector(SELECTOR_TYPE.SAVE_PROJECT);
        data = fs.display(SELECTOR_TYPE.SAVE_PROJECT, MainController.getMainParent());
        fname = (String)data[0];
        returnState = (Integer)data[1];

        switch (returnState) {
            case JFileChooser.APPROVE_OPTION:
                break;

            case JFileChooser.CANCEL_OPTION:
                return;

            case JFileChooser.ERROR_OPTION:
                Messenger.show(MSG.FILE_OPEN_FAILED);
                return;

            default: throw Exceptions.badSwitchBranch(returnState);
        }


        if (!fname.contains(FORMAT_COMMUNICATION.EXT_POINT)) {
            fname += FORMAT_COMMUNICATION.EXT_POINT + FORMAT_COMMUNICATION.EXT_QPD;
        }
        DS.setTmpFileName(fname);
        
        if (new File(fname).exists()) {
            if (!Messenger.agree(MSG.WORK_CHECK_SAVE_OVERWRITE)) {
                return;
            }
        }
        
        filename = fname;
        
        save_file();
    }

    public static void setFilename(String name) throws Exception {
        filename = name;
    }
    
    public static void online_help() throws Exception {
        new HelpDialog().setVisible(true);
    }

    public static boolean getChangesMade() throws Exception {
        return changesMade;
    }
    
    public static void setChangesMade(boolean value) throws Exception {
        changesMade = value;
    }
    
    
    
    public static void select_photo() throws Exception {
        FileSelector fs;
        Object[] data;
        String fname;
        int returnState;

        fs = new FileSelector(SELECTOR_TYPE.IMPORT_PHOTO);
        data = fs.display(SELECTOR_TYPE.IMPORT_PHOTO, MainController.getMainParent());
        fname = (String)data[0];
        returnState = (Integer)data[1];

        switch (returnState) {
            case JFileChooser.APPROVE_OPTION:
                break;

            case JFileChooser.CANCEL_OPTION:
                return;

            case JFileChooser.ERROR_OPTION:
                Messenger.show(MSG.FILE_OPEN_FAILED);
                return;

            default: throw Exceptions.badSwitchBranch(returnState);
        }

        BufferedImage img;

        img = CommunicationLocal.getImageFromLocal(fname);

        if (img.getHeight() > 1024 || img.getWidth() > 1024) {
            img = Generals.getScaledImage(img, 1024);
        }
        
        original_image = img;
        imageLoaded = true;
        MainController.getMainParent().setOriginalImage(img);
        CommunicationLocal.saveImage(PATHS.original_backup_file(), img);
    }

    public static void adjustColors() throws Exception {
        Adjuster adjuster;
        Object[] result;
        BufferedImage resultImage;


        if (!imageLoaded) {
            Messenger.show(MSG.WORK_NO_IMAGE_LOADED);
            return;
        }

        adjuster = new Adjuster();
        result = adjuster.open(CommunicationLocal.getImageFromLocal(PATHS.original_backup_file()),
                                brightness,
                                contrast);


        if (result == null) {
            return;
        }

        brightness = (Integer)result[1];
        contrast = (Integer)result[2];

        resultImage = (BufferedImage)result[0];

        original_image = resultImage;
        MainController.getMainParent().setOriginalImage(resultImage);
    }

    private static ArrayList<BufferedImage> getWholePalette() throws Exception {
        ArrayList<BufferedImage> images;
            
        images = new ArrayList<BufferedImage>();

        if (processed_user_fabric
                || processed_fabric_collection
                || afterRegeneratePeriod)
        {
            ArrayList<Fabric> fabrics =
                (afterRegeneratePeriod ?
                    fabrics_for_regeneration :
                    (processed_user_fabric ?
                        user_fabrics :
                        fabric_collection));
            int maxIndices;
            int idx;
            
            if (fabrics.isEmpty()) {
                return images;
            }

            maxIndices = indicesCorresp[0];
            for (int i=0; i<indicesCorresp.length; i++) {
                if (indicesCorresp[i] > maxIndices) {
                    maxIndices = indicesCorresp[i];
                }
            }
            maxIndices++;
            
            for (int i=0; i<maxIndices; i++) {
                idx = -1;
                for (int j=0; j<indicesCorresp.length; j++) {
                    if (indicesCorresp[j] == i) {
                        idx = j;
                        break;
                    }
                }
            
                images.add(Generals.scaleImage(
                                fabrics.get(idx).img, 75, 75, true));
            }
        }
        else {
            if (palette_refined.isEmpty()) {
                return images;
            }

            for (int i=0; i<palette_refined.size(); i++) {
                images.add(Generals.createImage(palette_refined.get(i), 75, 75));
            }
        }
        
        return images;
    }
    
    public static void view_palette() throws Exception {
        if (CNT.version == CNT.VERSION_TYPE.DEMO) {
            Messenger.show(MSG.RESTRICTION_DEMO_PRINT);
            return;
        }

        if (processed_user_fabric) {
            Messenger.show(MSG.WORK_RESTRICTED_PRINTING);
            return;
        }
            
        ArrayList<BufferedImage> images;

        images = getWholePalette();

        if (images.isEmpty()) {
            Messenger.show(MSG.WORK_NO_IMAGE_LOADED);
            return;
        }

        PaletteDialog dg;
        dg = new PaletteDialog(images);
        dg.setLocationRelativeTo(MainController.getMainParent());
        dg.setVisible(true);
    }
    
    public static void select_palette() throws Exception {
        ArrayList<BufferedImage> images;

        
        images = getWholePalette();

        if (images.isEmpty()) {
            Messenger.show(MSG.WORK_NO_IMAGE_LOADED);
            return;
        }
        
        PaletteSelector dg;
        dg = new PaletteSelector(images, fabricsSelected);
        dg.setLocationRelativeTo(MainController.getMainParent());
        dg.setVisible(true);
        
        if (doRegenerate) {
            olds = new Object[] {
                num_colors
            };

            process_image();
            
            doRegenerate = false;
            afterRegeneratePeriod = true;
        }
    }
    
    public static void restore_original_palette(boolean withProcess) throws Exception {
        afterRegeneratePeriod = false;

        if (olds.length > 0) {
            num_colors = (Integer)olds[0];
        }
        MainController.getMainParent().setPreProcessedData(new Object[] {
            num_colors
        });
        
        if (withProcess) {
            process_image();
        }
    }

    public static void restore_original_cb() throws Exception {
        BufferedImage original_backup;

        crops = new ArrayList<int[]>();
        
        original_backup =
            CommunicationLocal.getImageFromLocal(PATHS.original_backup_file());
        
        if (original_backup == null) {
            return;
        }

        original_image = Generals.copyImage(original_backup);
        MainController.getMainParent().setOriginalImage(original_image);
        MainController.getMainParent().setEnabledRestoreButton(false);
    }
    
    public static void setNumXTiles(int value) throws Exception {
        num_x_tiles = value;
    }

    public static void setNumColors(int value) throws Exception {
        num_colors = value;
    }

    public static void setColorPalette(
            boolean useGray,
            boolean useSepia,
            boolean useFabrics)
            throws Exception
    {
        use_grayscale = useGray;
        use_sepia = useSepia;
        use_fabric_collection = useFabrics;
    }

    public static void setGridAngle(int angle) throws Exception {
        grid_angle = angle;
    }

    public static void setUseQbnpa(boolean value) throws Exception {
        use_qbnpa = value;
    }

    public static void setUseCfqp(boolean value) throws Exception {
        use_cfqp = value;
    }

    public static void setUseUserFabric(boolean value) throws Exception {
        use_user_fabric = value;
    }

    public static void setShapeSmoothing(int value) throws Exception {
        shape_smoothing = value;
    }
    
    public static void pattern_button_cb(String name) throws Exception {
        MainController.getMainParent().setPatternSelected(pattern_index, false);
        
        pattern_index = Integer.parseInt(name);
        MainController.getMainParent().setPatternSelected(pattern_index, true);
    }

    public static int getPatternIndex() throws Exception {
        return pattern_index;
    }
    
    public static void doRegenerate() throws Exception {
        doRegenerate = true;
        afterRegeneratePeriod = false;
    }

    public static void setNextPattern() throws Exception {
        if (use_cfqp || use_qbnpa) {
            return;
        }
        
        pattern_button_cb(
            String.format(FORMAT.PALETTE_INDEX,
                            (pattern_index+1)%Patterns.patterns.length));
    }
    
    

    public static void cropImage(int[] crop) throws Exception {
        BufferedImage img;
        Graphics g;
        int cx, cy, cw, ch;

        cx = (int)((double)crop[0] / crop[4] * original_image.getWidth());
        cy = (int)((double)crop[1] / crop[5] * original_image.getHeight());
        cw = (int)((double)crop[2] / crop[4] * original_image.getWidth());
        ch = (int)((double)crop[3] / crop[5] * original_image.getHeight());
        
        if (cx <= 0 || cy <= 0 || cw <= 0 || ch <= 0) {
            return;
        }
        
        img = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        g.drawImage(original_image.getSubimage(cx, cy, cw, ch), 0, 0, null);
        
        original_image = img;
        MainController.getMainParent().setOriginalImage(img);
        
        MainController.getMainParent().setEnabledProcessButton(true);
        MainController.getMainParent().setEnabledRestoreButton(true);
        
        crops.add(crop);
    }
    
    public static void cropImageByArray() throws Exception {
        ArrayList<int[]> tmp;
        
        tmp = new ArrayList<int[]>();
        tmp.addAll(crops);
        
        restore_original_cb();
        
        for (int i=0; i<tmp.size(); i++) {
            cropImage(tmp.get(i));
        }
    }
    
    public static void process_image() throws Exception {
        new Thread() {
            public @Override void run() {
                boolean processSucceeded;
                
                processSucceeded = false;
                
                try {

                if (!imageLoaded) {
                    Messenger.show(MSG.WORK_NO_IMAGE_LOADED);
                }
                else {
                    process_image_core();
                    System.gc();
                }
                processSucceeded = true;

                }
                catch (Exception e) {
                    Logger.printErr(e);
                }
                finally {
                    if (!processSucceeded) {
                        Messenger.show(MSG.WORK_PROCESS_FAILED);
                    }
                    processProgressDialog.setVisible(false);
                }
            }
        }.start();

        processProgressDialog.setLocationRelativeTo(
            MainController.getMainParent().getRefElementForProgressBar());
        processProgressDialog.setVisible(true);
    }
    
    public static void process_image_core() throws Exception {
        BufferedImage src;
        BufferedImage dest;
        
        int num_pieces;
        
        Color c1;
        int x;
        int n;
        

        showProcessProgress(0.0);

        if (doRegenerate) {
            
        }
        else if (use_user_fabric && !user_fabrics.isEmpty()) {
            n = user_fabrics.size();
            num_colors = (n < num_colors ? n : num_colors);
        }
        else if (use_fabric_collection && !fabric_collection.isEmpty()) {
            n = fabric_collection.size();
            num_colors = (n < num_colors ? n : num_colors);
        }
        MainController.getMainParent().setPreProcessedData(new Object[] {
            num_colors
        });
        
        src = original_image;
        src = Generals.scaleImage(src, src.getWidth()/4, src.getHeight()/4, false);
        
        if (use_grayscale || use_sepia
                || use_user_fabric || use_fabric_collection)
        {
            BufferedImage img = Generals.copyImage(src);

            for (int j=0; j<img.getHeight(); j++) {
                for (int i=0; i<img.getWidth(); i++) {
                    c1 = new Color(img.getRGB(i, j));
                    x = (int)(0.3*c1.getRed() + 0.59*c1.getGreen() + 0.11*c1.getBlue());
                    img.setRGB(i, j, new ColorI(x, x, x, 255).getColor().getRGB());
                }
            }

            src = img;
        }

        ArrayList<Fabric> fabrics;

        if (doRegenerate) {
            ArrayList<BufferedImage> images;
            
            images = getWholePalette();
            fabrics = new ArrayList<Fabric>();

            for (int i=0; i<images.size(); i++) {
                if (!fabricsSelected.get(i)) {
                    continue;
                }
                
                Fabric f;
                
                f = new Fabric();
                f.img = images.get(i);
                f.color = average_color(f.img);
                f.fname = String.format(FORMAT.PALETTE_INDEX, i+1);
                
                fabrics.add(f);
            }
            
            fabrics_for_regeneration = fabrics;
        }
        else if (use_user_fabric && !user_fabrics.isEmpty()) {
            fabrics = user_fabrics;
        }
        else if (use_fabric_collection && !fabric_collection.isEmpty()) {
            fabrics = fabric_collection;
        }
        else {
            fabrics = null;
        }

        if (fabrics != null) {
            for (int i=0; i<fabrics.size(); i++) {
                palette.add(fabrics.get(i).color);
            }
        }

        processed_detail = num_x_tiles;
        processed_pattern = pattern_index;
        
        int detail = processed_detail;
        

        

//        double[] sine;
//        double[] cosine;
//        
//        sine = new double[] {0.0, 0.5, 0.7071067811865475};
//        cosine = new double[] {1.0, 0.8660254037844386, 0.7071067811865475};
//        
//        int angle = grid_angle*15 + 15;
//        int w0 = src.getWidth(), h0 = src.getHeight();
//        int w0_ = w0, h0_ = h0;
//        int w1, h1, w2, h2;
//
//        if (grid_angle!=0 && !use_cfqp) {
//            w0 += 2*src.getWidth()/2;
//            h0 += 2*src.getHeight()/2;
//            
//            src = Generals.getMatrixWithAutoBorder(
//                        src, src.getWidth()/2, src.getHeight()/2, false);
//
//            src = Generals.rotateImage(src, 360-angle, false, false);
//            
//            w1 = (int)(h0*sine[grid_angle] + w0*cosine[grid_angle]);
//            h1 = (int)(h0*cosine[grid_angle] + w0*sine[grid_angle]);
//            w2 = (int)(h1*sine[grid_angle] + w1*cosine[grid_angle]);
//            detail *= (int)((double)(w2)/w0 + 0.5);
//        }

        
        
        
        
        

        Object[] result;
        int idx;
        int no_colors_used;
        
        idx = 0;
        
//        no_colors_used = (fabrics != null) ?
//                            fabrics.size() :
//                            num_colors;
        no_colors_used = (doRegenerate) ?
                            fabrics.size() :
                            num_colors;
        
        if (use_cfqp) {
            result = ProcessImage.process_image_cfqp(
                            src,
                            Patterns.cfqp_patterns,
                            Patterns.num_cfqp_shapes,
                            detail,
                            no_colors_used,
                            use_grayscale,
                            use_sepia,
                            fabrics);
            
            num_pieces = (Integer)result[idx++];
            dest = (BufferedImage)result[idx++];
            palette = (ArrayList<RGB8>)result[idx++];
            color_indices = (short[][])result[idx++];
            pattern_indices = (short[][])result[idx++];
            order_labels = (GraphicLabel[])result[idx++];
            processed_num_tiles = (int[])result[idx++];
        }
        else if (use_qbnpa) {
            result = ProcessImage.process_image_qbnpa(
                            src,
                            Patterns.patterns[pattern_index],
                            detail,
                            no_colors_used,
                            shape_smoothing,
                            use_grayscale,
                            use_sepia,
                            fabrics);
            
            num_pieces = (Integer)result[idx++];
            dest = (BufferedImage)result[idx++];
            palette = (ArrayList<RGB8>)result[idx++];
            color_indices = (short[][])result[idx++];
            pattern_indices = (short[][])result[idx++];
            order_labels = (GraphicLabel[])result[idx++];
            processed_num_tiles = (int[])result[idx++];
        }
        else {
            result = ProcessImage.process_image(
                            src,
                            Patterns.patterns[pattern_index],
                            detail,
                            no_colors_used,
                            use_grayscale,
                            use_sepia,
                            fabrics);
            
            num_pieces = (Integer)result[idx++];
            dest = (BufferedImage)result[idx++];
            palette = (ArrayList<RGB8>)result[idx++];
            color_indices = (short[][])result[idx++];
            pattern_indices = (short[][])result[idx++];
            order_labels = (GraphicLabel[])result[idx++];
            processed_num_tiles = (int[])result[idx++];
        }
        
        if (doRegenerate) {
            palette = new ArrayList<RGB8>();
            for (int i=0; i<fabrics.size(); i++) {
                palette.add(fabrics.get(i).color);
            }
        }


//        if (grid_angle!=0 && !use_cfqp) {
//            int x0 = dest.getWidth()/4;
//            int y0 = dest.getHeight()/4;
//            int x1 = x0 + dest.getWidth()/2;
//            int y1 = y0 + dest.getHeight()/2;
//            
//            dest = Generals.rotateImage(dest, angle, false, false);
//            dest = Generals.cropImage(dest, x0, y0, x1, y1);
//            
//            color_indices = Generals.rotateMatrix2(color_indices, angle);
//            color_indices = Generals.cropMatrix(color_indices, x0, y0, x1, y1);
//            
//            pattern_indices = Generals.rotateMatrix2(pattern_indices, angle);
//            pattern_indices = Generals.cropMatrix(pattern_indices, x0, y0, x1, y1);
//            
//            int w = dest.getWidth();
//            int h = dest.getHeight();
//            int[] pieces;
//            
//            pieces = new int[num_pieces];
//
//            for (int j=0; j<h; j++) {
//                for (int i=0; i<w; i++) {
//                    x = pattern_indices[i][j];
//                    assert(x < num_pieces);
//                    if (pieces[x] == 0) pieces[x] = 1;
//                }
//            }
//
//            int count = 0;
//            for (int i=0; i<num_pieces; i++) {
//                if (pieces[i] != 0) {
//                    pieces[i] = count++;
//                }
//            }
//
//            num_pieces = count;
//
//            for (int j=0; j<h; j++) {
//                for (int i=0; i<w; i++) {
//                    pattern_indices[i][j] =
//                            (short)pieces[pattern_indices[i][j]];
//                }
//            }
//        }
//        
//        if (grid_angle!=0 && !use_cfqp) {
//            ArrayList<Integer> indices;
//            int cw, ch;
//            short count;
//            
//            cw = color_indices.length;
//            ch = color_indices[0].length;
//            
//            indices = new ArrayList<Integer>();
//            for (int i=0; i<cw; i++) {
//                for (int j=0; j<ch; j++) {
//                    if (!indices.contains((int)color_indices[i][j])) {
//                        indices.add((int)color_indices[i][j]);
//                    }
//                }
//            }
//            
//            indicesCorresp = new short[num_colors];
//            count = 0;
//            for (int i=0; i<num_colors; i++) {
//                if (indices.contains(i)) {
//                    indicesCorresp[i] = count;
//                    count++;
//                }
//                else {
//                    indicesCorresp[i] = -1;
//                }
//            }
//            
//            color_indices_refined = new short[cw][ch];
//            for (int i=0; i<cw; i++) {
//                for (int j=0; j<ch; j++) {
//                    color_indices_refined[i][j]
//                        = indicesCorresp[(int)color_indices[i][j]];
//                }
//            }
//            
//            palette_refined = new ArrayList<RGB8>();
//            for (int i=0; i<indices.size(); i++) {
//                idx = -1;
//                for (int j=0; j<indicesCorresp.length; j++) {
//                    if (indicesCorresp[j] == i) {
//                        idx = j;
//                        break;
//                    }
//                }
//                
//                palette_refined.add(palette.get(idx));
//            }
//
//            if (use_user_fabric) {
//                processed_fabrics_refined = new ArrayList<Fabric>();
//                for (int i=0; i<indices.size(); i++) {
//                    idx = -1;
//                    for (int j=0; j<indicesCorresp.length; j++) {
//                        if (indicesCorresp[j] == i) {
//                            idx = j;
//                            break;
//                        }
//                    }
//
//                    processed_fabrics_refined.add(user_fabrics.get(idx));
//                }
//            }
//            
//            if (use_fabric_collection) {
//                processed_collection_refined = new ArrayList<Fabric>();
//                for (int i=0; i<indices.size(); i++) {
//                    idx = -1;
//                    for (int j=0; j<indicesCorresp.length; j++) {
//                        if (indicesCorresp[j] == i) {
//                            idx = j;
//                            break;
//                        }
//                    }
//
//                    processed_collection_refined.add(fabric_collection.get(idx));
//                }
//            }
//        }
//        else {
            ArrayList<Integer> indices;
            int cw, ch;
            short count;
            
            cw = color_indices.length;
            ch = color_indices[0].length;
            
            indices = new ArrayList<Integer>();
            for (int i=0; i<cw; i++) {
                for (int j=0; j<ch; j++) {
                    if (!indices.contains((int)color_indices[i][j])) {
                        indices.add((int)color_indices[i][j]);
                    }
                }
            }
            
            indicesCorresp = new short[num_colors];
            count = 0;
            for (int i=0; i<num_colors; i++) {
                if (indices.contains(i)) {
                    indicesCorresp[i] = count;
                    count++;
                }
                else {
                    indicesCorresp[i] = -1;
                }
            }
            
            color_indices_refined = new short[cw][ch];
            for (int i=0; i<cw; i++) {
                System.arraycopy(color_indices[i], 0, color_indices_refined[i], 0, ch);
            }
            
            palette_refined = new ArrayList<RGB8>();
            for (int i=0; i<palette.size(); i++) {
                palette_refined.add(palette.get(i));
            }

            if (use_user_fabric) {
                processed_fabrics_refined = new ArrayList<Fabric>();
                for (int i=0; i<user_fabrics.size(); i++) {
                    processed_fabrics_refined.add(user_fabrics.get(i));
                }
            }

            if (use_fabric_collection) {
                processed_collection_refined = new ArrayList<Fabric>();
                for (int i=0; i<fabric_collection.size(); i++) {
                    processed_collection_refined.add(fabric_collection.get(i));
                }
            }
//        }

        if (!palette.isEmpty()) {
            fabric_areas = ProcessImage.calculate_fabric_area(
                                    color_indices,
                                    palette.size(),
                                    printController.getUnitSize(),
                                    num_x_tiles);
        }
        
        fabricsSelected = new ArrayList<Boolean>();
        for (int i=0; i<palette_refined.size(); i++) {
            fabricsSelected.add(true);
        }

        if (!(use_qbnpa || use_cfqp)) {
            order_labels = ProcessImage.calculate_labels(
                                pattern_indices,
                                color_indices,
                                num_pieces);
        }
        
        processed_w = color_indices.length;
        processed_h = color_indices[0].length;
        processed_user_fabric = use_user_fabric;
        if (processed_user_fabric) {
            processed_fabrics = user_fabrics;
        }
        processed_colors = num_colors;
        processed_grid_angle = grid_angle;
        processed_cfqp = use_cfqp;
        processed_qbnpa = use_qbnpa;
        processed_fabric_collection = (use_fabric_collection && !use_user_fabric);
        if (processed_fabric_collection) {
            processed_collection = fabric_collection;
        }
        processed_pattern = pattern_index;
        processed_colors = num_colors;
        processed_shape_smoothing = shape_smoothing;
        processed_grayscale = use_grayscale;
        processed_sepia = use_sepia;
        
        num_total_pieces = num_pieces;

        Logger.printOut("%d %d (%s, for print)\n",
            dest.getWidth(), dest.getHeight(),
            Patterns.patterns_names[pattern_index]);
        CommunicationLocal.saveImage(PATHS.processed_print_file(), dest);

//        double original_aspect = (double)(w0_)/h0_;
//        int num_x = (int)(num_x_tiles/
//                        Patterns.patterns[processed_pattern].shapes_divisor);
//        int num_y = (int)(num_x/original_aspect+0.5);
//        dest = Generals.scaleImage(dest, w0_, w0_*num_y/num_x, false);
        
        dest = Generals.scaleImage(dest,
                    original_image.getWidth(), original_image.getHeight(), false);
        

        MainController.getMainParent().setPostProcessedData(new Object[] {
            num_total_pieces,
            palette_refined.size()
        });

        processed_image = dest;

        
        
        // for printing-unfilled pattern debug,
        // comment the following line and uncomment the following block
        MainController.getMainParent().setProcessedImage(dest);
//        {
//        BufferedImage bim;
//        int w, h;
//        
//        w = dest.getWidth();
//        h = dest.getHeight();
//        
//        double x0 = 0*1.0;
//        double x1 = (0<1-1 ? (0+1)*1.0 : 1.0);
//        double y0 = 0*1.0;
//        double y1 = (0<1-1 ? (0+1)*1.0 : 1.0);
//        
//        bim = ProcessImage.generate_print_pattern(
//                    getProcessedData(),
//                    x0, x1, y0, y1,
//                    w, h,
//                    60,
//                    true);
//        MainController.getMainParent().setProcessedImage(bim);
//        }

        
        MainController.getMainParent().setEnabledProcessButton(false);
    }

    public static void showProcessProgress(double p) throws Exception {
        processProgressDialog.setValue((int)(p * 100));
    }

    

    private static void init_fabric_collections() throws Exception {
        try {
            
        File dir;
        String dirName;
        String fname;
        String[] dirNames;
        File file;
        ArrayList<String> collectionsDisplayNames;
        BufferedReader f;
        String str;
    
        dirName = PATHS.fabricsColl;
        dir = new File(dirName);
        dirNames = dir.list();

        collections_dir = new ArrayList<String>();
        collectionsDisplayNames = new ArrayList<String>();
        
        for (int i=0; i<dirNames.length; i++) {
            try {
                
            fname = dirName + dir_sep + dirNames[i];
            if (new File(fname).isFile()) {
                continue;
            }
            file = new File(fname + dir_sep + PATHS.fabricsCollectionBase);
            if (!file.exists()) {
                continue;
            }

            f = new BufferedReader(new FileReader(fname + dir_sep + PATHS.fabricsCollectionBase));
            str = f.readLine();
            f.close();
            
            collections_dir.add(fname);
            collectionsDisplayNames.add(str);
            
            }
            catch (Exception e) { Logger.printErr(e); }
        }
        
        MainController.getMainParent()
            .setFabricCollections(collectionsDisplayNames);
        
        selectedFabricCollIndex = -1;
        
        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.hideSplash();
            Messenger.show(MSG.FILE_WRONG_INSTALL);
            throw Exceptions.nullReturnValue();
        }
    }
    
    private static void load_fabric_collection(String dirName) throws Exception {
        File dir;
        String fname;
        String[] filesNames;
        BufferedImage img;
    
//        Logger.printOut("loading files in directory '%s'\n", dirName);
        dir = new File(dirName);
        filesNames = dir.list();

        fabric_collection = new ArrayList<Fabric>();
        
        for (int i=0; i<filesNames.length; i++) {
            fname = dirName + dir_sep + filesNames[i];
            if (new File(fname).isDirectory()) {
                continue;
            }
            if (!FORMAT_COMMUNICATION.extensionAcceptable(fname)) {
                continue;
            }
            
//            Logger.printOut("reading file '%s'\n", filesNames[i]);

            img = CommunicationLocal.getImageFromLocal(fname);

            if (img.getHeight() > 300 || img.getWidth() > 300) {
                img = Generals.getScaledImage(img, 300);
            }
            
            Fabric fab;
            fab = new Fabric();
            fab.img = img;
            fab.color = average_color(fab.img);
            fabric_collection.add(fab);
        }
    }

    public static void choose_fabric() throws Exception {
        FileSelector fs;
        Object[] data;
        File[] files;
        int returnState;

        fs = new FileSelector(SELECTOR_TYPE.IMPORT_FABRIC);
        data = fs.display(SELECTOR_TYPE.IMPORT_FABRIC, MainController.getMainParent());
        files = (File[])data[0];
        returnState = (Integer)data[1];

        switch (returnState) {
            case JFileChooser.APPROVE_OPTION:
                break;

            case JFileChooser.CANCEL_OPTION:
                return;

            case JFileChooser.ERROR_OPTION:
                Messenger.show(MSG.FILE_OPEN_FAILED);
                return;

            default: throw Exceptions.badSwitchBranch(returnState);
        }

        int count;
        
        count = files.length;
        user_fabrics = new ArrayList<Fabric>();
        
        for (int i=0; i<count; i++) {
            try {
                
            String fname;
            BufferedImage img;
            
            fname = files[i].getAbsolutePath();
            img = CommunicationLocal.getImageFromLocal(fname);

            if (img.getHeight() > 300 || img.getWidth() > 300) {
                img = Generals.getScaledImage(img, 300);
            }
            
            Fabric fab;
            fab = new Fabric();
            fab.img = img;
            fab.color = average_color(fab.img);
            fab.fname = PATHS.getFileNameWithoutExtension(fname);

            user_fabrics.add(fab);
            
            }
            catch (Exception e) { Logger.printErr(e); }
        }
        
        MainController.getMainParent().updateFabrics(getFabrics());

        MainController.getMainParent()
            .setEnabledUserFabricsCheck(!user_fabrics.isEmpty());
    }

    public static void sort_fabrics_by_value() throws Exception {
        sortFabrics(user_fabrics);
        
        MainController.getMainParent().updateFabrics(getFabrics());
    }

    public static void remove_all_fabrics() throws Exception {
        if (!Messenger.agree(MSG.WORK_CHECK_REMOVE_FABRICS)) {
            return;
        }

        user_fabrics.clear();
        MainController.getMainParent().updateFabrics(getFabrics());
        
        MainController.getMainParent()
            .setEnabledUserFabricsCheck(false);
    }

    public static void print_fabrics() throws Exception {
        if (CNT.version == CNT.VERSION_TYPE.DEMO) {
            Messenger.show(MSG.RESTRICTION_DEMO_PRINT);
            return;
        }

        if (getFabrics().isEmpty()) {
            Messenger.show(Messenger.MSG.PRINT_NO_FABRICS_LOADED);
            return;
        }
                
        fabricPrintController.open(-2);
    }

    public static void import_fabric_collection() throws Exception {
        FileSelector fs;
        Object[] data;
        String newDir, newDirShort, destDir;
        int returnState;

        fs = new FileSelector(SELECTOR_TYPE.IMPORT_FABRIC_COLL);
        data = fs.display(SELECTOR_TYPE.IMPORT_FABRIC_COLL, MainController.getMainParent());
        newDir = (String)data[0];
        returnState = (Integer)data[1];

        switch (returnState) {
            case JFileChooser.APPROVE_OPTION:
                break;

            case JFileChooser.CANCEL_OPTION:
                return;

            case JFileChooser.ERROR_OPTION:
                Messenger.show(MSG.FILE_OPEN_FAILED);
                return;

            default: throw Exceptions.badSwitchBranch(returnState);
        }

        newDirShort = newDir.substring(newDir.lastIndexOf('\\')+1);
        destDir = PATHS.fabricsColl + dir_sep + newDirShort;
        
        DS.setTmpFileName(newDirShort);
        if (collections_dir.contains(destDir)) {
            if (!Messenger.agree(MSG.FABRIC_COLL_CHECK_OVERWRITE)) {
                return;
            }
            else {
                CommunicationLocal.deleteDir(new File(destDir));
            }
        }

        CommunicationLocal.copyDir(new File(newDir), new File(destDir));

        init_fabric_collections();
    }

    public static void remove_fabric_collection() throws Exception {
        if (!Messenger.agree(MSG.FABRIC_COLL_CHECK_REMOVE)) {
            return;
        }
        
        File f;
        
        f = new File(collections_dir.get(selectedFabricCollIndex));
        
        if (f.exists()) {
            CommunicationLocal.deleteDir(f);
        }
        
        init_fabric_collections();
    }
    
    private static void sortFabrics(ArrayList<Fabric> fabrics) throws Exception {
        int n;
        Fabric f1, f2;
        n = fabrics.size();

        for (int i=0; i < n; i++) {
            for (int j=1; j < (n-i); j++) {
                if (fabrics.get(j).isBrighter(fabrics.get(j-1))) {
                    f1 = fabrics.get(j-1);
                    f2 = fabrics.get(j);
                    fabrics.set(j, f1);
                    fabrics.set(j-1, f2);
                }
            }
        }
    }

    private static RGB8 average_color(BufferedImage img) {
        RGBd color;
        Color tmp;
        
        color = new RGBd(0, 0, 0);

        for (int j=0; j<img.getHeight(); j++)
            for (int i=0; i<img.getWidth(); i++) {
                tmp = new Color(img.getRGB(i, j));
                        
                color.r += tmp.getRed();
                color.g += tmp.getGreen();
                color.b += tmp.getBlue();
            }

        return new RGB8(
                (int)(color.r/(img.getWidth()*img.getHeight()) + 0.5),
                (int)(color.g/(img.getWidth()*img.getHeight()) + 0.5),
                (int)(color.b/(img.getWidth()*img.getHeight()) + 0.5)
        );
    }

    private static ArrayList<BufferedImage> getFabrics() throws Exception {
        ArrayList<BufferedImage> images;
        
        images = new ArrayList<BufferedImage>();
        for (int i=0; i<user_fabrics.size(); i++) {
            images.add(Generals.scaleImage(
                            user_fabrics.get(i).img, 110, 130, true));
        }
        
        return images;
    }

    private static ArrayList<BufferedImage> getFabricsColl() throws Exception {
        ArrayList<BufferedImage> images;
        
        images = new ArrayList<BufferedImage>();
        for (int i=0; i<fabric_collection.size(); i++) {
            images.add(Generals.scaleImage(
                            fabric_collection.get(i).img, 110, 130, true));
        }
        
        return images;
    }

    public static BufferedImage getOriginalImage() throws Exception {
        return original_image;
    }
    
    public static BufferedImage getProcessedImage() throws Exception {
        return processed_image;
    }
    


    public static void load_fabrics_cb(int dirIndex) throws Exception {
        load_fabric_collection(collections_dir.get(dirIndex));

        sortFabrics(fabric_collection);
//        MainController.getMainParent().updateFabrics(getFabricsColl());
        
        selectedFabricCollIndex = dirIndex;
    }

    

    public static Object[] getProcessedData() throws Exception {
        return new Object[] {
            pattern_indices,
            CommunicationLocal.getImageFromLocal(PATHS.processed_print_file()),
            indicesCorresp,
            order_labels,
            (afterRegeneratePeriod ?
                fabrics_for_regeneration :
                (processed_user_fabric ?
                    processed_fabrics_refined :
                    (processed_fabric_collection ?
                        processed_collection_refined :
                        null))),
            color_indices_refined,
            pattern_ext,
            processed_cfqp,
            processed_qbnpa,
            processed_grid_angle
        };
    }

    public static boolean getUseCfqp() throws Exception {
        return processed_cfqp;
    }
    
    public static ArrayList<RGB8> getPalette() throws Exception {
//        if (afterRegeneratePeriod) {
            return palette;
//        }
//        
//        ArrayList<RGB8> tmp;
//        
//        tmp = new ArrayList<RGB8>();
//        for (int i=0; i<fabrics_for_regeneration.size(); i++) {
//            tmp.add(fabrics_for_regeneration.get(i).color);
//        }
//        
//        return tmp;
    }

    public static ArrayList<RGB8> getPaletteRefined() throws Exception {
//        if (afterRegeneratePeriod) {
            return palette_refined;
//        }
//        
//        ArrayList<RGB8> tmp;
//        
//        tmp = new ArrayList<RGB8>();
//        for (int i=0; i<fabrics_for_regeneration.size(); i++) {
//            tmp.add(fabrics_for_regeneration.get(i).color);
//        }
//        
//        return tmp;
    }

    public static short[] getIndicesCorresp() throws Exception {
        return indicesCorresp;
    }
    
    public static double[] getFabricAreas() throws Exception {
        return fabric_areas;
    }
    
    public static ArrayList<Fabric> getFabricsArrayRaw() throws Exception {
        return (afterRegeneratePeriod ? fabrics_for_regeneration : user_fabrics);
    }
    
    public static ArrayList<Fabric> getFabricsArray() throws Exception {
        return (afterRegeneratePeriod ?
                    fabrics_for_regeneration :
                    (processed_user_fabric ?
                        processed_fabrics :
                        (processed_fabric_collection ?
                            processed_collection :
                            null))
                )
        ;
    }

    public static void updatePrintExtendedPattern(
            int size_x, int size_y)
            throws Exception
    {
        short[] pat;
        short[] edg;
        int[][] pattern_tmp;
        int pw_ext, ph_ext;
        int pw_ext2, ph_ext2;
        ArrayList<Integer> markers1, markers2;
        int idx;

        
        pat = Patterns.patterns_ext[pattern_index].p;
        edg = Patterns.patterns_ext[pattern_index].e;
        pw_ext = Patterns.patterns_ext[pattern_index].w;
        ph_ext = Patterns.patterns_ext[pattern_index].h;
        pattern_ext = new int[pw_ext][ph_ext];
        for (int j=0; j<ph_ext; j++) {
            for (int i=0; i<pw_ext; i++) {
                pattern_ext[i][j] = pat[j*pw_ext + i];
            }
        }
        
        
        pw_ext2 = size_x;
        ph_ext2 = size_y;
        markers1 = new ArrayList<Integer>();
        for (int i=0; i<pw_ext-1; i++) {
            if (edg[i] == 0) {
                idx = pattern_ext[i][ph_ext-1];
                if (!markers1.contains(idx)) {
                    markers1.add(idx);
                }
            }
        }
        markers2 = new ArrayList<Integer>();
        for (int j=0; j<ph_ext-1; j++) {
            if (edg[j+pw_ext] == 0) {
                idx = pattern_ext[pw_ext-1][ph_ext-2-j];
                if (!markers2.contains(idx)) {
                    markers2.add(idx);
                }
            }
        }
        
//        Logger.printOut("Markers: ");
//        for (int i=0; i<markers1.size(); i++) {
//            Logger.printOut("%d ", markers1.get(i));
//        }
//        Logger.printOut(" -- ");
//        for (int i=0; i<markers2.size(); i++) {
//            Logger.printOut("%d ", markers2.get(i));
//        }
//        Logger.printOut("\n");
        
        pattern_tmp = Generals.resizeMatrix5(
                            pattern_ext, pw_ext, ph_ext, pw_ext2, ph_ext2);
        pattern_ext = Generals.resizeMatrix4(
                            pattern_ext, pw_ext, ph_ext, pw_ext2, ph_ext2);
        
        for (int i=0; i<pw_ext2-1; i++) {
            if (markers1.contains(pattern_tmp[i][ph_ext2-1])) {
                pattern_ext[i][ph_ext2-1] = 1;
            }
            else {
                pattern_ext[i][ph_ext2-1] = 0;
            }
        }
        for (int j=0; j<ph_ext2-1; j++) {
            if (markers2.contains(pattern_tmp[pw_ext2-1][j])) {
                pattern_ext[pw_ext2-1][j] = 1;
            }
            else {
                pattern_ext[pw_ext2-1][j] = 0;
            }
        }
    }
    
    public static String getPhotoFilename() throws Exception {
        return PATHS.getShortFilename(filename, 30);
    }

    public static double[] getDesignSize(double unitSize) throws Exception {
        double x_size = processed_detail*unitSize;
        double y_size = x_size*processed_h/processed_w;
        
        return new double[] { x_size, y_size };
    }

    public static double[] getPrintInfo(
            double cellScale, double page_w, double page_h)
            throws Exception
    {
        int num_x_pages, num_y_pages;
        double design_w, design_h;
        int print_w, print_h;
        int size_x, size_y;
        
        double aspect;
        int x_tiles, y_tiles;
        int max_tiles_on_page_x, max_tiles_on_page_y;
        int idxCrazy;

        
        aspect = (double)processed_image.getWidth()
                        / (double)processed_image.getHeight();
        
        design_w = processed_detail * cellScale;
        design_h = processed_detail / aspect * cellScale;
        
        x_tiles = processed_num_tiles[0];
        y_tiles = processed_num_tiles[1];
        
        size_x = (int)(design_w/x_tiles + 0.5);
        size_y = (int)(design_h/y_tiles + 0.5);

        max_tiles_on_page_x = (int)(page_w / size_x);
        max_tiles_on_page_y = (int)(page_h / size_y);

        
        idxCrazy = printController.getCrazySpecialPrintingIndex() + 1;
                
        if (processed_cfqp && idxCrazy>0) {
            print_w = (int)page_w;
            print_h = (int)page_h;
        }
        else {
            print_w = max_tiles_on_page_x * size_x;
            print_h = max_tiles_on_page_y * size_y;
        }

//        Logger.printOut("%d %d, %d %d, %d %d\n",
//            x_tiles, y_tiles,
//            max_tiles_on_page_x, max_tiles_on_page_y,
//            size_x, size_y);


        if (processed_cfqp && idxCrazy>0) {
            int fact, fact2;
            
            if (idxCrazy == 4) {
                fact = (int)Math.ceil(x_tiles / 2);
                if (fact*2 < x_tiles) {
                    fact++;
                }
                fact2 = (int)Math.ceil(y_tiles / 2);
                if (fact2*2 < y_tiles) {
                    fact2++;
                }
                
                num_x_pages = fact;
                num_y_pages = fact2;
            }
            else if (idxCrazy == 6) {
                if (page_w < page_h) {
                    fact = (int)Math.ceil(x_tiles / 2);
                    if (fact*2 < x_tiles) {
                        fact++;
                    }
                    fact2 = (int)Math.ceil(y_tiles / 3);
                    if (fact2*3 < y_tiles) {
                        fact2++;
                    }

                    num_x_pages = fact;
                    num_y_pages = fact2;
                }
                else {
                    fact = (int)Math.ceil(x_tiles / 3);
                    if (fact*3 < x_tiles) {
                        fact++;
                    }
                    fact2 = (int)Math.ceil(y_tiles / 2);
                    if (fact2*2 < y_tiles) {
                        fact2++;
                    }

                    num_x_pages = fact;
                    num_y_pages = fact2;
                }
            }
            else {
                if (page_w < page_h) {
                    fact = (int)Math.ceil(y_tiles / idxCrazy);
                    if (fact*idxCrazy < y_tiles) {
                        fact++;
                    }
                            
                    num_x_pages = x_tiles;
                    num_y_pages = fact;
                }
                else {
                    fact = (int)Math.ceil(x_tiles / idxCrazy);
                    if (fact*idxCrazy < x_tiles) {
                        fact++;
                    }
                            
                    num_x_pages = fact;
                    num_y_pages = y_tiles;
                }
            }
        }
        else {
            num_x_pages = x_tiles / max_tiles_on_page_x;
            num_y_pages = y_tiles / max_tiles_on_page_y;
            
            if (num_x_pages < (double)x_tiles / max_tiles_on_page_x) {
                num_x_pages++;
            }
            if (num_y_pages < (double)y_tiles / max_tiles_on_page_y) {
                num_y_pages++;
            }
        }

        
//        Logger.printOut("%f %f --> %d %d %f %f %d %d\n",
//            page_w,
//            page_h,
//            num_x_pages,
//            num_y_pages,
//            design_w,
//            design_h,
//            print_w,
//            print_h);
        
        return new double[] {
            num_x_pages,
            num_y_pages,
            design_w,
            design_h,
            print_w,
            print_h,
            size_x,
            size_y,
            size_x*x_tiles,
            size_y*y_tiles
        };
    }

    public static double[] getPrintInfo0(
            double unit_size, double page_w, double page_h)
            throws Exception
    {
        int num_x_pages, num_y_pages;

        double dpi = PrintController.FL_INCH;
        double aspect = (double)processed_image.getWidth()
                        / (double)processed_image.getHeight();
        double design_w;
        double design_h;
        int x_tiles;
        double x_unit, y_unit;
        
        design_w = processed_detail * unit_size * dpi;
        design_h = processed_detail / aspect * unit_size * dpi;
        
        x_tiles = Math.max((int)(processed_detail
                                / Patterns.patterns[processed_pattern].shapes_divisor),
                            1);

        x_unit = processed_detail*unit_size / x_tiles;
        y_unit = x_unit;

        final double side_margin = 0.625*dpi;
        final double top_margin = 0.625*dpi;
        final double bottom_margin = 0.625*dpi;

        double print_w = page_w - 2*side_margin/dpi;
        double print_h = page_h - (top_margin + bottom_margin)/dpi;

        if (!(processed_qbnpa || processed_cfqp)
                && (unit_size < print_w)
                && (unit_size < print_h)) {
            print_w = (int)(print_w/x_unit)*x_unit;
            print_h = (int)(print_h/y_unit)*y_unit;
        }

        num_x_pages = (int)(Math.ceil(design_w/print_w));
        num_y_pages = (int)(Math.ceil(design_h/print_h));

//        Logger.printOut("%d %d %f %f %f %f\n",
//            num_x_pages,
//            num_y_pages,
//            design_w,
//            design_h,
//            print_w,
//            print_h);
        
        return new double[] {
            num_x_pages,
            num_y_pages,
            design_w,
            design_h,
            print_w,
            print_h
        };
    }
    
    public static double[] getTotalNumPages(
            double unit_size, double page_w, double page_h)
            throws Exception
    {
        double[] info;
        int idxCrazy;
        int fact, fact2;

        idxCrazy = printController.getCrazySpecialPrintingIndex() + 1;
                
        if (processed_cfqp && idxCrazy>0) {
            int x_tiles, y_tiles;
            
            x_tiles = processed_num_tiles[0];
            y_tiles = processed_num_tiles[1];

            if (idxCrazy == 4) {
                fact = (int)Math.ceil(x_tiles / 2);
                if (fact*2 < x_tiles) {
                    fact++;
                }
                fact2 = (int)Math.ceil(y_tiles / 2);
                if (fact2*2 < y_tiles) {
                    fact2++;
                }
                
                info = new double[] {
                    fact,
                    fact2
                };
            }
            else {
                if (page_w < page_h) {
                    fact = (int)Math.ceil(y_tiles / idxCrazy);
                    if (fact*idxCrazy < y_tiles) {
                        fact++;
                    }
                            
                    info = new double[] {
                        x_tiles,
                        fact
                    };
                }
                else {
                    fact = (int)Math.ceil(x_tiles / idxCrazy);
                    if (fact*idxCrazy < x_tiles) {
                        fact++;
                    }
                            
                    info = new double[] {
                        fact,
                        y_tiles
                    };
                }
            }
        }
        else {
            info = getPrintInfo0(unit_size, page_w, page_h);
        }
        
        return new double[] { info[0], info[1] };
    }

    public static void print() throws Exception {
        if (CNT.version == CNT.VERSION_TYPE.DEMO) {
            Messenger.show(MSG.RESTRICTION_DEMO_PRINT);
            return;
        }

        if (processed_user_fabric) {
            Messenger.show(MSG.WORK_RESTRICTED_PRINTING);
            return;
        }
        
        if (new PreviewDialog().display()) {
            printController.print();
        }
    }
    
    public static void page_setup() throws Exception {
        if (CNT.version == CNT.VERSION_TYPE.DEMO) {
            Messenger.show(MSG.RESTRICTION_DEMO_PRINT);
            return;
        }

        if (processed_user_fabric) {
            Messenger.show(MSG.WORK_RESTRICTED_PRINTING);
            return;
        }

        printController.open();
    }

    public static int loadPreview() throws Exception {
        return printController.loadPreview();
    }

    public static void setPrintToJpg() throws Exception {
        FileSelector fs;
        Object[] data;
        String fname;
        int returnState;

        fs = new FileSelector(SELECTOR_TYPE.EXPORT_TO_JPG);
        data = fs.display(SELECTOR_TYPE.EXPORT_TO_JPG, MainController.getMainParent());
        fname = (String)data[0];
        returnState = (Integer)data[1];

        switch (returnState) {
            case JFileChooser.APPROVE_OPTION:
                break;

            case JFileChooser.CANCEL_OPTION:
                return;

            case JFileChooser.ERROR_OPTION:
                Messenger.show(MSG.FILE_OPEN_FAILED);
                return;

            default: throw Exceptions.badSwitchBranch(returnState);
        }


        if (!fname.contains(FORMAT_COMMUNICATION.EXT_POINT)) {
            fname += FORMAT_COMMUNICATION.EXT_POINT + FORMAT_COMMUNICATION.EXT_JPG;
        }
        DS.setTmpFileName(fname);
        
        if (new File(fname).exists()) {
            if (!Messenger.agree(MSG.WORK_CHECK_SAVE_OVERWRITE)) {
                return;
            }
        }

        BufferedImage img;
        Graphics2D g2d;
        int w, h;
        
        w = processed_image.getWidth();
        h = processed_image.getHeight();
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        g2d.drawImage(processed_image, 0, 0, null);
        
        ImageIO.write(
                img,
                PATHS.getExtension(fname),
                new File(fname));
    }



    
    
}
