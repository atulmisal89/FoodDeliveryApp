package com.fooddelivery.notificationservice.controller;

import com.fooddelivery.notificationservice.dto.NotificationDto;
import com.fooddelivery.notificationservice.dto.SendNotificationDto;
import com.fooddelivery.notificationservice.entity.NotificationStatus;
import com.fooddelivery.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send a notification")
    public ResponseEntity<NotificationDto> sendNotification(@Valid @RequestBody SendNotificationDto sendNotificationDto) {
        NotificationDto notification = notificationService.sendNotification(sendNotificationDto);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable Long notificationId) {
        NotificationDto notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID")
    public ResponseEntity<List<NotificationDto>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications by user ID")
    public ResponseEntity<List<NotificationDto>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status")
    public ResponseEntity<List<NotificationDto>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        List<NotificationDto> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get notifications by order ID")
    public ResponseEntity<List<NotificationDto>> getNotificationsByOrderId(@PathVariable Long orderId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByOrderId(orderId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/delivery/{deliveryId}")
    @Operation(summary = "Get notifications by delivery ID")
    public ResponseEntity<List<NotificationDto>> getNotificationsByDeliveryId(@PathVariable Long deliveryId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByDeliveryId(deliveryId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get notifications in date range")
    public ResponseEntity<List<NotificationDto>> getNotificationsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<NotificationDto> notifications = notificationService.getNotificationsInDateRange(startDate, endDate);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/mark-delivered")
    @Operation(summary = "Mark notification as delivered")
    public ResponseEntity<NotificationDto> markAsDelivered(@PathVariable Long notificationId) {
        NotificationDto notification = notificationService.markAsDelivered(notificationId);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/retry-failed")
    @Operation(summary = "Retry sending failed notifications")
    public ResponseEntity<String> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok("Retry process started");
    }
}
