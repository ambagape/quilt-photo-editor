package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import qp.CNT;
import qp.CNT.ICONS;
import qp.DS;
import qp.Logger;
import qp.control.AdjustController;
import qp.control.MainController;
import qp.design.Generals;
import qp.design.components.JLabelGlass;
import qp.design.components.JPanelGlass;
import qp.design.components.JPanelIcon;
import qp.design.components.JPanelIcon.POSITION;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class Adjuster extends JDialog {

    /** images border insets */
    private static final int mg = 5;



    private JPanelIcon displayerInput;
    private JPanelIcon displayerOutput;

    private JSlider sliderContrast;
    private JSlider sliderBrightness;
    
    private JLabelGlass lContrast;
    private JLabelGlass lBrightness;

    private JButton bOk, bCancel;


    private AdjustController adjustController;

    private boolean hasKeyManager;
    private KeyEventDispatcher keyManager;
    private boolean canceled;



    public Adjuster() throws Exception {
        super(MainController.getMainParent(),
                DS.ADJUST_DIALOG.getTitle(),
                true);


        JPanel panel;
        JPanelGlass pChild, pNephew, pGrandNephew;
        JPanelGlass pTemp;
        JPanelIcon iconBrightness, iconContrast;
        TitledBorder titleBorder;
        JLabelGlass label;
        JComponent separator;
        int contentWidth;
        float alignment;
        String[] ds;
        int idx;


        contentWidth = 600;
        ds = DS.ADJUST_DIALOG.str();
        idx = 0;


        setIconImage(ICONS.LOGO());
        setResizable(false);
        panel = new JPanel();
        panel.setLayout(new BorderLayout());


        // images panel
        pChild = new JPanelGlass(contentWidth, 300);
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));

        pNephew = new JPanelGlass();
        pGrandNephew = new JPanelGlass(256, 256);
        pGrandNephew.setLayout(new BoxLayout(pGrandNephew, BoxLayout.X_AXIS));
        displayerInput = new JPanelIcon(POSITION.LEFT_UP);
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.title());
        pGrandNephew.add(displayerInput);
        pNephew.setBorder(
            new CompoundBorder(
                new CompoundBorder(
                    titleBorder,
                    BorderFactory.createEmptyBorder(mg, mg, mg, mg)),
                BorderFactory.createLoweredBevelBorder()));
        pNephew.add(pGrandNephew);
        pChild.add(pNephew);

        pChild.add(Box.createRigidArea(new Dimension(20, 0)));

        pNephew = new JPanelGlass();
        pGrandNephew = new JPanelGlass(256, 256);
        pGrandNephew.setLayout(new BoxLayout(pGrandNephew, BoxLayout.X_AXIS));
        displayerOutput = new JPanelIcon(POSITION.LEFT_UP);
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.title());
        pGrandNephew.add(displayerOutput);
        pNephew.setBorder(
            new CompoundBorder(
                new CompoundBorder(
                    titleBorder,
                    BorderFactory.createEmptyBorder(mg, mg, mg, mg)),
                BorderFactory.createLoweredBevelBorder()));
        pNephew.add(pGrandNephew);
        pChild.add(pNephew);

        panel.add(Generals.surround(pChild, new Dimension(10, 10)),
                    BorderLayout.NORTH);


        // controls panel
        pTemp = new JPanelGlass();
        titleBorder = new TitledBorder(ds[idx++]);
        titleBorder.setTitleFont(Fonts.title());
        pTemp.setBorder(titleBorder);
        pTemp.setLayout(new BoxLayout(pTemp, BoxLayout.X_AXIS));

        pTemp.add(Box.createRigidArea(new Dimension(100, 0)));
        
        pChild = new JPanelGlass(400, 110);
        pChild.setLayout(new BorderLayout());

        pNephew = new JPanelGlass(200, 50);
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.setAlignmentY(LEFT_ALIGNMENT);

        iconBrightness = new JPanelIcon(ICONS.BRIGHTNESS);
        pNephew.add(iconBrightness);
        
        pGrandNephew = new JPanelGlass();
        pGrandNephew.setLayout(new BoxLayout(pGrandNephew, BoxLayout.Y_AXIS));
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.labelSmall());
        label.setAlignmentY(LEFT_ALIGNMENT);
        pGrandNephew.add(label);
        sliderBrightness = new JSlider(-255, 255, 0);
        sliderBrightness.setAlignmentY(LEFT_ALIGNMENT);
        sliderBrightness.setFont(Fonts.labelSmall());
        sliderBrightness.setOpaque(false);
        sliderBrightness.setPaintTicks(false);
        pGrandNephew.add(sliderBrightness);
        pNephew.add(pGrandNephew);

        lBrightness = new JLabelGlass();
        lBrightness.setPreferredSize(new Dimension(50, 25));
        lBrightness.setFont(Fonts.labelSmall());
        pNephew.add(lBrightness);
        
        pChild.add(pNephew, BorderLayout.NORTH);

        pChild.add(Box.createRigidArea(new Dimension(0, 0)), BorderLayout.CENTER);

        pNephew = new JPanelGlass(200, 50);
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.setAlignmentY(LEFT_ALIGNMENT);

        iconContrast = new JPanelIcon(ICONS.CONTRAST);
        pNephew.add(iconContrast);
        
        pGrandNephew = new JPanelGlass();
        pGrandNephew.setLayout(new BoxLayout(pGrandNephew, BoxLayout.Y_AXIS));
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.labelSmall());
        label.setAlignmentY(LEFT_ALIGNMENT);
        pGrandNephew.add(label);
        sliderContrast = new JSlider(-100, 100, 0);
        sliderContrast.setAlignmentY(LEFT_ALIGNMENT);
        sliderContrast.setFont(Fonts.labelSmall());
        sliderContrast.setOpaque(false);
        sliderContrast.setPaintTicks(false);
        pGrandNephew.add(sliderContrast);
        pNephew.add(pGrandNephew);

        lContrast = new JLabelGlass();
        lContrast.setPreferredSize(new Dimension(50, 25));
        lContrast.setFont(Fonts.labelSmall());
        pNephew.add(lContrast);
        
        pChild.add(pNephew, BorderLayout.SOUTH);

        pTemp.add(pChild);
        panel.add(Generals.surround(pTemp, new Dimension(10, 10)),
                    BorderLayout.CENTER);


        // decision panel
        pChild = new JPanelGlass(contentWidth, 50);
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        alignment = TOP_ALIGNMENT;

        separator = (JComponent)pChild.add(Box.createRigidArea(
                        new Dimension((contentWidth - 10 - 2*100)/2, 0)));
        separator.setAlignmentY(alignment);

        pNephew = new JPanelGlass(100, 30);
        bOk = new JButton(ds[idx++]);
        bOk.setPreferredSize(new Dimension(100, 25));
        bOk.setMaximumSize(new Dimension(100, 25));
        pNephew.add(bOk);
        pNephew.setAlignmentY(alignment);
        pChild.add(pNephew);

        ((JComponent)pChild.add(Box.createRigidArea(new Dimension(10, 0))))
                .setAlignmentY(alignment);

        pNephew = new JPanelGlass(100, 30);
        bCancel = new JButton(ds[idx++]);
        bCancel.setPreferredSize(new Dimension(100, 25));
        bCancel.setMaximumSize(new Dimension(100, 25));
        pNephew.add(bCancel);
        pNephew.setAlignmentY(alignment);
        pChild.add(pNephew);

        panel.add(pChild, BorderLayout.SOUTH);



        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(MainController.getMainParent());


        ToolTipManager.sharedInstance().setInitialDelay(CNT.SECOND);


        setCallbacks();
    }



    public Object[] open(Object ... input) {
        try {

        BufferedImage image;
        int brightness, contrast;


        // get input parameters
        image = (BufferedImage)input[0];
        brightness = (Integer)input[1];
        contrast = (Integer)input[2];


        // start adjuster
        adjustController = new AdjustController(
                                    displayerInput,
                                    displayerOutput);
        adjustController.setImageInput(image);
        pack();
        setLocationRelativeTo(MainController.getMainParent());
        
        sliderBrightness.setValue(sliderBrightness.getMinimum());
        sliderContrast.setValue(sliderContrast.getMinimum());
        sliderBrightness.setValue(brightness);
        sliderContrast.setValue(contrast);

        canceled = true;

        this.setVisible(true);


        // return result
        if (!canceled) {
            return new Object[] {
                adjustController.getImageAdjusted(),
                adjustController.getBrightness(),
                adjustController.getContrast()
            };
        }
        else {
            return null;
        }

        }
        catch (Exception e) { Logger.printErr(e); return null; }
    }



    private void setCallbacks() throws Exception {

        addWindowListener(new WindowAdapter() {
            public @Override void windowOpened(WindowEvent ev) {
                try {

                hasKeyManager = false;

                keyManager = new KeyEventDispatcher() {
                    public @Override boolean dispatchKeyEvent(KeyEvent ev) {
                        try {

                        if (!Adjuster.this.isShowing()
                                || !Adjuster.this.isFocused()) {
                            return false;
                        }

                        if (ev.getID() == KeyEvent.KEY_PRESSED) {
                            switch (ev.getKeyCode()) {
                                case KeyEvent.VK_ESCAPE:
                                    Adjuster.this.dispose();
                                    break;

                                default:
                                    break;
                            }
                        }

                        return false;

                        }
                        catch (Exception e) {
                            Logger.printErr(e);
                            return false;
                        }
                    }
                };

                Generals.addKeyDispatcher(keyManager);
                hasKeyManager = true;


                bOk.requestFocusInWindow();

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void windowClosing(WindowEvent ev) {
                try {

                if (hasKeyManager) {
                    Generals.removeKeyDispatcher(keyManager);
                    hasKeyManager = false;
                }

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void windowClosed(WindowEvent ev) {
                try {

                if (hasKeyManager) {
                    Generals.removeKeyDispatcher(keyManager);
                    hasKeyManager = false;
                }

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });



        sliderBrightness.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                lBrightness.setText(String.format(CNT.FORMAT.SLIDER, sliderBrightness.getValue()));
                adjustController.setBrightness(sliderBrightness.getValue());

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        sliderContrast.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                lContrast.setText(String.format(CNT.FORMAT.SLIDER, sliderContrast.getValue()));
                adjustController.setContrast(sliderContrast.getValue());

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });



        bOk.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                canceled = false;
                Adjuster.this.dispose();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bCancel.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                Adjuster.this.dispose();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

    }





}