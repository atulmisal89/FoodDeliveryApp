package com.fooddelivery.deliveryservice.dto;

import com.fooddelivery.deliveryservice.entity.DeliveryLocation;
import com.fooddelivery.deliveryservice.entity.DeliveryStatus;
import com.fooddelivery.deliveryservice.entity.PickupLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponseDto {
    private Long id;
    private String deliveryId;
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String vehicleNumber;
    private DeliveryStatus status;
    private PickupLocation pickupLocation;
    private DeliveryLocation deliveryLocation;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime assignedTime;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime estimatedDeliveryTime;
    private Double distance;
    private Double deliveryFee;
    private String deliveryOtp;
    private boolean otpVerified;
    private String customerSignature;
    private String deliveryProofUrl;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
