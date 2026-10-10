package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.CompletionType;
import com.frontrow.samaajseva.enums.ContentType;
import com.frontrow.samaajseva.enums.TopicType;
import com.frontrow.samaajseva.pojo.TopicMetadata;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicView extends BaseView {

    private String name;

    private TopicType topicType;

    private ContentType contentType;

    private CompletionType completionType;

    private ChapterView chapterView;

    private TopicMetadata metadata;

    private Integer sortOrder;

    private Boolean isDeleted;
}
