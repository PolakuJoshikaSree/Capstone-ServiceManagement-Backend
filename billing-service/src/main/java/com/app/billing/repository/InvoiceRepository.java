package com.app.billing.repository;

import com.app.billing.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByBookingId(String bookingId);
    List<Invoice> findByCustomerId(String customerId);
}
