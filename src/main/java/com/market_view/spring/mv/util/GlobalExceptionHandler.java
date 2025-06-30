package com.market_view.spring.mv.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public final static String MSG = "message";
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex) {
        Map<String, String> map = new HashMap<>();
        map.put(MSG, ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus()).body(map);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> map = new HashMap<>();
        map.put(MSG, ex.getMessage() != null ? ex.getMessage() : "Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> map = new HashMap<>();
        map.put(MSG, "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> map = new HashMap<>();
        map.put(MSG, "Access denied: insufficient permissions");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put(MSG, "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Object> handleParameterExceptions(Exception ex) {
        Map<String, String> map = new HashMap<>();
        map.put(MSG, "Invalid request parameter: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, String> map = new HashMap<>();
        map.put(MSG, "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
    }
}
