package modules;

import dataunits.CompileError;
import dataunits.Identifier;
import dataunits.IntConstant;
import dataunits.LiteralConstant;
import dataunits.Operator;
import dataunits.ReservedWord;
import dataunits.Token;
import util.PrintColor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jr
 */
public class Parser extends Thread {
    
    private static final int MAX_JOBS = 10;
    private String filepath;
    private StringBuffer tokenFlow;
    private StringBuffer errorMessages;
    private Token currentToken;
    Lexer lexer;
    
    
    //FOLLOW SETS OF EACH PRODUCTION
    Token[] declistFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            declistPrimeFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            declFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            possibleIdentFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, ReservedWord.SCAN},
            typeFollow = {Identifier.IDENTIFIER},
            stmtListFollow = {ReservedWord.WHILE,ReservedWord.END,ReservedWord.ELSE},
            assignStmtFollow = {Token.SEMI_COLON},
            ifStmtFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            ifStmtPrimeFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            whileFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            stmtSuffixFollow = {Identifier.IDENTIFIER, ReservedWord.DO, ReservedWord.PRINT, 
                            ReservedWord.IF,ReservedWord.SCAN, ReservedWord.WHILE, ReservedWord.END, ReservedWord.ELSE},
            readStmtFollow = {Token.SEMI_COLON},
            writeStmtFollow = {Token.SEMI_COLON},
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
                            IntConstant.INT_CONSTANT,LiteralConstant.LITERAL_CONSTANT},
            mulopFollow = {Operator.NEG, Operator.MINUS, Identifier.IDENTIFIER, Token.OPEN_PAREN,
                            IntConstant.INT_CONSTANT,LiteralConstant.LITERAL_CONSTANT},
            
    
    public Parser(String path){
        this.filepath = path;
        lexer = new Lexer(this.filepath);
        tokenFlow = new StringBuffer();
        errorMessages =  new StringBuffer();
        errorMessages.append("ARQUIVO: " + this.filepath + "\n");
    }
    
    public void run(){
        

        currentToken = lexer.getToken();
        program();
    }
    
    public String getTokenFlow(){
        return "FILE: " + this.filepath + "\n" + this.tokenFlow.toString();
    }
    
    public String getErrorMessages(){
        return errorMessages.toString() +
        " _________________________________";
    }
    
    private void program(){
        switch(currentToken.getTag()){
            case ReservedWord.PROGRAM_ID:
                eat(ReservedWord.PROGRAM); declList(); stmtList(); eat(ReservedWord.END);
                break;
            default:
                errorMessages.append(PrintColor.BLUE +"program \n" + PrintColor.RESET);
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
            case Token.IDENTIFIER:
                
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "declList\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void declListPrime(){

        switch(currentToken.getTag()){
            case ReservedWord.INT_ID:
            case ReservedWord.STRING_ID:
                decl(); declList();
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "declListPrime \n" + PrintColor.RESET);
                error();
        }
    }
    
    private void decl(){
        switch(currentToken.getTag()){
            case ReservedWord.INT_ID:
            case ReservedWord.STRING_ID:
                type(); identifierList(); eat(Token.SEMI_COLON);
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "decl\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void identifierList(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(Identifier.IDENTIFIER); possibleIdentifier();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "identifierList\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void possibleIdentifier(){
        switch(currentToken.getTag()){
            case ',':
                eat(Token.COMMA); eat(Identifier.IDENTIFIER); possibleIdentifier();
                break;
            case ';':
            
                
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "possibleIdentifier\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void stmtList(){
        System.out.println(currentToken);
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
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
        } 
     
    }
    
    private void stmtListPrime(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
            case ReservedWord.DO_ID:
            case ReservedWord.PRINT_ID:
            case ReservedWord.IF_ID:
            case ReservedWord.SCAN_ID:
                stmt(); stmtList();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "stmtListPrime \n" + PrintColor.RESET);
                error();
        }
    }
    

    
    public void assignStatement(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(Identifier.IDENTIFIER); eat(new Operator('=')); simpleExpression();
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "assignStatement" + PrintColor.RESET);
                error();
        }
    }
    
    private void ifStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.IF_ID:
                eat(ReservedWord.IF); condition(); eat(ReservedWord.THEN); stmtListPrime(); ifStatementPrime();
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "ifStatement\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void ifStatementPrime(){
        switch(currentToken.getTag()){
            case ReservedWord.END_ID:
                eat(ReservedWord.END);
            case ReservedWord.ELSE_ID:
                eat(ReservedWord.ELSE); stmtList(); eat(ReservedWord.END);
            default:
                errorMessages.append(PrintColor.BLUE + "ifStatementPrime\n" + PrintColor.RESET);
        }
    }
    
    private void simpleExpressionPrime(){
        switch(currentToken.getTag()){
            case '+':
            case '-':
            case Operator.OR_ID:
                addop(); term(); simpleExpressionPrime();
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
                errorMessages.append(PrintColor.BLUE + "simpleExpressionPrime\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void whileStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.DO_ID:
                eat(ReservedWord.DO); stmtListPrime(); stmtSufix();
                
            default:
                errorMessages.append(PrintColor.BLUE + "whileStatement\n" + PrintColor.RESET);
        }
    }
    
    private void stmtSufix(){
        switch(currentToken.getTag()){
            case ReservedWord.WHILE_ID:
                eat(ReservedWord.WHILE); condition(); eat(ReservedWord.END);
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "stmtSufix\n" + PrintColor.RESET);
                error();
        }
    }
    
    public void readStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.SCAN_ID:
                eat(ReservedWord.SCAN); eat(Token.OPEN_PAREN); 
                eat(Identifier.IDENTIFIER); eat(Token.CLOSE_PAREN);
                break;
            
            default:
                errorMessages.append(PrintColor.BLUE + "readStatement\n" + PrintColor.RESET);
        }
        
    }
    
    public void writeStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.PRINT_ID:
                eat(ReservedWord.PRINT); eat(Token.OPEN_PAREN); writable(); eat(Token.CLOSE_PAREN);
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "writeStatement\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void expression(){
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER:
            case '(':
            case IntConstant.INT_CONSTANT:
            case LiteralConstant.LITERAL_CONSTANT:
                simpleExpression(); expressionPrime();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "expression\n" + PrintColor.RESET);
        }
    }
    
    private void simpleExpression(){
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER:
            case '(':
            case IntConstant.INT_CONSTANT:
            case LiteralConstant.LITERAL_CONSTANT:
                term(); simpleExprPrime();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "simpleExpression\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void simpleExprPrime(){
        switch(currentToken.getTag()){
            case '+':
            case '-':
            case Operator.OR_ID:
                addop(); term(); simpleExprPrime();
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
                errorMessages.append(PrintColor.BLUE + "simpleExprPrime\n" + PrintColor.RESET);
                error();
                
        }
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
                errorMessages.append(PrintColor.BLUE + "addop\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void term(){
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER:
            case '(':
            case IntConstant.INT_CONSTANT:
            case LiteralConstant.LITERAL_CONSTANT:
                factora(); termPrime();
            break;
            
            default:
                errorMessages.append(PrintColor.BLUE + "term\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void termPrime(){
        switch(currentToken.getTag()){
            case '*':
            case '/':
            case Operator.AND_ID:
                mulop(); factora(); termPrime();
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
                errorMessages.append(PrintColor.BLUE + "termPrime\n" + PrintColor.RESET);
                error();
        }
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
                errorMessages.append(PrintColor.BLUE + "mulop\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void expressionPrime(){
        switch(currentToken.getTag()){
            case Operator.EQUAL_ID:
            case '>':
            case Operator.GREATER_EQUAL_ID:
            case '<':
            case Operator.LESS_EQUAL_ID:
            case Operator.DIFFERENT_ID:
                relop(); simpleExpression();
                break;
                
            case ')':
            case ReservedWord.END_ID:
            case ReservedWord.THEN_ID:
                
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "expressionPrime\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void factora(){

        switch(currentToken.getTag()){
            case '!':
                eat(Operator.NEG); factor();
                break;
            case '-':
                eat(Operator.MINUS); factor();
                break;
                
            case Token.IDENTIFIER:
            case '(':
            case IntConstant.INT_CONSTANT:
            case LiteralConstant.LITERAL_CONSTANT:
                factor();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "factora\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void factor(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(Identifier.IDENTIFIER);
                break;
                
            case Token.INT_CONSTANT:
            case Token.LITERAL_CONSTANT:
                constant();
                break;
                
            case '(':
                eat(Token.OPEN_PAREN); expression(); eat(Token.CLOSE_PAREN);
                break; 
                
            default:
                errorMessages.append(PrintColor.BLUE + "factor\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void constant(){
        switch(currentToken.getTag()){
            case Token.INT_CONSTANT:
                eat(new IntConstant(1));
                break;
            case Token.LITERAL_CONSTANT:
                eat(new LiteralConstant("a"));
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "constant\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void writable(){

        switch(currentToken.getTag()){
            case Token.LITERAL_CONSTANT:
                eat(new LiteralConstant("a"));
                break;
                
            case '!':
            case '-':
            case Token.IDENTIFIER:
            case '(':
            case Token.INT_CONSTANT:
                simpleExpression();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "writable\n" + PrintColor.RESET);
                error();
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
                errorMessages.append(PrintColor.BLUE + "relop\n" + PrintColor.RESET);
                error();
            }
    }
    
    private void condition(){
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER:
            case '(':
            case IntConstant.INT_CONSTANT:
            case LiteralConstant.LITERAL_CONSTANT:
                expression();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "condition\n" + PrintColor.RESET);
                error();
        }
    }
    
    private void stmt(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
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
                errorMessages.append(PrintColor.BLUE + "stmt\n" + PrintColor.RESET);
                error();
        }
    }
    
    
    private void type(){
        switch (currentToken.getTag()){
            case ReservedWord.INT_ID:
                    eat(ReservedWord.INT);
                break;
                
            case ReservedWord.STRING_ID:
                    eat(ReservedWord.STRING);
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "type\n" + PrintColor.RESET);
                error();
                
        }
    }
    
    private void eat(Token t){
        if (currentToken.equals(t)) {
            currentToken = lexer.getToken();
        }else {
            error();
        }
    }
    
    private void skipTo(Token[] followSet){
        
        boolean followElementFound = false;
        while(!followElementFound){
            
            for(Token followElement : followSet){
                if(followElement.equals(currentToken)){
                    followElementFound = true;
                    break;
                }
            }
            
            currentToken = lexer.getToken();
        }
    }
    
    private void error(){
        errorMessages.append(PrintColor.RED + "Unexpected token " + currentToken + " on line " + lexer.getCurrentLine() + PrintColor.RESET + "\n");
    }
    
    public static void main(String[] args) {
        
        if(args.length == 0){
            System.out.println("No file path informed. Please inform file paths.");
            System.out.println("Usage: java -jar KPiler.jar file1.txt file2.txt");
        }
        
        List<String> paths = new ArrayList<>();
        List<String> tokenFlow = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<Parser> compileJobs = new ArrayList<>();
        
        paths.addAll(Arrays.asList(args));
        
        
        while(paths.size() > 0 || compileJobs.size() > 0){
            
            if(compileJobs.size() < MAX_JOBS && paths.size() > 0){
                compileJobs.add(new Parser(paths.remove(0)));
                compileJobs.get(compileJobs.size() -1).start();
            }
            
            for(int i=0 ; i<compileJobs.size(); i++){
                if(!compileJobs.get(i).isAlive()){
                    tokenFlow.add(compileJobs.get(i).getTokenFlow());
                    errorMessages.add(compileJobs.get(i).getErrorMessages());
                    compileJobs.remove(i);
                }
            }
        }
        //PRINTING OUTPUT
        for(int i = 0; i<tokenFlow.size(); i++){
            System.out.println(errorMessages.get(i));
            //System.out.println(tokenFlow.get(i));
        }
       
    }

}
