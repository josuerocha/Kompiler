


package modules;

import Exceptions.LexerException;
import datastructures.SymbolTable;
import dataunits.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import static jdk.nashorn.internal.parser.TokenType.EOF;

/**
 *
 * @author jr
 */
public class Lexer extends Thread {

    private BufferedReader reader;
    FileInputStream fstream;
    private int currentLine;
    private char currentChar = ' ';
    private SymbolTable symbolTable = null;

    public Lexer(String filePath) throws LexerException {

        this.currentLine = 0;

        try {
            this.fstream = new FileInputStream(filePath);
            this.reader = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
        } catch (FileNotFoundException ex) {
            throw new LexerException("ERROR: File not found.", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new LexerException("ERROR: File not found.", ex);
        }
        
        symbolTable = SymbolTable.getInstance();
    }

    public char readChar() throws LexerException {
        
        char character;
        try {
            character = (char) this.reader.read();
            if (character == '\n') {
                this.currentLine++;
            }

        } catch (IOException ex) {
            throw new LexerException("EXCEPTION: input and output error reading file.", ex);
        }

        return character;
    }

    public boolean readChar(char ch) throws LexerException {
        currentChar = readChar();
        if (currentChar == ch) {
            currentChar = ' ';
            return true;
        } else {
            return false;
        }
    }

    public Token getToken() {
        //DISCARD DELIMITER CHARACTERS
        while (checkDelimiter()) {
            currentChar = readChar();
        }
        //IDENTIFY INVALID CHARACTERS
        if(checkInvalidCharacters()){
            CompileError error = new CompileError("Invalid character: " + currentChar,currentLine);
            currentChar = ' ';
            return error;
        }
        
        //IDENTIFY OPERATORS AND COMMENTS
        switch (currentChar) {
            case '=':
                if (readChar('=')) {
                    return new RelOperator(RelOperator.EQUAL_ID);
                } else {
                    return new MathOperator(MathOperator.ASSIGN_ID);
                }
            case '<':
                if (readChar('=')) {
                    return new RelOperator(RelOperator.LESS_EQUAL_ID);
                } else {
                    return new RelOperator(RelOperator.LESS_ID);
                }

            case '>':
                if (readChar('=')) {
                    return new RelOperator(RelOperator.GREATER_EQUAL_ID);
                } else {
                    return new RelOperator(RelOperator.LESS_EQUAL_ID);
                }

            case '!':
                if (readChar('=')) {
                    return new RelOperator(RelOperator.DIFFERENT_ID);
                } else {
                    return new Token('!');
                }

            case '*':
                currentChar = ' ';
                return new MathOperator(MathOperator.MUL_ID);

            case '/':
                if(readChar('/')){
                    while(!readChar('\n'));
                    currentChar = ' ';
                }
                if(readChar('*')){
                    if(!discardMultiLineComment()){
                        return new CompileError("Unclosed multiple line comment",currentLine);
                    }
                }else{
                    currentChar = ' ';
                    return new MathOperator(MathOperator.MUL_ID);
                }
                
            case '&':
                if (readChar('&')) {
                    return new MathOperator(MathOperator.MUL_ID);
                }
        }

        //RECOGNIZE NUMERICAL CONSTANT
        if (Character.isDigit(currentChar)) {
            StringBuffer buffer = new StringBuffer();
            do {
                buffer.append(currentChar);
                currentChar = readChar();
            } while (Character.isDigit(currentChar));

            int number = Integer.parseInt(buffer.toString());
            return new IntConstant(number);
        }

        //RECOGNIZE IDENTIFIERS
        if (Character.isLetter(currentChar)) {
            StringBuffer buffer = new StringBuffer();

            do {
                buffer.append(currentChar);
                currentChar = readChar();
            } while (Character.isLetterOrDigit(currentChar));

            String lexeme = buffer.toString();
            if (symbolTable.contains(lexeme)) {
                Token t = symbolTable.get(lexeme);

                if (t instanceof ReservedWord) {
                    return new ReservedWord(lexeme);
                } else {
                    return new Identifier(lexeme);
                }
            }else{
                Identifier id = new Identifier(lexeme);
                symbolTable.put(lexeme,id);
                return id;
            }
            
        }

        //RECOGNIZE STRING LITERALS
        if (currentChar == '“') {
            StringBuffer buffer = new StringBuffer();

            while (!readChar('”')) {
                if(currentChar == '\n'){
                    return new CompileError("Unclosed string literal", currentLine);
                }
                
                buffer.append(currentChar);
                
                
            }
            currentChar = ' ';
            String literal = buffer.toString();
            return new LiteralConstant(literal);
        }
        
        if(currentChar == ((char) -1)){
            return null;
        }

        Token t = new Token(currentChar);
        currentChar = ' ';
        return t;
    }
    
    private boolean checkInvalidCharacters(){
        return currentChar == 'ç' || currentChar == 'Ç';
    }
    
    private boolean checkDelimiter() {
        return currentChar == ' ' || currentChar == '\r' || currentChar == '\t' || currentChar == '\b' || currentChar == '\n';
    }
    
    private boolean discardMultiLineComment(){
        
        while(true){
            currentChar = readChar();
            
            if(currentChar == '*'){
                if(readChar('/')){
                    currentChar = ' ';
                    return true;
                }else if(currentChar == (char) -1){
                    return false;
                }
            }
        }
    }

}
