package com.app.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.booking.client.TechnicianClient;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.dto.response.CancelBookingResponse;
import com.app.booking.dto.response.RescheduleBookingResponse;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

        private final BookingRepository bookingRepository;
        
        private final TechnicianClient technicianClient;


        public BookingResponse createBooking(CreateBookingRequest request, String customerId) {

                Booking booking = Booking.builder()
                                .bookingId("BK-" + UUID.randomUUID().toString().substring(0, 8))
                                .customerId(customerId)
                                .serviceName(request.getServiceName())
                                .categoryName(request.getCategoryName())
                                .scheduledDate(request.getScheduledDate())
                                .timeSlot(request.getTimeSlot())
                                .address(request.getAddress())
                                .issueDescription(request.getIssueDescription())
                                .paymentMode(request.getPaymentMode())
                                .status(BookingStatus.REQUESTED)
                                .createdAt(LocalDateTime.now())
                                .build();

                bookingRepository.save(booking);

                return BookingResponse.builder()
                                .bookingId(booking.getBookingId())
                                .status(booking.getStatus().name())
                                .build();
        }

        public List<BookingListResponse> getAllBookings() {

                List<Booking> bookings = bookingRepository.findAll();

                return bookings.stream()
                                .map(booking -> BookingListResponse.builder()
                                                .bookingId(booking.getBookingId())
                                                .customerId(booking.getCustomerId())
                                                .serviceName(booking.getServiceName())
                                                .categoryName(booking.getCategoryName())
                                                .scheduledDate(booking.getScheduledDate())
                                                .timeSlot(booking.getTimeSlot())
                                                .address(booking.getAddress())
                                                .status(booking.getStatus().name())
                                                .createdAt(booking.getCreatedAt())
                                                .build())
                                .toList();
        }

        public BookingDetailsResponse getBookingByBookingId(String bookingId) {

                Booking booking = bookingRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Booking not found with bookingId: " + bookingId));

                return BookingDetailsResponse.builder()
                                .bookingId(booking.getBookingId())
                                .customerId(booking.getCustomerId())
                                .serviceName(booking.getServiceName())
                                .categoryName(booking.getCategoryName())
                                .scheduledDate(booking.getScheduledDate())
                                .timeSlot(booking.getTimeSlot())
                                .address(booking.getAddress())
                                .issueDescription(booking.getIssueDescription())
                                .paymentMode(booking.getPaymentMode())
                                .status(booking.getStatus().name())
                                .createdAt(booking.getCreatedAt())
                                .build();
        }
        
        public List<BookingListResponse> getMyBookings(String customerId) {

            List<Booking> bookings =
                    bookingRepository.findByCustomerId(customerId);

            return bookings.stream()
                    .map(booking -> BookingListResponse.builder()
                            .bookingId(booking.getBookingId())
                            .customerId(booking.getCustomerId())
                            .serviceName(booking.getServiceName())
                            .categoryName(booking.getCategoryName())
                            .scheduledDate(booking.getScheduledDate())
                            .timeSlot(booking.getTimeSlot())
                            .address(booking.getAddress())
                            .status(booking.getStatus().name())
                            .createdAt(booking.getCreatedAt())
                            .build()
                    )
                    .toList();
        }
        
        public List<BookingListResponse> getBookingHistory() {

            List<Booking> bookings =
                    bookingRepository.findAllByOrderByCreatedAtDesc();

            return bookings.stream()
                    .map(booking -> BookingListResponse.builder()
                            .bookingId(booking.getBookingId())
                            .customerId(booking.getCustomerId())
                            .serviceName(booking.getServiceName())
                            .categoryName(booking.getCategoryName())
                            .scheduledDate(booking.getScheduledDate())
                            .timeSlot(booking.getTimeSlot())
                            .address(booking.getAddress())
                            .status(booking.getStatus().name())
                            .createdAt(booking.getCreatedAt())
                            .build()
                    )
                    .toList();
        }
        
        public RescheduleBookingResponse rescheduleBooking(String bookingId, RescheduleBookingRequest request) {

            Booking booking = bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(() ->
                            new BookingNotFoundException(bookingId)
                    );

            // check if booking is cancelled
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                return RescheduleBookingResponse.builder()
                        .bookingId(bookingId)
                        .status("CANCELLED")
                        .message("Cancelled booking cannot be rescheduled")
                        .build();
            }
            
            booking.setScheduledDate(request.getScheduledDate());
            booking.setTimeSlot(request.getTimeSlot());
            
            bookingRepository.save(booking);

            return RescheduleBookingResponse.builder()
                    .bookingId(bookingId)
                    .status(booking.getStatus().name())
                    .message("Booking has been rescheduled")
                    .build();
        }
        
        public CancelBookingResponse cancelBooking(String bookingId) {

            Booking booking = bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(() ->
                            new BookingNotFoundException(bookingId)
                    );

//            check is booking already cancelled
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                return CancelBookingResponse.builder()
                        .bookingId(bookingId)
                        .status("CANCELLED")
                        .message("Booking is already cancelled")
                        .build();
            }

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            return CancelBookingResponse.builder()
                    .bookingId(bookingId)
                    .status("CANCELLED")
                    .message("Booking cancelled")
                    .build();
        }

		public List<BookingListResponse> getAssignedBookingsForTechnician(
                String technicianId
        ) {

            List<Booking> bookings =
                    bookingRepository.findByTechnicianId(technicianId);

            return bookings.stream()
                    .map(booking -> BookingListResponse.builder()
                            .bookingId(booking.getBookingId())
                            .customerId(booking.getCustomerId())
                            .serviceName(booking.getServiceName())
                            .categoryName(booking.getCategoryName())
                            .scheduledDate(booking.getScheduledDate())
                            .timeSlot(booking.getTimeSlot())
                            .address(booking.getAddress())
                            .status(booking.getStatus().name())
                            .createdAt(booking.getCreatedAt())
                            .build()
                    )
                    .toList();
        }
		public BookingResponse assignTechnician(String bookingId, String technicianId) {

		    Booking booking = bookingRepository.findByBookingId(bookingId)
		            .orElseThrow(() -> new BookingNotFoundException(bookingId));

		    if (booking.getStatus() != BookingStatus.REQUESTED) {
		        throw new IllegalArgumentException("Only REQUESTED bookings can be assigned");
		    }

		    booking.setTechnicianId(technicianId);
		    booking.setStatus(BookingStatus.ASSIGNED);

		    bookingRepository.save(booking);

		    technicianClient.markBusy(technicianId, bookingId);

		    return BookingResponse.builder()
		            .bookingId(bookingId)
		            .status(booking.getStatus().name())
		            .build();
		}

		public BookingResponse updateStatus(String bookingId, String status) {

		    Booking booking = bookingRepository.findByBookingId(bookingId)
		            .orElseThrow(() -> new BookingNotFoundException(bookingId));

		    BookingStatus newStatus = BookingStatus.valueOf(status);

		    if (booking.getStatus() == BookingStatus.CANCELLED) {
		        throw new IllegalArgumentException("Cancelled booking cannot be updated");
		    }

		    booking.setStatus(newStatus);

		    bookingRepository.save(booking);
		    if (newStatus == BookingStatus.COMPLETED && booking.getTechnicianId() != null) {
		        technicianClient.markAvailable(booking.getTechnicianId());
		    }

		    return BookingResponse.builder()
		            .bookingId(bookingId)
		            .status(newStatus.name())
		            .build();
		}



}
