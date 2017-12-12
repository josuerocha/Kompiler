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
        List<Integer> newTruelist = new ArrayList<>();
        for(int i = 0; i < truelist.size(); i++){
            newTruelist.add(truelist.get(i) + inc);
        }
        this.truelist = newTruelist;
        
        List<Integer> newFalselist = new ArrayList<>();
        for(int i=0; i<falselist.size(); i++){
            newFalselist.add(falselist.get(i) + inc);
        }
        falselist = newFalselist;
        
    }
    
}
