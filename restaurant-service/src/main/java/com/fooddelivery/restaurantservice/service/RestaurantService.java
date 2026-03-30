package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.entity.Restaurant;
import com.fooddelivery.restaurantservice.entity.RestaurantStatus;
import com.fooddelivery.restaurantservice.exception.ResourceNotFoundException;
import com.fooddelivery.restaurantservice.exception.UnauthorizedException;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    
    public RestaurantDto createRestaurant(RestaurantDto restaurantDto) {
        Restaurant restaurant = mapToEntity(restaurantDto);
        restaurant.setStatus(RestaurantStatus.INACTIVE);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return mapToDto(savedRestaurant);
    }
    
    public RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new UnauthorizedException("You are not authorized to update this restaurant"));
        
        updateRestaurantFromDto(restaurant, restaurantDto);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return mapToDto(updatedRestaurant);
    }
    
    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return mapToDto(restaurant);
    }
    
    public List<RestaurantDto> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RestaurantDto> getRestaurantsByOwnerId(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RestaurantDto> getActiveRestaurants() {
        return restaurantRepository.findByStatus(RestaurantStatus.ACTIVE).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RestaurantDto> getRestaurantsByCity(String city) {
        return restaurantRepository.findActiveRestaurantsByCity(city).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RestaurantDto> getRestaurantsByCuisine(String cuisine) {
        return restaurantRepository.findActiveRestaurantsByCuisine(cuisine).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RestaurantDto> getNearbyRestaurants(Double latitude, Double longitude, Double radius) {
        return restaurantRepository.findRestaurantsWithinRadius(latitude, longitude, radius).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public RestaurantDto updateRestaurantStatus(Long id, RestaurantStatus status, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new UnauthorizedException("You are not authorized to update this restaurant"));
        
        restaurant.setStatus(status);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return mapToDto(updatedRestaurant);
    }
    
    public void deleteRestaurant(Long id, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new UnauthorizedException("You are not authorized to delete this restaurant"));
        
        restaurantRepository.delete(restaurant);
    }
    
    public void updateRestaurantRating(Long restaurantId, Double newRating) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        double totalRating = restaurant.getRating() * restaurant.getTotalRatings();
        restaurant.setTotalRatings(restaurant.getTotalRatings() + 1);
        restaurant.setRating((totalRating + newRating) / restaurant.getTotalRatings());
        
        restaurantRepository.save(restaurant);
    }
    
    private RestaurantDto mapToDto(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .ownerId(restaurant.getOwnerId())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .address(restaurant.getAddress())
                .cuisineTypes(restaurant.getCuisineTypes())
                .openingTime(restaurant.getOpeningTime())
                .closingTime(restaurant.getClosingTime())
                .status(restaurant.getStatus())
                .rating(restaurant.getRating())
                .totalRatings(restaurant.getTotalRatings())
                .minimumOrderAmount(restaurant.getMinimumOrderAmount())
                .deliveryFee(restaurant.getDeliveryFee())
                .estimatedDeliveryTime(restaurant.getEstimatedDeliveryTime())
                .imageUrl(restaurant.getImageUrl())
                .build();
    }
    
    private Restaurant mapToEntity(RestaurantDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setOwnerId(dto.getOwnerId());
        restaurant.setPhoneNumber(dto.getPhoneNumber());
        restaurant.setEmail(dto.getEmail());
        restaurant.setAddress(dto.getAddress());
        restaurant.setCuisineTypes(dto.getCuisineTypes());
        restaurant.setOpeningTime(dto.getOpeningTime());
        restaurant.setClosingTime(dto.getClosingTime());
        restaurant.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        restaurant.setDeliveryFee(dto.getDeliveryFee());
        restaurant.setEstimatedDeliveryTime(dto.getEstimatedDeliveryTime());
        restaurant.setImageUrl(dto.getImageUrl());
        return restaurant;
    }
    
    private void updateRestaurantFromDto(Restaurant restaurant, RestaurantDto dto) {
        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setPhoneNumber(dto.getPhoneNumber());
        restaurant.setEmail(dto.getEmail());
        restaurant.setAddress(dto.getAddress());
        restaurant.setCuisineTypes(dto.getCuisineTypes());
        restaurant.setOpeningTime(dto.getOpeningTime());
        restaurant.setClosingTime(dto.getClosingTime());
        restaurant.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        restaurant.setDeliveryFee(dto.getDeliveryFee());
        restaurant.setEstimatedDeliveryTime(dto.getEstimatedDeliveryTime());
        restaurant.setImageUrl(dto.getImageUrl());
    }
}
