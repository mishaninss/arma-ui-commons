package com.github.mishaninss.exceptions;

/**
 * Created by Sergey_Mishanin
 */
public class SessionLostException extends RuntimeException {
    public SessionLostException(String message) {
        super(message);
    }

    public SessionLostException(String message, Throwable cause) {
        super(message, cause);
    }
}
