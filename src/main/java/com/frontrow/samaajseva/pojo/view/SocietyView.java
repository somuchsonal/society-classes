package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.pojo.Review;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocietyView extends BaseView {

    private String name;

    private String address;

    private Float latitude;

    private Float longitude;

    private ClusterView clusterView;

    private String societyImageUrl;

    private String societyVideoUrl;

    private List<String> programs;

    private List<Long> instructorIds;

    private List<Review> reviews;

}
