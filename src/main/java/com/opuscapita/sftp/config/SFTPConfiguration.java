package com.opuscapita.sftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="sftp.server")
public class SFTPConfiguration {

    private int port;
    private String welcome;
    private String hostKey;
}
