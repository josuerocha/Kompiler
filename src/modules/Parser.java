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
import dataunits.Attribute;
import dataunits.Instruction;
import util.PrintColor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.ListUtil;

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
    private CodeGenerator codeGenerator;
    private int offset = 0;
    Lexer lexer;
    
    /*
	* The Parser class is the entry point for the program. It receives the path and calls the Lexer and CodeGenerator in a single step.
	*/
    public Parser(String path){
        this.filepath = path;
        lexer = new Lexer(this.filepath);
        symbolTable = lexer.getSymbolTableInstance();
        tokenFlow = new StringBuffer();
        errorMessages =  new StringBuffer();
        codeGenerator = new CodeGenerator(extractFilename());
        errorMessages.append("ARQUIVO: ").append(this.filepath).append("\n");
    }
    
	
	/*
	* The parser was organized in a multi-thread architecture in order to speed up compilation
	*/
    @Override
    public void run(){
        currentToken = lexer.getToken();
        program();
        if(success){
            errorMessages.append(PrintColor.BLUE + "Compiled successfully :) \n" + PrintColor.RESET);
            codeGenerator.writeSourceFile();
        }
    }
    
    public String getErrorMessages(){
        return errorMessages.toString() +
        " _________________________________\n";
    }
    
	/*
	* This function uses regex to extract the file name from the informed path.
	*/
    private String extractFilename(){
        String filename = "";
        Pattern p = Pattern.compile("/(.*).k");   // the pattern to search for
        Matcher m = p.matcher(this.filepath);
        if(m.find()){
            filename = m.group(1);
        }
        
        return filename;
    }
    
	/*
	* The compiler was implemented using a recursive descent parser architecture. For each grammar production a recursive method was implemented.
	*/
    private void program(){
        codeGenerator.gen(new Instruction("START"));
        switch(currentToken.getTag()){
            case ReservedWord.PROGRAM_ID:
                eat(ReservedWord.PROGRAM); declList(); stmtList(); eat(ReservedWord.END);
                break;
            default:
                error();
        }
        codeGenerator.gen(new Instruction("STOP"));
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
                type = type().getType(); identifierList(type); eat(Token.SEMI_COLON);
                break;
                
            default:
                error();
                synchTo(declFollow);
        }
    }
    
    private void identifierList(Type type){
        
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                Token id;
                int initOffset = offset;
                id = eat(Identifier.IDENTIFIER); 
                symbolTable.get(id.getLexeme()).setRelativeAdress(offset++); //DEFINES RELATIVE ADDRESS AND INCREMENTS IT
                possibleIdentifier(type);
                
                //SEMANTICS: identifier unity check
                checkIdentifierUnicity(id, type);
                
                //SEMANTIC ACTIONS - CODE GENERATION
                codeGenerator.gen(new Instruction("PUSHN " + (offset-initOffset))); //SEMANTIC ACTION FOR RESERVING VARIABLE SPACE
                //END - SEMANTIC ACTIONS
                
                break;
                
            default:
                error();
                synchTo(identifierListFollow);
        }
    }
    
    private void possibleIdentifier(Type type){
        
        switch(currentToken.getTag()){
            case ',':
                Token id;
                eat(Token.COMMA); 
                id = eat(Identifier.IDENTIFIER); 
                //SEMANTIC ACTIONS
                
                symbolTable.get(id.getLexeme()).setRelativeAdress(offset++); //DEFINES RELATIVE ADDRESS AND INCREMENTS IT
                checkIdentifierUnicity(id, type);
                //END - SEMANTIC ACTIONS
                
                possibleIdentifier(type);
               
                
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
                error();
                synchTo(stmtListFollow);
        } 
     
    }
    
    private void stmtListPrime(){
        Attribute a = new Attribute();
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
                Attribute att;
                Type typeId, typeExpression;
                Token id = eat(Identifier.IDENTIFIER); eat(Operator.ASSIGN); att = simpleExpression();
                typeExpression = att.getType();
           
                //SEMANTIC ACTIONS
                //Checking whether id has been declared and getting its type
                if(id instanceof Identifier){ 
                    typeId = symbolTable.get(id.getLexeme()).getType();
                    int line = lexer.getCurrentLine();
                
                        if(typeId == null){
                            printUndeclaredId(id,line);
                        }else if( !typeId.equals(typeExpression) && !typeId.equals(Type.ERROR) && !typeExpression.equals(Type.ERROR) && !typeExpression.equals(Type.VOID)){
                            semanticError("type mismatch on assignment, expected " + typeId + " received " + typeExpression);
                        }
                }
                
                //code generation
                codeGenerator.gen(new Instruction("STOREL " + symbolTable.get(id.getLexeme()).getRelativeAdress()));
                
                //END SEMANTIC ACTIONS
                
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
    
    private Attribute ifStatement(){
        Attribute a = new Attribute();
        switch(currentToken.getTag()){
            case ReservedWord.IF_ID:
                Type type;
                int line = lexer.getCurrentLine();
                eat(ReservedWord.IF); a = condition(); eat(ReservedWord.THEN); 
                int mInst = codeGenerator.getNextInstr();
                stmtListPrime();
                
                int elseAddress = codeGenerator.getNextInstr();
                codeGenerator.backpatch(a.truelist, mInst);
                
                boolean hasElse = ifStatementPrime();
                
                if(hasElse){
                    codeGenerator.backpatch(a.falselist,elseAddress+1);
                }else{
                    codeGenerator.backpatch(a.falselist,elseAddress);
                }
                
                //SEMANTIC ACTIONS
                type = a.getType();
                if(!type.equals(Type.LOGICAL) && !type.equals(Type.ERROR) && !type.equals(Type.VOID)){
                    semanticError("type mismatch in if condition. Expected relational operation received: " + type,line);
                }
                
                //END SEMANTIC ACTIONS
                
                break;
            default:
                error();
                synchTo(ifStatementFollow);
        }
        return a;
    }
    
    private boolean ifStatementPrime(){
        boolean hasElse = false;
        switch(currentToken.getTag()){
            case ReservedWord.END_ID:
                eat(ReservedWord.END);
                break;
                
            case ReservedWord.ELSE_ID:
                eat(ReservedWord.ELSE); 
                hasElse = true;
                //SEMANTIC ACTIONS
                codeGenerator.gen(new Instruction("JUMP _"));
                int elseAddress = codeGenerator.getInst();
                //END SEMANTIC ACTIONS
                stmtList(); 
                eat(ReservedWord.END);
                
                int next = codeGenerator.getNextInstr();
                codeGenerator.backpatch(elseAddress, next);
                
                break;
                
            default:
                error();
                synchTo(ifStatementPrimeFollow);
        }
        return hasElse;
    }
    
    private void whileStatement(){
        
        switch(currentToken.getTag()){
            case ReservedWord.DO_ID:
                eat(ReservedWord.DO); 
                int initInstr = codeGenerator.getNextInstr(); 
                stmtListPrime(); stmtSufix(initInstr);
                break;
                
            default:
                error();
                synchTo(whileStatementFollow);
        }
    }
    
    private void stmtSufix(int address){
        Attribute a;
        switch(currentToken.getTag()){
            case ReservedWord.WHILE_ID:
                eat(ReservedWord.WHILE); a = condition(); eat(ReservedWord.END);
                Type type = a.getType();
                if( !type.equals(Type.LOGICAL) && !type.equals(Type.VOID) && !type.equals(Type.ERROR)){
                    semanticError("Invalid operand in while statement condition. Received " + type);
                }
                
                codeGenerator.backpatch(a.truelist, address);
                codeGenerator.backpatch(a.falselist, codeGenerator.getNextInstr());
                
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
                int line = lexer.getCurrentLine();
                Token id = eat(Identifier.IDENTIFIER); eat(Token.CLOSE_PAREN);
                
                //SEMANTIC ACTIONS
                Type idType;
                if(id instanceof Identifier){
                    SymbolTableEntry entry = symbolTable.get(id.getLexeme());
                    idType = entry.getType();
                    
                    codeGenerator.gen(new Instruction("READ"));
                    if(idType.equals(Type.INT)){ codeGenerator.gen(new Instruction("ATOI")); }
                    codeGenerator.gen(new Instruction("STOREL " + entry.getRelativeAdress()));
                    
                    if(!symbolTable.get(id.getLexeme()).isInstalled()){
                        printUndeclaredId(id,line); 
                    }
                }
                
                //END SEMANTIC ACTIONS
                
                
                break;
            
            default:
                error();
                synchTo(readStatementFollow);
                
        }
        
    }
    
    public void writeStatement(){
        
        switch(currentToken.getTag()){
            case ReservedWord.PRINT_ID:
                Attribute a;
                eat(ReservedWord.PRINT); eat(Token.OPEN_PAREN); a = writable(); eat(Token.CLOSE_PAREN);
                
                //SEMANTIC ACTIONS
                
                if(a.getType().equals(Type.INT)){
                        codeGenerator.gen(new Instruction("WRITEI"));
                }else if(a.getType().equals(Type.STRING)){
                        codeGenerator.gen(new Instruction("WRITES"));
                }
                //END SEMANTIC ACTIONS
                
                break;
                
            default:
                error();
                synchTo(writeStatementFollow);
        }
        
         if(recoveringFromError){
            synchTo(writeStatementFollow);
        }
    }
    
    private Attribute expression(){
        Attribute a = new Attribute();
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                Attribute a1;
                a1 = simpleExpression(); a = expressionPrime(a1);
                if(a.getType().equals(Type.VOID)){
                    a = a1;
                }
                
                break;
                
            default:
                error();
                synchTo(expressionFollow);
        }
        
        return a;
    }
    
    private Attribute expressionPrime(Attribute a1){
        Attribute a = new Attribute(Type.VOID);
        boolean differentEqualIndicator = false;
        switch(currentToken.getTag()){
            case Operator.EQUAL_ID:
            case Operator.DIFFERENT_ID:
                differentEqualIndicator = true;
            case '>':
            case Operator.GREATER_EQUAL_ID:
            case '<':
            case Operator.LESS_EQUAL_ID:
                Type type2; Attribute a2;
                
                a = relop(); int initIndex = codeGenerator.getNextInstr(); a2 = simpleExpression();
                type2 = a2.getType();
                //SEMANTIC ACTIONS
                Type type1 = a1.getType();
                if(type1.equals(Type.STRING) && type2.equals(Type.STRING) && differentEqualIndicator){
                    a.setType(Type.LOGICAL);
                }else if(type1.equals(Type.INT) && type2.equals(Type.INT)){
                    a.setType(Type.LOGICAL);
                }else if(type1.equals(Type.STRING) && type2.equals(Type.STRING) && !differentEqualIndicator){
                    a.setType(Type.ERROR);
                    semanticError("operators >,>=,<,<= not suported for expression type " + type1 + " and " + type2);
                }else if(!type1.equals(Type.ERROR) && !type2.equals(Type.ERROR)){
                    a.setType(Type.ERROR);
                    semanticError("incompatible operands in relational expression, type " + type1 + " and " + type2);
                }
                
                a.incrementLists(codeGenerator.getNextInstr() - initIndex);
                codeGenerator.appendBuffer();
                //END SEMANTIC ACTIONS
                break;
                
            case ')':
            case ReservedWord.END_ID:
            case ReservedWord.THEN_ID:
                
                break;
            default:
                error();
                synchTo(expressionPrimeFollow);
        }
        return a;
    }
    
    private Attribute simpleExpression(){
        Attribute a = new Attribute(Type.VOID);
        Type type = Type.VOID;
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                Attribute a1, a2;
                Type type1,type2;
                
                a1 = term(); type1 = a1.getType(); a2 = simpleExpressionPrime(a1);
                type2 = a2.getType();
                a = a1;
                //SEMANTIC ACTIONS
                codeGenerator.appendStringBufferReverse();
                if(type1.equals(type2) ){
                    type = type1;
                }else if(type2.equals(Type.VOID)){
                    type = type1;
                }else if(!type1.equals(Type.ERROR) && !type2.equals(Type.ERROR)){
                    semanticError("type mismatch in expression operands. Received types " + type1 + " and " + type2);
                    type = Type.ERROR;
                }
                
                a.setType(type);
                
                //END SEMANTIC ACTIONS
                
                break;
                
            default:
                error();
                synchTo(simpleExpressionFollow);
        }
        return a;
    }
    
    private Attribute simpleExpressionPrime(Attribute a1){
        Attribute a = new Attribute(Type.VOID);
        boolean sumIndicator = false;
        boolean orIndicator = false;
        switch(currentToken.getTag()){
            case '+':
                sumIndicator = true;
            case Operator.OR_ID:
                orIndicator = (sumIndicator != true);
            case '-':
                Attribute a2;
                Type type1, type2, output;
                type1 = a1.getType();
                addop(type1);  int mInst = codeGenerator.getInst(); a2 = term();  
                
                
                type2 = a2.getType();
                
                //SEMANTIC ACTIONS
                if(type1.equals(Type.STRING) && type2.equals(Type.STRING) && sumIndicator){
                    a.setType(Type.STRING);
                }else if(type1.equals(Type.LOGICAL) && type2.equals(Type.LOGICAL) && orIndicator){
                    a.setType(Type.LOGICAL);
                }else if(type1.equals(Type.INT) && type2.equals(Type.INT)){
                    a.setType(Type.INT);
                }else if(!type1.equals(Type.ERROR) && !type2.equals(Type.ERROR)){ //Avoiding to show an error message that has already been shown
                    semanticError("type mismatch on expression types " + type1 + " " + type2);
                   a.setType(Type.ERROR);
                }
                //END SEMANTIC ACTIONS
                
                output = simpleExpressionPrime(a).getType();
                codeGenerator.appendStringBufferReverse();
                codeGenerator.appendBufferReverse();
                if(orIndicator){
                    codeGenerator.backpatch(a1.falselist,mInst);
                    
                    a.falselist = a2.falselist;
                }
                
                
                if(output.equals(Type.ERROR)){
                    a.setType(Type.ERROR);
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
        
        return a;
    }
    
    private void addop(Type type){
        
        switch(currentToken.getTag()){
            case '+':
                eat(Operator.PLUS);
                
                if(type.equals(type.INT)){
                    codeGenerator.genBuffer(new Instruction("ADD"));
                }else if(type.equals(type.STRING)){
                    codeGenerator.genBuffer(new Instruction("CONCAT"));
                }
                
                break;
            case '-':
                eat(Operator.MINUS);
                codeGenerator.genBuffer(new Instruction("SUB"));
                break;
            case Operator.OR_ID:
                eat(Operator.OR);
                break;
                
            default:
                error();
                synchTo(addopFollow);
        }
    }
    
    private Attribute term(){
        Attribute a = new Attribute();
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                Type type1, type2;
                Attribute a1 = factora();  type1 = a1.getType(); a = termPrime(a1);
                type2 = a.getType();
                if(type2.equals(Type.VOID)){
                    a = a1;
                }
                if(type1.equals(type2) || type2.equals(Type.VOID)){
                    a.setType(type1);;
                }else{
                    a.setType(Type.ERROR);

                }

            break;
            
            default:
                error();
                synchTo(termFollow);
        }
        return a;
    }
    
    private Attribute termPrime(Attribute a1){
        Attribute a = new Attribute(Type.VOID);
        boolean andIndicator = false;
        boolean mulIndicator = false;
        boolean divIndicator;
        switch(currentToken.getTag()){
            case Operator.AND_ID:
                andIndicator = true;
            case '*':
                mulIndicator = !andIndicator;
            case '/':
                divIndicator = !andIndicator && !mulIndicator;
                
                Type type1 = a1.getType(), type2;
                mulop(); int mInst = codeGenerator.getNextInstr(); Attribute a2 = factora(); 
                type2 = a2.getType(); termPrime(a2);
                
                
                if(type1.equals(Type.INT) && (type2.equals(Type.INT)) && (mulIndicator || divIndicator )){
                    a.setType(Type.INT);
                }else if(type1.equals(Type.INT) && (type2.equals(Type.VOID)) && (mulIndicator || divIndicator )){
                    codeGenerator.appendBuffer();
                }else if(type1.equals(Type.LOGICAL) && (type2.equals(Type.LOGICAL) || type2.equals(Type.VOID)) && andIndicator){
                    a.setType(Type.LOGICAL);
                }else if(type1.equals(Type.VOID) && type2.equals(Type.INT)){
                    a.setType(Type.INT);
                    
                }else if(type1.equals(Type.VOID) && (type2.equals(Type.VOID))){
                    
                }else if(!type1.equals(Type.ERROR) && !type2.equals(Type.ERROR)){ //Avoiding to show an error message that has already been shown
                    a.setType(Type.ERROR);
                    semanticError("type mismatch on expression types " + type1 + " " + type2);
                }
                
                if(andIndicator){
                    codeGenerator.backpatch(a1.truelist, mInst);
                    a.truelist = a2.truelist;
                    a.falselist = ListUtil.merge(a1.falselist, a2.falselist);
                    
                }
                
                codeGenerator.appendBufferReverse();
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
        
        return a;
    }
    
    private void mulop(){
        
        switch(currentToken.getTag()){
            case '*':
                eat(Operator.MUL);
                codeGenerator.genBuffer(new Instruction("MUL"));
                break;
                
            case '/':
                eat(Operator.DIV);
                codeGenerator.genBuffer(new Instruction("DIV"));
                break;
                
            case Operator.AND_ID:
                eat(Operator.AND);
                break;
                
            default:
                error();
                synchTo(mulopFollow);
        }
    }
    
    private Attribute factora(){
        Attribute a = new Attribute();
        switch(currentToken.getTag()){
            case '!':
                eat(Operator.NEG); a = factor();
                break;
            case '-':
                eat(Operator.MINUS); a = factor();
                break;
                
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                a = factor();
                break;
                
            default:
                error();
                synchTo(factoraFollow);
        }
        
        return a;
    }
    
    private Attribute factor(){
        Attribute a = new Attribute();
        switch(currentToken.getTag()){
            case Token.IDENTIFIER_ID:
                //Storing type for verification
                Token id = eat(Identifier.IDENTIFIER);
                
                if(id instanceof Identifier){
                    a.setType( symbolTable.get(id.getLexeme()).getType());
                    int address = symbolTable.get(id.getLexeme()).getRelativeAdress();
                    if(null == a.getType()){
                        printUndeclaredId(id,lexer.getCurrentLine());
                        a.setType(Type.ERROR);
                    } //Checking whether id has been declared
                    
                    if(a.getType().equals(Type.INT)){
                        codeGenerator.gen(new Instruction("PUSHL " + address));
                    }else{
                        codeGenerator.genStringBuffer(new Instruction("PUSHL " + address));
                    }
                }
                
                break;
            
            case Token.INT_CONSTANT_ID:
            case Token.LIT_CONSTANT_ID:
                a = constant();
                break;
                
            case '(':
                
                eat(Token.OPEN_PAREN); a = expression(); eat(Token.CLOSE_PAREN);
                break; 
                
            default:
                error();
                synchTo(factorFollow);
        }
        
        return a;
    }
    
    private Attribute constant(){
        Type type = Type.VOID;
        Token t;
        switch(currentToken.getTag()){
            case Token.INT_CONSTANT_ID:
                t = eat(new IntConstant(1));
                type = Type.INT;
                codeGenerator.gen(new Instruction("PUSHI " + t.getLexeme()));
                break;
            case Token.LIT_CONSTANT_ID:
                t = eat(new LiteralConstant(""));
                codeGenerator.genStringBuffer(new Instruction("PUSHS \"" + t.getLexeme() + "\""));
                type = Type.STRING;
                break;
            default:
                error();
                synchTo(constantFollow);
        }
        
        return new Attribute(type);
    }
    
    private Attribute writable(){
        Attribute a = new Attribute(Type.VOID);
        switch(currentToken.getTag()){
            case Token.LIT_CONSTANT_ID:
                Token t = eat(new LiteralConstant(" "));
                a = new Attribute(Type.STRING,true);
                
                codeGenerator.gen(new Instruction("PUSHS \"" + t.getLexeme() + "\""));
                
                break;
                
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case Token.INT_CONSTANT_ID:
                a = simpleExpression();
                break;
                
            default:
                error();
                synchTo(writableFollow);
        }
        
        return a;
    }
    
    private Attribute relop(){
        Attribute a = new Attribute(Type.VOID);
        switch(currentToken.getTag()){
            case Operator.EQUAL_ID:
                eat(Operator.EQUAL);
                a.truelist = ListUtil.makeList(codeGenerator.getNextInstr()+2);
                a.falselist = ListUtil.makeList(codeGenerator.getNextInstr()+3);
                codeGenerator.genBuffer(new Instruction("EQUAL"));
                codeGenerator.genBuffer(new Instruction("NOT"));
                codeGenerator.genBuffer(new Instruction("JZ _"));
                codeGenerator.genBuffer(new Instruction("JUMP _"));
                break;
                
            case '>':
                eat(Operator.GT);
                a.truelist = ListUtil.makeList(codeGenerator.getNextInstr()+2);
                a.falselist = ListUtil.makeList(codeGenerator.getNextInstr()+3);
                codeGenerator.genBuffer(new Instruction("SUP"));
                codeGenerator.genBuffer(new Instruction("NOT"));
                codeGenerator.genBuffer(new Instruction("JZ _"));
                codeGenerator.genBuffer(new Instruction("JUMP _"));
                break;
                
            case Operator.GREATER_EQUAL_ID:
                eat(Operator.GE);
                a.truelist = ListUtil.makeList(codeGenerator.getNextInstr()+2);
                a.falselist = ListUtil.makeList(codeGenerator.getNextInstr()+3);
                codeGenerator.genBuffer(new Instruction("SUPEQ"));
                codeGenerator.genBuffer(new Instruction("NOT"));
                codeGenerator.genBuffer(new Instruction("JZ _"));
                codeGenerator.genBuffer(new Instruction("JUMP _"));
                break;
                
            case '<':
                eat(Operator.LT);
                a.truelist = ListUtil.makeList(codeGenerator.getNextInstr()+2);
                a.falselist = ListUtil.makeList(codeGenerator.getNextInstr()+3);
                codeGenerator.genBuffer(new Instruction("INF"));
                codeGenerator.genBuffer(new Instruction("NOT"));
                codeGenerator.genBuffer(new Instruction("JZ _"));
                codeGenerator.genBuffer(new Instruction("JUMP _"));
                break;
                
            case Operator.LESS_EQUAL_ID:
                eat(Operator.LE);
                a.truelist = ListUtil.makeList(codeGenerator.getNextInstr()+2);
                a.falselist = ListUtil.makeList(codeGenerator.getNextInstr()+3);
                codeGenerator.genBuffer(new Instruction("INFEQ"));
                codeGenerator.genBuffer(new Instruction("NOT"));
                codeGenerator.genBuffer(new Instruction("JZ _"));
                codeGenerator.genBuffer(new Instruction("JUMP _"));
                break;
                
            case Operator.DIFFERENT_ID:
                eat(Operator.DIFFERENT);
                a.truelist = ListUtil.makeList(codeGenerator.getNextInstr()+1);
                a.falselist = ListUtil.makeList(codeGenerator.getNextInstr()+2);
                codeGenerator.genBuffer(new Instruction("EQUAL"));
                codeGenerator.genBuffer(new Instruction("JZ _"));
                codeGenerator.genBuffer(new Instruction("JUMP _"));
                break;
                
            default:
                error();
                synchTo(relopFollow);
            }
        
        return a;
    }
    
    private Attribute condition(){
        Attribute a = new Attribute();
        switch(currentToken.getTag()){
            case '!':
            case '-':
            case Token.IDENTIFIER_ID:
            case '(':
            case IntConstant.INT_CONSTANT_ID:
            case LiteralConstant.LIT_CONSTANT_ID:
                
                a = expression();
                break;
                
            default:
                error();
                synchTo(conditionFollow);
        }
        
        return a;
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
    
    
    private Attribute type(){
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
        
        return new Attribute(type);
    }
    
    private Token eat(Token t){
        Token previousToken = currentToken;
        if (currentToken.equals(t)) {
            currentToken = lexer.getToken();
        }else {
            error();
        }
        
        return previousToken;
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
    
    public void semanticError(String message,int line){
        this.success = false;
        if(!recoveringFromError){
            errorMessages.append(PrintColor.RED + "Semantic error: ").append(message).append(" on line ").append(line).append(".").append("\n" + PrintColor.RESET);
        }
    }
    
    public void printUndeclaredId(Token id,int line){
        semanticError("use of undeclared identifier < "+ id.getLexeme() + " >",line);
    }
    
    private void checkIdentifierUnicity(Token token, Type type){
        //SEMANTICS: identifier unity check
        if(token instanceof Identifier){
            SymbolTableEntry idInfo = symbolTable.get(token.getLexeme());

            if(idInfo.isInstalled()){
                semanticError("redeclaration of identifier < " + token.getLexeme() + " >");
            }else{
                idInfo.installType(type);
            }
        }
    }
    
    public int genTempAddress(){
        return offset++;
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