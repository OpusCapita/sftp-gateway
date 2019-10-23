package com.opuscapita.bouncer.service;

import com.opuscapita.bouncer.client.ServiceClient;
import com.opuscapita.bouncer.model.RetryConfig;

public interface BouncerInterface {

    void loadPermissions(final String src);

    void normalizePermissions();

    void registerPermissions(RetryConfig config);

    void findResources(
            final String url,
            final String method,
            final Object userData,
            final ServiceClient serviceClient,
            final String serviceName
    );
}
