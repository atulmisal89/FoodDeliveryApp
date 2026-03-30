package com.fooddelivery.userservice.controller;

import com.fooddelivery.userservice.dto.ChangePasswordDto;
import com.fooddelivery.userservice.dto.UserResponseDto;
import com.fooddelivery.userservice.dto.UserUpdateDto;
import com.fooddelivery.userservice.entity.Address;
import com.fooddelivery.userservice.entity.UserRole;
import com.fooddelivery.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.claims['userId']")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.claims['userId']")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto updateDto) {
        UserResponseDto user = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/{id}/change-password")
    @Operation(summary = "Change user password")
    @PreAuthorize("#id == authentication.principal.claims['userId']")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto passwordDto) {
        userService.changePassword(id, passwordDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Admin endpoints
    @GetMapping("/admin/all")
    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/admin/role/{role}")
    @Operation(summary = "Get users by role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable UserRole role) {
        List<UserResponseDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    // Address management endpoints
    @PostMapping("/{userId}/addresses")
    @Operation(summary = "Add address for user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['userId']")
    public ResponseEntity<Address> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody Address address) {
        Address savedAddress = userService.addAddress(userId, address);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }
    
    @GetMapping("/{userId}/addresses")
    @Operation(summary = "Get user addresses")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['userId']")
    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable Long userId) {
        List<Address> addresses = userService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }
    
    @PutMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Update address")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['userId']")
    public ResponseEntity<Address> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody Address address) {
        Address updatedAddress = userService.updateAddress(userId, addressId, address);
        return ResponseEntity.ok(updatedAddress);
    }
    
    @DeleteMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Delete address")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['userId']")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        userService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
