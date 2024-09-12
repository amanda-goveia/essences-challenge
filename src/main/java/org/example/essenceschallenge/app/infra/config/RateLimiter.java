package org.example.essenceschallenge.app.infra.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
public class RateLimiter implements HandlerInterceptor {

    private final int capacity = 5;
    private final Bucket bucket;

    public RateLimiter() {
        this.bucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity)
                        .refillGreedy(capacity, Duration.ofMinutes(1))
                        .initialTokens(capacity))
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/api/v1/essences")) {
            if(isTokenAvailable())
                return true;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests - rate limit exceeded");
            return false;
        }
        return true;
    }

    private boolean isTokenAvailable() {
        return bucket.tryConsume(1);
    }
}


