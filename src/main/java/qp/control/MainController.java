package qp.control;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import qp.CNT;
import qp.CNT.FORMAT;
import qp.CNT.PATHS;
import qp.DS;
import qp.Exceptions;
import qp.Logger;
import qp.database.Base64;
import qp.database.CommunicationLocal;
import qp.database.Patterns;
import qp.design.Messenger;
import qp.design.Messenger.MSG;
import qp.design.Window;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;
import qp.design.dialogs.Splash;


/**
 *
 * Main operator of the application (the 'brain' of the application).
 *
 * @author Maira57
 */
public class MainController {

    private static Window mainWindow;
    
    private static Splash splashDialog;



    public static void start() throws Exception {
        /// check if trial mode still active
        if (CNT.version.equals(CNT.VERSION_TYPE.TRIAL)) {
            if (!licenseCheckPassed()) {
                Messenger.show(MSG.APP_TRIAL_EXPIRED);
                System.exit(0);
            }
        }
        else if (CNT.version.equals(CNT.VERSION_TYPE.BETA)) {
            if (!licenseCheckPassed()) {
                Messenger.show(MSG.APP_BETA_EXPIRED);
                System.exit(0);
            }
        }

        
        /// display introducing splash image
        splashDialog = new Splash();
        splashDialog.setVisible(true);

        
        /// initialize enbedded web browser
        NativeInterface.open();

        
        SwingUtilities.invokeLater(new Runnable() {
        public @Override void run() {
            try {

            /// initialize variables
            Fonts.initializeUI();
            Colors.initializeUI();

            Patterns.initialize(false);
            Patterns.initialize(true);
            Patterns.initializeCfqp();


            /// main window creation
            if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
                mainWindow = new Window(950, 840);
            }
            else {
                mainWindow = new Window(950, 760);
            }


            /// settings after main window creation
            mainWindow.initialize();
            QuiltedPhoto.startup();
            Messenger.setDefaultParent(mainWindow);


            // start application visually
            mainWindow.setVisible(true);

            splashDialog.dispose();





            QuiltedPhoto.test();

            if (!CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                if (!CNT.AS_FOR_RELEASE) {
//                    QuiltedPhoto.load_file(CNT.openPathDefault + "aqua.qpd");
//                    QuiltedPhoto.load_file(CNT.openPathDefault + "pink_rose.qpd");
//                    QuiltedPhoto.load_file(CNT.openPathDefault + "mona_lisa.qpd", true);
                    QuiltedPhoto.load_file(DS.startFile(), true);
                    mainWindow.test();
                }
                else {
                    QuiltedPhoto.load_file(DS.startFile(), true);
                    QuiltedPhoto.setFilename(new String());
                }
            }
            
            }
            catch (Exception e) {
                Logger.printErr(e);
                System.exit(0);
            }
        }
        });

        
        /// start embedded web browser
        NativeInterface.runEventPump();
    }

    public static boolean stop() {
        try {

        if (mainWindow != null) {
            mainWindow.dispose();
        }
        
        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }



    public static Window getMainParent() throws Exception {
        return mainWindow;
    }

    public static void fatalErrorsOccured(boolean setWindowInvisible) {
        try {

        if (setWindowInvisible) {
            if (mainWindow != null) {
                mainWindow.setVisible(false);
            }
        }

        if (mainWindow != null) {
            Messenger.show(mainWindow, MSG.APP_MAJOR_ERRORS);
        }
        else {
            Messenger.show(null, MSG.APP_MAJOR_ERRORS);
        }

        MainController.stop();

        System.exit(0);

        }
        catch (Exception e) {
            Logger.printErr(e);
            System.exit(0);
        }
    }

    public static boolean closeProtocol() {
        try {

        /// check if changes occured
        try {

        if (QuiltedPhoto.getChangesMade()) {
            if (!CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
                if (!Messenger.agree(MSG.WORK_CHECK_IGNORE_CHANGES)) {
                    return true;
                }
            }
        }
        
        String[] children = PATHS.dirTmp().list();
        for (int i=0; i<children.length; i++) {
            new File(PATHS.dirTmp(), children[i]).deleteOnExit();
        }
        new File(PATHS.processed_print_file()).deleteOnExit();
        new File(PATHS.original_backup_file()).deleteOnExit();
//        CommunicationLocal.deleteDir(PATHS.dirTmp);

        }
        catch (Exception e) { Logger.printErr(e); }

        /// close application entirely
        System.exit(0);

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static void hideSplash() throws Exception {
        splashDialog.dispose();
    }
    
    

    private static boolean licenseCheckPassed() {
        try {

        String path;
        boolean verbouseLicense;

        path = PATHS.licenseFileName();
        verbouseLicense = false;

        if (verbouseLicense) {
            JOptionPane.showMessageDialog(
                null,
                "license path = \"" + path + "\".\n");
        }

        if (!CommunicationLocal.fileExists(path)) {
            Logger.printOut("File not found.\n");
            return false;
        }
        else {
            String license;
            String[] str;
            Calendar expiryDate, nowDate, lastLoginDate;
            int year, month, date, hour, minute, second;
            boolean trialStarted;
            int idx;

            license = CommunicationLocal.loadLicense(path);
            license = Base64.decode(license);
            license = Base64.decode(license);
            if (verbouseLicense) {
                Logger.printOut("Read: \"%s\".\n", license);
            }
            str = license.split("[-\\ :]");
            idx = 0;
            year = Integer.parseInt(str[idx++]);
            month = Integer.parseInt(str[idx++]);
            date = Integer.parseInt(str[idx++]);
            hour = Integer.parseInt(str[idx++]);
            minute = Integer.parseInt(str[idx++]);
            second = Integer.parseInt(str[idx++]);
            expiryDate = Calendar.getInstance();
            expiryDate.set(year, month-1, date, hour, minute, second);
            trialStarted = (Integer.parseInt(str[idx++]) == 1);
            lastLoginDate = Calendar.getInstance();
            if (idx < str.length) {
                year = Integer.parseInt(str[idx++]);
                month = Integer.parseInt(str[idx++]);
                date = Integer.parseInt(str[idx++]);
                hour = Integer.parseInt(str[idx++]);
                minute = Integer.parseInt(str[idx++]);
                second = Integer.parseInt(str[idx++]);
                lastLoginDate.set(year, month-1, date, hour, minute, second);
            }
            
            nowDate = Calendar.getInstance();

            if (verbouseLicense) {
                Logger.printOut("Trial started = %b\n", trialStarted);
            }
            if (!trialStarted) {
                BufferedWriter f;

                f = new BufferedWriter(new FileWriter(path));

                expiryDate = Calendar.getInstance();
                if (CNT.version.equals(CNT.VERSION_TYPE.BETA)) {
                    expiryDate.add(Calendar.DATE, 14);
//                    expiryDate.add(Calendar.MINUTE, 30);
                }
                else if (CNT.version.equals(CNT.VERSION_TYPE.TRIAL)) {
                    expiryDate.add(Calendar.DATE, 7);
//                    expiryDate.add(Calendar.MINUTE, 30);
                }
                else {
                    throw Exceptions.badIfBranch(CNT.version);
                }

                license = FORMAT.getDateString(expiryDate);
                license += " " + "1";
                license += " " + FORMAT.getDateString(nowDate);
                if (verbouseLicense) {
                    Logger.printOut("Write: \"%s\".\n", license);
                }
                license = Base64.encode(license);
                license = Base64.encode(license);
                f.write(license);

                f.close();
            }
            else {
                license = FORMAT.getDateString(expiryDate);
                license += " " + "1";
                license += " " + FORMAT.getDateString(nowDate);
                if (verbouseLicense) {
                    Logger.printOut("Write: \"%s\".\n", license);
                }
                license = Base64.encode(license);
                license = Base64.encode(license);
            
                if (lastLoginDate.after(nowDate)) {
                    if (verbouseLicense) {
                        Logger.printOut("Not writing. 1.\n");
                    }
                    return false;
                }

                if (nowDate.after(expiryDate)) {
                    if (verbouseLicense) {
                        Logger.printOut("Not writing. 2.\n");
                    }
                    return false;
                }
                
                BufferedWriter f;

                f = new BufferedWriter(new FileWriter(path));

                f.write(license);

                f.close();
            }
        }
        
        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }


    


}
