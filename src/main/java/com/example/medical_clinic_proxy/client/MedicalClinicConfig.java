package com.example.medical_clinic_proxy.client;

import feign.hc5.ApacheHttp5Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MedicalClinicConfig {
    @Bean
    public feign.Client feignClient() {
        return new ApacheHttp5Client();
    }
}
