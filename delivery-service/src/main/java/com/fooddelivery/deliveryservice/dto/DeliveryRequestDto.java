package com.fooddelivery.deliveryservice.dto;

import com.fooddelivery.deliveryservice.entity.DeliveryLocation;
import com.fooddelivery.deliveryservice.entity.PickupLocation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    
    @NotNull(message = "Pickup location is required")
    private PickupLocation pickupLocation;
    
    @NotNull(message = "Delivery location is required")
    private DeliveryLocation deliveryLocation;
    
    private Double deliveryFee;
    
    private String notes;
}
