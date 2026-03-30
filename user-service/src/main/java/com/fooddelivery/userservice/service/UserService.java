package com.fooddelivery.userservice.service;

import com.fooddelivery.userservice.dto.*;
import com.fooddelivery.userservice.entity.Address;
import com.fooddelivery.userservice.entity.User;
import com.fooddelivery.userservice.entity.UserRole;
import com.fooddelivery.userservice.exception.ResourceNotFoundException;
import com.fooddelivery.userservice.exception.UserAlreadyExistsException;
import com.fooddelivery.userservice.repository.AddressRepository;
import com.fooddelivery.userservice.repository.UserRepository;
import com.fooddelivery.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registrationDto.getEmail() + " already exists");
        }
        
        if (registrationDto.getPhoneNumber() != null && 
            userRepository.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new UserAlreadyExistsException("User with phone number " + registrationDto.getPhoneNumber() + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRole(registrationDto.getRole() != null ? registrationDto.getRole() : UserRole.CUSTOMER);
        
        User savedUser = userRepository.save(user);
        
        return mapToUserResponseDto(savedUser);
    }
    
    public JwtResponseDto login(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        
        String token = jwtUtil.generateToken(userDetails.getUsername(), claims);
        
        return JwtResponseDto.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
    
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponseDto(user);
    }
    
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserResponseDto(user);
    }
    
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhoneNumber() != null) {
            if (!updateDto.getPhoneNumber().equals(user.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(updateDto.getPhoneNumber())) {
                throw new UserAlreadyExistsException("Phone number already in use");
            }
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        
        User updatedUser = userRepository.save(user);
        return mapToUserResponseDto(updatedUser);
    }
    
    public void changePassword(Long userId, ChangePasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }
    
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<UserResponseDto> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    // Address management methods
    public Address addAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        address.setUser(user);
        
        // If this is the first address or marked as default, set it as default
        if (address.isDefault() || user.getAddresses().isEmpty()) {
            // Reset other addresses as non-default
            user.getAddresses().forEach(a -> a.setDefault(false));
            address.setDefault(true);
        }
        
        return addressRepository.save(address);
    }
    
    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }
    
    public Address updateAddress(Long userId, Long addressId, Address addressUpdate) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to user");
        }
        
        address.setStreet(addressUpdate.getStreet());
        address.setCity(addressUpdate.getCity());
        address.setState(addressUpdate.getState());
        address.setZipCode(addressUpdate.getZipCode());
        address.setCountry(addressUpdate.getCountry());
        address.setLandmark(addressUpdate.getLandmark());
        address.setType(addressUpdate.getType());
        address.setLatitude(addressUpdate.getLatitude());
        address.setLongitude(addressUpdate.getLongitude());
        
        if (addressUpdate.isDefault()) {
            // Reset other addresses as non-default
            addressRepository.findByUserId(userId).forEach(a -> {
                if (!a.getId().equals(addressId)) {
                    a.setDefault(false);
                    addressRepository.save(a);
                }
            });
            address.setDefault(true);
        }
        
        return addressRepository.save(address);
    }
    
    public void deleteAddress(Long userId, Long addressId) {
        addressRepository.deleteByIdAndUserId(addressId, userId);
    }
    
    private UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .addresses(user.getAddresses())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
