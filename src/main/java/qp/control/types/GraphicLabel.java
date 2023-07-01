package qp.control.types;


/**
 *
 * @author Maira57
 */
public class GraphicLabel {

    public double x, y;
    public int order, index;
    
    

    public GraphicLabel() {
        x = 0;
        y = 0;
        order = -1;
        index = -1;
    }
    
    public GraphicLabel(double x_, double y_, int order_, int index_) {
        x = x_;
        y = y_;
        order = order_;
        index = index_;
    }
    
    public GraphicLabel(GraphicLabel label) {
        x = label.x;
        y = label.y;
        order = label.order;
        index = label.index;
    }
    
    
        
    public void set(GraphicLabel label) {
        x = label.x;
        y = label.y;
        order = label.order;
        index = label.index;
    }
    
    
    
    public boolean isEqualTo(GraphicLabel label) throws Exception {
        return (x != label.x
                || y != label.y
                || order != label.order
                || index != label.index);
    }
    
    
    
    
    
}
