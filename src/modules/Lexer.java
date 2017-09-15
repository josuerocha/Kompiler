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
import dataunits.Token;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author jr
 */
public class Lexer extends Thread {

    private BufferedReader reader;
    FileInputStream fstream; 
    private int currentLine;
    private char currentChar;
    
    public Lexer(String filePath) throws LexerException{
        
        this.currentLine = 0;
        
        try{
            this.fstream = new FileInputStream(filePath);
            this.reader = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
        }catch(FileNotFoundException ex){
            throw new LexerException("ERROR: File not found.",ex);
        }catch(UnsupportedEncodingException ex){
            throw new LexerException("ERROR: File not found.",ex);
        }
        
    }
    
    
    public void run(){
        char character;
        while((character = readChar()) != ((char)-1)){
            
            
            
        }
        
    }
    
    public char readChar() throws LexerException{
        
        char character;
        try{
            character = (char) this.reader.read();
            if(character == '\n'){
                this.currentLine++;
            }
            
        }catch(IOException ex){
            throw new LexerException("ERROR: input and output error reading file.",ex);
        }
        
        return character;
    }
    
    public boolean readChar(char ch) throws LexerException{
        return readChar() == ch;
    }
    
    public Token getToken(){
        
        while(checkDelimiter()){
            currentChar = readChar();
        }
        
        switch(currentChar){
            case '=':
                
                
            break;
        }
        
        
        Token t = new Token(currentChar);
        currentChar = '';
        return t;
    }
    
    private boolean checkDelimiter(){
        if(currentChar == ' ' || currentChar == '\r' || currentChar == '\t' || currentChar == '\b' || currentChar == '\n'){
            return true;
        }else{
            return false;
        }
    }

}
