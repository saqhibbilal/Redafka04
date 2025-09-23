package com.riyada.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riyada.userservice.dto.UserRegistrationDTO;
import com.riyada.userservice.entity.User;
import com.riyada.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserRegistrationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll(); // Clean up before each test
    }

    @Test
    void testUserRegistration_CompleteFlow() throws Exception {
        // Given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "integration@example.com",
                "password123",
                "Integration",
                "Test",
                "+1234567890");

        // When
        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.user.email").value("integration@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("Integration"))
                .andExpect(jsonPath("$.user.lastName").value("Test"))
                .andExpect(jsonPath("$.user.phone").value("+1234567890"))
                .andExpect(jsonPath("$.user.isActive").value(true))
                .andExpect(jsonPath("$.user.id").exists());

        // Then - Verify user was saved in database
        User savedUser = userRepository.findByEmail("integration@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("integration@example.com", savedUser.getEmail());
        assertEquals("Integration", savedUser.getFirstName());
        assertEquals("Test", savedUser.getLastName());
        assertEquals("+1234567890", savedUser.getPhone());
        assertTrue(savedUser.getIsActive());
        assertNotNull(savedUser.getPasswordHash());
        assertNotEquals("password123", savedUser.getPasswordHash()); // Password should be hashed
    }

    @Test
    void testUserRegistration_DuplicateEmail() throws Exception {
        // Given - Create a user first
        User existingUser = new User();
        existingUser.setEmail("duplicate@example.com");
        existingUser.setPasswordHash("hashedpassword");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setIsActive(true);
        userRepository.save(existingUser);

        // When - Try to register with same email
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "duplicate@example.com",
                "password123",
                "New",
                "User",
                null);

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User with email duplicate@example.com already exists"))
                .andExpect(jsonPath("$.error").value("REGISTRATION_FAILED"));
    }
}
