package com.fooddelivery.restaurantservice.dto;

import com.fooddelivery.restaurantservice.entity.FoodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    
    private Long categoryId;
    private String categoryName;
    
    private Long restaurantId;
    
    private String imageUrl;
    
    private FoodType foodType;
    
    private boolean isAvailable;
    
    private Integer preparationTime;
    
    private Double discount;
}
