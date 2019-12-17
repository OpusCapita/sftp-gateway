package com.opuscapita.transaction.model.properties;

public enum DocumentCategory {
    INBOUND("inbound"),
    CANONCAL("canonical"),
    ATTACHMENT("inboundAttachment");

    public final String category;

    private DocumentCategory(String _category) {
        this.category = _category;
    }


    @Override
    public String toString() {
        return this.category;
    }
}
