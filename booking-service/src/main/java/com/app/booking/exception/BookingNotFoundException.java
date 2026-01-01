package com.app.booking.exception;

public class BookingNotFoundException extends RuntimeException {
	public BookingNotFoundException(String bookingId) {
		super("Booking not found for id: " + bookingId);
	}
}
