package com.revature.exceptions;

public class PersistenceException extends RuntimeException {

    public PersistenceException() {
        super("An error ocurred when persisting data");
    }

    public PersistenceException(String message) {
        super(message);
    }
    
}
