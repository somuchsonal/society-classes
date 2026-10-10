package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.TopicCompletionMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarkTopicAsCompleteRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long kidId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long topicId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sessionId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private SessionType sessionType;

    //To be sent when the chapter is complete and chapter type is minor/major report
    private Long chapterId;

    private TopicCompletionMetadata metadata;
}
