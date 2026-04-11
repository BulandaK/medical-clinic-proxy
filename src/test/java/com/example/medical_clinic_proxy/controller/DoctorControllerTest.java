package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.NotFoundException;
import com.example.medical_clinic_proxy.exception.ServiceUnavailableException;
import com.example.medical_clinic_proxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getDoctorVisits_DataCorrect_ReturnsPageResponse() throws Exception {
        Long doctorId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, doctorId, "Adam Nowak", null, null);
        PageResponse<VisitDto> pageResponse = new PageResponse<>(List.of(visit), 1, 1, 1L, 0);

        when(doctorService.getAllVisits(any(), eq(doctorId))).thenReturn(pageResponse);

        mockMvc.perform(get("/doctor/{doctorId}/visits", doctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].doctorId").value(doctorId))
                .andExpect(jsonPath("$.content[0].doctorFullName").value("Adam Nowak"));
    }

    @Test
    void getDoctorVisits_NoVisits_ReturnsEmptyPageResponse() throws Exception {
        Long doctorId = 1L;
        PageResponse<VisitDto> pageResponse = new PageResponse<>(List.of(), 0, 0, 1L, 0);

        when(doctorService.getAllVisits(any(), eq(doctorId))).thenReturn(pageResponse);

        mockMvc.perform(get("/doctor/{doctorId}/visits", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getDoctorVisits_DoctorNotFound_ReturnsNotFound() throws Exception {
        Long doctorId = 99L;

        when(doctorService.getAllVisits(any(), eq(doctorId)))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        mockMvc.perform(get("/doctor/{doctorId}/visits", doctorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found (Doctor/Visit)"))
                .andExpect(jsonPath("$.path").value("/doctor/99/visits"));
    }

    @Test
    void getDoctorVisits_ServiceUnavailable_ReturnsServiceUnavailable() throws Exception {
        Long doctorId = 1L;

        when(doctorService.getAllVisits(any(), eq(doctorId)))
                .thenThrow(new ServiceUnavailableException("Service not available right now"));

        mockMvc.perform(get("/doctor/{doctorId}/visits", doctorId))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Service not available right now"))
                .andExpect(jsonPath("$.path").value("/doctor/1/visits"));
    }

    @Test
    void deleteVisit_DataCorrect_ReturnsNoContent() throws Exception {
        Long visitId = 1L;

        doNothing().when(doctorService).deleteVisit(visitId);

        mockMvc.perform(delete("/doctor/visits/{visitId}", visitId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteVisit_VisitNotFound_ReturnsNotFound() throws Exception {
        Long visitId = 99L;

        doThrow(new NotFoundException("Resource not found (Doctor/Visit)"))
                .when(doctorService).deleteVisit(visitId);

        mockMvc.perform(delete("/doctor/visits/{visitId}", visitId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found (Doctor/Visit)"))
                .andExpect(jsonPath("$.path").value("/doctor/visits/99"));
    }

    @Test
    void deleteVisit_ServiceUnavailable_ReturnsServiceUnavailable() throws Exception {
        Long visitId = 1L;

        doThrow(new ServiceUnavailableException("Service not available right now"))
                .when(doctorService).deleteVisit(visitId);

        mockMvc.perform(delete("/doctor/visits/{visitId}", visitId))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Service not available right now"))
                .andExpect(jsonPath("$.path").value("/doctor/visits/1"));
    }
}