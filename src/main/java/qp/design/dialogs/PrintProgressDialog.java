package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import qp.DS;
import qp.control.MainController;
import qp.design.Generals;
import qp.design.components.JLabelGlass;
import qp.design.components.JPanelGlass;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class PrintProgressDialog extends JDialog {

    private JProgressBar progressBar;
            
            
            
    public PrintProgressDialog() throws Exception {
        super(MainController.getMainParent(), DS.PRINT_PROGRESS_DIALOG.getTitle(), true);
        
        JPanelGlass panel;
        JLabelGlass label;
        String[] ds;
        int idx;
        
        ds = DS.PRINT_PROGRESS_DIALOG.str();
        idx = 0;
         
        panel = new JPanelGlass();
        panel.setLayout(new BorderLayout());
                
        label = new JLabelGlass(ds[idx++]);
        label.setFont(Fonts.progress());
        panel.add(label, BorderLayout.NORTH);
                
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(500, 30));
        progressBar.setMaximumSize(new Dimension(500, 30));
        panel.add(progressBar, BorderLayout.CENTER);
                
        getContentPane().setBackground(Colors.white);
        getContentPane().add(Generals.surround(panel, new Dimension(10, 10)));
        
        setUndecorated(true);
        setLocationRelativeTo(MainController.getMainParent());
        pack();
    }
    
    
    
    public void setValue(int value) throws Exception {
        progressBar.setValue(value);
        Thread.sleep(10);
    }

    
    
    
    
}
