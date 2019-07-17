package com.opuscapita.auth;

import com.opuscapita.auth.config.AuthConfiguration;
import com.opuscapita.auth.model.AuthRequest;
import com.opuscapita.auth.model.AuthResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ComponentScan
public class AuthService {

    private Log log = LogFactory.getLog(AuthService.class);

    private AuthConfiguration configuration;

    @Autowired
    public AuthService(AuthConfiguration _configuration) {
        this.configuration = _configuration;
    }

    public AuthResponse authenticateWithPassword(String _username, String _password) {
        log.info("Authentication with Password");
        RestTemplate rest = new RestTemplate();
        AuthRequest request = AuthRequest.createPasswordRequest(_username, _password);
        HttpEntity<String> entity = new HttpEntity<>(request.getMultiFormDataAsString(), request.getHttpHeader(this.configuration.getClientKey(), this.configuration.getClientSecret()));
        ResponseEntity<String> response = rest.exchange(this.configuration.getUrl() + this.configuration.getEndpoint(), HttpMethod.POST, entity, String.class);
        log.info(entity.getHeaders().toString());
        return new AuthResponse(response.getStatusCode(), response.getBody());
    }

    public AuthResponse authenticateWithPublicKey(String _username, String _publicKey) {
        return null;
    }

}
