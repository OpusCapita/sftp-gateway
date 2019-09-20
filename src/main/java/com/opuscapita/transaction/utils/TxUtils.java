package com.opuscapita.transaction.utils;

import com.opuscapita.transaction.model.EventOrigin;
import com.opuscapita.transaction.model.Tx;
import com.opuscapita.transaction.model.TxSchemaV1_5;
import com.opuscapita.transaction.model.document.Document;
import com.opuscapita.transaction.model.inbound.Inbound;
import com.opuscapita.transaction.model.inbound.ServiceProfile;
import com.opuscapita.transaction.model.properties.Version;
import org.apache.kafka.common.errors.UnsupportedVersionException;

import java.util.Objects;

public class TxUtils {
    public static String ESCAPED = "\"";
    public static String COMMA = ",";

    enum DEFAULT {
        SYSTEMNODE("SFTP"),
        SYSTEMTYPE("Gateway");

        public final Object value;

        DEFAULT(String _value) {
            this.value = _value;
        }
    }

    private TxUtils() {
    }

    public static Tx createEventTx(
            Version _version,
            String _actionId,
            String _businessPartnerId,
            String _senderBusinessPartnerId,
            String _gatewayId
    ) throws NullPointerException, UnsupportedVersionException {
        Inbound inbound = new Inbound(new ServiceProfile(_actionId, _businessPartnerId, _senderBusinessPartnerId, _gatewayId));
        EventOrigin eventOrigin = new EventOrigin(DEFAULT.SYSTEMNODE.toString(), DEFAULT.SYSTEMTYPE.toString());
        Document document = new Document();
        if (_version == Version.V_1_5) {
            return new TxSchemaV1_5(inbound, eventOrigin, document);
        } else {
            throw new UnsupportedVersionException("EventSchema in version " + _version + " is not supported");
        }
    }

    public static String attributeAsJson(final String field, final Object value) {
        return attributeAsJson(field, value, true);
    }

    public static String attributeAsJson(final String field, final Object value, final boolean appendComma) {
        Object val = value;
        if (Objects.isNull(value))
            val = "";

        StringBuilder builder = new StringBuilder();
        builder.append(ESCAPED);
        builder.append(field);
        builder.append(ESCAPED);
        builder.append(":");
        if (val.getClass() != Integer.class && val.getClass() != Boolean.class) {
            builder.append(ESCAPED);
            builder.append(value);
            builder.append(ESCAPED);
        } else {
            builder.append(value);
        }
        if (appendComma) {
            builder.append(COMMA);
        }
        return builder.toString();
    }
}
