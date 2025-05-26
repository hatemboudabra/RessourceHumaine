package com.notification.Notification_Serivce.service;

import com.notification.Notification_Serivce.entity.Notification;
import com.notification.Notification_Serivce.entity.dto.NotificationResponse;
import com.notification.Notification_Serivce.repo.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepo notificationRepo;
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendNotification(Long userId, Notification notification) {
        Notification saved = notificationRepo.save(notification);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                toResponse(saved)
        );
        log.info("Notification sent to user {}: {}", userId, notification.getMessage());

    }

    public List<NotificationResponse> getActiveNotificationsForUser(Long userId) {
        return notificationRepo.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .createdAt(LocalDateTime.parse(n.getCreatedAt().toString()))
                .build();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<Notification> old = notificationRepo.findByCreatedAtBefore(cutoff);
        if (!old.isEmpty()) {
            notificationRepo.deleteAll(old);
        }
    }
}



