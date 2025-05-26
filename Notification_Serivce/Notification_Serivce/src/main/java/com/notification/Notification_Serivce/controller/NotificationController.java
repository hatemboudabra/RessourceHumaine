package com.notification.Notification_Serivce.controller;

import com.notification.Notification_Serivce.entity.Notification;
import com.notification.Notification_Serivce.entity.dto.NotificationRequest;
import com.notification.Notification_Serivce.entity.dto.NotificationResponse;
import com.notification.Notification_Serivce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> sendNotification(
            @PathVariable Long userId,
            @RequestBody NotificationRequest request
    ) {
        Notification notif = Notification.builder()
                .userId(userId)
                .type(request.getType())
                .message(request.getMessage())
                .build();
        notificationService.sendNotification(userId, notif);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                notificationService.getActiveNotificationsForUser(userId)
        );
    }
}
