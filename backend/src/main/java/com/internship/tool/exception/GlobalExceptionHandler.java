package com.internship.tool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Catches all exceptions and returns consistent JSON error responses.
 * Day 4 — Java Developer 1
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ─────────────────────────────────────────────
    @ExceptionHandler(Exceptions.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            Exceptions.ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage()));
    }

    // ── 400 Bad Request ───────────────────────────────────────────
    @ExceptionHandler(Exceptions.BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            Exceptions.BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, ex.getMessage()));
    }

    // ── 401 Unauthorized ──────────────────────────────────────────
    @ExceptionHandler(Exceptions.UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            Exceptions.UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, ex.getMessage()));
    }

    // ── 409 Conflict ──────────────────────────────────────────────
    @ExceptionHandler(Exceptions.ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            Exceptions.ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage()));
    }

    // ── Validation errors (@Valid) ────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Validation failed", errors));
    }

    // ── 500 Internal Server Error (catch-all) ─────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "An unexpected error occurred"));
    }

    // ── Error Response record ─────────────────────────────────────
    public record ErrorResponse(
            int status,
            String message,
            Object details,
            LocalDateTime timestamp
    ) {
        public ErrorResponse(int status, String message) {
            this(status, message, null, LocalDateTime.now());
        }
        public ErrorResponse(int status, String message, Object details) {
            this(status, message, details, LocalDateTime.now());
        }
    }
}
