package com.frontrow.samaajseva.config;

import com.frontrow.samaajseva.pojo.CacheMessage;
import com.frontrow.samaajseva.pojo.view.CompletionPercentageView;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.frontrow.samaajseva.pubsub.CacheRedisSubscriber;
import com.frontrow.samaajseva.serializer.SnappyRedisSerializer;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

@Configuration
public class RedisConfig {

    @Value("${redis.host:localhost}")
    private String redisHost;
    @Value("${redis.port:6379}")
    private int redisPort;
    @Value("${redis.password}")
    private String redisPassword;
    @Value("${redis.useSsl:false}")
    private boolean redisUseSsl;
    @Value("${redis.cluster}")
    private String redisCluster;
    @Value("${redis.clusterPassword}")
    private String redisClusterPassword;
    @Value("${redis.clusterSsl:true}")
    private boolean redisClusterSsl;
    @Value("${redis.clusterMode:true}")
    private boolean redisClusterMode;
    @Value("${redis.retryAttempts:3}")
    private int redisRetryAttempts;
    @Value("${redis.retryInterval:1500}")
    private int redisRetryInterval;
    @Value("${redis.cache.topic:contentServiceCacheTopic}")
    private String redisCacheTopic;

    private final CacheRedisSubscriber cacheRedisSubscriber;

    @Autowired
    public RedisConfig(CacheRedisSubscriber cacheRedisSubscriber) {
        this.cacheRedisSubscriber = cacheRedisSubscriber;
    }

    @Bean
    public Config getRedissonConfig() {
        Config config = new Config();

        if (redisClusterMode) {
            String[] nodes = Arrays.stream(StringUtils.splitPreserveAllTokens(redisCluster, ","))
                    .map(String::trim)
                    .map(String::valueOf)
                    .map(node -> {
                        if (redisClusterSsl)
                            return "rediss://" + node;
                        else
                            return "redis://" + node;
                    }).toArray(String[]::new);
            if (StringUtils.isNotBlank(redisClusterPassword))
                config.useClusterServers()
                        .addNodeAddress(nodes)
                        .setPassword(redisClusterPassword)
                        .setRetryAttempts(redisRetryAttempts)
                        .setRetryInterval(redisRetryInterval);
            else
                config.useClusterServers()
                        .addNodeAddress(nodes)
                        .setRetryAttempts(redisRetryAttempts)
                        .setRetryInterval(redisRetryInterval);
        }
        else {
            String prefix = redisUseSsl ? "rediss://" : "redis://";
            String address = prefix + redisHost + ":" + redisPort;

            if (StringUtils.isNotBlank(redisPassword))
                config.useSingleServer()
                        .setAddress(address)
                        .setPassword(redisPassword)
                        .setRetryAttempts(redisRetryAttempts)
                        .setRetryInterval(redisRetryInterval);
            else
                config.useSingleServer()
                        .setAddress(address)
                        .setRetryAttempts(redisRetryAttempts)
                        .setRetryInterval(redisRetryInterval);
        }

        return config;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws Exception {
        RedissonConnectionFactory redissonConnectionFactory = new RedissonConnectionFactory(getRedissonConfig());
        redissonConnectionFactory.afterPropertiesSet();

        return redissonConnectionFactory;
    }

    @Bean
    public RedissonClient redissonClient() {
        return Redisson.create(getRedissonConfig());
    }

    @Bean
    @Qualifier("cacheRedisTemplate")
    public RedisTemplate<String, CacheMessage> cacheRedisTemplate() throws Exception {
        final RedisTemplate<String, CacheMessage> template =  new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(CacheMessage.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Qualifier("currentLevelRedisTemplate")
    public RedisTemplate<String, CurrentLevelView> currentLevelRedisTemplate() throws Exception {
        final RedisTemplate<String, CurrentLevelView> template =  new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new SnappyRedisSerializer<>(new Jackson2JsonRedisSerializer<>(CurrentLevelView.class)));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Qualifier("completionPercentageRedisTemplate")
    public RedisTemplate<String, CompletionPercentageView> completionPercentageRedisTemplate() throws Exception {
        final RedisTemplate<String, CompletionPercentageView> template =  new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new SnappyRedisSerializer<>(new Jackson2JsonRedisSerializer<>(CompletionPercentageView.class)));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(cacheRedisSubscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() throws Exception {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(redisCacheTopic);
    }
}
