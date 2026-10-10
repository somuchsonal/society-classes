package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchView extends BaseView {

    private SocietyView societyView;

    private Long instructorId;

    private Long categoryId;

    private List<BatchScheduleView> batchScheduleViewList;

    private Boolean isDeleted;

}
