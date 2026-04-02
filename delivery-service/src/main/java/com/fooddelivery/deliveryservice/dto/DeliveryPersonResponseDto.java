package com.fooddelivery.deliveryservice.dto;

import com.fooddelivery.deliveryservice.entity.DeliveryPersonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPersonResponseDto {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private DeliveryPersonStatus status;
    private String vehicleType;
    private String vehicleNumber;
    private String licenseNumber;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
    private boolean isAvailable;
    private Integer activeDeliveries;
    private Integer completedDeliveries;
    private Double rating;
    private Integer totalRatings;
    private String profileImageUrl;
    private String documents;
    private boolean isVerified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
