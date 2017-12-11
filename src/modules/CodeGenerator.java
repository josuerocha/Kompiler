package modules;

import dataunits.Instruction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jr
 */


public class CodeGenerator {
    
    private String filename;
    private String path = "output/";
    private List<Instruction> instructions;
    
    public CodeGenerator(String filename){
        instructions = new ArrayList<>();
        this.filename = filename + ".asm";
    }
    
    public void gen(Instruction inst){
        
        instructions.add(inst);
    }
    
    public int getNextInstr(){
        return instructions.size();
    }
    
    public int getInst(){
        return instructions.size()-1;
    }
    
    public void backpatch(List<Integer> list, int address){
        
    }
    
    public void writeSourceFile(){
        
        BufferedWriter fileWriter = null;
        try{
            fileWriter = new BufferedWriter(new FileWriter(this.path + this.filename,false));
            
            for(Instruction inst : instructions){
                fileWriter.write( inst.getMinemonic());
                fileWriter.newLine();
            }
            
            fileWriter.close();
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
       
        
    }
}
