package com.fooddelivery.deliveryservice.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLocation {
    private String customerName;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryContactName;
    private String deliveryContactPhone;
    private String landmark;
}
