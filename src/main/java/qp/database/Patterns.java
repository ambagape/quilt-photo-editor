package qp.database;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import qp.CNT;
import qp.CNT.PATHS;
import qp.Exceptions;
import qp.control.ProcessImage;
import qp.control.types.QPattern;
import qp.design.Generals;


/**
 *
 * @author Maira57
 */
public class Patterns {


    
    public static QPattern[] patterns;
    public static QPattern[] patterns_ext;
    
    public static String[] patterns_names;


    
    public static short[][] cfqp_patterns;

    public static int[] num_cfqp_shapes
            = new int[] {5,5,5,5,5,5,6,6,6,6,5,5,5,6,6,5,5,5,5,5,5,6,5,6,};


    
    public static void initialize(boolean extended) throws Exception {
        Object[] square;
        Object[] square_30;
        Object[] square_45;
        Object[] diamond;
        Object[] octagon;
        Object[] hexagon;
        Object[] triangle;
        Object[] diamonds2;
        Object[] diamonds3;
        Object[] triangles2;
        Object[] bricks;

        Object[] generic0;
        Object[] generic1;
        Object[] generic2;
        Object[] generic3;
        Object[] generic4;
        Object[] spiral;
        Object[] bricks2;
        Object[] generic5;

        
        // read from file
        BufferedReader f;
        String s;
        ArrayList<ArrayList<Short>> data;
        ArrayList<Integer> data_w;
        ArrayList<Integer> data_h;
        ArrayList<Short> entry;
        String[] str;
        int n;
        short v;
        
        f = new BufferedReader(new InputStreamReader(
                    CNT.resourcesClass.getResourceAsStream(
                        PATHS.patterns
                        + (extended ? "patterns_ext.txt" : "patterns.txt")
                    )
                ));

        data = new ArrayList<ArrayList<Short>>();
        data_w = new ArrayList<Integer>();
        data_h = new ArrayList<Integer>();
        s = f.readLine();
        while (s != null) {
            s = s.trim();
            if (s.isEmpty()) {
                s = f.readLine();
                continue;
            }
            if (s.startsWith("//")) {
                s = f.readLine();
                continue;
            }
            
            n = Integer.parseInt(s);
            data_w.add(n);
            s = f.readLine();
            n = Integer.parseInt(s);
            data_h.add(n);
            
            s = f.readLine();
            if (s.equals("uniform")) {
                entry = new ArrayList<Short>();
                
                if (data.size() % 2 != 0) {
                    n = data_w.get(data_w.size()-1) + data_h.get(data_h.size()-1) - 1;
                }
                else {
                    n = data_w.get(data_w.size()-1) * data_h.get(data_h.size()-1);
                }
                s = f.readLine();
                v = Short.parseShort(s);
                
                for (int i=0; i<n; i++) {
                    entry.add(v);
                }
                data.add(entry);
                s = f.readLine();
            }
            else if (s.equals("{")) {
                entry = new ArrayList<Short>();
                
                s = f.readLine();
                while (!s.equals("};")) {
                    str = s.split("[\\ \\,\\t]+");
                    for (int i=0; i<str.length; i++) {
                        if (!str[i].isEmpty()) {
                            entry.add(Short.parseShort(str[i]));
                        }
                    }
                    
                    s = f.readLine();
                }
                
                data.add(entry);
                s = f.readLine();
            }
            else {
                throw Exceptions.nullReturnValue();
            }
        }
        
        for (int i=data_w.size()-1; i>=0; i-=2) {
            data_w.remove(i);
            data_h.remove(i);
        }
        
        f.close();
        

        // set patterns names
        patterns_names = new String[] {
            "Square",
            "Square - at 45 degrees",
            "Tumbling blocks",
            
            "Octagon",
            "Hexagon",
            
            "Bricks",
            "Bricks 2",
            
            "Diamond",
            "Diamonds 2",
            "Diamonds 3",
            
            "Triangle",
            "Triangles 2",
            
            "6 Pointed Star",
            "Half Square-Triangle",
            "Basket Weave",
            "Half Square-Rectangle",
            "Log Cabin",
            "Courthouse Steps",
            "Rounded hexagon",
        };
        
        
        // initialize patterns and edges
        int idx;
        int idx2;
        
        idx = 0;
        idx2= 0;
        
        square = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        square_30 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        square_45 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        diamond = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        octagon = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        hexagon = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        triangle = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        diamonds2 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        diamonds3 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        triangles2 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        bricks = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
    
        generic0 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        generic1 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        generic2 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        generic3 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        generic4 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        spiral = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        bricks2 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        generic5 = new Object[] {
            getVector(data.get(idx++)), getVector(data.get(idx++)),
            data_w.get(idx2), data_h.get(idx2++)
        };
        
        if (!extended) {
            generic0[0] = QPattern.mirror_tb((short[])generic0[0], 64, 64);
            generic1[0] = QPattern.mirror_tb((short[])generic1[0], 64, 32);
        }
        
        
        // new patterns reading
        if (extended) {
            square = getPatternFromImage("square.png");
            square_30 = getPatternFromImage("square_30.png");
            square_45 = getPatternFromImage("square_45.png");
            diamond = getPatternFromImage("diamond.png");
            octagon = getPatternFromImage("octagon.png");
            hexagon = getPatternFromImage("hexagon.png");
            triangle = getPatternFromImage("triangle.png");
            diamonds2 = getPatternFromImage("diamonds2.png");
            diamonds3 = getPatternFromImage("diamonds3.png");
            triangles2 = getPatternFromImage("triangles2.png");
            bricks = getPatternFromImage("bricks.png");

            generic0 = getPatternFromImage("generic0.png");
            generic1 = getPatternFromImage("generic1.png");
            generic2 = getPatternFromImage("generic2.png");
            generic3 = getPatternFromImage("generic3.png");
            generic4 = getPatternFromImage("generic4.png");
            spiral = getPatternFromImage("spiral.png");
            bricks2 = getPatternFromImage("bricks2.png");
            generic5 = getPatternFromImage("generic5.png");
        }
        
                
        // create patterns array
        QPattern[] array;
        double fact_sc;
        
        if (extended) {
            fact_sc = 1.0;
        }
        else {
            fact_sc = 1.0;
        }
        
        array = new QPattern[] {
            new QPattern(square, 1.0, 0.75, 1.0*fact_sc),
            new QPattern(square_45, 1.0, 0.75, 1.0*fact_sc),
            new QPattern(square_30, 1.0, 0.3, Math.sqrt(50.0/5)*fact_sc),
            
            new QPattern(octagon, 1.0, 0.8, Math.sqrt(9.0/5)*fact_sc),
            new QPattern(hexagon, 1.5, 1.0, Math.sqrt(13.0/5)*fact_sc),
            
            new QPattern(bricks, 1.0, 2.0, Math.sqrt(33.0/5)*fact_sc),
            new QPattern(bricks2, 1.0, 2.0, Math.sqrt(34.0/5)*fact_sc),
            
            new QPattern(diamond, 1.5, 0.75, Math.sqrt(6.0/5)*fact_sc),
            new QPattern(diamonds2, 1.0, 1.0, Math.sqrt(20.0/5)*fact_sc),
            new QPattern(diamonds3, 1.0, 1.0, Math.sqrt(40.0/5)*fact_sc),
            
            new QPattern(triangle, 1.0, 1.0, Math.sqrt(17.0/5)*fact_sc),
            new QPattern(triangles2, 1.0, 1.0, Math.sqrt(32.0/5)*fact_sc),

            new QPattern(generic0, 1.0, 0.75, Math.sqrt(18.0/5)*fact_sc),
            new QPattern(generic1, 1.0, 0.75, Math.sqrt(29.0/5)*fact_sc),
            new QPattern(generic2, 1.0, 2.5, Math.sqrt(50.0/5)*fact_sc),
            new QPattern(generic3, 1.0, 2.5, Math.sqrt(33.0/5)*fact_sc),
            new QPattern(spiral, 1.0, 1.0, Math.sqrt(21.0/5)*fact_sc),
            new QPattern(generic4, 1.0, 2.0, Math.sqrt(40.0/5)*fact_sc),
            new QPattern(generic5, 1.0, 1.0, Math.sqrt(170.0/5)*fact_sc)
        };

        QPattern[] tmpArray;
        
        if (CNT.product.equals(CNT.PRODUCT_TYPE.DELUXE)) {
            tmpArray = array;
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.PHOTO_EXPRESS)) {
            tmpArray = new QPattern[] {
                array[0],
                array[1]
            };
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.LANDSCAPE_EXPRESS)) {
            tmpArray = new QPattern[] {
                array[0]
            };
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.COLOR_VALUATIONS)) {
            tmpArray = new QPattern[0];
        }
        else if (CNT.product.equals(CNT.PRODUCT_TYPE.STITCH_A_SKETCH)) {
            tmpArray = new QPattern[] {
                array[array.length-1]
            };
        }
        else {
            throw Exceptions.badIfBranch(CNT.product);
        }
        
        if (extended) {
            patterns_ext = tmpArray;
        }
        else {
            patterns = tmpArray;
        }
        
    }

    public static void initializeCfqp() throws Exception {
        // read from file
        BufferedReader f;
        String s;
        ArrayList<ArrayList<Short>> data;
        ArrayList<Short> entry;
        String[] str;
        int n;
        short v;
        
        f = new BufferedReader(new InputStreamReader(
                    CNT.resourcesClass.getResourceAsStream(
                        PATHS.patterns + "cfqp_patterns.txt")));

        data = new ArrayList<ArrayList<Short>>();
        s = f.readLine();
        while (s != null) {
            s = s.trim();
            if (s.isEmpty()) {
                s = f.readLine();
                continue;
            }
            if (s.startsWith("//")) {
                s = f.readLine();
                continue;
            }
            
            if (s.equals("uniform")) {
                entry = new ArrayList<Short>();
                
                s = f.readLine();
                n = Integer.parseInt(s);
                s = f.readLine();
                v = Short.parseShort(s);
                
                for (int i=0; i<n; i++) {
                    entry.add(v);
                }
                data.add(entry);
                s = f.readLine();
            }
            else if (s.equals("{")) {
                entry = new ArrayList<Short>();
                
                s = f.readLine();
                while (!s.equals("};")) {
                    str = s.split("[\\ \\,\\t]+");
                    for (int i=0; i<str.length; i++) {
                        if (!str[i].isEmpty()) {
                            entry.add(Short.parseShort(str[i]));
                        }
                    }
                    
                    s = f.readLine();
                }
                
                data.add(entry);
                s = f.readLine();
            }
            else {
                throw Exceptions.nullReturnValue();
            }
        }
        
        f.close();

        
        // initialize cfqp patterns
        int[] indices;
        
        indices = new int[] {
            0, 24, 48, 1, 25, 49, 2, 26, 50, 3, 
            27, 51, 4, 28, 52, 5, 29, 53, 6, 30, 
            54, 7, 31, 55, 8, 32, 56, 9, 33, 57, 
            10, 34, 58, 11, 35, 59, 12, 36, 60, 13,
            37, 61, 14, 38, 62, 15, 39, 63, 16, 40,
            64, 17, 41, 65, 18, 42, 66,  19, 43, 67,
            20, 44, 68, 21, 45, 69,  22, 46, 70, 23,
            47, 71
        };
        
        cfqp_patterns = new short[data.size()][];
        for (int i=0; i<data.size(); i++) {
            cfqp_patterns[indices[i]] = getVector(data.get(i));
        }
        
        
    }
    
    
    
    private static short[] getVector(ArrayList<Short> v) throws Exception {
        short[] v2;
        
        v2 = new short[v.size()];
        for (int i=0; i<v2.length; i++) {
            v2[i] = v.get(i);
        }
        
        return v2;
    }

    
    
    public static Object[] getPatternFromImage(String imageName) throws Exception {
        BufferedImage img;
        int[][] regions;
        short[] p;
        short[] e;
        int w, h;
        int black;

        
        img = CommunicationLocal.getImageFromLocal("input\\images\\" + imageName);
        w = img.getWidth();
        h = img.getHeight();
        black = 0xFF000000;

        regions = (int[][])(ProcessImage.getRegions(
                                Generals.getMatrixFromImage(img))[2]);

        // fix margins of 'regions' matrix
        for (int i=1; i<w-1; i++) {
            regions[i][0] = regions[i][1];
            regions[i][h-1] = regions[i][h-2];
        }
        for (int j=1; j<h-1; j++) {
            regions[0][j] = regions[1][j];
            regions[w-1][j] = regions[w-2][j];
        }
        regions[0][0] = regions[0][1];
        regions[w-1][0] = regions[w-2][0];
        regions[0][h-1] = regions[0][h-2];
        regions[w-1][h-1] = regions[w-1][h-2];
        
        w = img.getWidth()-1;
        h = img.getHeight()-1;
        
        p = new short[w*h];
        for (int j=0; j<h; j++) {
            for (int i=0; i<w; i++) {
                p[j*w + i] = (short)regions[i][j];
            }
        }
        
        e = new short[w + h - 1];
        for (int i=0; i<e.length; i++) {
            e[i] = 1;
        }
        for (int i=0; i<w; i++) {
            if (img.getRGB(i, h) == black) {
                e[i] = 0;
            }
        }
        for (int j=h-2; j>=0; j--) {
            if (img.getRGB(w, j) == black) {
                e[h-2 - j + w] = 0;
            }
        }

        
        return new Object[] {
            p,
            e,
            w,
            h
        };
    }
    

    
    
    
}
