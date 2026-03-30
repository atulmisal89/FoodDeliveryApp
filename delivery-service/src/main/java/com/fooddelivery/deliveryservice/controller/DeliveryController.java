package com.fooddelivery.deliveryservice.controller;

import com.fooddelivery.deliveryservice.dto.DeliveryRequestDto;
import com.fooddelivery.deliveryservice.dto.DeliveryResponseDto;
import com.fooddelivery.deliveryservice.dto.DeliveryStatusUpdateDto;
import com.fooddelivery.deliveryservice.entity.DeliveryStatus;
import com.fooddelivery.deliveryservice.service.DeliveryService;
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
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery Management", description = "Delivery management APIs")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @Operation(summary = "Create a new delivery")
    public ResponseEntity<DeliveryResponseDto> createDelivery(@Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {
        DeliveryResponseDto delivery = deliveryService.createDelivery(deliveryRequestDto);
        return new ResponseEntity<>(delivery, HttpStatus.CREATED);
    }

    @PutMapping("/{deliveryId}/assign-person")
    @Operation(summary = "Assign delivery person to delivery")
    public ResponseEntity<DeliveryResponseDto> assignDeliveryPerson(
            @PathVariable Long deliveryId,
            @RequestParam Long deliveryPersonId,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String vehicleNumber) {
        DeliveryResponseDto delivery = deliveryService.assignDeliveryPerson(deliveryId, deliveryPersonId, name, phone, vehicleNumber);
        return ResponseEntity.ok(delivery);
    }

    @PutMapping("/{deliveryId}/status")
    @Operation(summary = "Update delivery status")
    public ResponseEntity<DeliveryResponseDto> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @Valid @RequestBody DeliveryStatusUpdateDto statusUpdateDto) {
        DeliveryResponseDto delivery = deliveryService.updateDeliveryStatus(deliveryId, statusUpdateDto);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/{deliveryId}")
    @Operation(summary = "Get delivery by ID")
    public ResponseEntity<DeliveryResponseDto> getDeliveryById(@PathVariable Long deliveryId) {
        DeliveryResponseDto delivery = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/delivery-id/{deliveryId}")
    @Operation(summary = "Get delivery by delivery ID")
    public ResponseEntity<DeliveryResponseDto> getDeliveryByDeliveryId(@PathVariable String deliveryId) {
        DeliveryResponseDto delivery = deliveryService.getDeliveryByDeliveryId(deliveryId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get delivery by order ID")
    public ResponseEntity<DeliveryResponseDto> getDeliveryByOrderId(@PathVariable Long orderId) {
        DeliveryResponseDto delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}")
    @Operation(summary = "Get deliveries by delivery person")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveriesByDeliveryPerson(@PathVariable Long deliveryPersonId) {
        List<DeliveryResponseDto> deliveries = deliveryService.getDeliveriesByDeliveryPerson(deliveryPersonId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}/active")
    @Operation(summary = "Get active deliveries by delivery person")
    public ResponseEntity<List<DeliveryResponseDto>> getActiveDeliveriesByDeliveryPerson(@PathVariable Long deliveryPersonId) {
        List<DeliveryResponseDto> deliveries = deliveryService.getActiveDeliveriesByDeliveryPerson(deliveryPersonId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get deliveries by customer")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveriesByCustomer(@PathVariable Long customerId) {
        List<DeliveryResponseDto> deliveries = deliveryService.getDeliveriesByCustomer(customerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get deliveries by status")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        List<DeliveryResponseDto> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}/date-range")
    @Operation(summary = "Get delivery person deliveries in date range")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveryPersonDeliveriesInDateRange(
            @PathVariable Long deliveryPersonId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<DeliveryResponseDto> deliveries = deliveryService.getDeliveryPersonDeliveriesInDateRange(deliveryPersonId, startDate, endDate);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}/completed-count")
    @Operation(summary = "Get completed deliveries count for delivery person")
    public ResponseEntity<Long> getCompletedDeliveriesCount(@PathVariable Long deliveryPersonId) {
        Long count = deliveryService.getCompletedDeliveriesCount(deliveryPersonId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}/average-time")
    @Operation(summary = "Get average delivery time for delivery person")
    public ResponseEntity<Double> getAverageDeliveryTime(@PathVariable Long deliveryPersonId) {
        Double averageTime = deliveryService.getAverageDeliveryTime(deliveryPersonId);
        return ResponseEntity.ok(averageTime);
    }

    @PutMapping("/{deliveryId}/location")
    @Operation(summary = "Update delivery location")
    public ResponseEntity<DeliveryResponseDto> updateDeliveryLocation(
            @PathVariable Long deliveryId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        DeliveryResponseDto delivery = deliveryService.updateDeliveryLocation(deliveryId, latitude, longitude);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/verify-otp")
    @Operation(summary = "Verify delivery OTP")
    public ResponseEntity<DeliveryResponseDto> verifyDeliveryOtp(
            @PathVariable Long deliveryId,
            @RequestParam String otp) {
        DeliveryResponseDto delivery = deliveryService.verifyDeliveryOtp(deliveryId, otp);
        return ResponseEntity.ok(delivery);
    }
}
