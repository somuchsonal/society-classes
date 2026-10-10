package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoView extends BaseView {

    private String videoUrl;

    private String thumbnailUrl;

    private SessionView sessionView;
}
