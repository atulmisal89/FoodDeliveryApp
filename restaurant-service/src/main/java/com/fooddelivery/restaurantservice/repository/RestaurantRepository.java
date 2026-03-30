package com.fooddelivery.restaurantservice.repository;

import com.fooddelivery.restaurantservice.entity.Restaurant;
import com.fooddelivery.restaurantservice.entity.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByOwnerId(Long ownerId);
    
    List<Restaurant> findByStatus(RestaurantStatus status);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE' AND r.address.city = :city")
    List<Restaurant> findActiveRestaurantsByCity(@Param("city") String city);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE' AND :cuisine MEMBER OF r.cuisineTypes")
    List<Restaurant> findActiveRestaurantsByCuisine(@Param("cuisine") String cuisine);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE' AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(r.address.latitude)) * " +
           "cos(radians(r.address.longitude) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(r.address.latitude)))) < :radius")
    List<Restaurant> findRestaurantsWithinRadius(@Param("lat") Double latitude, 
                                                 @Param("lon") Double longitude, 
                                                 @Param("radius") Double radius);
    
    Optional<Restaurant> findByIdAndOwnerId(Long id, Long ownerId);
}
