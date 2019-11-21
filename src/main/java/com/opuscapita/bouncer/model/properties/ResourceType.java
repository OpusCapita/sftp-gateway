package com.opuscapita.bouncer.model.properties;

public enum ResourceType {
    UI("ui"),
    REST("rest");

    public final String type;

    ResourceType(String _type) {
        this.type = _type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
