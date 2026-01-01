package com.app.auth.exception;

import com.app.auth.payload.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(fe -> fe.getField(), fe -> fe.getDefaultMessage()));
        ApiResponse<Object> api = new ApiResponse<>();
        api.setStatus("ERROR");
        api.setMessage("Validation failed");
        api.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraint(ConstraintViolationException ex) {
        ApiResponse<Object> api = new ApiResponse<>();
        api.setStatus("ERROR");
        api.setMessage("Validation failed");
        Map<String, String> map = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> map.put(cv.getPropertyPath().toString(), cv.getMessage()));
        api.setData(map);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArg(IllegalArgumentException ex) {
        ApiResponse<Object> api = new ApiResponse<>();
        api.setStatus("ERROR");
        api.setMessage(ex.getMessage());
        api.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalState(IllegalStateException ex) {
        ApiResponse<Object> api = new ApiResponse<>();
        api.setStatus("ERROR");
        api.setMessage(ex.getMessage());
        api.setData(null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(api);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Object>> handleSecurity(SecurityException ex) {
        ApiResponse<Object> api = new ApiResponse<>();
        api.setStatus("ERROR");
        api.setMessage(ex.getMessage());
        api.setData(null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(api);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex, WebRequest req) {
        ApiResponse<Object> api = new ApiResponse<>();
        api.setStatus("ERROR");
        api.setMessage("Internal server error");
        Map<String, String> map = new HashMap<>();
        map.put("exception", ex.getClass().getSimpleName());
        map.put("message", ex.getMessage());
        api.setData(map);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(api);
    }
}

