package qp.control;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import qp.Logger;
import qp.design.Generals;
import qp.design.components.JPanelIcon;


/**
 *
 * @author Maira57
 */
public class AdjustController {

    private static final int standardWidth = 256;
    private static final int standardHeight = 256;



    private JPanelIcon displayerInput;
    private JPanelIcon displayerOutput;

    private BufferedImage imageOrig;
    private BufferedImage imageInput;
    private BufferedImage imageOutput;

    private int imageInputWidth, imageInputHeight;

    private int brightness;
    private int contrast;



    public AdjustController(
            JPanelIcon displayerInput,
            JPanelIcon displayerOutput)
            throws Exception
    {
        this.displayerInput = displayerInput;
        this.displayerOutput = displayerOutput;

        brightness = 0;
        contrast = 0;
    }



    public boolean setImageInput(BufferedImage imageOrig) throws Exception {
        try {

        Graphics2D g2d;

        this.imageOrig = imageOrig;

        if (imageOrig != null) {
            // scale to standard size and copy to image
            imageInput = Generals.scaleImage(
                            imageOrig, standardWidth, standardHeight, false);
        }
        else {
            // reset input image
            imageInput = new BufferedImage(
                                standardWidth, standardHeight,
                                Generals.IMAGE_ARGB);

            // display error message
//            Messenger.show(MSG.WORK_SET_IMAGE_FAILED);
        }

        displayerInput.setImage(getImageForDisplay(imageInput));
        displayerInput.repaint();

        imageInputWidth = imageInput.getWidth();
        imageInputHeight = imageInput.getHeight();


        imageOutput = new BufferedImage(imageInputWidth, imageInputHeight,
                                    Generals.IMAGE_ARGB);
        g2d = imageOutput.createGraphics();
        g2d.drawImage(imageInput, 0, 0, null);
        displayerOutput.setImage(getImageForDisplay(imageOutput));
        displayerOutput.repaint();
        g2d.dispose();


        return true;

        }
        catch (Exception e) {
            Logger.printErr(e);
//            Messenger.show(MSG.WORK_SET_IMAGE_FAILED);
            return false;
        }
    }

    private BufferedImage getImageForDisplay(BufferedImage src) throws Exception {
        BufferedImage dest;
        Graphics2D g2d;
        double sx, sy, s;
        int w, h;
        int tx, ty;


        // get w and h from original image and not from source image
        // because only the original image dimensions give the
        // initial scale
        w = imageOrig.getWidth();
        h = imageOrig.getHeight();

        sx = (double) standardWidth / (double) w;
        sy = (double) standardHeight / (double) h;
        if (sx < sy) {
            s = sx;
        }
        else {
            s = sy;
        }
        tx = (int) ((standardWidth - w * s) / 2.0);
        ty = (int) ((standardHeight - h * s) / 2.0);

        dest = new BufferedImage(standardWidth, standardHeight,
                                    Generals.IMAGE_ARGB);
        g2d = dest.createGraphics();
        g2d.drawImage(src,
                        tx, ty, (int) (w * s), (int) (h * s),
                        null);

        return dest;
    }

    public BufferedImage getImageAdjusted() throws Exception {
        return getImageAdjusted(imageOrig, brightness, contrast);
    }

    public static BufferedImage getImageAdjusted(
            BufferedImage image, int brightness, int contrast)
            throws Exception
    {
        BufferedImage output;

        output = getContrastImage(image, contrast);
        output = getBrightnessImage(output, brightness);

        return output;
    }



    /////////////////////////////////////////////////////////////
    ///  interaction with interface methods
    /////////////////////////////////////////////////////////////

    public void setBrightness(int brightness) throws Exception {
        this.brightness = brightness;

        imageOutput = getContrastImage(imageInput, contrast);
        imageOutput = getBrightnessImage(imageOutput, brightness);

        displayerOutput.setImage(getImageForDisplay(imageOutput));
        displayerOutput.repaint();
    }

    public int getBrightness() throws Exception {
        return brightness;
    }

    public void setContrast(int contrast) throws Exception {
        this.contrast = contrast;

        imageOutput = getContrastImage(imageInput, contrast);
        imageOutput = getBrightnessImage(imageOutput, brightness);

        displayerOutput.setImage(getImageForDisplay(imageOutput));
        displayerOutput.repaint();
    }

    public int getContrast() throws Exception {
        return contrast;
    }



    /////////////////////////////////////////////////////////////
    ///  secondary methods
    /////////////////////////////////////////////////////////////

    private static BufferedImage getBrightnessImage(
            BufferedImage src, int brightness)
            throws Exception
    {
        RescaleOp rescale;
        BufferedImage src2, dest;
        Graphics2D g2d;

        src2 = new BufferedImage(src.getWidth(), src.getHeight(),
                                    BufferedImage.TYPE_INT_RGB);
        g2d = src2.createGraphics();
        g2d.drawImage(src, 0, 0, null);

        dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                    BufferedImage.TYPE_INT_RGB);
        g2d = dest.createGraphics();
        g2d.drawImage(src, 0, 0, null);

        g2d.dispose();

        rescale = new RescaleOp(1.0f, brightness, null);
        rescale.filter(src2, dest);

        return dest;
    }

    private static BufferedImage getContrastImage(
            BufferedImage src, int contrast)
            throws Exception
    {
        BufferedImage dest;
        Graphics2D g2d;

        dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                    BufferedImage.TYPE_INT_RGB);
        g2d = dest.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();

        int w, h;
        Color c;
        int color;
        int alpha, red, green, blue;
        double fact;

        w = dest.getWidth();
        h = dest.getHeight();


        fact = (Math.tan ((contrast/100.0 + 1) * Math.PI/4) );
        for (int i=0; i<h; i++) {
            for (int j=0; j<w; j++) {
                c = new Color(dest.getRGB(j, i));

                alpha = c.getAlpha();
                red = c.getRed();
                green = c.getGreen();
                blue = c.getBlue();

                red = (int)((red - 127.0) * fact + 127.0);
                green = (int)((green - 127.0) * fact + 127.0);
                blue = (int)((blue - 127.0) * fact + 127.0);

                if (red>255) red = 255;
                if (red<0) red = 0;
                if (green>255) green = 255;
                if (green<0) green = 0;
                if (blue>255) blue = 255;
                if (blue<0) blue = 0;

                color = new Color(red, green, blue, alpha).getRGB();

                dest.setRGB(j, i, color);
            }
        }

        return dest;
    }





}
