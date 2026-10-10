package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.Level;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChapterView extends BaseView {

    private String name;

    private Long categoryId;

    private Level level;

    private Integer subLevel;

    private Integer subSubLevel;

    private ChapterType chapterType;

    private Integer sortOrder;

    private Boolean isDeleted;
}
