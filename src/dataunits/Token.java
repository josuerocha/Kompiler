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
     * *
     */
    public final static int //OTHER
            INT_CONSTANT_ID = 450,
            LIT_CONSTANT_ID = 451,
            IDENTIFIER_ID = 452,
            ERROR_ID = 455,
            EOF_ID = 500;

    public static Token SEMI_COLON = new Token(';'),
            OPEN_PAREN = new Token('('),
            CLOSE_PAREN = new Token(')'),
            COMMA = new Token(','),
            EOF = new Token(EOF_ID);

//------------------------------------------------------------------------------------
    public int tag;

    public Token(int tag) {
        this.tag = tag;
    }

    public int getTag() {
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
        } else {
            return true;
        }
    }

    public String getLexeme(){
        return Character.toString((char) tag);
    }
    
    public String toString() {
        if (this.tag == EOF_ID) {
            return "<EOF>";
        } else {
            return "<" + ((char) tag) + ">";
        }
    }
}
