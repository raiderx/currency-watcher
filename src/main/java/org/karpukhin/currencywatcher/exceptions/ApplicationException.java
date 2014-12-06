package org.karpukhin.currencywatcher.exceptions;

/**
 * @author Pavel Karpukhin
 * @since 30.03.14
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
