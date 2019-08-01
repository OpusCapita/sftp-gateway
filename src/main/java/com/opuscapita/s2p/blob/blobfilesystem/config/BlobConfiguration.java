package com.opuscapita.s2p.blob.blobfilesystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "blob.server")
public class BlobConfiguration {
    @Setter
    @Getter
    private String url;
    @Setter
    @Getter
    private String method;
    @Setter
    @Getter
    private int port;
    @Setter
    @Getter
    private String access;
    @Setter
    @Getter
    private String type;
}
