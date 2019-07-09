package com.opuscapita.auth;

import com.opuscapita.auth.config.AuthConfigiguration;
import com.opuscapita.auth.model.AuthResponse;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthService extends AbstractLoggingBean {


    private AuthConfigiguration configuration;

    @Autowired
    public AuthService(AuthConfigiguration _configuration) {
        this.configuration = _configuration;
        log.info(this.configuration.getClientId());
    }

    public AuthResponse authenticateWithPassword(String _username, String _password) {
        return null;
    }

    public AuthResponse authenticateWithPublicKey(String _username, String _publicKey) {
        return null;
    }


}
