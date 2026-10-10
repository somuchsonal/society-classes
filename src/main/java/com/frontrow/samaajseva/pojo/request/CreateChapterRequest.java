package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateChapterRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Level level;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer subLevel;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer subSubLevel;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ChapterType chapterType;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sortOrder;
}
