package dataunits;

/**
 *
 * @author jr
 */


public enum Type {
    
    //TYPE PREDEFINITIONS
    INT(1),STRING(2),VOID(3),ERROR(4);
    
    private int typeId;
    
    private Type(int typeId){
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
   
    
}
