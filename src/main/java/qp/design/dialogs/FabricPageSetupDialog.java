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
import qp.control.FabricPrintController;
import qp.control.MainController;
import qp.control.PrintController;
import qp.design.Generals;
import qp.design.components.JCheckBoxGlass;
import qp.design.components.JLabelGlass;
import qp.design.components.JPanelGlass;
import qp.design.components.Printer;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class FabricPageSetupDialog extends JDialog {

    private static final Dimension sizeGroup = new Dimension(5, 5);
    private static final Dimension sizeFieldShort = new Dimension(50, 25);
    private static final Dimension sizeComboBox = new Dimension(100, 20);
    private static final Dimension sizeButton = new Dimension(75, 25);
    
    
    
    private JSpinner unit_size_input;
    
    private JComboBox cbService;
    private JComboBox paper_size_choice;
    private JTextField page_w_input, page_h_input;

    
    private JCheckBoxGlass bShowSpaces;
    private JButton bPrint;
    private JButton bCancel;
    
    
    
    private FabricPrintController printController;

        
    
    public FabricPageSetupDialog(FabricPrintController printController) throws Exception {
        super(MainController.getMainParent(),
                DS.FABRIC_PAGE_SETUP_DIALOG.getTitle(),
                true);

        this.printController = printController;
        
        setResizable(false);
        
        JPanelGlass panel, pChild, pNephew;
        String[] ds;
        int idx;

        ds = DS.FABRIC_PAGE_SETUP_DIALOG.str();
        idx = 0;
        
        panel = new JPanelGlass(300, 250);
        panel.setLayout(new BorderLayout());
        
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));

        pChild.add(getPanelScaling());
        
        pChild.add(getPanelPaperSize());
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        bShowSpaces = new JCheckBoxGlass(ds[idx++]);
        pNephew.add(Generals.surround(bShowSpaces, new Dimension(20, 10)));
        pChild.add(pNephew);
        
        panel.add(Generals.surround(pChild, new Dimension(5, 5)), BorderLayout.CENTER);

        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        bPrint = new JButton(ds[idx++]);
        bPrint.setPreferredSize(sizeButton);
        bPrint.setMaximumSize(sizeButton);
        pChild.add(bPrint);
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

        ds = DS.FABRIC_PAGE_SETUP_DIALOG.strScaling();
        idx = 0;
        
        panel = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.label());
        panel.setBorder(titleBorder);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
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
        
        return Generals.surround(panel, sizeGroup);
    }
    
    private JPanelGlass getPanelPaperSize() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        TitledBorder titleBorder;
        String[] ds;
        int idx;

        ds = DS.FABRIC_PAGE_SETUP_DIALOG.strPaperSize();
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
//        cbService.setSelectedIndex(printController.getServiceIndex());
        boolean foundGoodPrinter;
        int noPrinters;
        
        foundGoodPrinter = false;
        noPrinters = cbService.getItemCount();
        
        for (int i=0; i<noPrinters; i++) {
            try {

            cbService.setSelectedIndex(i);

            
            unit_size_input.setValue(printController.getUnitSize());


            printController.setPageSizeUpdateEnabled(false);
            paper_size_choice.removeAllItems();
            String[] str;
            str = printController.getPageSizes();
            for (String s:str) {
                paper_size_choice.addItem(s);
            }
            printController.setPageSizeUpdateEnabled(true);
            paper_size_choice.setSelectedIndex(printController.getPageSizeIndex());

            setToLetterSize();

            bPrint.requestFocusInWindow();

            
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

    
    
    private void setCallbacks() throws Exception {

        this.addWindowListener(new WindowAdapter() {
            public @Override void windowOpened(WindowEvent ev) {
                try {
                    
                page_w_input.setEditable(false);
                page_h_input.setEditable(false);
                
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
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        cbService.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.setServiceIndex(
                    ((JComboBox)ev.getSource()).getSelectedIndex());

                printController.setPageSizeUpdateEnabled(false);
                paper_size_choice.removeAllItems();
                String[] str;
                str = printController.getPageSizes();
                for (String s:str) {
                    paper_size_choice.addItem(s);
                }
                printController.setPageSizeUpdateEnabled(true);

                
                int index;
                
                index = paper_size_choice.getSelectedIndex();
                
                paper_size_choice.setSelectedIndex(
                    (index+1)%paper_size_choice.getItemCount());
                paper_size_choice.setSelectedIndex(index);
                
                setToLetterSize();

                
                boolean emptyPapersize;
                
                emptyPapersize = (paper_size_choice.getItemCount() == 0);
                bPrint.setEnabled(!emptyPapersize);

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        paper_size_choice.addItemListener(new ItemListener() {
            public @Override void itemStateChanged(ItemEvent ev) {
                try {

                printController.setPageSizeIndex(
                    paper_size_choice.getSelectedIndex());
                
                page_w_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageWidth()
                        / PrintController.FL_INCH));
                page_h_input.setText(
                    String.format(FORMAT.PRINT_PAPER_SIZE,
                        (double)printController.getPageHeight()
                        / PrintController.FL_INCH));
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bShowSpaces.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                printController.setPutSpaces(bShowSpaces.isSelected());
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        
        
        bPrint.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                printController.print();

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
