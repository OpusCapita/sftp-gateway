package com.opuscapita.transaction.model.inbound;

import com.opuscapita.transaction.model.Tx;
import com.opuscapita.transaction.utils.TxUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class ServiceProfile implements Tx {

    private final String title = "serviceProfile";

    private String id;
    private String businessPartnerId;
    private String senderBusinessPartnerId;
    private String gatewayId;
    private Map<String, String> protocolAttributes;

    public ServiceProfile(
            String _id,
            String _businessPartnerId,
            String _senderBusinessPartnerId,
            String _gatewayId
    ) {
        if (Objects.isNull(_id))
            throw new NullPointerException("ID is null");
        if (Objects.isNull(_businessPartnerId))
            throw new NullPointerException("BusinessPartnerId is null");
        if (Objects.isNull(_senderBusinessPartnerId))
            throw new NullPointerException("SenderBusinessPartnerId is null");
        if (Objects.isNull(_gatewayId))
            throw new NullPointerException("GatewayId is null");

        this.setId(id);
        this.setBusinessPartnerId(_businessPartnerId);
        this.setSenderBusinessPartnerId(_senderBusinessPartnerId);
        this.setGatewayId(_gatewayId);
        this.setProtocolAttributes(new HashMap<>());
    }

    public Map<String, String> addProtocolAttribute(String key, String value) {
        if (Objects.isNull(this.protocolAttributes)) {
            this.protocolAttributes = new HashMap<>();
        }
        this.protocolAttributes.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
        return this.protocolAttributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"" + title + "\":{");
        builder.append(TxUtils.attributeAsJson("id", Objects.requireNonNull(this.getId(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("businessPartnerId", Objects.requireNonNull(this.getBusinessPartnerId(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("senderBusinessPartnerId", Objects.requireNonNull(this.getSenderBusinessPartnerId(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("gatewayId", Objects.requireNonNull(this.getGatewayId(), "--- no value ---")));
        builder.append("{\"protocolAttributes\":[");
        for (String key : this.getProtocolAttributes().keySet()) {
            builder.append("{");
            builder.append(TxUtils.attributeAsJson("key", Objects.requireNonNull(key, "--- no value ---")));
            builder.append(TxUtils.attributeAsJson("value", Objects.requireNonNull(this.getProtocolAttributes().get(key), "--- no value ---"), false));
            builder.append("}");
        }
        builder.append("]");
        builder.append("}}");
        return builder.toString();
    }
}
