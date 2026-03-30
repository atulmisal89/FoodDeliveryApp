package com.fooddelivery.deliveryservice.repository;

import com.fooddelivery.deliveryservice.entity.Delivery;
import com.fooddelivery.deliveryservice.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByDeliveryId(String deliveryId);
    
    Optional<Delivery> findByOrderId(Long orderId);
    
    List<Delivery> findByDeliveryPersonId(Long deliveryPersonId);
    
    List<Delivery> findByCustomerId(Long customerId);
    
    List<Delivery> findByStatus(DeliveryStatus status);
    
    List<Delivery> findByDeliveryPersonIdAndStatus(Long deliveryPersonId, DeliveryStatus status);
    
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPersonId = :deliveryPersonId AND d.status IN ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'ON_THE_WAY')")
    List<Delivery> findActiveDeliveriesByDeliveryPerson(@Param("deliveryPersonId") Long deliveryPersonId);
    
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPersonId = :deliveryPersonId AND d.deliveredTime BETWEEN :startDate AND :endDate")
    List<Delivery> findDeliveryPersonDeliveriesInDateRange(@Param("deliveryPersonId") Long deliveryPersonId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.deliveryPersonId = :deliveryPersonId AND d.status = 'DELIVERED'")
    Long countCompletedDeliveries(@Param("deliveryPersonId") Long deliveryPersonId);
    
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, d.assignedTime, d.deliveredTime)) FROM Delivery d WHERE d.deliveryPersonId = :deliveryPersonId AND d.status = 'DELIVERED'")
    Double calculateAverageDeliveryTime(@Param("deliveryPersonId") Long deliveryPersonId);
}
