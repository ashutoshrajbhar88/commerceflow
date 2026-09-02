package com.commerceflow.security;

import com.commerceflow.user.User;
import com.commerceflow.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // TEMPORARY DEBUG
        System.out.println("========== JWT REQUEST ==========");
        System.out.println("REQUEST: "
                + request.getMethod()
                + " "
                + request.getRequestURI());

        System.out.println("AUTH HEADER PRESENT: "
                + (authHeader != null));

        if (authHeader != null) {
            System.out.println("TOKEN LENGTH: "
                    + authHeader.length());
        }

        System.out.println("=================================");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(7);

            System.out.println("========== JWT PROCESSING ==========");
            System.out.println("TOKEN RECEIVED: true");

            String email = jwtService.extractEmail(token);

            System.out.println("EMAIL FROM TOKEN: " + email);

            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            System.out.println("USER FOUND: " + (user != null));

            if (user != null
                    && jwtService.isTokenValid(token, user)
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name()
                                        )
                                )
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                System.out.println("JWT AUTHENTICATION SUCCESS");
                System.out.println("USER ROLE: "
                        + user.getRole());
                System.out.println("AUTHORITIES: "
                        + authentication.getAuthorities());
            }

            System.out.println("====================================");

        } catch (Exception e) {

            System.out.println("========== JWT ERROR ==========");

            System.out.println("EXCEPTION: "
                    + e.getClass().getName());

            System.out.println("MESSAGE: "
                    + e.getMessage());

            e.printStackTrace();

            System.out.println("===============================");
        }

        filterChain.doFilter(request, response);
    }
}