package com.opuscapita.bouncer.model.abstractData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public abstract class AbstractBouncerDataObject<T> {

    public T fromJson(final String _json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(_json);
        return this.fromJson(jsonNode);
    }

    protected String listToString(List<?> list) {
        final StringBuilder _builder = new StringBuilder();
        _builder.append("[");
        for (Object o : list) {
            _builder.append("\"" + o.toString() + "\",");
        }
        if(_builder.length()>1) {
            _builder.setLength(_builder.length() - 1);
        }
        _builder.append("]");
        return _builder.toString();
    }

    public abstract T fromJson(final JsonNode _json);
}
