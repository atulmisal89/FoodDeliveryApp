package com.fooddelivery.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDto {
    @NotNull(message = "Payment ID is required")
    private Long paymentId;
    
    @NotNull(message = "Refund amount is required")
    @Positive(message = "Refund amount must be positive")
    private Double refundAmount;
    
    @NotBlank(message = "Refund reason is required")
    private String refundReason;
}
