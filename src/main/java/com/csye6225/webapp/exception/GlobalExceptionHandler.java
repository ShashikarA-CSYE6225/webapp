package com.csye6225.webapp.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {
    HttpHeaders headers;

    public GlobalExceptionHandler() {
        this.headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // For 405 status code - Method not allowed
    public ResponseEntity<Void> handleMethodNotSupported() {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(headers)
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class) // For 404 status code - Not Found
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(headers)
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) //For unknown json attributes - 400 Bad Request
    public ResponseEntity<Object> handleMessageNotReadableException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Unknown json attributes found"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class) //For leaving out required json attributes - 400 Bad Request
    public ResponseEntity<Object> handleDataIntegrityViolationException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Required attributes missing"));
    }

    @ExceptionHandler(TransactionSystemException.class) //For Email Validation - 400 Bad Request
    public ResponseEntity<Object> handleTransactionSystemException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request"));
    }

    @ExceptionHandler(IllegalArgumentException.class) //For Acc Created and Acc Updated - They Should not be given in request 400 Bad Request
    public ResponseEntity<Object> handleIllegalArgumentExceptionException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Not a valid request"));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class) //For Acc Created and Acc Updated - They Should not be given in request 400 Bad Request
    public ResponseEntity<Object> handleUsernameAlreadyExistsException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Username already exists"));
    }

    @ExceptionHandler(InvalidAuthorizationException.class) //Basic auth is null or null value in code - 400 Bad Request
    public ResponseEntity<Object> handleNullPointerException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Invalid Authorization"));
    }

    @ExceptionHandler(IncorrectPasswordException.class) //Basic auth is null or null value in code - 400 Bad Request
    public ResponseEntity<Object> handleIncorrectPasswordException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Incorrect password"));
    }

    @ExceptionHandler(UserNotFoundException.class) //Basic auth is null or null value in code - 400 Bad Request
    public ResponseEntity<Object> handleUserNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - User not found"));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingRequestHeaderException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request - Authorization not found"));
    }

    @ExceptionHandler(UserNotUpdatedException.class) //For blank values or general exception
    public ResponseEntity<Object> handleUserNotUpdatedException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request"));
    }

}
