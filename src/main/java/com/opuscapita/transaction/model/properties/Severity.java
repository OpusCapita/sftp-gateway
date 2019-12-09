package com.opuscapita.transaction.model.properties;

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

    @Override
    public String toString() {
        return this.severity;
    }
}
