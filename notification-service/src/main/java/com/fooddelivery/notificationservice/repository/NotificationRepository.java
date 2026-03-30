package com.fooddelivery.notificationservice.repository;

import com.fooddelivery.notificationservice.entity.Notification;
import com.fooddelivery.notificationservice.entity.NotificationStatus;
import com.fooddelivery.notificationservice.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByRelatedOrderId(Long orderId);

    List<Notification> findByRelatedDeliveryId(Long deliveryId);

    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Notification> findByStatusAndCreatedAtBefore(NotificationStatus status, LocalDateTime dateTime);
}
