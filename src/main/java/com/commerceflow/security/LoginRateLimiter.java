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

        AttemptRecord record = attempts.get(key);

        if (record == null) {
            return true;
        }

        Instant now = Instant.now();

        if (now.isAfter(record.windowStart().plus(WINDOW))) {
            attempts.remove(key);
            return true;
        }

        return record.count() < MAX_ATTEMPTS;
    }

    public void recordFailure(String key) {

        Instant now = Instant.now();

        attempts.compute(key, (k, existing) -> {

            if (existing == null
                    || now.isAfter(existing.windowStart().plus(WINDOW))) {

                return new AttemptRecord(1, now);
            }

            return new AttemptRecord(
                    existing.count() + 1,
                    existing.windowStart()
            );
        });
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private record AttemptRecord(
            int count,
            Instant windowStart
    ) {
    }
}