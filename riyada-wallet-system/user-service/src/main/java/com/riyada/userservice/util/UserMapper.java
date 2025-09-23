package com.riyada.userservice.util;

import com.riyada.userservice.dto.UserRegistrationDTO;
import com.riyada.userservice.dto.UserResponseDTO;
import com.riyada.userservice.dto.UserUpdateDTO;
import com.riyada.userservice.entity.User;

public class UserMapper {

    /**
     * Convert User entity to UserResponseDTO
     */
    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    /**
     * Convert UserRegistrationDTO to User entity
     */
    public static User toUser(UserRegistrationDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword()); // Will be encoded in service
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setIsActive(true);

        return user;
    }

    /**
     * Update User entity with UserUpdateDTO data
     */
    public static void updateUserFromDTO(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) {
            return;
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
    }
}
