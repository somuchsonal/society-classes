package com.frontrow.samaajseva.exception;

import org.springframework.lang.Nullable;

public enum ErrorCodes {

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NO_ENTITY_FOUND(501, "No entity found for the request"),
    SQL_EXCEPTION(564, "SQLIntegrityConstraintViolationException"),
    CONSTRAINT_VIOLATION_EXCEPTION(565, "ConstraintViolationException"),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(566, "DataIntegrityViolationException"),
    METHOD_ARGUMENT_NOT_VALID(567, "MethodArgumentNotValidException"),
    EXCEPTION(568, "Exception"),
    FONT_FILE_NOT_FOUND(569, "FontFileNotFoundException"),
    BATCH_ID_NOT_PROVIDED(502, "Batch Id is not provided"),
    INSTRUCTOR_CATEGORY_MISMATCH(503, "Given instructor is not mapped to the given category"),
    BATCH_ID_SHOULD_BE_EMPTY(504, "Batch id should be empty in create batch request"),
    VENUE_CONFLICTS_IN_SCHEDULE(505, "There are conflicts in venue of this schedule"),
    INSTRUCTOR_CONFLICTS_IN_SCHEDULE(506, "There are conflicts for instructor in this schedule"),
    BATCH_CONFLICTS_IN_SCHEDULE(507, "Conflicting schedules requested in this batch"),
    NO_ACTIVE_MEMBERSHIP(508, "Given kid has no active membership for this to happen"),
    UNUSED_MEMBERSHIP_ALREADY_PRESENT(509, "Unused membership already present for this kid and category"),
    KID_ALREADY_ADDED_TO_BATCH(510, "This kid is already added to this batch"),
    NOT_ELIGIBLE_FOR_COMPENSATORY_CLASS(511, "This kid is currently not eligible for compensatory class"),
    START_TIME_SHOULD_BE_BEFORE_END_TIME(512, "Start time must be before end time"),
    MEMBERSHIP_BATCH_CATEGORY_MISMATCH(503, "The categories for membership and batch dont match"),;

    private final int value;
    private final String reasonPhrase;

    ErrorCodes(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        return this.value + " " + this.name();
    }

    public static ErrorCodes valueOf(int statusCode) {
        ErrorCodes status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        } else {
            return status;
        }
    }

    @Nullable
    public static ErrorCodes resolve(int statusCode) {
        ErrorCodes[] var1 = values();

        for (ErrorCodes status : var1) {
            if (status.value == statusCode) {
                return status;
            }
        }

        return null;
    }
}
