package dataunits;

/**
 *
 * @author jr
 */


public class LiteralConstant extends Token{
    
    public static LiteralConstant LIT_CONSTANT = new LiteralConstant("");
    
    private String lexeme;
    
    public LiteralConstant(String lexeme){
        super(Token.LIT_CONSTANT_ID);
        this.lexeme = lexeme;
    }
    
    public String getLexeme(){
        return lexeme;
    }
    
    public String toString(){
        return "<LITERAL_CONST , " + lexeme +">";
    }
    
}
