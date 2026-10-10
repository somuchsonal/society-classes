package com.frontrow.samaajseva.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "event.notification")
@Getter
@Setter
@Validated
public class EventNotificationProperties {

    @NotBlank
    private String societyUserOnboardingEventName;

    @NotBlank
    public String frontrowSessionCancellationEventName;

    @NotBlank
    public String batchKidEnrolmentSingleScheduleEventName;

    @NotBlank
    public String batchKidEnrolmentTwoSchedulesEventName;

    @NotBlank
    public String instructorDelayEventName;

    @NotBlank
    public String holidaySessionCancellationEventName;
}
