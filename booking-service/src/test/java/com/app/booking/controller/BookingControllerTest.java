package com.app.booking.controller;

import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.security.JwtUtil;
import com.app.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    // Required to satisfy AuthFilter constructor
    @MockBean
    private JwtUtil jwtUtil;

    // ---------------- CREATE BOOKING ----------------
    @Test
    @WithMockUser(username = "cust1")
    void createBooking_authorized() throws Exception {

        when(bookingService.createBooking(any(), anyString()))
                .thenReturn(new BookingResponse("BK-1", "REQUESTED"));

        mockMvc.perform(post("/api/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "serviceName":"Plumbing",
                      "categoryName":"Repair",
                      "scheduledDate":"2026-01-10",
                      "timeSlot":"10:00 - 12:00",
                      "address":"Hyd",
                      "issueDescription":"Leak",
                      "paymentMode":"CASH"
                    }
                """))
                .andExpect(status().isUnauthorized());
    }

    // ---------------- GET ALL BOOKINGS ----------------
    @Test
    @WithMockUser(username = "admin")
    void getAll_authorized() throws Exception {

        when(bookingService.getAllBookings()).thenReturn(
                List.of(
                        BookingListResponse.builder()
                                .bookingId("BK-1")
                                .customerId("cust1")
                                .serviceName("Plumbing")
                                .categoryName("Repair")
                                .scheduledDate(null)
                                .timeSlot("10-12")
                                .address("Hyd")
                                .status("REQUESTED")
                                .createdAt(LocalDateTime.now())
                                .build()
                )
        );

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------- MY BOOKINGS ----------------
    @Test
    @WithMockUser(username = "cust1")
    void myBookings_authorized() throws Exception {

        when(bookingService.getMyBookings(anyString()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------- TECHNICIAN BOOKINGS ----------------
    @Test
    @WithMockUser(username = "tech1")
    void myAssignedBookings_authorized() throws Exception {

        when(bookingService.getAssignedBookingsForTechnician(anyString()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/bookings/technician/my"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------- UPDATE STATUS ----------------
    @Test
    @WithMockUser(username = "tech1")
    void updateStatus_authorized() throws Exception {

        when(bookingService.updateStatus(anyString(), anyString()))
                .thenReturn(new BookingResponse("BK-1", "COMPLETED"));

        mockMvc.perform(put("/api/bookings/BK-1/status")
                .contentType(APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isUnauthorized());
    }


    // ---------------- ASSIGN TECHNICIAN ----------------
    @Test
    @WithMockUser(username = "admin")
    void assignTechnician_authorized() throws Exception {

        when(bookingService.assignTechnician(anyString(), anyString()))
                .thenReturn(new BookingResponse("BK-1", "ASSIGNED"));

        mockMvc.perform(put("/api/bookings/BK-1/assign/TECH-1"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "tech1")
    void updateStatus_missingStatus_returns400() throws Exception {

        mockMvc.perform(put("/api/bookings/BK-1/status")
                .contentType(APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void myBookings_unauthorized() throws Exception {

        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void myAssignedBookings_unauthorized() throws Exception {

        mockMvc.perform(get("/api/bookings/technician/my"))
                .andExpect(status().isUnauthorized());
    }

}
