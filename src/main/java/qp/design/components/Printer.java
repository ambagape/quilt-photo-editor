package qp.design.components;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Arrays;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import qp.control.PrintController;


/**
 *
 * @author Maira57
 */
public final class Printer implements Printable {
    
    public static final String DEFAULT_PAGE_SIZE = new String("Letter");
    
    

    // controls the printing (intermediate between interface and job)
    private PrintController printController;

    // printing job (does all the printing work)
    private PrinterJob job;

    // attributes (the list-variable and each one individually)
    private PrintRequestAttributeSet attributes;
    private String jobName;
    private int serviceIndex;
    private PrintTableMediaSizeName pageSizeName;
    private Attribute orientation;
    private int copiesCount;
    private Attribute collate;
    private Attribute chromaticity;
    private boolean printRangeAll;
    private int[] printRangeInterval;

    // helpful lists
    private PrintService[] services;
    private ArrayList<String> pageSizeNames;
    private ArrayList<String> prettyPageSizeNames;
    private ArrayList<String> orderedPrettyPageSizeNames;



    public Printer(PrintController printController) throws Exception {
        this.printController = printController;

        services = PrinterJob.lookupPrintServices();
        
        attributes = new HashPrintRequestAttributeSet();

        pageSizeName = new PrintTableMediaSizeName(0);

        job = PrinterJob.getPrinterJob();
        
        
        boolean verbousePrinting;
        
        verbousePrinting = false;
        

//        if (CNT.AS_FOR_RELEASE) {
//            if (verbousePrinting) {
//                JOptionPane.showMessageDialog(
//                    null, "Printers initially: " + services.length + ".");
//            }
//            ArrayList<PrintService> servicesArray;
//            servicesArray = new ArrayList<PrintService>();
//            
//            if (verbousePrinting) {
//                String s;
//                s = new String();
//                for (int i=0; i<services.length; i++) {
//                    s += "Printer " + i + ": \"" + services[i].getName() + "\".\n";
//                }
//                JOptionPane.showMessageDialog(
//                    null,
//                    s);
//            }
//            
//            for (int i=0; i<services.length; i++) {
//                try {
//                    
////                if (verbousePrinting) {
////                    JOptionPane.showMessageDialog(
////                        null, "Trying out printer: " + i + ".");
////                }
//                setServiceIndex(i);
//                if (!orderedPrettyPageSizeNames.isEmpty()) {
//                    servicesArray.add(services[i]);
//                }
//                
//                }
//                catch (Exception e) {
////                    Logger.printErr(e);
//                    
//                    if (verbousePrinting) {
//                        JOptionPane.showMessageDialog(
//                            null, "Bad printer: " + i + ".");
//                    }
//                }
//            }
//            services = new PrintService[servicesArray.size()];
//            for (int i=0; i<servicesArray.size(); i++) {
//                services[i] = servicesArray.get(i);
//            }
//            if (verbousePrinting) {
//                JOptionPane.showMessageDialog(
//                    null, "Printers finally: " + services.length + ".");
//                if (services.length == 0) {
//                    JOptionPane.showMessageDialog(
//                        null, "No printers found!!");
//                }
//                JOptionPane.showMessageDialog(
//                    null, "Found " + services.length + " printers.");
//            }
//        }
        
        
    }



    public void init() throws Exception {
//        CommunicationLocal.createDir(PATHS.dirTmp);
        
        job.setPrintable(this);

        setJobName(new String());
        
        PrintService serviceDef;
        serviceDef = PrintServiceLookup.lookupDefaultPrintService();
        for (int i=0; i<services.length; i++) {
            if (services[i].equals(serviceDef)) {
                setServiceIndex(i);
//                JOptionPane.showMessageDialog(null, "Chose default print service " + i + ".");
                break;
            }
        }
        setPageSizeIndex(1);
        setPortraitOriented(true);
        setCopiesCount(1);
        setCollate(false);
        setColored(true);
        setPrintRange(true, -1, -1);
    }

    public void doPrint() throws Exception {
        PrintRequestAttributeSet attr2;
        int n;

        attr2 = new HashPrintRequestAttributeSet(attributes);
        
        n = PrintController.getMargins();
        attributes.remove(MediaPrintableArea.class);
        attributes.add(new MediaPrintableArea(
                n, n,
                getPageWidth() - 2*n, getPageHeight() - 2*n,
                MediaPrintableArea.INCH));

        if (job.printDialog(attributes)) {
            job.print(attributes);
        }
        else {
            n = 0;
            attributes.remove(MediaPrintableArea.class);
            attributes.add(new MediaPrintableArea(
                    n, n,
                    getPageWidth() - 2*n, getPageHeight() - 2*n,
                    MediaPrintableArea.INCH));
        }
        
        attributes = new HashPrintRequestAttributeSet(attr2);
    }

    public @Override int print(Graphics g, PageFormat pf, int page) {
        if (printController.printCore((Graphics2D)g,
                                        page,
                                        (int)pf.getImageableWidth(),
                                        (int)pf.getImageableHeight()))
        {
            return PAGE_EXISTS;
        }
        else {
            return NO_SUCH_PAGE;
        }
    }



    public void updatePageSizeNames() throws Exception {
        PrintService crtPrintService;
        PrintTableMediaSizeName aux;
        String[] str;

        pageSizeNames = new ArrayList<String>();
        pageSizeNames.addAll(Arrays.asList(
                new PrintTableMediaSizeName(0).getStringEnum()));


        prettyPageSizeNames = new ArrayList<String>();
        for (int i=0; i<pageSizeNames.size(); i++) {
            prettyPageSizeNames.add(getPrettyName(pageSizeNames.get(i)));
        }

        orderedPrettyPageSizeNames = new ArrayList<String>();
        crtPrintService = job.getPrintService();
        aux = new PrintTableMediaSizeName(0);
        for (int i=0; i<prettyPageSizeNames.size(); i++) {
            aux.set(i);
            if (crtPrintService.isAttributeValueSupported(
                    aux.getSize(),
                    new DocFlavor.BYTE_ARRAY(
                        DocFlavor.BYTE_ARRAY.PNG.getMimeType()),
                    attributes)
                && !prettyPageSizeNames.get(i).equals("Unknown"))
            {
                orderedPrettyPageSizeNames.add(prettyPageSizeNames.get(i));
            }
        }
        str = orderedPrettyPageSizeNames.toArray(new String[0]);
        Arrays.sort(str);
        orderedPrettyPageSizeNames.clear();
        orderedPrettyPageSizeNames.addAll(Arrays.asList(str));
        
        // special addition, just for this application
        String s;
        
        s = "Tabloid";
        if (orderedPrettyPageSizeNames.remove(s)) {
            orderedPrettyPageSizeNames.add(0, s);
        }
        s = "Letter";
        if (orderedPrettyPageSizeNames.remove(s)) {
            orderedPrettyPageSizeNames.add(0, s);
        }
        s = "Legal";
        if (orderedPrettyPageSizeNames.remove(s)) {
            orderedPrettyPageSizeNames.add(0, s);
        }
    }

    public String[] getServicesNames() throws Exception {
        String[] strings;

        strings = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            strings[i] = services[i].getName();
        }

        return strings;
    }
    
    public int getServicesCount() throws Exception {
        return services.length;
    }

    public String[] getPageSizes() throws Exception {
        return orderedPrettyPageSizeNames.toArray(new String[0]);
    }

    public int getPageWidth() throws Exception {
        return (int)job.getPageFormat(attributes).getImageableWidth();
    }

    public int getPageHeight() throws Exception {
        return (int)job.getPageFormat(attributes).getImageableHeight();
    }



    public void setJobName(String name) throws Exception {
        jobName = name;

        attributes.remove(JobName.class);
        attributes.add(new JobName(jobName, null));
    }

    public String getJobName() throws Exception {
        return jobName;
    }

    public void setServiceIndex(int index) throws Exception {
        serviceIndex = index;

        job.setPrintService(services[serviceIndex]);
        updatePageSizeNames();
    }

    public int getServiceIndex() throws Exception {
        return serviceIndex;
    }

    public void setPageSizeIndex(int index) throws Exception {
//        JOptionPane.showMessageDialog(
//                null,
//                String.format("Setting %d out of %d\n",
//                index, orderedPrettyPageSizeNames.size()));
        
        if (orderedPrettyPageSizeNames == null) {
            return;
        }

        if (index < 0) {
            return;
        }

        if (index > orderedPrettyPageSizeNames.size()) {
            return;
        }

        pageSizeName =
            new PrintTableMediaSizeName(
                prettyPageSizeNames.indexOf(
                    orderedPrettyPageSizeNames.get(index)));

        attributes.remove(MediaSizeName.class);
        attributes.add(pageSizeName.getSize());
    }
    
    public void setPageSizeByName(String name) throws Exception {
        int index;

        index = prettyPageSizeNames.indexOf(name);
        
        if (index < 0) {
            return;
        }
        
        pageSizeName = new PrintTableMediaSizeName(index);

        attributes.remove(MediaSizeName.class);
        attributes.add(pageSizeName.getSize());
    }

    public int getPageSizeIndex() throws Exception {
        return orderedPrettyPageSizeNames.indexOf(
                    prettyPageSizeNames.get(pageSizeName.getIndex()));
    }

    public void setPortraitOriented(boolean portraitOriented) throws Exception {
        if (portraitOriented) {
            orientation = OrientationRequested.PORTRAIT;

            attributes.remove(OrientationRequested.class);
            attributes.add(orientation);
        }
        else {
            orientation = OrientationRequested.LANDSCAPE;

            attributes.remove(OrientationRequested.class);
            attributes.add(orientation);
        }
    }

    public boolean isPortraitOriented() throws Exception {
        return (orientation == OrientationRequested.PORTRAIT);
    }

    public void setCopiesCount(int value) throws Exception {
        copiesCount = value;

        attributes.remove(Copies.class);
        attributes.add(new Copies(copiesCount));
    }

    public int getCopiesCount() throws Exception {
        return copiesCount;
    }

    public void setCollate(boolean collated) throws Exception {
        if (collated) {
            collate = SheetCollate.COLLATED;

            attributes.remove(SheetCollate.class);
            attributes.add(collate);
        }
        else {
            collate = SheetCollate.UNCOLLATED;

            attributes.remove(SheetCollate.class);
            attributes.add(collate);
        }
    }

    public boolean getCollate() throws Exception {
        return (collate == SheetCollate.COLLATED);
    }

    public void setColored(boolean colored) throws Exception {
        if (colored) {
            chromaticity = Chromaticity.COLOR;

            attributes.remove(Chromaticity.class);
            attributes.add(chromaticity);
        }
        else {
            chromaticity = Chromaticity.MONOCHROME;

            attributes.remove(Chromaticity.class);
            attributes.add(chromaticity);
        }
    }

    public boolean isColored() throws Exception {
        return (chromaticity == Chromaticity.COLOR);
    }

    public void setPrintRange(boolean all, int first, int last) throws Exception {
        printRangeAll = all;
        printRangeInterval = new int[] { first, last };
        
        if (all) {
            attributes.remove(PageRanges.class);
        }
        else {
            attributes.remove(PageRanges.class);
            attributes.add(new PageRanges(first, last));
        }
    }
    
    public boolean getPrintRangeAll() throws Exception {
        return printRangeAll;
    }

    public int[] getPrintRangeInterval() throws Exception {
        return printRangeInterval;
    }



    private class PrintTableMediaSizeName extends MediaSizeName {

        private int index;



        public PrintTableMediaSizeName(int value) throws Exception {
            super(value);
            set(value);
        }

        private void set(int value) throws Exception {
            EnumSyntax[] values;

            values = getEnumValueTable();

            for (int i=0; i<values.length; i++) {
                if (values[i].getValue() == value) {
                    index = i;
                    break;
                }
            }
        }

        public String[] getStringEnum() throws Exception {
            return getStringTable();
        }

        public MediaSizeName getSize() throws Exception {
            return (MediaSizeName)getEnumValueTable()[index];
        }

        public int getIndex() throws Exception {
            return index;
        }

    }

    public boolean containsPageSize(String name) throws Exception {
        return prettyPageSizeNames.contains(name);
    }

//    private String getPrettyName(String defaultName) {
//
//        if (defaultName.equals("AAA")) { return "AAA"; }
//        else if (defaultName.equals("iso-a0")) { return "A0"; }
//        else if (defaultName.equals("iso-a1")) { return "A1"; }
//        else if (defaultName.equals("iso-a2")) { return "A2"; }
//        else if (defaultName.equals("iso-a3")) { return "A3"; }
//        else if (defaultName.equals("iso-a4")) { return "A4"; }
//        else if (defaultName.equals("iso-a5")) { return "A5"; }
//        else if (defaultName.equals("iso-a6")) { return "A6"; }
//        else if (defaultName.equals("iso-a7")) { return "A7"; }
//        else if (defaultName.equals("iso-a8")) { return "A8"; }
//        else if (defaultName.equals("iso-a9")) { return "A9"; }
//        else if (defaultName.equals("iso-a10")) { return "A10"; }
//
//        else if (defaultName.equals("iso-b0")) { return "B0"; }
//        else if (defaultName.equals("iso-b1")) { return "B1"; }
//        else if (defaultName.equals("iso-b2")) { return "B2"; }
//        else if (defaultName.equals("iso-b3")) { return "B3"; }
//        else if (defaultName.equals("iso-b4")) { return "B4"; }
//        else if (defaultName.equals("iso-b5")) { return "B5"; }
//        else if (defaultName.equals("iso-b6")) { return "B6"; }
//        else if (defaultName.equals("iso-b7")) { return "B7"; }
//        else if (defaultName.equals("iso-b8")) { return "B8"; }
//        else if (defaultName.equals("iso-b9")) { return "B9"; }
//        else if (defaultName.equals("iso-b10")) { return "B10"; }
//
//        else if (defaultName.equals("na-letter")) { return "Letter"; }
//        else if (defaultName.equals("na-legal")) { return "Legal"; }
//        else if (defaultName.equals("na-8x10")) { return "8x10"; }
//        else if (defaultName.equals("na-5x7")) { return "5x7"; }
//        else if (defaultName.equals("executive")) { return "Executive"; }
//        else if (defaultName.equals("folio")) { return "Folio"; }
//        else if (defaultName.equals("invoice")) { return "Invoice"; }
//        else if (defaultName.equals("tabloid")) { return "Tabloid"; }
//        else if (defaultName.equals("ledger")) { return "Ledger"; }
//        else if (defaultName.equals("quarto")) { return "Quarto"; }
//
//        else if (defaultName.equals("iso-c0")) { return "C0"; }
//        else if (defaultName.equals("iso-c1")) { return "C1"; }
//        else if (defaultName.equals("iso-c2")) { return "C2"; }
//        else if (defaultName.equals("iso-c3")) { return "C3"; }
//        else if (defaultName.equals("iso-c4")) { return "C4"; }
//        else if (defaultName.equals("iso-c5")) { return "C5"; }
//        else if (defaultName.equals("iso-c6")) { return "C6"; }
//
//        else if (defaultName.equals("iso-designated-long")) { return "Envelope DL"; }
//        else if (defaultName.equals("na-10x13-envelope")) { return "Envelope 10x13"; }
//        else if (defaultName.equals("na-9x12-envelope")) { return "Envelope 9x12"; }
//        else if (defaultName.equals("na-number-10-envelope")) { return "Envelope #10"; }
//        else if (defaultName.equals("na-7x9-envelope")) { return "Envelope 7x9"; }
//        else if (defaultName.equals("na-9x11-envelope")) { return "Envelope 9x11"; }
//        else if (defaultName.equals("na-10x14-envelope")) { return "Envelope 10x14"; }
//        else if (defaultName.equals("na-number-9-envelope")) { return "Envelope #9"; }
//        else if (defaultName.equals("na-6x9-envelope")) { return "Envelope 6x9"; }
//        else if (defaultName.equals("na-10x15-envelope")) { return "Envelope 10x15"; }
//        else if (defaultName.equals("monarch-envelope")) { return "Envelope Monarch"; }
//
//        else if (defaultName.equals("jis-b0")) { return "B0 (JIS)"; }
//        else if (defaultName.equals("jis-b1")) { return "B1 (JIS)"; }
//        else if (defaultName.equals("jis-b2")) { return "B2 (JIS)"; }
//        else if (defaultName.equals("jis-b3")) { return "B3 (JIS)"; }
//        else if (defaultName.equals("jis-b4")) { return "B4 (JIS)"; }
//        else if (defaultName.equals("jis-b5")) { return "B5 (JIS)"; }
//        else if (defaultName.equals("jis-b6")) { return "B6 (JIS)"; }
//        else if (defaultName.equals("jis-b7")) { return "B7 (JIS)"; }
//        else if (defaultName.equals("jis-b8")) { return "B8 (JIS)"; }
//        else if (defaultName.equals("jis-b9")) { return "B9 (JIS)"; }
//        else if (defaultName.equals("jis-b10")) { return "B10 (JIS)"; }
//
//        else if (defaultName.equals("a")) { return "ANSI A"; }
//        else if (defaultName.equals("b")) { return "ANSI B"; }
//        else if (defaultName.equals("c")) { return "ANSI C"; }
//        else if (defaultName.equals("d")) { return "ANSI D"; }
//        else if (defaultName.equals("e")) { return "ANSI E"; }
//
//        else if (defaultName.equals("arch-a")) { return "ARCH A"; }
//        else if (defaultName.equals("arch-b")) { return "ARCH B"; }
//        else if (defaultName.equals("arch-c")) { return "ARCH C"; }
//        else if (defaultName.equals("arch-d")) { return "ARCH D"; }
//        else if (defaultName.equals("arch-e")) { return "ARCH E"; }
//
//        else if (defaultName.equals("japanese-postcard")) { return "Postcard Japanese"; }
//        else if (defaultName.equals("oufuko-postcard")) { return "Postcard Oufuko"; }
//        else if (defaultName.equals("italian-envelope")) { return "Envelope Italian"; }
//        else if (defaultName.equals("personal-envelope")) { return "Envelope Personal"; }
//
//        else if (defaultName.equals("na-number-11-envelope")) { return "Envelope #11"; }
//        else if (defaultName.equals("na-number-12-envelope")) { return "Envelope #12"; }
//        else if (defaultName.equals("na-number-14-envelope")) { return "Envelope #14"; }
//
//        else return "Unknown";
//
//    }
    
    private String getPrettyName(String defaultName) {

        if (defaultName.equals("AAA")) { return "AAA"; }
        else if (defaultName.equals("iso-a0")) { return "A0"; }
        else if (defaultName.equals("iso-a1")) { return "A1"; }
        else if (defaultName.equals("iso-a2")) { return "A2"; }
        else if (defaultName.equals("iso-a3")) { return "A3"; }
        else if (defaultName.equals("iso-a4")) { return "A4"; }
        else if (defaultName.equals("iso-a5")) { return "A5"; }
        else if (defaultName.equals("iso-a6")) { return "A6"; }
        else if (defaultName.equals("iso-a7")) { return "A7"; }
        else if (defaultName.equals("iso-a8")) { return "A8"; }
        else if (defaultName.equals("iso-a9")) { return "A9"; }
        else if (defaultName.equals("iso-a10")) { return "A10"; }

//        else if (defaultName.equals("iso-b0")) { return "B0"; }
//        else if (defaultName.equals("iso-b1")) { return "B1"; }
//        else if (defaultName.equals("iso-b2")) { return "B2"; }
//        else if (defaultName.equals("iso-b3")) { return "B3"; }
//        else if (defaultName.equals("iso-b4")) { return "B4"; }
//        else if (defaultName.equals("iso-b5")) { return "B5"; }
//        else if (defaultName.equals("iso-b6")) { return "B6"; }
//        else if (defaultName.equals("iso-b7")) { return "B7"; }
//        else if (defaultName.equals("iso-b8")) { return "B8"; }
//        else if (defaultName.equals("iso-b9")) { return "B9"; }
//        else if (defaultName.equals("iso-b10")) { return "B10"; }

        else if (defaultName.equals("na-letter")) { return "Letter"; }
        else if (defaultName.equals("na-legal")) { return "Legal"; }
//        else if (defaultName.equals("na-8x10")) { return "8x10"; }
//        else if (defaultName.equals("na-5x7")) { return "5x7"; }
//        else if (defaultName.equals("executive")) { return "Executive"; }
//        else if (defaultName.equals("folio")) { return "Folio"; }
//        else if (defaultName.equals("invoice")) { return "Invoice"; }
        else if (defaultName.equals("tabloid")) { return "Tabloid"; }
//        else if (defaultName.equals("ledger")) { return "Ledger"; }
//        else if (defaultName.equals("quarto")) { return "Quarto"; }

//        else if (defaultName.equals("iso-c0")) { return "C0"; }
//        else if (defaultName.equals("iso-c1")) { return "C1"; }
//        else if (defaultName.equals("iso-c2")) { return "C2"; }
//        else if (defaultName.equals("iso-c3")) { return "C3"; }
//        else if (defaultName.equals("iso-c4")) { return "C4"; }
//        else if (defaultName.equals("iso-c5")) { return "C5"; }
//        else if (defaultName.equals("iso-c6")) { return "C6"; }

//        else if (defaultName.equals("iso-designated-long")) { return "Envelope DL"; }
//        else if (defaultName.equals("na-10x13-envelope")) { return "Envelope 10x13"; }
//        else if (defaultName.equals("na-9x12-envelope")) { return "Envelope 9x12"; }
//        else if (defaultName.equals("na-number-10-envelope")) { return "Envelope #10"; }
//        else if (defaultName.equals("na-7x9-envelope")) { return "Envelope 7x9"; }
//        else if (defaultName.equals("na-9x11-envelope")) { return "Envelope 9x11"; }
//        else if (defaultName.equals("na-10x14-envelope")) { return "Envelope 10x14"; }
//        else if (defaultName.equals("na-number-9-envelope")) { return "Envelope #9"; }
//        else if (defaultName.equals("na-6x9-envelope")) { return "Envelope 6x9"; }
//        else if (defaultName.equals("na-10x15-envelope")) { return "Envelope 10x15"; }
//        else if (defaultName.equals("monarch-envelope")) { return "Envelope Monarch"; }

//        else if (defaultName.equals("jis-b0")) { return "B0 (JIS)"; }
//        else if (defaultName.equals("jis-b1")) { return "B1 (JIS)"; }
//        else if (defaultName.equals("jis-b2")) { return "B2 (JIS)"; }
//        else if (defaultName.equals("jis-b3")) { return "B3 (JIS)"; }
//        else if (defaultName.equals("jis-b4")) { return "B4 (JIS)"; }
//        else if (defaultName.equals("jis-b5")) { return "B5 (JIS)"; }
//        else if (defaultName.equals("jis-b6")) { return "B6 (JIS)"; }
//        else if (defaultName.equals("jis-b7")) { return "B7 (JIS)"; }
//        else if (defaultName.equals("jis-b8")) { return "B8 (JIS)"; }
//        else if (defaultName.equals("jis-b9")) { return "B9 (JIS)"; }
//        else if (defaultName.equals("jis-b10")) { return "B10 (JIS)"; }

//        else if (defaultName.equals("a")) { return "ANSI A"; }
//        else if (defaultName.equals("b")) { return "ANSI B"; }
//        else if (defaultName.equals("c")) { return "ANSI C"; }
//        else if (defaultName.equals("d")) { return "ANSI D"; }
//        else if (defaultName.equals("e")) { return "ANSI E"; }

//        else if (defaultName.equals("arch-a")) { return "ARCH A"; }
//        else if (defaultName.equals("arch-b")) { return "ARCH B"; }
//        else if (defaultName.equals("arch-c")) { return "ARCH C"; }
//        else if (defaultName.equals("arch-d")) { return "ARCH D"; }
//        else if (defaultName.equals("arch-e")) { return "ARCH E"; }

//        else if (defaultName.equals("japanese-postcard")) { return "Postcard Japanese"; }
//        else if (defaultName.equals("oufuko-postcard")) { return "Postcard Oufuko"; }
//        else if (defaultName.equals("italian-envelope")) { return "Envelope Italian"; }
//        else if (defaultName.equals("personal-envelope")) { return "Envelope Personal"; }

//        else if (defaultName.equals("na-number-11-envelope")) { return "Envelope #11"; }
//        else if (defaultName.equals("na-number-12-envelope")) { return "Envelope #12"; }
//        else if (defaultName.equals("na-number-14-envelope")) { return "Envelope #14"; }

        else return "Unknown";

    }





}
