package com.notification.Notification_Serivce.repo;

import com.notification.Notification_Serivce.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndDeletedFalse(Long userId);
    List<Notification> findByCreatedAtBefore(LocalDateTime threshold);
}
