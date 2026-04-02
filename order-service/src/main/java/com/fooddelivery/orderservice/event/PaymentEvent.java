package com.fooddelivery.orderservice.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
