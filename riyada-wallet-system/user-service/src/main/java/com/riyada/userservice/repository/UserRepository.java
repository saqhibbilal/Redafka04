package com.riyada.userservice.repository;

import com.riyada.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find active user by email
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    /**
     * Find user by email and active status
     */
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);

    /**
     * Count active users
     */
    long countByIsActiveTrue();

    /**
     * Find users by first name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    java.util.List<User> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);

    /**
     * Find users by last name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    java.util.List<User> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);
}
