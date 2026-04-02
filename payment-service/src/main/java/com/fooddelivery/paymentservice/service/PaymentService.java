package com.fooddelivery.paymentservice.service;

import com.fooddelivery.paymentservice.dto.PaymentRequestDto;
import com.fooddelivery.paymentservice.dto.PaymentResponseDto;
import com.fooddelivery.paymentservice.dto.RefundRequestDto;
import com.fooddelivery.paymentservice.entity.Payment;
import com.fooddelivery.paymentservice.entity.PaymentGateway;
import com.fooddelivery.paymentservice.entity.PaymentStatus;
import com.fooddelivery.paymentservice.event.OrderEvent;
import com.fooddelivery.paymentservice.exception.PaymentException;
import com.fooddelivery.paymentservice.exception.ResourceNotFoundException;
import com.fooddelivery.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public PaymentResponseDto processPayment(PaymentRequestDto requestDto) {
        // Check if payment already exists for this order
        if (paymentRepository.findByOrderId(requestDto.getOrderId()).isPresent()) {
            throw new PaymentException("Payment already exists for order: " + requestDto.getOrderId());
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setOrderId(requestDto.getOrderId());
        payment.setCustomerId(requestDto.getCustomerId());
        payment.setAmount(requestDto.getAmount());
        payment.setPaymentMethod(requestDto.getPaymentMethod());
        payment.setCurrency(requestDto.getCurrency());
        payment.setStatus(PaymentStatus.PROCESSING);
        
        // Select payment gateway based on method
        PaymentGateway gateway = selectPaymentGateway(requestDto.getPaymentMethod());
        payment.setGateway(gateway);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        try {
            // Process payment through gateway (simulated)
            boolean paymentSuccess = processWithGateway(requestDto, gateway);
            
            if (paymentSuccess) {
                savedPayment.setStatus(PaymentStatus.SUCCESS);
                savedPayment.setPaymentDate(LocalDateTime.now());
                savedPayment.setGatewayTransactionId(UUID.randomUUID().toString());
                
                // Publish payment success event
                publishPaymentEvent(savedPayment, "PAYMENT_SUCCESS");
                log.info("Payment successful for order: {}", requestDto.getOrderId());
            } else {
                savedPayment.setStatus(PaymentStatus.FAILED);
                savedPayment.setFailureReason("Payment declined by gateway");
                
                // Publish payment failure event
                publishPaymentEvent(savedPayment, "PAYMENT_FAILED");
                log.error("Payment failed for order: {}", requestDto.getOrderId());
            }
        } catch (Exception e) {
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setFailureReason(e.getMessage());
            log.error("Payment processing error: ", e);
        }
        
        Payment finalPayment = paymentRepository.save(savedPayment);
        return mapToResponseDto(finalPayment);
    }
    
    public PaymentResponseDto processRefund(RefundRequestDto refundDto) {
        Payment payment = paymentRepository.findById(refundDto.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + refundDto.getPaymentId()));
        
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Cannot refund payment with status: " + payment.getStatus());
        }
        
        if (refundDto.getRefundAmount() > payment.getAmount()) {
            throw new PaymentException("Refund amount cannot exceed payment amount");
        }
        
        // Process refund (simulated)
        payment.setRefundId(UUID.randomUUID().toString());
        payment.setRefundAmount(refundDto.getRefundAmount());
        payment.setRefundDate(LocalDateTime.now());
        payment.setRefundReason(refundDto.getRefundReason());
        
        if (refundDto.getRefundAmount().equals(payment.getAmount())) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        
        Payment refundedPayment = paymentRepository.save(payment);
        
        // Publish refund event
        publishPaymentEvent(refundedPayment, "PAYMENT_REFUNDED");
        log.info("Refund processed for payment: {}", payment.getTransactionId());
        
        return mapToResponseDto(refundedPayment);
    }
    
    public PaymentResponseDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        return mapToResponseDto(payment);
    }
    
    public PaymentResponseDto getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));
        return mapToResponseDto(payment);
    }
    
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return mapToResponseDto(payment);
    }
    
    public List<PaymentResponseDto> getCustomerPayments(Long customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<PaymentResponseDto> getCustomerPaymentsInDateRange(Long customerId, 
                                                                    LocalDateTime startDate, 
                                                                    LocalDateTime endDate) {
        return paymentRepository.findCustomerPaymentsInDateRange(customerId, startDate, endDate).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = paymentRepository.calculateTotalRevenue(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }
    
    public void cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new PaymentException("Cannot cancel successful payment. Please process refund instead.");
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
        
        // Publish cancellation event
        publishPaymentEvent(payment, "PAYMENT_CANCELLED");
        log.info("Payment cancelled: {}", payment.getTransactionId());
    }
    
    private PaymentGateway selectPaymentGateway(com.fooddelivery.paymentservice.entity.PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case CREDIT_CARD, DEBIT_CARD -> PaymentGateway.STRIPE;
            case UPI -> PaymentGateway.RAZORPAY;
            case WALLET -> PaymentGateway.PAYTM;
            case CASH_ON_DELIVERY -> PaymentGateway.INTERNAL;
            default -> PaymentGateway.RAZORPAY;
        };
    }
    
    private boolean processWithGateway(PaymentRequestDto requestDto, PaymentGateway gateway) {
        // Simulate payment processing
        // In real implementation, this would call actual payment gateway APIs
        
        // For demo purposes, randomly succeed 90% of payments
        return Math.random() > 0.1;
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private void publishPaymentEvent(Payment payment, String eventType) {
        PaymentEvent event = PaymentEvent.builder()
                .paymentId(payment.getId())
                .transactionId(payment.getTransactionId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .build();
        
        kafkaTemplate.send("payment-events", event);
    }
    
    private PaymentResponseDto mapToResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .gateway(payment.getGateway())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .currency(payment.getCurrency())
                .failureReason(payment.getFailureReason())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
    
    @lombok.Builder
    @lombok.Data
    static class PaymentEvent {
        private Long paymentId;
        private String transactionId;
        private Long orderId;
        private Long customerId;
        private Double amount;
        private String status;
        private String eventType;
        private LocalDateTime timestamp;
    }
    
    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: {} for order {}", event.getEventType(), event.getOrderId());
        
        // Handle order events as needed
        switch (event.getEventType()) {
            case "ORDER_CREATED":
                log.info("Order {} created, preparing for payment", event.getOrderId());
                break;
            case "ORDER_CANCELLED":
                log.info("Order {} cancelled, cancelling payment if any", event.getOrderId());
                break;
            default:
                log.debug("Unhandled order event type: {}", event.getEventType());
        }
    }
}
