package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventNotificationRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String eventName;

    private Long userId;

    private String deviceId;

    private String callingCode;

    private String contact;

    private String toEmail;

    private Map<String, String> templateData;

    private Map<String, String> chatButtonData;

    private List<String> templateDataList;

    private boolean overrideUserPreference;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String domain;

    private Boolean isOtpRequest;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggeredAt;

    private TriggeredForEntity triggeredForEntity;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriggeredForEntity {

        @NotNull
        private Long entityId;

        @NotBlank
        private String entityName;
    }
}
