package com.frontrow.samaajseva.utils;

public class Constants {

    public static final int DEFAULT_PAGE_SIZE = 1000;

    //IMPORTANT: Don't change these
    public static final String KAFKA_RETRY_TOPIC_SUFFIX = "_RETRY";
    public static final String KAFKA_ERROR_TOPIC_SUFFIX = "_ERROR";

    public static final String ALL = "ALL";

    public static final Integer MAX_NON_COMPACT_FORMATTED_POINTS = 99999;
    public static final String SYSTEM = "SYSTEM";

    //Request Headers
    public static final String HEADER_REQUEST_ID = "x-request-id";
    public static final String HEADER_USER_AGENT = "x-user-agent";
    public static final String HEADER_DOMAIN = "x-domain";
    public static final String HEADER_USER_ID = "x-user-id";

    //Symbols
    public static final String UNDERSCORE = "_";
    public static final String SLASH = "/";
    public static final String SPACE = " ";
    public static final String DOT = ".";
    public static final String SEPARATOR = " - ";

    //Notification Texts
    public static final String DAY = "Day";
    public static final String TIME = "Time";
    public static final String VENUE = "Venue";

}
