package com.fooddelivery.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private Long paymentId;
    private String transactionId;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private String status;
    private String eventType;
    private LocalDateTime timestamp;
}
