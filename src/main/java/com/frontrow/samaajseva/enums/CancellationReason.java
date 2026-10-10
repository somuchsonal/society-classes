package com.frontrow.samaajseva.enums;

public enum CancellationReason {
    INSTRUCTOR_UNAVAILABLE("Due to instructor unavailability"),
    GAZETTED_HOLIDAY("Due to a gazetted holiday"),
    MISCELLANEOUS("Due to unforeseen reasons");

    private final String reasonText;

    CancellationReason(String reasonText){
        this.reasonText = reasonText;
    }

    public String getReasonText() {
        return reasonText;
    }
}
