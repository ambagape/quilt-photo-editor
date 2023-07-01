package qp.control;
import java.awt.Color;
import java.util.ArrayList;
import qp.control.types.NODE;
import qp.control.types.RGB8;


/**
 *
 * @author Maira57
 */
public class NodeController {

    private static int[] mask = new int[] {
        0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
    };
    
    
    
    public static NODE CreateOctreePalette(
            int[][] img, int nMaxColors, int nColorBits,
            ArrayList<RGB8> palette, int quality)
            throws Exception
    {
        int i, j;
        NODE pTree;
        int nLeafCount;
        NODE[] pReducibleNodes;
        Color c1;
        int w, h;
        Object[] result;
        
        pReducibleNodes = new NODE[9];

        palette.clear();

        if (quality > 100) {
            quality = 100;
        }

        // Initialize octree variables
        pTree = null;
        nLeafCount = 0;
        if (nColorBits > 8) {
            // Just in case
            return null;
        }
        for (i=0; i<=nColorBits; i++) {
            pReducibleNodes[i] = null;
        }
        
        w = img.length;
        h = img[0].length;

        // change:  instead of going through the pixels in order, first add every 
        //  Nth row starting from 0, then add every Nth row starting from 1, etc.
        int N = (h+100) / 100;
        for (int k=0; k<N; k++) {
            for (i=k; i<h; i+=N) {
                for (j=0; j<w; j++) {
                    c1 = new Color(img[j][i]);
                    
                    result = AddColor(
                                    pTree,
                                    c1.getRed(), c1.getGreen(), c1.getBlue(),
                                    nColorBits, 0, nLeafCount,
                                    pReducibleNodes);
                    nLeafCount = (Integer)result[0];
                    pTree = (NODE)result[1];

                    int noTries;

                    noTries = 10;

                    while (nLeafCount > quality*nMaxColors && noTries>0) {
                        nLeafCount = ReduceTree(nColorBits, nLeafCount, pReducibleNodes);
//                        Logger.printOut("noTries: %d leafs = %d - %d\n",
//                            noTries, nLeafCount, quality*nMaxColors);
                        noTries--;
                    }
                }
            }
        }

        while (nLeafCount > nMaxColors) {
            nLeafCount = ReduceTree(nColorBits, nLeafCount, pReducibleNodes);
        }

        palette.clear();
        for (int l=0; l<nLeafCount; l++) {
            palette.add(new RGB8());
        }

        GetPaletteColors(pTree, palette, 0);

        return pTree;
    }

    public static int get_palette_index(
            NODE node, int r, int g, int b, int nLevel)
            throws Exception
    {
        if (node.bIsLeaf) {
            return node.index;
        }

        // Recurse a level deeper if the node is not a leaf
        else {
            int shift = 7 - nLevel;
            int nIndex = (((r & mask[nLevel]) >> shift) << 2) |
                (((g & mask[nLevel]) >> shift) << 1) |
                ((b & mask[nLevel]) >> shift);
            return get_palette_index(node.pChild[nIndex], r, g, b, nLevel + 1);
        }
    }

    
    
    private static Object[] AddColor(
            NODE ppNode, int r, int g, int b, int nColorBits,
            int nLevel, int pLeafCount, NODE[] pReducibleNodes)
            throws Exception
    {
        int nIndex, shift;

        // If the node doesn't exist, create it
        Object[] result;
        if (ppNode == null) {
            result = CreateNode(nLevel, nColorBits, pLeafCount,
                                pReducibleNodes);
            ppNode = (NODE)result[0];
            pLeafCount = (Integer)result[1];
        }

        // Update color information if it's a leaf node
        if (ppNode.bIsLeaf) {
            ppNode.nPixelCount++;
            ppNode.nRedSum += r;
            ppNode.nGreenSum += g;
            ppNode.nBlueSum += b;
        }

        // Recurse a level deeper if the node is not a leaf
        else {
            shift = 7 - nLevel;
            nIndex = (((r & mask[nLevel]) >> shift) << 2) |
                (((g & mask[nLevel]) >> shift) << 1) |
                ((b & mask[nLevel]) >> shift);
            result = AddColor(ppNode.pChild[nIndex],
                                r, g, b, nColorBits,
                                nLevel + 1, pLeafCount, pReducibleNodes);
            pLeafCount = (Integer)result[0];
            ppNode.pChild[nIndex] = (NODE)result[1];
        }
        
        return new Object[] {
            pLeafCount,
            ppNode
        };
    }

    private static Object[] CreateNode(
            int nLevel, int nColorBits, int pLeafCount,
            NODE[] pReducibleNodes)
            throws Exception
    {
        NODE pNode;

        pNode = new NODE();

        pNode.bIsLeaf = (nLevel == nColorBits);
        if (pNode.bIsLeaf) {
            pLeafCount++;
        }
        else {
            // Add the node to the reducible list for this level
            pReducibleNodes[nLevel] = pNode;
        }
        
        return new Object[] {
            pNode,
            pLeafCount
        };
    }

    private static Object[] find_deepest(
            NODE node, int pix_count)
            throws Exception
    {
        NODE deepest_node;
        int deepest;
        
        deepest = 0;
        deepest_node = node;

        if (node == null) {
            return null;
        }

        if (node.bIsLeaf) {
            pix_count = node.nPixelCount;
            return new Object[] {
                deepest_node,
                0,
                pix_count
            };
        }

        pix_count=0;
        int n, tmp_count=0, total_pix_count=0;
        NODE tmp_node;

        for (int i=0; i<8; i++) {
            if (node.pChild[i] == null) {
                continue;
            }
        
            Object[] result;
        
            result = find_deepest(node.pChild[i], tmp_count);
            tmp_node = (NODE)result[0];
            n = (Integer)result[1];
            tmp_count = (Integer)result[2];
            
            if(n > deepest) {
                deepest = n;
                pix_count = tmp_count;
                deepest_node = tmp_node;
            } else if(n == deepest && tmp_count < pix_count) {
                pix_count = tmp_count;
                deepest_node = ((n!=0) ? tmp_node : node);
            }

            total_pix_count += tmp_count;
        }

        pix_count = total_pix_count;

        return new Object[] {
            deepest_node,
            deepest+1,
            pix_count
        };
    }

    private static int ReduceTree(
            int nColorBits, int pLeafCount, NODE[] pReducibleNodes)
            throws Exception
    {
        int i;
        NODE pNode;
        int nRedSum, nGreenSum, nBlueSum, nChildren;

        // Find the deepest level containing at least one reducible node
        for (i=nColorBits - 1; (i>0) && (pReducibleNodes[i] == null); i--) {
        }

        Object[] result;
        
        result = find_deepest(pReducibleNodes[0], i);
        pNode = (NODE)result[0];

        nRedSum = nGreenSum = nBlueSum = nChildren = 0;
        for (i=0; i<8; i++) {
            if (pNode.pChild[i] != null) {
                nRedSum += pNode.pChild[i].nRedSum;
                nGreenSum += pNode.pChild[i].nGreenSum;
                nBlueSum += pNode.pChild[i].nBlueSum;
                pNode.nPixelCount += pNode.pChild[i].nPixelCount;
                pNode.pChild[i] = null;
                nChildren++;
            }
        }

        pNode.bIsLeaf = true;
        pNode.nRedSum = nRedSum;
        pNode.nGreenSum = nGreenSum;
        pNode.nBlueSum = nBlueSum;
        pLeafCount -= (nChildren - 1);
        
        return pLeafCount;
    }
    
    private static int GetPaletteColors(
            NODE pTree, ArrayList<RGB8> palette, int pIndex)
            throws Exception
    {
        int i;

        if (pTree.bIsLeaf) {
            RGB8 color;
            int t;
            
            t = pTree.nPixelCount >> 1; // factor to give correct rounding

            color = palette.get(pIndex);
            color.r = (int) ((pTree.nRedSum + t) / (pTree.nPixelCount));
            color.g = (int) ((pTree.nGreenSum + t) / (pTree.nPixelCount));
            color.b = (int) ((pTree.nBlueSum + t) / (pTree.nPixelCount));
            pTree.index = pIndex;
            pIndex++;
        }
        else {
            for (i=0; i<8; i++) {
                if (pTree.pChild[i] != null) {
                    pIndex = GetPaletteColors(pTree.pChild[i], palette, pIndex);
                }
            }
        }
        
        return pIndex;
    }

    
    
}
