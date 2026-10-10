package com.frontrow.samaajseva.config.properties.caffeine;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "completion.percentage.caffeine")
@Getter
@Setter
@Validated
public class CompletionPercentageCaffeineProperties extends BaseCaffeineProperties {

}
