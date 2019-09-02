package com.opuscapita.tnt.model.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tnt.server")
public class TNTConfiguration {
    @Setter
    @Getter
    private String method;
    @Setter
    @Getter
    private String url;
    @Setter
    @Getter
    private int port;
}