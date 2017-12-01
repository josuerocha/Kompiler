package data_structures;

import dataunits.Token;
import dataunits.Type;

/**
 *
 * @author jr
 */


public class SymbolTableEntry {
    
    private Token token;
    private Type type;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    
}
