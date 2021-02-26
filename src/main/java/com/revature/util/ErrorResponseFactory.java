package com.revature.util;

import com.revature.dtos.ErrorResponse;
import com.revature.dtos.HttpStatus;

/**
 * Taken from Quizzard project at https://github.com/210119-java-enterprise/quizzard
 */
public class ErrorResponseFactory {

    private static ErrorResponseFactory errRespFactory = new ErrorResponseFactory();

    private ErrorResponseFactory() {
        super();
    }

    public static ErrorResponseFactory getInstance() {
        return errRespFactory;
    }

    public ErrorResponse generateErrorResponse(int status, String message) {
        return new ErrorResponse(status, message, System.currentTimeMillis());
    }

    public ErrorResponse generateErrorResponse(HttpStatus status) {
        return new ErrorResponse(status.getStatus(), status.toString(), System.currentTimeMillis());
    }

}
