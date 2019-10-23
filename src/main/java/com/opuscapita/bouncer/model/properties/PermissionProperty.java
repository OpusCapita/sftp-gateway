package com.opuscapita.bouncer.model.properties;

import lombok.Data;

import java.util.Objects;

@Data
public class PermissionProperty {
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
    public String toString() {
        StringBuilder _builder = new StringBuilder();
        _builder.append("\"" + this.title + "\": {")
                .append("\"de\": \"" + this.de + "\"")
                .append("\"en\": \"" + this.en + "\"")
                .append("}");
        return _builder.toString();
    }

}
