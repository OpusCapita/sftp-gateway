package com.opuscapita.transaction.model;

import com.opuscapita.transaction.model.document.Document;
import com.opuscapita.transaction.model.inbound.Inbound;
import com.opuscapita.transaction.model.properties.LogAccess;
import com.opuscapita.transaction.model.properties.Severity;
import com.opuscapita.transaction.model.properties.StepStatus;
import com.opuscapita.transaction.utils.TxUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Data
public class TxSchemaV1_5 implements Tx {
    private final Logger log = LoggerFactory.getLogger(Tx.class);

    /**
     * Required
     */
    private final String version = "1.5";
    private final UUID transactionId = UUID.randomUUID();
    private final Date timestamp = Date.from(Instant.now());
    private LogAccess logAccess = LogAccess.SENDER;
    private Severity severity = Severity.INFO;

    /**
     * Not Required
     */
    private StepStatus stepStatus = StepStatus.COMPLETED;
    private String eventText = "";
    private String shortEventText = "";
    private Inbound inbound;
    private Document document;
    private EventOrigin eventOrigin;

    public TxSchemaV1_5(
            Inbound _inbound,
            EventOrigin _eventOrigin,
            Document _document
    ) {
        if (Objects.isNull(_inbound))
            throw new NullPointerException("Inbound is null");
        if (Objects.isNull(_eventOrigin))
            throw new NullPointerException("EventOrigin is null");
        if (Objects.isNull(_document))
            throw new NullPointerException("Document is null");

        this.setInbound(_inbound);
        this.setEventOrigin(_eventOrigin);
        this.setDocument(_document);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"event\":{");

        builder.append(TxUtils.attributeAsJson("version", this.getVersion()));
        builder.append(TxUtils.attributeAsJson("transactionId", this.getTransactionId()));
        builder.append(TxUtils.attributeAsJson("timestamp", TxUtils.getDateFormated(this.getTimestamp())));
        builder.append(TxUtils.attributeAsJson("logAccess", this.getLogAccess().toString()));
        builder.append(TxUtils.attributeAsJson("severity", this.getSeverity().toString()));
        builder.append(TxUtils.attributeAsJson("eventText", this.getEventText()));
        builder.append(TxUtils.attributeAsJson("shortEventText", this.getShortEventText()));
        builder.append(Objects.requireNonNull(this.getEventOrigin(), "").toString());
        if (!Objects.isNull(this.getEventOrigin())) {
            builder.append(TxUtils.COMMA);
        }
        builder.append(Objects.requireNonNull(this.getInbound(), "").toString());
        if (!Objects.isNull(this.getInbound())) {
            builder.append(TxUtils.COMMA);
        }
        builder.append(Objects.requireNonNull(this.getDocument(), "").toString());

        builder.append("}}");
        return builder.toString();
    }

}
