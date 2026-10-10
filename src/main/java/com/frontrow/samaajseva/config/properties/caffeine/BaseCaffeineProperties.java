package com.frontrow.samaajseva.config.properties.caffeine;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class BaseCaffeineProperties {

    @NotNull
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration expiry;

    @NotNull
    private Integer initialCapacity;

    @NotNull
    private Integer maxSize;
}
