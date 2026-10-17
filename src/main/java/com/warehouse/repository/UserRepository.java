package com.warehouse.repository;

import com.warehouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository untuk User entity
 * ID Type: UUID (unique identifier, not numeric)
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Update last login time
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") UUID userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);

    /**
     * Deactivate user
     */
    @Modifying
    @Query("UPDATE User u SET u.active = false WHERE u.id = :userId")
    void deactivateUser(@Param("userId") UUID userId);

}
