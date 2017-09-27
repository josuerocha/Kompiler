/*
 * The MIT License
 *
 * Copyright 2017 Josué Rocha Lima.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
    private Map<String,Token> map = new HashMap<String,Token>();
    
    public static SymbolTable getInstance(){
        if(instance == null){
            instance = new SymbolTable();
        }
        
        return instance;
    }
    
    private SymbolTable(){
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
