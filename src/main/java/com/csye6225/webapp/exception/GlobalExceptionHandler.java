package com.csye6225.webapp.exception;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleMessageNotReadableException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(Collections.singletonMap("error","Invalid Request"));
    }
}
