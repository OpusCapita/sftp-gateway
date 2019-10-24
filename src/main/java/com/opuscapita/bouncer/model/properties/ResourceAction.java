package com.opuscapita.bouncer.model.properties;

public enum ResourceAction {
    CREATE("POST"),
    VIEW("GET"),
    EDIT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH");

    public final String action;

    ResourceAction(String _action) {
        this.action = _action;
    }
}
