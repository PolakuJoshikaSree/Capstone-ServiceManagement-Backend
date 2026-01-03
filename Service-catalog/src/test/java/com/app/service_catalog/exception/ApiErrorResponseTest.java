package com.app.service_catalog.exception;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    @Test
    void builderAndGetters_workCorrectly() {

        Instant now = Instant.now();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("Category not found")
                .path("/api/categories/1")
                .validationErrors(Map.of("name", "required"))
                .build();

        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Category not found", response.getMessage());
        assertEquals("/api/categories/1", response.getPath());
        assertEquals(1, response.getValidationErrors().size());
    }

    @Test
    void equalsAndHashCode_areEqual() {

        Instant now = Instant.now();

        ApiErrorResponse r1 = ApiErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid")
                .path("/x")
                .build();

        ApiErrorResponse r2 = ApiErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid")
                .path("/x")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void equals_returnsFalse_forDifferentObject() {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(500)
                .error("Error")
                .message("Fail")
                .path("/err")
                .build();

        assertNotEquals(response, "string");
    }

    @Test
    void toString_isNotNull() {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(500)
                .error("Error")
                .message("Fail")
                .path("/err")
                .build();

        assertNotNull(response.toString());
    }
}
