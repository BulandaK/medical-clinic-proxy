package com.example.medical_clinic_proxy.client;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.hc5.ApacheHttp5Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MedicalClinicConfig {
    @Bean
    public feign.Client feignClient() {
        return new ApacheHttp5Client();
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, 1000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new MedicalClinicErrorDecoder();
    }
}
