package com.opuscapita.transaction.model;

import com.opuscapita.transaction.utils.TxUtils;
import lombok.Data;

import java.util.Objects;

@Data
public class EventOrigin implements Tx {

    private final String title = "eventOrigin";

    private String systemNode;
    private String systemType;

    public EventOrigin(String _systemNode, String _systemType) {
        if (Objects.isNull(_systemNode))
            throw new NullPointerException("SystemNode is null");
        if (Objects.isNull(_systemType))
            throw new NullPointerException("SystemType is null");

        this.setSystemNode(_systemNode);
        this.setSystemType(_systemType);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"" + title + "\":{");
        builder.append(TxUtils.attributeAsJson("systemNode", Objects.requireNonNull(this.getSystemNode(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("systemType", Objects.requireNonNull(this.getSystemType(), "--- no value ---"), false));
        builder.append("}");
        return builder.toString();
    }
}
