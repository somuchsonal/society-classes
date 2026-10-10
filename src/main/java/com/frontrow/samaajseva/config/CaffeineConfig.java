package com.frontrow.samaajseva.config;

import com.frontrow.samaajseva.config.properties.caffeine.CompletionPercentageCaffeineProperties;
import com.frontrow.samaajseva.config.properties.caffeine.CurrentLevelCaffeineProperties;
import com.frontrow.samaajseva.pojo.view.CompletionPercentageView;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CaffeineConfig {

    private final MeterRegistry meterRegistry;
    private final CurrentLevelCaffeineProperties currentLevelCaffeineProperties;
    private final CompletionPercentageCaffeineProperties completionPercentageCaffeineProperties;

    @Autowired
    public CaffeineConfig(MeterRegistry meterRegistry,
                          CurrentLevelCaffeineProperties currentLevelCaffeineProperties,
                          CompletionPercentageCaffeineProperties completionPercentageCaffeineProperties) {
        this.meterRegistry = meterRegistry;
        this.currentLevelCaffeineProperties = currentLevelCaffeineProperties;
        this.completionPercentageCaffeineProperties = completionPercentageCaffeineProperties;
    }

    @Bean(name = "currentLevelCaffeineCache")
    public Cache<String, CurrentLevelView> currentLevelCaffeineCache() {
        Cache<String, CurrentLevelView> cache = Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(currentLevelCaffeineProperties.getExpiry())
                .initialCapacity(currentLevelCaffeineProperties.getInitialCapacity())
                .maximumSize(currentLevelCaffeineProperties.getMaxSize())
                .build();

        CaffeineCacheMetrics.monitor(meterRegistry, cache, "currentLevelCache",
                "cacheManager", "currentLevel",
                "name", "currentLevelCache");
        return cache;
    }

    @Bean(name = "completionPercentageCaffeineCache")
    public Cache<String, CompletionPercentageView> completionPercentageCaffeineCache() {
        Cache<String, CompletionPercentageView> cache = Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(completionPercentageCaffeineProperties.getExpiry())
                .initialCapacity(completionPercentageCaffeineProperties.getInitialCapacity())
                .maximumSize(completionPercentageCaffeineProperties.getMaxSize())
                .build();

        CaffeineCacheMetrics.monitor(meterRegistry, cache, "completionPercentageCache",
                "cacheManager", "completionPercentage",
                "name", "completionPercentageCache");
        return cache;
    }
}
