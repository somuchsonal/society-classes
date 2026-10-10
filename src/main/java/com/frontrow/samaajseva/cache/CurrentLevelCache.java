package com.frontrow.samaajseva.cache;

import com.frontrow.samaajseva.config.properties.RedisProperties;
import com.frontrow.samaajseva.db.KidTopicTrackingDbService;
import com.frontrow.samaajseva.pojo.CacheMessage;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.frontrow.samaajseva.pubsub.CacheRedisPublisher;
import com.frontrow.samaajseva.utils.Constants;
import com.github.benmanes.caffeine.cache.Cache;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CurrentLevelCache {

    private static final String prefix = "{CurrentLevel1}_";

    private final Cache<String, CurrentLevelView> currentLevelCaffeineCache;
    private final RedisTemplate<String, CurrentLevelView> currentLevelRedisTemplate;
    private final KidTopicTrackingDbService kidTopicTrackingDbService;
    private final CacheRedisPublisher cacheRedisPublisher;
    private final RedisProperties redisProperties;

    @Autowired
    public CurrentLevelCache(@Qualifier("currentLevelCaffeineCache") Cache<String, CurrentLevelView> currentLevelCaffeineCache,
                             @Qualifier("currentLevelRedisTemplate") RedisTemplate<String, CurrentLevelView> currentLevelRedisTemplate,
                             KidTopicTrackingDbService kidTopicTrackingDbService,
                             CacheRedisPublisher cacheRedisPublisher,
                             RedisProperties redisProperties) {
        this.currentLevelCaffeineCache = currentLevelCaffeineCache;
        this.currentLevelRedisTemplate = currentLevelRedisTemplate;
        this.kidTopicTrackingDbService = kidTopicTrackingDbService;
        this.cacheRedisPublisher = cacheRedisPublisher;
        this.redisProperties = redisProperties;
    }

    @Timed
    public CurrentLevelView getCurrentLevel(Long kidId, Long categoryId) {
        String key = this.getKey(kidId, categoryId);

        CurrentLevelView currentLevelView = currentLevelCaffeineCache.getIfPresent(key);

        if (currentLevelView == null) {
            currentLevelView = currentLevelRedisTemplate.opsForValue().get(key);

            if (currentLevelView == null) {
                currentLevelView = kidTopicTrackingDbService.getCurrentLevel(kidId, categoryId);

                if (currentLevelView != null) {
                    currentLevelRedisTemplate.opsForValue().set(key, currentLevelView, redisProperties.getCurrentLevelCacheTtl());
                }
            }
            if (currentLevelView != null) {
                currentLevelCaffeineCache.put(key, currentLevelView);
            }
        }

        return currentLevelView;
    }

    private String getKey(Long kidId, Long categoryId) {
        return prefix + kidId + Constants.UNDERSCORE + categoryId;
    }

    public void invalidateCache(Long kidId, Long categoryId) {
        List<String> keys = Collections.singletonList(this.getKey(kidId, categoryId));

        currentLevelRedisTemplate.delete(keys);
        this.invalidateCaffeineCache(keys);
    }

    private void invalidateCaffeineCache(List<String> keys) {
        cacheRedisPublisher.publish(CacheMessage.builder()
                           .cacheName(CurrentLevelCache.class.getSimpleName())
                           .keys(keys)
                           .build());
    }
}
