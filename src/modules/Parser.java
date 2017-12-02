package modules;

import data_structures.SymbolTable;
import data_structures.SymbolTableEntry;
import dataunits.CompileError;
import dataunits.Identifier;
import dataunits.IntConstant;
import dataunits.LiteralConstant;
import dataunits.Operator;
import dataunits.ReservedWord;
import dataunits.Token;
import dataunits.Type;
import util.PrintColor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author jr
 */
public class Parser extends Thread {
    
    private static final int MAX_THREADS = 10;
    
    private String filepath;
    private StringBuffer tokenFlow;
    private StringBuffer errorMessages;
    private boolean success = true;
    private Token currentToken;
    private boolean recoveringFromError = false;
    private SymbolTable symbolTable;
    Lexer lexer;
            
    
    public Parser(String path){
        this.filepath = path;
        lexer = new Lexer(this.filepath);
        symbolTable = lexer.getSymbolTableInstance();
        tokenFlow = new StringBuffer();
        errorMessages =  new StringBuffer();
        errorMessages.append("ARQUIVO: ").append(this.filepath).append("\n");
    }
    
    public void run(){
        currentToken = lexer.getToken();
        program();
        if(success){
            errorMessages.append(PrintColor.BLUE + "Compiled successfully :) \n" + PrintColor.RESET);
            //errorMessages.append(1);
        }
    }
    
    public String getErrorMessages(){
        return errorMessages.toString() +
        " _________________________________\n";
    }
    
    private void program(){
        
        switch(currentToken.getTag()){
            case ReservedWord.PROGRAM_ID:
                eat(ReservedWord.PROGRAM); declList(); stmtList(); eat(ReservedWord.END);
                break;
            default:
                error();
        }
    }
    
    private void declList(){
        
        switch(currentToken.getTag()){
            case ReservedWord.INT_ID:
            case ReservedWord.STRING_ID:
                declListPrime();
                break;
            case ReservedWord.IF_ID:
            case ReservedWord.DO_ID:
            case ReservedWord.SCAN_ID:
            case ReservedWord.PRINT_ID:
            case Token.IDENTIFIER_ID:
                
                break;
                
            default:
                error();
                synchTo(declistFollow);
        }
    }
    
    private void declListPrime(){

        switch(currentToken.getTag()){
            case ReservedWord.INT_ID:
            case ReservedWord.STRING_ID:
                decl(); declList();
                break;
            default:
                error();
                synchTo(declistPrimeFollow);
        }
    }
    
    private void decl(){
        
        switch(currentToken.getTag()){
            case ReservedWord.INT_ID:
            case ReservedWord.STRING_ID:
                Type type;
                type = type(); identifierList(type); eat(Token.SEMI_COLON);
                break;
                
            default:
                error();
                synchTo(declFollow);
        }
    }
    
    private void identifierList(Type type){
        
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                Token potentialId;
                potentialId = currentToken; eat(Identifier.IDENTIFIER); 
                possibleIdentifier(type);
                
                //SEMANTICS: identifier unity check
                checkIdentifierUnicity(potentialId, type);
                
                break;
                
            default:
                error();
                synchTo(identifierListFollow);
        }
    }
    
    private void possibleIdentifier(Type type){
        
        switch(currentToken.getTag()){
            case ',':
                Token potentialId;
                eat(Token.COMMA); 
                potentialId = currentToken; eat(Identifier.IDENTIFIER); 
                possibleIdentifier(type);
                
                checkIdentifierUnicity(potentialId, type);
                
                break;
            case ';':
            
                
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "possibleIdentifier\n" + PrintColor.RESET);
                error();
                synchTo(possibleIdentifierFollow);
        }
    }
    
    private void stmtList(){

        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
            case ReservedWord.DO_ID:
            case ReservedWord.PRINT_ID:
            case ReservedWord.SCAN_ID:
            case ReservedWord.IF_ID:
                stmtListPrime();
                break;
                
            case ReservedWord.WHILE_ID:
            case ReservedWord.ELSE_ID:
            case ReservedWord.END_ID:
                
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "stmtList \n" + PrintColor.RESET);
                error();
                synchTo(stmtListFollow);
        } 
     
    }
    
    private void stmtListPrime(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
            case ReservedWord.DO_ID:
            case ReservedWord.PRINT_ID:
            case ReservedWord.IF_ID:
            case ReservedWord.SCAN_ID:
                stmt(); stmtList();
                break;
                
            default:
                error();
                synchTo(stmtListPrimeFollow);
        }
    }
    

    
    public void assignStatement(){
        
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                Type typeId = Type.VOID, typeExpression;
                if(currentToken instanceof Identifier){ typeId = symbolTable.get(currentToken.getLexeme()).getType();}
                
                eat(Identifier.IDENTIFIER); eat(Operator.ASSIGN); typeExpression = simpleExpression();
                
                if(typeId != null && !typeId.equals(typeExpression) && !typeId.equals(Type.ERROR) && !typeExpression.equals(Type.ERROR)){
                    semanticError("type mismatch on assignment, expected " + typeId + " received " + typeExpression);
                }
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "assignStatement" + PrintColor.RESET);
                error();
                synchTo(assignStatementFollow);
        }
        
        if(recoveringFromError){
            synchTo(assignStatementFollow);
        }
    }
    
    private void ifStatement(){
        
        switch(currentToken.getTag()){
            case ReservedWord.IF_ID:
                eat(ReservedWord.IF); condition(); eat(ReservedWord.THEN); stmtListPrime(); ifStatementPrime();
                break;
            default:
                error();
                synchTo(ifStatementFollow);
        }
    }
    
    private void ifStatementPrime(){
        
        switch(currentToken.getTag()){
            case ReservedWord.END_ID:
                eat(ReservedWord.END);
                break;
                
            case ReservedWord.ELSE_ID:
                eat(ReservedWord.ELSE); stmtList(); eat(ReservedWord.END);
                break;
                
            default:
                error();
                synchTo(ifStatementPrimeFollow);
        }
    }
    
    private void whileStatement(){
        
        switch(currentToken.getTag()){
            case ReservedWord.DO_ID:
                eat(ReservedWord.DO); stmtListPrime(); stmtSufix();
                break;
                
            default:
                error();
                synchTo(whileStatementFollow);
        }
    }
    
    private void stmtSufix(){
        
        switch(currentToken.getTag()){
            case ReservedWord.WHILE_ID:
                eat(ReservedWord.WHILE); condition(); eat(ReservedWord.END);
                break;
            default:
                error();
                synchTo(stmtSuffixFollow);
        }
    }
    
    public void readStatement(){
        
        switch(currentToken.getTag()){
            case ReservedWord.SCAN_ID:
                eat(ReservedWord.SCAN); eat(Token.OPEN_PAREN); 
                eat(Identifier.IDENTIFIER); eat(Token.CLOSE_PAREN);
                break;
            
            default:
                error();
                synchTo(readStatementFollow);
                
        }
        
    }
    
    public void writeStatement(){
        
        switch(currentToken.getTag()){
            case ReservedWord.PRINT_ID:
                eat(ReservedWord.PRINT); eat(Token.OPEN_PAREN); writable(); eat(Token.CLOSE_PAREN);
                break;
                
            default:
                error();
                synchTo(writeStatementFollow);
        }
        
         if(recoveringFromError){
            synchTo(writeStatementFollow);
        }
    }
    
    private Type expression(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                Type type1, type2;
                type1 = simpleExpression(); type2 = expressionPrime();

                if(type1.equals(type2) || type2.equals(Type.VOID)){
                    type = type1;
                }else{
                    type = Type.ERROR;
                }
                
                break;
                
            default:
                error();
                synchTo(expressionFollow);
        }
        
        return type;
    }
    
    private Type simpleExpression(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                Type type1,type2;
                type1 = term(); type2 = simpleExpressionPrime(type1);
                
                if(type1.equals(type2) ){
                    type = type1;
                }else if(type2.equals(Type.VOID)){
                    type = type1;
                }else{
                    semanticError("type mismatch in expression operands");
                    type = Type.ERROR;
                }
                
                
                break;
                
            default:
                error();
                synchTo(simpleExpressionFollow);
        }
        return type;
    }
    
    private Type simpleExpressionPrime(Type type1){
        Type type = Type.VOID;
        boolean sumIndicator = false;
        switch(currentToken.getTag()){
            case '+':
                sumIndicator = true;
            case '-':
            case Operator.OR_ID:
                Type type2, output;
                addop(); type2 = term(); 
                if(type1.equals(Type.STRING) && type2.equals(Type.STRING) && !sumIndicator){
                    type = Type.ERROR;
                }else if(type1.equals(type2)){
                    type = type1;
                }else{
                    type = Type.ERROR;
                }
                
                output = simpleExpressionPrime(type2);
                
                if(output.equals(Type.ERROR)){
                    type = Type.ERROR;
                }
                
                break;
            
            case Operator.EQUAL_ID:
            case '>':
            case Operator.GREATER_EQUAL_ID:
            case '<':
            case Operator.LESS_EQUAL_ID:
            case Operator.DIFFERENT_ID:
            case ')':
            case ReservedWord.END_ID:
            case ReservedWord.THEN_ID:
            case ';':
                  
                break;
                
            default:
                error();
                synchTo(simpleExpressionPrimeFollow);
                
        }
        
        return type;
    }
    
    private void addop(){
        
        switch(currentToken.getTag()){
            case '+':
                eat(Operator.PLUS);
                break;
            case '-':
                eat(Operator.MINUS);
                break;
            case Operator.OR_ID:
                eat(Operator.OR);
                break;
                
            default:
                error();
                synchTo(addopFollow);
        }
    }
    
    private Type term(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                Type type1, type2;
                
                type1 = factora(); type2 = termPrime();
                
                if(type1.equals(type2) || type2.equals(Type.VOID)){
                    type = type1;
                }else{
                    type = Type.ERROR;

                }

            break;
            
            default:
                errorMessages.append(PrintColor.BLUE + "term\n" + PrintColor.RESET);
                error();
                synchTo(termFollow);
        }
        return type;
    }
    
    private Type termPrime(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case '*':
            case '/':
            case Operator.AND_ID:
                Type type1, type2;
                
                mulop(); type1 = factora(); type2 = termPrime();
                
                if(type1.equals(Type.INT) && (type2.equals(Type.INT) || type2.equals(Type.VOID))){
                    type = Type.INT;
                }else{
                    type = Type.ERROR;
                }
                break;
                
            case '+':
            case '-':
            case Operator.OR_ID:
            case Operator.EQUAL_ID:    
            case '>':
            case Operator.GREATER_EQUAL_ID:
            case '<':
            case Operator.LESS_EQUAL_ID:
            case Operator.DIFFERENT_ID:
            case ')':
            case ReservedWord.END_ID:
            case ReservedWord.THEN_ID:
            case ';':
                
                break;
                
            default:
                error();
                synchTo(termPrimeFollow);
        }
        
        return type;
    }
    
    private void mulop(){
        
        switch(currentToken.getTag()){
            case '*':
                eat(Operator.MUL);
                break;
                
            case '/':
                eat(Operator.DIV);
                break;
                
            case Operator.AND_ID:
                eat(Operator.AND);
                break;
                
            default:
                error();
                synchTo(mulopFollow);
        }
    }
    
    private Type expressionPrime(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case Operator.EQUAL_ID:
            case '>':
            case Operator.GREATER_EQUAL_ID:
            case '<':
            case Operator.LESS_EQUAL_ID:
            case Operator.DIFFERENT_ID:
                
                relop(); type = simpleExpression();
                break;
                
            case ')':
            case ReservedWord.END_ID:
            case ReservedWord.THEN_ID:
                
                break;
            default:
                error();
                synchTo(expressionPrimeFollow);
        }
        return type;
    }
    
    private Type factora(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case '!':
                eat(Operator.NEG); type = factor();
                break;
            case '-':
                eat(Operator.MINUS); type = factor();
                break;
                
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                type = factor();
                break;
                
            default:
                error();
                synchTo(factoraFollow);
        }
        
        return type;
    }
    
    private Type factor(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                //Storing type for verification
                
                if(currentToken instanceof Identifier){
                    
                    type = symbolTable.get(currentToken.getLexeme()).getType();
                    
                    if(type == null){
                        semanticError("use of undeclared identifier < "+ currentToken.getLexeme() + " >");
                        type = Type.ERROR;
                    }
                    
                }
                
                eat(Identifier.IDENTIFIER);
                break;
            
            case Token.INT_CONSTANT_ID:
            case Token.LIT_CONSTANT_ID:
                type = constant();
                break;
                
            case '(':
                
                eat(Token.OPEN_PAREN); type = expression(); eat(Token.CLOSE_PAREN);
                break; 
                
            default:
                error();
                synchTo(factorFollow);
        }
        
        return type;
    }
    
    private Type constant(){
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case Token.INT_CONSTANT_ID:
                eat(new IntConstant(1));
                type = Type.INT;
                break;
            case Token.LIT_CONSTANT_ID:
                eat(new LiteralConstant(""));
                type = Type.STRING;
                break;
            default:
                error();
                synchTo(constantFollow);
        }
        
        return type;
    }
    
    private void writable(){
        
        switch(currentToken.getTag()){
            case Token.LIT_CONSTANT_ID:
                eat(new LiteralConstant("a"));
                break;
                
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case Token.INT_CONSTANT_ID:
                simpleExpression();
                break;
                
            default:
                error();
                synchTo(writableFollow);
        }

    }
    
    private void relop(){
        
        switch(currentToken.getTag()){
            case Operator.EQUAL_ID:
                eat(Operator.EQUAL);
                break;
                
            case '>':
                eat(Operator.GT);
                break;
                
            case Operator.GREATER_EQUAL_ID:
                eat(Operator.GE);
                break;
                
            case '<':
                eat(Operator.LT);
                break;
                
            case Operator.LESS_EQUAL_ID:
                eat(Operator.LE);
                break;
                
            case Operator.DIFFERENT_ID:
                eat(Operator.DIFFERENT);
                break;
                
            default:
                error();
                synchTo(relopFollow);
            }
    }
    
    private void condition(){
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                
                expression();
                break;
                
            default:
                error();
                synchTo(conditionFollow);
        }
    }
    
    private void stmt(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                    assignStatement(); eat(Token.SEMI_COLON);
                break;
                
            case ReservedWord.DO_ID:
                whileStatement();
                break;
                
            case ReservedWord.PRINT_ID:
                writeStatement(); eat(Token.SEMI_COLON);
                break;
            case ReservedWord.SCAN_ID:
                readStatement(); eat(Token.SEMI_COLON);
                break;
            case ReservedWord.IF_ID:
                ifStatement();
                break;
            default:
                error();
                synchTo(stmtFollow);
        }
    }
    
    
    private Type type(){
        Type type = Type.VOID;
        switch (currentToken.getTag()){
            case ReservedWord.INT_ID:
                    eat(ReservedWord.INT);
                    type = Type.INT;
                break;
                
            case ReservedWord.STRING_ID:
                    eat(ReservedWord.STRING);
                    type = Type.STRING;
                break;
                
            default:
                error();
                synchTo(typeFollow);
        }
        
        return type;
    }
    
    private void eat(Token t){
        if (currentToken.equals(t)) {
            currentToken = lexer.getToken();
        }else {
            error();
        }
    }
    
    private void synchTo(Token[] followSet){
       
        boolean followElementFound = false;
        do{
            
            for(Token followElement : followSet){
                if(followElement.equals(currentToken)){
                    followElementFound = true;
                    recoveringFromError = false;
                    break;
                }
            }
           
        }while(!followElementFound && (currentToken = lexer.getToken()) != Token.EOF);
    }
    
    private void error(){
        success = false;
        if(!recoveringFromError){
            if(currentToken instanceof CompileError){
                CompileError compileError = (CompileError) currentToken;
                errorMessages.append(PrintColor.RED + "Lexical error: ").append(compileError.getMessage()).append(" on line ").append(compileError.getLine()).append(PrintColor.RESET + "\n");
            }else if(currentToken.equals(Token.EOF)){
                errorMessages.append(PrintColor.RED + "Syntax error: unexpected end of file.").append( PrintColor.RESET + "\n");
            }else{
                errorMessages.append(PrintColor.RED + "Syntax error: unexpected token ").append(currentToken).append(" on line ").append(lexer.getCurrentLine()).append(PrintColor.RESET + "\n");
            }
            recoveringFromError = true;
        }
    }
    
    public void semanticError(String message){
        this.success = false;
        if(!recoveringFromError){
            errorMessages.append(PrintColor.RED + "Semantic error: ").append(message).append(" on line ").append(lexer.getCurrentLine()).append(".").append("\n" + PrintColor.RESET);
        }
    }
    
    public void checkIdentifierUnicity(Token token, Type type){
        //SEMANTICS: identifier unity check
        if(token instanceof Identifier){
            Identifier id = (Identifier) token;
            SymbolTableEntry idInfo = symbolTable.get(id.getLexeme());

            if(idInfo.isInstalled()){
                semanticError("redeclaration of identifier < " + id.getLexeme() + " >");
            }else{
                idInfo.installType(type);
            }
        }
    }
    
    public static void main(String[] args) {
        
        if(args.length == 0){
            System.out.println("No file path informed. Please inform file paths.");
            System.out.println("Usage: java -jar KPiler.jar file1.txt file2.txt");
        }
        
        List<String> paths = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<Parser> compileJobs = new ArrayList<>();
        
        paths.addAll(Arrays.asList(args));
        
        
        while(paths.size() > 0 || compileJobs.size() > 0){
            
            if(compileJobs.size() < MAX_THREADS && paths.size() > 0){
                compileJobs.add(new Parser(paths.remove(0)));
                compileJobs.get(compileJobs.size() -1).start();
            }
            
            for(int i=0 ; i<compileJobs.size(); i++){
                if(!compileJobs.get(i).isAlive()){
                    errorMessages.add(compileJobs.get(i).getErrorMessages());
                    compileJobs.remove(i);
                }
            }
        }
        //PRINTING OUTPUT
        for(int i = 0; i<errorMessages.size(); i++){
            System.out.print(errorMessages.get(i));
            
        }
       
    }
    
        //FOLLOW SETS OF EACH PRODUCTION
    Token[] declistFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            declistPrimeFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            declFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            identifierListFollow = {Token.SEMI_COLON},
            possibleIdentifierFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            typeFollow = {Identifier.IDENTIFIER},
            stmtListFollow = {ReservedWord.WHILE,ReservedWord.END,ReservedWord.ELSE},
            assignStatementFollow = {Token.SEMI_COLON},
            ifStatementFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            ifStatementPrimeFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            whileStatementFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            stmtSuffixFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            readStatementFollow = {Token.SEMI_COLON},
            writeStatementFollow = {Token.SEMI_COLON},
            writableFollow = {Token.CLOSE_PAREN},
            simpleExpressionPrimeFollow = {Operator.EQUAL, Operator.GT, Operator.GE, Operator.LT, Operator.LE,
                                     Operator.DIFFERENT,Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.THEN,
                                     Token.SEMI_COLON},
            termPrimeFollow = {Operator.PLUS, Operator.MINUS, Operator.OR, Operator.EQUAL, Operator.GT, 
                                Operator.GE,Operator.LT, Operator.LE, Operator.DIFFERENT, Token.CLOSE_PAREN, 
                                ReservedWord.END, ReservedWord.THEN, ReservedWord.SEMI_COLON},
            factoraFollow = {Operator.MUL, Operator.DIV, Operator.AND, Operator.PLUS, Operator.MINUS, Operator.OR,
                            Operator.EQUAL, Operator.GT, Operator.GE, Operator.LT,Operator.LE, Operator.DIFFERENT, 
                            Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.THEN, Token.SEMI_COLON},
            factorFollow = {Operator.MUL, Operator.DIV, Operator.AND, Operator.PLUS, Operator.MINUS, Operator.OR,
                            Operator.EQUAL, Operator.GT, Operator.GE, Operator.LT,Operator.LE, Operator.DIFFERENT, 
                            Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.THEN, Token.SEMI_COLON},
            relopFollow = {Operator.NEG, Operator.MINUS, Identifier.IDENTIFIER, Token.OPEN_PAREN,
                            IntConstant.INT_CONSTANT,LiteralConstant.LIT_CONSTANT},
            addopFollow = {Operator.NEG, Operator.MINUS, Identifier.IDENTIFIER, Token.OPEN_PAREN,
                            IntConstant.INT_CONSTANT,LiteralConstant.LIT_CONSTANT},
            mulopFollow = {Operator.NEG, Operator.MINUS, Identifier.IDENTIFIER, Token.OPEN_PAREN,
                            IntConstant.INT_CONSTANT,LiteralConstant.LIT_CONSTANT},
            constantFollow = {Operator.MUL, Operator.DIV, Operator.AND, Operator.PLUS, Operator.MINUS,Operator.OR,
                                Operator.EQUAL, Operator.GT, Operator.GE, Operator.LT, Operator.LE, Operator.DIFFERENT,
                                Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.THEN, Token.SEMI_COLON},
            stmtFollow =      {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.IF,ReservedWord.SCAN,
                                ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            termFollow =     {Operator.PLUS, Operator.MINUS, Operator.OR, Operator.EQUAL,Operator.GT, Operator.GE,
                              Operator.LT, Operator.LE, Operator.DIFFERENT,Token.CLOSE_PAREN, ReservedWord.END,
                              ReservedWord.THEN, Token.SEMI_COLON},
            stmtListPrimeFollow = {ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            simpleExpressionFollow = {Operator.EQUAL, Operator.GT, Operator.GE, Operator.LT, Operator.LE,
                                Operator.DIFFERENT, Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.END,
                                Token.SEMI_COLON},
            expressionFollow = {Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.THEN},
            expressionPrimeFollow = {Token.CLOSE_PAREN, ReservedWord.END, ReservedWord.THEN},
            conditionFollow = {ReservedWord.END, ReservedWord.THEN};

}