
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
    
    //
    OPERATOR = 300,
            
    //OTHER
    INT_CONSTANT = 450,
    LITERAL_CONSTANT = 451,
    IDENTIFIER = 452,
    ERROR = 455,
    EOF_ID = 500;
    
    
    public static Token SEMI_COLON = new Token(';'),
                        OPEN_PAREN = new Token('('),
                        CLOSE_PAREN = new Token(')'),
                        COMMA = new Token(','),
                        EOF = new Token(EOF_ID);
    
//------------------------------------------------------------------------------------
    
    public int tag;
    
    public Token(int tag){
        this.tag = tag;
    }
    
    public int getTag(){
        return tag;
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
        if(this.tag == EOF_ID){
            return "<EOF>";
        }else{
            return "<" + ((char) tag) + ">";
        }
    }
}
