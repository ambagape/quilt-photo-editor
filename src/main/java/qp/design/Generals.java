package qp.design;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import qp.CNT.FORMAT;
import qp.Logger;
import qp.control.types.RGB8;
import qp.design.components.JPanelGlass;


/**
 *
 *    Constants and functions available for all windows and dialogs
 * within this application.
 *
 * @author Maira57
 */
public class Generals {

    public static final int IMAGE_ARGB = BufferedImage.TYPE_INT_ARGB;



    public static String getAbsolutePath() {
        return FileSystemView.getFileSystemView().
                    getDefaultDirectory().getAbsolutePath();
    }

    public static FileFilter getFileFilter(
            String description,
            String ... extensions)
            throws Exception
    {
        return new FileNameExtensionFilter(description, extensions);
    }

    public static Object getDefault(Object key) throws Exception {
        return UIManager.get(key);
    }

    public static Object putDefault(Object key, Object value) throws Exception {
        return UIManager.put(key, value);
    }

    public static void invokeLater(Runnable doRun) throws Exception {
        SwingUtilities.invokeLater(doRun);
    }

    public static boolean isLeftMouseButton(MouseEvent ev) throws Exception {
        return SwingUtilities.isLeftMouseButton(ev);
    }

    public static void addKeyDispatcher(
            KeyEventDispatcher kd)
            throws Exception
    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(kd);
    }

    public static void removeKeyDispatcher(
            KeyEventDispatcher kd)
            throws Exception
    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .removeKeyEventDispatcher(kd);
    }

    public static Component rigidAreaOnX(
            Dimension d, float alignment)
            throws Exception
    {
        JComponent component;

        component = (JComponent)Box.createRigidArea(d);
        component.setAlignmentX(alignment);

        return component;
    }

    public static Component rigidAreaOnY(
            Dimension d, float alignment)
            throws Exception
    {
        JComponent component;

        component = (JComponent)Box.createRigidArea(d);
        component.setAlignmentY(alignment);

        return component;
    }

    public static boolean setDefaultCursor(Component c) {
        try {

        c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static boolean setWaitCursor(Component c) {
        try {

        c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static boolean setHandCursor(Component c) {
        try {

        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public static void showToolTipWhenActioned(
            final JComponent component,
            ActionMap actionMap)
            throws Exception
    {
        Action toolTipAction = actionMap.get("postTip");

        if (toolTipAction != null) {
            ActionEvent postTip = new ActionEvent(component,
                                                ActionEvent.ACTION_PERFORMED,
                                                new String());
            toolTipAction.actionPerformed(postTip);

            new Thread() {
                public @Override void run() {
                    try {

                    component.setToolTipText(FORMAT.STR_NULL);

                    }
                    catch (Exception e) { Logger.printErr(e); }
                }
            }.start();
        }
    }





    public static JPanelGlass surround(
            JComponent component,
            Dimension surroundSize)
            throws Exception
    {
        JPanelGlass panel;

        panel = new JPanelGlass();

        panel.setLayout(new BorderLayout());
        panel.add(Box.createRigidArea(surroundSize), BorderLayout.NORTH);
        panel.add(Box.createRigidArea(surroundSize), BorderLayout.WEST);
        panel.add((JComponent)component, BorderLayout.CENTER);
        panel.add(Box.createRigidArea(surroundSize), BorderLayout.EAST);
        panel.add(Box.createRigidArea(surroundSize), BorderLayout.SOUTH);

        panel.setAlignmentX(((JComponent)component).getAlignmentX());
        panel.setAlignmentY(((JComponent)component).getAlignmentY());

        return panel;
    }


    public static void setEnabledWithChildren(
            Component parent, boolean flag)
            throws Exception
    {
        JComponent parentJ;

        if (parent instanceof JComponent) {
            parentJ = (JComponent) parent;

            for (int i=0; i<parentJ.getComponentCount(); i++) {
                setEnabledWithChildren(parentJ.getComponent(i), flag);
            }
        }

        parent.setEnabled(flag);
    }
    
    public static void disableTooltips(Container c) throws Exception {
        Component[] comps;

        comps = c.getComponents();

        for (int i=0; i<comps.length; i++) {
            if (comps[i] instanceof JButton) {
                ((JButton)comps[i]).setToolTipText(FORMAT.STR_NULL);
            }
            else if (comps[i] instanceof Container) {
                disableTooltips((Container)comps[i]);
            }
        }
    }

    
    
    public static Dimension getPositionOfScaled(
            BufferedImage src,
            int desWidth, int desHeight)
            throws Exception
    {
        double sx, sy, s;
        int w, h, tx, ty;

        w = src.getWidth();
        h = src.getHeight();
        sx = (double)desWidth / (double)w;
        sy = (double)desHeight / (double)h;
        if (sx < sy) {
            s = sx;
        }
        else {
            s = sy;
        }
        tx = (int)((desWidth - w*s) / 2.0);
        ty = (int)((desHeight - h*s) / 2.0);

        return new Dimension(tx/2 - 0, ty/2 - 0);
    }
    
    public static BufferedImage getScaledImage(
            BufferedImage img,
            int maxDim)
            throws Exception
    {
        double factor;
        
        if (img.getHeight() > img.getWidth()) {
            factor = ((100.0*maxDim/img.getHeight()));
        }
        else {
            factor = ((100.0*maxDim/img.getWidth()));
        }
        
        return Generals.scaleImage(
                    img,
                    (int)(factor*img.getWidth()/100),
                    (int)(factor*img.getHeight()/100),
                    false);
    }

    public static BufferedImage scaleImage(
            BufferedImage src,
            int desWidth, int desHeight,
            boolean keepProportions)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;

        dest = new BufferedImage(desWidth, desHeight,
                                BufferedImage.TYPE_INT_ARGB);
        g2d = dest.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
        if (keepProportions) {
            double sx, sy, s;
            int w, h, tx, ty;

            w = src.getWidth();
            h = src.getHeight();
            sx = (double)desWidth / (double)w;
            sy = (double)desHeight / (double)h;
            if (sx < sy) {
                s = sx;
            }
            else {
                s = sy;
            }
            tx = (int)((desWidth - w*s) / 2.0);
            ty = (int)((desHeight - h*s) / 2.0);

            g2d.drawImage(src,
                        tx, ty, (int)(w*s), (int)(h*s),
                        null);
        }
        else {
            g2d.drawImage(src,
                        0, 0, desWidth, desHeight,
                        0, 0, src.getWidth(), src.getHeight(),
                        null);
        }
        
        g2d.dispose();

        return dest;
    }

    public static BufferedImage scaleImage2(
            BufferedImage src,
            int desWidth, int desHeight)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;

        dest = new BufferedImage(desWidth, desHeight,
                                BufferedImage.TYPE_INT_ARGB);
        g2d = dest.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            
        g2d.drawImage(src,
                    0, 0, desWidth, desHeight,
                    0, 0, src.getWidth(), src.getHeight(),
                    null);
        
        g2d.dispose();

        return dest;
    }

    public static BufferedImage copyImage(
            BufferedImage src)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;
            
        dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                BufferedImage.TYPE_INT_ARGB);
        g2d = dest.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        
        g2d.dispose();

        return dest;
    }
    
    public static BufferedImage createImage(
            short[] p,
            int pattern_w, int pattern_h)
            throws Exception
    {
        BufferedImage img;
        short value, value2;

        img = new BufferedImage(pattern_w, pattern_h, BufferedImage.TYPE_INT_ARGB);

        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w; j++) {
                img.setRGB(j, i, 0xFFFFFFFF);
            }
        }

        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w-1; j++) {
                value = p[i*pattern_w + j];
                value2 = p[i*pattern_w + j+1];
                
                if (value != value2) {
                    img.setRGB(j, i, 0xFF000000);
                }
            }
        }

        for (int i=0; i<pattern_h-1; i++) {
            for (int j=0; j<pattern_w; j++) {
                value = p[i*pattern_w + j];
                value2 = p[(i+1)*pattern_w + j];
                
                if (value != value2) {
                    img.setRGB(j, i, 0xFF000000);
                }
            }
        }
        
        return img;
    }

    public static BufferedImage createImage(
            RGB8 color,
            int w, int h)
            throws Exception
    {
        BufferedImage image;
        Graphics2D g;
        
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        
        g.setColor(new Color(color.r, color.g, color.b));
        g.fillRect(0, 0, w, h);
        
        g.dispose();
        
        return image;
    }

    public static BufferedImage cropImage(
            BufferedImage src,
            int x0, int y0, int x1, int y1)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;
        int w, h;
            
        w = x1 - x0;
        h = y1 - y0;
        dest = new BufferedImage(w, h,
                                BufferedImage.TYPE_INT_ARGB);
        g2d = dest.createGraphics();
        g2d.drawImage(src, 0, 0, w, h, x0, y0, x1, y1, null);
        
        g2d.dispose();

        return dest;
    }

    public static BufferedImage rotateImage(
            BufferedImage src,
            double angle,
            boolean withAutoBorderFilling,
            boolean repetitive)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;
        int w, h;
        
        w = src.getWidth();
        h = src.getHeight();
        
        if (!withAutoBorderFilling) {
            dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int i=0; i<w; i++) {
                for (int j=0; j<h; j++) {
                    dest.setRGB(i, j, 0);
                }
            }
            
            g2d = dest.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            
            g2d.rotate(Math.toRadians(angle), w/2, h/2);
            g2d.drawImage(src, 0, 0, null);
        }
        else {
            BufferedImage tmp;
            int bw, bh;
            
            bw = w/2;
            bh = h/2;
            
            tmp = getMatrixWithAutoBorder(src, bw, bh, repetitive);
            
            tmp = rotateImage(tmp, angle, false, false);
            
            dest = cropImage(tmp, bw, bh, w + bw, h + bh);
        }
        
        return dest;
    }
    
    public static BufferedImage getMatrixWithAutoBorder(
            BufferedImage src,
            int bw, int bh,
            boolean repetitive)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;
        int w, h;

        w = src.getWidth();
        h = src.getHeight();
        dest = new BufferedImage(w+2*bw, h+2*bh, BufferedImage.TYPE_INT_ARGB);
        g2d = dest.createGraphics();
        g2d.drawImage(src, bw, bh, null);

        if (repetitive) {
            // fill in the four corners of the border
            g2d.drawImage(src, 0, 0, bw, bh, w-bw, h-bh, w, h, null);

            g2d.drawImage(src, w+bw, 0, w+2*bw, bh, 0, h-bh, bw, h, null);

            g2d.drawImage(src, 0, h+bh, bw, h+2*bh, w-bw, 0, w, bh, null);

            g2d.drawImage(src, w+bw, h+bh, w+2*bw, h+2*bh, 0, 0, bw, bh, null);

            // fill in the top and bottom of the border
            g2d.drawImage(src, bw, 0, bw+w, bh, 0, h-bh, w, h, null);
            g2d.drawImage(src, bw, h+bh, bw+w, h+2*bh, 0, 0, w, bh, null);

            // fill in the left and right of the border
            g2d.drawImage(src, 0, bh, bw, bh+h, w-bw, 0, w, h, null);
            g2d.drawImage(src, w+bw, bh, w+2*bw, bh+h, 0, 0, bw, h, null);
        }
        else {
            // fill in the four corners of the border
            g2d.setColor(new Color(src.getRGB(0, 0)));
            g2d.fillRect(0, 0, bw, bh);

            g2d.setColor(new Color(src.getRGB(w-1, 0)));
            g2d.fillRect(w+bw, 0, bw, bh);

            g2d.setColor(new Color(src.getRGB(0, h-1)));
            g2d.fillRect(0, h+bh, bw, bh);

            g2d.setColor(new Color(src.getRGB(w-1, h-1)));
            g2d.fillRect(w+bw, h+bh, bw, bh);

            // fill in the top and bottom of the border
            g2d.drawImage(src, bw, 0, bw+w, bh, 0, 0, w, 1, null);
            g2d.drawImage(src, bw, h+bh, bw+w, h+2*bh, 0, h-1, w, h, null);

            // fill in the left and right of the border
            g2d.drawImage(src, 0, bh, bw, bh+h, 0, 0, 1, h, null);
            g2d.drawImage(src, w+bw, bh, w+2*bw, bh+h, w-1, 0, w, h, null);
        }
        
        return dest;
    }
    
    public static BufferedImage getImageFromMatrix(
            int[][] matrix)
            throws Exception
    {
        BufferedImage img;
        int w, h;
        
        w = matrix.length;
        h = matrix[0].length;
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                img.setRGB(i, j, matrix[i][j]);
            }
        }
        
        return img;
    }
    
    public static int[][] getMatrixFromImage(
            BufferedImage img)
            throws Exception
    {
        int[][] matrix;
        int w, h;
        
        w = img.getWidth();
        h = img.getHeight();
        matrix = new int[w][h];
        
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                matrix[i][j] = img.getRGB(i, j);
            }
        }
        
        return matrix;
    }

    

    public static int[][] getMatrixInts(short[][] mat_src) throws Exception {
        int[][] mat_dest;
        int w, h;
        
        w = mat_src.length;
        h = mat_src[0].length;
        
        mat_dest = new int[w][h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                mat_dest[i][j] = mat_src[i][j];
            }
        }
        
        return mat_dest;
    }
    
    public static short[][] getMatrixShorts(int[][] mat_src) throws Exception {
        short[][] mat_dest;
        int w, h;
        
        w = mat_src.length;
        h = mat_src[0].length;
        
        mat_dest = new short[w][h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                mat_dest[i][j] = (short)mat_src[i][j];
            }
        }
        
        return mat_dest;
    }
    
    public static short[][] cropMatrix(
            short[][] m,
            int x0, int y0, int x1, int y1)
            throws Exception
    {
        short[][] m2;

        m2 = new short[x1-x0][y1-y0];
        for (int i=x0; i<x1; i++) {
            for (int j=y0; j<y1; j++) {
                m2[i-x0][j-y0] = m[i][j];
            }
        }
        
        return m2;
    }
    
    public static int[][] cropMatrix2(
            int[][] m,
            int x0, int y0, int x1, int y1)
            throws Exception
    {
        int[][] m2;

        m2 = new int[x1-x0][y1-y0];
        for (int i=x0; i<x1; i++) {
            for (int j=y0; j<y1; j++) {
                m2[i-x0][j-y0] = m[i][j];
            }
        }
        
        return m2;
    }
    
    public static short[][] rotateMatrix(
            short[][] m,
            int angle)
            throws Exception
    {
        short[][] m2;
        int w, h;
        double angle2;
        double cos, sin;
        int x1, y1;
        
        w = m.length;
        h = m[0].length;
        
        angle2 = angle * Math.PI / 180;
        cos = Math.cos(angle2);
        sin = Math.sin(angle2);
        
        m2 = new short[w][h];
        for (int x=0; x<w; x++) {
            for (int y=0; y<h; y++) {
                x1 = (int)Math.round(x * cos - y * sin);
                y1 = (int)Math.round(y * cos + x * sin);
                
                if (x1 < 0) {
                    continue;
                }
                if (y1 < 0) {
                    continue;
                }
                if (x1 >= w) {
                    continue;
                }
                if (y1 >= h) {
                    continue;
                }
                m2[x1][y1] = m[x][y];
            }
        }
        
        return m2;
    }
    
    public static short[][] rotateMatrix2(
            short[][] m,
            int angle)
            throws Exception
    {
        short[][] m2;
        int w, h;
        BufferedImage tmp;
        int c;
        Color c2;
        
        w = m.length;
        h = m[0].length;
        
        tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                c = m[i][j];
                tmp.setRGB(i, j, new Color(
                                    (c / 100) / 100,
                                    (c / 100) % 100,
                                    c % 100,
                                    255)
                                .getRGB());
            }
        }
        tmp = rotateImage(tmp, angle, false, false);
        
        m2 = new short[w][h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                c2 = new Color(tmp.getRGB(i, j));
                
                m2[i][j] = (short)(c2.getRed() * 100 * 100
                                    + c2.getGreen() * 100
                                    + c2.getBlue());
            }
        }
        
        return m2;
    }

    public static int[][] resizeMatrix(
            int[][] pixels,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        int[][] temp;
        int[] temp_ = new int[w2*h2];
        int A, B, C, D, x, y, index, gray;
        float x_ratio = ((float)(w-1))/w2;
        float y_ratio = ((float)(h-1))/h2;
        float x_diff, y_diff;
        int offset = 0;
        int[] pixels_;
        
        pixels_ = new int[w*h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                pixels_[j*w + i] = pixels[i][j];
            }
        }

        
        for (int i=0; i<h2; i++) {
            for (int j=0; j<w2; j++) {
                x = (int)(x_ratio * j);
                y = (int)(y_ratio * i);
                x_diff = (x_ratio * j) - x;
                y_diff = (y_ratio * i) - y;
                index = y*w+x;

                // range is 0 to 255 thus bitwise AND with 0xff
                A = pixels_[index] & 0xff;
                B = pixels_[index+1] & 0xff;
                C = pixels_[index+w] & 0xff;
                D = pixels_[index+w+1] & 0xff;

                // Y = A(1-w)(1-h) + B(w)(1-h) + C(h)(1-w) + Dwh
                gray = (int)(
                        A*(1-x_diff)*(1-y_diff) +  B*(x_diff)*(1-y_diff) +
                        C*(y_diff)*(1-x_diff)   +  D*(x_diff*y_diff)
                );

                temp_[offset++] = gray;                                   
            }
        }
    
        
        temp = new int[w2][h2];
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                temp[i][j] = temp_[j*w + i];
            }
        }
        
        return temp;
    }

    public static int[][] resizeMatrix2(
            int[][] pixels,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        BufferedImage img, img2;
        Graphics2D g2d;
        int[][] temp;
        int[] pixels_;
        int black, white;
        
        pixels_ = new int[w*h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                pixels_[j*w + i] = pixels[i][j];
            }
        }

        
        white = 0xFFFFFFFF;
        black = 0xFF000000;
        
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w-1; i++) {
            for (int j=0; j<h-1; j++) {
                if (pixels[i][j]!=pixels[i+1][j]
                        || pixels[i][j]!=pixels[i][j+1]) {
                    img.setRGB(i, j, black);
                }
                else {
                    img.setRGB(i, j, white);
                }
            }
        }
        
        img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(img,
                    0, 0, w2, h2,
                    0, 0, w, h,
                    null);
        
        temp = new int[w2][h2];
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                if (img2.getRGB(i, j) != white) {
                    temp[i][j] = 1;
                }
                else {
                    temp[i][j] = 0;
                }
            }
        }
        
        img.flush();
        
        return temp;
    }

    public static int[][] resizeMatrix3(
            int[][] tmp,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        int[][] tmp2;
        double ratio_x, ratio_y;
        
        tmp2 = new int[w2][h2];
        ratio_x = (double)w2 / w;
        ratio_y = (double)h2 / h;
        
        for (int j=0; j<h-1; j++) {
            for (int i=0; i<w-1; i++) {
                int x = tmp[i][j];
                
                if ((x != tmp[i+1][j]) || (x != tmp[i][j+1])) {
                    tmp2[(int)(i*ratio_x)][(int)(j*ratio_y)] = 1;
                }
            }
        }
        
        return tmp2;
    }

    public static int[][] resizeMatrix4(
            int[][] pixels,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        BufferedImage img, img2;
        Graphics2D g2d;
        int[][] temp;
        int[] pixels_;
        int black, white, border;
        int c;

        
        pixels_ = new int[w*h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                pixels_[j*w + i] = pixels[i][j];
            }
        }

        
        border = new Color(255, 255, 255, 255).getRGB();
        
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                c = pixels[i][j];
                
                if (c == 255) {
                    img.setRGB(i, j, border);
                }
                else {
                    img.setRGB(i, j, new Color(
                                        (c / 100) / 100,
                                        (c / 100) % 100,
                                        c % 100,
                                        255)
                                    .getRGB());
                }
            }
        }
        
        img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(img,
                    0, 0, w2, h2,
                    0, 0, w, h,
                    null);

        
        white = 0xFFFFFFFF;
        black = 0xFF000000;
        
        img = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w2; i++) {
            for (int j=1; j<h2-1; j++) {
                if (img2.getRGB(i,j) == border) {
                    img2.setRGB(i, j, img2.getRGB(i, j-1));
                }
            }
        }
        for (int i=1; i<w2; i++) {
            for (int j=1; j<h2; j++) {
                if (img2.getRGB(i, j) != img2.getRGB(i-1, j)
                        || img2.getRGB(i, j) != img2.getRGB(i, j-1)) {
                    img.setRGB(i, j, black);
                }
                else {
                    img.setRGB(i, j, white);
                }
            }
        }
        for (int i=0; i<w2; i++) {
            img.setRGB(i, 0, white);
        }
        for (int j=0; j<h2; j++) {
            img.setRGB(0, j, white);
        }

        
        temp = new int[w2][h2];
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                if (img.getRGB(i, j) != white) {
                    temp[i][j] = 1;
                }
                else {
                    temp[i][j] = 0;
                }
            }
        }
        
        img.flush();
        img2.flush();

        
        return temp;
    }

    public static int[][] resizeMatrix5(
            int[][] pixels,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        BufferedImage img, img2;
        Graphics2D g2d;
        int[][] temp;
        int[] pixels_;
        int c;

        
        pixels_ = new int[w*h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                pixels_[j*w + i] = pixels[i][j];
            }
        }

        
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                c = pixels[i][j];
                img.setRGB(i, j, new Color(
                                    (c / 100) / 100,
                                    (c / 100) % 100,
                                    c % 100,
                                    255)
                                .getRGB());
            }
        }
        
        img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(img,
                    0, 0, w2, h2,
                    0, 0, w, h,
                    null);

        
        img = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                img.setRGB(i, j, img2.getRGB(i, j));
            }
        }

        
        temp = new int[w2][h2];
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                Color c2 = new Color(img.getRGB(i, j));
                
                temp[i][j] = (short)(c2.getRed() * 100 * 100
                                    + c2.getGreen() * 100
                                    + c2.getBlue());
            }
        }
        
        img.flush();
        img2.flush();

        
        return temp;
    }

    public static int[][] resizeMatrix4_0(
            int[][] pixels,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        BufferedImage img, img2;
        Graphics2D g2d;
        int[][] temp;
        int[] pixels_;
        int black, white;
        int c;

        
        pixels_ = new int[w*h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                pixels_[j*w + i] = pixels[i][j];
            }
        }

        
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                c = pixels[i][j];
                img.setRGB(i, j, new Color(
                                    (c / 100) / 100,
                                    (c / 100) % 100,
                                    c % 100,
                                    255)
                                .getRGB());
            }
        }
        
        img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(img,
                    0, 0, w2, h2,
                    0, 0, w, h,
                    null);

        
        white = 0xFFFFFFFF;
        black = 0xFF000000;
        
        img = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w2-1; i++) {
            for (int j=0; j<h2-1; j++) {
                if (img2.getRGB(i, j) != img2.getRGB(i+1, j)
                        || img2.getRGB(i, j) != img2.getRGB(i, j+1)) {
                    img.setRGB(i, j, black);
                }
                else {
                    img.setRGB(i, j, white);
                }
            }
        }

        
        temp = new int[w2][h2];
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                if (img.getRGB(i, j) != white) {
                    temp[i][j] = 1;
                }
                else {
                    temp[i][j] = 0;
                }
            }
        }
        
        img.flush();
        img2.flush();

        
        return temp;
    }

    public static int[][] resizeMatrix5_0(
            int[][] pixels,
            int w, int h,
            int w2, int h2)
            throws Exception
    {
        BufferedImage img, img2;
        Graphics2D g2d;
        int[][] temp;
        int[] pixels_;
        int c;

        
        pixels_ = new int[w*h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                pixels_[j*w + i] = pixels[i][j];
            }
        }

        
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                c = pixels[i][j];
                img.setRGB(i, j, new Color(
                                    (c / 100) / 100,
                                    (c / 100) % 100,
                                    c % 100,
                                    255)
                                .getRGB());
            }
        }
        
        img2 = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(img,
                    0, 0, w2, h2,
                    0, 0, w, h,
                    null);

        
        img = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<w2-1; i++) {
            for (int j=0; j<h2-1; j++) {
                img.setRGB(i, j, img2.getRGB(i, j));
            }
        }

        
        temp = new int[w2][h2];
        for (int i=0; i<w2; i++) {
            for (int j=0; j<h2; j++) {
                Color c2 = new Color(img.getRGB(i, j));
                
                temp[i][j] = (short)(c2.getRed() * 100 * 100
                                    + c2.getGreen() * 100
                                    + c2.getBlue());
            }
        }
        
        img.flush();
        img2.flush();

        
        return temp;
    }


    
    public static boolean imagesAreEqual(
            BufferedImage img1, BufferedImage img2)
            throws Exception
    {
        if (img1 == null || img2 == null) {
            if (!(img1 == null && img2 == null)) {
                return false;
            }
        }
        else {
            for (int i=0; i<img1.getWidth(); i++) {
                for (int j=0; j<img1.getHeight(); j++) {
                    if (img1.getRGB(i, j) != img2.getRGB(i, j)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }





}
