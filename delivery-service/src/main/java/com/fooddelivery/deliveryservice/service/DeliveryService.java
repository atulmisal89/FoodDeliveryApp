package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.dto.DeliveryRequestDto;
import com.fooddelivery.deliveryservice.dto.DeliveryResponseDto;
import com.fooddelivery.deliveryservice.dto.DeliveryStatusUpdateDto;
import com.fooddelivery.deliveryservice.entity.Delivery;
import com.fooddelivery.deliveryservice.entity.DeliveryStatus;
import com.fooddelivery.deliveryservice.exception.DeliveryNotFoundException;
import com.fooddelivery.deliveryservice.exception.InvalidDeliveryStateException;
import com.fooddelivery.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryResponseDto createDelivery(DeliveryRequestDto deliveryRequestDto) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(generateDeliveryId());
        delivery.setOrderId(deliveryRequestDto.getOrderId());
        delivery.setCustomerId(deliveryRequestDto.getCustomerId());
        delivery.setRestaurantId(deliveryRequestDto.getRestaurantId());
        delivery.setPickupLocation(deliveryRequestDto.getPickupLocation());
        delivery.setDeliveryLocation(deliveryRequestDto.getDeliveryLocation());
        delivery.setDeliveryFee(deliveryRequestDto.getDeliveryFee());
        delivery.setNotes(deliveryRequestDto.getNotes());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setAssignedTime(LocalDateTime.now());
        delivery.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery created with ID: {}", savedDelivery.getDeliveryId());

        return mapToResponseDto(savedDelivery);
    }

    public DeliveryResponseDto assignDeliveryPerson(Long deliveryId, Long deliveryPersonId, String name, String phone, String vehicleNumber) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new InvalidDeliveryStateException("Delivery can only be assigned when status is PENDING");
        }

        delivery.setDeliveryPersonId(deliveryPersonId);
        delivery.setDeliveryPersonName(name);
        delivery.setDeliveryPersonPhone(phone);
        delivery.setVehicleNumber(vehicleNumber);
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery {} assigned to person {}", deliveryId, deliveryPersonId);

        return mapToResponseDto(updatedDelivery);
    }

    public DeliveryResponseDto updateDeliveryStatus(Long deliveryId, DeliveryStatusUpdateDto statusUpdateDto) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with id: " + deliveryId));

        validateStatusTransition(delivery.getStatus(), statusUpdateDto.getStatus());

        DeliveryStatus previousStatus = delivery.getStatus();
        delivery.setStatus(statusUpdateDto.getStatus());

        switch (statusUpdateDto.getStatus()) {
            case ACCEPTED:
                break;
            case PICKED_UP:
                delivery.setPickupTime(LocalDateTime.now());
                break;
            case ON_THE_WAY:
                if (statusUpdateDto.getCurrentLatitude() != null) {
                    delivery.setCurrentLatitude(statusUpdateDto.getCurrentLatitude());
                }
                if (statusUpdateDto.getCurrentLongitude() != null) {
                    delivery.setCurrentLongitude(statusUpdateDto.getCurrentLongitude());
                }
                break;
            case DELIVERED:
                delivery.setDeliveredTime(LocalDateTime.now());
                if (statusUpdateDto.getCustomerSignature() != null) {
                    delivery.setCustomerSignature(statusUpdateDto.getCustomerSignature());
                }
                if (statusUpdateDto.getDeliveryProofUrl() != null) {
                    delivery.setDeliveryProofUrl(statusUpdateDto.getDeliveryProofUrl());
                }
                break;
            case CANCELLED:
                delivery.setCancellationReason(statusUpdateDto.getCancellationReason());
                break;
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery {} status updated from {} to {}", deliveryId, previousStatus, statusUpdateDto.getStatus());

        return mapToResponseDto(updatedDelivery);
    }

    public DeliveryResponseDto getDeliveryById(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with id: " + deliveryId));
        return mapToResponseDto(delivery);
    }

    public DeliveryResponseDto getDeliveryByDeliveryId(String deliveryId) {
        Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with delivery ID: " + deliveryId));
        return mapToResponseDto(delivery);
    }

    public DeliveryResponseDto getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found for order ID: " + orderId));
        return mapToResponseDto(delivery);
    }

    public List<DeliveryResponseDto> getDeliveriesByDeliveryPerson(Long deliveryPersonId) {
        return deliveryRepository.findByDeliveryPersonId(deliveryPersonId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDto> getActiveDeliveriesByDeliveryPerson(Long deliveryPersonId) {
        return deliveryRepository.findActiveDeliveriesByDeliveryPerson(deliveryPersonId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDto> getDeliveriesByCustomer(Long customerId) {
        return deliveryRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDto> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDto> getDeliveryPersonDeliveriesInDateRange(Long deliveryPersonId, LocalDateTime startDate, LocalDateTime endDate) {
        return deliveryRepository.findDeliveryPersonDeliveriesInDateRange(deliveryPersonId, startDate, endDate).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Long getCompletedDeliveriesCount(Long deliveryPersonId) {
        return deliveryRepository.countCompletedDeliveries(deliveryPersonId);
    }

    public Double getAverageDeliveryTime(Long deliveryPersonId) {
        return deliveryRepository.calculateAverageDeliveryTime(deliveryPersonId);
    }

    public DeliveryResponseDto updateDeliveryLocation(Long deliveryId, Double latitude, Double longitude) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with id: " + deliveryId));

        delivery.setCurrentLatitude(latitude);
        delivery.setCurrentLongitude(longitude);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToResponseDto(updatedDelivery);
    }

    public DeliveryResponseDto verifyDeliveryOtp(Long deliveryId, String otp) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with id: " + deliveryId));

        if (!delivery.getDeliveryOtp().equals(otp)) {
            throw new InvalidDeliveryStateException("Invalid OTP");
        }

        delivery.setOtpVerified(true);
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToResponseDto(updatedDelivery);
    }

    private void validateStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        boolean isValidTransition = switch (currentStatus) {
            case PENDING -> newStatus == DeliveryStatus.ASSIGNED || newStatus == DeliveryStatus.CANCELLED;
            case ASSIGNED -> newStatus == DeliveryStatus.ACCEPTED || newStatus == DeliveryStatus.CANCELLED;
            case ACCEPTED -> newStatus == DeliveryStatus.PICKED_UP || newStatus == DeliveryStatus.CANCELLED;
            case PICKED_UP -> newStatus == DeliveryStatus.ON_THE_WAY || newStatus == DeliveryStatus.CANCELLED;
            case ON_THE_WAY -> newStatus == DeliveryStatus.DELIVERED || newStatus == DeliveryStatus.CANCELLED;
            case DELIVERED -> false;
            case CANCELLED -> false;
            case REJECTED -> false;
            case REACHED -> false;
            case FAILED -> false;
        };

        if (!isValidTransition) {
            throw new InvalidDeliveryStateException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }

    private String generateDeliveryId() {
        return "DEL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private DeliveryResponseDto mapToResponseDto(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .id(delivery.getId())
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .customerId(delivery.getCustomerId())
                .restaurantId(delivery.getRestaurantId())
                .deliveryPersonId(delivery.getDeliveryPersonId())
                .deliveryPersonName(delivery.getDeliveryPersonName())
                .deliveryPersonPhone(delivery.getDeliveryPersonPhone())
                .vehicleNumber(delivery.getVehicleNumber())
                .status(delivery.getStatus())
                .pickupLocation(delivery.getPickupLocation())
                .deliveryLocation(delivery.getDeliveryLocation())
                .currentLatitude(delivery.getCurrentLatitude())
                .currentLongitude(delivery.getCurrentLongitude())
                .assignedTime(delivery.getAssignedTime())
                .pickupTime(delivery.getPickupTime())
                .deliveredTime(delivery.getDeliveredTime())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .distance(delivery.getDistance())
                .deliveryFee(delivery.getDeliveryFee())
                .deliveryOtp(delivery.getDeliveryOtp())
                .otpVerified(delivery.isOtpVerified())
                .customerSignature(delivery.getCustomerSignature())
                .deliveryProofUrl(delivery.getDeliveryProofUrl())
                .notes(delivery.getNotes())
                .cancellationReason(delivery.getCancellationReason())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
