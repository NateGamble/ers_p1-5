package com.revature.exceptions;

/**
 * Taken from Quizzard project at https://github.com/210119-java-enterprise/quizzard
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("Authentication failed!");
    }

    public AuthenticationException(String message) {
        super(message);
    }

}
