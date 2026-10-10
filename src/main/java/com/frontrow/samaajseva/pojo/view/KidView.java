package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KidView extends BaseView {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Long parentUserId;
}
