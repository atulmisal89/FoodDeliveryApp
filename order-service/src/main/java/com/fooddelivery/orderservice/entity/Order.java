package com.fooddelivery.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String orderNumber;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private Long restaurantId;
    
    private String restaurantName;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    private String paymentId;
    
    @Embedded
    private DeliveryAddress deliveryAddress;
    
    private Double subtotal;
    private Double deliveryFee;
    private Double tax;
    private Double discount;
    private Double totalAmount;
    
    private String specialInstructions;
    
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    
    private String cancellationReason;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
