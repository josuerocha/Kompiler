package dataunits;

import util.*;
/**
 *
 * @author jr
 */


public class CompileError extends Token{
    
    private String message;
    private int line;
    private String lexeme;
    
    public CompileError(String message,int line,String lexeme){
        super(Token.ERROR);
        this.message = message;
        this.line = line;
        this.lexeme = lexeme;
    }
    
    public String toString(){
        return "< LEXICAL_ERROR, " + lexeme + ">" +  PrintColor.RESET;
    }
}
