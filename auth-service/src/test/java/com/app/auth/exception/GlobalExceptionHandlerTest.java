package com.app.auth.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerFullTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void illegalArgument() {
        assertEquals(400,
                handler.handleIllegalArg(new IllegalArgumentException("bad"))
                        .getStatusCode().value());
    }

    @Test
    void illegalState() {
        assertEquals(403,
                handler.handleIllegalState(new IllegalStateException("forbidden"))
                        .getStatusCode().value());
    }

    @Test
    void genericException() {
        assertEquals(500,
                handler.handleAll(new RuntimeException("err"), null)
                        .getStatusCode().value());
    }
}
