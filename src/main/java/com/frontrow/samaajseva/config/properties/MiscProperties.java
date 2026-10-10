package com.frontrow.samaajseva.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "misc")
@Getter
@Setter
@Validated
public class MiscProperties {

    @NotNull
    private Integer populateSessionsForMonths;

    @NotNull
    private Integer compensatoryClassesPerMonth;

    @NotNull
    private Integer bufferDaysCount;
}
