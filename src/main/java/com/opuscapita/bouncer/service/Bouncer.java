package com.opuscapita.bouncer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opuscapita.bouncer.client.ServiceClient;
import com.opuscapita.bouncer.config.BouncerConfiguration;
import com.opuscapita.bouncer.exceptions.EmptyPermissionsException;
import com.opuscapita.bouncer.exceptions.PermissionsFileNotExists;
import com.opuscapita.bouncer.exceptions.PermissionsNotRegistered;
import com.opuscapita.bouncer.model.Permission;
import com.opuscapita.bouncer.model.RetryConfig;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ComponentScan
@Component
public class Bouncer implements BouncerInterface {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Getter
    private final Map<String, Permission> permissionMap;

    private final BouncerConfiguration configuration;
    private final ServiceClient serviceClient;


    @Autowired
    public Bouncer(
            final BouncerConfiguration _configuration,
            final ServiceClient _serviceClient
    ) {
        this.configuration = _configuration;
        this.serviceClient = _serviceClient;
        this.permissionMap = new HashMap<>();
    }

    @PostConstruct
    private void initializeAcl() {
        Map<String, Permission> _permissionMap;
        try {
            _permissionMap = this.loadPermissions(this.configuration.getPermissions());
            this.permissionMap.clear();
            this.permissionMap.putAll(_permissionMap);
            this.log.info("Permissions were loaded");
        } catch (PermissionsFileNotExists e) {
            this.log.error(e.getMessage());
        }
    }

    @Override
    public Map<String, Permission> loadPermissions(final File src) throws PermissionsFileNotExists {
        File permissionsFile = src;
        if (!permissionsFile.exists() || !permissionsFile.canRead() || !permissionsFile.isFile()) {
            throw new PermissionsFileNotExists("\"" + src + "\" does not exists.");
        }
        Map<String, Permission> _permissionMap = new LinkedHashMap<>();
        try {
            String content = FileUtils.readFileToString(permissionsFile, StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(content);
            for (Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> elt = it.next();
                _permissionMap.put(elt.getKey(), new Permission().fromJson(elt.getValue().toString()));
            }
        } catch (IOException e) {
            throw new PermissionsFileNotExists("\"" + src + "\" does not exists.");
        }

        return _permissionMap;
    }

    @Override
    public Map<String, Permission> loadPermissions(String src) throws PermissionsFileNotExists {
        Map<String, Permission> _permissionMap = new LinkedHashMap<>();
        try {
            String content = src.trim()
                    .replace("\n", "")
                    .replace("\t", "")
                    .replace("\r", "");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(content);

            for (Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> elt = it.next();
                _permissionMap.put(elt.getKey(), new Permission().fromJson(elt.getValue().toString()));
            }
        } catch (IOException e) {
            throw new PermissionsFileNotExists("\"" + src + "\" does not exists.");
        }

        return _permissionMap;
    }

    @Override
    public Map<String, Permission> loadPermissions(List<String> src) throws PermissionsFileNotExists {
        return null;
    }

    @Override
    public Map<String, Permission> normalizePermissions() {
        return null;
    }

    @Override
    public synchronized void registerPermissions(RetryConfig config) throws PermissionsNotRegistered, EmptyPermissionsException {
        int retryCounter = 0;
        if (this.getPermissionMap().isEmpty()) {
            throw new EmptyPermissionsException("Permissions does not exists");
        }
        log.info("Trying to register the Permissions: {}", retryCounter + 1);
        this.serviceClient.sendEvent(this.toString());
    }

    @Override
    public void findResources(String url, String method, Object userData, ServiceClient serviceClient, String serviceName) {

    }

    public void getResourceGroups(String _serviceName) {
        final String _url = "/api/resourceGroups/" + Objects.requireNonNull(_serviceName, "") + "?type=rest";

    }

    @Override
    public String toString() {
        StringBuilder _builder = new StringBuilder();
        _builder.append("{");
        for (String key : this.getPermissionMap().keySet()) {
            _builder.append("\"" + key + "\": {" + this.getPermissionMap().get(key).toString() + "}")
                    .append(",");
        }
        if (_builder.length() > 1) {
            _builder.setLength(_builder.length() - 1);
        }
        _builder.append("}");
        return _builder.toString();
    }
}
