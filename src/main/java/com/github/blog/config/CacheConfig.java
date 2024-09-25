package com.github.blog.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Value("${cache.maximum.size}")
    private int maximumSize;

    @Value("${cache.expire.after}")
    private int expireAfter;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("articles", "pagedArticles");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfter, TimeUnit.MINUTES)
                .maximumSize(maximumSize));
        return cacheManager;
    }
}
