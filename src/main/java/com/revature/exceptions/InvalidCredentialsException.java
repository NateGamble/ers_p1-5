package com.revature.exceptions;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("The credentials provided threw an error, check validity.");
    }

    public InvalidCredentialsException(String msg){
        super(msg);
    }
}
