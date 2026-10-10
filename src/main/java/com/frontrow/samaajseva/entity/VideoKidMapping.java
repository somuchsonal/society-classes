package com.frontrow.samaajseva.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "video_kid_mapping")
@Table(name = "video_kid_mapping")
public class VideoKidMapping extends CoreEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "video_id", insertable = false, updatable = false)
    private Long videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kid_id")
    private Kid kid;

    @Column(name = "kid_id", insertable = false, updatable = false)
    private Long kidId;
}
