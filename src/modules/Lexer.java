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

        this.currentLine = 1;

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
    
    private void markReaderPosition() throws LexerException{
        try{
            reader.mark(50);
        } catch (IOException ex) {
            throw new LexerException("EXCEPTION: input and output error reading file.", ex);
        }
    }
    
    private void resetReaderPosition() throws LexerException{
        try{
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

    public Token getToken() {
        boolean checkForDisposables = true;
        while(checkForDisposables){
            checkForDisposables = false;
            //DISCARD DELIMITER CHARACTERS
            while (checkDelimiter()) {
                checkForDisposables = true;
                currentChar = readChar();
            }

            //IDENTIFY MULTIPLE LINE COMMENTS AND SINGLE LINE COMMENTS
            if (currentChar == '/') {
                markReaderPosition();
                int commentLine = currentLine;
                if (readChar('/')) {
                    checkForDisposables = true;
                    while (!readChar('\n'));
                    currentChar = ' ';
                }
                else if (currentChar == '*') {
                    checkForDisposables = true;
                    if (!discardMultiLineComment()) {
                        return new CompileError("Unclosed multiple line comment", commentLine);
                    }
                }else{
                    currentChar = '/';
                    resetReaderPosition();
                }
            }
        }

        //IDENTIFY OPERATORS
        switch (currentChar) {
            case '=':
                if (readChar('=')) {
                    return Operator.EQ;
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
            } else {
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
                    return new CompileError("Unclosed string literal", currentLine-1);
                }

                buffer.append(currentChar);

            }
            currentChar = ' ';
            String literal = buffer.toString();
            return new LiteralConstant(literal);
        }

        if (currentChar == ((char) -1)) {
            return null;
        }
        Token t = new Token(currentChar);
        currentChar = ' ';
        return t;
    }

    private boolean checkInvalidCharacter() {
        return currentChar == 'ç' || currentChar == 'Ç';
    }

    private boolean checkDelimiter() {
        return currentChar == ' ' || currentChar == '\r' || currentChar == '\t' || currentChar == '\b' || currentChar == '\n';
    }

    private boolean discardMultiLineComment() {
        while (true) {
            currentChar = readChar();

            if (currentChar == '*') {
                if (readChar('/')) {
                    currentChar = ' ';
                    return true;
                }
            } else if (currentChar == (char) -1) {
                return false;
            }
        }
    }

}
