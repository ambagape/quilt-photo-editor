package qp.database;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import qp.CNT.PATHS;
import qp.Logger;


/**
 *
 * @author Maira57
 */
public class CommunicationLocal {



    public static boolean fileExists(String fileName) {
        try {

        return new File(fileName).exists();

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static boolean createDir(File dir) throws Exception {
        if (!dir.exists()) {
            return dir.mkdir();
        }
        
        return false;
    }
    
    public static void copyDir(File src, File dest) throws Exception {
    	if (src.isDirectory()) {
            //if directory not exists, create it
            createDir(dest);

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyDir(srcFile,destFile);
            }
    	}
        else {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest); 

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes 
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
    	}
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        return dir.delete();
    }

    public static String loadLicense(String path) throws Exception {
        BufferedReader f;

        f = new BufferedReader(new FileReader(path));

        return f.readLine();
    }

    

    public static void saveMatrix(
            String fileName,
            int[][] matrix,
            boolean withComma)
            throws Exception
    {
        BufferedWriter f;
        String s;
        int w, h;
        int max;
        String format;
        
        w = matrix.length;
        h = matrix[0].length;
        max = -1;
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                if (matrix[i][j] > max) {
                    max = matrix[i][j];
                }
            }
        }
        if (max < 10) {
            format = "1";
        }
        else if (max < 100) {
            format = "2";
        }
        else {
            format = "3";
        }
        
        f = new BufferedWriter(new FileWriter(fileName));

        if (withComma) {
            format = "%" + format + "d,";
        }
        else {
            format = "%" + format + "d";
        }
        
        for (int j=0; j<h; j++) {
            s = new String();
            for (int i=0; i<w; i++) {
                s += String.format(format, matrix[i][j]);
            }
            f.write(s + "\r\n");
        }
        
        f.close();
    }
    
    public static void saveImage(
            String fileName,
            BufferedImage image)
            throws Exception
    {
        ImageIO.write(
                image,
                PATHS.getExtension(fileName),
                new File(fileName));
    }



    public static BufferedImage getImageFromLocal(
            String fileName)
            throws Exception
    {
        if (fileExists(fileName)) {
            return ImageIO.read(new File(fileName));
        }
        else {
            return null;
        }
    }

    public static ImageIcon getImageIconFromJAR(Class m_class, String name) {
        try {

        return new ImageIcon(m_class.getResource(name));

        }
        catch (Exception e) { Logger.printErr(e); return null; }
    }

    public static BufferedImage getImageFromJAR(Class m_class, String name) {
        try {

        Image image;
        BufferedImage bImage;
        Graphics2D g2d;
        MediaTracker mTracker;
        URL rs;

        rs = m_class.getResource(name);
        if (rs == null) {
            return null;
        }

        if (name.contains(PATHS.icons2)) {
            image = Toolkit.getDefaultToolkit().getImage(rs);
            mTracker = new MediaTracker(new JButton());
            mTracker.addImage(image, 1);
            mTracker.waitForID(1, 100);

            bImage = new BufferedImage(
                        image.getWidth(null), image.getHeight(null),
                        BufferedImage.TRANSLUCENT);
            g2d = bImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            image.flush();

            return bImage;
        }

        return ImageIO.read(rs);

        }
        catch (Exception e) { Logger.printErr(e); return null; }
    }






}
