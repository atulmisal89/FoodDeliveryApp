package com.fooddelivery.restaurantservice.controller;

import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.entity.RestaurantStatus;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant Management", description = "Restaurant management APIs")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    
    @PostMapping
    @Operation(summary = "Create a new restaurant")
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantDto restaurantDto) {
        RestaurantDto createdRestaurant = restaurantService.createRestaurant(restaurantDto);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantDto restaurantDto,
            @RequestHeader("X-Owner-Id") Long ownerId) {
        RestaurantDto updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDto, ownerId);
        return ResponseEntity.ok(updatedRestaurant);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id) {
        RestaurantDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }
    
    @GetMapping
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        List<RestaurantDto> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get restaurants by owner")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsByOwner(@PathVariable Long ownerId) {
        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByOwnerId(ownerId);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active restaurants")
    public ResponseEntity<List<RestaurantDto>> getActiveRestaurants() {
        List<RestaurantDto> restaurants = restaurantService.getActiveRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Get restaurants by city")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsByCity(@PathVariable String city) {
        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByCity(city);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/cuisine/{cuisine}")
    @Operation(summary = "Get restaurants by cuisine")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsByCuisine(@PathVariable String cuisine) {
        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby restaurants")
    public ResponseEntity<List<RestaurantDto>> getNearbyRestaurants(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radius) {
        List<RestaurantDto> restaurants = restaurantService.getNearbyRestaurants(latitude, longitude, radius);
        return ResponseEntity.ok(restaurants);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update restaurant status")
    public ResponseEntity<RestaurantDto> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestParam RestaurantStatus status,
            @RequestHeader("X-Owner-Id") Long ownerId) {
        RestaurantDto updatedRestaurant = restaurantService.updateRestaurantStatus(id, status, ownerId);
        return ResponseEntity.ok(updatedRestaurant);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long id,
            @RequestHeader("X-Owner-Id") Long ownerId) {
        restaurantService.deleteRestaurant(id, ownerId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/rating")
    @Operation(summary = "Update restaurant rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long id,
            @RequestParam Double rating) {
        restaurantService.updateRestaurantRating(id, rating);
        return ResponseEntity.ok().build();
    }
}
