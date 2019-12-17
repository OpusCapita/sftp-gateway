package com.opuscapita.transaction.model.document;

import com.opuscapita.transaction.model.Tx;
import com.opuscapita.transaction.model.properties.EntityType;
import com.opuscapita.transaction.utils.TxUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class Document implements Tx {

    private final String title = "document";

    private String entityType;
    private String docTypeSub;
    private String docTypeSubCode;
    private int number;
    private Date issuedDate = new Date();
    private List<Content> content = new ArrayList<>();

    public Document(
            String _entityType,
            String _docTypeSub,
            String _docTypeSubCode,
            int _number,
            Date _issuedDate
    ) {
        if (Objects.isNull(_entityType))
            throw new NullPointerException("EntityType is null");
        if (Objects.isNull(_docTypeSub))
            throw new NullPointerException("DocSubType is null");
        if (Objects.isNull(_docTypeSubCode))
            throw new NullPointerException("DocSubTypeCode is null");
        if (Objects.isNull(_number))
            throw new NullPointerException("Number is null");
        if (Objects.isNull(_issuedDate))
            throw new NullPointerException("IssuedDate is null");

        this.setEntityType(_entityType);
        this.setDocTypeSub(_docTypeSub);
        this.setDocTypeSubCode(_docTypeSubCode);
        this.setNumber(_number);
        this.setIssuedDate(_issuedDate);
    }

    public Document() {
        this.setEntityType(EntityType.DOCUMENT.toString());
        this.setIssuedDate(new Date());
        this.setNumber(0);
        this.setDocTypeSub("");
        this.setDocTypeSubCode("");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"" + title + "\":{");
        builder.append(TxUtils.attributeAsJson("entityType", Objects.requireNonNull(this.getEntityType(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("docTypeSub", Objects.requireNonNull(this.getDocTypeSub(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("docTypeSubCode", Objects.requireNonNull(this.getDocTypeSubCode(), "--- no value ---")));
        builder.append(TxUtils.attributeAsJson("number", Objects.requireNonNull(String.valueOf(this.getNumber()), String.valueOf(0))));
        builder.append("\"content\":[");
        for (Content _content : this.getContent()) {
            builder.append(_content.toString());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("],");
        builder.append(TxUtils.attributeAsJson("issuedDate", Objects.requireNonNull(TxUtils.getDateFormated(this.getIssuedDate())), false));
        builder.append("}");
        return builder.toString();
    }
}
