package com.frontrow.samaajseva.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Getter
@Setter
@Entity(name = "cluster")
@Table(name = "cluster")
@Audited
public class Cluster extends CoreEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "city_id", insertable = false, updatable = false)
    private Long cityId;
}
