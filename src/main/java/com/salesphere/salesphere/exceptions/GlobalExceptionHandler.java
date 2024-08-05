package com.salesphere.salesphere.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseFactory.createErrorResponse("Runtime Error", ex.getMessage()));
    }
}

class ErrorResponseFactory {
    public static ErrorResponse createErrorResponse(String type, String message) {
        return new ErrorResponse(type, message);
    }
}

class ErrorResponse {
    private String type;
    private String message;

    public ErrorResponse(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}