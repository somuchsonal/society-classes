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
public class UpdateChapterRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long chapterId;

    private String name;

    private Level level;

    private Integer subLevel;

    private Integer subSubLevel;

    private ChapterType chapterType;

    private Integer sortOrder;
}
