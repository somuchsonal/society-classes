package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.validator.ValidUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateVideoRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @ValidUrl
    private String videoUrl;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @ValidUrl
    private String thumbnailUrl;

    private Long sessionId;

    private List<Long> kidIds;
}
