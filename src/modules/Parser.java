package modules;

import dataunits.CompileError;
import dataunits.Identifier;
import dataunits.ReservedWord;
import dataunits.Token;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jr
 */
public class Parser extends Thread {
    
    private static final int MAX_JOBS = 10;
    private String filepath;
    private StringBuffer tokenFlow;
    private StringBuffer errorMessages;
    private Token currentToken;
    Lexer lexer;
    
    public Parser(String path){
        this.filepath = path;
        lexer = new Lexer(this.filepath);
        tokenFlow = new StringBuffer();
        errorMessages =  new StringBuffer();
    }
    
    public void run(){
        

        Token token;
        while ((token = lexer.getToken()) != null) {
            if(token instanceof CompileError){
                errorMessages.append(token).append(" \n");
            }else{
                tokenFlow.append(token).append(" \n");
            }  
        }
    }
    
    public String getTokenFlow(){
        return "FILE: " + this.filepath + "\n" + this.tokenFlow.toString();
    }
    
    public String getErrorMessages(){
        return errorMessages.toString() +
        " _________________________________";
    }
    
    private void program(){
        eat(ReservedWord.PROGRAM); declList(); stmtList(); eat(ReservedWord.END);
    }
    
    private void declList(){
        declaration() ;
    }
    
    private void stmtList(){
        stmt(); stmt();
    }
    
    private void declaration(){
        type(); identList(); eat(new Token(';'));
    }
    
    private void stmt(){
        
    }
    
    private void identList(){
        eat(new Token(Identifier.IDENTIFIER)); eat(new Token(',')) ; eat(new Token(Identifier.IDENTIFIER));
    }
    
    private void type(){
        switch (currentToken.getTag()){
            case ReservedWord.INT_ID:
                    eat(ReservedWord.INT);
                break;
                
            case ReservedWord.STRING_ID:
                    eat(ReservedWord.STRING);
                break;
                
            default:
                
                break;
                
        }
    }
    
    private void eat(Token t){
        if (currentToken == t) {
            currentToken = lexer.getToken();
        }
        else {
            System.out.println("ERROR: expected " + t + " on line " + lexer.getCurrentLine() + "\n");
        }
    }
    
    
    public static void main(String[] args) {
        
        if(args.length == 0){
            System.out.println("No file path informed. Please inform file paths.");
            System.out.println("Usage: java -jar KPiler.jar file1.txt file2.txt");
        }
        
        List<String> paths = new ArrayList<>();
        List<String> tokenFlow = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<Parser> compileJobs = new ArrayList<>();
        
        paths.addAll(Arrays.asList(args));
        
        
        while(paths.size() > 0 || compileJobs.size() > 0){
            
            if(compileJobs.size() < MAX_JOBS && paths.size() > 0){
                compileJobs.add(new Parser(paths.remove(0)));
                compileJobs.get(compileJobs.size() -1).start();
            }
            
            for(int i=0 ; i<compileJobs.size(); i++){
                if(!compileJobs.get(i).isAlive()){
                    tokenFlow.add(compileJobs.get(i).getTokenFlow());
                    errorMessages.add(compileJobs.get(i).getErrorMessages());
                    compileJobs.remove(i);
                }
            }
        }
        //PRINTING OUTPUT
        for(int i = 0; i<tokenFlow.size(); i++){
            System.out.println(errorMessages.get(i));
            System.out.println(tokenFlow.get(i));
        }
       
    }

}
