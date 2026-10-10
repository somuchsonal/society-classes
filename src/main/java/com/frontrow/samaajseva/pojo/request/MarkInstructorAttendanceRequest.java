package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frontrow.samaajseva.enums.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarkInstructorAttendanceRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Long instructorId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Long sessionId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    SessionType sessionType;

    Double latitude;

    Double longitude;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @AssertFalse(message = "Location data should be provided for session type BATCH_SESSION")
    private boolean isLocationDataProvided() {
        return SessionType.BATCH_SESSION.equals(this.sessionType) && (latitude == null || longitude == null);
    }
}
