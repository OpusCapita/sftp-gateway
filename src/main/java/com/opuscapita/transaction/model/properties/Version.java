package com.opuscapita.transaction.model.properties;

public enum Version {
    V_1_0("1.0"),
    V_1_5("1.5"),
    V_2_0("2.0");

    public final String version;

    private Version(String _version) {
        this.version = _version;
    }

    @Override
    public String toString() {
        return this.version;
    }
}
