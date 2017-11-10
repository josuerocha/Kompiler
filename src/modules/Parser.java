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
            case Token.IDENTIFIER_ID:
                
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "declList\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "declListPrime \n" + PrintColor.RESET);
                error();
                synchTo(declistPrimeFollow);
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
                synchTo(declFollow);
        }
    }
    
    private void identifierList(){
        
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                eat(Identifier.IDENTIFIER); possibleIdentifier();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "identifierList\n" + PrintColor.RESET);
                error();
                synchTo(identifierListFollow);
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
                errorMessages.append(PrintColor.BLUE + "stmtListPrime \n" + PrintColor.RESET);
                error();
                synchTo(stmtListPrimeFollow);
        }
    }
    

    
    public void assignStatement(){
        
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                eat(Identifier.IDENTIFIER); eat(new Operator('=')); simpleExpression();
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "assignStatement" + PrintColor.RESET);
                error();
                synchTo(assignStatementFollow);
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
                errorMessages.append(PrintColor.BLUE + "ifStatementPrime\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "whileStatement\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "stmtSufix\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "readStatement\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "writeStatement\n" + PrintColor.RESET);
                error();
                synchTo(writeStatementFollow);
        }
    }
    
    private void expression(){
        
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                simpleExpression(); expressionPrime();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "expression\n" + PrintColor.RESET);
                error();
                synchTo(expressionFollow);
        }
    }
    
    private void simpleExpression(){
        
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                term(); simpleExpressionPrime();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "simpleExpression\n" + PrintColor.RESET);
                error();
                synchTo(simpleExpressionFollow);
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
                errorMessages.append(PrintColor.BLUE + "simpleExprPrime\n" + PrintColor.RESET);
                error();
                synchTo(simpleExpressionPrimeFollow);
                
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
                synchTo(addopFollow);
        }
    }
    
    private void term(){
        
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                factora(); termPrime();
            break;
            
            default:
                errorMessages.append(PrintColor.BLUE + "term\n" + PrintColor.RESET);
                error();
                synchTo(termFollow);
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
                synchTo(termPrimeFollow);
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
                synchTo(mulopFollow);
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
                synchTo(expressionPrimeFollow);
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
                
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                factor();
                break;
                
            default:
                errorMessages.append(PrintColor.BLUE + "factora\n" + PrintColor.RESET);
                error();
                synchTo(factoraFollow);
        }
    }
    
    private void factor(){
        
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                eat(Identifier.IDENTIFIER);
                break;
                
            case Token.INT_CONSTANT_ID:
            case Token.LIT_CONSTANT_ID:
                constant();
                break;
                
            case '(':
                eat(Token.OPEN_PAREN); expression(); eat(Token.CLOSE_PAREN);
                break; 
                
            default:
                errorMessages.append(PrintColor.BLUE + "factor\n" + PrintColor.RESET);
                error();
                synchTo(factorFollow);
        }
    }
    
    private void constant(){
        switch(currentToken.getTag()){
            case Token.INT_CONSTANT_ID:
                eat(new IntConstant(1));
                break;
            case Token.LIT_CONSTANT_ID:
                eat(new LiteralConstant("a"));
                break;
            default:
                errorMessages.append(PrintColor.BLUE + "constant\n" + PrintColor.RESET);
                error();
                synchTo(constantFollow);
        }
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
                errorMessages.append(PrintColor.BLUE + "writable\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "relop\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "condition\n" + PrintColor.RESET);
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
                errorMessages.append(PrintColor.BLUE + "stmt\n" + PrintColor.RESET);
                error();
                synchTo(stmtFollow);
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
                synchTo(typeFollow);
        }
    }
    
    private void eat(Token t){
        if (currentToken.equals(t)) {
            currentToken = lexer.getToken();
        }else {
            error(t);
        }
    }
    
    private void synchTo(Token[] followSet){
       
        boolean followElementFound = false;
        
        do{
            
            for(Token followElement : followSet){
                if(followElement.equals(currentToken)){
                    followElementFound = true;
                    break;
                }
            }
            
            
        }while(!((currentToken = lexer.getToken()) == Token.EOF) && !followElementFound);
    }
    
    private void error(){
        errorMessages.append(PrintColor.RED + "Unexpected token ").append(currentToken).append(" on line ").append(lexer.getCurrentLine()).append(PrintColor.RESET + "\n");
    }
    
    private void error(Token expected){
        errorMessages.append(PrintColor.RED + "Unexpected token ").append(currentToken).append(" on line ").append(lexer.getCurrentLine()).append(PrintColor.RESET + "Expected " + expected + "\n");
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
            
            if(compileJobs.size() < MAX_JOBS && paths.size() > 0){
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
            System.out.println(errorMessages.get(i));
            
        }
       
    }

}
