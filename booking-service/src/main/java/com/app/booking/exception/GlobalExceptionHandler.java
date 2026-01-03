package com.app.booking.exception;

import com.app.booking.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------------- VALIDATION ERRORS (400) ----------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(400)
                        .error("VALIDATION_ERROR")
                        .message("Invalid request data")
                        .validationErrors(errors)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ---------------- NOT FOUND (404) ----------------
    @ExceptionHandler({
            BookingNotFoundException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .status(404)
                        .error("NOT_FOUND")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ---------------- UNAUTHORIZED (401) ----------------
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(SecurityException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .status(401)
                        .error("UNAUTHORIZED")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ---------------- FORBIDDEN (403) ----------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder()
                        .status(403)
                        .error("FORBIDDEN")
                        .message("You are not allowed to access this resource")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ---------------- BAD REQUEST (400) ----------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(400)
                        .error("BAD_REQUEST")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ---------------- INTERNAL SERVER ERROR (500) ----------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {

        ex.printStackTrace(); // 👈 keep for debugging

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .status(500)
                        .error("INTERNAL_SERVER_ERROR")
                        .message("Something went wrong. Please try again.")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
