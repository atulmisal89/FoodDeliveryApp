package com.fooddelivery.notificationservice.service;

import com.fooddelivery.notificationservice.dto.NotificationDto;
import com.fooddelivery.notificationservice.dto.SendNotificationDto;
import com.fooddelivery.notificationservice.entity.Notification;
import com.fooddelivery.notificationservice.entity.NotificationStatus;
import com.fooddelivery.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationDto sendNotification(SendNotificationDto sendNotificationDto) {
        Notification notification = new Notification();
        notification.setUserId(sendNotificationDto.getUserId());
        notification.setTitle(sendNotificationDto.getTitle());
        notification.setMessage(sendNotificationDto.getMessage());
        notification.setType(sendNotificationDto.getType());
        notification.setRecipientEmail(sendNotificationDto.getRecipientEmail());
        notification.setRecipientPhone(sendNotificationDto.getRecipientPhone());
        notification.setRelatedOrderId(sendNotificationDto.getRelatedOrderId());
        notification.setRelatedDeliveryId(sendNotificationDto.getRelatedDeliveryId());
        notification.setStatus(NotificationStatus.PENDING);

        try {
            sendEmail(sendNotificationDto.getRecipientEmail(), sendNotificationDto.getTitle(), sendNotificationDto.getMessage());
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to {}", sendNotificationDto.getRecipientEmail());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            log.error("Failed to send email to {}: {}", sendNotificationDto.getRecipientEmail(), e.getMessage());
        }

        Notification savedNotification = notificationRepository.save(notification);
        return mapToDto(savedNotification);
    }

    public NotificationDto getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        return mapToDto(notification);
    }

    public List<NotificationDto> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsByOrderId(Long orderId) {
        return notificationRepository.findByRelatedOrderId(orderId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsByDeliveryId(Long deliveryId) {
        return notificationRepository.findByRelatedDeliveryId(deliveryId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public NotificationDto markAsDelivered(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notification.setStatus(NotificationStatus.DELIVERED);
        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDto(updatedNotification);
    }

    public void retryFailedNotifications() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Notification> failedNotifications = notificationRepository.findByStatusAndCreatedAtBefore(NotificationStatus.FAILED, oneHourAgo);

        for (Notification notification : failedNotifications) {
            try {
                sendEmail(notification.getRecipientEmail(), notification.getTitle(), notification.getMessage());
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                log.info("Retry successful for notification {}", notification.getId());
            } catch (Exception e) {
                log.error("Retry failed for notification {}: {}", notification.getId(), e.getMessage());
            }
        }
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@fooddelivery.com");
        mailSender.send(message);
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .recipientEmail(notification.getRecipientEmail())
                .recipientPhone(notification.getRecipientPhone())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .failureReason(notification.getFailureReason())
                .createdAt(notification.getCreatedAt())
                .relatedOrderId(notification.getRelatedOrderId())
                .relatedDeliveryId(notification.getRelatedDeliveryId())
                .build();
    }
}
