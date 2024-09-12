package org.example.essenceschallenge.app.infra.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${cache.duration.minutes}")
    private int duration;

    @Value("${cache.maximum.size}")
    private int maxSize;

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .evictionListener((key, value, cause) -> {
                    System.out.println("Evicted key: " + key + " due to " + cause);
                });

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("essencesById", "essences");
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }
}
