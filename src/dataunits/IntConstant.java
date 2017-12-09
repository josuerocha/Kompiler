
package dataunits;

/**
 *
 * @author jr
 */
public class IntConstant extends Token{
    
    public static IntConstant INT_CONSTANT = new IntConstant();
    
    private final int value;
    
    private IntConstant(){
        super(Token.INT_CONSTANT_ID);
        this.value = 0;
    }
    
    public IntConstant(int value){
        super(Token.INT_CONSTANT_ID);
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getLexeme(){
        return Integer.toString(this.value);
    }
    
    @Override
    public String toString(){
        return "<INT_CONSTANT," + Integer.toString(value) + ">";
    }
}
