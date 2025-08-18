package com.bookmyturf.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (var error : e.getBindingResult().getAllErrors()) {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        }
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid parameter: " + e.getName(), null);
    }

    // ✅ Access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        return buildError(HttpStatus.FORBIDDEN, "Access Denied", null);
    }

    // ✅ Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", null);
    }
    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<Map<String, Object>> handleCustomAppException(CustomAppException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), e.getDetails());
    }

    public static ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("status Code", status.value());
        response.put("Status", status.getReasonPhrase());
        response.put("Message", message);
        response.put("Error", details);
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<Map<String, Object>> GoodResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status Code", status.value());
        response.put("Status", status.getReasonPhrase());
        response.put("Message", message);
        response.put("Data", data);
        return new ResponseEntity<>(response, status);
    }
}
