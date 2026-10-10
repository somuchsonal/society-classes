package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBatchRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long societyId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long instructorId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CreateBatchScheduleRequest> createBatchScheduleRequestList;

    //The correct request body should return false
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @AssertFalse(message = "Batch Id should be null in batch schedule request list")
    private boolean checkBatchIdInScheduleRequest() {
         return !CollectionUtils.isEmpty(this.createBatchScheduleRequestList) && !CollectionUtils.isEmpty(this.getCreateBatchScheduleRequestList().stream().filter(request -> request.getBatchId() != null).collect(Collectors.toList()));
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @AssertFalse(message = "Start time must be before end time")
    private boolean isStartTimeBeforeEndTime() {
        return !CollectionUtils.isEmpty(this.createBatchScheduleRequestList) && !CollectionUtils.isEmpty(this.getCreateBatchScheduleRequestList().stream().filter(request -> !request.getStartTime().isBefore(request.getEndTime())).collect(Collectors.toList()));
    }
}
