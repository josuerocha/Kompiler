package dataunits;

/**
 *
 * @author jr
 */


public class ReservedWord extends Token{
    public static final int
    PROGRAM = 256,
    IF = 257,
    THEN = 258,
    ELSE = 259,
    END = 260,
    DO = 261,
    WHILE = 262,
    SCAN = 263,
    PRINT = 264;
    
    private int id;
    private String lexeme;
    
    public ReservedWord(String lexeme){
        super(Token.RESERVED_WORD);
        
        this.lexeme = lexeme;
        
    }
    
    public String toString(){
        return "< " + lexeme + " >";
    }
}
