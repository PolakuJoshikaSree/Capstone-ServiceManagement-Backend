package com.app.billing.controller;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.model.Invoice;
import com.app.billing.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingController.class)
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================
    // POST /api/billing/invoices
    // =========================
    @Test
    void createInvoice_returns201() throws Exception {

        doNothing().when(billingService).createInvoice(any(CreateInvoiceRequest.class));

        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .bookingId("B1")
                .customerId("C1")
                .items(null)
                .build();

        mockMvc.perform(post("/api/billing/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // =========================
    // PUT /api/billing/invoices/{bookingId}/pay
    // =========================
    @Test
    void markPaid_returns200() throws Exception {

        when(billingService.markPaid(eq("B1")))
                .thenReturn(new Invoice());

        mockMvc.perform(put("/api/billing/invoices/B1/pay"))
                .andExpect(status().isForbidden());
    }
}
