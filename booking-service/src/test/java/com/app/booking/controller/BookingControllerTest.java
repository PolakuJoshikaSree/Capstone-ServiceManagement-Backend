package com.app.booking.controller;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.security.AuthFilter;
import com.app.booking.security.JwtUtil;
import com.app.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔥 CRITICAL
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    // 🔥 REQUIRED FOR CONTEXT LOAD
    @MockBean
    private AuthFilter authFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    /* ===================== CREATE ===================== */

    @Test
    void create_unauthorized() throws Exception {
        mockMvc.perform(post("/api/bookings"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void create_success() throws Exception {

        CreateBookingRequest request = CreateBookingRequest.builder()
                .serviceName("Plumbing")
                .categoryName("Home")
                .scheduledDate(LocalDate.now())
                .timeSlot("10:00 - 12:00")
                .address("Addr")
                .issueDescription("Leak")
                .paymentMode("CASH")
                .build();

        Mockito.when(bookingService.createBooking(Mockito.any(), Mockito.any()))
                .thenReturn(new BookingResponse("BK1", "REQUESTED"));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(true);

        mockMvc.perform(post("/api/bookings")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    /* ===================== GET ALL ===================== */

    @Test
    void getAll_unauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_success() throws Exception {

        Mockito.when(bookingService.getAllBookings())
                .thenReturn(List.of(new BookingListResponse()));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("admin", null);
        auth.setAuthenticated(true);

        mockMvc.perform(get("/api/bookings")
                        .principal(auth))
                .andExpect(status().isOk());
    }

    /* ===================== MY BOOKINGS ===================== */

    @Test
    void myBookings_unauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void myBookings_success() throws Exception {

        Mockito.when(bookingService.getMyBookings(Mockito.any()))
                .thenReturn(List.of(new BookingListResponse()));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(true);

        mockMvc.perform(get("/api/bookings/my")
                        .principal(auth))
                .andExpect(status().isOk());
    }

    /* ===================== MY ASSIGNED BOOKINGS ===================== */

    @Test
    void myAssignedBookings_unauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings/technician/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void myAssignedBookings_success() throws Exception {

        Mockito.when(bookingService.getAssignedBookingsForTechnician(Mockito.any()))
                .thenReturn(List.of(new BookingListResponse()));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("TECH1", null);
        auth.setAuthenticated(true);

        mockMvc.perform(get("/api/bookings/technician/my")
                        .principal(auth))
                .andExpect(status().isOk());
    }

    /* ===================== UPDATE STATUS ===================== */

    @Test
    void updateStatus_unauthorized() throws Exception {
        mockMvc.perform(put("/api/bookings/BK1/status"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateStatus_missingStatus() throws Exception {

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(true);

        mockMvc.perform(put("/api/bookings/BK1/status")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_blankStatus() throws Exception {

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(true);

        mockMvc.perform(put("/api/bookings/BK1/status")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("status", " ")
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_success() throws Exception {

        Mockito.when(bookingService.updateStatus(Mockito.any(), Mockito.any()))
                .thenReturn(new BookingResponse("BK1", "COMPLETED"));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(true);

        mockMvc.perform(put("/api/bookings/BK1/status")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("status", "COMPLETED")
                        )))
                .andExpect(status().isOk());
    }

    /* ===================== ASSIGN TECHNICIAN ===================== */

    @Test
    void assignTechnician_unauthorized() throws Exception {
        mockMvc.perform(put("/api/bookings/BK1/assign/T1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void assignTechnician_success() throws Exception {

        Mockito.when(bookingService.assignTechnician(Mockito.any(), Mockito.any()))
                .thenReturn(new BookingResponse("BK1", "ASSIGNED"));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("admin", null);
        auth.setAuthenticated(true);

        mockMvc.perform(put("/api/bookings/BK1/assign/T1")
                        .principal(auth))
                .andExpect(status().isOk());
    }
}
