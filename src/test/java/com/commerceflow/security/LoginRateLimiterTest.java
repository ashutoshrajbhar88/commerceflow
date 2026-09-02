package com.commerceflow.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    @Test
    void sixthFailedAttemptShouldBeBlocked() {

        LoginRateLimiter limiter = new LoginRateLimiter();

        String key = "127.0.0.1:test@example.com";

        // First 5 failed attempts are allowed
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.isAllowed(key));
            limiter.recordFailure(key);
        }

        // Sixth attempt should be blocked
        assertFalse(limiter.isAllowed(key));
    }

    @Test
    void successfulLoginShouldResetFailedAttempts() {

        LoginRateLimiter limiter = new LoginRateLimiter();

        String key = "127.0.0.1:test@example.com";

        // Record 4 failed attempts
        for (int i = 0; i < 4; i++) {
            limiter.recordFailure(key);
        }

        assertTrue(limiter.isAllowed(key));

        // Successful login
        limiter.reset(key);

        // New attempt should be allowed again
        assertTrue(limiter.isAllowed(key));
    }
}