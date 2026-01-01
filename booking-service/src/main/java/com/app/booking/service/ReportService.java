package com.app.booking.service;

import com.app.booking.dto.response.BookingStatusReportResponse;
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
                        doc.getString("_id"),
                        doc.getLong("count")
                ))
                .toList();
    }

    /* ================= MONTHLY BOOKINGS ================= */

    public List<Document> getMonthlyBookings() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .andExpression("month(createdAt)").as("month"),
                Aggregation.group("month").count().as("count"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "_id")
        );

        return mongoTemplate.aggregate(
                aggregation,
                "customer-bookings",
                Document.class
        ).getMappedResults();
    }

    /* ================= MONTHLY REVENUE ================= */

    public List<Document> getMonthlyRevenue() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("paymentStatus").is("PAID")),
                Aggregation.project()
                        .andExpression("month(paidAt)").as("month")
                        .and("totalAmount").as("amount"),
                Aggregation.group("month").sum("amount").as("revenue"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "_id")
        );

        return mongoTemplate.aggregate(
                aggregation,
                "invoices",
                Document.class
        ).getMappedResults();
    }
}
