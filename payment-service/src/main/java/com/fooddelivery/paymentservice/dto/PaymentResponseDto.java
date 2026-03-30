package com.fooddelivery.paymentservice.dto;

import com.fooddelivery.paymentservice.entity.PaymentGateway;
import com.fooddelivery.paymentservice.entity.PaymentMethod;
import com.fooddelivery.paymentservice.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private Long id;
    private String transactionId;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private PaymentGateway gateway;
    private String gatewayTransactionId;
    private String currency;
    private String failureReason;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
