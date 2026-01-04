package com.app.booking.service;

import com.app.booking.dto.response.BookingStatusReportResponse;
import com.app.booking.dto.response.TechnicianTaskCountResponse;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MongoTemplate mongoTemplate;

    /* ================= BOOKINGS BY STATUS ================= */
    public List<BookingStatusReportResponse> getBookingsByStatus() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("status").count().as("count")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        "customer-bookings",
                        Document.class
                );

        return results.getMappedResults().stream()
                .map(doc -> new BookingStatusReportResponse(
                        doc.getString("_id"),                          // status
                        ((Number) doc.get("count")).longValue()        // SAFE conversion
                ))
                .toList();
    }

    /* ================= TECHNICIAN WISE TASK COUNT ================= */
    public List<TechnicianTaskCountResponse> getTechnicianTaskCount() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("technicianId").ne(null)),
                Aggregation.group("technicianId").count().as("taskCount")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(
                        aggregation,
                        "customer-bookings",
                        Document.class
                );

        return results.getMappedResults().stream()
                .map(doc -> new TechnicianTaskCountResponse(
                        doc.getString("_id"),                          // technicianId
                        ((Number) doc.get("taskCount")).longValue()    // SAFE conversion
                ))
                .toList();
    }
}
