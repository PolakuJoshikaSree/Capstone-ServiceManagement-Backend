package com.app.gateway.exception;

import com.app.gateway.payload.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleIllegalArg(new IllegalArgumentException("bad"));

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void handleIllegalState() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleIllegalState(new IllegalStateException("forbidden"));

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void handleSecurity() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleSecurity(new SecurityException("denied"));

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void handleConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        Path path = mock(Path.class);
        when(path.toString()).thenReturn("field");

        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("invalid");

        ConstraintViolationException ex =
                new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleConstraint(ex);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void handleWebExchangeBindException() {
        BeanPropertyBindingResult result =
                new BeanPropertyBindingResult(new Object(), "obj");

        result.addError(new FieldError("obj", "field", "invalid"));

        WebExchangeBindException ex =
                new WebExchangeBindException(null, result);

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void handleAllException() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleAll(new RuntimeException("boom"));

        assertEquals(500, response.getStatusCode().value());
    }
}
