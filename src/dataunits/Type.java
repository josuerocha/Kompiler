package dataunits;

/**
 *
 * @author jr
 */


public enum Type {
    
    //TYPE PREDEFINITIONS
    INT(1),STRING(2),LOGICAL(3),VOID(4),ERROR(5);
    
    private int typeId;
    
    private Type(int typeId){
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
   
    
}