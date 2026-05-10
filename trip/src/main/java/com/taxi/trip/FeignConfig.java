package com.taxi.trip;

import com.taxi.trip.security.ServiceJwtService;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    private final ServiceJwtService serviceJwtService;

    public FeignConfig(ServiceJwtService serviceJwtService) {
        this.serviceJwtService = serviceJwtService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String serviceToken = serviceJwtService.getCurrentToken();
            requestTemplate.header("Authorization", "Bearer " + serviceToken);
        };
    }
}