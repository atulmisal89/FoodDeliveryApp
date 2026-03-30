package com.fooddelivery.deliveryservice.repository;

import com.fooddelivery.deliveryservice.entity.DeliveryPerson;
import com.fooddelivery.deliveryservice.entity.DeliveryPersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    Optional<DeliveryPerson> findByUserId(Long userId);
    
    Optional<DeliveryPerson> findByEmail(String email);
    
    Optional<DeliveryPerson> findByPhoneNumber(String phoneNumber);
    
    List<DeliveryPerson> findByStatus(DeliveryPersonStatus status);
    
    List<DeliveryPerson> findByIsAvailableTrue();
    
    List<DeliveryPerson> findByIsVerifiedTrue();
    
    @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.status = 'ONLINE' AND dp.isAvailable = true AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(dp.currentLatitude)) * " +
           "cos(radians(dp.currentLongitude) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(dp.currentLatitude)))) < :radius " +
           "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(dp.currentLatitude)) * " +
           "cos(radians(dp.currentLongitude) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(dp.currentLatitude))))")
    List<DeliveryPerson> findNearbyAvailableDeliveryPersons(@Param("lat") Double latitude,
                                                            @Param("lon") Double longitude,
                                                            @Param("radius") Double radius);
    
    @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.rating >= :minRating ORDER BY dp.rating DESC")
    List<DeliveryPerson> findTopRatedDeliveryPersons(@Param("minRating") Double minRating);
}
