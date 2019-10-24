package com.opuscapita.bouncer.model.abstractData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class AbstractBouncerDataObject<T> {

    public T fromJson(final String _json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(_json);
        return this.fromJson(jsonNode);
    }

    public abstract T fromJson(final JsonNode _json);
}
