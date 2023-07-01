package qp.design;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import qp.CNT;
import qp.DS;
import qp.Exceptions;
import qp.Logger;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class WindowMenu extends JMenuBar {

    JMenu fileMenu;
    JMenuItem fileNew, fileOpen,
                fileSave, fileSaveAs, fileExportToJpg,
                filePageSetup, filePrintPreview, filePrint,
                fileExit;

    JMenu viewMenu;
    JMenuItem viewTab1, viewTab2, viewTab3, viewTab4, viewTab5;

    JMenu helpMenu;
    JMenuItem helpOnline, helpAbout;



    WindowMenu() throws Exception {
        String[] ds;
        char[] ds_mnm;
        int idx;


        setBackground(Colors.white);

        idx = 0;
        ds = DS.MAIN.menuStr();
        ds_mnm = DS.MAIN.menuMnm();


        fileMenu = new JMenu(ds[idx]);
        fileMenu.setMnemonic(ds_mnm[idx++]);
        add(fileMenu);

        fileMenu.add(fileNew = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileNew.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, Event.CTRL_MASK));
        fileMenu.addSeparator();
        fileMenu.add(fileOpen = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, Event.CTRL_MASK));
        fileMenu.addSeparator();
        fileMenu.add(fileSave = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Event.CTRL_MASK));
        fileMenu.add(fileSaveAs = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileSaveAs.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
        fileMenu.add(fileExportToJpg = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileMenu.addSeparator();
        fileMenu.add(filePageSetup = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileMenu.add(filePrintPreview = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileMenu.add(filePrint = new JMenuItem(ds[idx], ds_mnm[idx++]));
        filePrint.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, Event.CTRL_MASK));
        fileMenu.addSeparator();
        fileMenu.add(fileExit = new JMenuItem(ds[idx], ds_mnm[idx++]));
        fileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, Event.ALT_MASK));
        for (int i=0; i<fileMenu.getItemCount(); i++) {
            if (fileMenu.getItem(i) instanceof JMenuItem) {
                fileMenu.getItem(i).setBorderPainted(false);
            }
        }


        viewMenu = new JMenu(ds[idx]);
        viewMenu.setMnemonic(ds_mnm[idx++]);
        add(viewMenu);

        viewMenu.add(viewTab1 = new JMenuItem(ds[idx], ds_mnm[idx++]));
        viewMenu.add(viewTab2 = new JMenuItem(ds[idx], ds_mnm[idx++]));
        viewMenu.add(viewTab3 = new JMenuItem(ds[idx], ds_mnm[idx++]));
        viewMenu.add(viewTab4 = new JMenuItem(ds[idx], ds_mnm[idx++]));
        viewMenu.add(viewTab5 = new JMenuItem(ds[idx], ds_mnm[idx++]));


        helpMenu = new JMenu(ds[idx]);
        helpMenu.setMnemonic(ds_mnm[idx++]);
        add(helpMenu);

        helpMenu.add(helpOnline = new JMenuItem(ds[idx], ds_mnm[idx++]));
        
        helpMenu.addSeparator();

        helpMenu.add(helpAbout = new JMenuItem(ds[idx], ds_mnm[idx++]));

        for (int i=0; i<helpMenu.getItemCount(); i++) {
            if (helpMenu.getItem(i) instanceof JMenuItem) {
                helpMenu.getItem(i).setBorderPainted(false);
            }
        }
        
        
        
        if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
            viewTab2.setVisible(false);
            viewTab3.setVisible(false);
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            viewTab1.setVisible(false);
            viewTab2.setVisible(false);
            viewTab3.setVisible(false);
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            fileSave.setVisible(false);
            fileSaveAs.setVisible(false);
            fileExportToJpg.setVisible(false);
            filePrintPreview.setVisible(false);
            viewMenu.setVisible(false);
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            viewTab1.setVisible(false);
            viewTab3.setVisible(false);
        }
        else {
            throw Exceptions.badIfBranch(CNT.product);
        }



        setCallbacks();
    }



    private void setCallbacks() throws Exception {
        MouseListener menuMouseListener;
        menuMouseListener = new MouseAdapter() {
            public @Override void mouseEntered(MouseEvent ev) {
                try {

                setVisible(false);
                setBackground(Colors.white);
                setVisible(true);

                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseExited(MouseEvent ev) {
                try {

                setBackground(Colors.white);

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };

        fileMenu.addMouseListener(menuMouseListener);
        viewMenu.addMouseListener(menuMouseListener);
        helpMenu.addMouseListener(menuMouseListener);
    }





}
