package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KidReportCompletionView extends BaseView {

    private KidView kidView;

    private ChapterView chapterView;

    private Long sessionId;

    private SessionType sessionType;

    private List<KidTopicTrackingView> kidTopicTrackingViewList;

    private Integer totalStars;
}
