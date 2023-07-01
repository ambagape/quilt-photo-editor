package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import qp.CNT.FORMAT;
import qp.DS;
import qp.Logger;
import qp.control.FabricPrintController;
import qp.control.MainController;
import qp.design.Generals;
import qp.design.components.JButtonRounded;
import qp.design.components.JPanelGlass;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class PaletteDialog extends JDialog {

    private JButtonRounded bPrintAll;
    private JButtonRounded bPrintSelected;
    private JButtonRounded bDone;
    
    private JPanelGlass palette_scroll;
    
    
    
    private FabricPrintController controller;
    
    private int fabricIndex;

    
    
    public PaletteDialog(ArrayList<BufferedImage> images) throws Exception {
        super(MainController.getMainParent(), DS.PALETTE_DIALOG.getTitle(), true);
        
        JPanelGlass panel, pChild;
        JScrollPane scroll;
        String[] ds;
        int idx;
        
        ds = DS.PALETTE_DIALOG.str();
        idx = 0;

        panel = new JPanelGlass(545, 300);
        panel.setLayout(new BorderLayout());
        
        pChild = new JPanelGlass();
        bPrintAll = new JButtonRounded(ds[idx++]);
        bPrintAll.setPreferredSize(new Dimension(125, 25));
        bPrintAll.setHightlightColor(Colors.bluish);
        bPrintAll.setBackground(Colors.blue_weak);
        bPrintAll.setForeground(Colors.blue_dark);
        pChild.add(bPrintAll);
        pChild.add(Box.createRigidArea(new Dimension(5, 0)));
        bPrintSelected = new JButtonRounded(ds[idx++]);
        bPrintSelected.setPreferredSize(new Dimension(175, 25));
        bPrintSelected.setHightlightColor(Colors.bluish);
        bPrintSelected.setBackground(Colors.blue_weak);
        bPrintSelected.setForeground(Colors.blue_dark);
        pChild.add(bPrintSelected);
        pChild.add(Box.createRigidArea(new Dimension(5, 0)));
        bDone = new JButtonRounded(ds[idx++]);
        bDone.setPreferredSize(new Dimension(75, 25));
        bDone.setHightlightColor(Colors.bluish);
        bDone.setBackground(Colors.blue_weak);
        bDone.setForeground(Colors.blue_dark);
        pChild.add(bDone);
        panel.add(Generals.surround(pChild, new Dimension(5, 5)), BorderLayout.NORTH);
        
        palette_scroll = new JPanelGlass();
        palette_scroll.setLayout(new GridLayout(images.size()/4, 4, 5, 5));
        palette_scroll.setOpaque(true);
        palette_scroll.setBackground(Colors.white);

        JButton b;
        
        for (int i=0; i<images.size(); i++) {
            b = new JButton(String.format(FORMAT.PALETTE_INDEX, i+1));
            b.setIcon(new ImageIcon(images.get(i)));
            b.setName(String.format(FORMAT.PALETTE_INDEX, i));
            b.setBorder(BorderFactory.createLineBorder(Colors.blue_dark, 2));
            b.setPreferredSize(new Dimension(100, 100));
            b.setMaximumSize(new Dimension(100, 100));
            b.setVerticalTextPosition(SwingConstants.TOP);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            palette_scroll.add(b);
        }
        
        scroll = new JScrollPane(palette_scroll);
        panel.add(scroll, BorderLayout.CENTER);
        
        getContentPane().add(Generals.surround(panel, new Dimension(5, 5)));
        pack();
        
        
        setCallbacks();

        
        fabricIndex = 0;
        if (palette_scroll.getComponentCount() > 0) {
            ((JButton)palette_scroll.getComponent(0)).doClick();
        }
        
        
        controller = new FabricPrintController();
        controller.start();
        
        
        int n;

        n = palette_scroll.getComponentCount();
        for (int i=0; i<n; i++) {
            b = (JButton)palette_scroll.getComponent(i);
            
            b.setBorderPainted(false);
            b.setBackground(Colors.gray);
            b.setOpaque(false);
        }
        
        setPatternSelected(0, true);
    }
    
    

    private void setPatternSelected(int index, boolean value) throws Exception {
        JButton b;
        
        b = (JButton)palette_scroll.getComponent(index);
        
        b.setBorderPainted(value);

        b.setOpaque(value);
        b.setBackground(Colors.yellowish);
    }

    
    
    private void setCallbacks() throws Exception {
        
        for (int i=0; i<palette_scroll.getComponentCount(); i++) {
            ((JButton)palette_scroll.getComponent(i))
                .addActionListener(new ActionListener() {
                    public @Override void actionPerformed(ActionEvent ev) {
                        try {

                        JButton b;
                            
                        b = (JButton)ev.getSource();
                        setPatternSelected(fabricIndex, false);

                        fabricIndex = Integer.parseInt(b.getName());
                        setPatternSelected(fabricIndex, true);

                        }
                        catch (Exception e) { Logger.printErr(e); }
                    }
                });
        }

        bPrintAll.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                controller.open(-1);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bPrintSelected.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                controller.open(fabricIndex);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bDone.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                PaletteDialog.this.dispose();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }

    
    
    
    
}
