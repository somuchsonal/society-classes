package com.frontrow.samaajseva.config.properties;

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
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
@Validated
public class RedisProperties {

    @NotNull
    @DurationUnit(ChronoUnit.DAYS)
    private Duration currentLevelCacheTtl;

    @NotNull
    @DurationUnit(ChronoUnit.DAYS)
    private Duration completionPercentageCacheTtl;
}
