package dataunits;

/**
 *
 * @author jr
 */


public class Attribute {
    
    private Type type;
    private int tempAddress;
    
    public Attribute(Type type){
        this.type = type;
    }
    
    public Attribute(Type type, int tempAddress){
        this.type = type;
        this.tempAddress = tempAddress;
    }

    public int getTempAddress() {
        return tempAddress;
    }

    public Type getType() {
        return type;
    }
   
    
}
