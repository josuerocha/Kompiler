package modules;

import exceptions.LexerException;
import data_structures.SymbolTable;
import dataunits.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import util.PrintColor;

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
    Pattern p = Pattern.compile("[A-Za-z]");

    public Lexer(String filePath) throws LexerException {
        
        symbolTable = new SymbolTable();
        symbolTable.initializeReservedWords();
        
        this.currentLine = 1;

        try {
            this.fstream = new FileInputStream(filePath);
            this.reader = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
        } catch (FileNotFoundException ex) {
            String message = "**ERROR: FILE NOT FOUND. Please check if filepath was correctly specified.**";
            System.out.println(PrintColor.RED + message + PrintColor.RESET);
            System.exit(1);
            //throw new LexerException(message, ex);
        } catch (UnsupportedEncodingException ex) {
            String message = "**ERROR: UNSUPPORTED ENCODING**";
            System.out.println(PrintColor.RED + message + PrintColor.RESET);
            System.exit(1);
            //throw new LexerException(message, ex);
        }
        
    }

    public char readChar() throws LexerException {

        char character;
        try {
            character = (char) this.reader.read();
        } catch (IOException ex) {
            throw new LexerException("EXCEPTION: input and output error reading file.", ex);
        }

        return character;
    }
    
    public SymbolTable getSymbolTableInstance(){
        return this.symbolTable;
    }

    private void markReaderPosition() throws LexerException {
        try {
            reader.mark(50);
        } catch (IOException ex) {
            throw new LexerException("EXCEPTION: input and output error reading file.", ex);
        }
    }

    private void resetReaderPosition() throws LexerException {
        try {
            reader.reset();
        } catch (IOException ex) {
            throw new LexerException("EXCEPTION: input and output error reading file.", ex);
        }
    }

    private boolean readChar(char ch) throws LexerException {
        currentChar = readChar();
        if (currentChar == ch) {
            currentChar = ' ';
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("empty-statement")
    public Token getToken() {
        boolean checkForDisposables = true;
        while (checkForDisposables) {
            checkForDisposables = false;
            //DISCARD DELIMITER CHARACTERS
            while (checkDelimiter()) {
                checkForDisposables = true;
                if(currentChar == '\n'){
                    this.currentLine++;
                }
                currentChar = readChar();
            }

            //IDENTIFY MULTIPLE LINE COMMENTS AND SINGLE LINE COMMENTS
            if (currentChar == '/') {
                markReaderPosition();
                int commentLine = currentLine;
                if (readChar('/')) {
                    checkForDisposables = true;
                    while (!readChar('\n'));
                    this.currentLine++;
                    currentChar = ' ';
                } else if (currentChar == '*') {
                    checkForDisposables = true;
                    if (!discardMultiLineComment()) {
                        return new CompileError("Unclosed multiple line comment", commentLine, "Unclosed comment");
                    }
                } else {
                    currentChar = '/';
                    resetReaderPosition();
                }
            }
        }

        //IDENTIFY SEPARATORS
        switch(currentChar){
            case ';':
                currentChar = ' ';
                return new Token(';');
                
            case '(':
                currentChar = ' ';
                return new Token('(');
                
            case ')':
                currentChar = ' ';
                return new Token(')');
                
            case ',':
                currentChar = ' ';
                return new Token(',');
        }
        
        
        //IDENTIFY OPERATORS
        switch (currentChar) {
            case '=':
                if (readChar('=')) {
                    return Operator.EQUAL;
                } else {
                    return Operator.ASSIGN;
                }
            case '<':
                if (readChar('=')) {
                    return Operator.LE;
                } else {
                    return Operator.LT;
                }

            case '>':
                if (readChar('=')) {
                    return Operator.GE;
                } else {
                    return Operator.GT;
                }

            case '!':
                if (readChar('=')) {
                    return Operator.DIFFERENT;
                } else {
                    return new Token('!');
                }

            case '*':
                currentChar = ' ';
                return Operator.MUL;

            case '/':
                currentChar = ' ';
                return Operator.DIV;

            case '&':
                if (readChar('&')) {
                    return Operator.AND;
                }
            
            case '|':
                if (readChar('|')) {
                    return Operator.OR;
                }

            case '+':
                currentChar = ' ';
                return Operator.PLUS;
        }

        //RECOGNIZE NUMERICAL CONSTANT
        if (Character.isDigit(currentChar)) {
            StringBuilder buffer = new StringBuilder();
            do {
                buffer.append(currentChar);
                currentChar = readChar();
            } while (Character.isDigit(currentChar));

            int number = Integer.parseInt(buffer.toString());
            return new IntConstant(number);
        }
        
        //RECOGNIZE IDENTIFIERS
        if (Character.isLetter(currentChar)) {
            
            StringBuilder invalidChar = new StringBuilder();
            StringBuilder buffer = new StringBuilder();
            boolean isInvalidChar = false;
            boolean isValidChar;
            
            do {
                buffer.append(currentChar);
                
                 
                isValidChar = isAtoZChar(currentChar);
                if(!isValidChar){
                    invalidChar.append(currentChar);
                    if(!isInvalidChar){
                        isInvalidChar = true;
                    }
                }
                
                currentChar = readChar();
            } while (Character.isLetterOrDigit(currentChar));

            
            
            String lexeme = buffer.toString();
            if (symbolTable.contains(lexeme)) {
                Token t = symbolTable.getIdentifier(lexeme);

                if (t instanceof ReservedWord) {
                    return new ReservedWord(lexeme);
                } else {
                    return new Identifier(lexeme);
                }
            } else if(isInvalidChar){
                
                
                return new CompileError("Invalid character " + invalidChar.toString() + " in identifier specification < "+ lexeme + " >",currentLine,lexeme);
            }else {
                Identifier id = new Identifier(lexeme);
                symbolTable.put(lexeme, id);
                return id;
            }

        }

        //RECOGNIZE STRING LITERALS
        if (currentChar == '"') {
            StringBuffer buffer = new StringBuffer();

            while (!readChar('"')) {
                if (currentChar == '\n') {
                    this.currentLine++;
                    return new CompileError("Unclosed string literal", currentLine-1,buffer.toString());
                }

                buffer.append(currentChar);

            }
            currentChar = ' ';
            String literal = buffer.toString();
            return new LiteralConstant(literal);
        }

        if (currentChar == ((char) -1)) {
            return Token.EOF;
        }
        Token t = new CompileError("Invalid token " + currentChar, currentLine, ""+currentChar);
        currentChar = ' ';
        return t;
    }
    
    private boolean isAtoZChar(char ch){
        
        return  p.matcher(Character.toString(ch)).matches();
    }

    public int getCurrentLine() {
        return currentLine;
    }

    private boolean checkDelimiter() {
        return currentChar == ' ' || currentChar == '\r' || currentChar == '\t' || currentChar == '\b' || currentChar == '\n';
    }
    
    private boolean discardMultiLineComment() {
        while (true) {
            currentChar = readChar();

            switch (currentChar) {
                case '*':
                    if (readChar('/')) {
                        currentChar = ' ';
                        return true;
                    }   break;
                case '\n':
                    this.currentLine++;
                    break;
                case (char) -1:
                    return false;
                default:
                    break;
            }
        }
    }

}