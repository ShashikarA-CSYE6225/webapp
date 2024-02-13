package com.csye6225.webapp.controller;

import com.csye6225.webapp.service.DatabaseHealthCheckService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HealthCheckController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class HealthCheckControllerTest {

    @MockBean
    DatabaseHealthCheckService databaseHealthCheckService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void healthCheckIfDatabaseConnectionIsSuccessful() throws Exception {
        //Given
        when(databaseHealthCheckService.checkDatabaseConnection()).thenReturn(true);

        //When
        //Then
        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"));
    }

    @Test
    void healthCheckIfDatabaseConnectionIsFailure() throws Exception {
        //Given
        when(databaseHealthCheckService.checkDatabaseConnection()).thenReturn(false);

        //When
        //Then
        mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(content().string(""));
    }

    @Test
    void healthCheckIfRequestBodyIsGiven() throws Exception {
        //When
        //Then
        mockMvc.perform(get("/healthz")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(content().string(""));
    }

    @Test
    void healthCheckIfWrongMethodIsGiven() throws Exception {
        //When
        //Then
        mockMvc.perform(put("/healthz"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(content().string(""));
    }

    @Test
    void healthCheckIfQueryParametersAreGiven() throws Exception {
        //When
        //Then
        mockMvc.perform(get("/healthz")
                        .param("name","Shashi"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(content().string(""));
    }

    @Test
    void healthCheckIfUrlNotFound() throws Exception {
        //When
        //Then
        mockMvc.perform(get("/healthzz"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(content().string(""));
    }

    @Test
    void healthCheckForHEADEndpoint() throws Exception {
        //When
        //Then
        mockMvc.perform(head("/healthz"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Cache-Control", "no-cache, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(content().string(""));
    }

}