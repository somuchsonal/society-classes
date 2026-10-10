package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.entity.converter.TopicMetadataConverter;
import com.frontrow.samaajseva.enums.CompletionType;
import com.frontrow.samaajseva.enums.ContentType;
import com.frontrow.samaajseva.enums.TopicType;
import com.frontrow.samaajseva.pojo.TopicMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity(name = "topic")
@Table(name = "topic")
@Audited
public class Topic extends CoreEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "topic_type", nullable = false)
    private TopicType topicType;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_type", nullable = false)
    private CompletionType completionType = CompletionType.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "chapter_id", insertable = false, updatable = false)
    private Long chapterId;

    @Column(name = "metadata")
    @Convert(converter = TopicMetadataConverter.class)
    private TopicMetadata metadata;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
