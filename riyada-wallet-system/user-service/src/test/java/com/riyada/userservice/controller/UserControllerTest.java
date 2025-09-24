package com.riyada.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riyada.userservice.dto.UserRegistrationDTO;
import com.riyada.userservice.dto.UserUpdateDTO;
import com.riyada.userservice.entity.User;
import com.riyada.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testRegisterUser_Success() throws Exception {
                // Given
                UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                                "test@example.com",
                                "password123",
                                "John",
                                "Doe",
                                "+1234567890");

                User createdUser = new User();
                createdUser.setId(UUID.randomUUID());
                createdUser.setEmail("test@example.com");
                createdUser.setFirstName("John");
                createdUser.setLastName("Doe");
                createdUser.setPhone("+1234567890");
                createdUser.setIsActive(true);

                when(userService.createUser(any(User.class))).thenReturn(createdUser);

                // When & Then
                mockMvc.perform(post("/api/users/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User registered successfully"))
                                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                                .andExpect(jsonPath("$.user.firstName").value("John"))
                                .andExpect(jsonPath("$.user.lastName").value("Doe"));
        }

        @Test
        void testRegisterUser_EmailAlreadyExists() throws Exception {
                // Given
                UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                                "existing@example.com",
                                "password123",
                                "John",
                                "Doe",
                                null);

                when(userService.createUser(any(User.class)))
                                .thenThrow(new RuntimeException("User with email existing@example.com already exists"));

                // When & Then
                mockMvc.perform(post("/api/users/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message")
                                                .value("User with email existing@example.com already exists"))
                                .andExpect(jsonPath("$.error").value("REGISTRATION_FAILED"));
        }

        @Test
        void testRegisterUser_InvalidInput() throws Exception {
                // Given - Invalid registration data (missing required fields)
                UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
                registrationDTO.setEmail("invalid-email"); // Invalid email format
                registrationDTO.setPassword("123"); // Password too short

                // When & Then
                mockMvc.perform(post("/api/users/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testGetUserProfile_Success() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                User user = new User();
                user.setId(userId);
                user.setEmail("test@example.com");
                user.setFirstName("John");
                user.setLastName("Doe");
                user.setPhone("+1234567890");
                user.setIsActive(true);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                when(userService.getUserById(userId)).thenReturn(Optional.of(user));

                // When & Then
                mockMvc.perform(get("/api/users/profile/{userId}", userId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User profile retrieved successfully"))
                                .andExpect(jsonPath("$.user.id").value(userId.toString()))
                                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                                .andExpect(jsonPath("$.user.firstName").value("John"))
                                .andExpect(jsonPath("$.user.lastName").value("Doe"));
        }

        @Test
        void testGetUserProfile_UserNotFound() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                when(userService.getUserById(userId)).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/users/profile/{userId}", userId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("User not found with id: " + userId))
                                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"));
        }

        @Test
        void testUpdateUserProfile_Success() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                UserUpdateDTO updateDTO = new UserUpdateDTO();
                updateDTO.setEmail("updated@example.com");
                updateDTO.setFirstName("Jane");
                updateDTO.setLastName("Smith");
                updateDTO.setPhone("+9876543210");

                User updatedUser = new User();
                updatedUser.setId(userId);
                updatedUser.setEmail("updated@example.com");
                updatedUser.setFirstName("Jane");
                updatedUser.setLastName("Smith");
                updatedUser.setPhone("+9876543210");
                updatedUser.setIsActive(true);
                updatedUser.setCreatedAt(LocalDateTime.now());
                updatedUser.setUpdatedAt(LocalDateTime.now());

                when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

                // When & Then
                mockMvc.perform(put("/api/users/profile/{userId}", userId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User profile updated successfully"))
                                .andExpect(jsonPath("$.user.email").value("updated@example.com"))
                                .andExpect(jsonPath("$.user.firstName").value("Jane"))
                                .andExpect(jsonPath("$.user.lastName").value("Smith"));
        }

        @Test
        void testUpdateUserProfile_UserNotFound() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                UserUpdateDTO updateDTO = new UserUpdateDTO();
                updateDTO.setFirstName("Jane");

                doThrow(new RuntimeException("User not found with id: " + userId))
                                .when(userService).updateUser(eq(userId), any(User.class));

                // When & Then
                mockMvc.perform(put("/api/users/profile/{userId}", userId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("User not found with id: " + userId))
                                .andExpect(jsonPath("$.error").value("UPDATE_FAILED"));
        }

        @Test
        void testUpdateUserProfile_EmailAlreadyTaken() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                UserUpdateDTO updateDTO = new UserUpdateDTO();
                updateDTO.setEmail("taken@example.com");

                doThrow(new RuntimeException("Email taken@example.com is already taken"))
                                .when(userService).updateUser(eq(userId), any(User.class));

                // When & Then
                mockMvc.perform(put("/api/users/profile/{userId}", userId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Email taken@example.com is already taken"))
                                .andExpect(jsonPath("$.error").value("UPDATE_FAILED"));
        }

        @Test
        void testDeleteUserAccount_Success() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                User deactivatedUser = new User();
                deactivatedUser.setId(userId);
                deactivatedUser.setEmail("test@example.com");
                deactivatedUser.setIsActive(false);

                when(userService.deactivateUser(userId)).thenReturn(deactivatedUser);

                // When & Then
                mockMvc.perform(delete("/api/users/account/{userId}", userId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User account deactivated successfully"))
                                .andExpect(jsonPath("$.userId").value(userId.toString()));
        }

        @Test
        void testDeleteUserAccount_UserNotFound() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                doThrow(new RuntimeException("User not found with id: " + userId))
                                .when(userService).deactivateUser(userId);

                // When & Then
                mockMvc.perform(delete("/api/users/account/{userId}", userId)
                                .with(csrf()))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("User not found with id: " + userId))
                                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"));
        }
}
