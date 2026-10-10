package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.entity.converter.LongListConverter;
import com.frontrow.samaajseva.entity.converter.ReviewListConverter;
import com.frontrow.samaajseva.entity.converter.ProgramListConverter;
import com.frontrow.samaajseva.pojo.Program;
import com.frontrow.samaajseva.pojo.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;

@Getter
@Setter
@Entity(name = "society")
@Table(name = "society")
@Audited
public class Society extends CoreEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private Cluster cluster;

    @Column(name = "cluster_id", insertable = false, updatable = false)
    private Long clusterId;

    @Column(name = "society_image_url")
    private String societyImageUrl;

    @Column(name = "society_video_url")
    private String societyVideoUrl;

    @Column(name = "programs")
    @Convert(converter = ProgramListConverter.class)
    private List<Program> programs;

    @Column(name = "instructor_ids")
    @Convert(converter = LongListConverter.class)
    private List<Long> instructorIds;

    @Column(name = "reviews")
    @Convert(converter = ReviewListConverter.class)
    @NotAudited
    private List<Review> reviews;

}
