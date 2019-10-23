package com.opuscapita.sftp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource(value = "classpath:application-sftp.properties")
@Configuration
@ConfigurationProperties(prefix = "sftp.server")
public class SFTPConfiguration {

    private int port;
    private String welcome;
    private String hostKey;
}
