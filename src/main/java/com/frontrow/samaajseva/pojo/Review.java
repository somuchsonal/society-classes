package com.frontrow.samaajseva.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.frontrow.samaajseva.enums.Level;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Review {
    private String kidName;

    private String kidVideoUrl;

    private String parentTestimonial;

    private Level kidLevel;

    private Integer kidLearningSince;

    private Integer kidAge;
}
