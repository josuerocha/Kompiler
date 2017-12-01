package dataunits;

/**
 *
 * @author jr
 */


public class Type {
    
    //TYPE PREDEFINITIONS
    public enum TypeEnum {INT_ID,STRING_ID,VOID_ID,ERROR_ID };
    
    private TypeEnum typeId;
    
    public Type(TypeEnum typeId){
        this.typeId = typeId;
    }

    public TypeEnum getTypeId() {
        return typeId;
    }
     
    
    
}
