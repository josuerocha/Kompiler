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
package modules;

import Exceptions.LexerException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author jr
 */
public class Lexer extends Thread {

    private BufferedReader reader;
    private int currentLine;
    
    public Lexer(String filePath) throws LexerException{
        
        this.currentLine = 0;
        
        try{
            this.reader = new BufferedReader(new FileReader(filePath));
        }catch(FileNotFoundException ex){
            throw new LexerException("ERROR: File not found.",ex);
        }
        
    }
    
    public void run() throws LexerException{
        char character;
        while((character = readChar()) != null){
            
        }
        
    }
    
    public char readChar() throws LexerException{
        
        char character;
        try{
            character = (char) this.reader.read();
        }catch(IOException ex){
            throw new LexerException("ERROR: input and output error reading file.",ex);
        }
        
        return character;
    }

}
