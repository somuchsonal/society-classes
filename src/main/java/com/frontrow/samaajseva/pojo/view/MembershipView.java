package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MembershipView extends BaseView {

    private KidView kidView;

    private LocalDate startDate;

    private LocalDate endDate;

    private SocietyView societyView;

    private Long categoryId;

    private Integer validityInMonths;

    private Long orderId;

    private Boolean isPartlyPaid;
}
