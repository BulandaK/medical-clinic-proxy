package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.MedicalClinicException;
import com.example.medical_clinic_proxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    DoctorService doctorService;

    @Test
    void getDoctorAvailableVisits_ValidRequest_ReturnsPage() throws Exception {
        Long doctorId = 10L;
        PageResponse<VisitDto> mockResponse = new PageResponse<>(List.of(), 0, 10, 0L, 0);

        when(doctorService.getAvailableVisitsByDoctorId(any(), eq(doctorId)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/doctors/{doctorId}/visits", doctorId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalPages").exists());

    }

    @Test
    void getVisitsBySpecializationAndDate_ValidParams_ReturnsPage() throws Exception {
        String spec = "Cardiology";
        String dateStr = "2026-05-20";
        LocalDate date = LocalDate.parse(dateStr);
        PageResponse<VisitDto> mockResponse = new PageResponse<>(List.of(), 0, 10, 0L, 0);

        when(doctorService.getAvailableVisitsBySpecializationAndDate(any(), eq(date), eq(spec)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/doctors/visits")
                        .param("specialization", spec)
                        .param("date", dateStr)
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getVisitsBySpecializationAndDate_InvalidDateFormat_Returns400() throws Exception {
        mockMvc.perform(get("/doctors/visits")
                        .param("specialization", "Cardiology")
                        .param("date", "20-05-2026"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void getVisitsBySpecializationAndDate_MissingParam_Returns400() throws Exception {
        mockMvc.perform(get("/doctors/visits")
                        .param("date", "2026-05-20")) // Brak 'specialization'
                .andExpect(status().isBadRequest());
    }
    @Test
    void getDoctorAvailableVisits_ServiceThrowsException_ReturnsMappedError() throws Exception {
        // GIVEN
        Long doctorId = 1L;
        when(doctorService.getAvailableVisitsByDoctorId(any(), eq(doctorId)))
                .thenThrow(new MedicalClinicException("Doctor not found", HttpStatus.NOT_FOUND));

        // WHEN & THEN
        mockMvc.perform(get("/doctors/{doctorId}/visits", doctorId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
    }
}
