package com.commerceflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.HttpStatusAccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )

                        .accessDeniedHandler(
                                new HttpStatusAccessDeniedHandler(
                                        HttpStatus.FORBIDDEN
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC APIs
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()


                                // USER MANAGEMENT

// Any logged-in user can view their own profile
                                .requestMatchers(
                                        "/api/users/me"
                                ).authenticated()

// Only ADMIN can view all users or a specific user
                                .requestMatchers(
                                        "/api/users",
                                        "/api/users/*"
                                ).hasRole("ADMIN")

                                // REVIEW MANAGEMENT

// CUSTOMER can create a review
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/api/v1/products/*/reviews"
                                ).hasRole("CUSTOMER")

// Anyone can view product reviews
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/v1/products/*/reviews"
                                ).permitAll()

// CUSTOMER can update their own review
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/v1/reviews/*"
                                ).hasRole("CUSTOMER")

// CUSTOMER can delete their own review
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.DELETE,
                                        "/api/v1/reviews/*"
                                ).hasRole("CUSTOMER")



                        // PRODUCT MANAGEMENT - ADMIN ONLY
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        // CATEGORY MANAGEMENT - ADMIN ONLY
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/v1/categories/**"
                        ).hasRole("ADMIN")

                        // ORDER STATS AND DASHBOARD - ADMIN ONLY
                        .requestMatchers(
                                "/api/orders/stats",
                                "/api/orders/dashboard"
                        ).hasRole("ADMIN")

                                // GET ALL ORDERS - ADMIN ONLY
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders"
                                ).hasRole("ADMIN")

                                // ORDER HISTORY - ADMIN OR ORDER OWNER
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders/*/history"
                                ).authenticated()

                        // UPDATE ORDER STATUS - ADMIN ONLY
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PATCH,
                                "/api/orders/*/status"
                        ).hasRole("ADMIN")

                        // DELETE ORDER - ADMIN ONLY
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/orders/**"
                        ).hasRole("ADMIN")

                        // CREATE ORDER - CUSTOMER ONLY
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/orders"
                        ).hasRole("CUSTOMER")

                                // CART APIs - CUSTOMER ONLY
                                .requestMatchers(
                                        "/api/cart/**"
                                ).hasRole("CUSTOMER")

                                // PAYMENT APIs - AUTHENTICATED USERS

                                // CREATE PAYMENT - CUSTOMER ONLY
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/api/payments/**"
                                ).hasRole("CUSTOMER")
                                // PUBLIC PRODUCT IMAGE FILES
                                .requestMatchers(
                                        "/uploads/products/**"
                                ).permitAll()

// EVERYTHING ELSE REQUIRES LOGIN
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}