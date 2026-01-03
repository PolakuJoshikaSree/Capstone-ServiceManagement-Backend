package com.app.auth.exception;

import com.app.auth.payload.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ================= MethodArgumentNotValidException =================
    @Test
    void handleValidation_exception() throws Exception {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "obj");

        bindingResult.addError(
                new FieldError("obj", "email", "Email is required")
        );

        // Spring 6 requires MethodParameter
        Method method =
                this.getClass().getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());

        Map<?, ?> data = (Map<?, ?>) response.getBody().getData();
        assertTrue(data.containsKey("email"));
    }

    // ================= ConstraintViolationException =================
    @Test
    void handleConstraint_exception() {

        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);

        Mockito.when(path.toString()).thenReturn("field");
        Mockito.when(violation.getPropertyPath()).thenReturn(path);
        Mockito.when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations =
                Collections.singleton(violation);

        ConstraintViolationException ex =
                new ConstraintViolationException(violations);

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleConstraint(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());

        Map<?, ?> data = (Map<?, ?>) response.getBody().getData();
        assertTrue(data.containsKey("field"));
    }

    // ================= IllegalArgumentException =================
    @Test
    void handleIllegalArgument_exception() {

        IllegalArgumentException ex =
                new IllegalArgumentException("Invalid input");

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleIllegalArg(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Invalid input", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ================= IllegalStateException =================
    @Test
    void handleIllegalState_exception() {

        IllegalStateException ex =
                new IllegalStateException("Forbidden state");

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleIllegalState(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Forbidden state", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ================= SecurityException =================
    @Test
    void handleSecurity_exception() {

        SecurityException ex =
                new SecurityException("Access denied");

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleSecurity(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Access denied", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ================= Generic Exception =================
    @Test
    void handleAll_exception() {

        Exception ex = new Exception("Boom");
        WebRequest request = Mockito.mock(WebRequest.class);

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleAll(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERROR", response.getBody().getStatus());
        assertEquals("Internal server error", response.getBody().getMessage());

        Map<?, ?> data = (Map<?, ?>) response.getBody().getData();
        assertEquals("Exception", data.get("exception"));
        assertEquals("Boom", data.get("message"));
    }

    // ===== dummy method required for Spring 6 MethodParameter =====
    @SuppressWarnings("unused")
    private void dummyMethod(String param) {
    }
}
