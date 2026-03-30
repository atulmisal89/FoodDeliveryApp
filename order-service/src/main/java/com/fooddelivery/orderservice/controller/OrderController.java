package com.fooddelivery.orderservice.controller;

import com.fooddelivery.orderservice.dto.*;
import com.fooddelivery.orderservice.entity.OrderStatus;
import com.fooddelivery.orderservice.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order management APIs")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        OrderResponseDto order = orderService.createOrder(createOrderDto);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateDto statusUpdateDto) {
        OrderResponseDto order = orderService.updateOrderStatus(orderId, statusUpdateDto);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/assign-delivery")
    @Operation(summary = "Assign delivery person to order")
    public ResponseEntity<OrderResponseDto> assignDeliveryPerson(
            @PathVariable Long orderId,
            @RequestParam Long deliveryPersonId,
            @RequestParam String name,
            @RequestParam String phone) {
        OrderResponseDto order = orderService.assignDeliveryPerson(orderId, deliveryPersonId, name, phone);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<OrderResponseDto> getOrderByNumber(@PathVariable String orderNumber) {
        OrderResponseDto order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer")
    public ResponseEntity<List<OrderResponseDto>> getCustomerOrders(@PathVariable Long customerId) {
        List<OrderResponseDto> orders = orderService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get orders by restaurant")
    public ResponseEntity<List<OrderResponseDto>> getRestaurantOrders(@PathVariable Long restaurantId) {
        List<OrderResponseDto> orders = orderService.getRestaurantOrders(restaurantId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/delivery-person/{deliveryPersonId}")
    @Operation(summary = "Get orders by delivery person")
    public ResponseEntity<List<OrderResponseDto>> getDeliveryPersonOrders(@PathVariable Long deliveryPersonId) {
        List<OrderResponseDto> orders = orderService.getDeliveryPersonOrders(deliveryPersonId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/customer/{customerId}/date-range")
    @Operation(summary = "Get customer orders in date range")
    public ResponseEntity<List<OrderResponseDto>> getCustomerOrdersInDateRange(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<OrderResponseDto> orders = orderService.getCustomerOrdersInDateRange(customerId, startDate, endDate);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/restaurant/{restaurantId}/revenue")
    @Operation(summary = "Calculate restaurant revenue")
    public ResponseEntity<Double> calculateRestaurantRevenue(@PathVariable Long restaurantId) {
        Double revenue = orderService.calculateRestaurantRevenue(restaurantId);
        return ResponseEntity.ok(revenue);
    }
}
