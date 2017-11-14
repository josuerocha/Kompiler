package data_structures;

import dataunits.ReservedWord;
import dataunits.Token;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jr
 */
public class SymbolTable {
    
    static SymbolTable instance = null;
    private Map<String,Token> map = new HashMap<>();
    
    public SymbolTable(){
        
    }
    
    public void initializeReservedWords(){
        this.put(ReservedWord.DO.getLexeme(), ReservedWord.DO);
        this.put(ReservedWord.ELSE.getLexeme(), ReservedWord.ELSE);
        this.put(ReservedWord.END.getLexeme(), ReservedWord.END);
        this.put(ReservedWord.IF.getLexeme(), ReservedWord.IF);
        this.put(ReservedWord.SCAN.getLexeme(), ReservedWord.PRINT);
        this.put(ReservedWord.WHILE.getLexeme(), ReservedWord.WHILE);
        this.put(ReservedWord.PRINT.getLexeme(), ReservedWord.PRINT);
        this.put(ReservedWord.THEN.getLexeme(), ReservedWord.THEN);
        this.put(ReservedWord.PROGRAM.getLexeme(), ReservedWord.PROGRAM);
        this.put(ReservedWord.INT.getLexeme(), ReservedWord.INT);
        this.put(ReservedWord.STRING.getLexeme(), ReservedWord.STRING);
    }
    
    public void put(String key,Token word){
        if(!map.containsKey(key)){
            map.put(key,word);
        }
    }
    
    public boolean contains(String key){
        return map.containsKey(key);
    }
    
    public Token get(String key){
        return map.get(key);
    }
}
