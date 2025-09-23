package com.riyada.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riyada.userservice.dto.UserRegistrationDTO;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.message").value("User with email existing@example.com already exists"))
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
}
