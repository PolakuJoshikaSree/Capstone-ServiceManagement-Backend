package com.app.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.booking.model.Booking;

public interface BookingRepository extends MongoRepository<Booking, String> {

    Optional<Booking> findByBookingId(String bookingId);

    List<Booking> findByCustomerId(String customerId);

    Page<Booking> findByCustomerId(String customerId, Pageable pageable);

    Page<Booking> findAll(Pageable pageable);

    List<Booking> findAllByOrderByCreatedAtDesc();

    List<Booking> findByTechnicianId(String technicianId);
}
