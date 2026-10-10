package com.frontrow.samaajseva.config;

import com.frontrow.samaajseva.config.properties.kafka.KafkaProducerProperties;
import com.frontrow.samaajseva.config.properties.kafka.KafkaSocietyMembershipPurchaseEventConsumerProperties;
import com.frontrow.samaajseva.pojo.request.EventNotificationRequest;
import com.frontrow.samaajseva.pojo.request.SocietyMembershipPurchaseEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${kafka.bootstrapServers:localhost:9092}")
    private String bootstrapServers;
    @Value("${kafka.producer.connections.max.idle.mins:10}")
    private int maxIdleMins;
    @Value("${kafka.producer.retries:1}")
    private int retries;
    @Value("${kafka.producer.delivery.timeout.ms:30000}")
    private int deliveryTimeoutMs;
    @Value("${kafka.producer.acks:all}")
    private String acks;

    private final KafkaSocietyMembershipPurchaseEventConsumerProperties kafkaSocietyMembershipPurchaseEventConsumerProperties;
    private final KafkaProducerProperties kafkaProducerProperties;

    @Autowired
    public KafkaConfig(KafkaSocietyMembershipPurchaseEventConsumerProperties kafkaSocietyMembershipPurchaseEventConsumerProperties,
                       KafkaProducerProperties kafkaProducerProperties) {
        this.kafkaSocietyMembershipPurchaseEventConsumerProperties = kafkaSocietyMembershipPurchaseEventConsumerProperties;
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    @Bean(value = "societyMembershipPurchaseEventKafkaTemplate")
    public KafkaTemplate<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseEventKafkaTemplate() {
        return new KafkaTemplate<>(societyMembershipPurchaseEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, maxIdleMins*60*1000);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean("societyMembershipPurchaseEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SocietyMembershipPurchaseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(societyMembershipPurchaseEventConsumerFactory());
        factory.setConcurrency(kafkaSocietyMembershipPurchaseEventConsumerProperties.getConcurrency());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseEventConsumerFactory() {
        JsonDeserializer<SocietyMembershipPurchaseEvent> deserializer = new JsonDeserializer<>(SocietyMembershipPurchaseEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaSocietyMembershipPurchaseEventConsumerProperties.getGroupId());
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, kafkaSocietyMembershipPurchaseEventConsumerProperties.getConnectionsMaxIdleMins().toMinutes()*60*1000);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props,
                                                 new StringDeserializer(),
                                                 deserializer);
    }

    @Bean("societyMembershipPurchaseRetryKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseRetryKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SocietyMembershipPurchaseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(societyMembershipPurchaseRetryEventConsumerFactory());
        factory.setConcurrency(kafkaSocietyMembershipPurchaseEventConsumerProperties.getRetryConcurrency());
        factory.setCommonErrorHandler(this.societyMembershipPurchaseDefaultErrorHandler());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseRetryEventConsumerFactory() {
        JsonDeserializer<SocietyMembershipPurchaseEvent> deserializer = new JsonDeserializer<>(SocietyMembershipPurchaseEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaSocietyMembershipPurchaseEventConsumerProperties.getGroupId() + "_retry");
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, kafkaSocietyMembershipPurchaseEventConsumerProperties.getConnectionsMaxIdleMins().toMinutes()*60*1000);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props,
                                                 new StringDeserializer(),
                                                 deserializer);
    }

    private DefaultErrorHandler societyMembershipPurchaseDefaultErrorHandler() {
        FixedBackOff fixedBackOff = new FixedBackOff(kafkaSocietyMembershipPurchaseEventConsumerProperties.getBackoffInterval().getSeconds()*1000,
                                                     kafkaSocietyMembershipPurchaseEventConsumerProperties.getMaxRetries());

        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(societyMembershipPurchaseEventKafkaTemplate());

        return new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
    }

    @Bean(value = "eventNotificationKafkaTemplate")
    public KafkaTemplate<String, EventNotificationRequest> eventNotificationKafkaTemplate() {
        return new KafkaTemplate<>(eventNotificationProducerFactory());
    }

    @Bean
    public ProducerFactory<String, EventNotificationRequest> eventNotificationProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, kafkaProducerProperties.getConnectionsMaxIdle().toMinutes() * 60 * 1000);
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerProperties.getRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProducerProperties.getAcks());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, Math.toIntExact(kafkaProducerProperties.getDeliveryTimeout().toMillis()));
        return new DefaultKafkaProducerFactory<>(configProps);
    }

}
