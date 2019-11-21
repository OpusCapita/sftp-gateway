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
public class Resource extends AbstractBouncerDataObject<Resource> {
    private String resourceId;
    private List<ResourceType> typeList;
    private List<String> actionList;
    private List<String> roleIdList;
    private ResourceFields requestFields;
    private ResourceFields responseFields;

    static final String ACTIONS = "actions";
    static final String TYPE = "type";
    static final String ROLEIDS = "roleIds";

    public Resource(final String _ressourceId) {
        this.setResourceId(_ressourceId);
        this.setTypeList(new ArrayList<>());
        this.setActionList(new ArrayList<>());
        this.setRoleIdList(new ArrayList<>());
        this.setRequestFields(new ResourceFields("requestFields"));
        this.setResponseFields(new ResourceFields("responseFields"));
    }

    public Resource() {
        this.setResourceId("");
        this.setTypeList(new ArrayList<>());
        this.setActionList(new ArrayList<>());
        this.setRoleIdList(new ArrayList<>());
        this.setRequestFields(new ResourceFields("requestFields"));
        this.setResponseFields(new ResourceFields("responseFields"));
    }

    @Override
    public Resource fromJson(JsonNode _json) {
        this.setResourceId(Objects.requireNonNull(_json.get("resourceId"), "").asText());

        if (_json.get(TYPE) != null) {
            if (Objects.requireNonNull(_json.get(TYPE)).isArray()) {
                for (final JsonNode _typeNode : _json.get(TYPE)) {
                    this.typeList.add(ResourceType.valueOf(_typeNode.asText().toUpperCase()));
                }
            } else {
                this.typeList.add(ResourceType.valueOf(_json.get(TYPE).asText().toUpperCase()));
            }
        }

        if (_json.get(ACTIONS) != null) {
            if (Objects.requireNonNull(_json.get(ACTIONS)).isArray()) {
                for (final JsonNode _actionNode : _json.get(ACTIONS)) {
                    this.actionList.add(_actionNode.asText());
                }
            } else {
                this.actionList.add(_json.get(ACTIONS).asText());
            }
        }

        if (_json.get(ROLEIDS) != null) {
            if (Objects.requireNonNull(_json.get(ROLEIDS)).isArray()) {
                for (final JsonNode _roleNode : _json.get(ROLEIDS)) {
                    this.roleIdList.add(_roleNode.asText());
                }
            } else {
                this.roleIdList.add(_json.get(ROLEIDS).asText());
            }
        }

        if (_json.get("responseFields") != null) {
            this.getResponseFields().fromJson(_json.get("responseFields"));
        }

        if (_json.get("requestFields") != null) {
            this.getRequestFields().fromJson(_json.get("requestFields"));
        }

        return this;
    }

    @Override
    public String toString() {
        StringBuilder _builder = new StringBuilder();
        _builder.append("{")
                .append("\"resourceId\": \"" + this.getResourceId() + "\"")
                .append(",")
                .append("\"" + TYPE + "\": " + this.listToString(this.getTypeList()))
                .append(",")
                .append("\"" + ACTIONS + "\": " + this.listToString(this.getActionList()))
                .append(",")
                .append("\"" + ROLEIDS + "\": " + this.listToString(this.getRoleIdList()))
                .append(",")
                .append(this.getRequestFields())
                .append(",")
                .append(this.getResponseFields())
                .append("}");
        return _builder.toString();
    }
}
