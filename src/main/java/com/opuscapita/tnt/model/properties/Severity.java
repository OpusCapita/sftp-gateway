package com.opuscapita.tnt.model.properties;

public enum Severity {

    INFO("Info"),
    WARNING("Warning"),
    FATAL("Fatal"),
    ERROR("Error"),
    DEBUG("Debug");

    public final String severity;

    private Severity(String _severity) {
        this.severity = _severity;
    }
}
