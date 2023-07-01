package qp;
import qp.CNT.FORMAT_COMMUNICATION;
import qp.CNT.PATHS;
import qp.database.Base64;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 *
 * @author Maira57
 */
public class Logger {

    private static final String packageName =
            Logger.class.getPackage().getName();



    private static String logFileName;
    private static int noLines;



    public static void setLogFileName() {
        try {

        SimpleDateFormat sdf;
        File dirLog;

        dirLog = new File(PATHS.log);
        if (!dirLog.exists()) {
            dirLog.mkdirs();
        }

        sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        logFileName =
                PATHS.log
                + String.format("Log_%s.txt",
                                sdf.format(Calendar.getInstance().getTime()));

        }
        catch (Exception eLogger) {
            System.out.print(eLogger.toString());
            logFileName = PATHS.log + "Log.txt";
        }
    }

    static String getLogFileName() {
        try {

        return new File(logFileName).getAbsolutePath().replace("\\", "/");

        }
        catch (Exception eLogger) {
            System.out.print(eLogger.toString());
            return new String();
        }
    }

    static String getLogFileNameSplitted() {
        try {

        StringBuilder sb;
        String s;

        sb = new StringBuilder();
        s = getLogFileName();
        while (!s.isEmpty()) {
            if (s.length() < 70) {
                sb.append(s);
                s = new String();
            }
            else {
                sb.append(s.substring(0, 70));
                sb.append("<br/>");
                s = s.substring(70);
            }
        }

        return sb.toString();

        }
        catch (Exception eLogger) {
            System.out.print(eLogger.toString());
            return new String();
        }
    }



    public static void printErr(Exception e) {
        try {

        StackTraceElement[] stack = e.getStackTrace();

        // print entire error
        e.printStackTrace();

        // print first line from error
        System.out.printf(e.toString() + ", in ");
        for (int i=0; i<stack.length; i++) {
            if (stack[i].getClassName().startsWith(packageName)) {
                System.out.printf("%s : %s - %d (file '%s')\n",
                        stack[i].getClassName(),
                        stack[i].getMethodName(),
                        stack[i].getLineNumber(),
                        stack[i].getFileName());
                break;
            }
        }

        // get previous log content
        String s;
        s = getPreviousLogContent();

        // output error to log file
        BufferedWriter fout;
        fout = new BufferedWriter(new FileWriter(logFileName));
        fout.write(s);
        if (noLines == 1) {
            fout.write(Integer.toString(noLines));
            fout.newLine();
        }
        fout.write(String.format("Error no. %d:%s%s, in:%s",
                                noLines,
                                FORMAT_COMMUNICATION.NEW_LINE,
                                e.toString(),
                                FORMAT_COMMUNICATION.NEW_LINE));
        for (int i=0; i<stack.length; i++) {
            if (stack[i].getClassName().startsWith(packageName)) {
                fout.write(String.format(
                        "%s : %s - %d (file '%s')",
                        stack[i].getClassName(),
                        stack[i].getMethodName(),
                        stack[i].getLineNumber(),
                        stack[i].getFileName()));
                fout.newLine();
            }
        }
        fout.close();

        encryptLog();

        }
        catch (Exception eLogger) { System.out.print(eLogger.toString()); }
    }

    private static String getPreviousLogContent() {
        try {

        StringBuffer sbuf;
        String[] log;
        String line;
        int idxLine;

        noLines = 1;
        if (new File(logFileName).exists()) {
            log = decryptLog();

            idxLine = 0;
            sbuf = new StringBuffer();

            line = log[idxLine++];
            if (line != null) {
                noLines = Integer.parseInt(line);
                noLines++;
                line = Integer.toString(noLines);
            }
            while (idxLine < log.length) {
                sbuf.append(line).append(FORMAT_COMMUNICATION.NEW_LINE);
                line = log[idxLine++];
            }
            sbuf.append(line).append(FORMAT_COMMUNICATION.NEW_LINE);
            line = sbuf.toString();
        }
        else {
            line = new String();
        }

        return line;

        }
        catch (Exception eLogger) {
            System.out.print(eLogger.toString());
            return new String();
        }
    }

    private static void encryptLog() throws Exception {
        BufferedReader fin;
        BufferedWriter fout;
        ArrayList<String> data1;
        ArrayList<String> data2;
        ArrayList<String> data3;
        String line;
        String str;
        int periodicity;
        int pozChar;
        int limit;

        
        data1 = new ArrayList<String>();
        fin = new BufferedReader(new FileReader(logFileName));
        line = fin.readLine();
        while (line != null) {
            data1.add(line);
            line = fin.readLine();
        }
        fin.close();


        fout = new BufferedWriter(new FileWriter(logFileName));
        periodicity = 20;

        data2 = new ArrayList<String>();
        for (int i=0; i<data1.size(); i++) {
            data2.add(Base64.encode(data1.get(i)));
        }

        data3 = new ArrayList<String>();
        for (int i=0; i<data2.size(); i += periodicity) {
            line = new String();
            limit = Math.min(i+periodicity, data2.size());
            for (int j=i; j<limit; j++) {
                str = data2.get(j);
                pozChar = j-i;
                if (pozChar < str.length()-1) {
                    line += str.charAt(pozChar);
                    data3.add(str.substring(0, pozChar) + str.substring(pozChar+1));
                }
                else {
                    line += str.charAt(str.length()-1);
                    data3.add(str.substring(0, str.length()-1));
                }
            }
            data3.add(line);
        }

        for (int i=0; i<data3.size(); i++) {
            fout.write(data3.get(i));
            fout.newLine();
        }

        fout.close();
    }

    private static String[] decryptLog() throws Exception {
        BufferedReader fin;
        String line, str;
        ArrayList<String> data1;
        ArrayList<String> data2;
        int periodicity;
        int pozChar;
        int limit;
        ArrayList<String> log;

        periodicity = 20;

        fin = new BufferedReader(new FileReader(logFileName));
        line = fin.readLine();

        data1 = new ArrayList<String>();
        while (line != null) {
            data1.add(line);
            line = fin.readLine();
        }
        fin.close();

        data2 = new ArrayList<String>();
        for (int i=0; i<data1.size(); i += periodicity+1) {
            limit = Math.min(i+periodicity+1, data1.size());

            line = data1.get(limit-1);
            for (int j=0; j<line.length(); j++) {
                str = data1.get(i+j);
                pozChar = j;

                if (pozChar >= str.length()) {
                    data2.add(
                        str
                        + line.charAt(pozChar));
                }
                else {
                    data2.add(
                        str.substring(0, pozChar)
                        + line.charAt(pozChar)
                        + str.substring(pozChar));
                }
            }
        }

        log = new ArrayList<String>();
        for (int i=0; i<data2.size(); i++) {
            log.add(Base64.decode(data2.get(i)));
        }

        return log.toArray(new String[0]);
    }



    public static void printOut(String format, Object ... args) {
        try {

        System.out.printf(format, args);

        }
        catch (Exception eLogger) {
            System.out.print(eLogger.getMessage());
        }
    }





}
