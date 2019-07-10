package com.opuscapita.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth.server")
public class AuthConfiguration {
    private String url;
    private String endpoint;
    private String clientKey;
    private String clientSecret;

    public String getUrl() {
        return this.url;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getClientKey() {
        return this.clientKey;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
