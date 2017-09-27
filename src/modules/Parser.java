package modules;

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
    private StringBuffer output;
    
    public Parser(String path){
        this.path = path;
        output = new StringBuffer();
    }
    
    public void run(){
        Lexer lexer = new Lexer(this.path);

        Token token;
        while ((token = lexer.getToken()) != null) {

            output.append(token + "\n");
                    
        }
        
        printTokens();
    }
    
    public synchronized void printTokens(){
        System.out.println("FILE: " + this.path);
        System.out.print(this.output.toString());
        System.out.println("__________________________");
    }
    
    public static void main(String[] args) {
        List<String> paths = new ArrayList<String>();
        List<Parser> compileJobs = new ArrayList<Parser>();
        
        paths.addAll(Arrays.asList(args));
        
        
        while(paths.size() > 0){
            
            if(compileJobs.size() < MAX_JOBS){
                compileJobs.add(new Parser(paths.remove(0)));
                compileJobs.get(compileJobs.size() -1).start();
            }
            
            for(int i=0 ; i<compileJobs.size(); i++){
                if(!compileJobs.get(i).isAlive()){
                    compileJobs.remove(i);
                }
            }
            
            
        }
       
    }

}
