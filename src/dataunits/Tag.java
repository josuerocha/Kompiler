
package dataunits;

/**
 *
 * @author jr
 */
public enum Tag {
    
    /**
 * List of tokens and their corresponding code. 
 * 
 * <p>
 * **/
    
    //RESERVED WORDS
    PROGRAM(256),
    IF(257),
    THEN(258),
    ELSE(259),
    END(260),
    DO(261),
    WHILE(262),
    SCAN(263),
    PRINT(264),
    
    //DATATYPES
    INT(300),
    STRING(301),
    
    //RELATIONAL OPERATORS
    REL_OP(350),
    
    //MATHEMATICAL OPERATORS
    MATH_OP(400),
    
    //OTHER
    CONST(400),
    IDENTIFIER(401),
    SEMI_COLLON(402),
    PARENTHESIS(403);
    
    /**
    * Tag value container.
    */ 
    private final int tagId;
    
    /**
     * Constructor of the Tag enumerated.
     * @param tagValue : input argument to pass respective tag value.
     */
    Tag(int tagId){
        this.tagId = tagId;
    }
    
    public int getTagId(){
        return tagId;
    }
    
    public String toString(){
        return Integer.toString(tagId);
    }
    
}
