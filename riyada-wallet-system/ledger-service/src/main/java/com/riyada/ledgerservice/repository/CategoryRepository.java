package com.riyada.ledgerservice.repository;

import com.riyada.ledgerservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Find category by name
    Optional<Category> findByName(String name);

    // Find all active categories
    List<Category> findByIsActiveTrue();

    // Find all active categories ordered by name
    List<Category> findByIsActiveTrueOrderByNameAsc();

    // Check if category exists by name
    boolean existsByName(String name);

    // Find category by name and active status
    Optional<Category> findByNameAndIsActiveTrue(String name);
}
