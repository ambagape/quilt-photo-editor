package qp.control;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import qp.CNT.FORMAT;
import qp.CNT.PATHS;
import qp.DS;
import qp.Logger;
import qp.control.types.Fabric;
import qp.control.types.RGB8;
import qp.design.Generals;
import qp.design.Messenger;
import qp.design.Messenger.MSG;
import qp.design.components.Printer;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;
import qp.design.dialogs.FabricPageSetupDialog;


/**
 *
 * @author Maira57
 */
public class FabricPrintController extends PrintController {
    
    private static final int heightHeaderFabric = 20 + 5;



    private FabricPageSetupDialog printWindow;
    
    private double unitSize;
    private boolean putSpaces;
    
    private int fabricIndex;
    
    private int num_x_pages, num_y_pages;



    public FabricPrintController() throws Exception {
        super();
    }

    public @Override void start() throws Exception {
        unitSize = 1.0;
        putSpaces = false;

        printer = new Printer(this);

        printWindow = new FabricPageSetupDialog(this);
        
        printer.init();
        setParams(DS.PRINT_CONTROLLER.initParamsFabrics);
    }



    public boolean open(int index) {
        try {

        System.gc();

        if (printer.getServicesCount() == 0) {
            Messenger.show(MSG.PRINT_NONE_INSTALLED);
            return false;
        }
        
        if (index >= 0) {
            short[] indicesCorresp;
            int idx;

            indicesCorresp = QuiltedPhoto.getIndicesCorresp();
            idx = -1;
            for (int j=0; j<indicesCorresp.length; j++) {
                if (indicesCorresp[j] == index) {
                    idx = j;
                    break;
                }
            }
            fabricIndex = idx;
        }
        else {
            fabricIndex = index;
        }
        
        String fileName;
        
        fileName = QuiltedPhoto.getPhotoFilename();
        
        printer.setJobName(
            fileName.isEmpty() ? DS.PRINT_CONTROLLER.defaultJobName : fileName);
        updatePrintSettings();

        attributesString = getParams();

        if (index >= 0) {
            printWindow.setLocationRelativeTo(MainController.getMainParent());
            printWindow.display();
        }
        else {
            print();
        }

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public @Override void print() throws Exception {
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

    public @Override String getParams() throws Exception {
        String[] str;
        String s;

        s = new String();

        str = new String[] {
            Double.toString(getUnitSize()),
            Integer.toString(getPageSizeIndex()),
            Boolean.toString(getPutSpaces()),
        };

        for (int i=0; i<str.length-1; i++) {
            s += str[i] + FORMAT.PARAMS_SEP;
        }
        s += str[str.length-1];

        return s;
    }

    public @Override void setParams(String s) throws Exception {
        String[] params;
        int idx;

        params = s.split(FORMAT.PARAMS_SEP);
        idx = 0;

        setUnitSize(Double.parseDouble(params[idx++]));
        setPageSizeIndex(Integer.parseInt(params[idx++]));
        setPutSpaces(Boolean.parseBoolean(params[idx++]));
    }



    public @Override boolean printCore(
            Graphics2D g2d,
            int page,
            int pageWidth, int pageHeight)
    {
        try {

        int totalWidth, totalHeight;


        if (page >= 1) {
            return false;
        }

//        // if printing only one fabric, then margins are smaller.
//        if (fabricIndex >= 0) {
//            margins /= 2;
//        }
        
        totalWidth = getPageWidth() - 2*margins;
        totalHeight = getPageHeight() - heightHeaderFabric - 2*margins;
        
        g2d.setFont(Fonts.label());

        
        // print page info
        drawPageInfo(g2d);


        if (fabricIndex >= 0) {
            drawFabricSingle(
                    g2d,
                    totalWidth,
                    totalHeight);
        }
        else {
            drawFabricAll(g2d, totalWidth, totalHeight);
        }

        g2d.setColor(Colors.black);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        g2d.setFont(g2d.getFont().deriveFont(11.0f));
        g2d.drawString(DS.PRINT_CONTROLLER.getFooter(),
                        margins, totalHeight + heightHeaderFabric + 20);

//        // if printing only one fabric, then margins were smaller.
//        // Revert to original value
//        if (fabricIndex >= 0) {
//            margins *= 2;
//        }
        

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    private void drawPageInfo(
            Graphics2D g2d)
            throws Exception
    {
        String str;
        
        if (fabricIndex >= 0) {
            str = DS.PRINT_CONTROLLER.headerFabricSingle(
                    QuiltedPhoto.getIndicesCorresp()[fabricIndex]+1);
        }
        else {
            str = String.format(
                        DS.PRINT_CONTROLLER.headerFabricAll());
        }
        
        g2d.setColor(Colors.black);
        g2d.drawString(str,
                        getPageWidth()/2 - g2d.getFontMetrics().stringWidth(str)/2,
                        heightHeaderFabric);
    }

    private void drawFabricSingle(
            Graphics2D g2d,
            int total_w, int total_h)
            throws Exception
    {
        BufferedImage img;
        int nx, ny;
        int w, h;
        int sz;
        int sp;
        
        sz = (int)(unitSize * FL_INCH);
        
        if (QuiltedPhoto.getFabricsArray() != null) {
            Graphics2D g3;
            BufferedImage img2;
            
            img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
            img2 = QuiltedPhoto.getFabricsArray().get(fabricIndex).img;
            
            g3 = img.createGraphics();

            w = img2.getWidth();
            h = img2.getHeight();
            
            nx = total_w / w;
            ny = total_h / h;
        
            for (int i=0; i<nx; i++) {
                for (int j=0; j<ny; j++) {
                    g3.drawImage(img2, i*w, j*h, null);
                }
            }

            if (nx*w < total_w) {
                for (int j=0; j<ny; j++) {
                    g3.drawImage(img2, nx*w, j*h, total_w, (j+1)*h, 0, 0, total_w-nx*w, h, null);
                }
            }

            if (ny*h < total_h) {
                for (int i=0; i<nx; i++) {
                    g3.drawImage(img2, i*w, ny*h, (i+1)*w, total_h, 0, 0, w, total_h-ny*h, null);
                }
            }

            if (nx*w < total_w && ny*h < total_h) {
                g3.drawImage(img2, nx*w, ny*h, total_w, total_h, 0, 0, total_w-nx*w, total_h-ny*h, null);
            }
            
        }
        else {
            img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g3;
            RGB8 item;
            
            g3 = img.createGraphics();
            item = QuiltedPhoto.getPalette().get(fabricIndex);

            g3.setColor(new Color(item.r, item.g, item.b));
            g3.fillRect(0, 0, sz, sz);
        }
        
        w = img.getWidth();
        h = img.getHeight();

        if (putSpaces) {
            sp = 2;
        }
        else {
            sp = 0;
        }
        nx = total_w / (w+sp);
        ny = total_h / (h+sp);

        g2d.translate(margins, heightHeaderFabric + 5);
        
        for (int i=0; i<nx; i++) {
            for (int j=0; j<ny; j++) {
                g2d.drawImage(img, i*(w+sp), j*(h+sp), null);
            }
        }

        if (nx*(w+sp) < total_w) {
            for (int j=0; j<ny; j++) {
                g2d.drawImage(img, nx*(w+sp), j*(h+sp), total_w, (j+1)*(h+sp), 0, 0, total_w-nx*(w+sp), (h+sp), null);
            }
        }

        if (ny*(h+sp) < total_h) {
            for (int i=0; i<nx; i++) {
                g2d.drawImage(img, i*(w+sp), ny*(h+sp), (i+1)*(w+sp), total_h, 0, 0, (w+sp), total_h-ny*(h+sp), null);
            }
        }

        if (nx*(w+sp) < total_w && ny*(h+sp) < total_h) {
            g2d.drawImage(img, nx*(w+sp), ny*(h+sp), total_w, total_h, 0, 0, total_w-nx*(w+sp), total_h-ny*(h+sp), null);
        }

        g2d.translate(-margins, -(heightHeaderFabric + 5));
        
        
        
//        BufferedImage img;
//        int nx, ny;
//        int w, h;
//        int sz;
//        int sp;
//        
//        sz = (int)(unitSize * FL_INCH);
//
//        if (putSpaces) {
//            sp = 2;
//        }
//        else {
//            sp = 0;
//        }
//        
//        if (QuiltedPhoto.getFabricsArray() != null) {
//            Graphics2D g3;
//            BufferedImage img2;
//            
//            img2 = QuiltedPhoto.getFabricsArray().get(fabricIndex).img;
//            
//
//            w = img2.getWidth();
//            h = img2.getHeight();
//            
//            nx = (int)((double)total_w / w + 0.5);
//            ny = (int)((double)total_h / h + 0.5);
//            
//            img = new BufferedImage(total_w, total_h, BufferedImage.TYPE_INT_ARGB);
//            g3 = img.createGraphics();
//        
//            for (int i=0; i<nx; i++) {
//                for (int j=0; j<ny; j++) {
//                    g3.drawImage(img2, i*w, j*h, (i+1)*w, (j+1)*h, 0, 0, w, h, null);
//                }
//            }
//
//            if (nx*w < total_w) {
//                for (int j=0; j<ny; j++) {
//                    g3.drawImage(img2, nx*w, j*h, total_w, (j+1)*h, 0, 0, total_w-nx*w, h, null);
//                }
//            }
//
//            if (ny*h < total_h) {
//                for (int i=0; i<nx; i++) {
//                    g3.drawImage(img2, i*w, ny*h, (i+1)*w, total_h, 0, 0, w, total_h-ny*h, null);
//                }
//            }
//
//            if (nx*w < total_w && ny*h < total_h) {
//                g3.drawImage(img2, nx*w, ny*h, total_w, total_h, 0, 0, total_w-nx*w, total_h-ny*h, null);
//            }
//        }
//        else {
//            img = new BufferedImage(total_w, total_h, BufferedImage.TYPE_INT_ARGB);
//            
//            Graphics2D g3;
//            RGB8 item;
//            
//            g3 = img.createGraphics();
//            item = QuiltedPhoto.getPalette().get(fabricIndex);
//
//            g3.setColor(new Color(item.r, item.g, item.b));
//            g3.fillRect(0, 0, img.getWidth(), img.getHeight());
//        }
//        
////        w = img.getWidth();
////        h = img.getHeight();
//////        nx = total_w / (w+sp);
//////        ny = total_h / (h+sp);
//
//        g2d.translate(margins, heightHeaderFabric + 5);
//
//
//        int w2, h2;
//        
//        w = sz;
//        h = sz;
//        
//        w2 = w+sp;
//        h2 = h+sp;
//
//        nx = (int)((double)total_w / w2 + 0.5);
//        ny = (int)((double)total_h / h2 + 0.5);
//        System.out.printf("%d %d --- %d %d --- %d %d\n", w, h, w2, h2, nx, ny);
//
//        for (int i=0; i<nx; i++) {
//            for (int j=0; j<ny; j++) {
//                g2d.drawImage(img, i*w2, j*h2, (i+1)*w, (j+1)*h, 0, 0, w, h, null);
//            }
//        }
//
//        if (nx*w < total_w) {
//            for (int j=0; j<ny; j++) {
//                g2d.drawImage(img, nx*w2, j*h2, total_w, (j+1)*h, 0, 0, total_w-nx*w, h, null);
//            }
//        }
//
//        if (ny*h < total_h) {
//            for (int i=0; i<nx; i++) {
//                g2d.drawImage(img, i*w2, ny*h2, (i+1)*w, total_h, 0, 0, w, total_h-ny*h, null);
//            }
//        }
//
//        if (nx*w < total_w && ny*h < total_h) {
//            g2d.drawImage(img, nx*w2, ny*h2, total_w, total_h, 0, 0, total_w-nx*w, total_h-ny*h, null);
//        }
//        
//  
//        
//        
////        g2d.drawImage(img, 0, 0, null);
//
//        
//        
//        
////        for (int i=0; i<nx; i++) {
////            for (int j=0; j<ny; j++) {
////                g2d.drawImage(img, i*(w+sp), j*(h+sp), null);
////            }
////        }
////
////        if (nx*(w+sp) < total_w) {
////            for (int j=0; j<ny; j++) {
////                g2d.drawImage(img, nx*(w+sp), j*(h+sp), total_w, (j+1)*(h+sp), 0, 0, total_w-nx*(w+sp), (h+sp), null);
////            }
////        }
////
////        if (ny*(h+sp) < total_h) {
////            for (int i=0; i<nx; i++) {
////                g2d.drawImage(img, i*(w+sp), ny*(h+sp), (i+1)*(w+sp), total_h, 0, 0, (w+sp), total_h-ny*(h+sp), null);
////            }
////        }
////
////        if (nx*(w+sp) < total_w && ny*(h+sp) < total_h) {
////            g2d.drawImage(img, nx*(w+sp), ny*(h+sp), total_w, total_h, 0, 0, total_w-nx*(w+sp), total_h-ny*(h+sp), null);
////        }
//
//        g2d.translate(-margins, -(heightHeaderFabric + 5));
    }
    
    private void drawFabricAll(
            Graphics2D g2d,
            int total_w, int total_h)
            throws Exception
    {
        ArrayList<RGB8> palette;
        int y;

        y = heightHeaderFabric + 5;
        
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

        if (fabricIndex == -2) {
            fabrics = QuiltedPhoto.getFabricsArrayRaw();
            indicesCorresp = new short[fabrics.size()];
            for (int i=0; i<indicesCorresp.length; i++) {
                indicesCorresp[i] = (short)i;
            }
            noColors = fabrics.size();
        }
        else {
            fabrics = QuiltedPhoto.getFabricsArray();
            indicesCorresp = QuiltedPhoto.getIndicesCorresp();
            noColors = indicesCorresp[0];
            for (int i=0; i<indicesCorresp.length; i++) {
                if (indicesCorresp[i] > noColors) {
                    noColors = indicesCorresp[i];
                }
            }
            noColors++;
        }
        if (fabrics != null) {
            useFabrics = true;
        }
        else {
            useFabrics = false;
        }
        
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
                        true),
                    c*boxSize + 10,
                    r*boxSize,
                    null);

                if (fabricIndex == -1) {
                    g2d.setColor(Colors.black);
                    g2d.drawRect(c*boxSize,
                                    r*boxSize,
                                    boxSize,
                                    boxSize);
                }

                if (fabricIndex == -2) {
                    String s;
                    
                    s = PATHS.getShortFilename(fabrics.get(idx).fname, 10);
                    
                    label = String.format(FORMAT.PALETTE_INDEX_EXT, i+1, s);
                    g2d.setFont(g2d.getFont().deriveFont(9.0f));
                }
                
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
    }


    
    public @Override void updatePrintSettings() throws Exception {
        double[] values;

        if (fabricIndex == -2) {
            num_x_pages = 1;
            num_y_pages = 1;
        }
        else {
            values = QuiltedPhoto.getPrintInfo(
                        unitSize * FL_INCH,
                        getPageWidth() - 2*margins,
                        getPageHeight() - heightHeaderFabric - 2*margins);

            num_x_pages = (int)values[0];
            num_y_pages = (int)values[1];
        }
    }

    public @Override int getNumSheetsTotal() throws Exception {
        int num_total_sheets;
        
        num_total_sheets = num_x_pages * num_y_pages * getCopiesCount();
        
        return num_total_sheets;
    }

    
    
    public @Override void setUnitSize(double value) throws Exception {
        unitSize = value;
    }

    public @Override double getUnitSize() throws Exception {
        return unitSize;
    }

    public void setPutSpaces(boolean value) throws Exception {
        putSpaces = value;
    }

    public boolean getPutSpaces() throws Exception {
        return putSpaces;
    }
    
    



}
