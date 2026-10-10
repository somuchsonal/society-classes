package com.frontrow.samaajseva.enums;

public enum Level {
    BEGINNER(0),
    INTERMEDIATE(1),
    EXPERT(2);

    private Integer order;

    Level(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }
}
