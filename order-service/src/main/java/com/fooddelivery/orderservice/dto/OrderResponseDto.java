package com.fooddelivery.orderservice.dto;

import com.fooddelivery.orderservice.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private List<OrderItemDto> items;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String paymentId;
    private DeliveryAddress deliveryAddress;
    private Double subtotal;
    private Double deliveryFee;
    private Double tax;
    private Double discount;
    private Double totalAmount;
    private String specialInstructions;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
