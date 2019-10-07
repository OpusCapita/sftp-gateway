package com.opuscapita.transaction.model.properties;

public enum DocumentRefType {
    BLOB("blob"),
    SIRIUS("sirius");

    public final String type;

    private DocumentRefType(String _type) {
        this.type = _type;
    }
}
