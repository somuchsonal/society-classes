package com.frontrow.samaajseva.config.properties.kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Validated
public class ConsumerBaseProperties {

    @NotBlank
    private String groupId;

    @NotNull
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration connectionsMaxIdleMins;

    @NotNull
    private Integer concurrency;

    @NotNull
    private Boolean allowAutoCreateTopics;

    @NotNull
    private Integer retryConcurrency;

    @NotNull
    private Integer maxRetries;

    @NotNull
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration backoffInterval;

}
