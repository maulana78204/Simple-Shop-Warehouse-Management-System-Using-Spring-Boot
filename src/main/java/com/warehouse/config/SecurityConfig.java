package com.warehouse.config;

import com.warehouse.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Simple & Clear Security Configuration
 * 
 * ROLE PERMISSIONS:
 * - PUBLIC: /auth/register, /auth/login (Tanpa login)
 * - USER: GET /items, GET /auth/me (Setelah login)
 * - ADMIN: Full CRUD /items + semua USER access
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers().frameOptions().disable().and()
            .authorizeHttpRequests(auth -> auth
                // ==========================================
                // PUBLIC - Tidak perlu login
                // ==========================================
                .requestMatchers("/auth/register").permitAll()    // ✅ USER bisa register
                .requestMatchers("/auth/login").permitAll()       // ✅ USER bisa login
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // ==========================================
                // PROTECTED - Harus login
                // ==========================================
                // Profile - USER dan ADMIN
                .requestMatchers("GET", "/auth/me").authenticated()
                
                // Items READ - USER dan ADMIN
                .requestMatchers("GET", "/items").authenticated()
                .requestMatchers("GET", "/items/**").authenticated()
                
                // Items WRITE - ADMIN only
                .requestMatchers("POST", "/items").hasRole("ADMIN")
                .requestMatchers("PUT", "/items/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/items/**").hasRole("ADMIN")
                
                // Semua yang lain harus authenticated
                .anyRequest().authenticated()
            )
            .httpBasic();

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
