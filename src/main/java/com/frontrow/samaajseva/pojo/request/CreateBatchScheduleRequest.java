package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBatchScheduleRequest {

    //Not needed in requests of create batch
    private Long batchId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime startTime;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime endTime;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String venue;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @AssertFalse(message = "Start time must be before end time")
    private boolean isStartTimeBeforeEndTime(){
        return !startTime.isBefore(endTime);
    }
}
