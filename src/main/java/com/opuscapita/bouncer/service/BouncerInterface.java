package com.opuscapita.bouncer.service;

import com.opuscapita.bouncer.client.ServiceClient;
import com.opuscapita.bouncer.exceptions.PermissionsFileNotExists;
import com.opuscapita.bouncer.model.Permission;
import com.opuscapita.bouncer.model.RetryConfig;

import java.util.List;
import java.util.Map;

public interface BouncerInterface {

    Map<String, Permission> loadPermissions(final String src) throws PermissionsFileNotExists;

    Map<String, Permission> loadPermissions(final List<String> src) throws PermissionsFileNotExists;

    Map<String, Permission> normalizePermissions();

    void registerPermissions(RetryConfig config);

    void findResources(
            final String url,
            final String method,
            final Object userData,
            final ServiceClient serviceClient,
            final String serviceName
    );
}
