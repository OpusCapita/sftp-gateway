package com.opuscapita.transaction.model.inbound;

import com.opuscapita.transaction.model.Tx;
import lombok.Data;

import java.util.Objects;

@Data
public class Inbound implements Tx {
    private final String title = "inbound";
    private ServiceProfile serviceProfile;

    public Inbound(ServiceProfile _serviceProfile) {
        if (Objects.isNull(_serviceProfile))
            throw new NullPointerException("ServiceProfile is null");

        this.setServiceProfile(_serviceProfile);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"" + title + "\":{");
        builder.append(Objects.requireNonNull(this.serviceProfile, "").toString());
        builder.append("}");
        return builder.toString();
    }
}
