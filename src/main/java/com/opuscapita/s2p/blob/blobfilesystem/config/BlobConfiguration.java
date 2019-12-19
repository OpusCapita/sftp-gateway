package com.opuscapita.s2p.blob.blobfilesystem.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application-blob.properties")
@Configuration
@Data
@ConfigurationProperties(prefix = "blob.server")
public class BlobConfiguration {
    private String url;
    private String method;
    private int port;
    private String access;
    private String type;
}
