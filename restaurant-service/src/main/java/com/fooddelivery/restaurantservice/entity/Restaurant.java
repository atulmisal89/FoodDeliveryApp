package com.fooddelivery.restaurantservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Long ownerId; // Reference to User Service
    
    @Column(nullable = false)
    private String phoneNumber;
    
    @Column(nullable = false)
    private String email;
    
    @Embedded
    private RestaurantAddress address;
    
    @ElementCollection
    @CollectionTable(name = "restaurant_cuisines")
    private List<String> cuisineTypes = new ArrayList<>();
    
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    @Enumerated(EnumType.STRING)
    private RestaurantStatus status = RestaurantStatus.INACTIVE;
    
    private Double rating = 0.0;
    private Integer totalRatings = 0;
    
    private Double minimumOrderAmount = 0.0;
    private Double deliveryFee = 0.0;
    private Integer estimatedDeliveryTime = 30; // in minutes
    
    private String imageUrl;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MenuItem> menuItems = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
