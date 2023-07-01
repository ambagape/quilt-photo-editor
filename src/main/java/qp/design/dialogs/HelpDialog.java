package qp.design.dialogs;
import chrriis.dj.nativeswing.swtimpl.NSPanelComponent;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.design.components.JPanelGlass;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class HelpDialog extends JFrame {
    
    private JWebBrowser webBrowser;

        
    
    public HelpDialog() throws Exception {
        super(DS.HELP_DIALOG.getTitle());

        
        JPanelGlass panel;

        panel = new JPanelGlass(1000, 600);
        panel.setLayout(new BorderLayout());
        
        webBrowser = new JWebBrowser(
                        NSPanelComponent.destroyOnFinalization());
        webBrowser.setBarsVisible(false);
        webBrowser.setStatusBarVisible(true);

        panel.add(webBrowser, BorderLayout.CENTER);
        
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
        
                webBrowser.navigate(DS.MAIN.helpLink());
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }
    
    
    
    
    
}
