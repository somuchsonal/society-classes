package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.entity.converter.TopicCompletionMetadataConverter;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.TopicCompletionMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Setter
@Getter
@Entity(name = "kid_topic_tracking")
@Table(name = "kid_topic_tracking")
@Audited
public class KidTopicTracking extends CoreEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kid_id", nullable = false)
    private Kid kid;

    @Column(name = "kid_id", insertable = false, updatable = false)
    private Long kidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "topic_id", insertable = false, updatable = false)
    private Long topicId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column(name = "metadata")
    @Convert(converter = TopicCompletionMetadataConverter.class)
    private TopicCompletionMetadata metadata;
}
