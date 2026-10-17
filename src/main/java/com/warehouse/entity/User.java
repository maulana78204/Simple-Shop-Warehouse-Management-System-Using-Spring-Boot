package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Entity - Sesuai dengan schema PostgreSQL
 * ID: UUID (not numeric sequence)
 * Mapping dengan table: warehouse.users
 */
@Entity
@Table(name = "users", schema = "warehouse", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username", name = "uq_users_username"),
        @UniqueConstraint(columnNames = "email", name = "uq_users_email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements org.springframework.security.core.userdetails.UserDetails {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt encrypted

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "ROLE_USER"; // ROLE_USER, ROLE_MERCHANT, ROLE_ADMIN

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "account_locked")
    @Builder.Default
    private Boolean accountLocked = false;

    @Column(name = "login_attempts")
    @Builder.Default
    private Integer loginAttempts = 0;

    @Column(name = "last_locked_time")
    private LocalDateTime lastLockedTime;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // UserDetails Implementation
    @Override
    public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked && active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

}

