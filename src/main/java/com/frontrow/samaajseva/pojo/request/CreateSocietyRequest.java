package com.frontrow.samaajseva.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.pojo.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSocietyRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    private Float latitude;

    private Float longitude;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long clusterId;

    private String societyImageUrl;

    private String societyVideoUrl;

    private List<String> programs;

    private List<Long> instructorIds;

    private List<Review> reviews;
}
