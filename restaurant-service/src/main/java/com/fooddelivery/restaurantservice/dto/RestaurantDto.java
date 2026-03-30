package com.fooddelivery.restaurantservice.dto;

import com.fooddelivery.restaurantservice.entity.RestaurantAddress;
import com.fooddelivery.restaurantservice.entity.RestaurantStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDto {
    private Long id;
    
    @NotBlank(message = "Restaurant name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "Address is required")
    private RestaurantAddress address;
    
    private List<String> cuisineTypes;
    
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    private RestaurantStatus status;
    
    private Double rating;
    private Integer totalRatings;
    
    private Double minimumOrderAmount;
    private Double deliveryFee;
    private Integer estimatedDeliveryTime;
    
    private String imageUrl;
}
