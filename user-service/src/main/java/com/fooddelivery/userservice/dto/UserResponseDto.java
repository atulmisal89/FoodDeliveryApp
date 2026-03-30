package com.fooddelivery.userservice.dto;

import com.fooddelivery.userservice.entity.Address;
import com.fooddelivery.userservice.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private boolean enabled;
    private Set<Address> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
