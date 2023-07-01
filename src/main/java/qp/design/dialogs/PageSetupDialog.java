package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import qp.CNT.FORMAT;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.control.PrintController;
import qp.control.QuiltedPhoto;
import qp.design.Generals;
import qp.design.components.*;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class PageSetupDialog extends JDialog {

    private static final Dimension sizeGroup = new Dimension(5, 5);
    private static final Dimension sizeFieldShort = new Dimension(50, 25);
    private static final Dimension sizeFieldLong = new Dimension(230, 25);
    private static final Dimension sizeComboBox = new Dimension(100, 20);
    private static final Dimension sizeButton = new Dimension(75, 25);
    
    
    
    private JSpinner unit_size_input;
    private JTextField sheets_output;
    private JTextField design_size_output;

    private JRadioButtonGlass all_sheets_radio, sheets_radio;
    private JSpinner sheets_first, sheets_last;
    private JRadioButtonGlass bRowColumn, bColumnRow;
    
    private JRadioButtonGlass portrait_radio, landscape_radio;
    
    private JSpinner num_copies_counter;
    private JCheckBoxGlass collate_toggle;
    private JTextField num_sheets_output;
    
    private JComboBox cbService;
    private JComboBox paper_size_choice;
    private JTextField page_w_input, page_h_input;

    private JCheckBoxGlass bFillCells;
    private JCheckBoxGlass bConservePaper;
    
    private JPanelGlass pCrazyBlocks;
    private JCheckBoxGlass bCFQPblock1;
    private JCheckBoxGlass bCFQPblock2;
    private JCheckBoxGlass bCFQPblock3;
    private JCheckBoxGlass bCFQPblock4;

    
    private JButton bPrint, bPrintPreview;
    private JButton bOk, bCancel;
    
    
    
    private PrintController printController;
    
    private boolean crazySetting;

        
    
    public PageSetupDialog(PrintController printController) throws Exception {
        super(MainController.getMainParent(), DS.PAGE_SETUP_DIALOG.getTitle(), true);

        this.printController = printController;
        
        setResizable(false);
        
        JPanelGlass panel, pChild, pNephew;
        String[] ds;
        int idx;
        
        ds = DS.PAGE_SETUP_DIALOG.str();
        idx = 0;

        panel = new JPanelGlass(550, 400);
        panel.setLayout(new BorderLayout());
        
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));

        pChild.add(getPanelScaling());
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(getPanelRange());
        pNephew.add(getPanelOrientation());
        pChild.add(pNephew);
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(getPanelCopies());
        pNephew.add(getPanelPaperSize());
        pChild.add(pNephew);
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        bFillCells = new JCheckBoxGlass(ds[idx++]);
        pNephew.add(Generals.surround(bFillCells, new Dimension(10, 0)));
        bConservePaper = new JCheckBoxGlass(ds[idx++]);
        pNephew.add(Generals.surround(bConservePaper, new Dimension(10, 0)));
        pChild.add(pNephew);
        
        pCrazyBlocks = new JPanelGlass();
        pCrazyBlocks.setLayout(new BoxLayout(pCrazyBlocks, BoxLayout.X_AXIS));
        pCrazyBlocks.add(new JLabelGlass(ds[idx++]));
        pCrazyBlocks.add(Box.createRigidArea(new Dimension(10, 0)));
        pCrazyBlocks.add(bCFQPblock1 = new JCheckBoxGlass(ds[idx++], 0));
        pCrazyBlocks.add(Box.createRigidArea(new Dimension(5, 0)));
        pCrazyBlocks.add(bCFQPblock2 = new JCheckBoxGlass(ds[idx++], 1));
        pCrazyBlocks.add(Box.createRigidArea(new Dimension(5, 0)));
        pCrazyBlocks.add(bCFQPblock3 = new JCheckBoxGlass(ds[idx++], 3));
        pCrazyBlocks.add(Box.createRigidArea(new Dimension(5, 0)));
        pCrazyBlocks.add(bCFQPblock4 = new JCheckBoxGlass(ds[idx++], 5));
        pChild.add(Generals.surround(pCrazyBlocks, new Dimension(13, 0)));
        
        panel.add(Generals.surround(pChild, new Dimension(5, 5)), BorderLayout.CENTER);

        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        bPrint = new JButton(ds[idx++]);
        bPrint.setPreferredSize(sizeButton);
        bPrint.setMaximumSize(sizeButton);
        pChild.add(bPrint);
        pChild.add(Box.createRigidArea(new Dimension(10, 0)));
        bPrintPreview = new JButton(ds[idx++]);
        bPrintPreview.setPreferredSize(new Dimension(125, 25));
        bPrintPreview.setMaximumSize(new Dimension(125, 25));
        pChild.add(bPrintPreview);
        pChild.add(Box.createRigidArea(new Dimension(90, 0)));
        bOk = new JButton(ds[idx++]);
        bOk.setPreferredSize(sizeButton);
        bOk.setMaximumSize(sizeButton);
        pChild.add(bOk);
        pChild.add(Box.createRigidArea(new Dimension(10, 0)));
        bCancel = new JButton(ds[idx++]);
        bCancel.setPreferredSize(sizeButton);
        bCancel.setMaximumSize(sizeButton);
        pChild.add(bCancel);
        panel.add(Generals.surround(pChild, new Dimension(60, 5)), BorderLayout.SOUTH);

        
        getContentPane().add(Generals.surround(panel, new Dimension(5, 5)));
        pack();
        
        
        setLocationRelativeTo(MainController.getMainParent());
        
        
        setCallbacks();
    }

    private JPanelGlass getPanelScaling() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        TitledBorder titleBorder;
        String[] ds;
        int idx;
        
        ds = DS.PAGE_SETUP_DIALOG.strScaling();
        idx = 0;

        panel = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.label());
        panel.setBorder(titleBorder);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        pChild.add(Box.createRigidArea(new Dimension(0, 10)));
        pNephew = new JPanelGlass();
        pNephew.add(new JLabelGlass(ds[idx++]));
        unit_size_input = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1));
        unit_size_input.setPreferredSize(sizeFieldShort);
        unit_size_input.setMaximumSize(sizeFieldShort);
        pNephew.add(unit_size_input);
        
        pNephew.add(new JLabelGlass(ds[idx++]));
        pChild.add(pNephew);
        pChild.setAlignmentY(CENTER_ALIGNMENT);
        panel.add(pChild);

        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        pNephew = new JPanelGlass();
        pNephew.setLayout(new GridLayout(2, 1));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pChild.add(pNephew);
        pNephew = new JPanelGlass();
        pNephew.setLayout(new GridLayout(2, 1));
        sheets_output = new JTextField();
        sheets_output.setPreferredSize(sizeFieldLong);
        sheets_output.setMaximumSize(sizeFieldLong);
        sheets_output.setMinimumSize(sizeFieldLong);
        pNephew.add(Generals.surround(sheets_output, new Dimension(0, 2)));
        design_size_output = new JTextField();
        design_size_output.setPreferredSize(sizeFieldLong);
        design_size_output.setMaximumSize(sizeFieldLong);
        design_size_output.setMinimumSize(sizeFieldLong);
        pNephew.add(Generals.surround(design_size_output, new Dimension(0, 2)));
        pChild.add(pNephew);
        
        pChild.setAlignmentY(CENTER_ALIGNMENT);
        panel.add(pChild);
        
        return Generals.surround(panel, sizeGroup);
    }
    
    private JPanelGlass getPanelRange() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        TitledBorder titleBorder;
        String[] ds;
        int idx;
        
        ds = DS.PAGE_SETUP_DIALOG.strRange();
        idx = 0;

        panel = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.label());
        panel.setBorder(titleBorder);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new GridLayout(2, 1));
        pChild.add(all_sheets_radio = new JRadioButtonGlass(ds[idx++]));
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(sheets_radio = new JRadioButtonGlass(ds[idx++]));
        sheets_first = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));
        sheets_first.setPreferredSize(sizeFieldShort);
        sheets_first.setMaximumSize(sizeFieldShort);
        pNephew.add(sheets_first);
        pNephew.add(Box.createRigidArea(new Dimension(5, 0)));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pNephew.add(Box.createRigidArea(new Dimension(5, 0)));
        sheets_last = new JSpinner(new SpinnerNumberModel(16, 1, 16, 1));
        sheets_last.setPreferredSize(sizeFieldShort);
        sheets_last.setMaximumSize(sizeFieldShort);
        pNephew.add(sheets_last);
        pChild.add(pNephew);
        panel.add(pChild);
        
        pChild = new JPanelGlass();
        pChild.setLayout(new GridLayout(2, 1));
        pChild.add(bRowColumn = new JRadioButtonGlass(ds[idx++]));
        pChild.add(bColumnRow = new JRadioButtonGlass(ds[idx++]));
        panel.add(pChild);
        
        return Generals.surround(panel, sizeGroup);
    }
    
    private JPanelGlass getPanelOrientation() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild;
        TitledBorder titleBorder;
        String[] ds;
        int idx;
        
        ds = DS.PAGE_SETUP_DIALOG.strOrientation();
        idx = 0;

        panel = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.label());
        panel.setBorder(titleBorder);
        
        pChild = new JPanelGlass();
        pChild.setLayout(new GridLayout(2, 1));
        pChild.add(portrait_radio = new JRadioButtonGlass(ds[idx++]));
        pChild.add(landscape_radio = new JRadioButtonGlass(ds[idx++]));
        panel.add(pChild);
        
        return Generals.surround(panel, sizeGroup);
    }
    
    private JPanelGlass getPanelCopies() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        TitledBorder titleBorder;
        String[] ds;
        int idx;
        
        ds = DS.PAGE_SETUP_DIALOG.strCopies();
        idx = 0;

        panel = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.label());
        panel.setBorder(titleBorder);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(Box.createRigidArea(new Dimension(5, 0)));
        pNephew.add(new JLabelGlass(ds[idx++]));
        num_copies_counter = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
        num_copies_counter.setPreferredSize(sizeFieldShort);
        num_copies_counter.setMaximumSize(sizeFieldShort);
        pNephew.add(num_copies_counter);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(pNephew);
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(collate_toggle = new JCheckBoxGlass(ds[idx++]));
        pNephew.add(Box.createRigidArea(new Dimension(20, 0)));
        pNephew.add(new JLabelGlass(ds[idx++]));
        num_sheets_output = new JTextField();
        num_sheets_output.setPreferredSize(sizeFieldShort);
        num_sheets_output.setMaximumSize(sizeFieldShort);
        num_sheets_output.setMinimumSize(sizeFieldShort);
        pNephew.add(num_sheets_output);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(pNephew);
        
        pChild.setAlignmentY(CENTER_ALIGNMENT);
        panel.add(pChild);

        return Generals.surround(panel, sizeGroup);
    }
    
    private JPanelGlass getPanelPaperSize() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        TitledBorder titleBorder;
        String[] ds;
        int idx;
        
        ds = DS.PAGE_SETUP_DIALOG.strPaperSize();
        idx = 0;

        panel = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.label());
        panel.setBorder(titleBorder);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        pNephew = new JPanelGlass();
        cbService = new JComboBox(printController.getServicesNames());
        cbService.setMaximumSize(sizeComboBox);
        cbService.setPreferredSize(sizeComboBox);
        cbService.setAlignmentX(LEFT_ALIGNMENT);
        pNephew.add(Generals.surround(cbService, new Dimension(0, 0)));
        pChild.add(pNephew);
        pNephew = new JPanelGlass();
        paper_size_choice = new JComboBox();
        paper_size_choice.setMaximumSize(sizeComboBox);
        paper_size_choice.setPreferredSize(sizeComboBox);
        paper_size_choice.setAlignmentX(LEFT_ALIGNMENT);
        pNephew.add(Generals.surround(paper_size_choice, new Dimension(0, 0)));
        pChild.add(pNephew);
        pChild.setAlignmentY(CENTER_ALIGNMENT);
        panel.add(pChild);

        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        pNephew = new JPanelGlass();
        pNephew.setLayout(new GridLayout(2, 1));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pChild.add(pNephew);
        pNephew = new JPanelGlass();
        pNephew.setLayout(new GridLayout(2, 1));
        page_w_input = new JTextField();
        page_w_input.setPreferredSize(sizeFieldShort);
        page_w_input.setMaximumSize(sizeFieldShort);
        page_w_input.setMinimumSize(sizeFieldShort);
        pNephew.add(page_w_input);
        page_h_input = new JTextField();
        page_h_input.setPreferredSize(sizeFieldShort);
        page_h_input.setMaximumSize(sizeFieldShort);
        page_h_input.setMinimumSize(sizeFieldShort);
        pNephew.add(page_h_input);
        pChild.add(pNephew);
        pChild.add(Box.createRigidArea(new Dimension(3, 0)));
        pNephew = new JPanelGlass();
        pNephew.setLayout(new GridLayout(2, 1));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pChild.add(pNephew);
        
        pChild.setAlignmentY(CENTER_ALIGNMENT);
        panel.add(pChild);

        return Generals.surround(panel, sizeGroup);
    }



    public void display() throws Exception {
        pCrazyBlocks.setVisible(QuiltedPhoto.getUseCfqp());
        if (!QuiltedPhoto.getUseCfqp()) {
            unit_size_input.setEnabled(true);
            bFillCells.setEnabled(true);
        }
        
//        cbService.setSelectedIndex(printController.getServiceIndex());
        boolean foundGoodPrinter;
        int noPrinters;
        
        foundGoodPrinter = false;
        noPrinters = cbService.getItemCount();
        
        for (int i=0; i<noPrinters; i++) {
            try {

            cbService.setSelectedIndex(i);
            unit_size_input.setValue(printController.getUnitSize());
            if (printController.getPrintAllSheets()) {
                all_sheets_radio.doClick();
            }
            else {
                sheets_radio.doClick();
            }
            sheets_first.setValue(printController.getFirstSheet());
            sheets_last.setValue(printController.getLastSheet());
            if (printController.getRowColOrder()) {
                bRowColumn.doClick();
            }
            else {
                bColumnRow.doClick();
            }
            if (printController.isPortraitOriented()) {
                portrait_radio.doClick();
            }
            else {
                landscape_radio.doClick();
            }
            num_copies_counter.setValue(printController.getCopiesCount());
            collate_toggle.setSelected(false);
            if (printController.getCollate()) {
                collate_toggle.doClick();
            }
            page_w_input.setEditable(false);
            page_h_input.setEditable(false);

            updatePaperSizes();
            paper_size_choice.setSelectedIndex(printController.getPageSizeIndex());

            setToLetterSize();

            bPrint.requestFocusInWindow();

            crazySetting = false;


            foundGoodPrinter = true;
            
            break;

            }
            catch (Exception e) { Logger.printErr(e); }
        
        }
        
        if (!foundGoodPrinter) {
            JOptionPane.showMessageDialog(
                MainController.getMainParent(),
                "Application could not properly communicate with any of the"
                    + " printers installed on this computer. \nInstall a valid"
                    + " printer and then try again.");
            
            return;
        }
        
        setVisible(true);
    }
    
    private void setToLetterSize() throws Exception {
        if (printController.containsPageSize(Printer.DEFAULT_PAGE_SIZE)) {
            paper_size_choice.setSelectedItem(Printer.DEFAULT_PAGE_SIZE);
        }
    }
    
    
    
    public void updateScaling() throws Exception {
        double[] designSize;
        int num_sheets;


        designSize = printController.getDesignSize();
        design_size_output.setText(
                String.format(FORMAT.PRINT_DESIGN_INFO,
                designSize[0], designSize[1]));


        printController.updatePrintSettings();
        num_sheets = printController.getNumSheets();
        sheets_output.setText(String.format(
                FORMAT.PRINT_PAGES_COUNT,
                num_sheets));
        
        ((SpinnerNumberModel)sheets_first.getModel()).setMaximum(num_sheets);
        ((SpinnerNumberModel)sheets_last.getModel()).setMaximum(num_sheets);
        sheets_first.setValue(1);
        sheets_last.setValue(num_sheets);
        
        updateTotalNumberSheets();
    }
    
    private void updateTotalNumberSheets() throws Exception {
        num_sheets_output.setText(String.format(
                FORMAT.PRINT_PAGES_COUNT,
                printController.getNumSheetsTotal()));
    }

    private void updatePaperSizes() throws Exception {
        printController.setPageSizeUpdateEnabled(false);
        
        paper_size_choice.removeAllItems();
        String[] str;
        str = printController.getPageSizes();
        for (String s:str) {
            paper_size_choice.addItem(s);
        }
        
        printController.setPageSizeUpdateEnabled(true);
    }
    
    private void setCallbacks() throws Exception {

        this.addWindowListener(new WindowAdapter() {
            public @Override void windowOpened(WindowEvent ev) {
                try {
                    
                sheets_output.setEditable(false);
                design_size_output.setEditable(false);
                num_sheets_output.setEditable(false);
                
                sheets_output.setBackground(Colors.gray_light);
                design_size_output.setBackground(Colors.gray_light);
                num_sheets_output.setBackground(Colors.gray_light);
                page_w_input.setBackground(Colors.gray_light);
                page_h_input.setBackground(Colors.gray_light);

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        unit_size_input.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                printController.setUnitSize(
                    (Double)unit_size_input.getValue());
                updateScaling();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        all_sheets_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                if (!all_sheets_radio.isSelected()) {
                    all_sheets_radio.setSelected(true);
                    return;
                }
                sheets_radio.setSelected(!all_sheets_radio.isSelected());

                printController.setPrintAllSheets(true);
                updateTotalNumberSheets();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        sheets_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                if (!sheets_radio.isSelected()) {
                    sheets_radio.setSelected(true);
                    return;
                }
                all_sheets_radio.setSelected(!sheets_radio.isSelected());

                printController.setPrintAllSheets(false);
                updateTotalNumberSheets();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        sheets_first.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                printController.setFirstSheet((Integer)sheets_first.getValue());
                updateTotalNumberSheets();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        sheets_last.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                printController.setLastSheet((Integer)sheets_last.getValue());
                updateTotalNumberSheets();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bRowColumn.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                if (!bRowColumn.isSelected()) {
                    bRowColumn.setSelected(true);
                    return;
                }
                bColumnRow.setSelected(!bRowColumn.isSelected());

                printController.setRowColOrder(true);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bColumnRow.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                if (!bColumnRow.isSelected()) {
                    bColumnRow.setSelected(true);
                    return;
                }
                bRowColumn.setSelected(!bColumnRow.isSelected());

                printController.setRowColOrder(false);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        portrait_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                if (!portrait_radio.isSelected()) {
                    portrait_radio.setSelected(true);
                    return;
                }
                landscape_radio.setSelected(!portrait_radio.isSelected());

                printController.setPortraitOriented(true);
                
                page_w_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageWidth()
                        / PrintController.FL_INCH));
                page_h_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageHeight()
                        / PrintController.FL_INCH));
                
                updateScaling();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        landscape_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                if (!landscape_radio.isSelected()) {
                    landscape_radio.setSelected(true);
                    return;
                }
                portrait_radio.setSelected(!landscape_radio.isSelected());

                printController.setPortraitOriented(false);
                
                page_w_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageWidth()
                        / PrintController.FL_INCH));
                page_h_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageHeight()
                        / PrintController.FL_INCH));
                
                updateScaling();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        collate_toggle.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                printController.setCollate(collate_toggle.isSelected());
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        num_copies_counter.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                printController.setCopiesCount((Integer)num_copies_counter.getValue());
                updateTotalNumberSheets();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        cbService.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.setServiceIndex(
                    ((JComboBox)ev.getSource()).getSelectedIndex());

                updatePaperSizes();

                
                int index;
                
                index = paper_size_choice.getSelectedIndex();
                
                paper_size_choice.setSelectedIndex(
                    (index+1)%paper_size_choice.getItemCount());
                paper_size_choice.setSelectedIndex(index);

                setToLetterSize();
                
                
                boolean emptyPapersize;
                
                emptyPapersize = (paper_size_choice.getItemCount() == 0);
                bPrint.setEnabled(!emptyPapersize);
                bPrintPreview.setEnabled(!emptyPapersize);

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        paper_size_choice.addItemListener(new ItemListener() {
            public @Override void itemStateChanged(ItemEvent ev) {
                try {

                int index;
                
                index = paper_size_choice.getSelectedIndex();
                
                printController.setPageSizeIndex(index);

                page_w_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageWidth()
                        / PrintController.FL_INCH));
                page_h_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageHeight()
                        / PrintController.FL_INCH));

                updateScaling();
                
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bFillCells.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.setWithFilling(bFillCells.isSelected());
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bConservePaper.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.setConservePaper(bConservePaper.isSelected());
                
                updateScaling();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        ActionListener crazyListener;
        
        crazyListener = new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (crazySetting) {
                    return;
                }

                JCheckBoxGlass b;
                
                b = (JCheckBoxGlass)ev.getSource();
                
                if (!b.isSelected()) {
                    unit_size_input.setEnabled(true);
                    bFillCells.setEnabled(true);
                    
                    printController.setCrazySpecialPrintingIndex(-1);
                }
                else {
                    unit_size_input.setEnabled(false);
                    bFillCells.setEnabled(false);

                    crazySetting = true;
                    if (b.equals(bCFQPblock1)) {
                        bCFQPblock2.setSelected(false);
                        bCFQPblock3.setSelected(false);
                        bCFQPblock4.setSelected(false);
                    }
                    else if (b.equals(bCFQPblock2)) {
                        bCFQPblock3.setSelected(false);
                        bCFQPblock1.setSelected(false);
                        bCFQPblock4.setSelected(false);
                    }
                    else if (b.equals(bCFQPblock3)) {
                        bCFQPblock1.setSelected(false);
                        bCFQPblock2.setSelected(false);
                        bCFQPblock4.setSelected(false);
                    }
                    else if (b.equals(bCFQPblock4)) {
                        bCFQPblock1.setSelected(false);
                        bCFQPblock2.setSelected(false);
                        bCFQPblock3.setSelected(false);
                    }
                    crazySetting = false;
                    
                    printController.setCrazySpecialPrintingIndex(
                        (Integer)b.getTag());
                }
                
                updateScaling();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
        
        bCFQPblock1.addActionListener(crazyListener);
        bCFQPblock2.addActionListener(crazyListener);
        bCFQPblock3.addActionListener(crazyListener);
        bCFQPblock4.addActionListener(crazyListener);
        
        
        
        bPrint.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.print();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bPrintPreview.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                setVisible(false);
                if (new PreviewDialog().display()) {
                    printController.print();
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bOk.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                setVisible(false);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bCancel.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.cancel();
                setVisible(false);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }
    
    
    
    
    
}
