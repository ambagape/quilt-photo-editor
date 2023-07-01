package qp.design;
import java.awt.Container;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import qp.CNT.ICONS;
import qp.DS;
import qp.Exceptions;
import qp.Logger;
import qp.design.dialogs.LinkDialog;


/**
 *
 * @author Maira57
 */
public class Messenger {

    public static enum MSG {
        APP_MAJOR_ERRORS,
        APP_TRIAL_EXPIRED,
        APP_BETA_EXPIRED,

        FILE_OPEN_FAILED,
        FILE_NOT_EXISTS,
        FILE_SAVE_FAILED,
        FILE_WRONG_INSTALL,
        
        FORMAT_UKNOWN_VERSION,
        FORMAT_INEXISTENT_FABR_COLL,

        FABRIC_COLL_CHECK_OVERWRITE,
        FABRIC_COLL_CHECK_REMOVE,
        
        PRINT_NO_FABRICS_LOADED,
        PRINT_NONE_INSTALLED,
        
        RESTRICTION_DEMO_PRINT,
        RESTRICTION_DEMO_SAVE,
        
        WORK_CHECK_IGNORE_CHANGES,
        WORK_NO_IMAGE_LOADED,
        WORK_RESTRICTED_PRINTING,
        WORK_CHECK_REMOVE_FABRICS,
        WORK_CHECK_SAVE_OVERWRITE,
        WORK_PROCESS_FAILED
    }

    private static final int YES_NO = JOptionPane.YES_NO_OPTION;
    private static final int OK_CANCEL = JOptionPane.OK_CANCEL_OPTION;

    private static final int QUESTION = JOptionPane.QUESTION_MESSAGE;
    private static final int INFORMATION = JOptionPane.INFORMATION_MESSAGE;
    private static final int WARNING = JOptionPane.WARNING_MESSAGE;
    private static final int ERROR = JOptionPane.ERROR_MESSAGE;



    private static Container defaultParent;



    public static void setDefaultParent(Container parent) throws Exception {
        defaultParent = parent;
    }



    public static boolean agree(MSG dialogType) {
        try {

        return show(defaultParent, dialogType).equals(JOptionPane.YES_OPTION);

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static boolean agree(Container parent, MSG dialogType) {
        try {

        return show(parent, dialogType).equals(JOptionPane.YES_OPTION);

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }



    public static boolean confirm(MSG dialogType) {
        try {

        return show(defaultParent, dialogType).equals(JOptionPane.OK_OPTION);

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static boolean confirm(Container parent, MSG dialogType) {
        try {

        return show(parent, dialogType).equals(JOptionPane.OK_OPTION);

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }



    public static Object show(MSG dialogType) {
        try {

        return show(defaultParent, dialogType);

        }
        catch (Exception e) { Logger.printErr(e); return null; }
    }

    public static Object show(Container parent, MSG dialogType) {
        try {

        ImageIcon icon;

        switch (dialogType) {
            case PRINT_NO_FABRICS_LOADED:
            case PRINT_NONE_INSTALLED:
            case RESTRICTION_DEMO_PRINT:
            case RESTRICTION_DEMO_SAVE:
            case WORK_NO_IMAGE_LOADED:
            case WORK_RESTRICTED_PRINTING:
                icon = ICONS.WARNING;
                if (icon == null) {
                    JOptionPane.showMessageDialog(
                            parent,
                            DS.DIALOGS.texts()[dialogType.ordinal()],
                            DS.DIALOGS.titleWarning,
                            WARNING);
                }
                else {
                    JOptionPane.showMessageDialog(
                            parent,
                            DS.DIALOGS.texts()[dialogType.ordinal()],
                            DS.DIALOGS.titleWarning,
                            WARNING,
                            icon);
                }
                return null;
                
            case APP_TRIAL_EXPIRED:
            case APP_BETA_EXPIRED:
                new LinkDialog(DS.DIALOGS.titleInformation,
                                DS.DIALOGS.texts()[dialogType.ordinal()],
                                ICONS.INFO.getImage())
                    .setVisible(true);

                return null;

            case FABRIC_COLL_CHECK_OVERWRITE:
            case FABRIC_COLL_CHECK_REMOVE:
            case WORK_CHECK_IGNORE_CHANGES:
            case WORK_CHECK_SAVE_OVERWRITE:
                icon = ICONS.WARNING;
                if (icon == null) {
                    return JOptionPane.showConfirmDialog(
                            parent,
                            DS.DIALOGS.texts()[dialogType.ordinal()],
                            DS.DIALOGS.titleWarning,
                            YES_NO,
                            QUESTION);
                }
                else {
                    return JOptionPane.showConfirmDialog(
                            parent,
                            DS.DIALOGS.texts()[dialogType.ordinal()],
                            DS.DIALOGS.titleWarning,
                            YES_NO,
                            QUESTION,
                            icon);
                }

            case WORK_CHECK_REMOVE_FABRICS:
                icon = ICONS.Q_BLUE;
                if (icon == null) {
                    return JOptionPane.showConfirmDialog(
                            parent,
                            DS.DIALOGS.texts()[dialogType.ordinal()],
                            DS.DIALOGS.titleCheck,
                            YES_NO,
                            QUESTION);
                }
                else {
                    return JOptionPane.showConfirmDialog(
                            parent,
                            DS.DIALOGS.texts()[dialogType.ordinal()],
                            DS.DIALOGS.titleCheck,
                            YES_NO,
                            QUESTION,
                            icon);
                }
            
            case APP_MAJOR_ERRORS:
            case FILE_OPEN_FAILED:
            case FILE_NOT_EXISTS:
            case FILE_SAVE_FAILED:
            case FILE_WRONG_INSTALL:
            case FORMAT_UKNOWN_VERSION:
            case FORMAT_INEXISTENT_FABR_COLL:
            case WORK_PROCESS_FAILED:
                icon = ICONS.ERROR;
                if (icon == null) {
                    JOptionPane.showMessageDialog(
                        parent,
                        DS.DIALOGS.texts()[dialogType.ordinal()],
                        DS.DIALOGS.titleError,
                        ERROR);
                }
                else {
                    JOptionPane.showMessageDialog(
                        parent,
                        DS.DIALOGS.texts()[dialogType.ordinal()],
                        DS.DIALOGS.titleError,
                        ERROR,
                        icon);
                }
                return null;

            default: throw Exceptions.badSwitchBranch(dialogType);
        }

        }
        catch (Exception e) { Logger.printErr(e); return null; }
    }






}
