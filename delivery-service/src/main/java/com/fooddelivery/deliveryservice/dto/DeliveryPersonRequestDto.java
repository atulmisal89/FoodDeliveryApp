package com.fooddelivery.deliveryservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    private String vehicleType;
    
    private String vehicleNumber;
    
    private String licenseNumber;
    
    private String profileImageUrl;
    
    private String documents;
}
