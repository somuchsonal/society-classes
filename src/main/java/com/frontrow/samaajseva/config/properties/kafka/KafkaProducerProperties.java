package com.frontrow.samaajseva.config.properties.kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "kafka.producer")
@Getter
@Setter
@Validated
public class KafkaProducerProperties {

    @NotNull
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration connectionsMaxIdle;

    @NotNull
    private Integer retries;

    @NotNull
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration deliveryTimeout;

    @NotBlank
    private String acks;
}
