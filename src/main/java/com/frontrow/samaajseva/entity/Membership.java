package com.frontrow.samaajseva.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "membership")
@Table(name = "membership")
@Audited
public class Membership extends CoreEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kid_id", nullable = false)
    private Kid kid;

    @Column(name = "kid_id", insertable = false, updatable = false)
    private Long kidId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(name = "society_id", insertable = false, updatable = false)
    private Long societyId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "validity_in_months", nullable = false)
    private Integer validityInMonths;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "is_partly_paid", nullable = false)
    private Boolean isPartlyPaid = Boolean.FALSE;
}
