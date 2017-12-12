package modules;

import dataunits.Instruction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jr
 */


public class CodeGenerator {
    
    private String filename;
    private String path = "output/";
    public List<Instruction> instructions;
    private List<Instruction> instructionBuffer;
    
    public CodeGenerator(String filename){
        instructions = new ArrayList<>();
        instructionBuffer = new ArrayList<>();
        this.filename = filename + ".asm";
    }
    
    public void gen(Instruction inst){
        
        instructions.add(inst);
    }
    
    public void genBuffer(Instruction inst){
        
        instructionBuffer.add(inst);
    }
    
    public void appendBuffer(){
        instructions.addAll(instructionBuffer);
        instructionBuffer.clear();
    }
    
    public int getNextInstr(){
        return instructions.size();
    }
    
    public int getInst(){
        return instructions.size()-1;
    }
    
    public void backpatch(List<Integer> list, int address){
        for(Integer i : list){
            
            if (!instructions.get(i).patchAddress(address)){
                System.out.println("ERROR backpatching instruction " + i);
                System.out.println(instructions.get(i));
            }
        }
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
            
        }catch(IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
       
        
    }
}
