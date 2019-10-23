package com.opuscapita.bouncer.model.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResourceFields {
    private String title;
    private List<String> remove;
    private List<String> allow;

    public ResourceFields(final String _title) {
        this.setTitle(_title);
        this.setAllow(new ArrayList<>());
        this.setRemove(new ArrayList<>());
    }

    public List<String> addAllowField(final String _field) {
        this.getAllow().add(_field);
        return this.getAllow();
    }

    public List<String> addRemoveField(final String _field) {
        this.getRemove().add(_field);
        return this.getRemove();
    }
}
