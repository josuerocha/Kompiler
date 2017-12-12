package dataunits;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jr
 */


public class Attribute {
    
    private Type type;
    private int address;
    private boolean constant;
    public List<Integer> truelist = new ArrayList<>();
    public List<Integer> falselist = new ArrayList<>();
    
    public Attribute(){
        this.type = Type.VOID;
        this.constant = false;
    }
    
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

    public void setType(Type type) {
        this.type = type;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }
    
    public void incrementLists(int inc){
        for(Integer i : truelist){
            i++;
        }
        
        for(Integer i: falselist){
            i++;
        }
        
    }
    
}
