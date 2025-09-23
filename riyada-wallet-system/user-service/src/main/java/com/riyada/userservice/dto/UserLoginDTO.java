package com.riyada.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user login request
 */
public class UserLoginDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // Default constructor
    public UserLoginDTO() {
    }

    // Constructor with parameters
    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
