package com.app.billing.repository;

import com.app.billing.dto.MonthlyRevenueDTO;
import com.app.billing.model.Invoice;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByBookingId(String bookingId);
    List<Invoice> findByCustomerId(String customerId);
    @Aggregation(pipeline = {
    	    "{ $match: { paymentStatus: 'PAID', paidAt: { $ne: null } } }",
    	    "{ $group: { " +
    	        "_id: { year: { $year: '$paidAt' }, month: { $month: '$paidAt' } }, " +
    	        "totalRevenue: { $sum: '$totalAmount' } " +
    	    "} }",
    	    "{ $project: { " +
    	        "year: '$_id.year', " +
    	        "month: '$_id.month', " +
    	        "totalRevenue: 1, " +
    	        "_id: 0 " +
    	    "} }",
    	    "{ $sort: { year: 1, month: 1 } }"
    	})
    	List<MonthlyRevenueDTO> getMonthlyRevenue();


}
