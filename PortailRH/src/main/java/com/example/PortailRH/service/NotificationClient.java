package com.example.PortailRH.service;

import com.example.PortailRH.entity.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "notification-service", url = "http://localhost:9093")
public interface NotificationClient {
    @PostMapping("/api/notifications/{userId}")
    void sendNotification(@PathVariable("userId") Long userId, @RequestBody NotificationRequest request);
}
