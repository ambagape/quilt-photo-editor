package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.net.URL;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import qp.CNT.FORMAT;
import qp.CNT.FORMAT_COMMUNICATION;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.design.Generals;
import qp.design.components.JPanelGlass;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class AboutDialog extends JDialog {

    private JEditorPane lMessage;
    private JButton bOk;

        
    
    public AboutDialog() throws Exception {
        super(MainController.getMainParent(), DS.ABOUT_DIALOG.getTitle(), true);

        setResizable(false);
        
        JPanelGlass panel, pChild;

        panel = new JPanelGlass(430, 280);
        panel.setLayout(new BorderLayout());
        
        pChild = new JPanelGlass();
        pChild.add(Box.createRigidArea(new Dimension(350, 0)));
        pChild.add(bOk = new JButton(DS.ABOUT_DIALOG.str()[0]));
        panel.add(pChild, BorderLayout.NORTH);
        
        pChild = new JPanelGlass();
        lMessage = new JEditorPane(FORMAT.HTML, DS.ABOUT_DIALOG.getText());
        lMessage.setFont(Fonts.label());
        lMessage.setEditable(false);
        lMessage.setOpaque(false);
        
        pChild.add(Generals.surround(lMessage, new Dimension(20, 0)));
        panel.add(pChild, BorderLayout.CENTER);
        
        getContentPane().add(panel);
        getContentPane().setBackground(Colors.white);
        pack();
        
        
        setLocationRelativeTo(MainController.getMainParent());
        
        
        setCallbacks();
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

        lMessage.addHyperlinkListener(new HyperlinkListener() {
            public @Override void hyperlinkUpdate(HyperlinkEvent ev) {
                try {

                JEditorPane source;
                HyperlinkEvent.EventType eventType;
                URL url;
                String description;
                String s;
                
                source = (JEditorPane)ev.getSource();
                eventType = ev.getEventType();
                url = ev.getURL();
                description = ev.getDescription();
                

                if (url == null) {
                    s = description;
                }
                else {
                    s = url.toString();
                }

                if (eventType.equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    URI uri;

                    if (s.startsWith(FORMAT_COMMUNICATION.HTTP_HEADER)) {
                        uri = new URI(s);
                    }
                    else {
                        uri = new File(s).toURI();
                    }

                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(uri);
                    }
                }
                else if (eventType.equals(HyperlinkEvent.EventType.ENTERED)) {
                    source.setToolTipText(s);
                }
                else if (eventType.equals(HyperlinkEvent.EventType.EXITED)) {
                    source.setToolTipText(FORMAT.STR_NULL);
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
        bOk.addActionListener(new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                AboutDialog.this.dispose();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }
    
    
    
    
    
}
