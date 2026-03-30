package com.fooddelivery.notificationservice.dto;

import com.fooddelivery.notificationservice.entity.NotificationStatus;
import com.fooddelivery.notificationservice.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private String recipientEmail;
    private String recipientPhone;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private String failureReason;
    private LocalDateTime createdAt;
    private Long relatedOrderId;
    private Long relatedDeliveryId;
}
