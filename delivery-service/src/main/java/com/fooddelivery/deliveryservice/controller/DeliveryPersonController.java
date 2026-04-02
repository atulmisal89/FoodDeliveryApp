package com.fooddelivery.deliveryservice.controller;

import com.fooddelivery.deliveryservice.dto.DeliveryPersonRequestDto;
import com.fooddelivery.deliveryservice.dto.DeliveryPersonResponseDto;
import com.fooddelivery.deliveryservice.entity.DeliveryPersonStatus;
import com.fooddelivery.deliveryservice.service.DeliveryPersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-persons")
@RequiredArgsConstructor
@Tag(name = "Delivery Person Management", description = "Delivery person management APIs")
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;

    @PostMapping
    @Operation(summary = "Register a new delivery person")
    public ResponseEntity<DeliveryPersonResponseDto> registerDeliveryPerson(@Valid @RequestBody DeliveryPersonRequestDto requestDto) {
        DeliveryPersonResponseDto deliveryPerson = deliveryPersonService.createDeliveryPerson(requestDto);
        return new ResponseEntity<>(deliveryPerson, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery person by ID")
    public ResponseEntity<DeliveryPersonResponseDto> getDeliveryPersonById(@PathVariable Long id) {
        DeliveryPersonResponseDto deliveryPerson = deliveryPersonService.getDeliveryPersonById(id);
        return ResponseEntity.ok(deliveryPerson);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get delivery person by user ID")
    public ResponseEntity<DeliveryPersonResponseDto> getDeliveryPersonByUserId(@PathVariable Long userId) {
        DeliveryPersonResponseDto deliveryPerson = deliveryPersonService.getDeliveryPersonByUserId(userId);
        return ResponseEntity.ok(deliveryPerson);
    }

    @GetMapping
    @Operation(summary = "Get all delivery persons")
    public ResponseEntity<List<DeliveryPersonResponseDto>> getAllDeliveryPersons() {
        List<DeliveryPersonResponseDto> deliveryPersons = deliveryPersonService.getAllDeliveryPersons();
        return ResponseEntity.ok(deliveryPersons);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get delivery persons by status")
    public ResponseEntity<List<DeliveryPersonResponseDto>> getDeliveryPersonsByStatus(@PathVariable DeliveryPersonStatus status) {
        List<DeliveryPersonResponseDto> deliveryPersons = deliveryPersonService.getDeliveryPersonsByStatus(status);
        return ResponseEntity.ok(deliveryPersons);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available delivery persons")
    public ResponseEntity<List<DeliveryPersonResponseDto>> getAvailableDeliveryPersons() {
        List<DeliveryPersonResponseDto> deliveryPersons = deliveryPersonService.getAvailableDeliveryPersons();
        return ResponseEntity.ok(deliveryPersons);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update delivery person")
    public ResponseEntity<DeliveryPersonResponseDto> updateDeliveryPerson(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryPersonRequestDto requestDto) {
        DeliveryPersonResponseDto updatedPerson = deliveryPersonService.updateDeliveryPerson(id, requestDto);
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update delivery person status")
    public ResponseEntity<DeliveryPersonResponseDto> updateDeliveryPersonStatus(
            @PathVariable Long id,
            @RequestParam DeliveryPersonStatus status) {
        DeliveryPersonResponseDto updatedPerson = deliveryPersonService.updateDeliveryPersonStatus(id, status);
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/{id}/availability")
    @Operation(summary = "Update delivery person availability")
    public ResponseEntity<DeliveryPersonResponseDto> updateDeliveryPersonAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        DeliveryPersonResponseDto updatedPerson = deliveryPersonService.updateDeliveryPersonAvailability(id, available);
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update delivery person location")
    public ResponseEntity<DeliveryPersonResponseDto> updateDeliveryPersonLocation(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        DeliveryPersonResponseDto updatedPerson = deliveryPersonService.updateDeliveryPersonLocation(id, latitude, longitude);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete delivery person")
    public ResponseEntity<Void> deleteDeliveryPerson(@PathVariable Long id) {
        deliveryPersonService.deleteDeliveryPerson(id);
        return ResponseEntity.noContent().build();
    }
}
