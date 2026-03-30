package com.fooddelivery.deliveryservice.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLocation {
    private String restaurantName;
    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupContactName;
    private String pickupContactPhone;
}
