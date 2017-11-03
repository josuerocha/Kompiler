
package dataunits;

/**
 *
 * @author jr
 */
public class Token {
    
        /**
 * List of tokens and their corresponding code. 
 * 
 * <p>
 * **/
    //RESERVED WORDS
    public final static int
    RESERVED_WORD = 256,
    
    //
    OPERATOR = 300,
            
    //OTHER
    INT_CONSTANT = 450,
    LITERAL_CONSTANT = 451,
    IDENTIFIER = 452,
    ERROR = 455;
    
    
    public static Token SEMI_COLON = new Token(';'),
                        OPEN_PAREN = new Token('('),
                        CLOSE_PAREN = new Token(')'),
                        COMMA = new Token(',');
    
//------------------------------------------------------------------------------------
    
    private final int tag;
    private int line;
    
    public Token(int tag){
        this.tag = tag;
    }
    
    public int getTag(){
        return tag;
    }
    
    public void setLine(int line){
        this.line = line;
    }
    
    public int getLine(){
        return this.line;
    }
    
    @Override
public boolean equals(Object obj) {
    if (obj == null) {
        return false;
    }
    if (!Token.class.isAssignableFrom(obj.getClass())) {
        return false;
    }
    final Token other = (Token) obj;
    
    if (this.tag != other.tag) {
        return false;
    }else{
        return true;
    }
}
    
    public String toString(){
        return "<" + ((char) tag) + ">";
    }
}
