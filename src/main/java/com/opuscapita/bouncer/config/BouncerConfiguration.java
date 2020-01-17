package com.opuscapita.bouncer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
public class BouncerConfiguration {
    @Value(value = "${sftp.service-name}")
    private String serviceName;
    @Value(value = "${bouncer.service-name}")
    private String aclServiceName;
    @Value(value = "${bouncer.permissions:''}")
    private String permissions;
    @Value(value = "${bouncer.roles.allways-allow:''}")
    private List<String> rolesAllwaysAllow;
    @Value(value = "${bouncer.roles.allways-deny:''}")
    private List<String> rolesAllwaysDeny;
    @Value(value = "${bouncer.paths.public:''}")
    private List<String> publicPaths;
}
