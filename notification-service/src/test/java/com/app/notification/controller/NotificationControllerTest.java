package com.app.notification.controller;

import com.app.notification.model.Notification;
import com.app.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNotification_shouldReturnOk() throws Exception {

        Notification notification = Notification.builder()
                .id("1")
                .build();

        when(notificationService.create(any()))
                .thenReturn(notification);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId":"U1",
                      "role":"CUSTOMER",
                      "title":"Test",
                      "message":"Hello",
                      "type":"BOOKING_CREATED"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getForUser_shouldReturnList() throws Exception {

        when(notificationService.getForUser("U1"))
                .thenReturn(List.of(new Notification()));

        mockMvc.perform(get("/api/notifications/user/U1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void markAsRead_shouldReturnOk() throws Exception {

        doNothing().when(notificationService).markAsRead("1");

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());
    }
}
