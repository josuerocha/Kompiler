package dataunits;

/**
 *
 * @author jr
 */


public class LiteralConstant extends Token{
    
    private String lexeme;
    
    public LiteralConstant(String lexeme){
        super(Token.LITERAL_CONSTANT);
        this.lexeme = lexeme;
    }
    
    public String getLexeme(){
        return lexeme;
    }
    
    public String toString(){
        return "<LITERAL_CONST , " + lexeme +">";
    }
    
}
