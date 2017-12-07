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

    public String getMinemonic() {
        return minemonic;
    }

    public int getBranchAddress() {
        return branchAddress;
    }
    
    
    
}
