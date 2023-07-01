package qp.control.types;
import java.awt.Color;
import java.util.ArrayList;


/**
 *
 * @author Maira57
 */
public class GrayscaleQuantizer {
    
    private static final int DIM_MAX = 2000;
    
    

    public int[] pixel_count;
    public ArrayList<RGB8> palette;
    public ArrayList<Integer> boundaries;
    
    

    public GrayscaleQuantizer() {
        
        pixel_count = new int[DIM_MAX];
        for (int i=0; i<pixel_count.length; i++) {
            pixel_count[i] = 0;
        }
    }

    
    
    public ArrayList<RGB8> gen_palette(
            int[][] img, int num_c)
            throws Exception
    {
        if (num_c > DIM_MAX) num_c = DIM_MAX;
        else if (num_c < 2) num_c = 2;

        int w = img.length;
        int h = img[0].length;

        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                pixel_count[new Color(img[i][j]).getRed()]++;
            }
        }

        int total_count = w*h;

        palette = new ArrayList<RGB8>();
        for (int i=0; i<num_c; i++) {
            palette.add(new RGB8());
        }

        double group_size = (double)(total_count)/num_c;

        boundaries = new ArrayList<Integer>();
        for (int i=0; i<num_c; i++) {
            boundaries.add(0);
        }

        int lower = 0;
        double count = 0.0;
        int real_count = 0;
        int color = 0;
        int iter;
        
        iter = 0;

        for (int i=0; i<num_c; i++) {
            for (int j=lower; j<DIM_MAX; j++) {

                count += pixel_count[j];
                real_count += pixel_count[j];
                color += j*pixel_count[j];

                if (count >= group_size) {
                    int c;
                    c = (real_count!=0 ? (int)((double)(color)/real_count+0.5) : 0);
                    palette.set(i, new RGB8(c,c,c));

                    count -= group_size;
                    real_count = 0;
                    color = 0;
                    lower = j+1;
                    boundaries.set(iter, lower);
                    iter++;

                    break;
                }
            } // for j
        } // for i

        
        // reverse palette
        ArrayList<RGB8> v;
        v = new ArrayList<RGB8>();
        for (int i=palette.size()-1; i>=0; i--) {
            v.add(palette.get(i));
        }
        palette.clear();
        palette.addAll(v);

        
        return palette;
    }
    
    public Object[] reduce_colors(
            int[][] img,
            short[][] pindex)
            throws Exception
    {
        int w = img.length;
        int h = img[0].length;

        int[] colors = new int[DIM_MAX];
        int[] indices = new int[DIM_MAX];
        int num_c = palette.size();

        for (int i=0, lower=0; i<num_c; i++) {
            int upper = boundaries.get(i);
            int c = palette.get(num_c-1-i).r;
            for (int j=lower; j<upper; j++) {
                colors[j] = c;
                indices[j] = num_c-1-i;
            }
            lower = upper;
        }

        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                int x = new Color(img[i][j]).getRed();
                
                img[i][j] =
                    new Color(colors[x], colors[x], colors[x]).getRGB();
                pindex[i][j] = (short)indices[x];
            }
        }
        
        return new Object[] {
            img,
            pindex
        };
    }
    
    
    
    
    
}
