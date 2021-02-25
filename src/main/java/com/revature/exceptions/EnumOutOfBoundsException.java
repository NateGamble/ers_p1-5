package com.revature.exceptions;

public class EnumOutOfBoundsException extends RuntimeException {

    public EnumOutOfBoundsException() {
        super("Provided enum value is out of valid range");
    }

    public EnumOutOfBoundsException(String message) {
        super(message);
    }
    
    
}
