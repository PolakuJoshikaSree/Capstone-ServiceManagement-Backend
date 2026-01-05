package com.app.billing.controller;

import com.app.billing.dto.CreateInvoiceRequest;
import com.app.billing.model.Invoice;
import com.app.billing.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BillingService billingService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createInvoice_success() throws Exception {

        CreateInvoiceRequest request =
                CreateInvoiceRequest.builder().bookingId("BK1").build();

        mockMvc.perform(post("/api/billing/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void markPaid_success() throws Exception {

        Mockito.when(billingService.markPaid("BK1"))
                .thenReturn(new Invoice());

        mockMvc.perform(put("/api/billing/invoices/BK1/pay"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllInvoices() throws Exception {

        Mockito.when(billingService.getAllInvoices())
                .thenReturn(List.of(new Invoice()));

        mockMvc.perform(get("/api/billing/invoices"))
                .andExpect(status().isOk());
    }

    @Test
    void getInvoicesByCustomer() throws Exception {

        Mockito.when(billingService.getInvoicesByCustomer("C1"))
                .thenReturn(List.of(new Invoice()));

        mockMvc.perform(get("/api/billing/invoices/customer/C1"))
                .andExpect(status().isOk());
    }
}
