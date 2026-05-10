package com.taxi.trip.service;

import com.taxi.trip.entity.dto.NotificationRequestDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification-service.url}")
public interface NotificationServiceClient {
    @PostMapping("/notifications")
    void createNotification(
            @RequestBody NotificationRequestDto request);
}
