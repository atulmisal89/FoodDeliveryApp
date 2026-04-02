package com.fooddelivery.paymentservice.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderEvent {
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private Long restaurantId;
    private String status;
    private String eventType;
    private LocalDateTime timestamp;
}
