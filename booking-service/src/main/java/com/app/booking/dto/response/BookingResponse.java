package com.app.booking.dto.response;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {

	private String bookingId;
	private String status;
}