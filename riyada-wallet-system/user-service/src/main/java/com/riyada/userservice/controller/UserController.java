package com.riyada.userservice.controller;

import com.riyada.userservice.dto.LoginResponseDTO;
import com.riyada.userservice.dto.UserLoginDTO;
import com.riyada.userservice.dto.UserRegistrationDTO;
import com.riyada.userservice.dto.UserResponseDTO;
import com.riyada.userservice.dto.UserUpdateDTO;
import com.riyada.userservice.entity.User;
import com.riyada.userservice.service.UserService;
import com.riyada.userservice.util.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Register a new user
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            // Convert DTO to entity
            User user = UserMapper.toUser(registrationDTO);

            // Create user
            User createdUser = userService.createUser(user);

            // Convert to response DTO
            UserResponseDTO responseDTO = UserMapper.toUserResponseDTO(createdUser);

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            // Handle business logic errors (e.g., email already exists)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "REGISTRATION_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during registration");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Login user and return JWT token
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            // Authenticate user and generate token
            LoginResponseDTO loginResponse = userService.loginUser(loginDTO);

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", loginResponse);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle authentication errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "AUTHENTICATION_FAILED");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during login");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user profile by ID
     * GET /api/users/profile/{userId}
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable UUID userId) {
        try {
            // Get user by ID
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Convert to response DTO
            UserResponseDTO responseDTO = UserMapper.toUserResponseDTO(user);

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User profile retrieved successfully");
            response.put("user", responseDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle user not found errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "USER_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving user profile");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user profile by email
     * GET /api/users/profile/email/{email}
     */
    @GetMapping("/profile/email/{email}")
    public ResponseEntity<?> getUserProfileByEmail(@PathVariable String email) {
        try {
            // Get user by email
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Convert to response DTO
            UserResponseDTO responseDTO = UserMapper.toUserResponseDTO(user);

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User profile retrieved successfully");
            response.put("user", responseDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle user not found errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "USER_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving user profile");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update user profile
     * PUT /api/users/profile/{userId}
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable UUID userId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            // Convert DTO to entity for update
            User userDetails = new User();
            userDetails.setEmail(updateDTO.getEmail());
            userDetails.setFirstName(updateDTO.getFirstName());
            userDetails.setLastName(updateDTO.getLastName());
            userDetails.setPhone(updateDTO.getPhone());

            // Update user
            User updatedUser = userService.updateUser(userId, userDetails);

            // Convert to response DTO
            UserResponseDTO responseDTO = UserMapper.toUserResponseDTO(updatedUser);

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User profile updated successfully");
            response.put("user", responseDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle business logic errors (e.g., user not found, email already taken)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "UPDATE_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while updating user profile");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete user account (soft delete - deactivate)
     * DELETE /api/users/account/{userId}
     */
    @DeleteMapping("/account/{userId}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable UUID userId) {
        try {
            // Deactivate user (soft delete)
            User deactivatedUser = userService.deactivateUser(userId);

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User account deactivated successfully");
            response.put("userId", deactivatedUser.getId());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle user not found errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "USER_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            // Handle unexpected errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while deactivating user account");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
