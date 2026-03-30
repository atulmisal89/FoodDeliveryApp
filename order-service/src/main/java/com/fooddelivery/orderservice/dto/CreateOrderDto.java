package com.fooddelivery.orderservice.dto;

import com.fooddelivery.orderservice.entity.DeliveryAddress;
import com.fooddelivery.orderservice.entity.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    
    private String restaurantName;
    
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDto> items;
    
    @NotNull(message = "Delivery address is required")
    private DeliveryAddress deliveryAddress;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private String specialInstructions;
    
    private Double discount;
}
