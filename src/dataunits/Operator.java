package dataunits;

/**
 *
 * @author jr
 */


public class Operator extends Token{
    
    //OPERATOR IDs
    public static final int 
            AND_ID = 504,
            OR_ID = 505,
            GREATER_EQUAL_ID = 506,
            LESS_EQUAL_ID = 507,
            DIFFERENT_ID = 508,
            EQUAL_ID = 509;
    
    //LEXEME STORING VARIABLE
    private final String lexeme;
    
    //PRE-INSTATIATED OPERATORS IN ORDER TO SAVE MEMORY
    public static Operator EQUAL = new Operator(EQUAL_ID, "==");
    public static Operator GE = new Operator(GREATER_EQUAL_ID,">=");
    public static Operator LE = new Operator(LESS_EQUAL_ID,"<=");
    public static Operator GT = new Operator('>');
    public static Operator LT = new Operator('<');
    public static Operator AND = new Operator(AND_ID,"&&");
    public static Operator OR = new Operator(OR_ID,"||");
    public static Operator DIV = new Operator('/');
    public static Operator MUL = new Operator('*');
    public static Operator PLUS = new Operator('+');
    public static Operator MINUS = new Operator('-');
    public static Operator ASSIGN = new Operator('=');
    public static Operator DIFFERENT = new Operator(DIFFERENT_ID,"!=");
    public static Operator NEG = new Operator('!');
    
    public Operator(char ch){
        super(ch);
        lexeme = Character.toString(ch);
    }
            
    public Operator(int tag,String lexeme){
        super(tag);
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }
    
    public String toString(){
        return "<" + lexeme + ">";
    }
    
}
