package com.frontrow.samaajseva.cache;

import com.frontrow.samaajseva.config.properties.RedisProperties;
import com.frontrow.samaajseva.db.KidTopicTrackingDbService;
import com.frontrow.samaajseva.pojo.CacheMessage;
import com.frontrow.samaajseva.pojo.view.CompletionPercentageView;
import com.frontrow.samaajseva.pubsub.CacheRedisPublisher;
import com.frontrow.samaajseva.utils.Constants;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CompletionPercentageCache {

    private static final String prefix = "{CompletionPercentage1}_";

    private final Cache<String, CompletionPercentageView> completionPercentageCaffeineCache;
    private final RedisTemplate<String, CompletionPercentageView> completionPercentageRedisTemplate;
    private final KidTopicTrackingDbService kidTopicTrackingDbService;
    private final CacheRedisPublisher cacheRedisPublisher;
    private final RedisProperties redisProperties;

    @Autowired
    public CompletionPercentageCache(@Qualifier("completionPercentageCaffeineCache") Cache<String, CompletionPercentageView> completionPercentageCaffeineCache,
                                     @Qualifier("completionPercentageRedisTemplate") RedisTemplate<String, CompletionPercentageView> completionPercentageRedisTemplate,
                                     KidTopicTrackingDbService kidTopicTrackingDbService,
                                     CacheRedisPublisher cacheRedisPublisher, 
                                     RedisProperties redisProperties) {
        this.completionPercentageCaffeineCache = completionPercentageCaffeineCache;
        this.completionPercentageRedisTemplate = completionPercentageRedisTemplate;
        this.kidTopicTrackingDbService = kidTopicTrackingDbService;
        this.cacheRedisPublisher = cacheRedisPublisher;
        this.redisProperties = redisProperties;
    }


    public CompletionPercentageView getCompletionPercentage(Long kidId, Long categoryId) {
        String key = this.getKey(kidId, categoryId);

        CompletionPercentageView completionPercentageView = completionPercentageCaffeineCache.getIfPresent(key);

        if (completionPercentageView == null) {
            completionPercentageView = completionPercentageRedisTemplate.opsForValue().get(key);

            if (completionPercentageView == null) {
                completionPercentageView = kidTopicTrackingDbService.getCompletionPercentage(kidId, categoryId);

                if (completionPercentageView != null) {
                    completionPercentageRedisTemplate.opsForValue().set(key, completionPercentageView, redisProperties.getCompletionPercentageCacheTtl());
                }
            }
            if (completionPercentageView != null) {
                completionPercentageCaffeineCache.put(key, completionPercentageView);
            }
        }

        return completionPercentageView;
    }

    private String getKey(Long kidId, Long categoryId) {
        return prefix + kidId + Constants.UNDERSCORE + categoryId;
    }

    public void invalidateCache(Long kidId, Long categoryId) {
        List<String> keys = Collections.singletonList(this.getKey(kidId, categoryId));

        completionPercentageRedisTemplate.delete(keys);
        this.invalidateCaffeineCache(keys);
    }

    private void invalidateCaffeineCache(List<String> keys) {
        cacheRedisPublisher.publish(CacheMessage.builder()
                .cacheName(CompletionPercentageCache.class.getSimpleName())
                .keys(keys)
                .build());
    }
}
