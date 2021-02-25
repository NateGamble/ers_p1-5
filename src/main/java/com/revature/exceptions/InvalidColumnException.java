package com.revature.exceptions;

public class InvalidColumnException extends RuntimeException {

    public InvalidColumnException() {
        super("One or more columns in object contain invalid values");
    }

    public InvalidColumnException(String message) {
        super(message);
    }
    
    
}
