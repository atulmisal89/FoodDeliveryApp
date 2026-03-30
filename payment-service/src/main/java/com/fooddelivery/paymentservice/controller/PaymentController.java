package com.fooddelivery.paymentservice.controller;

import com.fooddelivery.paymentservice.dto.PaymentRequestDto;
import com.fooddelivery.paymentservice.dto.PaymentResponseDto;
import com.fooddelivery.paymentservice.dto.RefundRequestDto;
import com.fooddelivery.paymentservice.entity.PaymentStatus;
import com.fooddelivery.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Payment processing APIs")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/process")
    @Operation(summary = "Process a payment")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto payment = paymentService.processPayment(requestDto);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
    
    @PostMapping("/refund")
    @Operation(summary = "Process a refund")
    public ResponseEntity<PaymentResponseDto> processRefund(@Valid @RequestBody RefundRequestDto refundDto) {
        PaymentResponseDto refund = paymentService.processRefund(refundDto);
        return ResponseEntity.ok(refund);
    }
    
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponseDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID")
    public ResponseEntity<PaymentResponseDto> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentResponseDto payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponseDto payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer payments")
    public ResponseEntity<List<PaymentResponseDto>> getCustomerPayments(@PathVariable Long customerId) {
        List<PaymentResponseDto> payments = paymentService.getCustomerPayments(customerId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/customer/{customerId}/date-range")
    @Operation(summary = "Get customer payments in date range")
    public ResponseEntity<List<PaymentResponseDto>> getCustomerPaymentsInDateRange(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PaymentResponseDto> payments = paymentService.getCustomerPaymentsInDateRange(customerId, startDate, endDate);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/revenue")
    @Operation(summary = "Calculate total revenue")
    public ResponseEntity<Double> calculateTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double revenue = paymentService.calculateTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
    
    @DeleteMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancel a payment")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long paymentId) {
        paymentService.cancelPayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
