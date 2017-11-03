package modules;

import dataunits.CompileError;
import dataunits.Identifier;
import dataunits.IntConstant;
import dataunits.LiteralConstant;
import dataunits.Operator;
import dataunits.ReservedWord;
import dataunits.Token;
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
    
    public Parser(String path){
        this.filepath = path;
        lexer = new Lexer(this.filepath);
        tokenFlow = new StringBuffer();
        errorMessages =  new StringBuffer();
    }
    
    public void run(){
        

        Token token;
        while ((token = lexer.getToken()) != null) {
            if(token instanceof CompileError){
                errorMessages.append(token).append(" \n");
            }else{
                tokenFlow.append(token).append(" \n");
            }  
        }
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
                System.out.println("PROGRAM initializer expected.");
                break;
        }
    }
    
    private void declList(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                declListPrime();
                break;
            case ReservedWord.IF_ID:
            case ReservedWord.DO_ID:
            case ReservedWord.SCAN_ID:
            case ReservedWord.PRINT_ID:
                
                break;
                
            default:
                System.out.println("Expected IDENTIFIER");
        }
    }
    
    private void stmtList(){
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
                System.out.println("Syntax error on stmtList");
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
                System.out.println("Expected statement");
        }
    }
    
    private void declListPrime(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                decl();
            default:
                System.out.println("ERROR: expected ID");
        }
    }
    
    private void decl(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(new Identifier()); possibleIdentifier();
            default:
                System.out.println("ERROR: expected ID");
        }
    }
    
    private void possibleIdentifier(){
        switch(currentToken.getTag()){
            case ',':
                eat(Token.COMMA); decl();
                break;
            case Token.IDENTIFIER:
            case ReservedWord.DO_ID:
            case ReservedWord.PRINT_ID:
            case ReservedWord.IF_ID:
            case ReservedWord.SCAN_ID:
                
                break;
            default:
                System.out.println("ERROR: COMMA EXPECTED");
        }
    }
    
    public void assignStatement(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(new Identifier()); eat(new Operator('=')); simpleExpression();
                break;
            default:
                System.out.println("ERROR: Expected IDENTIFIER");
        }
    }
    
    private void ifStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.IF_ID:
                eat(ReservedWord.IF); condition(); eat(ReservedWord.THEN); stmtListPrime(); ifStatementPrime();
                break;
            default:
                System.out.println("ERROR: expected if");
        }
    }
    
    private void ifStatementPrime(){
        switch(currentToken.getTag()){
            case ReservedWord.END_ID:
                eat(ReservedWord.END);
            case ReservedWord.ELSE_ID:
                eat(ReservedWord.ELSE); stmtList(); eat(ReservedWord.END);
            default:
                System.out.println("ERROR: Expected END or ELSE");
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
                System.out.println("ERROR: operator expected");
        }
    }
    
    private void whileStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.DO_ID:
                eat(ReservedWord.DO); stmtListPrime(); stmtSufix();
                
            default:
                System.out.println("ERROR: expected do");
        }
    }
    
    private void stmtSufix(){
        switch(currentToken.getTag()){
            case ReservedWord.WHILE_ID:
                eat(ReservedWord.WHILE); condition(); eat(ReservedWord.END);
                break;
            default:
                System.out.println("ERROR: WHILE keyword expected");
        }
    }
    
    public void readStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.SCAN_ID:
                eat(ReservedWord.SCAN); eat(Token.OPEN_PAREN); 
                eat(new Identifier()); eat(Token.CLOSE_PAREN);
                break;
            
            default:
                System.out.println("ERROR: expected SCAN operator");
        }
        
    }
    
    public void writeStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.PRINT_ID:
                eat(ReservedWord.PRINT); eat(Token.OPEN_PAREN); writable(); eat(Token.CLOSE_PAREN);
                break;
                
            default:
                System.out.println("ERROR: expected PRINT keyword");
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
                System.out.println("ERROR: Expected expression");
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
                System.out.println("ERROR: Expected expression");
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
                System.out.println("ERROR: Add operator expected");
            
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
                System.out.println("ERROR: Expected expression");
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
                System.out.println("ERROR: expected operator");
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
                
            case Operator.OR_ID:
                eat(Operator.OR);
                break;
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
                System.out.println("ERROR: Operator expected");
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
        }
    }
    
    private void factor(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(new Identifier());
                break;
                
            case Token.INT_CONSTANT:
            case Token.LITERAL_CONSTANT:
                constant();
                break;
                
            case '(':
                eat(Token.OPEN_PAREN); expression(); eat(Token.CLOSE_PAREN);
                
                
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
                System.out.println("ERROR: constant expected");
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
                System.out.println("ERROR: expected literal or expression");
        }

    }
    
    private void declaration(){
        
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
                System.out.println("ERROR: operator expected");
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
                System.out.println("Error in condition production");
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
                System.out.println("ERROR: expected statement");
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
                    System.out.println("ERROR: type expected (INT/STRING)");
                break;
                
        }
    }
    
    private void eat(Token t){
        if (currentToken == t) {
            currentToken = lexer.getToken();
        }else {
            System.out.println("ERROR: expected " + t + " on line " + lexer.getCurrentLine() + "\n");
        }
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
