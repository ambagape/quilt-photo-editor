package qp.control;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import qp.DS;
import qp.Exceptions;
import qp.Logger;
import qp.control.types.*;
import qp.database.Patterns;
import qp.design.Generals;
import qp.design.constants.Colors;
import qp.design.constants.Fonts;


/**
 *
 * @author Maira57
 */
public class ProcessImage {
    
    private static final int MAX_SHAPES = 32;

    

    public static Object[] process_image(
            final BufferedImage original,
            QPattern pattern_, int num_x_tiles, int num_colors,
            boolean grayscale, boolean sepia,
            ArrayList<Fabric> fabrics)
            throws Exception
    {
        ArrayList<RGB8> palette;
        short[][] indices;
        short[][] tiles;
        GraphicLabel[] labels;
        
        Object[] result;

        int w = original.getWidth();
        int h = original.getHeight();

        int pattern_w = pattern_.w;
        int pattern_h = pattern_.h;
        double pattern_scale = pattern_.scale;
        short[] merge_ = pattern_.e;

        short[][] pattern;
        pattern = new short[pattern_w][pattern_h];
        for (int j=0; j<pattern_h; j++) {
            for (int i=0; i<pattern_w; i++) {
                pattern[i][j] = pattern_.p[j*pattern_w + i];
            }
        }
        short[] merge = merge_;

        double aspect = (double)(w)/(double)(h);
        double pattern_aspect = (double)(pattern_w)/(double)(pattern_h);

        // count the number of shapes in the pattern
        int num_shapes = 0;
        for (int j=0; j<pattern_h; j++) {
            for (int i=0; i<pattern_w; i++) {
                int x = pattern[i][j];
                if (x >= MAX_SHAPES) x = 0;
                if (x >= num_shapes) num_shapes = x+1;
            }
        }

        num_x_tiles = Math.max((int)(num_x_tiles/pattern_.shapes_divisor), 1);

        int num_y_tiles = (int)(num_x_tiles/aspect*pattern_aspect/pattern_scale+0.5);
        if (1 > num_y_tiles) num_y_tiles = 1;
        

        int new_w = num_x_tiles * pattern_w;
        int new_h = num_y_tiles * pattern_h;
        
        Logger.printOut("%d %d (tiles)\n",
            new_w, new_h);
        
        BufferedImage img0;
        int[][] img;
        
        img0 = Generals.scaleImage(original, new_w, new_h, false);
        img = Generals.getMatrixFromImage(img0);

        ArrayList<GraphicLabel> pattern_labels;
        pattern_labels = new ArrayList<GraphicLabel>();
        for (int i=0; i<num_shapes; i++) {
            pattern_labels.add(new GraphicLabel());
        }
        ArrayList<Integer> pixel_count;
        pixel_count = new ArrayList<Integer>();
        for (int i=0; i<num_shapes; i++) {
            pixel_count.add(0);
        }
        for (int j=0; j<pattern_h; j++) {
            for (int i=0; i<pattern_w; i++) {
                int k = pattern[i][j];
                pattern_labels.get(k).x += i/(double)(new_w);
                pattern_labels.get(k).y += j/(double)(new_h);
                pixel_count.set(k, pixel_count.get(k)+1);
            }
        }
        for (int i=0; i<num_shapes; i++) {
            pattern_labels.get(i).x /= pixel_count.get(i);
            pattern_labels.get(i).y /= pixel_count.get(i);
        }

        short[] merge_shapes = new short[num_shapes];
        int[] merge_count = new int[num_shapes];

        int shape_offset = 0;

        int num_merge;

        tiles = new short[new_w][new_h];

        for (int jj=0; jj<num_y_tiles; jj++) {
            int y_offset = jj*pattern_h;
            
            for (int ii=0; ii<num_x_tiles; ii++) {
                int x_offset = ii*pattern_w;
                num_merge = 0;
                
                for (int i=0; i<num_shapes; i++) merge_shapes[i] = -1;
                
                for (int i=0; i<pattern_w; i++)
                    if (jj!=0 && merge[i]!=0) {
                        int p = pattern[i][0];
                        if (-1 == merge_shapes[p]) {
                            merge_shapes[p] = tiles[x_offset+i][y_offset-1];
                            num_merge++;
                        }
                    }
                
                for (int i=0; i<pattern_h-1; i++)
                    if (ii!=0 && merge[i+pattern_w]!=0) {
                        int p = pattern[0][i+1];
                        if (-1 == merge_shapes[p]) {
                            merge_shapes[p] = tiles[x_offset-1][y_offset+i+1];
                            num_merge++;
                        }
                    }

                for (int i=1; i<num_shapes; i++) {
                    merge_count[i] = 0;
                }
                
                merge_count[0] = (merge_shapes[0]!=-1) ? 1 : 0;
                
                for (int i=1; i<num_shapes; i++) {
                    if (merge_shapes[i] != -1) {
                        merge_count[i] = merge_count[i-1]+1;
                    }
                    else {
                        merge_count[i] = merge_count[i-1];
                    }
                }

                for (int j=0; j<pattern_h; j++) {
                    for (int i=0; i<pattern_w; i++) {
                        short p = pattern[i][j];
                        
                        if (merge_shapes[p] != -1) {
                            p = merge_shapes[p];
                        }
                        else {
                            p += shape_offset - merge_count[p];
                        }
                        tiles[i+x_offset][j+y_offset] = p;
                    } // end for i
                } // end for j
                
                shape_offset += num_shapes - num_merge;
            } // end for ii
            
            QuiltedPhoto.showProcessProgress(0.45*(double)(jj+1)/num_y_tiles);
        } // end for jj

        int max_shape = shape_offset + num_shapes;

        int num_total_shapes = shape_offset;

        int[] shape_pixels = new int[max_shape];
        RGBd[] shape_colors = new RGBd[max_shape];
        for (int i=0; i<max_shape; i++) {
            shape_colors[i] = new RGBd();
        }

        for (int i=0; i<max_shape; i++) {
            shape_pixels[i] = 0;
            shape_colors[i].r = 0;
            shape_colors[i].g = 0;
            shape_colors[i].b = 0;
        }

        Color c1;
        for (int j=0; j<new_h; j++) {
            for (int i=0; i<new_w; i++) {
                int index = tiles[i][j];
                assert(index >= 0);
                assert(index < max_shape);
                if (index < 0 || index >= max_shape) {
                    continue;
                }

                c1 = new Color(img[i][j]);
                shape_colors[index].r += c1.getRed();
                shape_colors[index].g += c1.getGreen();
                shape_colors[index].b += c1.getBlue();
                shape_pixels[index]++;
            }
        }

        for (int i=0; i<max_shape; i++) {
            shape_colors[i].r = (int)(shape_colors[i].r/shape_pixels[i] + 0.5);
            shape_colors[i].g = (int)(shape_colors[i].g/shape_pixels[i] + 0.5);
            shape_colors[i].b = (int)(shape_colors[i].b/shape_pixels[i] + 0.5);
        }       

        for (int j=0; j<new_h; j++) {
            for (int i=0; i<new_w; i++) {

                int index = tiles[i][j];
                
                if (index < 0 || index >= max_shape) {
                    continue;
                }

                img[i][j] =  new Color((int)(shape_colors[index].r+0.5),
                                    (int)(shape_colors[index].g+0.5),
                                    (int)(shape_colors[index].b+0.5)).getRGB();
            }
        }

        indices = new short[new_w][new_h];
        palette = new ArrayList<RGB8>();

        result = quantize_color(
                img, num_colors, palette, indices, grayscale, sepia);
        img = (int[][])result[0];
        palette = (ArrayList<RGB8>)result[1];
        indices = (short[][])result[2];

        labels = new GraphicLabel[num_total_shapes];
        for (int i=0; i<num_total_shapes; i++) {
            labels[i] = new GraphicLabel();
        }
        

        for (int jj=0; jj<num_y_tiles; jj++) {
            int j_offset = pattern_h*jj;
            double y_offset = (double)(j_offset)/new_h;
            
            for (int ii=0; ii<num_x_tiles; ii++) {
                int i_offset = pattern_w*ii;
                double x_offset = (double)(i_offset)/new_w;
                
                for (int j=0; j<pattern_h; j++) {
                    for (int i=0; i<pattern_w; i++) {
                        int k = tiles[i+i_offset][j+j_offset];
                        
                        if (k < 0 || k >= max_shape) {
                            continue;
                        }
                        
                        int p = pattern[i][j];
                        
                        if (labels[k].index == -1) {
                            labels[k].x = pattern_labels.get(p).x + x_offset;
                            labels[k].y = pattern_labels.get(p).y + y_offset;
                            labels[k].index = indices[i+i_offset][j+j_offset];
                        }
                    }
                }
            }
            
            QuiltedPhoto.showProcessProgress(0.45+0.45*(double)(jj+1)/num_y_tiles);
        }

        if (fabrics != null) {
            BufferedImage f;
        
            for (int j=0; j<new_h; j++) {
                for (int i=0; i<new_w; i++) {
                    int index = indices[i][j];
                    f = fabrics.get(index).img;

                    int fw = f.getWidth(); int fh = f.getHeight();
                    
                    img[i][j] = f.getRGB(i%fw, j%fh);
                }
                
                if (j%100 == 0) {
                    QuiltedPhoto.showProcessProgress(0.9+0.1*(double)(j+1)/new_h);
                }
            }
        }

        BufferedImage processed;
        
        processed = new BufferedImage(
                img.length, img[0].length, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<img.length; i++) {
            for (int j=0; j<img[0].length; j++) {
                processed.setRGB(i, j, img[i][j]);
            }
        }
        
        return new Object[] {
            num_total_shapes,
            processed,
            palette,
            indices,
            tiles,
            labels,
            new int[] { num_x_tiles, num_y_tiles }
        };
    }

    public static Object[] process_image_cfqp(
            final BufferedImage original,
            short[][] patterns,
            int[] pattern_shapes,
            int num_x_tiles,
            int num_colors,
            boolean grayscale, boolean sepia,
            ArrayList<Fabric> fabrics)
            throws Exception
    {
        BufferedImage processed;
        GraphicLabel[] order_labels;
        short[] pattern;
        int[][] img_r, img_g, img_b;
        Color c1;
        int showStep;
        
        Object[] result;

        int pattern_w = 128;
        int pattern_h = 128;

        num_x_tiles /= 2;

        double aspect = (double)(original.getWidth())/original.getHeight();

        int num_y_tiles = (int)(num_x_tiles/aspect+0.5);
        if (1 > num_y_tiles) num_y_tiles = 1;

        int num_cfqp_patterns = 24;

        GraphicLabel[] letter_labels;
        letter_labels = new GraphicLabel[num_cfqp_patterns*6+10];
        for (int i=0; i<letter_labels.length; i++) {
            letter_labels[i] = new GraphicLabel(0, 0, 0, 0);
        }

        for (int i=0; i<num_cfqp_patterns; i++) {
            int[] count;
            
            pattern = patterns[i];
            count = new int[6];
            
            for (int k=0; k<6; k++) {
                count[k] = 0;
                letter_labels[i*6+k].order = k;
            }
            for (int y=0; y<128; y++) {
                for (int x=0; x<128; x++) {
                    int k = pattern[y*128+x];
                    letter_labels[i*6+k].x += x;
                    letter_labels[i*6+k].y += y;
                    count[k]++;
                }
            }
            for (int k=0; k<pattern_shapes[i]; k++) {
                letter_labels[i*6+k].x /= count[k]*num_x_tiles*128;
                letter_labels[i*6+k].y /= count[k]*num_y_tiles*128;
            }
        }

        short[][] patterns_ = patterns;

        BufferedImage img;
        img = original;

        int new_w = num_x_tiles*pattern_w;
        int new_h = num_y_tiles*pattern_h;
        img = Generals.scaleImage(img, new_w, new_h, false);

        img_r = new int[new_w][new_h];
        img_g = new int[new_w][new_h];
        img_b = new int[new_w][new_h];
        for (int j=0; j<new_h; j++) {
            for (int i=0; i<new_w; i++) {
                c1 = new Color(img.getRGB(i, j));
                img_r[i][j] = c1.getRed();
                img_g[i][j] = c1.getGreen();
                img_b[i][j] = c1.getBlue();
            }
        }

        int shape_offset = 0;

        short[][] pattern_indices = new short[num_x_tiles*128][num_y_tiles*128];

        order_labels = new GraphicLabel[num_x_tiles*num_y_tiles*6];
        for (int i=0; i<order_labels.length; i++) {
            order_labels[i] = new GraphicLabel(0, 0, 0, 0);
        }

        double[] total_err;
        total_err = new double[num_cfqp_patterns];
        
        RGBd[] pal;
        int[] pix_count;
        
        pal = new RGBd[pattern_shapes[6]];
        pix_count = new int[pattern_shapes[6]];

        for (int jj=0; jj<num_y_tiles; jj++) {
            int y_offset = jj*pattern_h;
            int y_offset_ = jj*128;
            
            for (int ii=0; ii<num_x_tiles; ii++) {
                int x_offset = ii*pattern_w;
                int x_offset_ = ii*128;
                
                int i2, j2;
                
                for (int n=0; n<num_cfqp_patterns; n++) {
                    for (int i=0; i<pal.length; i++) {
                        pal[i] = new RGBd();
                    }
                    for (int i=0; i<pix_count.length; i++) {
                        pix_count[i] = 0;
                    }
                    pattern = patterns[n];
                    for (int j=0; j<pattern_h; j++) {
                        for (int i=0; i<pattern_w; i++) {
                            int k = pattern[j*pattern_w+i];
                            i2 = i + x_offset;
                            j2 = j + y_offset;
                            
                            pal[k].add(img_r[i2][j2],
                                        img_g[i2][j2],
                                        img_b[i2][j2]);
                            pix_count[k]++;
                        }
                    }
                    for (int i=0; i<pattern_shapes[n]; i++) {
                        pal[i].mul(1.0/pix_count[i]);
                    }

                    double err = 0;
                    for (int j=0; j<pattern_h; j++) {
                        for (int i=0; i<pattern_w; i++) {
                            int k = pattern[j*pattern_w+i];
                            i2 = i + x_offset;
                            j2 = j + y_offset;
                            
                            double dr = img_r[i2][j2] - pal[k].r;
                            double dg = img_g[i2][j2] - pal[k].g;
                            double db = img_b[i2][j2] - pal[k].b;
                            err += Math.sqrt(dr*dr*0.3 + dg*dg*0.59 + db*db*0.11);
                        }
                    }
                    total_err[n] = err;
                } // for n

                int imin = 0;
                double min_err = 9e9;
                for (int n2=0; n2<6; n2++) {
                    if (total_err[n2] < min_err) {
                        min_err = total_err[n2];
                        imin = n2;
                    }
                }

                // now that we've selected the best pattern, apply it
                for (int i=0; i<pal.length; i++) {
                    pal[i] = new RGBd();
                }
                for (int i=0; i<pix_count.length; i++) {
                    pix_count[i] = 0;
                }
                pattern = patterns_[imin];
                for (int j=0; j<128; j++) {
                    for (int i=0; i<128; i++) {
                        int k = pattern[j*128+i];
                        i2 = (int)(i+x_offset);
                        j2 = (int)(j+y_offset);
                        
                        pal[k].add(img_r[i2][j2],
                                    img_g[i2][j2],
                                    img_b[i2][j2]);
                        pix_count[k]++;
                    }
                }
                for (int i=0; i<pattern_shapes[imin]; i++) {
                    pal[i].mul(1.0/pix_count[i]);
                }

                for (int k=0; k<pattern_shapes[imin]; k++) {
                    order_labels[k + shape_offset].set(letter_labels[imin*6+k]);
                    order_labels[k + shape_offset].x
                            += (double)x_offset_/(num_x_tiles*128.0);
                    order_labels[k + shape_offset].y
                            += (double)y_offset_/(num_y_tiles*128.0);
                }

                for (int j=0; j<128; j++) {
                    for (int i=0; i<128; i++) {
                        int k = pattern[j*pattern_w+i];
                        pattern_indices[i+x_offset_][j+y_offset_]
                            = (short)(k + shape_offset);

                        i2 = i + x_offset;
                        j2 = j + y_offset;

                        img_r[i2][j2] = (int)(pal[k].r+0.5);
                        img_g[i2][j2] = (int)(pal[k].g+0.5);
                        img_b[i2][j2] = (int)(pal[k].b+0.5);
                    }
                }

                shape_offset += pattern_shapes[imin];            
            } // end for ii
            
            QuiltedPhoto.showProcessProgress(0.8*(double)(jj+1)/num_y_tiles);
        } // end for jj
        
        for (int j=0; j<new_h; j++) {
            for (int i=0; i<new_w; i++) {
                img.setRGB(i, j, new Color(
                                    img_r[i][j], img_g[i][j], img_b[i][j])
                                .getRGB());
            }
        }


        int num_total_shapes = shape_offset;

        short[][] color_indices;
        ArrayList<RGB8> palette;
        
        color_indices = new short[new_w][new_h];
        palette = new ArrayList<RGB8>();

        result = quantize_color(
            Generals.getMatrixFromImage(img),
            num_colors, palette, color_indices,
            grayscale, sepia);
        img = Generals.getImageFromMatrix((int[][])result[0]);
        palette = (ArrayList<RGB8>)result[1];
        color_indices = (short[][])result[2];
        

        for (int i=0; i<order_labels.length; i++) {
            GraphicLabel l;
            l = order_labels[i];
            l.index = color_indices[(int)(l.x*new_w)][(int)(l.y*new_h)];
        }

        if (fabrics != null) {
            BufferedImage f;

            showStep = (new_h - 0) / 10;
            for (int j=0; j<new_h; j++) {
                for (int i=0; i<new_w; i++) {
                    f = fabrics.get(color_indices[i][j]).img;

                    int fw = f.getWidth(); int fh = f.getHeight();
                    
                    img.setRGB(i, j, f.getRGB(i%fw, j%fh));
                }
                
                if (j%showStep == 0) {
                    QuiltedPhoto.showProcessProgress(0.8+0.2*(double)(j+1)/new_h);
                }
            }
        }

        processed = img;
            

        return new Object[] {
            num_total_shapes,
            processed,
            palette,
            color_indices,
            pattern_indices,
            order_labels,
            new int[] { num_x_tiles, num_y_tiles }
        };
    }
    
    public static Object[] process_image_qbnpa(
            final BufferedImage original,
            QPattern pattern_,
            int detail, int num_colors,
            final int smooth,
            boolean grayscale, boolean sepia,
            ArrayList<Fabric> fabrics)
            throws Exception
    {
        BufferedImage processed;
        ArrayList<RGB8> palette;
        short[][] indices;
        short[][] shape_indices;
        GraphicLabel[] order_labels;
        int showStep;
        
        Object[] result;
        
        int num_x_tiles = detail;

        int w = original.getWidth();
        int h = original.getHeight();

        int pattern_w = pattern_.w;
        int pattern_h = pattern_.h;
        double pattern_scale = pattern_.scale;
        short[] merge_ = pattern_.e;

        double aspect = (double)(w)/(double)(h);
        double pattern_aspect = (double)(pattern_w)/(double)(pattern_h);

        short[][] pattern;
        pattern = new short[pattern_w][pattern_h];
        for (int i=0; i<pattern_w; i++) {
            for (int j=0; j<pattern_h; j++) {
                pattern[i][j] = pattern_.p[j*pattern_w + i];
            }
        }
        short[] merge = merge_;

        // count the number of shapes in the pattern
        int num_shapes = 0;
        for (int j=0; j<pattern_h; j++) {
            for (int i=0; i<pattern_w; i++) {
                int x = pattern[i][j];
                if (x >= MAX_SHAPES) x = 0;
                if (x >= num_shapes) num_shapes = x+1;
            }
        }

        num_x_tiles = Math.max((int)(num_x_tiles/pattern_.shapes_divisor), 1);

        int num_y_tiles = (int)(num_x_tiles/aspect*pattern_aspect/pattern_scale+0.5);
        if (1 > num_y_tiles) {
            num_y_tiles = 1;
        }

        BufferedImage img0 = original;
        int[][] img;

        int new_w = num_x_tiles*pattern_w;
        int new_h = num_y_tiles*pattern_h;
        img0 = Generals.scaleImage(img0, new_w, new_h, false);
        img = Generals.getMatrixFromImage(img0);

        GraphicLabel[] pattern_labels;
        pattern_labels = new GraphicLabel[num_shapes];
        for (int i=0; i<pattern_labels.length; i++) {
            pattern_labels[i] = new GraphicLabel();
        }
        int[] pixel_count;
        pixel_count = new int[num_shapes];
        for (int j=0; j<pattern_h; j++) {
            for (int i=0; i<pattern_w; i++) {
                int k = pattern[i][j];
                pattern_labels[k].x += i/(double)(new_w);
                pattern_labels[k].y += j/(double)(new_h);
                pixel_count[k]++;
            }
        }
        for (int i=0; i<num_shapes; i++) {
            pattern_labels[i].x /= pixel_count[i];
            pattern_labels[i].y /= pixel_count[i];
        }

        int[] merge_shapes;
        int[] merge_count;
        int shape_offset;
        int num_merge;
        
        merge_shapes = new int[num_shapes];
        merge_count = new int[num_shapes];

        shape_offset = 0;
        shape_indices = new short[new_w][new_h];

        for (int jj=0; jj<num_y_tiles; jj++) {
            int y_offset = jj*pattern_h;
            
            for (int ii=0; ii<num_x_tiles; ii++) {
                int x_offset = ii*pattern_w;
                num_merge = 0;
                
                for (int i=0; i<num_shapes; i++) {
                    merge_shapes[i] = -1;
                }
                
                for (int i=0; i<pattern_w; i++) {
                    if (jj!=0 && merge[i]!=0) {
                        int p = pattern[i][0];
                        if (-1 == merge_shapes[p]) {
                            merge_shapes[p] = shape_indices[x_offset+i][y_offset-1];
                            num_merge++;
                        }
                    }
                }
                
                for (int i=0; i<pattern_h-1; i++) {
                    if (ii!=0 && merge[i+pattern_w]!=0) {
                        int p = pattern[0][i+1];
                        if (-1 == merge_shapes[p]) {
                            merge_shapes[p] = shape_indices[x_offset-1][y_offset+i+1];
                            num_merge++;
                        }
                    }
                }

                for (int i=1; i<num_shapes; i++) merge_count[i] = 0;
                merge_count[0] = (merge_shapes[0]!=-1) ? 1 : 0;
                for (int i=1; i<num_shapes; i++) {
                    if (merge_shapes[i] != -1) {
                        merge_count[i] = merge_count[i-1]+1;
                    }
                    else {
                        merge_count[i] = merge_count[i-1];
                    }
                }

                for (int j=0; j<pattern_h; j++) {
                    for (int i=0; i<pattern_w; i++) {
                        int p = pattern[i][j];
                        
                        if (merge_shapes[p] != -1) {
                            p = merge_shapes[p];
                        } else {
                            p += shape_offset - merge_count[p];
                        }
                        shape_indices[i+x_offset][j+y_offset] = (short)p;
                    } // end for i
                } // end for j
                shape_offset += num_shapes - num_merge;
            } // end for ii
            
            QuiltedPhoto.showProcessProgress(0.25*(double)(jj+1)/num_y_tiles);
        } // end for jj

        int max_shape = shape_offset + num_shapes;

        int num_total_shapes = shape_offset;
        
        int[] shape_pixels = new int[max_shape];
        int[] shape_r, shape_g, shape_b;
        shape_r = new int[max_shape];
        shape_g = new int[max_shape];
        shape_b = new int[max_shape];

        for (int i=0; i<max_shape; i++) {
            shape_pixels[i] = 0;
            shape_r[i] = 0;
            shape_g[i] = 0;
            shape_b[i] = 0;
        }

        Color c1;

        showStep = (new_h - 0) / 10;
        for (int j=0; j<new_h; j++) {
            for (int i=0; i<new_w; i++) {
                int index = shape_indices[i][j];

                c1 = new Color(img[i][j]);
                shape_r[index] += c1.getRed();
                shape_g[index] += c1.getGreen();
                shape_b[index] += c1.getBlue();
                shape_pixels[index]++;
            }
            
            if (j%showStep == 0) {
                QuiltedPhoto.showProcessProgress(0.25+0.10*(double)(j+1)/new_h);
            }
        }

        for (int i=0; i<max_shape; i++) {
            shape_r[i] = (int)((double)shape_r[i]/shape_pixels[i] + 0.5);
            shape_g[i] = (int)((double)shape_g[i]/shape_pixels[i] + 0.5);
            shape_b[i] = (int)((double)shape_b[i]/shape_pixels[i] + 0.5);
        }       

        showStep = (new_h - 0) / 10;
        for (int j=0; j<new_h; j++) {
            for (int i=0; i<new_w; i++) {
                int index = shape_indices[i][j];

                c1 = new Color((int)(shape_r[index]+0.5),
                                (int)(shape_g[index]+0.5),
                                (int)(shape_b[index]+0.5));
                img[i][j] = c1.getRGB();
            }
            
            if (j%showStep == 0) {
                QuiltedPhoto.showProcessProgress(0.35+0.10*(double)(j+1)/new_h);
            }
        }
        
        indices = new short[new_w][new_h];
        palette = new ArrayList<RGB8>();

        result = quantize_color(
                img, num_colors, palette, indices, grayscale, sepia);
        img = (int[][])result[0];
        palette = (ArrayList<RGB8>)result[1];
        indices = (short[][])result[2];

        order_labels = new GraphicLabel[num_total_shapes];
        for (int i=0; i<order_labels.length; i++) {
            order_labels[i] = new GraphicLabel();
        }

        for (int jj=0; jj<num_y_tiles; jj++) {
            int j_offset = pattern_h*jj;
            double y_offset = (double)(j_offset)/new_h;
            
            for (int ii=0; ii<num_x_tiles; ii++) {
                int i_offset = pattern_w*ii;
                double x_offset = (double)(i_offset)/new_w;
                
                for (int j=0; j<pattern_h; j++) {
                    for (int i=0; i<pattern_w; i++) {
                        int k = shape_indices[i+i_offset][j+j_offset];
                        int p = pattern[i][j];
                        
                        if (order_labels[k].index == -1) {
                            order_labels[k].x = pattern_labels[p].x + x_offset;
                            order_labels[k].y = pattern_labels[p].y + y_offset;
                            order_labels[k].index = indices[i+i_offset][j+j_offset];
                        }
                    }
                }
            }
            
            QuiltedPhoto.showProcessProgress(0.45+0.25*(double)(jj+1)/num_y_tiles);
        }

        // now merge shapes that have the same color using floodfill
        Integer[] shape_index;

//        result = floodfill(indices);
        result = getRegions(Generals.getMatrixInts(indices));
        
        num_total_shapes = (Integer)result[0];
        shape_index = (Integer[])result[1];
//        shape_indices = (short[][])(result[2]);
        shape_indices = (short[][])Generals.getMatrixShorts((int[][])result[2]);
        
        short[] blend_count = new short[num_total_shapes];
        short[][] shape_indices_ = shape_indices;
        int smooth1 = (int)(smooth * pattern_w / 32.0 + 0.5);

        showStep = (new_h-smooth1 - smooth1) / 20;
        for (int j=smooth1; j<new_h-smooth1; j++) {
            for (int i=smooth1; i<new_w-smooth1; i++) {

                for (int k=0; k<blend_count.length; k++) {
                    blend_count[k] = 0;
                }

                short x = shape_indices_[i][j];

                for (int n=-smooth1; n<=smooth1; n++) {
                    for (int m=-smooth1; m<=smooth1; m++) {
                        blend_count[shape_indices_[i+m][j+n]]++;
                    }
                }

                short imax = x;
                int max_count = blend_count[x];
                for (int k=0; k<num_total_shapes; k++)
                    if (blend_count[k] > max_count) {
                        max_count = blend_count[k];
                        imax = (short)k;
                    }

                if (imax != x) {
                    shape_indices[i][j] = imax;
                    indices[i][j] = shape_index[imax].shortValue();
                    short t = indices[i][j];
                    
                    img[i][j] = new Color(palette.get(t).r,
                                            palette.get(t).g,
                                            palette.get(t).b).getRGB();
                }
            }
            
            if (j%showStep == 0) {
                QuiltedPhoto.showProcessProgress(0.70+0.25*(double)(j-smooth1+1)/(double)(new_h-2*smooth1));
            }
        }

        if (fabrics != null) {
            BufferedImage f;

            showStep = (new_h - 0) / 10;
            for (int j=0; j<new_h; j++) {
                for (int i=0; i<new_w; i++) {
                    f = fabrics.get(indices[i][j]).img;

                    int fw = f.getWidth(); int fh = f.getHeight();
                    
                    img[i][j] = f.getRGB(i%fw, j%fh);
                }
                
                if (j%showStep == 0) {
                    QuiltedPhoto.showProcessProgress(0.95+0.05*(double)(j+1)/new_h);
                }
            }
        }

        processed = Generals.getImageFromMatrix(img);

        return new Object[] {
            num_total_shapes,
            processed,
            palette,
            indices,
            shape_indices,
            order_labels,
            new int[] { num_x_tiles, num_y_tiles }
        };
    }

    private static Object[] floodfill(
            final short[][] index)
            throws Exception
    {
        ArrayList<Integer> shape_index;
        ArrayList<Integer> pixel_count;
        short[][] shapes;
        short[][] B;

        
        int w = index.length;
        int h = index[0].length;

        B = new short[w][h];
        shape_index = new ArrayList<Integer>();
        pixel_count = new ArrayList<Integer>();
        shapes = new short[w][h];

        // Implement a floodfill algorithm similar to that described here:
        // http://home.hccnet.nl/david.dirkse/math/floodfill.html
        // This required no recursion, and requires 1 additional byte per pixel.  

        int[] x_offset = new int[] { 1,-1, 0, 0 };
        int[] y_offset = new int[] { 0, 0,-1, 1 };
        int x, y;
        int dir;
        int current_index;
        int current_shape;
        String gotoLabel;
        
        x = 0;
        y = 0;
        dir = 0;
        current_index = 0;
        current_shape = 0;
        gotoLabel = new String();

        for (int j=0; j<h; j++) {    
            for (int i=0; i<w; i++) {
                if (gotoLabel.isEmpty()) {
                    if (B[i][j] == 7) {
                        continue;
                    }

                    x = i;
                    y = j;
                    dir = 0;
                    current_index = index[i][j];
                    shape_index.add(current_index);
                    pixel_count.add(1);
                    shapes[i][j] = (short)current_shape;

                    B[x][y] = 3;
                }

                if (gotoLabel.equals(DS.PROCESS_CONTROLLER.FF_NEXT_PIXEL)
                        || gotoLabel.isEmpty()) {
                    x += x_offset[dir];
                    y += y_offset[dir];

                    // check for bit 7 or 'bit 6'
                    if (!(x<0 || y<0 || x>=w || y>=h 
                        || index[x][y]!=current_index || (B[x][y]==7)) )
                    {

                        B[x][y] |= (dir | (1<<7));
                        shapes[x][y] = (short)current_shape;
                        pixel_count.set(current_shape,
                                        pixel_count.get(current_shape)+1);

                        if (dir != 1) {
                            dir = 0;
                        }

                        gotoLabel = DS.PROCESS_CONTROLLER.FF_NEXT_PIXEL;
                        continue;
                    }
                }

                if (gotoLabel.equals(DS.PROCESS_CONTROLLER.FF_PREV_PIXEL)
                        || gotoLabel.equals(DS.PROCESS_CONTROLLER.FF_NEXT_PIXEL)
                        || gotoLabel.isEmpty()) {
                    x -= x_offset[dir];
                    y -= y_offset[dir];
                    dir++;
                    if ((dir^1) == (B[x][y] & 0xf)) {
                        dir++;
                    }
                    if (dir > 3) {
                        dir = B[x][y]&0xf;
                        if (dir < 8) {
                            gotoLabel = DS.PROCESS_CONTROLLER.FF_PREV_PIXEL;
                            continue;
                        }
                        else {
                            gotoLabel = DS.PROCESS_CONTROLLER.FF_FILL;
                            continue;
                        }
                    }
                    else {
                        gotoLabel = DS.PROCESS_CONTROLLER.FF_NEXT_PIXEL;
                        continue;
                    }
                }

                if (gotoLabel.equals(DS.PROCESS_CONTROLLER.FF_FILL)
                        || gotoLabel.equals(DS.PROCESS_CONTROLLER.FF_NEXT_PIXEL)
                        || gotoLabel.equals(DS.PROCESS_CONTROLLER.FF_PREV_PIXEL)
                        || gotoLabel.isEmpty()) {
                    current_shape++;            
                }
                
                gotoLabel = new String();
            } // end for i
        } // end for j

        return new Object[] {
            current_shape,
            shape_index.toArray(new Integer[0]),
            shapes
        };
    }

    public static Object[] getRegions(
            int[][] bim)
            throws Exception
    {
        ArrayList<Integer> pixelsPerRegion;
        ArrayList<Integer> valueOfRegion;
        int[][] map_regions;
        int colorCrt;
        int w, h;
        int reg;
        boolean[][] map_dirty;
        ArrayList<int[]> regionStack;
        int[] location;
        int colorPhase;
        int r, c;
        int n;

        w = bim.length;
        h = bim[0].length;

        map_regions = new int[w][h];
        for (int i=0; i<h; i++) {
            for (int j=0; j<w; j++) {
                map_regions[j][i] = -1;
            }
        }
        pixelsPerRegion = new ArrayList<Integer>();
        valueOfRegion = new ArrayList<Integer>();


        // blend lonely pixels into a neighbouring color
        int lonelies;
        colorPhase = 0;
        lonelies = 0;

        for (int i=1; i<h-1; i++) {
            for (int j=1; j<w-1; j++) {
                colorCrt = bim[j][i];

                if ((colorCrt != bim[j-1][i])
                        && (colorCrt != bim[j+1][i])
                        && (colorCrt != bim[j][i-1])
                        && (colorCrt != bim[j][i+1]))
                {
                    // setting this lonely pixel to a neighbour's color
                    // (the left or the top neighbour)
                    switch (colorPhase) {
                        case 0: bim[j][i] = bim[j-1][i]; break;
                        case 1: bim[j][i] = bim[j][i-1]; break;

                        default: throw Exceptions.badSwitchBranch(colorPhase);
                    }
                    colorPhase++;
                    if (colorPhase == 2) {
                        colorPhase = 0;
                    }

                    lonelies++;
                }
            }
        }


        // init map dirty and locationsLeft
        map_dirty = new boolean[w][h];
        for (int i=0; i<h; i++) {
            for (int j=0; j<w; j++) {
                map_dirty[j][i] = false;
            }
        }
        for (int i=0; i<h; i++) {
            map_dirty[0][i] = true;
            map_dirty[w-1][i] = true;
        }
        for (int j=0; j<w; j++) {
            map_dirty[j][0] = true;
            map_dirty[j][h-1] = true;
        }

        int total_pixels;
        ArrayList<Integer> corners_c, corners_r;
        ArrayList<Boolean> corners_taken;
        
        total_pixels = w * h - (2*(h-2) + 2*w);
        corners_c = new ArrayList<Integer>();
        corners_r = new ArrayList<Integer>();
        corners_c.add(1);
        corners_r.add(1);
        for (int i=1; i<h-1; i++) {
            for (int j=1; j<w-1; j++) {
                if (bim[j][i] != bim[j-1][i] || bim[j][i] != bim[j][i-1]) {
                    corners_c.add(j);
                    corners_r.add(i);
                }
            }
        }
        corners_taken = new ArrayList<Boolean>();
        for (int i=0; i<corners_c.size(); i++) {
            corners_taken.add(false);
        }

        // set regions for all pixels
        do {
            c = -1;
            r = -1;
            for (int i=0; i<corners_c.size(); i++) {
                if (!corners_taken.get(i)) {
                    c = corners_c.get(i);
                    r = corners_r.get(i);
                    break;
                }
            }
            if (c == -1 || r == -1) {
                break;
            }


            n = 0;
            regionStack = new ArrayList<int[]>();
            reg = pixelsPerRegion.size();
            colorCrt = bim[c][r];
            regionStack.add(new int[] { c, r });
            map_dirty[c][r] = true;

            while (!regionStack.isEmpty()) {
                location = regionStack.get(0);
                c = location[0];
                r = location[1];

                n++;
                map_regions[c][r] = (short)reg;
                total_pixels--;

                if (!map_dirty[c-1][r]) {
                    if (bim[c-1][r] == colorCrt) {
                        regionStack.add(new int[] { c-1, r });
                        map_dirty[c-1][r] = true;
                    }
                }
                if (!map_dirty[c+1][r]) {
                    if (bim[c+1][r] == colorCrt) {
                        regionStack.add(new int[] { c+1, r });
                        map_dirty[c+1][r] = true;
                    }
                }
                // no need to scan (c, r-1)
                // ...
                if (!map_dirty[c][r+1]) {
                    if (bim[c][r+1] == colorCrt) {
                        regionStack.add(new int[] { c, r+1 });
                        map_dirty[c][r+1] = true;
                    }
                }

                regionStack.remove(0);
            }

            pixelsPerRegion.add(n);
            valueOfRegion.add(colorCrt);
            
            for (int i=corners_c.size()-1; i>=0; i--) {
                if (map_dirty[corners_c.get(i)][corners_r.get(i)]) {
                    corners_taken.set(i, true);
                }
            }
        }
        while (true);

        
        // it's a rough completion of the border, but a good enough one,
        // given the average resolution of the images
        n = 0;
        reg = pixelsPerRegion.size();
        for (int i=1; i<h-1; i++) {
            map_regions[0][i] = (short)reg;
            map_regions[w-1][i] = (short)reg;
            n += 2;
        }
        for (int j=0; j<w; j++) {
            map_regions[j][0] = (short)reg;
            map_regions[j][h-1] = (short)reg;
            n += 2;
        }
        pixelsPerRegion.add(n);
        valueOfRegion.add((int)bim[0][0]);
        

        return new Object[] {
            valueOfRegion.size(),
            valueOfRegion.toArray(new Integer[0]),
            map_regions
        };
    }
    
    public static BufferedImage generate_print_pattern(
            Object[] processedData,
            double x0, double x1, double y0, double y1,
            int w, int h,
            int page_x, int page_y,
            int new_design_w, int new_design_h,
            double cellScale,
            boolean withFilling)
            throws Exception
    {
        short[][] index;
        BufferedImage processedImage;
        short[] indicesCorresp;
        GraphicLabel[] order_labels;
        ArrayList<Fabric> fabrics;
        short[][] colors;
        int[][] pattern_ext;
        boolean use_cfqp;
        boolean use_qbnpa;
        int grid_angle;
        
        index = (short[][])processedData[0];
        processedImage = (BufferedImage)processedData[1];
        indicesCorresp = (short[])processedData[2];
        order_labels = (GraphicLabel[])processedData[3];
        fabrics = (ArrayList<Fabric>)processedData[4];
        colors = (short[][])processedData[5];
        pattern_ext = (int[][])processedData[6];
        use_cfqp = (Boolean)processedData[7];
        use_qbnpa = (Boolean)processedData[8];
        grid_angle = (Integer)processedData[9];


        BufferedImage pattern;
        Graphics2D g2d;
        int[][] tmp;
        int tw, th;
        int pw_ext, ph_ext;
        int black;
        String[] labels;

        
        black = 0xFF000000;
        labels = DS.PROCESS_CONTROLLER.patternLabels();

        pattern = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2d = pattern.createGraphics();

        tw = w-1;
        th = h-1;

        if (use_cfqp || use_qbnpa || grid_angle!=0) {
            int iw = index.length;
            int ih = index[0].length;
            int i0 = (int)((iw-1)*x0), i1 = (int)((iw-1)*x1);
            int j0 = (int)((ih-1)*y0), j1 = (int)((ih-1)*y1);
            int di = i1-i0+1;
            int dj = j1-j0+1;
            
            tmp = new int[di][dj];
            for (int j=0; j<dj; j++) {
                for (int i=0; i<di; i++) {
                    tmp[i][j] = index[i+i0][j+j0];
                }
            }

            // resize with 'nearest interpolation'
            tmp = Generals.resizeMatrix4(tmp, di, dj, tw, th);
        }
        else {
            tmp = new int[tw][th];

            pw_ext = pattern_ext.length;
            ph_ext = pattern_ext[0].length;

            for (int i=0; i<tw; i++) {
                for (int j=0; j<th; j++) {
                    tmp[i][j] = pattern_ext[i % pw_ext][j % ph_ext];
                }
            }
        }
        
        
        if (withFilling) {
            BufferedImage img;
            int iw = index.length;
            int ih = index[0].length;
            int i0 = (int)((iw-1)*x0), i1 = (int)((iw-1)*x1);
            int j0 = (int)((ih-1)*y0), j1 = (int)((ih-1)*y1);
            int di = i1-i0+1;
            int dj = j1-j0+1;

            if (fabrics == null) {
                img = Generals.scaleImage(
                        processedImage.getSubimage(
                            i0,
                            j0,
                            Math.min(di, processedImage.getWidth()-i0),
                            Math.min(dj, processedImage.getHeight()-j0)),
                        tw, th,
                        false);

                g2d.drawImage(
                        img,
                        0, 0,
                        null);
            }
            else {
                BufferedImage f;
                int[][] tmp2;

                pattern = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);

                tmp2 = new int[di][dj];
                for (int j=0; j<dj; j++) {
                    for (int i=0; i<di; i++) {
                        tmp2[i][j] = colors[i+i0][j+j0];
                    }
                }
                // resize with 'nearest interpolation'
                tmp2 = Generals.resizeMatrix5(tmp2, di, dj, tw, th);

                for (int i=0; i<tw; i++) {
                    for (int j=0; j<th; j++) {
                        int idx = tmp2[i][j];
                        f = fabrics.get(idx).img;

                        int fw = f.getWidth(); int fh = f.getHeight();

                        pattern.setRGB(i, j, f.getRGB(i%fw, j%fh));
                    }
                }
            }

            // for printing-unfilled pattern debug, comment the following line
            return pattern;
        }

        
        for (int j=0; j<th; j++) {
            for (int i=0; i<tw; i++) {
                int x = tmp[i][j];

                if (x == 1) {
                    pattern.setRGB(i, j, black);
                }
            }
        }

        if (x0 == 0.0) {
            for (int j=0; j<h; j++) {
                pattern.setRGB(0, j, black);
                pattern.setRGB(1, j, black);
            }
        }

        if (y0 == 0.0) {
            for (int i=0; i<w; i++) {
                pattern.setRGB(i, 0, black);
                pattern.setRGB(i, 1, black);
            }
        }

        if (x1 == 1.0) {
            for (int j=0; j<h; j++) {
                pattern.setRGB(w-1, j, black);
                pattern.setRGB(w-2, j, black);
            }
        }

        if (y1 == 1.0) {
            for (int i=0; i<w; i++) {
                pattern.setRGB(i, h-1, black);
                pattern.setRGB(i, h-2, black);
            }
        }

        
        // now, add numbers and/or letters
        String buffer;
        float fontSize;

        g2d.setColor(Colors.black);

        if (order_labels[0].order != -1) {
            fontSize = (float)(cellScale*40.0/100.0/3.0);
        }
        else {
            fontSize = (float)(cellScale*40.0/100.0);
        }
        g2d.setFont(g2d.getFont().deriveFont(fontSize));

        for (int i=0; i<order_labels.length; i++) {
            if (order_labels[i].index == -1) {
                continue;
            }

            int x, y;

            if (use_cfqp || use_qbnpa || grid_angle!=0) {
                x = (int)((order_labels[i].x-x0)*w/(x1-x0) + 0.5);
                y = (int)((order_labels[i].y-y0)*h/(y1-y0) + 0.5);
            }
            else {
//                x = (int)((order_labels[i].x)*design_w + 0.5);
//                y = (int)((order_labels[i].y)*design_h + 0.5);
                
                x = (int)((order_labels[i].x)*new_design_w + 0.5) - page_x;
                y = (int)((order_labels[i].y)*new_design_h + 0.5) - page_y;
                
//                x = (int)((order_labels[i].x-x0)*w/(x1-x0) + 0.5);
//                y = (int)((order_labels[i].y-y0)*h/(y1-y0) + 0.5);
            }

            int n = indicesCorresp[order_labels[i].index]+1;

            if (order_labels[i].order != -1) {
                buffer = String.format(
                            DS.PROCESS_CONTROLLER.patternLabelExtended,
                            labels[order_labels[i].order],
                            n);
            }
            else {
                buffer = String.format(
                            DS.PROCESS_CONTROLLER.patternLabelSimple,
                            n);
            }

            x -= g2d.getFontMetrics().stringWidth(buffer)/2;

            g2d.drawString(buffer, x, y + g2d.getFontMetrics().getHeight()/4);
        }
        

//        for (int j=0; j<th; j++) {
//            for (int i=0; i<tw; i++) {
//                if ((i + start_x) % pw_ext2 == 0
//                        && (j + start_y) % ph_ext2 == 0)
//                {
//                    pattern.setRGB(i, j, 0xFFAABBCC);
//                    if (j<th-2 && i<tw-2) {
//                        pattern.setRGB(i, j+1, 0xFFAABBCC);
//                        pattern.setRGB(i+1, j, 0xFFAABBCC);
//                        pattern.setRGB(i+1, j+1, 0xFFAABBCC);
//                    }
//                }
//            }
//        }
        
        return pattern;
    }
    
    public static BufferedImage generate_print_pattern0(
            Object[] processedData,
            double x0, double x1, double y0, double y1,
            int w,
            int h,
            double cellScale,
            boolean withFilling)
            throws Exception
    {
        short[][] index;
        BufferedImage processedImage;
        short[] indicesCorresp;
        GraphicLabel[] order_labels;
        ArrayList<Fabric> fabrics;
        short[][] colors;
        
        index = (short[][])processedData[0];
        processedImage = (BufferedImage)processedData[1];
        indicesCorresp = (short[])processedData[2];
        order_labels = (GraphicLabel[])processedData[3];
        fabrics = (ArrayList<Fabric>)processedData[4];
        colors = (short[][])processedData[5];

        assert(x0 < x1 && y0 < y1);
        
        int iw = index.length;
        int ih = index[0].length;
        int i0=(int)((iw-1)*x0), i1=(int)((iw-1)*x1);
        int j0=(int)((ih-1)*y0), j1=(int)((ih-1)*y1);
        int di = i1-i0+1;
        int dj = j1-j0+1;

        BufferedImage pattern;
        Graphics2D g2d;
        int[][] tmp;
        int black;
        String[] labels;
        
        black = 0xFF000000;
        labels = DS.PROCESS_CONTROLLER.patternLabels();
        
        pattern = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2d = pattern.createGraphics();

        int tw = w-1;
        int th = h-1;


        tmp = new int[di][dj];
        for (int j=0; j<dj; j++) {
            for (int i=0; i<di; i++) {
                tmp[i][j] = index[i+i0][j+j0];
            }
        }
        
        // resize with 'nearest interpolation'
        tmp = Generals.resizeMatrix4_0(tmp, di, dj, tw, th);

        
        if (withFilling) {
            BufferedImage img;

            if (fabrics == null) {
                img = Generals.scaleImage(
                        processedImage.getSubimage(
                            i0,
                            j0,
                            Math.min(di, processedImage.getWidth()-i0),
                            Math.min(dj, processedImage.getHeight()-j0)),
                        tw, th,
                        false);

                g2d.drawImage(
                        img,
                        0, 0,
                        null);
            }
            else {
                BufferedImage f;
                int[][] tmp2;

                pattern = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);

                tmp2 = new int[di][dj];
                for (int j=0; j<dj; j++) {
                    for (int i=0; i<di; i++) {
                        tmp2[i][j] = colors[i+i0][j+j0];
                    }
                }
                // resize with 'nearest interpolation'
                tmp2 = Generals.resizeMatrix5_0(tmp2, di, dj, tw, th);

                for (int i=0; i<tw; i++) {
                    for (int j=0; j<th; j++) {
                        int idx = tmp2[i][j];
                        f = fabrics.get(idx).img;

                        int fw = f.getWidth(); int fh = f.getHeight();

                        pattern.setRGB(i, j, f.getRGB(i%fw, j%fh));
                    }
                }
            }

            // for printing-unfilled pattern debug, comment the following line
            return pattern;
        }

        
        for (int j=0; j<th-1; j++) {
            for (int i=0; i<tw-1; i++) {
                int x = tmp[i][j];

                if (x == 1) {
                    pattern.setRGB(i, j, black);
                }
            }
        }

        if (x0 == 0.0) {
            for (int j=0; j<h; j++) {
                pattern.setRGB(0, j, black);
                pattern.setRGB(1, j, black);
            }
        }

        if (y0 == 0.0) {
            for (int i=0; i<w; i++) {
                pattern.setRGB(i, 0, black);
                pattern.setRGB(i, 1, black);
            }
        }

        if (x1 == 1.0) {
            for (int j=0; j<h; j++) {
                pattern.setRGB(w-1, j, black);
                pattern.setRGB(w-2, j, black);
            }
        }

        if (y1 == 1.0) {
            for (int i=0; i<w; i++) {
                pattern.setRGB(i, h-1, black);
                pattern.setRGB(i, h-2, black);
            }
        }

        // now, add numbers and/or letters
        if (order_labels != null) {
            String buffer;
            
            g2d.setColor(Colors.black);

            float fontSize;
            if (order_labels[0].order != -1) {
                fontSize = (float)(cellScale*40.0/100.0/3.0);
            }
            else {
                fontSize = (float)(cellScale*40.0/100.0);
            }
            g2d.setFont(g2d.getFont().deriveFont(fontSize));
            
            for (int i=0; i<order_labels.length; i++) {
                if (order_labels[i].index == -1) {
                    continue;
                }
                
                int x = (int)((order_labels[i].x-x0)*w/(x1-x0)+0.5);
                int y = (int)((order_labels[i].y-y0)*h/(y1-y0)+0.5);

                int n = indicesCorresp[order_labels[i].index]+1;
                assert(n < 256);

                if (order_labels[i].order != -1) {
                    buffer = String.format(
                                DS.PROCESS_CONTROLLER.patternLabelExtended,
                                labels[order_labels[i].order],
                                n);
                }
                else {
                    buffer = String.format(
                                DS.PROCESS_CONTROLLER.patternLabelSimple,
                                n);
                }

                x -= g2d.getFontMetrics().stringWidth(buffer)/2;

                g2d.drawString(buffer, x, y + g2d.getFontMetrics().getHeight()/4);
            }
        }
        
        return pattern;
    }
    
    public static BufferedImage generate_print_pattern_cfqp(
            Object[] processedData,
            int crazyIndex,
            int num_x_pages, int num_y_pages,
            double print_w, double print_h,
            int row, int column)
            throws Exception
    {
        short[][] index;
        short[] indicesCorresp;
        GraphicLabel[] order_labels;
        
        index = (short[][])processedData[0];
        indicesCorresp = (short[])processedData[2];
        order_labels = (GraphicLabel[])processedData[3];

        BufferedImage pattern;
        Graphics2D g2d;
        int[][] tmp;
        int black;
        String[] labels;
        int w, h;
        int pw, ph;
        int iw, ih;
        int i0, j0;
        int di, dj;
        int di_former, dj_former;
        int iCellStart, jCellStart;
        double factScale;
        double cellScale;
        boolean widthBiggerThanHeight;

        iw = index.length;
        ih = index[0].length;
        
        widthBiggerThanHeight = (print_w > print_h);
        
        pw = 128;
        ph = 128;
        crazyIndex++;
        
        if (crazyIndex == 4) {
            di = 2;
            dj = 2;
        }
        else if (crazyIndex == 6) {
            if (widthBiggerThanHeight) {
                di = 3;
                dj = 2;
            }
            else {
                di = 2;
                dj = 3;
            }
        }
        else {
            if (widthBiggerThanHeight) {
                di = crazyIndex;
                dj = 1;
            }
            else {
                di = 1;
                dj = crazyIndex;
            }
        }
        
        iCellStart = row*di;
        jCellStart = column*dj;

        i0 = iCellStart * pw;
        j0 = jCellStart * ph;
        di *= pw;
        dj *= ph;
        
        factScale = Math.min((double)print_w/(di+10), (double)print_h/(dj+10));

        di_former = di;
        dj_former = dj;
        if (i0+di > iw) {
            di = iw-i0;
        }
        if (j0+dj > ih) {
            dj = ih-j0;
        }
        
        w = (int)(di * factScale);
        h = (int)(dj * factScale);

        double x0 = (double)i0/iw;
        double x1 = (double)(i0+di)/iw;
        double y0 = (double)j0/ih;
        double y1 = (double)(j0+dj)/ih;
        
        cellScale = 72.0;
        
        black = 0xFF000000;
        labels = DS.PROCESS_CONTROLLER.patternLabels();
        
        pattern = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2d = pattern.createGraphics();

        int tw = w-1;
        int th = h-1;


        tmp = new int[di][dj];
        for (int j=0; j<dj; j++) {
            for (int i=0; i<di; i++) {
                tmp[i][j] = index[i+i0][j+j0];
            }
        }
        
        // resize with 'nearest interpolation'
        tmp = Generals.resizeMatrix4(tmp, di, dj, tw, th);

        
        for (int j=0; j<th-1; j++) {
            for (int i=0; i<tw-1; i++) {
                int x = tmp[i][j];

                if (x == 1) {
                    pattern.setRGB(i, j, black);
                }
            }
        }

        // now, add numbers and/or letters
        if (order_labels != null) {
            String buffer;
            
            g2d.setColor(Colors.black);

            float fontSize;
            if (order_labels[0].order != -1) {
                fontSize = (float)(cellScale*40.0/100.0/3.0);
            }
            else {
                fontSize = (float)(cellScale*40.0/100.0);
            }
            g2d.setFont(g2d.getFont().deriveFont(fontSize));
            
            for (int i=0; i<order_labels.length; i++) {
                if (order_labels[i].index == -1) {
                    continue;
                }
                
                int x = (int)((order_labels[i].x-x0)*w/(x1-x0)+0.5);
                int y = (int)((order_labels[i].y-y0)*h/(y1-y0)+0.5);

                int n = indicesCorresp[order_labels[i].index]+1;
                assert(n < 256);

                if (order_labels[i].order != -1) {
                    buffer = String.format(
                                DS.PROCESS_CONTROLLER.patternLabelExtended,
                                labels[order_labels[i].order],
                                n);
                }
                else {
                    buffer = String.format(
                                DS.PROCESS_CONTROLLER.patternLabelSimple,
                                n);
                }

                x -= g2d.getFontMetrics().stringWidth(buffer)/2;

                g2d.drawString(buffer, x, y + g2d.getFontMetrics().getHeight()/4);
            }
        }
        
        

        BufferedImage img2;
        int nx, ny;
        int old_pw, old_ph;
        int new_pw, new_ph;
        
        old_pw = (int)(pw * factScale);
        old_ph = (int)(ph * factScale);
        factScale = Math.min((double)print_w/(di_former), (double)print_h/(dj_former));
        w = (int)(di * factScale);
        h = (int)(dj * factScale);
        new_pw = (int)(pw * factScale);
        new_ph = (int)(ph * factScale);
        nx = di / pw;
        ny = dj / ph;

        img2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setColor(Colors.black);
        g2d.setStroke(new BasicStroke(2.0f));
        
        for (int i=0; i<nx; i++) {
            for (int j=0; j<ny; j++) {
                g2d.drawImage(
                    pattern,
                    i*new_pw+i*3, j*new_ph+j*3, i*new_pw+i*3 + old_pw, j*new_ph+j*3 + old_ph,
                    i*old_pw, j*old_ph, (i+1)*old_pw, (j+1)*old_ph,
                    null);
                
                g2d.drawRect(i*new_pw+i*3 + 1, j*new_ph+j*3 + 1, old_pw - 2, old_ph - 2);
            }
        }
        
        
        return img2;
    }

    public static double[] calculate_fabric_area(
            short[][] indices,
            int num_colors,
            double piece_size,
            int num_x_pieces)
    {
        double[] areas;
        int w, h;
        double pixel_size;

        areas = new double[num_colors];

        w = indices.length;
        h = indices[0].length;

        pixel_size = (piece_size*num_x_pieces)/w;
        pixel_size *= pixel_size;

        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                areas[indices[i][j]]++;
            }
        }

        for (int i=0; i<num_colors; i++) {
            areas[i] *= pixel_size;
        }
        
        return areas;
    }

    public static GraphicLabel[] calculate_labels(
            short[][] shape_indices,
            short[][] color_indices,
            int num_shapes)
    {    
        GraphicLabel[] labels;
        int w, h;
        int[] pixel_count;
        
        w = shape_indices.length;
        h = shape_indices[0].length;
        labels = new GraphicLabel[num_shapes];
        for (int i=0; i<labels.length; i++) {
            labels[i] = new GraphicLabel();
        }
        
        pixel_count = new int[num_shapes];

        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                int k = shape_indices[i][j];
                assert(k < num_shapes);
                if (k < 0 || k >= num_shapes) {
                    continue;
                }
                
                if (labels[k].index == -1) {
                    labels[k].index = color_indices[i][j];
                }
                labels[k].x += i + 0.5;
                labels[k].y += j + 0.5;
                pixel_count[k]++;
            }
        }

        for (int i=0; i<num_shapes; i++) {
            if (pixel_count[i] == 0) {
                labels[i].index = -1;
                labels[i].order = -1;
                continue;
            }
            labels[i].x /= (double)(pixel_count[i])*w;
            labels[i].y /= (double)(pixel_count[i])*h;
        }
        
        return labels;
    }
    
    public static Object[] quantize_color(
            int[][] img,
            int num_colors,
            ArrayList<RGB8> palette,
            short[][] pindex,
            boolean grayscale,
            boolean sepia)
            throws Exception
    {
        if (num_colors > 256) {
            num_colors = 256;
        }

        if (palette.isEmpty() && !grayscale && !sepia) {
            NODE tree;
            tree = NodeController.CreateOctreePalette(
                        img, num_colors, 8, palette, 1000);

            int size = palette.size();
            int[] palette_index = new int[size];
            int[] reverse_index = new int[size];
            for (int i=0; i<size; i++) {
                palette_index[i] = i;
            }

            sort(palette, palette_index, palette_index.length);

            for (int i=0; i<size; i++) {
                reverse_index[palette_index[i]] = i;
            }

            // some fixing
            for (int i=0; i<palette.size(); i++) {
                if (palette.get(i).r < 0) {
                    palette.get(i).r = -palette.get(i).r;
                    palette.get(i).g = -palette.get(i).g;
                    palette.get(i).b = -palette.get(i).b;
                }
            }
            
            Color c1;
            int w, h;
            w = img.length;
            h = img[0].length;
            for (int j=0; j<h; j++) {
                for (int i=0; i<w; i++) {
                    c1 = new Color(img[i][j]);
                    int index;
                    index = NodeController.get_palette_index(
                                tree,
                                c1.getRed(), c1.getGreen(), c1.getBlue(),
                                0);
                    assert(index < size);
                    int sorted_index = reverse_index[index];
                    img[i][j] =
                        new Color((int)palette.get(sorted_index).r,
                                (int)palette.get(sorted_index).g,
                                (int)palette.get(sorted_index).b).getRGB();
                    if (pindex != null) {
                        pindex[i][j] = (short)sorted_index;
                    }
                }
            }
        }
        else {
            GrayscaleQuantizer gquant;
            Object[] result;

            gquant = new GrayscaleQuantizer();
            palette = gquant.gen_palette(img, num_colors);
            result = gquant.reduce_colors(img, pindex);
            img = (int[][])result[0];
            pindex = (short[][])result[1];

            
            // set sepia filter
            if (sepia) {
                Color c1;
                RGB8 c0;
                int w, h;

                // transform image
                w = img.length;
                h = img[0].length;
                for (int j=0; j<h; j++) {
                    for (int i=0; i<w; i++) {
                        c1 = getSepiaColor(new Color(img[i][j]));
                        img[i][j] = c1.getRGB();
                    }
                }
                
                // transform palette
                for (int i=0; i<palette.size(); i++) {
                    c0 = palette.get(i);
                    c1 = new Color(c0.r, c0.g, c0.b, 255);
                    c1 = getSepiaColor(c1);
                    
                    c0.r = c1.getRed();
                    c0.g = c1.getGreen();
                    c0.b = c1.getBlue();
                }
                
            }
                  
        }

        System.gc();
        
        return new Object[] {
            img,
            palette,
            pindex
        };
    }
    
    private static Color getSepiaColor(Color c1) throws Exception {
        Color c2;
        int sepiaDepth = 20;
        int sepiaIntensity = 30;
        
        int r = c1.getRed();
        int g = c1.getGreen();
        int b = c1.getBlue();

        int grey = (r + g + b) / 3;
        r = g = b = grey;
        r = r + (sepiaDepth * 2);
        g = g + sepiaDepth;

        if (r>255) r=255;
        if (g>255) g=255;
        if (b>255) b=255;

        // Darken blue color to increase sepia effect
        b-= sepiaIntensity;

        // normalize if out of bounds
        if (b<0) b=0;
        if (b>255) b=255;

        c2 = new Color(r, g, b, c1.getAlpha());
        
        return c2;
    }
    
    public static void sort(
            ArrayList<RGB8> palette, int a[], int n)
            throws Exception
    {
        int i, j, t;
        RGB8 tmp;
        
        for (i = 0; i < n; i++) {
            for (j = 1; j < (n-i); j++) {
                if (!compare_RGB8_by_value(palette.get(j-1), palette.get(j))) {
                    t = a[j-1];
                    a[j-1] = a[j];
                    a[j] = t;
                    
                    tmp = palette.get(j-1);
                    palette.set(j-1, palette.get(j));
                    palette.set(j, tmp);
                }
            }
        }
    }

    // a helper function for sorting the palette
    private static boolean compare_RGB8_by_value(
            final RGB8 a, final RGB8 b)
            throws Exception
    {
        int x = 30*a.r + 59*a.g + 11*a.b;
        int y = 30*b.r + 59*b.g + 11*b.b;

        return x > y;
    }
 
    public static BufferedImage create_pattern_image(
            int patternIndex, int w, int h)
            throws Exception
    {
        BufferedImage pattern_image;
        BufferedImage pattern_sector;
        Graphics2D g;
        QPattern p;
        int pattern_w;
        int pattern_h;
        double x_scale;
        double y_scale;
        int num_x_tiles;
        int num_y_tiles;
        short value, value2;
        short[] line;
        
        p = Patterns.patterns[patternIndex];
        pattern_w = p.w;
        pattern_h = p.h;
        x_scale = p.x_scale;
        y_scale = x_scale*p.scale;
        num_x_tiles = (int)((double)(w+1 + pattern_w*x_scale - 1)/pattern_w/x_scale);
        num_y_tiles = (int)((double)(h+1 + pattern_h*y_scale - 1)/pattern_h/y_scale);
        line = p.e;

        // fixes (only here, for patterns display)
        if (Patterns.patterns_names[patternIndex].equals("Bricks")) {
            line = new short[] { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0 };
        }
        
        pattern_sector = new BufferedImage(pattern_w, pattern_h, BufferedImage.TYPE_INT_ARGB);

        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w; j++) {
                pattern_sector.setRGB(j, i, 0xFFFFFFFF);
            }
        }

        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w-1; j++) {
                value = p.p[i*pattern_w + j];
                value2 = p.p[i*pattern_w + j+1];
                
                if (value != value2) {
                    pattern_sector.setRGB(j, i, 0xFF000000);
                }
            }

            if (line[pattern_w-1 + i] == 0) {
                pattern_sector.setRGB(pattern_w-1, i, 0xFF000000);
            }
        }

        for (int i=0; i<pattern_h-1; i++) {
            for (int j=0; j<pattern_w; j++) {
                value = p.p[i*pattern_w + j];
                value2 = p.p[(i+1)*pattern_w + j];
                
                if (value != value2) {
                    pattern_sector.setRGB(j, i, 0xFF000000);
                }
            }
        }
        for (int j=0; j<pattern_w; j++) {
            if (line[j] == 0) {
                pattern_sector.setRGB(j, (pattern_h-1), 0xFF000000);
            }
        }

        pattern_image = new BufferedImage(num_x_tiles*pattern_w,
                                            num_y_tiles*pattern_h,
                                            BufferedImage.TYPE_INT_ARGB);
        g = pattern_image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i=0; i<num_x_tiles; i++) {
            for (int j=0; j<num_y_tiles; j++) {
                g.drawImage(pattern_sector, i*pattern_w, j*pattern_h, null);
            }
        }
        
        pattern_image = Generals.scaleImage(pattern_image, w, h, false);
        
//        pattern_image = Generals.rotateImage(pattern_image, 30, true, true);

        
        String str;
        int str_x, str_y;
        int str_w, str_h;
        
        g = pattern_image.createGraphics();
        
        str = Patterns.patterns_names[patternIndex];
        str_x = 5;
        str_y = 5;
        str_w = g.getFontMetrics().stringWidth(str) + 5;
        str_h = g.getFontMetrics().getHeight();
        g.setColor(Colors.white);
        g.fillRect(str_x,
                    str_y,
                    str_x + str_w,
                    str_y + str_h);
        g.setColor(Colors.gray_light);
        g.drawRect(str_x,
                    str_y,
                    str_x + str_w,
                    str_y + str_h);
        g.setColor(Colors.black);
        g.drawString(str,
                        str_x + 5,
                        str_y + 5 + 2 + str_h/2);

        
        return pattern_image;
    }

    public static BufferedImage create_cfqp_image(
            int pattern_index, int w, int h)
            throws Exception
    {
        BufferedImage pattern_image;
        Graphics2D g;
        short[] p;
        int pattern_shapes;
        int pattern_w;
        int pattern_h;
        short value, value2;
        int[] x_label;
        int[] y_label;
        int[] pos_count;


        p = Patterns.cfqp_patterns[pattern_index];
        pattern_shapes = Patterns.num_cfqp_shapes[pattern_index];
        
        pattern_w = 128;
        pattern_h = 128;
        
        pattern_image = new BufferedImage(pattern_w, pattern_h, BufferedImage.TYPE_INT_ARGB);

        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w; j++) {
                pattern_image.setRGB(j, i, 0xFFFFFFFF);
            }
        }

        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w-1; j++) {
                value = p[i*pattern_w + j];
                value2 = p[i*pattern_w + j+1];
                
                if (value != value2) {
                    pattern_image.setRGB(j, i, 0xFF000000);
                }
            }
        }

        for (int i=0; i<pattern_h-1; i++) {
            for (int j=0; j<pattern_w; j++) {
                value = p[i*pattern_w + j];
                value2 = p[(i+1)*pattern_w + j];
                
                if (value != value2) {
                    pattern_image.setRGB(j, i, 0xFF000000);
                }
            }
        }

        
        x_label = new int[pattern_shapes];
        y_label = new int[pattern_shapes];
        pos_count = new int[pattern_shapes];
        
        for (int i=0; i<pattern_shapes; i++) {
            x_label[i] = 0;
            y_label[i] = 0;
            pos_count[i] = 0;
        }
        
        for (int i=0; i<pattern_h; i++) {
            for (int j=0; j<pattern_w-1; j++) {
                value = p[i*pattern_w + j];

                x_label[value] += j;
                y_label[value] += i;
                pos_count[value]++;
            }
        }
        
        for (int i=0; i<pattern_shapes; i++) {
            x_label[i] /= pos_count[i];
            y_label[i] /= pos_count[i];
        }

        g = pattern_image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setColor(Colors.black);
        g.setFont(Fonts.label());
        for (int i=0; i<pattern_shapes; i++) {
            g.drawString(DS.PROCESS_CONTROLLER.patternLabels()[i],
                            x_label[i] - 3,
                            y_label[i] + g.getFontMetrics().getHeight()/2);
        }

        
        return Generals.scaleImage(pattern_image, w, h, false);
    }



    
    
}
