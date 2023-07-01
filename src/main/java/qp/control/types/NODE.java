package qp.control.types;


/**
 *
 * @author Maira57
 */
public class NODE {

    
    
    public boolean bIsLeaf;            // TRUE if node has no children
    public int nPixelCount;            // Number of pixels represented by this leaf
    public int nRedSum;                // Sum of red components
    public int nGreenSum;              // Sum of green components
    public int nBlueSum;               // Sum of blue components
    public int index;
    public NODE[] pChild;              // Pointers to child nodes
    
    
    
    public NODE() {
        bIsLeaf = false;
        nPixelCount = 0;
        nRedSum = 0;
        nGreenSum = 0;
        nBlueSum = 0;
        index = 0;
        
        pChild = new NODE[8];
        for (int i=0; i<8; i++) {
            pChild[i] = null;
        }
    }

    
    
    
    
    
    
}
