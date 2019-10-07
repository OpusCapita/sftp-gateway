package com.opuscapita.transaction.model.properties;

public enum EntityType {
    DOCUMENT("document"),
    CONTAINER("container");

    public final String type;

    private EntityType(String _type) {
        this.type = _type;
    }
}
