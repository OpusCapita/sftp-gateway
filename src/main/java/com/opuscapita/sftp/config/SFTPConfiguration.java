package com.opuscapita.sftp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="sftp.server")
public class SFTPConfiguration {

    private int port;
    private String welcome;
    private String hostKey;
}
