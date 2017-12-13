package dataunits;

/**
 *
 * @author jr
 */


public class Instruction {
    
    private int address;
    private String minemonic;
    private int branchAddress;

    public Instruction(String minemonic){
        this.minemonic = minemonic;
    }
    
    public int getAddress() {
        return address;
    }
    
    public boolean patchAddress(String label){
        boolean contained = minemonic.contains("_");
        
        minemonic = minemonic.replace("_", label);
        
        return !minemonic.contains("_") && contained;
    }

    public void appendLabel(String label){
        this.minemonic = label + ": " + this.minemonic;
    }
    
    public String getMinemonic() {
        return this.minemonic;
    }

    public int getBranchAddress() {
        return branchAddress;
    }
    
    @Override
    public String toString(){
        return this.minemonic;
    }
    
}
