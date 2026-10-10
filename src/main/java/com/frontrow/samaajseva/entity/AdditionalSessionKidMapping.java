package com.frontrow.samaajseva.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity(name = "additional_session_kid_mapping")
@Table(name = "additional_session_kid_mapping")
@Audited
public class AdditionalSessionKidMapping extends CoreEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "additional_session_id", nullable = false)
    private AdditionalSession additionalSession;

    @Column(name = "additional_session_id", insertable = false, updatable = false)
    private Long additionalSessionId;

    @Column(name = "kid_id", nullable = false)
    private Long kidId;

}
