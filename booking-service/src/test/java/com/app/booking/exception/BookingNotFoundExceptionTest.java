package com.app.booking.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingNotFoundExceptionTest {

    @Test
    void exceptionMessage_correct() {
        BookingNotFoundException ex =
                new BookingNotFoundException("BK123");

        assertEquals(
                "Booking not found for id: BK123",
                ex.getMessage()
        );
    }
}
