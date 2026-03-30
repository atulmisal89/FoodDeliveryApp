package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.*;
import com.fooddelivery.orderservice.entity.*;
import com.fooddelivery.orderservice.event.OrderEvent;
import com.fooddelivery.orderservice.exception.ResourceNotFoundException;
import com.fooddelivery.orderservice.exception.InvalidOrderStateException;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.orderservice.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    private static final double TAX_RATE = 0.18; // 18% GST
    
    public OrderResponseDto createOrder(CreateOrderDto createOrderDto) {
        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(createOrderDto.getCustomerId());
        order.setRestaurantId(createOrderDto.getRestaurantId());
        order.setRestaurantName(createOrderDto.getRestaurantName());
        order.setDeliveryAddress(createOrderDto.getDeliveryAddress());
        order.setPaymentMethod(createOrderDto.getPaymentMethod());
        order.setSpecialInstructions(createOrderDto.getSpecialInstructions());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        
        // Calculate pricing
        double subtotal = 0;
        for (OrderItemDto itemDto : createOrderDto.getItems()) {
            double itemTotal = itemDto.getUnitPrice() * itemDto.getQuantity();
            if (itemDto.getDiscount() != null) {
                itemTotal -= itemDto.getDiscount();
            }
            itemDto.setTotalPrice(itemTotal);
            subtotal += itemTotal;
        }
        
        order.setSubtotal(subtotal);
        order.setDeliveryFee(40.0); // Fixed delivery fee, can be made dynamic
        order.setTax(subtotal * TAX_RATE);
        order.setDiscount(createOrderDto.getDiscount() != null ? createOrderDto.getDiscount() : 0.0);
        order.setTotalAmount(subtotal + order.getDeliveryFee() + order.getTax() - order.getDiscount());
        
        // Set estimated delivery time (30-45 minutes from now)
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        for (OrderItemDto itemDto : createOrderDto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItemId(itemDto.getMenuItemId());
            orderItem.setItemName(itemDto.getItemName());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(itemDto.getUnitPrice());
            orderItem.setDiscount(itemDto.getDiscount());
            orderItem.setTotalPrice(itemDto.getTotalPrice());
            orderItem.setSpecialInstructions(itemDto.getSpecialInstructions());
            orderItemRepository.save(orderItem);
        }
        
        // Publish order created event
        publishOrderEvent(savedOrder, "ORDER_CREATED");
        
        return mapToResponseDto(savedOrder);
    }
    
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateDto statusUpdateDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), statusUpdateDto.getStatus());
        
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(statusUpdateDto.getStatus());
        
        // Handle specific status updates
        switch (statusUpdateDto.getStatus()) {
            case CANCELLED:
                order.setCancellationReason(statusUpdateDto.getReason());
                order.setPaymentStatus(PaymentStatus.CANCELLED);
                break;
            case DELIVERED:
                order.setActualDeliveryTime(LocalDateTime.now());
                order.setPaymentStatus(PaymentStatus.COMPLETED);
                break;
            case REFUNDED:
                order.setPaymentStatus(PaymentStatus.REFUNDED);
                break;
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        // Publish status update event
        publishOrderEvent(updatedOrder, "ORDER_STATUS_UPDATED");
        
        log.info("Order {} status updated from {} to {}", orderId, previousStatus, statusUpdateDto.getStatus());
        
        return mapToResponseDto(updatedOrder);
    }
    
    public OrderResponseDto assignDeliveryPerson(Long orderId, Long deliveryPersonId, String name, String phone) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        order.setDeliveryPersonId(deliveryPersonId);
        order.setDeliveryPersonName(name);
        order.setDeliveryPersonPhone(phone);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        
        Order updatedOrder = orderRepository.save(order);
        
        // Publish delivery assignment event
        publishOrderEvent(updatedOrder, "DELIVERY_ASSIGNED");
        
        return mapToResponseDto(updatedOrder);
    }
    
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapToResponseDto(order);
    }
    
    public OrderResponseDto getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return mapToResponseDto(order);
    }
    
    public List<OrderResponseDto> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<OrderResponseDto> getRestaurantOrders(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<OrderResponseDto> getDeliveryPersonOrders(Long deliveryPersonId) {
        return orderRepository.findByDeliveryPersonId(deliveryPersonId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<OrderResponseDto> getCustomerOrdersInDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findCustomerOrdersInDateRange(customerId, startDate, endDate).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public Double calculateRestaurantRevenue(Long restaurantId) {
        Double revenue = orderRepository.calculateRestaurantRevenue(restaurantId);
        return revenue != null ? revenue : 0.0;
    }
    
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        boolean isValidTransition = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY_FOR_PICKUP || newStatus == OrderStatus.CANCELLED;
            case READY_FOR_PICKUP -> newStatus == OrderStatus.OUT_FOR_DELIVERY || newStatus == OrderStatus.CANCELLED;
            case OUT_FOR_DELIVERY -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED;
            case DELIVERED -> newStatus == OrderStatus.REFUNDED;
            case CANCELLED -> newStatus == OrderStatus.REFUNDED;
            default -> false;
        };
        
        if (!isValidTransition) {
            throw new InvalidOrderStateException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }
    
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    private void publishOrderEvent(Order order, String eventType) {
        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(order.getStatus().name())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .build();
        
        kafkaTemplate.send("order-events", event);
        log.info("Published {} event for order {}", eventType, order.getOrderNumber());
    }
    
    private OrderResponseDto mapToResponseDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItemId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .totalPrice(item.getTotalPrice())
                        .specialInstructions(item.getSpecialInstructions())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .restaurantName(order.getRestaurantName())
                .items(itemDtos)
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentId(order.getPaymentId())
                .deliveryAddress(order.getDeliveryAddress())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .totalAmount(order.getTotalAmount())
                .specialInstructions(order.getSpecialInstructions())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .actualDeliveryTime(order.getActualDeliveryTime())
                .deliveryPersonId(order.getDeliveryPersonId())
                .deliveryPersonName(order.getDeliveryPersonName())
                .deliveryPersonPhone(order.getDeliveryPersonPhone())
                .cancellationReason(order.getCancellationReason())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
