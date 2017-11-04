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
    
    public static final ReservedWord PROGRAM = new ReservedWord("program");
    public static final ReservedWord IF = new ReservedWord("if");
    public static final ReservedWord THEN = new ReservedWord("then");
    public static final ReservedWord ELSE = new ReservedWord("else");
    public static final ReservedWord END = new ReservedWord("end");
    public static final ReservedWord DO = new ReservedWord("do");
    public static final ReservedWord WHILE = new ReservedWord("while");
    public static final ReservedWord SCAN = new ReservedWord("scan");
    public static final ReservedWord PRINT = new ReservedWord("print");
    public static final ReservedWord INT = new ReservedWord("int");
    public static final ReservedWord STRING = new ReservedWord("string");
    
    private String lexeme;
    
    public ReservedWord(String lexeme){
        super(0);
        this.lexeme = lexeme;
                
        switch(this.lexeme){
            case "program":
                super.tag = ReservedWord.PROGRAM_ID;
                break;
            case "int":
                super.tag = ReservedWord.INT_ID;
                break;
            case "string":
                super.tag = ReservedWord.STRING_ID;
                break;
            case "if":
                super.tag = ReservedWord.IF_ID;
                break;
            case "then":
                super.tag = ReservedWord.THEN_ID;
                
            break;
            case "else":
                super.tag = ReservedWord.ELSE_ID;
                break;
            case "end":
                super.tag = ReservedWord.END_ID;
                break;
            case "do":
                super.tag = ReservedWord.DO_ID;
                break;
            case "while":
                super.tag = ReservedWord.WHILE_ID;
                break;
            case "scan":
                super.tag = ReservedWord.SCAN_ID;
                break;
            case "print":
                super.tag = ReservedWord.PRINT_ID;
                break;
            default:
                System.out.println(lexeme);
        }
        
    }

    public String getLexeme() {
        return lexeme;
    }
    
    public String toString(){
        return "< " + lexeme.toUpperCase() + " >";
    }
}