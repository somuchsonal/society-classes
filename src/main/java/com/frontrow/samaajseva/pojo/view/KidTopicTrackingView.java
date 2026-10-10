package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.TopicCompletionMetadata;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KidTopicTrackingView extends BaseView {

    private KidView kidView;

    private TopicView topicView;

    private Long sessionId;

    private SessionType sessionType;

    private TopicCompletionMetadata metadata;
}
