package modules;

import dataunits.CompileError;
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
    private String path;
    private StringBuffer tokenFlow;
    private StringBuffer errorMessages;
    
    public Parser(String path){
        this.path = path;
        tokenFlow = new StringBuffer();
        errorMessages =  new StringBuffer();
    }
    
    public void run(){
        Lexer lexer = new Lexer(this.path);

        Token token;
        while ((token = lexer.getToken()) != null) {
            if(token instanceof CompileError){
                errorMessages.append(token + " \n");
            }else{
                tokenFlow.append(token + " \n");
            }  
        }
        
    }
    
    public String getTokenFlow(){
        return "FILE: " + this.path + "\n" + this.tokenFlow.toString();
    }
    
    public String getErrorMessages(){
        return errorMessages.toString() +
        " _________________________________";
    }
    
    public static void main(String[] args) {
        
        if(args.length == 0){
            System.out.println("No file path informed. Please inform file paths.");
            System.out.println("Usage: java -jar KPiler.jar file1.txt file2.txt");
        }
        
        List<String> paths = new ArrayList<String>();
        List<String> tokenFlow = new ArrayList<String>();
        List<String> errorMessages = new ArrayList<String>();
        List<Parser> compileJobs = new ArrayList<Parser>();
        
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
