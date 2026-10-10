package com.frontrow.samaajseva.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "city")
@Table(name = "city")
public class City extends CoreEntity{

    @Column(name = "name", nullable = false)
    private String name;
}
