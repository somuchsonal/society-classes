package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateKidRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentUserId;
}
