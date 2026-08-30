package com.example.servicea.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        logger.warn("Validation error: {}", fieldErrors);
        return build(HttpStatus.BAD_REQUEST, "Validation Failed",
                "One or more fields are invalid", fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(HttpMessageNotReadableException e) {
        logger.warn("Malformed request body: {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Malformed Request",
                "Request body is malformed or contains an invalid value", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        logger.warn("Type mismatch for parameter '{}': {}", e.getName(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Invalid Argument",
                "Invalid value for parameter '" + e.getName() + "'", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        logger.warn("Data integrity violation: {}", e.getMessage());
        // Walk the cause chain: the most specific message (e.g. the PostgreSQL
        // constraint message) is nested, not on the top-level wrapper.
        if (containsInChain(e, "unique") || containsInChain(e, "duplicate")) {
            return build(HttpStatus.CONFLICT, "Conflict",
                    "Duplicate entry: this value already exists", null);
        }
        return build(HttpStatus.BAD_REQUEST, "Data Integrity Violation",
                "Invalid data provided (e.g. a required field is missing)", null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException e) {
        logger.warn("Resource not found: {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "Not Found", e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        logger.error("Unhandled exception", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred", null);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String error,
                                                      String message, Map<String, String> fields) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        if (fields != null && !fields.isEmpty()) {
            body.put("fields", fields);
        }
        return ResponseEntity.status(status).body(body);
    }

    private boolean containsInChain(Throwable throwable, String needle) {
        String needleLower = needle.toLowerCase();
        Throwable current = throwable;
        while (current != null) {
            if (current.getMessage() != null && current.getMessage().toLowerCase().contains(needleLower)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
