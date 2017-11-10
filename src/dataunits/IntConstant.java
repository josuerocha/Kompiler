
package dataunits;

/**
 *
 * @author jr
 */
public class IntConstant extends Token{
    
    private final int value;
    
    public IntConstant(int value){
        super(Token.INT_CONSTANT);
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public String toString(){
        return "<INT_CONSTANT," + Integer.toString(value) + ">";
    }
}
