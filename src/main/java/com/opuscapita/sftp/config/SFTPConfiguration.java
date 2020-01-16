package com.opuscapita.sftp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SFTPConfiguration {
    @Value(value = "${sftp.service-name}")
    private String serviceName;
    @Value(value = "${sftp.server.port}")
    private int port;
    @Value(value = "${sftp.server.welcome}")
    private String welcome;
    @Value(value = "${sftp.server.host-key}")
    private String hostKey;
}
