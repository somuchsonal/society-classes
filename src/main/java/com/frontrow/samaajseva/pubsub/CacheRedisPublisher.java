package com.frontrow.samaajseva.pubsub;

import com.frontrow.samaajseva.pojo.CacheMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CacheRedisPublisher {

    private final RedisTemplate<String, CacheMessage> redisTemplate;
    private final ChannelTopic topic;

    @Autowired
    public CacheRedisPublisher(@Qualifier("cacheRedisTemplate") RedisTemplate<String, CacheMessage> redisTemplate,
                               ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(CacheMessage cacheMessage) {
        redisTemplate.convertAndSend(topic.getTopic(), cacheMessage);
    }
}
