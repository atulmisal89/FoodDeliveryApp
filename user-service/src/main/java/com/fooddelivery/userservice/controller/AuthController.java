package com.fooddelivery.userservice.controller;

import com.fooddelivery.userservice.dto.JwtResponseDto;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.UserRegistrationDto;
import com.fooddelivery.userservice.dto.UserResponseDto;
import com.fooddelivery.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserResponseDto user = userService.registerUser(registrationDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        JwtResponseDto response = userService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}
