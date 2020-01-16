package com.opuscapita.web.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "web.security")
public class JwtConfig {
    @Getter
    @Value("${web.security.jwt.uri:/api/sftp/**}")
    private String Uri;
    @Getter
    @Value("${web.security.jwt.header:X-User-Id-Token}")
    private String header;
    @Getter
    @Value("${web.security.jwt.prefix:Bearer }")
    private String prefix;
    @Getter
    @Value("${web.security.jwt.expiration:#{24*60*60}}")
    private int expiration;
    @Getter
    @Value("${web.security.jwt.secret:JwtSecretKey}")
    private String secret;
}
