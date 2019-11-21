package com.opuscapita.bouncer.client;


import com.opuscapita.bouncer.exceptions.PermissionsNotRegistered;

public interface ServiceClient {
    void sendEvent(final String message) throws PermissionsNotRegistered;
}
