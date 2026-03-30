package com.fooddelivery.deliveryservice.dto;

import com.fooddelivery.deliveryservice.entity.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusUpdateDto {
    @NotNull(message = "Status is required")
    private DeliveryStatus status;
    
    private String cancellationReason;
    private Double currentLatitude;
    private Double currentLongitude;
    private String deliveryOtp;
    private String customerSignature;
    private String deliveryProofUrl;
}
