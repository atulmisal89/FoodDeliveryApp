package com.fooddelivery.notificationservice.service;

import com.fooddelivery.notificationservice.dto.SendNotificationDto;
import com.fooddelivery.notificationservice.entity.NotificationType;
import com.fooddelivery.notificationservice.event.OrderEvent;
import com.fooddelivery.notificationservice.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group", 
                   properties = {"spring.json.value.default.type=com.fooddelivery.notificationservice.event.OrderEvent"})
    public void handleOrderEvent(OrderEvent event) {
        try {
            log.info("Received order event: {} for order {}", event.getEventType(), event.getOrderId());

            switch (event.getEventType()) {
                case "ORDER_CREATED":
                    sendOrderCreatedEmail(event);
                    break;
                case "DELIVERY_ASSIGNED":
                    sendDeliveryAssignedEmail(event);
                    break;
                case "ORDER_STATUS_UPDATED":
                    if ("DELIVERED".equals(event.getStatus())) {
                        sendOrderDeliveredEmail(event);
                    }
                    break;
                default:
                    log.debug("Unhandled order event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process order event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service-group",
                   properties = {"spring.json.value.default.type=com.fooddelivery.notificationservice.event.PaymentEvent"})
    public void handlePaymentEvent(PaymentEvent event) {
        try {
            log.info("Received payment event: {} for order {}", event.getEventType(), event.getOrderId());

            switch (event.getEventType()) {
                case "PAYMENT_SUCCESS":
                    sendPaymentSuccessEmail(event);
                    break;
                case "PAYMENT_FAILED":
                    sendPaymentFailedEmail(event);
                    break;
                case "PAYMENT_REFUNDED":
                    sendPaymentRefundedEmail(event);
                    break;
                default:
                    log.debug("Unhandled payment event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process payment event: {}", e.getMessage(), e);
        }
    }

    private void sendOrderCreatedEmail(OrderEvent event) {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(event.getCustomerId());
        dto.setTitle("Order Confirmed!");
        dto.setMessage(String.format("Your order %s has been received and is being processed. We'll notify you once it's ready!",
                event.getOrderNumber()));
        dto.setType(NotificationType.EMAIL);
        dto.setRecipientEmail(event.getCustomerEmail());
        dto.setRelatedOrderId(event.getOrderId());

        notificationService.sendNotification(dto);
        log.info("Order created email sent to {} for order {}", event.getCustomerEmail(), event.getOrderNumber());
    }

    private void sendDeliveryAssignedEmail(OrderEvent event) {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(event.getCustomerId());
        dto.setTitle("Delivery on the Way!");
        dto.setMessage(String.format("Good news! Your order %s is out for delivery. Our delivery partner will reach you soon.",
                event.getOrderNumber()));
        dto.setType(NotificationType.EMAIL);
        dto.setRecipientEmail(event.getCustomerEmail());
        dto.setRelatedOrderId(event.getOrderId());

        notificationService.sendNotification(dto);
        log.info("Delivery assigned email sent to {} for order {}", event.getCustomerEmail(), event.getOrderNumber());
    }

    private void sendOrderDeliveredEmail(OrderEvent event) {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(event.getCustomerId());
        dto.setTitle("Order Delivered!");
        dto.setMessage(String.format("Your order %s has been delivered. Enjoy your meal! We hope to see you again soon.",
                event.getOrderNumber()));
        dto.setType(NotificationType.EMAIL);
        dto.setRecipientEmail(event.getCustomerEmail());
        dto.setRelatedOrderId(event.getOrderId());

        notificationService.sendNotification(dto);
        log.info("Order delivered email sent to {} for order {}", event.getCustomerEmail(), event.getOrderNumber());
    }

    private void sendPaymentSuccessEmail(PaymentEvent event) {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(event.getCustomerId());
        dto.setTitle("Payment Successful!");
        dto.setMessage(String.format("Your payment of Rs. %.2f for order has been received. Transaction ID: %s",
                event.getAmount(), event.getTransactionId()));
        dto.setType(NotificationType.EMAIL);
        dto.setRecipientEmail(event.getCustomerEmail());
        dto.setRelatedOrderId(event.getOrderId());

        notificationService.sendNotification(dto);
        log.info("Payment success email sent to {} for transaction {}", event.getCustomerEmail(), event.getTransactionId());
    }

    private void sendPaymentFailedEmail(PaymentEvent event) {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(event.getCustomerId());
        dto.setTitle("Payment Failed");
        dto.setMessage(String.format("We couldn't process your payment of Rs. %.2f. Please try again or use a different payment method.",
                event.getAmount()));
        dto.setType(NotificationType.EMAIL);
        dto.setRecipientEmail(event.getCustomerEmail());
        dto.setRelatedOrderId(event.getOrderId());

        notificationService.sendNotification(dto);
        log.info("Payment failed email sent to {} for order {}", event.getCustomerEmail(), event.getOrderId());
    }

    private void sendPaymentRefundedEmail(PaymentEvent event) {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(event.getCustomerId());
        dto.setTitle("Refund Processed");
        dto.setMessage(String.format("Your refund of Rs. %.2f has been processed. The amount will be credited to your account within 5-7 business days.",
                event.getAmount()));
        dto.setType(NotificationType.EMAIL);
        dto.setRecipientEmail(event.getCustomerEmail());
        dto.setRelatedOrderId(event.getOrderId());

        notificationService.sendNotification(dto);
        log.info("Refund email sent to {} for transaction {}", event.getCustomerEmail(), event.getTransactionId());
    }
}
