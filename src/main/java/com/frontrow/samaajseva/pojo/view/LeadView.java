package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.Source;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeadView extends BaseView {

    private String kidName;

    private String contact;

    private Source source;

    private SocietyView societyView;

    private Long categoryId;

    private String parentName;

    private Integer age;

    private String apartment;

    private String alternateContact;

}
