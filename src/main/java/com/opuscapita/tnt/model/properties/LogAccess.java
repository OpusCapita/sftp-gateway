package com.opuscapita.tnt.model.properties;

public enum LogAccess {
    NONE("None"),
    RECEIVER("Receiver"),
    SENDER("Sender"),
    BOTH("Both");

    public final String access;

    private LogAccess(String _access) {
        this.access = _access;
    }
}
