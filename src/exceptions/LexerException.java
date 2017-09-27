
package exceptions;

/**
 *
 * @author jr
 */
public class LexerException extends RuntimeException {
    
    private Exception originalException;
    private String message;

    public LexerException(String message,Exception originalException){
        this.message = message;
        this.originalException = originalException;
    }
    
    public Exception getOriginalException() {
        return originalException;
    }

    public String getMessage() {
        return message;
    }
    
    
}
