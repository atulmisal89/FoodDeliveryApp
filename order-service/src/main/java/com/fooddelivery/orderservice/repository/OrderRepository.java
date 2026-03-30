package com.fooddelivery.orderservice.repository;

import com.fooddelivery.orderservice.entity.Order;
import com.fooddelivery.orderservice.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByRestaurantId(Long restaurantId);
    
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
    
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findCustomerOrdersInDateRange(@Param("customerId") Long customerId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findRestaurantOrdersInDateRange(@Param("restaurantId") Long restaurantId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customerId = :customerId")
    Long countOrdersByCustomer(@Param("customerId") Long customerId);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurantId = :restaurantId AND o.status = 'DELIVERED'")
    Double calculateRestaurantRevenue(@Param("restaurantId") Long restaurantId);
}
