package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.enums.Source;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity(name = "lead")
@Table(name = "lead")
@Audited
public class Lead extends CoreEntity {

    @Column(name = "kid_name")
    private String kidName;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(name = "society_id", insertable = false, updatable = false)
    private Long societyId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "apartment")
    private String apartment;

    @Column(name = "alternate_contact")
    private String alternateContact;

}
