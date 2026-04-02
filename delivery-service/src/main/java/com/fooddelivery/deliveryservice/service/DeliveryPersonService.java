package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.dto.DeliveryPersonRequestDto;
import com.fooddelivery.deliveryservice.dto.DeliveryPersonResponseDto;
import com.fooddelivery.deliveryservice.entity.DeliveryPerson;
import com.fooddelivery.deliveryservice.entity.DeliveryPersonStatus;
import com.fooddelivery.deliveryservice.exception.DeliveryPersonNotFoundException;
import com.fooddelivery.deliveryservice.repository.DeliveryPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;

    public DeliveryPersonResponseDto createDeliveryPerson(DeliveryPersonRequestDto requestDto) {
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setUserId(requestDto.getUserId());
        deliveryPerson.setName(requestDto.getName());
        deliveryPerson.setEmail(requestDto.getEmail());
        deliveryPerson.setPhoneNumber(requestDto.getPhoneNumber());
        deliveryPerson.setVehicleType(requestDto.getVehicleType());
        deliveryPerson.setVehicleNumber(requestDto.getVehicleNumber());
        deliveryPerson.setLicenseNumber(requestDto.getLicenseNumber());
        deliveryPerson.setProfileImageUrl(requestDto.getProfileImageUrl());
        deliveryPerson.setDocuments(requestDto.getDocuments());
        deliveryPerson.setStatus(DeliveryPersonStatus.OFFLINE);
        deliveryPerson.setAvailable(false);
        deliveryPerson.setActiveDeliveries(0);
        deliveryPerson.setCompletedDeliveries(0);
        deliveryPerson.setRating(0.0);
        deliveryPerson.setTotalRatings(0);
        deliveryPerson.setVerified(false);

        DeliveryPerson savedPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person registered with ID: {} and email: {}", savedPerson.getId(), savedPerson.getEmail());

        return mapToResponseDto(savedPerson);
    }

    public DeliveryPersonResponseDto getDeliveryPersonById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with id: " + id));
        return mapToResponseDto(deliveryPerson);
    }

    public DeliveryPersonResponseDto getDeliveryPersonByUserId(Long userId) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByUserId(userId)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with user id: " + userId));
        return mapToResponseDto(deliveryPerson);
    }

    public List<DeliveryPersonResponseDto> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DeliveryPersonResponseDto> getDeliveryPersonsByStatus(DeliveryPersonStatus status) {
        return deliveryPersonRepository.findByStatus(status).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DeliveryPersonResponseDto> getAvailableDeliveryPersons() {
        return deliveryPersonRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public DeliveryPersonResponseDto updateDeliveryPerson(Long id, DeliveryPersonRequestDto requestDto) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with id: " + id));

        deliveryPerson.setUserId(requestDto.getUserId());
        deliveryPerson.setName(requestDto.getName());
        deliveryPerson.setEmail(requestDto.getEmail());
        deliveryPerson.setPhoneNumber(requestDto.getPhoneNumber());
        deliveryPerson.setVehicleType(requestDto.getVehicleType());
        deliveryPerson.setVehicleNumber(requestDto.getVehicleNumber());
        deliveryPerson.setLicenseNumber(requestDto.getLicenseNumber());
        deliveryPerson.setProfileImageUrl(requestDto.getProfileImageUrl());
        deliveryPerson.setDocuments(requestDto.getDocuments());

        DeliveryPerson updatedPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person updated with ID: {}", updatedPerson.getId());

        return mapToResponseDto(updatedPerson);
    }

    public DeliveryPersonResponseDto updateDeliveryPersonStatus(Long id, DeliveryPersonStatus status) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with id: " + id));

        deliveryPerson.setStatus(status);
        DeliveryPerson updatedPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person {} status updated to {}", id, status);

        return mapToResponseDto(updatedPerson);
    }

    public DeliveryPersonResponseDto updateDeliveryPersonAvailability(Long id, boolean available) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with id: " + id));

        deliveryPerson.setAvailable(available);
        DeliveryPerson updatedPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person {} availability updated to {}", id, available);

        return mapToResponseDto(updatedPerson);
    }

    public DeliveryPersonResponseDto updateDeliveryPersonLocation(Long id, Double latitude, Double longitude) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with id: " + id));

        deliveryPerson.setCurrentLatitude(latitude);
        deliveryPerson.setCurrentLongitude(longitude);
        deliveryPerson.setLastLocationUpdate(LocalDateTime.now());

        DeliveryPerson updatedPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person {} location updated to {}, {}", id, latitude, longitude);

        return mapToResponseDto(updatedPerson);
    }

    public void deleteDeliveryPerson(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new DeliveryPersonNotFoundException("Delivery person not found with id: " + id));
        deliveryPersonRepository.delete(deliveryPerson);
        log.info("Delivery person {} deleted", id);
    }

    private DeliveryPersonResponseDto mapToResponseDto(DeliveryPerson deliveryPerson) {
        return DeliveryPersonResponseDto.builder()
                .id(deliveryPerson.getId())
                .userId(deliveryPerson.getUserId())
                .name(deliveryPerson.getName())
                .email(deliveryPerson.getEmail())
                .phoneNumber(deliveryPerson.getPhoneNumber())
                .status(deliveryPerson.getStatus())
                .vehicleType(deliveryPerson.getVehicleType())
                .vehicleNumber(deliveryPerson.getVehicleNumber())
                .licenseNumber(deliveryPerson.getLicenseNumber())
                .currentLatitude(deliveryPerson.getCurrentLatitude())
                .currentLongitude(deliveryPerson.getCurrentLongitude())
                .lastLocationUpdate(deliveryPerson.getLastLocationUpdate())
                .isAvailable(deliveryPerson.isAvailable())
                .activeDeliveries(deliveryPerson.getActiveDeliveries())
                .completedDeliveries(deliveryPerson.getCompletedDeliveries())
                .rating(deliveryPerson.getRating())
                .totalRatings(deliveryPerson.getTotalRatings())
                .profileImageUrl(deliveryPerson.getProfileImageUrl())
                .documents(deliveryPerson.getDocuments())
                .isVerified(deliveryPerson.isVerified())
                .verifiedAt(deliveryPerson.getVerifiedAt())
                .createdAt(deliveryPerson.getCreatedAt())
                .updatedAt(deliveryPerson.getUpdatedAt())
                .build();
    }
}
