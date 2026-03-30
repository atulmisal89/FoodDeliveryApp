package com.fooddelivery.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String deliveryId;
    
    @Column(nullable = false)
    private Long orderId;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private Long restaurantId;
    
    @Column(nullable = false)
    private Long deliveryPersonId;
    
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String vehicleNumber;
    
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;
    
    @Embedded
    private PickupLocation pickupLocation;
    
    @Embedded
    private DeliveryLocation deliveryLocation;
    
    private Double currentLatitude;
    private Double currentLongitude;
    
    private LocalDateTime assignedTime;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveredTime;
    
    private LocalDateTime estimatedDeliveryTime;
    
    private Double distance; // in kilometers
    private Double deliveryFee;
    
    private String deliveryOtp;
    private boolean otpVerified = false;
    
    private String customerSignature;
    private String deliveryProofUrl;
    
    private String notes;
    private String cancellationReason;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
