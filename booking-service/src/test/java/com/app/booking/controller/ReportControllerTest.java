package com.app.booking.controller;

import com.app.booking.security.AuthFilter;
import com.app.booking.security.JwtUtil;
import com.app.booking.dto.response.BookingStatusReportResponse;
import com.app.booking.dto.response.TechnicianTaskCountResponse;
import com.app.booking.service.ReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private AuthFilter authFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void bookingsByStatus_forbidden_whenAuthNull() throws Exception {
        mockMvc.perform(get("/api/reports/bookings-by-status"))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookingsByStatus_forbidden_whenNotAdmin() throws Exception {

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(
                        "user", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                );
        auth.setAuthenticated(true);

        mockMvc.perform(get("/api/reports/bookings-by-status")
                        .principal(auth))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookingsByStatus_success_whenAdmin() throws Exception {

        Mockito.when(reportService.getBookingsByStatus())
                .thenReturn(List.of(
                        new BookingStatusReportResponse("COMPLETED", 5)
                ));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(
                        "admin", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        auth.setAuthenticated(true);

        mockMvc.perform(get("/api/reports/bookings-by-status")
                        .principal(auth))
                .andExpect(status().isOk());
    }

    @Test
    void technicianTaskCount_success_whenAdmin() throws Exception {

        Mockito.when(reportService.getTechnicianTaskCount())
                .thenReturn(List.of(
                        new TechnicianTaskCountResponse("TECH1", 3)
                ));

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(
                        "admin", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        auth.setAuthenticated(true);

        mockMvc.perform(get("/api/reports/technician-task-count")
                        .principal(auth))
                .andExpect(status().isOk());
    }
}
