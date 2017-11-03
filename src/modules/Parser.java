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
                errorMessages.append(PrintColor.RED +"PROGRAM initializer expected. \n" + PrintColor.RESET);
                break;
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
                
                break;
                
            default:
                errorMessages.append(PrintColor.RED + "Expected IDENTIFIER line " + currentToken + lexer.getCurrentLine() + "\n" + PrintColor.RESET);
        }
    }
    
        private void declListPrime(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                decl(); declList();
            default:
                errorMessages.append(PrintColor.RED + "ERROR: expected ID declListPrime \n" + PrintColor.RESET);
        }
    }
    
    private void decl(){
        switch(currentToken.getTag()){
            case ReservedWord.INT_ID:
            case ReservedWord.STRING_ID:
                type(); identifierList(); eat(Token.SEMI_COLON);
                break;
                
            default:
                errorMessages.append(PrintColor.RED + "ERROR: expected ID decl\n" + PrintColor.RESET);
        }
    }
    
    private void identifierList(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(new Identifier()); possibleIdentifier();
                break;
        }
    }
    
    private void possibleIdentifier(){
        switch(currentToken.getTag()){
            case ',':
                eat(Token.COMMA); eat(new Identifier()); possibleIdentifier();
                break;
            case Token.IDENTIFIER:
            case ReservedWord.DO_ID:
            case ReservedWord.PRINT_ID:
            case ReservedWord.IF_ID:
            case ReservedWord.SCAN_ID:
                
                break;
            default:
                errorMessages.append(PrintColor.RED + "ERROR: COMMA EXPECTED\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "Syntax error on stmtList \n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: Expected statement \n" + PrintColor.RESET);
        }
    }
    

    
    public void assignStatement(){
        switch(currentToken.getTag()){
            case Token.IDENTIFIER:
                eat(new Identifier()); eat(new Operator('=')); simpleExpression();
                break;
            default:
                errorMessages.append(PrintColor.RED + "ERROR: Expected IDENTIFIER\n" + PrintColor.RESET);
        }
    }
    
    private void ifStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.IF_ID:
                eat(ReservedWord.IF); condition(); eat(ReservedWord.THEN); stmtListPrime(); ifStatementPrime();
                break;
            default:
                errorMessages.append(PrintColor.RED + "ERROR: expected if\n" + PrintColor.RESET);
        }
    }
    
    private void ifStatementPrime(){
        switch(currentToken.getTag()){
            case ReservedWord.END_ID:
                eat(ReservedWord.END);
            case ReservedWord.ELSE_ID:
                eat(ReservedWord.ELSE); stmtList(); eat(ReservedWord.END);
            default:
                errorMessages.append(PrintColor.RED + "ERROR: Expected END or ELSE\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: operator expected\n" + PrintColor.RESET);
        }
    }
    
    private void whileStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.DO_ID:
                eat(ReservedWord.DO); stmtListPrime(); stmtSufix();
                
            default:
                errorMessages.append(PrintColor.RED + "ERROR: expected do" + PrintColor.RESET);
        }
    }
    
    private void stmtSufix(){
        switch(currentToken.getTag()){
            case ReservedWord.WHILE_ID:
                eat(ReservedWord.WHILE); condition(); eat(ReservedWord.END);
                break;
            default:
                errorMessages.append(PrintColor.RED + "ERROR: WHILE keyword expected\n" + PrintColor.RESET);
        }
    }
    
    public void readStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.SCAN_ID:
                eat(ReservedWord.SCAN); eat(Token.OPEN_PAREN); 
                eat(new Identifier()); eat(Token.CLOSE_PAREN);
                break;
            
            default:
                errorMessages.append(PrintColor.RED + "ERROR: expected SCAN operator\n" + PrintColor.RESET);
        }
        
    }
    
    public void writeStatement(){
        switch(currentToken.getTag()){
            case ReservedWord.PRINT_ID:
                eat(ReservedWord.PRINT); eat(Token.OPEN_PAREN); writable(); eat(Token.CLOSE_PAREN);
                break;
                
            default:
                errorMessages.append(PrintColor.RED + "ERROR: expected PRINT keyword\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: Expected expression\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: Expected expression\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR in simpleExprPrime\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: Add operator expected\n" + PrintColor.RESET);
            
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
                errorMessages.append(PrintColor.RED + "ERROR: Expected expression\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: expected operator\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: Operator expected\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: factora\n" + PrintColor.RESET);
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
                break; 
                
            default:
                errorMessages.append(PrintColor.RED + "ERROR: factor\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: constant expected\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: expected literal or expression\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: operator expected\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "Error in condition production\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.RED + "ERROR: expected statement\n" + PrintColor.RESET);
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
                    errorMessages.append(PrintColor.RED + "ERROR: type expected (INT/STRING)\n" + PrintColor.RESET);
                break;
                
        }
    }
    
    private void eat(Token t){
        if (currentToken.equals(t)) {
            currentToken = lexer.getToken();
        }else {
            //System.out.println("ERROR: expected " + t + " on line " + lexer.getCurrentLine() + "\n");
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
