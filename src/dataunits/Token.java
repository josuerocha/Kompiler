
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
    SEMI_COLLON = 453,
    PARENTHESIS = 454,
    ERROR = 455;
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
    
    public String toString(){
        return "<" + ((char) tag) + ">";
    }
}
