package com.app.service_catalog.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerDirectTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
    }

    @Test
    void handleBadRequest() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleBadRequest(new BadRequestException("bad"), request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void handleDuplicateKey() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleDuplicateKey(
                        new org.springframework.dao.DuplicateKeyException("dup"), request);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void handleRuntime() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleRuntimeException(new RuntimeException("boom"), request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void handleGenericException() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleGenericException(new Exception("ex"), request);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void handleValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult result = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(result);
        when(result.getFieldErrors())
                .thenReturn(List.of(new FieldError("obj", "field", "invalid")));

        ResponseEntity<ApiErrorResponse> response =
                handler.handleValidationErrors(ex, request);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
