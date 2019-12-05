package com.opuscapita.transaction.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-tnt.properties")
@Data
public class TNTConfiguration {

    @Value(value = "${tnt.method}")
    private String method;
    @Value(value = "${tnt.url}")
    private String url;
    @Value(value = "${tnt.port}")
    private int port;
    @Value(value = "${tnt.event.path}")
    private String path;
}