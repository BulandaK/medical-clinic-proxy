package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.MedicalClinicException;
import com.example.medical_clinic_proxy.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class VisitControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    VisitService visitService;

    @Test
    void book_ValidData_ReturnsVisit() throws Exception {
        Long visitId = 1L;
        Long patientId = 2L;
        VisitDto visit = new VisitDto(visitId, null, null, 30L, "Adam Nowak", patientId, "Kamil Bulanda");

        when(visitService.bookVisit(eq(visitId), eq(patientId))).thenReturn(visit);

        mockMvc.perform(post("/visits/{visitId}/patient/{patientId}", visitId, patientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(visitId))
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.doctorFullName").value("Adam Nowak"));
    }
    @Test
    void book_ServiceThrowsException_ReturnsMappedError() throws Exception {
        Long visitId = 999L;
        Long patientId = 1L;

        when(visitService.bookVisit(visitId, patientId))
                .thenThrow(new MedicalClinicException("Visit not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/visits/{visitId}/patient/{patientId}", visitId, patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visit not found"));
    }
}
