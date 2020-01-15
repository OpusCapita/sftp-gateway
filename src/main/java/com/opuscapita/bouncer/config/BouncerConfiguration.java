package com.opuscapita.bouncer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@PropertySource(value = "classpath:application-bouncer.properties")
@Configuration
public class BouncerConfiguration {
    @Value(value = "${bouncer.service-name}")
    private String serviceName;
    @Value(value = "${bouncer.permissions.file:acl.json}")
    private String permissionsFile;
    @Value(value = "${bouncer.acl.service-name}")
    private String aclServiceName;
    @Value(value = "${bouncer.roles.allways-allow}")
    private List<String> rolesAllwaysAllow;
    @Value(value = "${bouncer.roles.allways-deny}")
    private List<String> rolesAllwaysDeny;
    @Value(value = "${bouncer.paths.public}")
    private List<String> publicPaths;
}
