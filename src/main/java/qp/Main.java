package qp;
import qp.control.MainController;


/**
 * Main entry intro the application
 * @author Maira57
 */
class Main {

    static MainController mainController;



    public static void main(String[] args) {
        try {

        // initialize logger
        Logger.setLogFileName();

        // start application
        MainController.start();

        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.fatalErrorsOccured(false);
        }
    }



}



