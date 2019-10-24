package com.opuscapita.bouncer.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.opuscapita.bouncer.model.abstractData.AbstractBouncerDataObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionProperty extends AbstractBouncerDataObject<PermissionProperty> {
    private String title;

    private String en;
    private String de;

    public PermissionProperty(
            final String _title,
            final String _en,
            final String _de
    ) {
        this.setTitle(Objects.requireNonNull(_title, "title"));
        this.setDe(Objects.requireNonNull(_de, ""));
        this.setEn(Objects.requireNonNull(_en, ""));
    }

    public PermissionProperty(
            final String _title
    ) {
        this.setTitle(Objects.requireNonNull(_title, "title"));
        this.setDe("");
        this.setEn("");
    }

    @Override
    public PermissionProperty fromJson(final JsonNode _json) {
        this.setEn(Objects.requireNonNull(_json.get("en"), "").asText());
        this.setDe(Objects.requireNonNull(_json.get("de"), "").asText());
        return this;
    }

    @Override
    public String toString() {
        StringBuilder _builder = new StringBuilder();
        _builder.append("\"" + this.title + "\": {")
                .append("\"de\": \"" + this.de + "\"")
                .append(",")
                .append("\"en\": \"" + this.en + "\"")
                .append("}");
        return _builder.toString();
    }

}
