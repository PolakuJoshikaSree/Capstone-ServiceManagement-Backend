package com.app.booking.exception;

import com.app.booking.dto.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleNotFound_bookingNotFound() {

        BookingNotFoundException ex =
                new BookingNotFoundException("BK1");

        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("NOT_FOUND", response.getBody().getError());
    }

    @Test
    void handleUnauthorized_securityException() {

        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorized(
                        new SecurityException("Unauthorized")
                );

        assertEquals(401, response.getStatusCode().value());
        assertEquals("UNAUTHORIZED", response.getBody().getError());
    }

    @Test
    void handleForbidden_accessDenied() {

        ResponseEntity<ErrorResponse> response =
                handler.handleForbidden(
                        new AccessDeniedException("Denied")
                );

        assertEquals(403, response.getStatusCode().value());
        assertEquals("FORBIDDEN", response.getBody().getError());
    }

    @Test
    void handleBadRequest_illegalArgument() {

        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequest(
                        new IllegalArgumentException("Bad input")
                );

        assertEquals(400, response.getStatusCode().value());
        assertEquals("BAD_REQUEST", response.getBody().getError());
    }

    @Test
    void handleGeneric_exception() {

        ResponseEntity<ErrorResponse> response =
                handler.handleGeneric(
                        new RuntimeException("Boom"),
                        null
                );

        assertEquals(500, response.getStatusCode().value());
        assertEquals("INTERNAL_SERVER_ERROR",
                response.getBody().getError());
    }
}
