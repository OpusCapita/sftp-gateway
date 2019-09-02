package com.opuscapita.tnt.model.properties;

public enum StepStatus {

    COMPLETED("Completed"),
    ACCEPTED("Accepted"),
    PROCESSING("Processing"),
    RUNNING("Running"),
    WAITING("Waiting"),
    FAILED("Failed"),
    REJECTED("Rejected");

    public final String status;

    private StepStatus(String _status) {
        this.status = _status;
    }
}