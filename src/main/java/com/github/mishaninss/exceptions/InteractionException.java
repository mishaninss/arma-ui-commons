package com.github.mishaninss.exceptions;

/**
 * Created by Sergey_Mishanin
 */
public class InteractionException extends RuntimeException {
    public InteractionException(String message) {
        super(message);
    }

    public InteractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
