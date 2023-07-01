package qp.design.components;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import qp.Exceptions;
import qp.Logger;
import qp.control.MainController;
import qp.design.constants.Colors;


/**
 *
 * @author Maira57
 */
public class JPanelIcon extends JPanel {

    public static enum POSITION {
        CENTRAL,
        LEFT_UP
    }



    private BufferedImage image;
    private Dimension size;
    private POSITION position;
    private boolean inactive;
    
    private boolean showCrop;
    private Rectangle crop;
    private boolean mouseDown;



    private void init(POSITION position) throws Exception {
        this.setOpaque(false);
        if (image != null) {
            size = new Dimension(image.getWidth(), image.getHeight());
            setPreferredSize(size);
        }
        else {
            size = new Dimension(50, 50);
        }
        this.position = position;
        inactive = false;

        showCrop = false;
        crop = new Rectangle();
        mouseDown = false;
        
        setCallbacks();
    }

    public JPanelIcon() throws Exception {
        super(true);

        init(POSITION.CENTRAL);
    }

    public JPanelIcon(POSITION position) throws Exception {
        super(true);

        init(position);
    }

    public JPanelIcon(BufferedImage icon) throws Exception {
        super(true);

        image = icon;
        init(POSITION.CENTRAL);
    }

    public JPanelIcon(BufferedImage icon, POSITION position) throws Exception {
        super(true);

        image = icon;
        init(position);
    }



    protected @Override void paintComponent(Graphics g) {
        try {

        int x, y;
        int w, h;


        // initializations
        w = size.width;
        h = size.height;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (inactive) {
            g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        }


        // draw the image
        switch (position) {
            case CENTRAL:
                x = (getWidth() - w) / 2;
                y = (getHeight() - h) / 2;
                if (image != null) {
                    g.drawImage(image, x, y, this);
                }
                break;

            case LEFT_UP:
                if (image != null) {
                    g.drawImage(image, 0, 0, this);
                }
                break;

            default: throw Exceptions.badSwitchBranch(position);
        }
        
        if (showCrop && mouseDown) {
            int cx, cy, cw, ch;
            
            if (crop.width < 0) {
                cx = crop.x + crop.width;
                cw = -crop.width;
            }
            else {
                cx = crop.x;
                cw = crop.width;
            }
            if (crop.height < 0) {
                cy = crop.y + crop.height;
                ch = -crop.height;
            }
            else {
                cy = crop.y;
                ch = crop.height;
            }
            
            g.setColor(Colors.red);
            g.drawRect(cx, cy, cw, ch);
        }

        }
        catch (Exception e) {
            Logger.printErr(e);
            MainController.fatalErrorsOccured(true);
        }
    }



    public BufferedImage getImage() throws Exception {
        return image;
    }
    
    public boolean setImage(BufferedImage image) {
        try {

        if (this.image != null) {
            this.image.flush();
        }
        this.image = image;
        size = new Dimension(image.getWidth(), image.getHeight());
        setPreferredSize(size);
        revalidate();

        return true;

        }
        catch (Exception e) { Logger.printErr(e); return false; }
    }

    public void setPosition(POSITION position) throws Exception {
        this.position = position;
    }
    
    public void setCropVisible(boolean value) throws Exception {
        showCrop = value;
    }
    
    public int[] getCrop() throws Exception {
        int cx, cy, cw, ch;

        if (crop.width < 0) {
            cx = crop.x + crop.width;
            cw = -crop.width;
        }
        else {
            cx = crop.x;
            cw = crop.width;
        }
        if (crop.height < 0) {
            cy = crop.y + crop.height;
            ch = -crop.height;
        }
        else {
            cy = crop.y;
            ch = crop.height;
        }
        
        if (cx < 0) {
            cx = 0;
        }
        if (cy < 0) {
            cy = 0;
        }
        if (cx + cw > size.width) {
            cw = size.width - cx;
        }
        if (cy + ch > size.height) {
            ch = size.height - cy;
        }
        
        return new int[] { cx, cy, cw, ch, size.width, size.height };
    }



    public @Override void setEnabled(boolean value) {
        try {

        super.setEnabled(value);

        inactive = !value;

        }
        catch (Exception e) { Logger.printErr(e); }
    }
    
    
    
    private void setCallbacks() throws Exception {
        
        addMouseListener(new MouseAdapter() {
            public @Override void mousePressed(MouseEvent ev) {
                try {

                crop.x = ev.getX();
                crop.y = ev.getY();
                mouseDown = true;
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }

            public @Override void mouseReleased(MouseEvent ev) {
                try {

                crop.width = ev.getX() - crop.x;
                crop.height = ev.getY() - crop.y;
                repaint();
                
                mouseDown = false;
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }

        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            
            public @Override void mouseDragged(MouseEvent ev) {
                try {

                crop.width = ev.getX() - crop.x;
                crop.height = ev.getY() - crop.y;
                repaint();
                    
                }
                catch (Exception e) { Logger.printErr(e); }
            }
            
        });
        
    }



}
