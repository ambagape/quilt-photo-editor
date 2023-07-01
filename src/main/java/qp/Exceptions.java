package qp;


/**
 *
 * @author Maira57
 */
public class Exceptions {


    
    public static Exception badSwitchBranch(Object value) throws Exception {
        if (value != null) {
            return new Exception(String.format(
                "Unknown 'switch' branch (type: %s, value: %s)",
                value.getClass().getName().toString(),
                value.toString()));
        }
        else {
            return new Exception(
                "Unknown 'switch' branch");
        }
    }

    public static Exception badIfBranch(Object value) throws Exception {
        if (value != null) {
            return new Exception(String.format(
                "Unknown 'if-else' branch (type: %s, value: %s)",
                value.getClass().getName().toString(),
                value.toString()));
        }
        else {
            return new Exception(
                "Unknown 'if-else' branch");
        }
    }

    public static Exception nullReturnValue() throws Exception {
        return new Exception(
                "Attempting to return a null value.");
    }





}
