package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.ConflictException;
import com.example.medical_clinic_proxy.exception.NotFoundException;
import com.example.medical_clinic_proxy.exception.ServiceUnavailableException;
import com.example.medical_clinic_proxy.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    PatientService patientService;

    @Test
    void getVisits_DataCorrect_ReturnsListVisitDto() throws Exception {
        Long patientId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, 10L, "Dr traphouse", patientId, "Jan Kowalski");
        List<VisitDto> visits = List.of(visit);

        when(patientService.getVisits(any())).thenReturn(visits);
        mockMvc.perform(get("/patient/{id}/my-visits", patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctorFullName").value("Dr traphouse"))
                .andExpect(jsonPath("$[0].patientFullName").value("Jan Kowalski"));
    }

    @Test
    void getVisits_PatientNotFound_ReturnsNotFound() throws Exception {
        Long patientId = 99L;

        when(patientService.getVisits(patientId))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        mockMvc.perform(get("/patient/{id}/my-visits", patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found (Doctor/Visit)"))
                .andExpect(jsonPath("$.path").value("/patient/99/my-visits"));
    }

    @Test
    void bookVisit_DataCorrect_ReturnsBookedVisit() throws Exception {
        Long visitId = 1L;
        Long patientId = 1L;
        VisitDto booked = new VisitDto(visitId, null, null, 10L, "Dr traphouse", patientId, "Jan Kowalski");

        when(patientService.bookVisit(visitId, patientId)).thenReturn(booked);

        mockMvc.perform(post("/patient/visits/{visitId}/book/{patientId}", visitId, patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(visitId))
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.patientFullName").value("Jan Kowalski"));
    }

    @Test
    void bookVisit_VisitAlreadyAssigned_ReturnsConflict() throws Exception {
        Long visitId = 1L;
        Long patientId = 1L;

        when(patientService.bookVisit(visitId, patientId))
                .thenThrow(new ConflictException("Visit already assigned"));

        mockMvc.perform(post("/patient/visits/{visitId}/book/{patientId}", visitId, patientId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Visit already assigned"))
                .andExpect(jsonPath("$.path").value("/patient/visits/1/book/1"));
    }

    @Test
    void bookVisit_VisitNotFound_ReturnsNotFound() throws Exception {
        Long visitId = 99L;
        Long patientId = 1L;

        when(patientService.bookVisit(visitId, patientId))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        mockMvc.perform(post("/patient/visits/{visitId}/book/{patientId}", visitId, patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found (Doctor/Visit)"))
                .andExpect(jsonPath("$.path").value("/patient/visits/99/book/1"));
    }

    @Test
    void getDoctorAvailableVisits_DataCorrect_ReturnsPageResponse() throws Exception {
        Long doctorId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, doctorId, "Dr traphouse", null, null);
        PageResponse<VisitDto> pageResponse = new PageResponse<>(List.of(visit), 1, 1, 1L, 1);

        when(patientService.getAvailableVisitsByDoctorId(any(), eq(doctorId))).thenReturn(pageResponse);

        mockMvc.perform(get("/patient/doctors/{doctorId}/visits", doctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].doctorId").value(doctorId))
                .andExpect(jsonPath("$.content[0].doctorFullName").value("Dr traphouse"));
    }

    @Test
    void getDoctorAvailableVisits_ServiceUnavailable_ReturnsServiceUnavailable() throws Exception {
        Long doctorId = 1L;

        when(patientService.getAvailableVisitsByDoctorId(any(), eq(doctorId)))
                .thenThrow(new ServiceUnavailableException("Service not available right now"));

        mockMvc.perform(get("/patient/doctors/{doctorId}/visits", doctorId))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Service not available right now"))
                .andExpect(jsonPath("$.path").value("/patient/doctors/1/visits"));
    }

    @Test
    void getAllDoctorsWithSpecialization_NotFound_ReturnsNotFound() throws Exception {
        String specialization = "nonexisting";

        when(patientService.getAllDoctorsWithSpecialization(specialization))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        mockMvc.perform(get("/patient/doctors/specialization/{specialization}", specialization))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found (Doctor/Visit)"))
                .andExpect(jsonPath("$.path").value("/patient/doctors/specialization/nonexisting"));
    }

    @Test
    void getAllDoctorsWithSpecialization_DataCorrect_ReturnsDoctorList() throws Exception {
        String specialization = "Kardiolog";
        DoctorDto doctor = new DoctorDto(1L, specialization, "adam.nowak@clinic.com", "Adam", "Nowak");

        when(patientService.getAllDoctorsWithSpecialization(specialization)).thenReturn(List.of(doctor));

        mockMvc.perform(get("/patient/doctors/specialization/{specialization}", specialization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Adam"))
                .andExpect(jsonPath("$[0].lastName").value("Nowak"))
                .andExpect(jsonPath("$[0].specialization").value(specialization));
    }

    @Test
    void getVisitsBySpecializationAndDate_DataCorrect_ReturnsPageResponse() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 31, 18, 0);
        String specialization = "Kardiolog";
        VisitDto visit = new VisitDto(1L, start, end, 1L, "Dr traphouse", null, null);
        PageResponse<VisitDto> pageResponse = new PageResponse<>(List.of(visit), 1, 1, 1L, 0);

        when(patientService.getAvailableVisitsBySpecializationAndDate(any(), eq(start), eq(end), eq(specialization)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/patient/available/visits")
                        .param("specialization", specialization)
                        .param("startRange", "2025-01-01T08:00:00")
                        .param("endRange", "2025-01-31T18:00:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].doctorFullName").value("Dr traphouse"));
    }

    @Test
    void getVisitsBySpecializationAndDate_ServiceUnavailable_ReturnsServiceUnavailable() throws Exception {
        when(patientService.getAvailableVisitsBySpecializationAndDate(any(), any(), any(), any()))
                .thenThrow(new ServiceUnavailableException("Service not available right now"));

        mockMvc.perform(get("/patient/available/visits"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Service not available right now"))
                .andExpect(jsonPath("$.path").value("/patient/available/visits"));
    }

    @Test
    void getVisitsBySpecializationAndDate_NotFound_ReturnsNotFound() throws Exception {
        when(patientService.getAvailableVisitsBySpecializationAndDate(any(), any(), any(), any()))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        mockMvc.perform(get("/patient/available/visits"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found (Doctor/Visit)"))
                .andExpect(jsonPath("$.path").value("/patient/available/visits"));
    }
}
