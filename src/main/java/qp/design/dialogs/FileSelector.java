package qp.design.dialogs;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import qp.CNT;
import qp.DS;
import qp.Exceptions;
import qp.Logger;
import qp.design.Generals;
import qp.design.components.ImagePreview;


/**
 *
 * @author Maira57
 */
public class FileSelector extends JFileChooser {

    public static enum SELECTOR_TYPE {
        IMPORT_FABRIC,
        IMPORT_FABRIC_COLL,
        IMPORT_PHOTO,
        OPEN_PROJECT,
        SAVE_PROJECT,
        EXPORT_TO_JPG
    }



    private static File importImageFolder,
                        openProjectFolder,
                        saveProjectFolder,
                        exportImageFolder;



    public FileSelector(
            SELECTOR_TYPE dialogType)
            throws Exception
    {
        setDialogTitle(DS.SELECTOR_DIALOG.getTitle(dialogType));
        
        setApproveButtonText(DS.SELECTOR_DIALOG.getApproveButtonText(dialogType));

        if (dialogType.equals(SELECTOR_TYPE.IMPORT_FABRIC_COLL)) {
            setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        else {
            setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        if (dialogType.equals(SELECTOR_TYPE.IMPORT_FABRIC)) {
            setMultiSelectionEnabled(true);
        }

        // image preview
        if (dialogType.equals(SELECTOR_TYPE.IMPORT_FABRIC)
                || dialogType.equals(SELECTOR_TYPE.IMPORT_PHOTO)
                || dialogType.equals(SELECTOR_TYPE.EXPORT_TO_JPG))
        {
            setAccessory(new ImagePreview(this));
        }

        if (!dialogType.equals(SELECTOR_TYPE.IMPORT_FABRIC_COLL)) {
            FileFilter[] filters;
            filters = DS.SELECTOR_DIALOG.getFilter(dialogType);
            for (int i=0; i<filters.length; i++) {
                addChoosableFileFilter(filters[i]);
            }
        }
    }

    
    
    public Object[] display(SELECTOR_TYPE dialogType, Component parent) {
        try {

        File folder;
        int result;


        // preparations
        Generals.disableTooltips(this);

        switch (dialogType) {
            case IMPORT_FABRIC: folder = importImageFolder; break;
            
            case IMPORT_FABRIC_COLL: folder = importImageFolder; break;
            
            case IMPORT_PHOTO: folder = importImageFolder; break;
            
            case OPEN_PROJECT: folder = openProjectFolder; break;
            
            case SAVE_PROJECT: folder = saveProjectFolder; break;
            
            case EXPORT_TO_JPG: folder = exportImageFolder; break;

            default: throw Exceptions.badSwitchBranch(dialogType);
        }

        if (!CNT.AS_FOR_RELEASE) {
            if (folder == null) {
                switch (dialogType) {
                    case IMPORT_FABRIC:
                    case IMPORT_FABRIC_COLL:
                    case IMPORT_PHOTO:
                        setCurrentDirectory(new File(CNT.importPathDefault));
                        break;

                    case OPEN_PROJECT:
                        setCurrentDirectory(new File(CNT.openPathDefault));
                        break;

                    case SAVE_PROJECT:
                        setCurrentDirectory(new File(CNT.savePathDefault));
                        break;

                    case EXPORT_TO_JPG:
                        setCurrentDirectory(new File(CNT.exportPathDefault));
                        break;

                    default: throw Exceptions.badSwitchBranch(dialogType);
                }
            }
            else {
                setCurrentDirectory(folder);
            }
        }
        else {
            if (folder != null) {
                setCurrentDirectory(folder);
            }
        }


        // show dialog
        switch (dialogType) {
            case IMPORT_FABRIC:
            case IMPORT_FABRIC_COLL:
            case IMPORT_PHOTO:
                result = showOpenDialog(parent);
                break;

            case OPEN_PROJECT:
                result = showOpenDialog(parent);
                break;

            case SAVE_PROJECT:
                result = showSaveDialog(parent);
                break;

            case EXPORT_TO_JPG:
                result = showSaveDialog(parent);
                break;

            default: throw Exceptions.badSwitchBranch(dialogType);
        }


        // process and return result
        if (result == JFileChooser.APPROVE_OPTION) {
            switch (dialogType) {
                case IMPORT_FABRIC:
                    importImageFolder = getCurrentDirectory();
                    
                    return new Object[] {
                                getSelectedFiles(),
                                result};

                case IMPORT_FABRIC_COLL:
                    importImageFolder = getCurrentDirectory();
                    
                    return new Object[] {
                                getSelectedFile().getAbsolutePath(),
                                result};
                    
                case IMPORT_PHOTO:
                    importImageFolder = getCurrentDirectory();
                    
                    return new Object[] {
                                getSelectedFile().getAbsolutePath(),
                                result};
                    
                case OPEN_PROJECT:
                    openProjectFolder = getCurrentDirectory();
                    
                    return new Object[] {
                                getSelectedFile().getAbsolutePath(),
                                result};

                case SAVE_PROJECT:
                    
                    saveProjectFolder = getCurrentDirectory();
                    return new Object[] {
                                getSelectedFile().getAbsolutePath(),
                                result};

                case EXPORT_TO_JPG:
                    
                    exportImageFolder = getCurrentDirectory();
                    return new Object[] {
                                getSelectedFile().getAbsolutePath(),
                                result};

                default: throw Exceptions.badSwitchBranch(dialogType);
            }
        }
        else {
            return new Object[] {
                        null,
                        result};
        }

        }
        catch (Exception e) {
            Logger.printErr(e);
            return new Object[] {
                        new String(),
                        JFileChooser.ERROR_OPTION};
        }
    }




}
