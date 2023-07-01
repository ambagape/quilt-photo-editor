package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import qp.CNT;
import qp.CNT.FORMAT;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.design.Generals;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class LinkDialog extends JDialog {

    private JPanel panel;
    private JEditorPane pane;
    private JButton bOk;



    public LinkDialog(String title, String text, Image icon) throws Exception {
        super(MainController.getMainParent(), title, true);

        JPanel pChild;

        setIconImage(icon);
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        pChild = new JPanel();

        pane = new JEditorPane(FORMAT.HTML, text);
        pane.setFont(Fonts.label());
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        pChild.add(Generals.surround(pane, new Dimension(5, 5)));
        panel.add(pChild, BorderLayout.NORTH);

        panel.add(Box.createRigidArea(new Dimension(0, 10)),
                    BorderLayout.CENTER);

        bOk = new JButton(DS.LINK_DIALOG.str()[0]);
        panel.add(Generals.surround(bOk, new Dimension(350, 0)),
                    BorderLayout.SOUTH);

        getContentPane().add(Generals.surround(panel, new Dimension(10, 10)));
        pack();
        setLocationRelativeTo(MainController.getMainParent());


        ToolTipManager.sharedInstance().setInitialDelay(CNT.SECOND);


        setCallbacks();
    }



    public static HyperlinkListener getHyperlinkListener() throws Exception {
        return new HyperlinkListener() {
            public @Override void hyperlinkUpdate(HyperlinkEvent ev) {
                try {

                EventType evType;
                String s;

                evType = ev.getEventType();
                if (ev.getURL() == null) {
                    s = ev.getDescription();
                }
                else {
                    s = ev.getURL().toString();
                }

                if (evType.equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (s.startsWith(FORMAT.HTTP_HEADER)) {
                            desktop.browse(new URI(s));
                        }
                        else {
                            desktop.browse(new File(s).toURI());
                        }
                    }
                }
                else if(evType.equals(HyperlinkEvent.EventType.ENTERED)) {
                    ((JComponent)ev.getSource()).setToolTipText(s);
                }
                else if(evType.equals(HyperlinkEvent.EventType.EXITED)) {
                    ((JComponent)ev.getSource()).setToolTipText(FORMAT.STR_NULL);
                }

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
    }

    private void setCallbacks() throws Exception {

        addWindowListener(new WindowAdapter() {
            public @Override void windowOpened(WindowEvent ev) {
                try {

                bOk.requestFocusInWindow();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

        pane.addHyperlinkListener(getHyperlinkListener());

        bOk.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                LinkDialog.this.dispose();

                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });

    }





}
