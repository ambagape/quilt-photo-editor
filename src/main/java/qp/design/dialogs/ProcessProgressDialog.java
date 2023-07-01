package qp.design.dialogs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import qp.DS;
import qp.Logger;
import qp.control.MainController;
import qp.design.components.JPanelGlass;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class ProcessProgressDialog extends JDialog {

    private JProgressBar progressBar;
    
    

    private Timer timer;
    private Color colorProgress;
    private boolean isLightColored;
            
            
            
    public ProcessProgressDialog() throws Exception {
        super(MainController.getMainParent(), DS.PROCESS_PROGRESS_DIALOG.getTitle(), true);
        
        JPanelGlass panel;
         
        panel = new JPanelGlass();
        panel.setLayout(new BorderLayout());
                
        progressBar = new JProgressBar() {
            public @Override void paintComponent(Graphics g) {
                super.paintComponent(g);

                try {
                    
                g.setColor(Colors.white);
                g.drawString(DS.PROCESS_PROGRESS_DIALOG.str()[0], 65, 30);
                
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        };
        progressBar.setFont(Fonts.progress());
        progressBar.setForeground(Colors.yellowish);
        progressBar.setBackground(Colors.gray_dark);
        progressBar.setPreferredSize(new Dimension(200, 50));
        progressBar.setMaximumSize(new Dimension(200, 50));
        panel.add(progressBar, BorderLayout.CENTER);
                
        getContentPane().add(panel);
        
        setUndecorated(true);
        setLocation(610, 180);
        pack();
        
        
        setCallbacks();

        timer.start();
    }
    
    
    
    public void setValue(int value) throws Exception {
        progressBar.setValue(value);
        Thread.sleep(10);
    }
    
    
    
    private void setCallbacks() throws Exception {

        timer = new Timer(1 * 500, new ActionListener() {
            public @Override void actionPerformed(ActionEvent ev) {
                try {

                if (isLightColored) {
                    colorProgress = Colors.yellowish;
                }
                else {
                    colorProgress = Colors.yellowish2;
                }
                progressBar.setForeground(colorProgress);
                isLightColored = !isLightColored;
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
                
        timer.setRepeats(true);

        
        addWindowListener(new WindowAdapter() {
            public @Override void windowClosing(WindowEvent ev) {
                try {

                if (timer != null) {
                    if (timer.isRunning()) {
                        timer.stop();
                    }
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void windowClosed(WindowEvent ev) {
                try {

                if (timer != null) {
                    if (timer.isRunning()) {
                        timer.stop();
                    }
                }
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
        });
        
    }

    
    
    
    
}
