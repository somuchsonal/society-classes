package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.Source;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLeadRequest {

    private String kidName;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String contact;

    private Source source;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long societyId;

    private Long categoryId;

    private String parentName;

    private Integer age;

    private String apartment;

    private String alternateContact;
}
