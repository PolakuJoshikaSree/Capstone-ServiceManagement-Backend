package com.app.booking.service;

import com.app.booking.dto.response.BookingStatusReportResponse;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ReportService reportService;

    // ---------------- BOOKINGS BY STATUS ----------------
    @Test
    void getBookingsByStatus_returnsData() {

        Document doc = new Document("_id", "COMPLETED")
                .append("count", 5L);

        AggregationResults<Document> results =
                new AggregationResults<>(List.of(doc), new Document());

        // ✅ FIX: match Aggregation properly
        lenient().when(
                mongoTemplate.aggregate(
                        any(Aggregation.class),
                        eq("customer-bookings"),
                        eq(Document.class)
                )
        ).thenReturn(results);

        List<BookingStatusReportResponse> response =
                reportService.getBookingsByStatus();

        assertEquals(1, response.size());
        assertEquals("COMPLETED", response.get(0).getStatus());
        assertEquals(5L, response.get(0).getCount());
    }

    // ---------------- MONTHLY BOOKINGS ----------------
    @Test
    void getMonthlyBookings_returnsDocuments() {

        Document doc = new Document("month", 1)
                .append("count", 10);

        AggregationResults<Document> results =
                new AggregationResults<>(List.of(doc), new Document());

        lenient().when(
                mongoTemplate.aggregate(
                        any(Aggregation.class),
                        eq("customer-bookings"),
                        eq(Document.class)
                )
        ).thenReturn(results);

        List<Document> response = reportService.getMonthlyBookings();

        assertEquals(1, response.size());
        assertEquals(1, response.get(0).get("month"));
    }

    // ---------------- MONTHLY REVENUE ----------------
    @Test
    void getMonthlyRevenue_returnsDocuments() {

        Document doc = new Document("month", 2)
                .append("revenue", 5000);

        AggregationResults<Document> results =
                new AggregationResults<>(List.of(doc), new Document());

        lenient().when(
                mongoTemplate.aggregate(
                        any(Aggregation.class),
                        eq("invoices"),
                        eq(Document.class)
                )
        ).thenReturn(results);

        List<Document> response = reportService.getMonthlyRevenue();

        assertEquals(1, response.size());
        assertEquals(5000, response.get(0).get("revenue"));
    }
}
