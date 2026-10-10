package com.frontrow.samaajseva.pubsub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontrow.samaajseva.cache.CurrentLevelCache;
import com.frontrow.samaajseva.pojo.CacheMessage;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CacheRedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final Cache<String, CurrentLevelView> currentLevelCaffeineCache;

    @Autowired
    public CacheRedisSubscriber(@Qualifier("currentLevelCaffeineCache")Cache<String, CurrentLevelView> currentLevelCaffeineCache) {
        this.currentLevelCaffeineCache = currentLevelCaffeineCache;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CacheMessage cacheMessage =
                    objectMapper.readValue(message.getBody(), new TypeReference<>() {
                    });

            if (cacheMessage.getCacheName().equalsIgnoreCase(CurrentLevelCache.class.getSimpleName())) {
                if (cacheMessage.getKeys() != null)
                    currentLevelCaffeineCache.invalidateAll(cacheMessage.getKeys());
                if (cacheMessage.getMap() != null) {
                    Map<String, CurrentLevelView> map = new HashMap<>();
                    for (Map.Entry<String, Object> entry : cacheMessage.getMap().entrySet()) {
                        map.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), new TypeReference<CurrentLevelView>() {
                        }));
                    }
                    currentLevelCaffeineCache.putAll(map);
                }
            }

        } catch (IOException e) {
            log.error("Error in de-serializing message: {}. Exception is: {}", message, e);
        }

    }
}

