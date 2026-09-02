package com.commerceflow.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final ConcurrentHashMap<String, AttemptRecord> attempts =
            new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        Instant now = Instant.now();

        AttemptRecord record = attempts.compute(key, (k, existing) -> {
            if (existing == null || now.isAfter(existing.windowStart().plus(WINDOW))) {
                return new AttemptRecord(1, now);
            }

            return new AttemptRecord(existing.count() + 1, existing.windowStart());
        });

        return record.count() <= MAX_ATTEMPTS;
    }

    private record AttemptRecord(int count, Instant windowStart) {
    }
}