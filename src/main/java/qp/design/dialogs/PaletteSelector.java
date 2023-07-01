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
import qp.control.QuiltedPhoto;
import qp.design.Generals;
import qp.design.components.JButtonRounded;
import qp.design.components.JLabelGlass;
import qp.design.components.JPanelGlass;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class PaletteSelector extends JDialog {

    private JButtonRounded bRegenerate;
    private JButtonRounded bRestoreOriginalPalette;
    private JButtonRounded bDone;
    
    private JPanelGlass palette_scroll;
    
    
    
    private FabricPrintController controller;
    
    private ArrayList<Boolean> fabricsSelected;

    
    
    public PaletteSelector(
            ArrayList<BufferedImage> images,
            ArrayList<Boolean> selected)
            throws Exception
    {
        super(MainController.getMainParent(), DS.PALETTE_SELECTOR_DIALOG.getTitle(), true);
        
        JPanelGlass panel, pChild, pNephew;
        JScrollPane scroll;
        String[] ds;
        int idx;
        
        ds = DS.PALETTE_SELECTOR_DIALOG.str();
        idx = 0;

        panel = new JPanelGlass(545, 300);
        panel.setLayout(new BorderLayout());
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        
        pNephew = new JPanelGlass();
        pNephew.add(new JLabelGlass(ds[idx++]));
        pChild.add(pNephew, BorderLayout.NORTH);
        
        pNephew = new JPanelGlass();
        bRegenerate = new JButtonRounded(ds[idx++]);
        bRegenerate.setPreferredSize(new Dimension(125, 25));
        bRegenerate.setHightlightColor(Colors.bluish);
        bRegenerate.setBackground(Colors.blue_weak);
        bRegenerate.setForeground(Colors.blue_dark);
        pNephew.add(bRegenerate);
        pNephew.add(Box.createRigidArea(new Dimension(5, 0)));
        bRestoreOriginalPalette = new JButtonRounded(ds[idx++]);
        bRestoreOriginalPalette.setPreferredSize(new Dimension(150, 25));
        bRestoreOriginalPalette.setHightlightColor(Colors.bluish);
        bRestoreOriginalPalette.setBackground(Colors.blue_weak);
        bRestoreOriginalPalette.setForeground(Colors.blue_dark);
        pNephew.add(bRestoreOriginalPalette);
        pNephew.add(Box.createRigidArea(new Dimension(5, 0)));
        bDone = new JButtonRounded(ds[idx++]);
        bDone.setPreferredSize(new Dimension(75, 25));
        bDone.setHightlightColor(Colors.bluish);
        bDone.setBackground(Colors.blue_weak);
        bDone.setForeground(Colors.blue_dark);
        pNephew.add(bDone);
        pChild.add(pNephew, BorderLayout.CENTER);
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
        
        
        fabricsSelected = selected;
        for (int i=0; i<n; i++) {
            setPatternSelected(i, fabricsSelected.get(i));
        }
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
                        int index;
                            
                        b = (JButton)ev.getSource();
                        index = Integer.parseInt(b.getName());
                        setPatternSelected(
                            index, !fabricsSelected.get(index));
                        fabricsSelected.set(index, !fabricsSelected.get(index));

                        }
                        catch (Exception e) { Logger.printErr(e); }
                    }
                });
        }
        
        bRegenerate.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.doRegenerate();
                PaletteSelector.this.dispose();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bRestoreOriginalPalette.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.restore_original_palette(true);
                PaletteSelector.this.dispose();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bDone.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                PaletteSelector.this.dispose();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }

    
    
    
    
}
