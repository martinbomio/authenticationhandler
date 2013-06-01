package edu.umflix.authenticationhandler.exceptions;

/**
 *
 */
public class InvalidUserException extends Exception {

    public InvalidUserException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the specified detail message
     */
    public InvalidUserException(String message) {
        super(message);
    }
}
