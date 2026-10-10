package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.CompletionType;
import com.frontrow.samaajseva.enums.ContentType;
import com.frontrow.samaajseva.enums.TopicType;
import com.frontrow.samaajseva.pojo.TopicMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTopicRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private TopicType topicType;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ContentType contentType;

    private CompletionType completionType = CompletionType.NONE;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long chapterId;

    private TopicMetadata metadata;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sortOrder;
}
