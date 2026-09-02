package com.commerceflow.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    @Test
    void sixthAttemptShouldBeBlocked() {

        LoginRateLimiter limiter = new LoginRateLimiter();

        String key = "127.0.0.1:test@example.com";

        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.isAllowed(key));
        }

        assertFalse(limiter.isAllowed(key));
    }
}