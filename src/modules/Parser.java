package modules;

import dataunits.Token;

/**
 *
 * @author jr
 */
public class Parser {

    public static void main(String[] args) {

        String path = "test/test1.j";
        Lexer lexer = new Lexer(path);

        Token token;
        while ((token = lexer.getToken()) != null) {

            System.out.print(token + "\n");
        }

    }

}
