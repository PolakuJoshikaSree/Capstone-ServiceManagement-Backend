package com.app.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.app.booking.model.Invoice;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByBookingId(String bookingId);

    List<Invoice> findByCustomerId(String customerId);

    List<Invoice> findByInvoiceStatus(String status);
}
