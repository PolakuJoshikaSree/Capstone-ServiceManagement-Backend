package com.app.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Instant timestamp = Instant.now();
    private String status;
    private String message;
    private T data;
}

