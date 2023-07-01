package qp.control;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import qp.CNT.FORMAT;
import qp.CNT.PATHS;
import qp.DS;
import qp.Logger;
import qp.control.types.Fabric;
import qp.control.types.RGB8;
import qp.database.CommunicationLocal;
import qp.design.Generals;
import qp.design.Messenger;
import qp.design.Messenger.MSG;
import qp.design.components.Printer;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;
import qp.design.dialogs.PageSetupDialog;
import qp.design.dialogs.PrintProgressDialog;


/**
 *
 * @author Maira57
 */
public class PrintController {

    public static final int FL_INCH = 72;
    protected static int margins = FL_INCH/4;
    private static final int heightHeader = 20 + 5;



    private PageSetupDialog printWindow;
    
    protected static PrintProgressDialog printProgressDialog;

    protected Printer printer;
    
    protected String attributesString;
    private double unitSize;
    private boolean allSheets;
    private int firstSheet, lastSheet;
    private boolean rowColOrder;
    private boolean withFilling;
    private boolean conservePaper;
    private int crazySpecialPrintingIndex;
    
    private int num_x_pages, num_y_pages;
    
    private int noFirsts;
    private int fabricsSummaryStart;

    private boolean pageSizeUpdateEnabled;



    public PrintController() throws Exception {
        printProgressDialog = new PrintProgressDialog();
    }

    public void start() throws Exception {
        pageSizeUpdateEnabled = true;
        
        unitSize = 1.0;
        allSheets = true;
        firstSheet = 1;
        lastSheet = 1;
        rowColOrder = true;
        withFilling = false;
        conservePaper = false;
        crazySpecialPrintingIndex = -1;

        printer = new Printer(this);

        printWindow = new PageSetupDialog(this);
        
        printer.init();
        setParams(DS.PRINT_CONTROLLER.initParamsPattern);
    }



    public boolean open() {
        try {

        System.gc();

        if (printer.getServicesCount() == 0) {
            Messenger.show(MSG.PRINT_NONE_INSTALLED);
            return false;
        }

        noFirsts = 1;
        
        String fileName;
        
        fileName = QuiltedPhoto.getPhotoFilename();

        printer.setJobName(
            fileName.isEmpty() ? DS.PRINT_CONTROLLER.defaultJobName : fileName);
        updatePrintSettings();

        attributesString = getParams();

        printWindow.setLocationRelativeTo(MainController.getMainParent());
        printWindow.display();

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public void print() throws Exception {
        new Thread() {
            public @Override void run() {
                try {

                showPrintProgress(0.0);

                printer.doPrint();

                printWindow.setVisible(false);

                }
                catch (Exception e) { Logger.printErr(e); }

                printProgressDialog.setVisible(false);
            }
        }.start();

        if (printWindow.isVisible()) {
            printProgressDialog.setLocationRelativeTo(printWindow);
        }
        else {
            printProgressDialog.setLocationRelativeTo(MainController.getMainParent());
        }
        printProgressDialog.setVisible(true);
    }

    public int loadPreview() throws Exception {
        new Thread() {
            public @Override void run() {
                try {

                showPrintProgress(0.0);

//                CommunicationLocal.createDir(PATHS.dirTmp);
                
                String[] children = PATHS.dirTmp().list();
                for (int i=0; i<children.length; i++) {
                    if (PATHS.processed_print_file().equals(
                            PATHS.dirTmp() + "\\" + children[i]))
                    {
                        continue;
                    }
                    
                    new File(PATHS.dirTmp(), children[i]).delete();
                }
                
                updatePrintSettings();

                BufferedImage img, crop;
                Graphics2D g2d;
                int totalWidth, totalHeight;
                int page;
                Object[] result;
                
                for (int i=0; i<num_x_pages*num_y_pages; i++) {
                    totalWidth = getPageWidth() - 2*margins;
                    totalHeight = getPageHeight() - heightHeader - 2*margins;
                    page = i;
                    
                    img = new BufferedImage(
                                    getPageWidth(), getPageHeight(),
                                    BufferedImage.TYPE_INT_ARGB);
                    g2d = img.createGraphics();
                    
                    g2d.setColor(Colors.white);
                    g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

                    g2d.setFont(Fonts.label());

                    if (conservePaper) {
                        result = getPrintCrop0(totalWidth, totalHeight, page);
                    }
                    else {
                        result = getPrintCrop(totalWidth, totalHeight, page);
                    }

                    // print page info
                    drawPageInfo(g2d,
                                page + noFirsts,
                                (Integer)result[1], (Integer)result[2]);

                    // print image crop
                    crop = (BufferedImage) result[0];
                    g2d.drawImage(crop,
                                    totalWidth/2 - crop.getWidth()/2 + margins,
                                    heightHeader + 10,
                                    null);
                    
                    CommunicationLocal.saveImage(PATHS.getPreviewImageName(i), img);
                }

                printWindow.setVisible(false);

                }
                catch (Exception e) {
                    Logger.printErr(e);
                }
                finally {
                    printProgressDialog.setVisible(false);
                }
            }
        }.start();

        if (printWindow.isVisible()) {
            printProgressDialog.setLocationRelativeTo(printWindow);
        }
        else {
            printProgressDialog.setLocationRelativeTo(MainController.getMainParent());
        }
        printProgressDialog.setVisible(true);
        
        return PATHS.dirTmp().list().length;
    }
    
    /**
     * Cancel all changes made for printing parameters.
     * @throws Exception
     */
    public void cancel() throws Exception {
        setParams(attributesString);
    }

    public static void showPrintProgress(double p) throws Exception {
        printProgressDialog.setValue((int)(p * 100));
    }



    public String[] getServicesNames() throws Exception {
        return printer.getServicesNames();
    }

    public String[] getPageSizes() throws Exception {
        return printer.getPageSizes();
    }

    public int getPageWidth() throws Exception {
        return printer.getPageWidth();
    }

    public int getPageHeight() throws Exception {
        return printer.getPageHeight();
    }

    public boolean isPageSizeUpdateEnabled() throws Exception {
        return pageSizeUpdateEnabled;
    }

    public void setPageSizeUpdateEnabled(boolean value) throws Exception {
        pageSizeUpdateEnabled = value;
    }

    public String getParams() throws Exception {
        String[] str;
        String s;

        s = new String();

        str = new String[] {
            Double.toString(getUnitSize()),
            Boolean.toString(getPrintAllSheets()),
            Integer.toString(getFirstSheet()),
            Integer.toString(getLastSheet()),
            Boolean.toString(getRowColOrder()),
            Boolean.toString(isPortraitOriented()),
            Integer.toString(getCopiesCount()),
            Boolean.toString(getCollate()),
            Integer.toString(getPageSizeIndex())
        };

        for (int i=0; i<str.length-1; i++) {
            s += str[i] + FORMAT.PARAMS_SEP;
        }
        s += str[str.length-1];

        return s;
    }

    public void setParams(String s) throws Exception {
        String[] params;
        int idx;

        params = s.split(FORMAT.PARAMS_SEP);
        idx = 0;

        setUnitSize(Double.parseDouble(params[idx++]));
        setPrintAllSheets(Boolean.parseBoolean(params[idx++]));
        setFirstSheet(Integer.parseInt(params[idx++]));
        setLastSheet(Integer.parseInt(params[idx++]));
        setRowColOrder(Boolean.parseBoolean(params[idx++]));
        setPortraitOriented(Boolean.parseBoolean(params[idx++]));
        setCopiesCount(Integer.parseInt(params[idx++]));
        setCollate(Boolean.parseBoolean(params[idx++]));
        setPageSizeIndex(Integer.parseInt(params[idx++]));
    }

    public static int getMargins() throws Exception {
        return margins;
    }
    


    public boolean printCore(
            Graphics2D g2d,
            int page,
            int pageWidth, int pageHeight)
    {
        try {

        int totalWidth, totalHeight;

        
        // put 'getPageHeight() - heightHeader - 2*margins' if
        // we have a footer
        totalWidth = getPageWidth() - 2*margins;
        totalHeight = getPageHeight() - heightHeader - margins;
        
        g2d.setFont(Fonts.label());

        
        if (page < noFirsts) {
            drawPageInfo(g2d,
                        page,
                        -1, -1);
            
            totalWidth = getPageWidth() - 2*margins;
            totalHeight = getPageHeight() - heightHeader - 2*margins;
        
            if (page == 0) {
                drawHeader(g2d, totalWidth, totalHeight);
            }
            else {
                drawFabricSummary(g2d, totalWidth, totalHeight);
            }
            
            return true;
        }
        else {
            page -= noFirsts;
        }

        if (page >= num_x_pages * num_y_pages) {
            return false;
        }

        Object[] result;
        BufferedImage img;
        if (conservePaper) {
            result = getPrintCrop0(totalWidth, totalHeight, page);
        }
        else {
            result = getPrintCrop(totalWidth, totalHeight, page);
        }
        
        // print page info
        drawPageInfo(g2d,
                    page + noFirsts,
                    (Integer)result[1], (Integer)result[2]);
        
        // print image crop
        img = (BufferedImage) result[0];
        g2d.drawImage(img,
                        totalWidth/2 - img.getWidth()/2 + margins,
                        heightHeader + 5,
                        null);


        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    private void drawPageInfo(
            Graphics2D g2d,
            int page,
            int row, int column)
            throws Exception
    {
        String str;
        String s;

        s = QuiltedPhoto.getPhotoFilename();
        
        if (page < noFirsts) {
            str = DS.PRINT_CONTROLLER.headerPatternInfo(s);
        }
        else {
            str = DS.PRINT_CONTROLLER.headerPatternContent(s, row, column);
        }
        
        g2d.setColor(Colors.black);
        g2d.drawString(str,
                        getPageWidth()/2 - g2d.getFontMetrics().stringWidth(str)/2,
                        heightHeader);
    }

    private void drawHeader(
            Graphics2D g2d,
            int total_w, int total_h)
            throws Exception
    {
        double design_w, design_h;
        int x, y;
        int imgSize;
        String[] ds;
        int idx;

        

        ds = DS.PRINT_CONTROLLER.patternGeneralInfo();
        idx = 0;
        
        imgSize = (int)(2.5*FL_INCH);
        
        g2d.drawImage(
            Generals.scaleImage(
                QuiltedPhoto.getProcessedImage(),
                imgSize,
                (int)(1.25*imgSize),
                true),
            margins, heightHeader + 40,
            null);
        
        g2d.setColor(Colors.blue_light);
        g2d.drawRect(margins - 5, heightHeader + 40 - 5,
                    imgSize + 2*5, (int)(1.25*imgSize) + 2*5);

        
        
        design_w = getDesignSize()[0];
        design_h = getDesignSize()[1];
        
        x = margins + imgSize + 20;
        y = heightHeader + 35 + g2d.getFontMetrics().getHeight();
        
        g2d.setColor(Colors.black);
        g2d.drawString(ds[idx++],
                        x, y);
        y += 20;
        
        g2d.drawString(String.format(
                            ds[idx++],
                            (int)(Math.ceil(design_w)),
                            (int)(Math.ceil(design_h))),
                        x, y);
        y += 30;
        
        g2d.setFont(g2d.getFont().deriveFont(15.0f));
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
        g2d.drawString(ds[idx++],
                        x, y);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        g2d.setFont(g2d.getFont().deriveFont(13.0f));
        y += 20;
        
        g2d.drawString(ds[idx++],
                        x, y);
        y += 15;
        g2d.drawString(ds[idx++],
                        x, y);
        y += 15;
        g2d.drawString(ds[idx++],
                        x, y);
        y += 15;
        g2d.drawString(ds[idx++],
                        x, y);
        y += 30;

        
        g2d.setFont(g2d.getFont().deriveFont(11.0f));

        for (int i=idx; i<ds.length; i++) {
            g2d.drawString(ds[i],
                            x, y);
            g2d.drawLine(x, y+2, x + 250, y+2);
            y += 15;
        }
        
        g2d.setFont(g2d.getFont().deriveFont(13.0f));
        

        
        double[] fabric_areas;
        int num_c;
        int fabrics_per_line;
        int fabric_data_w;
        int lines_header;
        short[] indicesCorresp;
        int index;

        ds = DS.PRINT_CONTROLLER.patternFabricsInfo();
        
        fabric_areas = QuiltedPhoto.getFabricAreas();
        fabric_data_w = 23;
        lines_header = 60;
        fabrics_per_line = (total_w - lines_header) / fabric_data_w;

        g2d.setFont(g2d.getFont().deriveFont(Font.ITALIC));
        
        indicesCorresp = QuiltedPhoto.getIndicesCorresp();
        num_c = indicesCorresp[0];
        for (int i=0; i<indicesCorresp.length; i++) {
            if (indicesCorresp[i] > num_c) {
                num_c = indicesCorresp[i];
            }
        }
        num_c++;
        
        for (int i=0; i<num_c; i++) {
            if (i%fabrics_per_line == 0) {
                x = margins;
                y += 27;

                
                g2d.setFont(g2d.getFont().deriveFont(9.0f));
                g2d.drawString(ds[0],
                                x,
                                y);

                g2d.drawString(ds[1],
                                x,
                                y + 12);
                g2d.setFont(g2d.getFont().deriveFont(7.0f));
                g2d.drawString(ds[3],
                                x + g2d.getFontMetrics().stringWidth(ds[2]),
                                y + 10);
                g2d.setFont(g2d.getFont().deriveFont(9.0f));

                
                g2d.drawLine(
                        x,
                        y-10,
                        x + lines_header + fabrics_per_line * fabric_data_w,
                        y-10);
                g2d.drawLine(
                        x,
                        y+16,
                        x + lines_header + fabrics_per_line * fabric_data_w,
                        y+16);
                
                x = margins + lines_header;
            }
            
            g2d.drawString(String.format(ds[4], i+1),
                            x + i%fabrics_per_line * fabric_data_w,
                            y);

            index = -1;
            for (int j=0; j<indicesCorresp.length; j++) {
                if (indicesCorresp[j] == i) {
                    index = j;
                    break;
                }
            }
            
            g2d.drawString(String.format(ds[4], (int)(Math.ceil(fabric_areas[index]))),
                            x + i%fabrics_per_line * fabric_data_w,
                            y + 12);
        }

        
        int fabrics_w, fabrics_h;
        int desiredMaxRows;
        int boxSize;
        int paletteNoColorsPerRow;
        int paletteNoRows;
        int noColors;
        
        noColors = num_c;
        
        fabrics_w = total_w;
        fabrics_h = total_h - (y + FL_INCH);
        
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        
        desiredMaxRows = 0;
        do {
            desiredMaxRows++;
            boxSize = Math.min(fabrics_h / desiredMaxRows, fabrics_w);
            paletteNoColorsPerRow = fabrics_w / boxSize;
            
            if (paletteNoColorsPerRow == 0) {
                paletteNoRows = -1;
            }
            else {
                paletteNoRows = noColors / paletteNoColorsPerRow;
                if (noColors % paletteNoColorsPerRow != 0) {
                    paletteNoRows += 1;
                }
            }
        }
        while (paletteNoRows > desiredMaxRows);
        
        if (boxSize > (int)(FL_INCH)) {
            boxSize = (int)(FL_INCH);
        }
        
        

        if (boxSize < 30) {
            int oldNoFirsts;
            
            fabricsSummaryStart = heightHeader + 40 - 40;
            oldNoFirsts = noFirsts;
            noFirsts = 2;
            setLastSheet(getLastSheet() - oldNoFirsts);
            
            g2d.setColor(Colors.black);
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
            g2d.setFont(g2d.getFont().deriveFont(11.0f));
            g2d.drawString(DS.PRINT_CONTROLLER.getFooter(),
                            margins, total_h + heightHeader + 20);
        }
        else {
            fabricsSummaryStart = y;
            drawFabricSummary(g2d, total_w, total_h);
        }
    }
    
    private void drawFabricSummary(
            Graphics2D g2d,
            int total_w, int total_h)
            throws Exception
    {
        ArrayList<RGB8> palette;
        int y;

        y = fabricsSummaryStart;
        y += 40;
        
        palette = QuiltedPhoto.getPaletteRefined();
        
        
        ArrayList<Fabric> fabrics;
        boolean useFabrics;
        int fabrics_w, fabrics_h;
        int desiredMaxRows;
        int boxSize;
        int paletteNoColorsPerRow;
        int paletteNoRows;
        int noColors;
        int c, r;
        int fontHeight;
        String label;
        Color c1;
        short[] indicesCorresp;
        int idx;
        
        fabrics = QuiltedPhoto.getFabricsArray();
        if (fabrics != null) {
            useFabrics = true;
        }
        else {
            useFabrics = false;
        }
        indicesCorresp = QuiltedPhoto.getIndicesCorresp();
        noColors = indicesCorresp[0];
        for (int i=0; i<indicesCorresp.length; i++) {
            if (indicesCorresp[i] > noColors) {
                noColors = indicesCorresp[i];
            }
        }
        noColors++;
        
        fabrics_w = total_w;
        fabrics_h = total_h - y;
        
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        g2d.setFont(g2d.getFont().deriveFont(13.0f));
        fontHeight = g2d.getFontMetrics().getHeight();
        g2d.translate(margins, y);
        
        desiredMaxRows = 0;
        do {
            desiredMaxRows++;
            boxSize = Math.min(fabrics_h / desiredMaxRows, fabrics_w);
            paletteNoColorsPerRow = fabrics_w / boxSize;
            
            if (paletteNoColorsPerRow == 0) {
                paletteNoRows = -1;
            }
            else {
                paletteNoRows = noColors / paletteNoColorsPerRow;
                if (noColors % paletteNoColorsPerRow != 0) {
                    paletteNoRows += 1;
                }
            }
        }
        while (paletteNoRows > desiredMaxRows);
        
        if (boxSize > (int)(FL_INCH)) {
            boxSize = (int)(FL_INCH);
            paletteNoColorsPerRow = fabrics_w / boxSize;
        }

        c = 0;
        r = 0;
        
        for (int i=0; i<noColors; i++) {
            label = String.format(FORMAT.PALETTE_INDEX, i+1);
            
            idx = -1;
            for (int j=0; j<indicesCorresp.length; j++) {
                if (indicesCorresp[j] == i) {
                    idx = j;
                    break;
                }
            }
            
            if (useFabrics) {
                g2d.drawImage(
                    Generals.scaleImage(
                        fabrics.get(idx).img,
                        boxSize - 20,
                        boxSize - 20,
                        false),
                    c*boxSize + 10,
                    r*boxSize,
                    null);
                
                g2d.setColor(Colors.black);
                g2d.drawRect(c*boxSize,
                                r*boxSize,
                                boxSize,
                                boxSize);
                
                g2d.setColor(Colors.black);
                g2d.drawString(label,
                                c*boxSize
                                    + boxSize/2
                                    - g2d.getFontMetrics().stringWidth(label)/2,
                                r*boxSize - fontHeight/2 + boxSize);
            }
            else {
                c1 = new Color(palette.get(i).r,
                                palette.get(i).g,
                                palette.get(i).b);
                
                g2d.setColor(c1);
                g2d.fillRect(c*boxSize,
                                r*boxSize,
                                boxSize,
                                boxSize);
                
                if (c1.getRed() + c1.getGreen() + c1.getBlue() > 500) {
                    g2d.setColor(Colors.black);
                }
                else {
                    g2d.setColor(Colors.white);
                }
                
                g2d.drawString(label,
                                c*boxSize
                                    + boxSize/2
                                    - g2d.getFontMetrics().stringWidth(label)/2,
                                r*boxSize + fontHeight/2 + boxSize/2);
            }

            c++;
            if (c == paletteNoColorsPerRow) {
                c = 0;
                r++;
            }
        }
        

        g2d.translate(-margins, -y);
        
        g2d.setColor(Colors.black);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        g2d.setFont(g2d.getFont().deriveFont(11.0f));
        g2d.drawString(DS.PRINT_CONTROLLER.getFooter(),
                        margins, total_h + heightHeader + 20);
    }
    
    private Object[] getPrintCrop(
            double page_w, double page_h,
            int page)
            throws Exception
    {
        BufferedImage img;
        double design_w, design_h;
        double print_w, print_h;
        int size_x, size_y;
        int w, h;
        int ref;
        double[] info;

        info = QuiltedPhoto.getPrintInfo(unitSize * FL_INCH, page_w, page_h);

        design_w = info[2];
        design_h = info[3];
        print_w = info[4];
        print_h = info[5];
        size_x = (int)info[6];
        size_y = (int)info[7];
        
        int i, j;
        if (rowColOrder) {
            i = page / num_y_pages;
            j = page % num_y_pages;
        } else {
            i = page % num_x_pages;
            j = page / num_x_pages;
        }

        if (QuiltedPhoto.getUseCfqp() && crazySpecialPrintingIndex >= 0) {
            img = ProcessImage.generate_print_pattern_cfqp(
                        QuiltedPhoto.getProcessedData(),
                        crazySpecialPrintingIndex,
                        num_x_pages, num_y_pages,
                        print_w, print_h,
                        i, j);
        }
        else {
            double x0 = i*print_w/design_w;
            double x1 = (i<num_x_pages-1 ? (i+1)*print_w/design_w : 1.0);
            double y0 = j*print_h/design_h;
            double y1 = (j<num_y_pages-1 ? (j+1)*print_h/design_h : 1.0);
            
            w = (int)(i<num_x_pages-1 ? (print_w) : ((x1-x0)*design_w));
            h = (int)(j<num_y_pages-1 ? (print_h) : ((y1-y0)*design_h));

            ref = (int)((double)w/size_x + 0.5);
            w = ref * size_x;
            ref = (int)((double)h/size_y + 0.5);
            h = ref * size_y;

            img = ProcessImage.generate_print_pattern(
                        QuiltedPhoto.getProcessedData(),
                        x0, x1, y0, y1,
                        w, h,
                        (int)(i*print_w), (int)(j*print_h),
                        (int)info[8], (int)info[9],
                        FL_INCH * unitSize,
                        withFilling);
        }

        showPrintProgress((double)(page+1)/(num_x_pages*num_y_pages));


        return new Object[] {
            img,
            j+1,
            i+1
        };
    }
    
    private Object[] getPrintCrop0(
            double page_w, double page_h,
            int page)
            throws Exception
    {
        BufferedImage img;
        double design_w, design_h;
        double print_w, print_h;
        double[] info;
        
        info = QuiltedPhoto.getPrintInfo0(unitSize, page_w, page_h);

        design_w = info[2];
        design_h = info[3];
        print_w = info[4];
        print_h = info[5];
        
        int i, j;
        if (rowColOrder) {
            i = page / num_y_pages;
            j = page % num_y_pages;
        } else {
            i = page % num_x_pages;
            j = page / num_x_pages;
        }

        if (crazySpecialPrintingIndex >= 0) {
            img = ProcessImage.generate_print_pattern_cfqp(
                        QuiltedPhoto.getProcessedData(),
                        crazySpecialPrintingIndex,
                        num_x_pages, num_y_pages,
                        print_w, print_h,
                        i, j);
        }
        else {
            double x0 = i*print_w/design_w;
            double x1 = (i<num_x_pages-1 ? (i+1)*print_w/design_w : 1.0);
            double y0 = j*print_h/design_h;
            double y1 = (j<num_y_pages-1 ? (j+1)*print_h/design_h : 1.0);

            img = ProcessImage.generate_print_pattern0(
                        QuiltedPhoto.getProcessedData(),
                        x0, x1, y0, y1,
                        ((i<num_x_pages-1 ? (int)(print_w) : (int)((x1-x0)*design_w))),
                        ((j<num_y_pages-1 ? (int)(print_h) : (int)((y1-y0)*design_h))),
                        FL_INCH * unitSize,
                        withFilling);
        }

        showPrintProgress((double)(page+1)/(num_x_pages*num_y_pages));


        return new Object[] {
            img,
            j+1,
            i+1
        };
    }



    public double[] getDesignSize() throws Exception {
        return QuiltedPhoto.getDesignSize(unitSize);
    }
    
    public void updatePrintSettings() throws Exception {
        double[] values;
        
        // put 'getPageHeight() - heightHeader - 2*margins' if
        // we have a footer

//        JOptionPane.showMessageDialog(
//            null, "Page size: " + getPageWidth() + " x " + getPageHeight());
        if (conservePaper) {
            values = QuiltedPhoto.getTotalNumPages(
                    unitSize,
                    getPageWidth() - 2*margins,
                    getPageHeight() - heightHeader - margins);
        }
        else {
            values = QuiltedPhoto.getPrintInfo(
                        unitSize * FL_INCH,
                        getPageWidth() - 2*margins,
                        getPageHeight() - heightHeader - margins);
        
            QuiltedPhoto.updatePrintExtendedPattern((int)values[6], (int)values[7]);
        }
        
        num_x_pages = (int)values[0];
        num_y_pages = (int)values[1];
    }
    
    public int getNumSheets() throws Exception {
        return num_x_pages * num_y_pages;
    }

    public int getNumSheetsTotal() throws Exception {
        int num_total_sheets;
        
        if (allSheets) {
            num_total_sheets = num_x_pages * num_y_pages * getCopiesCount();
        }
        else {
            num_total_sheets = (lastSheet-noFirsts - firstSheet + 1) * getCopiesCount();
        }
        
        return num_total_sheets;
    }

    public boolean containsPageSize(String name) throws Exception {
        return printer.containsPageSize(name);
    }
    
    
    
    public void setServiceIndex(int index) throws Exception {
        printer.setServiceIndex(index);
    }

    public int getServiceIndex() throws Exception {
        return printer.getServiceIndex();
    }

    public void setUnitSize(double value) throws Exception {
        unitSize = value;
    }

    public double getUnitSize() throws Exception {
        return unitSize;
    }

    public void setPrintAllSheets(boolean value) throws Exception {
        allSheets = value;
        printer.setPrintRange(value, firstSheet, lastSheet);
    }

    public boolean getPrintAllSheets() throws Exception {
        allSheets = printer.getPrintRangeAll();
        return allSheets;
    }

    public void setFirstSheet(int value) throws Exception {
        firstSheet = value;
        printer.setPrintRange(allSheets, firstSheet, lastSheet);
    }

    public int getFirstSheet() throws Exception {
        firstSheet = printer.getPrintRangeInterval()[0];
        return firstSheet;
    }

    public void setLastSheet(int value) throws Exception {
        lastSheet = value + noFirsts;
        printer.setPrintRange(allSheets, firstSheet, lastSheet);
    }

    public int getLastSheet() throws Exception {
        lastSheet = printer.getPrintRangeInterval()[1];
        return lastSheet;
    }

    public void setRowColOrder(boolean value) throws Exception {
        rowColOrder = value;
    }

    public boolean getRowColOrder() throws Exception {
        return rowColOrder;
    }

    public void setPortraitOriented(boolean value) throws Exception {
        printer.setPortraitOriented(value);
    }

    public boolean isPortraitOriented() throws Exception {
        return printer.isPortraitOriented();
    }

    public void setCopiesCount(int value) throws Exception {
        printer.setCopiesCount(value);
    }

    public int getCopiesCount() throws Exception {
        return printer.getCopiesCount();
    }

    public void setCollate(boolean value) throws Exception {
        printer.setCollate(value);
    }

    public boolean getCollate() throws Exception {
        return printer.getCollate();
    }

    public void setPageSizeIndex(int index) throws Exception {
        if (!pageSizeUpdateEnabled) {
            return;
        }

        printer.setPageSizeIndex(index);
    }

    public int getPageSizeIndex() throws Exception {
        return printer.getPageSizeIndex();
    }

    public void setWithFilling(boolean value) throws Exception {
        withFilling = value;
    }

    public boolean getWithFilling() throws Exception {
        return withFilling;
    }

    public void setConservePaper(boolean value) throws Exception {
        conservePaper = value;
    }

    public boolean getConservePaper() throws Exception {
        return conservePaper;
    }

    public void setCrazySpecialPrintingIndex(int value) throws Exception {
        crazySpecialPrintingIndex = value;
    }

    public int getCrazySpecialPrintingIndex() throws Exception {
        return crazySpecialPrintingIndex;
    }
    
    



}
