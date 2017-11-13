
package dataunits;

/**
 *
 * @author jr
 */
public class Identifier extends Token{
    
    public static Identifier IDENTIFIER = new Identifier("");

    private String lexeme;
    
    public Identifier(String text){
        super(Token.IDENTIFIER_ID);
        this.lexeme = text;
    }
    
    
    public String getLexeme(){
        return lexeme;
    }
    
    public String toString(){
        return "< ID , " + lexeme + " >";
    }
    
    
}
