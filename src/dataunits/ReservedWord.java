package dataunits;

/**
 *
 * @author jr
 */


public class ReservedWord extends Token{
    public static final int
    PROGRAM_ID = 256,
    IF_ID = 257,
    THEN_ID = 258,
    ELSE_ID = 259,
    END_ID = 260,
    DO_ID = 261,
    WHILE_ID = 262,
    SCAN_ID = 263,
    PRINT_ID = 264;
    
    public static ReservedWord PROGRAM = new ReservedWord("program");
    public static ReservedWord IF = new ReservedWord("if");
    public static ReservedWord THEN = new ReservedWord("then");
    public static ReservedWord ELSE = new ReservedWord("else");
    
    private int id;
    private String lexeme;
    
    public ReservedWord(String lexeme){
        super(Token.RESERVED_WORD);
        
        this.lexeme = lexeme;
        
        switch(lexeme){
            case "program":
                this.id = ReservedWord.PROGRAM_ID;
            break;
            case "if":
                this.id = ReservedWord.IF_ID;
            break;
            case "then":
                this.id = ReservedWord.THEN_ID;
            break;
            case "else":
                this.id = ReservedWord.ELSE_ID;
            case "end":
                this.id = ReservedWord.END_ID;
            case "do":
                this.id = ReservedWord.DO_ID;
            case "while":
                this.id = ReservedWord.WHILE_ID;
            case "scan":
                this.id = ReservedWord.SCAN_ID;
            case "print":
                this.id = ReservedWord.PRINT_ID;
        }
        
    }
    
    

    public String getLexeme() {
        return lexeme;
    }
    
    public String toString(){
        return "< " + lexeme.toUpperCase() + " >";
    }
}