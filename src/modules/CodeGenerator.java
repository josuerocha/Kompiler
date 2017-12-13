package modules;

import dataunits.Instruction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jr
 */


public class CodeGenerator {
    
    private final String filename;
    private final String path = "output/";
    private List<Instruction> instructions;
    private List<Instruction> instructionBuffer;
    private List<Instruction> instructionStringBuffer;
    private int labelCount = 0;
    private Map<Integer,String> addressToLabel;
    
    public CodeGenerator(String filename){
        instructions = new ArrayList<>();
        instructionBuffer = new ArrayList<>();
        instructionStringBuffer = new ArrayList<>();
        addressToLabel = new HashMap<>();
        this.filename = filename + ".asm";
    }
    
    public void gen(Instruction inst){
        
        instructions.add(inst);
    }
    
    public void genBuffer(Instruction inst){
        
        instructionBuffer.add(inst);
    }
    
    public void genStringBuffer(Instruction inst){
        
        instructionStringBuffer.add(inst);
    }
    
    public void appendBuffer(){
        instructions.addAll(instructionBuffer);
        instructionBuffer.clear();
    }
    
    public void appendStringBufferReverse(){
        Collections.reverse(instructionStringBuffer);
        instructions.addAll(instructionStringBuffer);
        instructionStringBuffer.clear();
    }
    
    public void appendBufferReverse(){
        Collections.reverse(instructionBuffer);
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
            
            String label;
            if(addressToLabel.containsKey(address)){
                label = addressToLabel.get(address);
            }else{
                label = getString(labelCount+1);
                addressToLabel.put(address, label);
                labelCount++;
            }
            
            if (!instructions.get(i).patchAddress(label)){
                System.out.println(this.filename);
                System.out.println("ERROR backpatching instruction " + i);
                System.out.println(instructions.get(i));
            }
            
        }
    }
    
    public void backpatch(int i, int address){
            
            String label;
            if(addressToLabel.containsKey(address)){
                label = addressToLabel.get(address);
            }else{
                label = getString(labelCount+1);
                addressToLabel.put(address, label);
                labelCount++;
            }
            
            if (!instructions.get(i).patchAddress(label)){
                System.out.println(this.filename);
                System.out.println("ERROR backpatching instruction " + i);
                System.out.println(instructions.get(i));
            }
            
        
    }
    
    public void appendLabels(){
        
        for(Integer i : addressToLabel.keySet()){
            String label = addressToLabel.get(i);
            instructions.get(i).appendLabel(label);
        }
    }
    
    public void writeSourceFile(){
        appendLabels();
        BufferedWriter fileWriter = null;
        try{
            fileWriter = new BufferedWriter(new FileWriter(this.path + this.filename,false));
            
            for(Instruction inst : instructions){
                if(inst.getMinemonic().contains("_")){
                    System.out.println("ERROR contains _ " + this.filename);
                }
                fileWriter.write( inst.getMinemonic());
                fileWriter.newLine();
            }
            
            fileWriter.close();
            
        }catch(IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
       
        
    }
    
    private String getString(int n) {
    char[] buf = new char[(int) Math.floor(Math.log(25 * (n + 1)) / Math.log(26))];
        for (int i = buf.length - 1; i >= 0; i--) {
            n--;
            buf[i] = (char) ('A' + n % 26);
            n /= 26;
        }
    return new String(buf);
    }
}
