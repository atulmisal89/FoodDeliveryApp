package com.fooddelivery.paymentservice.repository;

import com.fooddelivery.paymentservice.entity.Payment;
import com.fooddelivery.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByCustomerId(Long customerId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByCustomerIdAndStatus(Long customerId, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.customerId = :customerId AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findCustomerPaymentsInDateRange(@Param("customerId") Long customerId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.createdAt >= :since")
    Long countPaymentsByStatusSince(@Param("status") PaymentStatus status,
                                    @Param("since") LocalDateTime since);
}
