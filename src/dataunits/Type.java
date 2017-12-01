package dataunits;

/**
 *
 * @author jr
 */


public enum Type {
    
    //TYPE PREDEFINITIONS
    INT_ID(1),STRING_ID(2),VOID_ID(3),ERROR_ID(4);
    
    private int typeId;
    
    private Type(int typeId){
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
     
    
}
