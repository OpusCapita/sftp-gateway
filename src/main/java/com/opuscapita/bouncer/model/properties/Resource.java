package com.opuscapita.bouncer.model.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Resource {
    private String ressourceId;
    private List<ResourceType> type;
    private List<String> actions;
    private List<String> roleIds;

    public Resource(final String _ressourceId) {
        this.setRessourceId(_ressourceId);
        this.setType(new ArrayList<>());
        this.setActions(new ArrayList<>());
        this.setRoleIds(new ArrayList<>());
    }

    public String toString() {
        StringBuilder _builder = new StringBuilder();
        _builder.append("{")
                .append("}");
        return _builder.toString();
    }
}
