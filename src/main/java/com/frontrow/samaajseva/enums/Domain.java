package com.frontrow.samaajseva.enums;

public enum Domain {
    FRONTROW("https://www.frontrow.co.in"),
    CONNECT("https://www.connectcourses.in"),
    ALL("https://www.frontrow.co.in");

    private final String domainUrl;

    Domain(String domainUrl){
        this.domainUrl = domainUrl;
    }

    public String getDomainUrl() {
        return domainUrl;
    }
}
