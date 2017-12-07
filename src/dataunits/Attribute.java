package dataunits;

/**
 *
 * @author jr
 */


public class Attribute {
    
    private Type type;
    private int address;
    private boolean constant;
    
    public Attribute(Type type){
        this.type = type;
        this.constant = false;
    }
    
    public Attribute(Type type, boolean isConstant){
        this.type = type;
        this.constant = isConstant;
    }
    
    public Attribute(Type type, int address){
        this.type = type;
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public Type getType() {
        return type;
    }

    public boolean isConstant() {
        return constant;
    }

    
    
    
}
