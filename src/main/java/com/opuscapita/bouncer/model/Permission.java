package com.opuscapita.bouncer.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.opuscapita.bouncer.model.abstractData.AbstractBouncerDataObject;
import com.opuscapita.bouncer.model.properties.PermissionProperty;
import com.opuscapita.bouncer.model.properties.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class Permission extends AbstractBouncerDataObject<Permission> {
    private PermissionProperty name;
    private PermissionProperty description;
    private List<Resource> resources;

    public Permission() {
        this.setResources(new ArrayList<>());
        this.setName(new PermissionProperty("name"));
        this.setDescription(new PermissionProperty("description"));
    }

    @Override
    public Permission fromJson(final JsonNode _json) {
        this.getName().fromJson(Objects.requireNonNull(_json.get("name")));
        this.getDescription().fromJson(Objects.requireNonNull(_json.get("description")));
        if (Objects.requireNonNull(_json.get("resources")).isArray()) {
            for (final JsonNode _resNode : _json.get("resources")) {
                this.getResources().add(new Resource().fromJson(_resNode));
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder _builder = new StringBuilder();
        _builder.append(this.getName())
                .append(",")
                .append(this.getDescription())
                .append(",")
                .append("\"resources\": [");
        for (Resource _resource : this.getResources()) {
            _builder.append(_resource);
        }
        _builder.append("]");
        return _builder.toString();
    }
}
