package data_structures;

import dataunits.Token;
import dataunits.Attribute;
import dataunits.Type;

/**
 *
 * @author jr
 */


public class SymbolTableEntry {
    
    private Token token;
    private Type type;
    private boolean installed;
    private int relativeAdress;

    public SymbolTableEntry(){
        this.installed = false;
    }
    
    public SymbolTableEntry(Token token){
        this.installed = false;
        this.token = token;
    }
    
    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }
    
    public void installType(Type type){
        this.type = type;
        this.installed = true;
    }

    public int getRelativeAdress() {
        return relativeAdress;
    }

    public void setRelativeAdress(int relativeAdress) {
        this.relativeAdress = relativeAdress;
    }
    
    
    
    public boolean isInstalled(){
        return this.installed;
    }
    
}
