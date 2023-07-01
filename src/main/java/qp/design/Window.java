package qp.design;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import qp.CNT;
import qp.CNT.FORMAT;
import qp.CNT.ICONS;
import qp.CNT.IMAGES;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.control.ProcessImage;
import qp.control.QuiltedPhoto;
import qp.database.Patterns;
import qp.design.components.*;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;
import qp.design.dialogs.AboutDialog;


/**
 *
 * @author Maira57
 */
public class Window extends JFrame {
    
    private static final Dimension sizeMain = new Dimension(10, 10);
    private static final Dimension sizeScroll = new Dimension(5, 5);
    private static final Dimension sizeMainButton = new Dimension(90, 60);
    private static final Dimension sizeInterButtons = new Dimension(2, 0);
    private static final Dimension sizeSecondaryButton = new Dimension(100, 25);
    private static final Dimension sizeFieldShort = new Dimension(80, 25);
    private static final Dimension sizeFieldShort2 = new Dimension(60, 25);
    private static final Dimension sizeTabs = new Dimension(30, 10);
    private static final Dimension sizeEditor = new Dimension(240, 300);
    private static final Dimension sizeViewportOrig = new Dimension(280, 230);
    private static Dimension sizeViewport = new Dimension(290, 200);



    // components
    private WindowMenu menu;

    
    private JButtonColored bNew;
    private JButtonColored bOpen;
    private JButtonColored bSavePattern;
    private JButtonColored bPrintPattern;
    private JButtonColored bSelectPhoto;
    private JButtonColored bAdjustColors;
    private JButtonColored process_button;
    private JButtonColored bViewPalette;
    private JButtonColored bHelp;
    

    private JPanelGlass editor_container;

    private JLabelGlass lDetails;
    private JSlider detail_slider;
    private JSpinner spDetail;
    private JLabelGlass lFabrics;
    private JSlider fabric_slider;
    private JSpinner spFabrics;
    
    private JRadioButtonGlass color_radio, gray_radio, sepia_radio, fabric_radio;
    private JPanelGlass angle_group;
    private JRadioButtonGlass angle_straight_radio, angle_30_radio, angle_45_radio;
    
    private JButtonRounded bSelectPalette;

    
    private JPanelGlass original_container;
    private JPanelIcon original_box;
    private JButtonColored restore_original_button;
    
    private JPanelGlass processed_container;
    private JPanelIcon processed_box;
    private JTextField detail_output;
    private JTextField quantity_output;


    private JButtonRounded bMinimizeTabs, bMaximizeTabs;
    
    private JTabbedPane mainTabs;
    private JPanelGlass[] tabs;

    private JPanel patterns_scroll;

    private JCheckBoxGlass use_qbnpa_check;
    private JLabelGlass lUseQbnpa;
    private JSlider shape_smoothing_slider;
    
    private JCheckBoxGlass use_cfqp_check;
    
    private JButtonRounded bFabricsLoad;
    private JButtonRounded bFabricsSortByValue;
    private JButtonRounded bFabricsRemoveAll;
    private JButtonRounded bFabricsPrint;
    private JCheckBoxGlass user_fabric_check;
    private JPanelGlass sort_fabrics_group;

    private JPanelGlass fabricColl_group;
    private JButtonRounded bFabricCollImport;
    private JButtonRounded bFabricCollRemove;
        

    
    private JPanel pShader;
    
    
    
    // private variables
    private JPanel pBigLogo;
    private JLabel lBigLogoLeft;
    private JLabel lBigLogoMiddle;
    private JLabel lBigLogoRight;
    
    private ActionListener processListener;
    private ActionListener fabricCollActionListener;
    
    private boolean withPack;
    
    private boolean maximized;
    
    private int oldDetailValue;
    
    private boolean mutualLinkOn;



    public Window(int mainWidth, int mainHeight) throws Exception {
        super(DS.MAIN.getTitle());


        // global adjustments
        setIconImage(ICONS.LOGO());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);


        // create the layout
        getContentPane().add(getContent(mainWidth, mainHeight));
        
        
        getContentPane().setBackground(Colors.white);
        pack();
        Insets insets;
        insets = getInsets();
        setMinimumSize(new Dimension(
                mainWidth + 2*5 + insets.left + insets.right,
                mainHeight + 2*5 + insets.top + insets.bottom));


        // set window shader
        pShader = new JPanel();
        pShader.setBackground(Colors.white_semi);
        Generals.setWaitCursor(pShader);
        setGlassPane(pShader);


        // set tooltips
        ToolTipManager.sharedInstance().setInitialDelay(CNT.SECOND);
        ToolTipManager.sharedInstance().setDismissDelay(2 * CNT.SECOND);
        ToolTipManager.sharedInstance().setReshowDelay(0);


        // define callbacks
        setCallbacks();
    }

    public void initialize() throws Exception {
        withPack = true;
        
        detail_output.setEditable(false);
        quantity_output.setEditable(false);
        
        angle_group.setVisible(false);

        int n;
        JButton b;

        n = patterns_scroll.getComponentCount();
        for (int i=0; i<n; i++) {
            b = (JButton)patterns_scroll.getComponent(i);
            
            b.setBorderPainted(false);
            b.setBackground(Colors.gray);
            b.setOpaque(false);
        }

        mainTabs.setSelectedIndex(0);
        
        if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            user_fabric_check.setSelected(false);
            user_fabric_check.doClick();
        }

        if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            use_cfqp_check.setSelected(false);
            use_cfqp_check.doClick();
        }
        
        if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            use_qbnpa_check.setSelected(false);
            use_qbnpa_check.doClick();
        }

        maximized = false;
        maximize(maximized);
        
        oldDetailValue = 100;
        
        mutualLinkOn = true;
    }



    private JPanel getContent(int mainWidth, int mainHeight) throws Exception {
        JPanel panel;


        // create general layout
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(mainWidth, mainHeight));
        panel.setMaximumSize(new Dimension(mainWidth, mainHeight));
        panel.setBackground(Colors.white);
        panel.setLayout(new BorderLayout());


        panel.add(Generals.surround(
                    getPanelMenuAndMainButtons(mainWidth), new Dimension()),
                    BorderLayout.NORTH);

        
        if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            getPanelCentral();
        }
        else {
            panel.add(getPanelCentral(),
                    BorderLayout.CENTER);
        }
        

        if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            JPanelGlass pChild;
            JScrollPane scroll;
            TitledBorder border;
        
            getPanelTabs();

            pChild = new JPanelGlass();
            pChild.setLayout(new BorderLayout());
            border = new TitledBorder("Fabric Window");
            border.setTitleFont(Fonts.title());
            border.setTitleColor(Colors.blue_light);
            pChild.setBorder(border);
        
            scroll = new JScrollPane();
//            scroll.setPreferredSize(new Dimension(450, 180));

            sort_fabrics_group = new JPanelGlass();

            scroll.setViewportView(
                Generals.surround(sort_fabrics_group, sizeScroll));
            scroll.setAlignmentY(CENTER_ALIGNMENT);

            pChild.add(scroll, BorderLayout.CENTER);
            panel.add(Generals.surround(pChild, sizeMain),
                    BorderLayout.CENTER);
        }
        else {
            panel.add(Generals.surround(getPanelTabs(), sizeMain),
                    BorderLayout.SOUTH);
        }
        

        return panel;
    }
    
    private JPanel getPanelMenuAndMainButtons(int width) throws Exception {
        JPanel panel;
        JPanelGlass pChild, pNephew;
        JPanelGlass pTemp;
        String[] ds;
        int idx;


        panel = new JPanel();
        panel.setBackground(Colors.blue_light);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(700, 80));
        panel.setMaximumSize(new Dimension(700, 80));
        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS));


        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.X_AXIS));
        pChild.setAlignmentX(LEFT_ALIGNMENT);
        
        menu = new WindowMenu();
        pChild.add(menu);

        panel.add(pChild);


        ds = DS.MAIN.mainButtons();
        idx = 0;

        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());

        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));

        pNephew.add(bNew = getMainButton(ds[idx++], ICONS.NEW));
        pNephew.add(Box.createRigidArea(sizeInterButtons));
        pNephew.add(bOpen = getMainButton(ds[idx++], ICONS.OPEN));
        pNephew.add(Box.createRigidArea(sizeInterButtons));
        pNephew.add(bSavePattern = getMainButton(ds[idx++], ICONS.SAVE));

        pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

        pNephew.add(bPrintPattern = getMainButton(ds[idx++], ICONS.PRINT));

        pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

        if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            bOpen.setVisible(false);
            bSavePattern.setVisible(false);
            
            pNephew.add(bSelectPhoto = getMainButton("Import Fabric", ICONS.IMPORT));

            pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

            pNephew.add(process_button = getMainButton("Sort Fabric", ICONS.PROCESS, new Dimension(95, 60)));

            pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

            pNephew.add(bHelp = getMainButton("Video Help", ICONS.HELP));
            
            bAdjustColors = getMainButton(new String(), ICONS.ADJUST);
            bViewPalette = getMainButton(new String(), ICONS.PALETTE);
        }
        else {
            pNephew.add(bSelectPhoto = getMainButton(ds[idx++], ICONS.IMPORT));
            pNephew.add(Box.createRigidArea(sizeInterButtons));
            pNephew.add(bAdjustColors = getMainButton(ds[idx++], ICONS.ADJUST));

            pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

            pNephew.add(process_button = getMainButton(ds[idx++], ICONS.PROCESS, new Dimension(95, 60)));

            pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

            pNephew.add(bViewPalette = getMainButton(ds[idx++], ICONS.PALETTE));

            pNephew.add(new JLabelGlass(new ImageIcon(ICONS.SEP)));

            pNephew.add(bHelp = getMainButton(ds[idx++], ICONS.HELP));
        }

        pChild.add(pNephew, BorderLayout.WEST);


        pChild.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(pChild);

        
        pTemp = new JPanelGlass();
        pTemp.setLayout(new BorderLayout());
        
        pBigLogo = new JPanelGlass(
                    width,
                    IMAGES.BIG_LOGO_A.getHeight());
        pBigLogo.setLayout(new BorderLayout());
        lBigLogoLeft = new JLabel(new ImageIcon());
        pBigLogo.add(lBigLogoLeft,
                        BorderLayout.WEST);
        lBigLogoMiddle = new JLabel(new ImageIcon());
        pBigLogo.add(lBigLogoMiddle,
                        BorderLayout.CENTER);
        lBigLogoRight = new JLabel(new ImageIcon());
        pBigLogo.add(lBigLogoRight,
                        BorderLayout.EAST);
        pBigLogo.setAlignmentX(LEFT_ALIGNMENT);
        if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            pTemp.add(pBigLogo, BorderLayout.NORTH);
        }
        
        pTemp.add(Generals.surround(new JPanelIcon(ICONS.CORNER_LOGO),
                                    new Dimension(7, 0)),
                    BorderLayout.WEST);
        pTemp.add(Generals.surround(panel, sizeMain),
                    BorderLayout.CENTER);
        
        
        return pTemp;
    }

    private JPanelGlass getPanelCentral() throws Exception {
        JPanelGlass pCentral;
        JPanelGlass pChild, pNephew;
        JPanelGlass pTemp, pTemp2;
        JLabelGlass label;
        TitledBorder border;
        String[] ds;
        int idx;

        
        ds = DS.MAIN.panelCentral();
        idx = 0;
        
        pCentral = new JPanelGlass();
        pCentral.setLayout(new BorderLayout());
        

        editor_container = getPixelEditor();
        pCentral.add(Generals.surround(editor_container, sizeMain),
                    BorderLayout.WEST);


        pTemp2 = new JPanelGlass();
        pTemp2.setLayout(new BorderLayout());
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        
        original_container =
            new JPanelGlass(sizeViewport.width, sizeViewport.height);
        border = new TitledBorder(ds[idx++]);
        border.setTitleFont(Fonts.title());
        border.setTitleColor(Colors.blue_light);
        original_container.setBorder(border);
        original_box = new JPanelIcon();
        original_box.setCropVisible(true);
        original_container.add(original_box);
        pChild.add(original_container, BorderLayout.CENTER);

        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        
        restore_original_button =
                getMainButton(ds[idx++], ICONS.FIT, new Dimension(100, 60));
        pNephew.add(restore_original_button);
        
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.labelSmallBold());
        label.setForeground(Colors.blue_light);
        pNephew.add(Generals.surround(label, new Dimension(10, 0)));

        pTemp = new JPanelGlass();
        pTemp.setLayout(new BorderLayout());
        pTemp.add(pNephew, BorderLayout.WEST);
        
        pChild.add(Generals.surround(pTemp, sizeMain),
                    BorderLayout.SOUTH);

        pTemp2.add(Generals.surround(pChild, sizeMain),
                    BorderLayout.WEST);

        
        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        
        processed_container =
            new JPanelGlass(sizeViewport.width, sizeViewport.height);
        border = new TitledBorder(ds[idx++]);
        border.setTitleFont(Fonts.title());
        border.setTitleColor(Colors.blue_light);
        processed_container.setBorder(border);
        processed_box = new JPanelIcon();
        processed_container.add(processed_box);
        pChild.add(processed_container, BorderLayout.CENTER);

        pTemp2.add(Generals.surround(pChild, sizeMain),
                    BorderLayout.CENTER);

        pCentral.add(Generals.surround(pTemp2, new Dimension()),
                    BorderLayout.CENTER);


        return pCentral;
    }

    private JPanelGlass getPanelTabs() throws Exception {
        JPanelGlass panel, pChild, pNephew;
        JLabelGlass label;
        Dimension sizeMinMax;
        String[] ds;
        int idx;

        
        ds = DS.MAIN.panelTabFunctions();
        idx = 0;

        
        sizeMinMax = new Dimension(40, 15);
        
        
        panel = new JPanelGlass();
        panel.setLayout(new BorderLayout());


        // main tabs (general description and controls)
        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        
        label = new JLabelGlass(DS.MAIN.panelTabsMainTitle);
        label.setFont(Fonts.title());
        label.setForeground(Colors.blue_light);
        if (!CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
            label.setVisible(false);
        }
        pChild.add(label, BorderLayout.WEST);
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        bMinimizeTabs = new JButtonRounded(ds[idx++]);
        bMinimizeTabs.setFont(Fonts.title());
        bMinimizeTabs.setForeground(Colors.blue_light);
        bMinimizeTabs.setPreferredSize(sizeMinMax);
        bMinimizeTabs.setMaximumSize(sizeMinMax);
        pNephew.add(bMinimizeTabs);
        pNephew.add(Box.createRigidArea(new Dimension(5, 0)));
        bMaximizeTabs = new JButtonRounded(ds[idx++]);
        bMaximizeTabs.setFont(Fonts.title());
        bMaximizeTabs.setForeground(Colors.blue_light);
        bMaximizeTabs.setPreferredSize(sizeMinMax);
        bMaximizeTabs.setMaximumSize(sizeMinMax);
        pNephew.add(bMaximizeTabs);
        pChild.add(pNephew, BorderLayout.EAST);
        
        panel.add(Generals.surround(pChild, new Dimension(5, 0)),
                    BorderLayout.NORTH);
        

        // main tabs (the tabs)
        mainTabs = new JTabbedPane();
        mainTabs.setFont(Fonts.label());
        mainTabs.setForeground(Colors.LABEL_TEXT.getColor());

        tabs = new JPanelGlass[5];
        
        tabs[0] = getMainTab01();
        tabs[1] = getMainTab02();
        tabs[2] = getMainTab03();
        tabs[3] = getMainTab04();
        tabs[4] = getMainTab05();

        if (!CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)
                && !CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH))
        {
            mainTabs.addTab(
                    DS.MAIN.panelTabTitles()[0],
                    Generals.surround(tabs[0], sizeTabs));
        }
        if (!CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)
                && !CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)
//                && !CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)
                )
        {
            mainTabs.addTab(
                    DS.MAIN.panelTabTitles()[1],
                    Generals.surround(tabs[1], sizeTabs));
        }
        if (!CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)
                && !CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)
                && !CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)
                )
        {
            mainTabs.addTab(
                    DS.MAIN.panelTabTitles()[2],
                    Generals.surround(tabs[2], sizeTabs));
        }
        mainTabs.addTab(
                DS.MAIN.panelTabTitles()[3],
                Generals.surround(tabs[3], sizeTabs));
        mainTabs.addTab(
                DS.MAIN.panelTabTitles()[4],
                Generals.surround(tabs[4], sizeTabs));
        
        panel.add(mainTabs, BorderLayout.CENTER);

        
        return panel;
    }

    
    
    private JPanelGlass getPixelEditor() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        JPanelGlass pTemp;
        JLabelGlass label;
        TitledBorder border;
        Dimension sizeInterElem;
        String[] ds;
        JSpinner.NumberEditor editor;
        int idx;

        
        sizeInterElem = new Dimension(0, 15);
        ds = DS.MAIN.pixelEditor();
        idx = 0;
        
        
        pTemp = new JPanelGlass(sizeEditor.width, sizeEditor.height - 40);
        pTemp.setLayout(new BorderLayout());
        border = new TitledBorder(ds[idx++]);
        border.setTitleFont(Fonts.title());
        border.setTitleColor(Colors.blue_light);
        pTemp.setBorder(border);
        panel = new JPanelGlass();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.labelSmall());
        label.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(label);
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        lDetails = new JLabelGlass();
        lDetails.setPreferredSize(new Dimension(30, 25));
        lDetails.setFont(Fonts.labelSmall());
        pNephew.add(lDetails);
        if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            detail_slider = new JSlider(1, 50, 18);
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            detail_slider = new JSlider(1, 150, 42);
        }
        else {
            detail_slider = new JSlider(1, 100, 30);
        }
        detail_slider.setFont(Fonts.labelSmall());
        detail_slider.setOpaque(false);
        detail_slider.setPaintTicks(false);
        pNephew.add(detail_slider);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(pNephew);
        pChild.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(pChild);

        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(Box.createRigidArea(new Dimension(35, 0)));

        spDetail = new JSpinner(new SpinnerNumberModel(
                        detail_slider.getValue(),
                        detail_slider.getMinimum(),
                        detail_slider.getMaximum(),
                        1));
        spDetail.setPreferredSize(sizeFieldShort2);
        spDetail.setMaximumSize(sizeFieldShort2);
        editor = new JSpinner.NumberEditor(spDetail, FORMAT.SPINNER_INT_3_DEC);
        spDetail.setEditor(editor);
        pNephew.add(spDetail);
        pNephew.add(Box.createRigidArea(new Dimension(35, 0)));
        
        detail_output = new JTextField();
        detail_output.setPreferredSize(sizeFieldShort);
        detail_output.setMaximumSize(sizeFieldShort);
        detail_output.setMinimumSize(sizeFieldShort);
        pNephew.add(detail_output);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(pNephew);

        
        panel.add(Generals.rigidAreaOnX(sizeInterElem, LEFT_ALIGNMENT));
        
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.labelSmall());
        label.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(label);
        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        lFabrics = new JLabelGlass();
        lFabrics.setPreferredSize(new Dimension(30, 25));
        lFabrics.setFont(Fonts.labelSmall());
        pNephew.add(lFabrics);
        fabric_slider = new JSlider(2, 252, 24);
        fabric_slider.setFont(Fonts.labelSmall());
        fabric_slider.setOpaque(false);
        fabric_slider.setPaintTicks(false);
        pNephew.add(fabric_slider);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(pNephew);
        pChild.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(pChild);

        
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(Box.createRigidArea(new Dimension(35, 0)));
        
        spFabrics = new JSpinner(new SpinnerNumberModel(
                        fabric_slider.getValue(),
                        fabric_slider.getMinimum(),
                        fabric_slider.getMaximum(),
                        1));
        spFabrics.setPreferredSize(sizeFieldShort2);
        spFabrics.setMaximumSize(sizeFieldShort2);
        editor = new JSpinner.NumberEditor(spFabrics, FORMAT.SPINNER_INT_3_DEC);
        spFabrics.setEditor(editor);
        pNephew.add(spFabrics);
        pNephew.add(Box.createRigidArea(new Dimension(35, 0)));
        
        quantity_output = new JTextField();
        quantity_output.setPreferredSize(sizeFieldShort);
        quantity_output.setMaximumSize(sizeFieldShort);
        quantity_output.setMinimumSize(sizeFieldShort);
        pNephew.add(quantity_output);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(pNephew);

        
        panel.add(Generals.rigidAreaOnX(sizeInterElem, LEFT_ALIGNMENT));
        
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        border = new TitledBorder(ds[idx++]);
        border.setTitleFont(Fonts.blueTitle());
        border.setTitleColor(Colors.blue_strong);
        pChild.setBorder(border);
        
//        pChild.add(color_radio = new JRadioButtonGlass(ds[idx++]));
//        pChild.add(gray_radio = new JRadioButtonGlass(ds[idx++]));
//        pChild.add(sepia_radio = new JRadioButtonGlass(ds[idx++]));
//        pChild.add(fabric_radio = new JRadioButtonGlass(ds[idx++]));

        color_radio = new JRadioButtonGlass(ds[idx++]);
        gray_radio = new JRadioButtonGlass(ds[idx++]);
        sepia_radio = new JRadioButtonGlass(ds[idx++]);
        fabric_radio = new JRadioButtonGlass(ds[idx++]);
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(color_radio);
        pNephew.add(sepia_radio);
        pChild.add(pNephew);
        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(fabric_radio);
        pNephew.add(gray_radio);
        pChild.add(pNephew);
        
        color_radio.setFont(Fonts.labelSmall());
        gray_radio.setFont(Fonts.labelSmall());
        sepia_radio.setFont(Fonts.labelSmall());
        fabric_radio.setFont(Fonts.labelSmall());
        pChild.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(pChild);

        
        panel.add(Generals.rigidAreaOnX(sizeInterElem, LEFT_ALIGNMENT));
        
        
        angle_group = new JPanelGlass();
        angle_group.setLayout(new BoxLayout(angle_group, BoxLayout.X_AXIS));
        border = new TitledBorder(ds[idx++]);
        border.setTitleFont(Fonts.blueTitle());
        border.setTitleColor(Colors.blue_strong);
        angle_group.setBorder(border);
        angle_group.add(angle_straight_radio = new JRadioButtonGlass(ds[idx++]));
        angle_group.add(angle_30_radio = new JRadioButtonGlass(ds[idx++]));
        angle_group.add(angle_45_radio = new JRadioButtonGlass(ds[idx++]));
        angle_straight_radio.setFont(Fonts.labelSmall());
        angle_30_radio.setFont(Fonts.labelSmall());
        angle_45_radio.setFont(Fonts.labelSmall());
        angle_group.setAlignmentX(LEFT_ALIGNMENT);
        
        panel.add(angle_group);

        
//        panel.add(Generals.rigidAreaOnX(sizeInterElem, LEFT_ALIGNMENT));
        
        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        pChild.add(bSelectPalette =
                        getSecondaryButton(ds[idx++], new Dimension(125, 25)));
        pChild.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(pChild);
        
        
        pTemp.add(Generals.surround(panel, new Dimension(5, 5)));
        
        
        return pTemp;
    }
    
    private JPanelGlass getMainTab01() throws Exception {
        JPanelGlass panel;
        JScrollPane scroll;
        JLabel label;
        JButton b;
        String[] ds;
        int idx;

        
        ds = DS.MAIN.panelTab01();
        idx = 0;
        
        panel = new JPanelGlass();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.tabContent());
        panel.add(Generals.surround(label, new Dimension(0, 0)));
        
        
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        
        
        scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(450, 200));
        
        patterns_scroll = new JPanelGlass();
        patterns_scroll.setLayout(
                new GridLayout(Patterns.patterns.length, 1, 5, 5));
        for (int i=0; i<Patterns.patterns.length; i++) {
            b = new JButton();
            b.setIcon(
                new ImageIcon(ProcessImage.create_pattern_image(i, 400, 80)));
            b.setName(String.format(FORMAT.BTN_NAME, i));
            b.setBorder(BorderFactory.createLineBorder(Colors.blue_dark, 2));
            b.setPreferredSize(new Dimension(100, 100));
            b.setMaximumSize(new Dimension(100, 100));
            b.setVerticalTextPosition(SwingConstants.TOP);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            patterns_scroll.add(b);
        }
        
        scroll.setViewportView(
            Generals.surround(patterns_scroll, sizeScroll));
        scroll.setAlignmentY(CENTER_ALIGNMENT);
        
        panel.add(scroll);

        
        return panel;
    }
    
    private JPanelGlass getMainTab02() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew, pGrandNephew;
        JLabelGlass label;
        String[] ds;
        int idx;
        
        ds = DS.MAIN.panelTab02();
        idx = 0;
        
        panel = new JPanelGlass();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.tabContent());
        pChild.add(label);
        
        if (!CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            pChild.add(Box.createRigidArea(new Dimension(0, 50)));
        }

        use_qbnpa_check = new JCheckBoxGlass(ds[idx++]);
        use_qbnpa_check.setFont(Fonts.checkBoxBigger());
        if (!CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            pChild.add(use_qbnpa_check);
        }
        
        panel.add(pChild);

        
        panel.add(Box.createRigidArea(new Dimension(30, 0)));

        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        
        pChild.add(Box.createRigidArea(new Dimension(0, 30)));

        pNephew = new JPanelGlass(370, 50);
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.setBorder(new RoundBorder(Colors.white_semi));
        pNephew.add(new JLabelGlass(ds[idx++]));
        pNephew.add(Box.createRigidArea(new Dimension(15, 0)));
        pGrandNephew = new JPanelGlass();
        pGrandNephew.setLayout(new BoxLayout(pGrandNephew, BoxLayout.X_AXIS));
        lUseQbnpa = new JLabelGlass();
        lUseQbnpa.setPreferredSize(new Dimension(30, 35));
        lUseQbnpa.setMaximumSize(new Dimension(30, 35));
        pGrandNephew.add(lUseQbnpa);
        shape_smoothing_slider = new JSlider(1, 20, 5);
        shape_smoothing_slider.setOpaque(false);
        shape_smoothing_slider.setPreferredSize(new Dimension(200, 35));
        shape_smoothing_slider.setMaximumSize(new Dimension(200, 35));
        shape_smoothing_slider.setPaintTicks(false);
        shape_smoothing_slider.setToolTipText(ds[idx++]);
        pGrandNephew.add(shape_smoothing_slider);
        pNephew.add(pGrandNephew);
        pNephew.setAlignmentX(LEFT_ALIGNMENT);
        pChild.add(pNephew);
        
        panel.add(pChild);
        
        
        return panel;
    }
    
    private JPanelGlass getMainTab03() throws Exception {
        JPanelGlass panel;
        JScrollPane scroll;
        JPanelGlass pChild;
        JLabelGlass label;
        JButton b;
        int n;
        String[] ds;
        int idx;
        
        ds = DS.MAIN.panelTab03();
        idx = 0;
        
        panel = new JPanelGlass();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.tabContent());
        pChild.add(label);
        
        pChild.add(Box.createRigidArea(new Dimension(0, 30)));

        use_cfqp_check = new JCheckBoxGlass(ds[idx++]);
        use_cfqp_check.setFont(Fonts.checkBoxBigger());
        pChild.add(use_cfqp_check);
        panel.add(pChild);

        
        panel.add(Box.createRigidArea(new Dimension(30, 0)));

        
        scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(450, 200));
        
        pChild = new JPanelGlass();
        pChild.setLayout(new GridLayout(8, 3, 5, 5));
        n = patterns_scroll.getComponentCount();
        for (int i=0; i<24; i++) {
            b = new JButton();
            b.setIcon(new ImageIcon(
                ProcessImage.create_cfqp_image(i, 90, 90)));
            b.setName(String.format(FORMAT.BTN_NAME, i+n+1));
            b.setBorder(BorderFactory.createLineBorder(Colors.blue_dark, 2));
            b.setPreferredSize(new Dimension(110, 110));
            b.setMaximumSize(new Dimension(110, 110));
            b.setVerticalTextPosition(SwingConstants.TOP);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            pChild.add(b);
        }
        for (int i=0; i<pChild.getComponentCount(); i++) {
            b = (JButton)pChild.getComponent(i);
            
            b.setBorderPainted(false);
            b.setBackground(Colors.gray);
            b.setOpaque(false);
        }
        
        scroll.setViewportView(
            Generals.surround(pChild, sizeScroll));
        scroll.setAlignmentY(CENTER_ALIGNMENT);
        
        panel.add(scroll);
        
        
        return panel;
    }
    
    private JPanelGlass getMainTab04() throws Exception {
        JPanelGlass panel;
        JScrollPane scroll;
        JPanelGlass pChild;
        JLabelGlass label;
        String[] ds;
        int idx;
        
        ds = DS.MAIN.panelTab04();
        idx = 0;
        
        panel = new JPanelGlass();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        pChild.add(bFabricsLoad = getSecondaryButton(ds[idx++]));
        pChild.add(Box.createRigidArea(new Dimension(0, 10)));
        pChild.add(bFabricsSortByValue = getSecondaryButton(ds[idx++]));
        pChild.add(Box.createRigidArea(new Dimension(0, 10)));
        pChild.add(bFabricsRemoveAll = getSecondaryButton(ds[idx++]));
        pChild.add(Box.createRigidArea(new Dimension(0, 10)));
        pChild.add(bFabricsPrint = getSecondaryButton(ds[idx++]));
        pChild.add(Box.createRigidArea(new Dimension(0, 30)));
        user_fabric_check = new JCheckBoxGlass(ds[idx++]);
        user_fabric_check.setFont(Fonts.checkBoxBigger());
        pChild.add(user_fabric_check);
        panel.add(pChild);

        
        panel.add(Box.createRigidArea(new Dimension(30, 0)));

        
        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        
        scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(450, 180));
        
        sort_fabrics_group = new JPanelGlass();
        
        scroll.setViewportView(
            Generals.surround(sort_fabrics_group, sizeScroll));
        scroll.setAlignmentY(CENTER_ALIGNMENT);
        
        pChild.add(scroll, BorderLayout.CENTER);
        
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.tabContent());
        pChild.add(Generals.surround(label, new Dimension(2, 2)),
                    BorderLayout.SOUTH);
        panel.add(pChild);
        

        return panel;
    }
    
    private JPanelGlass getMainTab05() throws Exception {
        JPanelGlass panel;
        JPanelGlass pChild, pNephew;
        JScrollPane scroll;
        JLabelGlass label;
        String[] ds;
        int idx;
        
        ds = DS.MAIN.panelTab05();
        idx = 0;
        
        panel = new JPanelGlass();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        
        pChild = new JPanelGlass();
        pChild.setLayout(new BoxLayout(pChild, BoxLayout.Y_AXIS));
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.tabContent());
        pChild.add(label);
        panel.add(pChild);

        
        panel.add(Box.createRigidArea(new Dimension(30, 0)));


        pChild = new JPanelGlass();
        pChild.setLayout(new BorderLayout());
        
        scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(450, 180));
        
        fabricColl_group = new JPanelGlass();
        fabricColl_group.setLayout(
            new BoxLayout(fabricColl_group, BoxLayout.Y_AXIS));
        
        scroll.setViewportView(
            Generals.surround(fabricColl_group, sizeScroll));
        scroll.setAlignmentY(CENTER_ALIGNMENT);
        
        pChild.add(scroll, BorderLayout.CENTER);

        pNephew = new JPanelGlass();
        pNephew.setLayout(new BoxLayout(pNephew, BoxLayout.X_AXIS));
        pNephew.add(bFabricCollImport = getSecondaryButton(ds[idx++]));
        pNephew.add(Box.createRigidArea(new Dimension(30, 0)));
        pNephew.add(bFabricCollRemove =
                        getSecondaryButton(ds[idx++], new Dimension(125, 25)));
        pChild.add(Generals.surround(pNephew, new Dimension(2, 7)),
                    BorderLayout.SOUTH);
        
        panel.add(pChild);
        
        
        return panel;
    }

    private JButtonColored getMainButton(
            String text, Image icon)
            throws Exception
    {
        return getMainButton(text, icon, sizeMainButton);
    }

    private JButtonColored getMainButton(
            String text, Image icon, Dimension size)
            throws Exception
    {
        JButtonColored button;

        button = new JButtonColored(text, icon, true);
        button.setFont(Fonts.button());
        button.setPreferredSize(size);
        button.setMaximumSize(size);

        return button;
    }
    
    private JButtonRounded getSecondaryButton(
            String text)
            throws Exception
    {
        return getSecondaryButton(text, sizeSecondaryButton);
    }
    
    private JButtonRounded getSecondaryButton(
            String text,
            Dimension size)
            throws Exception
    {
        JButtonRounded b;
        
        b = new JButtonRounded(text);
        
        b.setPreferredSize(size);
        b.setMaximumSize(size);
        
        return b;
    }

    private BufferedImage getLogoMiddle(int totalWidth) throws Exception {
        BufferedImage logoMiddle;
        Graphics2D g;
        int widthMiddle, heightMiddle;
        int counts;
        int contentWidth;

        contentWidth = totalWidth - getInsets().left - getInsets().right;
        
        widthMiddle = IMAGES.BIG_LOGO_B.getWidth();
        heightMiddle = IMAGES.BIG_LOGO_B.getHeight();
        logoMiddle = new BufferedImage(
                            contentWidth
                                - IMAGES.BIG_LOGO_A.getWidth()
                                - IMAGES.BIG_LOGO_C.getWidth(),
                            heightMiddle,
                            BufferedImage.TYPE_INT_ARGB);

        g = logoMiddle.createGraphics();
        counts = logoMiddle.getWidth() / widthMiddle;
        for (int i=0; i<counts; i++) {
            g.drawImage(IMAGES.BIG_LOGO_B, i*widthMiddle, 0, null);
        }
        g.drawImage(IMAGES.BIG_LOGO_B,
                    counts * widthMiddle, 0,
                    logoMiddle.getWidth() - counts * widthMiddle, heightMiddle,
                    null);

        return logoMiddle;
    }
    


    public @Override void dispose() {
        try {

        MainController.closeProtocol();

        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.fatalErrorsOccured(false);
        }
    }

    
    
    public void setOriginalImage(BufferedImage image) throws Exception {
        BufferedImage image2;
        int new_w, new_h;
        
        if (image == null) {
            image = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);
        }
        
        new_w = sizeViewportOrig.width - 10;
        new_h = sizeViewportOrig.height - 10;
        if (new_w*image.getHeight()/image.getWidth() < new_h) {
            image2 = Generals.scaleImage(image, new_w, new_w*image.getHeight()/image.getWidth(), true);
        }
        else {
            image2 = Generals.scaleImage(image, new_h * image.getWidth() / image.getHeight(), new_h, true);
        }
        original_box.setImage(image2);
        
        original_container.removeAll();
        original_container.add(
            Generals.surround(
                original_box,
                Generals.getPositionOfScaled(
                    image2, sizeViewportOrig.width, sizeViewportOrig.height)));

        if (withPack) {
            original_container.revalidate();
        }

        if (withPack) {
            setProcessedImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
        }
        
        setEnabledPrintButton(false);
    }
    
    public void setProcessedImage(BufferedImage image) throws Exception {
        BufferedImage image2;
        int new_w, new_h;
        boolean doEnablePrinting;
        
        doEnablePrinting = true;
        
        if (image == null) {
            image = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);
            doEnablePrinting = false;
        }
        
        new_w = sizeViewport.width - 10;
        new_h = sizeViewport.height - 10;
        if (new_w*image.getHeight()/image.getWidth() < new_h) {
            image2 = Generals.scaleImage(image, new_w, new_w * image.getHeight() / image.getWidth(), true);
        }
        else {
            image2 = Generals.scaleImage(image, new_h * image.getWidth() / image.getHeight(), new_h, true);
        }
        processed_box.setImage(image2);
        
        processed_container.removeAll();
        processed_container.add(
            Generals.surround(
                processed_box,
                Generals.getPositionOfScaled(
                    image2, sizeViewport.width, sizeViewport.height)));

        if (withPack) {
            processed_container.revalidate();
        }
        
        setEnabledPrintButton(doEnablePrinting);
    }
    
    private WindowStateListener stateListener;
    
    public void setEnabledProcessButton(boolean value) throws Exception {
        if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            value = true;
        }
        
        if (value) {
            process_button.setForeground(Colors.red);
        }
        else {
            process_button.setForeground(Colors.label_text);
        }
        
        process_button.setEnabled(value);
        
        if (value) {
            QuiltedPhoto.setChangesMade(true);
        }
    }

    public void setEnabledRestoreButton(boolean value) throws Exception {
        restore_original_button.setEnabled(value);
    }

    public void setEnabledPrintButton(boolean value) throws Exception {
        if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            value = true;
        }
        
        bPrintPattern.setEnabled(value);
    }
    
    public void setParameters(Object[] data) throws Exception {
        int idx;
        int n;
        boolean b;
        
        idx = 0;
        
        setOriginalImage((BufferedImage)data[idx++]);
        
        b = (Boolean)data[idx++];
        use_qbnpa_check.setSelected(false);
        if (b) {
            use_qbnpa_check.doClick();
        }
        
        b = (Boolean)data[idx++];
        use_cfqp_check.setSelected(false);
        if (b) {
            use_cfqp_check.doClick();
        }
        
        fabric_slider.setValue(fabric_slider.getMinimum());
        fabric_slider.setValue((Integer)data[idx++]);
        
        detail_slider.setValue(detail_slider.getMinimum());
        detail_slider.setValue((Integer)data[idx++]);
        
        n = (Integer)data[idx++];
        ((JRadioButton)angle_group.getComponent(n)).setSelected(false);
        ((JRadioButton)angle_group.getComponent(n)).doClick();
        
        shape_smoothing_slider.setValue(shape_smoothing_slider.getMinimum());
        shape_smoothing_slider.setValue((Integer)data[idx++]);
        
        b = (Boolean)data[idx++];
        gray_radio.setSelected(false);
        if (b) {
            gray_radio.doClick();
        }
        
        b = (Boolean)data[idx++];
        sepia_radio.setSelected(false);
        if (b) {
            sepia_radio.doClick();
        }
        
        b = (Boolean)data[idx++];
        fabric_radio.setSelected(false);
        if (b) {
            fabric_radio.doClick();
        }
        
        color_radio.setSelected(false);
        if (!fabric_radio.isSelected()
                && !gray_radio.isSelected()
                && !sepia_radio.isSelected())
        {
            color_radio.doClick();
        }
        
        b = (Boolean)data[idx++];
        user_fabric_check.setSelected(false);
        if (b) {
            user_fabric_check.doClick();
        }
    }
    
    public void setPreProcessedData(Object[] data) throws Exception {
        fabric_slider.setValue(fabric_slider.getMinimum());
        fabric_slider.setValue((Integer)data[0]);
    }
    
    public void setPostProcessedData(Object[] data) throws Exception {
        detail_output.setText(DS.MAIN.output_detail((Integer)data[0]));
        quantity_output.setText(DS.MAIN.output_fabric((Integer)data[1]));
    }
    
    public void setPatternSelected(int index, boolean value) throws Exception {
        JButton b;
        
        b = (JButton)patterns_scroll.getComponent(index);
        
        b.setBorderPainted(value);
        b.setOpaque(value);
        b.setBackground(Colors.yellowish);
    }

    public void setEnabledUserFabricsCheck(boolean value) throws Exception {
        user_fabric_check.setEnabled(value);
    }

    public void updateFabrics(ArrayList<BufferedImage> images) throws Exception {
        int nx, ny;
        
        ny = 4;
        nx = images.size() / ny;
        
        sort_fabrics_group.setLayout(new GridLayout(nx, ny, 5, 5));
        
        sort_fabrics_group.removeAll();
        for (int i=0; i<images.size(); i++) {
            sort_fabrics_group.add(new JPanelIcon(images.get(i)));
        }
        
        sort_fabrics_group.repaint();
        pack();
    }

    public void setFabricCollections(ArrayList<String> names) throws Exception {
        JRadioButtonGlass[] buttons;
        
        fabricColl_group.removeAll();
        
        buttons = new JRadioButtonGlass[names.size()];
        for (int i=0; i<buttons.length; i++) {
            buttons[i] = new JRadioButtonGlass(names.get(i), i);
            
            buttons[i].addActionListener(processListener);

            buttons[i].addActionListener(fabricCollActionListener);
            
            buttons[i].setSelected(false);
            
            fabricColl_group.add(buttons[i]);
        }
        
        fabricColl_group.repaint();
        pack();
    }

    public void selectFabricCollection(int index) throws Exception {
        ((JRadioButtonGlass)fabricColl_group.getComponent(index))
                .doClick();
    }
    
    public JComponent getRefElementForProgressBar() throws Exception {
        return processed_container;
    }
    
    public void maximize(boolean doMaximize) throws Exception {
        int totalWidth;
        int width, height;

        
        width = sizeViewportOrig.width;
        height = sizeViewportOrig.height;
        
        original_container.setMaximumSize(
                new Dimension(width, height));
        original_container.setPreferredSize(
                new Dimension(width, height));

        
        if (doMaximize) {
            Dimension dMax = GraphicsEnvironment
                                .getLocalGraphicsEnvironment()
                                    .getMaximumWindowBounds().getSize();
            totalWidth = dMax.width + getInsets().left + getInsets().right;
            width = (dMax.width - sizeEditor.width - sizeViewportOrig.width - 130);
            height = (dMax.height - 500);
        }
        else {
            totalWidth = getMinimumSize().width;
            width = (getMinimumSize().width - sizeEditor.width - sizeViewportOrig.width - 130);
            height = sizeEditor.height;
        }
        if (!mainTabs.isVisible()) {
            height += mainTabs.getHeight();
        }
        sizeViewport = new Dimension(width, height);
        

        pBigLogo.setMaximumSize(new Dimension(
                        totalWidth,
                        IMAGES.BIG_LOGO_A.getHeight()));
        pBigLogo.setPreferredSize(new Dimension(
                        totalWidth,
                        IMAGES.BIG_LOGO_A.getHeight()));

        lBigLogoLeft.setIcon(new ImageIcon(IMAGES.BIG_LOGO_A));
        lBigLogoMiddle.setIcon(new ImageIcon(getLogoMiddle(totalWidth)));
        lBigLogoRight.setIcon(new ImageIcon(IMAGES.BIG_LOGO_C));
        

        processed_container.setMaximumSize(
                new Dimension(width, height));
        processed_container.setPreferredSize(
                new Dimension(width, height));

        
        withPack = false;
        setOriginalImage(QuiltedPhoto.getOriginalImage());
        setProcessedImage(QuiltedPhoto.getProcessedImage());
        withPack = true;
    }
    
    public void test() throws Exception {
    }
    


    public void setShaderVisible(boolean value) throws Exception {
        pShader.setVisible(value);
    }

    private int getTabIndex(String title) throws Exception {
        int idx;

        idx = -1;
        for (int i=0; i<mainTabs.getTabCount(); i++) {
            if (title.equals(mainTabs.getTitleAt(i))) {
                idx = i;
            }
        }
        
        return idx;
    }

    private void setMaximumDetail(int value) throws Exception {
        detail_slider.setMaximum(value);

        ((SpinnerNumberModel)(spDetail.getModel())).setMaximum(value);
    }
    
    private void setCallbacks() throws Exception {

        /////////////////////////////////////////////////////////////
        ///  key events
        /////////////////////////////////////////////////////////////

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher()
        {
            public @Override boolean dispatchKeyEvent(KeyEvent ev) {
                try {

                if (!Window.this.isShowing()) {
                    return false;
                }

                if (!Window.this.isFocused()) {
                    return false;
                }

                if (ev.getID() == KeyEvent.KEY_PRESSED) {
                    switch (ev.getKeyCode()) {
                        case KeyEvent.VK_F5:
                            process_button.doClick();
                            break;
                        
                        case KeyEvent.VK_N:
                            if (!CNT.AS_FOR_RELEASE) {
                                QuiltedPhoto.setNextPattern();
                            }
                            break;

                        default:
                            break;
                    }
                }

                return false;

                }
                catch (Exception e) { Logger.printErr(e); return false; }
            }
        });



        /////////////////////////////////////////////////////////////
        ///  window events
        /////////////////////////////////////////////////////////////

        stateListener = new WindowStateListener() {
            public @Override void windowStateChanged(WindowEvent ev) {
                try {

                if (ev.getNewState() == Frame.MAXIMIZED_BOTH) {
                    maximized = true;
                }
                else {
                    maximized = false;
                }
                maximize(maximized);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
        this.addWindowStateListener(stateListener);

        this.addWindowListener(new WindowAdapter() {
            public @Override void windowOpened(WindowEvent ev) {
                try {

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        /////////////////////////////////////////////////////////////
        ///  menu
        /////////////////////////////////////////////////////////////

        menu.fileNew.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                    QuiltedPhoto.remove_all_fabrics();
                }
                else {
                    QuiltedPhoto.new_file(true);
                }

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.fileOpen.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.load_file();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.fileSave.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.save_file();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.fileSaveAs.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.save_as();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.fileExportToJpg.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.setPrintToJpg();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.filePageSetup.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.page_setup();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.filePrintPreview.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.print();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.filePrint.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.page_setup();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.fileExit.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                Window.this.dispose();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.viewTab1.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                mainTabs.setSelectedIndex(getTabIndex(menu.viewTab1.getText()));

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.viewTab2.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                mainTabs.setSelectedIndex(getTabIndex(menu.viewTab2.getText()));

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.viewTab3.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                mainTabs.setSelectedIndex(getTabIndex(menu.viewTab3.getText()));

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.viewTab4.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                mainTabs.setSelectedIndex(getTabIndex(menu.viewTab4.getText()));

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.viewTab5.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                mainTabs.setSelectedIndex(getTabIndex(menu.viewTab5.getText()));

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.helpOnline.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                bHelp.doClick();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        menu.helpAbout.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                new AboutDialog().setVisible(true);

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        /////////////////////////////////////////////////////////////
        ///  main controls
        /////////////////////////////////////////////////////////////

        bNew.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                    QuiltedPhoto.remove_all_fabrics();
                }
                else {
                    QuiltedPhoto.new_file(true);
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bOpen.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.load_file();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bSavePattern.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.save_file();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bPrintPattern.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                    QuiltedPhoto.print_fabrics();
                }
                else {
                    QuiltedPhoto.page_setup();
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bSelectPhoto.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                    QuiltedPhoto.choose_fabric();
                }
                else {
                    QuiltedPhoto.select_photo();
                }
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        process_button.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                    QuiltedPhoto.sort_fabrics_by_value();
                }
                else {
                    QuiltedPhoto.restore_original_palette(false);
                    QuiltedPhoto.process_image();
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bViewPalette.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.view_palette();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bAdjustColors.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.adjustColors();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bHelp.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.online_help();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bSelectPalette.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.select_palette();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        restore_original_button.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.restore_original_cb();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        original_box.addMouseListener(new MouseAdapter() {
            public @Override void mouseClicked(MouseEvent ev) {
                try {

                setEnabledProcessButton(true);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
            
            public @Override void mouseReleased(MouseEvent ev) {
                try {

                QuiltedPhoto.cropImage(original_box.getCrop());
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        detail_slider.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                lDetails.setText(String.format(FORMAT.SLIDER, detail_slider.getValue()));
                QuiltedPhoto.setNumXTiles(detail_slider.getValue());
                
                if (mutualLinkOn) {
                    mutualLinkOn = false;
                    spDetail.setValue(detail_slider.getValue());
                    mutualLinkOn = true;
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        spDetail.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {
                
                if (mutualLinkOn) {
                    mutualLinkOn = false;
                    detail_slider.setValue((Integer)spDetail.getValue());
                    mutualLinkOn = true;
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        fabric_slider.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                lFabrics.setText(
                    String.format(FORMAT.SLIDER, fabric_slider.getValue()));
                QuiltedPhoto.setNumColors(fabric_slider.getValue());
                
                if (mutualLinkOn) {
                    mutualLinkOn = false;
                    spFabrics.setValue(fabric_slider.getValue());
                    mutualLinkOn = true;
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        spFabrics.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {
                
                if (mutualLinkOn) {
                    mutualLinkOn = false;
                    fabric_slider.setValue((Integer)spFabrics.getValue());
                    mutualLinkOn = true;
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        class RestrictKeyManager extends KeyManager {

            private String validChars;
            private int maxChars;

            public RestrictKeyManager(int maxChars) throws Exception {
                super();

                validChars = FORMAT.SPINNER_CHARS;
                this.maxChars = maxChars;
            }

            public RestrictKeyManager(
                    String validChars, int maxChars)
                    throws Exception
            {
                super();

                this.validChars = validChars;
                this.maxChars = maxChars;
            }

            public @Override boolean typed(
                    int keyCode, char key, Object source)
                    throws Exception
            {
                if (source instanceof JTextField) {
                    int d;

                    d = ((JTextField)source).getText().length();

                    if (d+1 > maxChars && maxChars > 0) {
                        return true;
                    }
                }

                if (validChars.indexOf(key) == -1) {
                    return true;
                }

                return false;
            }

        }

        ((JSpinner.NumberEditor)spDetail.getEditor()).getTextField()
                .addKeyListener(new RestrictKeyManager(3));

        ((JSpinner.NumberEditor)spFabrics.getEditor()).getTextField()
                .addKeyListener(new RestrictKeyManager(3));
        
        
        
        /////////////////////////////////////////////////////////////
        ///  main tabs
        /////////////////////////////////////////////////////////////

        bMinimizeTabs.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                mainTabs.setVisible(false);
                maximize(maximized);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bMaximizeTabs.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                mainTabs.setVisible(true);
                maximize(maximized);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        color_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                color_radio.setSelected(true);
                gray_radio.setSelected(false);
                sepia_radio.setSelected(false);
                fabric_radio.setSelected(false);

                QuiltedPhoto.setColorPalette(false, false, false);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        gray_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                color_radio.setSelected(false);
                gray_radio.setSelected(true);
                sepia_radio.setSelected(false);
                fabric_radio.setSelected(false);

                QuiltedPhoto.setColorPalette(true, false, false);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        sepia_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                color_radio.setSelected(false);
                gray_radio.setSelected(false);
                sepia_radio.setSelected(true);
                fabric_radio.setSelected(false);

                QuiltedPhoto.setColorPalette(false, true, false);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        fabric_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                color_radio.setSelected(false);
                gray_radio.setSelected(false);
                sepia_radio.setSelected(false);
                fabric_radio.setSelected(true);

                QuiltedPhoto.setColorPalette(false, false, true);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        angle_straight_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                angle_straight_radio.setSelected(true);
                angle_30_radio.setSelected(false);
                angle_45_radio.setSelected(false);

                QuiltedPhoto.setGridAngle(0);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        angle_30_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                angle_straight_radio.setSelected(false);
                angle_30_radio.setSelected(true);
                angle_45_radio.setSelected(false);

                QuiltedPhoto.setGridAngle(1);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        angle_45_radio.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                angle_straight_radio.setSelected(false);
                angle_30_radio.setSelected(false);
                angle_45_radio.setSelected(true);

                QuiltedPhoto.setGridAngle(2);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        for (int i=0; i<patterns_scroll.getComponentCount(); i++) {
            ((JButton)patterns_scroll.getComponent(i))
                .addActionListener(new ActionListener() {
                    public @Override void actionPerformed(ActionEvent ev) {
                        try {

                        JButton b;
                            
                        b = (JButton)ev.getSource();
                        QuiltedPhoto.pattern_button_cb(b.getName());

                        int def;
                        def = 100;
                        int[] maximums = new int[] {
                            def, def, def-10, def, def,
                            def+30, def+30, def, def, def+30,
                            def+30, def+30, def-15, def, def+30,
                            def+30, def+30, def+30, def+30
                        };
                        
                        if (!use_cfqp_check.isSelected()) {
                            setMaximumDetail(
                                maximums[QuiltedPhoto.getPatternIndex()]);
                        }
                        
                        oldDetailValue = detail_slider.getMaximum();

                        }
                        catch (Exception e) { Logger.printErr(e); }
                    }
                });
        }

        
        
        use_qbnpa_check.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                QuiltedPhoto.setUseQbnpa(use_qbnpa_check.isSelected());
                
                if (use_qbnpa_check.isSelected()) {
                    use_cfqp_check.setSelected(false);
                }

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        shape_smoothing_slider.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                lUseQbnpa.setText(
                    String.format(FORMAT.SLIDER, shape_smoothing_slider.getValue()));
                QuiltedPhoto.setShapeSmoothing(shape_smoothing_slider.getValue());
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        use_cfqp_check.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                QuiltedPhoto.setUseCfqp(use_cfqp_check.isSelected());
                
                if (use_cfqp_check.isSelected()) {
                    use_qbnpa_check.setSelected(false);
                }

                if (use_cfqp_check.isSelected()) {
                    setMaximumDetail(50);
                }
                else {
                    setMaximumDetail(oldDetailValue);
                }
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        
        
        bFabricsLoad.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.choose_fabric();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bFabricsSortByValue.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.sort_fabrics_by_value();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bFabricsRemoveAll.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.remove_all_fabrics();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        bFabricsPrint.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {
                    
                QuiltedPhoto.print_fabrics();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        user_fabric_check.addChangeListener(new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                QuiltedPhoto.setUseUserFabric(user_fabric_check.isSelected());

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        

        bFabricCollImport.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.import_fabric_collection();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bFabricCollRemove.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                QuiltedPhoto.remove_fabric_collection();
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        
        
        processListener = new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                setEnabledProcessButton(true);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
        
        bSelectPhoto.addActionListener(processListener);
        bAdjustColors.addActionListener(processListener);
        restore_original_button.addActionListener(processListener);

        for (int i=0; i<patterns_scroll.getComponentCount(); i++) {
            ((JButton)patterns_scroll.getComponent(i))
                .addActionListener(processListener);
        }
        
        color_radio.addActionListener(processListener);
        gray_radio.addActionListener(processListener);
        sepia_radio.addActionListener(processListener);
        fabric_radio.addActionListener(processListener);
        angle_straight_radio.addActionListener(processListener);
        angle_30_radio.addActionListener(processListener);
        angle_45_radio.addActionListener(processListener);
        
        use_qbnpa_check.addActionListener(processListener);
        use_cfqp_check.addActionListener(processListener);
        user_fabric_check.addActionListener(processListener);
        
        bFabricsLoad.addActionListener(processListener);
        bFabricsSortByValue.addActionListener(processListener);

        
        ChangeListener processChangeListener;
        
        processChangeListener = new ChangeListener() {
            public @Override void stateChanged(ChangeEvent ev) {
                try {

                setEnabledProcessButton(true);
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
        
        detail_slider.addChangeListener(processChangeListener);
        fabric_slider.addChangeListener(processChangeListener);
        shape_smoothing_slider.addChangeListener(processChangeListener);

        
        fabricCollActionListener = new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                for (int i=0; i<fabricColl_group.getComponentCount(); i++) {
                    ((JRadioButtonGlass)fabricColl_group.getComponent(i))
                        .setSelected(false);
                }
                ((JRadioButtonGlass)ev.getSource()).setSelected(true);
                    
                QuiltedPhoto.load_fabrics_cb(
                    (Integer)((JRadioButtonGlass)ev.getSource()).getTag());
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
        
        

    }





}
