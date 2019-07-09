package com.opuscapita.sftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="sftp.server")
public class SFTPConfiguration {

    private int port;
    private String welcome;
    private String hostKey;

    public int getPort() {
        return port;
    }

    public String getWelcome() {
        return welcome;
    }

    public String getHostKey() {
        return hostKey;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }
}
