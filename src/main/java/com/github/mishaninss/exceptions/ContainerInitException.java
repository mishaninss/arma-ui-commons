package com.github.mishaninss.exceptions;

/**
 * Created by Sergey_Mishanin
 */
public class ContainerInitException extends RuntimeException {
    public ContainerInitException(String message) {
        super(message);
    }

    public ContainerInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
