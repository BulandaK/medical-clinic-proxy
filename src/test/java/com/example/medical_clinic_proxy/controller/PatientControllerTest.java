package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        VisitDto visit = new VisitDto(1L,null,null,10L,"Dr traphouse",patientId,"Jan Kowalski");
        List<VisitDto> visits = List.of(visit);

        when(patientService.getVisits(any())).thenReturn(visits);
        mockMvc.perform(get("/patient/{id}/visits", patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctorFullName").value("Dr traphouse"))
                .andExpect(jsonPath("$[0].patientFullName").value("Jan Kowalski"));
    }
    @Test
    void getVisits_NoVisits_ReturnsEmptyList() throws Exception {
        Long patientId = 99L;
        when(patientService.getVisits(patientId)).thenReturn(List.of());

        mockMvc.perform(get("/patient/{id}/visits", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
