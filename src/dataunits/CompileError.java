package dataunits;

import util.Color;
/**
 *
 * @author jr
 */


public class CompileError extends Token{
    
    private String message;
    private int line;
    
    public CompileError(String message,int line){
        super(Token.ERROR);
        this.message = message;
        this.line = line;
    }
    
    public String toString(){
        return Color.RED + "ERROR: " + this.message + " on line " + this.line + Color.RESET;
    }
}
