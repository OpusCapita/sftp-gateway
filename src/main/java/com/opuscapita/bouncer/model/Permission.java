package com.opuscapita.bouncer.model;

import com.opuscapita.bouncer.model.properties.PermissionProperty;
import com.opuscapita.bouncer.model.properties.Resource;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Permission {
    private PermissionProperty name;
    private PermissionProperty description;
    private List<Resource> resources;

    public Permission() {
        this.setResources(new ArrayList<>());
        this.setName(new PermissionProperty("name"));
        this.setDescription(new PermissionProperty("description"));
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
