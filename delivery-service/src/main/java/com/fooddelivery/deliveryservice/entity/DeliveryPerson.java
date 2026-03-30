package com.fooddelivery.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId; // Reference to User Service
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    private DeliveryPersonStatus status = DeliveryPersonStatus.OFFLINE;
    
    private String vehicleType;
    private String vehicleNumber;
    private String licenseNumber;
    
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
    
    private boolean isAvailable = false;
    private Integer activeDeliveries = 0;
    private Integer completedDeliveries = 0;
    
    private Double rating = 0.0;
    private Integer totalRatings = 0;
    
    private String profileImageUrl;
    
    @Column(length = 1000)
    private String documents; // JSON string of document URLs
    
    private boolean isVerified = false;
    private LocalDateTime verifiedAt;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
