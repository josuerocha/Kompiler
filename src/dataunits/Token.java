/*
 * The MIT License
 *
 * Copyright 2017 jr.
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
package dataunits;

/**
 *
 * @author jr
 */
public class Token {
    
        /**
 * List of tokens and their corresponding code. 
 * 
 * <p>
 * **/
    //RESERVED WORDS
    public final static int
    RESERVED_WORD = 256,
    
    //
    OPERATOR = 300,
            
    //OTHER
    INT_CONSTANT = 450,
    LITERAL_CONSTANT = 451,
    IDENTIFIER = 452,
    SEMI_COLLON = 453,
    PARENTHESIS = 454,
    ERROR = 455;
//------------------------------------------------------------------------------------
    
    private final int tag;
    private int line;
    
    public Token(int tag){
        this.tag = tag;
    }
    
    public int getTag(){
        return tag;
    }
    
    public void setLine(int line){
        this.line = line;
    }
    
    public int getLine(){
        return this.line;
    }
    
    public String toString(){
        return "<" + ((char) tag) + ">";
    }
}
