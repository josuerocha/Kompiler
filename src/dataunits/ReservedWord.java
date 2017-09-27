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
    PRINT_ID = 264,
    //DATATYPES
    INT_ID = 265,
    STRING_ID = 266;
    
    public static ReservedWord PROGRAM = new ReservedWord("program");
    public static ReservedWord IF = new ReservedWord("if");
    public static ReservedWord THEN = new ReservedWord("then");
    public static ReservedWord ELSE = new ReservedWord("else");
    public static ReservedWord END = new ReservedWord("end");
    public static ReservedWord DO = new ReservedWord("do");
    public static ReservedWord WHILE = new ReservedWord("while");
    public static ReservedWord SCAN = new ReservedWord("scan");
    public static ReservedWord PRINT = new ReservedWord("print");
    public static ReservedWord INT = new ReservedWord("int");
    public static ReservedWord STRING = new ReservedWord("string");
    
    private int type;
    private String lexeme;
    
    public ReservedWord(String lexeme){
        super(Token.RESERVED_WORD);
        
        this.lexeme = lexeme;
        
        switch(lexeme){
            case "program":
                this.type = ReservedWord.PROGRAM_ID;
            break;
            case "if":
                this.type = ReservedWord.IF_ID;
            break;
            case "then":
                this.type = ReservedWord.THEN_ID;
            break;
            case "else":
                this.type = ReservedWord.ELSE_ID;
            case "end":
                this.type = ReservedWord.END_ID;
            case "do":
                this.type = ReservedWord.DO_ID;
            case "while":
                this.type = ReservedWord.WHILE_ID;
            case "scan":
                this.type = ReservedWord.SCAN_ID;
            case "print":
                this.type = ReservedWord.PRINT_ID;
        }
        
    }

    public String getLexeme() {
        return lexeme;
    }
    
    public String toString(){
        return "< " + lexeme.toUpperCase() + " >";
    }
}