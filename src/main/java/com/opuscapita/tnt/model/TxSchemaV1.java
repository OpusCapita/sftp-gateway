package com.opuscapita.tnt.model;

import com.opuscapita.tnt.model.properties.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
public class TxSchemaV1 implements Tx {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Required
     */
    private final String version = "1.0";
    private final UUID transactionId = UUID.randomUUID();
    private final Date timestamp = Date.from(Instant.now());
    private LogAccess logAccess;
    private Severity severity;

    /**
     * Not Required
     */
    private boolean archivable;
    private String wmTransactionId;
    private StepStatus stepStatus;
    private StepType stepType;
    private boolean processFinished;
    private Product product;
    private String productSub;
    private String shortEventInfo;
    private String eventText;


    @Override
    public String asJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"event\":{");

        builder.append(attributeAsJson("version", this.version));
        builder.append(attributeAsJson("transactionId", this.transactionId));
        builder.append(attributeAsJson("timestamp", this.timestamp));
        builder.append(attributeAsJson("logAccess", this.logAccess));
        builder.append(attributeAsJson("severity", this.severity));

        builder.append("}}");

        return builder.toString();
    }

    private String attributeAsJson(final String field, final Object value) {
        final String escaped = "\"";
        StringBuilder builder = new StringBuilder();
        builder.append(escaped);
        builder.append(field);
        builder.append(escaped);
        builder.append(":");
        if (value.getClass() == String.class) {
            builder.append(escaped);
        }
        builder.append(value);
        if (value.getClass() == String.class) {
            builder.append(escaped);
        }
        return builder.toString();
    }
}
