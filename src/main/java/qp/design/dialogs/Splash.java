package qp.design.dialogs;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import qp.CNT.IMAGES;
import qp.DS;
import qp.control.MainController;
import qp.design.components.JPanelIcon;


/**
 *
 * @author Maira57
 */
public class Splash extends JDialog {



    public Splash() throws Exception {
        super(MainController.getMainParent(), DS.MAIN.getTitle(), false);


        setAlwaysOnTop(true);
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        getContentPane().add(new JPanelIcon(IMAGES.SPLASH()));
        pack();
        setLocationRelativeTo(MainController.getMainParent());


        // functionality
        setCallbacks();
    }



    private void setCallbacks() throws Exception {

    }




}
