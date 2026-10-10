package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAdditionalSessionRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    private LocalDate startDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Long categoryId;

    private Long instructorId;

    private String link;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @AssertFalse(message = "Start time must be before end time")
    private boolean isStartTimeBeforeEndTime(){
        if(startTime != null && endTime != null)
            return !startTime.isBefore(endTime);

        return false;
    }
}
