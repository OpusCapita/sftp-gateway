package com.opuscapita.bouncer.model.properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.opuscapita.bouncer.model.abstractData.AbstractBouncerDataObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResourceFields extends AbstractBouncerDataObject<ResourceFields> {
    private String title;
    private List<String> removeList;
    private List<String> allowList;

    static final String REMOVE = "remove";
    static final String ALLOW = "allow";

    public ResourceFields(final String _title) {
        this.setTitle(_title);
        this.setAllowList(new ArrayList<>());
        this.setRemoveList(new ArrayList<>());
    }

    public List<String> addAllowField(final String _field) {
        this.getAllowList().add(_field);
        return this.getAllowList();
    }

    public List<String> addRemoveField(final String _field) {
        this.getRemoveList().add(_field);
        return this.getRemoveList();
    }

    @Override
    public ResourceFields fromJson(JsonNode _json) {
        if (_json.get(ALLOW) != null) {
            if (Objects.requireNonNull(_json.get(ALLOW)).isArray()) {
                for (final JsonNode _typeNode : _json.get(ALLOW)) {
                    this.addAllowField(_typeNode.asText());
                }
            } else {
                this.addAllowField(_json.get(ALLOW).asText());
            }
        }

        if (_json.get(REMOVE) != null) {
            if (Objects.requireNonNull(_json.get(REMOVE)).isArray()) {
                for (final JsonNode _typeNode : _json.get(REMOVE)) {
                    this.addRemoveField(_typeNode.asText());
                }
            } else {
                this.addRemoveField(_json.get(REMOVE).asText());
            }
        }
        return this;
    }
}
