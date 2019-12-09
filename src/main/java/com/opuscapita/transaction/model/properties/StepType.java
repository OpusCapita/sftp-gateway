package com.opuscapita.transaction.model.properties;

public enum StepType {
    RECEIVE("Receive"),
    PREPARE("Prepare"),
    VALIDATE("Validate"),
    REJECT("Reject"),
    CONVERT("Convert"),
    MERGE("Merge"),
    SPLIT("Split"),
    ROUTE("Route"),
    PROCESS("Process"),
    DELIVER("Deliver"),
    EXTRACT("Extract"),
    INTERNALRECEIVE("InternalReceive"),
    INTERNALDELIVER("InternalDeliver");

    public final String type;

    private StepType(String _type) {
        this.type = _type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
