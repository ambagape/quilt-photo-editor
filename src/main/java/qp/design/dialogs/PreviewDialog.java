package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import qp.CNT.PATHS;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.control.QuiltedPhoto;
import qp.database.CommunicationLocal;
import qp.design.Generals;
import qp.design.components.JPanelGlass;
import qp.design.components.JPanelIcon;


/**
 *
 * @author Maira57
 */
public class PreviewDialog extends JDialog {

    private JSpinner preview_page_counter;
    
    private JPanelGlass preview_wizard;
    
    private JButton bPrint;
    private JButton bCancel;
    
    

    private boolean doPrint;

        
    
    public PreviewDialog() throws Exception {
        super(MainController.getMainParent(), DS.PREVIEW_DIALOG.getTitle(), true);

        
        JScrollPane scroll;
        JPanelGlass panel, pChild, pNephew;
        String[] ds;
        int idx;
        
        ds = DS.PREVIEW_DIALOG.str();
        idx = 0;

        panel = new JPanelGlass(620, 553);
        panel.setLayout(new BorderLayout());

        
        pChild = new JPanelGlass(100, 25);
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        pChild.add(new JLabel(ds[idx++]));
        preview_page_counter = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        preview_page_counter.setPreferredSize(new Dimension(50, 25));
        preview_page_counter.setMaximumSize(new Dimension(50, 25));
        pChild.add(preview_page_counter);
        panel.add(Generals.surround(pChild, new Dimension(250, 10)), BorderLayout.NORTH);
        
        preview_wizard = new JPanelGlass();
        preview_wizard.setLayout(new BorderLayout());
        scroll = new JScrollPane(preview_wizard);
        panel.add(scroll, BorderLayout.CENTER);
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.Y_AXIS));
        bPrint = new JButton(ds[idx++]);
        bPrint.setPreferredSize(new Dimension(75, 25));
        bPrint.setMaximumSize(new Dimension(75, 25));
        bPrint.setAlignmentX(CENTER_ALIGNMENT);
        pNephew.add(bPrint);
        pChild.add(Generals.surround(pNephew, new Dimension(5, 5)), BorderLayout.CENTER);
        bCancel = new JButton(ds[idx++]);
        bCancel.setPreferredSize(new Dimension(75, 25));
        bCancel.setMaximumSize(new Dimension(75, 25));
        pChild.add(Generals.surround(bCancel, new Dimension(20, 5)), BorderLayout.EAST);
        panel.add(pChild, BorderLayout.SOUTH);

        
        getContentPane().add(Generals.surround(panel, new Dimension(5, 5)));
        pack();
        
        
        setLocationRelativeTo(MainController.getMainParent());
        
        
        setCallbacks();
    }
    
    
    
    public boolean display() throws Exception {
        doPrint = false;
        
        setVisible(true);
        
        return doPrint;
    }
    
    
    
    private void setCallbacks() throws Exception {

        this.addWindowListener(new WindowAdapter() {
            public @Override void windowOpened(WindowEvent ev) {
                try {

                int no_images;
                    
                no_images = QuiltedPhoto.loadPreview();
                ((SpinnerNumberModel)preview_page_counter.getModel())
                    .setMaximum(no_images);
                preview_page_counter.setValue(1);

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });


        
        preview_page_counter.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                int index;
                
                index = (Integer)preview_page_counter.getValue();
                preview_wizard.removeAll();
                preview_wizard.add(
                    new JPanelIcon(
                        CommunicationLocal.getImageFromLocal(
                            PATHS.getPreviewImageName(index-1))));
                pack();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        bPrint.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                doPrint = true;
                PreviewDialog.this.dispose();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bCancel.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                PreviewDialog.this.dispose();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }
    
    
    
    
    
}
