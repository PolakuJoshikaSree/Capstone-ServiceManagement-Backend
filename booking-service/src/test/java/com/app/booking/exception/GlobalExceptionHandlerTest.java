package com.app.booking.exception;

import com.app.booking.dto.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ---------- VALIDATION ERROR (400) ----------
    @Test
    void handleValidation_returns400() {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "obj");

        bindingResult.addError(
                new FieldError("obj", "field", "must not be blank")
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("VALIDATION_ERROR", response.getBody().getError());
        assertTrue(response.getBody().getValidationErrors().containsKey("field"));
    }

    // ---------- NOT FOUND (404) ----------
    @Test
    void handleNotFound_bookingNotFound() {

        BookingNotFoundException ex =
                new BookingNotFoundException("BK1");

        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("NOT_FOUND", response.getBody().getError());
    }

    @Test
    void handleNotFound_noSuchElement() {

        NoSuchElementException ex =
                new NoSuchElementException("missing");

        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
    }

    // ---------- UNAUTHORIZED (401) ----------
    @Test
    void handleUnauthorized_returns401() {

        SecurityException ex =
                new SecurityException("Invalid token");

        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorized(ex);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("UNAUTHORIZED", response.getBody().getError());
    }

    // ---------- FORBIDDEN (403) ----------
    @Test
    void handleForbidden_returns403() {

        AccessDeniedException ex =
                new AccessDeniedException("Denied");

        ResponseEntity<ErrorResponse> response =
                handler.handleForbidden(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("FORBIDDEN", response.getBody().getError());
    }

    // ---------- BAD REQUEST (400) ----------
    @Test
    void handleBadRequest_returns400() {

        IllegalArgumentException ex =
                new IllegalArgumentException("Bad input");

        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequest(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("BAD_REQUEST", response.getBody().getError());
    }

    // ---------- INTERNAL SERVER ERROR (500) ----------
    @Test
    void handleGeneric_returns500() {

        Exception ex =
                new Exception("Boom");

        ResponseEntity<ErrorResponse> response =
                handler.handleGeneric(ex, null);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
    }
}
