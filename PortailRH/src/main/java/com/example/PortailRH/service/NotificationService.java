package com.example.PortailRH.service;
/*
import com.example.PortailRH.entity.Notification;
import com.example.PortailRH.repository.NotificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepo notificationRepo;

    public NotificationService(SimpMessagingTemplate messagingTemplate, NotificationRepo notificationRepo) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepo = notificationRepo;
    }

    public void sendNotification(Long userId, Notification notification) {
        log.info("Sending WS notification to {} with payload {}", userId, notification);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
        log.info("Notification sent to user {}", userId);
        notificationRepo.save(notification);
    }

    public List<Notification> getActiveNotificationsForUser(Long userId) {
        return notificationRepo.findByUserIdAndDeletedFalse(userId);
    }
    public void moveToTrash(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(notification -> {
            notification.setDeleted(true);
            notificationRepo.save(notification);
        });
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteOldNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        log.info("Seuil de suppression : {}", threshold);

        List<Notification> oldNotifications = notificationRepo.findByCreatedAtBefore(threshold);
        log.info("Notifications à supprimer : {}", oldNotifications);

        if (!oldNotifications.isEmpty()) {
            notificationRepo.deleteAll(oldNotifications);
            log.info("Suppression de {} notifications.", oldNotifications.size());
        } else {
            log.info("Aucune notification à supprimer.");
        }
    }


}
*/