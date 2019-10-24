package com.opuscapita.bouncer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opuscapita.bouncer.client.ServiceClient;
import com.opuscapita.bouncer.config.BouncerConfiguration;
import com.opuscapita.bouncer.exceptions.PermissionsFileNotExists;
import com.opuscapita.bouncer.model.Permission;
import com.opuscapita.bouncer.model.RetryConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ComponentScan
@Component
public class Bouncer implements BouncerInterface {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BouncerConfiguration configuration;
    private final ServiceClient serviceClient;
    private final Map<String, Permission> permissionMap;

    @Autowired
    public Bouncer(
            final BouncerConfiguration _configuration,
            final ServiceClient _serviceClient
    ) {
        Map<String, Permission> _permissionMap;
        this.configuration = _configuration;
        this.serviceClient = _serviceClient;
        try {
            _permissionMap = this.loadPermissions(this.configuration.getPermissionsFile());
            this.log.info("Permissions were loaded");
        } catch (PermissionsFileNotExists e) {
            this.log.warn(e.getMessage());
            _permissionMap = new HashMap<>();
        }
        this.permissionMap = _permissionMap;
    }

    @Override
    public Map<String, Permission> loadPermissions(String src) throws PermissionsFileNotExists {
        File permissionsFile = new File(src);
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
    public Map<String, Permission> loadPermissions(List<String> src) throws PermissionsFileNotExists {
        return null;
    }

    @Override
    public Map<String, Permission> normalizePermissions() {
        return null;
    }

    @Override
    public void registerPermissions(RetryConfig config) {
        List<Permission> permissionList;
    }

    @Override
    public void findResources(String url, String method, Object userData, ServiceClient serviceClient, String serviceName) {

    }
}
