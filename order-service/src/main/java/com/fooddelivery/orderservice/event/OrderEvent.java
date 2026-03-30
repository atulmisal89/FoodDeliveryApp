package com.fooddelivery.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
